package entity;
/*
 * @(#) CaLam.java  1.0  [10:55:00 PM] Dec 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 20, 2025
 * @version: 1.0
 */

public class CaLam {
	private String caLamID;
	private LocalTime gioVaoCa;
	private LocalTime gioKetCa;

	public CaLam(String caLamID, LocalTime gioVaoCa, LocalTime gioKetCa) {
		super();
		this.caLamID = caLamID;
		this.gioVaoCa = gioVaoCa;
		this.gioKetCa = gioKetCa;
	}

	/**
	 * @param selectedItem
	 */
	public CaLam(String caLamID) {
		super();
		this.caLamID = caLamID;
	}

	public String getCaLamID() {
		return caLamID;
	}

	public LocalTime getGioVaoCa() {
		return gioVaoCa;
	}

	public LocalTime getGioKetCa() {
		return gioKetCa;
	}

	public void setCaLamID(String caLamID) {
		this.caLamID = caLamID;
	}

	public void setGioVaoCa(LocalTime gioVaoCa) {
		this.gioVaoCa = gioVaoCa;
	}

	public void setGioKetCa(LocalTime gioKetCa) {
		this.gioKetCa = gioKetCa;
	}

	@Override
	public String toString() {
		return caLamID + gioVaoCa + gioKetCa;
	}
}
