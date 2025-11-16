package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [12:58:05 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
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
import entity.DonDatCho;
import entity.Ga;
import entity.Ghe;
import entity.NhanVien;
import entity.PhieuGiuCho;
import entity.PhieuGiuChoChiTiet;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.AuthService;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

public class DatCho_BUS {
	private final PhieuGiuCho_DAO pgcDAO = new PhieuGiuCho_DAO();
	private final PhieuGiuChoChiTiet_DAO pgcctDAO = new PhieuGiuChoChiTiet_DAO();
	private final DonDatCho_DAO ddcDAO = new DonDatCho_DAO();
	private final Ghe_DAO gheDAO = new Ghe_DAO();

	public PhieuGiuCho taoPhieuGiuCho() {
		NhanVien nv = AuthService.getInstance().getCurrentUser();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmm");
		String pgcID = "PGC-" + now.format(formatter).toString();

		return new PhieuGiuCho(pgcID, nv, TrangThaiPhieuGiuCho.DANG_GIU);
	}

	public boolean themPhieuGiuCho(PhieuGiuCho phieuGiuCho) {
		return pgcDAO.createPhieuGiuCho(phieuGiuCho);
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

	public PhieuGiuChoChiTiet taoPhieuGiuChoChiTiet(PhieuGiuCho pgc, VeSession v, int soThuTu) {
		String chuyenID = v.getChuyenID();
		String tenGaDi = v.getTenGaDi();
		String tenGaDen = v.getTenGaDen();
		int soToa = v.getSoToa();
		int soGhe = v.getSoGhe();
		LocalDateTime thoiDiemGiuCho = v.getThoiDiemHetHan().minus(Duration.ofMinutes(10));

		if (!pgcctDAO.checkConflict(chuyenID, tenGaDi, tenGaDen, soToa, soGhe)) {
			String pgcctID = pgc.getPhieuGiuChoID() + "-" + String.valueOf(soThuTu);
			PhieuGiuChoChiTiet pgcct = new PhieuGiuChoChiTiet(pgcctID, pgc, new Chuyen(v.getChuyenID()),
					new Ghe(v.getGheID()), new Ga(v.getGaDiID()), new Ga(v.getGaDenID()), thoiDiemGiuCho,
					TrangThaiPhieuGiuCho.DANG_GIU.toString());
			return pgcct;
		}
		return null;
	}

	public boolean themPhieuGiuChoChiTiet(PhieuGiuChoChiTiet phieuGiuChoChiTiet) {
		return pgcctDAO.createPhieuGiuChoChiTiet(phieuGiuChoChiTiet);
	}

	public boolean xoaPhieuGiuChoVaChiTiet(List<VeSession> veTrongGio) {
		return true;
	}

	/**
	 * @param phieuGiuChoChiTietID
	 * @return
	 */
	public boolean xoaPhieuGiuChoChiTietByPgcctID(String phieuGiuChoChiTietID) {
		if (phieuGiuChoChiTietID.length() == 0 || phieuGiuChoChiTietID == null) {
			return false;
		}
		return pgcctDAO.deletePhieuGiuChoChiTiet(phieuGiuChoChiTietID);
	}

	/**
	 * @param phieuGiuChoID
	 * @return
	 */
	public boolean xoaPhieuGiuChoChiTietByPgcID(String phieuGiuChoID) {
		if (phieuGiuChoID.length() == 0 || phieuGiuChoID == null) {
			return false;
		}
		return pgcctDAO.deletePhieuGiuChoChiTietByPgcID(phieuGiuChoID);
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

	public DonDatCho taoDonDatCho(BookingSession bookingSession) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");

		String ddcID = "DDC-" + now.format(formatter).toString();

		return new DonDatCho(ddcID, bookingSession.getNhanVien(), bookingSession.getKhachHang(), now);
	}

	public boolean themDonDatCho(Connection conn, DonDatCho donDatCho) {
		return ddcDAO.insertDonDatCho(conn, donDatCho);
	}

	/**
	 * @param conn
	 * @param phieuGiuCho
	 * @param xacNhan
	 */
	public boolean capNhatPhieuGiuCho(Connection conn, PhieuGiuCho phieuGiuCho,
			TrangThaiPhieuGiuCho trangThaiPhieuGiuCho) {
		return pgcDAO.updateTrangThaiPhieuGiuCho(conn, phieuGiuCho.getPhieuGiuChoID(), trangThaiPhieuGiuCho.toString());
	}

	/**
	 * @param conn
	 * @param phieuGiuCho
	 * @param trangThaiPhieuGiuCho
	 */
	public boolean capNhatCacPhieuGiuChoChiTiet(Connection conn, PhieuGiuCho phieuGiuCho,
			TrangThaiPhieuGiuCho trangThaiPhieuGiuCho) {
		return pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByPhieuGiuChoID(conn, phieuGiuCho.getPhieuGiuChoID(),
				trangThaiPhieuGiuCho.toString());
	}
}