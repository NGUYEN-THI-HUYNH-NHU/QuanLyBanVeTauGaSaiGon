package dao;

import entity.Ve;
import entity.type.TrangThaiVe;

import java.util.Date;
import java.util.List;

public interface IVeDAO {
    List<Ve> getVeByDonDatChoID(String donDatChoID);

    boolean insertVe(Ve ve) throws Exception;

    Ve getVeByVeID(String veID);

    List<String> getVeIDsStartingWith(String baseID);

    boolean updateTrangThaiVe(String veID, TrangThaiVe trangThai);
    
    List<Ve> searchVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, int page, int limit);

    List<Ve> searchVeByKeyword(String keyword, String type, int page, int limi);

    // CÁC HÀM HỖ TRỢ SUGGESTION (Auto-complete)
    // Lấy Top 10 Mã Hóa Đơn gần đúng
    List<String> getTop10VeID(String keyword);

    // Lấy Top 10 Mã Giao Dịch gần đúng
    List<String> getTop10DonDatChoID(String keyword);

    // Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
    List<String> getTop10SoGiayToKhachHang(String keyword);

    int countVeByKeyword(String keyword, String type);

    int countVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay);
}
