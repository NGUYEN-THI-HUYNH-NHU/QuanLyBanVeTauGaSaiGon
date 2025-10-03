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
import java.time.LocalDateTime;
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
	    String querySQL = " DECLARE @gaDiID VARCHAR(50) = ?"
			    		+ " DECLARE @gaDenID VARCHAR(50) = ?"
			    		+ " DECLARE @ngayDi DATE = ?"
			    		+ " SELECT"
			    		+ " 	c.chuyenID,"
			    		+ "		c.tuyenID,"	
			    		+ " 	tau.tauID,"
			    		+ "		cg_di.gioKhoiHanh AS gioKhoiHanh,"
			    		+ " 	cg_den.gioDen AS gioDen"
			    		+ " FROM Chuyen c"
			    		+ " INNER JOIN ChuyenGa cg_di"
			    		+ " 	ON cg_di.chuyenID = c.chuyenID"
			    		+ " 	AND cg_di.gaID = @gaDiID"
			    		+ "	INNER JOIN ChuyenGa cg_den"
			    		+ " 	ON cg_den.chuyenID = c.chuyenID"
			    		+ " 	AND cg_den.gaID = @gaDenID"
			    		+ " 	AND cg_di.thuTu < cg_den.thuTu"
			    		+ "	LEFT JOIN Tau tau"
			    		+ " 	ON tau.tauID = c.tauID"
			    		+ "	WHERE"
			    		+ " 	CONVERT(date, COALESCE(cg_di.gioKhoiHanh, cg_di.gioDen, c.gioKhoiHanh)) = @ngayDi"
			    		+ "	ORDER BY"
			    		+ " 	COALESCE(cg_di.gioKhoiHanh, cg_di.gioDen, c.gioKhoiHanh);";
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
	        	LocalDateTime gioKhoiHanh = resultSet.getTimestamp("gioKhoiHanh").toLocalDateTime();
	        	LocalDateTime gioDen = resultSet.getTimestamp("gioDen").toLocalDateTime();
	        	chuyenList.add(new Chuyen(chuyenID, tuyen, tau, gioKhoiHanh, gioDen));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return chuyenList;
	}
}
