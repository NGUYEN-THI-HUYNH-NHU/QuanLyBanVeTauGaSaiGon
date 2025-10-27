package gui.application.form.banVe;
/*
 * @(#) PanelBanVe1.java  1.0  [10:30:48 AM] Oct 22, 2025
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

public class PanelBanVe1 extends JPanel {
	private PanelBuoc1 panelBuoc1;
	private PanelBuoc2 panelBuoc2;
	private PanelBuoc3 panelBuoc3;
	private JPanel pnlNorth;

	public PanelBanVe1() {
		setLayout(new BorderLayout());
		
		pnlNorth = new JPanel(new BorderLayout(1, 0));
		
		panelBuoc1 = new PanelBuoc1();
		panelBuoc2 = new PanelBuoc2();
		panelBuoc3 = new PanelBuoc3();
		
		pnlNorth.add(panelBuoc1, BorderLayout.WEST);
		pnlNorth.add(panelBuoc2, BorderLayout.CENTER);
				
		add(pnlNorth, BorderLayout.NORTH);
		add(panelBuoc3, BorderLayout.CENTER);
		
		panelBuoc2.setEnabled(false);
		panelBuoc3.setEnabled(false);
	}

	public PanelBuoc1 getPanelBuoc1() {
		return panelBuoc1;
	}

	public PanelBuoc2 getPanelBuoc2() {
		return panelBuoc2;
	}

	public PanelBuoc3 getPanelBuoc3() {
		return panelBuoc3;
	}
	
    public void setBuoc2Enabled(boolean enabled) {
        panelBuoc2.setComponentsEnabled(enabled);
    }

    public void setBuoc3Enabled(boolean enabled) {
        panelBuoc3.setComponentsEnabled(enabled);
    }
}