package entity;
/*
 * @(#) Tau.java  1.0  [10:01:01 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.LoaiTau;

import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Tau {
	private String tauID;
	private String tenTau;
	private LoaiTau loaiTau;
	private int soLuongToa;
	private String trangThai;
	private int vanTocTB;
	
	public Tau(String tauID, String tenTau, LoaiTau loaiTau, int soLuongToa, String trangThai) {
		super();
		this.tauID = tauID;
		this.tenTau = tenTau;
		this.loaiTau = loaiTau;
		this.soLuongToa = soLuongToa;
		this.trangThai = trangThai;
	}

	public Tau(String tauID, String tenTau, LoaiTau loaiTau, int soLuongToa, String trangThai, int vanTocTB) {
		this.tauID = tauID;
		this.tenTau = tenTau;
		this.loaiTau = loaiTau;
		this.soLuongToa = soLuongToa;
		this.trangThai = trangThai;
		this.vanTocTB = vanTocTB;
	}

	public Tau(String tauID) {
		super();
		this.tauID = tauID;
	}
	
	public Tau(String tauID, LoaiTau loaiTau) {
		super();
		this.tauID = tauID;
		this.loaiTau = loaiTau;
	}

	public Tau(String tauID, String tenTau) {
		super();
		this.tauID = tauID;
		this.tenTau = tenTau;
	}

	public String getTauID() {
		return tauID;
	}

	public String getTenTau() {
		return tenTau;
	}

	public LoaiTau getLoaiTau() {
		return loaiTau;
	}

	public int getSoLuongToa() {
		return soLuongToa;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTauID(String tauID) {
		this.tauID = tauID;
	}

	public void setTenTau(String tenTau) {
		this.tenTau = tenTau;
	}

	public void setLoaiTau(LoaiTau loaiTau) {
		this.loaiTau = loaiTau;
	}

	public void setSoLuongToa(int soLuongToa) {
		this.soLuongToa = soLuongToa;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	public int getVanTocTB() {
		return vanTocTB;
	}

	public void setVanTocTB(int vanTocTB) {
		this.vanTocTB = vanTocTB;
	}

	@Override
	public String toString() {
		return tauID + ";" + tenTau + ";" + loaiTau + ";" + soLuongToa
				+ ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tau tau = (Tau) o;
		return Objects.equals(getTauID(), tau.getTauID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getTauID());
	}
}