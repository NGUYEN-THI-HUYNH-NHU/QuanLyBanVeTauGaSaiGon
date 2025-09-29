package gui.application.form.banVe;
/*
 * @(#) PanelSoDoCho.java  1.0  [12:51:43 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;

import javax.swing.*;

import controller.PanelBuoc2Controller;

import java.awt.*;
import java.util.List;


public class PanelSoDoCho extends JPanel {
    private JPanel seatGridPanel;
    private JPanel navPanel;
    private JButton btnPrev, btnNext;
    private PanelBuoc2Controller controller;

    // current toa context
    private Toa currentToa;
    private java.util.List<Toa> toaList;
    private int currentIndex = 0;

    public PanelSoDoCho() {
        setLayout(new BorderLayout());
        navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPrev = new JButton("<");
        btnNext = new JButton(">");
        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        add(navPanel, BorderLayout.NORTH);

        seatGridPanel = new JPanel();
        add(seatGridPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(10, 300));

        btnPrev.addActionListener(e -> showPrevToa());
        btnNext.addActionListener(e -> showNextToa());
    }

    public void setController(PanelBuoc2Controller c) { this.controller = c; }

    public void setToaList(java.util.List<Toa> list) {
        this.toaList = list;
        this.currentIndex = 0;
        if (list != null && !list.isEmpty()) {
            setCurrentToa(list.get(0));
        } else {
            setCurrentToa(null);
        }
    }

    public void setCurrentToa(Toa t) {
        this.currentToa = t;
        // load seats from controller (which calls DAO via BUS)
        if (controller != null && t != null) {
            controller.loadSeatsForToa(t, seats -> {
                SwingUtilities.invokeLater(() -> renderSeats(seats));
            });
        } else {
            renderSeats(null);
        }
    }

    private void renderSeats(java.util.List<Ghe> seats) {
        seatGridPanel.removeAll();
        if (seats == null || seats.isEmpty()) {
            seatGridPanel.add(new JLabel("Không có ghế"));
            seatGridPanel.revalidate();
            seatGridPanel.repaint();
            return;
        }
        // simple heuristic: determine grid size (columns)
        int cols = 6;
        int rows = (int) Math.ceil(seats.size() / (double) cols);
        seatGridPanel.setLayout(new GridLayout(rows, cols, 6, 6));

        for (Ghe g : seats) {
            JButton b = new JButton(String.valueOf(g.getSoGhe()));
            // color by status
            if (g.getTrangThai() == TrangThaiGhe.OCCUPIED) {
                b.setBackground(Color.RED);
                b.setEnabled(false);
            } else {
                b.setBackground(Color.WHITE);
                b.setEnabled(true);
            }
            b.addActionListener(e -> {
                if (controller != null) controller.onSeatClicked(currentToa, g);
            });
            seatGridPanel.add(b);
        }
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void showPrevToa() {
        if (toaList == null || toaList.isEmpty()) return;
        currentIndex = Math.max(0, currentIndex - 1);
        setCurrentToa(toaList.get(currentIndex));
    }

    private void showNextToa() {
        if (toaList == null || toaList.isEmpty()) return;
        currentIndex = Math.min(toaList.size() - 1, currentIndex + 1);
        setCurrentToa(toaList.get(currentIndex));
    }

    // used by controller when user selects a chuyen -> set toa list
    public void setToaListAndSelect(java.util.List<Toa> list, int selectIndex) {
        setToaList(list);
        if (list != null && !list.isEmpty()) {
            currentIndex = Math.min(Math.max(0, selectIndex), list.size() - 1);
            setCurrentToa(list.get(currentIndex));
        }
    }
}
