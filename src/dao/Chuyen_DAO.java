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
import entity.type.LoaiTau;

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
                + "\r\n"
                + "SELECT\r\n"
                + "    c.chuyenID,\r\n"
                + "    c.tuyenID,\r\n"
                + "    c.tauID,\r\n"
                // [THÊM MỚI] Lấy thêm loaiTauID từ bảng Tau
                + "    t.loaiTauID,\r\n" 
                // Thông tin tại ga đi yêu cầu
                + "    cgDi.ngayDi   AS ngayDi,\r\n"
                + "    cgDi.gioDi    AS gioDi,\r\n"
                // Thông tin tại ga đến yêu cầu
                + "    cgDen.ngayDen  AS ngayDen,\r\n"
                + "    cgDen.gioDen  AS gioDen\r\n"
                + "FROM Chuyen c\r\n"
                // [THÊM MỚI] JOIN với bảng Tau để lấy loaiTauID
                + "INNER JOIN Tau t ON c.tauID = t.tauID\r\n" 
                // Tìm ga đi trong lịch trình của chuyến
                + "INNER JOIN ChuyenGa cgDi\r\n"
                + "    ON cgDi.chuyenID = c.chuyenID\r\n"
                + "    AND cgDi.gaID = @gaDiID\r\n"
                // Tìm ga đến trong lịch trình của chuyến
                + "INNER JOIN ChuyenGa cgDen\r\n"
                + "    ON cgDen.chuyenID = c.chuyenID\r\n"
                + "    AND cgDen.gaID = @gaDenID\r\n"
                + "WHERE\r\n"
                + "    cgDi.ngayDi = @ngayDi\r\n"
                + "    AND cgDi.thuTu < cgDen.thuTu\r\n"
                + "ORDER BY cgDi.gioDi, c.chuyenID;\r\n";
        
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

                    // --- KẾT THÚC SỬA LOGIC JAVA ---

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
}