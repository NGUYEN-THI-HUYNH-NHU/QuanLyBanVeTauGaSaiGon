package entity;
/*
 * @(#) DieuKienKhuyenMai.java  1.0  [3:36:18 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.HangKhachHang;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class DieuKienKhuyenMai {
	private String dieuKienID;
	private KhuyenMai khuyenMai;
	private Tuyen tuyen;
	private LoaiTau loaiTau;
	private HangToa hangToa;
	private HangKhachHang hangKhachHang;
	private LoaiDoiTuong loaiDoiTuong;
	private int ngayTrongTuan;
	private boolean ngayLe;
	private double minGiaTriDonHang;
	
	public DieuKienKhuyenMai(String dieuKienID, KhuyenMai khuyenMai, Tuyen tuyen, LoaiTau loaiTau, HangToa hangToa,
			HangKhachHang hangKhachHang, LoaiDoiTuong loaiDoiTuong, int ngayTrongTuan, boolean ngayLe,
			double minGiaTriDonHang) {
		super();
		this.dieuKienID = dieuKienID;
		this.khuyenMai = khuyenMai;
		this.tuyen = tuyen;
		this.loaiTau = loaiTau;
		this.hangToa = hangToa;
		this.hangKhachHang = hangKhachHang;
		this.loaiDoiTuong = loaiDoiTuong;
		this.ngayTrongTuan = ngayTrongTuan;
		this.ngayLe = ngayLe;
		this.minGiaTriDonHang = minGiaTriDonHang;
	}

	public String getDieuKienID() {
		return dieuKienID;
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public Tuyen getTuyen() {
		return tuyen;
	}

	public LoaiTau getLoaiTau() {
		return loaiTau;
	}

	public HangToa getHangToa() {
		return hangToa;
	}

	public HangKhachHang getHangKhachHang() {
		return hangKhachHang;
	}

	public LoaiDoiTuong getLoaiDoiTuong() {
		return loaiDoiTuong;
	}

	public int getNgayTrongTuan() {
		return ngayTrongTuan;
	}

	public boolean isNgayLe() {
		return ngayLe;
	}

	public double getMinGiaTriDonHang() {
		return minGiaTriDonHang;
	}

	public void setDieuKienID(String dieuKienID) {
		this.dieuKienID = dieuKienID;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	public void setTuyen(Tuyen tuyen) {
		this.tuyen = tuyen;
	}

	public void setLoaiTau(LoaiTau loaiTau) {
		this.loaiTau = loaiTau;
	}

	public void setHangToa(HangToa hangToa) {
		this.hangToa = hangToa;
	}

	public void setHangKhachHang(HangKhachHang hangKhachHang) {
		this.hangKhachHang = hangKhachHang;
	}

	public void setLoaiDoiTuong(LoaiDoiTuong loaiDoiTuong) {
		this.loaiDoiTuong = loaiDoiTuong;
	}

	public void setNgayTrongTuan(int ngayTrongTuan) {
		this.ngayTrongTuan = ngayTrongTuan;
	}

	public void setNgayLe(boolean ngayLe) {
		this.ngayLe = ngayLe;
	}

	public void setMinGiaTriDonHang(double minGiaTriDonHang) {
		this.minGiaTriDonHang = minGiaTriDonHang;
	}

	@Override
	public String toString() {
		return dieuKienID + ";" + khuyenMai + ";" + tuyen
				+ ";" + loaiTau + ";" + hangToa + ";" + hangKhachHang
				+ ";" + loaiDoiTuong + ";" + ngayTrongTuan + ";" + ngayLe
				+ ";" + minGiaTriDonHang;
	}
}