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

import java.util.Objects;

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
		if(gaID != null && !gaID.isEmpty()){
			this.gaID = gaID;
		}else{
			throw new IllegalArgumentException("Ga ID không được để trống!");
		}
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ga ga = (Ga) o;
		return Objects.equals(gaID, ga.gaID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gaID);
	}
}
