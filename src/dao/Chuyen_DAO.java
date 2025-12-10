package dao;
/*
 * @(#) Chuyen_DAO.java  1.0  [12:59:19 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.ah.A;
import connectDB.ConnectDB;
import entity.Chuyen;
import entity.ChuyenGa;
import entity.Tau;
import entity.Tuyen;
import entity.type.LoaiTau;

public class Chuyen_DAO {
	private ConnectDB connectDB;

	public Chuyen_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect();
	}

	public List<Chuyen> getChuyenByGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi) {
		Connection connection = connectDB.getConnection();
		String querySQL = "DECLARE @gaDiID VARCHAR(50) = ?;\r\n" + "DECLARE @gaDenID VARCHAR(50) = ?;\r\n"
				+ "DECLARE @ngayDi DATE = ?;\r\n" + "\r\n" + "SELECT\r\n" + "    c.chuyenID,\r\n" + "    c.tuyenID,\r\n"
				+ "    c.tauID,\r\n"
				// Lấy thêm loaiTauID từ bảng Tau
				+ "    t.loaiTauID,\r\n"
				// Thông tin tại ga đi yêu cầu
				+ "    cgDi.ngayDi   AS ngayDi,\r\n" + "    cgDi.gioDi    AS gioDi,\r\n"
				// Thông tin tại ga đến yêu cầu
				+ "    cgDen.ngayDen  AS ngayDen,\r\n" + "    cgDen.gioDen  AS gioDen\r\n" + "FROM Chuyen c\r\n"
				// JOIN với bảng Tau để lấy loaiTauID
				+ "INNER JOIN Tau t ON c.tauID = t.tauID\r\n"
				// Tìm ga đi trong lịch trình của chuyến
				+ "INNER JOIN ChuyenGa cgDi\r\n" + "    ON cgDi.chuyenID = c.chuyenID\r\n"
				+ "    AND cgDi.gaID = @gaDiID\r\n"
				// Tìm ga đến trong lịch trình của chuyến
				+ "INNER JOIN ChuyenGa cgDen\r\n" + "    ON cgDen.chuyenID = c.chuyenID\r\n"
				+ "    AND cgDen.gaID = @gaDenID\r\n" + "WHERE\r\n" + "    cgDi.ngayDi = @ngayDi\r\n"
				+ "    AND cgDi.thuTu < cgDen.thuTu\r\n" + "ORDER BY cgDi.gioDi, c.chuyenID;\r\n";

		List<Chuyen> chuyenList = new ArrayList<Chuyen>();

		try (PreparedStatement pstmt = connection.prepareStatement(querySQL)) {
			pstmt.setString(1, gaDiID);
			pstmt.setString(2, gaDenID);
			pstmt.setDate(3, java.sql.Date.valueOf(ngayDi));

			try (ResultSet resultSet = pstmt.executeQuery()) {
				while (resultSet.next()) {
					String chuyenID = resultSet.getString("chuyenID");
					Tuyen tuyen = new Tuyen(resultSet.getString("tuyenID"));
					String tauID = resultSet.getString("tauID");
					LoaiTau loaiTau = LoaiTau.valueOf(resultSet.getString("loaiTauID"));
					Tau tau = new Tau(tauID, loaiTau);

					// Lấy đúng các giá trị từ kết quả truy vấn
					LocalDate ngayDi_ThucTe = resultSet.getDate("ngayDi").toLocalDate();
					LocalTime gioDi_ThucTe = resultSet.getTime("gioDi").toLocalTime();
					LocalDate ngayDen_ThucTe = resultSet.getDate("ngayDen").toLocalDate();
					LocalTime gioDen_ThucTe = resultSet.getTime("gioDen").toLocalTime();

					Chuyen c = new Chuyen(chuyenID, tau, ngayDi_ThucTe, gioDi_ThucTe, ngayDen_ThucTe, gioDen_ThucTe);
					c.setTuyen(tuyen);

					chuyenList.add(c);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chuyenList;
	}

	public List<Chuyen> getAllChuyen(){
		List<Chuyen> list = new ArrayList<>();
		Connection con = connectDB.getInstance().getConnection();


		String sql = "SELECT c.*, t.tenTau, " +
				"(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu ASC) AS tenGaDi, " +
				"(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu DESC) AS tenGaDen, " +

				"(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, " +
				"(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc " +

				"FROM Chuyen c " +
				"JOIN Tau t ON c.tauID = t.tauID";

		try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

			while(rs.next()){
				String maChuyen = rs.getString("chuyenID");
				String tenTau = rs.getString("tenTau");
				String tenGaDi = rs.getString("tenGaDi");
				String tenGaDen = rs.getString("tenGaDen");
				String tenChuyen = (tenGaDi != null ? tenGaDi : "N/A") + " - " + (tenGaDen != null ? tenGaDen : "N/A");

				Date sqlDateDi = rs.getDate("ngayDi");
				Time sqlTimeDi = rs.getTime("gioDi");
				LocalDate ngayDi = (sqlDateDi != null) ? sqlDateDi.toLocalDate() : null;
				LocalTime gioDi = (sqlTimeDi != null) ? sqlTimeDi.toLocalTime() : null;

				Date sqlDateDen = rs.getDate("NgayDenThuc");
				Time sqlTimeDen = rs.getTime("GioDenThuc");
				LocalDate ngayDen = (sqlDateDen != null) ? sqlDateDen.toLocalDate() : null;
				LocalTime gioDen = (sqlTimeDen != null) ? sqlTimeDen.toLocalTime() : null;

				Chuyen c = new Chuyen(maChuyen);
				c.setTau(new Tau(rs.getString("tauID"), tenTau));

				if(rs.getString("tuyenID") != null) {
					c.setTuyen(new Tuyen(rs.getString("tuyenID")));
				}

				c.setNgayDi(ngayDi);
				c.setGioDi(gioDi);
				c.setNgayDen(ngayDen);
				c.setGioDen(gioDen);

				c.setGaDiHienThi(tenGaDi);
				c.setGaDenHienThi(tenGaDen);
				c.setTenChuyenHienThi(tenChuyen);

				list.add(c);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return list;
	}

	public Chuyen layChuyenTheoMa(String maChuyen) {
		Chuyen chuyen = null;
		Connection con = connectDB.getInstance().getConnection();

		String sql = "SELECT c.*, t.tenTau, " +
				"(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu ASC) AS tenGaDi, " +
				"(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu DESC) AS tenGaDen, " +
				"(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, " +
				"(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc " +

				"FROM Chuyen c " +
				"JOIN Tau t ON c.tauID = t.tauID " +
				"WHERE c.chuyenID = ?";

		try (PreparedStatement pst = con.prepareStatement(sql)) {
			pst.setString(1, maChuyen);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					String maChuyenDB = rs.getString("chuyenID");
					String tenTau = rs.getString("tenTau");
					String tenGaDi = rs.getString("tenGaDi");
					String tenGaDen = rs.getString("tenGaDen");

					String tenChuyen = (tenGaDi != null ? tenGaDi : "N/A") + " - " + (tenGaDen != null ? tenGaDen : "N/A");

					Date sqlDateDi = rs.getDate("ngayDi");
					Time sqlTimeDi = rs.getTime("gioDi");
					LocalDate ngayDi = (sqlDateDi != null) ? sqlDateDi.toLocalDate() : null;
					LocalTime gioDi = (sqlTimeDi != null) ? sqlTimeDi.toLocalTime() : null;

					Date sqlDateDen = rs.getDate("NgayDenThuc");
					Time sqlTimeDen = rs.getTime("GioDenThuc");
					LocalDate ngayDen = (sqlDateDen != null) ? sqlDateDen.toLocalDate() : null;
					LocalTime gioDen = (sqlTimeDen != null) ? sqlTimeDen.toLocalTime() : null;

					chuyen = new Chuyen(maChuyenDB);
					chuyen.setTau(new Tau(rs.getString("tauID"), tenTau));

					if(rs.getString("tuyenID") != null) {
						chuyen.setTuyen(new Tuyen(rs.getString("tuyenID")));
					}

					chuyen.setNgayDi(ngayDi);
					chuyen.setGioDi(gioDi);
					chuyen.setNgayDen(ngayDen);
					chuyen.setGioDen(gioDen);

					chuyen.setGaDiHienThi(tenGaDi);
					chuyen.setGaDenHienThi(tenGaDen);
					chuyen.setTenChuyenHienThi(tenChuyen);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return chuyen;
	}

	public List<Chuyen> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi) {
		List<Chuyen> list = new ArrayList<>();
		Connection con = connectDB.getInstance().getConnection();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT c.*, t.tenTau, ");
		sql.append("(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu ASC) AS tenGaDi, ");
		sql.append("(SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu DESC) AS tenGaDen, ");
		sql.append("(SELECT TOP 1 cg.ngayDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS NgayDenThuc, ");
		sql.append("(SELECT TOP 1 cg.gioDen FROM ChuyenGa cg WHERE cg.chuyenID = c.chuyenID ORDER BY cg.thuTu DESC) AS GioDenThuc ");
		sql.append("FROM Chuyen c JOIN Tau t ON c.tauID = t.tauID WHERE 1=1 ");

		List<Object> params = new ArrayList<>();


		if (!maChuyen.isEmpty()) {
			sql.append(" AND c.chuyenID LIKE ?");
			params.add("%" + maChuyen + "%");
		}
		if (!tenTau.isEmpty()) {
			sql.append(" AND t.tenTau LIKE ?");
			params.add("%" + tenTau + "%");
		}
		if (ngayDi != null) {
			sql.append(" AND c.ngayDi = ?");
			params.add(java.sql.Date.valueOf(ngayDi));
		}

		if (!gaDi.isEmpty()) {
			sql.append(" AND (SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu ASC) LIKE ?");
			params.add("%" + gaDi + "%");
		}

		if (!gaDen.isEmpty()) {
			sql.append(" AND (SELECT TOP 1 g.tenGa FROM TuyenChiTiet tct JOIN Ga g ON tct.gaID = g.gaID WHERE tct.tuyenID = c.tuyenID ORDER BY tct.thuTu DESC) LIKE ?");
			params.add("%" + gaDen + "%");
		}

		sql.append(" ORDER BY c.chuyenID");

		try (PreparedStatement pst = con.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				Object param = params.get(i);
				if (param instanceof String) {
					pst.setString(i + 1, (String) param);
				} else if (param instanceof java.sql.Date) {
					pst.setDate(i + 1, (java.sql.Date) param);
				}
			}

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					String maChuyenDB = rs.getString("chuyenID");
					String tenTauDB = rs.getString("tenTau");
					String tenGaDi = rs.getString("tenGaDi");
					String tenGaDen = rs.getString("tenGaDen");

					String tenChuyen = (tenGaDi != null ? tenGaDi : "N/A") + " - " + (tenGaDen != null ? tenGaDen : "N/A");

					Date sqlDateDi = rs.getDate("ngayDi");
					Time sqlTimeDi = rs.getTime("gioDi");
					LocalDate ngayDiObj = (sqlDateDi != null) ? sqlDateDi.toLocalDate() : null;
					LocalTime gioDi = (sqlTimeDi != null) ? sqlTimeDi.toLocalTime() : null;

					Date sqlDateDen = rs.getDate("NgayDenThuc");
					Time sqlTimeDen = rs.getTime("GioDenThuc");
					LocalDate ngayDen = (sqlDateDen != null) ? sqlDateDen.toLocalDate() : null;
					LocalTime gioDen = (sqlTimeDen != null) ? sqlTimeDen.toLocalTime() : null;

					Chuyen chuyen = new Chuyen(maChuyenDB);
					chuyen.setTau(new Tau(rs.getString("tauID"), tenTauDB));

					if(rs.getString("tuyenID") != null) {
						chuyen.setTuyen(new Tuyen(rs.getString("tuyenID")));
					}

					chuyen.setNgayDi(ngayDiObj);
					chuyen.setGioDi(gioDi);
					chuyen.setNgayDen(ngayDen);
					chuyen.setGioDen(gioDen);

					chuyen.setGaDiHienThi(tenGaDi);
					chuyen.setGaDenHienThi(tenGaDen);
					chuyen.setTenChuyenHienThi(tenChuyen);

					list.add(chuyen);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getAllMaChuyenID() {
		List<String> list = new ArrayList<>();
		String sql = "SELECT chuyenID FROM Chuyen";
		try (Statement stmt = connectDB.getInstance().getConnection().createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(rs.getString("chuyenID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getAllTenGa(){
		List<String> list = new ArrayList<>();
		String sql = "SELECT tenGa FROM Ga";
		try(Statement stmt = connectDB.getInstance().getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql)){
			while (rs.next()){
				list.add(rs.getString("tenGa"));
			}
		}catch(SQLException e){ e.printStackTrace();}
		return list;
	}

	public List<String> getAllTenTau() {
		List<String> list = new ArrayList<>();
		String sql = "SELECT tenTau FROM Tau";
		try (Statement stmt = connectDB.getInstance().getConnection().createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(rs.getString("tenTau"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}


	public List<String> getAllTuyenID(){
		List<String> list = new ArrayList<>();
		String sql = "SELECT tuyenID FROM Tuyen";
		try(Statement stmt = connectDB.getInstance().getConnection().createStatement();
		    ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(rs.getString("tuyenID"));
			}
		}catch (SQLException e){ e.printStackTrace();}
		return list;
	}

	public List<String> getAllTauID(){
		List<String> list = new ArrayList<>();
		String sql = "SELECT tauID FROM Tau";
		try(Statement stmt = connectDB.getInstance().getConnection().createStatement();
		    ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(rs.getString("tauID"));
			}
		}catch (SQLException e){ e.printStackTrace();}
		return list;
	}

	public boolean themChuyenMoi(Chuyen chuyen, List<ChuyenGa> lichTrinh){
		Connection con = null;
		PreparedStatement pstChuyen = null;
		PreparedStatement pstChuyenGa = null;
		boolean success = false;

		try{
			con = ConnectDB.getInstance().getConnection();
			con.setAutoCommit(false);

			String sqlChuyen = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, ngayDi, gioDi) VALUES (?, ?, ?, ?, ?)";
			pstChuyen = con.prepareStatement(sqlChuyen);
			pstChuyen.setString(1, chuyen.getChuyenID());
			pstChuyen.setString(2, chuyen.getTuyen().getTuyenID());
			pstChuyen.setString(3, chuyen.getTau().getTauID());
			pstChuyen.setDate(4, java.sql.Date.valueOf(chuyen.getNgayDi()));
			pstChuyen.setTime(5, java.sql.Time.valueOf(chuyen.getGioDi()));

			pstChuyen.executeUpdate();

			String sqlChuyenGa = "INSERT INTO ChuyenGa (chuyenID, gaID, thuTu, ngayDen, gioDen, NgayDi, gioDi) VALUES (?, ?, ?, ?, ?, ?, ?)";
			pstChuyenGa = con.prepareStatement(sqlChuyenGa);

			for (ChuyenGa cg : lichTrinh) {
				pstChuyenGa.setString(1, chuyen.getChuyenID());
				pstChuyenGa.setString(2, cg.getGa().getGaID());
				pstChuyenGa.setInt(3, cg.getThuTu());

				pstChuyenGa.setTime(5, cg.getGioDen() != null ? java.sql.Time.valueOf(cg.getGioDen()) : null);
				pstChuyenGa.setTime(7, cg.getGioDi() != null ? java.sql.Time.valueOf(cg.getGioDi()) : null);
				pstChuyenGa.setDate(4, cg.getNgayDen() != null ? java.sql.Date.valueOf(cg.getNgayDen()) : null);
				pstChuyenGa.setDate(6, cg.getNgayDi() != null ? java.sql.Date.valueOf(cg.getNgayDi()) : null);

				pstChuyenGa.addBatch();
			}
			pstChuyenGa.executeBatch();
			con.commit();
			success = true;
		}catch (SQLException e){
			e.printStackTrace();
			try {
				if (con != null) con.rollback();
			} catch (SQLException ex) { ex.printStackTrace(); }
		} finally {
			try {
				if (pstChuyen != null) pstChuyen.close();
				if (pstChuyenGa != null) pstChuyenGa.close();
				if (con != null) con.setAutoCommit(true);
			} catch (SQLException ex) { ex.printStackTrace(); }
		}
		return success;
	}

	public Map<String, String> getMapTenGaToID(){
		Map<String, String> map = new HashMap<>();
		String sql = "SELECT gaID, tenGa FROM Ga";
		try (Connection con = ConnectDB.getInstance().getConnection();
			 Statement stmt = con.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				map.put(rs.getString("tenGa"), rs.getString("gaID"));
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return map;
	}


}