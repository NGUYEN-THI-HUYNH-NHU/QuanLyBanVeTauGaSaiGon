package entity.type;
/*
 * @(#) VaiTroTaiKhoan.java  1.0  [4:58:06 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public enum VaiTroTaiKhoanEnums {
    NHAN_VIEN("Nhân viên quầy vé"),
    QUAN_LY("Quản lý nhà ga");

    private final String description;

    VaiTroTaiKhoanEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}