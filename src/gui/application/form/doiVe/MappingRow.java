package gui.application.form.doiVe;
/*
 * @(#) MappingRow.java  1.0  [6:45:43 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */
import gui.application.form.banVe.VeSession;

public class MappingRow {
	private VeDoiRow veCu; // Vé cũ (Đã có thông tin lệ phí, hành khách)
	private VeSession veMoi; // Vé mới (Có thể null nếu chưa chọn)

	public MappingRow(VeDoiRow veCu, VeSession veMoi) {
		this.veCu = veCu;
		this.veMoi = veMoi;
	}

	public VeDoiRow getVeCu() {
		return veCu;
	}

	public VeSession getVeMoi() {
		return veMoi;
	}

	public void setVeMoi(VeSession veMoi) {
		this.veMoi = veMoi;
	}

	// Tính tiền chênh lệch: (Giá vé mới + Phí đổi) - Giá vé cũ
	public double getChenhLech() {
		double giaMoi = (veMoi != null) ? veMoi.getVe().getGia() : 0;
		double giaCu = veCu.getVe().getGia();
		double phi = veCu.getLePhiDoiVe();
		return (giaMoi + phi) - giaCu;
	}
}