package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;

public class Dashboard_DAO {

	// =========================================================================
	// 1. KPI & TỔNG QUAN
	// =========================================================================
	public double getKpiTotalRevenue(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT SUM(tongTien) AS TongDoanhThu FROM HoaDon WHERE 1=1 ");
		if (startDate != null) {
			sql.append(" AND thoiDiemTao >= ?");
		}
		if (endDate != null) {
			sql.append(" AND thoiDiemTao < ?");
		}
		try (Connection conn = ConnectDB.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			if (startDate != null) {
				pstmt.setObject(idx++, startDate.atStartOfDay());
			}
			if (endDate != null) {
				pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
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
				"SELECT COUNT(veID) AS SoVeBan FROM Ve v JOIN DonDatCho d ON v.donDatChoID = d.donDatChoID WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG')");
		if (startDate != null) {
			sql.append(" AND d.thoiDiemDatCho >= ?");
		}
		if (endDate != null) {
			sql.append(" AND d.thoiDiemDatCho < ?");
		}
		return executeCountQuery(sql.toString(), startDate, endDate, "SoVeBan");
	}

	public int getTotalAvailableSeats(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT SUM(t.sucChua) AS TongSoGhe FROM Chuyen c JOIN Tau tau ON c.tauID = tau.tauID JOIN Toa t ON t.tauID = tau.tauID WHERE 1=1 ");
		if (startDate != null) {
			sql.append(" AND c.ngayDi >= ?");
		}
		if (endDate != null) {
			sql.append(" AND c.ngayDi <= ?");
		}
		try (Connection conn = ConnectDB.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			if (startDate != null) {
				pstmt.setObject(idx++, java.sql.Date.valueOf(startDate));
			}
			if (endDate != null) {
				pstmt.setObject(idx++, java.sql.Date.valueOf(endDate));
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

	public int getTotalRefundsAndExchanges(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')");
		if (startDate != null) {
			sql.append(" AND thoiDiemTao >= ?");
		}
		if (endDate != null) {
			sql.append(" AND thoiDiemTao < ?");
		}
		return executeCountQuery(sql.toString(), startDate, endDate, "SoLuong");
	}

	// =========================================================================
	// 2. BIỂU ĐỒ (Chart Data)
	// =========================================================================
	public Map<LocalDate, Double> getRevenueOverTime(LocalDate startDate, LocalDate endDate) {
		return getRevenueData(startDate, endDate, "CAST(thoiDiemTao AS DATE)");
	}

	public Map<LocalDate, Double> getRevenueOverTimeByMonth(LocalDate startDate, LocalDate endDate) {
		return getRevenueData(startDate, endDate, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)");
	}

	public Map<LocalDate, Double> getRevenueOverTimeByYear(LocalDate startDate, LocalDate endDate) {
		return getRevenueData(startDate, endDate, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)");
	}

	private Map<LocalDate, Double> getRevenueData(LocalDate s, LocalDate e, String datePart) {
		StringBuilder sql = new StringBuilder(
				"SELECT " + datePart + " AS KeyDate, SUM(tongTien) AS DoanhThu FROM HoaDon WHERE 1=1 ");
		if (s != null) {
			sql.append(" AND thoiDiemTao >= ?");
		}
		if (e != null) {
			sql.append(" AND thoiDiemTao < ?");
		}
		sql.append(" GROUP BY " + datePart + " ORDER BY KeyDate");
		return executeQueryForDateMap(sql.toString(), s, e, "KeyDate");
	}

	public Map<LocalDate, Integer> getInvoicesPaidOverTime(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", false);
	}

	public Map<LocalDate, Integer> getInvoicesRefundedOverTime(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "CAST(thoiDiemTao AS DATE)", true);
	}

	public Map<LocalDate, Integer> getInvoicesPaidByMonth(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", false);
	}

	public Map<LocalDate, Integer> getInvoicesRefundedByMonth(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "DATEADD(month, DATEDIFF(month, 0, thoiDiemTao), 0)", true);
	}

	public Map<LocalDate, Integer> getInvoicesPaidByYear(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", false);
	}

	public Map<LocalDate, Integer> getInvoicesRefundedByYear(LocalDate s, LocalDate e) {
		return getInvoiceData(s, e, "DATEADD(year, DATEDIFF(year, 0, thoiDiemTao), 0)", true);
	}

	private Map<LocalDate, Integer> getInvoiceData(LocalDate s, LocalDate e, String datePart, boolean isRefund) {
		String cond = isRefund ? " AND (hoaDonID LIKE 'HDHV%' OR hoaDonID LIKE 'HDDV%')"
				: " AND hoaDonID NOT LIKE 'HDHV%' AND hoaDonID NOT LIKE 'HDDV%'";
		StringBuilder sql = new StringBuilder(
				"SELECT " + datePart + " AS Ngay, COUNT(hoaDonID) AS SoLuong FROM HoaDon WHERE 1=1 " + cond);
		if (s != null) {
			sql.append(" AND thoiDiemTao >= ?");
		}
		if (e != null) {
			sql.append(" AND thoiDiemTao < ?");
		}
		sql.append(" GROUP BY " + datePart + " ORDER BY Ngay");

		return executeQueryForIntMapDateKey(sql.toString(), s, e);
	}

	public Map<LocalDate, Map<String, Integer>> getTicketsBySeatTypeOverTime(LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("SELECT CAST(hd.thoiDiemTao AS DATE) AS Ngay, "
				+ "    CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END AS LoaiGhe, "
				+ "    COUNT(v.veID) AS SoLuong "
				+ "FROM Ve v JOIN HoaDonChiTiet hdct ON v.veID = hdct.veID JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID JOIN Ghe g ON v.gheID = g.gheID JOIN Toa t ON g.toaID = t.toaID "
				+ "WHERE v.trangThai IN ('DA_BAN', 'DA_DUNG') ");
		if (startDate != null) {
			sql.append(" AND hd.thoiDiemTao >= ?");
		}
		if (endDate != null) {
			sql.append(" AND hd.thoiDiemTao < ?");
		}
		sql.append(
				" GROUP BY CAST(hd.thoiDiemTao AS DATE), CASE WHEN t.hangToaID = 'GN_K4' THEN N'Giường nằm 4' WHEN t.hangToaID = 'GN_K6' THEN N'Giường nằm 6' WHEN t.hangToaID = 'NM_CLC' THEN N'Ghế ngồi' ELSE N'Khác' END ORDER BY Ngay, LoaiGhe");
		Map<LocalDate, Map<String, Integer>> result = new LinkedHashMap<>();
		try (Connection conn = ConnectDB.getInstance().getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
			int idx = 1;
			if (startDate != null) {
				pstmt.setObject(idx++, startDate.atStartOfDay());
			}
			if (endDate != null) {
				pstmt.setObject(idx++, endDate.plusDays(1).atStartOfDay());
			}
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					java.sql.Date d = rs.getDate("Ngay");
					if (d != null) {
						result.putIfAbsent(d.toLocalDate(), new HashMap<>());
						result.get(d.toLocalDate()).put(rs.getString("LoaiGhe"), rs.getInt("SoLuong"));
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// =========================================================================
	// 3. CẢNH BÁO & CHI TIẾT CHUYẾN TÀU (QUAN TRỌNG)
	// =========================================================================

	/**
	 * Đếm số lượng chuyến tàu cảnh báo. index 0: High Occupancy (>=90%) index 1:
	 * Low Occupancy (<40% & sắp chạy trong 48h)
	 */
	public int[] getTripOccupancyAlerts(LocalDate startDate, LocalDate endDate) {
		int[] counts = { 0, 0 };
		// Dùng lại hàm lấy danh sách chi tiết để đếm size, tránh lặp logic
		List<Object[]> highList = getOccupancyList(startDate, endDate, true);
		List<Object[]> lowList = getOccupancyList(startDate, endDate, false);
		counts[0] = highList.size();
		counts[1] = lowList.size();
		return counts;
	}

	/** Lấy danh sách chi tiết chuyến sắp hết vé */
	public List<Object[]> getHighOccupancyList(LocalDate startDate, LocalDate endDate) {
		return getOccupancyList(startDate, endDate, true);
	}

	/** Lấy danh sách chi tiết chuyến bán thấp */
	public List<Object[]> getLowOccupancyList(LocalDate startDate, LocalDate endDate) {
		return getOccupancyList(startDate, endDate, false);
	}

	// Hàm core xử lý logic lọc chuyến tàu
	private List<Object[]> getOccupancyList(LocalDate startDate, LocalDate endDate, boolean isHighOccupancy) {
		List<Object[]> list = new ArrayList<>();
		String sql = "SELECT c.chuyenID, c.tuyenID, c.ngayDi, c.gioDi, "
				+ "    (SELECT moTa FROM Tuyen WHERE tuyenID = c.tuyenID) as TenTuyen, "
				+ "    (SELECT COUNT(*) FROM Ve v WHERE v.chuyenID = c.chuyenID AND v.trangThai = 'DA_BAN') AS SoVeDaBan, "
				+ "    (SELECT COUNT(*) FROM Ghe g JOIN Toa t ON g.toaID = t.toaID WHERE t.tauID = c.tauID) AS TongSoGhe "
				+ "FROM Chuyen c WHERE c.ngayDi BETWEEN ? AND ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			if (startDate == null) {
				startDate = LocalDate.of(2000, 1, 1);
			}
			if (endDate == null) {
				endDate = LocalDate.of(2099, 12, 31);
			}
			ps.setDate(1, java.sql.Date.valueOf(startDate));
			ps.setDate(2, java.sql.Date.valueOf(endDate));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int soVe = rs.getInt("SoVeDaBan");
					int tongGhe = rs.getInt("TongSoGhe");
					if (tongGhe == 0) {
						continue;
					}

					double tyLe = (double) soVe / tongGhe * 100;
					String chuyenID = rs.getString("chuyenID");
					String tuyenID = rs.getString("tuyenID");
					String moTaTuyen = rs.getString("TenTuyen");
					java.sql.Date ngayDi = rs.getDate("ngayDi");
					java.sql.Time gioDi = rs.getTime("gioDi");

					// Tách ga đi/đến từ mô tả (VD: "Hà Nội - Sài Gòn")
					String gaDi = "N/A", gaDen = "N/A";
					if (moTaTuyen != null && moTaTuyen.contains("-")) {
						String[] parts = moTaTuyen.split("-");
						if (parts.length >= 2) {
							gaDi = parts[0].trim();
							gaDen = parts[1].trim();
						}
					}

					boolean match = false;
					if (isHighOccupancy) {
						if (tyLe >= 90) {
							match = true;
						}
					} else {
						// Logic bán thấp: < 40% VÀ Sắp chạy (trong 48h)
						if (tyLe < 40) {
							LocalDateTime now = LocalDateTime.now();
							LocalDateTime departure = LocalDateTime.of(ngayDi.toLocalDate(), gioDi.toLocalTime());
							if (departure.isAfter(now) && departure.isBefore(now.plusHours(48))) {
								match = true;
							}
						}
					}

					if (match) {
						// Object[] trả về: ChuyenID, TuyenID, GaDi, GaDen, NgayDi, GioDi, SoVe, TyLe
						list.add(new Object[] { chuyenID, tuyenID, gaDi, gaDen, ngayDi.toLocalDate(),
								gioDi.toLocalTime(), soVe, Math.round(tyLe * 100.0) / 100.0 });
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// =========================================================================
	// 4. HELPER METHODS
	// =========================================================================
	private Map<LocalDate, Double> executeQueryForDateMap(String sql, LocalDate s, LocalDate e, String col) {
		Map<LocalDate, Double> r = new LinkedHashMap<>();
		try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
			int i = 1;
			if (s != null) {
				p.setObject(i++, s.atStartOfDay());
			}
			if (e != null) {
				p.setObject(i++, e.plusDays(1).atStartOfDay());
			}
			try (ResultSet rs = p.executeQuery()) {
				while (rs.next()) {
					if (rs.getDate(col) != null) {
						r.put(rs.getDate(col).toLocalDate(), rs.getDouble("DoanhThu"));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}

	private Map<LocalDate, Integer> executeQueryForIntMapDateKey(String sql, LocalDate s, LocalDate e) {
		Map<LocalDate, Integer> r = new LinkedHashMap<>();
		try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
			int i = 1;
			if (s != null) {
				p.setObject(i++, s.atStartOfDay());
			}
			if (e != null) {
				p.setObject(i++, e.plusDays(1).atStartOfDay());
			}
			try (ResultSet rs = p.executeQuery()) {
				while (rs.next()) {
					if (rs.getDate("Ngay") != null) {
						r.put(rs.getDate("Ngay").toLocalDate(), rs.getInt("SoLuong"));
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return r;
	}

	private int executeCountQuery(String sql, LocalDate s, LocalDate e, String col) {
		try (Connection c = ConnectDB.getInstance().getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
			int i = 1;
			if (s != null) {
				p.setObject(i++, s.atStartOfDay());
			}
			if (e != null) {
				p.setObject(i++, e.plusDays(1).atStartOfDay());
			}
			try (ResultSet rs = p.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(col);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}
}