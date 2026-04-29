package dao;

import entity.NhanVien;
import entity.VaiTroNhanVien;

import java.util.List;

public interface INhanVien_DAO extends IGenericDao<NhanVien, String> {
    List<NhanVien> getNhanVienVoiHoTen(String hoTenTim);

    String taoMaNhanVienTuDong();

    List<NhanVien> timKiemNhanVien(String ten, String sdt, String vaiTroID, Boolean isHoatDong);

    VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV);
}
