package entity;
/*
 * @(#) HoaDon.java  1.0  [12:36:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class HoaDon {
	private String hoaDonID;
	private DonDatCho donDatCho;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private LocalDateTime thoiDiemTao;
	private double tamTinh;
	private double tongGiamGia;
	private double tongThue;
	private double tongTien;
	private boolean trangThai;
	
	public HoaDon(String hoaDonID, DonDatCho donDatCho, KhachHang khachHang, NhanVien nhanVien,
			LocalDateTime thoiDiemTao, double tamTinh, double tongGiamGia, double tongThue, double tongTien,
			boolean trangThai) {
		super();
		this.hoaDonID = hoaDonID;
		this.donDatCho = donDatCho;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.thoiDiemTao = thoiDiemTao;
		this.tamTinh = tamTinh;
		this.tongGiamGia = tongGiamGia;
		this.tongThue = tongThue;
		this.tongTien = tongTien;
		this.trangThai = trangThai;
	}

	public String getHoaDonID() {
		return hoaDonID;
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public LocalDateTime getThoiDiemTao() {
		return thoiDiemTao;
	}

	public double getTamTinh() {
		return tamTinh;
	}

	public double getTongGiamGia() {
		return tongGiamGia;
	}

	public double getTongThue() {
		return tongThue;
	}

	public double getTongTien() {
		return tongTien;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setHoaDonID(String hoaDonID) {
		if(hoaDonID == null || hoaDonID.isEmpty()) {
			throw new IllegalArgumentException("HoaDonID không được để trống!");
		}
		this.hoaDonID = hoaDonID;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		this.donDatCho = donDatCho;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
		this.thoiDiemTao = thoiDiemTao;
	}

	public void setTamTinh(double tamTinh) {
		this.tamTinh = tamTinh;
	}

	public void setTongGiamGia(double tongGiamGia) {
		this.tongGiamGia = tongGiamGia;
	}

	public void setTongThue(double tongThue) {
		this.tongThue = tongThue;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return hoaDonID + ";" + donDatCho + ";" + khachHang + ";"
				+ nhanVien + ";" + thoiDiemTao + ";" + tamTinh + ";" + tongGiamGia
				+ ";" + tongThue + ";" + tongTien + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HoaDon hoaDon = (HoaDon) o;
		return Objects.equals(hoaDonID, hoaDon.hoaDonID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hoaDonID);
	}
}