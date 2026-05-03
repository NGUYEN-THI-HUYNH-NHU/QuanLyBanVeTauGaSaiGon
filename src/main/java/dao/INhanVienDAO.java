package dao;

import entity.CaLam;
import entity.NhanVien;
import entity.VaiTroNhanVien;

import java.util.List;

public interface INhanVienDAO extends IGenericDAO<NhanVien, String> {
    List<NhanVien> getNhanVienVoiHoTen(String hoTenTim);
    String taoMaNhanVienTuDong();
    List<NhanVien> timKiemNhanVien(String tuKhoa, String vaiTroID, Boolean isHoatDong);
    VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV);
    boolean capNhatAvatar(String nhanVienID, byte[] avatarData);
    List<String> layDanhSachMaNhanVien();
    List<CaLam> getAllCaLam();
    CaLam getCaLamById(String caLamID);
}
