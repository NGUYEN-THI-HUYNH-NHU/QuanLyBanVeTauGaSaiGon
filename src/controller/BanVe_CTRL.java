package controller;
/*
 * @(#) Ve_Ctrl.java  1.0  [10:09:47 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import bus.Ve_BUS;
import entity.Ve;
import gui.application.form.banVe.PanelTableVeBan;
import gui.application.form.banVe.SessionVe;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */
import gui.application.form.banVe.TicketWizardPanel;

public class BanVe_CTRL {
    private final Ve_BUS ve_bus;
    private PanelTableVeBan tablePanel;
    private TicketWizardPanel wizardPanel;
    private SessionVe session = new SessionVe();
    private List<Ve> lastTableData = new ArrayList<>();
    private int currentStep = 1;

    public BanVe_CTRL(Ve_BUS ve_bus) {
        this.ve_bus = ve_bus;
    }

    public void setTablePanel(PanelTableVeBan tablePanel) {
        this.tablePanel = tablePanel;
    }

    public void setWizardPanel(TicketWizardPanel wizardPanel) {
        this.wizardPanel = wizardPanel;
    }

    // load/refresh table data
    public void refreshTable() {
        lastTableData = ve_bus.getTickets(null, null);
        if (tablePanel != null) tablePanel.setData(lastTableData);;
    }

    public void searchTickets(String q, String status) {
        lastTableData = ve_bus.getTickets(q, status);
        if (tablePanel != null) tablePanel.setData(lastTableData);
    }

    public void setSelectedTicket(Ve ticket) {
        session.setSelectedTicket(ticket);
        // move wizard to refund/exchange if selected ticket state requires
        // for demo just print
        System.out.println("Selected ticket: " + (ticket != null ? ticket.getVeID() : "none"));
    }

    // wizard navigation
    public void wizardNext() {
        if (currentStep < 3) currentStep++;
        if (wizardPanel != null) wizardPanel.showStep(currentStep);
    }

    public void wizardBack() {
        if (currentStep > 1) currentStep--;
        if (wizardPanel != null) wizardPanel.showStep(currentStep);
    }

    public void wizardConfirm() {
        // in real app we validate the session and call appropriate BUS methods
        // Demo: create a sold ticket
        Ve created = ve_bus.sellTicket(session);
        JOptionPane.showMessageDialog(null, "Bán vé thành công: " + created.getVeID());
        refreshTable();
        // reset wizard
        session = new SessionVe();
        currentStep = 1;
        if (wizardPanel != null) wizardPanel.reset();
    }

    public void wizardCancel() {
        session = new SessionVe();
        currentStep = 1;
        if (wizardPanel != null) wizardPanel.reset();
    }
}
