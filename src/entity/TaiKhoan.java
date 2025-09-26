package entity;
/*
 * @(#) Account.java  1.0  [9:33:42 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

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
		if( taiKhoanID != null && !taiKhoanID.trim().isEmpty()){
			this.taiKhoanID = taiKhoanID;
		}else{
			throw new IllegalArgumentException("Tài khoản ID không được để trống!");
		}
	}

	public void setVaiTroTaiKhoan(VaiTroTaiKhoan vaiTroTaiKhoan) {
		if(vaiTroTaiKhoan != null) {
			this.vaiTroTaiKhoan = vaiTroTaiKhoan;
		}else{
			throw new IllegalArgumentException("Vai trò tài khoản không được để trống!");
		}
	}

	public void setNhanVien(NhanVien nhanVien) {
		if(nhanVien != null) {
			this.nhanVien = nhanVien;
		}else{
			throw new IllegalArgumentException("Nhân viên không được để trống!");
		}
	}

	public void setTenDangNhap(String tenDangNhap) {
		if(tenDangNhap != null && !tenDangNhap.trim().isEmpty()) {
			this.tenDangNhap = tenDangNhap;
		}else{
			throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
		}
	}

	public void setMatKhauHash(String matKhauHash) {
		if(matKhauHash != null && !matKhauHash.trim().isEmpty()) {
			this.matKhauHash = matKhauHash;
		}else{
			throw new IllegalArgumentException("Mật khẩu không được để trống!");
		}
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TaiKhoan taiKhoan = (TaiKhoan) o;
		return Objects.equals(getTaiKhoanID(), taiKhoan.getTaiKhoanID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getTaiKhoanID());
	}
}