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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

public class NhanVien_DAO {

	private ConnectDB connectionDB;

	public NhanVien_DAO() {
		connectionDB = ConnectDB.getInstance();
		connectionDB.connect();
	}

	public List<NhanVien> getAllNhanVien() {
		Connection connection = connectionDB.getConnection();
		String querySQL = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh, "
				+ "soDienThoai, email, diaChi, ngayThamGia, isHoatDong FROM NhanVien";
		List<NhanVien> nhanVienList = new ArrayList<NhanVien>();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
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

				nhanVienList.add(new NhanVien(nhanVienID, vaiTroNhanVien, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia, isHoatDong));
			}
			return nhanVienList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<NhanVien> getNhanVienVoiHoTen(String hoTenTim) {
		Connection connection = connectionDB.getConnection();
		String querySQL = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh,"
				+ " soDienThoai, email, diaChi, ngayThamGia, isHoatDong FROM NhanVien"
				+ " where hoTen = ?";
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
				
				nhanVienList.add(new NhanVien(nhanVienID, vaiTroNhanVien, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia, isHoatDong));
			}
			return nhanVienList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public NhanVien getNhanVienVoiID(String nhanVienIDTim) {
		Connection connection = connectionDB.getConnection();
		String querySQL = "SELECT nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh,"
				+ " soDienThoai, email, diaChi, ngayThamGia, isHoatDong FROM NhanVien WHERE nhanVienID = ?";
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
			preparedStatement.setString(1, nhanVienIDTim);
			ResultSet resultSet = preparedStatement.executeQuery();
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
				
				return new NhanVien(nhanVienID, vaiTroNhanVien, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia, isHoatDong);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String themNhanVien(NhanVien newNhanVien) {
		Connection connection = connectionDB.getConnection();
		String insertSQL = "INSERT INTO NhanVien (nhanVienID, vaiTroNhanVienID, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia, isHoatDong)"
				+ " OUTPUT inserted.NhanVienID VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement s = connection.prepareStatement(insertSQL);
			s.setString(1, newNhanVien.getNhanVienID());
			s.setString(2, newNhanVien.getVaiTroNhanVien().toString());
			s.setString(3, newNhanVien.getHoTen());
			s.setString(4, newNhanVien.isNu()? "1":"0");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			s.setString(5, newNhanVien.getNgaySinh().format(formatter));
			s.setString(6, newNhanVien.getEmail());
			s.setString(7, newNhanVien.getSoDienThoai());
			s.setString(8, newNhanVien.getDiaChi());
			s.setString(9, newNhanVien.getNgayThamGia().format(formatter));
			s.setString(10, newNhanVien.isHoatDong()? "1":"0");
			ResultSet rs = s.executeQuery();

			if (rs.next()) {
				return rs.getString(1);
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	public boolean capNhatNhanVien(NhanVien newNhanVien) {
		Connection connection = connectionDB.getConnection();
		String capNhatSQL = "UPDATE NhanVien SET vaiTroNhanVienID = ?, hoTen = ?, isNu = ?, ngaySinh = ?,"
				+ " soDienThoai = ?, email = ?, diaChi = ?, ngayThamGia = ?, isHoatDong = ?"
				+ " WHERE nhanVienID = ?";
		try {
			PreparedStatement s = connection.prepareStatement(capNhatSQL);
			s.setString(1, newNhanVien.getVaiTroNhanVien().toString());
			s.setString(2, newNhanVien.getHoTen());
			s.setString(3, newNhanVien.isNu()? "1":"0");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			s.setString(4, newNhanVien.getNgaySinh().format(formatter));
			s.setString(5, newNhanVien.getEmail());
			s.setString(6, newNhanVien.getSoDienThoai());
			s.setString(7, newNhanVien.getDiaChi());
			s.setString(8, newNhanVien.getNgayThamGia().format(formatter));
			s.setString(9, newNhanVien.isHoatDong()? "1":"0");
			s.setString(10, newNhanVien.getNhanVienID());
			
			int rowsAffected = s.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
