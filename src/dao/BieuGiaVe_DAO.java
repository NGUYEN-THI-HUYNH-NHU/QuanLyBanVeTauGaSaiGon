package dao;
/*
 * @(#) BieuGiaVe_DAO.java 1.0 [11:36:30 AM] Nov 1, 2025
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

//public class BieuGiaVe_DAO {
//	private final ConnectDB db = ConnectDB.getInstance();
//
//	public BieuGiaVe findApplicablePrice(String tuyenID, String hangToaID, String loaiTauID, int km) {
//		String sql = "SELECT TOP 1 * FROM BieuGiaVe WHERE tuyenApDungID = ? AND hangToaApDungID = ? AND LoaiTauApDungID = ? "
//				+ "AND ? >= ISNULL(minKm,0) AND ? <= ISNULL(maxKm, 2147483647) AND isCoHieuLuc = 1 ORDER BY doUuTien DESC, ngayCoHieuLuc DESC";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, tuyenID);
//			ps.setString(2, hangToaID);
//			ps.setString(3, loaiTauID);
//			ps.setInt(4, km);
//			ps.setInt(5, km);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					BieuGiaVe b = new BieuGiaVe();
//					b.setBieuGiaVeID(sql);
//					b.setTuyenApDung(rs.getString("tuyenApDungID"));
//					b.setHangToaApDung(rs.getString("hangToaApDungID"));
//					b.setLoaiTauApDung(rs.getString("LoaiTauApDungID"));
//					b.setDonGiaTrenKm(rs.getDouble("donGiaTrenKm"));
//					b.setGiaCoBan(rs.getDouble("giaCoDinh"));
//					b.setPhuPhiCaoDiem(rs.getDouble("phuPhiCaoDiem"));
//					return b;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * Tính giá vé cơ bản (không gồm khuyến mãi, thuế): - Nếu giá cố định != null ->
//	 * trả giá cố định - else: donGiaTrenKm * km -> cộng phụ phí
//	 */
//	public double tinhGia(String tuyenID, String hangToaID, String loaiTauID, int km) {
//		BieuGiaVe b = findApplicablePrice(tuyenID, hangToaID, loaiTauID, km);
//		if (b == null) {
//			return 0.0;
//		}
//		double base;
//		if (b.getGiaCoBan() != null && b.getGiaCoBan() > 0) {
//			base = b.getGiaCoBan();
//		} else if (b.getDonGiaTrenKm() != null) {
//			base = b.getDonGiaTrenKm() * km;
//		} else {
//			base = 0.0;
//		}
//		if (b.getPhuPhiCaoDiem() != null) {
//			base += b.getPhuPhiCaoDiem();
//		}
//		return base;
//	}
//}