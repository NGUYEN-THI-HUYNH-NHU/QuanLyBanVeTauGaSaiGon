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

import dao.impl.PhieuGiuChoChiTietDAO;
import dao.impl.PhieuGiuChoDAO;
import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;

import java.util.List;

public class PhieuGiuCho_BUS {
    private final PhieuGiuChoDAO pgcDAO = new PhieuGiuChoDAO();
    private final PhieuGiuChoChiTietDAO pgcctDAO = new PhieuGiuChoChiTietDAO();

    public void huyCacPhieuGiuChoChiTiet(List<Ve> listVe, TrangThaiPhieuGiuCho trangThai) {
        for (Ve ve : listVe) {
            pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByVe(ve, trangThai);
        }
    }
}
