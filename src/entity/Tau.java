package entity;
/*
 * @(#) Tau.java  1.0  [10:01:01 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.LoaiTau;

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
	
	public Tau(String tauID, String tenTau, LoaiTau loaiTau, int soLuongToa, String trangThai) {
		super();
		this.tauID = tauID;
		this.tenTau = tenTau;
		this.loaiTau = loaiTau;
		this.soLuongToa = soLuongToa;
		this.trangThai = trangThai;
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

	@Override
	public String toString() {
		return tauID + ";" + tenTau + ";" + loaiTau + ";" + soLuongToa
				+ ";" + trangThai;
	}	
}