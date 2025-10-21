package dao;
/*
 * @(#) Toa_DAO.java  1.0  [12:58:30 PM] Sep 29, 2025
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
import entity.Tau;
import entity.Toa;
import entity.type.HangToa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Toa_DAO {
    private ConnectDB connectDB = ConnectDB.getInstance();

    public Toa_DAO() {
    	connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public List<Toa> getToaByChuyenID(String chuyenID) {
        List<Toa> list = new ArrayList<>();
        Connection conn = connectDB.getConnection();
        String sql = "DECLARE @chuyenID VARCHAR(50) = ?;\r\n"
        		+ "\r\n"
        		+ "SELECT \r\n"
        		+ "    t.toaID,\r\n"
        		+ "    t.soToa,\r\n"
        		+ "    t.hangToaID,\r\n"
        		+ "	   t.sucChua,\r\n"
        		+ "    tau.tauID,\r\n"
        		+ "    tau.tenTau\r\n"
        		+ "FROM Chuyen c\r\n"
        		+ "INNER JOIN Tau tau \r\n"
        		+ "    ON tau.tauID = c.tauID\r\n"
        		+ "INNER JOIN Toa t \r\n"
        		+ "    ON t.tauID = tau.tauID\r\n"
        		+ "WHERE c.chuyenID = @chuyenID\r\n"
        		+ "ORDER BY t.soToa;";
        try {
        	PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, chuyenID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Toa t = new Toa();
                t.setToaID(rs.getString("toaID"));
                t.setTau(new Tau(rs.getString("tauID")));
                t.setHangToa(HangToa.valueOf(rs.getString("hangToaID")));
                t.setSucChua(rs.getInt("sucChua"));
                t.setSoToa(rs.getString("soToa"));
                list.add(t);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return list;
    }
    
    public Toa getToaByID(String toaID) {
    	Connection conn = connectDB.getConnection();
    	String sql = "select * from Toa where toaID = ?";
    	try {
    		PreparedStatement ps = conn.prepareStatement(sql);
    		ps.setString(1, toaID);
    		ResultSet rs = ps.executeQuery();
    		Toa t = new Toa();
            t.setToaID(rs.getString("toaID"));
            t.setTau(new Tau(rs.getString("tauID")));
            t.setHangToa(HangToa.valueOf(rs.getString("hangToaID")));
            t.setSucChua(rs.getInt("sucChua"));
            t.setSoToa(rs.getString("soToa"));
    	} catch (Exception e) {
        	e.printStackTrace();
        }
    	return null;
    }
    
    
    public Toa getToaByChuyenIDToaID(String chuyenID, String toaID) {
    	Connection conn = connectDB.getConnection();
    	String sql = "select toa.toaID, toa.tauID, toa.hangToaID, toa.sucChua, toa.soToa"
    			+ " from Toa toa join Chuyen c on toa.tauID = c.tauID"
    			+ " where c.chuyenID = ?"
    			+ " and toa.toaID = ?";
    	try {
    		PreparedStatement ps = conn.prepareStatement(sql);
    		ps.setString(1, chuyenID);
    		ps.setString(2, toaID);
    		ResultSet rs = ps.executeQuery();
    		Toa t = new Toa();
            t.setToaID(rs.getString("toaID"));
            t.setTau(new Tau(rs.getString("tauID")));
            t.setHangToa(HangToa.valueOf(rs.getString("hangToaID")));
            t.setSucChua(rs.getInt("sucChua"));
            t.setSoToa(rs.getString("soToa"));
    	} catch (Exception e) {
        	e.printStackTrace();
        }
    	return null;
    }
}