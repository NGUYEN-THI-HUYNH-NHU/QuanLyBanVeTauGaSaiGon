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
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import jakarta.persistence.Query;

import java.util.List;

public class PhieuDungPhongVIPDAO extends AbstractGenericDAO<PhieuDungPhongVIP, String> implements IPhieuDungPhongVIPDAO {

    public PhieuDungPhongVIPDAO() {
        super(PhieuDungPhongVIP.class);
    }

    @Override
    public PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(String veID) {
        return doInTransaction(em -> {
            String sql = "SELECT p.phieuDungPhongVIPID, p.dichVuPhongChoVIPID, p.veID, p.trangThai "
                    + "FROM PhieuDungPhongVIP p WHERE p.veID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, veID);

            try {
                List<Object[]> rsList = query.getResultList();
                if (rsList != null && !rsList.isEmpty()) {
                    Object[] rs = rsList.get(0);
                    PhieuDungPhongVIP phieu = new PhieuDungPhongVIP();
                    phieu.setPhieuDungPhongVIPID((String) rs[0]);
                    phieu.setDichVuPhongChoVIP(new DichVuPhongChoVIP((String) rs[1]));
                    phieu.setVe(new Ve((String) rs[2]));
                    phieu.setTrangThai(TrangThaiPDPVIP.valueOf((String) rs[3]));
                    return phieu;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public boolean updateTrangThaiPhieuDungPhongVIP(String phieuDungPhongChoVIPID,
                                                    TrangThaiPDPVIP trangThai) {
        return doInTransaction(em -> {
            String sql = "UPDATE PhieuDungPhongVIP SET trangThai = ?1 WHERE phieuDungPhongVIPID = ?2";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, trangThai.toString());
            query.setParameter(2, phieuDungPhongChoVIPID);

            return query.executeUpdate() > 0;
        });
    }
}
