package entity;
/*
 * @(#) KhuyenMai.java  1.0  [12:36:16 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class KhuyenMai {
	private String khuyenMaiID;
	private String maKhuyenMai;
	private String moTa;
	private double tyLeGiamGia;
	private double tienGiamGia;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;
	private double soLuong;
	private int gioiHanMoiKhachHang;
	private boolean trangThai;
	
	public KhuyenMai(String khuyenMaiID, String maKhuyenMai, String moTa, double tyLeGiamGia, double tienGiamGia,
			LocalDate ngayBatDau, LocalDate ngayKetThuc, double soLuong, int gioiHanMoiKhachHang
			, boolean trangThai) {
		super();
		this.khuyenMaiID = khuyenMaiID;
		this.maKhuyenMai = maKhuyenMai;
		this.moTa = moTa;
		this.tyLeGiamGia = tyLeGiamGia;
		this.tienGiamGia = tienGiamGia;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
		this.soLuong = soLuong;
		this.gioiHanMoiKhachHang = gioiHanMoiKhachHang;
		this.trangThai = trangThai;
	}
	public KhuyenMai() {
		super();
	}

	public String getKhuyenMaiID() {
		return khuyenMaiID;
	}

	public String getMaKhuyenMai() {
		return maKhuyenMai;
	}

	public String getMoTa() {
		return moTa;
	}

	public double getTyLeGiamGia() {
		return tyLeGiamGia;
	}

	public double getTienGiamGia() {
		return tienGiamGia;
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public double getSoLuong() {
		return soLuong;
	}

	public int getGioiHanMoiKhachHang() {
		return gioiHanMoiKhachHang;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setKhuyenMaiID(String khuyenMaiID) {
		if(khuyenMaiID == null || khuyenMaiID.isEmpty()) {
			throw new IllegalArgumentException("KhuyenMaiID không được để trống!");
		}
		this.khuyenMaiID = khuyenMaiID;
	}

	public void setMaKhuyenMai(String maKhuyenMai) {
		if(maKhuyenMai == null || maKhuyenMai.isEmpty()) {
			throw new IllegalArgumentException("Mã khuyến mãi không được để trống!");
		}
		this.maKhuyenMai = maKhuyenMai;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public void setTyLeGiamGia(double tyLeGiamGia) {
		this.tyLeGiamGia = tyLeGiamGia;
	}

	public void setTienGiamGia(double tienGiamGia) {
		this.tienGiamGia = tienGiamGia;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		if(ngayBatDau.isAfter(ngayKetThuc)) {
			throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc!");
		}
		this.ngayBatDau = ngayBatDau;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		if(ngayKetThuc.isBefore(ngayBatDau)) {
			throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu!");
		}
		this.ngayKetThuc = ngayKetThuc;
	}

	public void setSoLuong(double soLuong) {
		this.soLuong = soLuong;
	}

	public void setGioiHanMoiKhachHang(int gioiHanMoiKhachHang) {
		this.gioiHanMoiKhachHang = gioiHanMoiKhachHang;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return khuyenMaiID + ";" + maKhuyenMai + ";" + moTa
				+ ";" + tyLeGiamGia + ";" + tienGiamGia + ";" + ngayBatDau
				+ ";" + ngayKetThuc + ";" + soLuong + ";"
				+ gioiHanMoiKhachHang + ";"
				+ trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KhuyenMai khuyenMai = (KhuyenMai) o;
		return Objects.equals(getKhuyenMaiID(), khuyenMai.getKhuyenMaiID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getKhuyenMaiID());
	}
}