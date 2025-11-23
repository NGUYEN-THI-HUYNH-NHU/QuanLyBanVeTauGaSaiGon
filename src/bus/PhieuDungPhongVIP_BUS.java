package bus;
/*
 * @(#) PhieuDungPhongVIP_BUS.java  1.0  [8:57:21 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dao.PhieuDungPhongVIP_DAO;
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;

public class PhieuDungPhongVIP_BUS {
	private final PhieuDungPhongVIP_DAO phieuDungPhongVIPDAO = new PhieuDungPhongVIP_DAO();

	/**
	 * @param bookingSession
	 * @return
	 */
	public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(List<VeSession> listVeSession) {
		List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
		for (VeSession v : listVeSession) {
			String phieuID = "PDVIP-" + v.getVe().getVeID().substring(3);
			PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001"), v.getVe(),
					TrangThaiPDPVIP.CHUA_DUNG);
			v.setPhieuDungPhongVIP(phieu);
			dsPhieu.add(phieu);
		}
		return dsPhieu;
	}

	/**
	 * @param exchangeSession
	 * @return
	 */
	public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(ExchangeSession exchangeSession) {
		List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
		List<VeSession> dsVe = exchangeSession.getListVeMoiDangChon();
		for (VeSession v : dsVe) {
			String phieuID = "PDVIP-" + v.getVe().getVeID().substring(3);
			PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001"), v.getVe(),
					TrangThaiPDPVIP.CHUA_DUNG);
			v.setPhieuDungPhongVIP(phieu);
			dsPhieu.add(phieu);
		}
		return dsPhieu;
	}

	/**
	 * @param conn
	 * @param dsPhieu
	 */
	public boolean themCacPhieuDungPhongChoVIP(Connection conn, List<PhieuDungPhongVIP> dsPhieuDungPhongVIP)
			throws Exception {
		if (dsPhieuDungPhongVIP != null) {
			for (PhieuDungPhongVIP phieu : dsPhieuDungPhongVIP) {
				phieuDungPhongVIPDAO.insertPhieuDungPhongVIP(conn, phieu);
			}
			return true;
		}
		return false;
	}

	/**
	 * @param conn
	 * @param listVe
	 * @param daHoan
	 */
	public void capNhatPhieuDungPhongChoVIP(Connection conn, List<Ve> listVe, TrangThaiPDPVIP trangThai) {
		for (Ve ve : listVe) {
			PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(conn, ve.getVeID());
			if (phieu != null) {
				phieuDungPhongVIPDAO.updateTrangThaiPhieuDungPhongVIP(conn, phieu.getPhieuDungPhongChoVIPID(),
						trangThai);
			}
		}
	}
}
