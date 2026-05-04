package dao.impl;

import db.JPAUtil;
import entity.type.LoaiDichVuEnums;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThongKeNhanVienDAO implements dao.IThongKeNhanVienDAO {

    private Timestamp createTimestamp(LocalDate date, LocalTime time) {
        return Timestamp.valueOf(LocalDateTime.of(date, time));
    }

    // ======================================================================
    //  HELPER: Chạy native SQL qua Hibernate Session.doWork
    // ======================================================================
    private <R> R doWithConnection(SqlWork<R> work) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            final Object[] result = {null};
            final RuntimeException[] error = {null};

            session.doWork(conn -> {
                try {
                    result[0] = work.execute(conn);
                } catch (SQLException e) {
                    error[0] = new RuntimeException(e);
                }
            });

            if (error[0] != null) throw error[0];
            return (R) result[0];
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // ====================== TỔNG SỐ HÓA ĐƠN HOÀN THÀNH ======================
    @Override
    public int getTongSoHoaDonBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE nhanVienID = ? "
                + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

        return doWithConnection(conn -> {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                pst.setTimestamp(2, createTimestamp(ngay, gioBD));
                pst.setTimestamp(3, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        });
    }

    // ====================== TỔNG SỐ HÓA ĐƠN ĐỔI / TRẢ ======================
    @Override
    public int getTongSoHoaDonDoiTra(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%') "
                + "AND nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

        return doWithConnection(conn -> {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                pst.setTimestamp(2, createTimestamp(ngay, gioBD));
                pst.setTimestamp(3, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        });
    }

    // ====================== TỔNG SỐ VÉ BÁN ======================
    @Override
    public int getTongSoVeBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT SUM(ct.soLuong) FROM HoaDonChiTiet ct " + "JOIN HoaDon hd ON hd.hoaDonID = ct.hoaDonID "
                + "WHERE ct.loaiDichVu = ? " + "AND hd.nhanVienID = ? "
                + "AND hd.thoiDiemTao >= ? AND hd.thoiDiemTao <= ? ";

        return doWithConnection(conn -> {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, LoaiDichVuEnums.VE_BAN.toString());
                pst.setString(2, maNV);
                pst.setTimestamp(3, createTimestamp(ngay, gioBD));
                pst.setTimestamp(4, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return 0;
        });
    }

    // ====================== TỔNG TIỀN CHUYỂN KHOẢN ======================
    @Override
    public double getTongTienChuyenKhoan(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT SUM(CASE WHEN isThanhToanTienMat = 0 THEN tongTien ELSE 0 END) "
                + "FROM HoaDon WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

        return doWithConnection(conn -> {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                pst.setTimestamp(2, createTimestamp(ngay, gioBD));
                pst.setTimestamp(3, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
            return 0.0;
        });
    }

    // ====================== TỔNG TIỀN MẶT ======================
    @Override
    public double getTongTienMat(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT SUM(CASE WHEN isThanhToanTienMat = 1 THEN tongTien ELSE 0 END) "
                + "FROM HoaDon WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

        return doWithConnection(conn -> {
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                pst.setTimestamp(2, createTimestamp(ngay, gioBD));
                pst.setTimestamp(3, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble(1);
                    }
                }
            }
            return 0.0;
        });
    }

    // ====================== LẤY DANH SÁCH HÓA ĐƠN (ĐÃ SỬA) ======================
    @Override
    public List<Object[]> getListHoaDonTrongCa(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        String sql = "SELECT hoaDonID, thoiDiemTao, tongTien, isThanhToanTienMat " + "FROM HoaDon "
                + "WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? " + "ORDER BY thoiDiemTao DESC";

        return doWithConnection(conn -> {
            List<Object[]> list = new ArrayList<>();
            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, maNV);
                pst.setTimestamp(2, createTimestamp(ngay, gioBD));
                pst.setTimestamp(3, createTimestamp(ngay, gioKT));
                try (ResultSet rs = pst.executeQuery()) {
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

                    while (rs.next()) {
                        String maHD = rs.getString(1);
                        Timestamp t = rs.getTimestamp(2);
                        double tongTien = rs.getDouble(3);
                        int isTienMat = rs.getInt(4);

                        String hinhThuc;
                        if (isTienMat == 1) {
                            hinhThuc = "Tiền mặt";
                        } else {
                            hinhThuc = "Chuyển khoản";
                        }

                        String tt; // Biến trạng thái để hiển thị (thay cho cột trạng thái trong DB)

                        // Xác định trạng thái hiển thị
                        if (maHD.startsWith("HDHV")) {
                            tt = "Hoàn vé";
                        } else if (maHD.startsWith("HDDV")) {
                            tt = "Đổi vé";
                        } else {
                            tt = "Hoàn thành";
                        }

                        list.add(new Object[]{maHD, t != null ? t.toLocalDateTime().format(f) : "N/A", tongTien, hinhThuc, tt});
                    }
                }
            }
            return list;
        });
    }

    @FunctionalInterface
    interface SqlWork<R> {
        R execute(java.sql.Connection conn) throws SQLException;
    }
}