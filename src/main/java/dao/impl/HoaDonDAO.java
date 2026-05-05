package dao.impl;
/*
 * @(#) HoaDon_DAO.java  1.0  [11:33:37 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.IHoaDonDAO;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import jakarta.persistence.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonDAO extends AbstractGenericDAO<HoaDon, String> implements IHoaDonDAO {

    public HoaDonDAO() {
        super(HoaDon.class);
    }

    /**
     * @param loaiHD
     * @param khachHang
     * @param tuNgay
     * @param denNgay
     * @param hinhThucTT
     * @return
     */
    @Override
    public List<HoaDon> searchHoaDonByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                             Date denNgay, String hinhThucTT, int page, int limit) {
        return doInTransaction(em -> {
            List<HoaDon> list = new ArrayList<>();

            // 1. Khởi tạo câu truy vấn cơ bản, khai báo rõ các cột để select index chính xác
            StringBuilder sql = new StringBuilder("SELECT " +
                    "hd.hoaDonID, hd.khachHangID, hd.nhanVienID, hd.thoiDiemTao, hd.tongTien, hd.tienNhan, hd.tienHoan, hd.isThanhToanTienMat, hd.maGD, " +
                    "kh.hoTen, kh.soDienThoai, kh.soGiayTo "
                    + "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(loaiTraCuu)) {
                    sql.append(" AND hd.hoaDonID LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số điện thoại khách hàng".equals(loaiTraCuu)) {
                    sql.append(" AND kh.soDienThoai LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) {
                    sql.append(" AND kh.soGiayTo LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                }
            }

            // 2. Xử lý điều kiện NGÀY (Từ ngày ... Đến ngày)
            if (tuNgay != null) {
                sql.append(" AND hd.thoiDiemTao >= ?").append(paramIndex++);
                params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                sql.append(" AND hd.thoiDiemTao <= ?").append(paramIndex++);
                params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            // 3. Xử lý điều kiện KHÁCH HÀNG (Tên, SĐT, CCCD, ID)
            if (khachHangID != null) {
                sql.append(" AND hd.khachHangID = ?").append(paramIndex++);
                params.add(khachHangID);
            } else if (khachHang != null && !khachHang.trim().isEmpty()) {
                sql.append(
                        " AND (kh.hoTen LIKE ?" + paramIndex + " OR kh.soDienThoai LIKE ?" + (paramIndex + 1) + " OR kh.soGiayTo LIKE ?" + (paramIndex + 2) + " OR kh.khachHangID LIKE ?" + (paramIndex + 3) + ")");
                paramIndex += 4;
                String keyword = "%" + khachHang.trim() + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }

            // 4. Xử lý HÌNH THỨC THANH TOÁN
            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả")) {
                sql.append(" AND hd.isThanhToanTienMat = ?").append(paramIndex++);
                params.add(hinhThucTT.equals("Tiền mặt"));
            }

            // 5. Xử lý LOẠI HÓA ĐƠN
            if (loaiHD != null && !loaiHD.equals("Tất cả")) {
                if (loaiHD.equalsIgnoreCase("Hóa đơn bán vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HD-%'");
                } else if (loaiHD.equalsIgnoreCase("Hóa đơn hoàn vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HDHV-%'");
                } else if (loaiHD.equalsIgnoreCase("Hóa đơn đổi vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HDDV-%'");
                }
            }

            sql.append(" ORDER BY hd.thoiDiemTao DESC");

            Query query = em.createNativeQuery(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            // PHÂN TRANG: Cắt dòng dữ liệu theo page và limit
            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    KhachHang kh = new KhachHang();
                    kh.setKhachHangID((String) rs[1]);
                    kh.setHoTen((String) rs[9]);
                    kh.setSoDienThoai((String) rs[10]);
                    kh.setSoGiayTo((String) rs[11]);

                    HoaDon hd = new HoaDon();
                    hd.setHoaDonID((String) rs[0]);
                    hd.setKhachHang(kh);
                    hd.setNhanVien(new NhanVien((String) rs[2]));

                    Timestamp ts = (Timestamp) rs[3];
                    hd.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

                    hd.setTongTien(((Number) rs[4]).doubleValue());
                    hd.setTienNhan(((Number) rs[5]).doubleValue());
                    hd.setTienHoan(((Number) rs[6]).doubleValue());

                    Object isThanhToan = rs[7];
                    if (isThanhToan instanceof Boolean) {
                        hd.setThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hd.setThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
                    }

                    hd.setMaGD((String) rs[8]);

                    list.add(hd);
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
    public List<HoaDon> searchHoaDonByKeyword(String keyword, String type, int page, int limit) {
        return doInTransaction(em -> {
            List<HoaDon> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT " +
                    "hd.hoaDonID, hd.khachHangID, hd.nhanVienID, hd.thoiDiemTao, hd.tongTien, hd.tienNhan, hd.tienHoan, hd.isThanhToanTienMat, hd.maGD, " +
                    "kh.hoTen, kh.soDienThoai, kh.soGiayTo "
                    + "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã hóa đơn")) {
                    sql.append(" AND hd.hoaDonID LIKE ?1");
                } else if (type.equals("Số điện thoại khách hàng")) {
                    sql.append(" AND kh.soDienThoai LIKE ?1");
                } else if (type.equals("Số giấy tờ khách hàng")) {
                    sql.append(" AND kh.soGiayTo LIKE ?1");
                }
            }

            sql.append(" ORDER BY hd.thoiDiemTao DESC");

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    HoaDon hd = new HoaDon();
                    hd.setHoaDonID((String) rs[0]);

                    KhachHang kh = new KhachHang();
                    kh.setKhachHangID((String) rs[1]);
                    kh.setHoTen((String) rs[9]);
                    kh.setSoDienThoai((String) rs[10]);
                    kh.setSoGiayTo((String) rs[11]);
                    hd.setKhachHang(kh);

                    hd.setNhanVien(new NhanVien((String) rs[2]));

                    Timestamp ts = (Timestamp) rs[3];
                    hd.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

                    hd.setTongTien(((Number) rs[4]).doubleValue());
                    hd.setTienNhan(((Number) rs[5]).doubleValue());
                    hd.setTienHoan(((Number) rs[6]).doubleValue());

                    Object isThanhToan = rs[7];
                    if (isThanhToan instanceof Boolean) {
                        hd.setThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hd.setThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
                    }

                    hd.setMaGD((String) rs[8]);

                    list.add(hd);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    // CÁC HÀM HỖ TRỢ SUGGESTION (Auto-complete)
    // Lấy Top 10 Mã Hóa Đơn gần đúng
    @Override
    public List<String> getTop10HoaDonID(String keyword) {
        return doInTransaction(em -> {
            List<String> list = new ArrayList<>();
            String sql = "SELECT TOP 10 hoaDonID FROM HoaDon WHERE hoaDonID LIKE ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, "%" + keyword + "%");

            try {
                List<?> results = query.getResultList();
                for (Object rs : results) {
                    if (rs != null) {
                        list.add((String) rs);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        });
    }

    @Override
    public int countAll() {
        return doInTransaction(em -> {
            String sql = "SELECT COUNT(H.hoaDonID) FROM HoaDon H";
            Query query = em.createNativeQuery(sql);
            return ((Number) query.getSingleResult()).intValue();
        });
    }

    @Override
    public int countByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, String hinhThucTT) {
        return doInTransaction(em -> {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(hd.hoaDonID) "
                            + "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");

            int paramIndex = 1;
            List<Object> params = new ArrayList<>();

            if (tuKhoaTraCuu != null && !tuKhoaTraCuu.trim().isEmpty()) {
                if ("Mã hóa đơn".equals(loaiTraCuu)) {
                    sql.append(" AND hd.hoaDonID LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số điện thoại khách hàng".equals(loaiTraCuu)) {
                    sql.append(" AND kh.soDienThoai LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                } else if ("Số giấy tờ khách hàng".equals(loaiTraCuu)) {
                    sql.append(" AND kh.soGiayTo LIKE ?").append(paramIndex++);
                    params.add("%" + tuKhoaTraCuu.trim() + "%");
                }
            }

            // 2. Xử lý điều kiện NGÀY (Từ ngày ... Đến ngày)
            if (tuNgay != null) {
                sql.append(" AND hd.thoiDiemTao >= ?").append(paramIndex++);
                params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                sql.append(" AND hd.thoiDiemTao <= ?").append(paramIndex++);
                params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            // 3. Xử lý điều kiện KHÁCH HÀNG
            if (soGiayTo != null) {
                sql.append(" AND kh.soGiayTo = ?").append(paramIndex++);
                params.add(soGiayTo);
            } else if (khachHang != null && !khachHang.trim().isEmpty()) {
                sql.append(
                        " AND (kh.hoTen LIKE ?" + paramIndex + " OR kh.soDienThoai LIKE ?" + (paramIndex + 1) + " OR kh.soGiayTo LIKE ?" + (paramIndex + 2) + " OR kh.khachHangID LIKE ?" + (paramIndex + 3) + ")");
                paramIndex += 4;
                String keyword = "%" + khachHang.trim() + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }

            // 4. Xử lý HÌNH THỨC THANH TOÁN
            if (hinhThucTT != null && !hinhThucTT.equals("Tất cả")) {
                sql.append(" AND hd.isThanhToanTienMat = ?").append(paramIndex++);
                params.add(hinhThucTT.equals("Tiền mặt"));
            }

            // 5. Xử lý LOẠI HÓA ĐƠN
            if (loaiHD != null && !loaiHD.equals("Tất cả")) {
                if (loaiHD.equalsIgnoreCase("Hóa đơn bán vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HD-%'");
                } else if (loaiHD.equalsIgnoreCase("Hóa đơn hoàn vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HDHV-%'");
                } else if (loaiHD.equalsIgnoreCase("Hóa đơn đổi vé")) {
                    sql.append(" AND hd.hoaDonID LIKE 'HDDV-%'");
                }
            }

            // ĐÃ XÓA ORDER BY Ở ĐÂY!

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
    public int countByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(H.hoaDonID) "
                            + "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID "
                            + "WHERE 1=1 ");

            boolean hasParam = false;

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã hóa đơn")) {
                    sql.append(" AND H.hoaDonID LIKE ?1");
                } else if (type.equals("Số điện thoại khách hàng")) {
                    sql.append(" AND K.soDienThoai LIKE ?1");
                } else if (type.equals("Số giấy tờ khách hàng")) {
                    sql.append(" AND K.soGiayTo LIKE ?1");
                }
                hasParam = true;
            }

            Query query = em.createNativeQuery(sql.toString());

            if (hasParam) query.setParameter(1, "%" + keyword.trim() + "%");

            try {
                return ((Number) query.getSingleResult()).intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public List<HoaDon> getByPage(int page, int limit) {
        return doInTransaction(em -> {
            String sql = "SELECT H.hoaDonID, H.khachHangID, K.hoTen, K.soGiayTo, K.soDienThoai, H.thoiDiemTao, H.tongTien, H.tienNhan, H.tienHoan, H.isThanhToanTienMat, H.maGD\r\n"
                    + "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID\r\n" + "ORDER BY H.thoiDiemTao DESC";

            Query query = em.createNativeQuery(sql);
            List<HoaDon> dsHoaDon = new ArrayList<>();

            query.setFirstResult((page - 1) * limit);
            query.setMaxResults(limit);

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    HoaDon hoaDon = new HoaDon();
                    hoaDon.setHoaDonID((String) rs[0]);
                    hoaDon.setKhachHang(new KhachHang((String) rs[1], (String) rs[2], (String) rs[3], (String) rs[4]));

                    Timestamp ts = (Timestamp) rs[5];
                    hoaDon.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

                    hoaDon.setTongTien(((Number) rs[6]).doubleValue());
                    hoaDon.setTienNhan(((Number) rs[7]).doubleValue());
                    hoaDon.setTienHoan(((Number) rs[8]).doubleValue());

                    Object isThanhToan = rs[9];
                    if (isThanhToan instanceof Boolean) {
                        hoaDon.setThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hoaDon.setThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
                    }

                    hoaDon.setMaGD((String) rs[10]);

                    dsHoaDon.add(hoaDon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dsHoaDon;
        });
    }
}