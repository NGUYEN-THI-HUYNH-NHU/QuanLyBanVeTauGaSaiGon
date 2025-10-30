package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [12:58:05 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.DonDatCho_DAO;
import dao.Ghe_DAO;
import dao.PhieuGiuChoChiTiet_DAO;
import dao.PhieuGiuCho_DAO;
import entity.Chuyen;
import entity.Ga;
import entity.Ghe;
import entity.NhanVien;
import entity.PhieuGiuCho;
import entity.PhieuGiuChoChiTiet;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;

public class DatCho_BUS {
	private final PhieuGiuCho_DAO pgcDAO = new PhieuGiuCho_DAO();
	private final PhieuGiuChoChiTiet_DAO pgcctDAO = new PhieuGiuChoChiTiet_DAO();
	private final DonDatCho_DAO ddDAO = new DonDatCho_DAO();
	private final Ghe_DAO gheDAO = new Ghe_DAO();

	public PhieuGiuCho themPhieuGiuCho() {
		NhanVien nv = AuthService.getInstance().getCurrentUser();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmm");
		String pgcID = "PGC-" + now.format(formatter).toString();

		PhieuGiuCho pgc = new PhieuGiuCho(pgcID, nv, TrangThaiPhieuGiuCho.DANG_GIU);

		pgcDAO.createPhieuGiuCho(pgc);

		return pgc;
	}

//	public List<PhieuGiuChoChiTiet> themPhieuGiuChoChiTiet(PhieuGiuCho pgc, List<VeSession> veTrongGio) {
//		List<PhieuGiuChoChiTiet> dsPgcct = new ArrayList<PhieuGiuChoChiTiet>();
//		for (VeSession v : veTrongGio) {
//			PhieuGiuChoChiTiet pgcct = taoPhieuGiuChoChiTiet(pgc, v);
//			if (pgcctDAO.createPhieuGiuChoChiTiet(pgcct)) {
//				dsPgcct.add(pgcct);
//			}
//		}
//		return dsPgcct;
//	}

	public PhieuGiuChoChiTiet themPhieuGiuChoChiTiet(PhieuGiuCho pgc, VeSession v) {
		String chuyenID = v.getChuyenID();
		String tenGaDi = v.getTenGaDi();
		String tenGaDen = v.getTenGaDen();
		int soToa = v.getSoToa();
		int soGhe = v.getSoGhe();
		LocalDateTime thoiDiemGiuCho = v.getThoiDiemHetHan().minus(Duration.ofMinutes(10));

		if (!pgcctDAO.checkConflict(chuyenID, tenGaDi, tenGaDen, soToa, soGhe)) {
			String pgcctID = pgc.getPhieuGiuChoID() + "-" + String.valueOf(v.getSoGhe());
			PhieuGiuChoChiTiet pgcct = new PhieuGiuChoChiTiet(pgcctID, pgc, new Chuyen(v.getChuyenID()),
					new Ghe(v.getGheID()), new Ga(v.getGaDiID()), new Ga(v.getGaDenID()), thoiDiemGiuCho,
					TrangThaiPhieuGiuCho.DANG_GIU.toString());
			pgcctDAO.createPhieuGiuChoChiTiet(pgcct);
			return pgcct;
		}
		return null;
	}

	public boolean xoaPhieuGiuChoVaChiTiet(List<VeSession> veTrongGio) {
		return true;
	}

	/**
	 * @param veSession
	 * @return
	 */
	public boolean xoaPhieuGiuChoChiTiet(String phieuGiuChoChiTietID) {
		if (phieuGiuChoChiTietID.length() == 0 || phieuGiuChoChiTietID == null) {
			return false;
		}
		return pgcctDAO.deletePhieuGiuChoChiTiet(phieuGiuChoChiTietID);
	}

	/**
	 * @param bookingSession
	 */
	public boolean xoaPhieuGiuCho(String phieuGiuChoID) {
		if (phieuGiuChoID.length() == 0 || phieuGiuChoID == null) {
			return false;
		}
		return pgcDAO.deletePhieuGiuChoByID(phieuGiuChoID);
	}
}