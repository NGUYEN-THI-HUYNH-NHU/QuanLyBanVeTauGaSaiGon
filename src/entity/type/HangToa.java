package entity.type;
/*
 * @(#) EnumLoaiToa.java  1.0  [4:22:41 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

public enum HangToa {
//	  NGOI_MEM_CHAT_LUONG_CAO("Ngồi mềm chất lượng cao"),
//    GIUONG_6_DIEU_HOA("Giường nằm khoang 6 điều hòa"),
//    GIUONG_4_DIEU_HOA("Giường nằm khoang 4 điều hòa");
	NM_CLC("Ngồi mềm chất lượng cao"),
	GN_K4("Giường nằm khoang 4 điều hòa"),
	GN_K6("Giường nằm khoang 6 điều hòa");
    
    private final String description;

	HangToa(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
