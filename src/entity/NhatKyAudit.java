package entity;
/*
 * @(#) NhatKyAudit.java  1.0  [3:47:30 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

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

	@Override
	public String toString() {
		return nhatKyAuditID + ";" + tenThucThe + ";" + thucTheID
				+ ";" + thucHienBoi + ";" + loaiThaoTac + ";"
				+ thoiGianThaoTac + ";" + chiTiet;
	}
}
