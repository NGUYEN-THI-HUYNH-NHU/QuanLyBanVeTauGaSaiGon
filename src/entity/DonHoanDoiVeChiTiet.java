package entity;
/*
 * @(#) DonHoanDoiVe_ChiTiet.java  1.0  [2:48:23 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

import java.util.Objects;

public class DonHoanDoiVeChiTiet {
	private String donHoanDoiVeChiTietID;
	private GiaoDichHoanDoi donHoanDoiVe;
	private Ve veCu;
	private Ve veMoi;
	private double soTienHoan;
	private double phiPhatSinh;
	private String ghiChu;
	
	public DonHoanDoiVeChiTiet(String donHoanDoiVeChiTietID, GiaoDichHoanDoi donHoanDoiVe, Ve veCu, Ve veMoi,
			double soTienHoan, double phiPhatSinh, String ghiChu) {
		super();
		this.donHoanDoiVeChiTietID = donHoanDoiVeChiTietID;
		this.donHoanDoiVe = donHoanDoiVe;
		this.veCu = veCu;
		this.veMoi = veMoi;
		this.soTienHoan = soTienHoan;
		this.phiPhatSinh = phiPhatSinh;
		this.ghiChu = ghiChu;
	}

	public String getDonHoanDoiVeChiTietID() {
		return donHoanDoiVeChiTietID;
	}

	public GiaoDichHoanDoi getDonHoanDoiVe() {
		return donHoanDoiVe;
	}

	public Ve getVeCu() {
		return veCu;
	}

	public Ve getVeMoi() {
		return veMoi;
	}

	public double getSoTienHoan() {
		return soTienHoan;
	}

	public double getPhiPhatSinh() {
		return phiPhatSinh;
	}

	public String getGhiChu() {
		return ghiChu;
	}

	public void setDonHoanDoiVeChiTietID(String donHoanDoiVeChiTietID) {
		if(donHoanDoiVeChiTietID != null && !donHoanDoiVeChiTietID.isEmpty()){
			this.donHoanDoiVeChiTietID = donHoanDoiVeChiTietID;
		}
		else {
			throw new IllegalArgumentException("DonHoanDoiVeChiTietID không được để trống!");
		}
	}

	public void setDonHoanDoiVe(GiaoDichHoanDoi donHoanDoiVe) {
		this.donHoanDoiVe = donHoanDoiVe;
	}

	public void setVeCu(Ve veCu) {
		this.veCu = veCu;
	}

	public void setVeMoi(Ve veMoi) {
		this.veMoi = veMoi;
	}

	public void setSoTienHoan(double soTienHoan) {
		this.soTienHoan = soTienHoan;
	}

	public void setPhiPhatSinh(double phiPhatSinh) {
		this.phiPhatSinh = phiPhatSinh;
	}

	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}

	@Override
	public String toString() {
		return donHoanDoiVeChiTietID + ";" + donHoanDoiVe
				+ ";" + veCu + ";" + veMoi + ";" + soTienHoan + ";" + phiPhatSinh
				+ ";" + ghiChu;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DonHoanDoiVeChiTiet that = (DonHoanDoiVeChiTiet) o;
		return Objects.equals(donHoanDoiVeChiTietID, that.donHoanDoiVeChiTietID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(donHoanDoiVeChiTietID);
	}
}