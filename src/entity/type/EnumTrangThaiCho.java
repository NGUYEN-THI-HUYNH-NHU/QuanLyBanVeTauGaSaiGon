package entity.type;
/*
 * @(#) EnumTrangThaiCho.java  1.0  [1:09:53 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumTrangThaiCho {
	CHO_TRONG("Chỗ trống"),
    CHO_DA_BAN("Chỗ đã bán");

    private final String description;

    EnumTrangThaiCho(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
