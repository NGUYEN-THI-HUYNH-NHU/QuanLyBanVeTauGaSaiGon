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
	private ConnectDB connectDB = ConnectDB.getInstance();

	public GiaoDichHoanDoi_DAO() {
		connectDB.connect();
	}

	/**
	 * @param conn
	 * @param gd
	 */
	public boolean insertGiaoDichHoanDoi(Connection conn, GiaoDichHoanDoi giaoDichHoanDoi) throws Exception {
		String sql = "INSERT INTO GiaoDichHoanDoi (giaoDichHoanDoiID, nhanVienID, hoaDonID, veGocID, veMoiID, loaiGiaoDich, lyDo, thoiDiemGiaoDich, phiHoanDoi, soTienChenhLech) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\r\n";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, giaoDichHoanDoi.getGiaoDichHoanDoiID());
			ps.setString(2, giaoDichHoanDoi.getNhanVien().getNhanVienID());
			ps.setString(3, giaoDichHoanDoi.getHoaDon().getHoaDonID());
			ps.setString(4, giaoDichHoanDoi.getVeGoc().getVeID());
			if (giaoDichHoanDoi.getVeMoi() != null) {
				ps.setString(5, giaoDichHoanDoi.getVeMoi().getVeID());
			} else {
				ps.setString(5, null);
			}
			ps.setString(6, giaoDichHoanDoi.getLoaiGiaoDich().toString());
			ps.setString(7, giaoDichHoanDoi.getLyDo());
			ps.setTimestamp(8, java.sql.Timestamp.valueOf(giaoDichHoanDoi.getThoiDiemGiaoDich()));
			ps.setDouble(9, giaoDichHoanDoi.getPhiHoanDoi());
			ps.setDouble(10, giaoDichHoanDoi.getSoTienChenhLech());

			return ps.executeUpdate() > 0;
		}
	}
}