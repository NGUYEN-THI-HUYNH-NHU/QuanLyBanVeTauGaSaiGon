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

public class ThongKeKhachHang_DAO {

    private static final Logger LOGGER = Logger.getLogger(ThongKeKhachHang_DAO.class.getName());

    // === CONSTANTS ĐỊNH NGHĨA KHÁCH HÀNG ===
    private final double MOC_VIP = 10000000;    // > 10 triệu
    private final int SO_NGAY_NGU_DONG = 180;   // 6 tháng
    private final int MOC_THAN_THIET = 5;       // >= 5 lần
    // Khách quay lại sẽ là khoảng giữa (>=2 và < MOC_THAN_THIET)

    // === TÊN BẢNG & CỘT (Giữ nguyên như project của bạn) ===
    private final String TBL_KHACH_HANG = "KhachHang";
    private final String TBL_VE = "Ve";
    private final String TBL_DON_DAT_CHO = "DonDatCho";

    // Cột
    private final String COL_DDC_ID = "donDatChoID";
    private final String COL_DDC_KH_ID = "khachHangID";
    private final String COL_DDC_THOI_DIEM = "thoiDiemDatCho";
    private final String COL_KH_ID = "khachHangID";
    private final String COL_KH_HO_TEN = "hoTen";
    private final String COL_KH_DIA_CHI = "diaChi";
    private final String COL_KH_LOAI_DOI_TUONG = "loaiDoiTuongID";
    private final String COL_VE_ID = "veID";
    private final String COL_VE_GIA = "gia";
    private final String COL_VE_DDC_ID = "donDatChoID";

    public static class KhachHangRFM {
        public String khachHangID;
        public String hoTen;
        public String khuVuc;
        public String loaiDoiTuong;
        public int soLanMua;
        public double tongChiTieu;
        public LocalDate lanMuaCuoi;
        public LocalDate ngayMuaDauTien;
        public String phanLoai;

        public KhachHangRFM() {}
    }

    public List<String> getDanhSachLoaiDoiTuong() throws SQLException {
        List<String> danhSach = new ArrayList<>();
        String sql = "SELECT DISTINCT " + COL_KH_LOAI_DOI_TUONG + " FROM " + TBL_KHACH_HANG;
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String val = rs.getString(COL_KH_LOAI_DOI_TUONG);
                if(val != null) danhSach.add(val);
            }
        }
        return danhSach;
    }

    /**
     * Hàm thống kê RFM với logic:
     * - Khách quay lại: 2 <= SoLanMua < 5 AND Active < 6 tháng.
     */
    public Map<String, KhachHangRFM> getThongKeKhachHang(
            LocalDate tuNgay, LocalDate denNgay,
            String khuVuc, String loaiDoiTuong, String phanLoai) throws SQLException {

        Map<String, KhachHangRFM> results = new LinkedHashMap<>();

        String sql = String.format(
                "WITH RFM_Global AS ( " +
                        "    SELECT " +
                        "        ddc.%s AS KH_ID, " +
                        "        COUNT(v.%s) AS SoLanMua_Global, " +
                        "        SUM(v.%s) AS TongChiTieu_Global, " +
                        "        MAX(ddc.%s) AS LanMuaCuoi_Global, " +
                        "        MIN(ddc.%s) AS NgayMuaDauTien_Global " +
                        "    FROM %s v " +
                        "    JOIN %s ddc ON v.%s = ddc.%s " +
                        "    GROUP BY ddc.%s " +
                        "), " +
                        "PhanLoai_Global AS ( " +
                        "    SELECT " +
                        "        g.*, " +
                        "        kh.%s AS HoTen, " +
                        "        kh.%s AS LoaiDoiTuong, " +
                        "        LTRIM(RTRIM(PARSENAME(REPLACE(kh.%s, ',', '.'), 1))) AS KhuVuc, " +
                        "        CASE " +
                        "           WHEN g.TongChiTieu_Global >= ? THEN N'VIP' " +                         // 1. Tiền > 10tr
                        "           WHEN CAST(g.LanMuaCuoi_Global AS DATE) < CAST(DATEADD(day, -?, GETDATE()) AS DATE) THEN N'Ngủ đông' " + // 2. Ko mua 6 tháng
                        "           WHEN g.SoLanMua_Global >= ? THEN N'Thân thiết' " +                      // 3. Mua >= 5 lần (và đang Active)
                        "           WHEN g.SoLanMua_Global >= 2 THEN N'Khách quay lại' " +                  // 4. Mua >= 2 (tức là 2,3,4) (và đang Active)
                        "           ELSE N'Khách mới' " +                                                   // 5. Mua 1 lần
                        "        END AS LoaiKH " +
                        "    FROM RFM_Global g " +
                        "    JOIN %s kh ON g.KH_ID = kh.%s " +
                        "), " +
                        "KhachHang_TrongKy AS ( " +
                        "    SELECT DISTINCT ddc.%s " +
                        "    FROM %s v " +
                        "    JOIN %s ddc ON v.%s = ddc.%s " +
                        "    WHERE ddc.%s >= ? AND ddc.%s < ? " +
                        ") " +
                        "SELECT p.* FROM PhanLoai_Global p " +
                        "JOIN KhachHang_TrongKy k ON p.KH_ID = k.%s " +
                        "WHERE 1=1 ",

                // CTE 1
                COL_DDC_KH_ID, COL_VE_ID, COL_VE_GIA, COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM,
                TBL_VE, TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID, COL_DDC_KH_ID,
                // CTE 2
                COL_KH_HO_TEN, COL_KH_LOAI_DOI_TUONG, COL_KH_DIA_CHI,
                TBL_KHACH_HANG, COL_KH_ID,
                // CTE 3
                COL_DDC_KH_ID,
                TBL_VE, TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID,
                COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM,
                // Join
                COL_DDC_KH_ID
        );

        // Append filters
        if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) sql += " AND p.LoaiDoiTuong = ? ";
        if (phanLoai != null && !phanLoai.equals("Tất cả")) sql += " AND p.LoaiKH = ? ";

        sql += " ORDER BY p.TongChiTieu_Global DESC";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int idx = 1;
            // Params for CASE WHEN
            pstmt.setDouble(idx++, MOC_VIP);
            pstmt.setInt(idx++, SO_NGAY_NGU_DONG);
            pstmt.setInt(idx++, MOC_THAN_THIET);

            // Params for WHERE Time Range
            pstmt.setDate(idx++, java.sql.Date.valueOf(tuNgay));
            pstmt.setDate(idx++, java.sql.Date.valueOf(denNgay.plusDays(1)));

            // Params for Dynamic Filters
            if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) pstmt.setString(idx++, loaiDoiTuong);
            if (phanLoai != null && !phanLoai.equals("Tất cả")) pstmt.setString(idx++, phanLoai);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    KhachHangRFM item = new KhachHangRFM();
                    item.khachHangID = rs.getString("KH_ID");
                    item.hoTen = rs.getString("HoTen");
                    item.khuVuc = rs.getString("KhuVuc");
                    item.loaiDoiTuong = rs.getString("LoaiDoiTuong");
                    item.soLanMua = rs.getInt("SoLanMua_Global");
                    item.tongChiTieu = rs.getDouble("TongChiTieu_Global");
                    item.lanMuaCuoi = rs.getTimestamp("LanMuaCuoi_Global").toLocalDateTime().toLocalDate();
                    item.ngayMuaDauTien = rs.getTimestamp("NgayMuaDauTien_Global").toLocalDateTime().toLocalDate();
                    item.phanLoai = rs.getString("LoaiKH");
                    results.put(item.khachHangID, item);
                }
            }
        }
        return results;
    }
}