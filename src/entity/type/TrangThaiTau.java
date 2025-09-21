package entity.type;
/*
 * @(#) TrangThaiTau.java  1.0  [5:03:18 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public enum TrangThaiTau {
	HOAT_DONG("Hoạt động"),
	BAO_TRI("Bảo trì"),
	NGUNG_HOAT_DONG("Ngừng hoạt động");
	
	private final String description;

	TrangThaiTau(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
