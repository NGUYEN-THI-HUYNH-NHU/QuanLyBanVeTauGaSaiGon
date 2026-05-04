package dao;

import entity.HoaDon;

import java.util.Date;
import java.util.List;

public interface IHoaDonDAO {
    boolean insertHoaDon(HoaDon hoaDon) throws Exception;

    List<HoaDon> searchHoaDonByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                      Date denNgay, String hinhThucTT, int page, int limit);

    List<HoaDon> searchHoaDonByKeyword(String keyword, String type, int page, int limit);

    List<String> getTop10HoaDonID(String keyword);
    
    int countAll();

    int countByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, String hinhThucTT);

    int countByKeyword(String keyword, String type);

    List<HoaDon> getByPage(int page, int limit);
}
