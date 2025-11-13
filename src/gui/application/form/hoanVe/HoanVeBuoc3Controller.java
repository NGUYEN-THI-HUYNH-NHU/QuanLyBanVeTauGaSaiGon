package gui.application.form.hoanVe;

import java.util.List;

/*
 * @(#) PanelHoanVeBuoc3Controller.java  1.0  [3:07:04 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

public class HoanVeBuoc3Controller {

	private final PanelHoanVeBuoc3 panel;
	private ConfirmListener confirmListener;

	public interface ConfirmListener {
		void onConfirm();
	}

	public HoanVeBuoc3Controller(PanelHoanVeBuoc3 panel) {
		this.panel = panel;

		// Lắng nghe nút xác nhận
		this.panel.getBtnXacNhan().addActionListener(e -> {
			if (confirmListener != null) {
				confirmListener.onConfirm();
			}
		});
	}

	/**
	 * Được gọi bởi HoanVeController (Mediator) để hiển thị dữ liệu
	 */
	public void displayConfirmationData(List<VeHoanRow> selectedRows) {
		panel.displayConfirmation(selectedRows);
	}

	public void addConfirmListener(ConfirmListener listener) {
		this.confirmListener = listener;
	}
}
