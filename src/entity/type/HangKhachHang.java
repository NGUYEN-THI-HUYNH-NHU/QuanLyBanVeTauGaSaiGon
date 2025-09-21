package entity.type;
/*
 * @(#) LoaiKhachHang.java  1.0  [9:57:13 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public enum HangKhachHang {
	KHACH_HANG_THUONG("Khách hàng thường"),
    KHACH_HANG_VIP("Khách hàng VIP");
	
    private final String description;

    HangKhachHang(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
