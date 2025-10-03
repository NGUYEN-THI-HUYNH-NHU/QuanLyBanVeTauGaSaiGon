package entity;
/*
 * @(#) ChuyenGa.java  1.0  [3:17:46 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

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
	private LocalDateTime gioDen;
	private LocalDateTime gioDi;
	
	public ChuyenGa(Chuyen chuyen, Ga ga, int thuTu, LocalDateTime gioDen,
					LocalDateTime gioDi) {
		super();
		this.chuyen = chuyen;
		this.ga = ga;
		this.thuTu = thuTu;
		this.gioDen = gioDen;
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

	public LocalDateTime getGioDen() {
		return gioDen;
	}

	public LocalDateTime getGioDi() {
		return gioDi;
	}

	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
	}

	public void setGa(Ga ga) {
		this.ga = ga;
	}

	public void setThuTu(int thuTu) {
		if(thuTu < 1) {
			throw new IllegalArgumentException("Thứ tự không được nhỏ hơn 1!");
		}
		this.thuTu = thuTu;
	}

	public void setGioDen(LocalDateTime gioDen) {
		this.gioDen = gioDen;
	}

	public void setGioDi(LocalDateTime gioDi) {
		this.gioDi = gioDi;
	}

	@Override
	public String toString() {
		return  chuyen + ";" + ga + ";" + thuTu
				+ ";" + gioDen + ";" + gioDi;
	}

}