package entity.type;
/*
 * @(#) EnumLoaiVe.java  1.0  [12:30:54 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumLoaiVe {
	VE_MOT_CHIEU("Vé một chiều"),
    VE_KHU_HOI("Vé khứ hồi");

    private final String description;

	EnumLoaiVe(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
