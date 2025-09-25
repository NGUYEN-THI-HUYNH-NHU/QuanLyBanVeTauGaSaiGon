package entity;
/*
 * @(#) Chuyen.java  1.0  [10:09:55 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Chuyen {
	private String chuyenID;
	private Tuyen tuyen;
	private Tau tau;
	private LocalDateTime ngayGioKhoiHanh;
	private LocalDateTime ngayGioDen;
	
	public Chuyen(String chuyenID, Tuyen tuyen, Tau tau, LocalDateTime ngayGioKhoiHanh, LocalDateTime ngayGioDen) {
		super();
		this.chuyenID = chuyenID;
		this.tuyen = tuyen;
		this.tau = tau;
		this.ngayGioKhoiHanh = ngayGioKhoiHanh;
		this.ngayGioDen = ngayGioDen;
	}

	public String getChuyenID() {
		return chuyenID;
	}

	public Tuyen getTuyen() {
		return tuyen;
	}

	public Tau getTau() {
		return tau;
	}

	public LocalDateTime getNgayGioKhoiHanh() {
		return ngayGioKhoiHanh;
	}

	public LocalDateTime getNgayGioDen() {
		return ngayGioDen;
	}

	public void setChuyenID(String chuyenID) {
		this.chuyenID = chuyenID;
	}

	public void setTuyen(Tuyen tuyen) {
		this.tuyen = tuyen;
	}

	public void setTau(Tau tau) {
		this.tau = tau;
	}

	public void setNgayGioKhoiHanh(LocalDateTime ngayGioKhoiHanh) {
		this.ngayGioKhoiHanh = ngayGioKhoiHanh;
	}

	public void setNgayGioDen(LocalDateTime ngayGioDen) {
		this.ngayGioDen = ngayGioDen;
	}

	@Override
	public String toString() {
		return chuyenID + ";" + tuyen + ";" + tau + ";"
				+ ngayGioKhoiHanh + ";" + ngayGioDen;
	}
}