/*
 * @(#) KhachHangDAO.java  1.0  [3:35 PM] 5/1/2026
 *
 * Copyright (c) 2026 IUH. All rights reserved.
 */

/*
 * @description
 * @author: Yen
 * @date: 5/1/2026
 * @version: 1.0
 */

package dao.impl;

import dao.IKhachHangDAO;
import entity.KhachHang;

import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO extends AbstractGenericDAO<KhachHang, String> implements IKhachHangDAO {

    public KhachHangDAO() {
        super(KhachHang.class);
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        try {
            create(kh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        try {
            update(kh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean capNhatLoaiKhachHang(KhachHang khachHang) {
        try {
            return doInTransaction(em -> {
                int updated = em.createQuery("UPDATE KhachHang k SET k.loaiKhachHang = :loaiKH WHERE k.khachHangID = :id")
                        .setParameter("loaiKH", khachHang.getLoaiKhachHang())
                        .setParameter("id", khachHang.getKhachHangID())
                        .executeUpdate();
                return updated > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public KhachHang timKhachHangTheoSDT(String sdt) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT kh FROM KhachHang kh " +
                                    "LEFT JOIN FETCH kh.loaiDoiTuong " +
                                    "LEFT JOIN FETCH kh.loaiKhachHang " +
                                    "WHERE kh.soDienThoai = :sdt", KhachHang.class)
                            .setParameter("sdt", sdt)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public KhachHang timKhachHangTheoSoGiayTo(String soGiayTo) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT k FROM KhachHang k " +
                                    "LEFT JOIN FETCH k.loaiDoiTuong " +
                                    "LEFT JOIN FETCH k.loaiKhachHang " +
                                    "WHERE k.soGiayTo = :sgt", KhachHang.class)
                            .setParameter("sgt", soGiayTo)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<KhachHang> getAllKhachHang() {
        return doInTransaction(em ->
                em.createQuery("SELECT kh FROM KhachHang kh " +
                                "LEFT JOIN FETCH kh.loaiDoiTuong " +
                                "LEFT JOIN FETCH kh.loaiKhachHang", KhachHang.class)
                        .getResultList()
        );
    }

    @Override
    public KhachHang timKhachHangTheoID(String khachHangID) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT kh FROM KhachHang kh " +
                                    "LEFT JOIN FETCH kh.loaiDoiTuong " +
                                    "LEFT JOIN FETCH kh.loaiKhachHang " +
                                    "WHERE kh.khachHangID = :id", KhachHang.class)
                            .setParameter("id", khachHangID)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean saveOrUpdate(KhachHang khachHang) {
        if (timKhachHangTheoSoGiayTo(khachHang.getSoGiayTo()) != null) {
            return false;
        }
        return themKhachHang(khachHang);
    }

    @Override
    public List<KhachHang> getTop10KhachHangSuggest(String keyword) {
        try {
            return doInTransaction(em -> {
                String query = "%" + keyword + "%";
                return em.createQuery("SELECT k FROM KhachHang k " +
                                "LEFT JOIN FETCH k.loaiDoiTuong " +
                                "LEFT JOIN FETCH k.loaiKhachHang " +
                                "WHERE k.hoTen LIKE :kw " +
                                "OR k.soDienThoai LIKE :kw " +
                                "OR k.soGiayTo LIKE :kw " +
                                "OR k.khachHangID LIKE :kw", KhachHang.class)
                        .setParameter("kw", query)
                        .setMaxResults(10)
                        .getResultList();
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public String taoMaKhachHangTuDong() {
        return doInTransaction(em -> {
            Long count = em.createQuery("SELECT COUNT(k) FROM KhachHang k", Long.class)
                    .getSingleResult();

            long nextNumber = count + 1;
            String nextID = String.format("KH%05d", nextNumber);

            while (true) {
                Long exists = em.createQuery("SELECT COUNT(k) FROM KhachHang k WHERE k.khachHangID = :id", Long.class)
                        .setParameter("id", nextID)
                        .getSingleResult();

                if (exists == 0) {
                    break;
                }
                nextNumber++;
                nextID = String.format("KH%05d", nextNumber);
            }

            return nextID;
        });
    }

    @Override
    public List<String> getTop10SoGiayTo(String soGiayTo) {
        return getTop10String("soGiayTo", soGiayTo);
    }

    @Override
    public List<String> getTop10SoDienThoai(String soDienThoai) {
        return getTop10String("soDienThoai", soDienThoai);
    }

    @Override
    public List<String> getTop10HoTen(String hoTen) {
        return getTop10String("hoTen", hoTen);
    }

    private List<String> getTop10String(String col, String keyword) {
        return doInTransaction(em -> {
            String jpql = "SELECT k." + col + " FROM KhachHang k WHERE k." + col + " LIKE :keyword";

            try {
                return em.createQuery(jpql, String.class)
                        .setParameter("keyword", "%" + keyword + "%")
                        .setMaxResults(10)
                        .getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }

    // PHÂN TRANG
    @Override
    public List<KhachHang> getKhachHangPhanTrang(int page, int pageSize) {
        return doInTransaction(em -> {
            return em.createQuery("SELECT k FROM KhachHang k " +
                            "LEFT JOIN FETCH k.loaiDoiTuong " +
                            "LEFT JOIN FETCH k.loaiKhachHang", KhachHang.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        });
    }

    @Override
    public long getTotalKhachHang() {
        return doInTransaction(em -> {
            return em.createQuery("SELECT COUNT(k) FROM KhachHang k", Long.class)
                    .getSingleResult();
        });
    }
}