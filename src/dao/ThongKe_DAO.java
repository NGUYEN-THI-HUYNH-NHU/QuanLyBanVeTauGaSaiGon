package dao;

import connectDB.ConnectDB; // Đảm bảo lớp này tồn tại và hoạt động

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lớp DAO (Data Access Object) cho các chức năng thống kê doanh thu, chi phí.
 */
public class ThongKe_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKe_DAO.class.getName());
    private final ConnectDB connectDB;

    // Định dạng ngày tháng năm Java (an toàn luồng)
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    public ThongKe_DAO() {
        this.connectDB = ConnectDB.getInstance();
    }

    /** Lớp nội bộ chứa tất cả các chỉ số chi tiết cho mỗi khoảng thời gian */
    public static class ThongKeChiTietItem {
        public int    soLuongHoaDonBan = 0;
        public int    soLuongHoaDonHoanDoi = 0;
        public double tongThuDichVu = 0.0;
        public double tongDoanhThu = 0.0; // Tổng thu từ HDCT
        public double tongChi = 0.0;      // Tổng chi từ GDHD
        public double loiNhuan = 0.0;     // Sẽ tính = tongDoanhThu - tongChi

        public ThongKeChiTietItem() {} // Constructor mặc định
    }

    // ====== Các phương thức tổng quan (lấy tổng toàn bộ) ======

    public int getTongHoaDonBan() {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HoaDon";
        int count = 0;
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) count = rs.getInt("SoLuong");
        } catch (Exception e) { LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng hóa đơn bán (all time)", e); }
        return count;
    }

    public int getTongHoaDonHoanDoi() {
        String sql = "SELECT COUNT(*) AS SoLuong FROM GiaoDichHoanDoi";
        int count = 0;
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) count = rs.getInt("SoLuong");
        } catch (Exception e) { LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng hóa đơn hoàn đổi (all time)", e); }
        return count;
    }

    public double getTongThuDichVu() {
        // Kiểm tra phieuDungPhongVIPID thay vì loaiDichVu
        String sql = "SELECT ISNULL(SUM(thanhTien), 0) AS Tong FROM HoaDonChiTiet WHERE phieuDungPhongVIPID IS NOT NULL";
        double total = 0.0;
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) total = rs.getDouble("Tong");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng thu dịch vụ (all time)", e);
        }
        return total;
    }

    public double getTongThu() {
        String sql = "SELECT ISNULL(SUM(thanhTien), 0) AS Tong FROM HoaDonChiTiet";
        double total = 0.0;
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) total = rs.getDouble("Tong");
        } catch (Exception e) { LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng thu (all time)", e); }
        return total;
    }

    public double getTongChiHoanDoi() {
        String sql = "SELECT ISNULL(SUM(soTienChenhLech * -1), 0) AS Tong FROM GiaoDichHoanDoi WHERE soTienChenhLech < 0";
        double total = 0.0;
        try (Connection con = connectDB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) total = rs.getDouble("Tong");
        } catch (Exception e) { LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng chi hoàn đổi (all time)", e); }
        return total;
    }

    // ====== Các phương thức tổng quan THEO KHOẢNG THỜI GIAN ======

    private LocalDateTime getStartOfDay(LocalDate date) {
        return (date != null) ? date.atStartOfDay() : null;
    }
    private LocalDateTime getEndOfDayExclusive(LocalDate date) {
        return (date != null) ? date.plusDays(1).atStartOfDay() : null;
    }

    public int getTongHoaDonBanTrongKhoang(LocalDate from, LocalDate to) {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HoaDon WHERE thoiDiemTao >= ? AND thoiDiemTao < ?";
        int count = 0;
        LocalDateTime fromDT = getStartOfDay(from);
        LocalDateTime toDTEx = getEndOfDayExclusive(to);
        if (fromDT == null || toDTEx == null) return 0;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fromDT));
            pst.setTimestamp(2, Timestamp.valueOf(toDTEx));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) count = rs.getInt("SoLuong");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng hóa đơn bán trong khoảng", e);
        }
        return count;
    }

    public int getTongHoaDonHoanDoiTrongKhoang(LocalDate from, LocalDate to) {
        String sql = "SELECT COUNT(*) AS SoLuong FROM GiaoDichHoanDoi WHERE thoiDiemGiaoDich >= ? AND thoiDiemGiaoDich < ?";
        int count = 0;
        LocalDateTime fromDT = getStartOfDay(from);
        LocalDateTime toDTEx = getEndOfDayExclusive(to);
        if (fromDT == null || toDTEx == null) return 0;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fromDT));
            pst.setTimestamp(2, Timestamp.valueOf(toDTEx));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) count = rs.getInt("SoLuong");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng hóa đơn hoàn đổi trong khoảng", e);
        }
        return count;
    }

    public double getTongThuDichVuTrongKhoang(LocalDate from, LocalDate to) {
        String sql = """
            SELECT ISNULL(SUM(ct.thanhTien), 0) AS Tong
            FROM HoaDonChiTiet ct JOIN HoaDon hd ON ct.hoaDonID = hd.hoaDonID
            WHERE ct.phieuDungPhongVIPID IS NOT NULL AND hd.thoiDiemTao >= ? AND hd.thoiDiemTao < ?
            """;
        double total = 0.0;
        LocalDateTime fromDT = getStartOfDay(from);
        LocalDateTime toDTEx = getEndOfDayExclusive(to);
        if (fromDT == null || toDTEx == null) return 0.0;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fromDT));
            pst.setTimestamp(2, Timestamp.valueOf(toDTEx));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) total = rs.getDouble("Tong");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng thu dịch vụ trong khoảng", e);
        }
        return total;
    }

    public double getTongThuTrongKhoang(LocalDate from, LocalDate to) {
        String sql = """
            SELECT ISNULL(SUM(ct.thanhTien), 0) AS Tong
            FROM HoaDonChiTiet ct JOIN HoaDon hd ON ct.hoaDonID = hd.hoaDonID
            WHERE hd.thoiDiemTao >= ? AND hd.thoiDiemTao < ?
            """;
        double total = 0.0;
        LocalDateTime fromDT = getStartOfDay(from);
        LocalDateTime toDTEx = getEndOfDayExclusive(to);
        if (fromDT == null || toDTEx == null) return 0.0;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fromDT));
            pst.setTimestamp(2, Timestamp.valueOf(toDTEx));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) total = rs.getDouble("Tong");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng thu trong khoảng", e);
        }
        return total;
    }

    public double getTongChiHoanDoiTrongKhoang(LocalDate from, LocalDate to) {
        String sql = "SELECT ISNULL(SUM(soTienChenhLech * -1), 0) AS Tong FROM GiaoDichHoanDoi WHERE soTienChenhLech < 0 AND thoiDiemGiaoDich >= ? AND thoiDiemGiaoDich < ?";
        double total = 0.0;
        LocalDateTime fromDT = getStartOfDay(from);
        LocalDateTime toDTEx = getEndOfDayExclusive(to);
        if (fromDT == null || toDTEx == null) return 0.0;
        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.valueOf(fromDT));
            pst.setTimestamp(2, Timestamp.valueOf(toDTEx));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) total = rs.getDouble("Tong");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy tổng chi hoàn đổi trong khoảng", e);
        }
        return total;
    }

    // ====== Thống kê CHI TIẾT theo thời gian (Sửa đổi SQL) ======
    /**
     * Lấy dữ liệu chi tiết thống kê theo khoảng thời gian và loại nhóm.
     * @param loai Loại thống kê ("Tất cả", "Theo ngày", "Theo tháng", "Theo năm").
     * @param from Ngày bắt đầu (LocalDate).
     * @param to Ngày kết thúc (LocalDate).
     * @return Map chứa Thời gian (String) và đối tượng ThongKeChiTietItem.
     */
    public Map<String, ThongKeChiTietItem> getThongKeChiTietTheoThoiGian(String loai, LocalDate from, LocalDate to) {
        Map<String, ThongKeChiTietItem> map = new LinkedHashMap<>();
        String sqlSelectKey = "";
        String sqlGroupBy = "";
        String sqlOrderBy = "";
        DateTimeFormatter formatter = null;

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTimeExclusive = to.plusDays(1).atStartOfDay();

        switch (loai) {
            case "Theo ngày":
                sqlSelectKey = "CAST(T.ThoiDiem AS DATE) AS GroupingKeyDate";
                sqlGroupBy = "CAST(T.ThoiDiem AS DATE)";
                sqlOrderBy = "CAST(T.ThoiDiem AS DATE)";
                formatter = DATE_FORMATTER;
                break;
            case "Theo tháng":
                sqlSelectKey = "YEAR(T.ThoiDiem) AS GroupingYear, MONTH(T.ThoiDiem) AS GroupingMonth";
                sqlGroupBy = "YEAR(T.ThoiDiem), MONTH(T.ThoiDiem)";
                sqlOrderBy = "YEAR(T.ThoiDiem), MONTH(T.ThoiDiem)";
                formatter = MONTH_YEAR_FORMATTER;
                break;
            case "Theo năm":
            case "Tất cả":
            default:
                sqlSelectKey = "YEAR(T.ThoiDiem) AS GroupingKeyYear";
                sqlGroupBy = "YEAR(T.ThoiDiem)";
                sqlOrderBy = "YEAR(T.ThoiDiem)";
                break;
        }

        String sql = String.format("""
            WITH NguonDuLieu AS (
                SELECT
                    hd.thoiDiemTao AS ThoiDiem,
                    hd.hoaDonID AS ID_Goc,
                    1 AS LoaiGD,
                    ct.thanhTien AS DoanhThu,
                    CASE WHEN ct.phieuDungPhongVIPID IS NOT NULL THEN ct.thanhTien ELSE 0 END AS ThuDichVu,
                    0 AS ChiPhi
                FROM HoaDon hd JOIN HoaDonChiTiet ct ON hd.hoaDonID = ct.hoaDonID
                WHERE hd.thoiDiemTao >= ? AND hd.thoiDiemTao < ?
                UNION ALL
                SELECT
                    gd.thoiDiemGiaoDich AS ThoiDiem,
                    gd.giaoDichHoanDoiID AS ID_Goc,
                    2 AS LoaiGD,
                    0 AS DoanhThu,
                    0 AS ThuDichVu,
                    CASE WHEN gd.soTienChenhLech < 0 THEN (gd.soTienChenhLech * -1) ELSE 0 END AS ChiPhi
                FROM GiaoDichHoanDoi gd
                WHERE gd.thoiDiemGiaoDich >= ? AND gd.thoiDiemGiaoDich < ?
            )
            SELECT
                %s, -- Chọn khóa nhóm
                COUNT(DISTINCT CASE WHEN T.LoaiGD = 1 THEN T.ID_Goc ELSE NULL END) AS SoLuongHD_Ban,
                COUNT(DISTINCT CASE WHEN T.LoaiGD = 2 THEN T.ID_Goc ELSE NULL END) AS SoLuongHD_HoanDoi,
                ISNULL(SUM(T.ThuDichVu), 0) AS TongThuDichVu,
                ISNULL(SUM(T.DoanhThu), 0) AS TongDoanhThu,
                ISNULL(SUM(T.ChiPhi), 0) AS TongChiPhi
            FROM NguonDuLieu T
            WHERE T.ThoiDiem IS NOT NULL
            GROUP BY %s
            ORDER BY %s;
            """,
                sqlSelectKey, sqlGroupBy, sqlOrderBy
        );

        LOGGER.log(Level.INFO, "Executing SQL (getThongKeChiTietTheoThoiGian):\n{0}", sql);
        LOGGER.log(Level.INFO, "Params: From >= {0}, To < {1}", new Object[]{fromDateTime, toDateTimeExclusive});

        try (Connection con = connectDB.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setTimestamp(1, Timestamp.valueOf(fromDateTime));
            pst.setTimestamp(2, Timestamp.valueOf(toDateTimeExclusive));
            pst.setTimestamp(3, Timestamp.valueOf(fromDateTime));
            pst.setTimestamp(4, Timestamp.valueOf(toDateTimeExclusive));

            try (ResultSet rs = pst.executeQuery()) {
                LOGGER.log(Level.INFO, "Query executed. Processing results...");
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    String thoiGianFormatted;
                    ThongKeChiTietItem item = new ThongKeChiTietItem();

                    item.soLuongHoaDonBan = rs.getInt("SoLuongHD_Ban");
                    item.soLuongHoaDonHoanDoi = rs.getInt("SoLuongHD_HoanDoi");
                    item.tongThuDichVu = rs.getDouble("TongThuDichVu");
                    item.tongDoanhThu = rs.getDouble("TongDoanhThu");
                    item.tongChi = rs.getDouble("TongChiPhi");
                    item.loiNhuan = item.tongDoanhThu - item.tongChi;

                    switch (loai) {
                        case "Theo ngày":
                            Date dateKey = rs.getDate("GroupingKeyDate");
                            if (dateKey != null) {
                                thoiGianFormatted = dateKey.toLocalDate().format(formatter);
                            } else {
                                LOGGER.log(Level.WARNING, "Null DATE key found in row {0}", rowCount);
                                continue;
                            }
                            break;
                        case "Theo tháng":
                            int yearMonth = rs.getInt("GroupingYear");
                            int month = rs.getInt("GroupingMonth");
                            if (!rs.wasNull()) {
                                thoiGianFormatted = LocalDate.of(yearMonth, month, 1).format(formatter);
                            } else {
                                LOGGER.log(Level.WARNING, "Null YEAR or MONTH key found in row {0}", rowCount);
                                continue;
                            }
                            break;
                        case "Theo năm":
                        case "Tất cả":
                        default:
                            int yearKey = rs.getInt("GroupingKeyYear");
                            if (!rs.wasNull()) {
                                thoiGianFormatted = String.valueOf(yearKey);
                            } else {
                                LOGGER.log(Level.WARNING, "Null YEAR key found in row {0}", rowCount);
                                continue;
                            }
                            break;
                    }
                    map.put(thoiGianFormatted, item);
                }
                LOGGER.log(Level.INFO, "Finished processing {0} rows.", rowCount);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "SQL ERROR executing getThongKeChiTietTheoThoiGian", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "General ERROR in getThongKeChiTietTheoThoiGian", e);
        }

        LOGGER.log(Level.INFO, "DAO(Detail): getThongKeChiTietTheoThoiGian() returning map size: {0}", map.size());
        return map;
    }

} // End of ThongKe_DAO class