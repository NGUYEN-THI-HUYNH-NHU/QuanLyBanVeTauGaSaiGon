package dao.impl;
/*
 * @(#) GiaoDichThanhToan_DAO.java  1.0  [1:36:52 PM] Nov 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import connectDB.ConnectDB;
import dto.GiaoDichThanhToanDTO;

import java.sql.Connection;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 2, 2025
 * @version: 1.0
 */

public class GiaoDichThanhToan_DAO {

    private final ConnectDB connectDB = ConnectDB.getInstance();

    public GiaoDichThanhToan_DAO() {
        connectDB.connect();
    }

    public boolean createGiaoDichThanhToan(Connection conn, GiaoDichThanhToanDTO giaoDichThanhToan) {
        // TODO: tam thoi giao dich thanh toan bi gop voi hoa don, nen chua lam gi duoc
        // o day. Ve sau neu tach lai se sua sau;
        return true;
    }

}
