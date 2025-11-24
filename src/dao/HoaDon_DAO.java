package dao;
/*
 * @(#) HoaDon_DAO.java  1.0  [11:33:37 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {
	private final ConnectDB connectDB = ConnectDB.getInstance();

	public HoaDon_DAO() {
		connectDB.connect();
	}

	/**
	 * @param conn
	 * @param hoaDon
	 * @return
	 */
	public boolean insertHoaDon(Connection conn, HoaDon hoaDon) throws Exception {
		String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tongTien, maGD, tienNhan, tienHoan, isThanhToanTienMat) "
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

			return ps.executeUpdate() > 0;
		}
	}

	public List<HoaDon> searchAndFilter(String keyword, String searchType, String loaiHoaDon, String khachHangInfo,
			LocalDate fromDate, LocalDate toDate, String phuongThucTT) {

		return null;

	}

	/**
	 * @param nhanVien
	 * @return
	 */
	public List<HoaDon> getHoaDonByNhanVien(NhanVien nhanVien) {
		String sql = "SELECT H.hoaDonID, H.khachHangID, K.hoTen, K.soGiayTo, H.thoiDiemTao, H.tongTien, H.tienNhan, H.tienHoan, H.isThanhToanTienMat, H.maGD\r\n"
				+ "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID\r\n" + "WHERE H.nhanVienID = ?";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<HoaDon> dsHoaDon = new ArrayList<HoaDon>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nhanVien.getNhanVienID());
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				HoaDon hoaDon = new HoaDon();
				hoaDon.setHoaDonID(resultSet.getString("hoaDonID"));
				hoaDon.setNhanVien(nhanVien);
				hoaDon.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						resultSet.getString("soGiayTo")));
				hoaDon.setThoiDiemTao(resultSet.getTimestamp("thoiDiemTao").toLocalDateTime());
				hoaDon.setTongTien(resultSet.getDouble("tongTien"));
				hoaDon.setTienNhan(resultSet.getDouble("tienNhan"));
				hoaDon.setTienHoan(resultSet.getDouble("tienHoan"));
				hoaDon.setThanhToanTienMat(resultSet.getBoolean("isThanhToanTienMat"));
				hoaDon.setMaGD(resultSet.getString("maGD"));

				dsHoaDon.add(hoaDon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsHoaDon;
	}
}