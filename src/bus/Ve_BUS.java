package bus;
/*
 * @(#) Ve_Bus.java  1.0  [10:10:54 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.List;

import dao.Ve_DAO;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class Ve_BUS {
	private final Ve_DAO veDAO = new Ve_DAO();

	/**
	 * @param bookingSession
	 * @return
	 */
	public boolean themCacVe(String donDatChoID, BookingSession bookingSession) {
		List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
		List<VeSession> dsVeVe = bookingSession.getReturnSelected();

		for (VeSession v : dsVeDi) {
			String veID = "VE-" + v.getGaDiID() + v.getGaDenID() + "-" + v.getChuyenID() + "-" + v.getSoGhe();
			veDAO.createVe(veID, donDatChoID, v);
		}
		return true;
	}

}