package dao.impl;/*
 * @ (#) ChuyenGa_DAO.java   1.0     09/12/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 09/12/2025
 */

import connectDB.ConnectDB;
import entity.Chuyen;
import entity.ChuyenGa;
import entity.Ga;
import entity.Tau;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ChuyenGa_DAO {
    public List<ChuyenGa> getChiTietHanhTrinh(String maChuyen) {
        List<ChuyenGa> list = new ArrayList<>();
        Connection con = ConnectDB.getInstance().getConnection();

        String sql = "SELECT cg.*, g.tenGa, g.tinhThanh, c.tauID, t.tenTau FROM ChuyenGa cg " +
                "JOIN Ga g ON cg.gaID = g.gaID " +
                "JOIN Chuyen c ON cg.chuyenID = c.chuyenID " +
                "JOIN Tau t ON c.tauID = t.tauID " +
                "WHERE cg.chuyenID = ? " +
                "ORDER BY cg.thuTu ASC";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, maChuyen);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String maGa = rs.getString("gaID");
                String tenGa = rs.getString("tenGa");
                Ga ga = new Ga(maGa, tenGa);

                int thuTu = rs.getInt("thuTu");
                Date sqlNgayDen = rs.getDate("ngayDen");
                Time sqlGioDen = rs.getTime("gioDen");
                LocalDate ngayDen = (sqlNgayDen != null) ? sqlNgayDen.toLocalDate() : null;
                LocalTime gioDen = (sqlGioDen != null) ? sqlGioDen.toLocalTime() : null;
                Date sqlNgayDi = rs.getDate("ngayDi");
                Time sqlGioDi = rs.getTime("gioDi");
                LocalDate ngayDi = (sqlNgayDi != null) ? sqlNgayDi.toLocalDate() : null;
                LocalTime gioDi = (sqlGioDi != null) ? sqlGioDi.toLocalTime() : null;
                String tauID = rs.getString("tauID");
                String tenTau = rs.getString("tenTau");
                Tau tau = new Tau(tauID, tenTau);
                Chuyen chuyen = new Chuyen(maChuyen);
                chuyen.setTau(tau);
                ChuyenGa cg = new ChuyenGa(chuyen, ga, thuTu, ngayDen, gioDen, ngayDi, gioDi);
                list.add(cg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
