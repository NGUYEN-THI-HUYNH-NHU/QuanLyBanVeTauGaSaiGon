package entity;
/*
 * @(#) DieuKienKhuyenMai.java  1.0  [3:36:18 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.type.HangKhachHang;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class DieuKienKhuyenMai {
	private String dieuKienID;
	private KhuyenMai khuyenMai;
	private Tuyen tuyen;
	private LoaiTau loaiTau;
	private HangToa hangToa;
	private HangKhachHang hangKhachHang;
	private LoaiDoiTuong loaiDoiTuong;
	private int ngayTrongTuan;
	private boolean ngayLe;
	private double minGiaTriDonHang;
	private double maxGiaTriDonHang;
}
