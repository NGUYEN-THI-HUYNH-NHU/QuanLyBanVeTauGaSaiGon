package controller.hoanVe;
/*
 * @(#) HoanVe2Controller.java  1.0  [3:22:09 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */

import bus.HoanVe_BUS;
import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import gui.application.form.hoanVe.PanelHoanVe2;
import gui.application.form.hoanVe.PanelHoanVeBuoc4;
import gui.application.form.hoanVe.PanelHoanVeBuoc5;
import gui.application.form.hoanVe.VeHoanRow;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class HoanVe2Controller {
    private final PanelHoanVe2 view;
    private final PanelHoanVeBuoc4 p4;
    private final PanelHoanVeBuoc5 p5;

    private final HoanVe_BUS hoanVeBUS = new HoanVe_BUS();

    private DonDatChoDTO donDatCho;
    private KhachHangDTO khachHang;
    private List<VeHoanRow> listVeHoanRow;

    // Listener để báo cho wizard chính (PanelHoanVe) biết khi thanh toán xong
    private Runnable onPaymentSuccessListener;
    private Runnable onReturnListener;

    public HoanVe2Controller(PanelHoanVe2 view) {
        this.view = view;

        this.p4 = view.getPanelHoanVeBuoc4();
        this.p5 = view.getPanelHoanVeBuoc5();

        this.view.getBtnPrev().addActionListener(e -> {
            if (onReturnListener != null) {
                onReturnListener.run();
            }
        });
        // Khởi tạo logic liên kết
        initMediatorLogic();
    }

    public void addPanel2ReturnListener(Runnable listener) {
        this.onReturnListener = listener;
    }

    public void addPanel2PaymentSuccessListener(Runnable listener) {
        this.onPaymentSuccessListener = listener;
    }

    /**
     * Được gọi bởi PanelHoanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
     * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
     *
     */
    public void loadDataForConfirmation(DonDatChoDTO donDatCho, KhachHangDTO khachHang, List<VeHoanRow> listVeHoanRow) {
        this.donDatCho = donDatCho;
        this.khachHang = khachHang;
        this.listVeHoanRow = listVeHoanRow;

        // 1. Đặt lại trạng thái
        p4.setComponentsEnabled(true);
        p5.setComponentsEnabled(true);

        // 2. Tải dữ liệu vào bảng xác nhận (Buoc4)
        p4.hienThiThongTin(listVeHoanRow);

        // 3. Tính toán chi tiết thanh toán
        int tongTienVe = 0;
        int tongPhiHoan = 0;

        for (VeHoanRow row : listVeHoanRow) {
            tongTienVe += row.getVe().getGia();
            tongPhiHoan += row.getLePhiHoanVe();
        }

        // 4. Đẩy chi tiết thanh toán vào Buoc5
        p5.setChiTietThanhToan(tongTienVe, tongPhiHoan);
        p5.getRadTienMat().doClick();
    }

    /**
     * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
     */
    private void initMediatorLogic() {
        JButton payButtonCash = p5.getBtnXacNhanHoanVe();

        ActionListener paymentListener = e -> {
            if (this.khachHang == null || this.listVeHoanRow == null || this.listVeHoanRow.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Dữ liệu hoàn vé không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double tongTienHoan = p5.getTongTienHoan();

            // Tạo SwingWorker để xử lý DB (tránh đơ UI)
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        return hoanVeBUS.thucHienHoanVe(donDatCho, khachHang, listVeHoanRow, tongTienHoan);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        boolean saveSuccess = get();
                        if (saveSuccess) {
                            // a. Vô hiệu hóa PanelBuoc5
                            p5.setComponentsEnabled(false);

                            // b. Xuất PDF (Nếu cần)
                            // PdfTicketExporter exporter = new PdfTicketExporter();
                            // exporter.exportReturnReceipt(currentKhachHang, currentListVeHoan);

                            JOptionPane.showMessageDialog(view, "Hoàn vé thành công!", "Thông báo",
                                    JOptionPane.INFORMATION_MESSAGE);

                            // c. Báo hoàn tất
                            if (onPaymentSuccessListener != null) {
                                onPaymentSuccessListener.run();
                            }
                        } else {
                            JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin hoàn vé vào CSDL!", "Lỗi",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }.execute();
        };

        if (payButtonCash != null) {
            payButtonCash.addActionListener(paymentListener);
        }
    }
}