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
import jakarta.persistence.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonDatChoDAO extends AbstractGenericDAO<DonDatCho, String> implements IDonDatChoDAO {

    public DonDatChoDAO() {
        super(DonDatCho.class);
    }

    /**
     * @param donDatChoID
     * @param soGiayTo
     * @return
     */
    @Override
    public DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        return doInTransaction(em -> {
            String sql = "select d.donDatChoID, d.nhanVienID, d.khachHangID, d.thoiDiemDatCho\r\n"
                    + "from DonDatCho d join KhachHang k on d.khachHangID = k.khachHangID\r\n"
                    + "where d.donDatChoID = ?1 and k.soGiayTo = ?2";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, donDatChoID);
            query.setParameter(2, soGiayTo);

            try {
                List<Object[]> result = query.getResultList();
                if (result != null && !result.isEmpty()) {
                    Object[] row = result.get(0);
                    DonDatCho d = new DonDatCho();
                    d.setDonDatChoID((String) row[0]);
                    d.setNhanVien(new NhanVien((String) row[1]));
                    d.setKhachHang(new KhachHang((String) row[2]));
                    Timestamp t1 = (Timestamp) row[3];
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
                    return d;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public boolean insertDonDatCho(DonDatCho donDatCho) throws Exception {
        return doInTransaction(em -> {
            String sql = "INSERT INTO DonDatCho (donDatChoID, nhanVienID, khachHangID, thoiDiemDatCho) VALUES (?1, ?2, ?3, ?4)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, donDatCho.getDonDatChoID());
            query.setParameter(2, donDatCho.getNhanVien().getNhanVienID());
            query.setParameter(3, donDatCho.getKhachHang().getKhachHangID());
            query.setParameter(4, Timestamp.valueOf(donDatCho.getThoiDiemDatCho()));
            return query.executeUpdate() > 0;
        });
    }

    @Override
    public List<DonDatCho> getListDonDatCho() {
        return doInTransaction(em -> {
            String sql = "SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai,"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV," + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID \r\n"
                    + "GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen\r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC";

            Query query = em.createNativeQuery(sql);
            List<DonDatCho> ds = new ArrayList<>();
            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = new DonDatCho();
                    d.setDonDatChoID((String) row[0]);
                    Timestamp t1 = (Timestamp) row[1];
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                    d.setKhachHang(new KhachHang((String) row[2], (String) row[3], (String) row[4], (String) row[5]));
                    d.setNhanVien(new NhanVien((String) row[6], (String) row[7]));

                    d.setTongSoVe(((Number) row[8]).intValue());
                    d.setSoVeHoan(((Number) row[9]).intValue());
                    d.setSoVeDoi(((Number) row[10]).intValue());

                    ds.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ds;
        });
    }

    @Override
    public List<DonDatCho> searchDonDatChoByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n" + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n" + "WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã đặt chỗ")) {
                    sql.append(" AND d.donDatChoID LIKE ?1");
                } else if (type.equals("Số giấy tờ")) {
                    sql.append(" AND k.soGiayTo LIKE ?1");
                } else if (type.equals("Số điện thoại")) {
                    sql.append(" AND k.soDienThoai LIKE ?1");
                } else if (type.equals("Tên khách hàng")) {
                    sql.append(" AND k.hoTen LIKE ?1");
                }
            }

            sql.append("\r\n GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen \r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = new DonDatCho();
                    d.setDonDatChoID((String) row[0]);
                    Timestamp t1 = (Timestamp) row[1];
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                    d.setKhachHang(new KhachHang((String) row[2], (String) row[3], (String) row[4], (String) row[5]));
                    d.setNhanVien(new NhanVien((String) row[6], (String) row[7]));

                    d.setTongSoVe(((Number) row[8]).intValue());
                    d.setSoVeHoan(((Number) row[9]).intValue());
                    d.setSoVeDoi(((Number) row[10]).intValue());

                    list.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    @Override
    public List<String> getTop10DonDatChoID(String keyword) {
        return getTop10String("donDatChoID", "DonDatCho", keyword);
    }

    @Override
    public List<String> getTop10SoGiayTo(String keyword) {
        return getTop10String("soGiayTo", "KhachHang", keyword);
    }

    @Override
    public List<String> getTop10SoDienThoai(String keyword) {
        return getTop10String("soDienThoai", "KhachHang", keyword);
    }

    @Override
    public List<String> getTop10TenKhachHang(String keyword) {
        return getTop10String("hoTen", "KhachHang", keyword);
    }

    private List<String> getTop10String(String colName, String tableName, String keyword) {
        return doInTransaction(em -> {
            List<String> list = new ArrayList<>();
            String sql = "SELECT TOP 10 " + colName + " FROM " + tableName + " WHERE " + colName + " LIKE ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, "%" + keyword + "%");
            try {
                List<Object> results = query.getResultList();
                for (Object val : results) {
                    if (val != null) {
                        list.add(val.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    @Override
    public List<DonDatCho> searchDonDatChoByFilter(Date tuNgay, Date denNgay) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n" + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n" + "WHERE 1=1");

            int paramIndex = 1;
            if (tuNgay != null) {
                sql.append(" AND d.thoiDiemDatCho >= ?").append(paramIndex++);
            }
            if (denNgay != null) {
                sql.append(" AND d.thoiDiemDatCho <= ?").append(paramIndex++);
            }

            sql.append("\r\n GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen \r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());

            paramIndex = 1;
            if (tuNgay != null) {
                query.setParameter(paramIndex++, new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                query.setParameter(paramIndex++, new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            try {
                List<Object[]> resultList = query.getResultList();
                for (Object[] row : resultList) {
                    DonDatCho d = new DonDatCho();
                    d.setDonDatChoID((String) row[0]);
                    Timestamp t1 = (Timestamp) row[1];
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                    d.setKhachHang(new KhachHang((String) row[2], (String) row[3], (String) row[4], (String) row[5]));
                    d.setNhanVien(new NhanVien((String) row[6], (String) row[7]));

                    d.setTongSoVe(((Number) row[8]).intValue());
                    d.setSoVeHoan(((Number) row[9]).intValue());
                    d.setSoVeDoi(((Number) row[10]).intValue());

                    list.add(d);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
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

