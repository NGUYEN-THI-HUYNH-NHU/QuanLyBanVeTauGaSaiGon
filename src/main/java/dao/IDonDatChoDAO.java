package dao;

import entity.DonDatCho;

import java.util.Date;
import java.util.List;

public interface IDonDatChoDAO extends IGenericDAO<DonDatCho, String> {
    DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo);
    
    List<DonDatCho> searchDonDatChoByKeyword(String keyword, String type, int page, int limit);

    List<DonDatCho> searchDonDatChoByFilter(String tuKhoaTraCuu, String loaiTraCuu, Date tuNgay, Date denNgay, int page, int limit);

    int countDonDatChoByKeyword(String keyword, String type);

    int countDonDatChoByFilter(String keyword, String type, Date tuNgay, Date denNgay);

    int countAll();

    List<DonDatCho> getDonDatChoByPage(int page, int limit);

    List<String> getTop10DonDatChoID(String keyword);
}