package dao;

import entity.NhanVien;
import entity.TaiKhoan;

import java.util.List;

public interface ITaiKhoanDAO extends IGenericDAO<TaiKhoan, String> {
    TaiKhoan getTaiKhoanVoiTenDangNhap(String tenDangNhap);
    boolean capNhatMatKhau(String nhanVienID, String newMatKhau);
    boolean isTaiKhoanTonTai(String tenDangNhap);
    TaiKhoan getTaiKhoanVoiNhanVienID(String nhanVienID);
    NhanVien getNhanVienByTenDangNhap(String tenDangNhap, boolean xacThuc);
    List<TaiKhoan> getDanhSachTaiKhoan();
    boolean kiemTraTenDangNhap(String tenDN);
    String taoMaTaiKhoanMoi();
    String goiYTenDangNhap(String tenDN);
    List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai);
    boolean checkForgotPasswordInfo(String nhanVienID, String soDienThoai, String email);
    boolean checkDuplicatingPasswords(String nhanVienID, String newPass);
}
