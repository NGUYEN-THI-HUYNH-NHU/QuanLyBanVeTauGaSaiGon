package bus;
/*
 * @(#) ThanhToan_BUS.java  1.0  [5:12:09 PM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.GiaoDichThanhToan_DAO;
import entity.GiaoDichThanhToan;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */

public class ThanhToan_BUS {
	private final GiaoDichThanhToan_DAO gdttDAO = new GiaoDichThanhToan_DAO();

	/**
	 * @param giaoDichThanhToan
	 */
	public void luuThongTinThanhToan(GiaoDichThanhToan giaoDichThanhToan) {
		gdttDAO.createGiaoDichThanhToan(giaoDichThanhToan);
	}

}
