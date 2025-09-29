package dao;
/*
 * @(#) DonDatCho_DAO.java  1.0  [11:14:45 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Chuyen;
import entity.DonDatCho;
import entity.KhachHang;
import entity.type.TrangThaiDatCho;

public class DonDatCho_DAO {
    private ConnectDB connectDB;

    public DonDatCho_DAO() {
    	connectDB = ConnectDB.getInstance();
    	connectDB.connect();
    }

//    public boolean insert(DonDatCho d) {
//        String sql = "INSERT INTO DonDatCho (donDatChoID, khachHangID, chuyenID, thoiDiemDatCho, thoiDiemHetHan, tongTien, trangThaiDatChoID) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        Connection con = connectDB.getConnection();
//        PreparedStatement pstmt = null;
//        try {
//        	pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, d.getDonDatChoID());
//            pstmt.setString(2, d.getKhachHang().getKhachHangID());
//            pstmt.setString(3, d.getChuyen().getChuyenID());
//            pstmt.setTimestamp(4, d.getThoiDiemDatCho() == null ? null : java.sql.Timestamp.valueOf(d.getThoiDiemDatCho()));
//            pstmt.setTimestamp(5, d.getThoiDiemHetHan() == null ? null : java.sql.Timestamp.valueOf(d.getThoiDiemHetHan()));
//            pstmt.setDouble(6, d.getTongTien());
//            pstmt.setString(7, d.getTrangThaiDonDatCho().toString());
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) {
//        	e.printStackTrace(); return false;
//        }
//    }
//
//    public boolean update(DonDatCho d) {
//        String sql = "UPDATE DonDatCho SET khachHangID=?, chuyenID=?, thoiDiemDatCho=?, thoiDiemHetHan=?, tongTien=?, trangThaiDatChoID=? WHERE donDatChoID=?";
//        Connection con = connectDB.getConnection();
//        PreparedStatement pstmt = null;
//        try {
//        	pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, d.getKhachHang().getKhachHangID());
//            pstmt.setString(2, d.getChuyen().getChuyenID());
//            pstmt.setTimestamp(3, d.getThoiDiemDatCho() == null ? null : java.sql.Timestamp.valueOf(d.getThoiDiemDatCho()));
//            pstmt.setTimestamp(4, d.getThoiDiemHetHan() == null ? null : java.sql.Timestamp.valueOf(d.getThoiDiemHetHan()));
//            pstmt.setDouble(5, d.getTongTien());
//            pstmt.setString(6, d.getTrangThaiDonDatCho().toString());
//            pstmt.setString(7, d.getDonDatChoID());
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean delete(String id) {
//        String sql = "DELETE FROM DonDatCho WHERE donDatChoID = ?";
//        Connection con = connectDB.getConnection();
//        PreparedStatement pstmt = null;
//
//        try {
//        	pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, id);
//            return pstmt.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public DonDatCho findById(String id) {
//        String sql = "SELECT * FROM DonDatCho WHERE donDatChoID = ?";
//        Connection con = connectDB.getConnection();
//        PreparedStatement pstmt = null;
//		ResultSet resultSet = null;
//        try {
//        	pstmt = con.prepareStatement(sql);
//            pstmt.setString(1, id);
//            resultSet = pstmt.executeQuery();
//            if (resultSet.next()) {
//                DonDatCho d = new DonDatCho();
//                d.setDonDatChoID(resultSet.getString("donDatChoID"));
//                d.setKhachHang(new KhachHang(resultSet.getString("khachHangID")));
//                d.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
//                java.sql.Timestamp t1 = resultSet.getTimestamp("thoiDiemDatCho");
//                d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
//                java.sql.Timestamp t2 = resultSet.getTimestamp("thoiDiemHetHan");
//                d.setThoiDiemHetHan(t2 == null ? null : t2.toLocalDateTime());
//                d.setTongTien(resultSet.getDouble("tongTien"));
//                d.setTrangThaiDonDatCho(TrangThaiDatCho.valueOf(resultSet.getString("trangThaiDatChoID")));
//                return d;
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public List<DonDatCho> findAll() {
//        List<DonDatCho> ds = new ArrayList<>();
//        String sql = "SELECT * FROM DonDatCho";
//        Connection con = connectDB.getConnection();
//        PreparedStatement pstmt = null;;
//        ResultSet rs = null;
//        try {
//        	pstmt = con.prepareStatement(sql);
//        	rs = pstmt.executeQuery();
//            while (rs.next()) {
//                DonDatCho d = new DonDatCho();
//                d.setDonDatChoID(rs.getString("donDatChoID"));
//                d.setKhachHang(new KhachHang(rs.getString("khachHangID")));
//                d.setChuyen(new Chuyen(rs.getString("chuyenID"));
//                Timestamp t1 = rs.getTimestamp("thoiDiemDatCho");
//                d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
//                Timestamp t2 = rs.getTimestamp("thoiDiemHetHan");
//                d.setThoiDiemHetHan(t2 == null ? null : t2.toLocalDateTime());
//                d.setTongTien(rs.getDouble("tongTien"));
//                d.setTrangThaiDatChoID(rs.getString("trangThaiDatChoID"));
//                ds.add(d);
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
//
//    /* Nghiệp vụ: tạo reservation, hủy nếu hết hạn */
//    public String taoDonDatCho(String khachHangID, String chuyenID, double tongTien, LocalDateTime hetHan) {
//        String id = "DDC_" + System.currentTimeMillis();
//        DonDatCho d = new DonDatCho();
//        d.setDonDatChoID(id);
//        d.setKhachHang(khachHangID);
//        d.setChuyenID(chuyenID);
//        d.setThoiDiemDatCho(LocalDateTime.now());
//        d.setThoiDiemHetHan(hetHan);
//        d.setTongTien(tongTien);
//        d.setTrangThaiDatChoID("PENDING");
//        return insert(d) ? id : null;
//    }
//
//    public int huyNeuHetHan() {
//        String sql = "UPDATE DonDatCho SET trangThaiDatChoID = 'EXPIRED' WHERE trangThaiDatChoID = 'PENDING' AND thoiDiemHetHan < ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement pstmt = c.prepareStatement(sql)) {
//            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
//            return pstmt.executeUpdate();
//        } catch (SQLException e) { e.printStackTrace(); return 0; }
//    }
//
//    public List<DonDatCho> findActiveByKhachHang(String khachHangID) {
//        List<DonDatCho> ds = new ArrayList<>();
//        String sql = "SELECT * FROM DonDatCho WHERE khachHangID = ? AND trangThaiDatChoID IN ('PENDING','CONFIRMED')";
//        try (Connection c = db.getConnection();
//             PreparedStatement pstmt = c.prepareStatement(sql)) {
//            pstmt.setString(1, khachHangID);
//            try (ResultSet rs = pstmt.executeQuery()) {
//                while (rs.next()) {
//                    DonDatCho d = new DonDatCho();
//                    d.setDonDatChoID(rs.getString("donDatChoID"));
//                    d.setKhachHang(rs.getString("khachHangID"));
//                    d.setChuyenID(rs.getString("chuyenID"));
//                    Timestamp t1 = rs.getTimestamp("thoiDiemDatCho");
//                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
//                    Timestamp t2 = rs.getTimestamp("thoiDiemHetHan");
//                    d.setThoiDiemHetHan(t2 == null ? null : t2.toLocalDateTime());
//                    d.setTongTien(rs.getDouble("tongTien"));
//                    d.setTrangThaiDatChoID(rs.getString("trangThaiDatChoID"));
//                    ds.add(d);
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
}