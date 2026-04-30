package gui.application.form.banVe;
/*
 * @(#) VeSession.java  1.0  [10:47:34 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import entity.*;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * VeSession — đại diện 1 dòng trong giỏ vé (chưa thanh toán). Lưu đủ thông tin
 * để hiển thị và để backend gọi hold/confirm sau này.
 */
public class VeSession {
    private final LocalDateTime thoiDiemHetHan;
    private Ve ve;
    private PhieuGiuChoChiTiet phieuGiuChoChiTiet;
    private PhieuDungPhongVIP phieuDungPhongVIP;
    private int phiPhieuDungPhongChoVIP;
    private KhuyenMai khuyenMaiApDung = null;
    private int giamKM;
    private SuDungKhuyenMai suDungKhuyenMai;
    private int giamDoiTuong;
    private boolean isHanhKhachMoi = true;

    public VeSession(Ve ve, KhuyenMai khuyenMaiApDung, int giamKM, LocalDateTime thoiDiemHetHan) {
        super();
        this.ve = ve;
        this.khuyenMaiApDung = khuyenMaiApDung;
        this.giamKM = giamKM;
        this.thoiDiemHetHan = thoiDiemHetHan;
    }

    public VeSession(Ve ve) {
        super();
        this.ve = ve;
        this.thoiDiemHetHan = null;
    }

    public int getGiamDoiTuong() {
        return giamDoiTuong;
    }

    public void setGiamDoiTuong(int giamDoiTuong) {
        this.giamDoiTuong = giamDoiTuong;
    }

    public LocalDateTime getThoiDiemHetHan() {
        return thoiDiemHetHan;
    }

    public PhieuGiuChoChiTiet getPhieuGiuChoChiTiet() {
        return phieuGiuChoChiTiet;
    }

    public void setPhieuGiuChoChiTiet(PhieuGiuChoChiTiet phieuGiuChoChiTiet) {
        this.phieuGiuChoChiTiet = phieuGiuChoChiTiet;
    }

    public Ve getVe() {
        return ve;
    }

    public void setVe(Ve ve) {
        this.ve = ve;
    }

    public PhieuDungPhongVIP getPhieuDungPhongVIP() {
        return phieuDungPhongVIP;
    }

    public void setPhieuDungPhongVIP(PhieuDungPhongVIP phieuDungPhongVIP) {
        this.phieuDungPhongVIP = phieuDungPhongVIP;
    }

    public boolean isHanhKhachMoi() {
        return isHanhKhachMoi;
    }

    public void setHanhKhachMoi(boolean isHanhKhachMoi) {
        this.isHanhKhachMoi = isHanhKhachMoi;
    }

    public int getPhiPhieuDungPhongChoVIP() {
        return phiPhieuDungPhongChoVIP;
    }

    public void setPhiPhieuDungPhongChoVIP(int phiPhieuDungPhongChoVIP) {
        this.phiPhieuDungPhongChoVIP = phiPhieuDungPhongChoVIP;
    }

    public int getGiamKM() {
        return giamKM;
    }

    public void setGiamKM(int giamKM) {
        this.giamKM = giamKM;
    }

    public KhuyenMai getKhuyenMaiApDung() {
        return khuyenMaiApDung;
    }

    public void setKhuyenMaiApDung(KhuyenMai khuyenMaiApDung) {
        this.khuyenMaiApDung = khuyenMaiApDung;
    }

    public int getSoGhe() {
        return this.ve.getGhe().getSoGhe();
    }

    public SuDungKhuyenMai getSuDungKhuyenMai() {
        return suDungKhuyenMai;
    }

    public void setSuDungKhuyenMai(SuDungKhuyenMai suDungKhuyenMai) {
        this.suDungKhuyenMai = suDungKhuyenMai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VeSession)) {
            return false;
        }
        VeSession that = (VeSession) o;
        return Objects.equals(ve.getChuyen().getChuyenID(), that.ve.getChuyen().getChuyenID())
                && Objects.equals(ve.getGaDi().getTenGa(), that.ve.getGaDi().getTenGa())
                && Objects.equals(ve.getGaDen().getTenGa(), that.ve.getGaDen().getTenGa())
                && Objects.equals(ve.getGhe().getToa().getSoToa(), that.ve.getGhe().getToa().getSoToa())
                && Objects.equals(ve.getGhe().getSoGhe(), that.ve.getGhe().getSoGhe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ve.getChuyen().getChuyenID(), ve.getGaDi().getTenGa(), ve.getGaDen().getTenGa(),
                ve.getGhe().getToa().getSoToa(), ve.getGhe().getSoGhe());
    }

    @Override
    public String toString() {
        return ve.getChuyen().getTau().getTauID() + ";" + ve.getGaDi().getTenGa() + ";" + ve.getGaDen().getTenGa() + ";"
                + ve.getNgayGioDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ";"
                + ve.getGhe().getToa().getHangToa() + ";" + ve.getGhe().getToa().getSoToa() + ";"
                + ve.getGhe().getSoGhe();
    }

    public boolean isHoldExpired() {
        if (thoiDiemHetHan == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(thoiDiemHetHan);
    }

    public String prettyString() {
        DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
        return String.format("<html><b>%s</b> %s - %s<br/>%s<br/>%s Toa %s Chỗ %s<br/>Giá: <b>%s</b></html>",
                ve.getChuyen().getTau().getTauID(), ve.getGaDi().getTenGa(), ve.getGaDen().getTenGa(),
                ve.getNgayGioDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                ve.getGhe().getToa().getHangToa(), ve.getGhe().getToa().getSoToa(), ve.getGhe().getSoGhe(),
                formatter.format(ve.getGia()));
    }
}