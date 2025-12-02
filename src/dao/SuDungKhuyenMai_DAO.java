package dao;
/*
 * @(#) SuDungKhuyenMai_DAO.java  1.0  [10:50:42 PM] Dec 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;

import connectDB.ConnectDB;
import entity.SuDungKhuyenMai;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 1, 2025
 * @version: 1.0
 */

public class SuDungKhuyenMai_DAO {
	private final ConnectDB connectDB;

	public SuDungKhuyenMai_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
	}

	public boolean themSuDungKhuyenMai(Connection conn, SuDungKhuyenMai suDungKhuyenMai) throws Exception {
		String sql = "INSERT INTO SuDungKhuyenMai(suDungKhuyenMaiID, khuyenMaiID, hoaDonChiTietID, trangThai) VALUES(?, ?, ?, 'DA_AP_DUNG')";

		try (PreparedStatement pstm = conn.prepareStatement(sql)) {
			pstm.setString(1, suDungKhuyenMai.getSuDungKhuyenMaiID());
			pstm.setString(2, suDungKhuyenMai.getKhuyenMai().getKhuyenMaiID());
			pstm.setString(3, suDungKhuyenMai.getHoaDonChiTiet().getHoaDonChiTietID());
			return pstm.executeUpdate() > 0;
		}
	}

}
