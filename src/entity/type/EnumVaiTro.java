package entity.type;
/*
 * @(#) VaiTro.java  1.0  [9:48:57 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public enum EnumVaiTro {
    NHAN_VIEN_BAN_VE("Nhân viên quầy vé"),
    QUAN_LY_NHA_GA("Quản lý nhà ga");

    private final String description;

    EnumVaiTro(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
