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

import java.util.ArrayList;
import java.util.List;

public class Toa_DAO extends AbstractGenericDAO<Toa, String> implements dao.IToaDAO {
    public Toa_DAO() {
        super(Toa.class);
    }

    @Override
    public List<Toa> getToaByChuyenID(String chuyenID) {
        return doInTransaction(em -> {
            String sql = "SELECT \r\n"
                    + "    t.toaID,\r\n"
                    + "    t.soToa,\r\n"
                    + "    t.hangToaID,\r\n"
                    + "    ht.moTa,\r\n"
                    + "    t.sucChua,\r\n"
                    + "    tau.tauID,\r\n"
                    + "    tau.tenTau\r\n"
                    + "FROM Chuyen c\r\n"
                    + "INNER JOIN Tau tau \r\n"
                    + "    ON tau.tauID = c.tauID\r\n"
                    + "INNER JOIN Toa t \r\n"
                    + "    ON t.tauID = tau.tauID\r\n"
                    + "INNER JOIN HangToa ht \r\n"
                    + "    ON t.hangToaID = ht.hangToaID\r\n"
                    + "WHERE c.chuyenID = ?1\r\n"
                    + "ORDER BY t.soToa;";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, chuyenID);

            List<Object[]> results = query.getResultList();
            List<Toa> list = new ArrayList<>();

            for (Object[] row : results) {
                Toa t = new Toa();
                t.setToaID((String) row[0]);
                t.setSoToa(row[1] != null ? ((Number) row[1]).intValue() : 0);
                t.setHangToa(new HangToa((String) row[2], (String) row[3]));
                t.setSucChua(row[4] != null ? ((Number) row[4]).intValue() : 0);
                t.setTau(new Tau((String) row[5]));
                // Nếu entity Tau có hàm setTenTau, bạn có thể gọi: t.getTau().setTenTau((String) row[6]);

                list.add(t);
            }
            return list;
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