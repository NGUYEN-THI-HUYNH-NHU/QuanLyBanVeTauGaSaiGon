package dao;
/*
 * @(#) HoaDon_DAO.java  1.0  [11:33:37 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.HoaDon;

public class HoaDon_DAO {
	private final ConnectDB connectDB = ConnectDB.getInstance();

	public HoaDon_DAO() {
		connectDB.connect();
	}

	/**
	 * @param hoaDon
	 * @return
	 */
	public boolean createHoaDon(HoaDon hoaDon) {
		Connection conn = connectDB.getConnection();
		String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tongTien, maGD, tienNhan, tienHoan, isThanhToanTienMat, trangThai) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hoaDon.getHoaDonID());
			ps.setString(2, hoaDon.getKhachHang().getKhachHangID());
			ps.setString(3, hoaDon.getNhanVien().getNhanVienID());
			ps.setTimestamp(4, java.sql.Timestamp.valueOf(hoaDon.getThoiDiemTao()));
			ps.setDouble(5, hoaDon.getTongTien());
			if (hoaDon.getMaGD() != null) {
				ps.setString(6, hoaDon.getMaGD());
				ps.setDouble(8, 0);
			} else {
				ps.setNull(6, 0);
				ps.setDouble(8, hoaDon.getTienHoan());
			}
			ps.setDouble(7, hoaDon.getTienNhan());
			ps.setBoolean(9, hoaDon.isThanhToanTienMat());
			ps.setBoolean(10, hoaDon.isTrangThai());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param conn
	 * @param hoaDon
	 * @return
	 */
	public boolean insertHoaDon(Connection conn, HoaDon hoaDon) throws Exception {
		String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tongTien, maGD, tienNhan, tienHoan, isThanhToanTienMat, trangThai) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hoaDon.getHoaDonID());
			ps.setString(2, hoaDon.getKhachHang().getKhachHangID());
			ps.setString(3, hoaDon.getNhanVien().getNhanVienID());
			ps.setTimestamp(4, java.sql.Timestamp.valueOf(hoaDon.getThoiDiemTao()));
			ps.setDouble(5, hoaDon.getTongTien());
			if (hoaDon.getMaGD() != null) {
				ps.setString(6, hoaDon.getMaGD());
				ps.setDouble(8, 0);
			} else {
				ps.setNull(6, 0);
				ps.setDouble(8, hoaDon.getTienHoan());
			}
			ps.setDouble(7, hoaDon.getTienNhan());
			ps.setBoolean(9, hoaDon.isThanhToanTienMat());
			ps.setBoolean(10, hoaDon.isTrangThai());

			return ps.executeUpdate() > 0;
		}
	}
}