package bus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.type.VaiTroNhanVien;

public class NhanVien_BUS {

	private final NhanVien_DAO nhanVien_dao;
	private final NhatKyAudit_BUS audit_bus;

	public NhanVien_BUS(NhatKyAudit_BUS auditBus) {
		this.audit_bus = auditBus;
		this.nhanVien_dao = new NhanVien_DAO();
	}

	// ================= LẤY DỮ LIỆU =================

	public List<NhanVien> layDanhSachNhanVien() {
		return nhanVien_dao.getAllNhanVien();
	}

	public NhanVien layNhanVienBangMaNV(String maNV) {
		return nhanVien_dao.getNhanVienVoiID(maNV);
	}

	// ================= THÊM NHÂN VIÊN =================

	public boolean themNhanVien(NhanVien nv, String nguoiThucHienID) {
		boolean ok = nhanVien_dao.themNhanVien(nv);

		if (ok) {
			ghiAudit(
					nv.getNhanVienID(),
					nguoiThucHienID,
					entity.type.NhatKyAudit.THEM,
					"Thêm nhân viên: " + nv.getHoTen() + "-" + nv.getSoDienThoai()
			);
		}
		return ok;
	}

	// ================= SỬA NHÂN VIÊN =================

	public boolean suaNhanvVien(NhanVien nv, String nguoiThucHienID) {
		boolean ok = nhanVien_dao.capNhatNhanVien(nv);

		if (ok) {
			ghiAudit(
					nv.getNhanVienID(),
					nguoiThucHienID,
					entity.type.NhatKyAudit.SUA,
					"Cập nhật thông tin nhân viên: " + nv.getHoTen()
			);
		}
		return ok;
	}

	// giữ hàm cũ để không vỡ code
	public boolean suaNhanvVien(NhanVien nv) {
		return suaNhanvVien(nv, null);
	}

	// ================= CẬP NHẬT AVATAR =================

	public boolean capNhatAvatar(String nhanVienID, byte[] imgBytes, String nguoiThucHienID) {
		boolean ok = nhanVien_dao.capNhatAvatar(nhanVienID, imgBytes);

		if (ok) {
			ghiAudit(
					nhanVienID,
					nguoiThucHienID,
					entity.type.NhatKyAudit.SUA,
					"Cập nhật ảnh đại diện nhân viên"
			);
		}
		return ok;
	}

	// giữ hàm cũ để không vỡ code
	public boolean capNhatAvatar(String nhanVienID, byte[] imgBytes) {
		return capNhatAvatar(nhanVienID, imgBytes, nhanVienID);
	}


	//
	private void ghiAudit(
			String doiTuongID,
			String nguoiThucHienID,
			entity.type.NhatKyAudit loai,
			String chiTiet
	) {
		if (audit_bus == null) return;

		String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

		NhatKyAudit audit = new NhatKyAudit(
				audit_bus.taoMaNhatKyAuditMoi(),
				doiTuongID,
				nguoi,
				LocalDateTime.now(),
				loai,
				chiTiet,
				"NHAN_VIEN"
		);

		audit_bus.ghiNhatKyAudit(audit);
	}

	public boolean validHoTen(String hoTen) {
		String regex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
		return hoTen.matches(regex);
	}

	public boolean validSDT(String sdt) {
		String regex = "^(0[35789][0-9]{8})$";
		return sdt.matches(regex);
	}

	public boolean validEmail(String email) {
		String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(regex);
	}

	public boolean validDiaChi(String diaChi) {
		String regex = "^[\\wÀ-ỹ0-9\\s,.-]{1,100}$";
		return diaChi.matches(regex);
	}

	public boolean ngaySinh(LocalDate ns) {
		return ns.isBefore(LocalDate.now());
	}

	public boolean ngayThamGia(LocalDate ntg) {
		return !ntg.isAfter(LocalDate.now());
	}

	public boolean validCaLam(String caLam) {
		String regex = "^(Sáng|Chiều|Tối)$";
		return caLam.matches(regex);
	}

	public boolean validGioiTinh(boolean isNu) {
		return true;
	}


	public String taoMaNhanVienTuDong() {
		return nhanVien_dao.taoMaNhanVienTuDong();
	}

	public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVien vaiTro, Boolean isHoatDong) {
		return nhanVien_dao.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong);
	}

	public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
		NhanVien nv = nhanVien_dao.getNhanVienVoiID(maNV);
		return (nv != null) ? nv.getVaiTroNhanVien() : null;
	}

	public List<String> layDanhSachMaNhanVien() {
		return nhanVien_dao.layDanhSachMaNhanVien();
	}

	//so sanh khi co su cap nhat
//	public
}
