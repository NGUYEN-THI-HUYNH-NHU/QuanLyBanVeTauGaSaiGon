package dao;
/*
 * @(#) Ghe_DAO.java  1.0  [1:00:58 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Ghe_DAO {
	private ConnectDB connectDB = ConnectDB.getInstance();

	public Ghe_DAO() {
		connectDB.connect();
	}

	public List<Ghe> getGheByGaDiGaDenChuyenToa(String gaDiID, String gaDenID, String chuyenID, String toaID) {
		Connection conn = connectDB.getConnection();
		String sql = "DECLARE @chuyenID VARCHAR(50) = ?;\r\n"
				+ "DECLARE @gaDiID   VARCHAR(50) = ?;\r\n"
				+ "DECLARE @gaDenID  VARCHAR(50) = ?;\r\n"
				+ "DECLARE @toaID    VARCHAR(50) = ?;\r\n"
				+ "-- Lấy thứ tự ga đi / ga đến trên chuyến\r\n"
				+ "DECLARE @thuTuGaDi  INT, @thuTuGaDen INT;\r\n"
				+ "SELECT @thuTuGaDi = cg.thuTu\r\n"
				+ "FROM ChuyenGa cg\r\n"
				+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDiID;\r\n"
				+ "SELECT @thuTuGaDen = cg.thuTu\r\n"
				+ "FROM ChuyenGa cg\r\n"
				+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDenID;\r\n"
				+ "IF @thuTuGaDi IS NULL OR @thuTuGaDen IS NULL\r\n"
				+ "BEGIN\r\n"
				+ "    RAISERROR('Không tìm được gaDi hoặc gaDen cho chuyenID = %s', 16, 1, @chuyenID);\r\n"
				+ "    RETURN;\r\n"
				+ "END\r\n"
				+ "-- Main query: danh sách ghế + trạng thái\r\n"
				+ ";WITH GheList AS (\r\n"
				+ "    SELECT g.gheID, g.toaID, g.soGhe\r\n"
				+ "    FROM Ghe g\r\n"
				+ "    WHERE g.toaID = @toaID\r\n"
				+ "),\r\n"
				+ "-- Vé (sold) chồng lấp phân đoạn yêu cầu\r\n"
				+ "VeChongLap AS (\r\n"
				+ "    SELECT v.gheID, COUNT(1) AS cntVe, STRING_AGG(v.veID, ',') WITHIN GROUP (ORDER BY v.veID) AS veIDs\r\n"
				+ "    FROM Ve v\r\n"
				+ "    WHERE v.chuyenID = @chuyenID\r\n"
				+ "      AND v.trangThai IN ('DA_BAN','DA_DUNG')  -- tùy chỉnh trạng thái vé được xem là 'đã chiếm chỗ'\r\n"
				+ "      -- điều kiện chồng lấp phân đoạn (open-interval overlap)\r\n"
				+ "      AND v.thuTuGaDi < @thuTuGaDen\r\n"
				+ "      AND v.thuTuGaDen > @thuTuGaDi\r\n"
				+ "    GROUP BY v.gheID\r\n"
				+ "),\r\n"
				+ "-- Phiếu giữ chỗ chồng lấp, phân loại theo trạng thái giữ\r\n"
				+ "PGC_ChongLap AS (\r\n"
				+ "    SELECT pgc.gheID,\r\n"
				+ "           SUM(CASE WHEN pgc.trangThai = 'XAC_NHAN' THEN 1 ELSE 0 END) AS cntXacNhan,\r\n"
				+ "           SUM(CASE WHEN pgc.trangThai = 'DANG_GIU' AND pgc.thoiDiemHetGiuCho > SYSUTCDATETIME() THEN 1 ELSE 0 END) AS cntDangGiuConHieuLuc,\r\n"
				+ "           STRING_AGG(pgc.phieuGiuChoID, ',') WITHIN GROUP (ORDER BY pgc.phieuGiuChoID) AS pgcIDs\r\n"
				+ "    FROM PhieuGiuChoChiTiet pgc\r\n"
				+ "    JOIN PhieuGiuCho pg ON pg.phieuGiuChoID = pgc.phieuGiuChoID\r\n"
				+ "    WHERE pgc.chuyenID = @chuyenID\r\n"
				+ "      AND (\r\n"
				+ "           -- chỉ quan tâm các phiếu có trạng thái có thể chiếm chỗ\r\n"
				+ "           (pgc.trangThai = 'XAC_NHAN')\r\n"
				+ "           OR (pgc.trangThai = 'DANG_GIU' AND pgc.thoiDiemHetGiuCho > SYSUTCDATETIME())\r\n"
				+ "          )\r\n"
				+ "      AND pgc.thuTuGaDi < @thuTuGaDen\r\n"
				+ "      AND pgc.thuTuGaDen > @thuTuGaDi\r\n"
				+ "    GROUP BY pgc.gheID\r\n"
				+ ")\r\n"
				+ "SELECT\r\n"
				+ "    g.gheID,\r\n"
				+ "    g.soGhe,\r\n"
				+ "    g.toaID,\r\n"
				+ "    -- chi tiết về vé/giữ chỗ (counts & ids)\r\n"
				+ "    ISNULL(vc.cntVe, 0) AS soVeChongLap,\r\n"
				+ "    ISNULL(pc.cntXacNhan, 0) AS soPGC_XAC_NHAN,\r\n"
				+ "    ISNULL(pc.cntDangGiuConHieuLuc, 0) AS soPGC_DANG_GIU_HIEU_LUC,\r\n"
				+ "    vc.veIDs AS veIDs,\r\n"
				+ "    pc.pgcIDs AS phieuGiuChoIDs,\r\n"
				+ "    -- tính trạng thái cuối cùng (ưu tiên)\r\n"
				+ "    CASE\r\n"
				+ "        WHEN ISNULL(vc.cntVe, 0) > 0 THEN 'DA_BAN'                 -- có vé đã bán/đang dùng chồng lấp\r\n"
				+ "        WHEN ISNULL(pc.cntXacNhan, 0) > 0 THEN 'DA_BAN' -- có phiếu xác nhận\r\n"
				+ "        WHEN ISNULL(pc.cntDangGiuConHieuLuc, 0) > 0 THEN 'BI_CHIEM'  -- có phiếu đang giữ còn hiệu lực\r\n"
				+ "        ELSE 'TRONG'\r\n"
				+ "    END AS trangThai\r\n"
				+ "FROM GheList g\r\n"
				+ "LEFT JOIN VeChongLap vc ON vc.gheID = g.gheID\r\n"
				+ "LEFT JOIN PGC_ChongLap pc ON pc.gheID = g.gheID\r\n"
				+ "ORDER BY\r\n"
				+ "    CASE\r\n"
				+ "        WHEN ISNULL(vc.cntVe,0) > 0 THEN 1\r\n"
				+ "        WHEN ISNULL(pc.cntXacNhan,0) > 0 THEN 2\r\n"
				+ "        WHEN ISNULL(pc.cntDangGiuConHieuLuc,0) > 0 THEN 3\r\n"
				+ "        ELSE 4\r\n"
				+ "    END,\r\n"
				+ "    g.soGhe;\r\n";
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
		String sql = "select g.gheID, g.toaID, g.soGhe, g.trangThai"
				+ " from Ghe g join Toa toa on g.toaID = toa.toaID"
				+ "	join Tau t on toa.tauID = t.tauID"
				+ " join Chuyen c on t.tauID = c.tauID"
				+ " where c.chuyenID = ?"
				+ "	and g.gheID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,chuyenID);
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
}