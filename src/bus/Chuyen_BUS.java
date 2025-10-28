package bus;
/*
 * @(#) Chuyen_BUS.java  1.0  [12:42:29 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.HashMap;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.util.List;
import java.util.Map;

import dao.Chuyen_DAO;
import dao.Ga_DAO;
import dao.Ghe_DAO;
import dao.Toa_DAO;
import entity.Chuyen;
import entity.Ga;
import entity.Ghe;
import entity.Toa;

import java.time.LocalDate;

public class Chuyen_BUS {
	private Ghe_DAO gheDAO;
	private Toa_DAO toaDAO;
    private Chuyen_DAO chuyenDAO;
    private dao.Ga_DAO gaDAO;

    public Chuyen_BUS() {
    	gheDAO = new Ghe_DAO();
    	toaDAO = new Toa_DAO();
        chuyenDAO = new Chuyen_DAO();
        gaDAO = new Ga_DAO();
    }
    
    // Lấy danh sách các ghế trong toa của tàu được chọn kèm trạng thái để hiện thị màu trong sơ đồ chỗ
    public Map<String, String> layTrangThaiCacGheTrongToaCuaChuyen(String gaDiID, String gaDenID, String chuyenID, String toaID) {
        List<Ghe> gheList = gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID);

        Map<String, String> result = new HashMap<>();
        if (gheList != null) {
            for (Ghe ghe : gheList)
                result.put(ghe.getGheID(), ghe.toString());
        }

        return result;
    }

    public List<Chuyen> timChuyenTheoGaDiGaDenNgayDi(String gaDi, String gaDen, LocalDate ngayDi) {
        return chuyenDAO.getChuyenByGaDiGaDenNgayDi(gaDi, gaDen, ngayDi);
    }

    // Gợi ý ga đi (tên)
    public List<Ga> goiYGaDi(String prefix, int limit) {
        return gaDAO.searchGaByPrefix(prefix, limit);
      
    }

    // Gợi ý ga đến dựa trên ga đi đã chọn 
    public List<Ga> goiYGaDenTheoGaDi(String gaDiID, String prefixGaDen, int limit) {
        return gaDAO.searchGaDenKhaThiByGaDi(gaDiID, prefixGaDen, limit);
    }
    
    public Ga timGaTheoTenGa(String tenGa) {
    	return gaDAO.getGaByTenGa(tenGa);
    }
    
    public List<Toa> layCacToaTheoChuyen(String chuyenID) {
    	return toaDAO.getToaByChuyenID(chuyenID);
    }
    
    public List<Ghe> layCacGheTrongToaTrenChuyen(String gaDiID, String gaDenID, String chuyenID, String toaID) {
    	return gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID);
    }
    
	public double layGiaGheTheoPhanDoan(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
		return gheDAO.calcGia(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
	}
}