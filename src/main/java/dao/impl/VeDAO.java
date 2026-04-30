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
import entity.*;
import entity.type.TrangThaiVe;
import jakarta.persistence.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
        return doInTransaction(em -> {
            String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                    + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                    + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
                    + "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
                    + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
                    + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE donDatChoID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, donDatChoID);
            List<Ve> dsVe = new ArrayList<Ve>();
            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    Ve ve = new Ve();
                    ve.setVeID((String) rs[0]);
                    ve.setKhachHang(new KhachHang((String) rs[1], (String) rs[11],
                            new LoaiDoiTuong((String) rs[12]), (String) rs[13]));
                    ve.setDonDatCho(new DonDatCho(donDatChoID));
                    ve.setChuyen(new Chuyen((String) rs[2]));
                    ve.setGhe(new Ghe((String) rs[3],
                            new Toa((String) rs[15], new Tau((String) rs[18]),
                                    new HangToa((String) rs[17]), ((Number) rs[16]).intValue()),
                            ((Number) rs[14]).intValue()));
                    ve.setGaDi(new Ga((String) rs[4], (String) rs[5]));
                    ve.setGaDen(new Ga((String) rs[6], (String) rs[7]));
                    Timestamp t = (Timestamp) rs[8];
                    ve.setNgayGioDi(t != null ? t.toLocalDateTime() : null);
                    ve.setGia(((Number) rs[9]).doubleValue());
                    ve.setTrangThai(TrangThaiVe.valueOf((String) rs[10]));
                    ve.setVeDoi(((Number) rs[19]).intValue() == 1);

                    dsVe.add(ve);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dsVe;
        });
    }

    /**
     * @param ve
     * @return
     */
    @Override
    public boolean insertVe(Ve ve) throws Exception {
        return doInTransaction(em -> {
            String sql = "INSERT INTO Ve (veID, khachHangID, donDatChoID, chuyenID, gheID, gaDiID, gaDenID, ngayGioDi, gia, trangThai) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, ve.getVeID());
            query.setParameter(2, ve.getKhachHang().getKhachHangID());
            query.setParameter(3, ve.getDonDatCho().getDonDatChoID());
            query.setParameter(4, ve.getChuyen().getChuyenID());
            query.setParameter(5, ve.getGhe().getGheID());
            query.setParameter(6, ve.getGaDi().getGaID());
            query.setParameter(7, ve.getGaDen().getGaID());
            query.setParameter(8, Timestamp.valueOf(ve.getNgayGioDi()));
            query.setParameter(9, ve.getGia());
            query.setParameter(10, ve.getTrangThai().toString());

            return query.executeUpdate() > 0;
        });
    }

    /**
     * @param veID
     * @return
     */
    @Override
    public Ve getVeByVeID(String veID) {
        return doInTransaction(em -> {
            String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID\r\n"
                    + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID JOIN Ghe g ON V.gheID = G.gheID JOIN TOA T ON G.toaID = T.toaID JOIN Tau TAU ON T.tauID = TAU.tauID JOIN GA Ga1 ON V.gaDiID = Ga1.gaID JOIN GA Ga2 ON V.gaDenID = Ga2.gaID\r\n"
                    + "WHERE veID = ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, veID);
            try {
                List<Object[]> rsList = query.getResultList();
                if (rsList != null && !rsList.isEmpty()) {
                    Object[] rs = rsList.get(0);
                    Ve ve = new Ve();
                    ve.setVeID((String) rs[0]);
                    ve.setKhachHang(new KhachHang((String) rs[1], (String) rs[11],
                            new LoaiDoiTuong((String) rs[12]), (String) rs[13]));
                    ve.setDonDatCho(new DonDatCho());
                    ve.setChuyen(new Chuyen((String) rs[2]));
                    ve.setGhe(new Ghe((String) rs[3],
                            new Toa((String) rs[15], new Tau((String) rs[18]),
                                    new HangToa((String) rs[17]), ((Number) rs[16]).intValue()),
                            ((Number) rs[14]).intValue()));
                    ve.setGaDi(new Ga((String) rs[4], (String) rs[5]));
                    ve.setGaDen(new Ga((String) rs[6], (String) rs[7]));
                    Timestamp t = (Timestamp) rs[8];
                    ve.setNgayGioDi(t != null ? t.toLocalDateTime() : null);
                    ve.setGia(((Number) rs[9]).doubleValue());
                    ve.setTrangThai(TrangThaiVe.valueOf((String) rs[10]));
                    return ve;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public List<String> getVeIDsStartingWith(String baseID) {
        return doInTransaction(em -> {
            List<String> listIDs = new ArrayList<>();
            String sql = "SELECT veID FROM Ve WHERE veID LIKE ?1";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, baseID + "%");
            try {
                List<?> results = query.getResultList();
                for (Object rs : results) {
                    listIDs.add((String) rs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return listIDs;
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
            String sql = "UPDATE Ve SET trangThai = ?1 WHERE veID = ?2";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, trangThai.toString());
            query.setParameter(2, veID);

            return query.executeUpdate() > 0;
        });
    }

    @Override
    public List<Ve> getAllVe() {
        return doInTransaction(em -> {
            String sql = "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                    + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                    + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
                    + "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
                    + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
                    + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                    + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "ORDER BY D.thoiDiemDatCho DESC";
            Query query = em.createNativeQuery(sql);
            List<Ve> dsVe = new ArrayList<Ve>();

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    Ve ve = new Ve();
                    ve.setVeID((String) rs[0]);
                    ve.setKhachHang(new KhachHang((String) rs[3], (String) rs[13],
                            new LoaiDoiTuong((String) rs[14]), (String) rs[15]));
                    ve.setDonDatCho(new DonDatCho((String) rs[1]));
                    ve.setChuyen(new Chuyen((String) rs[4]));
                    ve.setGhe(new Ghe((String) rs[5],
                            new Toa((String) rs[17], new Tau((String) rs[20]),
                                    new HangToa((String) rs[19]), ((Number) rs[18]).intValue()),
                            ((Number) rs[16]).intValue()));
                    ve.setGaDi(new Ga((String) rs[6], (String) rs[7]));
                    ve.setGaDen(new Ga((String) rs[8], (String) rs[9]));
                    Timestamp t = (Timestamp) rs[10];
                    ve.setNgayGioDi(t != null ? t.toLocalDateTime() : null);
                    ve.setGia(((Number) rs[11]).doubleValue());
                    ve.setTrangThai(TrangThaiVe.valueOf((String) rs[12]));
                    ve.setVeDoi(((Number) rs[21]).intValue() == 1);

                    dsVe.add(ve);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dsVe;
        });
    }

    @Override
    public List<Ve> searchVeByFilter(String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay) {
        return doInTransaction(em -> {
            List<Ve> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder(
                    "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.khachHangID, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                            + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                            + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID "
                            + "JOIN Ghe g ON V.gheID = G.gheID " + "JOIN TOA T ON G.toaID = T.toaID "
                            + "JOIN Tau TAU ON T.tauID = TAU.tauID " + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID "
                            + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID " + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                            + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE 1=1 ");

            int paramIndex = 1;
            List<Object> params = new ArrayList<>();

            if (tuNgay != null) {
                sql.append(" AND D.thoiDiemDatCho >= ?").append(paramIndex++);
                params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
            }
            if (denNgay != null) {
                sql.append(" AND D.thoiDiemDatCho <= ?").append(paramIndex++);
                params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
            }

            if (soGiayTo != null) {
                sql.append(" AND K.soGiayTo = ?").append(paramIndex++);
                params.add(soGiayTo);
            } else if (khachHang != null && !khachHang.trim().isEmpty()) {
                sql.append(" AND (K.hoTen LIKE ?").append(paramIndex);
                sql.append(" OR K.soDienThoai LIKE ?").append(paramIndex + 1);
                sql.append(" OR K.khachHangID LIKE ?").append(paramIndex + 2);
                sql.append(" OR K.soGiayTo LIKE ?").append(paramIndex + 3).append(")");
                paramIndex += 4;

                String keyword = "%" + khachHang.trim() + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }

            if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
                if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) {
                    sql.append(" AND V.trangThai = '" + TrangThaiVe.DA_BAN + "'");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng")) {
                    sql.append(" AND V.trangThai = '" + TrangThaiVe.DA_DUNG + "'");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn")) {
                    sql.append(" AND V.trangThai = '" + TrangThaiVe.DA_HOAN + "'");
                } else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi")) {
                    sql.append(" AND V.trangThai = '" + TrangThaiVe.DA_DOI + "'");
                }
            }

            sql.append(" ORDER BY D.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    Ve ve = new Ve();
                    ve.setVeID((String) rs[0]);
                    ve.setKhachHang(new KhachHang((String) rs[13], (String) rs[14],
                            new LoaiDoiTuong((String) rs[15]), (String) rs[16]));
                    ve.setDonDatCho(new DonDatCho((String) rs[1]));
                    ve.setChuyen(new Chuyen((String) rs[4]));
                    ve.setGhe(new Ghe((String) rs[5],
                            new Toa((String) rs[18], new Tau((String) rs[21]),
                                    new HangToa((String) rs[20]), ((Number) rs[19]).intValue()),
                            ((Number) rs[17]).intValue()));
                    ve.setGaDi(new Ga((String) rs[6], (String) rs[7]));
                    ve.setGaDen(new Ga((String) rs[8], (String) rs[9]));
                    Timestamp t = (Timestamp) rs[10];
                    ve.setNgayGioDi(t != null ? t.toLocalDateTime() : null);
                    ve.setGia(((Number) rs[11]).doubleValue());
                    ve.setTrangThai(TrangThaiVe.valueOf((String) rs[12]));
                    ve.setVeDoi(((Number) rs[22]).intValue() == 1);

                    list.add(ve);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        });
    }

    @Override
    public List<Ve> searchVeByKeyword(String keyword, String type) {
        return doInTransaction(em -> {
            List<Ve> list = new ArrayList<>();
            StringBuilder sql = new StringBuilder(
                    "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.khachHangID, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                            + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                            + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID "
                            + "JOIN Ghe g ON V.gheID = G.gheID " + "JOIN TOA T ON G.toaID = T.toaID "
                            + "JOIN Tau TAU ON T.tauID = TAU.tauID " + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID "
                            + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID " + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                            + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE 1=1 ");

            if (keyword != null && !keyword.trim().isEmpty()) {
                if (type.equals("Mã vé")) {
                    sql.append(" AND V.veID LIKE ?1");
                } else if (type.equals("Mã đặt chỗ")) {
                    sql.append(" AND V.donDatChoID LIKE ?1");
                } else if (type.equals("Số giấy tờ khách hàng")) {
                    sql.append(" AND K.soGiayTo LIKE ?1");
                }
            }

            sql.append(" ORDER BY D.thoiDiemDatCho DESC");

            Query query = em.createNativeQuery(sql.toString());
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter(1, "%" + keyword.trim() + "%");
            }

            try {
                List<Object[]> rsList = query.getResultList();
                for (Object[] rs : rsList) {
                    Ve ve = new Ve();
                    ve.setVeID((String) rs[0]);
                    ve.setKhachHang(new KhachHang((String) rs[13], (String) rs[14],
                            new LoaiDoiTuong((String) rs[15]), (String) rs[16]));
                    ve.setDonDatCho(new DonDatCho((String) rs[1]));
                    ve.setChuyen(new Chuyen((String) rs[4]));
                    ve.setGhe(new Ghe((String) rs[5],
                            new Toa((String) rs[18], new Tau((String) rs[21]),
                                    new HangToa((String) rs[20]), ((Number) rs[19]).intValue()),
                            ((Number) rs[17]).intValue()));
                    ve.setGaDi(new Ga((String) rs[6], (String) rs[7]));
                    ve.setGaDen(new Ga((String) rs[8], (String) rs[9]));
                    Timestamp t = (Timestamp) rs[10];
                    ve.setNgayGioDi(t != null ? t.toLocalDateTime() : null);
                    ve.setGia(((Number) rs[11]).doubleValue());
                    ve.setTrangThai(TrangThaiVe.valueOf((String) rs[12]));
                    ve.setVeDoi(((Number) rs[22]).intValue() == 1);

                    list.add(ve);
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
    public List<String> getTop10VeID(String keyword) {
        return getTop10String("veID", "Ve", keyword);
    }

    // Lấy Top 10 Mã Giao Dịch gần đúng
    @Override
    public List<String> getTop10DonDatChoID(String keyword) {
        return getTop10String("donDatChoID", "DonDatCho", keyword);
    }

    // Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
    @Override
    public List<String> getTop10SoGiayToKhachHang(String keyword) {
        return getTop10String("soGiayTo", "KhachHang", keyword);
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

