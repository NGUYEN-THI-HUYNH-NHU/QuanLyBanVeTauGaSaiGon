package gui.application.form.hoaDon;

/*
 * @(#) HoaDonTableButtonRenderer.java  1.0  [3:55:46 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class HoaDonTableButtonRenderer extends JButton implements TableCellRenderer {
	public HoaDonTableButtonRenderer() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (column == HoaDonTableModel.COL_XEM) {
			setIcon(new FlatSVGIcon("gui/icon/svg/view.svg", 0.8f));
			setToolTipText("Xem chi tiết");
		} else if (column == HoaDonTableModel.COL_IN) {
			setIcon(new FlatSVGIcon("gui/icon/svg/print.svg", 0.8f));
			setToolTipText("In hóa đơn");
		}

		setOpaque(true);

		return this;
	}

}