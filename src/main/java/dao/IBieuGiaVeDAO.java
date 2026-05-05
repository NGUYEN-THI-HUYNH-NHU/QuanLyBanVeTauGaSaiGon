package dao;

import entity.BieuGiaVe;

import java.util.List;

public interface IBieuGiaVeDAO extends IGenericDAO<BieuGiaVe, String> {
    List<BieuGiaVe> getBieuGiaTheoTieuChi(String tuKhoa, String maTuyen, String loaiTau);
}
