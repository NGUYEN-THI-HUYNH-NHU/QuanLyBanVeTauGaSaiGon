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
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.LoaiDichVuEnums;
import jakarta.persistence.Query;

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
            String sql = "SELECT hoaDonChiTietID, veID, phieuDungPhongVIPID, tenDichVu, loaiDichVu, donViTinh, soLuong, donGia, thanhTien FROM HoaDonChiTiet WHERE hoaDonID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, hoaDonID);

            List<HoaDonChiTiet> listHDCT = new ArrayList<>();
            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    HoaDonChiTiet ct = new HoaDonChiTiet();
                    ct.setHoaDonChiTietID((String) rs[0]);

                    String veID = (String) rs[1];
                    if (veID != null) {
                        ct.setVe(new Ve(veID));
                    }

                    String phongVIPID = (String) rs[2];
                    if (phongVIPID != null) {
                        ct.setPhieuDungPhongVIP(new PhieuDungPhongVIP(phongVIPID));
                    }

                    ct.setTenDichVu((String) rs[3]);

                    String loaiDichVu = (String) rs[4];
                    if (loaiDichVu != null) {
                        ct.setLoaiDichVu(LoaiDichVuEnums.valueOf(loaiDichVu));
                    }

                    ct.setDonViTinh((String) rs[5]);
                    ct.setSoLuong(rs[6] != null ? ((Number) rs[6]).intValue() : 0);
                    ct.setDonGia(rs[7] != null ? ((Number) rs[7]).doubleValue() : 0.0);
                    ct.setThanhTien(rs[8] != null ? ((Number) rs[8]).doubleValue() : 0.0);

                    listHDCT.add(ct);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listHDCT;
        });
    }
}
