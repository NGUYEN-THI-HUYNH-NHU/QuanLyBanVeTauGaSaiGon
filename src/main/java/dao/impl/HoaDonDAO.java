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
     * @param hoaDon
     * @return
     */
    @Override
    public boolean insertHoaDon(HoaDon hoaDon) throws Exception {
        return doInTransaction(em -> {
            String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tongTien, maGD, tienNhan, tienHoan, isThanhToanTienMat) "
                    + "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9)";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, hoaDon.getHoaDonID());
            query.setParameter(2, hoaDon.getKhachHang().getKhachHangID());
            query.setParameter(3, hoaDon.getNhanVien().getNhanVienID());
            query.setParameter(4, Timestamp.valueOf(hoaDon.getThoiDiemTao()));
            query.setParameter(5, hoaDon.getTongTien());

            if (hoaDon.getMaGD() != null) {
                query.setParameter(6, hoaDon.getMaGD());
                query.setParameter(8, 0.0);
            } else {
                query.setParameter(6, null);
                query.setParameter(8, hoaDon.getTienHoan());
            }

            query.setParameter(7, hoaDon.getTienNhan());
            query.setParameter(9, hoaDon.getIsThanhToanTienMat());

            return query.executeUpdate() > 0;
        });
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
    public List<HoaDon> searchHoaDonByFilter(String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                             Date denNgay, String hinhThucTT) {
        return doInTransaction(em -> {
            List<HoaDon> list = new ArrayList<>();

            // 1. Khởi tạo câu truy vấn cơ bản, khai báo rõ các cột để select index chính xác
            StringBuilder sql = new StringBuilder("SELECT hd.hoaDonID, hd.khachHangID, hd.nhanVienID, hd.thoiDiemTao, hd.tongTien, hd.tienNhan, hd.tienHoan, hd.isThanhToanTienMat, hd.maGD, kh.hoTen, kh.soDienThoai, kh.soGiayTo "
                    + "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();
            int paramIndex = 1;

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
                        hd.setIsThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hd.setIsThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
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
    public List<HoaDon> searchHoaDonByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            List<HoaDon> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT hd.hoaDonID, hd.khachHangID, hd.nhanVienID, hd.thoiDiemTao, hd.tongTien, hd.tienNhan, hd.tienHoan, hd.isThanhToanTienMat, hd.maGD, kh.hoTen, kh.soDienThoai, kh.soGiayTo "
                    + "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã hóa đơn")) {
                    sql.append(" AND hd.hoaDonID LIKE ?1");
                } else if (type.equals("Mã khách hàng")) {
                    sql.append(" AND hd.khachHangID LIKE ?1");
                } else if (type.equals("Mã giao dịch")) {
                    sql.append(" AND hd.maGD LIKE ?1");
                }
            }

            sql.append(" ORDER BY hd.thoiDiemTao DESC");

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    HoaDon hd = new HoaDon();
                    hd.setHoaDonID((String) rs[0]);

                    KhachHang kh = new KhachHang();
                    kh.setKhachHangID((String) rs[1]);
                    kh.setHoTen((String) rs[9]);
                    kh.setSoGiayTo((String) rs[11]); // Để hiển thị CCCD
                    hd.setKhachHang(kh);

                    hd.setNhanVien(new NhanVien((String) rs[2]));

                    Timestamp ts = (Timestamp) rs[3];
                    hd.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

                    hd.setTongTien(((Number) rs[4]).doubleValue());
                    hd.setTienNhan(((Number) rs[5]).doubleValue());
                    hd.setTienHoan(((Number) rs[6]).doubleValue());

                    Object isThanhToan = rs[7];
                    if (isThanhToan instanceof Boolean) {
                        hd.setIsThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hd.setIsThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
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
        return getTop10String("hoaDonID", "HoaDon", keyword);
    }

    // Lấy Top 10 Mã Giao Dịch gần đúng
    @Override
    public List<String> getTop10MaGD(String keyword) {
        return getTop10String("maGD", "HoaDon", keyword);
    }

    // Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
    @Override
    public List<String> getTop10KhachHangID(String keyword) {
        return getTop10String("khachHangID", "KhachHang", keyword);
    }

    // Hàm chung để query string
    private List<String> getTop10String(String colName, String tableName, String keyword) {
        return doInTransaction(em -> {
            List<String> list = new ArrayList<>();
            String sql = "SELECT TOP 10 " + colName + " FROM " + tableName + " WHERE " + colName + " LIKE ?1";
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

    /**
     * @return
     */
    @Override
    public List<HoaDon> getAllHoaDon() {
        return doInTransaction(em -> {
            String sql = "SELECT H.hoaDonID, H.khachHangID, K.hoTen, K.soGiayTo, H.thoiDiemTao, H.tongTien, H.tienNhan, H.tienHoan, H.isThanhToanTienMat, H.maGD\r\n"
                    + "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID\r\n" + "ORDER BY H.thoiDiemTao DESC";

            Query query = em.createNativeQuery(sql);
            List<HoaDon> dsHoaDon = new ArrayList<>();
            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    HoaDon hoaDon = new HoaDon();
                    hoaDon.setHoaDonID((String) rs[0]);
                    hoaDon.setKhachHang(new KhachHang((String) rs[1], (String) rs[2], (String) rs[3]));

                    Timestamp ts = (Timestamp) rs[4];
                    hoaDon.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

                    hoaDon.setTongTien(((Number) rs[5]).doubleValue());
                    hoaDon.setTienNhan(((Number) rs[6]).doubleValue());
                    hoaDon.setTienHoan(((Number) rs[7]).doubleValue());

                    Object isThanhToan = rs[8];
                    if (isThanhToan instanceof Boolean) {
                        hoaDon.setIsThanhToanTienMat((Boolean) isThanhToan);
                    } else if (isThanhToan instanceof Number) {
                        hoaDon.setIsThanhToanTienMat(((Number) isThanhToan).intValue() == 1);
                    }

                    hoaDon.setMaGD((String) rs[9]);

                    dsHoaDon.add(hoaDon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dsHoaDon;
        });
    }
}