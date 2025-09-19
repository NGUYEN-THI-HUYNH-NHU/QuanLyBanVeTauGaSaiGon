package entity;
/*
 * @(#) DichVuAnUong.java  1.0  [12:57:18 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public class DichVuAnUong implements DichVu {
	private String maDichVuAnUong;
	private String tenDichVuAnUong;
	private boolean isDichVuAn;
	private double giaDichVuAnUong;
	
	
	@Override
	public String getMaDichVu() {
		return maDichVuAnUong;
	}
	@Override
	public double getGia() {
		return giaDichVuAnUong;
	}
}
