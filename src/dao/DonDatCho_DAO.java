package dao;
/*
 * @(#) DonDatCho_DAO.java  1.0  [11:14:45 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import connectDB.ConnectDB;
import entity.DonDatCho;

public class DonDatCho_DAO {
	private ConnectDB connectDB;

	public DonDatCho_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
	}

	/**
	 * @param ddcID
	 * @param nvID
	 * @param khID
	 * @param now
	 * @return
	 */
	public boolean createDonDatCho(DonDatCho donDatCho) {
		String sql = "INSERT INTO DonDatCho (donDatChoID, nhanVienID, khachHangID, thoiDiemDatCho) VALUES (?, ?, ?, ?)";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, donDatCho.getDonDatChoID());
			pstmt.setString(2, donDatCho.getNhanVien().getNhanVienID());
			pstmt.setString(3, donDatCho.getKhachHang().getKhachHangID());
			pstmt.setTimestamp(4, Timestamp.valueOf(donDatCho.getThoiDiemDatCho()));
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteDonDatCho(String donDatChoID) {
		String sql = "DELETE FROM DonDatCho WHERE donDatChoID = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, donDatChoID);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

//	public DonDatCho findByDonDatChoID(String donDatChoID) {
//		String sql = "SELECT * FROM DonDatCho WHERE donDatChoID = ?";
//		Connection con = connectDB.getConnection();
//		PreparedStatement pstmt = null;
//		ResultSet resultSet = null;
//		try {
//			pstmt = con.prepareStatement(sql);
//			pstmt.setString(1, id);
//			resultSet = pstmt.executeQuery();
//			if (resultSet.next()) {
//				DonDatCho d = new DonDatCho();
//				d.setDonDatChoID(resultSet.getString("donDatChoID"));
//				d.setKhachHang(new KhachHang(resultSet.getString("khachHangID")));
//				d.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
//				java.sql.Timestamp t1 = resultSet.getTimestamp("thoiDiemDatCho");
//				d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
//				java.sql.Timestamp t2 = resultSet.getTimestamp("thoiDiemHetHan");
//				d.setThoiDiemHetHan(t2 == null ? null : t2.toLocalDateTime());
//				d.setTongTien(resultSet.getDouble("tongTien"));
//				d.setTrangThaiDonDatCho(TrangThaiDatCho.valueOf(resultSet.getString("trangThaiDatChoID")));
//				return d;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

//	public List<DonDatCho> findDonDatChoByKhachHang(String khachHangID) {
//		List<DonDatCho> ds = new ArrayList<>();
//		String sql = "SELECT * FROM DonDatCho WHERE khachHangID = ? AND trangThaiDatChoID IN ('PENDING','CONFIRMED')";
//		try (Connection c = db.getConnection(); PreparedStatement pstmt = c.prepareStatement(sql)) {
//			pstmt.setString(1, khachHangID);
//			try (ResultSet rs = pstmt.executeQuery()) {
//				while (rs.next()) {
//					DonDatCho d = new DonDatCho();
//					d.setDonDatChoID(rs.getString("donDatChoID"));
//					d.setKhachHang(rs.getString("khachHangID"));
//					d.setChuyenID(rs.getString("chuyenID"));
//					Timestamp t1 = rs.getTimestamp("thoiDiemDatCho");
//					d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
//					Timestamp t2 = rs.getTimestamp("thoiDiemHetHan");
//					d.setThoiDiemHetHan(t2 == null ? null : t2.toLocalDateTime());
//					d.setTongTien(rs.getDouble("tongTien"));
//					d.setTrangThaiDatChoID(rs.getString("trangThaiDatChoID"));
//					ds.add(d);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ds;
//	}
}