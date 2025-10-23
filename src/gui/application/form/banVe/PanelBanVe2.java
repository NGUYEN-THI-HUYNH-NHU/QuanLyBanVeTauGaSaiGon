package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2.java  1.0  [12:05:29 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;

import javax.swing.JPanel;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */

public class PanelBanVe2 extends JPanel {
	private PanelBuoc4 panelBuoc4;
	private PanelBuoc5 panelBuoc5;

	public PanelBanVe2() {
		setLayout(new BorderLayout());
				
		panelBuoc4 = new PanelBuoc4();
		panelBuoc5 = new PanelBuoc5();
				
		add(panelBuoc4, BorderLayout.NORTH);
		add(panelBuoc5, BorderLayout.CENTER);
		
		panelBuoc4.setEnabled(false);
		panelBuoc5.setEnabled(false);
	}

	public PanelBuoc4 getPanelBuoc4() {
		return panelBuoc4;
	}

	public PanelBuoc5 getPanelBuoc5() {
		return panelBuoc5;
	}
	
	// === Các hàm điều khiển trạng thái (sẽ được gọi bởi Mediator) ===
    public void setBuoc2Enabled(boolean enabled) {
        // panelBuoc2.setEnabled(enabled); // Cách này không hiệu quả
        // Bạn nên tạo một hàm trong PanelBuoc2 để vô hiệu hóa các component con
        panelBuoc4.setComponentsEnabled(enabled); // (Bạn cần tự viết hàm này)
    }

    public void setBuoc3Enabled(boolean enabled) {
        // Tương tự, viết hàm setComponentsEnabled(enabled) cho PanelBuoc3
        panelBuoc5.setComponentsEnabled(enabled);
    }
}