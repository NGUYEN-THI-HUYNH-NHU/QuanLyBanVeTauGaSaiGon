package gui.application.form.banVe;
/*
 * @(#) DeleteButtonEditor.java  1.0  [2:12:48 PM] Oct 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 30, 2025
 * @version: 1.0
 */
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

	private JButton editorButton;
	private JTable table;
	private int row; // Dòng đang được sửa
	private boolean isPushed;

	public DeleteButtonEditor() {
		editorButton = new JButton("Xóa"); // Phải giống với renderer
		editorButton.setOpaque(true);
		// Bắt sự kiện click vào nút này
		editorButton.addActionListener(this);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.isPushed = true;
		// Có thể set foreground/background nếu muốn
		return editorButton;
	}

	@Override
	public Object getCellEditorValue() {
		// Giá trị trả về không quan trọng
		return "Delete";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Khi nút được bấm, báo cho JTable biết là đã "dừng sửa"
		// (để sự kiện click được hoàn tất)
		fireEditingStopped();
	}

	// Hai phương thức này quan trọng để nút hoạt động ngay khi bấm
	@Override
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}

	@Override
	public void cancelCellEditing() {
		isPushed = false;
		super.cancelCellEditing();
	}
}