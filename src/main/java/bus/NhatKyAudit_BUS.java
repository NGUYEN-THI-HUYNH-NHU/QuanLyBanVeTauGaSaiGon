package bus;

import dao.INhatKyAuditDAO;
import dao.impl.NhatKyAuditDAO;
import entity.NhatKyAudit;
import java.time.LocalDate;
import java.util.List;

public class NhatKyAudit_BUS {
    private final INhatKyAuditDAO nhatKyAudit_dao;

    public NhatKyAudit_BUS() {
        this.nhatKyAudit_dao = new NhatKyAuditDAO();
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

    public List<NhatKyAudit> layDanhSachPhanTrang(int page, int size, LocalDate tu, LocalDate den, String maNV, String loai) {
        return nhatKyAudit_dao.layDanhSachPhanTrang(page, size, tu, den, maNV, loai);
    }

    public long demTongSoDong(LocalDate tu, LocalDate den, String maNV, String loai) {
        return nhatKyAudit_dao.demTongSoDong(tu, den, maNV, loai);
    }

}
