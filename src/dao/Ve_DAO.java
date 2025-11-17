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
			while (resultSet.next()) {
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

	/**
	 * @param donDatChoID
	 * @return
	 */
	public List<Ve> getVeByDonDatChoID(String donDatChoID, TrangThaiVe trangThai) {
		String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, V.gaDenID, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID\r\n"
				+ "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID JOIN Ghe g ON V.gheID = G.gheID JOIN TOA T ON G.toaID = T.toaID JOIN Tau TAU ON T.tauID = TAU.tauID\r\n"
				+ "WHERE V.donDatChoID = ? AND V.trangThai = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<Ve> dsVe = new ArrayList<Ve>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, donDatChoID);
			pstmt.setString(2, trangThai.toString());
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
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

	/**
	 * @param conn
	 * @param v
	 * @return
	 */
	public boolean insertVe(Connection conn, Ve ve) {
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
	 * @param conn
	 * @param veID
	 */
	public boolean updateTrangThaiVe(Connection conn, String veID, TrangThaiVe trangThai) {
		String sql = "UPDATE Ve SET trangThai = ? WHERE veID = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, trangThai.toString());
			ps.setString(2, veID);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}