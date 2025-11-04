package entity;

import java.time.LocalDateTime;
import java.util.Objects;

import entity.type.TrangThaiVe;

/*
 * @(#) Ve.java  1.0  [11:27:32 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class Ve {
	private String veID;
	private KhachHang khachHang;
	private DonDatCho donDatCho;
	private Chuyen chuyen;
	private Ghe ghe;
	private Ga gaDi;
	private Ga gaDen;
	private LocalDateTime ngayGioDi;
	private double gia;
	private TrangThaiVe trangThai;

	public Ve(String veID, KhachHang khachHang, DonDatCho donDatCho, Chuyen chuyen, Ghe ghe, Ga gaDi, Ga gaDen,
			LocalDateTime ngayGioDi, double gia, TrangThaiVe trangThai) {
		super();
		this.veID = veID;
		this.khachHang = khachHang;
		this.donDatCho = donDatCho;
		this.chuyen = chuyen;
		this.ghe = ghe;
		this.gaDi = gaDi;
		this.gaDen = gaDen;
		this.ngayGioDi = ngayGioDi;
		this.gia = gia;
		this.trangThai = trangThai;
	}

	public Ve(String veID) {
		super();
		this.veID = veID;
	}

	public Ve() {
		super();
	}

	public String getVeID() {
		return veID;
	}

	public void setVeID(String veID) {
		if (veID != null && !veID.trim().isEmpty()) {
			this.veID = veID;
		} else {
			throw new IllegalArgumentException("VeID không được rỗng!");
		}
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		if (khachHang != null) {
			this.khachHang = khachHang;
		} else {
			throw new IllegalArgumentException("Hành khách không được rỗng!");
		}
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		if (donDatCho != null) {
			this.donDatCho = donDatCho;
		} else {
			throw new IllegalArgumentException("Đơn đặt chỗ không được rỗng!");
		}
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public void setChuyen(Chuyen chuyen) {
		if (chuyen != null) {
			this.chuyen = chuyen;
		} else {
			throw new IllegalArgumentException("Chuyến không được rỗng!");
		}
	}

	public Ghe getGhe() {
		return ghe;
	}

	public void setGhe(Ghe ghe) {
		if (ghe != null) {
			this.ghe = ghe;
		} else {
			throw new IllegalArgumentException("Ghế không được rỗng!");
		}
		this.ghe = ghe;
	}

	public Ga getGaDi() {
		return gaDi;
	}

	public void setGaDi(Ga gaDi) {
		if (gaDi != null) {
			this.gaDi = gaDi;
		} else {
			throw new IllegalArgumentException("Ga đi không được rỗng!");
		}
	}

	public Ga getGaDen() {
		return gaDen;
	}

	public void setGaDen(Ga gaDen) {
		if (gaDen != null) {
			this.gaDen = gaDen;
		} else {
			throw new IllegalArgumentException("Ga đến không được rỗng!");
		}
	}

	public double getGia() {
		return gia;
	}

	public void setGia(double gia) {
		this.gia = gia;
	}

	public LocalDateTime getNgayGioDi() {
		return ngayGioDi;
	}

	public void setNgayGioDi(LocalDateTime ngayGioDi) {
		this.ngayGioDi = ngayGioDi;
	}

	public TrangThaiVe getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThaiVe trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return veID + ";" + donDatCho + ";" + chuyen + ";" + ghe + ";" + khachHang + ";" + gia + ";" + ngayGioDi + ";"
				+ trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Ve ve = (Ve) o;
		return Objects.equals(getVeID(), ve.getVeID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getVeID());
	}
}