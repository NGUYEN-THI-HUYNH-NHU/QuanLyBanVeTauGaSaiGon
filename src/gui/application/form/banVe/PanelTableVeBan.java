package gui.application.form.banVe;
/*
 * @(#) TableVe.java  1.0  [3:50:17 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.BanVe_CTRL;
import entity.Ve;

public class PanelTableVeBan extends JPanel {
    private final TableModelVeBan model;
    private final JTable table;
    private final JTextField txtSearch;
    private final JComboBox<String> cmbStatus;
    private final JButton btnSearch;
    private BanVe_CTRL controller;

    public PanelTableVeBan(BanVe_CTRL controller) {
        this.controller = controller;
        setLayout(new BorderLayout(8, 8));
        // Search bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        cmbStatus = new JComboBox<>(new String[]{"ALL", "SOLD", "REFUNDED", "EXCHANGED"});
        btnSearch = new JButton("Tìm");
        JButton btnRefresh = new JButton("Làm mới");

        top.add(new JLabel("Tìm:"));
        top.add(txtSearch);
        top.add(new JLabel("Trạng thái:"));
        top.add(cmbStatus);
        top.add(btnSearch);
        top.add(btnRefresh);

        add(top, BorderLayout.NORTH);

        // Table
        model = new TableModelVeBan(new ArrayList<>());
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Listeners
        btnSearch.addActionListener(e -> doSearch());
        btnRefresh.addActionListener(e -> controller.refreshTable());

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int r = table.getSelectedRow();
                    if (r >= 0) {
                        Ve t = model.getTicketAt(r);
                        controller.setSelectedTicket(t);
                    }
                }
            }
        });
    }

    private void doSearch() {
        String q = txtSearch.getText().trim();
        String status = (String) cmbStatus.getSelectedItem();
        controller.searchTickets(q, "ALL".equals(status) ? null : status);
    }

    public void setData(List<Ve> data) {
        model.setData(data);
    }

    public Ve getSelectedTicket() {
        int r = table.getSelectedRow();
        return r >= 0 ? model.getTicketAt(r) : null;
    }
}