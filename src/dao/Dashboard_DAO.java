package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import connectDB.ConnectDB;

/**
 * Data Access Object (DAO) để lấy dữ liệu cho Dashboard. [FINAL VERSION] - Chứa
 * tất cả các hàm KPI, Charts, và Drill-Down.
 */
public class Dashboard_DAO {

	public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT SUM(v.gia) AS TongDoanhThu" + " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID"
						+ " WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
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
					return rs.getDouble("TongDoanhThu");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public int getKpiTicketsSold(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(veID) AS SoVeBan " + " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID"
						+ " WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
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
					return rs.getInt("SoVeBan");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getKpiUniqueCustomers(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT khachHangID) AS SoKhachHang "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID" + " WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
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
					return rs.getInt("SoKhachHang");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public Map<String, Double> getKpiTopRevenueRoute(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT TOP 1 (t.moTa + ' (' + tau.tenTau + ')') AS TenTuyen, SUM(v.gia) AS TongDoanhThu "
						+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
						+ "JOIN Chuyen c ON v.chuyenID = c.chuyenID " + "JOIN Tuyen t ON c.tuyenID = t.tuyenID "
						+ "JOIN Tau tau ON c.tauID = tau.tauID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY t.moTa, tau.tenTau ORDER BY TongDoanhThu DESC");
		Map<String, Double> result = new HashMap<>();
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
					result.put(rs.getString("TenTuyen"), rs.getDouble("TongDoanhThu"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<LocalDate, Double> getRevenueOverTime(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT CAST(d.thoiDiemDatCho AS DATE) AS Ngay, SUM(v.gia) AS DoanhThu "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID"
				+ " WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY CAST(d.thoiDiemDatCho AS DATE) ORDER BY Ngay");
		Map<LocalDate, Double> result = new LinkedHashMap<>();
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
					result.put(rs.getDate("Ngay").toLocalDate(), rs.getDouble("DoanhThu"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map<String, Double> getTop5RevenueTrips(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT TOP 5 (t.moTa + ' (' + tau.tenTau + ')') AS TenChuyen, SUM(v.gia) AS TongDoanhThu "
						+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
						+ "JOIN Chuyen c ON v.chuyenID = c.chuyenID " + "JOIN Tuyen t ON c.tuyenID = t.tuyenID "
						+ "JOIN Tau tau ON c.tauID = tau.tauID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY t.moTa, tau.tenTau ORDER BY TongDoanhThu DESC");
		Map<String, Double> result = new LinkedHashMap<>();
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
					result.put(rs.getString("TenChuyen"), rs.getDouble("TongDoanhThu"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * [Chart B.4] Lấy cơ cấu khách hàng (theo loại đối tượng).
	 */
	public Map<String, Integer> getCustomerTypeDistribution(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT ldt.moTa, COUNT(DISTINCT v.khachHangID) AS SoLuong "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
				+ "JOIN KhachHang kh ON v.khachHangID = kh.khachHangID "
				+ "JOIN LoaiDoiTuong ldt ON kh.loaiDoiTuongID = ldt.loaiDoiTuongID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY ldt.moTa");
		Map<String, Integer> result = new HashMap<>();
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
					result.put(rs.getString("moTa"), rs.getInt("SoLuong"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * [Chart B.5] Lấy số lượng vé bán theo Loại Ghế VÀ Ngày (Stacked Bar).
	 */
	public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT CAST(d.thoiDiemDatCho AS DATE) AS Ngay, ht.moTa AS LoaiGhe, COUNT(v.veID) AS SoLuong "
						+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
						+ "JOIN Ghe g ON v.gheID = g.gheID " + "JOIN Toa t ON g.toaID = t.toaID "
						+ "JOIN HangToa ht ON t.hangToaID = ht.hangToaID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY CAST(d.thoiDiemDatCho AS DATE), ht.moTa ");
		sql.append(" ORDER BY Ngay, LoaiGhe");
		Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
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
					LocalDate ngay = rs.getDate("Ngay").toLocalDate();
					String loaiGhe = rs.getString("LoaiGhe");
					int soLuong = rs.getInt("SoLuong");
					result.putIfAbsent(ngay, new HashMap<>());
					result.get(ngay).put(loaiGhe, soLuong);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * [Chart B.7] Lấy Top 5 khuyến mãi được sử dụng nhiều nhất.
	 */
	public Map<String, Integer> getTop5Promotions(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT TOP 5 km.maKhuyenMai, COUNT(sdkm.suDungKhuyenMaiID) AS SoLanSuDung "
						+ "FROM SuDungKhuyenMai sdkm " + "JOIN KhuyenMai km ON sdkm.khuyenMaiID = km.khuyenMaiID "
						+ "JOIN HoaDonChiTiet hdct ON sdkm.hoaDonChiTietID = hdct.hoaDonChiTietID "
						+ "JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND hd.thoiDiemTao >= ?");
		}
		if (endDate != null) {
			sql.append(" AND hd.thoiDiemTao < ?");
		}
		sql.append(" GROUP BY km.maKhuyenMai ORDER BY SoLanSuDung DESC");
		Map<String, Integer> result = new LinkedHashMap<>();
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
					result.put(rs.getString("maKhuyenMai"), rs.getInt("SoLanSuDung"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// =========================================================================
	// HÀM CHO KPI VÀ DRILL-DOWN (MỚI)
	// =========================================================================

	/**
	 * [KPI Helper] Lấy tổng số ghế có sẵn cho các chuyến trong khoảng thời gian.
	 */
	public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT SUM(t.sucChua) AS TongSoGhe " + "FROM Chuyen c "
				+ "JOIN Tau tau ON c.tauID = tau.tauID " + "JOIN Toa t ON t.tauID = tau.tauID " + "WHERE 1=1");
		if (startDate != null) {
			sql.append(" AND c.ngayDi >= ?");
		}
		if (endDate != null) {
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

	/**
	 * [KPI Helper] Lấy tổng số giao dịch Hoàn/Đổi vé trong khoảng thời gian.
	 */
	public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(giaoDichHoanDoiID) AS SoLuong " + "FROM GiaoDichHoanDoi " + "WHERE 1=1");
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

	/**
	 * [DRILL-DOWN & CHART] Lấy Doanh thu theo Tuyến (Không giới hạn TOP).
	 */
	public Map<String, Double> getRevenueByRoute(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT t.moTa AS TenTuyen, SUM(v.gia) AS TongDoanhThu "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
				+ "JOIN Chuyen c ON v.chuyenID = c.chuyenID " + "JOIN Tuyen t ON c.tuyenID = t.tuyenID "
				+ "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}

		sql.append(" GROUP BY t.moTa ORDER BY TongDoanhThu DESC");

		Map<String, Double> result = new LinkedHashMap<>();
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

	/**
	 * [DRILL-DOWN & CHART] Lấy Doanh thu theo Tháng.
	 */
	public Map<String, Double> getRevenueByMonth(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT "
				+ "   CAST(MONTH(d.thoiDiemDatCho) AS VARCHAR(2)) + '/' + CAST(YEAR(d.thoiDiemDatCho) AS VARCHAR(4)) AS ThangNam, "
				+ "   SUM(v.gia) AS DoanhThu " + " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
				+ "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}

		sql.append(" GROUP BY YEAR(d.thoiDiemDatCho), MONTH(d.thoiDiemDatCho) ");
		sql.append(" ORDER BY YEAR(d.thoiDiemDatCho), MONTH(d.thoiDiemDatCho)");

		Map<String, Double> result = new LinkedHashMap<>();
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

	/**
	 * [DRILL-DOWN & CHART] Lấy Top 10 Doanh thu theo Nhân Viên.
	 */
	public Map<String, Double> getRevenueByEmployee(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT TOP 10 nv.hoTen, SUM(v.gia) AS TongDoanhThu "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
				+ "JOIN DonDatCho ddc ON v.donDatChoID = ddc.donDatChoID "
				+ "JOIN NhanVien nv ON ddc.nhanVienID = nv.nhanVienID " + "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}

		sql.append(" GROUP BY nv.nhanVienID, nv.hoTen ORDER BY TongDoanhThu DESC");

		Map<String, Double> result = new LinkedHashMap<>();
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

	/**
	 * [DRILL-DOWN & CHART] Lấy Doanh thu theo Loại Ghế.
	 */
	public Map<String, Double> getRevenueBySeatType(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT ht.moTa, SUM(v.gia) AS TongDoanhThu "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID " + "JOIN Ghe g ON v.gheID = g.gheID "
				+ "JOIN Toa t ON g.toaID = t.toaID " + "JOIN HangToa ht ON t.hangToaID = ht.hangToaID "
				+ "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}

		sql.append(" GROUP BY ht.moTa ORDER BY TongDoanhThu DESC");

		Map<String, Double> result = new LinkedHashMap<>();
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

	/**
	 * [DRILL-DOWN & CHART] Lấy dữ liệu cho Tỷ lệ Khuyến mãi (Donut Chart).
	 */
	public Map<String, Integer> getPromotionRateData(LocalDate startDate, LocalDate endDate) {
		StringBuilder sqlTotal = new StringBuilder("SELECT COUNT(hoaDonID) AS TotalInvoices FROM HoaDon WHERE 1=1");
		StringBuilder sqlPromo = new StringBuilder("SELECT COUNT(DISTINCT hd.hoaDonID) AS PromoUsed "
				+ "FROM HoaDon hd " + "JOIN HoaDonChiTiet hdct ON hd.hoaDonID = hdct.hoaDonID "
				+ "WHERE hdct.loaiDichVu = 'KHUYEN_MAI'");

		if (startDate != null) {
			sqlTotal.append(" AND thoiDiemTao >= ?");
			sqlPromo.append(" AND hd.thoiDiemTao >= ?");
		}
		if (endDate != null) {
			sqlTotal.append(" AND thoiDiemTao < ?");
			sqlPromo.append(" AND hd.thoiDiemTao < ?");
		}
		int totalInvoices = 0;
		int promoUsed = 0;
		try (Connection conn = ConnectDB.getInstance().getConnection()) {
			try (PreparedStatement pstmt = conn.prepareStatement(sqlTotal.toString())) {
				int paramIndex = 1;
				if (startDate != null) {
					pstmt.setObject(paramIndex++, startDate.atStartOfDay());
				}
				if (endDate != null) {
					pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
				}
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						totalInvoices = rs.getInt("TotalInvoices");
					}
				}
			}
			try (PreparedStatement pstmt = conn.prepareStatement(sqlPromo.toString())) {
				int paramIndex = 1;
				if (startDate != null) {
					pstmt.setObject(paramIndex++, startDate.atStartOfDay());
				}
				if (endDate != null) {
					pstmt.setObject(paramIndex++, endDate.plusDays(1).atStartOfDay());
				}
				try (ResultSet rs = pstmt.executeQuery()) {
					if (rs.next()) {
						promoUsed = rs.getInt("PromoUsed");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Map<String, Integer> result = new LinkedHashMap<>();
		result.put("Đã sử dụng", promoUsed);
		result.put("Chưa sử dụng", totalInvoices - promoUsed);
		return result;
	}

	/**
	 * [DRILL-DOWN & CHART] Lấy Cơ cấu Khách hàng (Top 2 loại).
	 */
	public Map<String, Integer> getCustomerSplitData(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT lkh.moTa, COUNT(v.veID) AS SoLuong "
				+ " FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID "
				+ "JOIN KhachHang kh ON v.khachHangID = kh.khachHangID "
				+ "JOIN LoaiKhachHang lkh ON kh.loaiKhachHangID = lkh.loaiKhachHangID "
				+ "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");

		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		sql.append(" GROUP BY lkh.moTa ORDER BY SoLuong DESC");

		Map<String, Integer> result = new LinkedHashMap<>();
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
					// Chỉ lấy 2 loại có số lượng vé cao nhất
					if (result.size() < 2) {
						String moTa = rs.getString("moTa");
						if (result.isEmpty()) {
							result.put("Khách hàng cũ", rs.getInt("SoLuong"));
						} else {
							result.put("Khách hàng mới", rs.getInt("SoLuong"));
						}
					} else {
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}