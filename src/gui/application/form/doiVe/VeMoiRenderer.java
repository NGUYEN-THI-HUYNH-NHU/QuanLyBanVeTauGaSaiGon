package gui.application.form.doiVe;
/*
 * @(#) VeMoiRenderer.java  1.0  [9:35:01 PM] Nov 20, 2025
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
import java.text.DecimalFormat;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import gui.application.form.banVe.VeSession;

public class VeMoiRenderer extends DefaultListCellRenderer {

	private static final DecimalFormat df = new DecimalFormat("#,##0");

	// Renderer cho items trong ComboBox (ListCellRenderer)
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		formatLabel(lbl, value);
		return lbl;
	}

	// Helper để format text thống nhất
	public static void formatLabel(JLabel lbl, Object value) {
		if (value instanceof VeSession) {
			VeSession v = (VeSession) value;
			String text = String.format("<html>Toa %s - %s<br/>Chỗ %s<br/>Giá: <b>%s</b></html>", v.getSoToa(),
					v.getHangToa(), v.getSoGhe(), df.format(v.getGia()));
			lbl.setText(text);
			lbl.setToolTipText(v.prettyString()); // Tooltip chi tiết
		} else if (value == null) {
			lbl.setText("Chọn vé mới");
		} else {
			lbl.setText(value.toString());
		}
	}

	// Static method trả về TableCellRenderer để dùng cho ô JTable (khi không edit)
	public static DefaultTableCellRenderer getTableCellRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				formatLabel(lbl, value);
				return lbl;
			}
		};
	}
}