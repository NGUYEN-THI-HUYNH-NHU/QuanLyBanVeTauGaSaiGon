package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2Controller.java  1.0  [12:05:37 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.BanVe_BUS;
import bus.KhuyenMai_BUS;
import entity.GiaoDichThanhToan;
import entity.Ve;
import entity.type.LoaiDoiTuong;
import gui.application.AppHttpServer;
import gui.application.EmailService;
import gui.application.paymentHelper.PdfTicketExporter;
import gui.application.paymentHelper.VietQRService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller (Mediator) cho PanelBanVe2. Nhiệm vụ: 1. Lấy dữ liệu từ
 * BookingSession. 2. Đổ dữ liệu vào PanelBuoc4 (Xác nhận) và PanelBuoc5 (Chi
 * tiết giá). 3. Lắng nghe sự kiện "Xác nhận Thanh toán" từ PanelBuoc5. 4. Báo
 * cho Wizard (PanelBanVe) khi thanh toán hoàn tất.
 */
public class BanVe2Controller {
    private final PanelBanVe2 view;
    private final PanelBuoc4 p4;
    private final PanelBuoc5 p5;

    private final BanVe_BUS banVeBUS = new BanVe_BUS();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();

    private final BookingSession bookingSession;

    // Listener để báo cho wizard chính (PanelBanVe) biết
    private Runnable onPanel2ReturnListener;
    private Runnable onPaymentSuccessListener;

    private JDialog zoomDialog; // Lưu tham chiếu để tắt dialog này từ xa
    private String currentMaGiaoDich; // Lưu mã để ảnh to và nhỏ dùng chung 1 mã
    private String currentNoiDungCK;

    public BanVe2Controller(PanelBanVe2 view, BookingSession session) {
        this.view = view;
        this.bookingSession = session;

        this.p4 = view.getPanelBuoc4();
        this.p5 = view.getPanelBuoc5();

        this.view.getBtnPrev().addActionListener(e -> {
            if (onPanel2ReturnListener != null) {
                onPanel2ReturnListener.run();
            }
        });

        this.p4.setKhuyenMaiProvider((veSession) -> {
            return khuyenMaiBUS.getDanhSachKhuyenMaiPhuHop(veSession);
        });

        this.p4.addTableUpdateListener((e) -> {
            updatePaymentInfo();
        });

        // Khởi tạo logic liên kết
        initMediatorLogic();
    }

    public void addPanel2ReturnListener(Runnable listener) {
        this.onPanel2ReturnListener = listener;
    }

    public void addPanel2PaymentSuccessListener(Runnable listener) {
        this.onPaymentSuccessListener = listener;
    }

    private void updatePaymentInfo() {
        int tongTienVe = 0;
        double giamGiaDT = 0;
        int khuyenMai = 0;
        int dichVu = 0;

        List<VeSession> allTickets = bookingSession.getAllSelectedTickets();

        for (VeSession ve : allTickets) {
            tongTienVe += ve.getVe().getGia();
            dichVu += ve.getPhiPhieuDungPhongChoVIP();
            khuyenMai += ve.getGiamKM();

            // (Logic giảm đối tượng giữ nguyên)
            if (ve.getVe().getKhachHang().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
                ve.setGiamDoiTuong((int) (Math.round((ve.getVe().getGia() * 0.25) / 1000) * 1000));
                giamGiaDT += ve.getGiamDoiTuong();
            }
        }

        // Cập nhật lại UI PanelBuoc5
        p5.setChiTietThanhToan(tongTienVe, (int) giamGiaDT, khuyenMai, dichVu);
    }

    /**
     * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
     * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
     */
    public void loadDataForConfirmation() {
        p4.setComponentsEnabled(true);
        p5.setComponentsEnabled(true);

        p4.hienThiThongTin(bookingSession);

        updatePaymentInfo();
    }

    /**
     * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
     */
    private void initMediatorLogic() {
        // TẠO MỘT LISTENER CHUNG CHO VIỆC ĐỔI PHƯƠNG THỨC
        ActionListener switchPaymentModeListener = e -> {
            // Chỉ chạy logic khi nguồn phát sự kiện là nút ĐANG ĐƯỢC CHỌN
            // (Bỏ qua sự kiện của nút vừa bị Deselect)
            AbstractButton source = (AbstractButton) e.getSource();
            if (!source.isSelected()) {
                return;
            }

            if (p5.isThanhToanTienMat()) {
                // Chọn Tiền mặt -> Tắt Server
                stopPaymentServer();
                p5.getLblQRCodeDisplay().setIcon(null);
                p5.getLblQRCodeDisplay().setText("Chọn chuyển khoản để hiện QR...");
                if (zoomDialog != null && zoomDialog.isVisible()) {
                    closePaymentDialog();
                }
            } else {
                // Chọn Chuyển khoản -> Bật Server
                startPaymentListening();
            }
        };

        if (p5.getRadTienMat() != null) {
            p5.getRadTienMat().addActionListener(switchPaymentModeListener);
        }
        if (p5.getRadChuyenKhoan() != null) {
            p5.getRadChuyenKhoan().addActionListener(switchPaymentModeListener);
        }

        p5.getBtnXacNhanVaInCash().addActionListener(e -> {
            boolean isThanhToanTienMat = p5.isThanhToanTienMat();
            double tongTien = p5.getTongThanhToan();

            GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
            giaoDich.setTongTien(tongTien);
            giaoDich.setThanhToanTienMat(isThanhToanTienMat);

            if (isThanhToanTienMat) {
                double tienNhan = p5.getTienKhachDua();
                double tienHoan = tienNhan - tongTien;

                giaoDich.setTongTien(tongTien);
                giaoDich.setTienNhan(tienNhan);
                giaoDich.setTienHoan(tienHoan);

                processPaymentAndSave(giaoDich);
            }
        });

        // KHI BẤM VÀO ẢNH NHỎ -> CHỈ HIỆN ẢNH TO (KHÔNG TẠO GIAO DỊCH MỚI)
        if (p5.getLblQRCodeDisplay() != null) {
            p5.getLblQRCodeDisplay().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!p5.isThanhToanTienMat() && p5.getLblQRCodeDisplay().getIcon() != null
                            && currentMaGiaoDich != null) {
                        // Gọi hàm để hiện ảnh to
                        showZoomedQRCode();
                    }
                }
            });
        }
    }

    /**
     * KHỞI TẠO PHIÊN (Core Logic) - Tạo mã giao dịch - Bật Server Casso - Hiện QR
     * nhỏ (qr_only)
     */
    private void startPaymentListening() {
        // 1. Tạo mã giao dịch (Chỉ chữ và số để tránh lỗi)
        double tongTien = p5.getTongThanhToan();
        currentMaGiaoDich = "VETAU" + System.currentTimeMillis();
        currentNoiDungCK = "TT " + currentMaGiaoDich;

        System.out.println("--- BẮT ĐẦU THANH TOÁN QR ---");
        System.out.println("Mã mong đợi: " + currentMaGiaoDich);

        // Cờ để tránh xử lý 2 lần
        final boolean[] isProcessed = {false};
        final GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
        giaoDich.setTongTien(tongTien);
        giaoDich.setThanhToanTienMat(false);

        // 2. ĐĂNG KÝ LẮNG NGHE VÀO SERVER TỔNG
        AppHttpServer.addPaymentListener((content, amount) -> {
            // LOGIC XỬ LÝ TIỀN VỀ (Copy từ listener cũ sang)
            System.out.println(">> Controller nhận được tin: " + content);

            String cleanLog = content.toUpperCase().replaceAll("[^A-Z0-9]", "");
            String cleanMa = currentMaGiaoDich.toUpperCase().replaceAll("[^A-Z0-9]", "");

            if (cleanLog.contains(cleanMa)) {
                System.out.println(">> KHỚP MÃ! TIỀN VỀ!");
                isProcessed[0] = true;

                SwingUtilities.invokeLater(() -> {
                    p5.getLblQRCodeDisplay().setIcon(null);
                    // Tắt ảnh to nếu đang mở
                    if (zoomDialog != null && zoomDialog.isVisible()) {
                        closePaymentDialog();
                    }

                    JOptionPane.showMessageDialog(view,
                            "ĐÃ NHẬN ĐƯỢC TIỀN! (" + String.format("%,.0f", tongTien) + " VNĐ)", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);

                    giaoDich.setTienNhan(tongTien);
                    processPaymentAndSave(giaoDich);
                });
                // [Quan trọng] Sau khi xong thì hủy đăng ký để tránh nhận tin rác
                AppHttpServer.addPaymentListener(null);
            }
        });

        // 3. Tải ảnh QR NHỎ (qr_only)
        VietQRService qrService = new VietQRService();
        String qrUrl = qrService.generateQRUrl(tongTien, currentNoiDungCK, "qr_only");

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                return qrService.getQRCodeImage(qrUrl);
            }

            @Override
            protected void done() {
                try {
                    p5.setQRCodeImage(get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * HIỂN THỊ ẢNH TO (View Logic)
     */
    private void showZoomedQRCode() {
        if (currentMaGiaoDich == null) {
            return;
        }

        double tongTien = p5.getTongThanhToan();
        VietQRService qrService = new VietQRService();
        String qrUrl = qrService.generateQRUrl(tongTien, currentNoiDungCK, "compact");

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Giả lập delay xíu cho mượt nếu mạng quá nhanh
                return qrService.getQRCodeImage(qrUrl);
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        // Tạo giao diện Dialog chờ
                        JLabel lblImage = new JLabel(icon);
                        JLabel lblNote = new JLabel("<html><div style='text-align:center; width: 350px;'>"
                                + "<b style='font-size:16px; color:#0056b3'>QUÉT MÃ ĐỂ THANH TOÁN</b><br/><br/>"
                                + "Số tiền: <b style='color:red; font-size:14px'>" + String.format("%,.0f", tongTien)
                                + " VNĐ</b><br/>" + "Nội dung: <b style='color:green'>" + currentNoiDungCK
                                + "</b><br/><br/>"
                                + "<i>(Vui lòng không tắt bảng này, hệ thống sẽ tự động xác nhận...)</i>"
                                + "</div></html>");
                        lblNote.setHorizontalAlignment(JLabel.CENTER);

                        JPanel panel = new JPanel(new BorderLayout());
                        panel.add(lblNote, BorderLayout.NORTH);
                        panel.add(lblImage, BorderLayout.CENTER);

                        Object[] options = {"Đóng"};
                        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                                JOptionPane.DEFAULT_OPTION, null, options, options[0]);
                        zoomDialog = optionPane.createDialog(view, "Đang chờ thanh toán...");
                        zoomDialog.setModal(false);
                        zoomDialog.setVisible(true);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        // Hiện loading trong lúc chờ worker chạy, chặn thao tác khi đang tải ảnh
        // loadingDialog.setVisible(true);
    }

    private void closePaymentDialog() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog && window.isVisible()) {
                window.dispose();
            }
        }
    }

    /**
     * Lưu giao dịch và in vé Được gọi khi: 1. Thanh toán tiền mặt xong. 2. Web
     * Server nhận được tín hiệu VNPAY thành công. 3. Khách bấm xác nhận thủ công.
     */
    private void processPaymentAndSave(GiaoDichThanhToan giaoDich) {
        bookingSession.setGiaoDichThanhToan(giaoDich);

        // Vô hiệu hóa nút để tránh bấm nhiều lần
        p5.setComponentsEnabled(false);

        // Thực thi giao dịch trong SwingWorker
        new SwingWorker<Boolean, Void>() {
            private String errorMessage = "Lỗi không xác định";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return banVeBUS.thucHienBanVe(bookingSession);
                } catch (Exception ex) {
                    errorMessage = ex.getMessage();
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean saveSuccess = get();
                    if (saveSuccess) {
                        String emailKhach = bookingSession.getKhachHang().getEmail();
                        if (emailKhach != null && !emailKhach.isEmpty()) {
                            // Chạy luồng riêng để gửi email, không chờ đợi
                            new Thread(() -> {
                                List<Ve> listVeDaMua = new ArrayList<>();
                                for (VeSession vs : bookingSession.getAllSelectedTickets()) {
                                    listVeDaMua.add(vs.getVe());
                                }
                                // Gọi service gửi mail
                                EmailService.sendTicketEmail(emailKhach, listVeDaMua, bookingSession.getDonDatCho(),
                                        giaoDich.getTongTien());
                            }).start();
                        }

                        int choice = JOptionPane.showConfirmDialog(view,
                                "Bán vé thành công! Bạn có muốn in vé ngay không?", "In vé", JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            PdfTicketExporter exporter = new PdfTicketExporter();
                            exporter.exportTicketsToPdf(bookingSession);
                        }

                        p4.setComponentsEnabled(false);
                        p5.setComponentsEnabled(false);

                        // Báo cho wizard chính (PanelBanVe) biết để reset hoặc chuyển trang
                        if (onPaymentSuccessListener != null) {
                            onPaymentSuccessListener.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi khi lưu dữ liệu!\n" + errorMessage, "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        p5.setComponentsEnabled(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    p5.setComponentsEnabled(true);
                }
            }
        }.execute();
    }

    private void stopPaymentServer() {
        // Thay vì tắt server, ta chỉ cần hủy đăng ký lắng nghe
        AppHttpServer.addPaymentListener(null);
        System.out.println(">> Đã hủy lắng nghe thanh toán (Server vẫn chạy ngầm).");
    }
}