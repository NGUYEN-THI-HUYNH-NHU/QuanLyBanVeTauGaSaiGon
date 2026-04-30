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

import connectDB.ConnectDB;
import entity.*;
import entity.type.TrangThaiVe;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Ve_DAO {
    private ConnectDB connectDB = ConnectDB.getInstance();

    public Ve_DAO() {
        connectDB.connect();
    }

    /**
     * @param donDatChoID
     * @return
     */
    public List<Ve> getVeByDonDatChoID(String donDatChoID) {
        String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
                + "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
                + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
                + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE donDatChoID = ?";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        List<Ve> dsVe = new ArrayList<Ve>();
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, donDatChoID);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Ve ve = new Ve();
                ve.setVeID(resultSet.getString("veID"));
                ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
                        new LoaiDoiTuong(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
                ve.setDonDatCho(new DonDatCho(donDatChoID));
                ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
                ve.setGhe(new Ghe(resultSet.getString("gheID"),
                        new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
                                new HangToa(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
                        resultSet.getInt("soGhe")));
                ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
                ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
                Timestamp t = resultSet.getTimestamp("ngayGioDi");
                ve.setNgayGioDi(t.toLocalDateTime());
                ve.setGia(resultSet.getDouble("gia"));
                ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
                ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

                dsVe.add(ve);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsVe;
    }

    /**
     * @param conn
     * @param ve
     * @return
     */
    public boolean insertVe(Connection conn, Ve ve) throws Exception {
        String sql = "INSERT INTO Ve (veID, khachHangID, donDatChoID, chuyenID, gheID, gaDiID, gaDenID, ngayGioDi, gia, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ve.getVeID());
            ps.setString(2, ve.getKhachHang().getKhachHangID());
            ps.setString(3, ve.getDonDatCho().getDonDatChoID());
            ps.setString(4, ve.getChuyen().getChuyenID());
            ps.setString(5, ve.getGhe().getGheID());
            ps.setString(6, ve.getGaDi().getGaID());
            ps.setString(7, ve.getGaDen().getGaID());
            ps.setTimestamp(8, Timestamp.valueOf(ve.getNgayGioDi()));
            ps.setDouble(9, ve.getGia());
            ps.setString(10, ve.getTrangThai().toString());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * @param conn
     * @param veID
     */
    public boolean updateTrangThaiVe(Connection conn, String veID, TrangThaiVe trangThai) throws Exception {
        String sql = "UPDATE Ve SET trangThai = ? WHERE veID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai.toString());
            ps.setString(2, veID);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * @param veID
     * @return
     */
    public Ve getVeByVeID(String veID) {
        String sql = "SELECT V.veID, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID\r\n"
                + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID JOIN Ghe g ON V.gheID = G.gheID JOIN TOA T ON G.toaID = T.toaID JOIN Tau TAU ON T.tauID = TAU.tauID JOIN GA Ga1 ON V.gaDiID = Ga1.gaID JOIN GA Ga2 ON V.gaDenID = Ga2.gaID\r\n"
                + "WHERE veID = ?";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, veID);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                Ve ve = new Ve();
                ve.setVeID(resultSet.getString("veID"));
                ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
                        new LoaiDoiTuong(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
                ve.setDonDatCho(new DonDatCho());
                ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
                ve.setGhe(new Ghe(resultSet.getString("gheID"),
                        new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
                                new HangToa(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
                        resultSet.getInt("soGhe")));
                ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
                ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
                Timestamp t = resultSet.getTimestamp("ngayGioDi");
                ve.setNgayGioDi(t.toLocalDateTime());
                ve.setGia(resultSet.getDouble("gia"));
                ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
                return ve;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getVeIDsStartingWith(String baseID) {
        List<String> listIDs = new ArrayList<>();
        String sql = "SELECT veID FROM Ve WHERE veID LIKE ?";
        Connection con = ConnectDB.getInstance().getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, baseID + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listIDs.add(rs.getString("veID"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listIDs;
    }

    /**
     * @param veID
     * @param trangThai
     * @return
     */
    public boolean updateTrangThaiVe(String veID, TrangThaiVe trangThai) {
        String sql = "UPDATE Ve SET trangThai = ? WHERE veID = ?";
        Connection conn = connectDB.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThai.toString());
            ps.setString(2, veID);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Ve> getAllVe() {
        String sql = "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID " + "JOIN Ghe g ON V.gheID = G.gheID "
                + "JOIN TOA T ON G.toaID = T.toaID " + "JOIN Tau TAU ON T.tauID = TAU.tauID "
                + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID " + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID "
                + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "ORDER BY D.thoiDiemDatCho DESC";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        List<Ve> dsVe = new ArrayList<Ve>();

        try {
            pstmt = con.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                Ve ve = new Ve();
                ve.setVeID(resultSet.getString("veID"));
                ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
                        new LoaiDoiTuong(resultSet.getString("loaiDoiTuongID")), resultSet.getString("soGiayTo")));
                ve.setDonDatCho(new DonDatCho(resultSet.getString("donDatChoID")));
                ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
                ve.setGhe(new Ghe(resultSet.getString("gheID"),
                        new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
                                new HangToa(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
                        resultSet.getInt("soGhe")));
                ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
                ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
                Timestamp t = resultSet.getTimestamp("ngayGioDi");
                ve.setNgayGioDi(t.toLocalDateTime());
                ve.setGia(resultSet.getDouble("gia"));
                ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
                ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

                dsVe.add(ve);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsVe;
    }

    public List<Ve> searchVeByFilter(String trangThaiVe, String khachHang, String soGiayTo, Date tuNgay, Date denNgay) {
        List<Ve> list = new ArrayList<>();
        Connection conn = connectDB.getConnection();

        // 1. Khởi tạo câu truy vấn cơ bản (Join bảng để lấy thông tin khách)
        StringBuilder sql = new StringBuilder(
                "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.khachHangID, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                        + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                        + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID "
                        + "JOIN Ghe g ON V.gheID = G.gheID " + "JOIN TOA T ON G.toaID = T.toaID "
                        + "JOIN Tau TAU ON T.tauID = TAU.tauID " + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID "
                        + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID " + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                        + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // 2. Xử lý điều kiện NGÀY (Từ ngày ... Đến ngày)
        if (tuNgay != null) {
            sql.append(" AND D.thoiDiemDatCho >= ?");
            // Chuyển về đầu ngày (00:00:00)
            params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
        }
        if (denNgay != null) {
            sql.append(" AND D.thoiDiemDatCho <= ?");
            // Chuyển về cuối ngày (23:59:59)
            params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
        }

        // 3. Xử lý điều kiện KHÁCH HÀNG (Tên, SĐT, CCCD, ID)
        if (soGiayTo != null) {
            // Ưu tiên 1: Nếu có ID chính xác (do chọn từ auto-suggest)
            // Tìm chính xác cực nhanh (Index Scan)
            sql.append(" AND K.soGiayTo = ?");
            params.add(soGiayTo);
        } else if (khachHang != null && !khachHang.trim().isEmpty()) {
            // Ưu tiên 2: Nếu không có ID, tìm theo từ khóa (Table Scan / Like)
            sql.append(" AND (K.hoTen LIKE ? OR K.soDienThoai LIKE ? OR K.khachHangID LIKE ? OR K.soGiayTo LIKE ?)");
            String keyword = "%" + khachHang.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        // 5. Xử lý loại vé (trạng thái)
        if (trangThaiVe != null && !trangThaiVe.equals("Tất cả")) {
            if (trangThaiVe.equalsIgnoreCase("Vé đã bán")) {
                sql.append(" AND V.trangThai = " + String.format("'%s'", TrangThaiVe.DA_BAN));
            } else if (trangThaiVe.equalsIgnoreCase("Vé đã dùng")) {
                sql.append(" AND V.trangThai = " + String.format("'%s'", TrangThaiVe.DA_DUNG));
            } else if (trangThaiVe.equalsIgnoreCase("Vé đã hoàn")) {
                sql.append(" AND V.trangThai = " + String.format("'%s'", TrangThaiVe.DA_HOAN));
            } else if (trangThaiVe.equalsIgnoreCase("Vé đã đổi")) {
                sql.append(" AND V.trangThai = " + String.format("'%s'", TrangThaiVe.DA_DOI));
            }
        }

        // Sắp xếp giảm dần theo ngày tạo (Mới nhất lên đầu)
        sql.append(" ORDER BY D.thoiDiemDatCho DESC");

        // 6. Thực thi truy vấn
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Gán tham số
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Ve ve = new Ve();
                    ve.setVeID(resultSet.getString("veID"));
                    ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
                            new LoaiDoiTuong(resultSet.getString("loaiDoiTuongID")),
                            resultSet.getString("soGiayTo")));
                    ve.setDonDatCho(new DonDatCho(resultSet.getString("donDatChoID")));
                    ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
                    ve.setGhe(new Ghe(resultSet.getString("gheID"),
                            new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
                                    new HangToa(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
                            resultSet.getInt("soGhe")));
                    ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
                    ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
                    Timestamp t = resultSet.getTimestamp("ngayGioDi");
                    ve.setNgayGioDi(t.toLocalDateTime());
                    ve.setGia(resultSet.getDouble("gia"));
                    ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
                    ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

                    list.add(ve);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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

    /**
     * @param keyword
     * @param type
     * @return
     */
    public List<Ve> searchVeByKeyword(String keyword, String type) {
        List<Ve> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT V.veID, V.donDatChoID, D.thoiDiemDatCho, V.khachHangID, V.chuyenID, V.gheID, V.gaDiID, Ga1.tenGa AS tenGaDi, V.gaDenID, Ga2.tenGa AS tenGaDen, V.ngayGioDi, V.gia, V.trangThai, K.khachHangID, K.hoTen, K.loaiDoiTuongID, K.soGiayTo, G.soGhe, T.toaID, T.soToa, T.hangToaID, TAU.tauID,\r\n"
                        + "CASE WHEN GD.veMoiID IS NOT NULL THEN 1 ELSE 0 END AS isVeDoi "
                        + "FROM Ve V JOIN KhachHang K ON V.khachHangID = K.khachHangID "
                        + "JOIN Ghe g ON V.gheID = G.gheID " + "JOIN TOA T ON G.toaID = T.toaID "
                        + "JOIN Tau TAU ON T.tauID = TAU.tauID " + "JOIN GA Ga1 ON V.gaDiID = Ga1.gaID "
                        + "JOIN GA Ga2 ON V.gaDenID = Ga2.gaID " + "JOIN DonDatCho D ON V.donDatChoID = D.donDatChoID "
                        + "LEFT JOIN GiaoDichHoanDoi GD ON V.veID = GD.veMoiID \r\n" + "WHERE 1=1 ");
        Connection conn = connectDB.getConnection();

        // Xử lý tìm kiếm theo Loại
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (type.equals("Mã vé")) {
                sql.append(" AND V.veID LIKE ?");
            } else if (type.equals("Mã đặt chỗ")) {
                sql.append(" AND V.donDatChoID LIKE ?");
            } else if (type.equals("Số giấy tờ khách hàng")) {
                sql.append(" AND K.soGiayTo LIKE ?");
            }
        }

        sql.append(" ORDER BY D.thoiDiemDatCho DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword.trim() + "%");
            }

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    Ve ve = new Ve();
                    ve.setVeID(resultSet.getString("veID"));
                    ve.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
                            new LoaiDoiTuong(resultSet.getString("loaiDoiTuongID")),
                            resultSet.getString("soGiayTo")));
                    ve.setDonDatCho(new DonDatCho(resultSet.getString("donDatChoID")));
                    ve.setChuyen(new Chuyen(resultSet.getString("chuyenID")));
                    ve.setGhe(new Ghe(resultSet.getString("gheID"),
                            new Toa(resultSet.getString("toaID"), new Tau(resultSet.getString("tauID")),
                                    new HangToa(resultSet.getString("hangToaID")), resultSet.getInt("soToa")),
                            resultSet.getInt("soGhe")));
                    ve.setGaDi(new Ga(resultSet.getString("gaDiID"), resultSet.getString("tenGaDi")));
                    ve.setGaDen(new Ga(resultSet.getString("gaDenID"), resultSet.getString("tenGaDen")));
                    Timestamp t = resultSet.getTimestamp("ngayGioDi");
                    ve.setNgayGioDi(t.toLocalDateTime());
                    ve.setGia(resultSet.getDouble("gia"));
                    ve.setTrangThai(TrangThaiVe.valueOf(resultSet.getString("trangThai")));
                    ve.setVeDoi(resultSet.getBoolean("isVeDoi"));

                    list.add(ve);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // CÁC HÀM HỖ TRỢ SUGGESTION (Auto-complete)
    // Lấy Top 10 Mã Hóa Đơn gần đúng
    public List<String> getTop10VeID(String keyword) {
        return getTop10String("veID", "Ve", keyword);
    }

    // Lấy Top 10 Mã Giao Dịch gần đúng
    public List<String> getTop10DonDatChoID(String keyword) {
        return getTop10String("donDatChoID", "DonDatCho", keyword);
    }

    // Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
    public List<String> getTop10SoGiayToKhachHang(String keyword) {
        return getTop10String("soGiayTo", "KhachHang", keyword);
    }

    // Hàm chung để query string
    private List<String> getTop10String(String colName, String tableName, String keyword) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT TOP 10 " + colName + " FROM " + tableName + " WHERE " + colName + " LIKE ?";
        Connection conn = connectDB.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String val = rs.getString(1);
                    if (val != null) {
                        list.add(val);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}