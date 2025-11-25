package gui.application.form.doiVe;

/*
 * @(#) VeMoiCellEditor.java  1.0  [9:35:54 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import gui.application.form.banVe.VeSession;

public class VeMoiCellEditor extends AbstractCellEditor implements TableCellEditor {

	private JComboBox<VeSession> comboBox;
	private List<VeSession> allVeMoiAvailable; // Danh sách gốc tất cả vé mới

	public VeMoiCellEditor(List<VeSession> allVeMoi) {
		this.allVeMoiAvailable = allVeMoi;

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
		// 1. Luôn tạo lại model để đảm bảo danh sách mới nhất
		DefaultComboBoxModel<VeSession> model = new DefaultComboBoxModel<>();

		// 2. Thêm tùy chọn "Bỏ chọn" (null) vào đầu danh sách
		model.addElement(null);

		// 3. Thêm TẤT CẢ các vé mới vào (KHÔNG LỌC)
		if (allVeMoiAvailable != null) {
			for (VeSession v : allVeMoiAvailable) {
				model.addElement(v);
			}
		}

		comboBox.setModel(model);
		comboBox.setSelectedItem(value);

		return comboBox;
	}
}