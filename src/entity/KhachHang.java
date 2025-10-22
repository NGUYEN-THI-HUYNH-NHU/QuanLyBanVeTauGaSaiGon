package entity;
/*
 * @(#) KhachHang.java  1.0  [9:35:13 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */


import java.util.Objects;

import entity.type.LoaiDoiTuong;
import entity.type.LoaiKhachHang;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class KhachHang {
	private String khachHangID;
	private String hoTen;
	private String soDienThoai;
	private String email;
	private String soGiayTo;
	private String diaChi;
	private LoaiDoiTuong loaiDoiTuong;
	private LoaiKhachHang loaiKhachHang;

	
	public KhachHang(String khachHangID, String hoTen,
			String soDienThoai, String email, String soGiayTo, String diaChi, LoaiDoiTuong loaiDoiTuong, LoaiKhachHang loaiKhachHang) {
		super();
		this.khachHangID = khachHangID;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.soGiayTo = soGiayTo;
		this.diaChi = diaChi;
		this.loaiDoiTuong = loaiDoiTuong;
		this.loaiKhachHang = loaiKhachHang;
	}

	public KhachHang(String khachHangID, String hoTen, String soDienThoai, String email,
			String diaChi) {
		super();
		this.khachHangID = khachHangID;
		this.hoTen = hoTen;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.diaChi = diaChi;
	}
	
	public KhachHang(String khachHangID) {
		super();
		this.khachHangID = khachHangID;
	}
	
	public KhachHang() {
		super();
	}

	public String getKhachHangID() {
		return khachHangID;
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

	public void setKhachHangID(String khachHangID) {
		if(khachHangID == null || khachHangID.isEmpty()) {
			throw new IllegalArgumentException("KhachHangID không được để trống!");
		}
		this.khachHangID = khachHangID;
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

	public LoaiDoiTuong getLoaiDoiTuong() {
		return loaiDoiTuong;
	}

	public LoaiKhachHang getLoaiKhachHang() {
		return loaiKhachHang;
	}

	public String getSoGiayTo() {
		return soGiayTo;
	}

	public void setLoaiDoiTuong(LoaiDoiTuong loaiDoiTuong) {
		this.loaiDoiTuong = loaiDoiTuong;
	}

	public void setLoaiKhachHang(LoaiKhachHang loaiKhachHang) {
		this.loaiKhachHang = loaiKhachHang;
	}

	public void setSoGiayTo(String soGiayTo) {
		this.soGiayTo = soGiayTo;
	}

	@Override
	public String toString() {
		return loaiKhachHang + ";" + hoTen + ";" + soDienThoai + ";" + email
				+ ";" + soGiayTo + ";" + diaChi + ";" + khachHangID + ";" + loaiDoiTuong;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KhachHang khachHang = (KhachHang) o;
		return Objects.equals(getKhachHangID(), khachHang.getKhachHangID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getKhachHangID());
	}
}
