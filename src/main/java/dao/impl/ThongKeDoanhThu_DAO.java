package dao.impl;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * DAO cho PanelThongKeDoanhThu.
 * Cập nhật logic tính toán dựa trên bảng HoaDon thực tế:
 * - HĐ Bán: Bắt đầu bằng 'HD' (trừ HDHV, HDDV).
 * - HĐ Hoàn/Đổi: Bắt đầu bằng 'HDHV' hoặc 'HDDV'.
 * - Tổng Chi: Tổng trị tuyệt đối các dòng tongTien < 0.
 * - Doanh Thu: Tổng các dòng tongTien > 0.
 */
public class ThongKeDoanhThu_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeDoanhThu_DAO.class.getName());

    // ===== TÊN BẢNG & CỘT =====
    private static final String TBL_HOA_DON = "HoaDon";
    private static final String TBL_HOA_DON_CHI_TIET = "HoaDonChiTiet";
    private static final String TBL_VE = "Ve";
    private static final String TBL_GA = "Ga";

    // Cột HoaDon
    private static final String COL_HD_ID = "hoaDonID";
    private static final String COL_HD_THOI_DIEM_TAO = "thoiDiemTao";
    private static final String COL_HD_NHAN_VIEN_ID = "nhanVienID";
    private static final String COL_HD_IS_TIEN_MAT = "isThanhToanTienMat";
    private static final String COL_HD_TONG_TIEN = "tongTien"; // Dùng cột này cho cả thu và chi (âm)

    // Cột HoaDonChiTiet (Dùng để tính thu dịch vụ nếu cần)
    private static final String COL_HDCT_HOA_DON_ID = "hoaDonID";
    private static final String COL_HDCT_VE_ID = "veID";
    private static final String COL_HDCT_PHONG_VIP_ID = "phieuDungPhongVIPID";
    private static final String COL_HDCT_THANH_TIEN = "thanhTien";

    // Cột Vé & Ga (Dùng cho lọc tuyến)
    private static final String COL_VE_ID = "veID";
    private static final String COL_VE_GA_DI_ID = "gaDiID";
    private static final String COL_VE_GA_DEN_ID = "gaDenID";
    private static final String COL_GA_ID = "gaID";
    private static final String COL_TEN_GA = "tenGa";

    private TimeGrouping buildTimeGrouping(String loaiThoiGian) {
        switch (loaiThoiGian) {
            case "Theo ngày":
                String dayExpr = "CONVERT(date, b.thoiDiemTao)";
                String selectExpr = "CONVERT(varchar(10), " + dayExpr + ", 103)";
                return new TimeGrouping(selectExpr, dayExpr + ", " + selectExpr, dayExpr);
            case "Theo tháng":
                String yearExpr = "YEAR(b.thoiDiemTao)";
                String monthExpr = "MONTH(b.thoiDiemTao)";
                String sel = "RIGHT('0' + CAST(" + monthExpr + " AS varchar(2)), 2) + '/' + CAST(" + yearExpr + " AS varchar(4))";
                return new TimeGrouping(sel, yearExpr + ", " + monthExpr + ", " + sel, yearExpr + ", " + monthExpr);
            case "Theo năm":
            default:
                String yExpr = "YEAR(b.thoiDiemTao)";
                String selY = "CAST(" + yExpr + " AS varchar(4))";
                return new TimeGrouping(selY, yExpr + ", " + selY, yExpr);
        }
    }

    /**
     * CTE Base: Lọc dữ liệu cơ bản theo thời gian, tuyến, nhân viên, hình thức thanh toán.
     * Lưu ý: Không lọc >0 hay <0 ở đây để lấy toàn bộ lịch sử giao dịch.
     */
    private String buildBaseCTE(String loaiTuyen, String nhanVienID, Integer isTienMat) {
        StringBuilder sb = new StringBuilder();
        sb.append("WITH Base AS ( ");
        sb.append(" SELECT ");
        sb.append("   hd.").append(COL_HD_ID).append(" AS hoaDonID, ");
        sb.append("   hd.").append(COL_HD_THOI_DIEM_TAO).append(" AS thoiDiemTao, ");
        sb.append("   hd.").append(COL_HD_TONG_TIEN).append(" AS tongTien, "); // Chứa cả âm và dương
        sb.append("   hd.").append(COL_HD_IS_TIEN_MAT).append(" AS isThanhToanTienMat, ");
        // Tính thu dịch vụ (nếu cần hiển thị riêng)
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
        sb.append("   hd.").append(COL_HD_IS_TIEN_MAT).append(" ");
        sb.append(") ");

        return sb.toString();
    }

    private void setFilterParameters(PreparedStatement pstmt, LocalDate tuNgay, LocalDate denNgay,
                                     String loaiTuyen, String tenGaDi, String tenGaDen,
                                     String nhanVienID, Integer isTienMat) throws SQLException {
        int index = 1;
        pstmt.setDate(index++, java.sql.Date.valueOf(tuNgay));
        pstmt.setDate(index++, java.sql.Date.valueOf(denNgay.plusDays(1)));
        if ("Theo Ga đi/đến".equals(loaiTuyen)) {
            pstmt.setString(index++, tenGaDi);
            pstmt.setString(index++, tenGaDen);
        }
        if (nhanVienID != null) pstmt.setString(index++, nhanVienID);
        if (isTienMat != null) pstmt.setInt(index++, isTienMat);
    }

    // ======================================================================
    //  1. THỐNG KÊ CHI TIẾT (BẢNG & BIỂU ĐỒ)
    // ======================================================================
    public Map<String, ThongKeChiTietItem> getThongKeDoanhThuChiTiet(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, Integer isTienMat) throws SQLException {

        Map<String, ThongKeChiTietItem> results = new LinkedHashMap<>();
        TimeGrouping tg = buildTimeGrouping(loaiThoiGian);
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);

        /*
           LOGIC TÍNH TOÁN:
           - SoLuongHoaDonBan: ID LIKE 'HD%' AND NOT 'HDHV%' AND NOT 'HDDV%'
           - SoLuongHoaDonHoanDoi: ID LIKE 'HDHV%' OR 'HDDV%'
           - TongDoanhThu: SUM(tongTien) WHERE tongTien > 0
           - TongChi: ABS(SUM(tongTien)) WHERE tongTien < 0
           - LoiNhuan: DoanhThu - TongChi (hoặc đơn giản là SUM(tongTien) vì số âm đã trừ đi rồi)
        */
        String sql = baseCTE +
                "SELECT " +
                "  " + tg.selectExpr + " AS ThoiGian, " +
                // Đếm hóa đơn bán (trừ hoàn/đổi)
                "  COUNT(CASE WHEN b.hoaDonID NOT LIKE 'HDHV%' AND b.hoaDonID NOT LIKE 'HDDV%' THEN 1 END) AS SoLuongHoaDonBan, " +
                // Đếm hóa đơn hoàn/đổi
                "  COUNT(CASE WHEN b.hoaDonID LIKE 'HDHV%' OR b.hoaDonID LIKE 'HDDV%' THEN 1 END) AS SoLuongHoaDonHoanDoi, " +
                "  SUM(b.thuDichVu) AS TongThuDichVu, " +
                // Tổng doanh thu (chỉ lấy số dương)
                "  ISNULL(SUM(CASE WHEN b.tongTien > 0 THEN b.tongTien ELSE 0 END), 0) AS TongDoanhThu, " +
                // Tổng chi (lấy trị tuyệt đối của số âm)
                "  ISNULL(ABS(SUM(CASE WHEN b.tongTien < 0 THEN b.tongTien ELSE 0 END)), 0) AS TongChi, " +
                // Lợi nhuận = Tổng tất cả (dương + âm)
                "  ISNULL(SUM(b.tongTien), 0) AS LoiNhuan " +
                "FROM Base b " +
                "GROUP BY " + tg.groupBy + " " +
                "ORDER BY " + tg.orderBy;

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String key = rs.getString("ThoiGian");
                    ThongKeChiTietItem item = new ThongKeChiTietItem(key);
                    item.soLuongHoaDonBan = rs.getInt("SoLuongHoaDonBan");
                    item.soLuongHoaDonHoanDoi = rs.getInt("SoLuongHoaDonHoanDoi");
                    item.tongThuDichVu = rs.getDouble("TongThuDichVu");
                    item.tongDoanhThu = rs.getDouble("TongDoanhThu");
                    item.tongChi = rs.getDouble("TongChi");
                    item.loiNhuan = rs.getDouble("LoiNhuan");
                    results.put(key, item);
                }
            }
        }
        return results;
    }

    /**
     * Tổng Hóa Đơn Bán: Hóa đơn thường (không phải HDHV/HDDV)
     */
    public int getTongHoaDonBan(LocalDate tuNgay, LocalDate denNgay,
                                String loaiTuyen, String tenGaDi, String tenGaDen,
                                String nhanVienID, Integer isTienMat) throws SQLException {
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT COUNT(*) AS Cnt FROM Base b " +
                "WHERE b.hoaDonID NOT LIKE 'HDHV%' AND b.hoaDonID NOT LIKE 'HDDV%'"; // Logic mới

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("Cnt");
            }
        }
        return 0;
    }

    /**
     * Tổng Hóa Đơn Hoàn Trả: HDHV hoặc HDDV
     */
    public int getTongHoaDonHoanDoi(LocalDate tuNgay, LocalDate denNgay,
                                    String loaiTuyen, String tenGaDi, String tenGaDen,
                                    String nhanVienID, Integer isTienMat) throws SQLException {
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT COUNT(*) AS Cnt FROM Base b " +
                "WHERE b.hoaDonID LIKE 'HDHV%' OR b.hoaDonID LIKE 'HDDV%'"; // Logic mới

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("Cnt");
            }
        }
        return 0;
    }

    // ======================================================================
    //  2. CÁC HÀM CARD TỔNG QUAN
    // ======================================================================

    /**
     * Tổng Thu Dịch Vụ (Giữ nguyên logic cũ nhưng chạy trên Base mới)
     */
    public double getTongThuDichVu(LocalDate tuNgay, LocalDate denNgay,
                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                   String nhanVienID, Integer isTienMat) throws SQLException {
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE + "SELECT ISNULL(SUM(b.thuDichVu), 0) AS Tong FROM Base b";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("Tong");
            }
        }
        return 0.0;
    }

    /**
     * Lấy Map chứa: Doanh Thu, Chi, Lợi Nhuận
     * Logic mới:
     * - Doanh Thu = Tổng tiền dương
     * - Chi = Tổng tiền âm (ABS)
     * - Lợi nhuận = Tổng tiền (dương + âm)
     */
    public Map<String, Double> getTongDoanhThuChiLoiNhuan(LocalDate tuNgay, LocalDate denNgay,
                                                          String loaiTuyen, String tenGaDi, String tenGaDen,
                                                          String nhanVienID, Integer isTienMat) throws SQLException {
        Map<String, Double> result = new HashMap<>();
        String baseCTE = buildBaseCTE(loaiTuyen, nhanVienID, isTienMat);
        String sql = baseCTE +
                "SELECT " +
                "  ISNULL(SUM(CASE WHEN b.tongTien > 0 THEN b.tongTien ELSE 0 END), 0) AS TongDoanhThu, " +
                "  ISNULL(ABS(SUM(CASE WHEN b.tongTien < 0 THEN b.tongTien ELSE 0 END)), 0) AS TongChi, " +
                "  ISNULL(SUM(b.tongTien), 0) AS LoiNhuan " +
                "FROM Base b";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setFilterParameters(pstmt, tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put("doanhThu", rs.getDouble("TongDoanhThu"));
                    result.put("chi", rs.getDouble("TongChi"));
                    result.put("loiNhuan", rs.getDouble("LoiNhuan"));
                }
            }
        }
        return result;
    }

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

    // Class helper để group by thời gian
    private static class TimeGrouping {
        final String selectExpr;
        final String groupBy;
        final String orderBy;

        private TimeGrouping(String selectExpr, String groupBy, String orderBy) {
            this.selectExpr = selectExpr;
            this.groupBy = groupBy;
            this.orderBy = orderBy;
        }
    }
}