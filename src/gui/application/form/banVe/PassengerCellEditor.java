package gui.application.form.banVe;
/*
 * @(#) PassengerCellEditor.java  1.0  [10:13:44 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class PassengerCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final PassengerCellPanel panel;
	private final JPanel wrapperPanel;
	private PassengerRow current;
	private PanelBuoc3 panelBuoc3;

	public PassengerCellEditor(PanelBuoc3 panelBuoc3) {
		this.panel = new PassengerCellPanel();
		this.wrapperPanel = new JPanel(new BorderLayout());
		this.wrapperPanel.add(this.panel, BorderLayout.CENTER);
		this.wrapperPanel.setBorder(null);
		this.panelBuoc3 = panelBuoc3;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof PassengerRow) {
			current = (PassengerRow) value;

			// 1. Truyền table để panel có thể điều khiển (nhảy dòng, dừng edit)
			panel.setTable(table);
			// 2. Truyền PanelBuoc3 để panel có thể focus ra form bên ngoài
			panel.setPanelBuoc3(this.panelBuoc3);

			panel.setData(current);
			panel.setEditable(true);

			if (isSelected) {
				wrapperPanel.setBackground(table.getSelectionBackground());
				panel.setBackground(table.getSelectionBackground());
			} else {
				wrapperPanel.setBackground(table.getBackground());
				panel.setBackground(table.getBackground());
			}

			// request focus for first field after editor added to table
			SwingUtilities.invokeLater(() -> {
				panel.getTxtTen().requestFocusInWindow();
			});
		}

		return wrapperPanel;
	}

	@Override
	public Object getCellEditorValue() {
		if (current != null) {
			return panel.getData(current);
		}
		return null;
	}
}