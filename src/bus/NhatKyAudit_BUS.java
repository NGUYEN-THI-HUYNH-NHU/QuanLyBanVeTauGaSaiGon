package bus;

import dao.NhatKyAudit_DAO;

public class NhatKyAudit_BUS {
    private final NhatKyAudit_DAO nhatKyAudit_dao;

    public NhatKyAudit_BUS() {
        nhatKyAudit_dao = new NhatKyAudit_DAO();
    }

    //ghi nhật ký audit
    public void ghiNhatKyAudit(entity.NhatKyAudit nhatKy) {
        nhatKyAudit_dao.ghiNhatKyAudit(nhatKy);
    }


    //lấy danh sách nhật ký audit
    public java.util.List<entity.NhatKyAudit> layDanhSachNhatKy() {
        return nhatKyAudit_dao.layDanhSachNhatKy();
    }


    // tạo mã nhật ký audit mới
    public String taoMaNhatKyAuditMoi() {
        return nhatKyAudit_dao.maNhatKyMoi();
    }


    // lọc nhật ký audit theo khoang thời gian
    public java.util.List<entity.NhatKyAudit> locNhatKyTheoKhoangThoiGian(java.time.LocalDate ngayBatDau, java.time.LocalDate ngayKetThuc) {
        return nhatKyAudit_dao.layDanhSachNhatKyTheoKhoangThoiGian(ngayBatDau, ngayKetThuc);
    }


    // lọc nhật ký audit theo nhân viên
    public java.util.List<entity.NhatKyAudit> locNhatKyTheoNhanVien(String nhanVienID) {
        return nhatKyAudit_dao.layDanhSachNhatKyTheoNhanVien(nhanVienID);
    }


    // lọc nhật ký audit theo đối tượng thao tác
    public java.util.List<entity.NhatKyAudit> locNhatKyTheoDoiTuongThaoTac(String doiTuongThaoTac) {
        return nhatKyAudit_dao.layDanhSachNhatKyTheoDoiTuong(doiTuongThaoTac);
    }

}
