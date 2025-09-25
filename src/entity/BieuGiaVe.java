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

import java.time.LocalDate;
import java.time.LocalDateTime;

import entity.type.LoaiTau;

public class BieuGiaVe {
	private String bieuGiaVeID;
	private Tuyen tuyenApDung;
	private HangToa hangToaApDung;
	private LoaiTau loaiTauApDung;
	private int minKm;
	private int maxKm;
	private double donGiaTrenKm;
	private double giaCoDinh;
	private double phuPhiCaoDiem;
	private LocalDate ngayCoHieuLuc;
	private LocalDate ngayHetHieuLuc;
	private int doUuTien;
	private boolean isCoHieuLuc;
	private LocalDateTime thoiDiemTao;
	private LocalDateTime thoiDiemSua;
	
	public BieuGiaVe(String bieuGiaVeID, Tuyen tuyenApDung, HangToa hangToaApDung, LoaiTau loaiTauApDung, int minKm,
			int maxKm, double donGiaTrenKm, double giaCoDinh, double phuPhiCaoDiem, LocalDate ngayCoHieuLuc,
			LocalDate ngayHetHieuLuc, int doUuTien, boolean isCoHieuLuc, LocalDateTime thoiDiemTao,
			LocalDateTime thoiDiemSua) {
		super();
		this.bieuGiaVeID = bieuGiaVeID;
		this.tuyenApDung = tuyenApDung;
		this.hangToaApDung = hangToaApDung;
		this.loaiTauApDung = loaiTauApDung;
		this.minKm = minKm;
		this.maxKm = maxKm;
		this.donGiaTrenKm = donGiaTrenKm;
		this.giaCoDinh = giaCoDinh;
		this.phuPhiCaoDiem = phuPhiCaoDiem;
		this.ngayCoHieuLuc = ngayCoHieuLuc;
		this.ngayHetHieuLuc = ngayHetHieuLuc;
		this.doUuTien = doUuTien;
		this.isCoHieuLuc = isCoHieuLuc;
		this.thoiDiemTao = thoiDiemTao;
		this.thoiDiemSua = thoiDiemSua;
	}

	public String getBieuGiaVeID() {
		return bieuGiaVeID;
	}

	public Tuyen getTuyenApDung() {
		return tuyenApDung;
	}

	public HangToa getHangToaApDung() {
		return hangToaApDung;
	}

	public LoaiTau getLoaiTauApDung() {
		return loaiTauApDung;
	}

	public int getMinKm() {
		return minKm;
	}

	public int getMaxKm() {
		return maxKm;
	}

	public double getDonGiaTrenKm() {
		return donGiaTrenKm;
	}

	public double getGiaCoDinh() {
		return giaCoDinh;
	}

	public double getPhuPhiCaoDiem() {
		return phuPhiCaoDiem;
	}

	public LocalDate getNgayCoHieuLuc() {
		return ngayCoHieuLuc;
	}

	public LocalDate getNgayHetHieuLuc() {
		return ngayHetHieuLuc;
	}

	public int getDoUuTien() {
		return doUuTien;
	}

	public boolean isCoHieuLuc() {
		return isCoHieuLuc;
	}

	public LocalDateTime getThoiDiemTao() {
		return thoiDiemTao;
	}

	public LocalDateTime getThoiDiemSua() {
		return thoiDiemSua;
	}

	public void setBieuGiaVeID(String bieuGiaVeID) {
		this.bieuGiaVeID = bieuGiaVeID;
	}

	public void setTuyenApDung(Tuyen tuyenApDung) {
		this.tuyenApDung = tuyenApDung;
	}

	public void setHangToaApDung(HangToa hangToaApDung) {
		this.hangToaApDung = hangToaApDung;
	}

	public void setLoaiTauApDung(LoaiTau loaiTauApDung) {
		this.loaiTauApDung = loaiTauApDung;
	}

	public void setMinKm(int minKm) {
		this.minKm = minKm;
	}

	public void setMaxKm(int maxKm) {
		this.maxKm = maxKm;
	}

	public void setDonGiaTrenKm(double donGiaTrenKm) {
		this.donGiaTrenKm = donGiaTrenKm;
	}

	public void setGiaCoDinh(double giaCoDinh) {
		this.giaCoDinh = giaCoDinh;
	}

	public void setPhuPhiCaoDiem(double phuPhiCaoDiem) {
		this.phuPhiCaoDiem = phuPhiCaoDiem;
	}

	public void setNgayCoHieuLuc(LocalDate ngayCoHieuLuc) {
		this.ngayCoHieuLuc = ngayCoHieuLuc;
	}

	public void setNgayHetHieuLuc(LocalDate ngayHetHieuLuc) {
		this.ngayHetHieuLuc = ngayHetHieuLuc;
	}

	public void setDoUuTien(int doUuTien) {
		this.doUuTien = doUuTien;
	}

	public void setCoHieuLuc(boolean isCoHieuLuc) {
		this.isCoHieuLuc = isCoHieuLuc;
	}

	public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
		this.thoiDiemTao = thoiDiemTao;
	}

	public void setThoiDiemSua(LocalDateTime thoiDiemSua) {
		this.thoiDiemSua = thoiDiemSua;
	}

	@Override
	public String toString() {
		return bieuGiaVeID + ";" + tuyenApDung + ";"
				+ hangToaApDung + ";" + loaiTauApDung + ";" + minKm + ";" + maxKm
				+ ";" + donGiaTrenKm + ";" + giaCoDinh + ";" + phuPhiCaoDiem
				+ ";" + ngayCoHieuLuc + ";" + ngayHetHieuLuc + ";" + doUuTien
				+ ";" + isCoHieuLuc + ";" + thoiDiemTao + ";" + thoiDiemSua;
	}	
}