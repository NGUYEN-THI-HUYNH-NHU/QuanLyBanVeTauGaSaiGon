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
	DA_BAN("Vé đã bán"), DA_DUNG("Vé đã sử dụng"), HET_HAN("Vé hết hạn"), DA_HOAN("Vé đã hoàn"), DA_DOI("Vé đã đổi");

	private final String description;

	TrangThaiVe(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
