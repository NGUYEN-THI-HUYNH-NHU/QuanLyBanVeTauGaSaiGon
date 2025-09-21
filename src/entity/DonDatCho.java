package entity;
/*
 * @(#) LanDatCho.java  1.0  [12:44:56 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

import entity.type.TrangThaiDonDatCho;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class DonDatCho {
	private String maDonDatCho;
	private KhachHang khachHang;
	private LocalDateTime ngayGioTao;
	private LocalDateTime ngayGioHetHan;
	private double tongTien;
	private TrangThaiDonDatCho trangThaiDonDatCho;
}