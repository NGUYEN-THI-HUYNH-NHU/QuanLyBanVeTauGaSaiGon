package entity;
/*
 * @(#) DonDatChoChiTiet.java  1.0  [2:57:41 PM] Sep 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 25, 2025
 * @version: 1.0
 */

import java.util.Objects;

public class DonDatChoChiTiet {
	private String donDatChoChiTietID;
	private DonDatCho donDatCho;
	private Ghe ghe;
	private HanhKhach hanhKhach;
	private int thuTuGaDi;
	private int thuTuGaDen;
	private double gia;
	
	public DonDatChoChiTiet(String donDatChoChiTietID, DonDatCho donDatCho, Ghe ghe, HanhKhach hanhKhach, int thuTuGaDi,
			int thuTuGaDen, double gia) {
		super();
		this.donDatChoChiTietID = donDatChoChiTietID;
		this.donDatCho = donDatCho;
		this.ghe = ghe;
		this.hanhKhach = hanhKhach;
		this.thuTuGaDi = thuTuGaDi;
		this.thuTuGaDen = thuTuGaDen;
		this.gia = gia;
	}

	public String getDonDatChoChiTietID() {
		return donDatChoChiTietID;
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public Ghe getGhe() {
		return ghe;
	}

	public HanhKhach getHanhKhach() {
		return hanhKhach;
	}

	public int getThuTuGaDi() {
		return thuTuGaDi;
	}

	public int getThuTuGaDen() {
		return thuTuGaDen;
	}

	public double getGia() {
		return gia;
	}

	public void setDonDatChoChiTietID(String donDatChoChiTietID) {
		if(donDatChoChiTietID == null || donDatChoChiTietID.isEmpty()) {
			throw new IllegalArgumentException("Đơn đặt chỗ chi tiết ID không được để trống!");
		}
		this.donDatChoChiTietID = donDatChoChiTietID;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		this.donDatCho = donDatCho;
	}

	public void setGhe(Ghe ghe) {
		this.ghe = ghe;
	}

	public void setHanhKhach(HanhKhach hanhKhach) {
		this.hanhKhach = hanhKhach;
	}

	public void setThuTuGaDi(int thuTuGaDi) {
		this.thuTuGaDi = thuTuGaDi;
	}

	public void setThuTuGaDen(int thuTuGaDen) {
		this.thuTuGaDen = thuTuGaDen;
	}

	public void setGia(double gia) {
		this.gia = gia;
	}

	@Override
	public String toString() {
		return donDatChoChiTietID + ";" + donDatCho + ";"
				+ ghe + ";" + hanhKhach + ";" + thuTuGaDi + ";" + thuTuGaDen
				+ ";" + gia;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DonDatChoChiTiet that = (DonDatChoChiTiet) o;
		return Objects.equals(donDatChoChiTietID, that.donDatChoChiTietID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(donDatChoChiTietID);
	}
}
