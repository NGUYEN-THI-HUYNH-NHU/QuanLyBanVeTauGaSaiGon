package entity.type;
/*
 * @(#) TrangThaiPDPVIP.java  1.0  [10:37:35 AM] Oct 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 13, 2025
 * @version: 1.0
 */

public enum TrangThaiPDPVIP {
	CHUA_DUNG("Phiếu chưa sử dụng"), DA_DUNG("Phiếu đã sử dụng"), HET_HAN("Phiếu hết hạn"),
	DA_HUY("Phiếu đã hoàn theo vé hoàn");

	private final String description;

	TrangThaiPDPVIP(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}