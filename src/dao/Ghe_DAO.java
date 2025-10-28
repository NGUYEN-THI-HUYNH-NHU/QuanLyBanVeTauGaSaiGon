package dao;
/*
 * @(#) Ghe_DAO.java  1.0  [1:00:58 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import connectDB.ConnectDB;
import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;

public class Ghe_DAO {
	private ConnectDB connectDB = ConnectDB.getInstance();

	public Ghe_DAO() {
		connectDB.connect();
	}

	public List<Ghe> getGheByGaDiGaDenChuyenToa(String gaDiID, String gaDenID, String chuyenID, String toaID) {
		Connection conn = connectDB.getConnection();
		String sql = "DECLARE @chuyenID VARCHAR(50) = ?;\r\n" + "DECLARE @gaDiID   VARCHAR(50) = ?;\r\n"
				+ "DECLARE @gaDenID  VARCHAR(50) = ?;\r\n" + "DECLARE @toaID    VARCHAR(50) = ?;\r\n"
				// Thêm biến thời gian giữ chỗ (ví dụ: 10 phút)
				+ "DECLARE @holdMinutes INT = 10;\r\n" + "\r\n"
				+ "-- Lấy thứ tự ga đi / ga đến (của phân đoạn *yêu cầu*)\r\n"
				+ "DECLARE @thuTuGaDi_YeuCau  INT, @thuTuGaDen_YeuCau INT;\r\n"
				+ "SELECT @thuTuGaDi_YeuCau = cg.thuTu\r\n" + "FROM ChuyenGa cg\r\n"
				+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDiID;\r\n" + "\r\n"
				+ "SELECT @thuTuGaDen_YeuCau = cg.thuTu\r\n" + "FROM ChuyenGa cg\r\n"
				+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDenID;\r\n" + "\r\n"
				+ "IF @thuTuGaDi_YeuCau IS NULL OR @thuTuGaDen_YeuCau IS NULL\r\n" + "BEGIN\r\n"
				+ "    RAISERROR('Không tìm được gaDi hoặc gaDen cho chuyenID = %s', 16, 1, @chuyenID);\r\n"
				+ "    RETURN;\r\n" + "END\r\n" + "\r\n" + "-- Main query: danh sách ghế + trạng thái\r\n"
				+ ";WITH GheList AS (\r\n" + "    -- 1. Lấy danh sách ghế của toa này\r\n"
				+ "    SELECT g.gheID, g.toaID, g.soGhe\r\n" + "    FROM Ghe g\r\n" + "    WHERE g.toaID = @toaID\r\n"
				+ "),\r\n" + "-- Vé (sold) chồng lấp phân đoạn yêu cầu\r\n" + "VeChongLap AS (\r\n"
				+ "    -- 2. Tìm các vé ĐÃ BÁN/ĐÃ DÙNG chồng lấp với phân đoạn yêu cầu\r\n" + "    SELECT \r\n"
				+ "        v.gheID, \r\n" + "        COUNT(1) AS cntVe\r\n" + "    FROM Ve v\r\n"
				+ "    -- SỬA LỖI: JOIN với ChuyenGa để lấy thuTu của vé đã bán\r\n"
				+ "    JOIN ChuyenGa cgDi ON v.chuyenID = cgDi.chuyenID AND v.gaDiID = cgDi.gaID\r\n"
				+ "    JOIN ChuyenGa cgDen ON v.chuyenID = cgDen.chuyenID AND v.gaDenID = cgDen.gaID\r\n"
				+ "    WHERE v.chuyenID = @chuyenID\r\n" + "      AND v.trangThai IN ('DA_BAN','DA_DUNG')\r\n"
				+ "      -- điều kiện chồng lấp: [start_đã_bán] < [end_yêu_cầu] AND [end_đã_bán] > [start_yêu_cầu]\r\n"
				+ "      AND cgDi.thuTu < @thuTuGaDen_YeuCau\r\n" + "      AND cgDen.thuTu > @thuTuGaDi_YeuCau\r\n"
				+ "    GROUP BY v.gheID\r\n" + "),\r\n"
				+ "-- Phiếu giữ chỗ chồng lấp, phân loại theo trạng thái giữ\r\n" + "PGC_ChongLap AS (\r\n"
				+ "    -- 3. Tìm các phiếu ĐANG GIỮ/ĐÃ XÁC NHẬN chồng lấp với phân đoạn yêu cầu\r\n" + "    SELECT \r\n"
				+ "        pgcct.gheID,\r\n"
				+ "        SUM(CASE WHEN pgcct.trangThai = 'XAC_NHAN' THEN 1 ELSE 0 END) AS cntXacNhan,\r\n"
				+ "        SUM(CASE WHEN pgcct.trangThai = 'DANG_GIU' \r\n"
				+ "                     AND pg.trangThai = 'DANG_GIU' \r\n"
				// Kiểm tra thời gian tạo của phiếu cha (PhieuGiuCho)
				+ "                     AND pg.thoiDiemTao > DATEADD(minute, -@holdMinutes, SYSUTCDATETIME()) \r\n"
				+ "                THEN 1 ELSE 0 END) AS cntDangGiuConHieuLuc\r\n"
				+ "    FROM PhieuGiuChoChiTiet pgcct\r\n"
				+ "    -- SỬA LỖI: JOIN với PhieuGiuCho để lấy trạng thái và thời gian (logic đúng từ schema v6.1)\r\n"
				+ "    JOIN PhieuGiuCho pg ON pg.phieuGiuChoID = pgcct.phieuGiuChoID\r\n"
				+ "    -- SỬA LỖI: JOIN với ChuyenGa để lấy thuTu của phiếu đang giữ\r\n"
				+ "    JOIN ChuyenGa cgDi ON pgcct.chuyenID = cgDi.chuyenID AND pgcct.gaDiID = cgDi.gaID\r\n"
				+ "    JOIN ChuyenGa cgDen ON pgcct.chuyenID = cgDen.chuyenID AND pgcct.gaDenID = cgDen.gaID\r\n"
				+ "    WHERE pgcct.chuyenID = @chuyenID\r\n"
				+ "      -- Chỉ quan tâm các phiếu có trạng thái chiếm chỗ\r\n"
				+ "      AND pgcct.trangThai IN ('DANG_GIU', 'XAC_NHAN')\r\n"
				+ "      -- điều kiện chồng lấp: [start_đang_giữ] < [end_yêu_cầu] AND [end_đang_giữ] > [start_yêu_cầu]\r\n"
				+ "      AND cgDi.thuTu < @thuTuGaDen_YeuCau\r\n" + "      AND cgDen.thuTu > @thuTuGaDi_YeuCau\r\n"
				+ "    GROUP BY pgcct.gheID\r\n" + ")\r\n" + "-- 4. Tổng hợp kết quả\r\n" + "SELECT\r\n"
				+ "    g.gheID,\r\n" + "    g.soGhe,\r\n" + "    g.toaID,\r\n"
				+ "    -- tính trạng thái cuối cùng (ưu tiên)\r\n" + "    CASE\r\n"
				+ "        WHEN ISNULL(vc.cntVe, 0) > 0 THEN 'DA_BAN'\r\n"
				+ "        WHEN ISNULL(pc.cntXacNhan, 0) > 0 THEN 'DA_BAN'\r\n"
				+ "        WHEN ISNULL(pc.cntDangGiuConHieuLuc, 0) > 0 THEN 'BI_CHIEM'\r\n" + "        ELSE 'TRONG'\r\n"
				+ "    END AS trangThai\r\n" + "FROM GheList g\r\n"
				+ "LEFT JOIN VeChongLap vc ON vc.gheID = g.gheID\r\n"
				+ "LEFT JOIN PGC_ChongLap pc ON pc.gheID = g.gheID\r\n" + "ORDER BY g.soGhe;\r\n";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, chuyenID);
			ps.setString(2, gaDiID);
			ps.setString(3, gaDenID);
			ps.setString(4, toaID);
			ResultSet rs = ps.executeQuery();
			List<Ghe> gheList = new ArrayList<Ghe>();
			while (rs.next()) {
				Ghe g = new Ghe();
				g.setGheID(rs.getString("gheID"));
				g.setToa(new Toa(rs.getString("toaID")));
				g.setSoGhe(rs.getInt("soGhe"));
				g.setTrangThai(TrangThaiGhe.valueOf(rs.getString("trangThai")));
				gheList.add(g);
			}
			return gheList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Ghe getGheByChuyenIDGheID(String chuyenID, String gheID) {
		Connection conn = connectDB.getConnection();
		String sql = "select g.gheID, g.toaID, g.soGhe, g.trangThai" + " from Ghe g join Toa toa on g.toaID = toa.toaID"
				+ "	join Tau t on toa.tauID = t.tauID" + " join Chuyen c on t.tauID = c.tauID" + " where c.chuyenID = ?"
				+ "	and g.gheID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, chuyenID);
			ps.setString(2, gheID);
			ResultSet rs = ps.executeQuery();
			Ghe g = new Ghe();
			g.setGheID(rs.getString("gheID"));
			g.setToa(new Toa(rs.getString("toaID")));
			g.setSoGhe(rs.getInt("soGhe"));
			g.setTrangThai(TrangThaiGhe.valueOf(rs.getString("trangThai")));
			return g;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean updateTrangThaiGhe(String gheID, Boolean trangThai) {
		Connection conn = connectDB.getConnection();
		String sql = "UPDATE Ghe SET trangThai = ? WHERE gheID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setBoolean(1, trangThai);
			ps.setString(2, gheID);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Tính giá vé cho một phân đoạn, dựa trên Loại tàu và Hạng toa cụ thể. Query
	 * này hiệu quả hơn vì không cần JOIN Ghe, Toa, Tau.
	 *
	 * @param chuyenID  ID của chuyến (để tính khoảng cách)
	 * @param gaDiID    ID của ga đi
	 * @param gaDenID   ID của ga đến
	 * @param loaiTauID ID của Loại tàu (từ BieuGiaVe)
	 * @param hangToaID ID của Hạng toa (từ BieuGiaVe)
	 * @return Giá vé (BigDecimal), hoặc BigDecimal.ZERO nếu không tìm thấy quy tắc
	 *         giá.
	 */
	public int calcGia(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
		Connection conn = connectDB.getConnection();

		String sql = "-- 0. Khai báo tham số đầu vào\n" + "DECLARE @chuyenID VARCHAR(50) = ?;\n"
				+ "DECLARE @gaDiID VARCHAR(50) = ?;\n" + "DECLARE @gaDenID VARCHAR(50) = ?;\n"
				+ "DECLARE @loaiTauID VARCHAR(50) = ?;\n" + "DECLARE @hangToaID VARCHAR(50) = ?;\n" + "\n"
				+ "-- 1. CTE: Lấy TuyenID và tính Khoảng cách (Km)\n" + ";WITH Distance AS (\n" + "    SELECT \n"
				// Tính khoảng cách tuyệt đối
				+ "        ABS(ttDen.khoangCachTuGaXuatPhatKm - ttDi.khoangCachTuGaXuatPhatKm) AS km\n"
				+ "    FROM Chuyen c\n"
				+ "    JOIN TuyenChiTiet ttDi ON c.tuyenID = ttDi.tuyenID AND ttDi.gaID = @gaDiID\n"
				+ "    JOIN TuyenChiTiet ttDen ON c.tuyenID = ttDen.tuyenID AND ttDen.gaID = @gaDenID\n"
				+ "    WHERE c.chuyenID = @chuyenID\n" + "),\n"
				+ "-- 2. CTE: Tìm quy tắc giá tốt nhất (ưu tiên cao nhất) phù hợp\n" + "BestRule AS (\n"
				+ "    SELECT TOP 1\n" + "        bg.donGiaTrenKm,\n" + "        bg.giaCoBan\n"
				+ "    FROM BieuGiaVe bg\n" + "    JOIN Distance d ON d.km BETWEEN bg.minKm AND bg.maxKm\n"
				+ "    WHERE \n" + "        bg.isCoHieuLuc = 1\n"
				+ "        AND bg.loaiTauApDungID = @loaiTauID -- Dùng tham số trực tiếp\n"
				+ "        AND bg.hangToaApDungID = @hangToaID -- Dùng tham số trực tiếp\n"
				+ "    ORDER BY bg.doUuTien DESC -- Lấy quy tắc có độ ưu tiên cao nhất\n" + "),\n"
				+ "-- 3. CTE: Tính giá cơ bản (trước khi nhân hệ số)\n" + "BasePrice AS (\n" + "    SELECT\n"
				+ "        CASE\n" + "            WHEN r.donGiaTrenKm IS NOT NULL THEN r.donGiaTrenKm * d.km\n"
				+ "            ELSE r.giaCoBan\n" + "        END AS giaTrcHeSo\n" + "    FROM BestRule r, Distance d\n"
				+ "),\n" + "-- 4. CTE: Lấy các hệ số nhân\n" + "Multipliers AS (\n" + "    SELECT \n"
				// Dùng ISNULL để mặc định là 1.0 nếu không tìm thấy hệ số
				+ "        ISNULL((SELECT hsg FROM HeSoGiaLoaiTau hst WHERE hst.loaiTauID = @loaiTauID AND hst.isCoHieuLuc = 1), 1.0) AS hsgTau,\n"
				+ "        ISNULL((SELECT hsg FROM HeSoGiaHangToa hsh WHERE hsh.hangToaID = @hangToaID AND hsh.isCoHieuLuc = 1), 1.0) AS hsgToa\n"
				+ ")\n" + "-- 5. Tính toán cuối cùng\n" + "SELECT \n"
				+ "    ISNULL(ROUND(bp.giaTrcHeSo * m.hsgTau * m.hsgToa, 2), 0.00) AS finalPrice\n"
				+ "FROM BasePrice bp, Multipliers m;";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, chuyenID);
			ps.setString(2, gaDiID);
			ps.setString(3, gaDenID);
			ps.setString(4, loaiTauID);
			ps.setString(5, hangToaID);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return (int) (Math.round(rs.getDouble("finalPrice") / 500.0) * 500);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// Nếu có lỗi, trả về 0
		}
		return 0;
	}
}