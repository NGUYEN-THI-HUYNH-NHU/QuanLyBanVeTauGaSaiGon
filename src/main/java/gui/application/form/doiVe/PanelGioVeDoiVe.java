package gui.application.form.doiVe;
/*
 * @(#) PanelGioVeDoiVe.java  1.0  [3:12:41 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.itextpdf.text.Font;
import controller.doiVe.DoiVe2Controller;
import gui.application.form.banVe.VeSession;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelGioVeDoiVe extends JPanel {
    private JPanel container;
    private JLabel lblGioVe;
    private JScrollPane scr;
    private JButton btnMuaVe;

    private DoiVe2Controller mediator;

    public PanelGioVeDoiVe() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(230, 400));
        setBorder(new EmptyBorder(0, 0, 0, 0));

        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        scr = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scr.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        lblGioVe = new JLabel("Giỏ vé");
        lblGioVe.setIcon(new FlatSVGIcon(getClass().getResource("/icon/svg/shopping-cart.svg")));
        lblGioVe.setFont(lblGioVe.getFont().deriveFont(Font.BOLD, 16f));
        lblGioVe.setOpaque(true);
        lblGioVe.setForeground(new Color(0, 145, 212));
        lblGioVe.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 130, 196)));

        add(lblGioVe, BorderLayout.NORTH);
        add(scr, BorderLayout.CENTER);
        add(btnMuaVe = new JButton("Tiếp tục"), BorderLayout.SOUTH);

    }

    public void setMediator(DoiVe2Controller controller) {
        this.mediator = controller;
    }

    public void addBuyButtonListener(ActionListener l) {
        if (btnMuaVe != null && l != null) {
            // Xóa listener cũ (nếu có) để đảm bảo chỉ có 1
            for (ActionListener al : btnMuaVe.getActionListeners()) {
                btnMuaVe.removeActionListener(al);
            }
            btnMuaVe.addActionListener(l);
        }
    }

    public void refresh(List<VeSession> dsVeSession) {
        container.removeAll();

        if (dsVeSession == null || dsVeSession.isEmpty()) {
            JLabel emptyLabel = new JLabel("Giỏ vé trống");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            container.add(emptyLabel);
        } else {
            for (VeSession v : dsVeSession) {
                JPanel row = createVeRow(v);
                container.add(row);
            }
        }

        container.revalidate();
        container.repaint();
    }

    private JPanel createVeRow(VeSession v) {
        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        row.setPreferredSize(new Dimension(230, 80));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        row.setBackground(Color.WHITE);

        JLabel info = new JLabel(v.prettyString());
        info.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        JLabel lblTimer = new JLabel(formatRemaining(100));
        lblTimer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        lblTimer.setForeground(new Color(205, 0, 0));

        JButton btnTrash = new JButton("");
        btnTrash.setIcon(new FlatSVGIcon("icon/svg/delete.svg", 0.40f));
        btnTrash.setToolTipText("Xóa vé");
        btnTrash.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTrash.setFocusable(false);
        btnTrash.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        btnTrash.setBackground(Color.WHITE);
        btnTrash.addActionListener(e -> {
            if (mediator != null) {
                mediator.handleGioVeRemove(v);
            }
        });

        JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        east.setBackground(Color.WHITE);
        east.add(lblTimer);
        east.add(btnTrash);

        row.add(info, BorderLayout.CENTER);
        row.add(east, BorderLayout.EAST);

        if (mediator != null) {
            mediator.registerCountdownLabelForVe(v, lblTimer);
        }

        return row;
    }

    private String formatRemaining(long seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        long m = seconds / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
