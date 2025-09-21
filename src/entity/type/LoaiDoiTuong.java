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

public enum LoaiDoiTuong {
	NGUOI_LON("Người lớn"),
    TRE_EM("Trẻ em"),
	NGUOI_CAO_TUOI("Người cao tuổi");
	
    private final String description;

    LoaiDoiTuong(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
