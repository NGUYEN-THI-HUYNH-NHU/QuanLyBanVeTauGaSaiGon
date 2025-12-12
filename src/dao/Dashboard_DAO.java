package dao;

import connectDB.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Dashboard_DAO {

    // =========================================================================
    // 1. CÁC HÀM KPI (THẺ SỐ LIỆU TỔNG QUAN)
    // =========================================================================

    public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT SUM(tongTien) AS TongDoanhThu FROM HoaDon WHERE trangThai = 1");
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
        StringBuilder sql = new StringBuilder("SELECT COUNT(veID) AS SoVeBan FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND d.thoiDiemDatCho >= ?");
        if (endDate != null) sql.append(" AND d.thoiDiemDatCho < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoVeBan");
    }

    public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT SUM(t.sucChua) AS TongSoGhe FROM Chuyen c JOIN Tau tau ON c.tauID = tau.tauID JOIN Toa t ON t.tauID = tau.tauID WHERE 1=1");
        if (startDate != null) sql.append(" AND c.ngayDi >= ?");
        if (endDate != null) sql.append(" AND c.ngayDi <= ?");
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1; if (startDate != null) pstmt.setObject(idx++, startDate); if (endDate != null) pstmt.setObject(idx++, endDate);
            try (ResultSet rs = pstmt.executeQuery()) { if (rs.next()) return rs.getInt("TongSoGhe"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
        // Đếm hóa đơn có mã bắt đầu bằng HDHV (Hoàn) hoặc HDDV (Đổi)
        StringBuilder sql = new StringBuilder("SELECT COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        return executeCountQuery(sql.toString(), startDate, endDate, "SoLuong");
    }

    // =========================================================================
    // 2. BIỂU ĐỒ DOANH THU (CỘT)
    // =========================================================================

    public Map<LocalDate, Double> getRevenueOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT CAST(thoiDiemTao AS DATE) AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE trangThai = 1 ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY CAST(thoiDiemTao AS DATE) ORDER BY KeyDate");
        return executeQueryForDateMap(sql.toString(), startDate, endDate, "KeyDate");
    }

    public Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE trangThai = 1 ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) ORDER BY KeyDate");
        return executeQueryForDateMap(sql.toString(), startDate, endDate, "KeyDate");
    }

    public Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE trangThai = 1 ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) ORDER BY KeyDate");
        return executeQueryForDateMap(sql.toString(), startDate, endDate, "KeyDate");
    }

    // =========================================================================
    // 3. BIỂU ĐỒ ĐƯỜNG: HÓA ĐƠN (PHÂN LOẠI BÁN vs HOÀN/ĐỔI)
    // =========================================================================

    // --- A. THEO NGÀY (Cho bộ lọc Hôm nay, Tuần, Tháng) ---
    public Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate startDate, LocalDate endDate) {
        // Hóa đơn BÁN: Loại trừ HDHV và HDDV
        StringBuilder sql = new StringBuilder("SELECT CAST(thoiDiemTao AS DATE) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE trangThai = 1 AND hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%' ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY CAST(thoiDiemTao AS DATE) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }
    public Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate startDate, LocalDate endDate) {
        // Hóa đơn HOÀN/ĐỔI: Chỉ lấy HDHV hoặc HDDV
        StringBuilder sql = new StringBuilder("SELECT CAST(thoiDiemTao AS DATE) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%') ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY CAST(thoiDiemTao AS DATE) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }

    // --- B. THEO THÁNG (Cho bộ lọc Năm này) ---
    public Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE trangThai = 1 AND hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%' ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }
    public Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%') ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }

    // --- C. THEO NĂM (Cho bộ lọc Tất cả) ---
    public Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE trangThai = 1 AND hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%' ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }
    public Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder("SELECT DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%') ");
        if (startDate != null) sql.append(" AND thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND thoiDiemTao < ?");
        sql.append(" GROUP BY DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0) ORDER BY Ngay");
        return executeQueryForIntMapDateKey(sql.toString(), startDate, endDate);
    }

    // =========================================================================
    // 4. BIỂU ĐỒ NGẢ VÉ (JOIN QUA HOADONCHITIET)
    // =========================================================================

    public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT CAST(hd.thoiDiemTao AS DATE) AS Ngay, " +
                        "    CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' " +
                        "         WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' " +
                        "         WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' " +
                        "         ELSE N'Khác' END AS LoaiGhe, " +
                        "    COUNT(v.veID) AS SoLuong " +
                        "FROM Ve v " +
                        "JOIN HoaDonChiTiet hdct ON v.veID = hdct.veID " +
                        "JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID " +
                        "JOIN Ghe g ON v.gheID = g.gheID " +
                        "JOIN Toa t ON g.toaID = t.toaID " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG') "
        );

        if (startDate != null) sql.append(" AND hd.thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND hd.thoiDiemTao < ?");

        sql.append(" GROUP BY CAST(hd.thoiDiemTao AS DATE), " +
                "      CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' " +
                "           WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' " +
                "           WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' " +
                "           ELSE N'Khác' END " +
                " ORDER BY Ngay, LoaiGhe");

        Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (startDate != null) pstmt.setObject(idx++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("Ngay");
                    if (sqlDate != null) {
                        result.putIfAbsent(sqlDate.toLocalDate(), new HashMap<>());
                        result.get(sqlDate.toLocalDate()).put(rs.getString("LoaiGhe"), rs.getInt("SoLuong"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // =========================================================================
    // 5. CÁC BIỂU ĐỒ TRÒN (PIE CHART)
    // =========================================================================

    public Map<String, Integer> getCustomerSplitData(LocalDate startDate, LocalDate endDate) {
        // Sử dụng CTE để tính RFM và phân loại khách hàng
        StringBuilder sql = new StringBuilder();
        sql.append("WITH Metrics AS ( ");
        sql.append("    SELECT ");
        sql.append("        kh.khachHangID, ");
        sql.append("        SUM(v.gia) AS TongChiTieu, ");
        sql.append("        COUNT(v.veID) AS SoLanMua, ");
        sql.append("        MAX(d.thoiDiemDatCho) AS LanMuaCuoi ");
        sql.append("    FROM KhachHang kh ");
        sql.append("    JOIN DonDatCho d ON kh.khachHangID = d.khachHangID ");
        sql.append("    JOIN Ve v ON d.donDatChoID = v.donDatChoID ");
        sql.append("    WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG') ");

        if (startDate != null) sql.append(" AND d.thoiDiemDatCho >= ? ");
        if (endDate != null) sql.append(" AND d.thoiDiemDatCho < ? ");

        sql.append("    GROUP BY kh.khachHangID ");
        sql.append("), ");
        sql.append("Classified AS ( ");
        sql.append("    SELECT ");
        sql.append("        CASE ");
        sql.append("            WHEN TongChiTieu >= 10000000 THEN N'VIP' ");
        sql.append("            WHEN DATEDIFF(day, LanMuaCuoi, GETDATE()) > 180 THEN N'Ngủ đông' ");
        sql.append("            WHEN SoLanMua >= 5 THEN N'Thân thiết' ");
        sql.append("            WHEN SoLanMua >= 2 THEN N'Khách quay lại' ");
        sql.append("            ELSE N'Khách mới' ");
        sql.append("        END AS LoaiKH ");
        sql.append("    FROM Metrics ");
        sql.append(") ");
        sql.append("SELECT LoaiKH, COUNT(*) AS SoLuong FROM Classified GROUP BY LoaiKH ORDER BY SoLuong DESC");

        return executeQueryForIntMap(sql.toString(), startDate, endDate);
    }

    public Map<String, Integer> getPromotionRateData(LocalDate startDate, LocalDate endDate) {
        StringBuilder sqlTotal = new StringBuilder("SELECT COUNT(hoaDonID) AS Total FROM HoaDon WHERE 1=1");
        StringBuilder sqlPromo = new StringBuilder("SELECT COUNT(DISTINCT hd.hoaDonID) AS Promo FROM HoaDon hd JOIN HoaDonChiTiet hdct ON hd.hoaDonID=hdct.hoaDonID WHERE hdct.loaiDichVu='KHUYEN_MAI'");
        if(startDate!=null){sqlTotal.append(" AND thoiDiemTao >= ?");sqlPromo.append(" AND hd.thoiDiemTao >= ?");}
        if(endDate!=null){sqlTotal.append(" AND thoiDiemTao < ?");sqlPromo.append(" AND hd.thoiDiemTao < ?");}
        int total=0,promo=0;
        try(Connection conn=ConnectDB.getInstance().getConnection()){
            PreparedStatement p1=conn.prepareStatement(sqlTotal.toString()); int i=1;if(startDate!=null)p1.setObject(i++,startDate.atStartOfDay());if(endDate!=null)p1.setObject(i++,endDate.plusDays(1).atStartOfDay());ResultSet r1=p1.executeQuery();if(r1.next())total=r1.getInt(1);
            PreparedStatement p2=conn.prepareStatement(sqlPromo.toString()); i=1;if(startDate!=null)p2.setObject(i++,startDate.atStartOfDay());if(endDate!=null)p2.setObject(i++,endDate.plusDays(1).atStartOfDay());ResultSet r2=p2.executeQuery();if(r2.next())promo=r2.getInt(1);
        }catch(Exception e){e.printStackTrace();}
        Map<String,Integer> r=new LinkedHashMap<>(); r.put("Đã sử dụng",promo);r.put("Chưa sử dụng",total-promo);return r;
    }

    // =========================================================================
    // 6. HELPER METHODS
    // =========================================================================

    private Map<LocalDate, Double> executeQueryForDateMap(String sql, LocalDate s, LocalDate e, String col) {
        Map<LocalDate, Double> r = new LinkedHashMap<>();
        try(Connection c=ConnectDB.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;if(s!=null)p.setObject(i++,s.atStartOfDay());if(e!=null)p.setObject(i++,e.plusDays(1).atStartOfDay());
            try(ResultSet rs=p.executeQuery()){while(rs.next())if(rs.getDate(col)!=null)r.put(rs.getDate(col).toLocalDate(),rs.getDouble("DoanhThu"));}
        }catch(Exception ex){ex.printStackTrace();}return r;
    }

    private Map<String, Integer> executeQueryForIntMap(String sql, LocalDate s, LocalDate e) {
        Map<String,Integer> r=new LinkedHashMap<>();
        try(Connection c=ConnectDB.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;if(s!=null)p.setObject(i++,s.atStartOfDay());if(e!=null)p.setObject(i++,e.plusDays(1).atStartOfDay());
            try(ResultSet rs=p.executeQuery()){while(rs.next())r.put(rs.getString(1),rs.getInt(2));}
        }catch(Exception ex){ex.printStackTrace();}return r;
    }

    private Map<LocalDate, Integer> executeQueryForIntMapDateKey(String sql, LocalDate s, LocalDate e) {
        Map<LocalDate, Integer> r = new LinkedHashMap<>();
        try(Connection c=ConnectDB.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;if(s!=null)p.setObject(i++,s.atStartOfDay());if(e!=null)p.setObject(i++,e.plusDays(1).atStartOfDay());
            try(ResultSet rs=p.executeQuery()){
                while(rs.next()) {
                    if (rs.getDate("Ngay") != null)
                        r.put(rs.getDate("Ngay").toLocalDate(), rs.getInt("SoLuong"));
                }
            }
        }catch(Exception ex){ex.printStackTrace();}return r;
    }

    private int executeCountQuery(String sql, LocalDate s, LocalDate e, String col) {
        try(Connection c=ConnectDB.getInstance().getConnection();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;if(s!=null)p.setObject(i++,s.atStartOfDay());if(e!=null)p.setObject(i++,e.plusDays(1).atStartOfDay());
            try(ResultSet rs=p.executeQuery()){if(rs.next())return rs.getInt(col);}
        }catch(Exception ex){ex.printStackTrace();}return 0;
    }
}