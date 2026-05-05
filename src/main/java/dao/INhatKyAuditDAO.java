package dao;

import entity.NhatKyAudit;
import java.time.LocalDate;
import java.util.List;

public interface INhatKyAuditDAO extends IGenericDAO<NhatKyAudit, String>{
    void ghiNhatKyAudit(NhatKyAudit nhatKy);
    List<NhatKyAudit> layDanhSachNhatKy();
    String maNhatKyMoi();
    List<NhatKyAudit> timKiemNhatKy(LocalDate tuNgay, LocalDate denNgay, String nhanVienID, String loaiThaoTac, String doiTuongID);
    String layTenNhanVienTheoMaNV(String nhanVienID);
    List<NhatKyAudit> locNhatKyTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc);

    List<NhatKyAudit> layDanhSachPhanTrang(int page, int pageSize, LocalDate tu, LocalDate den, String maNV, String loai);

    long demTongSoDong(LocalDate tu, LocalDate den, String maNV, String loai);
}
