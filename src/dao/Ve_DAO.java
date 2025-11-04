package dao;
/*
 * @(#) Ve_DAO.java  1.0  [11:13:56 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;

import connectDB.ConnectDB;
import entity.Ve;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class Ve_DAO {
	private ConnectDB connectDB = ConnectDB.getInstance();

	public Ve_DAO() {
		connectDB.connect();
	}

	public boolean createVe(Ve ve) {
		Connection conn = connectDB.getConnection();
		String sql = "INSERT INTO Ve (veID, khachHangID, donDatChoID, chuyenID, gheID, gaDiID, gaDenID, ngayGioDi, gia, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
//			System.out.println(veID);
//			System.out.println(veSession.getHanhKhach().getKhachHangID());
//			System.out.println(donDatChoID);
//			System.out.println(veSession.getChuyenID());
//			System.out.println(veSession.getGheID());
//			System.out.println(veSession.getGaDiID());
//			System.out.println(veSession.getGaDenID());
//			System.out
//					.println(java.sql.Timestamp.valueOf(LocalDateTime.of(veSession.getNgayDi(), veSession.getGioDi())));
//			System.out.println(veSession.getGia());
//			System.out.println(TrangThaiVe.DA_BAN.toString());

			ps.setString(1, ve.getVeID());
			ps.setString(2, ve.getKhachHang().getKhachHangID());
			ps.setString(3, ve.getDonDatCho().getDonDatChoID());
			ps.setString(4, ve.getChuyen().getChuyenID());
			ps.setString(5, ve.getGhe().getGheID());
			ps.setString(6, ve.getGaDi().getGaID());
			ps.setString(7, ve.getGaDen().getGaID());
			ps.setTimestamp(8, java.sql.Timestamp.valueOf(ve.getNgayGioDi()));
			ps.setDouble(9, ve.getGia());
			ps.setString(10, ve.getTrangThai().toString());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

//	public boolean updateVe(Ve ve) {
//		String sql = "UPDATE Ve SET donDatChoID=?, chuyenID=?, gheID=?, hanhKhachID=?, thuTuGaDi=?, thuTuGaDen=?, gia=?, trangThai=?, ngayBan=? WHERE veID=?";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, ve.getDonDatChoID());
//			ps.setString(2, ve.getChuyenID());
//			ps.setString(3, ve.getGheID());
//			ps.setString(4, ve.getHanhKhachID());
//			ps.setInt(5, ve.getThuTuGaDi());
//			ps.setInt(6, ve.getThuTuGaDen());
//			ps.setDouble(7, ve.getGia());
//			ps.setString(8, ve.getTrangThai());
//			ps.setTimestamp(9, ve.getThoiDiemBan() == null ? null : Timestamp.valueOf(ve.getThoiDiemBan()));
//			ps.setString(10, ve.getVeID());
//			return ps.executeUpdate() > 0;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public boolean deleteVe(String veID) {
//		String sql = "DELETE FROM Ve WHERE veID = ?";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, veID);
//			return ps.executeUpdate() > 0;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public Ve findById(String veID) {
//		String sql = "SELECT * FROM Ve WHERE veID = ?";
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, veID);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					Ve v = new Ve();
//					v.setVeID(rs.getString("veID"));
//					v.setDonDatChoID(rs.getString("donDatChoID"));
//					v.setChuyenID(rs.getString("chuyenID"));
//					v.setGheID(rs.getString("gheID"));
//					v.setHanhKhachID(rs.getString("hanhKhachID"));
//					v.setThuTuGaDi(rs.getInt("thuTuGaDi"));
//					v.setThuTuGaDen(rs.getInt("thuTuGaDen"));
//					v.setGia(rs.getDouble("gia"));
//					v.setTrangThai(rs.getString("trangThai"));
//					Timestamp t = rs.getTimestamp("ngayBan");
//					v.setThoiDiemBan(t == null ? null : t.toLocalDateTime());
//					return v;
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public List<Ve> findAll() {
//		String sql = "SELECT * FROM Ve";
//		List<Ve> list = new ArrayList<>();
//		try (Connection c = db.getConnection();
//				PreparedStatement ps = c.prepareStatement(sql);
//				ResultSet rs = ps.executeQuery()) {
//			while (rs.next()) {
//				Ve v = new Ve();
//				v.setVeID(rs.getString("veID"));
//				v.setDonDatChoID(rs.getString("donDatChoID"));
//				v.setChuyenID(rs.getString("chuyenID"));
//				v.setGheID(rs.getString("gheID"));
//				v.setHanhKhachID(rs.getString("hanhKhachID"));
//				v.setThuTuGaDi(rs.getInt("thuTuGaDi"));
//				v.setThuTuGaDen(rs.getInt("thuTuGaDen"));
//				v.setGia(rs.getDouble("gia"));
//				v.setTrangThai(rs.getString("trangThai"));
//				Timestamp t = rs.getTimestamp("ngayBan");
//				v.setThoiDiemBan(t == null ? null : t.toLocalDateTime());
//				list.add(v);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//	/* ======= Nghiệp vụ bán vé / giữ chỗ ======= */
//
//	/**
//	 * Tìm các ghế còn trống cho chuyến và đoạn (thuTuGaDi..thuTuGaDen) Logic: chọn
//	 * ghế của các toa thuộc tàu của chuyenId và đảm bảo không có vé hiện thời có
//	 * trạng thái chiếm chỗ trùng chồng lấp đoạn.
//	 */
//	public List<Ghe> timGheConTrong(String chuyenID, int thuTuGaDiMoi, int thuTuGaDenMoi) {
//		String sql = "" + "SELECT g.* FROM Ghe g " + "JOIN Toa t ON g.toaID = t.toaID "
//				+ "JOIN Tau tau ON t.tauID = tau.tauID " + "JOIN Chuyen c ON c.tauID = tau.tauID "
//				+ "WHERE c.chuyenID = ? " + "AND NOT EXISTS (" + "  SELECT 1 FROM Ve v "
//				+ "  WHERE v.gheID = g.gheID AND v.chuyenID = ? AND v.trangThai IN ('BOOKED','RESERVED','CONFIRMED','USED')"
//				+ "    AND (? < v.thuTuGaDen AND ? > v.thuTuGaDi) " + ")";
//		List<Ghe> ds = new ArrayList<>();
//		try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
//			ps.setString(1, chuyenID);
//			ps.setString(2, chuyenID);
//			ps.setInt(3, thuTuGaDenMoi); // new_end > existing_start -> parameter order aligns with condition
//			ps.setInt(4, thuTuGaDiMoi); // new_start < existing_end
//			try (ResultSet rs = ps.executeQuery()) {
//				while (rs.next()) {
//					Ghe g = new Ghe();
//					g.setGheID(rs.getString("gheID"));
//					g.setToaID(rs.getString("toaID"));
//					g.setSoGhe(rs.getString("soGhe"));
//					g.setTrangThai(rs.getBoolean("trangThai"));
//					ds.add(g);
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return ds;
//	}
//
//	/**
//	 * Xác nhận vé: chuyển từ DonDatCho -> Ve (thanh toán xong) Tạo vé (Ve) cho từng
//	 * DonDatChoChiTiet, cập nhật trạng thái DonDatCho
//	 */
//	public boolean xacNhanVeTuDonDatCho(String donDatChoID, String nhanVienThanhToanID) {
//		DonDatCho_DAO rdao = new DonDatCho_DAO();
//		DonDatCho dd = rdao.findById(donDatChoID);
//		if (dd == null) {
//			return false;
//		}
//		DonDatChoChiTiet_DAO ctDao = new DonDatChoChiTiet_DAO();
//		List<DonDatChoChiTiet> ds = ctDao.findByDonDatChoId(donDatChoID);
//		if (ds.isEmpty()) {
//			return false;
//		}
//
//		try (Connection c = db.getConnection()) {
//			c.setAutoCommit(false);
//			Ve_DAO veDao = new Ve_DAO();
//			HoaDon_DAO hdDao = new HoaDon_DAO();
//			// Tạo hóa đơn trước (simple)
//			HoaDon hd = new HoaDon();
//			hd.setHoaDonID("HD_" + System.currentTimeMillis());
//			hd.setKhachHangID(dd.getKhachHangID());
//			hd.setNhanVienID(nhanVienThanhToanID);
//			hd.setThoiDiemTao(LocalDateTime.now());
//			hd.setTamTinh(dd.getTongTien());
//			hd.setTongTien(dd.getTongTien());
//			hd.setTrangThai(true);
//			if (!hdDao.insert(hd)) {
//				c.rollback();
//				c.setAutoCommit(true);
//				return false;
//			}
//
//			// Tạo vé + chi tiết hóa đơn
//			for (DonDatChoChiTiet ct : ds) {
//				Ve v = new Ve();
//				v.setVeID("VE_" + System.currentTimeMillis() + "_" + ct.getGheID());
//				v.setDonDatChoID(donDatChoID);
//				v.setChuyenID(dd.getChuyenID());
//				v.setGheID(ct.getGheID());
//				v.setHanhKhachID(ct.getHanhKhachID());
//				v.setThuTuGaDi(ct.getThuTuGaDi());
//				v.setThuTuGaDen(ct.getThuTuGaDen());
//				v.setGia(ct.getGia());
//				v.setTrangThai("BOOKED");
//				v.setThoiDiemBan(LocalDateTime.now());
//				if (!veDao.insert(v)) {
//					c.rollback();
//					c.setAutoCommit(true);
//					return false;
//				}
//
//				// thêm chi tiết hóa đơn liên kết vé: HoaDonChiTiet_DAO
//				HoaDonChiTiet_DAO hdcDao = new HoaDonChiTiet_DAO();
//				HoaDonChiTiet hdc = new HoaDonChiTiet();
//				hdc.setHoaDonChiTietID("HDC_" + System.currentTimeMillis());
//				hdc.setHoaDonID(hd.getHoaDonID());
//				hdc.setLoaiDichVu("VEXE");
//				hdc.setMatHangID("VE");
//				hdc.setTenMatHang("Vé " + v.getVeID());
//				hdc.setDonGia(v.getGia());
//				hdc.setSoLuong(1);
//				hdc.setTienNhan(v.getGia());
//				hdc.setThue(0);
//				hdc.setVeID(v.getVeID());
//				if (!hdcDao.insert(hdc)) {
//					c.rollback();
//					c.setAutoCommit(true);
//					return false;
//				}
//			}
//
//			// cập nhật DonDatCho trạng thái -> CONFIRMED
//			dd.setTrangThaiDatChoID("CONFIRMED");
//			if (!rdao.update(dd)) {
//				c.rollback();
//				c.setAutoCommit(true);
//				return false;
//			}
//
//			c.commit();
//			c.setAutoCommit(true);
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
}