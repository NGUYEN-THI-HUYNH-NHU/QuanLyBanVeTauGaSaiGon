package controller;

import bus.KhuyenMai_BUS;
import dto.NhanVienDTO;
import entity.*;
import gui.application.AuthService;

import java.time.LocalDate;
import java.util.List;

public class KhuyenMai_CTRL {
    private final KhuyenMai_BUS khuyenMai_bus;

    public KhuyenMai_CTRL() {
        this.khuyenMai_bus = new KhuyenMai_BUS();
    }

    private String getMaNguoiThucHienHienTai() {
        NhanVienDTO currentUser = AuthService.getInstance().getCurrentUser();
        return (currentUser != null && currentUser.getId() != null) ? currentUser.getId() : "SYSTEM";
    }

    // ================= LẤY DỮ LIỆU =================

    public List<KhuyenMai> layDanhSachKhuyenMai() {
        return khuyenMai_bus.layDanhSachKhuyenMai();
    }

    public List<KhuyenMai> layKhuyenMaiTheoLoai(String loai) {
        return khuyenMai_bus.layKhuyenMaiTheoLoai(loai);
    }

    public List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai,
                                        LocalDate ngayBatDau, LocalDate ngayKetThuc,
                                        LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuong) {
        return khuyenMai_bus.timKiemKhuyenMai(tuKhoa, maTuyen, trangThai, ngayBatDau, ngayKetThuc, loaiTau, hangToa, loaiDoiTuong);
    }

    public String layDieuKienKhuyenMaiTheoMaKhuyenMai(String khuyenMaiID) {
        return khuyenMai_bus.layDieuKienKhuyenMaiTheoMaKhuyenMai(khuyenMaiID);
    }

    public DieuKienKhuyenMai layDieuKienKhuyenMaiTheoMaKhuyenMaiObj(String khuyenMaiID) {
        return khuyenMai_bus.layKhuyenMaiTheoMaKhuyenMaiObj(khuyenMaiID);
    }

    public KhuyenMai layKhuyenMaiTheoID(String khuyenMaiID) {
        return khuyenMai_bus.layKhuyenMaiTheoID(khuyenMaiID);
    }

    public List<Tuyen> layDanhSachTuyen() {
        return khuyenMai_bus.layDanhSachTuyen();
    }

    // Bổ sung 3 hàm để GUI nạp dữ liệu vào ComboBox thực thể
    public List<LoaiTau> layDanhSachLoaiTau() {
        return khuyenMai_bus.layDanhSachLoaiTau();
    }

    public List<HangToa> layDanhSachHangToa() {
        return khuyenMai_bus.layDanhSachHangToa();
    }

    public List<LoaiDoiTuong> layDanhSachLoaiDoiTuong() {
        return khuyenMai_bus.layDanhSachLoaiDoiTuong();
    }

    // ================= THAO TÁC NGHIỆP VỤ (Hành động) =================

    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return khuyenMai_bus.themKhuyenMai(km, dkkm, getMaNguoiThucHienHienTai());
    }

    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return khuyenMai_bus.suaKhuyenMai(km, dkkm, getMaNguoiThucHienHienTai());
    }

    public boolean tuDongCapNhatTrangThai() {
        return khuyenMai_bus.capNhatTrangThaiKhuyenMai();
    }

    // ================= SINH MÃ TỰ ĐỘNG =================

    public String taoMaKhuyenMaiTuDong() {
        return khuyenMai_bus.taoMaKhuyenMaiTuDong();
    }

    public String taoMaDieuKienKhuyenMaiTuDong() {
        return khuyenMai_bus.taoDieuKienKhuyenMaiTuDong();
    }

    // ================= TẦNG VALIDATION =================

    public boolean kiemTraCodeKhuyenMai(String code) {
        return khuyenMai_bus.kiemTraCodeKhuyenMai(code);
    }

    public boolean kiemMoTa(String moTa) {
        return khuyenMai_bus.kiemMoTa(moTa);
    }

    public boolean kiemTraTyLeGiamGia(double tyLeGiamGia) {
        return khuyenMai_bus.kiemTraTyLeGiamGia(tyLeGiamGia);
    }

    public boolean kiemTraTienGiamGia(double tiemGiamGia) {
        return khuyenMai_bus.kiemTraTienGiamGia(tiemGiamGia);
    }

    public boolean kiemTraSoLuong(double soLuong) {
        return khuyenMai_bus.kiemTraSoLuong(soLuong);
    }

    public boolean kiemTraGioiHanMoiKhachHang(int gioiHan) {
        return khuyenMai_bus.kiemTraGioiHanMoiKhachHang(gioiHan);
    }

    public boolean kiemTraNgayBatDau(LocalDate ngayBatDau) {
        return khuyenMai_bus.kiemTraNgayBatDau(ngayBatDau);
    }

    public boolean kiemTraNgayKetThuc(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return khuyenMai_bus.kiemTraNgayKetThuc(ngayBatDau, ngayKetThuc);
    }

    public boolean kiemTraNgayTrongTuan(int ngayTrongTuan) {
        return khuyenMai_bus.kiemTraNgayTrongTuan(ngayTrongTuan);
    }

    public boolean kiemTraMinGiaTriDonHang(double minGiaTriDonHang) {
        return khuyenMai_bus.kiemTraMinGiaTriDonHang(minGiaTriDonHang);
    }
}