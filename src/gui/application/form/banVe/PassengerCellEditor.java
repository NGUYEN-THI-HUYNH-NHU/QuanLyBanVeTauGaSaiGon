package gui.application.form.banVe;
/*
 * @(#) PassengerCellEditor.java  1.0  [10:13:44 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

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

// (Giả sử bạn đã import các lớp liên quan)

public class PassengerCellEditor extends AbstractCellEditor implements TableCellEditor {
	// (Kỹ thuật bọc panel để giữ L&F)
	private final JPanel wrapperPanel;
	private final PassengerCellPanel panel;

	private PassengerRow current;
	private PanelBuoc3 panelBuoc3; // Tham chiếu đến Panel cha

	public PassengerCellEditor(PanelBuoc3 panelBuoc3) {
		this.panelBuoc3 = panelBuoc3;
		this.panel = new PassengerCellPanel();

		// Kỹ thuật bọc panel
		this.wrapperPanel = new JPanel(new java.awt.BorderLayout());
		this.wrapperPanel.add(this.panel, java.awt.BorderLayout.CENTER);
		this.wrapperPanel.setBorder(null);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof PassengerRow) {
			current = (PassengerRow) value;

			// === TRUYỀN THAM CHIẾU XUỐNG PANEL ===
			panel.setTable(table);
			panel.setPanelBuoc3(this.panelBuoc3);
			// (Quan trọng) Truyền Controller xuống
			panel.setController(this.panelBuoc3.getController());

			panel.setData(current);
			panel.setEditable(true);

			// (Code set màu nền cho wrapperPanel và panel giữ nguyên)
			if (isSelected) {
				wrapperPanel.setBackground(table.getSelectionBackground());
				panel.setBackground(table.getSelectionBackground());
			} else {
				wrapperPanel.setBackground(table.getBackground());
				panel.setBackground(table.getBackground());
			}

			// Yêu cầu focus vào trường ĐẦU TIÊN
			SwingUtilities.invokeLater(() -> {
				panel.getTxtID().requestFocusInWindow();
			});
		}
		// Trả về panel bọc ngoài
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