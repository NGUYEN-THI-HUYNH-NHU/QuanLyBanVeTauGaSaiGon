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

import java.util.Objects;

import entity.type.LoaiDichVu;

public class HoaDonChiTiet {
	private String hoaDonChiTietID;
	private HoaDon hoaDon;
	private Ve ve;
	private PhieuDungPhongVIP phieuDungPhongVIP;
	private String tenDichVu;
	private LoaiDichVu loaiDichVu;
	private String donViTinh;
	private int soLuong;
	private double donGia;
	private double thanhTien;

	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, Ve ve, PhieuDungPhongVIP phieuDungPhongVIP,
			String tenDichVu, LoaiDichVu loaiDichVu, String donViTinh, int soLuong, double donGia, double thanhTien) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.ve = ve;
		this.phieuDungPhongVIP = phieuDungPhongVIP;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.donViTinh = donViTinh;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, Ve ve, String tenDichVu, LoaiDichVu loaiDichVu,
			String donViTinh, int soLuong, double donGia, double thanhTien) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.ve = ve;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.donViTinh = donViTinh;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, PhieuDungPhongVIP phieuDungPhongVIP, String tenDichVu,
			LoaiDichVu loaiDichVu, String donViTinh, int soLuong, double donGia, double thanhTien) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.phieuDungPhongVIP = phieuDungPhongVIP;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.donViTinh = donViTinh;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, String tenDichVu, LoaiDichVu loaiDichVu,
			String donViTinh, int soLuong, double donGia, double thanhTien) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.donViTinh = donViTinh;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, String tenDichVu, LoaiDichVu loaiDichVu, int soLuong,
			double donGia, double thanhTien) {
		super();
		this.hoaDonChiTietID = hoaDonChiTietID;
		this.hoaDon = hoaDon;
		this.tenDichVu = tenDichVu;
		this.loaiDichVu = loaiDichVu;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.thanhTien = thanhTien;
	}

	public HoaDonChiTiet() {
		super();
	}

	public PhieuDungPhongVIP getPhieuDungPhongVIP() {
		return phieuDungPhongVIP;
	}

	public LoaiDichVu getLoaiDichVu() {
		return loaiDichVu;
	}

	public void setPhieuDungPhongVIP(PhieuDungPhongVIP phieuDungPhongVIP) {
		this.phieuDungPhongVIP = phieuDungPhongVIP;
	}

	public void setLoaiDichVu(LoaiDichVu loaiDichVu) {
		this.loaiDichVu = loaiDichVu;
	}

	public String getHoaDonChiTietID() {
		return hoaDonChiTietID;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public Ve getVe() {
		return ve;
	}

	public String getTenDichVu() {
		return tenDichVu;
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public void setHoaDonChiTietID(String hoaDonChiTietID) {
		if (hoaDonChiTietID != null && !hoaDonChiTietID.isEmpty()) {
			this.hoaDonChiTietID = hoaDonChiTietID;
		} else {
			throw new IllegalArgumentException("HoaDonChiTietID không được để trống!");
		}
	}

	public void setHoaDon(HoaDon hoaDon) {
		if (hoaDon == null) {
			throw new IllegalArgumentException("HoaDon không được để trống!");
		}
		this.hoaDon = hoaDon;
	}

	public void setVe(Ve ve) {
		if (ve == null) {
			throw new IllegalArgumentException("Ve không được để trống!");
		}
		this.ve = ve;
	}

	public void setTenDichVu(String tenDichVu) {
		if (tenDichVu == null || tenDichVu.isEmpty()) {
			throw new IllegalArgumentException("TenDichVu không được để trống!");
		}
		this.tenDichVu = tenDichVu;
	}

	public void setDonViTinh(String donViTinh) {
		if (donViTinh == null || donViTinh.isEmpty()) {
			throw new IllegalArgumentException("DonViTinh không được để trống!");
		}
		this.donViTinh = donViTinh;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public void setThanhTien(double thanhTien) {
		this.thanhTien = thanhTien;
	}

	@Override
	public String toString() {
		return hoaDonChiTietID + ";" + hoaDon + ";" + ve + ";" + tenDichVu + ";" + donViTinh + ";" + soLuong + ";"
				+ donGia + ";" + thanhTien;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		HoaDonChiTiet that = (HoaDonChiTiet) o;
		return Objects.equals(hoaDonChiTietID, that.hoaDonChiTietID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hoaDonChiTietID);
	}
}