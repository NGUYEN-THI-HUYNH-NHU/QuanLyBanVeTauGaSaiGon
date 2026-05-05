package dao;/*
 * @ (#) IChuyenDAO.java   1.0     05/05/2026
package dao.impl;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IChuyenDAO {
    List<Chuyen> getChuyenByGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi);

    List<Chuyen> getAllChuyen();

    Chuyen layChuyenTheoMa(String maChuyen);

    List<Chuyen> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi);

    default List<Chuyen> getListChuyenFromResultSet(EntityManager em, String sql, Object... params) {
        Query query = em.createNativeQuery(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
        }

        List<Object[]> results = query.getResultList();
        List<Chuyen> list = new ArrayList<>();

        for (Object[] row : results) {
            String maChuyen = (String) row[0];
            String tuyenID = (String) row[1];
            String tauID = (String) row[2];
            java.sql.Date sqlDateDi = (java.sql.Date) row[3];
            java.sql.Time sqlTimeDi = (java.sql.Time) row[4];

            String tenTau = (String) row[5];
            String loaiTauStr = (String) row[6];
            String tenGaDi = (String) row[7];
            String tenGaDen = (String) row[8];
            java.sql.Date sqlDateDen = (java.sql.Date) row[9];
            java.sql.Time sqlTimeDen = (java.sql.Time) row[10];

            Chuyen c = new Chuyen(maChuyen);

            String tenChuyen = (tenGaDi != null ? tenGaDi : "N/A") + " - " + (tenGaDen != null ? tenGaDen : "N/A");
            c.setTenChuyenHienThi(tenChuyen);
            c.setTenGaDiHienThi(tenGaDi);
            c.setTenGaDenHienThi(tenGaDen);

            Tau tau = new Tau(tauID, tenTau);
            if (loaiTauStr != null) {
                tau.setLoaiTau(new LoaiTau(loaiTauStr));
            }
            c.setTau(tau);

            if (tuyenID != null) {
                c.setTuyen(new Tuyen(tuyenID));
            }

            c.setNgayDi(sqlDateDi != null ? sqlDateDi.toLocalDate() : null);
            c.setGioDi(sqlTimeDi != null ? sqlTimeDi.toLocalTime() : null);
            c.setNgayDen(sqlDateDen != null ? sqlDateDen.toLocalDate() : null);
            c.setGioDen(sqlTimeDen != null ? sqlTimeDen.toLocalTime() : null);

            list.add(c);
        }
        return list;
    }

    List<String> getAllMaChuyenID();

    List<String> getAllTenGa();

    List<String> getAllTenTau();

    List<String> getAllTuyenID();

    List<String> getAllTauID();

    boolean themChuyenMoi(Chuyen chuyen, List<ChuyenGa> lichTrinh);

    Map<String, String> getMapTenGaToID();

    boolean capNhatChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinhMoi);

    List<Ga> getDsGaTheoTuyen(String tuyenID);

    boolean existsById(String chuyenID);

    int getTocDoTau(String tauID);

    List<String[]> getTauHoatDong();

    List<Ga> getDsGaVaTrangThaiLonTheoTuyen(String tuyenID);

    boolean themChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh);

    boolean capNhatChuyenBatch(List<Chuyen> dsChuyen, List<List<ChuyenGa>> dsLichTrinh);

    List<Chuyen> getChuyenTheoNgay(LocalDate ngay);

    int[] getThongKeCho(String chuyenID, String gaDiID, String gaDenID);
}
