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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * SelectedTicket — đại diện 1 dòng trong giỏ vé (chưa thanh toán).
 * Lưu đủ thông tin để hiển thị và để backend gọi hold/confirm sau này.
 */
//public class VeSession {
//    private final String chuyenId;
//    private final String tenTau;
//    private final String gaDiId;
//    private final String tenGaDi;
//    private final String gaDenId;
//    private final String tenGaDen;
//
//    private final String toaID;
//    private final String soToa;
//    private final String gheID;
//    private final String soGhe;
//    private final double gia;
//    private Instant thoiDiemHetHan;
//
//    public VeSession(String chuyenId, String tenTau,
//                          String gaDiId, String tenGaDi, String gaDenId, String tenGaDen,
//                          String toaID, String soToa,
//                          String gheID, String soGhe,
//                          long gia, Instant thoiDiemHetHan) {
//        this.chuyenId = chuyenId;
//        this.tenTau = tenTau;
//        this.gaDiId = gaDiId;
//        this.tenGaDi = tenGaDi;
//        this.gaDenId = gaDenId;
//        this.tenGaDen = tenGaDen;
//        this.toaID = toaID;
//        this.soToa = soToa;
//        this.gheID = gheID;
//        this.soGhe = soGhe;
//        this.gia = gia;
//        this.thoiDiemHetHan = thoiDiemHetHan;
//    }
//
//       
//    public String getChuyenId() {
//		return chuyenId;
//    }
//	public String getTenTau() {
//		return tenTau;
//	}
//	public String getGaDiId() {
//		return gaDiId;
//	}
//	public String getTenGaDi() {
//		return tenGaDi;
//	}
//	public String getGaDenId() {
//		return gaDenId;
//	}
//	public String getTenGaDen() {
//		return tenGaDen;
//	}
//	public String getToaID() {
//		return toaID;
//	}
//	public String getSoToa() {
//		return soToa;
//	}
//	public String getGheID() {
//		return gheID;
//	}
//	public String getSoGhe() {
//		return soGhe;
//	}
//	public double getGia() {
//		return gia;
//	}
//	public Instant getThoiDiemHetHan() {
//		return thoiDiemHetHan;
//	}
//    
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) 
//        	return true;
//        if (!(o instanceof VeSession)) 
//        	return false;
//        VeSession that = (VeSession) o;
//        return Objects.equals(chuyenId, that.chuyenId) &&
//                Objects.equals(toaID, that.toaID) &&
//                Objects.equals(gheID, that.gheID);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(chuyenId, toaID, gheID);
//    }
//
//    @Override
//    public String toString() {
//        return chuyenId + ";" + soToa + ";" + soGhe;
//    }
//
//    public boolean isHoldExpired() {
//        if (thoiDiemHetHan == null) return false;
//        return Instant.now().isAfter(thoiDiemHetHan);
//    }
//}
public class VeSession {
	private final String chuyenID;
    private final String tenTau;
    private final String tenGaDi;
    private final String tenGaDen;
    private final LocalDate ngayDi;
    private final LocalTime gioDi;
    private final String toaID;
    private final String soToa;
    private final String soGhe;
    private final Instant thoiDiemHetHan;

    public VeSession(String chuyenID, String tenTau, String tenGaDi, String tenGaDen, LocalDate ngayDi, LocalTime gioDi, String toaID,
			String soToa, String soGhe, Instant thoiDiemHetHan) {
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

	public String getSoToa() {
		return soToa;
	}

	public String getSoGhe() {
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
                Objects.equals(soToa, that.soGhe) &&
                Objects.equals(soGhe, that.soGhe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chuyenID, soToa, soGhe);
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
}