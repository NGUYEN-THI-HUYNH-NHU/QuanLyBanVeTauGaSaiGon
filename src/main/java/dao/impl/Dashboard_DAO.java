package dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import connectDB.ConnectDB;

public class Dashboard_DAO {

    // =========================================================================
    // 1. KPI & TỔNG QUAN
    // =========================================================================

    public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
        // Tính tổng tiền bán (loại trừ hóa đơn hoàn/đổi)
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(tongTien) AS TongDoanhThu FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%')");

        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public int getKpiTicketsSold(LocalDate startDate, LocalDate endDate) {
        // Lưu ý: trangThai này là của bảng VE (Ve), không phải HoaDon nên giữ nguyên
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(veID) AS SoVeBan FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND d.thoiDiemDatCho >= ?");
        if (endDate != null) sql.append(" AND d.thoiDiemDatCho < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoVeBan");
    }

    public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(t.sucChua) AS TongSoGhe FROM Chuyen c JOIN Tau tau ON c.tauID = tau.tauID JOIN Toa t ON t.tauID = tau.tauID WHERE 1=1");
        if (startDate != null) sql.append(" AND c.ngayDi >= ?");
        if (endDate != null) sql.append(" AND c.ngayDi <= ?");
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (startDate != null) pstmt.setObject(idx++, java.sql.Date.valueOf(startDate));
            if (endDate != null) pstmt.setObject(idx++, java.sql.Date.valueOf(endDate));
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt("TongSoGhe"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoLuong");
    }

    // =========================================================================
    // 2. BIỂU ĐỒ DOANH THU THEO PHƯƠNG THỨC THANH TOÁN (PIE CHART)
    // =========================================================================

    /**
     * Lấy doanh thu chia theo isThanhToanTienMat (1: Tiền mặt, 0: Chuyển khoản)
     */
    public Map<String, Double> getRevenueByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        // Group by isThanhToanTienMat
        StringBuilder sql = new StringBuilder(
                "SELECT isThanhToanTienMat, SUM(tongTien) FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%') ");

        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");

        sql.append(" GROUP BY isThanhToanTienMat");

        Map<String, Double> data = new LinkedHashMap<>();
        data.put("Tiền mặt", 0.0);
        data.put("Chuyển khoản", 0.0);

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Cột 1 là isThanhToanTienMat (boolean/bit)
                    boolean isCash = rs.getBoolean(1);
                    double value = rs.getDouble(2);

                    if (isCash) {
                        data.put("Tiền mặt", value);
                    } else {
                        data.put("Chuyển khoản", value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // =========================================================================
    // 3. CÁC BIỂU ĐỒ KHÁC (Chart Data)
    // =========================================================================

    // Helper cho Revenue Over Time (Nếu còn dùng)
    private Map<LocalDate, Double> getRevenueData(LocalDate s, LocalDate e, String datePart) {
        StringBuilder sql = new StringBuilder("SELECT " + datePart
                + " AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%') ");
        if (s != null) sql.append(" AND thoiDiemTao >= ?");
        if (e != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY " + datePart + " ORDER BY KeyDate");
        return executeQueryForDateMap(sql.toString(), s, e, "KeyDate");
    }

    // Các hàm lấy doanh thu theo thời gian (Cho biểu đồ cột cũ - giữ lại để tránh lỗi code cũ)
    public Map<LocalDate, Double> getRevenueOverTime(LocalDate s, LocalDate e) { return getRevenueData(s, e, "CAST(thoiDiemTao AS DATE)"); }
    public Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate s, LocalDate e) { return getRevenueData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)"); }
    public Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate s, LocalDate e) { return getRevenueData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)"); }

    // Các hàm Invoice (Hóa đơn)
    public Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", false); }
    public Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", true); }
    public Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", false); }
    public Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", true); }
    public Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", false); }
    public Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate s, LocalDate e) { return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", true); }

    private Map<LocalDate, Integer> getInvoiceData(LocalDate s, LocalDate e, String datePart, boolean isRefund) {
        String cond = isRefund ? "(hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')" : "(hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%')";
        StringBuilder sql = new StringBuilder("SELECT " + datePart + " AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE " + cond);
        if (s != null) sql.append(" AND thoiDiemTao >= ?");
        if (e != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY " + datePart + " ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), s, e);
    }

    public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT CAST(hd.thoiDiemTao AS DATE) AS Ngay, "
                + "    CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END AS LoaiGhe, "
                + "    COUNT(v.veID) AS SoLuong "
                + "FROM Ve v JOIN HoaDonChiTiet hdct ON v.veID = hdct.veID JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID JOIN Ghe g ON v.gheID = g.gheID JOIN Toa t ON g.toaID = t.toaID "
                + "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG') ");
        if (startDate != null) sql.append(" AND hd.thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND hd.thoiDiemTao < ?");
        sql.append(" GROUP BY CAST(hd.thoiDiemTao AS DATE), CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END ORDER BY Ngay, LoaiGhe");

        Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("Ngay");
                    if (d != null) {
                        result.putIfAbsent(d.toLocalDate(), new HashMap<>());
                        result.get(d.toLocalDate()).put(rs.getString("LoaiGhe"), rs.getInt("SoLuong"));
                    }
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return result;
    }

    // =========================================================================
    // 4. CẢNH BÁO & CHI TIẾT CHUYẾN TÀU
    // =========================================================================

    public int[] getTripOccupancyAlerts(LocalDate startDate, LocalDate endDate) {
        int[] counts = { 0, 0 };
        List<Object[]> highList = getOccupancyList(startDate, endDate, true);
        List<Object[]> lowList = getOccupancyList(startDate, endDate, false);
        counts[0] = highList.size();
        counts[1] = lowList.size();
        return counts;
    }

    public List<Object[]> getHighOccupancyList(LocalDate startDate, LocalDate endDate) {
        return getOccupancyList(startDate, endDate, true);
    }

    public List<Object[]> getLowOccupancyList(LocalDate startDate, LocalDate endDate) {
        return getOccupancyList(startDate, endDate, false);
    }

    private List<Object[]> getOccupancyList(LocalDate startDate, LocalDate endDate, boolean isHighOccupancy) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT c.chuyenID, c.tuyenID, c.ngayDi, c.gioDi, "
                + "    (SELECT moTa FROM Tuyen WHERE tuyenID = c.tuyenID) as TenTuyen, "
                + "    (SELECT COUNT(*) FROM Ve v WHERE v.chuyenID = c.chuyenID AND v.trangThai = 'DA_BAN') AS SoVeDaBan, "
                + "    (SELECT COUNT(*) FROM Ghe g JOIN Toa t ON g.toaID = t.toaID WHERE t.tauID = c.tauID) AS TongSoGhe "
                + "FROM Chuyen c WHERE c.ngayDi BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
            if (endDate == null) endDate = LocalDate.of(2099, 12, 31);
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int soVe = rs.getInt("SoVeDaBan");
                    int tongGhe = rs.getInt("TongSoGhe");
                    if (tongGhe == 0) continue;

                    double tyLe = (double) soVe / tongGhe * 100;
                    String chuyenID = rs.getString("chuyenID");
                    String tuyenID = rs.getString("tuyenID");
                    String moTaTuyen = rs.getString("TenTuyen");
                    java.sql.Date ngayDi = rs.getDate("ngayDi");
                    java.sql.Time gioDi = rs.getTime("gioDi");

                    String gaDi = "N/A", gaDen = "N/A";
                    if (moTaTuyen != null && moTaTuyen.contains("-")) {
                        String[] parts = moTaTuyen.split("-");
                        if (parts.length >= 2) { gaDi = parts[0].trim(); gaDen = parts[1].trim(); }
                    }

                    boolean match = false;
                    if (isHighOccupancy) {
                        if (tyLe >= 90) match = true;
                    } else {
                        if (tyLe < 40) {
                            LocalDateTime now = LocalDateTime.now();
                            LocalDateTime departure = LocalDateTime.of(ngayDi.toLocalDate(), gioDi.toLocalTime());
                            if (departure.isAfter(now) && departure.isBefore(now.plusHours(48))) {
                                match = true;
                            }
                        }
                    }

                    if (match) {
                        list.add(new Object[] { chuyenID, tuyenID, gaDi, gaDen, ngayDi.toLocalDate(),
                                gioDi.toLocalTime(), soVe, Math.round(tyLe * 100.0) / 100.0 });
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // =========================================================================
    // 5. HELPER METHODS
    // =========================================================================
    private Map<LocalDate, Double> executeQueryForDateMap(String sql, LocalDate s, LocalDate e, String col) {
        Map<LocalDate, Double> r = new LinkedHashMap<>();
        try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            int i = 1;
            if (s != null) p.setObject(i++, s.atStartOfDay());
            if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    if (rs.getDate(col) != null) r.put(rs.getDate(col).toLocalDate(), rs.getDouble("DoanhThu"));
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return r;
    }

    private Map<LocalDate, Integer> executeQueryForIntMapDateKey(String sql, LocalDate s, LocalDate e) {
        Map<LocalDate, Integer> r = new LinkedHashMap<>();
        try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            int i = 1;
            if (s != null) p.setObject(i++, s.atStartOfDay());
            if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    if (rs.getDate("Ngay") != null) r.put(rs.getDate("Ngay").toLocalDate(), rs.getInt("SoLuong"));
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return r;
    }

    private int executeCountQuery(String sql, LocalDate s, LocalDate e, String col) {
        try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            int i = 1;
            if (s != null) p.setObject(i++, s.atStartOfDay());
            if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
            try (ResultSet rs = p.executeQuery()) { if (rs.next()) return rs.getInt(col); }
        } catch (Exception ex) { ex.printStackTrace(); }
        return 0;
    }
}