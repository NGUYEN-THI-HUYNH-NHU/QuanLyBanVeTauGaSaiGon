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
	VE_CHUA_SU_DUNG("Vé chưa sử dụng"),
	VE_DA_SU_DUNG("Vé đã sử dụng"),
	VE_HET_HAN("Vé hết hạn"),
	VE_DA_HOAN("Vé đã hoàn"),
	VE_DA_DOI("Vé đã hoàn");

	private final String description;

	TrangThaiVe(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
