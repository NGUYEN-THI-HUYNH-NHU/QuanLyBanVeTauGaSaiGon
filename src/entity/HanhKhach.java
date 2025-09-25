package entity;
/*
 * @(#) HanhKhach.java  1.0  [1:22:58 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.LoaiDoiTuong;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class HanhKhach {
	private String hanhKhachID;
	private LoaiDoiTuong loaiDoiTuong;
	private KhachHang khachHang;
	private String hoTen;
	private String soGiayTo;
	
	public HanhKhach(String hanhKhachID, LoaiDoiTuong loaiDoiTuong, KhachHang khachHang, String hoTen,
			String soGiayTo) {
		super();
		this.hanhKhachID = hanhKhachID;
		this.loaiDoiTuong = loaiDoiTuong;
		this.khachHang = khachHang;
		this.hoTen = hoTen;
		this.soGiayTo = soGiayTo;
	}

	public String getHanhKhachID() {
		return hanhKhachID;
	}

	public LoaiDoiTuong getLoaiDoiTuong() {
		return loaiDoiTuong;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public String getHoTen() {
		return hoTen;
	}

	public String getSoGiayTo() {
		return soGiayTo;
	}

	public void setHanhKhachID(String hanhKhachID) {
		this.hanhKhachID = hanhKhachID;
	}

	public void setLoaiDoiTuong(LoaiDoiTuong loaiDoiTuong) {
		this.loaiDoiTuong = loaiDoiTuong;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setHoTen(String hoTen) {
		this.hoTen = hoTen;
	}

	public void setSoGiayTo(String soGiayTo) {
		this.soGiayTo = soGiayTo;
	}

	@Override
	public String toString() {
		return hanhKhachID + ";" + loaiDoiTuong + ";" + khachHang
				+ ";" + hoTen + ";" + soGiayTo;
	}
	
	
}