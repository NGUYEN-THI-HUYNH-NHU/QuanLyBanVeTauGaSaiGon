package gui.application.form.banVe;
/*
 * @(#) SessionVe.java  1.0  [10:14:43 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.Ve;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class SessionVe {
	private Ve selectedTicket;
	private String trainId;
	private String seatId;
	private String customerName;
	
	
	public Ve getSelectedTicket() {
		return selectedTicket;
	}
	
	
	public void setSelectedTicket(Ve selectedTicket) {
		this.selectedTicket = selectedTicket;
	}
	
	
	public String getTrainId() {
		return trainId;
	}
	
	
	public void setTrainId(String trainId) {
		this.trainId = trainId;
	}
	
	
	public String getSeatId() {
		return seatId;
	}
	
	
	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}
	
	
	public String getCustomerName() {
		return customerName;
	}
	
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}