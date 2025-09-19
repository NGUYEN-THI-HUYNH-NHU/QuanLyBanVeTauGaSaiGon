package entity;
/*
 * @(#) Ve.java  1.0  [11:27:32 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

import entity.type.EnumLoaiVe;
import entity.type.EnumTrangThaiVe;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class Ve {
	private String maVe;
	private EnumLoaiVe loaiVe;
	private double gia;
	private HanhKhach hanhKhach;
	private EnumTrangThaiVe trangThaiVe;
	private LocalDateTime ngayBan;
	private KhuyenMai khuyenMai;
}