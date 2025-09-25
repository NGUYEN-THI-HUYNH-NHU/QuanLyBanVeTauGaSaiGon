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

import java.util.Objects;

public class Tuyen {
	private String tuyenID;
	private Ga gaDi;
	private Ga gaDen;
	private int khoangCachKm;
	private int thoiGianDuKienPhut;

	public Tuyen() {
		super();
	}

	public Tuyen(String tuyenID, Ga gaDi, Ga gaDen, int khoangCachKm, int thoiGianDuKienPhut) {
		this.tuyenID = tuyenID;
		this.gaDi = gaDi;
		this.gaDen = gaDen;
		this.khoangCachKm = khoangCachKm;
		this.thoiGianDuKienPhut = thoiGianDuKienPhut;
	}

	public String getTuyenID() {
		return tuyenID;
	}

	public void setTuyenID(String tuyenID) {
		if(tuyenID == null || tuyenID.isEmpty()) {
			throw new IllegalArgumentException("TuyenID khong duoc de trong");
		}
		this.tuyenID = tuyenID;
	}

	public Ga getGaDi() {
		return gaDi;
	}

	public void setGaDi(Ga gaDi) {
		if (gaDi == null) {
			throw new IllegalArgumentException("Ga di khong duoc de trong");
		}
		this.gaDi = gaDi;
	}

	public Ga getGaDen() {
		return gaDen;
	}

	public void setGaDen(Ga gaDen) {
		if (gaDen == null) {
			throw new IllegalArgumentException("Ga den khong duoc de trong");
		}
		this.gaDen = gaDen;
	}

	public int getKhoangCachKm() {
		return khoangCachKm;
	}

	public void setKhoangCachKm(int khoangCachKm) {
		if (khoangCachKm <= 0) {
			throw new IllegalArgumentException("Khoang cach phai lon hon 0");
		}
		this.khoangCachKm = khoangCachKm;
	}

	public int getThoiGianDuKienPhut() {
		return thoiGianDuKienPhut;
	}

	public void setThoiGianDuKienPhut(int thoiGianDuKienPhut) {
		if (thoiGianDuKienPhut <= 0) {
			throw new IllegalArgumentException("Thoi gian du kien phai lon hon 0");
		}
		this.thoiGianDuKienPhut = thoiGianDuKienPhut;
	}

	@Override
	public String toString() {
		return tuyenID + ";"
				+ gaDi + ";"
				+ gaDen + ";"
				+ khoangCachKm + ";"
				+ thoiGianDuKienPhut;
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
