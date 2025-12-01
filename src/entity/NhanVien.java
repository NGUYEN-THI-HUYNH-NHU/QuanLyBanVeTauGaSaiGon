package entity;
/*
 * @(#) NhanVien.java  1.0  [9:52:26 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.VaiTroNhanVien;

import java.time.LocalDate;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class NhanVien {
	private String nhanVienID;
	private VaiTroNhanVien vaiTroNhanVien;
	private String hoTen;
	private boolean isNu;
	private LocalDate ngaySinh;
	private String soDienThoai;
	private String email;
	private String diaChi;
	private LocalDate ngayThamGia;
	private boolean isHoatDong;
	private String caLam;
	private byte[] avatar;

	public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, boolean isNu, LocalDate ngaySinh,
			String soDienThoai, String email, String diaChi, LocalDate ngayThamGia, boolean isHoatDong, String caLam,
			byte[] avatar) {
		super();
		this.nhanVienID = nhanVienID;
		this.vaiTroNhanVien = vaiTroNhanVien;
		this.hoTen = hoTen;
		this.isNu = isNu;
		this.ngaySinh = ngaySinh;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.diaChi = diaChi;
		this.ngayThamGia = ngayThamGia;
		this.isHoatDong = isHoatDong;
		this.caLam = caLam;
		this.avatar = avatar;
	}

	public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, boolean isNu, LocalDate ngaySinh,
			String soDienThoai, String email, String diaChi, LocalDate ngayThamGia, boolean isHoatDong, String caLam) {
		super();
		this.nhanVienID = nhanVienID;
		this.vaiTroNhanVien = vaiTroNhanVien;
		this.hoTen = hoTen;
		this.isNu = isNu;
		this.ngaySinh = ngaySinh;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.diaChi = diaChi;
		this.ngayThamGia = ngayThamGia;
		this.isHoatDong = isHoatDong;
		this.caLam = caLam;
	}

	public NhanVien(String nhanVienID) {
		super();
		this.nhanVienID = nhanVienID;
	}

	public NhanVien() {

	}

	public String getCaLam() {
		return caLam;
	}

	public void setCaLma(String caLam) {
		this.caLam = caLam;
	}

	public String getNhanVienID() {
		return nhanVienID;
	}

	public VaiTroNhanVien getVaiTroNhanVien() {
		return vaiTroNhanVien;
	}

	public String getHoTen() {
		return hoTen;
	}

	public boolean isNu() {
		return isNu;
	}

	public LocalDate getNgaySinh() {
		return ngaySinh;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public String getEmail() {
		return email;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public LocalDate getNgayThamGia() {
		return ngayThamGia;
	}

	public boolean isHoatDong() {
		return isHoatDong;
	}

	public void setNhanVienID(String nhanVienID) {
		if (nhanVienID == null || nhanVienID.isEmpty()) {
			throw new IllegalArgumentException("Mã nhân viên không được để trống");
		}
		this.nhanVienID = nhanVienID;
	}

	public void setVaiTroNhanVien(VaiTroNhanVien vaiTroNhanVien) {
		if (vaiTroNhanVien == null) {
			throw new IllegalArgumentException("Vai trò nhân viên không được để trống");
		}
		this.vaiTroNhanVien = vaiTroNhanVien;
	}

	public void setHoTen(String hoTen) {
		if (hoTen == null || hoTen.isEmpty()) {
			throw new IllegalArgumentException("Họ tên không được để trống");
		}
		this.hoTen = hoTen;
	}

	public void setNu(boolean isNu) {
		this.isNu = isNu;
	}

	public void setNgaySinh(LocalDate ngaySinh) {
		this.ngaySinh = ngaySinh;
	}

	public void setSoDienThoai(String soDienThoai) {
		if (soDienThoai == null || soDienThoai.isEmpty()) {
			throw new IllegalArgumentException("Số điện thoại không được để trống");
		}
		this.soDienThoai = soDienThoai;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public void setNgayThamGia(LocalDate ngayThamGia) {
		if (ngayThamGia.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Ngày tham gia không được sau ngày hiện tại");
		}
		this.ngayThamGia = ngayThamGia;
	}

	public void setHoatDong(boolean isHoatDong) {
		this.isHoatDong = isHoatDong;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setCaLam(String caLam) {
		this.caLam = caLam;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	@Override
	public String toString() {
		return nhanVienID + ";" + vaiTroNhanVien + ";" + hoTen + isNu + ";" + ngaySinh + ";" + soDienThoai + ";" + email
				+ ";" + diaChi + ";" + ngayThamGia + ";" + isHoatDong + ";" + caLam;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NhanVien nhanVien = (NhanVien) o;
		return Objects.equals(getNhanVienID(), nhanVien.getNhanVienID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getNhanVienID());
	}
}