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
	private boolean isGaLon;
	private String tinhThanh;

	public Ga(String gaID, String tenGa, boolean isGaLon, String tinhThanh) {
		super();
		this.gaID = gaID;
		this.tenGa = tenGa;
		this.tinhThanh = tinhThanh;
	}

	public Ga(String gaID, String tenGa) {
		super();
		this.gaID = gaID;
		this.tenGa = tenGa;
	}

	public Ga(String gaID) {
		super();
		this.gaID = gaID;
	}

	public String getGaID() {
		return gaID;
	}

	public boolean isGaLon() {
		return isGaLon;
	}

	public String getTenGa() {
		return tenGa;
	}

	public String getTinhThanh() {
		return tinhThanh;
	}

	public void setGaID(String gaID) {
		if (gaID != null && !gaID.isEmpty()) {
			this.gaID = gaID;
		} else {
			throw new IllegalArgumentException("Ga ID không được để trống!");
		}
	}

	public void setTenGa(String tenGa) {
		if (tenGa != null && !tenGa.isEmpty()) {
			this.tenGa = tenGa;
		} else {
			throw new IllegalArgumentException("Tên ga không được để trống!");
		}
	}

	public void setGaLon(boolean gaLon) {
		isGaLon = gaLon;
	}

	public void setTinhThanh(String tinhThanh) {
		if (tinhThanh != null && !tinhThanh.isEmpty()) {
			this.tinhThanh = tinhThanh;
		} else {
			throw new IllegalArgumentException("Tỉnh thành không được để trống!");
		}
	}

	@Override
	public String toString() {
		return gaID + ";" + tenGa + ";" + tinhThanh;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Ga ga = (Ga) o;
		return Objects.equals(gaID, ga.gaID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gaID);
	}
}
