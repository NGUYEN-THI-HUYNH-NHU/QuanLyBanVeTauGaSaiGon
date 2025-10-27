package gui.application.form.banVe;
/*
 * @(#) PaymentPanel.java  1.0  [10:41:50 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class PanelBuoc5 extends JPanel {
	private JButton btnThanhToan;
    public PanelBuoc5() {
        setLayout(new FlowLayout());
        add(new JLabel("Bước 5: Thanh toán"));
        add(btnThanhToan = new JButton("Thanh toán"));
    }
    
    public JButton getBtnThanhToan() {
    	return btnThanhToan;
    }
    
    public void setComponentsEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	}
}