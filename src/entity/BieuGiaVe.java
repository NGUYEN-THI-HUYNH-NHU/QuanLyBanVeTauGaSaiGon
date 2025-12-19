package entity;
/*
 * @(#) BangGia.java  1.0  [12:54:26 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import entity.type.HangToa;
import entity.type.LoaiTau;

public class BieuGiaVe {
	private String bieuGiaVeID;
	private Tuyen tuyenApDung;
	private LoaiTau loaiTauApDung;
	private HangToa hangToaApDung;
	private int minKm;
	private int maxKm;
	private double donGiaTrenKm;
	private double giaCoBan;
	private double phuPhiCaoDiem;
	private int doUuTien;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;

	public BieuGiaVe() {
		super();
	}

	public BieuGiaVe(String bieuGiaVeID, Tuyen tuyenApDung, LoaiTau loaiTauApDung, HangToa hangToaApDung, int minKm,
			int maxKm, double donGiaTrenKm, double giaCoBan, double phuPhiCaoDiem, int doUuTien, LocalDate ngayBatDau,
			LocalDate ngayKetThuc) {
		super();
		this.bieuGiaVeID = bieuGiaVeID;
		this.tuyenApDung = tuyenApDung;
		this.loaiTauApDung = loaiTauApDung;
		this.hangToaApDung = hangToaApDung;
		this.minKm = minKm;
		this.maxKm = maxKm;
		this.donGiaTrenKm = donGiaTrenKm;
		this.giaCoBan = giaCoBan;
		this.phuPhiCaoDiem = phuPhiCaoDiem;
		this.doUuTien = doUuTien;
		this.ngayBatDau = ngayBatDau;
		this.ngayKetThuc = ngayKetThuc;
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

	public void setBieuGiaVeID(String bieuGiaVeID) {
		this.bieuGiaVeID = bieuGiaVeID;
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		this.ngayBatDau = ngayBatDau;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		this.ngayKetThuc = ngayKetThuc;
	}

	public Tuyen getTuyenApDung() {
		return tuyenApDung;
	}

	public void setTuyenApDung(Tuyen tuyenApDung) {
		this.tuyenApDung = tuyenApDung;
	}

	@Override
	public String toString() {
		return bieuGiaVeID + ";" + hangToaApDung + ";" + loaiTauApDung + ";" + minKm + ";" + maxKm + ";" + donGiaTrenKm
				+ ";" + giaCoBan + ";" + phuPhiCaoDiem + ";" + doUuTien;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BieuGiaVe bieuGiaVe = (BieuGiaVe) o;
		return Objects.equals(bieuGiaVeID, bieuGiaVe.bieuGiaVeID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(bieuGiaVeID);
	}
}