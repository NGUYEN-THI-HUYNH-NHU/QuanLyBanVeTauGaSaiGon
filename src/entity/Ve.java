package entity;
/*
 * @(#) Ve.java  1.0  [11:27:32 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class Ve implements DichVu {
	private String maVe;
	private DonDatCho donDatCho;
	private Chuyen chuyen;
	private Cho cho;
	private String loaiVe;
	private double giaVe;
	private HanhKhach hanhKhach;
	private HoaDon hoaDon;
	private String trangThaiVe;
	
	@Override
	public String getMaDichVu() {
		return maVe;
	}
	@Override
	public double getGia() {
		return giaVe;
	}
}