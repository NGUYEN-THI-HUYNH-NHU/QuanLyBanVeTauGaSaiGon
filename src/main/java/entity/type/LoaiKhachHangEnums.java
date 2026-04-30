package entity.type;
/*
 * @(#) LoaiKhachHang.java  1.0  [2:11:14 PM] Oct 10, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 10, 2025
 * @version: 1.0
 */

public enum LoaiKhachHangEnums {
    HANH_KHACH("Hành khách"),
    KHACH_HANG("Khách hàng"),
    HANH_KHACH_KHACH_HANG("Hành khách + Khách hàng");

    private final String description;

    LoaiKhachHangEnums(String description) {
        this.description = description;
    }

    public static LoaiKhachHangEnums fromDescription(String desc) {
        if (desc == null) return null;
        for (LoaiKhachHangEnums x : values()) {
            if (x.getDescription().equalsIgnoreCase(desc.trim())) {
                return x;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
}
