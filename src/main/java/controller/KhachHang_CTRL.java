package controller;

import bus.KhachHang_BUS;
import dto.KhachHangDTO;
import dto.NhanVienDTO;
import entity.KhachHang;
import gui.application.AuthService;

import java.util.List;

public class KhachHang_CTRL {
    private final KhachHang_BUS khachHang_bus;
    private final NhanVienDTO nhanVienHienTai;

    public KhachHang_CTRL() {
        this.khachHang_bus = new KhachHang_BUS();
        this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
    }

    public boolean themKhachHang(KhachHangDTO kh) {
        return khachHang_bus.themKhachHang(kh);
    }

    public boolean capNhatKhachHang(KhachHangDTO kh) {
        return khachHang_bus.capNhatKhachHang(kh);
    }

    public KhachHang timKiemKhachHang(String sdt) {
        return khachHang_bus.timKiemKhachHangTheoSDT(sdt);
    }

    public KhachHangDTO timKiemKhachHangTheoSoGiayTo(String soGiayTo) {
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

    public List<KhachHang> getKhachHangPhanTrang(int page, int pageSize) {
        return khachHang_bus.getKhachHangPhanTrang(page, pageSize);
    }

    public long getTotalKhachHang() {
        return khachHang_bus.getTotalKhachHang();
    }
}