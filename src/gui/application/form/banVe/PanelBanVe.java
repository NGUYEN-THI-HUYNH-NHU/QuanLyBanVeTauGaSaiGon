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

        // Create panels and keep references
        PanelBuoc1 panelBuoc1 = new PanelBuoc1();
        PanelBuoc2 panelBuoc2 = new PanelBuoc2();
        PanelBuoc3 panelBuoc3 = new PanelBuoc3();
        PanelBuoc4 panelBuoc4 = new PanelBuoc4();
        PanelBuoc5 panelBuoc5 = new PanelBuoc5();
        PanelBuoc6 panelBuoc6 = new PanelBuoc6();


        // Add step panels to container with card names
        stepPanel.add(panelBuoc1, "search");
        stepPanel.add(panelBuoc2, "seat");
        stepPanel.add(panelBuoc3, "info");
        stepPanel.add(panelBuoc4, "confirm");
        stepPanel.add(panelBuoc5, "payment");
        stepPanel.add(panelBuoc6, "complete");

        add(stepPanel, BorderLayout.CENTER);

        // Navigation panel
        WizardNavigationPanel navPanel = new WizardNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);

        // Controller
        wizardController = new WizardController(cardLayout, stepPanel);
        navPanel.setController(wizardController);

        // Register panels with wizard so it can call enter(...) for step 2
        wizardController.registerPanel(1, "search", panelBuoc1);
        wizardController.registerPanel(2, "seat", panelBuoc2);
        wizardController.registerPanel(3, "info", panelBuoc3);
        wizardController.registerPanel(4, "confirm", panelBuoc4);
        wizardController.registerPanel(5, "payment", panelBuoc5);
        wizardController.registerPanel(6, "complete", panelBuoc6);

        // Inject wizard controller into PanelBuoc1's controller via PanelBuoc1 API
        panelBuoc1.setWizardController(wizardController);
    }
}