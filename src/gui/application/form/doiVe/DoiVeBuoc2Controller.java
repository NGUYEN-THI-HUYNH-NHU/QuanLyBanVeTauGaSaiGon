package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc2Controller.java  1.0  [5:33:04 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.util.List;

import entity.KhachHang;
import entity.PhieuDungPhongVIP;
import entity.Ve;

public class DoiVeBuoc2Controller {
	private PanelDoiVeBuoc2 panel;

	protected interface ContinueListener {
		void onContinue(List<VeDoiRow> selectedRows);
	}

	private ContinueListener continueListener;

	public DoiVeBuoc2Controller(PanelDoiVeBuoc2 panel) {
		this.panel = panel;

		this.panel.getBtnTiepTuc().addActionListener(e -> {
			// Lấy danh sách các dòng được chọn từ View
			List<VeDoiRow> selected = panel.getSelectedVeDoiRows();

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
	public void disPlayDonDatCho(List<Ve> listVe, List<PhieuDungPhongVIP> listPhieu, KhachHang khachHang) {
		panel.showDonDatCho(listVe, listPhieu, khachHang);
	}

	public void addContinueListener(ContinueListener listener) {
		this.continueListener = listener;
	}

	public void refreshRowDisplay(VeDoiRow row) {
		panel.refreshRow(row);
	}
}
