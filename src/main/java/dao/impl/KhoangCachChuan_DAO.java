package dao.impl;/*
 * @ (#) KhoangCachChuan_DAO.java   1.0     28/10/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
 */

import connectDB.ConnectDB;
import entity.KhoangCachChuan;
import jakarta.persistence.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KhoangCachChuan_DAO extends AbstractGenericDAO<KhoangCachChuan, String> implements dao.IKhoangCachChuanDAO {

    public KhoangCachChuan_DAO() {
        super(KhoangCachChuan.class);
    }

    /**
     * Lấy khoảng cách chuẩn giữa hai ga liền kề
     *
     * @param gaID_Dau  ID Ga Xuất Phát của đoạn (ví dụ: 'HNI').
     * @param gaID_Cuoi ID Ga Đích của đoạn (ví dụ: 'VIN').
     * @return Khoảng cách thực tế giữa hai ga, hoặc -1 nếu không tìm thấy.
     */
    @Override
    public int getKhoangCachDoan(String gaID_Dau, String gaID_Cuoi) {
        return doInTransaction(em -> {
            String sql = "SELECT khoangCachKm " +
                    "FROM KhoangCachChuan " +
                    "WHERE (GaID_Dau = ?1 AND GaID_Cuoi = ?2) " +
                    "OR (GaID_Cuoi = ?3 AND GaID_Dau = ?4)";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, gaID_Dau);
            query.setParameter(2, gaID_Cuoi);
            query.setParameter(3, gaID_Cuoi);
            query.setParameter(4, gaID_Dau);

            List<?> results = query.getResultList();

            if (!results.isEmpty() && results.get(0) != null) {
                return ((Number) results.get(0)).intValue();
            }
            return -1;
        });
    }

    /**
     * Tải toàn bộ bảng khoảng cách chuẩn vào bộ nhớ dưới dạng Đồ thị (Graph).
     *
     * @return Map<String, Map<String, Integer>> (Đồ thị: GaID_Nguồn -> ( GaID_Đích -> Khoảng cách ))
     */
    @Override
    public Map<String, Map<String, Integer>> getAllKhoangCachMap() {
        return doInTransaction(em -> {
            Map<String, Map<String, Integer>> doThi = new HashMap<>();

            String sql = "SELECT gaID_Dau, gaID_Cuoi, khoangCachKm FROM KhoangCachChuan";

            Query query = em.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                String gaID_Dau = ((String) row[0]).trim();
                String gaID_Cuoi = ((String) row[1]).trim();
                int khoangCachKm = ((Number) row[2]).intValue();

                doThi.putIfAbsent(gaID_Dau, new HashMap<>());
                doThi.get(gaID_Dau).put(gaID_Cuoi, khoangCachKm);

                doThi.putIfAbsent(gaID_Cuoi, new HashMap<>());
                doThi.get(gaID_Cuoi).put(gaID_Dau, khoangCachKm);
            }

            return doThi;
        });
    }
}
