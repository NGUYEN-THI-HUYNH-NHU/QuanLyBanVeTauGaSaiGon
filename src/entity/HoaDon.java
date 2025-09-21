package entity;
/*
 * @(#) HoaDon.java  1.0  [12:36:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class HoaDon {
	private String maHoaDon;
	private DonDatCho donDatCho;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private LocalDateTime ngayLap;
	private double tamTinh;
	private double tongGiamGia;
	private double tongThue;
	private double tongTien;
	private boolean isDaThanhToan;
}