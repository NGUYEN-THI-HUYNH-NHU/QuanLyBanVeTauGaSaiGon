package dao;

import connectDB.ConnectDB; // Import lớp kết nối của bạn
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho PanelThongKeVe.
 * ĐÃ SỬA LỖI: Cú pháp "WITH" clause.
 */
public class ThongKeVe_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeVe_DAO.class.getName());

    // === TÊN CỘT/BẢNG ĐÃ XÁC NHẬN ===
    private final String TBL_VE = "Ve";
    private final String TBL_CHUYEN = "chuyen";
    private final String TBL_TUYEN = "tuyen";
    private final String TBL_GA = "Ga";

    private final String COL_VE_ID = "veID";
    private final String COL_TRANG_THAI = "trangThai";
    private final String COL_GIA_VE = "gia";
    private final String COL_THOI_DIEM_BAN = "thoiDiemBan";
    private final String COL_VE_CHUYEN_ID = "chuyenID";
    private final String COL_CHUYEN_ID = "chuyenID";
    private final String COL_NGAY_DI = "ngayDi";
    private final String COL_CHUYEN_TUYEN_ID = "tuyenID";
    private final String COL_TUYEN_ID = "tuyenID";
    private final String COL_MO_TA_TUYEN = "moTa";

    private final String COL_GA_ID = "gaID";
    private final String COL_TEN_GA = "tenGa";
    private final String COL_GA_DI_ID = "gaDiID";
    private final String COL_GA_DEN_ID = "gaDenID";

    private final String TT_DA_BAN = "DA_BAN";
    private final String TT_DA_DUNG = "DA_DUNG";
    private final String TT_DA_HOAN = "DA_HOAN";
    private final String TT_DA_DOI = "DA_DOI";

    /**
     * Lớp nội tại để chứa kết quả thống kê chi tiết theo từng mốc thời gian.
     */
    public static class ThongKeVeChiTietItem {
        public String thoiGian;
        public int tongSoVeBan = 0;
        public int tongVeConHieuLuc = 0;
        public int tongVeDaDung = 0;
        public int tongVeDaDoi = 0;
        public int tongVeHoan = 0;
        public double tongTienVe = 0.0;
        public String tuyenDuong; // <<< THÊM MỚI (thay cho tenGaDi, tenGaDen)

        public ThongKeVeChiTietItem(String thoiGian) {
            this.thoiGian = thoiGian;
        }
    }

    // ===========================================
    // HÀM MỚI: Lấy danh sách ga
    // ===========================================

    public List<String> getDanhSachTenGa() throws SQLException {
        List<String> danhSachGa = new ArrayList<>();
        String sql = String.format("SELECT %s FROM %s ORDER BY %s",
                COL_TEN_GA, TBL_GA, COL_TEN_GA);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSachGa.add(rs.getString(COL_TEN_GA));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy danh sách tên ga", e);
            throw e;
        }
        return danhSachGa;
    }


    /**
     * Hàm chính: Lấy dữ liệu thống kê vé chi tiết, nhóm theo thời gian.
     * (Đã sửa lỗi cú pháp 'WITH')
     */
    public Map<String, ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {

        Map<String, ThongKeVeChiTietItem> results = new LinkedHashMap<>();

        // 1. Xác định cách GROUP BY
        String groupByClause, thoiGianSelect;
        switch (loaiThoiGian) {
            case "Theo ngày":
                thoiGianSelect = String.format("FORMAT(v.%s, 'dd/MM/yyyy')", COL_THOI_DIEM_BAN);
                groupByClause = thoiGianSelect;
                break;
            case "Theo tháng":
                thoiGianSelect = String.format("FORMAT(v.%s, 'MM/yyyy')", COL_THOI_DIEM_BAN);
                groupByClause = String.format("YEAR(v.%s), MONTH(v.%s), FORMAT(v.%s, 'MM/yyyy')",
                        COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN);
                break;
            default: // "Theo năm" hoặc "Tất cả"
                thoiGianSelect = String.format("YEAR(v.%s)", COL_THOI_DIEM_BAN);
                groupByClause = String.format("YEAR(v.%s)", COL_THOI_DIEM_BAN);
                break;
        }

        // 2. Xây dựng câu SQL
        // <<< SỬA LỖI: Tách CTE (WITH) ra khỏi câu SELECT chính

        // Phần 1: Câu CTE (Bảng tạm ThongKeNhom)
        String cteSql = String.format(
                "WITH ThongKeNhom AS ( " +
                        "    SELECT " +
                        "        %s AS ThoiGian, " +
                        "        g_di.%s AS TenGaDi, " +
                        "        g_den.%s AS TenGaDen, " +
                        "        COUNT(v.%s) AS TongSoVeBan, " +
                        "        SUM(CASE WHEN v.%s = ? THEN 1 ELSE 0 END) AS TongVeConHieuLuc, " +
                        "        SUM(CASE WHEN v.%s = ? THEN 1 ELSE 0 END) AS TongVeDaDung, " +
                        "        SUM(CASE WHEN v.%s = ? THEN 1 ELSE 0 END) AS TongVeDaDoi, " +
                        "        SUM(CASE WHEN v.%s = ? THEN 1 ELSE 0 END) AS TongVeHoan, " +
                        "        ISNULL(SUM(v.%s), 0) AS TongTienVe " +
                        "    FROM %s v " +
                        "    LEFT JOIN %s c ON v.%s = c.%s " +
                        "    LEFT JOIN %s t ON c.%s = t.%s " +
                        "    LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "    LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "    WHERE (v.%s >= ? AND v.%s < ?) ",

                thoiGianSelect, COL_TEN_GA, COL_TEN_GA, COL_VE_ID, COL_TRANG_THAI, COL_TRANG_THAI,
                COL_TRANG_THAI, COL_TRANG_THAI, COL_GIA_VE, TBL_VE, TBL_CHUYEN, COL_VE_CHUYEN_ID,
                COL_CHUYEN_ID, TBL_TUYEN, COL_CHUYEN_TUYEN_ID, COL_TUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN
        );

        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            cteSql += String.format(" AND g_di.%s = ? AND g_den.%s = ? ", COL_TEN_GA, COL_TEN_GA);
        }

        cteSql += " GROUP BY " + groupByClause + String.format(", g_di.%s, g_den.%s ", COL_TEN_GA, COL_TEN_GA);
        cteSql += ") "; // Đóng CTE

        // Phần 2: Câu SELECT chính, đặt SAU CTE
        String finalSql = cteSql +
                "SELECT ThoiGian, " +
                "  SUM(TongSoVeBan) AS TongSoVeBan, " +
                "  SUM(TongVeConHieuLuc) AS TongVeConHieuLuc, " +
                "  SUM(TongVeDaDung) AS TongVeDaDung, " +
                "  SUM(TongVeDaDoi) AS TongVeDaDoi, " +
                "  SUM(TongVeHoan) AS TongVeHoan, " +
                "  SUM(TongTienVe) AS TongTienVe, " +
                // Nối các tuyến đường tìm thấy, dùng WITHIN GROUP
                "  STRING_AGG(ISNULL(TenGaDi, 'N/A') + ' -> ' + ISNULL(TenGaDen, 'N/A'), ', ') WITHIN GROUP (ORDER BY TenGaDi, TenGaDen) AS TuyenDuong " +
                "FROM ThongKeNhom " +
                "GROUP BY ThoiGian " +
                "ORDER BY ThoiGian";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(finalSql)) { // <<< Dùng finalSql

            int paramIndex = 1;
            // Tham số cho CTE
            pstmt.setString(paramIndex++, TT_DA_BAN);
            pstmt.setString(paramIndex++, TT_DA_DUNG);
            pstmt.setString(paramIndex++, TT_DA_DOI);
            pstmt.setString(paramIndex++, TT_DA_HOAN);
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

            if (loaiTuyen.equals("Theo Ga đi/đến")) {
                pstmt.setString(paramIndex++, tenGaDi);
                pstmt.setString(paramIndex++, tenGaDen);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String thoiGianKey = rs.getString("ThoiGian");
                    ThongKeVeChiTietItem item = new ThongKeVeChiTietItem(thoiGianKey);
                    item.tongSoVeBan = rs.getInt("TongSoVeBan");
                    item.tongVeConHieuLuc = rs.getInt("TongVeConHieuLuc");
                    item.tongVeDaDung = rs.getInt("TongVeDaDung");
                    item.tongVeDaDoi = rs.getInt("TongVeDaDoi");
                    item.tongVeHoan = rs.getInt("TongVeHoan");
                    item.tongTienVe = rs.getDouble("TongTienVe");
                    item.tuyenDuong = rs.getString("TuyenDuong"); // Lấy các tuyến đường

                    results.put(thoiGianKey, item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy thống kê vé chi tiết", e);
            throw e;
        }
        return results;
    }

    // ===========================================
    // Các hàm CARD (cần 1 helper)
    // ===========================================

    private String buildCardSql(String loaiTuyen, String countOrSumSql) {
        String sql = String.format(
                "SELECT %s FROM %s v " +
                        "LEFT JOIN %s c ON v.%s = c.%s " +
                        "LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "WHERE v.%s >= ? AND v.%s < ? ",
                countOrSumSql, TBL_VE,
                TBL_CHUYEN, COL_VE_CHUYEN_ID, COL_CHUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN
        );

        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            sql += String.format(" AND g_di.%s = ? AND g_den.%s = ? ", COL_TEN_GA, COL_TEN_GA);
        }
        return sql;
    }

    private void setCardParameters(PreparedStatement pstmt, LocalDate tuNgay, LocalDate denNgay,
                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                   String... extraParams) throws SQLException {
        int paramIndex = 1;
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            pstmt.setString(paramIndex++, tenGaDi);
            pstmt.setString(paramIndex++, tenGaDen);
        }

        for (String param : extraParams) {
            pstmt.setString(paramIndex++, param);
        }
    }

    public int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, countSql);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, countSql);
        sql += " AND v." + COL_TRANG_THAI + " = ? ";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, TT_DA_BAN);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeDaDungTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, countSql);
        sql += " AND v." + COL_TRANG_THAI + " = ? ";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, TT_DA_DUNG);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeDaDoiTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, countSql);
        sql += " AND v." + COL_TRANG_THAI + " = ? ";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, TT_DA_DOI);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeHoanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, countSql);
        sql += " AND v." + COL_TRANG_THAI + " = ? ";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, TT_DA_HOAN);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTongTienVeTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen) throws SQLException {
        String sumSql = String.format("ISNULL(SUM(v.%s), 0)", COL_GIA_VE);
        String sql = buildCardSql(loaiTuyen, sumSql);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}