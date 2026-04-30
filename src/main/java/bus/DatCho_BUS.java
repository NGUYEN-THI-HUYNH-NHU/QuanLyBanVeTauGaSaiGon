package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [12:58:05 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import dao.impl.DonDatChoDAO;
import dao.impl.PhieuGiuChoChiTietDAO;
import dao.impl.PhieuGiuChoDAO;
import dto.KhachHangDTO;
import dto.PhieuGiuChoDTO;
import dto.VeDTO;
import entity.*;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;
import mapper.KhachHangMapper;
import mapper.PhieuGiuChoMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

public class DatCho_BUS {
    private final PhieuGiuChoDAO pgcDAO = new PhieuGiuChoDAO();
    private final PhieuGiuChoChiTietDAO pgcctDAO = new PhieuGiuChoChiTietDAO();
    private final DonDatChoDAO ddcDAO = new DonDatChoDAO();

    private PhieuGiuCho taoPhieuGiuCho() {
        NhanVien nv = AuthService.getInstance().getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
        String pgcID = "PGC-" + now.format(formatter);

        return new PhieuGiuCho(pgcID, nv, TrangThaiPhieuGiuCho.DANG_GIU);
    }

    public PhieuGiuChoChiTiet taoPhieuGiuChoChiTiet(PhieuGiuCho pgc, VeSession v, int soThuTu) {
        String chuyenID = v.getVe().getChuyenID();
        String tenGaDi = v.getVe().getTenGaDi();
        String tenGaDen = v.getVe().getTenGaDen();
        int soToa = v.getVe().getSoToa();
        int soGhe = v.getVe().getSoGhe();
        LocalDateTime thoiDiemGiuCho = v.getThoiDiemHetHan().minus(Duration.ofMinutes(10));

        if (!pgcctDAO.checkConflict(chuyenID, tenGaDi, tenGaDen, soToa, soGhe)) {
            String pgcctID = pgc.getPhieuGiuChoID() + "-" + soThuTu;
            VeDTO ve = v.getVe();
            Ghe ghe = Ghe.builder().gheID(ve.getGheID()).soGhe(ve.getSoGhe()).build();
            Ga gaDi = Ga.builder().gaID(ve.getGaDiID()).tenGa(ve.getTenGaDi()).build();
            Ga gaDen = Ga.builder().gaID(ve.getGaDenID()).tenGa(ve.getTenGaDen()).build();

            PhieuGiuChoChiTiet pgcct = new PhieuGiuChoChiTiet(pgcctID, pgc,
                    new Chuyen(ve.getChuyenID()), ghe, gaDi, gaDen, thoiDiemGiuCho,
                    TrangThaiPhieuGiuCho.DANG_GIU.toString());
            return pgcct;
        }
        return null;
    }

    /**
     * @param phieuGiuChoChiTietID
     * @return
     */
    public boolean xoaPhieuGiuChoChiTietByPgcctID(String phieuGiuChoChiTietID) {
        if (phieuGiuChoChiTietID.length() == 0 || phieuGiuChoChiTietID == null) {
            return false;
        }
        return pgcctDAO.delete(phieuGiuChoChiTietID);
    }

    /**
     * @param phieuGiuChoID
     * @return
     */
    public boolean xoaPhieuGiuChoChiTietByPgcID(String phieuGiuChoID) {
        if (phieuGiuChoID.length() == 0 || phieuGiuChoID == null) {
            return false;
        }
        return pgcctDAO.deletePhieuGiuChoChiTietByPgcID(phieuGiuChoID);
    }

    public boolean xoaPhieuGiuCho(String phieuGiuChoID) {
        if (phieuGiuChoID.length() == 0 || phieuGiuChoID == null) {
            return false;
        }
        return pgcDAO.delete(phieuGiuChoID);
    }

    public DonDatCho taoDonDatCho(NhanVien nhanVien, KhachHangDTO khachHang) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy-HHmmss");

        String ddcID = "DDC-" + now.format(formatter);

        return DonDatCho.builder().donDatChoID(ddcID).nhanVien(nhanVien).khachHang(KhachHangMapper.INSTANCE.toEntity(khachHang)).thoiDiemDatCho(now).build();
    }

    public boolean themDonDatCho(DonDatCho donDatCho) throws Exception {
        return ddcDAO.insertDonDatCho(donDatCho);
    }

    /**
     * @param phieuGiuCho
     */
    public boolean capNhatPhieuGiuCho(PhieuGiuCho phieuGiuCho,
                                      TrangThaiPhieuGiuCho trangThaiPhieuGiuCho) throws Exception {
        return pgcDAO.updateTrangThaiPhieuGiuCho(phieuGiuCho.getPhieuGiuChoID(), trangThaiPhieuGiuCho.toString());
    }

    /**
     * @param phieuGiuCho
     * @param trangThaiPhieuGiuCho
     */
    public boolean capNhatCacPhieuGiuChoChiTiet(PhieuGiuCho phieuGiuCho,
                                                TrangThaiPhieuGiuCho trangThaiPhieuGiuCho) throws Exception {
        return pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByPhieuGiuChoID(phieuGiuCho.getPhieuGiuChoID(),
                trangThaiPhieuGiuCho.toString());
    }

    /**
     * Thực hiện toàn bộ nghiệp vụ giữ chỗ trong một transaction duy nhất. Sẽ tạo
     * PGC cha, rồi tạo các PGC con.
     *
     * @param veTrongGio Danh sách vé session cần giữ
     * @throws Exception nếu có lỗi (ví dụ: ghế bị trùng)
     */
    public PhieuGiuChoDTO thucHienGiuCho(List<VeSession> veTrongGio) throws Exception {
        try {
            // 1. TẠO VÀ THÊM PHIẾU CHA
            PhieuGiuCho pgc = taoPhieuGiuCho();
            pgc.setChiTiets(new HashSet<>());
            if (pgc == null || pgcDAO.create(pgc) == null) {
                throw new Exception("Không thể tạo phiếu giữ chỗ cha trong CSDL.");
            }
            // 2. TẠO VÀ THÊM CÁC CHI TIẾT
            for (int i = 0; i < veTrongGio.size(); i++) {
                VeSession v = veTrongGio.get(i);
                PhieuGiuChoChiTiet pgcct = taoPhieuGiuChoChiTiet(pgc, v, i + 1);

                if (pgcct == null) {
                    throw new Exception("Ghế " + v.getVe().getSoGhe() + " (Toa "
                            + v.getVe().getSoToa() + ") đã bị người khác chọn.");
                }

                if (pgcctDAO.create(pgcct) == null) {
                    throw new Exception("Không thể lưu chi tiết giữ chỗ cho ghế " + v.getVe().getSoGhe());
                }
                pgc.getChiTiets().add(pgcct);
                v.setPhieuGiuChoChiTiet(pgcct);
            }
            return PhieuGiuChoMapper.INSTANCE.toDTO(pgc);
//            return Mapper.map(pgc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DatCho_BUS: Lỗi khi giữ chỗ");
        }
    }

    public boolean hoanTacGiuCho(PhieuGiuCho phieuGiuCho) throws Exception {
        try {
            // 1. Xóa các phiếu giữ chỗ chi tiết
            pgcctDAO.deletePhieuGiuChoChiTietByPgcID(phieuGiuCho.getPhieuGiuChoID());

            // 2. Xóa phiếu giữ chỗ
            pgcDAO.delete(phieuGiuCho.getPhieuGiuChoID());
        } catch (Exception e) {
            throw new Exception("DatCho_BUS: Lỗi khi hủy giữ chỗ: " + e.getMessage());
        }
        return false;
    }
}