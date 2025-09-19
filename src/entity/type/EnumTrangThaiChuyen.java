package entity.type;
/*
 * @(#) Enum_TrangThaiChuyen.java  1.0  [10:11:40 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public enum EnumTrangThaiChuyen {
	CHUAN_BI("Chuẩn bị"),
	DA_KHOI_HANH("Đã khởi hành"),
	DA_HOAN_THANH("Đã hoàn thành"),
	BI_HUY("Bị hủy");

    private final String description;

    EnumTrangThaiChuyen(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
