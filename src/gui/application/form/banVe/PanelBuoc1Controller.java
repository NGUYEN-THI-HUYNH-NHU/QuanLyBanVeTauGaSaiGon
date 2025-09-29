package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1Controller.java  1.0  [10:42:13 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener; 

import bus.Chuyen_BUS;
import entity.Chuyen;
import entity.Ga;

public class PanelBuoc1Controller {

    private final PanelBuoc1 view;
    private final Chuyen_BUS chuyenBUS;
    private final int DEBOUNCE_MS = 300;

    private final javax.swing.Timer debounceTimerGaDi;
    private final javax.swing.Timer debounceTimerGaDen;

    public PanelBuoc1Controller(PanelBuoc1 view, Chuyen_BUS chuyenBUS) {
        this.view = view;
        this.chuyenBUS = chuyenBUS;

        // timers: restart on document changes
        debounceTimerGaDi = new javax.swing.Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGaDiSuggestions());
        debounceTimerGaDi.setRepeats(false);

        debounceTimerGaDen = new javax.swing.Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGaDenSuggestions());
        debounceTimerGaDen.setRepeats(false);

        attachListeners();
    }

    private void attachListeners() {
        // Document listeners for txtGaDi
        view.addDocumentListenerToGaDi(new DocumentListener() {
            private void handleSearch() {
                view.getTxtGaDi().putClientProperty("gaId", null);
                debounceTimerGaDi.restart();
            }
            @Override public void insertUpdate(DocumentEvent e) { handleSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { handleSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { handleSearch(); }
        });

        // Document listeners for txtGaDen
        view.addDocumentListenerToGaDen(new DocumentListener() {
            private void handleSearch() {
                view.getTxtGaDen().putClientProperty("gaId", null);
                debounceTimerGaDen.restart();
            }
            @Override public void insertUpdate(DocumentEvent e) { handleSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { handleSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { handleSearch(); }
        });

        // button tìm kiếm
        view.getBtnTimKiem().addActionListener(ev -> performSearch());

        // (Optional) you can set a SearchResultListener on view to receive results elsewhere
    }

    // ---------------- Suggestions fetch ----------------
    private void fetchGaDiSuggestions() {
        final String prefix = view.getGaDiText().trim();
        if (prefix.isEmpty()) {
            view.hideCurrentPopup();
            return;
        }

        new SwingWorker<List<Ga>, Void>() {
            @Override
            protected List<Ga> doInBackground() throws Exception {
                // gọi BUS
                return chuyenBUS.goiYGaDI(prefix, 10);
            }

            @Override
            protected void done() {
                try {
                    List<Ga> list = get();
                    if (list == null || list.isEmpty()) {
                        view.hideCurrentPopup();
                        return;
                    }
                    // Hiển thị gợi ý — nhưng cần để menu item gắn gaId
                    SwingUtilities.invokeLater(() -> showGaSuggestionsWithIds(view.getTxtGaDi(), list));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void fetchGaDenSuggestions() {
        final String prefix = view.getGaDenText().trim();
        if (prefix.isEmpty()) {
            view.hideCurrentPopup();
            return;
        }

        Object gaDiIdObj = view.getTxtGaDi().getClientProperty("gaId");
        if (gaDiIdObj == null) {
            // fallback: global stations
            new SwingWorker<List<Ga>, Void>() {
                @Override
                protected List<Ga> doInBackground() throws Exception {
                    return chuyenBUS.goiYGaDI(prefix, 10);
                }
                @Override
                protected void done() {
                    try {
                        List<Ga> list = get();
                        if (list == null || list.isEmpty()) { view.hideCurrentPopup(); return; }
                        SwingUtilities.invokeLater(() -> showGaSuggestionsWithIds(view.getTxtGaDen(), list));
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }.execute();
            return;
        }

        final String gaDiID = gaDiIdObj.toString();
        final LocalDate ngayDi = safeGetNgayDiOrToday();

        new SwingWorker<List<Ga>, Void>() {
            @Override
            protected List<Ga> doInBackground() throws Exception {
                return chuyenBUS.goiYGaDenTheoGaDi(gaDiID, prefix, 10);
            }

            @Override
            protected void done() {
                try {
                    List<Ga> list = get();
                    if (list == null || list.isEmpty()) { view.hideCurrentPopup(); return; }
                    SwingUtilities.invokeLater(() -> showGaSuggestionsWithIds(view.getTxtGaDen(), list));
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // Helper to build popup items with attached gaId
    private void showGaSuggestionsWithIds(javax.swing.JTextField textField, List<Ga> gaList) {
        view.hideCurrentPopup();
        if (gaList == null || gaList.isEmpty()) return;

        javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
        for (Ga g : gaList) {
            JMenuItem item = new JMenuItem(g.getTenGa());
            item.addActionListener(ae -> {
                textField.setText(g.getTenGa());
                textField.putClientProperty("gaId", g.getGaID());
                view.hideCurrentPopup();
            });
            popup.add(item);
        }
        popup.show(textField, 0, textField.getHeight());
        // keep reference in view
        // the view's hideCurrentPopup() will close it when needed; but controller shows it directly here
        // To keep same behavior, also set view.currentPopup via reflection isn't good — instead reuse view.showGaSuggestions is not used here.
        // But we can set view's currentPopup by calling view.showGaSuggestions (generic); we need gaList's toString then can't set gaId.
        // So we directly show popup here and also set view.currentPopup by calling view.hideCurrentPopup afterward if needed.
        // Simpler: rely on view.hideCurrentPopup to hide visible popup; but to track this popup, set it to view via a small helper:
        try {
            java.lang.reflect.Field f = PanelBuoc1.class.getDeclaredField("currentPopup");
            f.setAccessible(true);
            f.set(view, popup);
        } catch (Exception ign) {
            // non-critical: if reflection fails, popup will still show but view.hideCurrentPopup may not find it
        }
    }

    // ---------------- Perform search ----------------
    private void performSearch() {
        final Object gaDiIdObj = view.getTxtGaDi().getClientProperty("gaId");
        final Object gaDenIdObj = view.getTxtGaDen().getClientProperty("gaId");

        new SwingWorker<List<Chuyen>, Void>() {
            @Override
            protected List<Chuyen> doInBackground() throws Exception {
                String gaDiId = null, gaDenId = null;

                if (gaDiIdObj != null) {
                    gaDiId = gaDiIdObj.toString();
                } else {
                    Ga ga = chuyenBUS.timGaTheoTenGa(view.getGaDiText().trim());
                    if (ga != null) gaDiId = ga.getGaID();
                }

                if (gaDenIdObj != null) {
                    gaDenId = gaDenIdObj.toString();
                } else {
                    Ga ga = chuyenBUS.timGaTheoTenGa(view.getGaDenText().trim());
                    if (ga != null) gaDenId = ga.getGaID();
                }

                if (gaDiId == null || gaDenId == null) {
                    return Collections.emptyList();
                }

                LocalDate ngayDi = safeGetNgayDiOrToday();

                return chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);
            }

            @Override
            protected void done() {
                try {
                    List<Chuyen> results = get();
                    // notify view / listener
                    view.notifySearchResults(results);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    // safe get ngayDi or today if parsing fails
    private LocalDate safeGetNgayDiOrToday() {
        try {
            return view.getNgayDi();
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}