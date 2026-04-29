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
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelBanVe2 extends JPanel {

	private final PanelBuoc4 panelBuoc4;
	private final PanelBuoc5 panelBuoc5;
	private JPanel pnlNav;
	private JButton btnPrev;
	private JPanel centerPanel;

	public PanelBanVe2() {
		setLayout(new BorderLayout(8, 8));

		centerPanel = new JPanel(new BorderLayout());

		panelBuoc4 = new PanelBuoc4();
		panelBuoc5 = new PanelBuoc5();

		centerPanel.add(panelBuoc4, BorderLayout.NORTH);
		centerPanel.add(panelBuoc5, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPrev = new JButton("Quay lại");
		pnlNav.add(btnPrev);

		add(pnlNav, BorderLayout.SOUTH);
	}

	public PanelBuoc4 getPanelBuoc4() {
		return panelBuoc4;
	}

	public PanelBuoc5 getPanelBuoc5() {
		return panelBuoc5;
	}

	public JButton getBtnPrev() {
		return btnPrev;
	}
}