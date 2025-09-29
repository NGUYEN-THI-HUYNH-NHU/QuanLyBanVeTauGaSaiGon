package gui.application.form.banVe;
/*
 * @(#) TicketSalePanel.java  1.0  [10:36:50 AM] Sep 28, 2025
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
import java.awt.CardLayout;

import javax.swing.JPanel;

import entity.NhanVien;

public class PanelBanVe extends JPanel {
    private CardLayout cardLayout;
    private JPanel stepPanel;
    private WizardController wizardController;

    public PanelBanVe(NhanVien nhanVien) {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        stepPanel = new JPanel(cardLayout);

        // Add step panels
        stepPanel.add(new PanelBuoc1(), "search");
        stepPanel.add(new PanelBuoc2(), "seat");
        stepPanel.add(new PanelBuoc3(), "info");
        stepPanel.add(new PanelBuoc4(), "confirm");
        stepPanel.add(new PanelBuoc5(), "payment");

        add(stepPanel, BorderLayout.CENTER);

        // Navigation panel
        WizardNavigationPanel navPanel = new WizardNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);

        // Controller
        wizardController = new WizardController(cardLayout, stepPanel);
        navPanel.setController(wizardController);
    }
}