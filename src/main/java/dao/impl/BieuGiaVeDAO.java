package dao.impl;
/*
 * @(#) BieuGiaVe_DAO.java 1.0 [11:36:30 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 *
 * @author: NguyenThiHuynhNhu
 *
 * @date: Nov 1, 2025
 *
 * @version: 1.0
 */

import dao.IBieuGiaVeDAO;
import entity.BieuGiaVe;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class BieuGiaVeDAO extends AbstractGenericDAO<BieuGiaVe, String> implements IBieuGiaVeDAO {

    public BieuGiaVeDAO() {
        super(BieuGiaVe.class);
    }

    @Override
    public List<BieuGiaVe> findAll() {
        return doInTransaction(em -> {
            String jpql = "SELECT b FROM BieuGiaVe b " +
                    "LEFT JOIN FETCH b.loaiTauApDung " +
                    "LEFT JOIN FETCH b.hangToaApDung " +
                    "LEFT JOIN FETCH b.tuyenApDung";
            return em.createQuery(jpql, BieuGiaVe.class).getResultList();
        });
    }

    @Override
    public List<BieuGiaVe> getBieuGiaTheoTieuChi(String tuKhoa, String maTuyen, String loaiTau) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT b FROM BieuGiaVe b " +
                    "LEFT JOIN FETCH b.loaiTauApDung " +
                    "LEFT JOIN FETCH b.hangToaApDung " +
                    "LEFT JOIN FETCH b.tuyenApDung " +
                    "WHERE 1=1");

            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) jpql.append(" AND b.bieuGiaVeID LIKE :tuKhoa");
            if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty())
                jpql.append(" AND b.tuyenApDung.tuyenID = :maTuyen");
            if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả"))
                jpql.append(" AND b.loaiTauApDung.loaiTauID = :loaiTau");

            jpql.append(" ORDER BY b.doUuTien DESC, b.ngayBatDau DESC");

            TypedQuery<BieuGiaVe> query = em.createQuery(jpql.toString(), BieuGiaVe.class);

            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) query.setParameter("tuKhoa", "%" + tuKhoa + "%");
            if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty())
                query.setParameter("maTuyen", maTuyen);
            if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả")) query.setParameter("loaiTau", loaiTau);

            return query.getResultList();
        });
    }
}
