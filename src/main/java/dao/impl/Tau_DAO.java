package dao.impl;
/*
 * @(#) Tau_DAO.java  1.0  [4:26:23 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import connectDB.ConnectDB;
import entity.Tau;
import entity.type.TrangThaiTau;
import jakarta.persistence.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Tau_DAO extends AbstractGenericDAO<Tau, String> implements dao.ITauDAO {

    public Tau_DAO() {
        super(Tau.class);
    }

    @Override
    public TrangThaiTau layTrangThaiTau(String tauID) {
        return doInTransaction(em -> {
            String sql = "SELECT trangThai FROM Tau WHERE tauID = ?1";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, tauID);

            List<?> results = query.getResultList();

            if (!results.isEmpty() && results.get(0) != null) {
                String statusStr = (String) results.get(0);
                try {
                    return TrangThaiTau.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
    }

}