package controller;

import bus.KhachHang_BUS;
import entity.KhachHang;
import entity.NhanVien;
import gui.application.AuthService;

import java.util.List;

public class KhachHang_CTRL {
    private final KhachHang_BUS khachHang_bus;
    private final NhanVien nhanVienHienTai;


    public KhachHang_CTRL() {
        khachHang_bus = new KhachHang_BUS();
        this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
    }

    public boolean themKhachHang(KhachHang kh) {
        String nguoiThucHienID = nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null;
        return khachHang_bus.themKhachHang(kh);
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        String nguoiThucHienID = nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null;
        return khachHang_bus.capNhatKhachHang(kh);
    }

    public KhachHang timKiemKhachHang(String sdt) {
        return khachHang_bus.timKiemKhachHang(sdt);
    }

    public KhachHang timKiemKhachHangTheoSoGiayTo(String soGiayTo) {
        return khachHang_bus.timKiemKhachHangTheoSoGiayTo(soGiayTo);
    }

    public List<KhachHang> getAllKhachHang() {
        return khachHang_bus.getAllKhachHang();
    }

    public boolean isValidEmail(String email) {
        return khachHang_bus.isValidEmail(email);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        return khachHang_bus.isValidPhoneNumber(phoneNumber);
    }

    public boolean isValidTen(String ten) {
        return khachHang_bus.isValidTen(ten);
    }

    public boolean isValidDiaChi(String diaChi) {
        return khachHang_bus.isValidDiaChi(diaChi);
    }

    public boolean kiemTraTrungSDT(String sdt) {
        return khachHang_bus.kiemTraTrungSDT(sdt);
    }

    public boolean kiemTraTrungSoGiayTo(String soGiayTo) {
        return khachHang_bus.kiemTraTrungSoGiayTo(soGiayTo);
    }
    public String taoMaKhachHang() {
        return khachHang_bus.taoMaKhachHangTuDong();
    }
}
