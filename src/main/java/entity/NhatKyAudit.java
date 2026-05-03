package entity;
/*
 * @(#) NhatKyAudit.java  1.0  [3:47:30 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "NhatKyAudit")
@Setter
@Getter
@NoArgsConstructor
@Builder
public class NhatKyAudit {
	@Id
	@Column(name = "nhatKyID", length = 50)
	private String nhatKyAuditID;

	@Column(name = "doiTuongID", length = 50)
	private String doiTuongID;

	@Column(name = "nhanVienID", length = 50)
	private String nhanVienID;

	@Column(name = "thoiDiemThaoTac")
	private LocalDateTime thoiDiemThaoTac;

	@Enumerated(EnumType.STRING)
	@Column(name = "loaiThaoTac", length = 50)
	private entity.type.NhatKyAudit loaiThaoTac;

	@Column(name = "chiTiet", columnDefinition = "NVARCHAR(MAX)")
	private String chiTiet;

	@Column(name = "doiTuongThaoTac", length = 100)
	private String doiTuongThaoTac;

	public NhatKyAudit(String nhatKyAuditID, String doiTuongID, String nhanVienID, LocalDateTime thoiDiemThaoTac,
					   entity.type.NhatKyAudit loaiThaoTac, String chiTiet, String doiTuongThaoTac) {
		this.nhatKyAuditID = nhatKyAuditID;
		this.doiTuongID = doiTuongID;
		this.nhanVienID = nhanVienID;
		this.thoiDiemThaoTac = thoiDiemThaoTac;
		this.loaiThaoTac = loaiThaoTac;
		this.chiTiet = chiTiet;
		this.doiTuongThaoTac = doiTuongThaoTac;
	}

	@Override
	public String toString() {
		return nhanVienID + ";" + doiTuongThaoTac + ";" + doiTuongID + ";" + nhanVienID + ";" + thoiDiemThaoTac + ";"
				+ loaiThaoTac + ";" + chiTiet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NhatKyAudit that = (NhatKyAudit) o;
		return Objects.equals(getNhatKyAuditID(), that.getNhatKyAuditID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getNhatKyAuditID());
	}
}
