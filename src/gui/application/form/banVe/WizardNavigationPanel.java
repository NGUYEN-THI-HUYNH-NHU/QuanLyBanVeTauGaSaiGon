package gui.application.form.banVe;
/*
 * @(#) WizardNavigationPanel.java  1.0  [10:38:15 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import javax.swing.JButton;
import javax.swing.JPanel;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

public class WizardNavigationPanel extends JPanel {
    private JButton btnPrev, btnNext, btnCancel;
    private WizardController controller;

    public WizardNavigationPanel() {
        btnPrev = new JButton("Quay lại");
        btnNext = new JButton("Tiếp");
        btnCancel = new JButton("Hủy");

        add(btnPrev);
        add(btnNext);
        add(btnCancel);

        btnPrev.addActionListener(e -> controller.previousStep());
        btnNext.addActionListener(e -> controller.nextStep());
        btnCancel.addActionListener(e -> controller.reset());
    }

    public void setController(WizardController controller) {
        this.controller = controller;
    }
}