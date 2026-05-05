package dao;

import entity.HoaDonChiTiet;

import java.util.List;

public interface IHoaDonChiTietDAO {
    List<HoaDonChiTiet> getHoaDonChiTietByHoaDonID(String hoaDonID);
}
