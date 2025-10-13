package entity;
/*
 * @(#) LanDatCho.java  1.0  [12:44:56 PM] Sep 18, 2025
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

public class DonDatCho {
	private String donDatChoID;
	private NhanVien nhanVien;
	private KhachHang khachHang;
	private LocalDateTime thoiDiemDatCho;

	public DonDatCho(String donDatChoID, NhanVien nhanVien, KhachHang khachHang, LocalDateTime thoiDiemDatCho) {
		this.donDatChoID = donDatChoID;
		this.nhanVien = nhanVien;
		this.khachHang = khachHang;
		this.thoiDiemDatCho = thoiDiemDatCho;
	}

	public DonDatCho(String donDatChoID) {
		super();
		this.donDatChoID = donDatChoID;
	}

 	public DonDatCho() {
 		super();
 	}

	public String getDonDatChoID() {
		return donDatChoID;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public LocalDateTime getThoiDiemDatCho() {
		return thoiDiemDatCho;
	}

	public void setDonDatChoID(String donDatChoID) {
		if(donDatChoID != null && !donDatChoID.trim().isEmpty()) {
			this.donDatChoID = donDatChoID;
		}else{
			throw new IllegalArgumentException("Đơn đặt chỗ ID không được để trống!");
		}
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setThoiDiemDatCho(LocalDateTime thoiDiemDatCho) {
		this.thoiDiemDatCho = thoiDiemDatCho;
	}

	@Override
	public String toString() {
		return donDatChoID + ";" + nhanVien
				+ ";" + khachHang
				+ ";" + thoiDiemDatCho
				;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DonDatCho donDatCho = (DonDatCho) o;
		return Objects.equals(donDatChoID, donDatCho.donDatChoID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(donDatChoID);
	}
}