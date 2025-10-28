package bus;

import dao.KhuyenMai_DAO;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.Tuyen;
import java.time.LocalDate;
import java.util.List;

public class KhuyenMai_BUS {
    private final KhuyenMai_DAO khuyenMai_dao;

    public KhuyenMai_BUS() {
        khuyenMai_dao = new KhuyenMai_DAO();
    }

    //lấy danh sách khuyến mãi
    public List<KhuyenMai> layDanhSachKhuyenMai() {
        return khuyenMai_dao.getAllKhuyenMai();
    }

    //thêm khuyến mãi
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return khuyenMai_dao.themKhuyenMai(km, dkkm);
    }

    //sửa khuyến mãi
    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return khuyenMai_dao.suaKhuyenMai(km, dkkm);
    }

    //tìm khuyến mãi
    public List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate tuNgay, LocalDate denNgay) {
        return khuyenMai_dao.timKiemKhuyenMai(tuKhoa, maTuyen, trangThai, tuNgay, denNgay);
    }
    //lay dieu kien khuyen mai theo ma khuyen mai
    public DieuKienKhuyenMai layDieuKienKhuyenMaiTheoMaKhuyenMai(String khuyenMaiIDString) {
        return khuyenMai_dao.layDieuKienKhuyenMai(khuyenMaiIDString);
    }

    //==========regex
    public boolean kiemTraCodeKhuyenMai(String code) {
        String regex = "^[A-Z][A-Z0-9_]{4,30}$"; // ví dụ: LE2024, MUA_50, KM12345
        return code != null && code.matches(regex);
    }
    public boolean kiemMoTa(String moTa){
        String regex = "^.{0,100}$";
        return moTa.matches(regex);
    }
    public boolean kiemTraTyLeGiamGia(double tyLeGiamGia){
        return tyLeGiamGia >=0 && tyLeGiamGia <=100;
    }
    public boolean kiemTraTienGiamGia(double tiemGiamGia){
        return tiemGiamGia >=0;
    }
    public boolean kiemTraSoLuong(double soLuong){
        return soLuong >= 0;
    }
    public boolean kiemTraGioiHanMoiKhachHang(int gioiHan){
        return gioiHan >=0;
    }
    public boolean kiemTraNgayBatDau(LocalDate ngayBatDau){
        return !ngayBatDau.isBefore(LocalDate.now());
    }
    public boolean kiemTraNgayKetThuc(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return !ngayKetThuc.isBefore(ngayBatDau);
    }

    public boolean kiemTraMinGiaTriDonHang(double minGiaTri){
        return minGiaTri >=0;
    }
    public boolean kiemTraNgayTrongTuan(int ngayTrongTuan) {
        if (ngayTrongTuan == 0) {
            return true;
        }
        return ngayTrongTuan >= 1 && ngayTrongTuan <= 7;
    }

    //tạo mã khuyến mãi tự động
    public String taoMaKhuyenMaiTuDong() {
        return khuyenMai_dao.taoMaKhuyenMaiTuDong();
    }
    //tao id dieu kien khuyen mai tu dong
    public String taoDieuKienKhuyenMaiTuDong() {
        return khuyenMai_dao.taoMaDieuKienTuDong();
    }
    // lay danh khach dieu kien khuyen mai
    public List<DieuKienKhuyenMai> layDanhSachDKKM() {
        return khuyenMai_dao.getAllDKKM();
    }
    //lay danh sach tuyen
    public List<Tuyen> layDanhSachTuyen() {
        return khuyenMai_dao.layDanhSachTuyen();
    }
}
