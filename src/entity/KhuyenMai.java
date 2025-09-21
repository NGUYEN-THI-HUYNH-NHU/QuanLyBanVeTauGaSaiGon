package entity;
/*
 * @(#) KhuyenMai.java  1.0  [12:36:16 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class KhuyenMai implements DichVu {
	private String maKhuyenMai;
	private String code;
	private String moTa;
	private boolean loaiGiamGia;
	private double giaGiam;
	private LocalDate ngayApDung;
	private LocalDate ngayHetHan;
	private double tienGiamToiDaChoPhep;
	private String dichVuApDung;
	private String loaiToaApDung;
	private String loaiTauApDung;
	private HanhKhach doiTuongApDung;
	
	@Override
	public String getMaDichVu() {
		return maKhuyenMai;
	}
	@Override
	public double getGia() {
		return giaGiam;
	}
}
