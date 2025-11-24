package gui.tuyChinh;
/*
 * @(#) DateTimeRenderer.java  1.0  [6:07:22 PM] Nov 24, 2025
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DateTimeRenderer extends DefaultTableCellRenderer {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (value instanceof LocalDateTime) {
			value = ((LocalDateTime) value).format(FORMATTER);
		}

		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}