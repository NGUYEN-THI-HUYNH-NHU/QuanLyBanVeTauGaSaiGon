package controller;

import bus.NhatKyAudit_BUS;
import entity.NhatKyAudit;

import java.time.LocalDate;

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
    public java.util.List<NhatKyAudit> layDanhSachNhatKy() {
        return nhatKyAuditBus.layDanhSachNhatKy();
    }

    // tạo mã nhật ký audit mới
    public String taoMaNhatKyAuditMoi() {
        return nhatKyAuditBus.taoMaNhatKyAuditMoi();
    }

    // lọc nhật ký audit theo khoảng thời gian
    public java.util.List<NhatKyAudit> locNhatKyTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return nhatKyAuditBus.locNhatKyTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
    }

    // lọc nhật ký audit theo nhân viên
    public java.util.List<NhatKyAudit> locNhatKyTheoNhanVien(String nhanVienID) {
        return nhatKyAuditBus.locNhatKyTheoNhanVien(nhanVienID);
    }

    // lọc nhật ký audit theo đối tượng thao tác
    public java.util.List<NhatKyAudit> locNhatKyTheoDoiTuongThaoTac(String doiTuongThaoTac) {
        return nhatKyAuditBus.locNhatKyTheoDoiTuongThaoTac(doiTuongThaoTac);
    }

    //Tạo mã nhật ký audit tự động
    public String taoMaNhatKyAuditTuDong() {
        return nhatKyAuditBus.taoMaNhatKyAuditMoi();
    }

}
