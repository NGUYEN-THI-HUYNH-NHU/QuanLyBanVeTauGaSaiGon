package bus;

import java.util.List;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;

public class TaiKhoan_BUS {
	private final TaiKhoan_DAO taiKhoan_dao;

	public TaiKhoan_BUS() {
		taiKhoan_dao = new TaiKhoan_DAO();
	}

	// lay danh sach tai khoan
	public List<TaiKhoan> layDanhSachTaiKhoan() {
		return taiKhoan_dao.getDanhSachTaiKhoan();
	}

	// cap nhat tai khoan
	public boolean capNhatTaiKhoan(TaiKhoan tk) {
		return taiKhoan_dao.capNhatTaiKhoan(tk);
	}

	// them tai khoan
	public boolean themTaiKhoan(TaiKhoan tk) {
		return taiKhoan_dao.taoTaiKhoan(tk);
	}

	// lay tai khoan theo ten dang nhap
	public TaiKhoan layTKThenDangNhap(String tenDangNhap) {
		return taiKhoan_dao.getTaiKhoanVoiTenDangNhap(tenDangNhap);
	}

	// lay tai khoan theo ma nhan vien
	public TaiKhoan layTKTheoMaNV(String maNV) {
		return taiKhoan_dao.getTaiKhoanVoiNhanVienID(maNV);
	}

	// kiem tra ten dang nhap da ton tai
	public boolean kiemTraTenDangNhapTonTai(String tenDN) {
		return taiKhoan_dao.kiemTraTenDangNhap(tenDN);
	}

	// doi mat khau
	public boolean doiMatKhau(String tenDN, String matKhauMoi) {
		return taiKhoan_dao.capNhatMatKhau(tenDN, matKhauMoi);
	}

	// tao ma tai khoan
	public String taoMaTaiKhoan() {
		return taiKhoan_dao.taoMaTaiKhoanMoi();
	}

	// goi y khi tim kiem ten dang nhap
	public String goiYTenDangNhap(String tenDN) {
		return taiKhoan_dao.goiYTenDangNhap(tenDN);
	}

	// regex
	// kiem tra ten dang nhap
	public boolean kiemTraTenDangNhap(String tenDN) {
		String regex = "^[a-zA-Z0-9._-]{5,20}$";
		return tenDN.matches(regex);
	}

	// kiem tra xac nhan mat khau
	public boolean kiemTraXacNhanMatKhau(String matKhau, String xacNhanMatKhau) {
		return matKhau.equals(xacNhanMatKhau);
	}

	// tim kiem tong hop
	public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
		return taiKhoan_dao.timKiemTongHop(maNV, tenDN, vaiTro, trangThai);
	}

	public boolean isKhopMatKhau(String nhanVienID, String matKhau) {
		return taiKhoan_dao.getTaiKhoanVoiNhanVienID(nhanVienID).getMatKhauHash().equals(matKhau);
	}

}
