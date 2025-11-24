package gui.application.form.hoanVe;
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
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

public class HoanVeBuoc3Controller {

	private final PanelHoanVeBuoc3 panel;

	protected interface RowSelectionChangeListener {
		void onRowSelectionChanged(VeHoanRow row);
	}

	private RowSelectionChangeListener selectionChangeListener;

	protected interface ConfirmListener {
		void onConfirm();
	}

	private ConfirmListener confirmListener;

	public HoanVeBuoc3Controller(PanelHoanVeBuoc3 panel) {
		this.panel = panel;

		// Lắng nghe thay đổi trên row
		this.panel.addRowSelectionListener(new Consumer<VeHoanRow>() {
			@Override
			public void accept(VeHoanRow row) {
				// Báo sự kiện này lên cho Mediator
				if (JOptionPane.showConfirmDialog(panel, "Bạn xác nhận bỏ chọn hoàn vé này?", "Lưu ý",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					panel.removeRow(row);
					if (selectionChangeListener != null) {
						selectionChangeListener.onRowSelectionChanged(row);
					}
				}
			}
		});

		// Lắng nghe nút xác nhận
		this.panel.getBtnXacNhan().addActionListener(e -> {
			// Báo sự kiện này lên cho Mediator
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

	public void addRowSelectionChangeListener(RowSelectionChangeListener listener) {
		this.selectionChangeListener = listener;
	}

	public void addConfirmListener(ConfirmListener listener) {
		this.confirmListener = listener;
	}
}
