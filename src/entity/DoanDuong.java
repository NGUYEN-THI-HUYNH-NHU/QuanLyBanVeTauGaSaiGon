package entity;
/*
 * @(#) DoanDuong.java  1.0  [3:24:50 PM] Sep 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 25, 2025
 * @version: 1.0
 */

import java.util.Objects;

public class DoanDuong {
	private String doanDuongID;
	private Chuyen chuyen;
	private Ga gaDi;
	private Ga gaDen;
	private int thuTuGaDi;
	private int thuTuGaDen;
	private int khoangCachKm;
	private int thoiGianDuKienPhut;
	public DoanDuong(String doanDuongID, Chuyen chuyen, Ga gaDi, Ga gaDen, int thuTuGaDi, int thuTuGaDen,
			int khoangCachKm, int thoiGianDuKienPhut) {
		super();
		this.doanDuongID = doanDuongID;
		this.chuyen = chuyen;
		this.gaDi = gaDi;
		this.gaDen = gaDen;
		this.thuTuGaDi = thuTuGaDi;
		this.thuTuGaDen = thuTuGaDen;
		this.khoangCachKm = khoangCachKm;
		this.thoiGianDuKienPhut = thoiGianDuKienPhut;
	}
	public String getDoanDuongID() {
		return doanDuongID;
	}
	public Chuyen getChuyen() {
		return chuyen;
	}
	public Ga getGaDi() {
		return gaDi;
	}
	public Ga getGaDen() {
		return gaDen;
	}
	public int getThuTuGaDi() {
		return thuTuGaDi;
	}
	public int getThuTuGaDen() {
		return thuTuGaDen;
	}
	public int getKhoangCachKm() {
		return khoangCachKm;
	}
	public int getThoiGianDuKienPhut() {
		return thoiGianDuKienPhut;
	}
	public void setDoanDuongID(String doanDuongID) {
		if(doanDuongID == null || doanDuongID.isEmpty()) {
			throw new IllegalArgumentException("DoanDuongID không được để trống!");
		}
		this.doanDuongID = doanDuongID;
	}
	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
	}
	public void setGaDi(Ga gaDi) {
		this.gaDi = gaDi;
	}
	public void setGaDen(Ga gaDen) {
		this.gaDen = gaDen;
	}
	public void setThuTuGaDi(int thuTuGaDi) {
		this.thuTuGaDi = thuTuGaDi;
	}
	public void setThuTuGaDen(int thuTuGaDen) {
		this.thuTuGaDen = thuTuGaDen;
	}
	public void setKhoangCachKm(int khoangCachKm) {
		this.khoangCachKm = khoangCachKm;
	}
	public void setThoiGianDuKienPhut(int thoiGianDuKienPhut) {
		this.thoiGianDuKienPhut = thoiGianDuKienPhut;
	}
	@Override
	public String toString() {
		return doanDuongID + ";" + chuyen + ";" + gaDi + ";" + gaDen
				+ ";" + thuTuGaDi + ";" + thuTuGaDen + ";" + khoangCachKm
				+ ";" + thoiGianDuKienPhut;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DoanDuong doanDuong = (DoanDuong) o;
		return Objects.equals(doanDuongID, doanDuong.doanDuongID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(doanDuongID);
	}
}
