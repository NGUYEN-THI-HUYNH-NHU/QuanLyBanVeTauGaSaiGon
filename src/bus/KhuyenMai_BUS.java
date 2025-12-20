package bus;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dao.KhuyenMai_DAO;
import dao.SuDungKhuyenMai_DAO;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.SuDungKhuyenMai;
import entity.Tuyen;
import entity.Ve;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;
import entity.type.TrangThaiSDKM;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;

public class KhuyenMai_BUS {
	private final KhuyenMai_DAO khuyenMai_dao;
	private final SuDungKhuyenMai_DAO suDungKhuyenMaiDAO = new SuDungKhuyenMai_DAO();
	private final NhanVien nhanVienHienTai;
	private final NhatKyAudit_BUS nhatKyAudit_bus;

	public KhuyenMai_BUS() {
		khuyenMai_dao = new KhuyenMai_DAO();
		this.nhanVienHienTai = AuthService.getInstance().getCurrentUser();
		this.nhatKyAudit_bus = new NhatKyAudit_BUS();
	}

	// lấy danh sách khuyến mãi
	public List<KhuyenMai> layDanhSachKhuyenMai() {
		return khuyenMai_dao.getAllKhuyenMai();
	}

	// tìm khuyến mãi
	public List<KhuyenMai> timKiemKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate ngayBatDau,
			LocalDate ngayKetThuc, LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuong) {
		return khuyenMai_dao.timKhuyenMai(tuKhoa, maTuyen, trangThai, ngayBatDau, ngayKetThuc, loaiTau, hangToa,
				loaiDoiTuong);
	}

	// lay dieu kien khuyen mai theo ma khuyen mai
	public String layDieuKienKhuyenMaiTheoMaKhuyenMai(String khuyenMaiIDString) {
		return khuyenMai_dao.layDieuKienKhuyenMai(khuyenMaiIDString);
	}

	// ==========regex
	public boolean kiemTraCodeKhuyenMai(String code) {
		String regex = "^[A-Z][A-Z0-9_]{4,30}$"; // ví dụ: LE_2024, MUA_50
		return code != null && code.matches(regex);
	}

	public boolean kiemMoTa(String moTa) {
		String regex = "^.{0,100}$";
		return moTa.matches(regex);
	}

	public boolean kiemTraTyLeGiamGia(double tyLeGiamGia) {
		return tyLeGiamGia >= 0 && tyLeGiamGia <= 100;
	}

	public boolean kiemTraTienGiamGia(double tiemGiamGia) {
		return tiemGiamGia >= 0;
	}

	public boolean kiemTraSoLuong(double soLuong) {
		return soLuong >= 0;
	}

	public boolean kiemTraGioiHanMoiKhachHang(int gioiHan) {
		return gioiHan >= 0;
	}

	public boolean kiemTraNgayBatDau(LocalDate ngayBatDau) {
		return !ngayBatDau.isBefore(LocalDate.now());
	}

	public boolean kiemTraNgayKetThuc(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
		return !ngayKetThuc.isBefore(ngayBatDau);
	}

	public boolean kiemTraMinGiaTriDonHang(double minGiaTri) {
		return minGiaTri >= 0;
	}

	public boolean kiemTraNgayTrongTuan(int ngayTrongTuan) {
		if (ngayTrongTuan == 0) {
			return true;
		}
		return ngayTrongTuan >= 1 && ngayTrongTuan <= 7;
	}

	// Tu cap nhat trang thai
	public boolean capNhatTrangThaiKhuyenMai() {
		return khuyenMai_dao.tuDongCapNhatTrangThai();
	}

	// tạo mã khuyến mãi tự động
	public String taoMaKhuyenMaiTuDong() {
		return khuyenMai_dao.taoMaKhuyenMaiTuDong();
	}

	// tao id dieu kien khuyen mai tu dong
	public String taoDieuKienKhuyenMaiTuDong() {
		return khuyenMai_dao.taoMaDieuKienTuDong();
	}

	// lay dieu kien khuyen mai theo ma khuyen mai
	public DieuKienKhuyenMai layKhuyenMaiTheoMaKhuyenMai(String khuyenMaiIDString) {
		return khuyenMai_dao.layDieuKienKhuyenMaiTheoKhuyenMai(khuyenMaiIDString);
	}

	// lay danh khach dieu kien khuyen mai
//	public List<DieuKienKhuyenMai> layDanhSachDKKM() {
//		return khuyenMai_dao.getAllDKKM();
//	}

	// lay danh sach tuyen
	public List<Tuyen> layDanhSachTuyen() {
		return khuyenMai_dao.layDanhSachTuyen();
	}

	public KhuyenMai timKhuyenMaiChoVe(Ve ve) {
		// TODO Auto-generated method stub
		return new KhuyenMai();
	}

	public List<KhuyenMai> getDanhSachKhuyenMaiPhuHop(VeSession veSession) {
		return khuyenMai_dao.getDanhSachKhuyenMaiPhuHop(veSession);
	}

	public void ganDanhSachSuDungKhuyenMai(List<VeSession> listVeSession) {
		for (VeSession ve : listVeSession) {
			if (ve.getKhuyenMaiApDung() != null && ve.getKhuyenMaiApDung().getKhuyenMaiID() != null) {
				String sdkmID = "SD-" + UUID.randomUUID();
				ve.setSuDungKhuyenMai(
						new SuDungKhuyenMai(sdkmID, ve.getKhuyenMaiApDung(), null, TrangThaiSDKM.DA_AP_DUNG));
			}
		}
	}

	public void themDanhSachSuDungKhuyenMai(Connection conn, List<VeSession> listVeSession) throws Exception {
		for (VeSession ve : listVeSession) {
			if (ve.getKhuyenMaiApDung() != null && ve.getKhuyenMaiApDung().getKhuyenMaiID() != null) {
				suDungKhuyenMaiDAO.themSuDungKhuyenMai(conn, ve.getSuDungKhuyenMai());
				khuyenMai_dao.giamSoLuongKhuyenMai(conn, ve.getKhuyenMaiApDung().getKhuyenMaiID());
			}
		}
	}

	/**
	 * @param conn
	 * @param listVe
	 */
	public int capNhatTrangThaiSDKMCuaVe(Connection conn, List<Ve> listVe) throws Exception {
		return suDungKhuyenMaiDAO.huySuDungKhuyenMaiChoListVe(conn, listVe);
	}

	/**
	 * @param conn
	 * @param listVe
	 * @return
	 */
	public Map<String, Integer> layDanhSachKhuyenMaiCanHoan(Connection conn, List<Ve> listVe) throws Exception {
		return khuyenMai_dao.getDanhSachKhuyenMaiCanHoan(conn, listVe);
	}

	/**
	 * @param conn
	 * @param kmID
	 * @param soLuongCanCong
	 */
	public boolean congSoLuongKhuyenMai(Connection conn, String kmID, int soLuongCanCong) throws Exception {
		return khuyenMai_dao.updateSoLuongKhuyenMai(conn, kmID, soLuongCanCong);
	}

	// Ghi vào log
	public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
		if (nguoiThucHienID == null || nguoiThucHienID.isBlank()) {
			nguoiThucHienID = nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : "SYSTEM";
		}

		NhatKyAudit audit = new NhatKyAudit(nhatKyAudit_bus.taoMaNhatKyAuditMoi(), doiTuongID, nguoiThucHienID,
				LocalDateTime.now(), loai, chiTiet, "KHUYEN_MAI");
		nhatKyAudit_bus.ghiNhatKyAudit(audit);
	}

	// Thêm khuyến mãi
	public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
		boolean ok = khuyenMai_dao.themKhuyenMai(km, dkkm);

		if (ok) {
			ghiLog(km.getKhuyenMaiID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
					entity.type.NhatKyAudit.THEM, "Thêm khuyến mãi: " + km.getMaKhuyenMai() + " - " + km.getMoTa());
		}
		return ok;
	}

	// Tìm thành phần đã bị sửa
	public String thanhPhanDaBiSua(KhuyenMai kmMoi, DieuKienKhuyenMai dkkmMoi, KhuyenMai kmCu,
			DieuKienKhuyenMai dkkmCu) {
		StringBuilder thayDoi = new StringBuilder();

		if (!kmMoi.getMaKhuyenMai().equals(kmCu.getMaKhuyenMai())) {
			thayDoi.append(String.format("Cập nhật code khuyến mãi: ('%s' -> '%s')" + "\n", kmCu.getMaKhuyenMai(),
					kmMoi.getMaKhuyenMai()));
		}
		if (!kmMoi.getMoTa().equals(kmCu.getMoTa())) {
			thayDoi.append(String.format("Cập nhật mô tả: ('%s' thành '%s')" + "\n", kmCu.getMoTa(), kmMoi.getMoTa()));
		}
		if (kmMoi.getTyLeGiamGia() != kmCu.getTyLeGiamGia()) {
			thayDoi.append(String.format("Cập nhật tỉ lệ giảm giá: (%.2f%% -> %.2f%%)" + "\n", kmCu.getTyLeGiamGia(),
					kmMoi.getTyLeGiamGia()));
		}
		if (kmMoi.getTienGiamGia() != kmCu.getTienGiamGia()) {
			thayDoi.append(String.format("Cập nhật tiền giảm giá: (%.2f -> %.2f)" + "\n", kmCu.getTienGiamGia(),
					kmMoi.getTienGiamGia()));
		}
		if (kmMoi.getSoLuong() != kmCu.getSoLuong()) {
			thayDoi.append(
					String.format("Cập nhật số lượng: (%d -> %d)" + "\n", kmCu.getSoLuong(), kmMoi.getSoLuong()));
		}
		if (!kmMoi.isTrangThai() == (kmCu.isTrangThai())) {
			thayDoi.append(String.format("Cập nhật trạng thái: ('%s' -> '%s')" + "\n", kmCu.isTrangThai(),
					kmMoi.isTrangThai()));
		}

		// Điều kiện khuyến mãi
		if (dkkmMoi.getMinGiaTriDonHang() != dkkmCu.getMinGiaTriDonHang()) {
			thayDoi.append(String.format("Cập nhật giới hạn mới khách hàng: (%d -> %d)" + "\n",
					dkkmCu.getMinGiaTriDonHang(), dkkmMoi.getMinGiaTriDonHang()));
		}
		if (!dkkmMoi.getTuyen().getTuyenID().equals(dkkmCu.getTuyen().getTuyenID())) {
			thayDoi.append(String.format("Cập nhật mã tuyến: ('%s' -> '%s')" + "\n", dkkmCu.getTuyen().getTuyenID(),
					dkkmMoi.getTuyen().getTuyenID()));
		}
		if (!dkkmMoi.getHangToa().equals(dkkmCu.getHangToa())) {
			thayDoi.append(String.format("Cập nhật hạng toa: ('%s' -> '%s')" + "\n", dkkmCu.getHangToa(),
					dkkmMoi.getHangToa()));
		}
		if (dkkmMoi.getNgayTrongTuan() != (dkkmCu.getNgayTrongTuan())) {
			thayDoi.append(String.format("Cập nhật ngày bắt đầu: ('%s' -> '%s')" + "\n", dkkmCu.getNgayTrongTuan(),
					dkkmMoi.getNgayTrongTuan()));
		}
		if (!dkkmMoi.getLoaiTau().equals(dkkmCu.getLoaiTau())) {
			thayDoi.append(String.format("Cập nhật ngày kết thúc: ('%s' -> '%s')" + "\n", dkkmCu.getLoaiTau(),
					dkkmMoi.getLoaiTau()));
		}

		return thayDoi.toString();

	}

	// Sửa khuyến mãi
	public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
		// 1. Lấy thông tin khuyến mãi cũ
		KhuyenMai khuyenMaiCu = khuyenMai_dao.timKiemKhuyenMaiByID(km.getKhuyenMaiID());
		DieuKienKhuyenMai dieuKienKhuyenMaiCu = khuyenMai_dao.layDieuKienKhuyenMaiTheoKhuyenMai(km.getKhuyenMaiID());
		if (khuyenMaiCu == null || dieuKienKhuyenMaiCu == null) {
			return false;
		}

		// 2. Cập nhật khuyến mãi
		boolean ok = khuyenMai_dao.suaKhuyenMai(km, dkkm);
		if (!ok) {
			return false;
		}

		// 3. build chi tiet thay doi
		String thanhPhan = thanhPhanDaBiSua(km, dkkm, khuyenMaiCu, dieuKienKhuyenMaiCu);
		if (thanhPhan == null || thanhPhan.isBlank()) {
			return true;
		}

		// 4. Ghi log
		if (ok) {
			ghiLog(km.getKhuyenMaiID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
					entity.type.NhatKyAudit.SUA, "Cập nhật khuyến mãi: " + thanhPhan);
		}
		return ok;
	}

	// tim khuyen mai theo ID
	public KhuyenMai layKhuyenMaiTheoID(String khuyenMaiID) {
		return khuyenMai_dao.timKiemKhuyenMaiByID(khuyenMaiID);
	}
}