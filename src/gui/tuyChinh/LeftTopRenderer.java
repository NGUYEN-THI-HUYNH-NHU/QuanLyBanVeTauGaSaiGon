package gui.tuyChinh;
/*
 * @(#) LeftTopRenderer.java  1.0  [8:38:19 PM] Dec 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 14, 2025
 * @version: 1.0
 */
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class LeftTopRenderer extends DefaultTableCellRenderer {

	public LeftTopRenderer() {
		setHorizontalAlignment(SwingConstants.LEFT);
		setVerticalAlignment(SwingConstants.TOP);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
