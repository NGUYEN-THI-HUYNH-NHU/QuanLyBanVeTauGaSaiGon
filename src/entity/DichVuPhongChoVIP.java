package entity;
/*
 * @(#) DichVuPhongChoVIP.java  1.0  [1:51:58 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

public class DichVuPhongChoVIP implements DichVu {
	private String maDichVuPhongChoVIP;
	private String tenDichVuPhongChoVIP;
	private double giaDichVuPhongChoVIP;
	
	@Override
	public String getMaDichVu() {
		return maDichVuPhongChoVIP;
	}
	@Override
	public double getGia() {
		return giaDichVuPhongChoVIP;
	}
}
