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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Chuyen;
import entity.Tau;
import entity.Tuyen;

public class Chuyen_DAO {
	private ConnectDB connectDB;
	
	public Chuyen_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect(); 
	}
	
	public List<Chuyen> getChuyenByGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi) {
	    Connection connection = connectDB.getConnection();
	    String querySQL = "DECLARE @gaDiID VARCHAR(50) = ?;\r\n"
	    		+ "DECLARE @gaDenID VARCHAR(50) = ?;\r\n"
	    		+ "DECLARE @ngayDi DATE = ?;\r\n"
	    		+ "SELECT\r\n"
	    		+ "    c.chuyenID,\r\n"
	    		+ "    c.tuyenID,\r\n"
	    		+ "    c.tauID,\r\n"
	    		+ "	cgDi.ngayDi   AS ngayDi,\r\n"
	    		+ "    cgDi.gioDi    AS gioDi,\r\n"
	    		+ "    cgDen.ngayDen  AS ngayDen,\r\n"
	    		+ "    cgDen.gioDen  AS gioDen\r\n"
	    		+ "FROM Chuyen c\r\n"
	    		+ "INNER JOIN ChuyenGa cgDi\r\n"
	    		+ "    ON cgDi.chuyenID = c.chuyenID\r\n"
	    		+ "    AND cgDi.gaID = @gaDiID\r\n"
	    		+ "INNER JOIN ChuyenGa cgDen\r\n"
	    		+ "    ON cgDen.chuyenID = c.chuyenID\r\n"
	    		+ "    AND cgDen.gaID = @gaDenID\r\n"
	    		+ "WHERE\r\n"
	    		+ "    c.ngayDi = @ngayDi\r\n"
	    		+ "    AND cgDi.thuTu < cgDen.thuTu\r\n"
	    		+ "ORDER BY c.gioDi, c.chuyenID, cgDi.thuTu;";
	    List<Chuyen> chuyenList = null;

	    try {
	        PreparedStatement pstmt = connection.prepareStatement(querySQL);
	        pstmt.setString(1, gaDiID);
	        pstmt.setString(2, gaDenID);
	        pstmt.setDate(3, java.sql.Date.valueOf(ngayDi));
	        ResultSet resultSet = pstmt.executeQuery();
	        chuyenList = new ArrayList<Chuyen>();
	        
	        while (resultSet.next()) {
	        	String chuyenID = resultSet.getString("chuyenID");
	        	Tuyen tuyen = new Tuyen(resultSet.getString("tuyenID"));
	        	Tau tau = new Tau(resultSet.getString("tauID"));
	        	LocalTime gioDi = resultSet.getTime("gioDi").toLocalTime();
	        	LocalDate ngayDen = resultSet.getDate("ngayDen").toLocalDate();
	        	LocalTime gioDen = resultSet.getTime("gioDen").toLocalTime();
	        	
	        	chuyenList.add(new Chuyen(chuyenID, tuyen, tau, ngayDi, gioDi, ngayDen, gioDen));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return chuyenList;
	}
}
