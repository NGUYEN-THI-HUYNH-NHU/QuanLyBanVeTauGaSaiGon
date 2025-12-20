package dao;
/*
 * @(#) Nhan.java  1.0  [4:10:00 PM] Sep 25, 2025
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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

public class NhanVien_DAO {

	private ConnectDB connectDB;

	public NhanVien_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
	}

	public List<NhanVien> getNhanVienVoiHoTen(String hoTenTim) {
		Connection connection = connectDB.getConnection();
		String querySQL = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh,"
				+ " soDienThoai, email, diaChi, ngayThamGia, isHoatDong, caLam FROM NhanVien" + " where hoTen LIKE ?";
		List<NhanVien> nhanVienList = new ArrayList<NhanVien>();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
			preparedStatement.setString(1, "%" + hoTenTim + "%");
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
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

				nhanVienList.add(new NhanVien(nhanVienID, vaiTroNhanVien, hoTen, isNu, ngaySinh, soDienThoai, email,
						diaChi, ngayThamGia, isHoatDong, caLam));
			}
			return nhanVienList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	// tạo mã nhân viên tự động
	public String taoMaNhanVienTuDong() {
		String maMoi = "NV001";
		String sql = "SELECT MAX(nhanVienID) AS maCuoi FROM NhanVien";
		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				String maCuoi = rs.getString("maCuoi");
				if (maCuoi != null) {
					int so = Integer.parseInt(maCuoi.substring(2)) + 1;
					maMoi = String.format("NV%03d", so);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maMoi;
	}

	// tìm kiếm nhân viên bằng: sdt, ten, vai tro, trang thai (có thể mà 1 hay nhiều tiêu chí cùng 1 lúc)
	public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVien vaiTro, Boolean isHoatDong) {
		Connection connection = connectDB.getConnection();
		List<NhanVien> ds = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM NhanVien WHERE 1=1");

		if (ten != null && !ten.isEmpty()) {
			sql.append(" AND hoTen LIKE ?");
		}
		if (sdt != null && !sdt.isEmpty()) {
			sql.append(" AND soDienThoai LIKE ?");
		}
		if (vaiTro != null) {
			sql.append(" AND vaiTroNhanVienID = ?");
		}
		if (isHoatDong != null) {
			sql.append(" AND isHoatDong = ?");
		}

		try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

			int i = 1;
			if (ten != null && !ten.isEmpty()) {
				stmt.setString(i++, "%" + ten + "%");
			}
			if (sdt != null && !sdt.isEmpty()) {
				stmt.setString(i++, "%" + sdt + "%");
			}
			if (vaiTro != null) {
				stmt.setString(i++, vaiTro.toString());
			}
			if (isHoatDong != null) {
				stmt.setBoolean(i++, isHoatDong);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				NhanVien nv = new NhanVien(rs.getString("nhanVienID"),
						VaiTroNhanVien.valueOf(rs.getString("vaiTroNhanVienID")), rs.getString("hoTen"),
						rs.getBoolean("isNu"), rs.getDate("ngaySinh").toLocalDate(), rs.getString("soDienThoai"),
						rs.getString("email"), rs.getString("diaChi"), rs.getDate("ngayThamGia").toLocalDate(),
						rs.getBoolean("isHoatDong"), rs.getString("caLam"));
				ds.add(nv);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}

	// lay vai tro nhan vien theo ma nhan vien
	public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
		String sql = "SELECT vaiTroNhanVienID FROM NhanVien WHERE nhanVienID = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maNV);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return VaiTroNhanVien.valueOf(rs.getString("vaiTroNhanVienID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Helper map ResultSet to Entity
	private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
		String nhanVienID = rs.getString("nhanVienID");
		VaiTroNhanVien vaiTro = VaiTroNhanVien.valueOf(rs.getString("vaiTroNhanVienID"));
		String hoTen = rs.getString("hoTen");
		boolean isNu = rs.getBoolean("isNu");

		Date ngaySinhSQL = rs.getDate("ngaySinh");
		LocalDate ngaySinh = ngaySinhSQL != null ? ngaySinhSQL.toLocalDate() : null;

		String sdt = rs.getString("soDienThoai");
		String email = rs.getString("email");
		String diaChi = rs.getString("diaChi");

		Date ngayThamGiaSQL = rs.getDate("ngayThamGia");
		LocalDate ngayThamGia = ngayThamGiaSQL != null ? ngayThamGiaSQL.toLocalDate() : null;

		boolean isHoatDong = rs.getBoolean("isHoatDong");
		String caLam = rs.getString("caLam");
		byte[] avatar = rs.getBytes("avatar");

		NhanVien nv = new NhanVien(nhanVienID, vaiTro, hoTen, isNu, ngaySinh, sdt, email, diaChi, ngayThamGia,
				isHoatDong, caLam);
		nv.setAvatar(avatar);
		return nv;
	}

	public List<NhanVien> getAllNhanVien() {
		String sql = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh,"
				+ "soDienThoai, email, diaChi, ngayThamGia, isHoatDong, caLam, avatar FROM NhanVien";
		List<NhanVien> ds = new ArrayList<>();

		try (Connection con = connectDB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				ds.add(mapResultSetToNhanVien(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ds;
	}

	public NhanVien getNhanVienVoiID(String nhanVienIDTim) {
		String querySQL = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh,"
				+ "soDienThoai, email, diaChi, ngayThamGia, isHoatDong, caLam, avatar FROM NhanVien WHERE nhanVienID = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(querySQL)) {
			ps.setString(1, nhanVienIDTim);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapResultSetToNhanVien(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean themNhanVien(NhanVien nv) {
		String sql = "INSERT INTO NhanVien (nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh, "
				+ "soDienThoai, email, diaChi, ngayThamGia, isHoatDong, caLam, avatar) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nv.getNhanVienID());
			ps.setString(2, nv.getVaiTroNhanVien().toString());
			ps.setString(3, nv.getHoTen());
			ps.setBoolean(4, nv.isNu());
			ps.setDate(5, java.sql.Date.valueOf(nv.getNgaySinh()));
			ps.setString(6, nv.getSoDienThoai());
			ps.setString(7, nv.getEmail());
			ps.setString(8, nv.getDiaChi());
			ps.setDate(9, java.sql.Date.valueOf(nv.getNgayThamGia()));
			ps.setBoolean(10, nv.isHoatDong());
			ps.setString(11, nv.getCaLam());
			ps.setBytes(12, nv.getAvatar()); // Lưu ảnh

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean capNhatNhanVien(NhanVien nv) {
		String sql = "UPDATE NhanVien SET vaiTroNhanVienID = ?, hoTen = ?, isNu = ?, ngaySinh = ?, "
				+ "soDienThoai = ?, email = ?, diaChi = ?, ngayThamGia = ?, isHoatDong = ?, caLam = ?, avatar = ? "
				+ "WHERE nhanVienID = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, nv.getVaiTroNhanVien().toString());
			ps.setString(2, nv.getHoTen());
			ps.setBoolean(3, nv.isNu());
			ps.setDate(4, Date.valueOf(nv.getNgaySinh()));
			ps.setString(5, nv.getSoDienThoai());
			ps.setString(6, nv.getEmail());
			ps.setString(7, nv.getDiaChi());
			ps.setDate(8, Date.valueOf(nv.getNgayThamGia()));
			ps.setBoolean(9, nv.isHoatDong());
			ps.setString(10, nv.getCaLam());
			ps.setBytes(11, nv.getAvatar());
			ps.setString(12, nv.getNhanVienID());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	// Phương thức chuyên biệt chỉ để cập nhật ảnh (nhanh hơn update toàn bộ)
	public boolean capNhatAvatar(String nhanVienID, byte[] avatarData) {
		String sql = "UPDATE NhanVien SET avatar = ? WHERE nhanVienID = ?";
		try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setBytes(1, avatarData);
			ps.setString(2, nhanVienID);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	//lấy danh sách mã nhân viên
	public List<String> layDanhSachMaNhanVien() {
		List<String> danhSachMaNV = new ArrayList<>();
		String sql = "SELECT nhanVienID FROM NhanVien";
		try (Connection con = connectDB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				danhSachMaNV.add(rs.getString("nhanVienID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return danhSachMaNV;
	}

	//lấy danh sách nhân viên chưa có tài khoản
	public List<NhanVien> layNhanVienChuaCoTaiKhoan() {
		List<NhanVien> danhSachNV = new ArrayList<>();
		String sql = "SELECT * FROM NhanVien WHERE nhanVienID NOT IN (SELECT nhanVienID FROM TaiKhoan)";
		try (Connection con = connectDB.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				danhSachNV.add(mapResultSetToNhanVien(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return danhSachNV;
	}



}
