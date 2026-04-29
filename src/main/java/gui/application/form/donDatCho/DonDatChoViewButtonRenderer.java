package gui.application.form.donDatCho;
/*
 * @(#) DonDatChoViewButtonRenderer.java  1.0  [12:43:47 PM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;

public class DonDatChoViewButtonRenderer extends JButton implements TableCellRenderer {
	public DonDatChoViewButtonRenderer() {
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setIcon(new FlatSVGIcon("icon/svg/view.svg", 0.8f));
		setToolTipText("Xem chi tiết đơn và danh sách vé");
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		return this;
	}
}