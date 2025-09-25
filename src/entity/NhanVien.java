package entity;
/*
 * @(#) NhanVien.java  1.0  [9:52:26 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

import entity.type.VaiTroNhanVien;

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
	private String soDienThoai;
	private String email;
	private String diaChi;
	private LocalDate ngayThamGia;
	private boolean trangThai;
	
	public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, String soDienThoai, String email,
			String diaChi, LocalDate ngayThamGia, boolean trangThai) {
		super();
		this.nhanVienID = nhanVienID;
		this.vaiTroNhanVien = vaiTroNhanVien;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.diaChi = diaChi;
		this.ngayThamGia = ngayThamGia;
		this.trangThai = trangThai;
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

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setNhanVienID(String nhanVienID) {
		this.nhanVienID = nhanVienID;
	}

	public void setVaiTroNhanVien(VaiTroNhanVien vaiTroNhanVien) {
		this.vaiTroNhanVien = vaiTroNhanVien;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public void setSoDienThoai(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	public void setNgayThamGia(LocalDate ngayThamGia) {
		this.ngayThamGia = ngayThamGia;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return nhanVienID + ";" + vaiTroNhanVien + ";" + hoTen
				+ ";" + soDienThoai + ";" + email + ";" + diaChi + ", ngayThamGia="
				+ ngayThamGia + ", trangThai=" + trangThai + "]";
	}
	
	
}
