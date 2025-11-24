package entity.type;
/*
 * @(#) LoaiKeywordSearchHoaDon.java  1.0  [7:42:09 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */

public enum LoaiKeywordSearchHoaDon {
	HOADONID("Mã hóa đơn"), KHACHHANGID("Mã khách hàng"), HET_HAN("Hết hạn"), DA_HUY("Đã hủy");

	private final String description;

	LoaiKeywordSearchHoaDon(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
