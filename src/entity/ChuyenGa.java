package entity;
/*
 * @(#) ChuyenGa.java  1.0  [3:17:46 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class ChuyenGa {
	private String chuyenGaID;
	private Chuyen chuyen;
	private Ga ga;
	private int thuThu;
	private LocalDateTime ngayGioKhoiHanh;
	private LocalDateTime ngayGioDen;
	
	public ChuyenGa(String chuyenGaID, Chuyen chuyen, Ga ga, int thuThu, LocalDateTime ngayGioKhoiHanh,
			LocalDateTime ngayGioDen) {
		super();
		this.chuyenGaID = chuyenGaID;
		this.chuyen = chuyen;
		this.ga = ga;
		this.thuThu = thuThu;
		this.ngayGioKhoiHanh = ngayGioKhoiHanh;
		this.ngayGioDen = ngayGioDen;
	}

	public String getChuyenGaID() {
		return chuyenGaID;
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public Ga getGa() {
		return ga;
	}

	public int getThuThu() {
		return thuThu;
	}

	public LocalDateTime getNgayGioKhoiHanh() {
		return ngayGioKhoiHanh;
	}

	public LocalDateTime getNgayGioDen() {
		return ngayGioDen;
	}

	public void setChuyenGaID(String chuyenGaID) {
		this.chuyenGaID = chuyenGaID;
	}

	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
	}

	public void setGa(Ga ga) {
		this.ga = ga;
	}

	public void setThuThu(int thuThu) {
		this.thuThu = thuThu;
	}

	public void setNgayGioKhoiHanh(LocalDateTime ngayGioKhoiHanh) {
		this.ngayGioKhoiHanh = ngayGioKhoiHanh;
	}

	public void setNgayGioDen(LocalDateTime ngayGioDen) {
		this.ngayGioDen = ngayGioDen;
	}

	@Override
	public String toString() {
		return chuyenGaID + ";" + chuyen + ";" + ga + ";" + thuThu
				+ ";" + ngayGioKhoiHanh + ";" + ngayGioDen;
	}
}