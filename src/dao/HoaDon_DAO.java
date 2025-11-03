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
			ps.setString(6, hoaDon.getMaGD());
			ps.setDouble(7, hoaDon.getTienNhan());
			ps.setDouble(8, hoaDon.getTienHoan());
			ps.setBoolean(9, hoaDon.isThanhToanTienMat());
			ps.setBoolean(10, hoaDon.isTrangThai());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

//	public boolean insert(HoaDon hd) {
//		String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tamTinh, tongGiamGia, tongThue, tongTien, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, hd.getHoaDonID());
//			ps.setString(2, hd.getKhachHangID());
//			ps.setString(3, hd.getNhanVienID());
//			ps.setTimestamp(4, hd.getThoiDiemTao() == null ? null : Timestamp.valueOf(hd.getThoiDiemTao()));
//			ps.setDouble(5, hd.getTamTinh());
//			ps.setDouble(6, 0); // tongGiamGia
//			ps.setDouble(7, 0); // tongThue
//			ps.setDouble(8, hd.getTongTien());
//			ps.setBoolean(9, hd.isTrangThai());
//			return ps.executeUpdate() > 0;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public HoaDon findById(String id) {
//		String sql = "SELECT * FROM HoaDon WHERE hoaDonID = ?";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, id);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					HoaDon h = new HoaDon();
//					h.setHoaDonID(rs.getString("hoaDonID"));
//					h.setKhachHangID(rs.getString("khachHangID"));
//					h.setNhanVienID(rs.getString("nhanVienID"));
//					Timestamp t = rs.getTimestamp("thoiDiemTao");
//					h.setThoiDiemTao(t == null ? null : t.toLocalDateTime());
//					h.setTamTinh(rs.getDouble("tamTinh"));
//					h.setTongTien(rs.getDouble("tongTien"));
//					h.setTrangThai(rs.getBoolean("trangThai"));
//					return h;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public List<HoaDon> findByKhachHang(String khachHangID) {
//		String sql = "SELECT * FROM HoaDon WHERE khachHangID = ?";
//		List<HoaDon> ds = new ArrayList<>();
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, khachHangID);
//			try (ResultSet rs = ps.executeQuery()) {
//				while (rs.next()) {
//					HoaDon h = new HoaDon();
//					h.setHoaDonID(rs.getString("hoaDonID"));
//					h.setKhachHangID(rs.getString("khachHangID"));
//					h.setNhanVienID(rs.getString("nhanVienID"));
//					Timestamp t = rs.getTimestamp("thoiDiemTao");
//					h.setThoiDiemTao(t == null ? null : t.toLocalDateTime());
//					h.setTamTinh(rs.getDouble("tamTinh"));
//					h.setTongTien(rs.getDouble("tongTien"));
//					h.setTrangThai(rs.getBoolean("trangThai"));
//					ds.add(h);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ds;
//	}
}