package dao;

/*
 * @(#) GiaoDichHoanDoi_DAO.java  1.0  [11:37:32 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.PreparedStatement;

import connectDB.ConnectDB;
import entity.GiaoDichHoanDoi;

public class GiaoDichHoanDoi_DAO {
	private final ConnectDB db = ConnectDB.getInstance();

	/**
	 * @param conn
	 * @param gd
	 */
	public boolean insertGiaoDichHoanDoi(Connection conn, GiaoDichHoanDoi giaoDichHoanDoi) {
		String sql = "INSERT INTO GiaoDichHoanDoi (giaoDichHoanDoiID, nhanVienID, hoaDonID, veGocID, veMoiID, loaiGiaoDich, lyDo, thoiDiemGiaoDich, phiHoanDoi, soTienChenhLech) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\r\n";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setString(1, giaoDichHoanDoi.getGiaoDichHoanDoiID());
			ps.setString(2, giaoDichHoanDoi.getNhanVien().getNhanVienID());
			ps.setString(3, giaoDichHoanDoi.getHoaDon().getHoaDonID());
			ps.setString(4, giaoDichHoanDoi.getVeGoc().getVeID());
			ps.setNull(5, 0);
			ps.setString(6, giaoDichHoanDoi.getLoaiGiaoDich().toString());
			ps.setString(7, giaoDichHoanDoi.getLyDo());
			ps.setTimestamp(8, java.sql.Timestamp.valueOf(giaoDichHoanDoi.getThoiDiemGiaoDich()));
			ps.setDouble(9, giaoDichHoanDoi.getPhiHoanDoi());
			ps.setDouble(10, giaoDichHoanDoi.getSoTienChenhLech());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}