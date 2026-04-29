package entity;/*
				* @ (#) PhieuGiuChoChiTiet.java   1.0     02/10/2025
				package entity;
				
				
				/**
				* @description :
				* @author : Vy, Pham Kha Vy
				* @version 1.0
				* @created : 02/10/2025
				*/

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuGiuChoChiTiet {
	private String phieuGiuChoChiTietID;
	private PhieuGiuCho phieuGiuCho;
	private Chuyen chuyen;
	private Ghe ghe;
	private Ga gaDi;
	private Ga gaDen;
	private LocalDateTime thoiDiemGiuCho;
	private String trangThai;

	public PhieuGiuChoChiTiet(String phieuGiuChoChiTietID, PhieuGiuCho phieuGiuCho, Chuyen chuyen, Ghe ghe, Ga gaDi,
			Ga gaDen, LocalDateTime thoiDiemGiuCho, String trangThai) {
		this.phieuGiuChoChiTietID = phieuGiuChoChiTietID;
		this.phieuGiuCho = phieuGiuCho;
		this.chuyen = chuyen;
		this.ghe = ghe;
		this.gaDi = gaDi;
		this.gaDen = gaDen;
		this.thoiDiemGiuCho = thoiDiemGiuCho;
		this.trangThai = trangThai;
	}

	public PhieuGiuChoChiTiet() {
		super();
	}

	public String getPhieuGiuChoChiTietID() {
		return phieuGiuChoChiTietID;
	}

	public void setPhieuGiuChoChiTietID(String phieuGiuChoChiTietID) {
		this.phieuGiuChoChiTietID = phieuGiuChoChiTietID;
	}

	public PhieuGiuCho getPhieuGiuCho() {
		return phieuGiuCho;
	}

	public void setPhieuGiuCho(PhieuGiuCho phieuGiuCho) {
		if (phieuGiuCho == null) {
			throw new IllegalArgumentException("PhieuGiuCho không được để trống!");
		}
		this.phieuGiuCho = phieuGiuCho;
	}

	public Chuyen getChuyen() {
		return chuyen;
	}

	public void setChuyen(Chuyen chuyen) {
		if (chuyen == null) {
			throw new IllegalArgumentException("Chuyen không được để trống!");
		}
		this.chuyen = chuyen;
	}

	public Ghe getGhe() {
		return ghe;
	}

	public void setGhe(Ghe ghe) {
		if (ghe == null) {
			throw new IllegalArgumentException("Ghe không được để trống!");
		}
		this.ghe = ghe;
	}

	public Ga getGaDi() {
		return gaDi;
	}

	public void setGaDi(Ga gaDi) {
		this.gaDi = gaDi;
	}

	public Ga getGaDen() {
		return gaDen;
	}

	public void setGaDen(Ga gaDen) {
		this.gaDen = gaDen;
	}

	public LocalDateTime getThoiDiemHetGiuCho() {
		return thoiDiemGiuCho;
	}

	public void setThoiDiemGiuCho(LocalDateTime thoiDiemGiuCho) {
		if (thoiDiemGiuCho == null) {
			throw new IllegalArgumentException("ThoiDiemHetGiuCho không được để trống!");
		}
		this.thoiDiemGiuCho = thoiDiemGiuCho;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return phieuGiuChoChiTietID + ";" + phieuGiuCho.getPhieuGiuChoID() + ";" + chuyen.getChuyenID() + ";"
				+ ghe.getGheID() + ";" + gaDi.getGaID() + ";" + gaDen.getGaID() + ";" + thoiDiemGiuCho + ";"
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
		PhieuGiuChoChiTiet that = (PhieuGiuChoChiTiet) o;
		return Objects.equals(getPhieuGiuChoChiTietID(), that.getPhieuGiuChoChiTietID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPhieuGiuChoChiTietID());
	}
}
