package entity.type;
/*
 * @(#) TrangThaiSDKM.java  1.0  [4:58:41 PM] Dec 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 2, 2025
 * @version: 1.0
 */

public enum TrangThaiSDKM {
	DA_AP_DUNG("Khuyến mãi áp dụng vào vé tàu"), DA_HUY("Hủy sử dụng khuyến mãi do hoàn/đổi vé");

	private final String description;

	TrangThaiSDKM(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
