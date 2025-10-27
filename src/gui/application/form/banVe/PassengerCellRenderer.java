package gui.application.form.banVe;
/*
 * @(#) PassengerCellRenderer.java  1.0  [10:14:12 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

//PassengerCellRenderer.java
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PassengerCellRenderer implements TableCellRenderer {
	private final PassengerCellPanel panel = new PassengerCellPanel();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
	                                              int row, int column) {
	   if (value instanceof PassengerRow) {
	       panel.setData((PassengerRow) value);
	       panel.setEditable(false);
	   }
	   if (isSelected) {
	       panel.setBackground(table.getSelectionBackground());
	   } else {
	       panel.setBackground(table.getBackground());
	   }
	   return panel;
	}
}
