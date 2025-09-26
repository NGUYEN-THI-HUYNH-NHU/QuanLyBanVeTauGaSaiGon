package entity;
/*
 * @(#) PhieuDungPhongChoVIP.java  1.0  [3:45:53 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

import java.util.Objects;

public class PhieuDungPhongChoVIP {
	private String phieuDungPhongChoVIPID;
	private HoaDonChiTiet hoaDonChiTiet;
	private DichVuPhongChoVIP dichVuPhongChoVIP;
	private HanhKhach hanhKhach;
	private int soLuong;
	
	public PhieuDungPhongChoVIP(String phieuDungPhongChoVIPID, HoaDonChiTiet hoaDonChiTiet,
			DichVuPhongChoVIP dichVuPhongChoVIP, HanhKhach hanhKhach, int soLuong) {
		super();
		this.phieuDungPhongChoVIPID = phieuDungPhongChoVIPID;
		this.hoaDonChiTiet = hoaDonChiTiet;
		this.dichVuPhongChoVIP = dichVuPhongChoVIP;
		this.hanhKhach = hanhKhach;
		this.soLuong = soLuong;
	}

	public String getPhieuDungPhongChoVIPID() {
		return phieuDungPhongChoVIPID;
	}

	public HoaDonChiTiet getHoaDonChiTiet() {
		return hoaDonChiTiet;
	}

	public DichVuPhongChoVIP getDichVuPhongChoVIP() {
		return dichVuPhongChoVIP;
	}

	public HanhKhach getHanhKhach() {
		return hanhKhach;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setPhieuDungPhongChoVIPID(String phieuDungPhongChoVIPID) {
		if(phieuDungPhongChoVIPID == null || phieuDungPhongChoVIPID.isEmpty()) {
			throw new IllegalArgumentException("PhieuDungPhongChoVIPID không được để trống!");
		}
		this.phieuDungPhongChoVIPID = phieuDungPhongChoVIPID;
	}

	public void setHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
		this.hoaDonChiTiet = hoaDonChiTiet;
	}

	public void setDichVuPhongChoVIP(DichVuPhongChoVIP dichVuPhongChoVIP) {
		this.dichVuPhongChoVIP = dichVuPhongChoVIP;
	}

	public void setHanhKhach(HanhKhach hanhKhach) {
		this.hanhKhach = hanhKhach;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	@Override
	public String toString() {
		return phieuDungPhongChoVIPID + ";"
				+ hoaDonChiTiet + ";" + dichVuPhongChoVIP + ";" + hanhKhach + ";"
				+ soLuong;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PhieuDungPhongChoVIP that = (PhieuDungPhongChoVIP) o;
		return Objects.equals(getPhieuDungPhongChoVIPID(), that.getPhieuDungPhongChoVIPID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPhieuDungPhongChoVIPID());
	}
}
