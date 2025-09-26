package entity;


/*
 * @(#) Cho.java  1.0  [11:21:20 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import java.util.Objects;

public class Ghe {
	private String gheID;
	private Toa toa;
	private int soGhe;
	private boolean trangThai;
	
	public Ghe(String gheID, Toa toa, int soGhe, boolean trangThai) {
		super();
		this.gheID = gheID;
		this.toa = toa;
		this.soGhe = soGhe;
		this.trangThai = trangThai;
	}

	public String getGheID() {
		return gheID;
	}

	public Toa getToa() {
		return toa;
	}

	public int getSoGhe() {
		return soGhe;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setGheID(String gheID) {
		if(gheID != null && !gheID.isEmpty()) {
			this.gheID = gheID;
		}else{
			throw new IllegalArgumentException("Ghe ID không được để trống!");
		}
	}

	public void setToa(Toa toa) {
		this.toa = toa;
	}

	public void setSoGhe(int soGhe) {
		this.soGhe = soGhe;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return gheID + ";" + toa + ";" + soGhe + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ghe ghe = (Ghe) o;
		return Objects.equals(gheID, ghe.gheID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(gheID);
	}
}