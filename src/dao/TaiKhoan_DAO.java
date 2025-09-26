package dao;
/*
 * @(#) TaiKhoan_DAO.java  1.0  [4:09:04 PM] Sep 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 25, 2025
 * @version: 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.type.VaiTroNhanVien;
import entity.type.VaiTroTaiKhoan;


public class TaiKhoan_DAO {
	private ConnectDB connectDB;
	private NhanVien_DAO nhanVien_DAO;

	public TaiKhoan_DAO() {
		this.nhanVien_DAO = new NhanVien_DAO();
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
	}

	public TaiKhoan getTaiKhoanByTenDangNhap(String tenDangNhap) {
		TaiKhoan taiKhoan = null;
		Connection connection = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			stmt = connection.prepareStatement("SELECT * FROM TaiKhoan WHERE tenDangNhap = ?");
			stmt.setString(1, tenDangNhap);
			resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				taiKhoan = new TaiKhoan();
				taiKhoan.setTenDangNhap(resultSet.getString(2));
				taiKhoan.setMatKhauHash(resultSet.getString(3));;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectDB.close(stmt, resultSet);
		}

		return taiKhoan;
	}

	public boolean taoTaiKhoan(TaiKhoan taiKhoan) {
		PreparedStatement stmt = null;
		int n = 0;

		try {
			String sql = "INSERT INTO TaiKhoan (vaiTroTaiKhoanID, nhanVienID, tenDangNhap, matKhauHash, thoiDiemTao, trangThai) VALUES (?, ?, ?, ?, ?, ?)";
			stmt = connectDB.getConnection().prepareStatement(sql);
			stmt.setString(1, taiKhoan.getVaiTroTaiKhoan().getDescription());
			stmt.setString(2, taiKhoan.getNhanVien().getNhanVienID());
			stmt.setString(3, taiKhoan.getTenDangNhap());
			stmt.setString(4, taiKhoan.getMatKhauHash());
			stmt.setTimestamp(5, java.sql.Timestamp.valueOf(taiKhoan.getThoiDiemTao()));
			stmt.setBoolean(6, taiKhoan.isHoatDong());
			
			n = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectDB.close(stmt, null);
		}

		return n > 0;
	}

	public boolean capNhatMatKhau(String nhanVienID, String newMatKhau) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean success = false;

		try {
			connection = connectDB.getConnection();
			String sql = "UPDATE TaiKhoan SET matKhau = ? WHERE nhanVienID = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, BCrypt.hashpw(newMatKhau, BCrypt.gensalt()));
			preparedStatement.setString(2, nhanVienID);

			int rowsUpdated = preparedStatement.executeUpdate();
			if (rowsUpdated > 0) {
				System.out.println("Cap nhat mat khau thanh cong.");
				success = true;
			} else {
				System.out.println("Cap nhat mat khau khong thanh cong.");
			}
		} catch (SQLException e) {
			System.out.println("Loi cap nhat mat khau: " + e.getMessage());
		} finally {
			connectDB.close(preparedStatement, null);
		}

		return success;
	}
	
	public boolean isTaiKhoanTonTai(String tenDangNhap) {
		Connection connection = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet resultSet = null;

		try {
			stmt = connection.prepareStatement("SELECT * FROM TaiKhoan WHERE tenDangNhap = ?");
			stmt.setString(1, tenDangNhap);
			resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectDB.close(stmt, resultSet);
		}
		return false;
	}

	public TaiKhoan getTaiKhoanVoiNhanVienID(String nhanVienIDtim) {
		Connection connection = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		String sqlQuery = "SELECT * FROM TaiKhoan WHERE nhanVienID = ?";

		try {
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setString(1, nhanVienIDtim);
			resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				String taiKhoanID = resultSet.getString(1);
				VaiTroTaiKhoan vaiTroTaiKhoan = VaiTroTaiKhoan.valueOf(resultSet.getString(2));
				String tenDangNhap = resultSet.getString(4);
				String matKhauHash = resultSet.getString(5);
				LocalDateTime thoiDiemTao = resultSet.getTimestamp(6).toLocalDateTime();
				boolean isHoatDong = resultSet.getBoolean(7);
				
				return new TaiKhoan(taiKhoanID, vaiTroTaiKhoan, nhanVien_DAO.getNhanVienVoiID(nhanVienIDtim), tenDangNhap, matKhauHash, thoiDiemTao, isHoatDong);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectDB.close(stmt, resultSet);
		}
		return null;
	}
	
	public NhanVien getNhanVienByTenDangNhap(String tenDangNhap, boolean xacThuc) {
		NhanVien employee = null;
		Connection connection = connectDB.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String sqlQuery = "select *"
				+ " from NhanVien join TaiKhoan on NhanVien.nhanVienID = TaiKhoan.nhanVienID";

		if (getTaiKhoanByTenDangNhap(tenDangNhap) != null && xacThuc) {
			try {
				statement = connection.prepareStatement(sqlQuery);
				statement.setString(1, tenDangNhap);
				resultSet = statement.executeQuery();

				if (resultSet.next()) {
					String id = resultSet.getString("nhanVienID");
					VaiTroNhanVien vaiTro = VaiTroNhanVien.valueOf(resultSet.getString("vaiTroNhanVienID"));
					String hoTen = resultSet.getString("hoTen");
					boolean isNu = resultSet.getBoolean("isNu");
					LocalDate ngaySinh = resultSet.getDate("ngaySinh").toLocalDate();
					String sdt = resultSet.getString("soDienThoai");
					String email = resultSet.getString("email");
					String diaChi = resultSet.getString("diaChi");
					LocalDate ngayThamGia = resultSet.getDate("ngayThamGia").toLocalDate();
					boolean isHoatDong = resultSet.getBoolean("isHoatDong");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				connectDB.close(statement, resultSet);
			}
		}

		return employee;
	}
}