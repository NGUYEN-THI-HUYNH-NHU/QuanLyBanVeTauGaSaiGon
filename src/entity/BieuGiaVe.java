package entity;
/*
 * @(#) BangGia.java  1.0  [12:54:26 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import entity.type.HangToa;

import java.util.Objects;

import entity.type.LoaiTau;

public class BieuGiaVe {
	private String bieuGiaVeID;
	private HangToa hangToaApDung;
	private LoaiTau loaiTauApDung;
	private int minKm;
	private int maxKm;
	private double donGiaTrenKm;
	private double giaCoBan;
	private double phuPhiCaoDiem;
	private int doUuTien;
	private boolean isCoHieuLuc;

	public BieuGiaVe(){
		super();
	}

	public BieuGiaVe(String bieuGiaVeID, HangToa hangToaApDungID, LoaiTau loaiTauApDungID, int minKm, int maxKm, double donGiaTrenKm, double giaCoBan, double phuPhiCaoDiem, int doUuTien, boolean isCoHieuLuc) {
		this.bieuGiaVeID = bieuGiaVeID;
		this.hangToaApDung = hangToaApDungID;
		this.loaiTauApDung = loaiTauApDungID;
		this.minKm = minKm;
		this.maxKm = maxKm;
		this.donGiaTrenKm = donGiaTrenKm;
		this.giaCoBan = giaCoBan;
		this.phuPhiCaoDiem = phuPhiCaoDiem;
		this.doUuTien = doUuTien;
		this.isCoHieuLuc = isCoHieuLuc;
	}

	public String getBieuGiaVeID() {
		return bieuGiaVeID;
	}

	public HangToa getHangToaApDung() {
		return hangToaApDung;
	}

	public void setHangToaApDung(HangToa hangToaApDung) {
		this.hangToaApDung = hangToaApDung;
	}

	public LoaiTau getLoaiTauApDung() {
		return loaiTauApDung;
	}

	public void setLoaiTauApDung(LoaiTau loaiTauApDung) {
		this.loaiTauApDung = loaiTauApDung;
	}

	public int getMinKm() {
		return minKm;
	}

	public void setMinKm(int minKm) {
		if(minKm <= 0) {
			throw new IllegalArgumentException("Số km tối thiểu phải lớn hơn 0");
		}
		this.minKm = minKm;
	}

	public int getMaxKm() {
		return maxKm;
	}

	public void setMaxKm(int maxKm) {
		this.maxKm = maxKm;
	}

	public double getDonGiaTrenKm() {
		return donGiaTrenKm;
	}

	public void setDonGiaTrenKm(double donGiaTrenKm) {
		this.donGiaTrenKm = donGiaTrenKm;
	}

	public double getGiaCoBan() {
		return giaCoBan;
	}

	public void setGiaCoBan(double giaCoBan) {
		this.giaCoBan = giaCoBan;
	}

	public double getPhuPhiCaoDiem() {
		return phuPhiCaoDiem;
	}

	public void setPhuPhiCaoDiem(double phuPhiCaoDiem) {
		this.phuPhiCaoDiem = phuPhiCaoDiem;
	}

	public int getDoUuTien() {
		return doUuTien;
	}

	public void setDoUuTien(int doUuTien) {
		this.doUuTien = doUuTien;
	}

	public boolean isCoHieuLuc() {
		return isCoHieuLuc;
	}

	public void setCoHieuLuc(boolean coHieuLuc) {
		isCoHieuLuc = coHieuLuc;
	}

	public void setBieuGiaVeID(String bieuGiaVeID) {
		if (bieuGiaVeID != null && !bieuGiaVeID.isEmpty()) {
			this.bieuGiaVeID = bieuGiaVeID;
		} else {
			throw new IllegalArgumentException("Biểu giá vé ID không được để trống!");
		}
	}

	@Override
	public String toString() {
		return bieuGiaVeID + ";"
				+ hangToaApDung + ";" + loaiTauApDung + ";" + minKm + ";" + maxKm
				+ ";" + donGiaTrenKm + ";" + giaCoBan + ";" + phuPhiCaoDiem
				+ ";" + doUuTien
				+ ";" + isCoHieuLuc ;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BieuGiaVe bieuGiaVe = (BieuGiaVe) o;
		return Objects.equals(bieuGiaVeID, bieuGiaVe.bieuGiaVeID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(bieuGiaVeID);
	}
}