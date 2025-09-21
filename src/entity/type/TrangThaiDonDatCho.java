package entity.type;
/*
 * @(#) TrangThaiDonDatVe.java  1.0  [4:59:08 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public enum TrangThaiDonDatCho {
	DANG_DOI("Nhân viên quầy vé"),
	DA_XAC_NHAN("Quản lý nhà ga"),
	HET_HAN("Hết hạn"),
	DA_HUY("Đã hủy");
	
    private final String description;

    TrangThaiDonDatCho(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
