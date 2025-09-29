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

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.toedter.calendar.JDateChooser;

import bus.Chuyen_BUS;
import entity.Chuyen;
import entity.Ga;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

public class PanelBuoc1 extends JPanel {
    private Chuyen_BUS chuyenBUS;
    private javax.swing.Timer debounceTimerGaDi;
    private javax.swing.Timer debounceTimerGaDen;
    private final int DEBOUNCE_MS = 300;

    private JTextField txtGaDi, txtGaDen;
    private JDateChooser dateNgayDi, dateNgayVe;
    private JRadioButton radMotChieu, radKhuHoi;
    private JButton btnTimKiem;
    private JPanel container;

    // popup reuse (so we can hide previous)
    private JPopupMenu currentPopup;

    // listener để controller nhận kết quả tìm chuyến
    private SearchResultListener searchResultListener;

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

        // BUS
        chuyenBUS = new Chuyen_BUS();

        // debounce timers
        debounceTimerGaDi = new javax.swing.Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGaDiSuggestions());
        debounceTimerGaDi.setRepeats(false);

        debounceTimerGaDen = new javax.swing.Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGaDenSuggestions());
        debounceTimerGaDen.setRepeats(false);

        // add document listeners (clear stored gaId when user types)
        txtGaDi.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                // khi user edit, clear gaId selection
                txtGaDi.putClientProperty("gaId", null);
                debounceTimerGaDi.restart();
            }

            public void insertUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void changedUpdate(DocumentEvent e) { changed(); }
        });

        txtGaDen.getDocument().addDocumentListener(new DocumentListener() {
            private void changed() {
                txtGaDen.putClientProperty("gaId", null);
                debounceTimerGaDen.restart();
            }

            public void insertUpdate(DocumentEvent e) { changed(); }
            public void removeUpdate(DocumentEvent e) { changed(); }
            public void changedUpdate(DocumentEvent e) { changed(); }
        });

        // btnTimKiem action
        btnTimKiem.addActionListener(ev -> performSearch());
    }

    // ---------- Getters (sửa getNgayDi/getNgayVe đúng) ----------
    public JTextField getTxtGaDi() { return txtGaDi; }
    public JTextField getTxtGaDen() { return txtGaDen; }
    public JDateChooser getDateNgayDi() { return dateNgayDi; }
    public JDateChooser getDateNgayVe() { return dateNgayVe; }
    public boolean isKhuHoi() { return radKhuHoi.isSelected(); }
    public JButton getBtnTimKiem() { return btnTimKiem; }

    public String getGaDi() { return txtGaDi.getText(); }
    public String getGaDen() { return txtGaDen.getText(); }

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

    // ---------- Suggestions UI ----------
    private void fetchGaDiSuggestions() {
        final String prefix = txtGaDi.getText().trim();
        if (prefix.isEmpty()) {
            hideCurrentPopup();
            return;
        }
        new javax.swing.SwingWorker<List<Ga>, Void>() {
            protected List<Ga> doInBackground() throws Exception {
                return chuyenBUS.goiYGaDI(prefix, 10); // giả sử BUS cung cấp method này
            }
            protected void done() {
                try {
                    List<Ga> list = get();
                    showGaSuggestions(txtGaDi, list);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void fetchGaDenSuggestions() {
        final String prefix = txtGaDen.getText().trim();
        if (prefix.isEmpty()) {
            hideCurrentPopup();
            return;
        }
        Object gaDiIdObj = txtGaDi.getClientProperty("gaId");
        if (gaDiIdObj == null) {
            // fallback: search global stations
            new javax.swing.SwingWorker<List<Ga>, Void>() {
                protected List<Ga> doInBackground() throws Exception {
                    return chuyenBUS.goiYGaDI(prefix, 10);
                }
                protected void done() {
                    try { showGaSuggestions(txtGaDen, get()); } catch (Exception ex) { ex.printStackTrace(); }
                }
            }.execute();
            return;
        }
        final String gaDiID = gaDiIdObj.toString();
        final LocalDate ngayDi = safeGetNgayDiOrToday();
        new javax.swing.SwingWorker<List<Ga>, Void>() {
            protected List<Ga> doInBackground() throws Exception {
                return chuyenBUS.goiYGaDenTheoGaDi(gaDiID, prefix, 10);
            }
            protected void done() {
                try { showGaSuggestions(txtGaDen, get()); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private LocalDate safeGetNgayDiOrToday() {
        try { return getNgayDi(); }
        catch (Exception e) { return LocalDate.now(); }
    }

    private void showGaSuggestions(JTextField textField, List<Ga> gaList) {
        hideCurrentPopup();
        if (gaList == null || gaList.isEmpty()) return;

        JPopupMenu popup = new JPopupMenu();
        for (Ga s : gaList) {
            final Ga ga = s; // effectively final for lambda
            JMenuItem item = new JMenuItem(ga.getTenGa());
            item.addActionListener(ae -> {
                textField.setText(ga.getTenGa());
                textField.putClientProperty("gaId", ga.getGaID());
                hideCurrentPopup();
            });
            popup.add(item);
        }

        // show under textfield
        popup.show(textField, 0, textField.getHeight());
        currentPopup = popup;
    }

    private void hideCurrentPopup() {
        if (currentPopup != null && currentPopup.isVisible()) {
            currentPopup.setVisible(false);
        }
        currentPopup = null;
    }

    // ---------- Search (bấm nút Tìm) ----------
    private void performSearch() {
        // resolve IDs
        Object gaDiIdObj = txtGaDi.getClientProperty("gaId");
        Object gaDenIdObj = txtGaDen.getClientProperty("gaId");

        new javax.swing.SwingWorker<List<Chuyen>, Void>() {
            protected List<Chuyen> doInBackground() throws Exception {
                String gaDiId = null, gaDenId = null;
                if (gaDiIdObj != null) gaDiId = gaDiIdObj.toString();
                else {
                    Ga ga = chuyenBUS.timGaTheoTenGa(txtGaDi.getText().trim());
                    if (ga != null) gaDiId = ga.getGaID();
                }
                if (gaDenIdObj != null) gaDenId = gaDenIdObj.toString();
                else {
                    Ga ga = chuyenBUS.timGaTheoTenGa(txtGaDen.getText().trim());
                    if (ga != null) gaDenId = ga.getGaID();
                }

                if (gaDiId == null || gaDenId == null) {
                    return Collections.emptyList(); // hoặc bạn có thể show dialog lỗi
                }

                LocalDate ngayDi = safeGetNgayDiOrToday();
                return chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);
            }

            protected void done() {
                try {
                    List<Chuyen> results = get();
                    onSearchResultsReady(results);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    // Gọi listener khi có kết quả tìm chuyến
    private void onSearchResultsReady(List<Chuyen> results) {
        if (this.searchResultListener != null) {
            this.searchResultListener.onSearchResult(results);
        } else {
            // default: bạn có thể debug / log
            System.out.println("Search returned " + (results == null ? 0 : results.size()) + " chuyến(s).");
        }
    }

    // allow controller to set listener
    public void setSearchResultListener(SearchResultListener listener) {
        this.searchResultListener = listener;
    }

    public interface SearchResultListener {
        void onSearchResult(List<Chuyen> chuyenList);
    }
}