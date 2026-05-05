package dao.impl;
/*
 * @(#) DonDatCho_DAO.java  1.0  [11:14:45 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.IDonDatChoDAO;
import entity.DonDatCho;
import entity.KhachHang;
import entity.NhanVien;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonDatChoDAO extends AbstractGenericDAO<DonDatCho, String> implements IDonDatChoDAO {

    public DonDatChoDAO() {
        super(DonDatCho.class);
    }

    @Override
    public DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        return doInTransaction(em -> {
            String jpql = "SELECT d FROM DonDatCho d "
                    + "JOIN FETCH d.khachHang k "
                    + "LEFT JOIN FETCH k.loaiKhachHang "
                    + "LEFT JOIN FETCH k.loaiDoiTuong "
                    + "LEFT JOIN FETCH d.nhanVien "
                    + "WHERE d.donDatChoID = :id AND k.soGiayTo = :soGiayTo";
            var query = em.createQuery(jpql, DonDatCho.class);
            query.setParameter("id", donDatChoID);
            query.setParameter("soGiayTo", soGiayTo);

            try {
                return query.getSingleResult();
            } catch (Exception e) {
                // e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public List<DonDatCho> searchDonDatChoByKeyword(String keyword, String type, int page, int limit) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            StringBuilder jpql = new StringBuilder("SELECT d, k, nv, "
                    + "COUNT(v.veID), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_HOAN THEN 1 ELSE 0 END), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_DOI THEN 1 ELSE 0 END) "
                    + "FROM DonDatCho d "
                    + "JOIN d.khachHang k "
                    + "JOIN d.nhanVien nv "
                    + "LEFT JOIN Ve v ON d = v.donDatCho "
                    + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    jpql.append(" AND d.donDatChoID LIKE :keyword");
                } else if ("Số giấy tờ".equals(type)) {
                    jpql.append(" AND k.soGiayTo LIKE :keyword");
                } else if ("Số điện thoại".equals(type)) {
                    jpql.append(" AND k.soDienThoai LIKE :keyword");
                } else if ("Tên khách hàng".equals(type)) {
                    jpql.append(" AND k.hoTen LIKE :keyword");
                }
            }

            jpql.append(" GROUP BY d, k, nv ORDER BY d.thoiDiemDatCho DESC");

            var query = em.createQuery(jpql.toString(), Object[].class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = (DonDatCho) row[0];
                    KhachHang k = (KhachHang) row[1];
                    NhanVien nv = (NhanVien) row[2];
                    d.setKhachHang(k);
                    d.setNhanVien(nv);
                    d.setTongSoVe(row[3] == null ? 0 : ((Number) row[3]).intValue());
                    d.setSoVeHoan(row[4] == null ? 0 : ((Number) row[4]).intValue());
                    d.setSoVeDoi(row[5] == null ? 0 : ((Number) row[5]).intValue());
                    list.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    @Override
    public List<DonDatCho> searchDonDatChoByFilter(String tuKhoaTraCuu, String loaiTraCuu, Date tuNgay, Date denNgay, int page, int limit) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            StringBuilder jpql = new StringBuilder("SELECT d, k, nv, "
                    + "COUNT(v.veID), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_HOAN THEN 1 ELSE 0 END), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_DOI THEN 1 ELSE 0 END) "
                    + "FROM DonDatCho d "
                    + "JOIN d.khachHang k "
                    + "JOIN d.nhanVien nv "
                    + "LEFT JOIN Ve v ON d = v.donDatCho "
                    + "WHERE 1=1 ");

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(loaiTraCuu)) {
                    jpql.append(" AND d.donDatChoID LIKE :keyword");
                } else if ("Số giấy tờ".equals(loaiTraCuu)) {
                    jpql.append(" AND k.soGiayTo LIKE :keyword");
                } else if ("Số điện thoại".equals(loaiTraCuu)) {
                    jpql.append(" AND k.soDienThoai LIKE :keyword");
                } else if ("Tên khách hàng".equals(loaiTraCuu)) {
                    jpql.append(" AND k.hoTen LIKE :keyword");
                }
            }

            if (tuNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho >= :tuNgay");
            }
            if (denNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho <= :denNgay");
            }

            jpql.append(" GROUP BY d, k, nv ORDER BY d.thoiDiemDatCho DESC");

            var query = em.createQuery(jpql.toString(), Object[].class);

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                query.setParameter("keyword", "%" + tuKhoaTraCuu.trim() + "%");
            }
            if (tuNgay != null) {
                query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            }
            if (denNgay != null) {
                query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = (DonDatCho) row[0];
                    KhachHang k = (KhachHang) row[1];
                    NhanVien nv = (NhanVien) row[2];
                    d.setKhachHang(k);
                    d.setNhanVien(nv);
                    d.setTongSoVe(row[3] == null ? 0 : ((Number) row[3]).intValue());
                    d.setSoVeHoan(row[4] == null ? 0 : ((Number) row[4]).intValue());
                    d.setSoVeDoi(row[5] == null ? 0 : ((Number) row[5]).intValue());
                    list.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        });
    }

    @Override
    public int countDonDatChoByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(d) FROM DonDatCho d "
                            + "JOIN d.khachHang k "
                            + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    jpql.append(" AND d.donDatChoID LIKE :keyword");
                } else if ("Số giấy tờ".equals(type)) {
                    jpql.append(" AND k.soGiayTo LIKE :keyword");
                } else if ("Số điện thoại".equals(type)) {
                    jpql.append(" AND k.soDienThoai LIKE :keyword");
                } else if ("Tên khách hàng".equals(type)) {
                    jpql.append(" AND k.hoTen LIKE :keyword");
                }
            }

            var query = em.createQuery(jpql.toString(), Number.class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            try {
                return query.getSingleResult().intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public int countDonDatChoByFilter(String keyword, String type, Date tuNgay, Date denNgay) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT COUNT(d) FROM DonDatCho d "
                            + "JOIN d.khachHang k "
                            + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    jpql.append(" AND d.donDatChoID LIKE :keyword");
                } else if ("Số giấy tờ".equals(type)) {
                    jpql.append(" AND k.soGiayTo LIKE :keyword");
                } else if ("Số điện thoại".equals(type)) {
                    jpql.append(" AND k.soDienThoai LIKE :keyword");
                } else if ("Tên khách hàng".equals(type)) {
                    jpql.append(" AND k.hoTen LIKE :keyword");
                }
            }

            if (tuNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho >= :tuNgay");
            }
            if (denNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho <= :denNgay");
            }

            var query = em.createQuery(jpql.toString(), Number.class);

            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }
            if (tuNgay != null) {
                query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            }
            if (denNgay != null) {
                query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            }

            try {
                return query.getSingleResult().intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public int countAll() {
        return doInTransaction(em -> {
            String jpql = "SELECT COUNT(d) FROM DonDatCho d";
            var query = em.createQuery(jpql, Number.class);
            return query.getSingleResult().intValue();
        });
    }

    @Override
    public List<DonDatCho> getDonDatChoByPage(int page, int limit) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            String jpql = "SELECT d, k, nv, "
                    + "COUNT(v.veID), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_HOAN THEN 1 ELSE 0 END), "
                    + "SUM(CASE WHEN v.trangThai = entity.type.TrangThaiVe.DA_DOI THEN 1 ELSE 0 END) "
                    + "FROM DonDatCho d "
                    + "JOIN d.khachHang k "
                    + "JOIN d.nhanVien nv "
                    + "LEFT JOIN Ve v ON d = v.donDatCho "
                    + "GROUP BY d, k, nv "
                    + "ORDER BY d.thoiDiemDatCho DESC";

            var query = em.createQuery(jpql, Object[].class);

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = (DonDatCho) row[0];
                    KhachHang k = (KhachHang) row[1];
                    NhanVien nv = (NhanVien) row[2];
                    d.setKhachHang(k);
                    d.setNhanVien(nv);
                    d.setTongSoVe(row[3] == null ? 0 : ((Number) row[3]).intValue());
                    d.setSoVeHoan(row[4] == null ? 0 : ((Number) row[4]).intValue());
                    d.setSoVeDoi(row[5] == null ? 0 : ((Number) row[5]).intValue());
                    list.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    @Override
    public List<String> getTop10DonDatChoID(String donDatChoID) {
        return doInTransaction(em -> {
            List<String> list = new ArrayList<>();
            String jpql = "SELECT d.donDatChoID FROM DonDatCho d WHERE d.donDatChoID LIKE :id";
            var query = em.createQuery(jpql, String.class);
            query.setParameter("id", "%" + donDatChoID + "%");
            query.setMaxResults(10);
            return query.getResultList();
        });
    }

    // --- Helper Methods xử lý ngày giờ ---

    private Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return localDateTimeToDate(startOfDay);
    }

    private Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return localDateTimeToDate(endOfDay);
    }

    private LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
