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
 * DAO cho PanelThongKeKhachHang.
 * ĐÃ SỬA LỖI: Tính toán RFM (Phân loại) trên TOÀN BỘ lịch sử,
 * sau đó mới lọc ra khách hàng hoạt động TRONG KỲ.
 */
public class ThongKeKhachHang_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeKhachHang_DAO.class.getName());

    // === TÊN CỘT/BẢNG ===
    private final String TBL_KHACH_HANG = "KhachHang";
    private final String TBL_VE = "Ve";

    private final String COL_KH_ID = "khachHangID";
    private final String COL_KH_HO_TEN = "hoTen";
    private final String COL_KH_DIA_CHI = "diaChi";
    private final String COL_KH_LOAI_DOI_TUONG = "loaiDoiTuongID";

    private final String COL_VE_KH_ID = "khachHangID";
    private final String COL_VE_THOI_DIEM_BAN = "thoiDiemBan";
    private final String COL_VE_GIA = "gia";
    private final String COL_VE_ID = "veID";

    /**
     * Lớp nội tại để chứa kết quả phân tích RFM của mỗi khách hàng.
     */
    public static class KhachHangRFM {
        public String khachHangID;
        public String hoTen;
        public String khuVuc;
        public String loaiDoiTuong;
        public int soLanMua; // Frequency (Tổng số lần mua)
        public double tongChiTieu; // Monetary (Tổng chi tiêu)
        public LocalDate lanMuaCuoi; // Recency (Lần mua cuối)
        public LocalDate ngayMuaDauTien; // Ngày mua đầu tiên (thật)
        public String phanLoai; // (VIP, Mới,...)

        public KhachHangRFM() {}
    }

    /**
     * Lấy danh sách các Khu Vực (Tỉnh/Thành) từ cột diaChi
     */
    public List<String> getDanhSachKhuVuc() throws SQLException {
        List<String> danhSach = new ArrayList<>();
        // Tách chuỗi sau dấu phẩy cuối cùng (ví dụ: "Q1, TP.HCM" -> "TP.HCM")
        String sql = String.format(
                "SELECT DISTINCT LTRIM(RTRIM(PARSENAME(REPLACE(%s, ',', '.'), 1))) AS KhuVuc " +
                        "FROM %s WHERE %s IS NOT NULL AND CHARINDEX(',', %s) > 0",
                COL_KH_DIA_CHI, TBL_KHACH_HANG, COL_KH_DIA_CHI, COL_KH_DIA_CHI
        );

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSach.add(rs.getString("KhuVuc"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy danh sách khu vực", e);
            throw e;
        }
        return danhSach;
    }

    /**
     * Lấy danh sách các Loại Đối Tượng (ví dụ: NGUOI_LON)
     */
    public List<String> getDanhSachLoaiDoiTuong() throws SQLException {
        List<String> danhSach = new ArrayList<>();
        String sql = String.format("SELECT DISTINCT %s FROM %s WHERE %s IS NOT NULL",
                COL_KH_LOAI_DOI_TUONG, TBL_KHACH_HANG, COL_KH_LOAI_DOI_TUONG
        );

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                danhSach.add(rs.getString(COL_KH_LOAI_DOI_TUONG));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy danh sách loại đối tượng", e);
            throw e;
        }
        return danhSach;
    }


    /**
     * Hàm chính: Lấy dữ liệu thống kê chi tiết khách hàng (RFM)
     */
    public Map<String, KhachHangRFM> getThongKeKhachHang(
            LocalDate tuNgay, LocalDate denNgay,
            String khuVuc, String loaiDoiTuong, String phanLoai) throws SQLException {

        Map<String, KhachHangRFM> results = new LinkedHashMap<>();

        // 1. Định nghĩa các mốc phân loại (bạn có thể thay đổi)
        final double MOC_VIP = 10000000; // 10 triệu
        final int MOC_TRUNG_THANH = 5; // 5 lần mua
        final int SO_NGAY_NGU_DONG = 180; // 6 tháng

        // 2. Xây dựng câu SQL
        String sql = String.format(
                // B1: Tính RFM toàn bộ lịch sử cho TẤT CẢ khách hàng
                "WITH RFM_Global AS ( " +
                        "    SELECT " +
                        "        v.%s AS KH_ID, " +
                        "        COUNT(v.%s) AS SoLanMua_Global, " +
                        "        SUM(v.%s) AS TongChiTieu_Global, " +
                        "        MAX(v.%s) AS LanMuaCuoi_Global, " +
                        "        MIN(v.%s) AS NgayMuaDauTien_Global " +
                        "    FROM %s v " +
                        "    GROUP BY v.%s " +
                        "), " +
                        // B2: Phân loại TẤT CẢ khách hàng
                        "PhanLoai_Global AS ( " +
                        "    SELECT " +
                        "        g.*, " +
                        "        kh.%s AS HoTen, " +
                        "        kh.%s AS LoaiDoiTuong, " +
                        "        LTRIM(RTRIM(PARSENAME(REPLACE(kh.%s, ',', '.'), 1))) AS KhuVuc, " +
                        "        CASE " +
                        "           WHEN g.TongChiTieu_Global >= ? THEN N'VIP' " + // ?1 = MOC_VIP
                        "           WHEN g.SoLanMua_Global >= ? THEN N'Thân thiết' " + // ?2 = MOC_TRUNG_THANH
                        "           WHEN CAST(g.LanMuaCuoi_Global AS DATE) < CAST(DATEADD(day, -?, GETDATE()) AS DATE) THEN N'Ngủ đông' " + // ?3 = SO_NGAY_NGU_DONG
                        "           WHEN g.SoLanMua_Global = 1 THEN N'Khách mới' " + // Khách mới (chỉ mua 1 lần)
                        "           ELSE N'Khách quay lại' " + // Mua > 1 lần, nhưng chưa đủ VIP/Thân thiết
                        "        END AS LoaiKH " +
                        "    FROM RFM_Global g " +
                        "    JOIN %s kh ON g.KH_ID = kh.%s " +
                        "), " +
                        // B3: Lấy danh sách KH có hoạt động TRONG KỲ LỌC
                        "KhachHang_TrongKy AS ( " +
                        "    SELECT DISTINCT v.%s " +
                        "    FROM %s v " +
                        "    WHERE v.%s >= ? AND v.%s < ? " + // ?4 = tuNgay, ?5 = denNgay
                        ") " +
                        // B4: Lọc và trả về (chỉ những KH có trong PhanLoai_Global VÀ KhachHang_TrongKy)
                        "SELECT p.* FROM PhanLoai_Global p " +
                        "JOIN KhachHang_TrongKy k ON p.KH_ID = k.%s " +
                        "WHERE 1=1 ",

                // CTE 1 (RFM_Global)
                COL_VE_KH_ID, COL_VE_ID, COL_VE_GIA, COL_VE_THOI_DIEM_BAN, COL_VE_THOI_DIEM_BAN,
                TBL_VE, COL_VE_KH_ID,
                // CTE 2 (PhanLoai_Global)
                COL_KH_HO_TEN, COL_KH_LOAI_DOI_TUONG, COL_KH_DIA_CHI,
                TBL_KHACH_HANG, COL_KH_ID,
                // CTE 3 (KhachHang_TrongKy)
                COL_VE_KH_ID, TBL_VE, COL_VE_THOI_DIEM_BAN, COL_VE_THOI_DIEM_BAN,
                // Final Join
                COL_VE_KH_ID
        );

        // Thêm bộ lọc động
        if (khuVuc != null && !khuVuc.equals("Tất cả")) {
            sql += " AND p.KhuVuc = ? "; // ?6
        }
        if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) {
            sql += " AND p.LoaiDoiTuong = ? "; // ?7
        }
        if (phanLoai != null && !phanLoai.equals("Tất cả")) {
            sql += " AND p.LoaiKH = ? "; // ?8
        }

        sql += " ORDER BY p.TongChiTieu_Global DESC";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            // Tham số cho CASE WHEN (Phân loại)
            pstmt.setDouble(paramIndex++, MOC_VIP);
            pstmt.setInt(paramIndex++, MOC_TRUNG_THANH);
            pstmt.setInt(paramIndex++, SO_NGAY_NGU_DONG);

            // Tham số cho WHERE (Lọc kỳ)
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(tuNgay));
            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(denNgay.plusDays(1)));

            // Tham số cho các bộ lọc động
            if (khuVuc != null && !khuVuc.equals("Tất cả")) {
                pstmt.setString(paramIndex++, khuVuc);
            }
            if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) {
                pstmt.setString(paramIndex++, loaiDoiTuong);
            }
            if (phanLoai != null && !phanLoai.equals("Tất cả")) {
                pstmt.setString(paramIndex++, phanLoai);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KhachHangRFM item = new KhachHangRFM();
                    item.khachHangID = rs.getString("KH_ID");
                    item.hoTen = rs.getString("HoTen");
                    item.khuVuc = rs.getString("KhuVuc");
                    item.loaiDoiTuong = rs.getString("LoaiDoiTuong");
                    item.soLanMua = rs.getInt("SoLanMua_Global"); // Lấy tổng số lần mua
                    item.tongChiTieu = rs.getDouble("TongChiTieu_Global"); // Lấy tổng chi tiêu
                    item.lanMuaCuoi = rs.getTimestamp("LanMuaCuoi_Global").toLocalDateTime().toLocalDate();
                    item.ngayMuaDauTien = rs.getTimestamp("NgayMuaDauTien_Global").toLocalDateTime().toLocalDate(); // <<< ĐÂY LÀ NGÀY ĐÚNG
                    item.phanLoai = rs.getString("LoaiKH");

                    results.put(item.khachHangID, item);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi SQL khi lấy thống kê RFM khách hàng", e);
            throw e;
        }
        return results;
    }
}