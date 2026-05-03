package gui.application.form.banVe;
/*
 * @(#) KhuyenMaiCellEditor.java  1.0  [10:27:05 PM] Dec 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 1, 2025
 * @version: 1.0
 */

import bus.KhuyenMai_BUS;
import entity.KhuyenMai;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class KhuyenMaiCellEditor extends DefaultCellEditor {
    private final JComboBox<KhuyenMai> cbKhuyenMai;
    private final PanelBuoc4.KhuyenMaiProvider khuyenMaiProvider;
    private final VeBanTableModel model;

    public KhuyenMaiCellEditor(JComboBox<KhuyenMai> cbKhuyenMai, PanelBuoc4.KhuyenMaiProvider khuyenMaiProvider,
                               VeBanTableModel model) {
        super(cbKhuyenMai);
        this.cbKhuyenMai = cbKhuyenMai;
        this.khuyenMaiProvider = khuyenMaiProvider;
        this.model = model;
        // Để danh sách khi xổ xuống cũng hiện đúng format "Mã (-%)"
        this.cbKhuyenMai.setRenderer(new KhuyenMaiListRenderer());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        // 1. Lấy dữ liệu dòng hiện tại
        PassengerRow p = model.getRowAt(row);
        VeSession v = p.getVeSession();

        // 2. Gọi Provider để lấy danh sách KM phù hợp
        if (khuyenMaiProvider != null) {
            List<KhuyenMai> listKM = khuyenMaiProvider.getKhuyenMaiFor(v);

            // 3. Cập nhật ComboBox
            cbKhuyenMai.removeAllItems();
            cbKhuyenMai.addItem(null);
            if (listKM != null) {
                for (KhuyenMai km : listKM) {
                    cbKhuyenMai.addItem(km);
                }
            }
            cbKhuyenMai.setSelectedItem(KhuyenMai_BUS.getBestPromotion(v, listKM));
        }

        if (comp instanceof JComponent) {
            JComponent jComp = (JComponent) comp;
            jComp.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, table.getGridColor()));
            jComp.setOpaque(true);
        }

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}