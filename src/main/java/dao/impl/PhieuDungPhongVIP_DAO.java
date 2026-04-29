package dao.impl;
/*
 * @(#) PhieuDungPhongVIP_DAO.java  1.0  [8:57:41 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;

public class PhieuDungPhongVIP_DAO {
	private ConnectDB connectDB = ConnectDB.getInstance();

	public PhieuDungPhongVIP_DAO() {
		connectDB.connect();
	}

	public boolean createPhieuDungPhongVIP(PhieuDungPhongVIP phieuDungPhongChoVIP) {
		Connection conn = connectDB.getConnection();
		String sql = "INSERT INTO PhieuDungPhongVIP (phieuDungPhongVIPID, dichVuPhongChoVIPID, veID, trangThai) "
				+ "VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, phieuDungPhongChoVIP.getPhieuDungPhongChoVIPID());
			ps.setString(2, phieuDungPhongChoVIP.getDichVuPhongChoVIP().getDichVuPhongChoVIPID());
			ps.setString(3, phieuDungPhongChoVIP.getVe().getVeID());
			ps.setString(4, phieuDungPhongChoVIP.getTrangThai().toString());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param conn
	 * @param phieu
	 */
	public boolean insertPhieuDungPhongVIP(Connection conn, PhieuDungPhongVIP phieuDungPhongChoVIP) throws Exception {
		String sql = "INSERT INTO PhieuDungPhongVIP (phieuDungPhongVIPID, dichVuPhongChoVIPID, veID, trangThai) "
				+ "VALUES (?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, phieuDungPhongChoVIP.getPhieuDungPhongChoVIPID());
			ps.setString(2, phieuDungPhongChoVIP.getDichVuPhongChoVIP().getDichVuPhongChoVIPID());
			ps.setString(3, phieuDungPhongChoVIP.getVe().getVeID());
			ps.setString(4, phieuDungPhongChoVIP.getTrangThai().toString());

			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * @param conn
	 * @param veID
	 * @return
	 */
	public PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(Connection conn, String veID) {
		String sql = "SELECT P.phieuDungPhongVIPID, P.dichVuPhongChoVIPID, P.veID, P.trangThai \r\n"
				+ "FROM PhieuDungPhongVIP P JOIN Ve V ON P.veID = V.veID \r\n" + "WHERE V.veID = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, veID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				PhieuDungPhongVIP phieu = new PhieuDungPhongVIP();
				phieu.setPhieuDungPhongChoVIPID(rs.getString("phieuDungPhongVIPID"));
				phieu.setDichVuPhongChoVIP(new DichVuPhongChoVIP(rs.getString("dichVuPhongChoVIPID")));
				phieu.setVe(new Ve(rs.getString("veID")));
				phieu.setTrangThai(TrangThaiPDPVIP.valueOf(rs.getString("trangThai")));
				return phieu;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * @param conn
	 * @param phieuDungPhongChoVIPID
	 * @return
	 */
	public PhieuDungPhongVIP getPhieuDungPhongVIPByID(Connection conn, String phieuDungPhongChoVIPID) {
		String sql = "SELECT * from PhieuDungPhongVIP WHERE phieuDungPhongVIPID = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, phieuDungPhongChoVIPID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				PhieuDungPhongVIP phieu = new PhieuDungPhongVIP();
				phieu.setPhieuDungPhongChoVIPID(rs.getString("phieuDungPhongVIPID"));
				phieu.setDichVuPhongChoVIP(new DichVuPhongChoVIP(rs.getString("dichVuPhongChoVIPID")));
				phieu.setVe(new Ve(rs.getString("veID")));
				phieu.setTrangThai(TrangThaiPDPVIP.valueOf(rs.getString("trangThai")));
				return phieu;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * @param conn
	 * @param phieuDungPhongChoVIPID
	 * @return
	 */
	public PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(String veID) {
		Connection conn = connectDB.getConnection();
		String sql = "SELECT * from PhieuDungPhongVIP WHERE veID = ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, veID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				PhieuDungPhongVIP phieu = new PhieuDungPhongVIP();
				phieu.setPhieuDungPhongChoVIPID(rs.getString("phieuDungPhongVIPID"));
				phieu.setDichVuPhongChoVIP(new DichVuPhongChoVIP(rs.getString("dichVuPhongChoVIPID")));
				phieu.setVe(new Ve(rs.getString("veID")));
				phieu.setTrangThai(TrangThaiPDPVIP.valueOf(rs.getString("trangThai")));
				return phieu;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * @param phieuDungPhongChoVIPID
	 * @param trangThai
	 */
	public boolean updateTrangThaiPhieuDungPhongVIP(Connection conn, String phieuDungPhongChoVIPID,
			TrangThaiPDPVIP trangThai) {
		String sql = "UPDATE PhieuDungPhongVIP SET trangThai = ? WHERE phieuDungPhongVIPID = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, trangThai.toString());
			ps.setString(2, phieuDungPhongChoVIPID);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
