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
        String sql = "DECLARE @chuyenID VARCHAR(50)     = ?;"
        		+ "DECLARE @toaID VARCHAR(50)           = ?;"
        		+ "DECLARE @gaDiID VARCHAR(50)          = ?;"
        		+ "DECLARE @gaDenID VARCHAR(50)         = ?;"
        		+ "DECLARE @excludeDonDatChogID VARCHAR(50) = NULL; -- optional: bỏ qua 1 đặt chỗ cụ thể (nếu có)\r\n"
        		+ "\r\n"
        		+ "-- Nội bộ: lấy thuTu từ ChuyenGa\r\n"
        		+ "DECLARE @thuTuGaDi INT, @thuTuGaDen INT;\r\n"
        		+ "\r\n"
        		+ "SELECT @thuTuGaDi = cg.thuTu\r\n"
        		+ "FROM ChuyenGa cg\r\n"
        		+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDiID;\r\n"
        		+ "\r\n"
        		+ "SELECT @thuTuGaDen = cg.thuTu\r\n"
        		+ "FROM ChuyenGa cg\r\n"
        		+ "WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDenID;\r\n"
        		+ "\r\n"
        		+ "-- Kiểm tra tồn tại\r\n"
        		+ "IF @thuTuGaDi IS NULL\r\n"
        		+ "BEGIN\r\n"
        		+ "    RAISERROR('Ga đi (gaID=%s) không có trên chuyến %s', 16, 1, @gaDiID, @chuyenID);\r\n"
        		+ "    RETURN;\r\n"
        		+ "END\r\n"
        		+ "\r\n"
        		+ "IF @thuTuGaDen IS NULL\r\n"
        		+ "BEGIN\r\n"
        		+ "    RAISERROR('Ga đến (gaID=%s) không có trên chuyến %s', 16, 1, @gaDenID, @chuyenID);\r\n"
        		+ "    RETURN;\r\n"
        		+ "END\r\n"
        		+ "\r\n"
        		+ "-- Nếu thứ tự ngược, hoán đổi để đảm bảo start < end (nếu muốn báo lỗi thay vì swap, đổi logic ở đây)\r\n"
        		+ "IF @thuTuGaDi >= @thuTuGaDen\r\n"
        		+ "BEGIN\r\n"
        		+ "    DECLARE @tmp INT = @thuTuGaDi;\r\n"
        		+ "    SET @thuTuGaDi = @thuTuGaDen;\r\n"
        		+ "    SET @thuTuGaDen = @tmp;\r\n"
        		+ "END\r\n"
        		+ "\r\n"
        		+ ";WITH SeatsInTrain AS (\r\n"
        		+ "    -- lấy tất cả ghế thuộc toa của tàu chạy chuyến\r\n"
        		+ "    SELECT\r\n"
        		+ "        g.gheID,\r\n"
        		+ "        g.soGhe,\r\n"
        		+ "        t.toaID,\r\n"
        		+ "        t.soToa,\r\n"
        		+ "        t.hangToaID\r\n"
        		+ "    FROM Chuyen c\r\n"
        		+ "    INNER JOIN Toa t ON t.tauID = c.tauID\r\n"
        		+ "    INNER JOIN Ghe g  ON g.toaID = t.toaID\r\n"
        		+ "    WHERE c.chuyenID = @chuyenID\r\n"
        		+ "      AND t.toaID = @toaID\r\n"
        		+ ")\r\n"
        		+ "SELECT\r\n"
        		+ "    s.gheID,\r\n"
        		+ "    s.soGhe,\r\n"
        		+ "    s.toaID,\r\n"
        		+ "    s.soToa,\r\n"
        		+ "    CASE\r\n"
        		+ "        WHEN vcnt.conflictVeCount > 0 THEN 'OCCUPIED'\r\n"
        		+ "        WHEN ddcnt.conflictDonDatChoCount > 0 THEN 'RESERVED'\r\n"
        		+ "        ELSE 'AVAILABLE'\r\n"
        		+ "    END AS trangThai,\r\n"
        		+ "    vcnt.conflictVeCount,\r\n"
        		+ "    ddcnt.conflictDonDatChoCount,\r\n"
        		+ "    vlist.conflictingVeIDs,\r\n"
        		+ "    ddlist.conflictingDonDatChoIDs\r\n"
        		+ "FROM SeatsInTrain s\r\n"
        		+ "LEFT JOIN HangToa ht ON ht.hangToaID = s.hangToaID\r\n"
        		+ "\r\n"
        		+ "OUTER APPLY (\r\n"
        		+ "    -- số vé (Ve) đang chiếm chỗ và chồng lấp với đoạn yêu cầu\r\n"
        		+ "    SELECT COUNT(*) AS conflictVeCount\r\n"
        		+ "    FROM Ve v\r\n"
        		+ "    WHERE v.chuyenID = @chuyenID\r\n"
        		+ "      AND v.gheID = s.gheID\r\n"
        		+ "      AND v.trangThai IN ('RESERVED','BOOKED','USED')    -- trạng thái chiếm chỗ\r\n"
        		+ "      AND NOT (v.thuTuGaDen <= @thuTuGaDi OR v.thuTuGaDi >= @thuTuGaDen)\r\n"
        		+ ") vcnt\r\n"
        		+ "\r\n"
        		+ "OUTER APPLY (\r\n"
        		+ "    -- danh sách vé gây conflict (STRING_AGG, SQL Server 2017+)\r\n"
        		+ "    SELECT STRING_AGG(v.veID, ',') WITHIN GROUP (ORDER BY v.ngayBan) AS conflictingVeIDs\r\n"
        		+ "    FROM Ve v\r\n"
        		+ "    WHERE v.chuyenID = @chuyenID\r\n"
        		+ "      AND v.gheID = s.gheID\r\n"
        		+ "      AND v.trangThai IN ('RESERVED','BOOKED','USED')\r\n"
        		+ "      AND NOT (v.thuTuGaDen <= @thuTuGaDi OR v.thuTuGaDi >= @thuTuGaDen)\r\n"
        		+ ") vlist\r\n"
        		+ "\r\n"
        		+ "OUTER APPLY (\r\n"
        		+ "    -- số đặt chỗ (DonDatCho) pending chồng lấp (loại trừ chính đơn đặt chỗ đang thao tác nếu truyền @excludeDonDatChoID)\r\n"
        		+ "    SELECT COUNT(*) AS conflictDonDatChoCount\r\n"
        		+ "    FROM DonDatCho dd\r\n"
        		+ "    JOIN DonDatChoChiTiet ddct ON ddct.donDatChoID = dd.donDatChoID\r\n"
        		+ "    WHERE dd.chuyenID = @chuyenID\r\n"
        		+ "      AND ddct.gheID = s.gheID\r\n"
        		+ "      AND dd.trangThaiDatChoID = 'PENDING'\r\n"
        		+ "      AND (@excludeDonDatChoID IS NULL OR dd.donDatChoID <> @excludeDonDatChoID)\r\n"
        		+ "      AND NOT (ddct.thuTuGaDen <= @thuTuGaDi OR ddct.thuTuGaDi >= @thuTuGaDen)\r\n"
        		+ ") ddcnt\r\n"
        		+ "\r\n"
        		+ "OUTER APPLY (\r\n"
        		+ "    SELECT STRING_AGG(dd.donDatChoID, ',') WITHIN GROUP (ORDER BY dd.thoiDiemDatCho) AS conflictingDonDatChoIDs\r\n"
        		+ "    FROM DonDatCho dd\r\n"
        		+ "    JOIN DonDatChoChiTiet ddct ON ddct.donDatChoID = dd.donDatChoID\r\n"
        		+ "    WHERE dd.chuyenID = @chuyenID\r\n"
        		+ "      AND ddct.gheID = s.gheID\r\n"
        		+ "      AND dd.trangThaiDatChoID = 'PENDING'\r\n"
        		+ "      AND (@excludeDonDatChoID IS NULL OR dd.donDatChoID <> @excludeDonDatChoID)\r\n"
        		+ "      AND NOT (ddct.thuTuGaDen <= @thuTuGaDi OR ddct.thuTuGaDi >= @thuTuGaDen)\r\n"
        		+ ") ddlist\r\n"
        		+ "\r\n"
        		+ "ORDER BY TRY_CAST(s.soToa AS INT), TRY_CAST(s.soGhe AS INT), s.soGhe;";
        try {
        	PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, chuyenID);
            ps.setString(2, toaID);
            ps.setString(3, gaDiID);
            ps.setString(4, gaDenID);
            ResultSet rs = ps.executeQuery();
            List<Ghe> gheList = new ArrayList<Ghe>();
            while (rs.next()) {
                Ghe g = new Ghe();
                g.setGheID(rs.getString("gheID"));
                g.setToa(new Toa(rs.getString("toaID")));
                g.setSoGhe(rs.getString("soGhe"));
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
            g.setSoGhe(rs.getString("soGhe"));
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