package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import connectDB.ConnectDB;

/**
 * DAO thống kê cuối ca làm việc của nhân viên. ĐÃ FIX: - TIME vs DATETIME (dùng
 * LocalDateTime + Timestamp) - Đổi hinhThucThanhToan -> isThanhToanTienMat -
 * Đồng bộ đúng schema từ HeThongQuanLyBanVeTauGaSaiGon_V8.sql
 */
public class ThongKeNhanVien_DAO {

	public ThongKeNhanVien_DAO() {
		ConnectDB.getInstance().connect();
	}

	// ================================
	// HÀM TẠO DATETIME GỘP NGÀY + GIỜ
	// ================================
	private LocalDateTime toDateTime(LocalDate ngay, LocalTime gio) {
		return LocalDateTime.of(ngay, gio);
	}

	private void close(ResultSet rs, PreparedStatement pst) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pst != null) {
				pst.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ================================
	// 1. TỔNG HÓA ĐƠN HOÀN THÀNH
	// ================================
	public int getTongSoHoaDonBanDuoc(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa,
			LocalTime gioKetThucCa) {

		int count = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? "
				+ "AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);

			LocalDateTime start = toDateTime(ngayLamViec, gioBatDauCa);
			LocalDateTime end = toDateTime(ngayLamViec, gioKetThucCa);

			pst.setString(1, maNhanVien);
			pst.setTimestamp(2, Timestamp.valueOf(start));
			pst.setTimestamp(3, Timestamp.valueOf(end));

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

	// ================================
	// 2. TỔNG HÓA ĐƠN ĐỔI / TRẢ
	// ================================
	public int getTongSoHoaDonDoiTra(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa,
			LocalTime gioKetThucCa) {

		int count = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE hoaDonID LIKE 'HDHV%' " + "OR hoaDonID LIKE 'HDDV%'"
				+ "AND nhanVienID = ? " + "AND thoiDiemTao >= ? " + "AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);

			LocalDateTime start = toDateTime(ngayLamViec, gioBatDauCa);
			LocalDateTime end = toDateTime(ngayLamViec, gioKetThucCa);

			pst.setString(1, maNhanVien);
			pst.setTimestamp(2, Timestamp.valueOf(start));
			pst.setTimestamp(3, Timestamp.valueOf(end));

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

	// ================================
	// 3. TỔNG VÉ BÁN TRONG CA
	// ================================
	public int getTongSoVeBanDuoc(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa,
			LocalTime gioKetThucCa) {

		int count = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(cthd.veID) FROM HoaDonChiTiet cthd "
				+ "JOIN HoaDon hd ON cthd.hoaDonID = hd.hoaDonID " + "WHERE hd.nhanVienID = ? "
				+ "AND hd.thoiDiemTao >= ? " + "AND hd.thoiDiemTao <= ?";

		try {
			pst = con.prepareStatement(sql);

			LocalDateTime start = toDateTime(ngayLamViec, gioBatDauCa);
			LocalDateTime end = toDateTime(ngayLamViec, gioKetThucCa);

			pst.setString(1, maNhanVien);
			pst.setTimestamp(2, Timestamp.valueOf(start));
			pst.setTimestamp(3, Timestamp.valueOf(end));

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

	// ================================
	// 4. TỔNG TIỀN CHUYỂN KHOẢN (isThanhToanTienMat = 0)
	// ================================
	public double getTongTienChuyenKhoan(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa,
			LocalTime gioKetThucCa) {

		double total = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT SUM(tongTien) FROM HoaDon " + "WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? "
				+ "AND thoiDiemTao <= ? " + "AND isThanhToanTienMat = 0 "; // CHUYỂN KHOẢN

		try {
			pst = con.prepareStatement(sql);

			LocalDateTime start = toDateTime(ngayLamViec, gioBatDauCa);
			LocalDateTime end = toDateTime(ngayLamViec, gioKetThucCa);

			pst.setString(1, maNhanVien);
			pst.setTimestamp(2, Timestamp.valueOf(start));
			pst.setTimestamp(3, Timestamp.valueOf(end));

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

	// ================================
	// 5. TỔNG TIỀN MẶT (isThanhToanTienMat = 1)
	// ================================
	public double getTongTienMat(String maNhanVien, LocalDate ngayLamViec, LocalTime gioBatDauCa,
			LocalTime gioKetThucCa) {

		double total = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT SUM(tongTien) FROM HoaDon " + "WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? "
				+ "AND thoiDiemTao <= ? " + "AND isThanhToanTienMat = 1 "; // TIỀN MẶT

		try {
			pst = con.prepareStatement(sql);

			LocalDateTime start = toDateTime(ngayLamViec, gioBatDauCa);
			LocalDateTime end = toDateTime(ngayLamViec, gioKetThucCa);

			pst.setString(1, maNhanVien);
			pst.setTimestamp(2, Timestamp.valueOf(start));
			pst.setTimestamp(3, Timestamp.valueOf(end));

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
}
