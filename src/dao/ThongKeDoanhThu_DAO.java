package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO cho PanelThongKeDoanhThu.
 * Hỗ trợ lọc theo Thời gian, Tuyến, Nhân viên, Hình thức Thanh toán.
 *
 * Thiết kế lại:
 * - Gom hóa đơn trong CTE Base để tránh nhân bản khi JOIN nhiều bảng.
 * - Các hàm card & chi tiết đều dùng chung CTE và bộ lọc.
 */
public class ThongKeDoanhThu_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeDoanhThu_DAO.class.getName());

    // ===== TÊN BẢNG =====
    private static final String TBL_HOA_DON = "HoaDon";
    private static final String TBL_HOA_DON_CHI_TIET = "HoaDonChiTiet";
    private static final String TBL_VE = "Ve";
    private static final String TBL_GA = "Ga";
    private static final String TBL_NHAN_VIEN = "NhanVien"; // (hiện chỉ dùng ID từ HoaDon)

    // ===== CỘT HoaDon =====
    private static final String COL_HD_ID = "hoaDonID";
    private static final String COL_HD_THOI_DIEM_TAO = "thoiDiemTao";
    private static final String COL_HD_NHAN_VIEN_ID = "nhanVienID";
    private static final String COL_HD_IS_TIEN_MAT = "isThanhToanTienMat";
    private static final String COL_HD_TONG_TIEN = "tongTien";   // Doanh thu của hóa đơn
    private static final String COL_HD_TIEN_HOAN = "tienHoan";   // Số tiền hoàn (chi phí)

    // ===== CỘT HoaDonChiTiet =====
    private static final String COL_HDCT_HOA_DON_ID = "hoaDonID";
    private static final String COL_HDCT_VE_ID = "veID";
    private static final String COL_HDCT_PHONG_VIP_ID = "phieuDungPhongVIPID"; // sử dụng để xác định dịch vụ
    private static final String COL_HDCT_THANH_TIEN = "thanhTien";

    // ===== CỘT Vé =====
    private static final String COL_VE_ID = "veID";
    private static final String COL_VE_GA_DI_ID = "gaDiID";
    private static final String COL_VE_GA_DEN_ID = "gaDenID";

    // ===== CỘT Ga =====
    private static final String COL_GA_ID = "gaID";
    private static final String COL_TEN_GA = "tenGa";

    // ===== CỘT Nhân viên =====
    private static final String COL_NV_ID = "nhanVienID";

    /**
     * Item chi tiết theo 1 mốc thời gian (ngày/tháng/năm)
     */
    public static class ThongKeChiTietItem {
        public String thoiGian;
        public int soLuongHoaDonBan = 0;
        public int soLuongHoaDonHoanDoi = 0;
        public double tongThuDichVu = 0.0;
        public double tongDoanhThu = 0.0;
        public double tongChi = 0.0;
        public double loiNhuan = 0.0;

        public ThongKeChiTietItem(String thoiGian) {
            this.thoiGian = thoiGian;
        }
    }

    /**
     * Cấu hình group-by theo loại thời gian (ngày/tháng/năm).
     */
    private static class TimeGrouping {
        final String selectExpr; // biểu thức SELECT hiển thị (string)
        final String groupBy;    // biểu thức GROUP BY
        final String orderBy;    // biểu thức ORDER BY

        private TimeGrouping(String selectExpr, String groupBy, String orderBy) {
            this.selectExpr = selectExpr;
            this.groupBy = groupBy;
            this.orderBy = orderBy;
        }
    }

    // ===== Helper build group-by theo loại thời gian =====
    private TimeGrouping buildTimeGrouping(String loaiThoiGian) {
        // Dùng alias b cho CTE Base (b.thoiDiemTao)
        switch (loaiThoiGian) {
            case "Theo ngày": {
                // Ngày: dùng CONVERT(date, ...) để GROUP BY chính xác, hiển thị dd/MM/yyyy
                String dayExpr = "CONVERT(date, b.thoiDiemTao)";
                String selectExpr = "CONVERT(varchar(10), " + dayExpr + ", 103)"; // dd/MM/yyyy
                String groupBy = dayExpr + ", " + selectExpr;
                String orderBy = dayExpr;
                return new TimeGrouping(selectExpr, groupBy, orderBy);
            }
            case "Theo tháng": {
                // Tháng/Năm: MM/yyyy, GROUP BY theo YEAR & MONTH để ORDER đúng
                String yearExpr = "YEAR(b.thoiDiemTao)";
                String monthExpr = "MONTH(b.thoiDiemTao)";
                String selectExpr = "RIGHT('0' + CAST(" + monthExpr + " AS varchar(2)), 2) + '/' + CAST(" + yearExpr + " AS varchar(4))";
                String groupBy = yearExpr + ", " + monthExpr + ", " + selectExpr;
                String orderBy = yearExpr + ", " + monthExpr;
                return new TimeGrouping(selectExpr, groupBy, orderBy);
            }
            case "Theo năm":
            default: {
                // Năm: yyyy
                String yearExpr = "YEAR(b.thoiDiemTao)";
                String selectExpr = "CAST(" + yearExpr + " AS varchar(4))";
                String groupBy = yearExpr + ", " + selectExpr;
                String orderBy = yearExpr;
                return new TimeGrouping(selectExpr, groupBy, orderBy);
            }
        }
    }

    /**
     * Xây dựng phần CTE Base dùng chung:
     *
     * WITH Base AS (
     *   SELECT
     *     hd.hoaDonID,
     *     hd.thoiDiemTao,
     *     hd.tongTien,
     *     hd.tienHoan,
     *     SUM(CASE WHEN hdct.phieuDungPhongVIPID IS NOT NULL THEN hdct.thanhTien ELSE 0 END) AS thuDichVu
     *   FROM ...
     *   LEFT JOIN ...
     *   WHERE hd.thoiDiemTao >= ? AND hd.thoiDiemTao < ?
     *     [AND g_di.tenGa = ? AND g_den.tenGa = ?]
     *     [AND hd.nhanVienID = ?]
     *     [AND hd.isThanhToanTienMat = ?]
     *   GROUP BY hd.hoaDonID, hd.thoiDiemTao, hd.tongTien, hd.tienHoan
     * )
     */
    private String buildBaseCTE(String loaiTuyen, String nhanVienID, Integer isTienMat) {
        StringBuilder sb = new StringBuilder();
        sb.append("WITH Base AS ( ");
        sb.append(" SELECT ");
        sb.append("   hd.").append(COL_HD_ID).append(" AS hoaDonID,");
        sb.append("   hd.").append(COL_HD_THOI_DIEM_TAO).append(" AS thoiDiemTao,");
        sb.append("   hd.").append(COL_HD_TONG_TIEN).append(" AS tongTien,");
        sb.append("   hd.").append(COL_HD_TIEN_HOAN).append(" AS tienHoan,");
        sb.append("   SUM(CASE WHEN hdct.").append(COL_HDCT_PHONG_VIP_ID)
                .append(" IS NOT NULL THEN hdct.").append(COL_HDCT_THANH_TIEN)
                .append(" ELSE 0 END) AS thuDichVu ");
        sb.append(" FROM ").append(TBL_HOA_DON).append(" hd ");
        sb.append(" LEFT JOIN ").append(TBL_HOA_DON_CHI_TIET).append(" hdct ");
        sb.append("   ON hd.").append(COL_HD_ID).append(" = hdct.").append(COL_HDCT_HOA_DON_ID).append(" ");
        sb.append(" LEFT JOIN ").append(TBL_VE).append(" v ");
        sb.append("   ON hdct.").append(COL_HDCT_VE_ID).append(" = v.").append(COL_VE_ID).append(" ");

        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            sb.append(" LEFT JOIN ").append(TBL_GA).append(" g_di ");
            sb.append("   ON v.").append(COL_VE_GA_DI_ID).append(" = g_di.").append(COL_GA_ID).append(" ");
            sb.append(" LEFT JOIN ").append(TBL_GA).append(" g_den ");
            sb.append("   ON v.").append(COL_VE_GA_DEN_ID).append(" = g_den.").append(COL_GA_ID).append(" ");
        }

        sb.append(" WHERE hd.").append(COL_HD_THOI_DIEM_TAO).append(" >= ? ");
        sb.append("   AND hd.").append(COL_HD_THOI_DIEM_TAO).append(" < ? ");

        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            sb.append("   AND g_di.").append(COL_TEN_GA).append(" = ? ");
            sb.append("   AND g_den.").append(COL_TEN_GA).append(" = ? ");
        }
        if (nhanVienID != null) {
            sb.append("   AND hd.").append(COL_HD_NHAN_VIEN_ID).append(" = ? ");
        }
        if (isTienMat != null) {
            sb.append("   AND hd.").append(COL_HD_IS_TIEN_MAT).append(" = ? ");
        }

        sb.append(" GROUP BY ");
        sb.append("   hd.").append(COL_HD_ID).append(", ");
        sb.append("   hd.").append(COL_HD_THOI_DIEM_TAO).append(", ");
        sb.append("   hd.").append(COL_HD_TONG_TIEN).append(", ");
        sb.append("   hd.").append(COL_HD_TIEN_HOAN).append(" ");
        sb.append(") ");

        return sb.toString();
    }

    /**
     * Gán tham số filter cho PreparedStatement.
     * Thứ tự phải đúng với buildBaseCTE:
     *  1: tuNgay
     *  2: denNgay (exclusive: denNgay + 1)
     *  3-4: tenGaDi, tenGaDen (nếu loaiTuyen = Theo Ga đi/đến)
     *  5: nhanVienID (nếu != null)
     *  6: isTienMat (nếu != null)
     */
    private void setFilterParameters(PreparedStatement pstmt,
                                     LocalDate tuNgay, LocalDate denNgay,
                                     String loaiTuyen, String tenGaDi, String tenGaDen,
                                     String nhanVienID, Integer isTienMat) throws SQLException {
        int index = 1;
        // khoảng thời gian: [tuNgay, denNgay+1)
        pstmt.setDate(index++, java.sql.Date.valueOf(tuNgay));
        pstmt.setDate(index++, java.sql.Date.valueOf(denNgay.plusDays(1)));

        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            pstmt.setString(index++, tenGaDi);
            pstmt.setString(index++, tenGaDen);
        }
        if (nhanVienID != null) {
            pstmt.setString(index++, nhanVienID);
        }
        if (isTienMat != null) {
            pstmt.setInt(index++, isTienMat);
        }
    }

    // ======================================================================
    //  1. THỐNG KÊ CHI TIẾT THEO THỜI GIAN
    // ======================================================================

    /**
     * Lấy dữ liệu thống kê doanh thu chi tiết theo thời gian (ngày/tháng/năm).
     *
     * @param loaiThoiGian "Theo ngày" | "Theo tháng" | "Theo năm" (hoặc "Tất cả" đã được Panel ép thành "Theo năm")
     */
    public Map<String, ThongKeChiTietItem> getThongKeDoanhThuChiTiet(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, Integer isTienMat) throws SQLException {

        Map<String, ThongKeChiTietItem> results = new LinkedHashMap<>();

        TimeGrouping tg = buildTimeGrouping(loaiThoiGian);
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);

        String sql =
                baseCTE +
                        "SELECT " +
                        "  " + tg.selectExpr + " AS ThoiGian, " +
                        "  COUNT(*) AS SoLuongHoaDonBan, " +
                        "  SUM(CASE WHEN b.tienHoan > 0 THEN 1 ELSE 0 END) AS SoLuongHoaDonHoanDoi, " +
                        "  SUM(b.thuDichVu) AS TongThuDichVu, " +
                        "  SUM(b.tongTien) AS TongDoanhThu, " +
                        "  SUM(b.tienHoan) AS TongChi, " +
                        "  (SUM(b.tongTien) - SUM(b.tienHoan)) AS LoiNhuan " +
                        "FROM Base b " +
                        "GROUP BY " + tg.groupBy + " " +
                        "ORDER BY " + tg.orderBy;

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String thoiGianKey = rs.getString("ThoiGian");
                    ThongKeChiTietItem item = new ThongKeChiTietItem(thoiGianKey);
                    item.soLuongHoaDonBan = rs.getInt("SoLuongHoaDonBan");
                    item.soLuongHoaDonHoanDoi = rs.getInt("SoLuongHoaDonHoanDoi");
                    item.tongThuDichVu = rs.getDouble("TongThuDichVu");
                    item.tongDoanhThu = rs.getDouble("TongDoanhThu");
                    item.tongChi = rs.getDouble("TongChi");
                    item.loiNhuan = rs.getDouble("LoiNhuan");

                    results.put(thoiGianKey, item);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy thống kê doanh thu chi tiết", ex);
            throw ex;
        }

        return results;
    }

    // ======================================================================
    //  2. CÁC HÀM CARD TỔNG QUAN
    // ======================================================================

    /**
     * Đếm tổng số hóa đơn bán (tất cả hóa đơn trong khoảng).
     */
    public int getTongHoaDonBan(LocalDate tuNgay, LocalDate denNgay,
                                String loaiTuyen, String tenGaDi, String tenGaDen,
                                String nhanVienID, Integer isTienMat) throws SQLException {

        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE + "SELECT COUNT(*) AS Cnt FROM Base b";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Cnt");
                }
            }
        }
        return 0;
    }

    /**
     * Đếm số hóa đơn có hoàn/đổi (tienHoan > 0).
     */
    public int getTongHoaDonHoanDoi(LocalDate tuNgay, LocalDate denNgay,
                                    String loaiTuyen, String tenGaDi, String tenGaDen,
                                    String nhanVienID, Integer isTienMat) throws SQLException {

        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT COUNT(*) AS Cnt " +
                "FROM Base b " +
                "WHERE b.tienHoan > 0";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Cnt");
                }
            }
        }
        return 0;
    }

    /**
     * Tổng thu dịch vụ (phòng VIP) trong khoảng thời gian & bộ lọc.
     */
    public double getTongThuDichVu(LocalDate tuNgay, LocalDate denNgay,
                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                   String nhanVienID, Integer isTienMat) throws SQLException {

        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT ISNULL(SUM(b.thuDichVu), 0) AS TongThuDichVu " +
                "FROM Base b";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongThuDichVu");
                }
            }
        }
        return 0.0;
    }

    /**
     * Trả về map: doanhThu, chi, loiNhuan cho toàn bộ khoảng thời gian & bộ lọc.
     */
    public Map<String, Double> getTongDoanhThuChiLoiNhuan(LocalDate tuNgay, LocalDate denNgay,
                                                          String loaiTuyen, String tenGaDi, String tenGaDen,
                                                          String nhanVienID, Integer isTienMat) throws SQLException {

        Map<String, Double> result = new HashMap<>();
        result.put("doanhThu", 0.0);
        result.put("chi", 0.0);
        result.put("loiNhuan", 0.0);

        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT " +
                "  ISNULL(SUM(b.tongTien), 0) AS TongDoanhThu, " +
                "  ISNULL(SUM(b.tienHoan), 0) AS TongChi " +
                "FROM Base b";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double doanhThu = rs.getDouble("TongDoanhThu");
                    double chi = rs.getDouble("TongChi");
                    result.put("doanhThu", doanhThu);
                    result.put("chi", chi);
                    result.put("loiNhuan", doanhThu - chi);
                }
            }
        }
        return result;
    }
}
