package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2.java  1.0  [12:05:29 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PanelBanVe2 extends JPanel {

	private final PanelBuoc4 panelBuoc4;
	private final PanelBuoc5 panelBuoc5;

	public PanelBanVe2() {
		setLayout(new BorderLayout(8, 8));

		// 1. Khởi tạo các panel con
		panelBuoc4 = new PanelBuoc4();
		panelBuoc5 = new PanelBuoc5();

		// 2. Thêm vào layout
		add(panelBuoc4, BorderLayout.NORTH);
		add(panelBuoc5, BorderLayout.CENTER);
	}

	// --- Getters cho Controller ---

	public PanelBuoc4 getPanelBuoc4() {
		return panelBuoc4;
	}

	public PanelBuoc5 getPanelBuoc5() {
		return panelBuoc5;
	}
}