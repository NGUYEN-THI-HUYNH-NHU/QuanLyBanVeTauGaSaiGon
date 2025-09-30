package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [12:58:05 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.DonDatCho_DAO;
import dao.Ghe_DAO;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ghe;
import entity.Toa;
import entity.Ve;

public class DonDatCho_BUS {
    private final DonDatCho_DAO ddDAO = new DonDatCho_DAO();
    private final Ghe_DAO gheDAO = new Ghe_DAO();

    public Ve createHold(Toa toa, Ghe ghe) throws Exception {
//        Ve v = ddDAO.createHoldForSeat(toa.getToaID(), ghe.getGheID());
        return new Ve();
    }

    public boolean releaseHold(Ve v) throws Exception {
//        return ddDAO.releaseHoldByVeId(v.getVeID());
    	return true;
    }
}