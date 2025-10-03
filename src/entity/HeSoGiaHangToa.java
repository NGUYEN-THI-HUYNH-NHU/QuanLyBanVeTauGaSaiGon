package entity;
/*
 * @(#) HeSoGiaHangToa.java  1.0  [6:22:46 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.util.Objects;

import entity.type.HangToa;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public class HeSoGiaHangToa {
	private String hsgHangToaID;
	private HangToa hangToa;
	private double hsg;
	private boolean isCoHieuLuc;

	public HeSoGiaHangToa(String hsgHangToaID, HangToa hangToa, double hsg, boolean isCoHieuLuc) {
		this.hsgHangToaID = hsgHangToaID;
		this.hangToa = hangToa;
		this.hsg = hsg;
		this.isCoHieuLuc = isCoHieuLuc;
	}

	public String getHsgHangToaID() {
		return hsgHangToaID;
	}

	public HangToa getHangToa() {
		return hangToa;
	}

	public double getHsg() {
		return hsg;
	}

	public boolean isCoHieuLuc() {
		return isCoHieuLuc;
	}

	public void setHsgHangToaID(String hsgHangToaID) {
		if(hsgHangToaID == null || hsgHangToaID.isEmpty()) {
			throw new IllegalArgumentException("HsgHangToaID không được để trống!");
		}
		this.hsgHangToaID = hsgHangToaID;
	}

	public void setHangToa(HangToa hangToa) {
		if(hangToa == null) {
			throw new IllegalArgumentException("Hạng toa không được để trống!");
		}
		this.hangToa = hangToa;
	}

	public void setHsg(double hsg) {
		if(hsg <= 0) {
			throw new IllegalArgumentException("Hệ số giá phải lớn hơn 0!");
		}
		this.hsg = hsg;
	}

	public void setCoHieuLuc(boolean coHieuLuc) {
		isCoHieuLuc = coHieuLuc;
	}

	@Override
	public String toString() {
		return hsgHangToaID + ";" + hangToa + ";" + hsg
				+ ";" + isCoHieuLuc ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HeSoGiaHangToa that = (HeSoGiaHangToa) o;
		return Objects.equals(hsgHangToaID, that.hsgHangToaID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hsgHangToaID);
	}
}