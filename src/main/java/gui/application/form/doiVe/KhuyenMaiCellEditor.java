package gui.application.form.doiVe;
/*
 * @(#) KhuyenMaiCellEditor.java  1.0  [7:35:31 PM] Dec 3, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dto.KhuyenMaiDTO;
import gui.application.form.banVe.KhuyenMaiListRenderer;
import gui.application.form.banVe.VeSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 3, 2025
 * @version: 1.0
 */

public class KhuyenMaiCellEditor extends DefaultCellEditor {
    private final JComboBox<KhuyenMaiDTO> cbKhuyenMai;
    private final PanelDoiVeBuoc7.KhuyenMaiProvider khuyenMaiProvider;
    private final MappingVeTableModel model;

    public KhuyenMaiCellEditor(JComboBox<KhuyenMaiDTO> cbKhuyenMai, PanelDoiVeBuoc7.KhuyenMaiProvider khuyenMaiProvider,
                               MappingVeTableModel model) {
        super(cbKhuyenMai);
        this.cbKhuyenMai = cbKhuyenMai;
        this.khuyenMaiProvider = khuyenMaiProvider;
        this.model = model;
        this.cbKhuyenMai.setRenderer(new KhuyenMaiListRenderer());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 1. Lấy dữ liệu dòng hiện tại
        MappingRow mappingRow = model.getRowAt(row);
        VeSession v = mappingRow.getVeSessionMoi();

        // Reset ComboBox trước
        cbKhuyenMai.removeAllItems();
        cbKhuyenMai.addItem(null);

        // 2. Chỉ load danh sách nếu đã có Vé Mới
        if (v != null && khuyenMaiProvider != null) {
            List<KhuyenMaiDTO> listKM = khuyenMaiProvider.getKhuyenMaiFor(v);
            if (listKM != null) {
                for (KhuyenMaiDTO km : listKM) {
                    cbKhuyenMai.addItem(km);
                }
            }
        }

        // 3. Chọn item
        cbKhuyenMai.setSelectedItem(value);

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }
}