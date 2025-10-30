package entity.type;
/*
 * @(#) TrangThaiPhieuGiuCho.java  1.0  [12:39:33 PM] Oct 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 30, 2025
 * @version: 1.0
 */

public enum TrangThaiPhieuGiuCho {
	DANG_GIU("Đang giữ chỗ"), HET_HAN("Hết hạn giữ chỗ"), XAC_NHAN("Đã xác nhận");

	private final String description;

	TrangThaiPhieuGiuCho(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
