package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc2Controller.java  1.0  [3:06:46 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */
import java.util.List;

import entity.KhachHang;
import entity.Ve;

public class HoanVeBuoc2Controller {
	private PanelHoanVeBuoc2 panel;

	protected interface ContinueListener {
		void onContinue(List<VeHoanRow> selectedRows);
	}

	private ContinueListener continueListener;

	public HoanVeBuoc2Controller(PanelHoanVeBuoc2 panel) {
		this.panel = panel;

		this.panel.getBtnTiepTuc().addActionListener(e -> {
			// Lấy danh sách các dòng được chọn từ View
			List<VeHoanRow> selected = panel.getSelectedVeHoanRows();

			if (selected.isEmpty()) {
				// (Thông báo lỗi nếu chưa chọn vé nào)
				return;
			}

			// Phát sự kiện cho Mediator
			if (continueListener != null) {
				continueListener.onContinue(selected);
			}
		});
	}

	/**
	 * @param khachHang
	 * @param listVe
	 * 
	 */
	public void disPlayDonDatCho(List<Ve> listVe, KhachHang khachHang) {
		panel.showDonDatCho(listVe, khachHang);
	}

	public void addContinueListener(ContinueListener listener) {
		this.continueListener = listener;
	}

	public void refreshRowDisplay(VeHoanRow row) {
		panel.refreshRow(row);
	}
}
