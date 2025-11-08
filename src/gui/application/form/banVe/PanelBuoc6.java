package gui.application.form.banVe;
/*
 * @(#) ConfirmPanel.java  1.0  [10:40:53 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class PanelBuoc6 extends JPanel {
	private JLabel lbl;
	private JTextArea area;

	public PanelBuoc6() {
		setLayout(new BorderLayout());
		add(lbl = new JLabel("Bước 4: Xác nhận thông tin vé"), BorderLayout.NORTH);
		add(area = new JTextArea("Thông tin vé sẽ hiển thị ở đây"), BorderLayout.CENTER);
		area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));
	}

	/**
	 * @param bookingSession
	 */
	public void loadCompletionData(BookingSession bookingSession) {
		for (VeSession v : bookingSession.getAllSelectedTickets()) {
			area.add(new JLabel(v.prettyString()));
		}
	}

	public JLabel getLbl() {
		return lbl;
	}

	public JTextArea getArea() {
		return area;
	}

	public void setLbl(JLabel lbl) {
		this.lbl = lbl;
	}

	public void setArea(JTextArea area) {
		this.area = area;
	}
}