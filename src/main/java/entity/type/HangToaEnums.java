package entity.type;

public enum HangToaEnums {
    GN_K4("Giường nằm khoang 4 điều hòa"),
    GN_K6("Giường nằm khoang 6 điều hòa"),
    NM_CLC("Ngồi mềm chất lượng cao");

    private final String description;

    HangToaEnums(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
