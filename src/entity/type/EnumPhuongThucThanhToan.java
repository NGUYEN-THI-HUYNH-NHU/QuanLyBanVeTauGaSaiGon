package entity.type;
/*
 * @(#) EnumPhuongThucThanhToan.java  1.0  [12:38:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumPhuongThucThanhToan {
	THANH_TOAN_TIEN_MAT("Thanh toán tiền mặt"),
    THANH_TOAN_CHUYEN_KHOAN("Thanh toán chuyển khoản");
	
    private final String description;

    EnumPhuongThucThanhToan(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
