package entity;
/*
 * @(#) PhieuHoanVe.java  1.0  [1:59:59 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class DonHoanDoiVe {
	private String donHoanDoiVeID;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private boolean laDonHoan;
	private LocalDate ngayYeuCau;
	private double tongTienHoan;
	private String trangThai;

	public DonHoanDoiVe(String donHoanDoiVeID, KhachHang khachHang, NhanVien nhanVien, boolean laDonHoan, LocalDate ngayYeuCau, double tongTienHoan, String trangThai) {
		this.donHoanDoiVeID = donHoanDoiVeID;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.laDonHoan = laDonHoan;
		this.ngayYeuCau = ngayYeuCau;
		this.tongTienHoan = tongTienHoan;
		this.trangThai = trangThai;
	}

	public String getDonHoanDoiVeID() {
		return donHoanDoiVeID;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public boolean isLaDonHoan() {
		return laDonHoan;
	}

	public LocalDate getNgayYeuCau() {
		return ngayYeuCau;
	}

	public double getTongTienHoan() {
		return tongTienHoan;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setDonHoanDoiVeID(String donHoanDoiVeID) {
		if(donHoanDoiVeID != null && !donHoanDoiVeID.trim().isEmpty()) {
			this.donHoanDoiVeID = donHoanDoiVeID;
		}else{
			throw new IllegalArgumentException("Đơn hoàn đổi vé ID không được để trống!");
		}
	}

	public void setKhachHang(KhachHang khachHang) {
		if(khachHang != null) {
			this.khachHang = khachHang;
		}else{
			throw new IllegalArgumentException("Khách hàng không được rỗng");
		}
	}

	public void setNhanVien(NhanVien nhanVien) {
		if(nhanVien != null) {
			this.nhanVien = nhanVien;
		}else{
			throw new IllegalArgumentException("Nhân viên không được rỗng");
		}
	}

	public void setLaDonHoan(boolean laDonHoan) {
		if(!laDonHoan && !this.laDonHoan) {
			throw new IllegalArgumentException("Không thể chuyển đổi từ đơn đổi vé sang đơn hoàn vé");
		}
		this.laDonHoan = laDonHoan;
	}

	public void setNgayYeuCau(LocalDate ngayYeuCau) {
		if(ngayYeuCau == null || ngayYeuCau.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Ngày yêu cầu không được rỗng và phải là ngày trong quá khứ hoặc ngày hiện tại");
		}
		this.ngayYeuCau = ngayYeuCau;
	}

	public void setTongTienHoan(double tongTienHoan) {
		this.tongTienHoan = tongTienHoan;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return donHoanDoiVeID + ";"
				+ khachHang + ";" + nhanVien + ";"
				+ laDonHoan + ";"
				+ ngayYeuCau + ";" + tongTienHoan + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DonHoanDoiVe that = (DonHoanDoiVe) o;
		return Objects.equals(getDonHoanDoiVeID(), that.getDonHoanDoiVeID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getDonHoanDoiVeID());
	}
}
