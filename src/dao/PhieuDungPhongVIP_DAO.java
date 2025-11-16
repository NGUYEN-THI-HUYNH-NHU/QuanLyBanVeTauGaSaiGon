package dao;
/*
 * @(#) PhieuDungPhongVIP_DAO.java  1.0  [8:57:41 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.PhieuDungPhongVIP;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */

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
	public boolean insertPhieuDungPhongVIP(Connection conn, PhieuDungPhongVIP phieuDungPhongChoVIP) {
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
}
