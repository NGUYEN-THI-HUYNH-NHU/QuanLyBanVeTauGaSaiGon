package entity;

import java.time.LocalDate;

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
	private LocalDate hieuLucTu;
	private LocalDate hieuLucDen;
	private boolean trangThai;
	
	public DichVuPhongChoVIP(String dichVuPhongChoVIPID, double gia, String moTa, LocalDate hieuLucTu,
			LocalDate hieuLucDen, boolean trangThai) {
		super();
		this.dichVuPhongChoVIPID = dichVuPhongChoVIPID;
		this.gia = gia;
		this.moTa = moTa;
		this.hieuLucTu = hieuLucTu;
		this.hieuLucDen = hieuLucDen;
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

	public LocalDate getHieuLucTu() {
		return hieuLucTu;
	}

	public LocalDate getHieuLucDen() {
		return hieuLucDen;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setDichVuPhongChoVIPID(String dichVuPhongChoVIPID) {
		this.dichVuPhongChoVIPID = dichVuPhongChoVIPID;
	}

	public void setGia(double gia) {
		this.gia = gia;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public void setHieuLucTu(LocalDate hieuLucTu) {
		this.hieuLucTu = hieuLucTu;
	}

	public void setHieuLucDen(LocalDate hieuLucDen) {
		this.hieuLucDen = hieuLucDen;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return dichVuPhongChoVIPID + ";" + gia + ";" + moTa
				+ ";" + hieuLucTu + ";" + hieuLucDen + ";" + trangThai;
	}
	
	
}
