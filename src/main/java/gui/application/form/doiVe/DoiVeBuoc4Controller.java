package gui.application.form.doiVe;

/*
 * @(#) DoiVeBuoc4Controller.java  1.0  [12:24:37 PM] Nov 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 18, 2025
 * @version: 1.0
 */

import bus.Chuyen_BUS;
import dto.ChuyenDTO;
import gui.application.form.banVe.SearchCriteria;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DoiVeBuoc4Controller {
    private final PanelDoiVeBuoc4 panel;
    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
    private final ExchangeSession session;
    private SearchNewTicketListener searchListener;

    public DoiVeBuoc4Controller(PanelDoiVeBuoc4 panel) {
        this.panel = panel;
        this.session = ExchangeSession.getInstance();
        init();
    }

    public void addSearchListener(SearchNewTicketListener listener) {
        this.searchListener = listener;
    }

    private void init() {
        // Lấy component nhập liệu thực sự bên trong JDateChooser (JTextField)
        JComponent editorNgayDi = panel.getDateNgayDi().getDateEditor().getUiComponent();
        // Gán InputMap/ActionMap cho component đó
        InputMap dateDiIM = editorNgayDi.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap dateDiAM = editorNgayDi.getActionMap();

        dateDiIM.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterOnDateDi");
        dateDiAM.put("enterOnDateDi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.getBtnTimKiem().doClick();
            }
        });

        panel.getBtnTimKiem().addActionListener(e -> performSearch());
    }

    /**
     * Hàm này được gọi bởi Mediator khi PanelDoiVe2 được hiển thị Mục đích: Điền
     * sẵn ga đi/đến từ vé cũ
     */
    public void initDataFromSession() {
        String gaDi = session.getGaDiMacDinh();
        String gaDen = session.getGaDenMacDinh();

        panel.getTxtGaDi().setText(gaDi);
        panel.getTxtGaDen().setText(gaDen);
    }

    public void performSearch() {
        // 1. Lấy thông tin từ Panel (đã được fill sẵn hoặc user chọn ngày)
        LocalDate ngayDi = panel.getNgayDi();
        String tenGaDi = panel.getGaDi();
        String tenGaDen = panel.getGaDen();

        // Lấy ID ga từ Session (vì textfield không chứa ID)
        String maGaDi = session.getGaDiIdMacDinh();
        String maGaDen = session.getGaDenIdMacDinh();

        if (ngayDi == null) {
            JOptionPane.showMessageDialog(panel, "Vui lòng chọn ngày đi.", "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        panel.getBtnTimKiem().setEnabled(false);

        new SwingWorker<List<ChuyenDTO>, Void>() {
            @Override
            protected List<ChuyenDTO> doInBackground() throws Exception {
                // Gọi BUS tìm chuyến (tái sử dụng BUS bán vé)
                List<ChuyenDTO> results = chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(maGaDi, maGaDen, ngayDi);

                // Lọc chuyến chưa khởi hành (ví dụ: sau hiện tại 1 giờ)
                if (results != null) {
                    results.removeIf(c -> !LocalDateTime.now().plusHours(1)
                            .isBefore(LocalDateTime.of(c.getNgayDi(), c.getGioDi())));
                }
                return results;
            }

            @Override
            protected void done() {
                try {
                    List<ChuyenDTO> results = get();
                    panel.getBtnTimKiem().setEnabled(true);

                    if (results == null || results.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "Không tìm thấy chuyến tàu phù hợp.", "Kết quả",
                                JOptionPane.INFORMATION_MESSAGE);
                        if (searchListener != null) {
                            searchListener.onSearchFailure();
                        }
                    } else {
                        // Tạo SearchCriteria để lưu vào session
                        SearchCriteria criteria = new SearchCriteria.Builder().gaDiId(maGaDi).tenGaDi(tenGaDi)
                                .gaDenId(maGaDen).tenGaDen(tenGaDen).ngayDi(ngayDi).khuHoi(false).build();
                        session.setCriteriaTimKiem(criteria);

                        if (searchListener != null) {
                            searchListener.onSearchSuccess(results, criteria);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    panel.getBtnTimKiem().setEnabled(true);
                    JOptionPane.showMessageDialog(panel, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // Interface để DoiVe2Controller (Mediator) lắng nghe
    protected interface SearchNewTicketListener {
        void onSearchSuccess(List<ChuyenDTO> result, SearchCriteria criteria);

        void onSearchFailure();
    }
}