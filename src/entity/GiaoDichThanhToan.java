package entity;

public class GiaoDichThanhToan {
	private double tienNhan;
	private double tienHoan;
	private String maGD;
	private double tongTien;
	private boolean isThanhToanTienMat;
	private boolean trangThai;

	public GiaoDichThanhToan(double tienNhan, double tienHoan, String maGD, double tongTien, boolean isThanhToanTienMat,
			boolean trangThai) {
		super();
		this.tienNhan = tienNhan;
		this.tienHoan = tienHoan;
		this.maGD = maGD;
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

	public String getMaGD() {
		return maGD;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setMaGD(String maGD) {
		this.maGD = maGD;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public double getTienHoan() {
		return tienHoan;
	}

	public boolean isThanhToanTienMat() {
		return isThanhToanTienMat;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTienNhan(double tienNhan) {
		this.tienNhan = tienNhan;
	}

	public void setTienHoan(double tienHoan) {
		this.tienHoan = tienHoan;
	}

	public void setThanhToanTienMat(boolean thanhToanTienMat) {
		isThanhToanTienMat = thanhToanTienMat;
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