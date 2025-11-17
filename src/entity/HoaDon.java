package entity;
/*
 * @(#) HoaDon.java  1.0  [12:36:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */
import java.time.LocalDateTime;
import java.util.Objects;

public class HoaDon {
	private String hoaDonID;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private LocalDateTime thoiDiemTao;
	private double tongTien;
	private String maGD;
	private double tienNhan;
	private double tienHoan;
	private boolean isThanhToanTienMat;
	private boolean trangThai;

	public HoaDon(String hoaDonID, KhachHang khachHang, NhanVien nhanVien, LocalDateTime thoiDiemTao, double tongTien,
			String maGD, double tienNhan, double tienHoan, boolean isThanhToanTienMat, boolean trangThai) {
		super();
		this.hoaDonID = hoaDonID;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.thoiDiemTao = thoiDiemTao;
		this.tongTien = tongTien;
		this.maGD = maGD;
		this.tienNhan = tienNhan;
		this.tienHoan = tienHoan;
		this.isThanhToanTienMat = isThanhToanTienMat;
		this.trangThai = trangThai;
	}

	public HoaDon() {
		super();
	}

	public String getHoaDonID() {
		return hoaDonID;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public LocalDateTime getThoiDiemTao() {
		return thoiDiemTao;
	}

	public void setHoaDonID(String hoaDonID) {
		if (hoaDonID == null || hoaDonID.isEmpty()) {
			throw new IllegalArgumentException("HoaDonID không được để trống!");
		}
		this.hoaDonID = hoaDonID;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
		this.thoiDiemTao = thoiDiemTao;
	}

	public double getTongTien() {
		return tongTien;
	}

	public String getMaGD() {
		return maGD;
	}

	public double getTienNhan() {
		return tienNhan;
	}

	public double getTienHoan() {
		return tienHoan;
	}

	public boolean isThanhToanTienMat() {
		return isThanhToanTienMat;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public void setMaGD(String maGD) {
		this.maGD = maGD;
	}

	public void setTienNhan(double tienNhan) {
		this.tienNhan = tienNhan;
	}

	public void setTienHoan(double tienHoan) {
		this.tienHoan = tienHoan;
	}

	public void setThanhToanTienMat(boolean isThanhToanTienMat) {
		this.isThanhToanTienMat = isThanhToanTienMat;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		HoaDon hoaDon = (HoaDon) o;
		return Objects.equals(hoaDonID, hoaDon.hoaDonID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(hoaDonID);
	}

	@Override
	public String toString() {
		return hoaDonID + ";" + khachHang.getKhachHangID() + ";" + nhanVien.getNhanVienID() + ";" + thoiDiemTao + ";"
				+ tongTien + ";" + maGD + ";" + tienNhan + ";" + tienHoan + ";" + isThanhToanTienMat + ";" + trangThai;
	}
}