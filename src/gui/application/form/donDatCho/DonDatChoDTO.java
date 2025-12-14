package gui.application.form.donDatCho;
/*
 * @(#) ViewDonDatCho.java  1.0  [12:46:41 PM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */
import java.util.Objects;

import entity.DonDatCho;

public class DonDatChoDTO {
	private DonDatCho donDatCho;
	private int tongSoVe;
	private int soVeHoan;
	private int soVeDoi;

	public DonDatChoDTO(DonDatCho donDatCho, int tongSoVe, int soVeHoan, int soVeDoi) {
		super();
		this.donDatCho = donDatCho;
		this.tongSoVe = tongSoVe;
		this.soVeHoan = soVeHoan;
		this.soVeDoi = soVeDoi;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DonDatCho donDatCho = (DonDatCho) o;
		return Objects.equals(donDatCho.getDonDatChoID(), donDatCho.getDonDatChoID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(donDatCho.getDonDatChoID());
	}

	public int getTongSoVe() {
		return tongSoVe;
	}

	public int getSoVeHoan() {
		return soVeHoan;
	}

	public int getSoVeDoi() {
		return soVeDoi;
	}

	public DonDatCho getDonDatCho() {
		return donDatCho;
	}

	public void setDonDatCho(DonDatCho donDatCho) {
		this.donDatCho = donDatCho;
	}

	public void setTongSoVe(int tongSoVe) {
		this.tongSoVe = tongSoVe;
	}

	public void setSoVeHoan(int soVeHoan) {
		this.soVeHoan = soVeHoan;
	}

	public void setSoVeDoi(int soVeDoi) {
		this.soVeDoi = soVeDoi;
	}
}
