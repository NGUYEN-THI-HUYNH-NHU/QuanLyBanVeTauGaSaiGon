package dao;

import entity.DonDatCho;

import java.util.Date;
import java.util.List;

public interface IDonDatChoDAO extends IGenericDAO<DonDatCho, String> {
    DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo);

    boolean insertDonDatCho(DonDatCho donDatCho) throws Exception;

    List<DonDatCho> getListDonDatCho();


    List<DonDatCho> searchDonDatChoByKeyword(String keyword, String type);


    List<String> getTop10DonDatChoID(String keyword);


    List<String> getTop10SoGiayTo(String keyword);


    List<String> getTop10SoDienThoai(String keyword);


    List<String> getTop10TenKhachHang(String keyword);


    List<DonDatCho> searchDonDatChoByFilter(Date tuNgay, Date denNgay);
}
