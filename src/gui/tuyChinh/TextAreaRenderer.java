package gui.tuyChinh;
/*
 * @(#) TextAreaRenderer.java  1.0  [12:51:56 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TextAreaRenderer extends JTextArea implements TableCellRenderer {
	public TextAreaRenderer() {
		setLineWrap(true); // Bật wrap
		setWrapStyleWord(true); // Wrap theo từ
		setOpaque(true); // Hiển thị nền
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setText(value == null ? "" : value.toString());

		// Màu nền khi chọn
		if (isSelected) {
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
		}

//		// Tự điều chỉnh chiều cao dòng theo nội dung
//		setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);
//		int preferredHeight = getPreferredSize().height;
//		if (table.getRowHeight(row) != preferredHeight) {
//			table.setRowHeight(row, preferredHeight);
//		}

		return this;
	}
}