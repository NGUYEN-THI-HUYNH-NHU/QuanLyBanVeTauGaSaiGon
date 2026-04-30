package dao;

import entity.HoaDon;
import entity.NhanVien;

import java.util.Date;
import java.util.List;

public interface IHoaDonDAO {
    boolean insertHoaDon(HoaDon hoaDon) throws Exception;

    List<HoaDon> getHoaDonByNhanVien(NhanVien nhanVien);

    List<HoaDon> searchHoaDonByFilter(String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                      Date denNgay, String hinhThucTT);

    List<HoaDon> searchHoaDonByKeyword(String keyword, String type);

    // CÁC HÀM HỖ TRỢ SUGGESTION (Auto-complete)
    // Lấy Top 10 Mã Hóa Đơn gần đúng
    List<String> getTop10HoaDonID(String keyword);

    // Lấy Top 10 Mã Giao Dịch gần đúng
    List<String> getTop10MaGD(String keyword);

    // Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
    List<String> getTop10KhachHangID(String keyword);

    List<HoaDon> getAllHoaDon();
}
