package entity.type;
/*
 * @(#) EnumLoaiDichVu.java  1.0  [12:43:55 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum EnumLoaiDichVu {
	BAN_VE("Bán vé"),
    HOAN_VE("Hoàn vé"),
	DOI_VE("Đổi vé"),
	BAO_HIEM("Bảo hiểm"),
	AN_UONG("Ăn uống"),
	PHONG_VIP("Phòng chờ VIP"),
	HANH_LY_KY_GUI("Hành lý ký gửi"),
	KHUYEN_MAI("Khuyến mãi");

    private final String description;

    EnumLoaiDichVu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
