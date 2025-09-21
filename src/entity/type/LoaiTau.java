package entity.type;
/*
 * @(#) LoaiTau.java  1.0  [9:45:34 AM] Sep 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 20, 2025
 * @version: 1.0
 */

public enum LoaiTau {
	TAU_NHANH("Tàu nhanh"),
    TAU_DU_LICH("Tàu du lịch/địa phương");
	
    private final String description;

	LoaiTau(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
