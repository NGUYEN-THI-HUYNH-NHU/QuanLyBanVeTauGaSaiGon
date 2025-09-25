package entity;
/*
 * @(#) HoaDon_ChiTiet.java  1.0  [3:23:13 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class HoaDonChiTiet {
	private String hoaDonChiTietID;
	private HoaDon hoaDon;
	private String loaiDichVu;
	private String dichVuID;
	private String tenDichVu;
	private double donGia;
	private int soLuong;
	private double soTien;
	private double thue;
	private Ve ve;
	
	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, String loaiDichVu, String dichVuID, String tenDichVu,
			double donGia, int soLuong, double soTien, double thue, Ve ve) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.loaiDichVu = loaiDichVu;
		this.dichVuID = dichVuID;
		this.tenDichVu = tenDichVu;
		this.donGia = donGia;
		this.soLuong = soLuong;
		this.soTien = soTien;
		this.thue = thue;
		this.ve = ve;
	}

	public String getHoaDonChiTietID() {
		return hoaDonChiTietID;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public String getLoaiDichVu() {
		return loaiDichVu;
	}

	public String getDichVuID() {
		return dichVuID;
	}

	public String getTenDichVu() {
		return tenDichVu;
	}

	public double getDonGia() {
		return donGia;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public double getSoTien() {
		return soTien;
	}

	public double getThue() {
		return thue;
	}

	public Ve getVe() {
		return ve;
	}

	public void setHoaDonChiTietID(String hoaDonChiTietID) {
		this.hoaDonChiTietID = hoaDonChiTietID;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public void setLoaiDichVu(String loaiDichVu) {
		this.loaiDichVu = loaiDichVu;
	}

	public void setDichVuID(String dichVuID) {
		this.dichVuID = dichVuID;
	}

	public void setTenDichVu(String tenDichVu) {
		this.tenDichVu = tenDichVu;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public void setSoTien(double soTien) {
		this.soTien = soTien;
	}

	public void setThue(double thue) {
		this.thue = thue;
	}

	public void setVe(Ve ve) {
		this.ve = ve;
	}

	@Override
	public String toString() {
		return hoaDonChiTietID + ";" + hoaDon + ";" + loaiDichVu
				+ ";" + dichVuID + ";" + tenDichVu + ";" + donGia + ";" + soLuong
				+ ";" + soTien + ";" + thue + ";" + ve;
	}
}