//package dao;
///*
// * @(#) GiaoDichHoanDoi_DAO.java  1.0  [11:37:32 AM] Nov 1, 2025
// *
// * Copyright (c) 2025 IUH. All rights reserved.
// */
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.time.LocalDateTime;
//
//import connectDB.ConnectDB;
//import entity.Ve;
//
///*
// * @description
// * @author: NguyenThiHuynhNhu
// * @date: Nov 1, 2025
// * @version: 1.0
// */
//
//public class GiaoDichHoanDoi_DAO {
//	private final ConnectDB db = ConnectDB.getInstance();
//
//	/**
//	 * Tính tiền hoàn vé theo quy tắc đơn giản: - Nếu yêu cầu trước giờ khởi hành >
//	 * 48h: hoàn 90% - 24h-48h: 70% - <24h: 50% - Nếu đã đi (USED) hoặc EXPIRED:
//	 * không hoàn (Bạn có thể điều chỉnh luật tùy schema thực tế)
//	 */
//	public double tinhTienHoan(String veID, LocalDateTime yeuCau) {
//		Ve_DAO veDao = new Ve_DAO();
//		Ve v = veDao.findById(veID);
//		if (v == null) {
//			return 0.0;
//		}
//		if ("USED".equalsIgnoreCase(v.getTrangThai()) || "EXPIRED".equalsIgnoreCase(v.getTrangThai())) {
//			return 0.0;
//		}
//
//		// lấy thời gian khởi hành từ Chuyen
//		String sqlChuyen = "SELECT gioKhoiHanh FROM Chuyen WHERE chuyenID = ?";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sqlChuyen)) {
//			ps.setString(1, v.getChuyenID());
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					Timestamp t = rs.getTimestamp("gioKhoiHanh");
//					if (t == null) {
//						return 0.0;
//					}
//					LocalDateTime khoiHanh = t.toLocalDateTime();
//					long hours = java.time.Duration.between(yeuCau, khoiHanh).toHours();
//					double ratio = 0.0;
//					if (hours > 48) {
//						ratio = 0.9;
//					} else if (hours > 24) {
//						ratio = 0.7;
//					} else if (hours >= 0) {
//						ratio = 0.5;
//					} else {
//						ratio = 0.0;
//					}
//					return v.getGia() * ratio;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return 0.0;
//	}
//
//	/**
//	 * Xử lý hoàn vé: tính tiền hoàn, ghi DonHoanDoi và cập nhật trạng thái vé + tạo
//	 * giao dịch hoàn tiền
//	 */
//	public boolean xuLyHoanVe(String veID, LocalDateTime yeuCau, String nhanVienID) {
//		double soTienHoan = tinhTienHoan(veID, yeuCau);
//		if (soTienHoan <= 0) {
//			return false;
//		}
//		// Thực tế: insert DonHoanDoi, DonHoanDoiChiTiet, tạo giao dịch hoàn tiền,
//		// update trạng thái vé -> "REFUNDED"
//		try (Connection c = db.getConnection()) {
//			c.setAutoCommit(false);
//			String donHoanID = "DHD_" + System.currentTimeMillis();
//			String insertDHD = "INSERT INTO DonHoanDoi (donHoanDoiID, donDatChoID, khachHangID, nhanVienID, laDonHoan, ngayYeuCau, tongTienHoan, trangThai) "
//					+ "VALUES (?, NULL, NULL, ?, 1, ?, ?, 'APPROVED')";
//			try (PreparedStatement ps = c.prepareStatement(insertDHD)) {
//				ps.setString(1, donHoanID);
//				ps.setString(2, nhanVienID);
//				ps.setTimestamp(3, Timestamp.valueOf(yeuCau));
//				ps.setDouble(4, soTienHoan);
//				ps.executeUpdate();
//			}
//
//			String insertCT = "INSERT INTO DonHoanDoiChiTiet (donHoanDoiChiTietID, donHoanDoiID, veCuID, veMoiID, soTienHoan, phiPhatSinh, ghiChu) VALUES (?, ?, ?, NULL, ?, 0, ?)";
//			try (PreparedStatement ps2 = c.prepareStatement(insertCT)) {
//				ps2.setString(1, "DHDCT_" + System.currentTimeMillis());
//				ps2.setString(2, donHoanID);
//				ps2.setString(3, veID);
//				ps2.setDouble(4, soTienHoan);
//				ps2.setString(5, "Hoàn vé tự động");
//				ps2.executeUpdate();
//			}
//
//			String updVe = "UPDATE Ve SET trangThai = 'REFUNDED' WHERE veID = ?";
//			try (PreparedStatement ps3 = c.prepareStatement(updVe)) {
//				ps3.setString(1, veID);
//				ps3.executeUpdate();
//			}
//
//			// TODO: tạo giao dịch hoàn tiền trong GiaoDichThanhToan nếu schema cần
//
//			c.commit();
//			c.setAutoCommit(true);
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//}