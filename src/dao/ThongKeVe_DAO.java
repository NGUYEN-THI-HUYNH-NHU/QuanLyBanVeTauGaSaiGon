package dao;

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
 * DAO cho PanelThongKeVe.
 * Cập nhật: Lấy thời gian từ DonDatCho (thoiDiemDatCho) thay vì Ve (thoiDiemBan).
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

    // === MỚI: Bảng Đơn Đặt Chỗ ===
    private final String TBL_DON_DAT_CHO = "DonDatCho";
    private final String COL_DDC_ID = "donDatChoID";
    private final String COL_DDC_THOI_DIEM = "thoiDiemDatCho"; // Cột thời gian mới

    // Cột Ve
    private final String COL_VE_ID = "veID";
    private final String COL_TRANG_THAI = "trangThai";
    private final String COL_GIA_VE = "gia";
    // private final String COL_THOI_DIEM_BAN = "thoiDiemBan"; // <-- ĐÃ BỎ
    private final String COL_VE_DDC_ID = "donDatChoID"; // Khóa ngoại liên kết sang DonDatCho
    private final String COL_VE_CHUYEN_ID = "chuyenID";
    private final String COL_GA_DI_ID = "gaDiID";
    private final String COL_GA_DEN_ID = "gaDenID";
    private final String COL_VE_GHE_ID = "gheID";

    // Cột Chuyen
    private final String COL_CHUYEN_ID = "chuyenID";
    private final String COL_NGAY_DI = "ngayDi";

    // Cột Ga
    private final String COL_GA_ID = "gaID";
    private final String COL_TEN_GA = "tenGa";

    // Cột HoaDonChiTiet
    private final String COL_HDCT_HOA_DON_ID = "hoaDonID";
    private final String COL_HDCT_VE_ID = "veID";

    // Cột HoaDon
    private final String COL_HD_HOA_DON_ID = "hoaDonID";
    private final String COL_HD_NHAN_VIEN_ID = "nhanVienID";

    // Cột NhanVien
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
    private final String TT_DA_HOAN = "DA_HOAN";
    private final String TT_DA_DOI = "DA_DOI";

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

    // ===========================================
    // HÀM TẢI DỮ LIỆU COMBOBOX
    // ===========================================

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

    // ===========================================
    // HÀM THỐNG KÊ CHÍNH (Đã cập nhật JOIN DonDatCho)
    // ===========================================

    public Map<String, ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, String hangToaID, String trangThai) throws SQLException {

        Map<String, ThongKeVeChiTietItem> results = new LinkedHashMap<>();

        // Sử dụng ddc.thoiDiemDatCho thay cho v.thoiDiemBan
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

        // CTE: Join thêm bảng DonDatCho (alias ddc)
        String cteSql = String.format(
                "WITH ThongKeNhom AS ( " +
                        "    SELECT DISTINCT v.%s, " +
                        "        %s AS ThoiGian, " +
                        "        g_di.%s AS TenGaDi, " +
                        "        g_den.%s AS TenGaDen, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeConHieuLuc, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeDaDung, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeDaDoi, " +
                        "        CASE WHEN v.%s = ? THEN 1 ELSE 0 END AS IsVeHoan, " +
                        "        v.%s AS GiaVe " +
                        "    FROM %s v " +
                        "    LEFT JOIN %s ddc ON v.%s = ddc.%s " + // <<< JOIN MỚI: Ve -> DonDatCho
                        "    LEFT JOIN %s c ON v.%s = c.%s " +
                        "    LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "    LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "    LEFT JOIN %s hdct ON v.%s = hdct.%s " +
                        "    LEFT JOIN %s hd ON hdct.%s = hd.%s " +
                        "    LEFT JOIN %s g ON v.%s = g.%s " +
                        "    LEFT JOIN %s toa ON g.%s = toa.%s " +
                        "    WHERE (ddc.%s >= ? AND ddc.%s < ?) ", // <<< ĐK LỌC: ddc.thoiDiemDatCho

                COL_VE_ID, thoiGianSelect, COL_TEN_GA, COL_TEN_GA,
                COL_TRANG_THAI, COL_TRANG_THAI, COL_TRANG_THAI, COL_TRANG_THAI, COL_GIA_VE,
                TBL_VE,
                TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID, // Tham số JOIN DonDatCho
                TBL_CHUYEN, COL_VE_CHUYEN_ID, COL_CHUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                TBL_HOA_DON_CHI_TIET, COL_VE_ID, COL_HDCT_VE_ID,
                TBL_HOA_DON, COL_HDCT_HOA_DON_ID, COL_HD_HOA_DON_ID,
                TBL_GHE, COL_VE_GHE_ID, COL_GHE_ID,
                TBL_TOA, COL_GHE_TOA_ID, COL_TOA_ID,
                COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM // Tham số WHERE
        );

        if (loaiTuyen.equals("Theo Ga đi/đến")) {
            cteSql += String.format(" AND g_di.%s = ? AND g_den.%s = ? ", COL_TEN_GA, COL_TEN_GA);
        }
        if (nhanVienID != null) {
            // Lưu ý: Nếu muốn lọc nhân viên theo người Đặt vé thì dùng ddc.nhanVienID,
            // nếu lọc người Xuất hóa đơn thì giữ hd.nhanVienID. Ở đây giữ nguyên hd.
            cteSql += String.format(" AND hd.%s = ? ", COL_HD_NHAN_VIEN_ID);
        }
        if (hangToaID != null) {
            cteSql += String.format(" AND toa.%s = ? ", COL_TOA_HANG_TOA_ID);
        }
        if (trangThai != null) {
            cteSql += String.format(" AND v.%s = ? ", COL_TRANG_THAI);
        }

        cteSql += ") ";

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
            if (nhanVienID != null) {
                pstmt.setString(paramIndex++, nhanVienID);
            }
            if (hangToaID != null) {
                pstmt.setString(paramIndex++, hangToaID);
            }
            if (trangThai != null) {
                pstmt.setString(paramIndex++, trangThai);
            }

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
    // Các hàm CARD (Tổng quan) - Đã cập nhật JOIN
    // ===========================================

    private String buildCardSql(String loaiTuyen, String nhanVienID, String hangToaID, String trangThai, String countOrSumSql) {
        // Subquery để lọc DISTINCT vé trước khi đếm
        // Đã thêm LEFT JOIN DonDatCho ddc
        // Đã sửa WHERE dùng ddc.thoiDiemDatCho
        String sql = String.format(
                "SELECT %s FROM (SELECT DISTINCT v.%s, v.%s, v.%s FROM %s v " +
                        "    LEFT JOIN %s ddc ON v.%s = ddc.%s " + // <<< JOIN MỚI
                        "    LEFT JOIN %s c ON v.%s = c.%s " +
                        "    LEFT JOIN %s g_di ON v.%s = g_di.%s " +
                        "    LEFT JOIN %s g_den ON v.%s = g_den.%s " +
                        "    LEFT JOIN %s hdct ON v.%s = hdct.%s " +
                        "    LEFT JOIN %s hd ON hdct.%s = hd.%s " +
                        "    LEFT JOIN %s g ON v.%s = g.%s " +
                        "    LEFT JOIN %s toa ON g.%s = toa.%s " +
                        "    WHERE (ddc.%s >= ? AND ddc.%s < ?) ", // <<< ĐK LỌC MỚI

                countOrSumSql, COL_VE_ID, COL_GIA_VE, COL_TRANG_THAI, TBL_VE,
                TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID, // Tham số Join
                TBL_CHUYEN, COL_VE_CHUYEN_ID, COL_CHUYEN_ID,
                TBL_GA, COL_GA_DI_ID, COL_GA_ID,
                TBL_GA, COL_GA_DEN_ID, COL_GA_ID,
                TBL_HOA_DON_CHI_TIET, COL_VE_ID, COL_HDCT_VE_ID,
                TBL_HOA_DON, COL_HDCT_HOA_DON_ID, COL_HD_HOA_DON_ID,
                TBL_GHE, COL_VE_GHE_ID, COL_GHE_ID,
                TBL_TOA, COL_GHE_TOA_ID, COL_TOA_ID,
                COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM // Tham số Where
        );

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

        sql += ") AS UniqueVe";
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
        for (String param : extraParams) {
            pstmt.setString(paramIndex++, param);
        }
    }

    // Các hàm getTong... (Giữ nguyên logic gọi buildCardSql)
    public int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_BAN);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public int getTongVeDaDungTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_DUNG);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public int getTongVeDaDoiTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_DOI);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public int getTongVeHoanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String countSql = String.format("COUNT(%s)", COL_VE_ID);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, countSql);
        sql += " WHERE " + COL_TRANG_THAI + " = ? ";
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai, TT_DA_HOAN);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        return 0;
    }

    public double getTongTienVeTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException {
        String sumSql = String.format("ISNULL(SUM(%s), 0)", COL_GIA_VE);
        String sql = buildCardSql(loaiTuyen, nhanVienID, hangToaID, trangThai, sumSql);
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setCardParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getDouble(1); }
        }
        return 0.0;
    }
}