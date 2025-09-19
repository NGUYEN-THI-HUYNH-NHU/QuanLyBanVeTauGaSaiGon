package entity.type;
/*
 * @(#) EnumLoaiDoiTuong.java  1.0  [1:26:50 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumLoaiDoiTuong {
	NGUOI_LON("Đối tượng "),
    TRE_EM("Khách hàng VIP"),
	NGUOI_CAO_TUOI("Người cao tuổi");
	
    private final String description;

    EnumLoaiDoiTuong(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
