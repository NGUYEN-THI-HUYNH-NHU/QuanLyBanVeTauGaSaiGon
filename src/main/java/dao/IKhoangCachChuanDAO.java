package dao;/*
 * @ (#) IKhoangCachChuanDAO.java   1.0     05/05/2026
package dao;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import java.util.Map;

public interface IKhoangCachChuanDAO {
    int getKhoangCachDoan(String gaID_Dau, String gaID_Cuoi);

    Map<String, Map<String, Integer>> getAllKhoangCachMap();
}
