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
//
//public class HoaDonChiTiet_DAO {
//	private final ConnectDB db = ConnectDB.getInstance();
//
//	public boolean insert(HoaDonChiTiet hdc) {
//		String sql = "INSERT INTO HoaDonChiTiet (hoaDonChiTietID, hoaDonID, loaiDichVu, matHangID, tenMatHang, donGia, soLuong, soTien, thue, veID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, hdc.getHoaDonChiTietID());
//			ps.setString(2, hdc.getHoaDonID());
//			ps.setString(3, hdc.getLoaiDichVu());
//			ps.setString(4, hdc.getMatHangID());
//			ps.setString(5, hdc.getTenMatHang());
//			ps.setDouble(6, hdc.getDonGia());
//			ps.setInt(7, hdc.getSoLuong());
//			ps.setDouble(8, hdc.getTienNhan());
//			ps.setDouble(9, hdc.getThue());
//			ps.setString(10, hdc.getVeID());
//			return ps.executeUpdate() > 0;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	// update/delete/findById/findAll tương tự có thể thêm khi cần
//}
