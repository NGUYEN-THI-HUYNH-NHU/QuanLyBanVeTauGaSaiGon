package entity;
/*
 * @(#) NhatKyAudit.java  1.0  [3:47:30 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
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
	private String veID;
	private String nhanVienID;
	private LocalDateTime thoiDiemThaoTac;
	private String chiTiet;

	public NhatKyAudit(String nhatKyAuditID, String veID, String nhanVienID, LocalDateTime thoiDiemThaoTac, String chiTiet) {
		this.nhatKyAuditID = nhatKyAuditID;
		this.veID = veID;
		this.nhanVienID = nhanVienID;
		this.thoiDiemThaoTac = thoiDiemThaoTac;
		this.chiTiet = chiTiet;
	}

	public String getNhatKyAuditID() {
		return nhatKyAuditID;
	}

	public String getVeID() {
		return veID;
	}

	public String getNhanVienID() {
		return nhanVienID;
	}

	public LocalDateTime getThoiDiemThaoTac() {
		return thoiDiemThaoTac;
	}

	public String getChiTiet() {
		return chiTiet;
	}

	public void setNhatKyAuditID(String nhatKyAuditID) {
		if(nhatKyAuditID == null || nhatKyAuditID.isEmpty()) {
			throw new IllegalArgumentException("NhatKyAuditID không được để trống!");
		}
		this.nhatKyAuditID = nhatKyAuditID;
	}

	public void setVeID(String veID) {
		this.veID = veID;
	}

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
				+ veID + ";"
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
