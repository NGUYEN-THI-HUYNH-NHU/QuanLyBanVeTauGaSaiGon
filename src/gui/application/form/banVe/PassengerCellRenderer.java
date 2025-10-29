package gui.application.form.banVe;
/*
 * @(#) PassengerCellRenderer.java  1.0  [10:14:12 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Color;
import java.awt.Component;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

//PassengerCellRenderer.java
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class PassengerCellRenderer implements TableCellRenderer {

	private final PassengerCellPanel panel;

	public PassengerCellRenderer() {
		this.panel = new PassengerCellPanel();
		this.panel.setOpaque(true); // đảm bảo background được vẽ đúng
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (value instanceof PassengerRow passenger) {
			panel.setData(passenger);
			panel.setEditable(false);
		}
		// Thiết lập màu nền & viền cho rõ ràng
		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
			panel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 1));
		} else {
			panel.setBackground(table.getBackground());
			panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		}

		return panel;
	}
}
