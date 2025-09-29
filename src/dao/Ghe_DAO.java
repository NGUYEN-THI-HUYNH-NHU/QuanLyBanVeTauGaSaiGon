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

    public Ghe_DAO() { connectDB.connect(); }

    public List<Ghe> getGheByGaDiGaDenChuyenIDToaID(String gaDiID, String gaDenID, String chuyenID, String toaID) {
        Connection conn = connectDB.getConnection();
        String sql = "DECLARE @chuyenID VARCHAR(50)      = ?;"
        		+ " DECLARE @toaID VARCHAR(50)           = ?;"
        		+ " DECLARE @gaDiID VARCHAR(50)          = ?;"
        		+ " DECLARE @gaDenID VARCHAR(50)         = ?;"
        		+ " DECLARE @excludeDonDatChoID VARCHAR(50) = NULL; -- optional: bỏ qua 1 đặt chỗ cụ thể (nếu có)"
        		+ " "
        		+ " -- Nội bộ: lấy thuTu từ ChuyenGa"
        		+ " DECLARE @thuTuGaDi INT, @thuTuGaDen INT;"
        		+ " "
        		+ " SELECT @thuTuGaDi = cg.thuTu"
        		+ " FROM ChuyenGa cg"
        		+ " WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDiID;"
        		+ " "
        		+ " SELECT @thuTuGaDen = cg.thuTu"
        		+ " FROM ChuyenGa cg"
        		+ " WHERE cg.chuyenID = @chuyenID AND cg.gaID = @gaDenID;"
        		+ " "
        		+ " -- Kiểm tra tồn tại"
        		+ " IF @thuTuGaDi IS NULL"
        		+ " BEGIN"
        		+ "     RAISERROR('Ga đi (gaID=%s) không có trên chuyến %s', 16, 1, @gaDiID, @chuyenID);"
        		+ "     RETURN;"
        		+ " END"
        		+ " "
        		+ " IF @thuTuGaDen IS NULL"
        		+ " BEGIN"
        		+ "     RAISERROR('Ga đến (gaID=%s) không có trên chuyến %s', 16, 1, @gaDenID, @chuyenID);"
        		+ "     RETURN;"
        		+ " END"
        		+ " "
        		+ " -- Nếu thứ tự ngược, hoán đổi để đảm bảo start < end (nếu muốn báo lỗi thay vì swap, đổi logic ở đây)"
        		+ " IF @thuTuGaDi >= @thuTuGaDen"
        		+ " BEGIN"
        		+ "     DECLARE @tmp INT = @thuTuGaDi;"
        		+ "     SET @thuTuGaDi = @thuTuGaDen;"
        		+ "     SET @thuTuGaDen = @tmp;"
        		+ " END"
        		+ " "
        		+ " ;WITH SeatsInTrain AS ("
        		+ "     -- lấy tất cả ghế thuộc toa của tàu chạy chuyến"
        		+ "     SELECT"
        		+ "         g.gheID,"
        		+ "         g.soGhe,"
        		+ "         t.toaID,"
        		+ "         t.soToa,"
        		+ "         t.hangToaID"
        		+ "     FROM Chuyen c"
        		+ "     INNER JOIN Toa t ON t.tauID = c.tauID"
        		+ "     INNER JOIN Ghe g  ON g.toaID = t.toaID"
        		+ "     WHERE c.chuyenID = @chuyenID"
        		+ "       AND t.toaID = @toaID"
        		+ " )"
        		+ " SELECT"
        		+ "     s.gheID,"
        		+ "     s.soGhe,"
        		+ "     s.toaID,"
        		+ "     CASE"
        		+ "         WHEN vcnt.conflictVeCount > 0 THEN 'OCCUPIED'"
        		+ "         WHEN ddcnt.conflictDonDatChoCount > 0 THEN 'RESERVED'"
        		+ "         ELSE 'AVAILABLE'"
        		+ "     END AS trangThai"
        		+ " FROM SeatsInTrain s"
        		+ " LEFT JOIN HangToa ht ON ht.hangToaID = s.hangToaID"
        		+ " "
        		+ " OUTER APPLY ("
        		+ "     -- số vé (Ve) đang chiếm chỗ và chồng lấp với đoạn yêu cầu"
        		+ "     SELECT COUNT(*) AS conflictVeCount"
        		+ "     FROM Ve v"
        		+ "     WHERE v.chuyenID = @chuyenID"
        		+ "       AND v.gheID = s.gheID"
        		+ "       AND v.trangThai IN ('RESERVED','BOOKED','USED')    -- trạng thái chiếm chỗ"
        		+ "       AND NOT (v.thuTuGaDen <= @thuTuGaDi OR v.thuTuGaDi >= @thuTuGaDen)"
        		+ " ) vcnt"
        		+ " "
        		+ " OUTER APPLY ("
        		+ "     -- danh sách vé gây conflict (STRING_AGG, SQL Server 2017+)"
        		+ "     SELECT STRING_AGG(v.veID, ',') WITHIN GROUP (ORDER BY v.ngayBan) AS conflictingVeIDs"
        		+ "     FROM Ve v"
        		+ "     WHERE v.chuyenID = @chuyenID"
        		+ "       AND v.gheID = s.gheID"
        		+ "       AND v.trangThai IN ('RESERVED','BOOKED','USED')"
        		+ "       AND NOT (v.thuTuGaDen <= @thuTuGaDi OR v.thuTuGaDi >= @thuTuGaDen)"
        		+ " ) vlist"
        		+ " "
        		+ " OUTER APPLY ("
        		+ "     -- số đặt chỗ (DonDatCho) pending chồng lấp (loại trừ chính đơn đặt chỗ đang thao tác nếu truyền @excludeDonDatChoID)"
        		+ "     SELECT COUNT(*) AS conflictDonDatChoCount"
        		+ "     FROM DonDatCho dd"
        		+ "     JOIN DonDatChoChiTiet ddct ON ddct.donDatChoID = dd.donDatChoID"
        		+ "     WHERE dd.chuyenID = @chuyenID"
        		+ "       AND ddct.gheID = s.gheID"
        		+ "       AND dd.trangThaiDatChoID = 'PENDING'"
        		+ "       AND (@excludeDonDatChoID IS NULL OR dd.donDatChoID <> @excludeDonDatChoID)"
        		+ "       AND NOT (ddct.thuTuGaDen <= @thuTuGaDi OR ddct.thuTuGaDi >= @thuTuGaDen)"
        		+ " ) ddcnt"
        		+ " "
        		+ " OUTER APPLY ("
        		+ "     SELECT STRING_AGG(dd.donDatChoID, ',') WITHIN GROUP (ORDER BY dd.thoiDiemDatCho) AS conflictingDonDatChoIDs"
        		+ "     FROM DonDatCho dd"
        		+ "     JOIN DonDatChoChiTiet ddct ON ddct.donDatChoID = dd.donDatChoID"
        		+ "     WHERE dd.chuyenID = @chuyenID"
        		+ "       AND ddct.gheID = s.gheID"
        		+ "       AND dd.trangThaiDatChoID = 'PENDING'"
        		+ "       AND (@excludeDonDatChoID IS NULL OR dd.donDatChoID <> @excludeDonDatChoID)"
        		+ "       AND NOT (ddct.thuTuGaDen <= @thuTuGaDi OR ddct.thuTuGaDi >= @thuTuGaDen)"
        		+ " ) ddlist"
        		+ " "
        		+ " ORDER BY TRY_CAST(s.soToa AS INT), TRY_CAST(s.soGhe AS INT), s.soGhe;";
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