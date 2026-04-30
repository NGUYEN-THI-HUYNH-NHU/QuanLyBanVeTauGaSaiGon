package bus;
/*
 * @(#) PhieuGiuChoChiTiet_BUS.java  1.0  [3:11:31 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import dao.impl.PhieuGiuChoChiTiet_DAO;
import dao.impl.PhieuGiuCho_DAO;
import entity.PhieuGiuCho;
import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;

import java.sql.Connection;
import java.util.List;

public class PhieuGiuCho_BUS {
    private final PhieuGiuCho_DAO pgcDAO = new PhieuGiuCho_DAO();
    private final PhieuGiuChoChiTiet_DAO pgcctDAO = new PhieuGiuChoChiTiet_DAO();

    /**
     * @param conn
     * @param listVeHoanRow
     * @param daHuy
     */
    public void huyCacPhieuGiuChoChiTiet(Connection conn, List<Ve> listVe, TrangThaiPhieuGiuCho trangThai) {
        for (Ve ve : listVe) {
            pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByVe(conn, ve, trangThai);
        }
    }

    /**
     * @param veID
     * @return
     */
    public PhieuGiuCho timPhieuGiuChoByVeID(Connection conn, String veID) {
        if (veID != null) {
            return pgcDAO.getPhieuGiuChoByVeID(conn, veID);
        }
        return null;
    }
}
