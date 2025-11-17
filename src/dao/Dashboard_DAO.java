package dao;

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Access Object (DAO) để lấy dữ liệu cho Dashboard.
 * [CẬP NHẬT] Thêm 2 hàm mới cho KPI Tỷ lệ lấp đầy và Tỷ lệ đổi trả.
 */
public class Dashboard_DAO {

    // ... (Các hàm getKpiTotalRevenue, getKpiTicketsSold, getKpiUniqueCustomers không đổi) ...

    /**
     * [KPI 1] Lấy tổng doanh thu từ vé bán trong một khoảng thời gian.
     */
    public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(v.gia) AS TongDoanhThu " +
                        "FROM Ve v " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    /**
     * [KPI 2] Lấy tổng số vé đã bán trong một khoảng thời gian.
     */
    public int getKpiTicketsSold(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(veID) AS SoVeBan " +
                        "FROM Ve " +
                        "WHERE trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND thoiDiemBan < ?");
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("SoVeBan");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * [KPI 3 - CŨ] Lấy số lượng khách hàng duy nhất đã mua vé.
     * (Hàm này không còn được dùng ở KPI, nhưng vẫn hữu ích)
     */
    public int getKpiUniqueCustomers(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT khachHangID) AS SoKhachHang " +
                        "FROM Ve " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND thoiDiemBan < ?");
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("SoKhachHang");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * [KPI 4 - CŨ] Lấy tuyến có doanh thu cao nhất.
     * (Hàm này không còn được dùng ở KPI, nhưng vẫn hữu ích)
     */
    public Map<String, Double> getKpiTopRevenueRoute(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT TOP 1 (t.moTa + ' (' + tau.tenTau + ')') AS TenTuyen, SUM(v.gia) AS TongDoanhThu " +
                        "FROM Ve v " +
                        "JOIN Chuyen c ON v.chuyenID = c.chuyenID " +
                        "JOIN Tuyen t ON c.tuyenID = t.tuyenID " +
                        "JOIN Tau tau ON c.tauID = tau.tauID " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        sql.append(" GROUP BY t.moTa, tau.tenTau ORDER BY TongDoanhThu DESC");
        Map<String, Double> result = new HashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) result.put(rs.getString("TenTuyen"), rs.getDouble("TongDoanhThu"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // ... (Các hàm getRevenueOverTime, getTop5RevenueTrips, ... không đổi) ...
    public Map<LocalDate, Double> getRevenueOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT CAST(v.thoiDiemBan AS DATE) AS Ngay, SUM(v.gia) AS DoanhThu " +
                        "FROM Ve v " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        sql.append(" GROUP BY CAST(v.thoiDiemBan AS DATE) ORDER BY Ngay");
        Map<LocalDate, Double> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.put(rs.getDate("Ngay").toLocalDate(), rs.getDouble("DoanhThu"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<String, Double> getTop5RevenueTrips(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT TOP 5 (t.moTa + ' (' + tau.tenTau + ')') AS TenChuyen, SUM(v.gia) AS TongDoanhThu " +
                        "FROM Ve v " +
                        "JOIN Chuyen c ON v.chuyenID = c.chuyenID " +
                        "JOIN Tuyen t ON c.tuyenID = t.tuyenID " +
                        "JOIN Tau tau ON c.tauID = tau.tauID " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        sql.append(" GROUP BY t.moTa, tau.tenTau ORDER BY TongDoanhThu DESC");
        Map<String, Double> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.put(rs.getString("TenChuyen"), rs.getDouble("TongDoanhThu"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<String, Integer> getCustomerTypeDistribution(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT ldt.moTa, COUNT(DISTINCT v.khachHangID) AS SoLuong " +
                        "FROM Ve v " +
                        "JOIN KhachHang kh ON v.khachHangID = kh.khachHangID " +
                        "JOIN LoaiDoiTuong ldt ON kh.loaiDoiTuongID = ldt.loaiDoiTuongID " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        sql.append(" GROUP BY ldt.moTa");
        Map<String, Integer> result = new HashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.put(rs.getString("moTa"), rs.getInt("SoLuong"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT CAST(v.thoiDiemBan AS DATE) AS Ngay, ht.moTa AS LoaiGhe, COUNT(v.veID) AS SoLuong " +
                        "FROM Ve v " +
                        "JOIN Ghe g ON v.gheID = g.gheID " +
                        "JOIN Toa t ON g.toaID = t.toaID " +
                        "JOIN HangToa ht ON t.hangToaID = ht.hangToaID " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND v.thoiDiemBan >= ?");
        if (endDate != null) sql.append(" AND v.thoiDiemBan < ?");
        sql.append(" GROUP BY CAST(v.thoiDiemBan AS DATE), ht.moTa ");
        sql.append(" ORDER BY Ngay, LoaiGhe");
        Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                    String loaiGhe = rs.getString("LoaiGhe");
                    int soLuong = rs.getInt("SoLuong");
                    result.putIfAbsent(ngay, new HashMap<>());
                    result.get(ngay).put(loaiGhe, soLuong);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<String, Integer> getTop5Promotions(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT TOP 5 km.maKhuyenMai, COUNT(sdkm.suDungKhuyenMaiID) AS SoLanSuDung " +
                        "FROM SuDungKhuyenMai sdkm " +
                        "JOIN KhuyenMai km ON sdkm.khuyenMaiID = km.khuyenMaiID " +
                        "JOIN HoaDonChiTiet hdct ON sdkm.hoaDonChiTietID = hdct.hoaDonChiTietID " +
                        "JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND hd.thoiDiemTao >= ?");
        if (endDate != null) sql.append(" AND hd.thoiDiemTao < ?");
        sql.append(" GROUP BY km.maKhuyenMai ORDER BY SoLanSuDung DESC");
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) result.put(rs.getString("maKhuyenMai"), rs.getInt("SoLanSuDung"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<String, Integer> getEmployeeStats() {
        String sql = "SELECT vtnv.moTa, COUNT(nv.nhanVienID) AS SoLuong " +
                "FROM NhanVien nv " +
                "JOIN VaiTroNhanVien vtnv ON nv.vaiTroNhanVienID = vtnv.vaiTroNhanVienID " +
                "WHERE nv.isHoatDong = 1 " +
                "GROUP BY vtnv.moTa";
        Map<String, Integer> result = new HashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) result.put(rs.getString("moTa"), rs.getInt("SoLuong"));
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
    public Map<String, Integer> getRefundExchangeStats(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT loaiGiaoDich, COUNT(giaoDichHoanDoiID) AS SoLuong " +
                        "FROM GiaoDichHoanDoi " +
                        "WHERE 1=1");
        if (startDate != null) sql.append(" AND thoiDiemGiaoDich >= ?");
        if (endDate != null) sql.append(" AND thoiDiemGiaoDich < ?");
        sql.append(" GROUP BY loaiGiaoDich");
        Map<String, Integer> result = new HashMap<>();
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (startDate != null) pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            if (endDate != null) pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String loaiGD = rs.getString("loaiGiaoDich");
                    String tenHienThi = loaiGD.equals("HOAN_VE") ? "Hoàn Vé" : "Đổi Vé";
                    result.put(tenHienThi, rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    // =========================================================================
    // [HÀM MỚI] Cho KPI Tỷ Lệ Lấp Đầy
    // =========================================================================
    public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
        // Tính toán đơn giản: Tổng sức chứa của tất cả các chuyến đã chạy
        StringBuilder sql = new StringBuilder(
                "SELECT SUM(t.sucChua) AS TongSoGhe " +
                        "FROM Chuyen c " +
                        "JOIN Tau tau ON c.tauID = tau.tauID " +
                        "JOIN Toa t ON t.tauID = tau.tauID " +
                        "WHERE 1=1");

        // Lọc theo ngayDi của bảng Chuyen
        if (startDate != null) {
            sql.append(" AND c.ngayDi >= ?");
        }
        if (endDate != null) {
            // Dùng <= cho endDate vì ngayDi là kiểu DATE
            sql.append(" AND c.ngayDi <= ?");
        }

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate);
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TongSoGhe");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // =========================================================================
    // [HÀM MỚI] Cho KPI Tỷ Lệ Đổi Trả
    // =========================================================================
    public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(giaoDichHoanDoiID) AS SoLuong " +
                        "FROM GiaoDichHoanDoi " +
                        "WHERE 1=1");

        if (startDate != null) {
            sql.append(" AND thoiDiemGiaoDich >= ?");
        }
        if (endDate != null) {
            sql.append(" AND thoiDiemGiaoDich < ?");
        }

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoLuong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // =========================================================================
    // [HÀM MỚI] Cho Form Chi Tiết: Lấy Top 10 Doanh thu theo Tuyến
    // =========================================================================
    public Map<String, Double> getRevenueByRoute(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT TOP 10 t.moTa AS TenTuyen, SUM(v.gia) AS TongDoanhThu " + // Lấy TOP 10
                        "FROM Ve v " +
                        "JOIN Chuyen c ON v.chuyenID = c.chuyenID " +
                        "JOIN Tuyen t ON c.tuyenID = t.tuyenID " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

        if (startDate != null) {
            sql.append(" AND v.thoiDiemBan >= ?");
        }
        if (endDate != null) {
            sql.append(" AND v.thoiDiemBan < ?");
        }

        sql.append(" GROUP BY t.moTa ORDER BY TongDoanhThu DESC");

        Map<String, Double> result = new LinkedHashMap<>(); // Giữ thứ tự
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("TenTuyen"), rs.getDouble("TongDoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    // =========================================================================
    // [HÀM MỚI] Cho Form Chi Tiết: Lấy Top 10 Doanh thu theo Nhân Viên
    // =========================================================================
    public Map<String, Double> getRevenueByEmployee(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT TOP 10 nv.hoTen, SUM(v.gia) AS TongDoanhThu " +
                        "FROM Ve v " +
                        "JOIN DonDatCho ddc ON v.donDatChoID = ddc.donDatChoID " +
                        "JOIN NhanVien nv ON ddc.nhanVienID = nv.nhanVienID " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

        if (startDate != null) {
            sql.append(" AND v.thoiDiemBan >= ?");
        }
        if (endDate != null) {
            sql.append(" AND v.thoiDiemBan < ?");
        }

        sql.append(" GROUP BY nv.nhanVienID, nv.hoTen ORDER BY TongDoanhThu DESC");

        Map<String, Double> result = new LinkedHashMap<>(); // Giữ thứ tự
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("hoTen"), rs.getDouble("TongDoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // =========================================================================
    // [HÀM MỚI] Cho Form Chi Tiết: Lấy Doanh thu theo Loại Ghế
    // =========================================================================
    public Map<String, Double> getRevenueBySeatType(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT ht.moTa, SUM(v.gia) AS TongDoanhThu " +
                        "FROM Ve v " +
                        "JOIN Ghe g ON v.gheID = g.gheID " +
                        "JOIN Toa t ON g.toaID = t.toaID " +
                        "JOIN HangToa ht ON t.hangToaID = ht.hangToaID " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

        if (startDate != null) {
            sql.append(" AND v.thoiDiemBan >= ?");
        }
        if (endDate != null) {
            sql.append(" AND v.thoiDiemBan < ?");
        }

        sql.append(" GROUP BY ht.moTa ORDER BY TongDoanhThu DESC");

        Map<String, Double> result = new LinkedHashMap<>(); // Giữ thứ tự
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("moTa"), rs.getDouble("TongDoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    // =========================================================================
    // [HÀM MỚI] Cho Form Chi Tiết: Lấy Doanh thu theo Tháng
    // [SỬA LỖI] - Sử dụng GROUP BY YEAR/MONTH chuẩn thay vì FORMAT
    // =========================================================================
    public Map<String, Double> getRevenueByMonth(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        // Chuyển đổi an toàn sang VARCHAR để làm key
                        "   CAST(MONTH(v.thoiDiemBan) AS VARCHAR(2)) + '/' + CAST(YEAR(v.thoiDiemBan) AS VARCHAR(4)) AS ThangNam, " +
                        "   SUM(v.gia) AS DoanhThu " +
                        "FROM Ve v " +
                        "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

        if (startDate != null) {
            sql.append(" AND v.thoiDiemBan >= ?");
        }
        if (endDate != null) {
            sql.append(" AND v.thoiDiemBan < ?");
        }

        // GROUP BY và ORDER BY theo YEAR và MONTH
        sql.append(" GROUP BY YEAR(v.thoiDiemBan), MONTH(v.thoiDiemBan) ");
        sql.append(" ORDER BY YEAR(v.thoiDiemBan), MONTH(v.thoiDiemBan)");

        Map<String, Double> result = new LinkedHashMap<>(); // Giữ thứ tự
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (startDate != null) {
                pstmt.setObject(paramIndex++, startDate.atStartOfDay());
            }
            if (endDate != null) {
                pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("ThangNam"), rs.getDouble("DoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}