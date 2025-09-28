package gui.application.form.banVe;

import entity.NhanVien;

/*
 * @(#) FormBanVe.java  1.0  [3:49:29 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import javax.swing.*;

import bus.Ve_BUS;
import controller.BanVe_CTRL;

import java.awt.*;

public class PanelBanVe extends JPanel {
    private final PanelTableVeBan tablePanel;
    private final TicketWizardPanel wizardPanel;
    private final BanVe_CTRL controller;

    public PanelBanVe(NhanVien nhanVien) {
        setLayout(new BorderLayout());
        // create BUS and controller
        Ve_BUS bus = new Ve_BUS();
        controller = new BanVe_CTRL(bus);

        // Build UI
        tablePanel = new PanelTableVeBan(controller);
        wizardPanel = new TicketWizardPanel(controller);

        // controller knows locations to update
        controller.setTablePanel(tablePanel);
        controller.setWizardPanel(wizardPanel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tablePanel, wizardPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(250);
        add(split, BorderLayout.CENTER);

        // initial load
        controller.refreshTable();
    }
}