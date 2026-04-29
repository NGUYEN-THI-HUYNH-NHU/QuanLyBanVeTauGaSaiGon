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

import entity.DonDatCho;

import java.util.Objects;

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
        return Objects.equals(donDatCho.getId(), donDatCho.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(donDatCho.getId());
    }

    public int getTongSoVe() {
        return tongSoVe;
    }

    public void setTongSoVe(int tongSoVe) {
        this.tongSoVe = tongSoVe;
    }

    public int getSoVeHoan() {
        return soVeHoan;
    }

    public void setSoVeHoan(int soVeHoan) {
        this.soVeHoan = soVeHoan;
    }

    public int getSoVeDoi() {
        return soVeDoi;
    }

    public void setSoVeDoi(int soVeDoi) {
        this.soVeDoi = soVeDoi;
    }

    public DonDatCho getDonDatCho() {
        return donDatCho;
    }

    public void setDonDatCho(DonDatCho donDatCho) {
        this.donDatCho = donDatCho;
    }
}
