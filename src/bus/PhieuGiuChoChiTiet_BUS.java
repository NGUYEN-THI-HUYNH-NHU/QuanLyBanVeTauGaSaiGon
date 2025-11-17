package bus;
/*
 * @(#) PhieuGiuChoChiTiet_BUS.java  1.0  [3:11:31 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.util.List;

import dao.PhieuGiuChoChiTiet_DAO;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.form.hoanVe.VeHoanRow;

public class PhieuGiuChoChiTiet_BUS {
	private final PhieuGiuChoChiTiet_DAO pgcctDAO = new PhieuGiuChoChiTiet_DAO();

	/**
	 * @param conn
	 * @param listVeHoanRow
	 * @param daHuy
	 */
	public void huyCacPhieuGiuChoChiTiet(Connection conn, List<VeHoanRow> listVeHoanRow,
			TrangThaiPhieuGiuCho trangThai) {
		for (VeHoanRow r : listVeHoanRow) {
			pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByVe(conn, r.getVe(), TrangThaiPhieuGiuCho.HET_GIU);
		}
	}

}
