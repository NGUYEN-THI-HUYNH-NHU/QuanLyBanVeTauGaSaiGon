package entity;
/*
 * @(#) HeSoGiaLoaiTau.java  1.0  [10:20:47 AM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

import entity.type.LoaiTau;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public class HeSoGiaLoaiTau {
	private String hsgLoaiTauID;
	private LoaiTau loaiTau;
	private double hsg;
	private LocalDate ngayCoHieuLuc;
	private LocalDate ngayHetHieuLuc;
	
	public HeSoGiaLoaiTau(String hsgLoaiTauID, LoaiTau loaiTau, double hsg, LocalDate ngayCoHieuLuc,
			LocalDate ngayHetHieuLuc) {
		super();
		this.hsgLoaiTauID = hsgLoaiTauID;
		this.loaiTau = loaiTau;
		this.hsg = hsg;
		this.ngayCoHieuLuc = ngayCoHieuLuc;
		this.ngayHetHieuLuc = ngayHetHieuLuc;
	}

	public String getHsgLoaiTauID() {
		return hsgLoaiTauID;
	}

	public LoaiTau getLoaiTau() {
		return loaiTau;
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

	public void setHsgLoaiTauID(String hsgLoaiTauID) {
		this.hsgLoaiTauID = hsgLoaiTauID;
	}

	public void setLoaiTau(LoaiTau loaiTau) {
		this.loaiTau = loaiTau;
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
		return hsgLoaiTauID + ";" + loaiTau + ";" + hsg
				+ ";" + ngayCoHieuLuc + ";" + ngayHetHieuLuc;
	}
	
	
}