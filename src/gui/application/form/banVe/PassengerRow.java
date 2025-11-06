package gui.application.form.banVe;

/*
 * @(#) HanhKhachSession.java  1.0  [8:24:53 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */
import entity.type.LoaiDoiTuong;

public class PassengerRow {
	private String fullName = "";
	private LoaiDoiTuong type = LoaiDoiTuong.NGUOI_LON;
	private String idNumber = "";
	private final VeSession veSession;

	public PassengerRow(VeSession v) {
		this.veSession = v;
	}

	public VeSession getVeSession() {
		return veSession;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public LoaiDoiTuong getType() {
		return type;
	}

	public void setType(LoaiDoiTuong type) {
		this.type = type;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

// public void setPrice(double price) { this.price = price; recalcTotal(); }

// public void setDiscount(double discount) { this.discount = discount; recalcTotal(); }

	public double getTotal() {
		return veSession.getGia() - veSession.getGiam();
	}

// private void recalcTotal() {
//     this.total = Math.max(0.0, this.price - this.discount);
// }
}