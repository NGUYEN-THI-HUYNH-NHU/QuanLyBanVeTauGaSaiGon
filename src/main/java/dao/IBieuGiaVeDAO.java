package dao;

import entity.BieuGiaVe;

import java.util.List;

public interface IBieuGiaVeDAO {
    List<BieuGiaVe> getAllBieuGia();

    List<BieuGiaVe> getBieuGiaTheoTieuChi(String tuKhoa, String maTuyen, String loaiTau);

    boolean themBieuGia(BieuGiaVe bg);

    boolean capNhatBieuGia(BieuGiaVe bg);

    boolean xoaBieuGia(String id);

    BieuGiaVe getBieuGiaByID(String id);
}
