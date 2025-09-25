package entity;
/*
 * @(#) Ga.java  1.0  [10:00:22 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Ga {
	private String gaID;
	private String tenGa;
	private String tinhThanh;
	
	public Ga(String gaID, String tenGa, String tinhThanh) {
		super();
		this.gaID = gaID;
		this.tenGa = tenGa;
		this.tinhThanh = tinhThanh;
	}

	public String getGaID() {
		return gaID;
	}

	public String getTenGa() {
		return tenGa;
	}

	public String getTinhThanh() {
		return tinhThanh;
	}

	public void setGaID(String gaID) {
		this.gaID = gaID;
	}

	public void setTenGa(String tenGa) {
		this.tenGa = tenGa;
	}

	public void setTinhThanh(String tinhThanh) {
		this.tinhThanh = tinhThanh;
	}

	@Override
	public String toString() {
		return gaID + ";" + tenGa + ";" + tinhThanh;
	}
}
