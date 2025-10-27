package dao;
/*
 * @(#) PhieuGiuCho_DAO.java  1.0  [2:03:36 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.PhieuGiuCho;

public class PhieuGiuCho_DAO {

	private ConnectDB connectDB = ConnectDB.getInstance();

	public PhieuGiuCho_DAO() {
		connectDB.connect();
	}

	public boolean createPhieuGiuCho(PhieuGiuCho pgc) {
		Connection conn = connectDB.getConnection();
		String sql = "INSERT INTO PhieuGiuCho (phieuGiuChoID, nhanVienID, thoiDiemTao, trangThai)"
				+ "VALUES (?, ?, SYSUTCDATETIME(), ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, pgc.getPhieuGiuChoID());
			ps.setString(2, pgc.getNhanVien().getNhanVienID());
			ps.setString(3, pgc.getTrangThai());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public PhieuGiuCho getPhieuGiuChoByID(String phieuGiuChoID) {
		Connection conn = connectDB.getConnection();
		String sql = "SELECT * FROM PhieuGiuCho WHERE phieuGiuChoID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, phieuGiuChoID);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					PhieuGiuCho pgc = new PhieuGiuCho();
					pgc.setPhieuGiuChoID(rs.getString("phieuGiuChoID"));

					NhanVien nv = new NhanVien(rs.getString("nhanVienID"));
					pgc.setNhanVien(nv);

					pgc.setThoiDiemTao(rs.getTimestamp("thoiDiemTao").toLocalDateTime());
					pgc.setTrangThai(rs.getString("trangThai"));

					return pgc;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Cập nhật trạng thái của một PhieuGiuCho. Dùng khi xác nhận (XAC_NHAN) hoặc
	 * hủy (HET_HAN).
	 *
	 * @param phieuGiuChoID ID của phiếu cần cập nhật.
	 * @param newTrangThai  Trạng thái mới ('XAC_NHAN' hoặc 'HET_HAN').
	 * @return true nếu cập nhật thành công, false nếu thất bại.
	 */
	public boolean updateTrangThai(String phieuGiuChoID, String newTrangThai) {
		Connection conn = connectDB.getConnection();
		String sql = "UPDATE PhieuGiuCho SET trangThai = ? WHERE phieuGiuChoID = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, newTrangThai);
			ps.setString(2, phieuGiuChoID);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Chạy định kỳ để "dọn dẹp" các phiếu giữ chỗ (bảng cha) đã hết hạn. Chỉ cập
	 * nhật các phiếu 'DANG_GIU' đã quá thời gian quy định.
	 *
	 * @param expiryMinutes Số phút mà một phiếu được coi là hết hạn.
	 * @return Số lượng phiếu (bảng cha) đã được cập nhật sang 'HET_HAN'.
	 */
	public int cleanUpExpiredPhieuGiuCho(int expiryMinutes) {
		Connection conn = connectDB.getConnection();
		String sql = "UPDATE PhieuGiuCho\n" + "SET trangThai = 'HET_HAN'\n" + "WHERE trangThai = 'DANG_GIU'\n"
				+ "  AND thoiDiemTao < DATEADD(minute, -?, SYSUTCDATETIME());";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, expiryMinutes);

			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
}