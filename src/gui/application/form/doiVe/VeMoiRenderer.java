package gui.application.form.doiVe;
/*
 * @(#) VeMoiRenderer.java  1.0  [9:35:01 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import gui.tuyChinh.ArrowIcon;

public class VeMoiRenderer implements TableCellRenderer {
	private final JPanel panel;
	private final JLabel textLabel;
	private final JLabel arrowLabel;

	public VeMoiRenderer() {
		panel = new JPanel(new BorderLayout());
		textLabel = new JLabel();
		arrowLabel = new JLabel();

		// Cấu hình mũi tên (Dùng icon tự vẽ cho đơn giản và nhẹ)
		arrowLabel.setIcon(new ArrowIcon());
		arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Padding cho mũi tên

		// Cấu hình text
		textLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Padding cho text

		panel.add(textLabel, BorderLayout.CENTER);
		panel.add(arrowLabel, BorderLayout.EAST);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		// 1. Render text bằng logic chung
		VeMoiListRenderer.renderVeMoi(textLabel, value);

		// 2. Xử lý màu sắc (Quan trọng để đồng bộ với bảng)
		if (isSelected) {
			panel.setBackground(table.getSelectionBackground());
			textLabel.setForeground(table.getSelectionForeground());
			// Mũi tên màu trắng khi selected
			arrowLabel.setIcon(new ArrowIcon(table.getSelectionForeground()));
		} else {
			panel.setBackground(table.getBackground());
			textLabel.setForeground(table.getForeground());
			// Mũi tên màu xám khi bình thường
			arrowLabel.setIcon(new ArrowIcon(Color.GRAY));
		}

		textLabel.setFont(table.getFont());
		return panel;
	}
}