package entity.type;

public enum NhatKyAudit {
    THEM("Thêm"),
    SUA("Sửa"),
    XOA("Xóa"),
//    DANG_NHAP("Đăng nhập"),
//    DANG_XUAT("Đăng xuất"),
    DOI_MAT_KHAU("Đổi mật khẩu"),
    QUEN_MAT_KHAU("Quên mật khẩu"),
    BAN_VE("Bán vé"),
    DOI_VE("Đổi vé"),
    HOAN_VE("Hoàn vé"),
    IN_VE("In vé");

    private final String moTa;

    NhatKyAudit(String moTa) {this.moTa = moTa;}

    public String getMoTa() {return moTa;}
}

