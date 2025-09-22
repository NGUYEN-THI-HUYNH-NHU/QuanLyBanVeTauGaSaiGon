package entity;
/*
 * @(#) ThanhToan.java  1.0  [12:54:12 PM] Sep 18, 2025
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

public class ThanhToan {
	private String thanhToanID;
	private HoaDon hoaDon;
	private KhachHang khachHang;
	private double soTien;
	private LocalDateTime ngayGioThanhToan;
	private boolean isThanhToanTienMat;
	private boolean trangThai;
}
