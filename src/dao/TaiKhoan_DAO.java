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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

	public TaiKhoan getTaiKhoanVoiTenDangNhap(String tenDangNhap) {
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
				taiKhoan.setTenDangNhap(resultSet.getString(4));
				taiKhoan.setMatKhauHash(resultSet.getString(5));
				;
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
			String sql = "INSERT INTO TaiKhoan (taiKhoanID, vaiTroTaiKhoanID, nhanVienID, tenDangNhap, matKhauHash, thoiDiemTao, trangThai) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
			Connection con = connectDB.getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, taiKhoan.getTaiKhoanID());
			stmt.setString(2, taiKhoan.getVaiTroTaiKhoan().name());
			stmt.setString(3, taiKhoan.getNhanVien().getNhanVienID());
			stmt.setString(4, taiKhoan.getTenDangNhap());
			stmt.setString(5, taiKhoan.getMatKhauHash());
			stmt.setTimestamp(6, java.sql.Timestamp.valueOf(taiKhoan.getThoiDiemTao()));
			stmt.setBoolean(7, taiKhoan.isHoatDong());
			n = stmt.executeUpdate();

		} catch (SQLException e) {
			System.out.print("Them tai khoan that bai: " + e.getMessage());
			e.printStackTrace();
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

				return new TaiKhoan(taiKhoanID, vaiTroTaiKhoan, nhanVien_DAO.getNhanVienVoiID(nhanVienIDtim),
						tenDangNhap, matKhauHash, thoiDiemTao, isHoatDong);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connectDB.close(stmt, resultSet);
		}
		return null;
	}

	public NhanVien getNhanVienByTenDangNhap(String tenDangNhap, boolean xacThuc) {
		NhanVien nhanVien = null;
		Connection connection = connectDB.getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		String sqlQuery = "select NV.nhanVienID, NV.vaiTroNhanVienID, NV.hoTen, NV.isNu, NV.ngaySinh, NV.soDienThoai, NV.email, NV.diaChi, NV.ngayThamGia, NV.isHoatDong, NV.caLam, NV.avatar"
				+ " from NhanVien NV join TaiKhoan TK on NV.nhanVienID = TK.nhanVienID" + " WHERE TK.tenDangNhap = ?";

		if (getTaiKhoanVoiTenDangNhap(tenDangNhap) != null && xacThuc) {
			try {
				statement = connection.prepareStatement(sqlQuery);
				statement.setString(1, tenDangNhap);
				resultSet = statement.executeQuery();

				if (resultSet.next()) {
					String nhanVienID = resultSet.getString(1);
					VaiTroNhanVien vaiTroNhanVien = VaiTroNhanVien.valueOf(resultSet.getString(2));
					String hoTen = resultSet.getString(3);
					boolean isNu = resultSet.getBoolean(4);
					LocalDate ngaySinh = resultSet.getDate(5).toLocalDate();
					String soDienThoai = resultSet.getString(6);
					String email = resultSet.getString(7);
					String diaChi = resultSet.getString(8);
					LocalDate ngayThamGia = resultSet.getDate(9).toLocalDate();
					boolean isHoatDong = resultSet.getBoolean(10);
					String caLam = resultSet.getString(11);

					byte[] avatar = resultSet.getBytes("avatar");

					nhanVien = new NhanVien(nhanVienID, vaiTroNhanVien, hoTen, isNu, ngaySinh, soDienThoai, email,
							diaChi, ngayThamGia, isHoatDong, caLam, avatar);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				connectDB.close(statement, resultSet);
			}
		}
		return nhanVien;
	}

	public List<TaiKhoan> getDanhSachTaiKhoan() {
		Connection con = connectDB.getConnection();
		String sql = "SELECT * FROM TaiKhoan";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<TaiKhoan> dsTaiKhoan = new ArrayList<>();
		try {
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				String taiKhoanID = rs.getString(1);
				String vaiTroStr = rs.getString(2);
				VaiTroTaiKhoan vaiTroTaiKhoan = VaiTroTaiKhoan.valueOf(vaiTroStr);
				String nhanVienID = rs.getString(3);
				String tenDangNhap = rs.getString(4);
				String matKhauHash = rs.getString(5);
				LocalDateTime thoiDiemTao = rs.getTimestamp(6).toLocalDateTime();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				String thoiDiemTaoStr = thoiDiemTao.format(formatter);
				boolean isHoatDong = rs.getBoolean(7);

				TaiKhoan taiKhoan = new TaiKhoan(taiKhoanID, vaiTroTaiKhoan, nhanVien_DAO.getNhanVienVoiID(nhanVienID),
						tenDangNhap, matKhauHash, thoiDiemTao, isHoatDong);
				dsTaiKhoan.add(taiKhoan);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsTaiKhoan;
	}

	public boolean kiemTraTenDangNhap(String tenDN) {
		Connection con = connectDB.getConnection();
		String sql = "SELECT *FROM TaiKhoan WHERE tenDangNhap = ?";
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, tenDN);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean capNhatTaiKhoan(TaiKhoan tkMoi) {
		Connection con = connectDB.getConnection();
		String sql = "UPDATE TaiKhoan SET vaiTroTaiKhoanID = ?, tenDangNhap = ?, matKhauHash = ?, trangThai = ? WHERE taiKhoanID = ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, tkMoi.getVaiTroTaiKhoan().name());
			stmt.setString(2, tkMoi.getTenDangNhap());
			stmt.setString(3, tkMoi.getMatKhauHash());
			stmt.setBoolean(4, tkMoi.isHoatDong());
			stmt.setString(5, tkMoi.getTaiKhoanID());

			int n = stmt.executeUpdate();
			return n > 0;
		} catch (SQLException e) {
			System.out.print("Cap nhat tai khoan that bai: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public String taoMaTaiKhoanMoi() {
		String maMoi = "TK001";
		Connection con = connectDB.getConnection();
		String sql = "SELECT MAX(TaiKhoanID) AS maCuoi FROM TaiKhoan";
		try (PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				String maCuoi = rs.getString("maCuoi");
				if (maCuoi != null) {
					int so = Integer.parseInt(maCuoi.substring(2)) + 1;
					maMoi = String.format("TK%03d", so);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maMoi;
	}

	public String goiYTenDangNhap(String tenDN) {
		Connection con = connectDB.getConnection();
		String sql = "SELECT tenDangNhap FROM TaiKhoan WHERE tenDangNhap LIKE ?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, tenDN + "%");
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenDangNhap = rs.getString(1);
				int soThuTu = Integer.parseInt(tenDangNhap.substring(tenDN.length()));
				soThuTu++;
				return tenDN + soThuTu;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tenDN;
	}

	// tim kiem tong hop (maNV, tenDangNhap, vaiTro, trangThai)
	public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
		Connection con = connectDB.getConnection();
		StringBuilder sql = new StringBuilder("SELECT * FROM TaiKhoan WHERE 1=1");
		List<TaiKhoan> dsTK = new ArrayList<>();

		if (maNV != null && !maNV.isEmpty()) {
			sql.append(" AND nhanVienID LIKE ?");
		}
		if (tenDN != null && !tenDN.isEmpty()) {
			sql.append(" AND tenDangNhap LIKE ?");
		}
		if (vaiTro != null && !vaiTro.isEmpty()) {
			sql.append(" AND vaiTroTaiKhoanID = ?");
		}
		if (trangThai != null) {
			sql.append(" AND trangThai = ?");
		}

		try (PreparedStatement stmt = con.prepareStatement(sql.toString())) {
			int i = 1;
			if (maNV != null && !maNV.isEmpty()) {
				stmt.setString(i++, "%" + maNV + "%");
			}
			if (tenDN != null && !tenDN.isEmpty()) {
				stmt.setString(i++, "%" + tenDN + "%");
			}
			if (vaiTro != null && !vaiTro.isEmpty()) {
				stmt.setString(i++, vaiTro);
			}
			if (trangThai != null) {
				stmt.setBoolean(i++, trangThai);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String taiKhoanID = rs.getString(1);
				VaiTroTaiKhoan vaiTroTK = VaiTroTaiKhoan.valueOf(rs.getString(2));
				String nhanVienID = rs.getString(3);
				String tenDangNhap = rs.getString(4);
				String matKhauHash = rs.getString(5);
				LocalDateTime thoiDiemTao = rs.getTimestamp(6).toLocalDateTime();
				boolean isHoatDong = rs.getBoolean(7);

				TaiKhoan taiKhoan = new TaiKhoan(taiKhoanID, vaiTroTK, nhanVien_DAO.getNhanVienVoiID(nhanVienID),
						tenDangNhap, matKhauHash, thoiDiemTao, isHoatDong);
				dsTK.add(taiKhoan);
			}
			return dsTK;
		} catch (SQLException e) {
			System.out.print("Tim kiem tong hop tai khoan that bai: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}