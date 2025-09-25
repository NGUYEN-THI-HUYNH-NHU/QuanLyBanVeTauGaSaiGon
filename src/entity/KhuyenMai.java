package entity;
/*
 * @(#) KhuyenMai.java  1.0  [12:36:16 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

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
	private LocalDate ngayTao;
	private LocalDate ngayCapNhat;
	private boolean trangThai;
	
	public KhuyenMai(String khuyenMaiID, String maKhuyenMai, String moTa, double tyLeGiamGia, double tienGiamGia,
			LocalDate ngayBatDau, LocalDate ngayKetThuc, double soLuong, int gioiHanMoiKhachHang, LocalDate ngayTao,
			LocalDate ngayCapNhat, boolean trangThai) {
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
		this.ngayTao = ngayTao;
		this.ngayCapNhat = ngayCapNhat;
		this.trangThai = trangThai;
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

	public LocalDate getNgayTao() {
		return ngayTao;
	}

	public LocalDate getNgayCapNhat() {
		return ngayCapNhat;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setKhuyenMaiID(String khuyenMaiID) {
		this.khuyenMaiID = khuyenMaiID;
	}

	public void setMaKhuyenMai(String maKhuyenMai) {
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
		this.ngayBatDau = ngayBatDau;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public void setSoLuong(double soLuong) {
		this.soLuong = soLuong;
	}

	public void setGioiHanMoiKhachHang(int gioiHanMoiKhachHang) {
		this.gioiHanMoiKhachHang = gioiHanMoiKhachHang;
	}

	public void setNgayTao(LocalDate ngayTao) {
		this.ngayTao = ngayTao;
	}

	public void setNgayCapNhat(LocalDate ngayCapNhat) {
		this.ngayCapNhat = ngayCapNhat;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return khuyenMaiID + ";" + maKhuyenMai + ";" + moTa
				+ ";" + tyLeGiamGia + ";" + tienGiamGia + ";" + ngayBatDau
				+ ";" + ngayKetThuc + ";" + soLuong + ";"
				+ gioiHanMoiKhachHang + ";" + ngayTao + ";" + ngayCapNhat + ";"
				+ trangThai;
	}
}