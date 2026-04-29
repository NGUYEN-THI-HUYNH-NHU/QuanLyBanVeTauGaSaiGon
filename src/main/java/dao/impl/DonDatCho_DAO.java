package dao.impl;
/*
 * @(#) DonDatCho_DAO.java  1.0  [11:14:45 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import connectDB.ConnectDB;
import entity.DonDatCho;
import entity.KhachHang;
import entity.NhanVien;
import gui.application.form.donDatCho.DonDatChoDTO;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonDatCho_DAO {
    private ConnectDB connectDB;

    public DonDatCho_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public boolean deleteDonDatCho(String donDatChoID) {
        String sql = "DELETE FROM DonDatCho WHERE donDatChoID = ?";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, donDatChoID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param donDatChoID
     * @param soGiayTo
     * @return
     */
    public DonDatCho findDonDatChoByIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        String sql = "select d.donDatChoID, d.nhanVienID, d.khachHangID, d.thoiDiemDatCho\r\n"
                + "from DonDatCho d join KhachHang k on d.khachHangID = k.khachHangID\r\n"
                + "where d.donDatChoID = ? and k.soGiayTo = ?";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, donDatChoID);
            pstmt.setString(2, soGiayTo);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                DonDatCho d = new DonDatCho();
                d.setId(resultSet.getString("donDatChoID"));
                d.setNhanVien(new NhanVien(resultSet.getString("nhanVienID")));
                d.setKhachHang(new KhachHang(resultSet.getString("khachHangID")));
                java.sql.Timestamp t1 = resultSet.getTimestamp("thoiDiemDatCho");
                d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());
                return d;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param conn
     * @param donDatCho
     * @return
     */
    public boolean insertDonDatCho(Connection conn, DonDatCho donDatCho) throws Exception {
        String sql = "INSERT INTO DonDatCho (donDatChoID, nhanVienID, khachHangID, thoiDiemDatCho) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, donDatCho.getId());
            pstmt.setString(2, donDatCho.getNhanVien().getId());
            pstmt.setString(3, donDatCho.getKhachHang().getId());
            pstmt.setTimestamp(4, Timestamp.valueOf(donDatCho.getThoiDiemDatCho()));
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<DonDatChoDTO> getListDonDatCho() {
        String sql = "SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, -- Các cột của đơn đặt chỗ\r\n"
                + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai,"
                + "    nv.nhanVienID, nv.hoTen as hoTenNV," + "    COUNT(v.veID) AS tongSoVe,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID -- Dùng LEFT JOIN để lấy cả đơn chưa có vé (nếu có)\r\n"
                + "GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen\r\n"
                + "ORDER BY d.thoiDiemDatCho DESC";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        List<DonDatChoDTO> ds = new ArrayList<DonDatChoDTO>();
        try {
            pstmt = con.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                DonDatCho d = new DonDatCho();
                d.setId(resultSet.getString("donDatChoID"));
                d.setNhanVien(new NhanVien(resultSet.getString("nhanVienID"), resultSet.getString("hoTenNV")));
                d.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTenKH"),
                        resultSet.getString("soGiayTo"), resultSet.getString("soDienThoai")));
                java.sql.Timestamp t1 = resultSet.getTimestamp("thoiDiemDatCho");
                d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                int tongSoVe = resultSet.getInt("tongSoVe");
                int soVeHoan = resultSet.getInt("soVeHoan");
                int soVeDoi = resultSet.getInt("soVeDoi");

                ds.add(new DonDatChoDTO(d, tongSoVe, soVeHoan, soVeDoi));
            }
            return ds;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DonDatChoDTO> searchDonDatChoByKeyword(String keyword, String type) {
        List<DonDatChoDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n" + "    COUNT(v.veID) AS tongSoVe,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n" + "WHERE 1=1");
        Connection conn = connectDB.getConnection();

        // Xử lý tìm kiếm theo Loại
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (type.equals("Mã đặt chỗ")) {
                sql.append(" AND d.donDatChoID LIKE ?");
            } else if (type.equals("Số giấy tờ")) {
                sql.append(" AND k.soGiayTo LIKE ?");
            } else if (type.equals("Số điện thoại")) {
                sql.append(" AND k.soDienThoai LIKE ?");
            } else if (type.equals("Tên khách hàng")) {
                sql.append(" AND k.hoTen LIKE ?");
            }
        }

        sql.append("\r\n GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen \r\n"
                + "ORDER BY d.thoiDiemDatCho DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonDatCho d = new DonDatCho();
                    d.setId(rs.getString("donDatChoID"));
                    d.setNhanVien(new NhanVien(rs.getString("nhanVienID"), rs.getString("hoTenNV")));
                    d.setKhachHang(new KhachHang(rs.getString("khachHangID"), rs.getString("hoTenKH"),
                            rs.getString("soGiayTo"), rs.getString("soDienThoai")));
                    java.sql.Timestamp t1 = rs.getTimestamp("thoiDiemDatCho");
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                    int tongSoVe = rs.getInt("tongSoVe");
                    int soVeHoan = rs.getInt("soVeHoan");
                    int soVeDoi = rs.getInt("soVeDoi");

                    list.add(new DonDatChoDTO(d, tongSoVe, soVeHoan, soVeDoi));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param keyword
     */
    public List<String> getTop10DonDatChoID(String keyword) {
        return getTop10String("donDatChoID", "DonDatCho", keyword);
    }

    /**
     * @param keyword
     */
    public List<String> getTop10SoGiayTo(String keyword) {
        return getTop10String("soGiayTo", "KhachHang", keyword);
    }

    /**
     * @param keyword
     */
    public List<String> getTop10SoDienThoai(String keyword) {
        return getTop10String("soDienThoai", "KhachHang", keyword);
    }

    /**
     * @param keyword
     */
    public List<String> getTop10TenKhachHang(String keyword) {
        return getTop10String("hoTen", "KhachHang", keyword);
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

    /**
     * @param tuNgay
     * @param denNgay
     * @return
     */
    public List<DonDatChoDTO> searchDonDatChoByFilter(Date tuNgay, Date denNgay) {
        List<DonDatChoDTO> list = new ArrayList<>();
        Connection conn = connectDB.getConnection();

        // 1. Khởi tạo câu truy vấn cơ bản (Join bảng để lấy thông tin khách)
        StringBuilder sql = new StringBuilder("SELECT \r\n" + "    d.donDatChoID, d.thoiDiemDatCho, \r\n"
                + "    k.khachHangID, k.hoTen as hoTenKH, k.soGiayTo, k.soDienThoai, \r\n"
                + "    nv.nhanVienID, nv.hoTen as hoTenNV,\r\n" + "    COUNT(v.veID) AS tongSoVe,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_HOAN' THEN 1 ELSE 0 END) AS soVeHoan,\r\n"
                + "    SUM(CASE WHEN v.trangThai = 'DA_DOI' THEN 1 ELSE 0 END) AS soVeDoi\r\n" + "FROM DonDatCho d\r\n"
                + "JOIN KhachHang k ON d.khachHangID = k.khachHangID\r\n"
                + "JOIN NhanVien nv ON d.nhanVienID = nv.nhanVienID\r\n"
                + "LEFT JOIN Ve v ON d.donDatChoID = v.donDatChoID\r\n" + "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // 2. Xử lý điều kiện NGÀY (Từ ngày ... Đến ngày)
        if (tuNgay != null) {
            sql.append(" AND d.thoiDiemDatCho >= ?");
            // Chuyển về đầu ngày (00:00:00)
            params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
        }
        if (denNgay != null) {
            sql.append(" AND d.thoiDiemDatCho <= ?");
            // Chuyển về cuối ngày (23:59:59)
            params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
        }

        sql.append("\r\n GROUP BY \r\n" + "    d.donDatChoID, d.thoiDiemDatCho,\r\n"
                + "    k.khachHangID, k.hoTen, k.soGiayTo, k.soDienThoai,\r\n" + "    nv.nhanVienID, nv.hoTen \r\n"
                + "ORDER BY d.thoiDiemDatCho DESC");

        // 6. Thực thi truy vấn
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Gán tham số
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonDatCho d = new DonDatCho();
                    d.setId(rs.getString("donDatChoID"));
                    d.setNhanVien(new NhanVien(rs.getString("nhanVienID"), rs.getString("hoTenNV")));
                    d.setKhachHang(new KhachHang(rs.getString("khachHangID"), rs.getString("hoTenKH"),
                            rs.getString("soGiayTo"), rs.getString("soDienThoai")));
                    java.sql.Timestamp t1 = rs.getTimestamp("thoiDiemDatCho");
                    d.setThoiDiemDatCho(t1 == null ? null : t1.toLocalDateTime());

                    int tongSoVe = rs.getInt("tongSoVe");
                    int soVeHoan = rs.getInt("soVeHoan");
                    int soVeDoi = rs.getInt("soVeDoi");

                    list.add(new DonDatChoDTO(d, tongSoVe, soVeHoan, soVeDoi));
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
}