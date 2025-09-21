package entity;
/*
 * @(#) NhanVien.java  1.0  [9:52:26 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

import entity.type.VaiTroNhanVien;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class NhanVien {
	private String maNhanVien;
	private VaiTroNhanVien vaiTroNhanVien;
	private String hoTen;
	private String soDienThoai;
	private String email;
	private String diaChi;
	private LocalDate ngayVaoLam;
	private boolean isDangHoatDong;
}
