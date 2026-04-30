package dao;

import entity.HoaDonChiTiet;

import java.util.List;

public interface IHoaDonChiTietDAO {
    boolean insertHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) throws Exception;

    List<HoaDonChiTiet> getHoaDonChiTietByHoaDonID(String hoaDonID);
}
