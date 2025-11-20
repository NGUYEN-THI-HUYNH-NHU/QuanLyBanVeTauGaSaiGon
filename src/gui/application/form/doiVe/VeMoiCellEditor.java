package gui.application.form.doiVe;
/*
 * @(#) VeMoiCellEditor.java  1.0  [9:35:54 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import gui.application.form.banVe.VeSession;

public class VeMoiCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JComboBox<VeSession> comboBox;
	private List<VeSession> allVeMoiAvailable; // Danh sách gốc tất cả vé mới
	private MappingVeTableModel tableModel; // Tham chiếu model để check các dòng khác

	public VeMoiCellEditor(List<VeSession> allVeMoi, MappingVeTableModel model) {
		this.allVeMoiAvailable = allVeMoi;
		this.tableModel = model;

		comboBox = new JComboBox<>();
		comboBox.setRenderer(new VeMoiRenderer());

		// Khi chọn xong thì stop editing để update model
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
			}
		});
	}

	// Cập nhật lại danh sách nguồn (nếu cần thiết khi controller thay đổi dữ liệu)
	public void setSourceList(List<VeSession> list) {
		this.allVeMoiAvailable = list;
	}

	@Override
	public Object getCellEditorValue() {
		return comboBox.getSelectedItem();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// 1. Lấy vé đang được chọn ở các dòng KHÁC
		Set<VeSession> selectedInOtherRows = new HashSet<>();

		List<MappingRow> rows = tableModel.getRows();
		for (int i = 0; i < rows.size(); i++) {
			// Bỏ qua dòng hiện tại đang edit (row)
			// Lưu ý: row là view index, nếu table có sort/filter cần convert,
			// nhưng ở đây model.getRows() lấy theo model index nên ta cần cẩn thận.
			// Tuy nhiên MappingVeTableModel thường list cố định, giả sử row index khớp
			// model.
			if (i != row) {
				VeSession v = rows.get(i).getVeMoi();
				if (v != null) {
					selectedInOtherRows.add(v);
				}
			}
		}

		// 2. Lọc danh sách:
		// Vé hợp lệ = (Vé trong kho) - (Vé đã chọn ở dòng khác)
		// Lưu ý: Phải giữ lại vé (value) đang được chọn ở dòng NÀY (nếu có)
		List<VeSession> filteredList = new ArrayList<>();

		for (VeSession v : allVeMoiAvailable) {
			// Nếu vé này chưa bị dòng khác chọn HOẶC nó chính là vé đang chọn của dòng này
			if (!selectedInOtherRows.contains(v) || (value != null && v.equals(value))) {
				filteredList.add(v);
			}
		}

		// 3. Cập nhật Model cho ComboBox
		DefaultComboBoxModel<VeSession> model = new DefaultComboBoxModel<>();
		for (VeSession v : filteredList) {
			model.addElement(v);
		}
		comboBox.setModel(model);

		// 4. Set lại giá trị đã chọn
		comboBox.setSelectedItem(value);

		return comboBox;
	}
}