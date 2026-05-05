package dao.impl;

import db.JPAUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DashboardDAO extends BaseDAO implements dao.IDashboardDAO {

    private <R> R doWithConnection(SqlWork<R> work) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Session session = em.unwrap(Session.class);
            final Object[] result = {null};
            final RuntimeException[] error = {null};
            session.doWork(conn -> {
                try {
                    result[0] = work.execute(conn);
                } catch (SQLException e) {
                    error[0] = new RuntimeException(e);
                }
            });
            if (error[0] != null) throw error[0];
            return (R) result[0];
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(tongTien) AS TongDoanhThu FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%')");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");

        return doWithConnection(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
                if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getDouble("TongDoanhThu");
                }
            }
            return 0.0;
        });
    }

    // =========================================================================
    // 1. KPI & TỔNG QUAN
    // =========================================================================

    @Override
    public int getKpiTicketsSold(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(veID) AS SoVeBan FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND d.thoiDiemDatCho >= ?");
        if (endDate != null) sql.append(" AND d.thoiDiemDatCho < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoVeBan");
    }

    @Override
    public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(t.sucChua) AS TongSoGhe FROM Chuyen c JOIN Tau tau ON c.tauID = tau.tauID JOIN Toa t ON t.tauID = tau.tauID WHERE 1=1");
        if (startDate != null) sql.append(" AND c.ngayDi >= ?");
        if (endDate != null) sql.append(" AND c.ngayDi <= ?");

        return doWithConnection(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (startDate != null) pstmt.setObject(idx++, java.sql.Date.valueOf(startDate));
                if (endDate != null) pstmt.setObject(idx++, java.sql.Date.valueOf(endDate));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) return rs.getInt("TongSoGhe");
                }
            }
            return 0;
        });
    }

    @Override
    public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoLuong");
    }

    @Override
    public Map<String, Double> getRevenueByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT isThanhToanTienMat, SUM(tongTien) FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%') ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY isThanhToanTienMat");

        return doWithConnection(conn -> {
            Map<String, Double> data = new LinkedHashMap<>();
            data.put("Tiền mặt", 0.0);
            data.put("Chuyển khoản", 0.0);
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
                if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        boolean isCash = rs.getBoolean(1);
                        double value = rs.getDouble(2);
                        if (isCash) data.put("Tiền mặt", value);
                        else data.put("Chuyển khoản", value);
                    }
                }
            }
            return data;
        });
    }

    // =========================================================================
    // 2. DOANH THU THEO PHƯƠNG THỨC THANH TOÁN
    // =========================================================================

    private Map<LocalDate, Double> getRevenueData(LocalDate s, LocalDate e, String datePart) {
        StringBuilder sql = new StringBuilder("SELECT " + datePart
                + " AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE (hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%') ");
        if (s != null) sql.append(" AND thoiDiemTao >= ?");
        if (e != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY " + datePart + " ORDER BY KeyDate");
        return executeQueryForDateMap(sql.toString(), s, e, "KeyDate");
    }

    // =========================================================================
    // 3. DOANH THU THEO THỜI GIAN
    // =========================================================================

    @Override
    public Map<LocalDate, Double> getRevenueOverTime(LocalDate s, LocalDate e) {
        return getRevenueData(s, e, "CAST(thoiDiemTao AS DATE)");
    }

    @Override
    public Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate s, LocalDate e) {
        return getRevenueData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)");
    }

    @Override
    public Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate s, LocalDate e) {
        return getRevenueData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)");
    }

    @Override
    public Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", false);
    }

    // =========================================================================
    // 4. HÓA ĐƠN
    // =========================================================================

    @Override
    public Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", true);
    }

    @Override
    public Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", false);
    }

    @Override
    public Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", true);
    }

    @Override
    public Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", false);
    }

    @Override
    public Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate s, LocalDate e) {
        return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", true);
    }

    private Map<LocalDate, Integer> getInvoiceData(LocalDate s, LocalDate e, String datePart, boolean isRefund) {
        String cond = isRefund
                ? "(hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')"
                : "(hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%')";
        StringBuilder sql = new StringBuilder("SELECT " + datePart + " AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE " + cond);
        if (s != null) sql.append(" AND thoiDiemTao >= ?");
        if (e != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY " + datePart + " ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), s, e);
    }

    @Override
    public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT CAST(hd.thoiDiemTao AS DATE) AS Ngay, "
                        + "CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END AS LoaiGhe, "
                        + "COUNT(v.veID) AS SoLuong "
                        + "FROM Ve v JOIN HoaDonChiTiet hdct ON v.veID = hdct.veID JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID JOIN Ghe g ON v.gheID = g.gheID JOIN Toa t ON g.toaID = t.toaID "
                        + "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG') ");
        if (startDate != null) sql.append(" AND hd.thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND hd.thoiDiemTao < ?");
        sql.append(" GROUP BY CAST(hd.thoiDiemTao AS DATE), CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END ORDER BY Ngay, LoaiGhe");

        return doWithConnection(conn -> {
            Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
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
            }
            return result;
        });
    }

    @Override
    public int[] getTripOccupancyAlerts(LocalDate startDate, LocalDate endDate) {
        return new int[]{
                getHighOccupancyList(startDate, endDate).size(),
                getLowOccupancyList(startDate, endDate).size()
        };
    }

    // =========================================================================
    // 5. CẢNH BÁO & CHI TIẾT CHUYẾN TÀU
    // =========================================================================

    @Override
    public List<Object[]> getHighOccupancyList(LocalDate startDate, LocalDate endDate) {
        return getOccupancyList(startDate, endDate, true);
    }

    @Override
    public List<Object[]> getLowOccupancyList(LocalDate startDate, LocalDate endDate) {
        return getOccupancyList(startDate, endDate, false);
    }

    private List<Object[]> getOccupancyList(LocalDate startDate, LocalDate endDate, boolean isHighOccupancy) {
        LocalDate start = startDate != null ? startDate : LocalDate.of(2000, 1, 1);
        LocalDate end = endDate != null ? endDate : LocalDate.of(2099, 12, 31);
        String sql = "SELECT c.chuyenID, c.tuyenID, c.ngayDi, c.gioDi, "
                + "(SELECT moTa FROM Tuyen WHERE tuyenID = c.tuyenID) as TenTuyen, "
                + "(SELECT COUNT(*) FROM Ve v WHERE v.chuyenID = c.chuyenID AND v.trangThai = 'DA_BAN') AS SoVeDaBan, "
                + "(SELECT COUNT(*) FROM Ghe g JOIN Toa t ON g.toaID = t.toaID WHERE t.tauID = c.tauID) AS TongSoGhe "
                + "FROM Chuyen c WHERE c.ngayDi BETWEEN ? AND ?";

        return doWithConnection(conn -> {
            List<Object[]> list = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, java.sql.Date.valueOf(start));
                ps.setDate(2, java.sql.Date.valueOf(end));
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
                            if (parts.length >= 2) {
                                gaDi = parts[0].trim();
                                gaDen = parts[1].trim();
                            }
                        }

                        boolean match = false;
                        if (isHighOccupancy) {
                            if (tyLe >= 90) match = true;
                        } else {
                            if (tyLe < 40) {
                                LocalDateTime now = LocalDateTime.now();
                                LocalDateTime departure = LocalDateTime.of(ngayDi.toLocalDate(), gioDi.toLocalTime());
                                if (departure.isAfter(now) && departure.isBefore(now.plusHours(48))) match = true;
                            }
                        }

                        if (match) {
                            list.add(new Object[]{chuyenID, tuyenID, gaDi, gaDen,
                                    ngayDi.toLocalDate(), gioDi.toLocalTime(),
                                    soVe, Math.round(tyLe * 100.0) / 100.0});
                        }
                    }
                }
            }
            return list;
        });
    }

    private Map<LocalDate, Double> executeQueryForDateMap(String sql, LocalDate s, LocalDate e, String col) {
        return doWithConnection(conn -> {
            Map<LocalDate, Double> r = new LinkedHashMap<>();
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                int i = 1;
                if (s != null) p.setObject(i++, s.atStartOfDay());
                if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getDate(col) != null)
                            r.put(rs.getDate(col).toLocalDate(), rs.getDouble("DoanhThu"));
                    }
                }
            }
            return r;
        });
    }

    // =========================================================================
    // 6. HELPER METHODS
    // =========================================================================

    private Map<LocalDate, Integer> executeQueryForIntMapDateKey(String sql, LocalDate s, LocalDate e) {
        return doWithConnection(conn -> {
            Map<LocalDate, Integer> r = new LinkedHashMap<>();
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                int i = 1;
                if (s != null) p.setObject(i++, s.atStartOfDay());
                if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        if (rs.getDate("Ngay") != null)
                            r.put(rs.getDate("Ngay").toLocalDate(), rs.getInt("SoLuong"));
                    }
                }
            }
            return r;
        });
    }

    private int executeCountQuery(String sql, LocalDate s, LocalDate e, String col) {
        return doWithConnection(conn -> {
            try (PreparedStatement p = conn.prepareStatement(sql)) {
                int i = 1;
                if (s != null) p.setObject(i++, s.atStartOfDay());
                if (e != null) p.setObject(i++, e.plusDays(1).atStartOfDay());
                try (ResultSet rs = p.executeQuery()) {
                    if (rs.next()) return rs.getInt(col);
                }
            }
            return 0;
        });
    }

    @FunctionalInterface
    interface SqlWork<R> {
        R execute(java.sql.Connection conn) throws SQLException;
    }
}