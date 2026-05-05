package dao.impl;
/*
 * @(#) HoaDonChiTiet_DAO.java 1.0 [11:34:32 AM] Nov 1, 2025
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

import dao.IHoaDonChiTietDAO;
import entity.HoaDonChiTiet;

import java.util.ArrayList;
import java.util.List;

public class HoaDonChiTietDAO extends AbstractGenericDAO<HoaDonChiTiet, String> implements IHoaDonChiTietDAO {

    public HoaDonChiTietDAO() {
        super(HoaDonChiTiet.class);
    }

    /**
     * @param hoaDonID
     * @return
     */
    @Override
    public List<HoaDonChiTiet> getHoaDonChiTietByHoaDonID(String hoaDonID) {
        return doInTransaction(em -> {
            String jpql = "SELECT ct FROM HoaDonChiTiet ct " +
                    "LEFT JOIN FETCH ct.ve " +
                    "LEFT JOIN FETCH ct.phieuDungPhongVIP " +
                    "WHERE ct.hoaDon.hoaDonID = :hoaDonID";
            try {
                return em.createQuery(jpql, HoaDonChiTiet.class)
                        .setParameter("hoaDonID", hoaDonID)
                        .getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }
}
