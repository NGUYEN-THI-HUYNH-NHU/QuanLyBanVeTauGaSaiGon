package entity;
/*
 * @(#) SuDungKhuyenMai.java  1.0  [3:39:39 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
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
	private KhachHang khachHang;
	private LocalDateTime thoiDiemDung;
	
	public SuDungKhuyenMai(String suDungKhuyenMaiID, KhuyenMai khuyenMai, HoaDonChiTiet hoaDonChiTiet,
			KhachHang khachHang, LocalDateTime thoiDiemDung) {
		super();
		this.suDungKhuyenMaiID = suDungKhuyenMaiID;
		this.khuyenMai = khuyenMai;
		this.hoaDonChiTiet = hoaDonChiTiet;
		this.khachHang = khachHang;
		this.thoiDiemDung = thoiDiemDung;
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

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public LocalDateTime getThoiDiemDung() {
		return thoiDiemDung;
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

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setThoiDiemDung(LocalDateTime thoiDiemDung) {
		this.thoiDiemDung = thoiDiemDung;
	}

	@Override
	public String toString() {
		return suDungKhuyenMaiID + ";" + khuyenMai
				+ ";" + hoaDonChiTiet + ";" + khachHang + ";" + thoiDiemDung;
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
