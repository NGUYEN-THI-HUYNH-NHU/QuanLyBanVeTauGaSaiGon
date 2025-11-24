package gui.tuyChinh;
/*
 * @(#) TextAreaRenderer.java  1.0  [12:51:56 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

import javax.swing.BorderFactory;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import gui.application.form.hoaDon.HoaDonChiTietTableModel;

public class TextAreaRenderer extends JTextArea implements TableCellRenderer {
	// Tạo các Border để tái sử dụng, tránh tạo mới liên tục gây tốn bộ nhớ

	public TextAreaRenderer() {
		setLineWrap(true);
		setWrapStyleWord(true);
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, table.getGridColor()));
		setText(value == null ? "" : value.toString());

		// Tự động chỉnh chiều cao dòng (Auto Row Height)
		if (table.getModel() instanceof HoaDonChiTietTableModel) {
			if (column == HoaDonChiTietTableModel.COL_VE_PHIEU_ID) {
				setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
				int preferredHeight = getPreferredSize().height;

				// update lại height của row nếu cần
				if (table.getRowHeight(row) != preferredHeight) {
					table.setRowHeight(row, preferredHeight);
				}
			}
		} else {
			setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
			int preferredHeight = getPreferredSize().height;

			// update lại height của row nếu cần
			if (table.getRowHeight(row) != preferredHeight) {
				table.setRowHeight(row, preferredHeight);
			}
		}

		return this;
	}
}