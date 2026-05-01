package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc1Controller.java  1.0  [5:29:47 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import bus.DonDatCho_BUS;
import bus.KhachHang_BUS;
import bus.PhieuDungPhongVIP_BUS;
import bus.Ve_BUS;
import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import dto.PhieuDungPhongVIPDTO;
import dto.VeDTO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class DoiVeBuoc1Controller {
    private final PanelDoiVeBuoc1 panel;

    private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final PhieuDungPhongVIP_BUS phieuDungPhongVIPBUS = new PhieuDungPhongVIP_BUS();
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private SearchListener searchListener;

    public DoiVeBuoc1Controller(PanelDoiVeBuoc1 panel) {
        this.panel = panel;
        init();
    }

    public void addSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    private void init() {
        // 1. Tự động focus vào ô Mã ĐĐC khi mở
        SwingUtilities.invokeLater(() -> {
            panel.getTxtMaDDC().requestFocusInWindow();
        });

        // 2. Gán sự kiện cho nút Tra cứu
        panel.getBtnTraCuu().addActionListener(e -> performSearch());

        // 3. Xử lý phím Enter
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        // --- Xử lý Enter trên txtMaDDC (chuyển focus xuống txtCCCD) ---
        InputMap imMaDDC = panel.getTxtMaDDC().getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap amMaDDC = panel.getTxtMaDDC().getActionMap();

        imMaDDC.put(enterKey, "focusNext");
        amMaDDC.put("focusNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.getTxtCCCD().requestFocusInWindow();
            }
        });

        // --- Xử lý Enter trên txtCCCD (tương tự click nút Tra cứu) ---
        InputMap imCCCD = panel.getTxtCCCD().getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap amCCCD = panel.getTxtCCCD().getActionMap();

        imCCCD.put(enterKey, "triggerSearch");
        amCCCD.put("triggerSearch", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kích hoạt sự kiện click của nút
                // Nút sẽ gọi performSearch() thông qua ActionListener đã gán ở trên
                panel.getBtnTraCuu().doClick();
            }
        });
    }

    public void performSearch() {
        String maDDC = panel.getTxtMaDDC().getText().trim();
        String cccd = panel.getTxtCCCD().getText().trim();

        panel.getBtnTraCuu().setEnabled(false);

        new SwingWorker<DonDatChoDTO, Void>() {
            @Override
            protected DonDatChoDTO doInBackground() throws Exception {
                DonDatChoDTO donDatCho = null;
                donDatCho = donDatChoBUS.timDonDatChoTheoIDVaSoGiayTo(maDDC, cccd);
                return donDatCho;
            }

            @Override
            protected void done() {
                try {
                    DonDatChoDTO donDatCho = get();
                    if (donDatCho != null) {
                        // Nếu tìm thấy -> lấy vé và khách hàng
                        List<VeDTO> danhSachVe = veBUS.timCacVeTheoDonDatChoID(maDDC);
                        List<PhieuDungPhongVIPDTO> danhSachPhieu = phieuDungPhongVIPBUS.timCacPhieuTheoVe(danhSachVe);
                        KhachHangDTO khachHang = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccd);

                        panel.getBtnTraCuu().setEnabled(true);

                        // Báo cho Mediator (DoiVe1Controller)
                        if (searchListener != null) {
                            searchListener.onSearchSuccess(donDatCho, danhSachVe, danhSachPhieu, khachHang);
                        }
                    } else {
                        // Xử lý khi tra cứu thành công nhưng không tìm thấy kết quả
                        panel.getBtnTraCuu().setEnabled(true);
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                                "Không tìm thấy đơn đặt chỗ với thông tin cung cấp.", "Không tìm thấy",
                                JOptionPane.INFORMATION_MESSAGE));

                        if (searchListener != null) {
                            searchListener.onSearchFailure();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    panel.getBtnTraCuu().setEnabled(true);
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
                            "Lỗi khi tìm đơn đặt chỗ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                    if (searchListener != null) {
                        searchListener.onSearchFailure();
                    }
                }
            }
        }.execute();
    }

    // Interface để DoiVe1Controller (Mediator) lắng nghe
    protected interface SearchListener {
        void onSearchSuccess(DonDatChoDTO donDatCho, List<VeDTO> danhSachVe, List<PhieuDungPhongVIPDTO> danhSachPhieu,
                             KhachHangDTO khachHang);

        void onSearchFailure();
    }
}