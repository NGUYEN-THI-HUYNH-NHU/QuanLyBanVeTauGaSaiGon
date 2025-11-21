package gui.application.form.doiVe;
/*
 * @(#) PanelBuoc2.java  1.0  [10:39:25 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class PanelDoiVeBuoc5 extends JPanel {
	private PanelChuyenDoiVe panelChuyen;
	private PanelGioVeDoiVe panelGioVe;

	public PanelDoiVeBuoc5() {
		setLayout(new BorderLayout(2, 0));
		setBorder(new TitledBorder(""));
		setPreferredSize(new Dimension(0, 440));

		panelGioVe = new PanelGioVeDoiVe();
		add(panelGioVe, BorderLayout.EAST);

		panelChuyen = new PanelChuyenDoiVe();

		add(panelChuyen, BorderLayout.CENTER);
		add(panelGioVe, BorderLayout.EAST);
	}

	public PanelGioVeDoiVe getPanelGioVe() {
		return panelGioVe;
	}

	public PanelChuyenDoiVe getPanelChuyen() {
		return panelChuyen;
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (panelGioVe != null) {
			panelGioVe.setEnabled(enabled);
		}
	}
}