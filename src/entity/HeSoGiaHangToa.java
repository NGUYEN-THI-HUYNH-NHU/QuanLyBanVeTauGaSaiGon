package entity;
/*
 * @(#) HeSoGiaHangToa.java  1.0  [6:22:46 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

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
	private LocalDate ngayCoHieuLuc;
	private LocalDate ngayHetHieuLuc;
	
	public HeSoGiaHangToa(String hsgHangToaID, HangToa hangToa, double hsg, LocalDate ngayCoHieuLuc,
			LocalDate ngayHetHieuLuc) {
		super();
		this.hsgHangToaID = hsgHangToaID;
		this.hangToa = hangToa;
		this.hsg = hsg;
		this.ngayCoHieuLuc = ngayCoHieuLuc;
		this.ngayHetHieuLuc = ngayHetHieuLuc;
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

	public LocalDate getNgayCoHieuLuc() {
		return ngayCoHieuLuc;
	}

	public LocalDate getNgayHetHieuLuc() {
		return ngayHetHieuLuc;
	}

	public void setHsgHangToaID(String hsgHangToaID) {
		this.hsgHangToaID = hsgHangToaID;
	}

	public void setHangToa(HangToa hangToa) {
		this.hangToa = hangToa;
	}

	public void setHsg(double hsg) {
		this.hsg = hsg;
	}

	public void setNgayCoHieuLuc(LocalDate ngayCoHieuLuc) {
		this.ngayCoHieuLuc = ngayCoHieuLuc;
	}

	public void setNgayHetHieuLuc(LocalDate ngayHetHieuLuc) {
		this.ngayHetHieuLuc = ngayHetHieuLuc;
	}

	@Override
	public String toString() {
		return hsgHangToaID + ";" + hangToa + ";" + hsg
				+ ";" + ngayCoHieuLuc + ";" + ngayHetHieuLuc;
	}	
}