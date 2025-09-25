package entity;

import entity.type.HangToa;

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
	private int sttToa;
	
	public Toa(String toaID, Tau tau, HangToa hangToa, int sucChua, int sttToa) {
		super();
		this.toaID = toaID;
		this.tau = tau;
		this.hangToa = hangToa;
		this.sucChua = sucChua;
		this.sttToa = sttToa;
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

	public int getSttToa() {
		return sttToa;
	}

	public void setToaID(String toaID) {
		this.toaID = toaID;
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

	public void setSttToa(int sttToa) {
		this.sttToa = sttToa;
	}

	@Override
	public String toString() {
		return toaID + ";" + tau + ";" + hangToa + ";" + sucChua + ";"
				+ sttToa;
	}
}
