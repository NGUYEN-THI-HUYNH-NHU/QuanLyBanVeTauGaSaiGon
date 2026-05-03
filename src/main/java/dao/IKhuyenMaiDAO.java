package dao;

import dto.VeDTO;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.Tuyen;
import gui.application.form.banVe.VeSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import entity.*;

public interface IKhuyenMaiDAO extends IGenericDAO<KhuyenMai, String>{
    boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm);
    boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dk);
    List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate ngayBD, LocalDate ngayKT, LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuongEnums);
    String layDieuKienKhuyenMai(String khuyenMaiID);
    List<KhuyenMai> getAllKhuyenMai();
    String taoMaKhuyenMaiTuDong();
    String taoMaDieuKienTuDong();
    DieuKienKhuyenMai layDieuKienKhuyenMaiTheoKhuyenMai(String khuyenMaiID);
    List<Tuyen> layDanhSachTuyen();

    List<LoaiTau> layDanhSachLoaiTau();

    List<HangToa> layDanhSachHangToa();

    List<LoaiDoiTuong> layDanhSachLoaiDoiTuong();

    boolean tuDongCapNhatTrangThai();
    List<KhuyenMai> getDanhSachKhuyenMaiPhuHop(VeSession veSession);
    boolean giamSoLuongKhuyenMai(String khuyenMaiID);
    int demSoLanSuDungCuaKhachHang(String khuyenMaiID, String khachHangID);
    Map<String, Integer> getDanhSachKhuyenMaiCanHoan(List<VeDTO> listVe);
    boolean updateSoLuongKhuyenMai(String khuyenMaiID, int soLuongCanCong);
    KhuyenMai timKiemKhuyenMaiByID(String khuyenMaiID);
}
