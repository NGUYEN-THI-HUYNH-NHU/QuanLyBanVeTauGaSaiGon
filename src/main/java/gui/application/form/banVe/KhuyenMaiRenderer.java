package gui.application.form.banVe;
/*
 * @(#) KhuyenMaiComboboxRenderer.java  1.0  [5:52:29 PM] Dec 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 1, 2025
 * @version: 1.0
 */

import gui.tuyChinh.ArrowIcon;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class KhuyenMaiRenderer implements TableCellRenderer {
    private final JPanel panel;
    private final JLabel textLabel;
    private final JLabel arrowLabel;

    public KhuyenMaiRenderer() {
        panel = new JPanel(new BorderLayout());
        textLabel = new JLabel();
        arrowLabel = new JLabel();

        // Cấu hình mũi tên (Dùng icon tự vẽ cho đơn giản và nhẹ)
        arrowLabel.setIcon(new ArrowIcon());
        arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Padding cho mũi tên

        // Cấu hình text
        textLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); // Padding cho text

        panel.add(textLabel, BorderLayout.CENTER);
        panel.add(arrowLabel, BorderLayout.EAST);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        // 1. Render text bằng logic chung
        KhuyenMaiListRenderer.renderKhuyenMai(textLabel, value);

        // 2. Xử lý màu sắc (Quan trọng để đồng bộ với bảng)
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
            textLabel.setForeground(table.getSelectionForeground());
            // Mũi tên màu trắng khi selected
            arrowLabel.setIcon(new ArrowIcon(table.getSelectionForeground()));
        } else {
            panel.setBackground(table.getBackground());
            textLabel.setForeground(table.getForeground());
            // Mũi tên màu xám khi bình thường
            arrowLabel.setIcon(new ArrowIcon(Color.GRAY));
        }

        textLabel.setFont(table.getFont());
        return panel;
    }
}