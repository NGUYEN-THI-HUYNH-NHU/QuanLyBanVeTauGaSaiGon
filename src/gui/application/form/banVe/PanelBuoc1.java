package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1.java  1.0  [10:38:53 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class PanelBuoc1 extends JPanel {
    private PanelBuoc1Controller controller;
    private JTextField txtGaDi, txtGaDen;
    private JDateChooser dateChooserNgayDi, dateChooserNgayVe;
    private JRadioButton radMotChieu, radKhuHoi;
    private JButton btnTimKiem;
    private JPanel container;

    public PanelBuoc1() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Tìm kiếm chuyến"));

        // Form
        container = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ga đi
        gbc.gridx = 0;
        gbc.gridy = 0;
        container.add(new JLabel("Ga đi:"), gbc);
        gbc.gridx = 1;
        txtGaDi = new JTextField(15);
        txtGaDi.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đi...");
        container.add(txtGaDi, gbc);

        // Ga đến
        gbc.gridx = 0;
        gbc.gridy = 1;
        container.add(new JLabel("Ga đến:"), gbc);
        gbc.gridx = 1;
        txtGaDen = new JTextField(15);
        txtGaDen.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đến...");
        container.add(txtGaDen, gbc);

        // Loại hành trình
        gbc.gridx = 0;
        gbc.gridy = 2;
        container.add(new JLabel("Loại hành trình:"), gbc);
        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radMotChieu = new JRadioButton("Một chiều", true);
        radKhuHoi = new JRadioButton("Khứ hồi");
        ButtonGroup group = new ButtonGroup();
        group.add(radMotChieu);
        group.add(radKhuHoi);
        radioPanel.add(radMotChieu);
        radioPanel.add(radKhuHoi);
        container.add(radioPanel, gbc);

        // Ngày đi
        gbc.gridx = 0;
        gbc.gridy = 3;
        container.add(new JLabel("Ngày đi:"), gbc);
        gbc.gridx = 1;
        dateChooserNgayDi = new JDateChooser();
        dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayDi.setDate(new java.util.Date());
        container.add(dateChooserNgayDi, gbc);

        // Ngày về
        gbc.gridx = 0;
        gbc.gridy = 4;
        container.add(new JLabel("Ngày về:"), gbc);
        gbc.gridx = 1;
        dateChooserNgayVe = new JDateChooser();
        dateChooserNgayVe.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayVe.setEnabled(false);
        container.add(dateChooserNgayVe, gbc);

        radMotChieu.addActionListener(e -> dateChooserNgayVe.setEnabled(false));
        radKhuHoi.addActionListener(e -> dateChooserNgayVe.setEnabled(true));

        // Nút tìm kiếm
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnTimKiem = new JButton("Tìm chuyến tàu");
        container.add(btnTimKiem, gbc);

        add(container, BorderLayout.CENTER);

        // Document listeners -> chuyển đến controller (controller sẽ được tạo ngay sau)
        txtGaDi.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { controller.handleSearchGaDi(); }
            public void removeUpdate(DocumentEvent e) { controller.handleSearchGaDi(); }
            public void changedUpdate(DocumentEvent e) { controller.handleSearchGaDi(); }
        });

        txtGaDen.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { controller.handleSearchGaDen(); }
            public void removeUpdate(DocumentEvent e) { controller.handleSearchGaDen(); }
            public void changedUpdate(DocumentEvent e) { controller.handleSearchGaDen(); }
        });

        controller = new PanelBuoc1Controller(this);
    }

    // ---------- Getters cho Controller su dung ----------
    public JTextField getTxtGaDi() { return txtGaDi; }
    public JTextField getTxtGaDen() { return txtGaDen; }
    public JDateChooser getDateNgayDi() { return dateChooserNgayDi; }
    public JDateChooser getDateNgayVe() { return dateChooserNgayVe; }
    public boolean isKhuHoi() { return radKhuHoi.isSelected(); }
    public JButton getBtnTimKiem() { return btnTimKiem; }

    public String getGaDi() { return txtGaDi.getText(); }
    public String getGaDen() { return txtGaDen.getText(); }

    public LocalDate getNgayDi() {
        java.util.Date d = dateChooserNgayDi.getDate();
        if (d == null) return null;
        Instant instant = d.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getNgayVe() {
        java.util.Date d = dateChooserNgayVe.getDate();
        if (d == null) return null;
        Instant instant = d.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}