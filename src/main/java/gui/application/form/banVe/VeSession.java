package gui.application.form.banVe;
/*
 * @(#) VeSession.java  1.0  [10:47:34 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dto.PhieuDungPhongVIPDTO;
import dto.PhieuGiuChoChiTietDTO;
import dto.VeDTO;
import entity.KhuyenMai;
import entity.SuDungKhuyenMai;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * VeSession — đại diện 1 dòng trong giỏ vé (chưa thanh toán). Lưu đủ thông tin
 * để hiển thị và để backend gọi hold/confirm sau này.
 */
@Setter
@Getter
public class VeSession {
    private final LocalDateTime thoiDiemHetHan;
    private VeDTO ve;
    private PhieuGiuChoChiTietDTO phieuGiuChoChiTiet;
    private PhieuDungPhongVIPDTO phieuDungPhongVIP;
    private double phiPhieuDungPhongChoVIP;
    private KhuyenMai khuyenMaiApDung = null;
    private double giamKM;
    private SuDungKhuyenMai suDungKhuyenMai;
    private double giamDoiTuong;

    public VeSession(VeDTO ve, KhuyenMai khuyenMaiApDung, int giamKM, LocalDateTime thoiDiemHetHan) {
        super();
        this.ve = ve;
        this.khuyenMaiApDung = khuyenMaiApDung;
        this.giamKM = giamKM;
        this.thoiDiemHetHan = thoiDiemHetHan;
    }

    public VeSession(VeDTO ve) {
        super();
        this.ve = ve;
        this.thoiDiemHetHan = null;
    }

    public int getSoGhe() {
        return this.ve.getSoGhe();
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
        return Objects.equals(ve.getChuyenID(), that.ve.getChuyenID())
                && Objects.equals(ve.getTenGaDi(), that.ve.getTenGaDi())
                && Objects.equals(ve.getTenGaDen(), that.ve.getTenGaDen())
                && Objects.equals(ve.getSoToa(), that.ve.getSoToa())
                && Objects.equals(ve.getSoGhe(), that.ve.getSoGhe());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ve.getChuyenID(), ve.getTenGaDi(), ve.getTenGaDen(),
                ve.getSoToa(), ve.getSoGhe());
    }

    @Override
    public String toString() {
        return ve.getTauID() + ";" + ve.getTenGaDi() + ";" + ve.getTenGaDen() + ";"
                + ve.getNgayGioDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ";"
                + ve.getHangToaID() + ";" + ve.getSoToa() + ";"
                + ve.getSoGhe();
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
                ve.getTauID(), ve.getTenGaDi(), ve.getTenGaDen(),
                ve.getNgayGioDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                ve.getHangToaID(), ve.getSoToa(), ve.getSoGhe(),
                formatter.format(ve.getGia()));
    }
}