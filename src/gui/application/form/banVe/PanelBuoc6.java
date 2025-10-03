package gui.application.form.banVe;
/*
 * @(#) ConfirmPanel.java  1.0  [10:40:53 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class PanelBuoc6 extends JPanel {
    public PanelBuoc6() {
        setLayout(new BorderLayout());
        add(new JLabel("Bước 4: Xác nhận thông tin vé"), BorderLayout.NORTH);
        add(new JTextArea("Thông tin vé sẽ hiển thị ở đây"), BorderLayout.CENTER);
    }
}