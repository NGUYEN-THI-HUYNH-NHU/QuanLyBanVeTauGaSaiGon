package entity.type;
/*
 * @(#) EnumLoaiTau.java  1.0  [10:01:44 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public enum EnumLoaiTau {
	TAU_NHANH("Tàu nhanh"),
    TAU_CHAM("Tàu chậm");

    private final String description;

    EnumLoaiTau(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
