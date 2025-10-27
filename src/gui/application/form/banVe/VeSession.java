package gui.application.form.banVe;
/*
 * @(#) VeSession.java  1.0  [10:47:34 AM] Sep 30, 2025
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.swing.JLabel;

/**
 * VeSession — đại diện 1 dòng trong giỏ vé (chưa thanh toán).
 * Lưu đủ thông tin để hiển thị và để backend gọi hold/confirm sau này.
 */
public class VeSession {
	private final String chuyenID;
    private final String tenTau;
    private final String tenGaDi;
    private final String tenGaDen;
    private final LocalDate ngayDi;
    private final LocalTime gioDi;
    private final String toaID;
    private final int soToa;
    private final int soGhe;
    private final Instant thoiDiemHetHan;

    public VeSession(String chuyenID, String tenTau, String tenGaDi, String tenGaDen, LocalDate ngayDi, LocalTime gioDi, String toaID,
			int soToa, int soGhe, Instant thoiDiemHetHan) {
		super();
		this.chuyenID = chuyenID;
		this.tenTau = tenTau;
		this.tenGaDi = tenGaDi;
		this.tenGaDen = tenGaDen;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
		this.toaID = toaID;
		this.soToa = soToa;
		this.soGhe = soGhe;
		this.thoiDiemHetHan = thoiDiemHetHan;
	}
    
	public String getTenTau() {
		return tenTau;
	}

	public String getTenGaDi() {
		return tenGaDi;
	}

	public String getTenGaDen() {
		return tenGaDen;
	}

	public LocalDate getNgayDi() {
		return ngayDi;
	}

	public LocalTime getGioDi() {
		return gioDi;
	}

	public String getToaID() {
		return toaID;
	}

	public int getSoToa() {
		return soToa;
	}

	public int getSoGhe() {
		return soGhe;
	}

	public Instant getThoiDiemHetHan() {
		return thoiDiemHetHan;
	}
    
    @Override
    public boolean equals(Object o) {
        if (this == o) 
        	return true;
        if (!(o instanceof VeSession)) 
        	return false;
        VeSession that = (VeSession) o;
        return Objects.equals(chuyenID, that.chuyenID) &&
                Objects.equals(tenGaDi, that.tenGaDi) &&
                Objects.equals(tenGaDen, that.tenGaDen) &&
                Objects.equals(soToa, that.soToa) &&
                Objects.equals(soGhe, that.soGhe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chuyenID, tenGaDi, tenGaDen, soToa, soGhe);
    }

    @Override
    public String toString() {
        return tenTau + ";" + tenGaDi + ";" + tenGaDen + ";"
        		+ ngayDi.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ";"
        		+ gioDi.format(DateTimeFormatter.ofPattern("hh:mm")) + ";"
        		+ toaID + ";" + soToa + ";" + soGhe;
    }

    public boolean isHoldExpired() {
        if (thoiDiemHetHan == null) return false;
        return Instant.now().isAfter(thoiDiemHetHan);
    }
    
	public String prettyString() {
		return String.format("<html><b>%s</b> %s-%s<br/>%s %s<br/>%s toa %s chỗ %s</html>",
				getTenTau(), getTenGaDi(), getTenGaDen(),
				getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				getGioDi().format(DateTimeFormatter.ofPattern("HH:mm")), getToaID(), getSoToa(), getSoGhe());
	}
}