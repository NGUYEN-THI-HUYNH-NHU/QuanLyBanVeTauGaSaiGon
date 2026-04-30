package dao.impl;

import connectDB.ConnectDB;

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
 * DAO xử lý thống kê vé.
 * Cập nhật: Lấy dữ liệu thời gian từ DonDatCho (thoiDiemDatCho) thay vì Ve (thoiDiemBan).
 */
public class ThongKeVe_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeVe_DAO.class.getName());

    // === TÊN CỘT/BẢNG ===
    private final String TBL_VE = "Ve";
    private final String TBL_CHUYEN = "chuyen";
    private final String TBL_GA = "Ga";
    private final String TBL_HOA_DON_CHI_TIET = "HoaDonChiTiet";
    private final String TBL_HOA_DON = "HoaDon";
    private final String TBL_NHAN_VIEN = "NhanVien";
    private final String TBL_GHE = "Ghe";
    private final String TBL_TOA = "Toa";
    private final String TBL_HANG_TOA = "HangToa";

    // Bảng Đơn Đặt Chỗ (Mới)
    private final String TBL_DON_DAT_CHO = "DonDatCho";
    private final String COL_DDC_ID = "donDatChoID";
    private final String COL_DDC_THOI_DIEM = "thoiDiemDatCho";

    // Cột Ve
    private final String COL_VE_ID = "veID";
    private final String COL_TRANG_THAI = "trangThai";
    private final String COL_GIA_VE = "gia";
    private final String COL_VE_DDC_ID = "donDatChoID";
    private final String COL_VE_CHUYEN_ID = "chuyenID";
    private final String COL_GA_DI_ID = "gaDiID";
    private final String COL_GA_DEN_ID = "gaDenID";
    private final String COL_VE_GHE_ID = "gheID";

    // Cột Chuyen
    private final String COL_CHUYEN_ID = "chuyenID";

    // Cột Ga
    private final String COL_GA_ID = "gaID";
    private final String COL_TEN_GA = "tenGa";

    // Cột HoaDonChiTiet & HoaDon
    private final String COL_HDCT_VE_ID = "veID";
    private final String COL_HDCT_HOA_DON_ID = "hoaDonID";
    private final String COL_HD_HOA_DON_ID = "hoaDonID";
    private final String COL_HD_NHAN_VIEN_ID = "nhanVienID";

    // Cột NhanVien
    private final String COL_NV_ID = "nhanVienID";
    private final String COL_NV_HO_TEN = "hoTen";

    // Cột Ghe, Toa, HangToa
    private final String COL_GHE_ID = "gheID";
    private final String COL_GHE_TOA_ID = "toaID";
    private final String COL_TOA_ID = "toaID";
    private final String COL_TOA_HANG_TOA_ID = "hangToaID";
    private final String COL_HANG_TOA_ID = "hangToaID";
    private final String COL_HANG_TOA_MO_TA = "moTa";

    // Trạng thái vé (Constants)
    private final String TT_DA_BAN = "DA_BAN";
    private final String TT_DA_DUNG = "DA_DUNG";
    private final String TT_DA_HOAN = "DA_HOAN";
    private final String TT_DA_DOI = "DA_DOI";

    public List<String> getDanhSachTenGa() throws SQLException {
        List<String> danhSachGa = new ArrayList<>();
        String sql = String.format("SELECT %s FROM %s ORDER BY %s", COL_TEN_GA, TBL_GA, COL_TEN_GA);
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) danhSachGa.add(rs.getString(COL_TEN_GA));
        }
        return danhSachGa;
    }

    // ===========================================
    // 1. CÁC HÀM HỖ TRỢ LẤY DỮ LIỆU COMBOBOX
    // ===========================================

    public Map<String, String> getDanhSachLoaiVe() throws SQLException {
        Map<String, String> danhSach = new LinkedHashMap<>();
        String sql = String.format("SELECT %s, %s FROM %s WHERE %s IS NOT NULL ORDER BY %s",
                COL_HANG_TOA_ID, COL_HANG_TOA_MO_TA, TBL_HANG_TOA, COL_HANG_TOA_ID, COL_HANG_TOA_ID);
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                danhSach.put(rs.getString(COL_HANG_TOA_ID), rs.getString(COL_HANG_TOA_MO_TA));
            }
        }
        return danhSach;
    }

    public Map<String, String> getDanhSachNhanVien() throws SQLException {
        Map<String, String> danhSach = new LinkedHashMap<>();
        String sql = String.format("SELECT %s, %s FROM %s ORDER BY %s",
                COL_NV_ID, COL_NV_HO_TEN, TBL_NHAN_VIEN, COL_NV_HO_TEN);
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                danhSach.put(rs.getString(COL_NV_ID), rs.getString(COL_NV_HO_TEN));
            }
        }
        return danhSach;
    }

    public Map<String, ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, String hangToaID, String trangThai) throws SQLException {

        Map<String, ThongKeVeChiTietItem> results = new LinkedHashMap<>();

        // Xử lý GROUP BY theo thời gian (Ngày/Tháng/Năm)
        String groupByClause, thoiGianSelect;
        switch (loaiThoiGian) {
            case "Theo ngày":
                thoiGianSelect = String.format("CONVERT(VARCHAR(10), ddc.%s, 103)", COL_DDC_THOI_DIEM); // dd/MM/yyyy
                groupByClause = thoiGianSelect;
                break;
            case "Theo tháng":
                thoiGianSelect = String.format(
                        "RIGHT('0' + CAST(MONTH(ddc.%s) AS VARCHAR(2)), 2) + '/' + CAST(YEAR(ddc.%s) AS VARCHAR(4))",
                        COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM);
                groupByClause = String.format("YEAR(ddc.%s), MONTH(ddc.%s)", COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM);
                break;
            default: // "Theo năm"
                thoiGianSelect = String.format("CAST(YEAR(ddc.%s) AS VARCHAR(4))", COL_DDC_THOI_DIEM);
                groupByClause = String.format("YEAR(ddc.%s)", COL_DDC_THOI_DIEM);
                break;
        }

        // Tạo câu truy vấn CTE (Common Table Expression)
        StringBuilder cteSql = new StringBuilder();
        cteSql.append("WITH ThongKeNhom AS ( ")
                .append("    SELECT DISTINCT v.").append(COL_VE_ID).append(", ")
                .append("        ").append(thoiGianSelect).append(" AS ThoiGian, ")
                .append("        g_di.").append(COL_TEN_GA).append(" AS TenGaDi, ")
                .append("        g_den.").append(COL_TEN_GA).append(" AS TenGaDen, ")
                .append("        CASE WHEN v.").append(COL_TRANG_THAI).append(" = ? THEN 1 ELSE 0 END AS IsVeConHieuLuc, ")
                .append("        CASE WHEN v.").append(COL_TRANG_THAI).append(" = ? THEN 1 ELSE 0 END AS IsVeDaDung, ")
                .append("        CASE WHEN v.").append(COL_TRANG_THAI).append(" = ? THEN 1 ELSE 0 END AS IsVeDaDoi, ")
                .append("        CASE WHEN v.").append(COL_TRANG_THAI).append(" = ? THEN 1 ELSE 0 END AS IsVeHoan, ")
                .append("        v.").append(COL_GIA_VE).append(" AS GiaVe ")
                .append("    FROM ").append(TBL_VE).append(" v ")
                .append("    LEFT JOIN ").append(TBL_DON_DAT_CHO).append(" ddc ON v.").append(COL_VE_DDC_ID).append(" = ddc.").append(COL_DDC_ID)
                .append("    LEFT JOIN ").append(TBL_CHUYEN).append(" c ON v.").append(COL_VE_CHUYEN_ID).append(" = c.").append(COL_CHUYEN_ID)
                .append("    LEFT JOIN ").append(TBL_GA).append(" g_di ON v.").append(COL_GA_DI_ID).append(" = g_di.").append(COL_GA_ID)
                .append("    LEFT JOIN ").append(TBL_GA).append(" g_den ON v.").append(COL_GA_DEN_ID).append(" = g_den.").append(COL_GA_ID)
                .append("    LEFT JOIN ").append(TBL_HOA_DON_CHI_TIET).append(" hdct ON v.").append(COL_VE_ID).append(" = hdct.").append(COL_HDCT_VE_ID)
                .append("    LEFT JOIN ").append(TBL_HOA_DON).append(" hd ON hdct.").append(COL_HDCT_HOA_DON_ID).append(" = hd.").append(COL_HD_HOA_DON_ID)
                .append("    LEFT JOIN ").append(TBL_GHE).append(" g ON v.").append(COL_VE_GHE_ID).append(" = g.").append(COL_GHE_ID)
                .append("    LEFT JOIN ").append(TBL_TOA).append(" toa ON g.").append(COL_GHE_TOA_ID).append(" = toa.").append(COL_TOA_ID)
                .append("    WHERE (ddc.").append(COL_DDC_THOI_DIEM).append(" >= ? AND ddc.").append(COL_DDC_THOI_DIEM).append(" < ?) ");

        // Các điều kiện lọc động trong CTE
        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            cteSql.append(" AND g_di.").append(COL_TEN_GA).append(" = ? AND g_den.").append(COL_TEN_GA).append(" = ? ");
        }
        if (nhanVienID != null) {
            cteSql.append(" AND hd.").append(COL_HD_NHAN_VIEN_ID).append(" = ? ");
        }
        if (hangToaID != null) {
            cteSql.append(" AND toa.").append(COL_TOA_HANG_TOA_ID).append(" = ? ");
        }
        if (trangThai != null) {
            cteSql.append(" AND v.").append(COL_TRANG_THAI).append(" = ? ");
        }
        cteSql.append(") ");

        // Query chính: Group by và Aggregate từ CTE
        String finalSql = cteSql.toString() +
                "SELECT ThoiGian, " +
                "  COUNT(veID) AS TongSoVeBan, " +
                "  SUM(IsVeConHieuLuc) AS TongVeConHieuLuc, " +
                "  SUM(IsVeDaDung) AS TongVeDaDung, " +
                "  SUM(IsVeDaDoi) AS TongVeDaDoi, " +
                "  SUM(IsVeHoan) AS TongVeHoan, " +
                "  SUM(GiaVe) AS TongTienVe, " +
                "  CASE WHEN ? = N'Theo Ga đi/đến' " +
// Ép kiểu sang NVARCHAR(MAX) để tránh lỗi giới hạn 8000 ký tự
                "       THEN STRING_AGG(CAST(ISNULL(TenGaDi, 'N/A') + ' -> ' + ISNULL(TenGaDen, 'N/A') AS NVARCHAR(MAX)), ', ') WITHIN GROUP (ORDER BY TenGaDi, TenGaDen) " + "       ELSE N'N/A' " +
                "  END AS TuyenDuong " +
                "FROM ThongKeNhom " +
                "WHERE ThoiGian IS NOT NULL " +
                "GROUP BY ThoiGian " +
                "ORDER BY ThoiGian";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(finalSql)) {

            int paramIndex = 1;

            // Set params cho CTE
            pstmt.setString(paramIndex++, TT_DA_BAN);
            pstmt.setString(paramIndex++, TT_DA_DUNG);
            pstmt.setString(paramIndex++, TT_DA_DOI);
            pstmt.setString(paramIndex++, TT_DA_HOAN);

            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

            if ("Theo Ga đi/đến".equals(loaiTuyen)) {
                pstmt.setString(paramIndex++, tenGaDi);
                pstmt.setString(paramIndex++, tenGaDen);
            }
            if (nhanVienID != null) {
                pstmt.setString(paramIndex++, nhanVienID);
            }
            if (hangToaID != null) {
                pstmt.setString(paramIndex++, hangToaID);
            }
            if (trangThai != null) {
                pstmt.setString(paramIndex++, trangThai);
            }

            // Set param cho Query chính (biến loaiTuyen)
            pstmt.setString(paramIndex++, loaiTuyen);

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
                    item.tuyenDuong = rs.getString("TuyenDuong");
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
    // 2. HÀM THỐNG KÊ CHI TIẾT (BIỂU ĐỒ/BẢNG)
    // ===========================================

    // Helper: Xây dựng câu SQL chung cho các Card
    private String buildCardSql(String loaiTuyen, String nhanVienID, String hangToaID, String trangThai, String countOrSumSql) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ").append(countOrSumSql).append(" FROM (SELECT DISTINCT v.").append(COL_VE_ID)
                .append(", v.").append(COL_GIA_VE).append(", v.").append(COL_TRANG_THAI)
                .append(" FROM ").append(TBL_VE).append(" v ")
                .append("    LEFT JOIN ").append(TBL_DON_DAT_CHO).append(" ddc ON v.").append(COL_VE_DDC_ID).append(" = ddc.").append(COL_DDC_ID)
                .append("    LEFT JOIN ").append(TBL_CHUYEN).append(" c ON v.").append(COL_VE_CHUYEN_ID).append(" = c.").append(COL_CHUYEN_ID)
                .append("    LEFT JOIN ").append(TBL_GA).append(" g_di ON v.").append(COL_GA_DI_ID).append(" = g_di.").append(COL_GA_ID)
                .append("    LEFT JOIN ").append(TBL_GA).append(" g_den ON v.").append(COL_GA_DEN_ID).append(" = g_den.").append(COL_GA_ID)
                .append("    LEFT JOIN ").append(TBL_HOA_DON_CHI_TIET).append(" hdct ON v.").append(COL_VE_ID).append(" = hdct.").append(COL_HDCT_VE_ID)
                .append("    LEFT JOIN ").append(TBL_HOA_DON).append(" hd ON hdct.").append(COL_HDCT_HOA_DON_ID).append(" = hd.").append(COL_HD_HOA_DON_ID)
                .append("    LEFT JOIN ").append(TBL_GHE).append(" g ON v.").append(COL_VE_GHE_ID).append(" = g.").append(COL_GHE_ID)
                .append("    LEFT JOIN ").append(TBL_TOA).append(" toa ON g.").append(COL_GHE_TOA_ID).append(" = toa.").append(COL_TOA_ID)
                .append("    WHERE (ddc.").append(COL_DDC_THOI_DIEM).append(" >= ? AND ddc.").append(COL_DDC_THOI_DIEM).append(" < ?) ");

        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            sql.append(" AND g_di.").append(COL_TEN_GA).append(" = ? AND g_den.").append(COL_TEN_GA).append(" = ? ");
        }
        if (nhanVienID != null) {
            sql.append(" AND hd.").append(COL_HD_NHAN_VIEN_ID).append(" = ? ");
        }
        if (hangToaID != null) {
            sql.append(" AND toa.").append(COL_TOA_HANG_TOA_ID).append(" = ? ");
        }
        if (trangThai != null) {
            sql.append(" AND v.").append(COL_TRANG_THAI).append(" = ? ");
        }

        sql.append(") AS UniqueVe");
        return sql.toString();
    }

    // ===========================================
    // 3. CÁC HÀM CARD (TỔNG QUAN)
    // ===========================================

    // Helper: Set tham số cho PreparedStatement của Card
    private void setCardParameters(PreparedStatement pstmt, LocalDate tuNgay, LocalDate denNgay,
                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                   String nhanVienID, String hangToaID, String trangThai,
                                   String... extraParams) throws SQLException {
        int paramIndex = 1;
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            pstmt.setString(paramIndex++, tenGaDi);
            pstmt.setString(paramIndex++, tenGaDen);
        }
        if (nhanVienID != null) {
            pstmt.setString(paramIndex++, nhanVienID);
        }
        if (hangToaID != null) {
            pstmt.setString(paramIndex++, hangToaID);
        }
        if (trangThai != null) {
            pstmt.setString(paramIndex++, trangThai);
        }
        // Set thêm các tham số phụ (ví dụ: trạng thái cụ thể cho WHERE bên ngoài subquery)
        for (String param : extraParams) {
            pstmt.setString(paramIndex++, param);
        }
    }

    public int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // --- Các hàm Public lấy số liệu thẻ ---

    public int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        // Lọc thêm trạng thái cụ thể bên ngoài subquery
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_BAN);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeDaDungTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_DUNG);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeDaDoiTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_DOI);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeHoanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_HOAN);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTongTienVeTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String sumSql = String.format("ISNULL(SUM(%s), 0)", COL_GIA_VE);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, sumSql);
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    // Inner class DTO để hứng dữ liệu thống kê chi tiết
    public static class ThongKeVeChiTietItem {
        public String thoiGian;
        public int tongSoVeBan = 0;
        public int tongVeConHieuLuc = 0;
        public int tongVeDaDung = 0;
        public int tongVeDaDoi = 0;
        public int tongVeHoan = 0;
        public double tongTienVe = 0.0;
        public String tuyenDuong;

        public ThongKeVeChiTietItem(String thoiGian) {
            this.thoiGian = thoiGian;
        }
    }
}