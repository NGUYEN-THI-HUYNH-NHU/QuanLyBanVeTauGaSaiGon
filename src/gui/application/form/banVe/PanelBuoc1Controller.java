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
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import bus.Chuyen_BUS;
import entity.Ga;
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
    
    private WizardController wizardController;

    public PanelBuoc1Controller(PanelBuoc1 panel) {
        this.panel = panel;
        init();
    }
    
    public void setWizardController(WizardController wizardController) {
        this.wizardController = wizardController;
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
        if (p == popupGaDi) popupGaDi = null;
        if (p == popupGaDen) popupGaDen = null;
    }

    // Tìm chuyến khi bấm nút
    private void performSearch() {
        // 1) Build criteria from UI
        final SearchCriteria criteria = buildSearchCriteriaFromPanel();

        // 2) Quick validation
        if (criteria == null || !criteria.isValidForSearch()) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                    "Vui lòng chọn hoặc nhập đúng Ga đi, Ga đến và Ngày đi.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE));
            return;
        }

        // disable nút tìm kiếm để tránh double-click
        SwingUtilities.invokeLater(() -> panel.getBtnTimKiem().setEnabled(false));

        new SwingWorker<List<Chuyen>, Void>() {
            protected List<Chuyen> doInBackground() {
                try {
                    String gaDiId = criteria.getGaDiId();
                    String gaDenId = criteria.getGaDenId();

                    // Nếu id chưa resolve, thử resolve bằng tên
                    if (gaDiId == null || gaDiId.trim().isEmpty()) {
                        String name = criteria.getGaDiName();
                        if (name != null && !name.trim().isEmpty()) {
                            Ga g = chuyenBUS.timGaTheoTenGa(name);
                            if (g != null) gaDiId = g.getGaID();
                        }
                    }
                    if (gaDenId == null || gaDenId.trim().isEmpty()) {
                        String name = criteria.getGaDenName();
                        if (name != null && !name.trim().isEmpty()) {
                            Ga g = chuyenBUS.timGaTheoTenGa(name);
                            if (g != null) gaDenId = g.getGaID();
                        }
                    }

                    // Nếu vẫn không resolve được id -> trả về empty (handled in done())
                    if (gaDiId == null || gaDenId == null) {
                        return Collections.emptyList();
                    }

                    LocalDate ngayDi = criteria.getNgayDi();
                    if (ngayDi == null) ngayDi = LocalDate.now();

                    // gọi BUS chính để tìm chuyến
                    return chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    return Collections.emptyList();
                }
            }

            protected void done() {
                try {
                    List<Chuyen> results = get();
                    panel.getBtnTimKiem().setEnabled(true);

                    if (results == null || results.isEmpty()) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                                "Không tìm thấy chuyến phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE));
                        return;
                    }

                    // --- Lưu vào BookingSession thông qua WizardController ---
                    if (wizardController == null) {
                        // Nếu wizard chưa được set -> báo lỗi dev, nhưng vẫn show kết quả
                        System.err.println("PanelBuoc1Controller.performSearch: wizard is null. Hãy setWizardController(...) từ nơi tạo UI.");
                        // fallback: chỉ show count
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                                "Tìm được " + results.size() + " chuyến (wizard chưa được kết nối).", "Kết quả", JOptionPane.INFORMATION_MESSAGE));
                        return;
                    }

                    BookingSession session = wizardController.getBookingSession();
                    // Lưu criteria: lưu cả phiên bản resolved id (nếu có) — rebuild criteria with resolved ids
                    SearchCriteria resolvedCriteria = new SearchCriteria.Builder()
                            .gaDiId(criteria.getGaDiId() != null ? criteria.getGaDiId() : null)
                            .tenGaDi(criteria.getGaDiName())
                            .gaDenId(criteria.getGaDenId() != null ? criteria.getGaDenId() : null)
                            .tenGaDen(criteria.getGaDenName())
                            .ngayDi(criteria.getNgayDi())
                            .ngayVe(criteria.getNgayVe())
                            .khuHoi(criteria.isKhuHoi())
                            .build();

                    session.setOutboundCriteria(resolvedCriteria);
                    session.setOutboundResults(results);

                    wizardController.goToStep(2, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    panel.getBtnTimKiem().setEnabled(true);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                            "Lỗi khi tìm chuyến: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                }
            }
        }.execute();
    }
    
    private SearchCriteria buildSearchCriteriaFromPanel() {
        String gaDiId = selectedGaDi;
        String gaDenId = selectedGaDen;

        SearchCriteria criteria = new SearchCriteria.Builder()
                .gaDiId(gaDiId)
                .tenGaDi(panel.getGaDi())
                .gaDenId(gaDenId)
                .tenGaDen(panel.getGaDen())
                .ngayDi(panel.getNgayDi())
                .ngayVe(panel.getNgayVe())
                .khuHoi(panel.isKhuHoi())
                .build();
        return criteria;
    }
}