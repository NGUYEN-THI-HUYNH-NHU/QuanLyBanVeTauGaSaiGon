package entity.type;
/*
 * @(#) TrangThaiCho.java  1.0  [5:06:24 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public enum TrangThaiCho {
	TRANG("Ghế trống"),
	XANH_LA("Ghế chặng dài - Có thể đặt/ Phụ phí chặng dài"),
	XANH_DUONG("Ghế chặng dài - Không thể đặt"),
	DO("Ghế đã bán");
	
	private final String description;

	TrangThaiCho(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
