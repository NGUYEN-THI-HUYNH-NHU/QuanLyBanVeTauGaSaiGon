package gui.application.form.banVe;
/*
 * @(#) PassengerCellRenderer.java  1.0  [10:14:12 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PassengerCellRenderer implements TableCellRenderer {

    private final PassengerCellPanel panel;

    public PassengerCellRenderer() {
        this.panel = new PassengerCellPanel();
        this.panel.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        if (value instanceof PassengerRow passenger) {
            panel.setData(passenger);
            panel.setEditable(false);
        }
        // Thiết lập màu nền cho rõ ràng
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }

        return panel;
    }

    public PassengerCellPanel getPanel() {
        return panel;
    }
}
