package gui.application.form.banVe;
/*
 * @(#) SelectedTicket.java  1.0  [10:47:34 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 30, 2025
 * @version: 1.0
 */
import java.time.Instant;
import java.util.Objects;

/**
 * SelectedTicket — đại diện 1 dòng trong giỏ vé (chưa thanh toán).
 * Lưu đủ thông tin để hiển thị và để backend gọi hold/confirm sau này.
 */
public class SelectedTicket {
    private final String chuyenId;
    private final String tenTau;
    private final String gaDiId;
    private final String tenGaDi;
    private final String gaDenId;
    private final String tenGaDen;

    private final String toaID;
    private final String soToa;
    private final String gheID;
    private final String soGhe;
    private final double gia;
    private Instant thoiDiemHetHan;

    public SelectedTicket(String chuyenId, String tenTau,
                          String gaDiId, String tenGaDi, String gaDenId, String tenGaDen,
                          String toaID, String soToa,
                          String gheID, String soGhe,
                          long gia, Instant thoiDiemHetHan) {
        this.chuyenId = chuyenId;
        this.tenTau = tenTau;
        this.gaDiId = gaDiId;
        this.tenGaDi = tenGaDi;
        this.gaDenId = gaDenId;
        this.tenGaDen = tenGaDen;
        this.toaID = toaID;
        this.soToa = soToa;
        this.gheID = gheID;
        this.soGhe = soGhe;
        this.gia = gia;
        this.thoiDiemHetHan = thoiDiemHetHan;
    }

    public String getChuyenId() {
    	return chuyenId;
    }
    public String getTrainName() {
    	return tenTau;
    }
    public String getGaDiId() {
    	return gaDiId;
    }
    public String getGaDiName() {
    	return tenGaDi;
    }
    public String getGaDenId() {
    	return gaDenId;
    }
    public String getGaDenName() {
    	return tenGaDen;
    }
    public String getCoachId() {
    	return toaID;
    }
    public String getCoachCode() {
    	return soToa;
    }
    public String getSeatId() {
    	return gheID;
    }
    public String getSeatLabel() {
    	return soGhe;
    }
    public double getPrice() {
    	return gia;
    }
    public Instant getHoldExpiresAt() {
    	return thoiDiemHetHan;
    }
    public void setHoldExpiresAt(Instant thoiDiemHetHan) {
    	this.thoiDiemHetHan = thoiDiemHetHan;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectedTicket)) return false;
        SelectedTicket that = (SelectedTicket) o;
        return Objects.equals(chuyenId, that.chuyenId) &&
                Objects.equals(toaID, that.toaID) &&
                Objects.equals(gheID, that.gheID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chuyenId, toaID, gheID);
    }

    @Override
    public String toString() {
        return "SelectedTicket{" +
                "chuyenId='" + chuyenId + '\'' +
                ", soToa='" + soToa + '\'' +
                ", soGhe='" + soGhe + '\'' +
                '}';
    }

    public boolean isHoldExpired() {
        if (thoiDiemHetHan == null) return false;
        return Instant.now().isAfter(thoiDiemHetHan);
    }
}