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
		this.gheID = gheID;
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
	
	
}