package dao;

import connectDB.ConnectDB;
import entity.KhachHang;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiKhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_DAO {
    private final ConnectDB connectDB;

    public KhachHang_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }
    // Thêm khách hàng
    public boolean themKhachHang(KhachHang kh) {
        String sql = """
                INSERT INTO KhachHang (khachHangID, hoTen, soDienThoai, email, soGiayTo, diaChi, loaiDoiTuongID, loaiKhachHangID)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, kh.getKhachHangID());
            pstmt.setString(2, kh.getHoTen());
            pstmt.setString(3, kh.getSoDienThoai());
            pstmt.setString(4, kh.getEmail());
            pstmt.setString(5, kh.getSoGiayTo());
            pstmt.setString(6, kh.getDiaChi());
            pstmt.setString(7, kh.getLoaiDoiTuong() != null ? kh.getLoaiDoiTuong().name() : null);
            pstmt.setString(8, kh.getLoaiKhachHang() != null ? kh.getLoaiKhachHang().name() : null);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Cập nhật khách hàng
    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = " UPDATE KhachHang SET hoTen = ?, soDienThoai = ?, email = ?, soGiayTo = ?, diaChi = ?, loaiDoiTuongID = ?, loaiKhachHangID = ? WHERE khachHangID = ? ";
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, kh.getHoTen());
            pstmt.setString(2, kh.getSoDienThoai());
            pstmt.setString(3, kh.getEmail());
            pstmt.setString(4, kh.getSoGiayTo());
            pstmt.setString(5, kh.getDiaChi());
            pstmt.setString(6, kh.getLoaiDoiTuong() != null ? kh.getLoaiDoiTuong().name() : null);
            pstmt.setString(7, kh.getLoaiKhachHang() != null ? kh.getLoaiKhachHang().name() : null);
            pstmt.setString(8, kh.getKhachHangID());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tìm khách hàng theo sdt
    public KhachHang timKhachHangTheoSDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE soDienThoai = ?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, sdt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return taoKhachHangTuResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Tìm khách hàng theo sgt
    public KhachHang timKhachHangTheoSoGiayTo(String soGiayTo) {
        String sql = "SELECT * FROM KhachHang WHERE soGiayTo = ?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, soGiayTo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return taoKhachHangTuResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy toàn bộ danh sách khách hàng
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()){
                KhachHang kh = taoKhachHangTuResultSet(rs);
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tạo đối tượng KhachHang từ ResultSet
    private KhachHang taoKhachHangTuResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("khachHangID");
        String hoTen = rs.getString("hoTen");
        String sdt = rs.getString("soDienThoai");
        String email = rs.getString("email");
        String soGiayTo = rs.getString("soGiayTo");
        String diaChi = rs.getString("diaChi");

        LoaiDoiTuong loaiDT = null;
        LoaiKhachHang loaiKH = null;
        try {
            String loaiDoiTuongStr = rs.getString("loaiDoiTuongID");
            String loaiKhachHangStr = rs.getString("loaiKhachHangID");

            if (loaiDoiTuongStr != null) loaiDT = LoaiDoiTuong.valueOf(loaiDoiTuongStr);
            if (loaiKhachHangStr != null) loaiKH = LoaiKhachHang.valueOf(loaiKhachHangStr);
        } catch (IllegalArgumentException ex) {
            System.err.println("Không tìm thấy giá trị tương thích!!" + ex.getMessage());
        }

        return new KhachHang(id, hoTen, sdt, email, soGiayTo, diaChi, loaiDT, loaiKH);
    }
}
