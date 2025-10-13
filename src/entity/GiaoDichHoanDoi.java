package entity;
/*
 * @(#) GiaoDichHoanDoi.java  1.0  [1:59:59 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */
import java.time.LocalDateTime;
import java.util.Objects;

public class GiaoDichHoanDoi {
	private String giaoDichHoanDoiID;
	private NhanVien nhanVien;
	private HoaDon hoaDon;
	private Ve veGoc;
	private Ve veMoi;
	private String loaiGiaoDich;
	private String lyDo;
	private LocalDateTime thoiDiemGiaoDich;
	private double phiHoanDoi;
	private double soTienChenhLech;

	public GiaoDichHoanDoi(String giaoDichHoanDoiID, NhanVien nhanVien, HoaDon hoaDon, Ve veGoc, Ve veMoi,
			String loaiGiaoDich, String lyDo, LocalDateTime thoiDiemGiaoDich, double phiHoanDoi,
			double soTienChenhLech) {
		super();
		this.giaoDichHoanDoiID = giaoDichHoanDoiID;
		this.nhanVien = nhanVien;
		this.hoaDon = hoaDon;
		this.veGoc = veGoc;
		this.veMoi = veMoi;
		this.loaiGiaoDich = loaiGiaoDich;
		this.lyDo = lyDo;
		this.thoiDiemGiaoDich = thoiDiemGiaoDich;
		this.phiHoanDoi = phiHoanDoi;
		this.soTienChenhLech = soTienChenhLech;
	}

	public String getGiaoDichHoanDoiID() {
		return giaoDichHoanDoiID;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public HoaDon getHoaDon() {
		return hoaDon;
	}

	public Ve getVeGoc() {
		return veGoc;
	}

	public Ve getVeMoi() {
		return veMoi;
	}

	public String getLoaiGiaoDich() {
		return loaiGiaoDich;
	}

	public String getLyDo() {
		return lyDo;
	}

	public LocalDateTime getThoiDiemGiaoDich() {
		return thoiDiemGiaoDich;
	}

	public double getPhiHoanDoi() {
		return phiHoanDoi;
	}

	public double getSoTienChenhLech() {
		return soTienChenhLech;
	}
	
	public void setGiaoDichHoanDoiID(String giaoDichHoanDoiID) {
		if(giaoDichHoanDoiID != null && !giaoDichHoanDoiID.trim().isEmpty()) {
			this.giaoDichHoanDoiID = giaoDichHoanDoiID;
		}else{
			throw new IllegalArgumentException("Giao dịch hoàn đổi vé ID không được để trống!");
		}
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public void setVeGoc(Ve veGoc) {
		this.veGoc = veGoc;
	}

	public void setVeMoi(Ve veMoi) {
		this.veMoi = veMoi;
	}

	public void setLoaiGiaoDich(String loaiGiaoDich) {
		this.loaiGiaoDich = loaiGiaoDich;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}

	public void setThoiDiemGiaoDich(LocalDateTime thoiDiemGiaoDich) {
		if(thoiDiemGiaoDich == null || thoiDiemGiaoDich.isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("Ngày yêu cầu không được rỗng và phải là ngày trong quá khứ hoặc ngày hiện tại");
		}
		this.thoiDiemGiaoDich = thoiDiemGiaoDich;
	}

	public void setPhiHoanDoi(double phiHoanDoi) {
		this.phiHoanDoi = phiHoanDoi;
	}

	public void setSoTienChenhLech(double soTienChenhLech) {
		this.soTienChenhLech = soTienChenhLech;
	}

	public void setNhanVien(NhanVien nhanVien) {
		if(nhanVien != null) {
			this.nhanVien = nhanVien;
		}else{
			throw new IllegalArgumentException("Nhân viên không được rỗng");
		}
	}
	

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GiaoDichHoanDoi that = (GiaoDichHoanDoi) o;
		return Objects.equals(getGiaoDichHoanDoiID(), that.getGiaoDichHoanDoiID());
	}

	@Override
	public String toString() {
		return giaoDichHoanDoiID + ";" + nhanVien.getNhanVienID() + ";"
				+ hoaDon.getHoaDonID() + veGoc.getVeID() + ";" + veMoi.getVeID() + ";" + loaiGiaoDich + ";" + lyDo
				+ ";" + thoiDiemGiaoDich + ";" + phiHoanDoi + ";" + soTienChenhLech;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getGiaoDichHoanDoiID());
	}
}
