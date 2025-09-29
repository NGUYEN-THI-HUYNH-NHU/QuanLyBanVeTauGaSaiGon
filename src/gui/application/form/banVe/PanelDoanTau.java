package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2DoanTau.java  1.0  [12:51:09 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Toa;

import javax.swing.*;

import controller.PanelBuoc2Controller;

import java.awt.*;
import java.util.List;

/**
 * show carriage list (buttons), clicking a toa triggers controller.onToaSelected
 */
public class PanelDoanTau extends JPanel {
    private JPanel flow;
    private PanelBuoc2Controller controller;

    public PanelDoanTau() {
        setLayout(new BorderLayout());
        flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JScrollPane scr = new JScrollPane(flow,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scr.setBorder(BorderFactory.createEmptyBorder());
        add(scr, BorderLayout.CENTER);
        setPreferredSize(new Dimension(10, 70));
    }

    public void setController(PanelBuoc2Controller controller) { this.controller = controller; }

    public void showToaList(List<Toa> list, java.util.function.Consumer<Toa> onSelect) {
        flow.removeAll();
        if (list == null || list.isEmpty()) {
            flow.add(new JLabel("Không có toa"));
        } else {
            for (Toa t : list) {
                JButton btn = new JButton(String.valueOf(t.getSoToa()));
                btn.setPreferredSize(new Dimension(60, 40));
                btn.addActionListener(e -> {
                    onSelect.accept(t);
                    // visually highlight: clear others
                    highlightButton(btn);
                });
                flow.add(btn);
            }
        }
        flow.revalidate();
        flow.repaint();
    }

    private void highlightButton(JButton selected) {
        Component[] comps = flow.getComponents();
        for (Component c : comps) {
            if (c instanceof JButton) {
                c.setBackground(null);
            }
        }
        selected.setBackground(new Color(135, 206, 250));
    }
}

