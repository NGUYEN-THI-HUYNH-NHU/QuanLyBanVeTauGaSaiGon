package dao;
/*
 * @(#) Ve_DAO.java  1.0  [11:13:56 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Chuyen;
import entity.DonDatCho;
import entity.Ga;
import entity.Ghe;
import entity.KhachHang;
import entity.Tau;
import entity.Toa;
import entity.Ve;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.TrangThaiVe;

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

	/**
	 * @param donDatChoID
	 * @return
	 */
	public List<Ve> getVeByDonDatChoID(String donDatChoID) {
		String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, V.gaDenID, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID\r\n"
				+ "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID JOIN Ghe g ON V.gheID = G.gheID JOIN TOA T ON G.toaID = T.toaID JOIN Tau TAU ON T.tauID = TAU.tauID\r\n"
				+ "WHERE donDatChoID = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<Ve> dsVe = new ArrayList<Ve>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, donDatChoID);
			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				Ve ve = new Ve();
				ve.setVeID(resultSet.getString("veID"));
				ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						LoaiDoiTuong.valueOf(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
				ve.setDonDatCho(new DonDatCho(donDatChoID));
				ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
				ve.setGhe(new Ghe(resultSet.getString("gheID"),
						new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
								HangToa.valueOf(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
						resultSet.getInt("soGhe")));
				ve.setGaDi(new Ga(resultSet.getString("gaDiID")));
				ve.setGaDen(new Ga(resultSet.getString("gaDenID")));
				java.sql.Timestamp t = resultSet.getTimestamp("ngayGioDi");
				ve.setNgayGioDi(t.toLocalDateTime());
				ve.setGia(resultSet.getDouble("gia"));
				ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));

				dsVe.add(ve);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsVe;
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
}