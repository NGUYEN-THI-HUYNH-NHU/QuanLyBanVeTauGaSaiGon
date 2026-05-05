package dao;

import entity.LoaiTau;

import java.util.List;

public interface ILoaiTauDAO extends IGenericDAO<LoaiTau, String> {
    List<String> getAllLoaiTauMoTa();
}
