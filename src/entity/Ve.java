package entity;

import java.time.LocalDateTime;
import java.util.Objects;

import entity.type.TrangThaiVe;

/*
 * @(#) Ve.java  1.0  [11:27:32 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class Ve {
	private String veID;
	private DonDatCho donDatCho;
	private Chuyen chuyen;
	private Ghe ghe;
	private HanhKhach hanhKhach;
	private int thuTuGaDi;
	private int thuTuGaDen;
	private double gia;
	private TrangThaiVe trangThai;
	private LocalDateTime ngayBan;
	
	public Ve(String veID, DonDatCho donDatCho, Chuyen chuyen, Ghe ghe, HanhKhach hanhKhach, int thuTuGaDi,
			int thuTuGaDen, double gia, TrangThaiVe trangThai, LocalDateTime ngayBan) {
		super();
		this.veID = veID;
		this.donDatCho = donDatCho;
		this.chuyen = chuyen;
		this.ghe = ghe;
		this.hanhKhach = hanhKhach;
		this.thuTuGaDi = thuTuGaDi;
		this.thuTuGaDen = thuTuGaDen;
		this.gia = gia;
		this.trangThai = trangThai;
		this.ngayBan = ngayBan;
	}

	public String getVeID() {
		return veID;
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public Chuyen getChuyen() {
		return chuyen;
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

	public TrangThaiVe getTrangThai() {
		return trangThai;
	}

	public LocalDateTime getNgayBan() {
		return ngayBan;
	}

	public void setVeID(String veID) {
		if(veID != null && !veID.isEmpty()) {
			this.veID = veID;
		}else{
			throw new IllegalArgumentException("Vé ID không được để trống!");
		}
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		this.donDatCho = donDatCho;
	}

	public void setChuyen(Chuyen chuyen) {
		this.chuyen = chuyen;
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

	public void setTrangThai(TrangThaiVe trangThai) {
		this.trangThai = trangThai;
	}

	public void setNgayBan(LocalDateTime ngayBan) {
		if(ngayBan == null) {
			throw new IllegalArgumentException("Ngày bán không được để trống!");
		}
		this.ngayBan = ngayBan;
	}

	@Override
	public String toString() {
		return veID + ";" + donDatCho + ";" + chuyen + ";" + ghe + ";"
				+ hanhKhach + ";" + thuTuGaDi + ";" + thuTuGaDen + ";" + gia
				+ ";" + trangThai + ";" + ngayBan;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ve ve = (Ve) o;
		return Objects.equals(getVeID(), ve.getVeID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getVeID());
	}
}