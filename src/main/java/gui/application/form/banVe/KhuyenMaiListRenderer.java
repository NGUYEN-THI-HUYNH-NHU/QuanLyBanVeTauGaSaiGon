package gui.application.form.banVe;
/*
 * @(#) KhuyenMaiListRenderer.java  1.0  [12:40:51 AM] Dec 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 21, 2025
 * @version: 1.0
 */

import dto.KhuyenMaiDTO;

import javax.swing.*;
import java.awt.*;

public class KhuyenMaiListRenderer extends DefaultListCellRenderer {
    // Logic hiển thị text được tách riêng (Static) để các class khác gọi dùng ké
    public static void renderKhuyenMai(JLabel label, Object value) {
        if (value instanceof KhuyenMaiDTO) {
            KhuyenMaiDTO km = (KhuyenMaiDTO) value;
            if (km.getId() != null && !km.getId().isEmpty()) {
                label.setText(km.getMaKhuyenMai() + " (" + getGiamGiaString(km) + ")");
                label.setToolTipText(km.getMoTa());
            } else {
                label.setText("Không áp dụng");
                label.setToolTipText("Không có khuyến mãi");
            }
        } else {
            label.setText("Không áp dụng");
            label.setToolTipText(null);
        }
    }

    private static String getGiamGiaString(KhuyenMaiDTO km) {
        if (km.getTyLeGiamGia() > 0) {
            return String.format("-%.0f%%", km.getTyLeGiamGia() * 100);
        } else {
            return String.format("-%.0f đ", km.getTienGiamGia());
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        renderKhuyenMai(this, value);
        return this;
    }
}