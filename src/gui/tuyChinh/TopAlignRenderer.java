package gui.tuyChinh;

/*
 * @(#) TopAlignRenderer.java  1.0  [5:47:42 PM] Nov 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 21, 2025
 * @version: 1.0
 */
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class TopAlignRenderer extends DefaultTableCellRenderer {

	public TopAlignRenderer() {
		setVerticalAlignment(SwingConstants.TOP);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
