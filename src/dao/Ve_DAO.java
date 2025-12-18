package dao;
/*
 * @(#) Ve_DAO.java  1.0  [11:13:56 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
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

public class Ve_DAO {
	private ConnectDB connectDB = ConnectDB.getInstance();

	public Ve_DAO() {
		connectDB.connect();
	}

	/**
	 * @param donDatChoID
	 * @return
	 */
	public List<Ve> getVeByDonDatChoID(String donDatChoID) {
		String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
				+ "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
				+ "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
				+ "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
				+ "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
				+ "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE donDatChoID = ?";
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
				ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
				ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
				java.sql.Timestamp t = resultSet.getTimestamp("ngayGioDi");
				ve.setNgayGioDi(t.toLocalDateTime());
				ve.setGia(resultSet.getDouble("gia"));
				ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
				ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

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
	public boolean insertVe(Connection conn, Ve ve) throws Exception {
		String sql = "INSERT INTO Ve (veID, khachHangID, donDatChoID, chuyenID, gheID, gaDiID, gaDenID, ngayGioDi, gia, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
		}
	}

	/**
	 * @param conn
	 * @param veID
	 */
	public boolean updateTrangThaiVe(Connection conn, String veID, TrangThaiVe trangThai) throws Exception {
		String sql = "UPDATE Ve SET trangThai = ? WHERE veID = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, trangThai.toString());
			ps.setString(2, veID);

			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * @param veID
	 * @return
	 */
	public Ve getVeByVeID(String veID) {
		String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID\r\n"
				+ "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID JOIN Ghe g ON V.gheID = G.gheID JOIN TOA T ON G.toaID = T.toaID JOIN Tau TAU ON T.tauID = TAU.tauID JOIN GA Ga1 ON V.gaDiID = Ga1.gaID JOIN GA Ga2 ON V.gaDenID = Ga2.gaID\r\n"
				+ "WHERE veID = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, veID);
			resultSet = pstmt.executeQuery();
			if (resultSet.next()) {
				Ve ve = new Ve();
				ve.setVeID(resultSet.getString("veID"));
				ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						LoaiDoiTuong.valueOf(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
				ve.setDonDatCho(new DonDatCho());
				ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
				ve.setGhe(new Ghe(resultSet.getString("gheID"),
						new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
								HangToa.valueOf(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
						resultSet.getInt("soGhe")));
				ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
				ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
				java.sql.Timestamp t = resultSet.getTimestamp("ngayGioDi");
				ve.setNgayGioDi(t.toLocalDateTime());
				ve.setGia(resultSet.getDouble("gia"));
				ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
				return ve;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getVeIDsStartingWith(String baseID) {
		List<String> listIDs = new ArrayList<>();
		String sql = "SELECT veID FROM Ve WHERE veID LIKE ?";
		Connection con = ConnectDB.getInstance().getConnection();
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, baseID + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				listIDs.add(rs.getString("veID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listIDs;
	}

	/**
	 * @param veID
	 * @param daDung
	 * @return
	 */
	public boolean updateTrangThaiVe(String veID, TrangThaiVe trangThai) {
		String sql = "UPDATE Ve SET trangThai = ? WHERE veID = ?";
		Connection conn = connectDB.getConnection();

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, trangThai.toString());
			ps.setString(2, veID);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Ve> getVeByNhanVienID(String nhanVienID) {
		String sql = "SELECT V.veID, V.donDatChoID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
				+ "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
				+ "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
				+ "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
				+ "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
				+ "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE nhanVienID = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<Ve> dsVe = new ArrayList<Ve>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nhanVienID);
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				Ve ve = new Ve();
				ve.setVeID(resultSet.getString("veID"));
				ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						LoaiDoiTuong.valueOf(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
				ve.setDonDatCho(new DonDatCho(resultSet.getString("donDatChoID")));
				ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
				ve.setGhe(new Ghe(resultSet.getString("gheID"),
						new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
								HangToa.valueOf(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
						resultSet.getInt("soGhe")));
				ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
				ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
				java.sql.Timestamp t = resultSet.getTimestamp("ngayGioDi");
				ve.setNgayGioDi(t.toLocalDateTime());
				ve.setGia(resultSet.getDouble("gia"));
				ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
				ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

				dsVe.add(ve);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsVe;
	}
}