package dao;

import entity.Ve;
import entity.type.TrangThaiVe;

import java.util.Date;
import java.util.List;

public interface IVeDAO {
    List<Ve> getVeByDonDatChoID(String donDatChoID);
    
    List<String> getVeIDsStartingWith(String baseID);

    boolean updateTrangThaiVe(String veID, TrangThaiVe trangThai);

    List<Ve> searchVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, int page, int limit);

    List<Ve> searchVeByKeyword(String keyword, String type, int page, int limi);

    List<String> getTop10VeID(String keyword);

    int countVeByKeyword(String keyword, String type);

    int countVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay);
}
