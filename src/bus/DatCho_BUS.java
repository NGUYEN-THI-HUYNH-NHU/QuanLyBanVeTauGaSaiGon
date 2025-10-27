package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [12:58:05 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.DonDatCho_DAO;
import dao.Ghe_DAO;
import dao.PhieuGiuChoChiTiet_DAO;
import dao.PhieuGiuCho_DAO;
import entity.Chuyen;
import entity.Ga;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ghe;
import entity.NhanVien;
import entity.PhieuGiuCho;
import entity.PhieuGiuChoChiTiet;
import entity.Toa;
import entity.Ve;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;

public class DatCho_BUS {
	private final PhieuGiuCho_DAO pgcDAO = new PhieuGiuCho_DAO();
	private final PhieuGiuChoChiTiet_DAO pgcctDAO = new PhieuGiuChoChiTiet_DAO();
    private final DonDatCho_DAO ddDAO = new DonDatCho_DAO();
    private final Ghe_DAO gheDAO = new Ghe_DAO();

    public Ve createHold(Toa toa, Ghe ghe) throws Exception {
//        Ve v = ddDAO.createHoldForSeat(toa.getToaID(), ghe.getGheID());
        return new Ve();
    }

    public boolean releaseHold(VeSession v) throws Exception {
//        return ddDAO.releaseHoldByVeId(v.getVeID());
    	return true;
    }
    
	public boolean taoPhieuGiuChoVaChiTiet(List<VeSession> veTrongGio) {
//		NhanVien nv = AuthService.getInstance().getCurrentUser();
//		LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmm");
//
//        String pgcID = "PGC-" + now.format(formatter).toString();
//        PhieuGiuCho pgc = new PhieuGiuCho(pgcID, nv, now, "DANG_GIU");
//        pgcDAO.createPhieuGiuCho(pgc);
//        
//        for (VeSession v : veTrongGio) {
//        	String chuyenID = v.getChuyenID();
//        	String tenGaDi = v.getTenGaDi();
//        	String tenGaDen = v.getTenGaDen();
//        	String toaID = v.getToaID();
//        	int gheID = v.getSoGhe();
//        	Instant thoiDiemGiuCho = v.getThoiDiemHetHan().minus(Duration.ofMinutes(10));
//        	if (pgcctDAO.checkConflict(chuyenID, tenGaDi, tenGaDen, toaID, gheID)) {
//        		String pgcctID = pgcID + "-" +  v.getSoGhe();		
//        		pgcctDAO.createPhieuGiuChoChiTiet(new PhieuGiuChoChiTiet(pgcctID, pgc, 
//        				new Chuyen(v.getChuyenID()), new Ghe(v.getToaID(), v.getSoGhe()),
//        			    new Ga(v.getTenGaDi()), new Ga(v.getTenGaDen()), 
//        			    LocalDateTime.ofInstant(thoiDiemGiuCho, ZoneId.systemDefault()), "DANG_GIU"));
//        	}
//        }
        
		return true;
	}
}