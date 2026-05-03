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
    public boolean insertDonDatCho(DonDatCho donDatCho) {
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
    public List<DonDatCho> searchDonDatChoByKeyword(String keyword, String type, int page, int limit) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n"
                    + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n"
                    + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n"
                    + "WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    sql.append(" AND d.donDatChoID LIKE ?1");
                } else if ("Số giấy tờ".equals(type)) {
                    sql.append(" AND k.soGiayTo LIKE ?1");
                } else if ("Số điện thoại".equals(type)) {
                    sql.append(" AND k.soDienThoai LIKE ?1");
                } else if ("Tên khách hàng".equals(type)) {
                    sql.append(" AND k.hoTen LIKE ?1");
                }
            }

            sql.append("\r\n GROUP BY \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n"
                    + "    nv.nhanVienID, nv.hoTen \r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            // Phân trang
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

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
                    d.setSoVeHoan(row[9] != null ? ((Number) row[9]).intValue() : 0);
                    d.setSoVeDoi(row[10] != null ? ((Number) row[10]).intValue() : 0);

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
            StringBuilder sql = new StringBuilder("SELECT \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n"
                    + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n"
                    + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n"
                    + "WHERE 1=1");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            // 1. Thêm điều kiện từ keyword (Tra cứu)
            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(loaiTraCuu)) {
                    sql.append(" AND d.donDatChoID LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số giấy tờ".equals(loaiTraCuu)) {
                    sql.append(" AND k.soGiayTo LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số điện thoại".equals(loaiTraCuu)) {
                    sql.append(" AND k.soDienThoai LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Tên khách hàng".equals(loaiTraCuu)) {
                    sql.append(" AND k.hoTen LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                }
            }

            // 2. Thêm điều kiện từ filter (Ngày tháng)
            if (tuNgay != null) {
                sql.append(" AND d.thoiDiemDatCho >= ?").append(paramIndex++);
                params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                sql.append(" AND d.thoiDiemDatCho <= ?").append(paramIndex++);
                params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            sql.append("\r\n GROUP BY \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n"
                    + "    nv.nhanVienID, nv.hoTen \r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            // Phân trang
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

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
                    d.setSoVeHoan(row[9] != null ? ((Number) row[9]).intValue() : 0);
                    d.setSoVeDoi(row[10] != null ? ((Number) row[10]).intValue() : 0);

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
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(d.donDatChoID) FROM DonDatCho d "
                            + "JOIN KhachHang k ON d.khachHangID = k.khachHangID "
                            + "WHERE 1=1");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    sql.append(" AND d.donDatChoID LIKE ?1");
                } else if ("Số giấy tờ".equals(type)) {
                    sql.append(" AND k.soGiayTo LIKE ?1");
                } else if ("Số điện thoại".equals(type)) {
                    sql.append(" AND k.soDienThoai LIKE ?1");
                } else if ("Tên khách hàng".equals(type)) {
                    sql.append(" AND k.hoTen LIKE ?1");
                }
            }

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            try {
                return ((Number) query.getSingleResult()).intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public int countDonDatChoByFilter(String keyword, String type, Date tuNgay, Date denNgay) {
        return doInTransaction(em -> {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(d.donDatChoID) FROM DonDatCho d "
                            + "JOIN KhachHang k ON d.khachHangID = k.khachHangID "
                            + "WHERE 1=1");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                if ("Mã đặt chỗ".equals(type)) {
                    sql.append(" AND d.donDatChoID LIKE ?").append(paramIndex++);
                    params.add("%" + keyword.trim() + "%");
                } else if ("Số giấy tờ".equals(type)) {
                    sql.append(" AND k.soGiayTo LIKE ?").append(paramIndex++);
                    params.add("%" + keyword.trim() + "%");
                } else if ("Số điện thoại".equals(type)) {
                    sql.append(" AND k.soDienThoai LIKE ?").append(paramIndex++);
                    params.add("%" + keyword.trim() + "%");
                } else if ("Tên khách hàng".equals(type)) {
                    sql.append(" AND k.hoTen LIKE ?").append(paramIndex++);
                    params.add("%" + keyword.trim() + "%");
                }
            }

            if (tuNgay != null) {
                sql.append(" AND d.thoiDiemDatCho >= ?").append(paramIndex++);
                params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                sql.append(" AND d.thoiDiemDatCho <= ?").append(paramIndex++);
                params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            Query query = em.createNativeQuery(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            try {
                return ((Number) query.getSingleResult()).intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
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
    public int countAll() {
        return doInTransaction(em -> {
            String sql = "SELECT COUNT(D.donDatChoID) FROM DonDatCho D";
            Query query = em.createNativeQuery(sql);
            return ((Number) query.getSingleResult()).intValue();
        });
    }

    @Override
    public List<DonDatCho> getDonDatChoByPage(int page, int limit) {
        return doInTransaction(em -> {
            List<DonDatCho> list = new ArrayList<>();
            String sql = "SELECT \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                    + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                    + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n"
                    + "    COUNT(v.veID) AS tongSoVe,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                    + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n"
                    + "FROM DonDatCho d\r\n"
                    + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                    + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                    + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n"
                    + "GROUP BY \r\n"
                    + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                    + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n"
                    + "    nv.nhanVienID, nv.hoTen\r\n"
                    + "ORDER BY d.thoiDiemDatCho DESC";


            Query query = em.createNativeQuery(sql);

            // Phân trang dưới CSDL bằng JPA (Tự động dịch thành OFFSET ... FETCH NEXT)
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

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
}
