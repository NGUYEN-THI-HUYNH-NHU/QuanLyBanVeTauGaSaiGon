package dao;

import connectDB.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Date; // Dùng java.sql.Date cho các thao tác với CSDL
import java.sql.Timestamp; // Để xử lý thời gian cụ thể hơn nếu cần

/**
 * DAO (Data Access Object) để lấy dữ liệu thống kê cuối ca cho nhân viên.
 * Các phương thức sẽ truy vấn CSDL để tính toán các số liệu như:
 * tổng hóa đơn, vé bán, doanh thu theo hình thức thanh toán.
 */
public class ThongKeNhanVien_DAO {

    public ThongKeNhanVien_DAO() {
        // Đảm bảo kết nối CSDL được khởi tạo
        ConnectDB.getInstance().connect();
    }

    /**
     * Lấy tổng số hóa đơn bán được bởi một nhân viên trong một ca làm việc cụ thể.
     * Hóa đơn bán được (không phải hóa đơn hoàn/đổi)
     * @param maNhanVien Mã nhân viên
     * @param ngayLamViec Ngày làm việc
     * @param gioBatDauCa Giờ bắt đầu của ca làm việc
     * @param gioKetThucCa Giờ kết thúc của ca làm việc
     * @return Tổng số hóa đơn bán được
     */
    public int getTongSoHoaDonBanDuoc(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa, LocalTime gioKetThucCa) {
        int count = 0;
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;

        // Giả định bảng HoaDon có các trường: maHoaDon, maNhanVien, ngayLapHoaDon, gioLapHoaDon, trangThaiHoaDon
        // Và trangThaiHoaDon = 'HOAN_THANH' hoặc tương tự để chỉ hóa đơn bán thành công
        String sql = "SELECT COUNT(maHoaDon) FROM HoaDon " +
                "WHERE maNhanVien = ? " +
                "AND CAST(ngayLapHoaDon AS DATE) = ? " +
                "AND CAST(ngayLapHoaDon AS TIME) >= ? " +
                "AND CAST(ngayLapHoaDon AS TIME) <= ? " +
                "AND trangThaiHoaDon = N'Hoàn thành'"; // Hoặc trạng thái khác phù hợp

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, maNhanVien);
            pst.setDate(2, Date.valueOf(ngayLamViec)); // Chuyển LocalDate sang java.sql.Date
            pst.setTime(3, java.sql.Time.valueOf(gioBatDauCa)); // Chuyển LocalTime sang java.sql.Time
            pst.setTime(4, java.sql.Time.valueOf(gioKetThucCa));

            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, pst);
        }
        return count;
    }

    /**
     * Lấy tổng số hóa đơn đổi trả (hoặc hủy) bởi một nhân viên trong một ca làm việc cụ thể.
     * @param maNhanVien Mã nhân viên
     * @param ngayLamViec Ngày làm việc
     * @param gioBatDauCa Giờ bắt đầu của ca làm việc
     * @param gioKetThucCa Giờ kết thúc của ca làm việc
     * @return Tổng số hóa đơn đổi trả
     */
    public int getTongSoHoaDonDoiTra(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa, LocalTime gioKetThucCa) {
        int count = 0;
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;

        // Giả định có trạng thái 'HOAN_TRA' hoặc 'HUY' cho hóa đơn
        String sql = "SELECT COUNT(maHoaDon) FROM HoaDon " +
                "WHERE maNhanVien = ? " +
                "AND CAST(ngayLapHoaDon AS DATE) = ? " +
                "AND CAST(ngayLapHoaDon AS TIME) >= ? " +
                "AND CAST(ngayLapHoaDon AS TIME) <= ? " +
                "AND trangThaiHoaDon = N'Đổi/Trả'"; // Hoặc trạng thái khác phù hợp

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, maNhanVien);
            pst.setDate(2, Date.valueOf(ngayLamViec));
            pst.setTime(3, java.sql.Time.valueOf(gioBatDauCa));
            pst.setTime(4, java.sql.Time.valueOf(gioKetThucCa));

            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, pst);
        }
        return count;
    }

    /**
     * Lấy tổng số vé bán được (từ các hóa đơn 'Hoàn thành') bởi một nhân viên trong một ca làm việc cụ thể.
     * @param maNhanVien Mã nhân viên
     * @param ngayLamViec Ngày làm việc
     * @param gioBatDauCa Giờ bắt đầu của ca làm việc
     * @param gioKetThucCa Giờ kết thúc của ca làm việc
     * @return Tổng số vé bán được
     */
    public int getTongSoVeBanDuoc(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa, LocalTime gioKetThucCa) {
        int count = 0;
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;

        // Giả định mỗi ChiTietHoaDon tương ứng với 1 vé
        // Cần JOIN với bảng HoaDon để lọc theo nhân viên và thời gian
        String sql = "SELECT COUNT(cthd.maVe) FROM ChiTietHoaDon cthd " +
                "JOIN HoaDon hd ON cthd.maHoaDon = hd.maHoaDon " +
                "WHERE hd.maNhanVien = ? " +
                "AND CAST(hd.ngayLapHoaDon AS DATE) = ? " +
                "AND CAST(hd.ngayLapHoaDon AS TIME) >= ? " +
                "AND CAST(hd.ngayLapHoaDon AS TIME) <= ? " +
                "AND hd.trangThaiHoaDon = N'Hoàn thành'"; // Chỉ đếm vé từ hóa đơn hoàn thành

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, maNhanVien);
            pst.setDate(2, Date.valueOf(ngayLamViec));
            pst.setTime(3, java.sql.Time.valueOf(gioBatDauCa));
            pst.setTime(4, java.sql.Time.valueOf(gioKetThucCa));

            rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, pst);
        }
        return count;
    }

    /**
     * Lấy tổng tiền thu được qua hình thức chuyển khoản bởi một nhân viên
     * trong một ca làm việc cụ thể.
     * @param maNhanVien Mã nhân viên
     * @param ngayLamViec Ngày làm việc
     * @param gioBatDauCa Giờ bắt đầu của ca làm việc
     * @param gioKetThucCa Giờ kết thúc của ca làm việc
     * @return Tổng tiền chuyển khoản
     */
    public double getTongTienChuyenKhoan(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa, LocalTime gioKetThucCa) {
        double total = 0.0;
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;

        // Giả định bảng HoaDon có trường hinhThucThanhToan (e.g., 'Chuyen Khoan', 'Tien Mat')
        String sql = "SELECT SUM(tongTien) FROM HoaDon " +
                "WHERE maNhanVien = ? " +
                "AND CAST(ngayLapHoaDon AS DATE) = ? " +
                "AND CAST(ngayLapHoaDon AS TIME) >= ? " +
                "AND CAST(ngayLapHoaDon AS TIME) <= ? " +
                "AND hinhThucThanhToan = N'Chuyển khoản' " +
                "AND trangThaiHoaDon = N'Hoàn thành'";

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, maNhanVien);
            pst.setDate(2, Date.valueOf(ngayLamViec));
            pst.setTime(3, java.sql.Time.valueOf(gioBatDauCa));
            pst.setTime(4, java.sql.Time.valueOf(gioKetThucCa));

            rs = pst.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, pst);
        }
        return total;
    }

    /**
     * Lấy tổng tiền thu được bằng tiền mặt bởi một nhân viên trong một ca làm việc cụ thể.
     * @param maNhanVien Mã nhân viên
     * @param ngayLamViec Ngày làm việc
     * @param gioBatDauCa Giờ bắt đầu của ca làm việc
     * @param gioKetThucCa Giờ kết thúc của ca làm việc
     * @return Tổng tiền mặt
     */
    public double getTongTienMat(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa, LocalTime gioKetThucCa) {
        double total = 0.0;
        Connection con = ConnectDB.getInstance().getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;

        String sql = "SELECT SUM(tongTien) FROM HoaDon " +
                "WHERE maNhanVien = ? " +
                "AND CAST(ngayLapHoaDon AS DATE) = ? " +
                "AND CAST(ngayLapHoaDon AS TIME) >= ? " +
                "AND CAST(ngayLapHoaDon AS TIME) <= ? " +
                "AND hinhThucThanhToan = N'Tiền mặt' " +
                "AND trangThaiHoaDon = N'Hoàn thành'";

        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, maNhanVien);
            pst.setDate(2, Date.valueOf(ngayLamViec));
            pst.setTime(3, java.sql.Time.valueOf(gioBatDauCa));
            pst.setTime(4, java.sql.Time.valueOf(gioKetThucCa));

            rs = pst.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, pst);
        }
        return total;
    }

    /**
     * Phương thức tiện ích để đóng ResultSet và PreparedStatement.
     * @param rs ResultSet
     * @param pst PreparedStatement
     */
    private void close(ResultSet rs, PreparedStatement pst) {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}