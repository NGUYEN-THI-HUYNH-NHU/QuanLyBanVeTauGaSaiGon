package gui.tuyChinh;
/*
 * @(#) SimpleComboboxRenderer.java  1.0  [1:23:27 AM] Dec 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 21, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class SimpleComboBoxRenderer implements TableCellRenderer {
	private final JPanel panel;
	private final JLabel textLabel;
	private final JLabel arrowLabel;

	public SimpleComboBoxRenderer() {
		panel = new JPanel(new BorderLayout());
		textLabel = new JLabel();
		arrowLabel = new JLabel();

		// Cấu hình mũi tên
		arrowLabel.setIcon(new ArrowIcon(new Color(36, 104, 155)));
		arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Padding phải

		textLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0)); // Padding text
		textLabel.setVerticalAlignment(SwingConstants.CENTER);

		panel.add(textLabel, BorderLayout.CENTER);
		panel.add(arrowLabel, BorderLayout.EAST);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		// 1. Set text
		textLabel.setText(value != null ? value.toString() : "");
		textLabel.setToolTipText(value != null ? value.toString() : null);

		// 2. Xử lý màu sắc Selection
		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
			textLabel.setForeground(table.getSelectionForeground());
			arrowLabel.setIcon(new ArrowIcon(table.getSelectionForeground()));
		} else {
			panel.setBackground(table.getBackground());
			textLabel.setForeground(table.getForeground());
			arrowLabel.setIcon(new ArrowIcon(Color.GRAY));
		}

		textLabel.setFont(table.getFont());
		return panel;
	}
}