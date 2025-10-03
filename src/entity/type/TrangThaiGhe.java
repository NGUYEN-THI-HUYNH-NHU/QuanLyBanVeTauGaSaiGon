package entity.type;
/*
 * @(#) TrangThaiGhe.java  1.0  [9:08:39 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

public enum TrangThaiGhe {
    DA_BAN("Ghế đã bán"),
    BI_CHIEM("Ghế bị chiếm dụng"),
    TRONG("Ghế trống");

    private final String description;

    TrangThaiGhe(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
