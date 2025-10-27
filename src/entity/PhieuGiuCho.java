package entity;
/*
 * @(#) PhieuGiuCho_DAO.java  1.0  [2:50:00 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuGiuCho {
	private String phieuGiuChoID;
	private NhanVien nhanVien;
	private LocalDateTime thoiDiemTao;
	private String trangThai;

	public PhieuGiuCho(String phieuGiuChoID, NhanVien nhanVien, LocalDateTime thoiDiemTao, String trangThai) {
		this.phieuGiuChoID = phieuGiuChoID;
		this.nhanVien = nhanVien;
		this.thoiDiemTao = thoiDiemTao;
		this.trangThai = trangThai;
	}

	public PhieuGiuCho() {
		super();
	}

	public PhieuGiuCho(String phieuGiuChoID) {
		this.phieuGiuChoID = phieuGiuChoID;
	}

	public String getPhieuGiuChoID() {
		return phieuGiuChoID;
	}

	public void setPhieuGiuChoID(String phieuGiuChoID) {
		if (phieuGiuChoID == null || phieuGiuChoID.isEmpty()) {
			throw new IllegalArgumentException("PhieuGiuChoID không được để trống!");
		}
		this.phieuGiuChoID = phieuGiuChoID;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		if (nhanVien == null) {
			throw new IllegalArgumentException("NhanVien không được để trống!");
		}
		this.nhanVien = nhanVien;
	}

	public LocalDateTime getThoiDiemTao() {
		return thoiDiemTao;
	}

	public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
		if (thoiDiemTao == null) {
			throw new IllegalArgumentException("ThoiDiemTao không được để trống!");
		}
		this.thoiDiemTao = thoiDiemTao;
	}

	public String getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(String trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public String toString() {
		return phieuGiuChoID + ";" + nhanVien + ";" + thoiDiemTao + ";" + trangThai;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PhieuGiuCho that = (PhieuGiuCho) o;
		return Objects.equals(getPhieuGiuChoID(), that.getPhieuGiuChoID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPhieuGiuChoID());
	}
}
