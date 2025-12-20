package dao;

import connectDB.ConnectDB;
import entity.NhatKyAudit;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NhatKyAudit_DAO {
    private final ConnectDB connectDB;

    public NhatKyAudit_DAO() {
        connectDB = ConnectDB.getInstance();
    }

    // ghi vào nhật ký
    public void ghiNhatKyAudit(NhatKyAudit nhatKy) {
        String sql = "INSERT INTO NhatKyAudit " +
                "(nhatKyID, doiTuongID, nhanVienID, thoiDiemThaoTac, loaiThaoTac, chiTiet, doiTuongThaoTac) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nhatKy.getNhatKyAuditID());
            ps.setString(2, nhatKy.getDoiTuongID());
            ps.setString(3, nhatKy.getNhanVienID());
            ps.setTimestamp(4, Timestamp.valueOf(nhatKy.getThoiDiemThaoTac()));
            ps.setString(5, nhatKy.getLoaiThaoTac().name());
            ps.setString(6, nhatKy.getChiTiet());
            ps.setString(7, nhatKy.getDoiTuongThaoTac());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // lấy danh sách nhật ký
    public List<NhatKyAudit> layDanhSachNhatKy() {
        String sql = "SELECT * FROM NhatKyAudit ORDER BY thoiDiemThaoTac DESC";
        List<NhatKyAudit> danhSachNhatKy = new ArrayList<>();

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                danhSachNhatKy.add(mapRow(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachNhatKy;
    }


    public String maNhatKyMoi() {
        String sql = "SELECT MAX(nhatKyID) AS MaxID FROM NhatKyAudit";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String maxID = rs.getString("MaxID");


                if (maxID == null) {
                    return "NK00001";
                }

                int number = Integer.parseInt(maxID.substring(2));
                return "NK" + String.format("%05d", number + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "NK00001";
    }

    // lọc/tìm kiếm nhật ký theo nhiều tiêu chí kết hợp
    public List<NhatKyAudit> timKiemNhatKy(LocalDate tuNgay,
                                           LocalDate denNgay,
                                           String nhanVienID,
                                           String loaiThaoTac,
                                           String doiTuongID) {

        List<NhatKyAudit> ds = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM NhatKyAudit WHERE 1=1");

        if (tuNgay != null && denNgay != null) {
            sql.append(" AND CAST(thoiDiemThaoTac AS DATE) BETWEEN ? AND ?");
        }

        if (nhanVienID != null && !nhanVienID.equals("TẤT CẢ")) {
            sql.append(" AND nhanVienID = ?");
        }

        if (loaiThaoTac != null && !loaiThaoTac.isBlank() && !loaiThaoTac.equals("TẤT CẢ")) {
            sql.append(" AND loaiThaoTac = ?"); // ✅ sửa chỗ bị trống "AND  = ?"
        }

        if (doiTuongID != null && !doiTuongID.isBlank()) {
            sql.append(" AND doiTuongID LIKE ?");
        }

        sql.append(" ORDER BY thoiDiemThaoTac DESC");

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;

            if (tuNgay != null && denNgay != null) {
                ps.setDate(i++, Date.valueOf(tuNgay));
                ps.setDate(i++, Date.valueOf(denNgay));
            }

            if (nhanVienID != null && !nhanVienID.equals("TẤT CẢ")) {
                ps.setString(i++, nhanVienID);
            }

            if (loaiThaoTac != null && !loaiThaoTac.isBlank() && !loaiThaoTac.equals("TẤT CẢ")) {
                ps.setString(i++, loaiThaoTac);
            }

            if (doiTuongID != null && !doiTuongID.isBlank()) {
                ps.setString(i++, "%" + doiTuongID + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }

    // ánh xạ 1 dòng ResultSet -> NhatKyAudit
    private NhatKyAudit mapRow(ResultSet rs) throws SQLException {
        return new NhatKyAudit(
                rs.getString("nhatKyID"),
                rs.getString("doiTuongID"),
                rs.getString("nhanVienID"),
                rs.getTimestamp("thoiDiemThaoTac").toLocalDateTime(),
                entity.type.NhatKyAudit.valueOf(rs.getString("loaiThaoTac")),
                rs.getString("chiTiet"),
                rs.getString("doiTuongThaoTac")
        );
    }

    // lấy tên nhân viên theo mã
    public String layTenNhanVienTheoMaNV(String nhanVienID) {
        String sql = "SELECT hoTen FROM NhanVien WHERE nhanVienID = ?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nhanVienID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("hoTen");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // lọc theo khoảng thời gian
    public List<NhatKyAudit> locNhatKyTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        List<NhatKyAudit> ds = new ArrayList<>();
        String sql = "SELECT * FROM NhatKyAudit " +
                "WHERE CAST(thoiDiemThaoTac AS DATE) BETWEEN ? AND ? " +
                "ORDER BY thoiDiemThaoTac DESC";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(ngayBatDau));
            ps.setDate(2, Date.valueOf(ngayKetThuc));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ds.add(mapRow(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ds;
    }
}
