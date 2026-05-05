package dao;/*
 * @ (#) ITuyenChiTietDAO.java   1.0     05/05/2026
package dao;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import entity.TuyenChiTiet;

import java.util.List;

public interface ITuyenChiTietDAO {
    List<TuyenChiTiet> layDanhSachTheoTuyenID(String tuyenID);

    boolean themDanhSachChiTiet(List<TuyenChiTiet> danhSachChiTiet);

    boolean xoaChiTietTheoTuyenID(String tuyenID);
}
