package bus;
/*
 * @(#) Chuyen_BUS.java  1.0  [12:42:29 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.util.List;

import dao.Chuyen_DAO;
import entity.Chuyen;

import java.time.LocalDate;
import java.util.ArrayList;

public class Chuyen_BUS {
    private Chuyen_DAO chuyenDAO;

    public Chuyen_BUS() {
        chuyenDAO = new Chuyen_DAO();
    }

    public List<Chuyen> timChuyen(String gaDi, String gaDen, LocalDate ngayDi) {
        List<Chuyen> danhSachChuyen = new ArrayList<>();

        // Tìm chuyến đi
        List<Chuyen> chuyenDi = chuyenDAO.getAllChuyenTheoGaDiGaDenNgayDi(gaDi, gaDen, ngayDi);
        danhSachChuyen.addAll(chuyenDi);

        return danhSachChuyen;
    }
}