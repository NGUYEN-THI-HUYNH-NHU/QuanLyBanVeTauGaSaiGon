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

public enum LoaiKhachHang {
	HANH_KHACH("Hành khách"),
    KHACH_HANG("Khách hàng"),
	HANH_KHACH_KHACH_HANG("Hành khách + Khách hàng");
	
    private final String description;

    LoaiKhachHang(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
