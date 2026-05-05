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
import entity.Ve;
import entity.type.TrangThaiVe;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import mapper.ChuyenMapper;
import mapper.GheMapper;
import mapper.KhuyenMaiMapper;
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

        Ve ve = Ve.builder()
                .chuyen(ChuyenMapper.INSTANCE.toEntity(chuyen))
                .gaDi(gaDi).gaDen(gaDen).ghe(GheMapper.INSTANCE.toEntity(ghe))
                .ngayGioDi(ngayGioDi).gia(gia).trangThai(TrangThaiVe.DA_BAN)
                .build();
        VeDTO veDTO = VeMapper.INSTANCE.toDTO(ve);
        veDTO.setTauID(toa.getTauID());
        veDTO.setToaID(toa.getId());
        veDTO.setHangToaID(toa.getHangToaID());
        veDTO.setSoToa(toa.getSoToa());

        KhuyenMaiDTO khuyenMai = KhuyenMaiMapper.INSTANCE.toDTO(khuyenMaiBUS.timKhuyenMaiChoVe(ve));
        int giamKM = 0;

        return new VeSession(veDTO, khuyenMai, giamKM, thoiDiemHetHan);
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
        List<Ve> dsVe = new ArrayList<>();
        List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
        List<VeSession> dsVeVe = bookingSession.getReturnSelected();
        DonDatChoDTO donDatCho = bookingSession.getDonDatCho();

        for (VeSession v : dsVeDi) {
            VeDTO ve = v.getVe();
            String veID = taoVeIDDuyNhat(ve);
            ve.setVeID(veID);
            ve.setDonDatChoID(donDatCho.getId());
            ve.setGia(ve.getGia() - v.getGiamKM() - v.getGiamDoiTuong());

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
    public boolean themCacVe(List<Ve> dsVe) {
        for (Ve v : dsVe) {
            if (veDAO.create(v) == null) return false;
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

    public List<VeDTO> locVeTheoCacTieuChi(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay,
                                           Date denNgay, int page, int limit) {
        return veDAO.searchVeByFilter(tuKhoaTraCuu, loaiTraCuu, trangThaiVe, khachHang, soGiayTo, tuNgay, denNgay, page, limit).stream().map(VeMapper.INSTANCE::toDTO).toList();
    }

    public int countVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay,
                               Date denNgay) {
        return veDAO.countVeByFilter(tuKhoaTraCuu, loaiTraCuu, trangThaiVe, khachHang, soGiayTo, tuNgay, denNgay);
    }

    /**
     * @param keyword
     * @param type
     * @return
     */
    public List<VeDTO> layVeTheoKeyword(String keyword, String type, int page, int limit) {
        return veDAO.searchVeByKeyword(keyword, type, page, limit)
                .stream().map(VeMapper.INSTANCE::toDTO).toList();
    }

    public int countVeByKeyword(String keyword, String type) {
        return veDAO.countVeByKeyword(keyword, type);
    }

    /**
     * @param veID
     * @return
     */
    public List<String> layTop10VeID(String veID) {
        return veDAO.getTop10VeID(veID);
    }

    public int countAllVe() {
        return veDAO.countAllVe();
    }

    public List<VeDTO> getVeByPage(int currentPage, int rowsPerPage) {
        return veDAO.getVeByPage(currentPage, rowsPerPage).stream().map(VeMapper.INSTANCE::toDTO).toList();
    }

    public VeDTO getVeByVeID(String veID) {
        return VeMapper.INSTANCE.toDTO(veDAO.findById(veID));
    }

    public boolean updateTrangThaiVe(String veID, TrangThaiVe trangThaiVe) {
        return veDAO.updateTrangThaiVe(veID, trangThaiVe);
    }
}