package gui.application.form.doiVe;
/*
 * @(#) KhuyenMaiCellEditor.java  1.0  [7:35:31 PM] Dec 3, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import entity.KhuyenMai;
import gui.application.form.banVe.VeSession;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 3, 2025
 * @version: 1.0
 */

public class KhuyenMaiCellEditor extends DefaultCellEditor {
	private final JComboBox<KhuyenMai> cbKhuyenMai;
	private final PanelDoiVeBuoc7.KhuyenMaiProvider khuyenMaiProvider;
	private final MappingVeTableModel model;

	public KhuyenMaiCellEditor(JComboBox<KhuyenMai> cbKhuyenMai, PanelDoiVeBuoc7.KhuyenMaiProvider khuyenMaiProvider,
			MappingVeTableModel model) {
		super(cbKhuyenMai);
		this.cbKhuyenMai = cbKhuyenMai;
		this.khuyenMaiProvider = khuyenMaiProvider;
		this.model = model;
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
			List<KhuyenMai> listKM = khuyenMaiProvider.getKhuyenMaiFor(v);
			if (listKM != null) {
				for (KhuyenMai km : listKM) {
					cbKhuyenMai.addItem(km);
				}
			}
		}

		// 3. Chọn item
		cbKhuyenMai.setSelectedItem(value);

		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}