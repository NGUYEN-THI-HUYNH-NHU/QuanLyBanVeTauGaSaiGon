package dao.impl;
/*
 * @(#) PhieuDungPhongVIP_DAO.java  1.0  [8:57:41 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */

import dao.IPhieuDungPhongVIPDAO;
import entity.PhieuDungPhongVIP;
import entity.type.TrangThaiPDPVIP;
import jakarta.persistence.Query;

public class PhieuDungPhongVIPDAO extends AbstractGenericDAO<PhieuDungPhongVIP, String> implements IPhieuDungPhongVIPDAO {

    public PhieuDungPhongVIPDAO() {
        super(PhieuDungPhongVIP.class);
    }

    @Override
    public PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(String veID) {
        return doInTransaction(em -> {
            String jpql = "SELECT p FROM PhieuDungPhongVIP p WHERE p.ve.veID = :veID";
            try {
                return em.createQuery(jpql, PhieuDungPhongVIP.class)
                        .setParameter("veID", veID)
                        .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public boolean updateTrangThaiPhieuDungPhongVIP(String phieuDungPhongChoVIPID,
                                                    TrangThaiPDPVIP trangThai) {
        return doInTransaction(em -> {
            String jpql = "UPDATE PhieuDungPhongVIP p SET p.trangThai = :trangThai WHERE p.phieuDungPhongVIPID = :id";
            Query query = em.createQuery(jpql);
            query.setParameter("trangThai", trangThai);
            query.setParameter("id", phieuDungPhongChoVIPID);

            return query.executeUpdate() > 0;
        });
    }
}
