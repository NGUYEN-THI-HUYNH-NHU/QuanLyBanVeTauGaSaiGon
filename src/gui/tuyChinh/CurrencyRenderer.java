package gui.tuyChinh;

/*
 * @(#) CurrencyRenderer.java  1.0  [5:46:11 PM] Nov 21, 2025
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
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CurrencyRenderer extends DefaultTableCellRenderer {

	private final DecimalFormat df;

	public CurrencyRenderer() {
		this.df = new DecimalFormat("#,##0đ");
		setHorizontalAlignment(SwingConstants.RIGHT);
		setVerticalAlignment(SwingConstants.TOP);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (value instanceof Double) {
			label.setText(df.format(value));
		}

		return label;
	}
}
