package gui.application.form.bieuGia;
/*
 * @(#) BieuGiaVeTableButtonRenderer.java  1.0  [9:28:23 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class BieuGiaVeTableButtonRenderer extends JButton implements TableCellRenderer {
	public BieuGiaVeTableButtonRenderer() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (column == BieuGiaVeTableModel.COL_XEM) {
			setIcon(new FlatSVGIcon("gui/icon/svg/view-blue.svg", 0.8f));
			setToolTipText("Xem chi tiết");
		} else if (column == BieuGiaVeTableModel.COL_SUA) {
			setIcon(new FlatSVGIcon("gui/icon/svg/edit-yellow.svg", 0.8f));
			setToolTipText("Chỉnh sửa");
		}

		setOpaque(true);
		return this;
	}
}