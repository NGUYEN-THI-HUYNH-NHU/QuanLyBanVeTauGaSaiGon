package dao.impl;
/*
 * @(#) SuDungKhuyenMai_DAO.java  1.0  [10:50:42 PM] Dec 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import connectDB.ConnectDB;
import entity.SuDungKhuyenMai;
import entity.Ve;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 1, 2025
 * @version: 1.0
 */

public class SuDungKhuyenMai_DAO {
    private final ConnectDB connectDB;

    public SuDungKhuyenMai_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public boolean themSuDungKhuyenMai(Connection conn, SuDungKhuyenMai suDungKhuyenMai) throws Exception {
        String sql = "INSERT INTO SuDungKhuyenMai(suDungKhuyenMaiID, khuyenMaiID, hoaDonChiTietID, trangThai) VALUES(?, ?, ?, 'DA_AP_DUNG')";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, suDungKhuyenMai.getId());
            pstm.setString(2, suDungKhuyenMai.getKhuyenMai().getId());
            pstm.setString(3, suDungKhuyenMai.getHoaDonChiTiet().getId());
            return pstm.executeUpdate() > 0;
        }
    }

    /**
     * @param conn
     * @param listVe
     * @return
     */
    public int huySuDungKhuyenMaiChoListVe(Connection conn, List<Ve> listVe) throws Exception {
        if (listVe == null || listVe.isEmpty()) {
            return 0;
        }
        String sql = "UPDATE SuDungKhuyenMai " + "SET trangThai = 'DA_HUY' " + "WHERE hoaDonChiTietID IN ("
                + "    SELECT hoaDonChiTietID FROM HoaDonChiTiet " + "    WHERE veID = ? AND loaiDichVu = 'KHUYEN_MAI'"
                + ")";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            for (Ve ve : listVe) {
                pstm.setString(1, ve.getId());
                pstm.addBatch();
            }

            // Thực thi toàn bộ lô lệnh cùng lúc
            int[] results = pstm.executeBatch();

            // Tính tổng số dòng đã được cập nhật (số khuyến mãi đã hủy)
            int totalUpdated = 0;
            for (int result : results) {
                if (result > 0) {
                    totalUpdated += result;
                } else if (result == Statement.SUCCESS_NO_INFO) {
                    totalUpdated++;
                }
            }
            return totalUpdated;
        }
    }
}
