package entity;

import entity.type.HangToa;

import java.util.Objects;

/*
 * @(#) Toa.java  1.0  [10:14:42 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */


/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Toa {
	private String toaID;
	private Tau tau;
	private HangToa hangToa;
	private int sucChua;
	private String soToa;
	
	public Toa(String toaID, Tau tau, HangToa hangToa, int sucChua, String soToa) {
		super();
		this.toaID = toaID;
		this.tau = tau;
		this.hangToa = hangToa;
		this.sucChua = sucChua;
		this.soToa = soToa;
	}
	
	public Toa(String toaID, String soToa) {
		super();
		this.toaID = toaID;
		this.soToa = soToa;
	}
	
	public Toa() {
		super();
	}
	
	public Toa(String toaID) {
		super();
		this.toaID = toaID;
	}

	public String getToaID() {
		return toaID;
	}

	public Tau getTau() {
		return tau;
	}

	public HangToa getHangToa() {
		return hangToa;
	}

	public int getSucChua() {
		return sucChua;
	}

	public String getSoToa() {
		return soToa;
	}

	public void setToaID(String toaID) {
		if(toaID != null && !toaID.isEmpty()) {
			this.toaID = toaID;
		}else{
			throw new IllegalArgumentException("Toa ID không được để trống!");
		}
	}

	public void setTau(Tau tau) {
		this.tau = tau;
	}

	public void setHangToa(HangToa hangToa) {
		this.hangToa = hangToa;
	}

	public void setSucChua(int sucChua) {
		this.sucChua = sucChua;
	}

	public void setSoToa(String soToa) {
		this.soToa = soToa;
	}

	@Override
	public String toString() {
		return toaID + ";" + tau + ";" + hangToa + ";" + sucChua + ";"
				+ soToa;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Toa toa = (Toa) o;
		return Objects.equals(getToaID(), toa.getToaID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getToaID());
	}
}
