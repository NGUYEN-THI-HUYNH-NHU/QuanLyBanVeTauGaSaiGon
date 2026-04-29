/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/29/2026
 */

package entity;

public class GiaoDichThanhToan {
    private double tienNhan;
    private double tienHoan;
    private String maGD;
    private double tongTien;
    private boolean isThanhToanTienMat;
    private boolean trangThai;

    public GiaoDichThanhToan(double tienNhan, double tienHoan, double tongTien, boolean isThanhToanTienMat,
                             boolean trangThai) {
        super();
        this.tienNhan = tienNhan;
        this.tienHoan = tienHoan;
        this.tongTien = tongTien;
        this.isThanhToanTienMat = isThanhToanTienMat;
        this.trangThai = trangThai;
    }

    public GiaoDichThanhToan(double tienNhan, String maGD, double tongTien, boolean isThanhToanTienMat,
                             boolean trangThai) {
        super();
        this.tienNhan = tienNhan;
        this.tongTien = tongTien;
        this.isThanhToanTienMat = isThanhToanTienMat;
        this.trangThai = trangThai;
    }

    public GiaoDichThanhToan() {
        super();
    }

    public double getTienNhan() {
        return tienNhan;
    }

    public void setTienNhan(double tienNhan) {
        this.tienNhan = tienNhan;
    }

    public String getMaGD() {
        return maGD;
    }

    public void setMaGD(String maGD) {
        this.maGD = maGD;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public double getTienHoan() {
        return tienHoan;
    }

    public void setTienHoan(double tienHoan) {
        this.tienHoan = tienHoan;
    }

    public boolean isThanhToanTienMat() {
        return isThanhToanTienMat;
    }

    public void setThanhToanTienMat(boolean thanhToanTienMat) {
        isThanhToanTienMat = thanhToanTienMat;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tienNhan + ";" + tienHoan + ";" + maGD + ";" + tongTien + ";" + isThanhToanTienMat + ";" + trangThai;
    }
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) {
//			return true;
//		}
//		if (o == null || getClass() != o.getClass()) {
//			return false;
//		}
//		GiaoDichThanhToan that = (GiaoDichThanhToan) o;
//		return Objects.equals(giaoDichThanhToanID, that.giaoDichThanhToanID);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(giaoDichThanhToanID);
//	}
}