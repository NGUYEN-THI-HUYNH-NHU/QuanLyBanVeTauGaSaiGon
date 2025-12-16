package dao;
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

import entity.Tau;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

import connectDB.ConnectDB;

public class Tau_DAO {
	private ConnectDB connectDB;

    public Tau_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }


}