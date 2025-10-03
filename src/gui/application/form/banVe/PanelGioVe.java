package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2GioVe.java  1.0  [12:52:28 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ve;

import javax.swing.*;

import java.awt.*;
import java.util.List;

public class PanelGioVe extends JPanel {
    private JPanel container;
    private PanelBuoc2Controller controller;

    public PanelGioVe() {
        setLayout(new BorderLayout());
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        JScrollPane scr = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scr, BorderLayout.CENTER);
        setPreferredSize(new Dimension(240, 10));
    }

    public void setController(PanelBuoc2Controller c) {
    	this.controller = c;
    }

    public void refresh(List<Ve> tickets) {
        container.removeAll();
        if (tickets == null || tickets.isEmpty()) {
            container.add(new JLabel("Giỏ vé trống"));
        } else {
            for (Ve v : tickets) {
                JPanel row = createTicketRow(v);
                container.add(row);
            }
        }
        container.revalidate();
        container.repaint();
    }

    private JPanel createTicketRow(Ve v) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JLabel info = new JLabel(String.format("<html><b>%s</b><br/>%s -> %s, %s, Toa %s, Ghế %s</html>",
                v.getChuyen().getTau() == null ? "" : v.getChuyen().getTau().getTauID(),
                v.getChuyen().getTuyen().getGaDi().toString(),
                v.getChuyen().getTuyen().getGaDen().toString(),
                v.getChuyen().getGioDi(),
                v.getGhe().getToa().getHangToa().toString(),
                v.getGhe().getSoGhe()));

        // countdown label placeholder
        JLabel lblTimer = new JLabel(formatRemaining(100));
        lblTimer.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));

        JButton btnTrash = new JButton("\uD83D\uDDD1"); // trash icon char
        btnTrash.setToolTipText("Xóa vé / hủy giữ chỗ");
        btnTrash.addActionListener(e -> {
            if (controller != null) controller.onRemoveTicket(v);
        });

        row.add(info, BorderLayout.CENTER);
        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        east.add(lblTimer);
        east.add(btnTrash);
        row.add(east, BorderLayout.EAST);

        if (controller != null)
        	controller.registerCountdownLabelForTicket(v, lblTimer);

        return row;
    }

    private String formatRemaining(long seconds) {
        if (seconds <= 0) return "00:00";
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}