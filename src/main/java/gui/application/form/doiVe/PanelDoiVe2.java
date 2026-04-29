package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVe2.java  1.0  [5:27:06 PM] Nov 17, 2025
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

public class PanelDoiVe2 extends JPanel {
	private PanelDoiVeBuoc4 panelBuoc4;
	private PanelDoiVeBuoc5 panelBuoc5;
	private PanelDoiVeBuoc6 panelBuoc6;
	private JPanel pnlNorth;

	public PanelDoiVe2() {
		setLayout(new BorderLayout());

		pnlNorth = new JPanel(new BorderLayout(1, 0));

		panelBuoc4 = new PanelDoiVeBuoc4();
		panelBuoc5 = new PanelDoiVeBuoc5();
		panelBuoc6 = new PanelDoiVeBuoc6();

		pnlNorth.add(panelBuoc4, BorderLayout.WEST);
		pnlNorth.add(panelBuoc5, BorderLayout.CENTER);

		add(pnlNorth, BorderLayout.NORTH);
		add(panelBuoc6, BorderLayout.CENTER);

		panelBuoc5.setEnabled(false);
		panelBuoc6.setEnabled(false);
	}

	public void setBuoc4Enabled(boolean enabled) {
		panelBuoc4.setComponentsEnabled(enabled);
	}

	public void setBuoc5Enabled(boolean enabled) {
		panelBuoc5.setComponentsEnabled(enabled);
	}

	public void setBuoc6Enabled(boolean enabled) {
		panelBuoc6.setComponentsEnabled(enabled);
	}

	public PanelDoiVeBuoc4 getPanelBuoc4() {
		return panelBuoc4;
	}

	public PanelDoiVeBuoc5 getPanelBuoc5() {
		return panelBuoc5;
	}

	public PanelDoiVeBuoc6 getPanelBuoc6() {
		return panelBuoc6;
	}
}