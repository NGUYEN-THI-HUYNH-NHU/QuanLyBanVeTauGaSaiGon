package controller.doiVe;

import bus.DoiVe_BUS;
import bus.KhuyenMai_BUS;
import dto.GiaoDichThanhToanDTO;
import gui.application.AppHttpServer;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.*;
import gui.application.paymentHelper.PdfTicketExporter;
import gui.application.paymentHelper.VietQRService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DoiVe3Controller {
    private final PanelDoiVe3 view;
    private final PanelDoiVeBuoc7 p7;
    private final PanelDoiVeBuoc8 p8;

    private final DoiVe_BUS doiVeBUS = new DoiVe_BUS();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();

    private final ExchangeSession exchangeSession;

    private JDialog zoomDialog; // Lưu tham chiếu để tắt dialog này từ xa
    private String currentMaGiaoDich; // Lưu mã để ảnh to và nhỏ dùng chung 1 mã
    private String currentNoiDungCK;

    // Listener để báo cho wizard chính (PanelBanVe) biết
    private Runnable onPanel3ReturnListener;
    private Runnable onPaymentSuccessListener;

    public DoiVe3Controller(PanelDoiVe3 view, ExchangeSession session) {
        this.view = view;
        this.exchangeSession = session;

        this.p7 = view.getPanelDoiVeBuoc7();
        this.p8 = view.getPanelDoiVeBuoc8();

        this.view.getBtnPrev().addActionListener(e -> {
            if (onPanel3ReturnListener != null) {
                onPanel3ReturnListener.run();
            }
        });

        this.p7.setKhuyenMaiProvider((veSession) -> {
            return khuyenMaiBUS.getDanhSachKhuyenMaiPhuHop(veSession);
        });

        this.p7.addTableUpdateListener((e) -> {
            updatePaymentInfo();
        });

        this.p7.getTable()
                .removeColumn(this.p7.getTable().getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI));
        this.p7.getTable().removeColumn(
                this.p7.getTable().getColumnModel().getColumn(MappingVeTableModel.COL_CHON_PHIEU_VIP - 1));

        // Khởi tạo logic liên kết
        initMediatorLogic();
    }

    public void addPanel3ReturnListener(Runnable listener) {
        this.onPanel3ReturnListener = listener;
    }

    public void addPanel3PaymentSuccessListener(Runnable listener) {
        this.onPaymentSuccessListener = listener;
    }

    private void updatePaymentInfo() {
        int tongTienVeCu = 0;
        int tongTienVeMoi = 0;
        int tongGiamKhuyenMai = 0;
        int tongTienDichVu = 0;
        int tongPhiDoiVe = 0;

        List<VeDoiRow> listVeDoi = exchangeSession.getListVeCuCanDoi();
        List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
        for (VeDoiRow veDoi : listVeDoi) {
            tongTienVeCu += veDoi.getVe().getGia();
            tongPhiDoiVe += veDoi.getLePhiDoiVe();
        }
        for (VeSession veMoi : listVeMoi) {
            tongTienVeMoi += veMoi.getVe().getGia();
            tongGiamKhuyenMai += veMoi.getGiamKM();
            tongTienDichVu += veMoi.getPhiPhieuDungPhongChoVIP();
        }

        // Cập nhật lại UI PanelDoiVeBuoc8
        p8.setChiTietThanhToan(tongTienVeCu, tongTienVeMoi, tongGiamKhuyenMai, tongTienDichVu, tongPhiDoiVe);

        if (p8.getTongThanhToan() <= 0) {
            p8.setEnableChuyenKhoan(false);
            p8.selectTienMat();
        } else {
            p8.setEnableChuyenKhoan(true);
        }
    }

    /**
     * Được gọi bởi PanelDoiVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
     * liệu từ session, tính toán và đổ vào Buoc7, Buoc8.
     */
    public void loadDataForConfirmation() {
        // 1. Đặt lại trạng thái
        p7.setComponentsEnabled(true);
        p8.setComponentsEnabled(true);

        // 2. Tải dữ liệu vào bảng xác nhận (Buoc7)
        p7.hienThiThongTin(exchangeSession);

        updatePaymentInfo();
    }

    /**
     * Hàm nội bộ để kết nối logic giữa Buoc7 và Buoc8
     */
    private void initMediatorLogic() {
        // TẠO MỘT LISTENER CHUNG CHO VIỆC ĐỔI PHƯƠNG THỨC
        ActionListener switchPaymentModeListener = e -> {
            if (p8.isThanhToanTienMat()) {
                stopPaymentServer();
                p8.getLblQRCodeDisplay().setIcon(null);
                p8.getLblQRCodeDisplay().setText("Đang tải mã QR...");
                // Tắt ảnh to nếu đang mở
                if (zoomDialog != null && zoomDialog.isVisible()) {
                    closePaymentDialog();
                }
            } else {
                startPaymentListening();
            }
        };
        if (p8.getRadTienMat() != null) {
            p8.getRadTienMat().addActionListener(switchPaymentModeListener);
        }
        if (p8.getRadChuyenKhoan() != null) {
            p8.getRadChuyenKhoan().addActionListener(switchPaymentModeListener);
        }

        // Lắng nghe nút thanh toán từ PanelDoiVeBuoc8
        p8.getBtnXacNhanVaInCash().addActionListener(e -> {
            boolean isThanhToanTienMat = p8.isThanhToanTienMat();
            double tongTien = p8.getTongThanhToan();
            double tienNhan = 0;
            double tienHoan = 0;

            GiaoDichThanhToanDTO giaoDich = new GiaoDichThanhToanDTO();
            giaoDich.setTongTien(tongTien);
            giaoDich.setThanhToanTienMat(isThanhToanTienMat);

            if (isThanhToanTienMat) {
                if (tongTien >= 0) {
                    tienNhan = p8.getTienKhachDua();
                    tienHoan = tienNhan - tongTien;
                } else {
                    tienNhan = 0;
                    tienHoan = Math.abs(tongTien);
                }

                giaoDich.setTongTien(tongTien);
                giaoDich.setTienNhan(tienNhan);
                giaoDich.setTienHoan(tienHoan);

                processPaymentAndSave(giaoDich);
            }
        });

        // 1. Lắng nghe sự kiện chuyển tab (Radio Button)
        p8.getRadChuyenKhoan().addActionListener(e -> {
            if (p8.isThanhToanTienMat()) {
                return;
            }
            // Gọi hàm bắt đầu lắng nghe và hiện QR nhỏ
            startPaymentListening();
        });

        // 2. KHI BẤM VÀO ẢNH NHỎ -> CHỈ HIỆN ẢNH TO (KHÔNG TẠO GIAO DỊCH MỚI)
        if (p8.getLblQRCodeDisplay() != null) {
            p8.getLblQRCodeDisplay().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!p8.isThanhToanTienMat() && p8.getLblQRCodeDisplay().getIcon() != null
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
        double tongTien = p8.getTongThanhToan();
        currentMaGiaoDich = "DOIVE" + System.currentTimeMillis();
        currentNoiDungCK = "TT " + currentMaGiaoDich;

        System.out.println("--- BẮT ĐẦU THANH TOÁN QR ---");
        System.out.println("Mã mong đợi: " + currentMaGiaoDich);

        // Cờ để tránh xử lý 2 lần
        final boolean[] isProcessed = {false};
        final GiaoDichThanhToanDTO giaoDich = new GiaoDichThanhToanDTO();
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
                    p8.getLblQRCodeDisplay().setIcon(null);
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

        // 4. Tải ảnh QR NHỎ (qr_only)
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
                    p8.setQRCodeImage(get());
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

        double tongTien = p8.getTongThanhToan();
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
    private void processPaymentAndSave(GiaoDichThanhToanDTO giaoDich) {
        exchangeSession.setGiaoDichThanhToan(giaoDich);

        // Vô hiệu hóa nút để tránh bấm nhiều lần
        p8.setComponentsEnabled(false);

        // Thực thi giao dịch trong SwingWorker
        new SwingWorker<Boolean, Void>() {
            private String errorMessage = "Lỗi không xác định";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return doiVeBUS.thucHienDoiVe(exchangeSession);
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
                        int choice = JOptionPane.showConfirmDialog(view,
                                "Đổi vé thành công! Bạn có muốn in vé mới ngay không?", "In vé",
                                JOptionPane.YES_NO_OPTION);

                        if (choice == JOptionPane.YES_OPTION) {
                            PdfTicketExporter exporter = new PdfTicketExporter();
                            exporter.exportTicketsToPdf(exchangeSession);
                        }

                        p7.setComponentsEnabled(false);
                        p8.setComponentsEnabled(false);

                        // b. Báo cho wizard chính (PanelBanVe) biết
                        if (onPaymentSuccessListener != null) {
                            onPaymentSuccessListener.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!\n" + errorMessage, "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        p8.setComponentsEnabled(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    p8.setComponentsEnabled(true);
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