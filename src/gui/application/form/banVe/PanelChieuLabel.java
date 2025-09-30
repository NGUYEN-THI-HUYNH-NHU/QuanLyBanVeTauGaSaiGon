package gui.application.form.banVe;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*
 * @(#) PanelBuoc2ChieuLabel.java  1.0  [12:49:52 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */


public class PanelChieuLabel extends JPanel {
    private JLabel lblInfo;

    public PanelChieuLabel() {
        setLayout(new BorderLayout());
        lblInfo = new JLabel("Chiều: —", SwingConstants.LEFT);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(lblInfo, BorderLayout.CENTER);
    }

    public void setInfo(String text) {
        lblInfo.setText("<html><b>" + text + "</b></html>");
    }
}
