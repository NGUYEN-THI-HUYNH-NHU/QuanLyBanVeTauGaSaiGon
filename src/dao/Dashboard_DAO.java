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

/**
 * Lớp DAO (Data Access Object) chịu trách nhiệm truy vấn dữ liệu
 * cho Dashboard từ CSDL SQL Server.
 */
public class Dashboard_DAO {

    /**
     * Lấy tổng doanh thu từ các hóa đơn được lập trong ngày hôm nay.
     * @return Tổng doanh thu (double).
     */
    public double getTongDoanhThuHomNay() {
        double tongDoanhThu = 0;
        String sql = "SELECT SUM(TongTien) FROM HoaDon " +
                "WHERE CAST(NgayLapHoaDon AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                tongDoanhThu = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongDoanhThu;
    }

    /**
     * Lấy tổng số vé đã bán (vé thuộc các hóa đơn) trong ngày hôm nay.
     * @return Tổng số vé (int).
     */
    public int getSoVeDaBanHomNay() {
        int soVe = 0;
        String sql = "SELECT COUNT(v.VeTauID) FROM VeTau v " +
                "JOIN HoaDon h ON v.HoaDonID = h.HoaDonID " +
                "WHERE CAST(h.NgayLapHoaDon AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                soVe = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soVe;
    }

    /**
     * Lấy tỷ lệ vé đã bị hủy (trạng thái 'DA_HUY') trong tổng số vé được tạo hôm nay.
     * @return Tỷ lệ hủy vé (double, ví dụ: 3.5).
     */
    public double getTyLeHuyVeHomNay() {
        double tyLe = 0;
        String sql = "SELECT " +
                "    SUM(CASE WHEN v.trangThai = N'DA_HUY' THEN 1 ELSE 0 END) AS SoVeHuy, " +
                "    COUNT(v.VeTauID) AS TongSoVe " +
                "FROM VeTau v JOIN HoaDon h ON v.HoaDonID = h.HoaDonID " +
                "WHERE CAST(h.NgayLapHoaDon AS DATE) = CAST(GETDATE() AS DATE)";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int soVeHuy = rs.getInt("SoVeHuy");
                int tongSoVe = rs.getInt("TongSoVe");
                if (tongSoVe > 0) {
                    tyLe = (double) soVeHuy * 100.0 / tongSoVe;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tyLe;
    }

    /**
     * Lấy tỷ lệ lấp đầy trung bình cho các chuyến tàu khởi hành hôm nay.
     * (Số vé đã bán) / (Tổng số ghế của các chuyến khởi hành hôm nay)
     * @return Tỷ lệ lấp đầy (double, ví dụ: 88.0).
     */
    public double getTyLeDayTBHomNay() {
        double tyLe = 0;
        String sql = "WITH SoVeBan AS ( " +
                "    SELECT COUNT(v.VeTauID) AS TongSoVeBan " +
                "    FROM VeTau v " +
                "    JOIN LichTrinh lt ON v.LichTrinhID = lt.LichTrinhID " +
                "    WHERE CAST(lt.ThoiGianDi AS DATE) = CAST(GETDATE() AS DATE) AND v.trangThai != N'DA_HUY' " +
                "), TongGhe AS ( " +
                "    SELECT COUNT(g.GheID) AS TongSoGhe " +
                "    FROM ChuyenTau ct " +
                "    JOIN LichTrinh lt ON ct.LichTrinhID = lt.LichTrinhID " +
                "    JOIN Tau t ON ct.TauID = t.TauID " +
                "    JOIN Toa toa ON t.TauID = toa.TauID " +
                "    JOIN Ghe g ON toa.ToaID = g.ToaID " +
                "    WHERE CAST(lt.ThoiGianDi AS DATE) = CAST(GETDATE() AS DATE) " +
                ") " +
                "SELECT CAST(ISNULL(sv.TongSoVeBan, 0) * 100.0 / NULLIF(tg.TongSoGhe, 0) AS DECIMAL(10, 2)) " +
                "FROM SoVeBan sv, TongGhe tg";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                tyLe = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tyLe;
    }

    /**
     * Lấy số ngày đặt vé trung bình trước ngày khởi hành.
     * Tính toán dựa trên các vé đặt trong vòng 1 năm qua.
     * @return Số ngày trung bình (double).
     */
    public double getNgayDatVeTrungBinh() {
        double avgDays = 0;
        String sql = "SELECT AVG(CAST(DATEDIFF(day, v.NgayDatVe, lt.ThoiGianDi) AS FLOAT)) " +
                "FROM VeTau v JOIN LichTrinh lt ON v.LichTrinhID = lt.LichTrinhID " +
                "WHERE v.NgayDatVe > DATEADD(year, -1, GETDATE()) AND lt.ThoiGianDi > v.NgayDatVe";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                avgDays = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return avgDays;
    }

    /**
     * Lấy doanh thu theo tuyến, sắp xếp giảm dần, giới hạn bởi 'limit'.
     * @param limit Số lượng tuyến hàng đầu muốn lấy.
     * @return Map<String, Double> (Tên tuyến, Tổng doanh thu).
     */
    public Map<String, Double> getDoanhThuTheoTuyen(int limit) {
        Map<String, Double> data = new LinkedHashMap<>(); // Giữ thứ tự
        String sql = "SELECT TOP (?) (g1.TenGa + ' - ' + g2.TenGa) AS TenTuyen, SUM(v.GiaVe) AS DoanhThu " +
                "FROM VeTau v " +
                "JOIN LichTrinh lt ON v.LichTrinhID = lt.LichTrinhID " +
                "JOIN Ga g1 ON lt.GaDiID = g1.GaID " +
                "JOIN Ga g2 ON lt.GaDenID = g2.GaID " +
                "WHERE v.trangThai != N'DA_HUY' " +
                "GROUP BY (g1.TenGa + ' - ' + g2.TenGa) " +
                "ORDER BY DoanhThu DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("TenTuyen"), rs.getDouble("DoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Lấy số lượng vé bán ra theo Hạng toa trong 5 ngày gần nhất.
     * @return Map<LocalDate, Map<String, Integer>> (Ngày, Map<Tên Hạng Toa, Số Lượng>).
     */
    public Map<LocalDate, Map<String, Integer>> getVeTheoLoaiGhe5NgayQua() {
        Map<LocalDate, Map<String, Integer>> data = new LinkedHashMap<>();
        String sql = "SELECT CAST(h.NgayLapHoaDon AS DATE) AS Ngay, ht.tenHangToa, COUNT(v.VeTauID) AS SoLuong " +
                "FROM VeTau v " +
                "JOIN Ghe g ON v.GheID = g.GheID " +
                "JOIN Toa t ON g.ToaID = t.ToaID " +
                "JOIN HangToa ht ON t.HangToaID = ht.HangToaID " +
                "JOIN HoaDon h ON v.HoaDonID = h.HoaDonID " +
                "WHERE h.NgayLapHoaDon >= DATEADD(day, -4, CAST(GETDATE() AS DATE)) AND v.trangThai != N'DA_HUY' " +
                "GROUP BY CAST(h.NgayLapHoaDon AS DATE), ht.tenHangToa " +
                "ORDER BY Ngay ASC, ht.tenHangToa ASC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                String tenHangToa = rs.getString("tenHangToa");
                int soLuong = rs.getInt("SoLuong");

                // Đảm bảo có một map cho ngày đó
                data.putIfAbsent(ngay, new HashMap<>());
                // Thêm dữ liệu vào map của ngày
                data.get(ngay).put(tenHangToa, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Lấy doanh thu VÀ số lượng vé theo tháng, trong 12 tháng gần nhất.
     * @return Map<String, double[]> (Tháng "yyyy-MM", [DoanhThu, SoVe]).
     */
    public Map<String, double[]> getDoanhThuVaSoVeTheoThang() {
        Map<String, double[]> data = new LinkedHashMap<>();
        String sql = "SELECT FORMAT(h.NgayLapHoaDon, 'yyyy-MM') AS Thang, SUM(h.TongTien) AS TongDoanhThu, COUNT(v.VeTauID) AS TongSoVe " +
                "FROM HoaDon h " +
                "JOIN VeTau v ON h.HoaDonID = v.HoaDonID " +
                "WHERE h.NgayLapHoaDon >= DATEADD(year, -1, GETDATE()) AND v.trangThai != N'DA_HUY' " +
                "GROUP BY FORMAT(h.NgayLapHoaDon, 'yyyy-MM') " +
                "ORDER BY Thang ASC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String thang = rs.getString("Thang");
                double[] values = new double[2];
                values[0] = rs.getDouble("TongDoanhThu");
                values[1] = rs.getDouble("TongSoVe");
                data.put(thang, values);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Lấy tổng số vé đã bán theo từng loại ghế (Hạng toa).
     * @return Map<String, Integer> (Tên Hạng Toa, Tổng số vé).
     */
    public Map<String, Integer> getSoVeTheoLoaiGhe() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT ht.tenHangToa, COUNT(v.VeTauID) AS SoLuong " +
                "FROM VeTau v " +
                "JOIN Ghe g ON v.GheID = g.GheID " +
                "JOIN Toa t ON g.ToaID = t.ToaID " +
                "JOIN HangToa ht ON t.HangToaID = ht.HangToaID " +
                "WHERE v.trangThai != N'DA_HUY' " +
                "GROUP BY ht.tenHangToa " +
                "ORDER BY SoLuong DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("tenHangToa"), rs.getInt("SoLuong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Lấy số lượng vé khuyến mãi (có KhuyenMaiID) theo tháng, trong 12 tháng qua.
     * @return Map<String, Integer> (Tháng "yyyy-MM", Số vé KM).
     */
    public Map<String, Integer> getSoVeKhuyenMaiTheoThang() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT FORMAT(h.NgayLapHoaDon, 'yyyy-MM') AS Thang, COUNT(v.VeTauID) AS SoLuongKM " +
                "FROM VeTau v " +
                "JOIN HoaDon h ON v.HoaDonID = h.HoaDonID " +
                "WHERE v.KhuyenMaiID IS NOT NULL AND v.trangThai != N'DA_HUY' " +
                "AND h.NgayLapHoaDon >= DATEADD(year, -1, GETDATE()) " +
                "GROUP BY FORMAT(h.NgayLapHoaDon, 'yyyy-MM') " +
                "ORDER BY Thang ASC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("Thang"), rs.getInt("SoLuongKM"));
            }
        } catch (SQLException e) {
            e.printStackTrace();

            e.printStackTrace();
        }
        return data;
    }
}
