package controller;
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
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import bus.Chuyen_BUS;
import entity.Ga;
import gui.application.form.banVe.PanelBuoc1;
import entity.Chuyen;

public class PanelBuoc1Controller {

    private final PanelBuoc1 panel;
    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();

    private final int DEBOUNCE_MS = 300;
    private Timer debounceTimerGaDi;
    private Timer debounceTimerGaDen;

    private JPopupMenu popupGaDi;
    private JPopupMenu popupGaDen;

    private String selectedGaDi = null;
    private String selectedGaDen = null;

    // Dùng để ngăn chặn việc gọi handle khi sử dụng setText(...) trong code
    private volatile boolean suppressGaDiChange = false;
    private volatile boolean suppressGaDenChange = false;

    // Đánh dấu người dùng đã "xác nhận" giá trị (chọn gợi ý, nhấn Enter hoặc mất focus)
    private boolean gaDiConfirmed = false;
    private boolean gaDenConfirmed = false;

    // lưu text tại thời điểm confirmed để so sánh khi user chỉnh sửa
    private String lastGaDiConfirmedText = null;
    private String lastGaDenConfirmedText = null;

    public PanelBuoc1Controller(PanelBuoc1 panel) {
        this.panel = panel;
        init();
    }

    private void init() {
        debounceTimerGaDi = new Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGoiYGaDi());
        debounceTimerGaDi.setRepeats(false);

        debounceTimerGaDen = new Timer(DEBOUNCE_MS, (ActionEvent e) -> fetchGoiYGaDen());
        debounceTimerGaDen.setRepeats(false);

        // Ẩn popup khi mất focus VÀ đánh dấu đã xác nhận nếu có nội dung
        panel.getTxtGaDi().addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                String txt = panel.getTxtGaDi().getText().trim();
                if (!txt.isEmpty()) {
                    gaDiConfirmed = true;
                    lastGaDiConfirmedText = txt;
                } else {
                    gaDiConfirmed = false;
                    lastGaDiConfirmedText = null;
                    selectedGaDi = null;
                }
                hidePopup(popupGaDi);
            }
        });
        panel.getTxtGaDen().addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                String txt = panel.getTxtGaDen().getText().trim();
                if (!txt.isEmpty()) {
                    gaDenConfirmed = true;
                    lastGaDenConfirmedText = txt;
                } else {
                    gaDenConfirmed = false;
                    lastGaDenConfirmedText = null;
                    selectedGaDen = null;
                }
                hidePopup(popupGaDen);
            }
        });

        // Nhấn ESC để ẩn popup
        panel.getTxtGaDi().getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "hideGaDiPopup");
        panel.getTxtGaDi().getActionMap().put("hideGaDiPopup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { hidePopup(popupGaDi); }
        });
        panel.getTxtGaDen().getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "hideGaDenPopup");
        panel.getTxtGaDen().getActionMap().put("hideGaDenPopup", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { hidePopup(popupGaDen); }
        });

        // Nhấn Enter để xác nhận giá trị (giống như khi mất focus)
        panel.getTxtGaDi().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmGaDi");
        panel.getTxtGaDi().getActionMap().put("confirmGaDi", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String txt = panel.getTxtGaDi().getText().trim();
                if (!txt.isEmpty()) {
                    gaDiConfirmed = true;
                    lastGaDiConfirmedText = txt;
                }
                hidePopup(popupGaDi);
            }
        });
        panel.getTxtGaDen().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "confirmGaDen");
        panel.getTxtGaDen().getActionMap().put("confirmGaDen", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String txt = panel.getTxtGaDen().getText().trim();
                if (!txt.isEmpty()) {
                    gaDenConfirmed = true;
                    lastGaDenConfirmedText = txt;
                }
                hidePopup(popupGaDen);
            }
        });

        panel.getBtnTimKiem().addActionListener(e -> performSearch());
    }

    // Được gọi khi người dùng gõ vào ô Ga đi
    public void handleSearchGaDi() {
        if (suppressGaDiChange) {
            suppressGaDiChange = false;
            return;
        }

        String txt = panel.getTxtGaDi().getText().trim();

        if (txt.length() < 1) {
            hidePopup(popupGaDi);
            selectedGaDi = null;
            gaDiConfirmed = false;
            lastGaDiConfirmedText = null;
            return;
        }

        // Nếu đã xác nhận trước đó và nội dung không thay đổi -> không hiển thị gợi ý
        if (gaDiConfirmed && lastGaDiConfirmedText != null && lastGaDiConfirmedText.equals(txt)) {
            hidePopup(popupGaDi);
            return;
        }

        gaDiConfirmed = false;
        lastGaDiConfirmedText = null;
        selectedGaDi = null;

        debounceTimerGaDi.restart();
    }

    // Được gọi khi người dùng gõ vào ô Ga đến
    public void handleSearchGaDen() {
        if (suppressGaDenChange) {
            suppressGaDenChange = false;
            return;
        }

        String txt = panel.getTxtGaDen().getText().trim();

        if (txt.length() < 1) {
            hidePopup(popupGaDen);
            selectedGaDen = null;
            gaDenConfirmed = false;
            lastGaDenConfirmedText = null;
            return;
        }

        if (gaDenConfirmed && lastGaDenConfirmedText != null && lastGaDenConfirmedText.equals(txt)) {
            hidePopup(popupGaDen);
            return;
        }

        gaDenConfirmed = false;
        lastGaDenConfirmedText = null;
        selectedGaDen = null;

        debounceTimerGaDen.restart();
    }

    // Gọi API để lấy gợi ý Ga đi
    private void fetchGoiYGaDi() {
        final String prefix = panel.getTxtGaDi().getText().trim();
        if (prefix.length() < 1) return;

        final String currentPrefix = prefix;
        new SwingWorker<List<Ga>, Void>() {
            protected List<Ga> doInBackground() {
                try {
                    return chuyenBUS.goiYGaDi(currentPrefix, 5);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return Collections.emptyList();
                }
            }
            protected void done() {
                try {
                    // ensure user didn't change text meanwhile
                    if (!panel.getTxtGaDi().getText().trim().equals(currentPrefix)) {
                        return;
                    }
                    List<Ga> list = get();
                    showGaDiPopup(list);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
    
    // Gọi API để lấy gợi ý Ga đến
    private void fetchGoiYGaDen() {
        final String prefix = panel.getTxtGaDen().getText().trim();
        if (prefix.length() < 1) return;
        final String currentPrefix = prefix;

        new SwingWorker<List<Ga>, Void>() {
            protected List<Ga> doInBackground() {
                try {
                    if (selectedGaDi != null) {
                        return chuyenBUS.goiYGaDenTheoGaDi(selectedGaDi, currentPrefix, 5);
                    } else {
                        return chuyenBUS.goiYGaDi(currentPrefix, 5);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return Collections.emptyList();
                }
            }
            protected void done() {
                try {
                    if (!panel.getTxtGaDen().getText().trim().equals(currentPrefix)) {
                        return;
                    }
                    List<Ga> list = get();

                    // Loại bỏ Ga đi khỏi danh sách gợi ý Ga đến
                    if (list != null && selectedGaDi != null) {
                        list.removeIf(g -> selectedGaDi.equals(g.getGaID()));
                    }

                    showGaDenPopup(list);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    // Hiển thị popup gợi ý cho Ga đi
    private void showGaDiPopup(List<Ga> list) {
        hidePopup(popupGaDi);
        if (list == null || list.isEmpty()) return;

        // Nếu đã xác nhận trước đó và nội dung không thay đổi -> không hiển thị gợi ý
        String curText = panel.getTxtGaDi().getText().trim();
        if (gaDiConfirmed && lastGaDiConfirmedText != null && lastGaDiConfirmedText.equals(curText)) {
            return;
        }

        popupGaDi = new JPopupMenu();
        popupGaDi.setFocusable(false);

        for (Ga g : list) {
            JMenuItem it = new JMenuItem(g.getTenGa());
            it.setFocusable(false);
            it.addActionListener(ae -> {
                suppressGaDiChange = true;
                selectedGaDi = g.getGaID();
                
                // Set text và đánh dấu xác nhận
                panel.getTxtGaDi().setText(g.getTenGa());
                gaDiConfirmed = true;
                lastGaDiConfirmedText = g.getTenGa();

                // Xóa text ga đến nếu có thay đổi ở ga đi
                selectedGaDen = null;
                suppressGaDenChange = true;
                panel.getTxtGaDen().setText("");
                gaDenConfirmed = false;
                lastGaDenConfirmedText = null;
                hidePopup(popupGaDen);

                hidePopup(popupGaDi);
            });
            popupGaDi.add(it);
        }

        popupGaDi.show(panel.getTxtGaDi(), 0, panel.getTxtGaDi().getHeight());
    }

    // Hiển thị popup gợi ý cho Ga đến
    private void showGaDenPopup(List<Ga> list) {
        hidePopup(popupGaDen);
        if (list == null || list.isEmpty()) return;

        String curText = panel.getTxtGaDen().getText().trim();
        if (gaDenConfirmed && lastGaDenConfirmedText != null && lastGaDenConfirmedText.equals(curText)) {
            return;
        }

        popupGaDen = new JPopupMenu();
        popupGaDen.setFocusable(false);

        for (Ga g : list) {
            JMenuItem it = new JMenuItem(g.getTenGa());
            it.setFocusable(false);
            it.addActionListener(ae -> {
                suppressGaDenChange = true;

                selectedGaDen = g.getGaID();
                panel.getTxtGaDen().setText(g.getTenGa());
                gaDenConfirmed = true;
                lastGaDenConfirmedText = g.getTenGa();

                hidePopup(popupGaDen);
            });
            popupGaDen.add(it);
        }

        popupGaDen.show(panel.getTxtGaDen(), 0, panel.getTxtGaDen().getHeight());
    }

    private void hidePopup(JPopupMenu p) {
        if (p != null && p.isVisible()) p.setVisible(false);
        // also null-out references to avoid stale state
        if (p == popupGaDi) popupGaDi = null;
        if (p == popupGaDen) popupGaDen = null;
    }

    // Tìm chuyến khi bấm nút
    private void performSearch() {
        new SwingWorker<List<Chuyen>, Void>() {
            protected List<Chuyen> doInBackground() {
                try {
                    String gaDiId = selectedGaDi;
                    String gaDenId = selectedGaDen;

                    // Nếu user không chọn suggestion thì thử resolve bằng tên
                    if (gaDiId == null) {
                        String name = panel.getTxtGaDi().getText().trim();
                        if (!name.isEmpty()) {
                            Ga g = chuyenBUS.timGaTheoTenGa(name);
                            if (g != null) gaDiId = g.getGaID();
                        }
                    }
                    if (gaDenId == null) {
                        String name = panel.getTxtGaDen().getText().trim();
                        if (!name.isEmpty()) {
                            Ga g = chuyenBUS.timGaTheoTenGa(name);
                            if (g != null) gaDenId = g.getGaID();
                        }
                    }

                    if (gaDiId == null || gaDenId == null) {
                        // show error on EDT
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                                "Vui lòng chọn hoặc nhập đúng Ga đi và Ga đến.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE));
                        return Collections.emptyList();
                    }

                    LocalDate ngayDi = panel.getNgayDi();
                    if (ngayDi == null) ngayDi = LocalDate.now();
                    return chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return Collections.emptyList();
                }
            }
            protected void done() {
                try {
                    List<Chuyen> results = get();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                            "Tìm được " + (results == null ? 0 : results.size()) + " chuyến."));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}