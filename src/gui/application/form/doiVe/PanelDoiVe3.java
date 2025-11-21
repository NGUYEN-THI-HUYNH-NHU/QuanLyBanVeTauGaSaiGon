package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVe3.java  1.0  [5:27:13 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class PanelDoiVe3 extends JPanel {
	private final PanelDoiVeBuoc7 panelBuoc7;
	private final PanelDoiVeBuoc8 panelBuoc8;
	private JPanel pnlNav;
	private JButton btnPrev;
	private JPanel centerPanel;

	public PanelDoiVe3() {
		setLayout(new BorderLayout(8, 8));

		centerPanel = new JPanel(new BorderLayout());

		panelBuoc7 = new PanelDoiVeBuoc7();
		panelBuoc8 = new PanelDoiVeBuoc8();

		centerPanel.add(panelBuoc7, BorderLayout.NORTH);
		centerPanel.add(panelBuoc8, BorderLayout.CENTER);

		add(centerPanel, BorderLayout.CENTER);

		pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnPrev = new JButton("Quay lại");
		pnlNav.add(btnPrev);

		add(pnlNav, BorderLayout.SOUTH);
	}

	public PanelDoiVeBuoc7 getPanelDoiVeBuoc7() {
		return panelBuoc7;
	}

	public PanelDoiVeBuoc8 getPanelDoiVeBuoc8() {
		return panelBuoc8;
	}

	public JButton getBtnPrev() {
		return btnPrev;
	}
}
