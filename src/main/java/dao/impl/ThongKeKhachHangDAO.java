package dao.impl;

import db.JPAUtil;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThongKeKhachHangDAO extends BaseDAO {

    // === CONSTANTS ĐỊNH NGHĨA KHÁCH HÀNG ===
    private final double MOC_VIP = 50000000;    // > 50 triệu
    private final int SO_NGAY_NGU_DONG = 180;   // 6 tháng
    private final int MOC_THAN_THIET = 10;      // >= 10 lần
    // Khách quay lại sẽ là khoảng giữa (>=2 và < MOC_THAN_THIET)

    // === TÊN BẢNG & CỘT ===
    private final String TBL_KHACH_HANG = "KhachHang";
    private final String TBL_VE = "Ve";
    private final String TBL_DON_DAT_CHO = "DonDatCho";

    // Cột
    private final String COL_DDC_ID = "donDatChoID";
    private final String COL_DDC_KH_ID = "khachHangID";
    private final String COL_DDC_THOI_DIEM = "thoiDiemDatCho";

    private final String COL_KH_ID = "khachHangID";
    private final String COL_KH_HO_TEN = "hoTen";
    // Đã xóa COL_KH_DIA_CHI vì không dùng để tách Khu vực nữa
    private final String COL_KH_LOAI_DOI_TUONG = "loaiDoiTuongID";

    private final String COL_VE_ID = "veID";
    private final String COL_VE_GIA = "gia";
    private final String COL_VE_DDC_ID = "donDatChoID";

    // ======================================================================
    //  HELPER: Chạy native SQL qua Hibernate Session.doWork
    // ======================================================================
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

    public List<String> getDanhSachLoaiDoiTuong() throws SQLException {
        String sql = "SELECT DISTINCT " + COL_KH_LOAI_DOI_TUONG + " FROM " + TBL_KHACH_HANG;
        return doWithConnection(conn -> {
            List<String> danhSach = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String val = rs.getString(COL_KH_LOAI_DOI_TUONG);
                    if (val != null) danhSach.add(val);
                }
            }
            return danhSach;
        });
    }

    /**
     * Hàm thống kê RFM.
     * Đã loại bỏ logic xử lý Khu Vực trong SQL để tối ưu hiệu năng.
     */
    public Map<String, KhachHangRFM> getThongKeKhachHang(
            LocalDate tuNgay, LocalDate denNgay,
            String loaiDoiTuong, String phanLoai) throws SQLException {

        // Câu lệnh SQL đã được rút gọn phần xử lý địa chỉ
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
                        // ĐÃ XÓA DÒNG XỬ LÝ ĐỊA CHỈ TẠI ĐÂY
                        "        CASE " +
                        "           WHEN g.TongChiTieu_Global >= ? THEN N'VIP' " +
                        "           WHEN CAST(g.LanMuaCuoi_Global AS DATE) < CAST(DATEADD(day, -?, GETDATE()) AS DATE) THEN N'Ngủ đông' " +
                        "           WHEN g.SoLanMua_Global >= ? THEN N'Thân thiết' " +
                        "           WHEN g.SoLanMua_Global >= 2 THEN N'Khách quay lại' " +
                        "           ELSE N'Khách mới' " +
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

                // CTE 1: RFM_Global
                COL_DDC_KH_ID, COL_VE_ID, COL_VE_GIA, COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM,
                TBL_VE, TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID, COL_DDC_KH_ID,

                // CTE 2: PhanLoai_Global (Đã bỏ COL_KH_DIA_CHI)
                COL_KH_HO_TEN, COL_KH_LOAI_DOI_TUONG,
                TBL_KHACH_HANG, COL_KH_ID,

                // CTE 3: KhachHang_TrongKy
                COL_DDC_KH_ID,
                TBL_VE, TBL_DON_DAT_CHO, COL_VE_DDC_ID, COL_DDC_ID,
                COL_DDC_THOI_DIEM, COL_DDC_THOI_DIEM,

                // Final Join
                COL_DDC_KH_ID
        );

        // Thêm bộ lọc động
        if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) sql += " AND p.LoaiDoiTuong = ? ";
        if (phanLoai != null && !phanLoai.equals("Tất cả")) sql += " AND p.LoaiKH = ? ";

        sql += " ORDER BY p.TongChiTieu_Global DESC";

        final String finalSql = sql;
        return doWithConnection(conn -> {
            Map<String, KhachHangRFM> results = new LinkedHashMap<>();
            try (PreparedStatement pstmt = conn.prepareStatement(finalSql)) {
                int idx = 1;
                // Tham số cho CASE WHEN (Phân loại)
                pstmt.setDouble(idx++, MOC_VIP);
                pstmt.setInt(idx++, SO_NGAY_NGU_DONG);
                pstmt.setInt(idx++, MOC_THAN_THIET);

                // Tham số cho WHERE Time Range
                pstmt.setDate(idx++, java.sql.Date.valueOf(tuNgay));
                pstmt.setDate(idx++, java.sql.Date.valueOf(denNgay.plusDays(1)));

                // Tham số cho Dynamic Filters
                if (loaiDoiTuong != null && !loaiDoiTuong.equals("Tất cả")) pstmt.setString(idx++, loaiDoiTuong);
                if (phanLoai != null && !phanLoai.equals("Tất cả")) pstmt.setString(idx++, phanLoai);

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        KhachHangRFM item = new KhachHangRFM();
                        item.khachHangID = rs.getString("KH_ID");
                        item.hoTen = rs.getString("HoTen");
                        // Không lấy cột KhuVuc nữa
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
        });
    }

    @FunctionalInterface
    interface SqlWork<R> {
        R execute(java.sql.Connection conn) throws SQLException;
    }

    /**
     * DTO lưu kết quả thống kê (Đã xóa trường khuVuc)
     */
    public static class KhachHangRFM {
        public String khachHangID;
        public String hoTen;
        public String loaiDoiTuong;
        public int soLanMua;
        public double tongChiTieu;
        public LocalDate lanMuaCuoi;
        public LocalDate ngayMuaDauTien;
        public String phanLoai;

        public KhachHangRFM() {
        }
    }
}