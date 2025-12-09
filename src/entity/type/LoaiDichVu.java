package entity.type;
/*
 * @(#) LoaiDichVu.java  1.0  [6:02:07 PM] Nov 3, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 3, 2025
 * @version: 1.0
 */

public enum LoaiDichVu {
	VE_BAN("Vé bán"), VE_HOAN("Vé hoàn"), VE_DOI("Vé đổi"), PHI_HOAN("Lệ phí hoàn vé"), PHI_DOI("Lệ phí đổi vé"),
	KHUYEN_MAI("Khuyến mãi"), PHONG_VIP("Phòng VIP"), PHIEU_HUY("Phiếu dùng phòng VIP hủy");

	private final String description;

	LoaiDichVu(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}