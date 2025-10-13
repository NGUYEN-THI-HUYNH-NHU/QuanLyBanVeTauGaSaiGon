package entity;
/*
 * @(#) ThanhToan.java  1.0  [12:54:12 PM] Sep 18, 2025
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

public class GiaoDichThanhToan {
	private String giaoDichThanhToanID;
	private HoaDon hoaDon;
	private double tienNhan;
	private double tienHoan;
	private LocalDateTime thoiDiemThanhToan;
	private boolean isThanhToanTienMat;
	private boolean trangThai;

	public GiaoDichThanhToan(String giaoDichThanhToanID, HoaDon hoaDon, double tienNhan, double tienHoan, LocalDateTime thoiDiemThanhToan, boolean isThanhToanTienMat, boolean trangThai) {
		this.giaoDichThanhToanID = giaoDichThanhToanID;
		this.hoaDon = hoaDon;
		this.tienNhan = tienNhan;
		this.tienHoan = tienHoan;
		this.thoiDiemThanhToan = thoiDiemThanhToan;
		this.isThanhToanTienMat = isThanhToanTienMat;
		this.trangThai = trangThai;
	}

	public String getGiaoDichThanhToanID() {
		return giaoDichThanhToanID;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public double getTienNhan() {
		return tienNhan;
	}

	public double getTienHoan() {
		return tienHoan;
	}

	public LocalDateTime getThoiDiemThanhToan() {
		return thoiDiemThanhToan;
	}

	public boolean isThanhToanTienMat() {
		return isThanhToanTienMat;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setGiaoDichThanhToanID(String giaoDichThanhToanID) {
		if(giaoDichThanhToanID != null && !giaoDichThanhToanID.trim().isEmpty()) {
			this.giaoDichThanhToanID = giaoDichThanhToanID;
		}else{
			throw new IllegalArgumentException("GiaoDichThanhToanID không được để trống!");
		}
	}

	public void setHoaDon(HoaDon hoaDon) {
		if(hoaDon != null){
			this.hoaDon = hoaDon;
		}else{
			throw new IllegalArgumentException("Hóa đơn không được để trống!");
		}
	}

	public void setTienNhan(double tienNhan) {
		this.tienNhan = tienNhan;
	}

	public void setTienHoan(double tienHoan) {
		this.tienHoan = tienHoan;
	}

	public void setThoiDiemThanhToan(LocalDateTime thoiDiemThanhToan) {
		this.thoiDiemThanhToan = thoiDiemThanhToan;
	}

	public void setThanhToanTienMat(boolean thanhToanTienMat) {
		isThanhToanTienMat = thanhToanTienMat;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return giaoDichThanhToanID + ";" + hoaDon + ";" + tienNhan + ";" + tienHoan + ";" + thoiDiemThanhToan
				+ ";" + isThanhToanTienMat + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GiaoDichThanhToan that = (GiaoDichThanhToan) o;
		return Objects.equals(giaoDichThanhToanID, that.giaoDichThanhToanID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(giaoDichThanhToanID);
	}
}