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
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private LocalDateTime thoiDiemTao;

	public HoaDon(String hoaDonID, KhachHang khachHang, NhanVien nhanVien, LocalDateTime thoiDiemTao) {
		this.hoaDonID = hoaDonID;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.thoiDiemTao = thoiDiemTao;
	}

	public String getHoaDonID() {
		return hoaDonID;
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

	public void setHoaDonID(String hoaDonID) {
		if(hoaDonID == null || hoaDonID.isEmpty()) {
			throw new IllegalArgumentException("HoaDonID không được để trống!");
		}
		this.hoaDonID = hoaDonID;
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

	@Override
	public String toString() {
		return hoaDonID + ";"
				+ khachHang + ";"
				+ nhanVien + ";"
				+ thoiDiemTao;
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