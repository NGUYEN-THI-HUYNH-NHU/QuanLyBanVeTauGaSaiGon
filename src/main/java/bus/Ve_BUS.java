package bus;
/*
 * @(#) Ve_Bus.java  1.0  [10:10:54 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

import dao.impl.VeDAO;
import dto.*;
import entity.Ga;
import entity.KhuyenMai;
import entity.Ve;
import entity.type.TrangThaiVe;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import mapper.ChuyenMapper;
import mapper.GheMapper;
import mapper.VeMapper;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ve_BUS {
    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
    private final VeDAO veDAO = new VeDAO();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();

    public VeSession createVeSessionForSeat(ChuyenDTO chuyen, ToaDTO toa, GheDTO ghe, SearchCriteria criteria) {
        Ga gaDi = new Ga(criteria.getGaDiId(), criteria.getGaDiName());
        Ga gaDen = new Ga(criteria.getGaDenId(), criteria.getGaDenName());

        LocalDateTime ngayGioDi = LocalDateTime.of(chuyen.getNgayDi(), chuyen.getGioDi());

        LocalDateTime thoiDiemHetHan = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);

        int gia = chuyenBUS.layGiaGheTheoPhanDoan(chuyen.getId(), criteria.getGaDiId(), criteria.getGaDenId(),
                chuyen.getLoaiTauID(), toa.getHangToaID());

        Ve ve = new Ve();
        ve.setChuyen(ChuyenMapper.INSTANCE.toEntity(chuyen));
        ve.setGaDi(gaDi);
        ve.setGaDen(gaDen);
        ve.setGhe(GheMapper.INSTANCE.toEntity(ghe));
        ve.setNgayGioDi(thoiDiemHetHan);
        ve.setNgayGioDi(ngayGioDi);
        ve.setGia(gia);
        ve.setTrangThai(TrangThaiVe.DA_BAN);

        // TODO: tim khuyen mai
        KhuyenMai khuyenMai = khuyenMaiBUS.timKhuyenMaiChoVe(ve);
        int giamKM = 0;

        return new VeSession(VeMapper.INSTANCE.toDTO(ve), khuyenMai, giamKM, thoiDiemHetHan);
    }

    private String taoVeIDDuyNhat(VeDTO ve) {
        // 1. Tạo Base ID chuẩn
        String baseID = "VE-" + ve.getGaDiID() + ve.getGaDenID() + ve.getChuyenID() + "-"
                + String.format("%02d", ve.getSoToa())
                + String.format("%02d", ve.getSoGhe());

        // 2. Lấy tất cả các ID trong DB đang bắt đầu bằng Base ID này
        List<String> existingIDs = veDAO.getVeIDsStartingWith(baseID);

        // Nếu chưa có vé nào trùng -> Dùng luôn Base ID
        if (existingIDs.isEmpty()) {
            return baseID;
        }

        // 3. Tìm phiên bản lớn nhất (i)
        int maxVersion = -1; // -1 nghĩa là chưa có phiên bản chấm nào, 0 là bản gốc

        for (String id : existingIDs) {
            if (id.equals(baseID)) {
                // Nếu tìm thấy bản gốc, ít nhất max là 0
                if (maxVersion < 0) {
                    maxVersion = 0;
                }
            } else if (id.startsWith(baseID + ".")) {
                // Nếu tìm thấy bản có đuôi .i (VD: .1, .2, .10)
                try {
                    // Cắt bỏ phần base và dấu chấm để lấy số
                    String suffix = id.substring(baseID.length() + 1);
                    int version = Integer.parseInt(suffix);

                    if (version > maxVersion) {
                        maxVersion = version;
                    }
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu đuôi không phải số (phòng hờ dữ liệu rác)
                }
            }
        }

        // 4. Tạo ID mới
        // Nếu maxVersion = -1 (chưa có gì) -> BaseID (đã return ở trên rồi)
        // Nếu maxVersion = 0 (đã có bản gốc) -> BaseID.1
        // Nếu maxVersion = 2 (đã có .2) -> BaseID.3
        return baseID + "." + (maxVersion + 1);
    }

    /**
     * @param bookingSession
     * @return List<Ve>
     */
    public List<Ve> taoCacVeVaThemVaoBookingSession(BookingSession bookingSession) {
        List<Ve> dsVe = new ArrayList<Ve>();
        List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
        List<VeSession> dsVeVe = bookingSession.getReturnSelected();
        DonDatChoDTO donDatCho = bookingSession.getDonDatCho();

        for (VeSession v : dsVeDi) {
            VeDTO ve = v.getVe();
            String veID = taoVeIDDuyNhat(ve);
            ve.setVeID(veID);
            ve.setDonDatChoID(donDatCho.getId());

            dsVe.add(VeMapper.INSTANCE.toEntity(ve));
            v.setVe(ve);
        }
        for (VeSession v : dsVeVe) {
            VeDTO ve = v.getVe();
            String veID = taoVeIDDuyNhat(ve);
            ve.setVeID(veID);
            ve.setDonDatChoID(donDatCho.getId());

            dsVe.add(VeMapper.INSTANCE.toEntity(ve));
            v.setVe(ve);
        }
        return dsVe;
    }

    /**
     * @param exchangeSession
     * @return
     */
    public List<Ve> taoCacVeVaThemVaoExchangeSession(ExchangeSession exchangeSession) {
        List<Ve> dsVe = new ArrayList<>();
        DonDatChoDTO donDatCho = exchangeSession.getDonDatChoMoi();
        int n = exchangeSession.getListVeMoiDangChon().size();

        for (int i = 0; i < n; i++) {
            VeDTO ve = exchangeSession.getListVeMoiDangChon().get(i).getVe();
            String veID = taoVeIDDuyNhat(ve);
            ve.setVeID(veID);
            ve.setDonDatChoID(donDatCho.getId());

            dsVe.add(VeMapper.INSTANCE.toEntity(ve));
            exchangeSession.getListVeMoiDangChon().get(i).setVe(ve);
        }

        return dsVe;
    }

    /**
     * @param dsVe
     * @return boolean
     */
    public boolean themCacVe(List<Ve> dsVe) throws Exception {
        for (Ve v : dsVe) {
            if (!veDAO.insertVe(v)) {
                return false;
            }
        }
        return true;

    }

    /**
     * @param donDatChoID
     * @return
     */
    public List<VeDTO> timCacVeTheoDonDatChoID(String donDatChoID) {
        return veDAO.getVeByDonDatChoID(donDatChoID)
                .stream()
                .map(VeMapper.INSTANCE::toDTO)
                .toList();
    }

    /**
     * @param listVe
     * @param trangThai
     */
    public void capNhatTrangThaiVe(List<VeDTO> listVe, TrangThaiVe trangThai) throws Exception {
        for (VeDTO ve : listVe) {
            veDAO.updateTrangThaiVe(ve.getVeID(), trangThai);
        }
    }

    /**
     * @return
     */
    public List<Ve> layCacVe() {
        return veDAO.getAllVe();
    }

    public List<Ve> locVeTheoCacTieuChi(String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay,
                                        Date denNgay) {
        return veDAO.searchVeByFilter(trangThaiVe, khachHang, soGiayTo, tuNgay, denNgay);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10DonDatChoID(String keyword) {
        return veDAO.getTop10DonDatChoID(keyword);
    }

    /**
     * @param keyword
     * @param type
     * @return
     */
    public List<Ve> layVeTheoKeyword(String keyword, String type) {
        return veDAO.searchVeByKeyword(keyword, type);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10VeID(String keyword) {
        return veDAO.getTop10VeID(keyword);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10SoGiayToKhachHang(String keyword) {
        return veDAO.getTop10SoGiayToKhachHang(keyword);
    }
}