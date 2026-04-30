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

import entity.HoaDonChiTiet;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.LoaiDichVuEnums;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class HoaDonChiTietDAO extends AbstractGenericDAO<HoaDonChiTiet, String> implements dao.IHoaDonChiTietDAO {

    public HoaDonChiTietDAO() {
        super(HoaDonChiTiet.class);
    }

    @Override
    public boolean insertHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) throws Exception {
        return doInTransaction(em -> {
            String sql = "INSERT INTO HoaDonChiTiet (hoaDonChiTietID, hoaDonID, veID, phieuDungPhongVIPID, tenDichVu, loaiDichVu, donViTinh, soLuong, donGia, thanhTien) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, hoaDonChiTiet.getHoaDonChiTietID());
            query.setParameter(2, hoaDonChiTiet.getHoaDon() != null ? hoaDonChiTiet.getHoaDon().getHoaDonID() : null);

            if (hoaDonChiTiet.getVe() != null && (hoaDonChiTiet.getLoaiDichVu() == LoaiDichVuEnums.VE_BAN
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVuEnums.VE_HOAN
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVuEnums.VE_DOI)) {
                query.setParameter(3, hoaDonChiTiet.getVe().getVeID());
                query.setParameter(4, null);
            } else if (hoaDonChiTiet.getPhieuDungPhongVIP() != null
                    || hoaDonChiTiet.getLoaiDichVu() == LoaiDichVuEnums.PHONG_VIP) {
                query.setParameter(3, null);
                query.setParameter(4, hoaDonChiTiet.getPhieuDungPhongVIP().getPhieuDungPhongVIPID());
            } else {
                query.setParameter(3, null);
                query.setParameter(4, null);
            }

            query.setParameter(5, hoaDonChiTiet.getTenDichVu());
            query.setParameter(6, hoaDonChiTiet.getLoaiDichVu() != null ? hoaDonChiTiet.getLoaiDichVu().toString() : null);
            query.setParameter(7, hoaDonChiTiet.getDonViTinh());
            query.setParameter(8, hoaDonChiTiet.getSoLuong());
            query.setParameter(9, hoaDonChiTiet.getDonGia());
            query.setParameter(10, hoaDonChiTiet.getThanhTien());

            return query.executeUpdate() > 0;
        });
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
