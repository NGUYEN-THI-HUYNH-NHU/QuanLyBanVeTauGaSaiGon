package dao.impl;
/*
 * @(#) Ve_DAO.java  1.0  [11:13:56 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

import dao.IVeDAO;
import entity.Ve;
import entity.type.TrangThaiVe;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class VeDAO extends AbstractGenericDAO<Ve, String> implements IVeDAO {

    public VeDAO() {
        super(Ve.class);
    }

    /**
     * @param donDatChoID
     * @return
     */
    @Override
    public List<Ve> getVeByDonDatChoID(String donDatChoID) {
        List<Ve> list = doInTransaction(em -> {
            String jpql = "SELECT v FROM Ve v LEFT JOIN FETCH v.khachHang LEFT JOIN FETCH v.chuyen LEFT JOIN FETCH v.ghe g LEFT JOIN FETCH g.toa t LEFT JOIN FETCH t.tau LEFT JOIN FETCH v.gaDi LEFT JOIN FETCH v.gaDen WHERE v.donDatCho.donDatChoID = :donDatChoID";
            return em.createQuery(jpql, Ve.class)
                    .setParameter("donDatChoID", donDatChoID)
                    .getResultList();
        });
        fillIsVeDoi(list);
        return list;
    }

    @Override
    public List<String> getVeIDsStartingWith(String baseID) {
        return doInTransaction(em -> {
            String jpql = "SELECT v.veID FROM Ve v WHERE v.veID LIKE :baseID";
            return em.createQuery(jpql, String.class)
                    .setParameter("baseID", baseID + "%")
                    .getResultList();
        });
    }

    /**
     * @param veID
     * @param trangThai
     * @return
     */
    @Override
    public boolean updateTrangThaiVe(String veID, TrangThaiVe trangThai) {
        return doInTransaction(em -> {
            String jpql = "UPDATE Ve v SET v.trangThai = :trangThai WHERE v.veID = :veID";
            return em.createQuery(jpql)
                    .setParameter("trangThai", trangThai)
                    .setParameter("veID", veID)
                    .executeUpdate() > 0;
        });
    }

    @Override
    public List<Ve> searchVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, int page, int limit) {
        List<Ve> list = doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Ve v " +
                    "LEFT JOIN FETCH v.khachHang k " +
                    "LEFT JOIN FETCH v.chuyen " +
                    "LEFT JOIN FETCH v.ghe g " +
                    "LEFT JOIN FETCH g.toa t " +
                    "LEFT JOIN FETCH t.tau " +
                    "LEFT JOIN FETCH v.gaDi " +
                    "LEFT JOIN FETCH v.gaDen " +
                    "JOIN v.donDatCho d WHERE 1=1 ");

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã vé".equals(loaiTraCuu)) jpql.append(" AND v.veID LIKE :tuKhoa");
                else if ("Mã đặt chỗ".equals(loaiTraCuu)) jpql.append(" AND v.donDatCho.donDatChoID LIKE :tuKhoa");
                else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) jpql.append(" AND k.soGiayTo LIKE :tuKhoa");
            }

            if (tuNgay != null) jpql.append(" AND d.thoiDiemDatCho >= :tuNgay");
            if (denNgay != null) jpql.append(" AND d.thoiDiemDatCho <= :denNgay");

            if (soGiayTo != null) jpql.append(" AND k.soGiayTo = :soGiayTo");
            else if (khachHang != null && !khachHang.trim().isEmpty())
                jpql.append(" AND (k.hoTen LIKE :khachHang OR k.soDienThoai LIKE :khachHang OR k.khachHangID LIKE :khachHang OR k.soGiayTo LIKE :khachHang)");

            if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
                if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) jpql.append(" AND v.trangThai = :trangThaiDaBan");
                else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng")) jpql.append(" AND v.trangThai = :trangThaiDaDung");
                else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn")) jpql.append(" AND v.trangThai = :trangThaiDaHoan");
                else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi")) jpql.append(" AND v.trangThai = :trangThaiDaDoi");
            }

            jpql.append(" ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createQuery(jpql.toString(), Ve.class);

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty())
                query.setParameter("tuKhoa", "%" + tuKhoaTraCuu.trim() + "%");
            if (tuNgay != null) query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            if (denNgay != null) query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            if (soGiayTo != null) query.setParameter("soGiayTo", soGiayTo);
            else if (khachHang != null && !khachHang.trim().isEmpty())
                query.setParameter("khachHang", "%" + khachHang.trim() + "%");

            if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
                if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) query.setParameter("trangThaiDaBan", TrangThaiVe.DA_BAN);
                else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng"))
                    query.setParameter("trangThaiDaDung", TrangThaiVe.DA_DUNG);
                else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn"))
                    query.setParameter("trangThaiDaHoan", TrangThaiVe.DA_HOAN);
                else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi"))
                    query.setParameter("trangThaiDaDoi", TrangThaiVe.DA_DOI);
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            return query.getResultList();
        });
        fillIsVeDoi(list);
        return list;
    }

    @Override
    public List<Ve> searchVeByKeyword(String keyword, String type, int page, int limit) {
        List<Ve> list = doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Ve v LEFT JOIN FETCH v.khachHang k LEFT JOIN FETCH v.chuyen LEFT JOIN FETCH v.ghe g LEFT JOIN FETCH g.toa t LEFT JOIN FETCH t.tau LEFT JOIN FETCH v.gaDi LEFT JOIN FETCH v.gaDen JOIN v.donDatCho d WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã vé")) jpql.append(" AND v.veID LIKE :keyword");
                else if (type.equals("Mã đặt chỗ")) jpql.append(" AND v.donDatCho.donDatChoID LIKE :keyword");
                else if (type.equals("Số giấy tờ khách hàng")) jpql.append(" AND k.soGiayTo LIKE :keyword");
            }

            jpql.append(" ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createQuery(jpql.toString(), Ve.class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            return query.getResultList();
        });
        fillIsVeDoi(list);
        return list;
    }

    @Override
    public int countVeByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(v.veID) FROM Ve v LEFT JOIN v.khachHang k WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã vé")) {
                    jpql.append(" AND v.veID LIKE :keyword");
                } else if (type.equals("Mã đặt chỗ")) {
                    jpql.append(" AND v.donDatCho.donDatChoID LIKE :keyword");
                } else if (type.equals("Số giấy tờ khách hàng")) {
                    jpql.append(" AND k.soGiayTo LIKE :keyword");
                }
            }

            Query query = em.createQuery(jpql.toString(), Long.class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim() + "%");
            }

            return ((Long) query.getSingleResult()).intValue();
        });
    }

    @Override
    public int countVeByFilter(String tuKhoaTraCuu, String loaiTraCuu, String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(v.veID) FROM Ve v LEFT JOIN v.khachHang k JOIN v.donDatCho d WHERE 1=1 ");

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã vé".equals(loaiTraCuu)) {
                    jpql.append(" AND v.veID LIKE :tuKhoa");
                } else if ("Mã đặt chỗ".equals(loaiTraCuu)) {
                    jpql.append(" AND d.donDatChoID LIKE :tuKhoa");
                } else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) {
                    jpql.append(" AND k.soGiayTo LIKE :tuKhoa");
                }
            }

            if (tuNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho >= :tuNgay");
            }
            if (denNgay != null) {
                jpql.append(" AND d.thoiDiemDatCho <= :denNgay");
            }

            if (soGiayTo != null) {
                jpql.append(" AND k.soGiayTo = :soGiayTo");
            } else if (khachHang != null && !khachHang.trim().isEmpty()) {
                jpql.append(" AND (k.hoTen LIKE :khachHang OR k.soDienThoai LIKE :khachHang OR k.khachHangID LIKE :khachHang OR k.soGiayTo LIKE :khachHang)");
            }

            if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
                if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) {
                    jpql.append(" AND v.trangThai = :trangThaiDaBan");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng")) {
                    jpql.append(" AND v.trangThai = :trangThaiDaDung");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn")) {
                    jpql.append(" AND v.trangThai = :trangThaiDaHoan");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi")) {
                    jpql.append(" AND v.trangThai = :trangThaiDaDoi");
                }
            }

            Query query = em.createQuery(jpql.toString(), Long.class);

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                query.setParameter("tuKhoa", "%" + tuKhoaTraCuu.trim() + "%");
            }
            if (tuNgay != null) {
                query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            }
            if (denNgay != null) {
                query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            }
            if (soGiayTo != null) {
                query.setParameter("soGiayTo", soGiayTo);
            } else if (khachHang != null && !khachHang.trim().isEmpty()) {
                query.setParameter("khachHang", "%" + khachHang.trim() + "%");
            }

            if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
                if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) {
                    query.setParameter("trangThaiDaBan", TrangThaiVe.DA_BAN);
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng")) {
                    query.setParameter("trangThaiDaDung", TrangThaiVe.DA_DUNG);
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn")) {
                    query.setParameter("trangThaiDaHoan", TrangThaiVe.DA_HOAN);
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi")) {
                    query.setParameter("trangThaiDaDoi", TrangThaiVe.DA_DOI);
                }
            }

            return ((Long) query.getSingleResult()).intValue();
        });
    }

    @Override
    public List<String> getTop10VeID(String veID) {
        return doInTransaction(em -> {
            String jpql = "SELECT v.veID FROM Ve v WHERE v.veID LIKE :veID";
            return em.createQuery(jpql, String.class)
                    .setParameter("veID", "%" + veID + "%")
                    .setMaxResults(10)
                    .getResultList();
        });
    }

    public List<Ve> getVeByPage(int page, int limit) {
        List<Ve> list = doInTransaction(em -> {
            String jpql = "SELECT v FROM Ve v LEFT JOIN FETCH v.khachHang LEFT JOIN FETCH v.chuyen LEFT JOIN FETCH v.ghe g LEFT JOIN FETCH g.toa t LEFT JOIN FETCH t.tau LEFT JOIN FETCH v.gaDi LEFT JOIN FETCH v.gaDen JOIN v.donDatCho d ORDER BY d.thoiDiemDatCho DESC";
            return em.createQuery(jpql, Ve.class)
                    .setFirstResult((page - 1) * limit)
                    .setMaxResults(limit)
                    .getResultList();
        });
        fillIsVeDoi(list);
        return list;
    }

    public int countAllVe() {
        return doInTransaction(em -> {
            String jpql = "SELECT COUNT(v.veID) FROM Ve v";
            return em.createQuery(jpql, Long.class).getSingleResult().intValue();
        });
    }

    private void fillIsVeDoi(List<Ve> veList) {
        if (veList == null || veList.isEmpty()) return;
        List<String> veIDs = veList.stream().map(Ve::getVeID).toList();
        doInTransaction(em -> {
            String jpql = "SELECT gd.veMoi.veID FROM GiaoDichHoanDoi gd WHERE gd.veMoi.veID IN :veIDs";
            List<String> doiIDs = em.createQuery(jpql, String.class)
                    .setParameter("veIDs", veIDs)
                    .getResultList();
            for (Ve v : veList) {
                v.setVeDoi(doiIDs.contains(v.getVeID()));
            }
            return null;
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
