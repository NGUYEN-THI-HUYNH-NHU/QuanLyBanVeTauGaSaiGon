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
import dao.Ga_DAO;
import entity.Chuyen;
import entity.Ga;

import java.time.LocalDate;

public class Chuyen_BUS {
    private Chuyen_DAO chuyenDAO;
    private Ga_DAO gaDAO;

    public Chuyen_BUS() {
        chuyenDAO = new Chuyen_DAO();
        gaDAO = new Ga_DAO();
    }

    public List<Chuyen> timChuyenTheoGaDiGaDenNgayDi(String gaDi, String gaDen, LocalDate ngayDi) {
        return chuyenDAO.getChuyenByGaDiGaDenNgayDi(gaDi, gaDen, ngayDi);
    }

    // Gợi ý ga đi (tên)
    public List<Ga> goiYGaDI(String prefix, int limit) {
        return gaDAO.searchGaByPrefix(prefix, limit);
      
    }

    // Gợi ý ga đến dựa trên ga đi đã chọn (dùng ngayDi để filter chuyến ngày đó)
    public List<Ga> goiYGaDenTheoGaDi(String gaDiID, String destPrefix, int limit) {
        return gaDAO.searchGaDenKhaThiByGaDi(gaDiID, destPrefix, limit);
    }
    
    public Ga timGaTheoTenGa(String tenGa) {
    	return gaDAO.getGaByTenGa(tenGa);
    }
}