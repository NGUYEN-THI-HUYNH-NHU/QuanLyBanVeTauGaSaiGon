package bus;

import java.time.LocalDate;
import java.util.List;

import dao.NhatKyAudit_DAO;
import entity.NhatKyAudit;

public class NhatKyAudit_BUS {
	private final NhatKyAudit_DAO nhatKyAudit_dao;

	public NhatKyAudit_BUS() {
		nhatKyAudit_dao = new NhatKyAudit_DAO();
	}

	// ghi nhật ký audit
	public void ghiNhatKyAudit(NhatKyAudit nhatKy) {
		nhatKyAudit_dao.ghiNhatKyAudit(nhatKy);
	}

	// lấy danh sách nhật ký audit
	public List<NhatKyAudit> layDanhSachNhatKy() {
		return nhatKyAudit_dao.layDanhSachNhatKy();
	}

	// tạo mã nhật ký audit mới
	public String taoMaNhatKyAuditMoi() {
		return nhatKyAudit_dao.maNhatKyMoi();
	}

	// lọc audit với nhiều tiêu chí (khoảng thời gian, nhân viên, đối tượng thao
	// tác)
	public List<NhatKyAudit> locNhatKy(LocalDate ngayBatDau, LocalDate ngayKetThuc, String nhanVienID,
			String doiTuongThaoTac, String doiTuongID) {
		return nhatKyAudit_dao.timKiemNhatKy(ngayBatDau, ngayKetThuc, nhanVienID, doiTuongThaoTac, doiTuongID);
	}

	// lay ten nhan vien theo ma nhan vien
	public String layTenNhanVienTheoMaNV(String maNV) {
		return nhatKyAudit_dao.layTenNhanVienTheoMaNV(maNV);
	}

	// lọc audit theo khoảng thời gian
	public List<NhatKyAudit> locNhatKyTheoThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
		return nhatKyAudit_dao.locNhatKyTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
	}

}
