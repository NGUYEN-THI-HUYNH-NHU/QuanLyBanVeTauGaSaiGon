package dao;

import entity.Ghe;

import java.util.List;

public interface IGheDAO extends IGenericDAO<Ghe, String> {
    List<Ghe> getGheByGaDiGaDenChuyenToa(String gaDiID, String gaDenID, String chuyenID, String toaID);
    
    int calcGia(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID);

}
