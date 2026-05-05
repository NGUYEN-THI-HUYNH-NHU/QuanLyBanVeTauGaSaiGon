package dao;

import entity.GiaoDichHoanDoi;

public interface IGiaoDichHoanDoiDAO extends IGenericDAO<GiaoDichHoanDoi, String> {
    boolean insertGiaoDichHoanDoi(GiaoDichHoanDoi giaoDichHoanDoi) throws Exception;
}