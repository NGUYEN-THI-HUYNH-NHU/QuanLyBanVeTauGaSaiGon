package gui.application.form.banVe;
/*
 * @(#) PassengerCellEditor.java  1.0  [10:13:44 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

//PassengerCellEditor.java
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

public class PassengerCellEditor extends AbstractCellEditor implements TableCellEditor {
	private final PassengerCellPanel panel = new PassengerCellPanel();
	private PassengerRow current;

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof PassengerRow) {
			current = (PassengerRow) value;
			panel.setData(current);
			panel.setEditable(true);
			panel.updateChildUI(); // ensure borders match current LAF
			// request focus for first field after editor added to table
			SwingUtilities.invokeLater(() -> {
				panel.getTxtTen().requestFocusInWindow(); // make tfName public/package or add getter
			});
		}
		return panel;
	}

	@Override
	public Object getCellEditorValue() {
		if (current != null) {
			return panel.getData(current);
		}
		return null;
	}
}