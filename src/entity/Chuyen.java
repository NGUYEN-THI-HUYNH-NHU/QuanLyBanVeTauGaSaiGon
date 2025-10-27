package entity;
/*
 * @(#) Chuyen.java  1.0  [10:09:55 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Chuyen {
	private String chuyenID;
	private Tuyen tuyen;
	private Tau tau;
	private LocalDate ngayDi;
	private LocalTime gioDi;
	private LocalDate ngayDen;
	private LocalTime gioDen;
	private int soChoDat;
	private int soChoTrong;

	public Chuyen(String chuyenID, Tuyen tuyen, Tau tau, LocalDate ngayDi, LocalTime gioDi, LocalDate ngayDen,
			LocalTime gioDen, int soChoDat, int soChoTrong) {
		super();
		this.chuyenID = chuyenID;
		this.tuyen = tuyen;
		this.tau = tau;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
		this.ngayDen = ngayDen;
		this.gioDen = gioDen;
		this.soChoDat = soChoDat;
		this.soChoTrong = soChoTrong;
	}

	public Chuyen(String chuyenID, Tau tau, LocalDate ngayDi, LocalTime gioDi, LocalDate ngayDen, LocalTime gioDen) {
		super();
		this.chuyenID = chuyenID;
		this.tau = tau;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
		this.ngayDen = ngayDen;
		this.gioDen = gioDen;
	}

	public Chuyen(String chuyenID, Tau tau, LocalDate ngayDi, LocalTime gioDi) {
		super();
		this.chuyenID = chuyenID;
		this.tau = tau;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
	}

	public Chuyen(String chuyenID) {
		this.chuyenID = chuyenID;
	}

	public String getChuyenID() {
		return chuyenID;
	}

	public void setChuyenID(String chuyenID) {
		if (chuyenID != null && !chuyenID.trim().isEmpty()) {
			this.chuyenID = chuyenID;
		} else {
			throw new IllegalArgumentException("ChuyenID không được rỗng");
		}
	}

	public Tuyen getTuyen() {
		return tuyen;
	}

	public void setTuyen(Tuyen tuyen) {
		if (tuyen != null) {
			this.tuyen = tuyen;
		} else {
			throw new IllegalArgumentException("Tuyến không được rỗng");
		}
	}

	public Tau getTau() {
		return tau;
	}

	public void setTau(Tau tau) {
		if (tau != null) {
			this.tau = tau;
		} else {
			throw new IllegalArgumentException("Tàu không được rỗng");
		}
	}

	public LocalDate getNgayDi() {
		return ngayDi;
	}

	public void setNgayDi(LocalDate ngayDi) {
		if (ngayDi == null || ngayDi.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Ngày đi không được rỗng và phải là ngày trong tương lai");
		}
		this.ngayDi = ngayDi;
	}

	public LocalTime getGioDi() {
		return gioDi;
	}

	public String getGioDiString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return gioDi.format(formatter);
	}

	public void setGioDi(LocalTime gioDi) {
		if (gioDi == null || gioDi.isBefore(LocalTime.of(0, 0)) || gioDi.isAfter(LocalTime.of(23, 59))) {
			throw new IllegalArgumentException("Giờ đi phải >= 0 và <= 23");
		}
		this.gioDi = gioDi;
	}

	public LocalDate getNgayDen() {
		return ngayDen;
	}

	public void setNgayDen(LocalDate ngayDen) {
		if (ngayDen == null || ngayDen.isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Ngày đi không được rỗng và phải là ngày trong tương lai");
		}
		this.ngayDen = ngayDen;
	}

	public LocalTime getGioDen() {
		return gioDen;
	}

	public String getGioDenString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		return gioDen.format(formatter);
	}

	public void setGioDen(LocalTime gioDen) {
		if (gioDen == null || gioDen.isBefore(LocalTime.of(0, 0)) || gioDen.isAfter(LocalTime.of(23, 59))) {
			throw new IllegalArgumentException("Giờ đi phải >= 0 và <= 23");
		}
		this.gioDen = gioDen;
	}

	public int getSoChoDat() {
		return soChoDat;
	}

	public int getSoChoTrong() {
		return soChoTrong;
	}

	public void setSoChoDat(int soChoDat) {
		this.soChoDat = soChoDat;
	}

	public void setSoChoTrong(int soChoTrong) {
		this.soChoTrong = soChoTrong;
	}

	@Override
	public String toString() {
		return chuyenID + ";" + tuyen + ";" + tau + ";" + ngayDi + ";" + gioDi + ngayDen + ";" + gioDen;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Chuyen chuyen = (Chuyen) o;
		return Objects.equals(chuyenID, chuyen.chuyenID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(chuyenID);
	}
}