package entity;
/*
 * @(#) PhieuHoanVe.java  1.0  [1:59:59 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class DonHoanDoiVe {
	private String donHoanDoiVeID;
	private DonDatCho donDatCho;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private String loaiHoanDoiVe;
	private LocalDate ngayYeuCau;
	private double tongTienHoan;
	private boolean trangThai;
	
	public DonHoanDoiVe(String donHoanDoiVeID, DonDatCho donDatCho, KhachHang khachHang, NhanVien nhanVien,
			String loaiHoanDoiVe, LocalDate ngayYeuCau, double tongTienHoan, boolean trangThai) {
		super();
		this.donHoanDoiVeID = donHoanDoiVeID;
		this.donDatCho = donDatCho;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.loaiHoanDoiVe = loaiHoanDoiVe;
		this.ngayYeuCau = ngayYeuCau;
		this.tongTienHoan = tongTienHoan;
		this.trangThai = trangThai;
	}

	public String getDonHoanDoiVeID() {
		return donHoanDoiVeID;
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public String getLoaiHoanDoiVe() {
		return loaiHoanDoiVe;
	}

	public LocalDate getNgayYeuCau() {
		return ngayYeuCau;
	}

	public double getTongTienHoan() {
		return tongTienHoan;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setDonHoanDoiVeID(String donHoanDoiVeID) {
		this.donHoanDoiVeID = donHoanDoiVeID;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		this.donDatCho = donDatCho;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public void setLoaiHoanDoiVe(String loaiHoanDoiVe) {
		this.loaiHoanDoiVe = loaiHoanDoiVe;
	}

	public void setNgayYeuCau(LocalDate ngayYeuCau) {
		this.ngayYeuCau = ngayYeuCau;
	}

	public void setTongTienHoan(double tongTienHoan) {
		this.tongTienHoan = tongTienHoan;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return donHoanDoiVeID + ";" + donDatCho + ";"
				+ khachHang + ";" + nhanVien + ";" + loaiHoanDoiVe + ";"
				+ ngayYeuCau + ";" + tongTienHoan + ";" + trangThai;
	}
}
