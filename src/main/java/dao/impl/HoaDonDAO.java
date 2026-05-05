package dao.impl;
/*
 * @(#) HoaDon_DAO.java  1.0  [11:33:37 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.IHoaDonDAO;
import entity.HoaDon;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HoaDonDAO extends AbstractGenericDAO<HoaDon, String> implements IHoaDonDAO {

    public HoaDonDAO() {
        super(HoaDon.class);
    }

    @Override
    public List<HoaDon> searchHoaDonByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                             Date denNgay, String hinhThucTT, int page, int limit) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT h FROM HoaDon h " +
                    "LEFT JOIN FETCH h.khachHang k WHERE 1=1 ");

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(loaiTraCuu)) {
                    jpql.append(" AND h.hoaDonID LIKE :tuKhoa");
                } else if ("Số điện thoại khách hàng".equals(loaiTraCuu)) {
                    jpql.append(" AND k.soDienThoai LIKE :tuKhoa");
                } else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) {
                    jpql.append(" AND k.soGiayTo LIKE :tuKhoa");
                }
            }

            if (tuNgay != null) jpql.append(" AND h.thoiDiemTao >= :tuNgay");
            if (denNgay != null) jpql.append(" AND h.thoiDiemTao <= :denNgay");


            if (khachHangID != null) jpql.append(" AND h.khachHang.khachHangID = :khachHangID");
            else if (khachHang != null && !khachHang.trim().isEmpty())
                jpql.append(" AND (k.hoTen LIKE :khachHang OR k.soDienThoai LIKE :khachHang OR k.soGiayTo LIKE :khachHang OR k.khachHangID LIKE :khachHang)");


            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả")) jpql.append(" AND h.thanhToanTienMat = :isTienMat");

            if (loaiHD != null && !loaiHD.equals("Tất cả")) {
                if (loaiHD.equalsIgnoreCase("Hóa đơn bán vé")) jpql.append(" AND h.hoaDonID LIKE 'HD-%'");
                else if (loaiHD.equalsIgnoreCase("Hóa đơn hoàn vé")) jpql.append(" AND h.hoaDonID LIKE 'HDHV-%'");
                else if (loaiHD.equalsIgnoreCase("Hóa đơn đổi vé")) jpql.append(" AND h.hoaDonID LIKE 'HDDV-%'");
            }

            jpql.append(" ORDER BY h.thoiDiemTao DESC");

            var query = em.createQuery(jpql.toString(), HoaDon.class);

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty())
                query.setParameter("tuKhoa", "%" + tuKhoaTraCuu.trim() + "%");
            if (tuNgay != null) query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            if (denNgay != null) query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            if (khachHangID != null) query.setParameter("khachHangID", khachHangID);
            else if (khachHang != null && !khachHang.trim().isEmpty())
                query.setParameter("khachHang", "%" + khachHang.trim() + "%");
            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả"))
                query.setParameter("isTienMat", hinhThucTT.equals("Tiền mặt"));

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

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

    @Override
    public List<HoaDon> searchHoaDonByKeyword(String keyword, String type, int page, int limit) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT h FROM HoaDon h " +
                    "LEFT JOIN FETCH h.khachHang k WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(type)) jpql.append(" AND h.hoaDonID LIKE :keyword");
                else if ("Số điện thoại khách hàng".equals(type)) jpql.append(" AND k.soDienThoai LIKE :keyword");
                else if ("Số giấy tờ khách hàng".equals(type)) jpql.append(" AND k.soGiayTo LIKE :keyword");
            }

            jpql.append(" ORDER BY h.thoiDiemTao DESC");

            var query = em.createQuery(jpql.toString(), HoaDon.class);
            if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.trim() + "%");

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            return query.getResultList();
        });
    }

    @Override
    public List<String> getTop10HoaDonID(String keyword) {
        return doInTransaction(em -> em.createQuery("SELECT h.hoaDonID FROM HoaDon h " +
                        "WHERE h.hoaDonID LIKE :keyword", String.class)
                .setParameter("keyword", "%" + keyword + "%")
                .setMaxResults(10)
                .getResultList());
    }

    @Override
    public int countAll() {
        return doInTransaction(em -> {
            var query = em.createQuery("SELECT COUNT(h.hoaDonID) FROM HoaDon h", Long.class);
            return query.getSingleResult().intValue();
        });
    }

    @Override
    public int countByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String khachHangID, Date tuNgay, Date denNgay, String hinhThucTT) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(h.hoaDonID) FROM HoaDon h " +
                    "LEFT JOIN h.khachHang k WHERE 1=1 ");

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(loaiTraCuu)) jpql.append(" AND h.hoaDonID LIKE :tuKhoa");
                else if ("Số điện thoại khách hàng".equals(loaiTraCuu)) jpql.append(" AND k.soDienThoai LIKE :tuKhoa");
                else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) jpql.append(" AND k.soGiayTo LIKE :tuKhoa");
            }

            if (tuNgay != null) jpql.append(" AND h.thoiDiemTao >= :tuNgay");
            if (denNgay != null) jpql.append(" AND h.thoiDiemTao <= :denNgay");

            if (khachHangID != null) jpql.append(" AND h.khachHang.khachHangID = :khachHangID");
            else if (khachHang != null && !khachHang.trim().isEmpty())
                jpql.append(" AND (k.hoTen LIKE :khachHang OR k.soDienThoai LIKE :khachHang OR k.soGiayTo LIKE :khachHang OR k.khachHangID LIKE :khachHang)");

            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả")) jpql.append(" AND h.thanhToanTienMat = :isTienMat");

            if (loaiHD != null && !loaiHD.equals("Tất cả")) {
                if (loaiHD.equalsIgnoreCase("Hóa đơn bán vé")) jpql.append(" AND h.hoaDonID LIKE 'HD-%'");
                else if (loaiHD.equalsIgnoreCase("Hóa đơn hoàn vé")) jpql.append(" AND h.hoaDonID LIKE 'HDHV-%'");
                else if (loaiHD.equalsIgnoreCase("Hóa đơn đổi vé")) jpql.append(" AND h.hoaDonID LIKE 'HDDV-%'");
            }

            var query = em.createQuery(jpql.toString(), Long.class);

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty())
                query.setParameter("tuKhoa", "%" + tuKhoaTraCuu.trim() + "%");
            if (tuNgay != null) query.setParameter("tuNgay", dateToLocalDateTime(atStartOfDay(tuNgay)));
            if (denNgay != null) query.setParameter("denNgay", dateToLocalDateTime(atEndOfDay(denNgay)));
            if (khachHangID != null) query.setParameter("khachHangID", khachHangID);
            else if (khachHang != null && !khachHang.trim().isEmpty())
                query.setParameter("khachHang", "%" + khachHang.trim() + "%");
            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả"))
                query.setParameter("isTienMat", hinhThucTT.equals("Tiền mặt"));

            return query.getSingleResult().intValue();
        });
    }

    @Override
    public int countByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(h.hoaDonID) FROM HoaDon h " +
                    "LEFT JOIN h.khachHang k WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(type)) jpql.append(" AND h.hoaDonID LIKE :keyword");
                else if ("Số điện thoại khách hàng".equals(type)) jpql.append(" AND k.soDienThoai LIKE :keyword");
                else if ("Số giấy tờ khách hàng".equals(type)) jpql.append(" AND k.soGiayTo LIKE :keyword");
            }

            var query = em.createQuery(jpql.toString(), Long.class);
            if (keyword != null && !keyword.trim().isEmpty()) query.setParameter("keyword", "%" + keyword.trim() + "%");

            return query.getSingleResult().intValue();
        });
    }

    @Override
    public List<HoaDon> getByPage(int page, int limit) {
        return doInTransaction(em -> em.createQuery("SELECT h FROM HoaDon h " +
                        "LEFT JOIN FETCH h.khachHang k " +
                        "ORDER BY h.thoiDiemTao DESC", HoaDon.class)
                .setFirstResult((page - 1) * limit)
                .setMaxResults(limit)
                .getResultList());
    }
}
