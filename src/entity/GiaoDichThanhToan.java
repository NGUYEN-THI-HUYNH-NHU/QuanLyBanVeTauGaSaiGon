package entity;
/*
 * @(#) ThanhToan.java  1.0  [12:54:12 PM] Sep 18, 2025
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

public class GiaoDichThanhToan {
	private String giaoDichThanhToan;
	private HoaDon hoaDon;
	private KhachHang khachHang;
	private double soTien;
	private LocalDateTime thoiDiemThanhToan;
	private boolean isThanhToanTienMat;
	private boolean trangThai;
	
	public GiaoDichThanhToan(String giaoDichThanhToan, HoaDon hoaDon, KhachHang khachHang, double soTien,
			LocalDateTime thoiDiemThanhToan, boolean isThanhToanTienMat, boolean trangThai) {
		super();
		this.giaoDichThanhToan = giaoDichThanhToan;
		this.hoaDon = hoaDon;
		this.khachHang = khachHang;
		this.soTien = soTien;
		this.thoiDiemThanhToan = thoiDiemThanhToan;
		this.isThanhToanTienMat = isThanhToanTienMat;
		this.trangThai = trangThai;
	}

	public String getGiaoDichThanhToan() {
		return giaoDichThanhToan;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public double getSoTien() {
		return soTien;
	}

	public LocalDateTime getThoiDiemThanhToan() {
		return thoiDiemThanhToan;
	}

	public boolean isThanhToanTienMat() {
		return isThanhToanTienMat;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setGiaoDichThanhToan(String giaoDichThanhToan) {
		this.giaoDichThanhToan = giaoDichThanhToan;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setSoTien(double soTien) {
		this.soTien = soTien;
	}

	public void setThoiDiemThanhToan(LocalDateTime thoiDiemThanhToan) {
		this.thoiDiemThanhToan = thoiDiemThanhToan;
	}

	public void setThanhToanTienMat(boolean isThanhToanTienMat) {
		this.isThanhToanTienMat = isThanhToanTienMat;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return giaoDichThanhToan + ";" + hoaDon + ";"
				+ khachHang + ";" + soTien + ";" + thoiDiemThanhToan
				+ ";" + isThanhToanTienMat + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GiaoDichThanhToan that = (GiaoDichThanhToan) o;
		return Objects.equals(giaoDichThanhToan, that.giaoDichThanhToan);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(giaoDichThanhToan);
	}
}
