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
	private HanhKhach hanhKhach;
	private DonDatCho donDatCho;
	private Chuyen chuyen;
	private Ghe ghe;
	private Ga gaDiID;
	private Ga gaDenID;
	private int thuTuGaDi;
	private int thuTuGaDen;
	private double gia;
	private LocalDateTime thoiDiemBan;
	private TrangThaiVe trangThai;
	
	public Ve(String veID, DonDatCho donDatCho, Chuyen chuyen, Ghe ghe, HanhKhach hanhKhach, int thuTuGaDi,
			int thuTuGaDen, double gia, TrangThaiVe trangThai, LocalDateTime thoiDiemBan) {
		super();
		this.veID = veID;
		this.donDatCho = donDatCho;
		this.chuyen = chuyen;
		this.ghe = ghe;
		this.hanhKhach = hanhKhach;
		this.thuTuGaDi = thuTuGaDi;
		this.thuTuGaDen = thuTuGaDen;
		this.gia = gia;
		this.trangThai = trangThai;
		this.thoiDiemBan = thoiDiemBan;
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
		if(veID != null && !veID.trim().isEmpty()) {
			this.veID = veID;
		} else {
			throw new IllegalArgumentException("VeID không được rỗng!");
		}
	}

	public HanhKhach getHanhKhach() {
		return hanhKhach;
	}

	public void setHanhKhach(HanhKhach hanhKhach) {
		if(hanhKhach != null) {
			this.hanhKhach = hanhKhach;
		} else {
			throw new IllegalArgumentException("Hành khách không được rỗng!");
		}
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		if(donDatCho != null) {
			this.donDatCho = donDatCho;
		} else {
			throw new IllegalArgumentException("Đơn đặt chỗ không được rỗng!");
		}
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public void setChuyen(Chuyen chuyen) {
		if(chuyen != null) {
			this.chuyen = chuyen;
		} else {
			throw new IllegalArgumentException("Chuyến không được rỗng!");
		}
	}

	public Ghe getGhe() {
		return ghe;
	}

	public void setGhe(Ghe ghe) {
		if(ghe != null) {
			this.ghe = ghe;
		} else {
			throw new IllegalArgumentException("Ghế không được rỗng!");
		}
		this.ghe = ghe;
	}

	public Ga getGaDiID() {
		return gaDiID;
	}

	public void setGaDiID(Ga gaDiID) {
		if(gaDiID != null) {
			this.gaDiID = gaDiID;
		} else {
			throw new IllegalArgumentException("Ga đi không được rỗng!");
		}
	}

	public Ga getGaDenID() {
		return gaDenID;
	}

	public void setGaDenID(Ga gaDenID) {
		if(gaDenID != null) {
			this.gaDenID = gaDenID;
		} else {
			throw new IllegalArgumentException("Ga đến không được rỗng!");
		}
	}

	public int getThuTuGaDi() {
		return thuTuGaDi;
	}

	public void setThuTuGaDi(int thuTuGaDi) {
		if(thuTuGaDi >= 0) {
			this.thuTuGaDi = thuTuGaDi;
		} else {
			throw new IllegalArgumentException("Thứ tự ga đi không được âm!");
		}
	}

	public int getThuTuGaDen() {
		return thuTuGaDen;
	}

	public void setThuTuGaDen(int thuTuGaDen) {
		if(thuTuGaDen >= 0) {
			this.thuTuGaDen = thuTuGaDen;
		} else {
			throw new IllegalArgumentException("Thứ tự ga đến không được âm!");
		}
	}

	public double getGia() {
		return gia;
	}

	public void setGia(double gia) {
		this.gia = gia;
	}

	public LocalDateTime getThoiDiemBan() {
		return thoiDiemBan;
	}

	public void setThoiDiemBan(LocalDateTime thoiDiemBan) {
		this.thoiDiemBan = thoiDiemBan;
	}

	public TrangThaiVe getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThaiVe trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return veID + ";" + donDatCho + ";" + chuyen + ";" + ghe + ";" + hanhKhach + ";"
				+ thuTuGaDi + ";" + thuTuGaDen + ";" + gia + ";" + thoiDiemBan + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ve ve = (Ve) o;
		return Objects.equals(getVeID(), ve.getVeID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getVeID());
	}
}