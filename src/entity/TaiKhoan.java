package entity;
/*
 * @(#) Account.java  1.0  [9:33:42 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

import entity.type.VaiTroTaiKhoan;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class TaiKhoan {
	private String taiKhoanID;
	private VaiTroTaiKhoan vaiTroTaiKhoan;
	private NhanVien nhanVien;
	private String tenDangNhap;
	private String matKhauHash;
	private LocalDateTime thoiDiemTao;
	private boolean isHoatDong;
	
	public TaiKhoan(String taiKhoanID, VaiTroTaiKhoan vaiTroTaiKhoan, NhanVien nhanVien, String tenDangNhap,
			String matKhauHash, LocalDateTime thoiDiemTao, boolean isHoatDong) {
		super();
		this.taiKhoanID = taiKhoanID;
		this.vaiTroTaiKhoan = vaiTroTaiKhoan;
		this.nhanVien = nhanVien;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.thoiDiemTao = thoiDiemTao;
		this.isHoatDong = isHoatDong;
	}
	
	public TaiKhoan(VaiTroTaiKhoan vaiTroTaiKhoan, NhanVien nhanVien, String tenDangNhap,
			String matKhauHash, LocalDateTime thoiDiemTao, boolean isHoatDong) {
		super();
		this.vaiTroTaiKhoan = vaiTroTaiKhoan;
		this.nhanVien = nhanVien;
		this.tenDangNhap = tenDangNhap;
		this.matKhauHash = matKhauHash;
		this.thoiDiemTao = thoiDiemTao;
		this.isHoatDong = isHoatDong;
	}
	
	public TaiKhoan() {
		super();
	}

	public String getTaiKhoanID() {
		return taiKhoanID;
	}

	public VaiTroTaiKhoan getVaiTroTaiKhoan() {
		return vaiTroTaiKhoan;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public String getMatKhauHash() {
		return matKhauHash;
	}

	public LocalDateTime getThoiDiemTao() {
		return thoiDiemTao;
	}

	public boolean isHoatDong() {
		return isHoatDong;
	}

	public void setTaiKhoanID(String taiKhoanID) {
		this.taiKhoanID = taiKhoanID;
	}

	public void setVaiTroTaiKhoan(VaiTroTaiKhoan vaiTroTaiKhoan) {
		this.vaiTroTaiKhoan = vaiTroTaiKhoan;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public void setMatKhauHash(String matKhauHash) {
		this.matKhauHash = matKhauHash;
	}

	public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
		this.thoiDiemTao = thoiDiemTao;
	}

	public void setHoatDong(boolean isHoatDong) {
		this.isHoatDong = isHoatDong;
	}

	@Override
	public String toString() {
		return taiKhoanID + ";" + vaiTroTaiKhoan + ";" + nhanVien
				+ ";" + tenDangNhap + ";" + matKhauHash + ";" + thoiDiemTao
				+ ";" + isHoatDong;
	}
}
