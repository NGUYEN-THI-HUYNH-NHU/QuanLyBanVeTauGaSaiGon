package dao;

import entity.Ghe;

import java.util.List;

public interface IGhe_DAO extends IGenericDao<Ghe, String> {
    List<Ghe> getGheByGaDiGaDenChuyenToa(String gaDiID, String gaDenID, String chuyenID, String toaID);

    Ghe getGheByChuyenIDGheID(String chuyenID, String gheID);

    int calcGia(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID);

}
