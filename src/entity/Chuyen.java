package entity;
/*
 * @(#) Chuyen.java  1.0  [10:09:55 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
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
	private LocalDateTime gioKhoiHanh;
	private LocalDateTime gioDen;
	
	public Chuyen(String chuyenID, Tuyen tuyen, Tau tau, LocalDateTime gioKhoiHanh, LocalDateTime gioDen) {
		super();
		this.chuyenID = chuyenID;
		this.tuyen = tuyen;
		this.tau = tau;
		this.gioKhoiHanh = gioKhoiHanh;
		this.gioDen = gioDen;
	}
	
	public Chuyen(String chuyenID) {
		this.chuyenID = chuyenID;
	}
	
	public Chuyen() {
		super();
	}

	public String getChuyenID() {
		return chuyenID;
	}

	public Tuyen getTuyen() {
		return tuyen;
	}

	public Tau getTau() {
		return tau;
	}

	public LocalDateTime getNgayGioKhoiHanh() {
		return gioKhoiHanh;
	}

	public LocalDateTime getNgayGioDen() {
		return gioDen;
	}

	public void setChuyenID(String chuyenID) {
		if(chuyenID != null && !chuyenID.trim().isEmpty()){
			this.chuyenID = chuyenID;
		}else{
			throw new IllegalArgumentException("Chuyến ID không được để trống!");
		}

		this.chuyenID = chuyenID;
	}

	public void setTuyen(Tuyen tuyen) {
		this.tuyen = tuyen;
	}

	public void setTau(Tau tau) {
		this.tau = tau;
	}

	public void setNgayGioKhoiHanh(LocalDateTime gioKhoiHanh) {
		if(gioKhoiHanh.isAfter(gioDen)) {
			throw new IllegalArgumentException("Ngày giờ khởi hành phải trước ngày giờ đến!");
		}
		this.gioKhoiHanh = gioKhoiHanh;
	}

	public void setNgayGioDen(LocalDateTime gioDen) {
		if(gioDen.isBefore(gioKhoiHanh)) {
			throw new IllegalArgumentException("Ngày giờ đến phải sau ngày giờ khởi hành!");
		}
		this.gioDen = gioDen;
	}

	@Override
	public String toString() {
		return chuyenID + ";" + tuyen + ";" + tau + ";"
				+ gioKhoiHanh + ";" + gioDen;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chuyen chuyen = (Chuyen) o;
		return Objects.equals(chuyenID, chuyen.chuyenID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(chuyenID);
	}
}