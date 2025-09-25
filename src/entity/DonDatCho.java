package entity;
/*
 * @(#) LanDatCho.java  1.0  [12:44:56 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

import entity.type.TrangThaiDatCho;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class DonDatCho {
	private String donDatChoID;
	private KhachHang khachHang;
	private Chuyen chuyen;
	private LocalDateTime thoiDiemDatCho;
	private LocalDateTime thoiDiemHetHan;
	private double tongTien;
	private TrangThaiDatCho trangThaiDonDatCho;
}