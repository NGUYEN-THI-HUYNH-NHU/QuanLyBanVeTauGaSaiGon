package entity;
/*
 * @(#) HeSoGiaLoaiTau.java  1.0  [10:20:47 AM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.util.Objects;

import entity.type.LoaiTau;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public class HeSoGiaLoaiTau {
	private String hsgLoaiTauID;
	private LoaiTau loaiTau;
	private double hsg;
	private boolean isCoHieuLuc;


	public HeSoGiaLoaiTau(String hsgLoaiTauID, LoaiTau loaiTau, double hsg, boolean isCoHieuLuc) {
		this.hsgLoaiTauID = hsgLoaiTauID;
		this.loaiTau = loaiTau;
		this.hsg = hsg;
		this.isCoHieuLuc = isCoHieuLuc;
	}

	public String getHsgLoaiTauID() {
		return hsgLoaiTauID;
	}

	public LoaiTau getLoaiTau() {
		return loaiTau;
	}

	public double getHsg() {
		return hsg;
	}

	public boolean isCoHieuLuc() {
		return isCoHieuLuc;
	}

	public void setHsgLoaiTauID(String hsgLoaiTauID) {
		if(hsgLoaiTauID == null || hsgLoaiTauID.isEmpty()) {
			throw new IllegalArgumentException("HsgLoaiTauID không được để trống!");
		}
		this.hsgLoaiTauID = hsgLoaiTauID;
	}

	public void setLoaiTau(LoaiTau loaiTau) {
		this.loaiTau = loaiTau;
	}

	public void setHsg(double hsg) {
		this.hsg = hsg;
	}

	public void setCoHieuLuc(boolean coHieuLuc) {
		isCoHieuLuc = coHieuLuc;
	}

	@Override
	public String toString() {
		return hsgLoaiTauID + ";" + loaiTau + ";" + hsg
				+ ";" + isCoHieuLuc ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		HeSoGiaLoaiTau that = (HeSoGiaLoaiTau) o;
		return Objects.equals(hsgLoaiTauID, that.hsgLoaiTauID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hsgLoaiTauID);
	}
}