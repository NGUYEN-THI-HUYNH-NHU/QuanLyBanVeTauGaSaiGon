package entity.type;
/*
 * @(#) LoaiGiaoDich.java  1.0  [2:55:25 PM] Nov 16, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 16, 2025
 * @version: 1.0
 */

public enum LoaiGiaoDich {
	HOAN_VE("Hoàn vé"), DOI_VE("Đổi vé");

	private final String description;

	LoaiGiaoDich(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
