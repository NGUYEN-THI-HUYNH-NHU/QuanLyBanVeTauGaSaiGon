package dao;

import entity.KhachHang;
import java.util.List;

public interface IKhachHangDAO extends IGenericDAO<KhachHang, String> {
    boolean themKhachHang(KhachHang kh);
    boolean capNhatKhachHang(KhachHang kh);
    boolean capNhatLoaiKhachHang(KhachHang  khachHang);
    KhachHang timKhachHangTheoSDT(String sdt);
    KhachHang timKhachHangTheoSoGiayTo(String soGiayTo);
    List<KhachHang> getAllKhachHang();
    KhachHang timKhachHangTheoID(String khachHangID);
    boolean saveOrUpdate(KhachHang khachHang);
    List<KhachHang> getTop10KhachHangSuggest(String keyword);
    String taoMaKhachHangTuDong();
}
