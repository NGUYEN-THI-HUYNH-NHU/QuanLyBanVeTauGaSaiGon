package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.type.LoaiDichVu;

public class ThongKeNhanVien_DAO {

	public ThongKeNhanVien_DAO() {
		ConnectDB.getInstance().connect();
	}

	private Timestamp createTimestamp(LocalDate date, LocalTime time) {
		return Timestamp.valueOf(LocalDateTime.of(date, time));
	}

	// ====================== TỔNG SỐ HÓA ĐƠN HOÀN THÀNH ======================
	public int getTongSoHoaDonBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		int count = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE nhanVienID = ? "
				+ "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, maNV);
			pst.setTimestamp(2, createTimestamp(ngay, gioBD));
			pst.setTimestamp(3, createTimestamp(ngay, gioKT));
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

	// ====================== TỔNG SỐ HÓA ĐƠN ĐỔI / TRẢ ======================
	public int getTongSoHoaDonDoiTra(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		int count = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT COUNT(hoaDonID) FROM HoaDon " + "WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%') "
				+ "AND nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, maNV);
			pst.setTimestamp(2, createTimestamp(ngay, gioBD));
			pst.setTimestamp(3, createTimestamp(ngay, gioKT));
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

	// ====================== TỔNG SỐ VÉ BÁN ======================
	public int getTongSoVeBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		int soLuong = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT SUM(ct.soLuong) FROM HoaDonChiTiet ct " + "JOIN HoaDon hd ON hd.hoaDonID = ct.hoaDonID "
				+ "WHERE ct.loaiDichVu = ? " + "AND hd.nhanVienID = ? "
				+ "AND hd.thoiDiemTao >= ? AND hd.thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, LoaiDichVu.VE_BAN.toString());
			pst.setString(2, maNV);
			pst.setTimestamp(3, createTimestamp(ngay, gioBD));
			pst.setTimestamp(4, createTimestamp(ngay, gioKT));
			rs = pst.executeQuery();
			if (rs.next()) {
				soLuong = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst);
		}

		return soLuong;
	}

	// ====================== TỔNG TIỀN CHUYỂN KHOẢN ======================
	public double getTongTienChuyenKhoan(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		double total = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT SUM(CASE WHEN isThanhToanTienMat = 0 THEN tongTien ELSE 0 END) "
				+ "FROM HoaDon WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, maNV);
			pst.setTimestamp(2, createTimestamp(ngay, gioBD));
			pst.setTimestamp(3, createTimestamp(ngay, gioKT));
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

	// ====================== TỔNG TIỀN MẶT ======================
	public double getTongTienMat(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		double total = 0;
		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		String sql = "SELECT SUM(CASE WHEN isThanhToanTienMat = 1 THEN tongTien ELSE 0 END) "
				+ "FROM HoaDon WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? ";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, maNV);
			pst.setTimestamp(2, createTimestamp(ngay, gioBD));
			pst.setTimestamp(3, createTimestamp(ngay, gioKT));
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

	// ====================== LẤY DANH SÁCH HÓA ĐƠN (ĐÃ SỬA) ======================
	public List<Object[]> getListHoaDonTrongCa(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
		List<Object[]> list = new ArrayList<>();

		Connection con = ConnectDB.getInstance().getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;

		// Bỏ cột trangThai khỏi SELECT list (hoặc giữ lại tùy nhu cầu, nhưng logic dưới
		// sẽ không dùng)
		String sql = "SELECT hoaDonID, thoiDiemTao, tongTien, isThanhToanTienMat " + "FROM HoaDon "
				+ "WHERE nhanVienID = ? " + "AND thoiDiemTao >= ? AND thoiDiemTao <= ? " + "ORDER BY thoiDiemTao DESC";

		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, maNV);
			pst.setTimestamp(2, createTimestamp(ngay, gioBD));
			pst.setTimestamp(3, createTimestamp(ngay, gioKT));
			rs = pst.executeQuery();

			DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

			while (rs.next()) {
				String maHD = rs.getString(1);
				Timestamp t = rs.getTimestamp(2);
				double tongTien = rs.getDouble(3);
				int isTienMat = rs.getInt(4);

				String hinhThuc;
				if (isTienMat == 1) {
					hinhThuc = "Tiền mặt";
				} else {
					hinhThuc = "Chuyển khoản";
				}

				String tt; // Biến trạng thái để hiển thị (thay cho cột trạng thái trong DB)

				// Xác định trạng thái hiển thị
				if (maHD.startsWith("HDHV")) {
					tt = "Hoàn vé";
				} else if (maHD.startsWith("HDDV")) {
					tt = "Đổi vé";
				} else {
					tt = "Hoàn thành";
				}

				// === BỎ CỘT TRẠNG THÁI KHỎI MẢNG OBJECT TRẢ VỀ ===
				list.add(new Object[] { maHD, t != null ? t.toLocalDateTime().format(f) : "N/A", tongTien, hinhThuc, tt // Cột
																														// Trạng
																														// thái
																														// (đã
																														// xử
																														// lý)
																														// được
																														// đặt
																														// vào
																														// vị
																														// trí
																														// cột
																														// thứ
																														// 5
				});
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, pst);
		}

		return list;
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
}