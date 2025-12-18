package entity;
/*
 * @(#) NhatKyAudit.java  1.0  [3:47:30 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

public class NhatKyAudit {
	private String nhatKyAuditID;
	private String doiTuongID;
	private String nhanVienID;
	private LocalDateTime thoiDiemThaoTac;
	private String chiTiet;
	private String doiTuongThaoTac;

	public NhatKyAudit(String nhatKyAuditID, String doiTuongID, String nhanVienID, LocalDateTime thoiDiemThaoTac, String chiTiet,  String doiTuongThaoTac) {
		this.nhatKyAuditID = nhatKyAuditID;
		this.doiTuongID = doiTuongID;
		this.nhanVienID = nhanVienID;
		this.thoiDiemThaoTac = thoiDiemThaoTac;
		this.chiTiet = chiTiet;
		this.doiTuongThaoTac = doiTuongThaoTac;
	}

	public String getNhatKyAuditID() {
		return nhatKyAuditID;
	}

	public String getDoiTuongThaoTac() {
		return doiTuongThaoTac;
	}

	public String getNhanVienID() {
		return nhanVienID;
	}

	public LocalDateTime getThoiDiemThaoTac() {
		return thoiDiemThaoTac;
	}

	public String getDoiTuongID() {return doiTuongID;}

	public String getChiTiet() {
		return chiTiet;
	}

	public void setNhatKyAuditID(String nhatKyAuditID) {
		if(nhatKyAuditID == null || nhatKyAuditID.isEmpty()) {
			throw new IllegalArgumentException("NhatKyAuditID không được để trống!");
		}
		this.nhatKyAuditID = nhatKyAuditID;
	}

	public void setDoiTuongThaoTac(String doiTuongThaoTac) {
		this.doiTuongThaoTac = doiTuongThaoTac;
	}

	public void setDoiTuongID(String doiTuongID) {this.doiTuongID = doiTuongID;}

	public void setNhanVienID(String nhanVienID) {
		this.nhanVienID = nhanVienID;
	}

	public void setThoiDiemThaoTac(LocalDateTime thoiDiemThaoTac) {
		this.thoiDiemThaoTac = thoiDiemThaoTac;
	}

	public void setChiTiet(String chiTiet) {
		this.chiTiet = chiTiet;
	}

	@Override
	public String toString() {
		return nhanVienID + ";"
				+ doiTuongThaoTac + ";"
				+ doiTuongID + ";"
				+ nhanVienID + ";"
				+ thoiDiemThaoTac + ";"
				+ chiTiet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		NhatKyAudit that = (NhatKyAudit) o;
		return Objects.equals(getNhatKyAuditID(), that.getNhatKyAuditID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getNhatKyAuditID());
	}
}
