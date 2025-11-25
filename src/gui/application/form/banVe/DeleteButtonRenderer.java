package gui.application.form.banVe;
/*
 * @(#) DeleteButtonRenderer.java  1.0  [2:11:58 PM] Oct 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 30, 2025
 * @version: 1.0
 */

import java.awt.Component;
import java.awt.Cursor;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class DeleteButtonRenderer extends JButton implements TableCellRenderer {

	public DeleteButtonRenderer() {
		setIcon(new FlatSVGIcon("gui/icon/svg/delete.svg", 0.5f));
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		setText((value == null) ? "" : value.toString());

		// Giữ màu nền mặc định của bảng khi không được chọn
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			setBackground(UIManager.getColor("Table.background"));
			setForeground(UIManager.getColor("Table.foreground"));
		}

		return this;
	}
}