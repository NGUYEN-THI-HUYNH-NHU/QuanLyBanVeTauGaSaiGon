package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVe1.java  1.0  [5:26:32 PM] Nov 17, 2025
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

import javax.swing.JPanel;

public class PanelDoiVe1 extends JPanel {
	private PanelDoiVeBuoc1 panelDoiVeBuoc1;
	private PanelDoiVeBuoc2 panelDoiVeBuoc2;
	private PanelDoiVeBuoc3 panelDoiVeBuoc3;

	public PanelDoiVe1() {
		setLayout(new BorderLayout());

		JPanel pnlNorth = new JPanel(new BorderLayout(1, 0));

		panelDoiVeBuoc1 = new PanelDoiVeBuoc1();
		panelDoiVeBuoc2 = new PanelDoiVeBuoc2();
		panelDoiVeBuoc3 = new PanelDoiVeBuoc3();

		pnlNorth.add(panelDoiVeBuoc1, BorderLayout.WEST);
		pnlNorth.add(panelDoiVeBuoc2, BorderLayout.CENTER);

		add(pnlNorth, BorderLayout.NORTH);
		add(panelDoiVeBuoc3, BorderLayout.CENTER);

		panelDoiVeBuoc2.setEnabled(false);
		panelDoiVeBuoc2.setEnabled(false);

	}

	public PanelDoiVeBuoc1 getPanelDoiVeBuoc1() {
		return panelDoiVeBuoc1;
	}

	public PanelDoiVeBuoc2 getPanelDoiVeBuoc2() {
		return panelDoiVeBuoc2;
	}

	public PanelDoiVeBuoc3 getPanelDoiVeBuoc3() {
		return panelDoiVeBuoc3;
	}

	public void setBuoc1Enabled(boolean enabled) {
		panelDoiVeBuoc1.setComponentsEnabled(enabled);
	}

	public void setBuoc2Enabled(boolean enabled) {
		panelDoiVeBuoc2.setComponentsEnabled(enabled);
	}

	public void setBuoc3Enabled(boolean enabled) {
		panelDoiVeBuoc3.setComponentsEnabled(enabled);
	}
}