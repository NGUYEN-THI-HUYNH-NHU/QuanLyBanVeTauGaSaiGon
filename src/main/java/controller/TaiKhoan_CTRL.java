package controller;

import bus.TaiKhoan_BUS;
import dto.NhanVienDTO;
import entity.TaiKhoan;
import entity.VaiTroTaiKhoan;
import gui.application.AuthService;
import java.util.List;

public class TaiKhoan_CTRL {
    private final TaiKhoan_BUS taiKhoan_bus;
    private final NhanVienDTO nhanVienHienTai;

    public TaiKhoan_CTRL() {
        this.taiKhoan_bus = new TaiKhoan_BUS();
        this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
    }

    // Lấy danh sách tài khoản
    public List<TaiKhoan> layDanhSachTaiKhoan() {
        return taiKhoan_bus.layDanhSachTaiKhoan();
    }

    // Thêm tài khoản
    public boolean themTaiKhoan(TaiKhoan tk) {
        return taiKhoan_bus.themTaiKhoan(tk);
    }

    // Cập nhật tài khoản
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        return taiKhoan_bus.suaTaiKhoan(tk);
    }

    // Lấy tài khoản theo tên đăng nhập
    public TaiKhoan layTKTheoTenDangNhap(String tenDN) {
        return taiKhoan_bus.layTKThenDangNhap(tenDN);
    }

    // Tạo mã tài khoản tự động
    public String taoMaTaiKhoan() {
        return taiKhoan_bus.taoMaTaiKhoan();
    }

    // Kiểm tra tên đăng nhập đã tồn tại trong DB chưa
    public boolean kiemTraTenDangNhapTonTai(String tenDN) {
        return taiKhoan_bus.kiemTraTenDangNhapTonTai(tenDN);
    }

    // Kiểm tra regex tên đăng nhập hợp lệ
    public boolean kiemTraTenDangNhap(String tenDN) {
        return taiKhoan_bus.kiemTraTenDangNhap(tenDN);
    }

    // Đổi mật khẩu
    public boolean doiMatKhau(String tenDN, String matKhauMoi) {
        return taiKhoan_bus.doiMatKhau(tenDN, matKhauMoi);
    }

    // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp nhau không
    public boolean kiemTraMatKhau(String matKhau, String xacNhanMatKhau) {
        return taiKhoan_bus.kiemTraXacNhanMatKhau(matKhau, xacNhanMatKhau);
    }

    // Tìm kiếm tổng hợp tài khoản
    public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        return taiKhoan_bus.timKiemTongHop(maNV, tenDN, vaiTro, trangThai);
    }

    // Lấy tài khoản bằng mã nhân viên
    public TaiKhoan layTKTheoMaNV(String maNV) {
        return taiKhoan_bus.layTKTheoMaNV(maNV);
    }

    // SỬA TẠI ĐÂY: Lấy tài khoản theo ID tài khoản để phục vụ việc giữ lại mật khẩu khi Sửa
    public TaiKhoan layTaiKhoanTheoID(String id) {
        return taiKhoan_bus.layTKTheoID(id);
    }

    // SỬA TẠI ĐÂY: Lấy đối tượng VaiTroTaiKhoan từ Database để tránh lỗi Lazy Loading
    public VaiTroTaiKhoan layVaiTroTheoID(String vaiTroID) {
        return taiKhoan_bus.layVaiTroTheoID(vaiTroID);
    }
}