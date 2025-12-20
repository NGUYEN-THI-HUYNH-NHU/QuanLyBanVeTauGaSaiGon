package bus;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import dao.KhachHang_DAO;
import entity.KhachHang;
import entity.NhanVien;
import entity.NhatKyAudit;
import gui.application.AuthService;

public class KhachHang_BUS {
	private final KhachHang_DAO khachHang_dao;
	private final NhanVien nhanVienHienTai;
	private final NhatKyAudit_BUS nhatKyAudit_bus;

	public KhachHang_BUS() {
		khachHang_dao = new KhachHang_DAO();
		this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
		this.nhatKyAudit_bus = new NhatKyAudit_BUS();
	}

	// them khach hang
	public boolean themKhachHang(KhachHang kh) {
		boolean ok = khachHang_dao.themKhachHang(kh);

		if (ok) {
			ghiLog(kh.getKhachHangID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
					entity.type.NhatKyAudit.THEM, "Thêm khách hàng: " + kh.getHoTen() + " - " + kh.getSoDienThoai());
		}
		return ok;
	}

	// ghi log
	public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
		if (nhatKyAudit_bus == null) {
			return;
		}

		String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

		NhatKyAudit audit = new NhatKyAudit(nhatKyAudit_bus.taoMaNhatKyAuditMoi(), doiTuongID, nguoi,
				LocalDateTime.now(), loai, String.format("<html>̀%s</html>", chiTiet), "KHACH_HANG");

		nhatKyAudit_bus.ghiNhatKyAudit(audit);

	}

	// tim kiem khach hang
//	public KhachHang timKiemKhachHangTheoID(String khachHangID) {
//		return khachHang_dao.timKhachHangTheoID(khachHangID);
//	}

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
		return String.format("KH%05d", maxID + 1);
	}

	public boolean themHoacCapNhatKhachHang(Connection conn, KhachHang khachHang) throws Exception {
		return khachHang_dao.saveOrUpdate(conn, khachHang);
	}

	// Cập nhật loại khách hàng
	public boolean capNhatLoaiKhachHang(KhachHang kh) {
		return khachHang_dao.capNhatLoaiKhachHang(kh);
	}

	/**
	 * @param keyword
	 * @return
	 */
	public List<KhachHang> layGoiYKhachHang(String keyword) {
		return khachHang_dao.getTop10KhachHangSuggest(keyword);
	}

	// ================= LẤY THÀNH PHẦN BỊ THAY ĐỔI ===================
	public String thanhPhanDaBiSua(KhachHang cu, KhachHang moi) {
		StringBuilder thayDoi = new StringBuilder();

		if (!cu.getHoTen().equals(moi.getHoTen())) {
			thayDoi.append("Cập nhật tên khách hàng: (" + "" + cu.getHoTen() + ")" + " -> (" + moi.getHoTen() + ")\n");
		}
		if (moi.getSoDienThoai() != null && !cu.getSoDienThoai().equals(moi.getSoDienThoai())) {
			thayDoi.append("Cập nhật số điện thoại: (" + "" + cu.getSoDienThoai() + ")" + " -> (" + moi.getSoDienThoai()
					+ ")\n");
		}
		if (moi.getEmail() != null && !cu.getEmail().equals(moi.getEmail())) {
			thayDoi.append("Cập nhật email: (" + "" + cu.getEmail() + ")" + " -> (" + moi.getEmail() + ")\n");
		}
		if (moi.getDiaChi() != null && !cu.getDiaChi().equals(moi.getDiaChi())) {
			thayDoi.append("Cập nhật địa chỉ: (" + "" + cu.getDiaChi() + ")" + " -> (" + moi.getDiaChi() + ")\n");
		}
		if (!cu.getSoGiayTo().equals(moi.getSoGiayTo())) {
			thayDoi.append(
					"Cập nhật số giấy tờ: (" + "" + cu.getSoGiayTo() + ")" + " -> (" + moi.getSoGiayTo() + ")\n");
		}
		if (!cu.getLoaiKhachHang().equals(moi.getLoaiKhachHang())) {
			thayDoi.append("Cập nhật loại khách hàng: (" + "" + cu.getLoaiKhachHang() + ")" + " -> ("
					+ moi.getLoaiKhachHang() + ")\n");
		}
		if (moi.getLoaiDoiTuong() != null && moi.getLoaiDoiTuong() != null
				&& !cu.getLoaiDoiTuong().equals(moi.getLoaiDoiTuong())) {
			thayDoi.append("Cập nhật ngày sinh: (" + "" + cu.getLoaiDoiTuong() + ")" + " -> (" + moi.getLoaiDoiTuong()
					+ ")\n");
		}
		return thayDoi.toString();
	}

	// Cập nhật khách hàng
	public boolean capNhatKhachHang(KhachHang kh) {

		// 1. Lấy thông tin khách hàng cũ
		KhachHang khachHangCu = khachHang_dao.timKhachHangTheoID(kh.getKhachHangID());
		if (khachHangCu == null) {
			return false;
		}

		// 2. Cập nhật khách hàng
		boolean ok = khachHang_dao.capNhatKhachHang(kh);
		if (!ok) {
			return false;
		}

		// 3. build chi tiet thay doi
		String thanhPhan = thanhPhanDaBiSua(khachHangCu, kh);
		if (thanhPhan == null || thanhPhan.isBlank()) {
			return true;
		}

		// 4. Ghi log
		if (ok) {
			ghiLog(kh.getKhachHangID(), AuthService.getInstance().getCurrentUser().getNhanVienID(),
					entity.type.NhatKyAudit.SUA, "Cập nhật khách hàng: " + thanhPhan);
		}
		System.out.println("thanhPhan=" + thanhPhan);
		return ok;
	}

}
