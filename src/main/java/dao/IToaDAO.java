package dao;/*
 * @ (#) IToaDAO.java   1.0     05/05/2026
package dao;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import entity.Toa;

import java.util.List;

public interface IToaDAO {
    List<Toa> getToaByChuyenID(String chuyenID);

    Toa getToaByID(String toaID);

    Toa getToaByChuyenIDToaID(String chuyenID, String toaID);
}
