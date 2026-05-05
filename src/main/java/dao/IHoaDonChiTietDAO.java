package dao;

import entity.HoaDonChiTiet;

import java.util.List;

public interface IHoaDonChiTietDAO extends IGenericDAO<HoaDonChiTiet, String> {
    List<HoaDonChiTiet> getHoaDonChiTietByHoaDonID(String hoaDonID);
}
