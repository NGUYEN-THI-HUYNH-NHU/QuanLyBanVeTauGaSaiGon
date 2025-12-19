package dao;
/*
 * @(#) HoaDon_DAO.java  1.0  [11:33:37 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {
	private final ConnectDB connectDB = ConnectDB.getInstance();

	public HoaDon_DAO() {
		connectDB.connect();
	}

	/**
	 * @param conn
	 * @param hoaDon
	 * @return
	 */
	public boolean insertHoaDon(Connection conn, HoaDon hoaDon) throws Exception {
		String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tongTien, maGD, tienNhan, tienHoan, isThanhToanTienMat) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hoaDon.getHoaDonID());
			ps.setString(2, hoaDon.getKhachHang().getKhachHangID());
			ps.setString(3, hoaDon.getNhanVien().getNhanVienID());
			ps.setTimestamp(4, java.sql.Timestamp.valueOf(hoaDon.getThoiDiemTao()));
			ps.setDouble(5, hoaDon.getTongTien());
			if (hoaDon.getMaGD() != null) {
				ps.setString(6, hoaDon.getMaGD());
				ps.setDouble(8, 0);
			} else {
				ps.setNull(6, 0);
				ps.setDouble(8, hoaDon.getTienHoan());
			}
			ps.setDouble(7, hoaDon.getTienNhan());
			ps.setBoolean(9, hoaDon.isThanhToanTienMat());

			return ps.executeUpdate() > 0;
		}
	}

	/**
	 * @param nhanVien
	 * @return
	 */
	public List<HoaDon> getHoaDonByNhanVien(NhanVien nhanVien) {
		String sql = "SELECT H.hoaDonID, H.khachHangID, K.hoTen, K.soGiayTo, H.thoiDiemTao, H.tongTien, H.tienNhan, H.tienHoan, H.isThanhToanTienMat, H.maGD\r\n"
				+ "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID\r\n" + "WHERE H.nhanVienID = ?\r\n"
				+ "ORDER BY H.thoiDiemTao DESC";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<HoaDon> dsHoaDon = new ArrayList<HoaDon>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nhanVien.getNhanVienID());
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				HoaDon hoaDon = new HoaDon();
				hoaDon.setHoaDonID(resultSet.getString("hoaDonID"));
				hoaDon.setNhanVien(nhanVien);
				hoaDon.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						resultSet.getString("soGiayTo")));
				hoaDon.setThoiDiemTao(resultSet.getTimestamp("thoiDiemTao").toLocalDateTime());
				hoaDon.setTongTien(resultSet.getDouble("tongTien"));
				hoaDon.setTienNhan(resultSet.getDouble("tienNhan"));
				hoaDon.setTienHoan(resultSet.getDouble("tienHoan"));
				hoaDon.setThanhToanTienMat(resultSet.getBoolean("isThanhToanTienMat"));
				hoaDon.setMaGD(resultSet.getString("maGD"));

				dsHoaDon.add(hoaDon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsHoaDon;
	}

	/**
	 * @param loaiHD
	 * @param khachHang
	 * @param tuNgay
	 * @param denNgay
	 * @param hinhThucTT
	 * @return
	 */
	public List<HoaDon> searchHoaDonByFilter(String loaiHD, String khachHang, String khachHangID, Date tuNgay,
			Date denNgay, String hinhThucTT) {
		List<HoaDon> list = new ArrayList<>();
		Connection conn = connectDB.getConnection();

		// 1. Khởi tạo câu truy vấn cơ bản (Join bảng để lấy thông tin khách)
		StringBuilder sql = new StringBuilder("SELECT hd.*, kh.hoTen, kh.soDienThoai, kh.soGiayTo "
				+ "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 AND ");

		List<Object> params = new ArrayList<>();

		// 2. Xử lý điều kiện NGÀY (Từ ngày ... Đến ngày)
		if (tuNgay != null) {
			sql.append(" AND hd.thoiDiemTao >= ?");
			// Chuyển về đầu ngày (00:00:00)
			params.add(new Timestamp(atStartOfDay(tuNgay).getTime()));
		}
		if (denNgay != null) {
			sql.append(" AND hd.thoiDiemTao <= ?");
			// Chuyển về cuối ngày (23:59:59)
			params.add(new Timestamp(atEndOfDay(denNgay).getTime()));
		}

		// 3. Xử lý điều kiện KHÁCH HÀNG (Tên, SĐT, CCCD, ID)
		if (khachHangID != null) {
			// Ưu tiên 1: Nếu có ID chính xác (do chọn từ auto-suggest)
			// Tìm chính xác cực nhanh (Index Scan)
			sql.append(" AND hd.khachHangID = ?");
			params.add(khachHangID);
		} else if (khachHang != null && !khachHang.trim().isEmpty()) {
			// Ưu tiên 2: Nếu không có ID, tìm theo từ khóa (Table Scan / Like)
			sql.append(
					" AND (kh.hoTen LIKE ? OR kh.soDienThoai LIKE ? OR kh.soGiayTo LIKE ? OR kh.khachHangID LIKE ?)");
			String keyword = "%" + khachHang.trim() + "%";
			params.add(keyword);
			params.add(keyword);
			params.add(keyword);
			params.add(keyword);
		}

		// 4. Xử lý HÌNH THỨC THANH TOÁN
		if (hinhThucTT != null && !hinhThucTT.equals("Tất cả")) {
			sql.append(" AND hd.isThanhToanTienMat = ?");
			params.add(hinhThucTT.equals("Tiền mặt"));
		}

		// 5. Xử lý LOẠI HÓA ĐƠN
		if (loaiHD != null && !loaiHD.equals("Tất cả")) {
			if (loaiHD.equalsIgnoreCase("Hóa đơn bán vé")) {
				sql.append(" AND hd.hoaDonID LIKE 'HD-%'");
			} else if (loaiHD.equalsIgnoreCase("Hóa đơn hoàn vé")) {
				sql.append(" AND hd.hoaDonID LIKE 'HDHV-%'");
			} else if (loaiHD.equalsIgnoreCase("Hóa đơn đổi vé")) {
				sql.append(" AND hd.hoaDonID LIKE 'HDDV-%'");
			}
		}

		// Sắp xếp giảm dần theo ngày tạo (Mới nhất lên đầu)
		sql.append(" ORDER BY hd.thoiDiemTao DESC");

		// 6. Thực thi truy vấn
		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			// Gán tham số
			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					// Mapping dữ liệu Khách Hàng
					KhachHang kh = new KhachHang();
					kh.setKhachHangID(rs.getString("khachHangID"));
					kh.setHoTen(rs.getString("hoTen"));
					kh.setSoDienThoai(rs.getString("soDienThoai"));
					kh.setSoGiayTo(rs.getString("soGiayTo"));

					// Mapping dữ liệu Hóa Đơn
					HoaDon hd = new HoaDon();
					hd.setHoaDonID(rs.getString("hoaDonID"));
					hd.setKhachHang(kh);
					hd.setNhanVien(new NhanVien(rs.getString("nhanVienID")));

					// Chuyển đổi Timestamp sang LocalDateTime
					Timestamp ts = rs.getTimestamp("thoiDiemTao");
					hd.setThoiDiemTao(ts != null ? ts.toLocalDateTime() : null);

					hd.setTongTien(rs.getDouble("tongTien"));
					hd.setTienNhan(rs.getDouble("tienNhan"));
					hd.setTienHoan(rs.getDouble("tienHoan"));
					hd.setThanhToanTienMat(rs.getBoolean("isThanhToanTienMat"));
					hd.setMaGD(rs.getString("maGD"));

					list.add(hd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	// --- Helper Methods xử lý ngày giờ ---

	private Date atStartOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime startOfDay = localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
		return localDateTimeToDate(startOfDay);
	}

	private Date atEndOfDay(Date date) {
		LocalDateTime localDateTime = dateToLocalDateTime(date);
		LocalDateTime endOfDay = localDateTime.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
		return localDateTimeToDate(endOfDay);
	}

	private LocalDateTime dateToLocalDateTime(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	private Date localDateTimeToDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public List<HoaDon> searchHoaDonByKeyword(String keyword, String type) {
		List<HoaDon> list = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT hd.*, kh.hoTen, kh.soDienThoai, kh.soGiayTo "
				+ "FROM HoaDon hd JOIN KhachHang kh ON hd.khachHangID = kh.khachHangID " + "WHERE 1=1 ");
		Connection conn = connectDB.getConnection();

		// Xử lý tìm kiếm theo Loại
		if (keyword != null && !keyword.trim().isEmpty()) {
			if (type.equals("Mã hóa đơn")) {
				sql.append(" AND hd.hoaDonID LIKE ?");
			} else if (type.equals("Mã khách hàng")) {
				sql.append(" AND hd.khachHangID LIKE ?");
			} else if (type.equals("Mã giao dịch")) {
				sql.append(" AND hd.maGD LIKE ?");
			}
		}

		sql.append(" ORDER BY hd.thoiDiemTao DESC");

		try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
			int index = 1;
			if (keyword != null && !keyword.trim().isEmpty()) {
				ps.setString(index++, "%" + keyword.trim() + "%");
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					HoaDon hd = new HoaDon();
					hd.setHoaDonID(rs.getString("hoaDonID"));

					KhachHang kh = new KhachHang();
					kh.setKhachHangID(rs.getString("khachHangID"));
					kh.setHoTen(rs.getString("hoTen"));
					kh.setSoGiayTo(rs.getString("soGiayTo")); // Để hiển thị CCCD
					hd.setKhachHang(kh);

					hd.setThoiDiemTao(rs.getTimestamp("thoiDiemTao").toLocalDateTime());
					hd.setTongTien(rs.getDouble("tongTien"));
					hd.setTienNhan(rs.getDouble("tienNhan"));
					hd.setTienHoan(rs.getDouble("tienHoan"));
					hd.setThanhToanTienMat(rs.getBoolean("isThanhToanTienMat"));
					hd.setMaGD(rs.getString("maGD"));

					list.add(hd);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// CÁC HÀM HỖ TRỢ SUGGESTION (Auto-complete)
	// Lấy Top 10 Mã Hóa Đơn gần đúng
	public List<String> getTop10HoaDonID(String keyword) {
		return getTop10String("hoaDonID", "HoaDon", keyword);
	}

	// Lấy Top 10 Mã Giao Dịch gần đúng
	public List<String> getTop10MaGD(String keyword) {
		return getTop10String("maGD", "HoaDon", keyword);
	}

	// Lấy Top 10 Mã Khách Hàng (Tìm trong bảng KhachHang để gợi ý ID tồn tại)
	public List<String> getTop10KhachHangID(String keyword) {
		return getTop10String("khachHangID", "KhachHang", keyword);
	}

	// Hàm chung để query string
	private List<String> getTop10String(String colName, String tableName, String keyword) {
		List<String> list = new ArrayList<>();
		String sql = "SELECT TOP 10 " + colName + " FROM " + tableName + " WHERE " + colName + " LIKE ?";
		Connection conn = connectDB.getConnection();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, "%" + keyword + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					String val = rs.getString(1);
					if (val != null) {
						list.add(val);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @return
	 */
	public List<HoaDon> getAllHoaDon() {
		String sql = "SELECT H.hoaDonID, H.khachHangID, K.hoTen, K.soGiayTo, H.thoiDiemTao, H.tongTien, H.tienNhan, H.tienHoan, H.isThanhToanTienMat, H.maGD\r\n"
				+ "FROM HoaDon H JOIN KhachHang K ON H.khachHangID = K.khachHangID\r\n" + "ORDER BY H.thoiDiemTao DESC";
		Connection con = connectDB.getConnection();
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		List<HoaDon> dsHoaDon = new ArrayList<HoaDon>();
		try {
			pstmt = con.prepareStatement(sql);
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				HoaDon hoaDon = new HoaDon();
				hoaDon.setHoaDonID(resultSet.getString("hoaDonID"));
				hoaDon.setKhachHang(new KhachHang(resultSet.getString("khachHangID"), resultSet.getString("hoTen"),
						resultSet.getString("soGiayTo")));
				hoaDon.setThoiDiemTao(resultSet.getTimestamp("thoiDiemTao").toLocalDateTime());
				hoaDon.setTongTien(resultSet.getDouble("tongTien"));
				hoaDon.setTienNhan(resultSet.getDouble("tienNhan"));
				hoaDon.setTienHoan(resultSet.getDouble("tienHoan"));
				hoaDon.setThanhToanTienMat(resultSet.getBoolean("isThanhToanTienMat"));
				hoaDon.setMaGD(resultSet.getString("maGD"));

				dsHoaDon.add(hoaDon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsHoaDon;
	}
}