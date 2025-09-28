package entity;
/*
 * @(#) LanDatCho.java  1.0  [12:44:56 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

import entity.type.TrangThaiDatCho;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class DonDatCho {
	private String donDatChoID;
	private KhachHang khachHang;
	private Chuyen chuyen;
	private LocalDateTime thoiDiemDatCho;
	private LocalDateTime thoiDiemHetHan;
	private double tongTien;
	private TrangThaiDatCho trangThaiDonDatCho;
	
	public DonDatCho(String donDatChoID, KhachHang khachHang, Chuyen chuyen, LocalDateTime thoiDiemDatCho,
			LocalDateTime thoiDiemHetHan, double tongTien, TrangThaiDatCho trangThaiDonDatCho) {
		super();
		this.donDatChoID = donDatChoID;
		this.khachHang = khachHang;
		this.chuyen = chuyen;
		this.thoiDiemDatCho = thoiDiemDatCho;
		this.thoiDiemHetHan = thoiDiemHetHan;
		this.tongTien = tongTien;
		this.trangThaiDonDatCho = trangThaiDonDatCho;
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

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public LocalDateTime getThoiDiemDatCho() {
		return thoiDiemDatCho;
	}

	public LocalDateTime getThoiDiemHetHan() {
		return thoiDiemHetHan;
	}

	public double getTongTien() {
		return tongTien;
	}

	public TrangThaiDatCho getTrangThaiDonDatCho() {
		return trangThaiDonDatCho;
	}

	public void setDonDatChoID(String donDatChoID) {
		if(donDatChoID != null && !donDatChoID.trim().isEmpty()) {
			this.donDatChoID = donDatChoID;
		}else{
			throw new IllegalArgumentException("Đơn đặt chỗ ID không được để trống!");
		}
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
	}

	public void setThoiDiemDatCho(LocalDateTime thoiDiemDatCho) {
		this.thoiDiemDatCho = thoiDiemDatCho;
	}

	public void setThoiDiemHetHan(LocalDateTime thoiDiemHetHan) {
		this.thoiDiemHetHan = thoiDiemHetHan;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public void setTrangThaiDonDatCho(TrangThaiDatCho trangThaiDonDatCho) {
		this.trangThaiDonDatCho = trangThaiDonDatCho;
	}

	@Override
	public String toString() {
		return donDatChoID + ";" + khachHang + ";" + chuyen
				+ ";" + thoiDiemDatCho + ";" + thoiDiemHetHan + ";" + tongTien
				+ ";" + trangThaiDonDatCho;
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