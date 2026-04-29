package dao;/*
 * @ (#) TuyenChiTiet_DAO.java   1.0     21/10/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 21/10/2025
 */

import connectDB.ConnectDB;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TuyenChiTiet_DAO {
    private final ConnectDB connectDB;

    public TuyenChiTiet_DAO(){
        connectDB = ConnectDB.getInstance();
    }

    /**
     * Lấy danh sách TuyenChiTiet (các Ga trên tuyến) dựa trên TuyenID.
     * Dữ liệu trả về chứa đủ thông tin Ga và Mô tả Tuyen cần thiết cho nghiệp vụ.
     * @param tuyenID ID của tuyến.
     * @return List<TuyenChiTiet> chứa chi tiết các ga trên tuyến, sắp xếp theo thứ tự.
     */
    public List<TuyenChiTiet> layDanhSachTheoTuyenID(String tuyenID){
        List<TuyenChiTiet> danhSach = new ArrayList<>();
        String sql = "SELECT tct.tuyenID, tct.gaID, tct.thuTu, tct.khoangCachTuGaXuatPhatKm, " +
                "t.moTa, ga.tenGa " +
                "FROM TuyenChiTiet tct " +
                "JOIN Tuyen t ON tct.tuyenID = t.tuyenID " +
                "JOIN Ga ga ON tct.gaID = ga.gaID " +
                "WHERE tct.tuyenID = ? " +
                "ORDER BY tct.thuTu ASC";
        try(Connection con = connectDB.getConnection();
            PreparedStatement pstm = con.prepareStatement(sql)) {
            pstm.setString(1, tuyenID);
            try(ResultSet rs = pstm.executeQuery()) {
                while (rs.next()) {
                    Tuyen tuyen = new Tuyen(rs.getString("tuyenID"), rs.getString("moTa"));
                    Ga ga = new Ga(rs.getString("gaID"), rs.getString("tenGa"));
                    TuyenChiTiet tct = new TuyenChiTiet(
                            tuyen,
                            ga,
                            rs.getInt("thuTu"),
                            rs.getInt("khoangCachTuGaXuatPhatKm")
                    );
                    danhSach.add(tct);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
            return danhSach;
    }

    /**
     * Thêm danh sách TuyenChiTiet vào CSDL bằng Batch Insert trong một giao dịch (Transaction).
     * @param danhSachChiTiet Danh sách các chi tiết tuyến cần thêm.
     * @return boolean true nếu thêm thành công.
     */
    public boolean themDanhSachChiTiet(List<TuyenChiTiet> danhSachChiTiet) {
        String sql = "INSERT INTO TuyenChiTiet (tuyenID, gaID, thuTu, khoangCachTuGaXuatPhatKm) VALUES (?, ?, ?, ?)";
        Connection con = null;
        try {
            con = connectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement pstmtChiTiet = con.prepareStatement(sql)) {
                for (TuyenChiTiet chiTiet : danhSachChiTiet) {
                    pstmtChiTiet.setString(1, chiTiet.getTuyen().getTuyenID());
                    pstmtChiTiet.setString(2, chiTiet.getGa().getGaID());
                    pstmtChiTiet.setInt(3, chiTiet.getThuTu());
                    pstmtChiTiet.setInt(4, chiTiet.getKhoangCachTuGaXuatPhatKm());
                    pstmtChiTiet.addBatch();
                }
                pstmtChiTiet.executeBatch();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }

    /**
     * Xóa tất cả TuyenChiTiet của một Tuyen. Thường được dùng trong nghiệp vụ Cập nhật.
     * @param tuyenID ID của tuyến cần xóa chi tiết.
     * @return boolean true nếu xóa thành công ít nhất một bản ghi.
     */
    public boolean xoaChiTietTheoTuyenID(String tuyenID) {
        String sql = "DELETE FROM TuyenChiTiet WHERE tuyenID = ?";
        try (Connection con = connectDB.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, tuyenID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
