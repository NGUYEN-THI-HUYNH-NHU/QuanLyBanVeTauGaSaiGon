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

import entity.LoaiDoiTuong;
import entity.type.LoaiDoiTuongEnums;

public class PassengerRow {
    private final VeSession veSession;
    private String soGiayTo = "";
    private String hoTen = "";
    private LoaiDoiTuong loaiDoiTuong = new LoaiDoiTuong(LoaiDoiTuongEnums.NGUOI_LON.name());

    public PassengerRow(VeSession v) {
        this.veSession = v;
    }

    public VeSession getVeSession() {
        return veSession;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LoaiDoiTuong getLoaiDoiTuong() {
        return loaiDoiTuong;
    }

    public void setLoaiDoiTuong(LoaiDoiTuong loaiDoiTuong) {
        this.loaiDoiTuong = loaiDoiTuong;
    }

    public String getSoGiayTo() {
        return soGiayTo;
    }

    public void setSoGiayTo(String soGiayTo) {
        this.soGiayTo = soGiayTo;
    }

    public double getTotal() {
        return veSession.getVe().getGia() - veSession.getGiamKM();
    }
}