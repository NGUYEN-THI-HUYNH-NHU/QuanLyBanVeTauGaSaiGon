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

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import entity.KhuyenMai;

public class KhuyenMaiRenderer extends DefaultTableCellRenderer implements ListCellRenderer<Object> {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		renderKhuyenMai(value);
		return this;
	}

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		setFont(list.getFont());
		setOpaque(true);

		renderKhuyenMai(value);
		return this;
	}

	private void renderKhuyenMai(Object value) {
		if (value instanceof KhuyenMai) {
			KhuyenMai km = (KhuyenMai) value;
			if (km != null) {
				setText(km.getMaKhuyenMai() + " (" + getGiamGiaString(km) + ")");
				setToolTipText(km.getMoTa());
			}
		} else {
			setText("Không áp dụng"); // Hoặc "" nếu value == null
			setToolTipText(null);
		}
	}

	private String getGiamGiaString(KhuyenMai km) {
		if (km.getTyLeGiamGia() > 0) {
			return String.format("-%.0f%%", km.getTyLeGiamGia() * 100);
		} else {
			return String.format("-%.0f VNĐ", km.getTienGiamGia());
		}
	}
}