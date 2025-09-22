package entity;
/*
 * @(#) Account.java  1.0  [9:33:42 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

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
	private LocalDate ngayTao;
	private boolean trangThai;
}
