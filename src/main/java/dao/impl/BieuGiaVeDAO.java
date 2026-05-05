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
import entity.HangToa;
import entity.LoaiTau;
import entity.Tuyen;
import jakarta.persistence.Query;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BieuGiaVeDAO extends AbstractGenericDAO<BieuGiaVe, String> implements IBieuGiaVeDAO {

    public BieuGiaVeDAO() {
        super(BieuGiaVe.class);
    }

    @Override
    public List<BieuGiaVe> getBieuGiaTheoTieuChi(String tuKhoa, String maTuyen, String loaiTau) {
        return doInTransaction(em -> {
            List<BieuGiaVe> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT bieuGiaVeID, tuyenApDungID, loaiTauApDungID, hangToaApDungID, minKm, maxKm, donGiaTrenKm, giaCoBan, phuPhiCaoDiem, doUuTien, ngayBatDau, ngayKetThuc FROM BieuGiaVe WHERE 1=1");

            int paramIndex = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                sql.append(" AND bieuGiaVeID LIKE ?").append(paramIndex++);
            }
            if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty()) {
                sql.append(" AND tuyenApDungID = ?").append(paramIndex++);
            }
            if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả")) {
                sql.append(" AND loaiTauApDungID = ?").append(paramIndex++);
            }

            sql.append(" ORDER BY doUuTien DESC, ngayBatDau DESC");

            Query query = em.createNativeQuery(sql.toString());

            paramIndex = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                query.setParameter(paramIndex++, "%" + tuKhoa + "%");
            }
            if (maTuyen != null && !maTuyen.equalsIgnoreCase("Tất cả") && !maTuyen.isEmpty()) {
                query.setParameter(paramIndex++, maTuyen);
            }
            if (loaiTau != null && !loaiTau.equalsIgnoreCase("Tất cả")) {
                query.setParameter(paramIndex++, loaiTau);
            }

            try {
                List<Object[]> results = query.getResultList();
                for (Object[] rs : results) {
                    list.add(mapRow(rs));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    private BieuGiaVe mapRow(Object[] rs) {
        BieuGiaVe bg = new BieuGiaVe();
        bg.setBieuGiaVeID((String) rs[0]);
        String tuyenID = (String) rs[1];
        bg.setTuyenApDung(tuyenID != null ? new Tuyen(tuyenID) : null);

        String loaiTauID = (String) rs[2];
        bg.setLoaiTauApDung(loaiTauID != null ? new LoaiTau(loaiTauID) : null);

        String hangToaID = (String) rs[3];
        bg.setHangToaApDung(hangToaID != null ? new HangToa(hangToaID) : null);

        bg.setMinKm(rs[4] != null ? ((Number) rs[4]).intValue() : 0);
        bg.setMaxKm(rs[5] != null ? ((Number) rs[5]).intValue() : 0);
        bg.setDonGiaTrenKm(rs[6] != null ? ((Number) rs[6]).doubleValue() : 0);
        bg.setGiaCoBan(rs[7] != null ? ((Number) rs[7]).doubleValue() : 0);
        bg.setPhuPhiCaoDiem(rs[8] != null ? ((Number) rs[8]).doubleValue() : 0);
        bg.setDoUuTien(rs[9] != null ? ((Number) rs[9]).intValue() : 0);

        if (rs[10] != null) {
            bg.setNgayBatDau(rs[10] instanceof Date ? ((Date) rs[10]).toLocalDate() : LocalDate.parse(rs[10].toString()));
        }
        if (rs[11] != null) {
            bg.setNgayKetThuc(rs[11] instanceof Date ? ((Date) rs[11]).toLocalDate() : LocalDate.parse(rs[11].toString()));
        }

        return bg;
    }
}