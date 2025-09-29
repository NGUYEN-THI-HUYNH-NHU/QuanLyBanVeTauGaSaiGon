package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1Test.java  1.0  [9:20:02 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;

import com.toedter.calendar.JDateChooser;

public class PanelBuoc1Test extends JPanel {

    private static final long serialVersionUID = 1L;

    // UI components
    private final JTextField txtGaDi, txtGaDen;
    private final JDateChooser dateNgayDi, dateNgayVe;
    private final JRadioButton radMotChieu, radKhuHoi;
    private final JButton btnTimKiem;
    private final JPanel container;

    // popup reuse
    private JPopupMenu currentPopup;

    // listener để controller nhận kết quả tìm chuyến
    private SearchResultListener searchResultListener;

    public PanelBuoc1Test() {
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
        dateNgayDi = new JDateChooser();
        dateNgayDi.setDateFormatString("dd/MM/yyyy");
        dateNgayDi.setDate(new java.util.Date());
        container.add(dateNgayDi, gbc);

        // Ngày về
        gbc.gridx = 0;
        gbc.gridy = 4;
        container.add(new JLabel("Ngày về:"), gbc);
        gbc.gridx = 1;
        dateNgayVe = new JDateChooser();
        dateNgayVe.setDateFormatString("dd/MM/yyyy");
        dateNgayVe.setEnabled(false);
        container.add(dateNgayVe, gbc);

        radMotChieu.addActionListener(e -> dateNgayVe.setEnabled(false));
        radKhuHoi.addActionListener(e -> dateNgayVe.setEnabled(true));

        // Nút tìm kiếm
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnTimKiem = new JButton("Tìm chuyến tàu");
        container.add(btnTimKiem, gbc);

        add(container, BorderLayout.CENTER);
    }

    // ---------------- Getters để controller dùng ----------------
    public JTextField getTxtGaDi() { return txtGaDi; }
    public JTextField getTxtGaDen() { return txtGaDen; }
    public JDateChooser getDateNgayDi() { return dateNgayDi; }
    public JDateChooser getDateNgayVe() { return dateNgayVe; }
    public boolean isKhuHoi() { return radKhuHoi.isSelected(); }
    public JButton getBtnTimKiem() { return btnTimKiem; }

    public String getGaDiText() { return txtGaDi.getText(); }
    public String getGaDenText() { return txtGaDen.getText(); }

    public LocalDate getNgayDi() {
        java.util.Date d = dateNgayDi.getDate();
        if (d == null) throw new IllegalStateException("Ngày đi chưa được chọn");
        Instant instant = d.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getNgayVe() {
        java.util.Date d = dateNgayVe.getDate();
        if (d == null) throw new IllegalStateException("Ngày về chưa được chọn");
        Instant instant = d.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ---------------- UI helpers (popup suggestions) ----------------
    // view chịu trách nhiệm hiển thị gợi ý (UI-only). Controller sẽ gọi showGaSuggestions.
    public void showGaSuggestions(javax.swing.JTextField textField, List<?> gaList) {
        hideCurrentPopup();
        if (gaList == null || gaList.isEmpty()) return;

        JPopupMenu popup = new JPopupMenu();
        for (Object o : gaList) {
            // Expect each entry has toString or a getTenGa(); controller can pass domain objects (Ga)
            final String label = o.toString();
            JMenuItem item = new JMenuItem(label);
            item.addActionListener(ae -> {
                textField.setText(label);
                // controller should also set client property "gaId" on selection if needed
                hideCurrentPopup();
            });
            popup.add(item);
        }

        popup.show(textField, 0, textField.getHeight());
        currentPopup = popup;
    }

    public void hideCurrentPopup() {
        if (currentPopup != null && currentPopup.isVisible()) {
            currentPopup.setVisible(false);
        }
        currentPopup = null;
    }

    // ---------------- Search result notification (view -> consumer) ----------------
    // Controller có thể đặt listener để nhận kết quả
    public void setSearchResultListener(SearchResultListener listener) {
        this.searchResultListener = listener;
    }

    // Controller gọi phương thức này để gửi kết quả (UI chỉ thông báo listener)
    public void notifySearchResults(List<?> chuyenList) {
        if (this.searchResultListener != null) {
            this.searchResultListener.onSearchResult(chuyenList);
        } else {
            System.out.println("Search returned " + (chuyenList == null ? 0 : chuyenList.size()) + " chuyến(s).");
        }
    }

    public interface SearchResultListener {
        void onSearchResult(List<?> chuyenList);
    }

    // Utility: cho controller dễ attach DocumentListener (view không tự thêm)
    public void addDocumentListenerToGaDi(DocumentListener dl) {
        txtGaDi.getDocument().addDocumentListener(dl);
    }
    public void addDocumentListenerToGaDen(DocumentListener dl) {
        txtGaDen.getDocument().addDocumentListener(dl);
    }
}