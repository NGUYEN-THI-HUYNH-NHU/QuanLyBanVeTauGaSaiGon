package dao;
/*
 * @(#) Chuyen_DAO.java  1.0  [12:59:19 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Chuyen;
import entity.Tau;
import entity.Tuyen;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class Chuyen_DAO {
	private ConnectDB connectDB;
	
	public Chuyen_DAO() {
		connectDB = ConnectDB.getInstance();
		connectDB.connect(); 
	}
	
	public List<Chuyen> getAllChuyenTheoGaDiGaDenNgayDi(String gaDiID, String gaDenID, LocalDate ngayDi) {
	    Connection connection = connectDB.getConnection();
	    String querySQL = "SELECT * FROM Chuyen"
	    		+ " WHERE gaDiID = ?"
	    		+ " AND gaDenID = ?"
	    		+ " AND CAST(gioKhoiHanh AS date) = ?";
	    List<Chuyen> chuyenList = new ArrayList<>();

	    try {
	        PreparedStatement pstmt = connection.prepareStatement(querySQL);
	        pstmt.setString(1, gaDiID);
	        pstmt.setString(2, gaDenID);
	        pstmt.setDate(3, java.sql.Date.valueOf(ngayDi));

	        ResultSet resultSet = pstmt.executeQuery();
	        while (resultSet.next()) {
	        	String chuyenID = resultSet.getString("chuyenID");
	        	Tuyen tuyen = new Tuyen(resultSet.getString("tuyenID"));
	        	Tau tau = new Tau(resultSet.getString("tauID"));
	        	LocalDateTime gioKhoiHanh = resultSet.getTimestamp("gioKhoiHanh").toLocalDateTime();
	        	LocalDateTime gioDen = resultSet.getTimestamp("gioDen").toLocalDateTime();
	        	chuyenList.add(new Chuyen(chuyenID, tuyen, tau, gioKhoiHanh, gioDen));
	        }
	        return chuyenList;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
}
