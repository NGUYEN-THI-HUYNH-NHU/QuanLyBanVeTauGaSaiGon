package bus;
/*
 * @(#) PhieuDungPhongVIP_BUS.java  1.0  [8:57:21 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dao.PhieuDungPhongVIP_DAO;
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.type.TrangThaiPDPVIP;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */

public class PhieuDungPhongVIP_BUS {
	private final PhieuDungPhongVIP_DAO phieuDungPhongVIPDAO = new PhieuDungPhongVIP_DAO();

	/**
	 * @param bookingSession
	 * @return
	 */
	public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(BookingSession bookingSession) {
		List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
		List<VeSession> dsVe = bookingSession.getAllSelectedTickets();
		for (VeSession v : dsVe) {
			String phieuID = "PDVIP-" + v.getVe().getVeID().substring(3);
			PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001"), v.getVe(),
					TrangThaiPDPVIP.DA_DUNG);
			v.setPhieuDungPhongVIP(phieu);
			dsPhieu.add(phieu);
		}
		return dsPhieu;
	}

	/**
	 * @param conn
	 * @param dsPhieu
	 */
	public boolean themCacPhieuDungPhongChoVIP(Connection conn, List<PhieuDungPhongVIP> dsPhieuDungPhongVIP) {
		if (dsPhieuDungPhongVIP != null) {
			for (PhieuDungPhongVIP phieu : dsPhieuDungPhongVIP) {
				phieuDungPhongVIPDAO.insertPhieuDungPhongVIP(conn, phieu);
			}
			return true;
		}
		return false;
	}

}
