package dao.impl;
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
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.BieuGiaVe;
import entity.Tuyen;
import entity.type.HangToa;
import entity.type.LoaiTau;

public class BieuGiaVe_DAO {
	private final ConnectDB connectDB = ConnectDB.getInstance();

	// Helper: Chuyển đổi Date SQL sang LocalDate
	private LocalDate toLocalDate(Date date) {
		return (date != null) ? date.toLocalDate() : null;
	}

	// Helper: Chuyển đổi LocalDate sang Date SQL
	private Date toSqlDate(LocalDate date) {
		return (date != null) ? Date.valueOf(date) : null;
	}

	public List<BieuGiaVe> getAllBieuGia() {
		List<BieuGiaVe> list = new ArrayList<>();
		String sql = "SELECT * FROM BieuGiaVe ORDER BY doUuTien DESC, ngayBatDau DESC";

		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				BieuGiaVe bg = new BieuGiaVe();
				bg.setBieuGiaVeID(rs.getString("bieuGiaVeID"));

				// Map các object con (Nếu null trong DB thì để object là null hoặc object rỗng
				// tùy logic View)
				String tuyenID = rs.getString("tuyenApDungID");
				bg.setTuyenApDung(tuyenID != null ? new Tuyen(tuyenID) : null);

				String loaiTauID = rs.getString("loaiTauApDungID");
				bg.setLoaiTauApDung(loaiTauID != null ? LoaiTau.valueOf(loaiTauID) : null);

				String hangToaID = rs.getString("hangToaApDungID");
				bg.setHangToaApDung(hangToaID != null ? HangToa.valueOf(hangToaID) : null);

				bg.setMinKm(rs.getInt("minKm"));
				bg.setMaxKm(rs.getInt("maxKm"));

				bg.setDonGiaTrenKm(rs.getObject("donGiaTrenKm") != null ? rs.getDouble("donGiaTrenKm") : 0);
				bg.setGiaCoBan(rs.getObject("giaCoBan") != null ? rs.getDouble("giaCoBan") : 0);
				bg.setPhuPhiCaoDiem(rs.getDouble("phuPhiCaoDiem"));
				bg.setDoUuTien(rs.getInt("doUuTien"));

				bg.setNgayBatDau(toLocalDate(rs.getDate("ngayBatDau")));
				bg.setNgayKetThuc(toLocalDate(rs.getDate("ngayKetThuc")));

				list.add(bg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(list);
		return list;
	}

	public List<BieuGiaVe> getBieuGiaTheoTieuChi(String tuKhoa, String maTuyen, String loaiTau) {
		List<BieuGiaVe> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM BieuGiaVe WHERE 1=1");

		// Logic "Search Flexible": Chỉ thêm điều kiện nếu người dùng có nhập/chọn
		if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
			sql.append(" AND bieuGiaVeID LIKE ?");
		}
		if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty()) {
			sql.append(" AND tuyenApDungID = ?");
		}
		if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả")) {
			sql.append(" AND loaiTauApDungID = ?");
		}

		sql.append(" ORDER BY doUuTien DESC, ngayBatDau DESC");

		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			int index = 1;
			if (tuKhoa != null && !tuKhoa.trim().isEmpty()) ps.setString(index++, "%" + tuKhoa + "%");
			if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty()) ps.setString(index++, maTuyen);
			if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả")) ps.setString(index++, loaiTau);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) list.add(mapRow(rs));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return list;
	}

	private BieuGiaVe mapRow(ResultSet rs) throws SQLException {
		BieuGiaVe bg = new BieuGiaVe();
		bg.setBieuGiaVeID(rs.getString("bieuGiaVeID"));
		String tuyenID = rs.getString("tuyenApDungID");
		bg.setTuyenApDung(tuyenID != null ? new Tuyen(tuyenID) : null);
		String loaiTauID = rs.getString("loaiTauApDungID");
		bg.setLoaiTauApDung(loaiTauID != null ? LoaiTau.valueOf(loaiTauID) : null);
		String hangToaID = rs.getString("hangToaApDungID");
		bg.setHangToaApDung(hangToaID != null ? HangToa.valueOf(hangToaID) : null);
		bg.setMinKm(rs.getInt("minKm"));
		bg.setMaxKm(rs.getInt("maxKm"));
		bg.setDonGiaTrenKm(rs.getObject("donGiaTrenKm") != null ? rs.getDouble("donGiaTrenKm") : 0);
		bg.setGiaCoBan(rs.getObject("giaCoBan") != null ? rs.getDouble("giaCoBan") : 0);
		bg.setPhuPhiCaoDiem(rs.getDouble("phuPhiCaoDiem"));
		bg.setDoUuTien(rs.getInt("doUuTien"));
		bg.setNgayBatDau(toLocalDate(rs.getDate("ngayBatDau")));
		bg.setNgayKetThuc(toLocalDate(rs.getDate("ngayKetThuc")));
		return bg;
	}

	public boolean themBieuGia(BieuGiaVe bg) throws SQLException {
		String sql = "INSERT INTO BieuGiaVe(bieuGiaVeID, tuyenApDungID, loaiTauApDungID, hangToaApDungID, "
				+ "minKm, maxKm, donGiaTrenKm, giaCoBan, phuPhiCaoDiem, doUuTien, ngayBatDau, ngayKetThuc) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, bg.getBieuGiaVeID());

			// Xử lý Nullable Foreign Keys
			ps.setString(2, (bg.getTuyenApDung() != null) ? bg.getTuyenApDung().getTuyenID() : null);
			ps.setString(3, (bg.getLoaiTauApDung() != null) ? bg.getLoaiTauApDung().toString() : null);
			ps.setString(4, (bg.getHangToaApDung() != null) ? bg.getHangToaApDung().toString() : null);

			ps.setInt(5, bg.getMinKm());
			ps.setInt(6, bg.getMaxKm());

			// Xử lý giá (1 trong 2 phải có giá trị, cái kia null)
			if (bg.getDonGiaTrenKm() > 0) {
				ps.setDouble(7, bg.getDonGiaTrenKm());
				ps.setNull(8, Types.DECIMAL);
			} else {
				ps.setNull(7, Types.DECIMAL);
				ps.setDouble(8, bg.getGiaCoBan());
			}

			ps.setDouble(9, bg.getPhuPhiCaoDiem());
			ps.setInt(10, bg.getDoUuTien());
			ps.setDate(11, toSqlDate(bg.getNgayBatDau()));
			ps.setDate(12, toSqlDate(bg.getNgayKetThuc()));

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean capNhatBieuGia(BieuGiaVe bg) {
		String sql = "UPDATE BieuGiaVe SET tuyenApDungID=?, loaiTauApDungID=?, hangToaApDungID=?, "
				+ "minKm=?, maxKm=?, donGiaTrenKm=?, giaCoBan=?, phuPhiCaoDiem=?, doUuTien=?, "
				+ "ngayBatDau=?, ngayKetThuc=? WHERE bieuGiaVeID=?";
		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			// Tương tự như thêm mới
			ps.setString(1, (bg.getTuyenApDung() != null) ? bg.getTuyenApDung().getTuyenID() : null);
			ps.setString(2, (bg.getLoaiTauApDung() != null) ? bg.getLoaiTauApDung().toString() : null);
			ps.setString(3, (bg.getHangToaApDung() != null) ? bg.getHangToaApDung().toString() : null);
			ps.setInt(4, bg.getMinKm());
			ps.setInt(5, bg.getMaxKm());

			if (bg.getDonGiaTrenKm() > 0) {
				ps.setDouble(6, bg.getDonGiaTrenKm());
				ps.setNull(7, Types.DECIMAL);
			} else {
				ps.setNull(6, Types.DECIMAL);
				ps.setDouble(7, bg.getGiaCoBan());
			}

			ps.setDouble(8, bg.getPhuPhiCaoDiem());
			ps.setInt(9, bg.getDoUuTien());
			ps.setDate(10, toSqlDate(bg.getNgayBatDau()));
			ps.setDate(11, toSqlDate(bg.getNgayKetThuc()));
			ps.setString(12, bg.getBieuGiaVeID());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean xoaBieuGia(String id) {
		String sql = "DELETE FROM BieuGiaVe WHERE bieuGiaVeID = ?";
		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public BieuGiaVe getBieuGiaByID(String id) {
		BieuGiaVe bg = null;
		ConnectDB.getInstance();
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {

			String sql = "SELECT * FROM BieuGiaVe WHERE bieuGiaVeID = ?";
			pstm = con.prepareStatement(sql);
			pstm.setString(1, id);
			rs = pstm.executeQuery();

			if (rs.next()) {
				String bieuGiaVeID = rs.getString("bieuGiaVeID");

				java.sql.Date sqlDateBD = rs.getDate("ngayBatDau");
				LocalDate ngayBatDau = (sqlDateBD != null) ? sqlDateBD.toLocalDate() : null;

				java.sql.Date sqlDateKT = rs.getDate("ngayKetThuc");
				LocalDate ngayKetThuc = (sqlDateKT != null) ? sqlDateKT.toLocalDate() : null;

				int minKm = rs.getInt("minKm");
				int maxKm = rs.getInt("maxKm");
				double donGiaTrenKm = rs.getDouble("donGiaTrenKm");
				double giaCoBan = rs.getDouble("giaCoBan");
				double phuPhiCaoDiem = rs.getDouble("phuPhiCaoDiem"); // Mới thêm
				int doUuTien = rs.getInt("doUuTien");                 // Mới thêm

				String tuyenID = rs.getString("tuyenApDungID");
				Tuyen tuyen = new Tuyen(tuyenID);

				String loaiTauStr = rs.getString("loaiTauApDungID");
				LoaiTau loaiTau = null;
				if (loaiTauStr != null) {
					try {
						loaiTau = LoaiTau.valueOf(loaiTauStr);
					} catch (IllegalArgumentException e) {
						for (LoaiTau lt : LoaiTau.values()) {
							if (lt.getDescription().equalsIgnoreCase(loaiTauStr)) {
								loaiTau = lt;
								break;
							}
						}
					}
				}

				String hangToaStr = rs.getString("hangToaApDungID");
				HangToa hangToa = null;
				if (hangToaStr != null) {
					try {
						hangToa = HangToa.valueOf(hangToaStr);
					} catch (IllegalArgumentException e) {
						for (HangToa ht : HangToa.values()) {
							if (ht.name().equalsIgnoreCase(hangToaStr)) {
								hangToa = ht;
								break;
							}
						}
					}
				}

				bg = new BieuGiaVe(
						bieuGiaVeID,
						tuyen,
						loaiTau,
						hangToa,
						minKm,
						maxKm,
						donGiaTrenKm,
						giaCoBan,
						phuPhiCaoDiem,
						doUuTien,
						ngayBatDau,
						ngayKetThuc
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstm != null) pstm.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return bg;
	}
}