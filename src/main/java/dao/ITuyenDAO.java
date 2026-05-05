package dao;/*
 * @ (#) ITuyenDAO.java   1.0     05/05/2026
package dao;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import entity.Tuyen;
import entity.TuyenChiTiet;

import java.util.List;

public interface ITuyenDAO {
    List<Tuyen> getAllTuyen();

    List<Tuyen> getTuyenByID(String tuyenIDTim);

    List<Tuyen> getTuyenTheoGa(String gaDi, String gaDen);

    boolean themTuyenMoi(Tuyen tuyenMoi);

    boolean xoaTuyen(String tuyenID);

    boolean capNhatTuyen(Tuyen tuyenCapNhat);

    Tuyen getTuyenByExactID(String tuyenIDTim);

    List<Tuyen> getTop10Tuyen(String keyword);

    Tuyen layTuyenTheoMa(String maTuyen);

    List<TuyenChiTiet> layDanhSachTuyenChiTiet(String maTuyen);
}
