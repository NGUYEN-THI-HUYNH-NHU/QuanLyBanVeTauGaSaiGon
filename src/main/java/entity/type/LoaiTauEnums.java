package entity.type;

public enum LoaiTauEnums {
    TAU_DU_LICH("Tàu du lịch/ tàu địa phương"),
    TAU_NHANH("Tàu");
    private final String description;

    LoaiTauEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
