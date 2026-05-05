package dao;/*
 * @ (#) IGaDAO.java   1.0     05/05/2026
package dao;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import entity.Ga;

import java.util.List;

public interface IGaDAO {
    List<Ga> searchGaByPrefix(String prefix, int limit);

    Ga getGaByTenGa(String tenGa);

    List<Ga> getGaByTenGaList(String tenGaTim);

    List<Ga> searchGaDenKhaThiByGaDi(String gaDiID, String prefixGaDen, int limit);

    List<Ga> getAllGa();

    Ga getGaByIDTim(String gaIDTim);

    boolean themGa(Ga gaMoi);

    int capNhatGa(String gaIDSua, Ga gaCapNhat);
}
