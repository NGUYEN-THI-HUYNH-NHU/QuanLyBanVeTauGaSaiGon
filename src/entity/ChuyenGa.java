package entity;
/*
 * @(#) ChuyenGa.java  1.0  [3:17:46 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.time.LocalTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class ChuyenGa {
	private Chuyen chuyen;
	private Ga ga;
	private int thuTu;
	private LocalDate ngayDen;
	private LocalTime gioDen;
	private LocalDate ngayDi;
	private LocalTime gioDi;

	public ChuyenGa() {
		super();
	}

	public ChuyenGa(Chuyen chuyen, Ga ga, int thuTu, LocalDate ngayDen, LocalTime gioDen, LocalDate ngayDi,
			LocalTime gioDi) {
		super();
		this.chuyen = chuyen;
		this.ga = ga;
		this.thuTu = thuTu;
		this.ngayDen = ngayDen;
		this.gioDen = gioDen;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public Ga getGa() {
		return ga;
	}

	public int getThuTu() {
		return thuTu;
	}

	public LocalDate getNgayDen() {
		return ngayDen;
	}

	public LocalTime getGioDen() {
		return gioDi;
	}

	public LocalDate getNgayDi() {
		return ngayDi;
	}

	public LocalTime getGioDi() {
		return gioDi;
	}

	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
	}

	public void setGa(Ga ga) {
		this.ga = ga;
	}

	public void setThuTu(int thuTu) {
		if (thuTu < 1) {
			throw new IllegalArgumentException("Thứ tự không được nhỏ hơn 1!");
		}
		this.thuTu = thuTu;
	}

	public void setNgayDen(LocalDate ngayDen) {
		this.ngayDen = ngayDen;
	}

	public void setGioDen(LocalTime gioDen) {
		this.gioDen = gioDen;
	}

	public void setNgayDi(LocalDate ngayDi) {
		this.ngayDi = ngayDi;
	}

	public void setGioDi(LocalTime gioDi) {
		this.gioDi = gioDi;
	}

	@Override
	public String toString() {
		return chuyen + ";" + ga + ";" + thuTu + ";" + ngayDen + ";" + gioDen + ";" + ngayDi + ";" + gioDi;
	}
}