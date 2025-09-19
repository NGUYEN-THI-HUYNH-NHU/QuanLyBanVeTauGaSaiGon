package entity;
/*
 * @(#) HoaDon.java  1.0  [12:36:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.List;

import entity.type.EnumLoaiDichVu;
import entity.type.EnumPhuongThucThanhToan;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class HoaDon {
	private String maHoaDon;
	private EnumLoaiDichVu loaiDichVu;
	private NhanVien nhanVien;
	private NguoiDatVe khachHang;
	private List<Ve> dsVe;
	private LocalDateTime ngayTao;
	private EnumPhuongThucThanhToan phuongThucThanhToan;
}