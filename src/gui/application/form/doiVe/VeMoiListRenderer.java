package gui.application.form.doiVe;
/*
 * @(#) VeMoiListRenderer.java  1.0  [1:08:26 AM] Dec 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import entity.Toa;
import gui.application.form.banVe.VeSession;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 21, 2025
 * @version: 1.0
 */

public class VeMoiListRenderer extends DefaultListCellRenderer {
	private static final DecimalFormat df = new DecimalFormat("#,##0");

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		renderVeMoi(this, value);
		return this;
	}

	// Logic hiển thị text được tách riêng (Static) để các class khác gọi dùng ké
	public static void renderVeMoi(JLabel label, Object value) {
		if (value instanceof VeSession) {
			VeSession v = (VeSession) value;
			Toa toa = v.getVe().getGhe().getToa();
			String text = String.format("<html>Toa %s - %s<br/>Chỗ %s<br/>Giá: <b>%s</b></html>", toa.getSoToa(),
					toa.getHangToa(), v.getSoGhe(), df.format(v.getVe().getGia()));
			label.setText(text);
			label.setToolTipText(v.prettyString()); // Tooltip chi tiết
		} else if (value == null) {
			label.setText("Chọn vé mới");
		} else {
			label.setText(value.toString());
		}
	}
}