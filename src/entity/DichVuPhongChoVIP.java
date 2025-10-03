package entity;

import java.time.LocalDate;
import java.util.Objects;

/*
 * @(#) DichVuPhongChoVIP.java  1.0  [1:51:58 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class DichVuPhongChoVIP {
	private String dichVuPhongChoVIPID;
	private double gia;
	private String moTa;
	private LocalDate ngayCoHieuLuc;
	private LocalDate ngayHetHieuLuc;
	private boolean trangThai;
	
	public DichVuPhongChoVIP(String dichVuPhongChoVIPID, double gia, String moTa, LocalDate ngayCoHieuLuc,
							 LocalDate ngayHetHieuLuc, boolean trangThai) {
		super();
		this.dichVuPhongChoVIPID = dichVuPhongChoVIPID;
		this.gia = gia;
		this.moTa = moTa;
		this.ngayCoHieuLuc = ngayCoHieuLuc;
		this.ngayHetHieuLuc = ngayHetHieuLuc;
		this.trangThai = trangThai;
	}

	public String getDichVuPhongChoVIPID() {
		return dichVuPhongChoVIPID;
	}

	public double getGia() {
		return gia;
	}

	public String getMoTa() {
		return moTa;
	}

	public LocalDate getNgayCoHieuLuc() {
		return ngayCoHieuLuc;
	}

	public LocalDate getNgayHetHieuLuc() {
		return ngayHetHieuLuc;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setDichVuPhongChoVIPID(String dichVuPhongChoVIPID) {
		if(dichVuPhongChoVIPID != null && !dichVuPhongChoVIPID.isEmpty()){
			this.dichVuPhongChoVIPID = dichVuPhongChoVIPID;
		}
		else {
			throw new IllegalArgumentException("DichVuPhongChoVIPID không được để trống!");
		}
	}

	public void setGia(double gia) {
		if(gia < 0) {
			throw new IllegalArgumentException("Giá không được âm!");
		}
		this.gia = gia;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public void setNgayCoHieuLuc(LocalDate ngayCoHieuLuc) {
		if(ngayCoHieuLuc.isAfter(ngayHetHieuLuc)) {
			throw new IllegalArgumentException("Ngày có hiệu lực phải trước ngày hết hiệu lực!");
		}
		this.ngayCoHieuLuc = ngayCoHieuLuc;
	}

	public void setNgayHetHieuLuc(LocalDate ngayHetHieuLuc) {
		if(ngayHetHieuLuc.isBefore(ngayCoHieuLuc)) {
			throw new IllegalArgumentException("Ngày hết hiệu lực phải sau ngày có hiệu lực!");
		}
		this.ngayHetHieuLuc = ngayHetHieuLuc;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return dichVuPhongChoVIPID + ";" + gia + ";" + moTa
				+ ";" + ngayCoHieuLuc + ";" + ngayHetHieuLuc + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DichVuPhongChoVIP that = (DichVuPhongChoVIP) o;
		return Objects.equals(dichVuPhongChoVIPID, that.dichVuPhongChoVIPID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(dichVuPhongChoVIPID);
	}
}
