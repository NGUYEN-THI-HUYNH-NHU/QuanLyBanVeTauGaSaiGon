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
    public List<BieuGiaVe> getAllBieuGia() {
        return doInTransaction(em -> {
            List<BieuGiaVe> list = new ArrayList<>();
            String sql = "SELECT bieuGiaVeID, tuyenApDungID, loaiTauApDungID, hangToaApDungID, minKm, maxKm, donGiaTrenKm, giaCoBan, phuPhiCaoDiem, doUuTien, ngayBatDau, ngayKetThuc " +
                    "FROM BieuGiaVe ORDER BY doUuTien DESC, ngayBatDau DESC";
            Query query = em.createNativeQuery(sql);
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

    @Override
    public boolean themBieuGia(BieuGiaVe bg) {
        return doInTransaction(em -> {
            String sql = "INSERT INTO BieuGiaVe(bieuGiaVeID, tuyenApDungID, loaiTauApDungID, hangToaApDungID, "
                    + "minKm, maxKm, donGiaTrenKm, giaCoBan, phuPhiCaoDiem, doUuTien, ngayBatDau, ngayKetThuc) "
                    + "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, bg.getBieuGiaVeID());
            query.setParameter(2, bg.getTuyenApDung() != null ? bg.getTuyenApDung().getTuyenID() : null);
            query.setParameter(3, bg.getLoaiTauApDung() != null ? bg.getLoaiTauApDung().getLoaiTauID() : null);
            query.setParameter(4, bg.getHangToaApDung() != null ? bg.getHangToaApDung().getHangToaID() : null);
            query.setParameter(5, bg.getMinKm());
            query.setParameter(6, bg.getMaxKm());

            if (bg.getDonGiaTrenKm() > 0) {
                query.setParameter(7, bg.getDonGiaTrenKm());
                query.setParameter(8, null);
            } else {
                query.setParameter(7, null);
                query.setParameter(8, bg.getGiaCoBan());
            }

            query.setParameter(9, bg.getPhuPhiCaoDiem());
            query.setParameter(10, bg.getDoUuTien());
            query.setParameter(11, bg.getNgayBatDau() != null ? Date.valueOf(bg.getNgayBatDau()) : null);
            query.setParameter(12, bg.getNgayKetThuc() != null ? Date.valueOf(bg.getNgayKetThuc()) : null);

            return query.executeUpdate() > 0;
        });
    }

    @Override
    public boolean capNhatBieuGia(BieuGiaVe bg) {
        return doInTransaction(em -> {
            String sql = "UPDATE BieuGiaVe SET tuyenApDungID=?1, loaiTauApDungID=?2, hangToaApDungID=?3, "
                    + "minKm=?4, maxKm=?5, donGiaTrenKm=?6, giaCoBan=?7, phuPhiCaoDiem=?8, doUuTien=?9, "
                    + "ngayBatDau=?10, ngayKetThuc=?11 WHERE bieuGiaVeID=?12";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, bg.getTuyenApDung() != null ? bg.getTuyenApDung().getTuyenID() : null);
            query.setParameter(2, bg.getLoaiTauApDung() != null ? bg.getLoaiTauApDung().getLoaiTauID() : null);
            query.setParameter(3, bg.getHangToaApDung() != null ? bg.getHangToaApDung().getHangToaID() : null);
            query.setParameter(4, bg.getMinKm());
            query.setParameter(5, bg.getMaxKm());

            if (bg.getDonGiaTrenKm() > 0) {
                query.setParameter(6, bg.getDonGiaTrenKm());
                query.setParameter(7, null);
            } else {
                query.setParameter(6, null);
                query.setParameter(7, bg.getGiaCoBan());
            }

            query.setParameter(8, bg.getPhuPhiCaoDiem());
            query.setParameter(9, bg.getDoUuTien());
            query.setParameter(10, bg.getNgayBatDau() != null ? Date.valueOf(bg.getNgayBatDau()) : null);
            query.setParameter(11, bg.getNgayKetThuc() != null ? Date.valueOf(bg.getNgayKetThuc()) : null);
            query.setParameter(12, bg.getBieuGiaVeID());

            return query.executeUpdate() > 0;
        });
    }

    @Override
    public boolean xoaBieuGia(String id) {
        return doInTransaction(em -> {
            String sql = "DELETE FROM BieuGiaVe WHERE bieuGiaVeID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, id);
            return query.executeUpdate() > 0;
        });
    }

    @Override
    public BieuGiaVe getBieuGiaByID(String id) {
        return doInTransaction(em -> {
            String sql = "SELECT bieuGiaVeID, tuyenApDungID, loaiTauApDungID, hangToaApDungID, minKm, maxKm, donGiaTrenKm, giaCoBan, phuPhiCaoDiem, doUuTien, ngayBatDau, ngayKetThuc " +
                    "FROM BieuGiaVe WHERE bieuGiaVeID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, id);

            try {
                List<Object[]> rsList = query.getResultList();
                if (rsList != null && !rsList.isEmpty()) {
                    return mapRow(rsList.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}