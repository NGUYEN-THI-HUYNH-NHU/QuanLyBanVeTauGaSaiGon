package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2ChuyenTau.java  1.0  [12:50:26 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */


import entity.Chuyen;

import javax.swing.*;

import controller.PanelBuoc2Controller;

//import controller.PanelBuoc2Controller;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * shows a horizontal list of "train cards" (buttons).
 */
public class PanelChuyenTau extends JPanel {
    private JPanel flowPanel;
    private JScrollPane scroll;
    private PanelBuoc2Controller controller;

    public PanelChuyenTau() {
        setLayout(new BorderLayout());
        flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        scroll = new JScrollPane(flowPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(10, 120));
    }

    public void setController(PanelBuoc2Controller controller) {
        this.controller = controller;
    }

    public void showChuyenList(List<Chuyen> list) {
        flowPanel.removeAll();
        if (list == null || list.isEmpty()) {
            flowPanel.add(new JLabel("Không có chuyến"));
        } else {
            for (Chuyen c : list) {
                JPanel card = createChuyenCard(c, sel -> {
                    if (controller != null) controller.onChuyenSelected(c);
                });
                flowPanel.add(card);
            }
        }
        flowPanel.revalidate();
        flowPanel.repaint();
    }

    private JPanel createChuyenCard(Chuyen c, Consumer<Chuyen> onSelect) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(200, 90));
        p.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel lblTau = new JLabel(c.getTau() == null ? "Tau" : c.getTau().getTauID());
        JLabel lblTime = new JLabel(String.format("%s → %s", c.getNgayGioKhoiHanh(), c.getNgayGioDen()));
        // TODO: replace with real numbers from BUS/DAO
        JLabel lblSeats = new JLabel("Đặt: 0  Trống: ?");

        JPanel top = new JPanel(new GridLayout(2,1));
        top.add(lblTau);
        top.add(lblTime);

        p.add(top, BorderLayout.CENTER);
        p.add(lblSeats, BorderLayout.SOUTH);

        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onSelect.accept(c);
            }
        });
        return p;
    }
}
