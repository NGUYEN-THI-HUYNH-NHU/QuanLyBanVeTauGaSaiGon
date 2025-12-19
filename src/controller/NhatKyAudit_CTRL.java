package controller;

import bus.NhatKyAudit_BUS;
import entity.NhatKyAudit;

import java.time.LocalDate;
import java.util.List;

public class NhatKyAudit_CTRL {
    private final NhatKyAudit_BUS nhatKyAuditBus;

    public NhatKyAudit_CTRL() {
        nhatKyAuditBus = new NhatKyAudit_BUS();
    }

    // ghi nhật ký audit
    public void ghiNhatKyAudit(NhatKyAudit nhatKy) {
        nhatKyAuditBus.ghiNhatKyAudit(nhatKy);
    }

    // lấy danh sách nhật ký audit
    public List<NhatKyAudit> layDanhSachNhatKy() {
        return nhatKyAuditBus.layDanhSachNhatKy();
    }

    // tạo mã nhật ký audit mới
    public String taoMaNhatKyAuditMoi() {
        return nhatKyAuditBus.taoMaNhatKyAuditMoi();
    }

    // lọc audit với nhiều tiêu chí (khoảng thời gian, nhân viên, đối tượng thao tác)
    public List<NhatKyAudit> locNhatKy(LocalDate ngayBatDau, LocalDate ngayKetThuc, String nhanVienID, String doiTuongThaoTac, String doiTuongID) {
        return nhatKyAuditBus.locNhatKy(ngayBatDau, ngayKetThuc, nhanVienID, doiTuongThaoTac, doiTuongID);
    }
    //lay ten nhan vien theo ma nhan vien
    public String layTenNhanVienTheoMaNV(String maNV) {
        return nhatKyAuditBus.layTenNhanVienTheoMaNV(maNV);
    }
    //lọc audit theo khoảng thời gian
    public List<NhatKyAudit> locNhatKyTheoThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return nhatKyAuditBus.locNhatKyTheoThoiGian(ngayBatDau, ngayKetThuc);
    }
}
