package entity.type;
/*
 * @(#) EnumTrangThaiTau.java  1.0  [10:04:15 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public enum EnumTrangThaiTau {
	HOAT_DONG("Hoạt động"),
    BAO_TRI("Bảo trì"),
    NGUNG_HOAT_DONG("Ngừng hoạt động");

    private final String description;

    EnumTrangThaiTau(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
