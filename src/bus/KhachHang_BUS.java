package bus;

import java.sql.Connection;
import java.util.List;

import dao.KhachHang_DAO;
import entity.KhachHang;

public class KhachHang_BUS {
	private final KhachHang_DAO khachHang_dao;

	public KhachHang_BUS() {
		khachHang_dao = new KhachHang_DAO();
	}

	// hêm khách hàng
	public boolean themKhachHang(KhachHang kh) {
		return khachHang_dao.themKhachHang(kh);
	}

	// Cập nhật khách hàng
	public boolean capNhatKhachHang(KhachHang kh) {
		return khachHang_dao.capNhatKhachHang(kh);
	}

	// tìm kiếm khách hàng theo sdt
	public KhachHang timKiemKhachHang(String sdt) {
		return khachHang_dao.timKhachHangTheoSDT(sdt);
	}

	// tìm kiếm khách hàng theo sgt
	public KhachHang timKiemKhachHangTheoSoGiayTo(String soGiayTo) {
		return khachHang_dao.timKhachHangTheoSoGiayTo(soGiayTo);
	}

	// Lấy danh sách tất cả khách hàng
	public List<KhachHang> getAllKhachHang() {
		return khachHang_dao.getAllKhachHang();
	}

	// Kiểm tra định dạng email
	public boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email != null && email.matches(emailRegex);
	}

	// Kiểm tra định dạng sdt
	public boolean isValidPhoneNumber(String phoneNumber) {
		String phoneRegex = "^(0[35789][0-9]{8})$";
		return phoneNumber != null && phoneNumber.matches(phoneRegex);
	}

	// Kiểm tra tên
	public boolean isValidTen(String ten) {
		String tenRegex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
		return ten != null && ten.matches(tenRegex);
	}

	// Kiểm tra địa chỉ
	public boolean isValidDiaChi(String diaChi) {
		String diaChiRegex = "^[\\p{L}0-9\\s,./-]+$";
		return diaChi != null && diaChi.matches(diaChiRegex);
	}

	// Kiểm tra trùng số điện thoại trong CSDL
	public boolean kiemTraTrungSDT(String sdt) {
		for (KhachHang kh : khachHang_dao.getAllKhachHang()) {
			if (kh.getSoDienThoai() != null && kh.getSoDienThoai().equalsIgnoreCase(sdt)) {
				return true;
			}
		}
		return false;
	}

	// Kiểm tra trùng số giấy tờ trong CSDL
	public boolean kiemTraTrungSoGiayTo(String soGiayTo) {
		for (KhachHang kh : khachHang_dao.getAllKhachHang()) {
			if (kh.getSoGiayTo() != null && kh.getSoGiayTo().equalsIgnoreCase(soGiayTo)) {
				return true;
			}
		}
		return false;
	}

	// Tạo mã khách hàng tự động
	public String taoMaKhachHangTuDong() {
		List<KhachHang> danhSachKhachHang = khachHang_dao.getAllKhachHang();
		int maxID = 0;
		for (KhachHang kh : danhSachKhachHang) {
			String idStr = kh.getKhachHangID().replace("KH", "");
			try {
				int id = Integer.parseInt(idStr);
				if (id > maxID) {
					maxID = id;
				}
			} catch (NumberFormatException e) {

			}
		}
		return String.format("KH%03d", maxID + 1);
	}

	public boolean themHoacCapNhatKhachHang(Connection conn, KhachHang khachHang) {
		return khachHang_dao.saveOrUpdate(conn, khachHang);
	}

	// Cập nhật loại khách hàng
	public boolean capNhatLoaiKhachHang(KhachHang kh) {
		return khachHang_dao.capNhatLoaiKhachHang(kh);
	}

}
