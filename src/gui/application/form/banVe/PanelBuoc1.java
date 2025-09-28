package gui.application.form.banVe;
/*
 * @(#) SearchTripPanel.java  1.0  [10:38:53 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */


import javax.swing.border.TitledBorder;
import com.toedter.calendar.JDateChooser;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class PanelBuoc1 extends JPanel {
    private JTextField txtGaDi, txtGaDen;
    private JDateChooser dateNgayDi, dateNgayVe;
    private JRadioButton rdoMotChieu, rdoKhuHoi;
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
        gbc.gridx = 0; gbc.gridy = 0;
        container.add(new JLabel("Ga đi:"), gbc);
        gbc.gridx = 1;
        txtGaDi = new JTextField(15);
        txtGaDi.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đi...");
        container.add(txtGaDi, gbc);

        // Ga đến
        gbc.gridx = 0; gbc.gridy = 1;
        container.add(new JLabel("Ga đến:"), gbc);
        gbc.gridx = 1;
        txtGaDen = new JTextField(15);
        txtGaDen.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đến...");
        container.add(txtGaDen, gbc);

        // Loại hành trình
        gbc.gridx = 0; gbc.gridy = 2;
        container.add(new JLabel("Loại hành trình:"), gbc);
        gbc.gridx = 1;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rdoMotChieu = new JRadioButton("Một chiều", true);
        rdoKhuHoi = new JRadioButton("Khứ hồi");
        ButtonGroup group = new ButtonGroup();
        group.add(rdoMotChieu);
        group.add(rdoKhuHoi);
        radioPanel.add(rdoMotChieu);
        radioPanel.add(rdoKhuHoi);
        container.add(radioPanel, gbc);

        // Ngày đi
        gbc.gridx = 0; gbc.gridy = 3;
        container.add(new JLabel("Ngày đi:"), gbc);
        gbc.gridx = 1;
        dateNgayDi = new JDateChooser();
        dateNgayDi.setDateFormatString("dd/MM/yyyy");
        dateNgayDi.setDate(new java.util.Date());
        container.add(dateNgayDi, gbc);

        // Ngày về
        gbc.gridx = 0; gbc.gridy = 4;
        container.add(new JLabel("Ngày về:"), gbc);
        gbc.gridx = 1;
        dateNgayVe = new JDateChooser();
        dateNgayVe.setDateFormatString("dd/MM/yyyy");
        dateNgayVe.setEnabled(false);
        container.add(dateNgayVe, gbc);

        rdoMotChieu.addActionListener(e -> dateNgayVe.setEnabled(false));
        rdoKhuHoi.addActionListener(e -> dateNgayVe.setEnabled(true));

        // Nút tìm kiếm
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnTimKiem = new JButton("Tìm chuyến tàu");
        container.add(btnTimKiem, gbc);

        add(container, BorderLayout.CENTER);
    }

    // Getter cho controller
    public JTextField getTxtGaDi() { return txtGaDi; }
    public JTextField getTxtGaDen() { return txtGaDen; }
    public JDateChooser getDateNgayDi() { return dateNgayDi; }
    public JDateChooser getDateNgayVe() { return dateNgayVe; }
    public boolean isKhuHoi() { return rdoKhuHoi.isSelected(); }
    public JButton getBtnTimKiem() { return btnTimKiem; }
    
    public String getGaDi() { return txtGaDi.getText(); }
    public String getGaDen() { return txtGaDen.getText(); }
    public LocalDate getNgayDi() { return LocalDate.parse(dateNgayDi.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    public LocalDate getNgayVe() { return LocalDate.parse(dateNgayVe.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    
    public void showGaDiSuggestions(List<String> suggestions) {
        showSuggestions(txtGaDi, suggestions);
    }

    public void showGaDenSuggestions(List<String> suggestions) {
        showSuggestions(txtGaDen, suggestions);
    }

    private void showSuggestions(JTextField textField, List<String> suggestions) {
        JPopupMenu popup = new JPopupMenu();
        for (String ga : suggestions) {
            JMenuItem item = new JMenuItem(ga);
            item.addActionListener(e -> {
                textField.setText(ga);
                popup.setVisible(false);
            });
            popup.add(item);
        }
        if (!suggestions.isEmpty()) {
            popup.show(textField, 0, textField.getHeight());
        }
    }
}