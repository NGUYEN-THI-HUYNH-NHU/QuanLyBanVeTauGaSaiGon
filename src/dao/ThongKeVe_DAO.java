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
 * Giữ nguyên cấu trúc ban đầu của bạn, CHỈ SỬA:
 * - Dùng CONVERT/MONTH/YEAR thay FORMAT() để tránh Conversion Failed
 * - Sửa thứ tự set tham số (đặt loaiTuyen ở CUỐI cùng)
 */
public class ThongKeVe_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeVe_DAO.class.getName());

    // === TÊN CỘT/BẢNG ===
    private final String TBL_VE = "Ve";
    private final String TBL_CHUYEN = "chuyen";
    private final String TBL_GA = "Ga";
    private final String TBL_HOA_DON_CHI_TIET = "HoaDonChiTiet";
    private final String TBL_HOA_DON = "HoaDon";
    private final String TBL_NHAN_VIEN = "NhanVien"; // Giả định
    private final String TBL_GHE = "Ghe";
    private final String TBL_TOA = "Toa";
    private final String TBL_HANG_TOA = "HangToa";

    // Cột Ve
    private final String COL_VE_ID = "veID";
    private final String COL_TRANG_THAI = "trangThai";
    private final String COL_GIA_VE = "gia";
    private final String COL_THOI_DIEM_BAN = "thoiDiemBan"; // Kiểu DATETIME
    private final String COL_VE_CHUYEN_ID = "chuyenID";
    private final String COL_GA_DI_ID = "gaDiID";
    private final String COL_GA_DEN_ID = "gaDenID";
    private final String COL_VE_GHE_ID = "gheID"; // <<< MỚI

    // Cột Chuyen
    private final String COL_CHUYEN_ID = "chuyenID";
    private final String COL_NGAY_DI = "ngayDi"; // Kiểu DATE

    // Cột Ga
    private final String COL_GA_ID = "gaID";
    private final String COL_TEN_GA = "tenGa";

    // Cột HoaDonChiTiet
    private final String COL_HDCT_HOA_DON_ID = "hoaDonID";
    private final String COL_HDCT_VE_ID = "veID";

    // Cột HoaDon
    private final String COL_HD_HOA_DON_ID = "hoaDonID";
    private final String COL_HD_NHAN_VIEN_ID = "nhanVienID";

    // Cột NhanVien (Giả định)
    private final String COL_NV_ID = "nhanVienID";
    private final String COL_NV_HO_TEN = "hoTen";

    // Cột Ghe
    private final String COL_GHE_ID = "gheID";
    private final String COL_GHE_TOA_ID = "toaID";

    // Cột Toa
    private final String COL_TOA_ID = "toaID";
    private final String COL_TOA_HANG_TOA_ID = "hangToaID";

    // Cột HangToa
    private final String COL_HANG_TOA_ID = "hangToaID";
    private final String COL_HANG_TOA_MO_TA = "moTa";

    // Trạng thái vé
    private final String TT_DA_BAN = "DA_BAN";
    private final String TT_DA_DUNG = "DA_DUNG";
    private final String TT_DA_HOAN = "DA_HOAN"; // Hoặc "DA_HUY"
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
        public String tuyenDuong; // Nối Ga đi -> Ga đến

        public ThongKeVeChiTietItem(String thoiGian) {
            this.thoiGian = thoiGian;
        }
    }

    // ===========================================
    // HÀM TẢI DỮ LIỆU COMBOBOX (GIỮ NGUYÊN)
    // ===========================================

    public List<String> getDanhSachTenGa() throws SQLException {
        List<String> danhSachGa = new ArrayList<>();
        String sql = String.format("SELECT %s FROM %s ORDER BY %s",
                COL_TEN_GA, TBL_GA, COL_TEN_GA);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) danhSachGa.add(rs.getString(COL_TEN_GA));
        }
        return danhSachGa;
    }

    // Dùng Map để lưu ID (GN_K4) và Tên (Giường nằm K4)
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

    // Dùng Map để lưu ID (NV001) và Tên (Nguyễn Văn A)
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

    // ===========================================
    // HÀM THỐNG KÊ CHÍNH (GIỮ NGUYÊN TÊN & THAM SỐ)
    // ===========================================

    /**
     * Hàm chính: Lấy dữ liệu thống kê vé chi tiết, nhóm theo thời gian.
     * SỬA NHẸ:
     *  - Không dùng FORMAT(); dùng CONVERT/CAST/MONTH/YEAR để tránh lỗi convert.
     *  - KHÔNG set loaiTuyen sớm; set loaiTuyen ở cuối (đúng placeholder ngoài SELECT).
     */
    public Map<String, ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, String hangToaID, String trangThai) throws SQLException {

        Map<String, ThongKeVeChiTietItem> results = new LinkedHashMap<>();

        // 1. Xác định cách GROUP BY (thay FORMAT -> CONVERT/CAST)
        String groupByClause, thoiGianSelect;
        switch (loaiThoiGian) {
            case "Theo ngày":
                thoiGianSelect = String.format("CONVERT(VARCHAR(10), v.%s, 103)", COL_THOI_DIEM_BAN); // dd/MM/yyyy
                groupByClause = thoiGianSelect;
                break;
            case "Theo tháng":
                thoiGianSelect = String.format(
                        "RIGHT('0' + CAST(MONTH(v.%s) AS VARCHAR(2)), 2) + '/' + CAST(YEAR(v.%s) AS VARCHAR(4))",
                        COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN); // MM/yyyy
                groupByClause = String.format("YEAR(v.%s), MONTH(v.%s)", COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN);
                break;
            default: // "Theo năm" hoặc "Tất cả"
                thoiGianSelect = String.format("CAST(YEAR(v.%s) AS VARCHAR(4))", COL_THOI_DIEM_BAN);
                groupByClause = String.format("YEAR(v.%s)", COL_THOI_DIEM_BAN);
                break;
        }

        // 2. CTE (giữ nguyên cấu trúc)
        String cteSql = String.format(
                "WITH ThongKeNhom AS ( " +
                        "    SELECT DISTINCT v.%s, " + // Dùng DISTINCT ở đây
                        "        %s AS ThoiGian, " +
                        "        g_di.%s AS TenGaDi, " +
                        "        g_den.%s AS TenGaDen, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeConHieuLuc, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeDaDung, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeDaDoi, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeHoan, " +
                        "        v.%s AS GiaVe " +
                        "    FROM %s v " +
                        "    LEFT JOIN %s c ON v.%s = c.%s " +
                        "    LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "    LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "    LEFT JOIN %s hdct ON v.%s = hdct.%s " + // Join Hóa đơn CT
                        "    LEFT JOIN %s hd ON hdct.%s = hd.%s " + // Join Hóa đơn
                        "    LEFT JOIN %s g ON v.%s = g.%s " + // Join Ghế
                        "    LEFT JOIN %s toa ON g.%s = toa.%s " + // Join Toa
                        "    WHERE (v.%s >= ? AND v.%s < ?) ", // Lọc thời gian

                COL_VE_ID, thoiGianSelect, COL_TEN_GA, COL_TEN_GA,
                COL_TRANG_THAI, COL_TRANG_THAI, COL_TRANG_THAI, COL_TRANG_THAI, COL_GIA_VE,
                TBL_VE, TBL_CHUYEN, COL_VE_CHUYEN_ID, COL_CHUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                TBL_HOA_DON_CHI_TIET, COL_VE_ID, COL_HDCT_VE_ID,
                TBL_HOA_DON, COL_HDCT_HOA_DON_ID, COL_HD_HOA_DON_ID,
                TBL_GHE, COL_VE_GHE_ID, COL_GHE_ID,
                TBL_TOA, COL_GHE_TOA_ID, COL_TOA_ID,
                COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN
        );

        // Thêm bộ lọc động (giữ nguyên)
        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            cteSql += String.format(" AND g_di.%s = ? AND g_den.%s = ? ", COL_TEN_GA, COL_TEN_GA);
        }
        if (nhanVienID != null) {
            cteSql += String.format(" AND hd.%s = ? ", COL_HD_NHAN_VIEN_ID);
        }
        if (hangToaID != null) {
            cteSql += String.format(" AND toa.%s = ? ", COL_TOA_HANG_TOA_ID);
        }
        if (trangThai != null) {
            cteSql += String.format(" AND v.%s = ? ", COL_TRANG_THAI);
        }

        cteSql += ") "; // Đóng CTE

        // 3. SELECT ngoài (giữ nguyên)
        String finalSql = cteSql +
                "SELECT ThoiGian, " +
                "  COUNT(veID) AS TongSoVeBan, " +
                "  SUM(IsVeConHieuLuc) AS TongVeConHieuLuc, " +
                "  SUM(IsVeDaDung) AS TongVeDaDung, " +
                "  SUM(IsVeDaDoi) AS TongVeDaDoi, " +
                "  SUM(IsVeHoan) AS TongVeHoan, " +
                "  SUM(GiaVe) AS TongTienVe, " +
                "  CASE WHEN ? = N'Theo Ga đi/đến' " +
                "       THEN STRING_AGG(ISNULL(TenGaDi, 'N/A') + ' -> ' + ISNULL(TenGaDen, 'N/A'), ', ') WITHIN GROUP (ORDER BY TenGaDi, TenGaDen) " +
                "       ELSE N'N/A' " +
                "  END AS TuyenDuong " +
                "FROM ThongKeNhom " +
                "WHERE ThoiGian IS NOT NULL " +
                "GROUP BY ThoiGian " +
                "ORDER BY ThoiGian";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(finalSql)) {

            int paramIndex = 1;

            // >>>> CHÚ Ý: KHÔNG set loaiTuyen ở đây nữa <<<<
            // Đầu tiên là 4 trạng thái trong CTE:
            pstmt.setString(paramIndex++, TT_DA_BAN);
            pstmt.setString(paramIndex++, TT_DA_DUNG);
            pstmt.setString(paramIndex++, TT_DA_DOI);
            pstmt.setString(paramIndex++, TT_DA_HOAN);

            // Khoảng thời gian
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

            // Các bộ lọc động
            if (loaiTuyen.equals("Theo Ga đi/đến")) {
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

            // Cuối cùng mới đến tham số của SELECT ngoài (CASE WHEN ? = ...)
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
    // Các hàm CARD (Tổng quan) — GIỮ NGUYÊN KHAI BÁO
    // ===========================================

    // Hàm helper chính
    private String buildCardSql(String loaiTuyen, String nhanVienID, String hangToaID, String trangThai, String countOrSumSql) {
        String sql = String.format(
                "SELECT %s FROM (SELECT DISTINCT v.%s, v.%s, v.%s FROM %s v " + // Dùng Subquery với DISTINCT
                        "    LEFT JOIN %s c ON v.%s = c.%s " +
                        "    LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "    LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "    LEFT JOIN %s hdct ON v.%s = hdct.%s " +
                        "    LEFT JOIN %s hd ON hdct.%s = hd.%s " +
                        "    LEFT JOIN %s g ON v.%s = g.%s " +
                        "    LEFT JOIN %s toa ON g.%s = toa.%s " +
                        "    WHERE (v.%s >= ? AND v.%s < ?) ",
                countOrSumSql, COL_VE_ID, COL_GIA_VE, COL_TRANG_THAI, TBL_VE, // Các cột trong DISTINCT
                TBL_CHUYEN, COL_VE_CHUYEN_ID, COL_CHUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                TBL_HOA_DON_CHI_TIET, COL_VE_ID, COL_HDCT_VE_ID,
                TBL_HOA_DON, COL_HDCT_HOA_DON_ID, COL_HD_HOA_DON_ID,
                TBL_GHE, COL_VE_GHE_ID, COL_GHE_ID,
                TBL_TOA, COL_GHE_TOA_ID, COL_TOA_ID,
                COL_THOI_DIEM_BAN, COL_THOI_DIEM_BAN
        );

        // Thêm bộ lọc động
        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            sql += String.format(" AND g_di.%s = ? AND g_den.%s = ? ", COL_TEN_GA, COL_TEN_GA);
        }
        if (nhanVienID != null) {
            sql += String.format(" AND hd.%s = ? ", COL_HD_NHAN_VIEN_ID);
        }
        if (hangToaID != null) {
            sql += String.format(" AND toa.%s = ? ", COL_TOA_HANG_TOA_ID);
        }
        if (trangThai != null) {
            sql += String.format(" AND v.%s = ? ", COL_TRANG_THAI);
        }

        sql += ") AS UniqueVe"; // Đóng Subquery
        return sql;
    }

    private void setCardParameters(PreparedStatement pstmt, LocalDate tuNgay, LocalDate denNgay,
                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                   String nhanVienID, String hangToaID, String trangThai,
                                   String... extraParams) throws SQLException {
        int paramIndex = 1;
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
        pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

        if (loaiTuyen.equals("Theo Ga đi/đến")) {
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

        // Dùng cho các hàm đếm theo trạng thái (ví dụ: getTongVeDaDung)
        for (String param : extraParams) {
            pstmt.setString(paramIndex++, param);
        }
    }

    // Các hàm getTong...
    public int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? "; // Lọc trạng thái trên kết quả DISTINCT

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}
