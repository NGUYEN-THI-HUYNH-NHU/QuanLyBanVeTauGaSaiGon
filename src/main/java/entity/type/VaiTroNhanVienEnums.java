package entity.type;

public enum VaiTroNhanVienEnums {
    QUAN_LY("Quản lý"),
    NHAN_VIEN("Nhân viên quầy vé");

    private final String description;

    VaiTroNhanVienEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static VaiTroNhanVienEnums fromDescription(String desc) {
        for (VaiTroNhanVienEnums vt : values()) {
            if (vt.getDescription().equalsIgnoreCase(desc)) {
                return vt;
            }
        }
        return NHAN_VIEN;
    }
}