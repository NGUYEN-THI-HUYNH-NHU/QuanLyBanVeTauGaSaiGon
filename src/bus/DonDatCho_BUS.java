package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [2:11:25 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.DonDatCho_DAO;
import entity.DonDatCho;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

public class DonDatCho_BUS {
	private final DonDatCho_DAO donDatChoDAO = new DonDatCho_DAO();

	/**
	 * @param donDatChoID
	 * @param soGiayTo
	 * @return
	 */
	public DonDatCho timDonDatChoTheoIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
		return donDatChoDAO.findDonDatChoByIDVaSoGiayTo(donDatChoID, soGiayTo);
	}

}
