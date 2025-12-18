package entity.type;
/*
 * @(#) TrangThaiVe.java  1.0  [6:14:34 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public enum TrangThaiVe {
	DA_BAN("Đã bán"), DA_DUNG("Đã sử dụng"), HET_HAN("Hết hạn"), DA_HOAN("Đã hoàn"), DA_DOI("Đã đổi");

	private final String description;

	TrangThaiVe(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
