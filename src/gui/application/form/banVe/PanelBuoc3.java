package gui.application.form.banVe;
/*
 * @(#) PassengerInfoPanel.java  1.0  [10:39:57 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class PanelBuoc3 extends JPanel {
    public PanelBuoc3() {
        setLayout(new GridLayout(3, 2));
        add(new JLabel("Họ tên:"));
        add(new JTextField(15));
        add(new JLabel("CMND:"));
        add(new JTextField(15));
        add(new JLabel("SĐT:"));
        add(new JTextField(15));
    }
}