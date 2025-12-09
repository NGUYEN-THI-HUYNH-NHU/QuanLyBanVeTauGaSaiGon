package controller;

import java.util.List;

import bus.TaiKhoan_BUS;
import entity.TaiKhoan;

public class TaiKhoan_CTRL {
	private final TaiKhoan_BUS taiKhoan_bus;

	public TaiKhoan_CTRL() {
		taiKhoan_bus = new TaiKhoan_BUS();
	}

	// lay danh sach tai khoan
	public List<TaiKhoan> layDanhSachTaiKhoan() {
		return taiKhoan_bus.layDanhSachTaiKhoan();
	}

	// them tai khoan
	public boolean themTaiKhoan(TaiKhoan tk) {
		return taiKhoan_bus.themTaiKhoan(tk);
	}

	// cap nhat tai khoan
	public boolean capNhatTaiKhoan(TaiKhoan tk) {
		return taiKhoan_bus.capNhatTaiKhoan(tk);
	}

	// lay tai khoan theo ten dang nhap
	public TaiKhoan layTKTheoTenDangNhap(String tenDN) {
		return taiKhoan_bus.layTKThenDangNhap(tenDN);
	}

	// tao ma tai khoan
	public String taoMaTaiKhoan() {
		return taiKhoan_bus.taoMaTaiKhoan();
	}

	// lay tai khoan theo ma nhan vien
	public TaiKhoan layTKTheoMaNV(String maNV) {
		return taiKhoan_bus.layTKTheoMaNV(maNV);
	}

	// kiem tra ten dang nhap da ton tai
	public boolean kiemTraTenDangNhapTonTai(String tenDN) {
		return taiKhoan_bus.kiemTraTenDangNhapTonTai(tenDN);
	}

	// kiem tra ten dang nhap
	public boolean kiemTraTenDangNhap(String tenDN) {
		return taiKhoan_bus.kiemTraTenDangNhap(tenDN);
	}

	// doi mat khau
	public boolean doiMatKhau(String tenDN, String matKhauMoi) {
		return taiKhoan_bus.doiMatKhau(tenDN, matKhauMoi);
	}

	// kiem tra mat khau
	public boolean kiemTraMatKhau(String matKhau, String xacNhanMatKhau) {
		return taiKhoan_bus.kiemTraXacNhanMatKhau(matKhau, xacNhanMatKhau);
	}

	// tim kiem tong hop
	public List<TaiKhoan> timKiemTongHop(String maNV, String vaiTro, String tenDN, Boolean trangThai) {
		return taiKhoan_bus.timKiemTongHop(maNV, vaiTro, tenDN, trangThai);
	}

}
