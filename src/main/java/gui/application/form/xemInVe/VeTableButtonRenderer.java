package gui.application.form.xemInVe;
/*
 * @(#) VeTableButtonRenderer.java  1.0  [7:25:39 PM] Dec 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.extras.FlatSVGIcon;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 17, 2025
 * @version: 1.0
 */

public class VeTableButtonRenderer extends JButton implements TableCellRenderer {
	public VeTableButtonRenderer() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		if (column == VeTableModel.COL_IN) {
			setIcon(new FlatSVGIcon("icon/svg/print.svg", 0.8f));
			setToolTipText("In vé");
		}

		setOpaque(true);

		return this;
	}

}
