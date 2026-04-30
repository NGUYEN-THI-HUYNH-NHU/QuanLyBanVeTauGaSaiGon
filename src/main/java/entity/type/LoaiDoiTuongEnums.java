package entity.type;
/*
 * @(#) LoaiDoiTuong.java  1.0  [6:02:07 PM] Nov 3, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 3, 2025
 * @version: 1.0
 */

public enum LoaiDoiTuongEnums {
    TRE_EM("Trẻ em"), NGUOI_LON("Người lớn"), NGUOI_CAO_TUOI("Người cao tuổi");

    private final String description;

    LoaiDoiTuongEnums(String description) {
        this.description = description;
    }

    public static LoaiDoiTuongEnums fromDescription(String desc) {
        if (desc == null) return null;
        for (LoaiDoiTuongEnums x : values()) {
            if (x.getDescription().equalsIgnoreCase(desc.trim())) {
                return x;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }
}