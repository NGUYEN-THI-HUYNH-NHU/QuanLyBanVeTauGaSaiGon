package bus;
/*
 * @(#) Ve_Bus.java  1.0  [10:10:54 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.Ve_DAO;
import entity.Chuyen;
import entity.DonDatCho;
import entity.Ga;
import entity.Ghe;
import entity.Ve;
import entity.type.TrangThaiVe;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;

public class Ve_BUS {
	private final Ve_DAO veDAO = new Ve_DAO();

	/**
	 * @param donDatCho
	 * @param bookingSession
	 * @return List<Ve>
	 */
	public List<Ve> taoCacVeVaThemVaoBookingSession(BookingSession bookingSession) {
		List<Ve> dsVe = new ArrayList<Ve>();
		List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
		List<VeSession> dsVeVe = bookingSession.getReturnSelected();
		DonDatCho donDatCho = bookingSession.getDonDatCho();

		for (VeSession v : dsVeDi) {
			String veID = "VE-" + v.getGaDiID() + v.getGaDenID() + v.getChuyenID() + "-" + v.getGheID();
			Ve ve = new Ve(veID, v.getHanhKhach(), donDatCho, new Chuyen(v.getChuyenID()),
					new Ghe(v.getGheID(), v.getSoGhe()), new Ga(v.getGaDiID(), v.getTenGaDi()),
					new Ga(v.getGaDenID(), v.getTenGaDen()), LocalDateTime.of(v.getNgayDi(), v.getGioDi()), v.getGia(),
					TrangThaiVe.DA_BAN);
			dsVe.add(ve);
			v.setVe(ve);
		}
		for (VeSession v : dsVeVe) {
			String veID = "VE-" + v.getGaDiID() + v.getGaDenID() + v.getChuyenID() + "-" + v.getGheID();
			Ve ve = new Ve(veID, v.getHanhKhach(), donDatCho, new Chuyen(v.getChuyenID()),
					new Ghe(v.getGheID(), v.getSoGhe()), new Ga(v.getGaDiID(), v.getTenGaDi()),
					new Ga(v.getGaDenID(), v.getTenGaDen()), LocalDateTime.of(v.getNgayDi(), v.getGioDi()), v.getGia(),
					TrangThaiVe.DA_BAN);
			dsVe.add(ve);
			v.setVe(ve);
		}
		return dsVe;
	}

	/**
	 * @param exchangeSession
	 * @return
	 */
	public List<Ve> taoCacVeVaThemVaoExchangeSession(ExchangeSession exchangeSession) {
		List<Ve> dsVe = new ArrayList<Ve>();
		List<VeSession> dsVeMoi = exchangeSession.getListVeMoiDangChon();
		DonDatCho donDatCho = exchangeSession.getDonDatChoMoi();

		for (VeSession v : dsVeMoi) {
			String veID = "VE-" + v.getGaDiID() + v.getGaDenID() + v.getChuyenID() + "-" + v.getGheID();
			Ve ve = new Ve(veID, v.getHanhKhach(), donDatCho, new Chuyen(v.getChuyenID()),
					new Ghe(v.getGheID(), v.getSoGhe()), new Ga(v.getGaDiID(), v.getTenGaDi()),
					new Ga(v.getGaDenID(), v.getTenGaDen()), LocalDateTime.of(v.getNgayDi(), v.getGioDi()), v.getGia(),
					TrangThaiVe.DA_BAN);
			dsVe.add(ve);
			v.setVe(ve);
		}

		return dsVe;
	}

	/**
	 * @param conn
	 * @param dsVe
	 * @return boolean
	 */
	public boolean themCacVe(Connection conn, List<Ve> dsVe) throws Exception {
		for (Ve v : dsVe) {
			if (!veDAO.insertVe(conn, v)) {
				return false;
			}
		}
		return true;

	}

	/**
	 * @param donDatChoID
	 * @return
	 */
	public List<Ve> timCacVeTheoDonDatChoID(String donDatChoID) {
		return veDAO.getVeByDonDatChoID(donDatChoID);
	}

	/**
	 * @param donDatChoID
	 * @param trangThai
	 * @return
	 */
	public List<Ve> timCacVeTheoDonDatChoID(String donDatChoID, TrangThaiVe trangThai) {
		return veDAO.getVeByDonDatChoID(donDatChoID, trangThai);
	}

	/**
	 * @param conn
	 * @param listVeHoanRow
	 * @param trangThai
	 */
	public void capNhatTrangThaiVe(Connection conn, List<Ve> listVe, TrangThaiVe trangThai) throws Exception {
		for (Ve ve : listVe) {
			veDAO.updateTrangThaiVe(conn, ve.getVeID(), trangThai);
		}
	}
}