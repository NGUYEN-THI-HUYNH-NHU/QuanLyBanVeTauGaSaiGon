package entity;
/*
 * @(#) PhieuHoanVe.java  1.0  [1:59:59 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class DonHoanDoiVe implements DichVu {
	private String maDonHoanDoiVe;
	private DonDatCho donDatCho;
	private KhachHang khachHang;
	private NhanVien nhanVien;
	private String loaiHoanDoiVe;
	private LocalDate ngayYeuCau;
	private double tongTienHoan;
	private boolean trangThai;
	
	@Override
	public String getMaDichVu() {
		return maDonHoanDoiVe;
	}
	
	@Override
	public double getGia() {
		return tongTienHoan;
	}
}
