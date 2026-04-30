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

import connectDB.ConnectDB;
import dao.impl.DonDatChoDAO;
import dao.impl.PhieuGiuChoChiTietDAO;
import dao.impl.PhieuGiuChoDAO;
import entity.*;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatCho_BUS {
    private final PhieuGiuChoDAO pgcDAO = new PhieuGiuChoDAO();
    private final PhieuGiuChoChiTietDAO pgcctDAO = new PhieuGiuChoChiTietDAO();
    private final DonDatChoDAO ddcDAO = new DonDatChoDAO();

    public PhieuGiuCho taoPhieuGiuCho() {
        NhanVien nv = AuthService.getInstance().getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyHHmmss");
        String pgcID = "PGC-" + now.format(formatter).toString();

        return new PhieuGiuCho(pgcID, nv, TrangThaiPhieuGiuCho.DANG_GIU);
    }

    public boolean themPhieuGiuCho(PhieuGiuCho phieuGiuCho) throws Exception {
        return pgcDAO.insertPhieuGiuCho(phieuGiuCho);
    }

    public PhieuGiuChoChiTiet taoPhieuGiuChoChiTiet(Connection conn, PhieuGiuCho pgc, VeSession v, int soThuTu) {
        String chuyenID = v.getVe().getChuyen().getChuyenID();
        String tenGaDi = v.getVe().getGaDi().getTenGa();
        String tenGaDen = v.getVe().getGaDen().getTenGa();
        int soToa = v.getVe().getGhe().getToa().getSoToa();
        int soGhe = v.getVe().getGhe().getSoGhe();
        LocalDateTime thoiDiemGiuCho = v.getThoiDiemHetHan().minus(Duration.ofMinutes(10));

        if (!pgcctDAO.checkConflict(chuyenID, tenGaDi, tenGaDen, soToa, soGhe)) {
            String pgcctID = pgc.getPhieuGiuChoID() + "-" + String.valueOf(soThuTu);
            PhieuGiuChoChiTiet pgcct = new PhieuGiuChoChiTiet(pgcctID, pgc,
                    new Chuyen(v.getVe().getChuyen().getChuyenID()), v.getVe().getGhe(), v.getVe().getGaDi(),
                    v.getVe().getGaDen(), thoiDiemGiuCho, TrangThaiPhieuGiuCho.DANG_GIU.toString());
            return pgcct;
        }
        return null;
    }

    public boolean themPhieuGiuChoChiTiet(PhieuGiuChoChiTiet phieuGiuChoChiTiet) throws Exception {
        return pgcctDAO.insertPhieuGiuChoChiTiet(phieuGiuChoChiTiet);
    }

    public boolean xoaPhieuGiuChoVaChiTiet(List<VeSession> veTrongGio) {
        return true;
    }

    /**
     * @param phieuGiuChoChiTietID
     * @return
     */
    public boolean xoaPhieuGiuChoChiTietByPgcctID(String phieuGiuChoChiTietID) {
        if (phieuGiuChoChiTietID.length() == 0 || phieuGiuChoChiTietID == null) {
            return false;
        }
        return pgcctDAO.deletePhieuGiuChoChiTiet(phieuGiuChoChiTietID);
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
        return pgcDAO.deletePhieuGiuChoByID(phieuGiuChoID);
    }

    public DonDatCho taoDonDatCho(NhanVien nhanVien, KhachHang khachHang) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy-HHmmss");

        String ddcID = "DDC-" + now.format(formatter).toString();

        return new DonDatCho(ddcID, nhanVien, khachHang, now);
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
     * @return PhieuGiuCho (đã kèm các chi tiết) nếu thành công
     * @throws Exception nếu có lỗi (ví dụ: ghế bị trùng)
     */
    public PhieuGiuCho thucHienGiuCho(List<VeSession> veTrongGio) throws Exception {
        Connection conn = null;
        PhieuGiuCho pgc = null; // Khai báo ở ngoài để return

        try {
            // 1. Lấy kết nối VÀ BẮT ĐẦU TRANSACTION
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 2. TẠO VÀ THÊM PHIẾU CHA
            pgc = taoPhieuGiuCho();
            if (pgc == null || !themPhieuGiuCho(pgc)) {
                throw new Exception("Không thể tạo phiếu giữ chỗ cha trong CSDL.");
            }
            // 3. TẠO VÀ THÊM CÁC CHI TIẾT
            for (int i = 0; i < veTrongGio.size(); i++) {
                VeSession v = veTrongGio.get(i);
                PhieuGiuChoChiTiet pgcct = taoPhieuGiuChoChiTiet(conn, pgc, v, i + 1);

                if (pgcct == null) {
                    throw new Exception("Ghế " + v.getVe().getGhe().getSoGhe() + " (Toa "
                            + v.getVe().getGhe().getToa().getSoToa() + ") đã bị người khác chọn.");
                }

                if (!themPhieuGiuChoChiTiet(pgcct)) {
                    throw new Exception("Không thể lưu chi tiết giữ chỗ cho ghế " + v.getVe().getGhe().getSoGhe());
                }
                v.setPhieuGiuChoChiTiet(pgcct);
            }

            // 4. COMMIT
            conn.commit();
            return pgc;

        } catch (Exception e) {
            // 5. ROLLBACK
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;

        } finally {
            // 6. LUÔN LUÔN ĐÓNG KẾT NỐI
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean hoanTacGiuCho(PhieuGiuCho phieuGiuCho) throws Exception {
        Connection conn = null;

        try {
            // 1. Lấy kết nối VÀ BẮT ĐẦU TRANSACTION
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 2. Xóa các phiếu giữ chỗ chi tiết
            pgcctDAO.deletePhieuGiuChoChiTietByPgcID(phieuGiuCho.getPhieuGiuChoID());

            // 3. Xóa phiếu giữ chỗ
            pgcDAO.deletePhieuGiuChoByID(phieuGiuCho.getPhieuGiuChoID());

            // 4. COMMIT
            conn.commit();
        } catch (Exception e) {
            // 5. ROLLBACK
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
            throw e;

        } finally {
            // 6. LUÔN LUÔN ĐÓNG KẾT NỐI
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();

                    return true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}