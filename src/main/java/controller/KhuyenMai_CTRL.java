package controller;

import bus.KhuyenMai_BUS;
import entity.*;
import gui.application.AuthService;

import java.time.LocalDate;
import java.util.List;

public class KhuyenMai_CTRL {
    private final KhuyenMai_BUS khuyenMai_bus;
    private final NhanVien nhanVienHienTai;

    public KhuyenMai_CTRL() {
        this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
        khuyenMai_bus = new KhuyenMai_BUS();
    }

    //lấy danh sách khuyến mãi
    public List<KhuyenMai> layDanhSachKhuyenMai() {
        return khuyenMai_bus.layDanhSachKhuyenMai();
    }

    //lấy khuyến mãi theo loại
    public List<KhuyenMai> layKhuyenMaiTheoLoai(String loai) {
        return khuyenMai_bus.layDanhSachKhuyenMai();
    }

    //thêm khách hàng
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        String nguoiThucHienID = (nhanVienHienTai != null && nhanVienHienTai.getNhanVienID() != null) ? nhanVienHienTai.getNhanVienID() : null;
        return khuyenMai_bus.themKhuyenMai(km, dkkm);
    }

    //sửa khách hàng
    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        String nguoiThucHienID = (nhanVienHienTai != null && nhanVienHienTai.getNhanVienID() != null) ? nhanVienHienTai.getNhanVienID() : null;
        return khuyenMai_bus.suaKhuyenMai(km, dkkm);
    }

    //tìm khuyến mãi
    public List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai,
                                        LocalDate ngayBatDau, LocalDate ngayKetThuc,
                                        LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuong) {
        return khuyenMai_bus.timKiemKhuyenMai(tuKhoa, maTuyen, trangThai, ngayBatDau, ngayKetThuc, loaiTau, hangToa, loaiDoiTuong);
    }

    //lay ma dieu kien khuyen mai theo ma khuyen mai
    public String layDieuKienKhuyenMaiTheoMaKhuyenMai(String khuyenMaiID) {
        return khuyenMai_bus.layDieuKienKhuyenMaiTheoMaKhuyenMai(khuyenMaiID);
    }

    //lay dieu kien khuyen mai theo ma khuyen mai
    public DieuKienKhuyenMai layDieuKienKhuyenMaiTheoMaKhuyenMaiObj(String khuyenMaiID) {
        return khuyenMai_bus.layKhuyenMaiTheoMaKhuyenMai(khuyenMaiID);
    }

    //==========regex
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

    //tao mã khuyến mãi tự động
    public String taoMaKhuyenMaiTuDong() {
        return khuyenMai_bus.taoMaKhuyenMaiTuDong();
    }

    //tao mã điều kiện khuyến mãi tự động
    public String taoMaDieuKienKhuyenMaiTuDong() {
        return khuyenMai_bus.taoDieuKienKhuyenMaiTuDong();
    }

    //lay danh sach tuyen
    public List<Tuyen> layDanhSachTuyen() {
        return khuyenMai_bus.layDanhSachTuyen();
    }

    //tu dong cap nhat trang thai
    public boolean tuDongCapNhatTrangThai() {
        return khuyenMai_bus.capNhatTrangThaiKhuyenMai();
    }

    //tim khuyen mai theo ID
    public KhuyenMai layKhuyenMaiTheoID(String khuyenMaiID) {
        return khuyenMai_bus.layKhuyenMaiTheoID(khuyenMaiID);
    }


}
