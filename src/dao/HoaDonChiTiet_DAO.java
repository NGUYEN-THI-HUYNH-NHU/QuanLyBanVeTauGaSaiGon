package dao;
/*
 * @(#) HoaDonChiTiet_DAO.java 1.0 [11:34:32 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * 
 * @author: NguyenThiHuynhNhu
 * 
 * @date: Nov 1, 2025
 * 
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.HoaDonChiTiet;

public class HoaDonChiTiet_DAO {
	private final ConnectDB connectDB = ConnectDB.getInstance();

	public HoaDonChiTiet_DAO() {
		connectDB.connect();
	}

	public boolean createHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
		Connection conn = connectDB.getConnection();
		String sql = "INSERT INTO HoaDonChiTiet (hoaDonChiTietID, hoaDonID, veID, phieuDungPhongVIPID, tenDichVu, loaiDichVu, donViTinh, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, hoaDonChiTiet.getHoaDonChiTietID());
			ps.setString(2, hoaDonChiTiet.getHoaDon().getHoaDonID());
			if (hoaDonChiTiet.getVe() != null) {
				ps.setString(3, hoaDonChiTiet.getVe().getVeID());
				ps.setNull(4, 0);
			} else {
				ps.setNull(3, 0);
				ps.setString(4, hoaDonChiTiet.getPhieuDungPhongVIP().getPhieuDungPhongChoVIPID());
			}
			ps.setString(5, hoaDonChiTiet.getTenDichVu());
			ps.setString(6, hoaDonChiTiet.getLoaiDichVu().toString());
			ps.setString(7, hoaDonChiTiet.getDonViTinh());
			ps.setInt(8, hoaDonChiTiet.getSoLuong());
			ps.setDouble(9, hoaDonChiTiet.getDonGia());
			ps.setDouble(10, hoaDonChiTiet.getThanhTien());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
