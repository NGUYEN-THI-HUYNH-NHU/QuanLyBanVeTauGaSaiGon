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
	private String tenThucThe;
	private String thucTheID;
	private String thucHienBoi;
	private String loaiThaoTac;
	private LocalDateTime thoiGianThaoTac;
	private String chiTiet;
	
	public NhatKyAudit(String nhatKyAuditID, String tenThucThe, String thucTheID, String thucHienBoi,
			String loaiThaoTac, LocalDateTime thoiGianThaoTac, String chiTiet) {
		super();
		this.nhatKyAuditID = nhatKyAuditID;
		this.tenThucThe = tenThucThe;
		this.thucTheID = thucTheID;
		this.thucHienBoi = thucHienBoi;
		this.loaiThaoTac = loaiThaoTac;
		this.thoiGianThaoTac = thoiGianThaoTac;
		this.chiTiet = chiTiet;
	}

	public String getNhatKyAuditID() {
		return nhatKyAuditID;
	}

	public String getTenThucThe() {
		return tenThucThe;
	}

	public String getThucTheID() {
		return thucTheID;
	}

	public String getThucHienBoi() {
		return thucHienBoi;
	}

	public String getLoaiThaoTac() {
		return loaiThaoTac;
	}

	public LocalDateTime getThoiGianThaoTac() {
		return thoiGianThaoTac;
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

	public void setTenThucThe(String tenThucThe) {
		if(tenThucThe == null || tenThucThe.isEmpty()) {
			throw new IllegalArgumentException("Tên thực thể không được để trống!");
		}
		this.tenThucThe = tenThucThe;
	}

	public void setThucTheID(String thucTheID) {
		this.thucTheID = thucTheID;
	}

	public void setThucHienBoi(String thucHienBoi) {
		this.thucHienBoi = thucHienBoi;
	}

	public void setLoaiThaoTac(String loaiThaoTac) {
		this.loaiThaoTac = loaiThaoTac;
	}

	public void setThoiGianThaoTac(LocalDateTime thoiGianThaoTac) {
		if(thoiGianThaoTac == null) {
			throw new IllegalArgumentException("Thời gian thao tác không được để trống!");
		}
		this.thoiGianThaoTac = thoiGianThaoTac;
	}

	public void setChiTiet(String chiTiet) {
		this.chiTiet = chiTiet;
	}

	@Override
	public String toString() {
		return nhatKyAuditID + ";" + tenThucThe + ";" + thucTheID
				+ ";" + thucHienBoi + ";" + loaiThaoTac + ";"
				+ thoiGianThaoTac + ";" + chiTiet;
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
