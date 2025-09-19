package entity.type;
/*
 * @(#) EnumTrangThaiVe.java  1.0  [12:33:07 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumTrangThaiVe {
	VE_CHUA_SU_DUNG("Vé chưa sử dụng"),
    VE_DA_SU_DUNG("Vé đã sử dụng"),
    VE_HET_HAN("Vé hết hạn"),
	VE_DA_HOAN("Vé đã hoàn"),
	VE_DA_HUY("Vé đã hủy");
	
    private final String description;

    EnumTrangThaiVe(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
