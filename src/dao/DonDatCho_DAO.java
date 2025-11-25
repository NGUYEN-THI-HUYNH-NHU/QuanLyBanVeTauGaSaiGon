package dao;
/*
 * @(#) DonDatCho_DAO.java  1.0  [11:14:45 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import connectDB.ConnectDB;
import entity.DonDatCho;
import entity.KhachHang;
import entity.NhanVien;

public class DonDatCho_DAO {
	private ConnectDB connectDB;

	public DonDatCho_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
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

	/**
	 * @param donDatChoID
	 * @param soDienThoai
	 * @return
	 */
	public DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
		String sql = "select d.donDatChoID, d.nhanVienID, d.khachHangID, d.thoiDiemDatCho\r\n"
				+ "from DonDatCho d join KhachHang k on d.khachHangID = k.khachHangID\r\n"
				+ "where d.donDatChoID = ? and k.soGiayTo = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, donDatChoID);
			pstmt.setString(2, soGiayTo);
			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				DonDatCho d = new DonDatCho();
				d.setDonDatChoID(resultSet.getString("donDatChoID"));
				d.setNhanVien(new NhanVien(resultSet.getString("nhanVienID")));
				d.setKhachHang(new KhachHang(resultSet.getString("khachHangID")));
				java.sql.Timestamp t1 = resultSet.getTimestamp("thoiDiemDatCho");
				d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
				return d;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param conn
	 * @param donDatCho
	 * @return
	 */
	public boolean insertDonDatCho(Connection conn, DonDatCho donDatCho) throws Exception {
		String sql = "INSERT INTO DonDatCho (donDatChoID, nhanVienID, khachHangID, thoiDiemDatCho) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, donDatCho.getDonDatChoID());
			pstmt.setString(2, donDatCho.getNhanVien().getNhanVienID());
			pstmt.setString(3, donDatCho.getKhachHang().getKhachHangID());
			pstmt.setTimestamp(4, Timestamp.valueOf(donDatCho.getThoiDiemDatCho()));
			return pstmt.executeUpdate() > 0;
		}
	}
}