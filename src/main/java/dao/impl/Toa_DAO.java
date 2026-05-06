package dao.impl;
/*
 * @(#) Toa_DAO.java  1.0  [12:58:30 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.HangToa;
import entity.Tau;
import entity.Toa;
import jakarta.persistence.Query;

import java.util.List;

public class Toa_DAO extends AbstractGenericDAO<Toa, String> implements dao.IToaDAO {
    public Toa_DAO() {
        super(Toa.class);
    }

    @Override
    public List<Toa> getToaByChuyenID(String chuyenID) {
        return doInTransaction(em -> {
            String jpql = "SELECT t FROM Toa t " +
                    "JOIN FETCH t.hangToa " +
                    "JOIN FETCH t.tau tau " +
                    "JOIN Chuyen c ON tau = c.tau " +
                    "WHERE c.chuyenID = :chuyenID ORDER BY t.soToa";
            return em.createQuery(jpql, Toa.class)
                    .setParameter("chuyenID", chuyenID)
                    .getResultList();
        });
    }

    @Override
    public Toa getToaByID(String toaID) {
        return doInTransaction(em -> em.find(Toa.class, toaID));
    }


    @Override
    public Toa getToaByChuyenIDToaID(String chuyenID, String toaID) {
        return doInTransaction(em -> {
            String sql = "select toa.toaID, toa.tauID, toa.hangToaID, toa.sucChua, toa.soToa"
                    + " from Toa toa join Chuyen c on toa.tauID = c.tauID"
                    + " where c.chuyenID = ?1"
                    + " and toa.toaID = ?2";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, chuyenID);
            query.setParameter(2, toaID);

            List<Object[]> results = query.getResultList();

            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                Toa t = new Toa();
                t.setToaID((String) row[0]);
                t.setTau(new Tau((String) row[1]));
                t.setHangToa(new HangToa((String) row[2]));
                t.setSucChua(row[3] != null ? ((Number) row[3]).intValue() : 0);
                t.setSoToa(row[4] != null ? ((Number) row[4]).intValue() : 0);

                return t;
            }
            return null;
        });
    }
}