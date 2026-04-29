package dao.impl;
/*
 * @(#) Tau_DAO.java  1.0  [4:26:23 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import connectDB.ConnectDB;
import entity.type.TrangThaiTau;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tau_DAO {
    private ConnectDB connectDB;

    public Tau_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public TrangThaiTau layTrangThaiTau(String tauID) {
        Connection con = connectDB.getConnection();
        String sql = "SELECT trangThai FROM Tau WHERE tauID = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, tauID);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    String statusStr = rs.getString("trangThai");
                    if (statusStr != null) {
                        return TrangThaiTau.valueOf(statusStr);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Tàu không tồn tại
    }

}