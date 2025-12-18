package entity;
/*
 * @(#) Tuyen.java  1.0  [10:06:00 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

import java.util.List;
import java.util.Objects;

public class Tuyen {
	private String tuyenID;
	private String moTa;
	private boolean trangThai;

	public Tuyen() {
		super();
	}

	public Tuyen(String tuyenID, String moTa) {
		this.tuyenID = tuyenID;
		this.moTa = moTa;
	}

	public Tuyen(String tuyenID, String moTa, boolean trangThai) {
		this.tuyenID = tuyenID;
		this.moTa = moTa;
		this.trangThai = trangThai;
	}

	public Tuyen(String tuyenID) {
		super();
		this.tuyenID = tuyenID;
	}

	public String getTuyenID() {
		return tuyenID;
	}

	public void setTuyenID(String tuyenID) {
		if(tuyenID == null || tuyenID.isEmpty()) {
			throw new IllegalArgumentException("TuyenID không được để trống!");
		}
		this.tuyenID = tuyenID;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return tuyenID + ";"
				+ moTa;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuyen tuyen = (Tuyen) o;
		return Objects.equals(tuyenID, tuyen.tuyenID);

	}

	@Override
	public int hashCode() {
		return Objects.hash(tuyenID);
	}


}
