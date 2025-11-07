package gui.application.form.banVe;
/*
 * @(#) VeSession.java  1.0  [10:47:34 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import entity.KhachHang;
import entity.PhieuGiuChoChiTiet;
import entity.Ve;

/**
 * VeSession — đại diện 1 dòng trong giỏ vé (chưa thanh toán). Lưu đủ thông tin
 * để hiển thị và để backend gọi hold/confirm sau này.
 */
public class VeSession {
	private final String chuyenID;
	private final String tenTau;
	private final String tenGaDi;
	private final String gaDiID;
	private final String tenGaDen;
	private final String gaDenID;
	private final LocalDate ngayDi;
	private final LocalTime gioDi;
	private final String toaID;
	private final String hangToa;
	private final int soToa;
	private final String gheID;
	private final int soGhe;
	private final int gia;
	private int phongChoVIP;
	private String khuyenMaiCode;
	private int giam;
	private int giamDoiTuong;
	private final LocalDateTime thoiDiemHetHan;
	private KhachHang hanhKhach;
	private PhieuGiuChoChiTiet pgcct;
	private Ve ve;
	private boolean isHanhKhachMoi = true;

	public VeSession(String chuyenID, String tenTau, String tenGaDi, String gaDiID, String tenGaDen, String gaDenID,
			LocalDate ngayDi, LocalTime gioDi, String toaID, String hangToa, int soToa, String gheID, int soGhe,
			int gia, String khuyenMaiCode, int giam, LocalDateTime thoiDiemHetHan) {
		super();
		this.chuyenID = chuyenID;
		this.tenTau = tenTau;
		this.tenGaDi = tenGaDi;
		this.gaDiID = gaDiID;
		this.tenGaDen = tenGaDen;
		this.gaDenID = gaDenID;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
		this.toaID = toaID;
		this.hangToa = hangToa;
		this.soToa = soToa;
		this.gheID = gheID;
		this.soGhe = soGhe;
		this.gia = gia;
		this.khuyenMaiCode = khuyenMaiCode;
		this.giam = giam;
		this.thoiDiemHetHan = thoiDiemHetHan;
	}

	public String getHangToa() {
		return hangToa;
	}

	public String getGaDiID() {
		return gaDiID;
	}

	public String getGaDenID() {
		return gaDenID;
	}

	public String getGheID() {
		return gheID;
	}

	public String getKhuyenMaiCode() {
		return khuyenMaiCode;
	}

	public int getGiam() {
		return giam;
	}

	public int getGia() {
		return gia;
	}

	public String getChuyenID() {
		return chuyenID;
	}

	public String getTenTau() {
		return tenTau;
	}

	public String getTenGaDi() {
		return tenGaDi;
	}

	public String getTenGaDen() {
		return tenGaDen;
	}

	public LocalDate getNgayDi() {
		return ngayDi;
	}

	public LocalTime getGioDi() {
		return gioDi;
	}

	public String getToaID() {
		return toaID;
	}

	public int getSoToa() {
		return soToa;
	}

	public int getSoGhe() {
		return soGhe;
	}

	public LocalDateTime getThoiDiemHetHan() {
		return thoiDiemHetHan;
	}

	public KhachHang getHanhKhach() {
		return hanhKhach;
	}

	public void setHanhKhach(KhachHang hanhKhach) {
		this.hanhKhach = hanhKhach;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof VeSession)) {
			return false;
		}
		VeSession that = (VeSession) o;
		return Objects.equals(chuyenID, that.chuyenID) && Objects.equals(tenGaDi, that.tenGaDi)
				&& Objects.equals(tenGaDen, that.tenGaDen) && Objects.equals(soToa, that.soToa)
				&& Objects.equals(soGhe, that.soGhe);
	}

	@Override
	public int hashCode() {
		return Objects.hash(chuyenID, tenGaDi, tenGaDen, soToa, soGhe);
	}

	@Override
	public String toString() {
		return tenTau + ";" + tenGaDi + ";" + tenGaDen + ";" + ngayDi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				+ ";" + gioDi.format(DateTimeFormatter.ofPattern("hh:mm")) + ";" + toaID + ";" + soToa + ";" + soGhe;
	}

	public boolean isHoldExpired() {
		if (thoiDiemHetHan == null) {
			return false;
		}
		return LocalDateTime.now().isAfter(thoiDiemHetHan);
	}

	public String prettyString() {
		DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
		return String.format("<html><b>%s</b> %s - %s<br/>%s %s<br/>%s Toa %s Chỗ %s<br/>Giá: <b>%s</b></html>",
				getTenTau(), getTenGaDi(), getTenGaDen(), getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				getGioDi().format(DateTimeFormatter.ofPattern("HH:mm")), getHangToa(), getSoToa(), getSoGhe(),
				formatter.format(getGia()));
	}

	public PhieuGiuChoChiTiet getPgcct() {
		return pgcct;
	}

	public void setPgcct(PhieuGiuChoChiTiet pgcct) {
		this.pgcct = pgcct;
	}

	public Ve getVe() {
		return ve;
	}

	public void setVe(Ve ve) {
		this.ve = ve;
	}

	public boolean isHanhKhachMoi() {
		return isHanhKhachMoi;
	}

	public void setHanhKhachMoi(boolean isHanhKhachMoi) {
		this.isHanhKhachMoi = isHanhKhachMoi;
	}

	public void setKhuyenMaiCode(String khuyenMaiCode) {
		this.khuyenMaiCode = khuyenMaiCode;
	}

	public void setGiam(int giam) {
		this.giam = giam;
	}

	public int getGiamDoiTuong() {
		return giamDoiTuong;
	}

	public void setGiamDoiTuong(int giamDoiTuong) {
		this.giamDoiTuong = giamDoiTuong;
	}

	public int getPhongChoVIP() {
		return phongChoVIP;
	}

	public void setPhongChoVIP(int phongChoVIP) {
		this.phongChoVIP = phongChoVIP;
	}
}