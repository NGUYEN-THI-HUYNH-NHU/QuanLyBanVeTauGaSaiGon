package gui.application.form.banVe;
/*
 * @(#) TicketWizardPanel.java  1.0  [10:07:42 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controller.BanVe_CTRL;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class TicketWizardPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final JButton btnBack = new JButton("Quay lại");
    private final JButton btnNext = new JButton("Tiếp theo");
    private final JButton btnConfirm = new JButton("Xác nhận");
    private final JButton btnCancel = new JButton("Hủy");
    private final BanVe_CTRL controller;

    // simple steps
    private final JPanel step1 = new JPanel(new BorderLayout());
    private final JPanel step2 = new JPanel(new BorderLayout());
    private final JPanel step3 = new JPanel(new BorderLayout());

    public TicketWizardPanel(BanVe_CTRL controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // build cards (in real app, each is a separate class)
        step1.add(new JLabel("Bước 1 - Tìm chuyến (demo)"), BorderLayout.CENTER);
        step2.add(new JLabel("Bước 2 - Chọn chỗ (demo)"), BorderLayout.CENTER);
        step3.add(new JLabel("Bước 3 - Thông tin khách hàng (demo)"), BorderLayout.CENTER);

        cards.add(step1, "step1");
        cards.add(step2, "step2");
        cards.add(step3, "step3");

        add(cards, BorderLayout.CENTER);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nav.add(btnBack);
        nav.add(btnNext);
        nav.add(btnConfirm);
        nav.add(btnCancel);
        add(nav, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> controller.wizardBack());
        btnNext.addActionListener(e -> controller.wizardNext());
        btnConfirm.addActionListener(e -> controller.wizardConfirm());
        btnCancel.addActionListener(e -> controller.wizardCancel());

        showStep(1);
    }

    public void showStep(int step) {
        switch (step) {
            case 1 -> cardLayout.show(cards, "step1");
            case 2 -> cardLayout.show(cards, "step2");
            case 3 -> cardLayout.show(cards, "step3");
            default -> cardLayout.show(cards, "step1");
        }
    }

    // convenience method to reset wizard
    public void reset() {
        showStep(1);
    }
}