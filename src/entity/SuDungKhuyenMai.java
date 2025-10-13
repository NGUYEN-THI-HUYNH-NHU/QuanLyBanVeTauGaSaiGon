package entity;
/*
 * @(#) SuDungKhuyenMai.java  1.0  [3:39:39 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class SuDungKhuyenMai {
	private String suDungKhuyenMaiID;
	private KhuyenMai khuyenMai;
	private HoaDonChiTiet hoaDonChiTiet;
	
	public SuDungKhuyenMai(String suDungKhuyenMaiID, KhuyenMai khuyenMai, HoaDonChiTiet hoaDonChiTiet) {
		super();
		this.suDungKhuyenMaiID = suDungKhuyenMaiID;
		this.khuyenMai = khuyenMai;
		this.hoaDonChiTiet = hoaDonChiTiet;
	}

	public String getSuDungKhuyenMaiID() {
		return suDungKhuyenMaiID;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public HoaDonChiTiet getHoaDonChiTiet() {
		return hoaDonChiTiet;
	}
	
	public void setSuDungKhuyenMaiID(String suDungKhuyenMaiID) {
		this.suDungKhuyenMaiID = suDungKhuyenMaiID;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public void setHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
		this.hoaDonChiTiet = hoaDonChiTiet;
	}

	@Override
	public String toString() {
		return suDungKhuyenMaiID + ";" + khuyenMai.getKhuyenMaiID() + ";" + hoaDonChiTiet.getHoaDonChiTietID();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SuDungKhuyenMai that = (SuDungKhuyenMai) o;
		return Objects.equals(getSuDungKhuyenMaiID(), that.getSuDungKhuyenMaiID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getSuDungKhuyenMaiID());
	}
}
