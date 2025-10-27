package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2Controller.java  1.0  [12:05:37 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */
import javax.swing.JButton;

public class BanVe2Controller {

    private final PanelBanVe2 view; // View gộp (chứa Buoc4 và Buoc5)
    private final BookingSession bookingSession; // Session dữ liệu

    // Các panel con
    private final PanelBuoc4 panelBuoc4;
    private final PanelBuoc5 panelBuoc5;

    // Listener để báo cho wizard chính (PanelBanVe) biết khi thanh toán xong
    private Runnable onPaymentSuccessListener;

    public BanVe2Controller(PanelBanVe2 view, BookingSession session) {
        this.view = view;
        this.bookingSession = session;

        // Giả định PanelBanVe2 có các hàm getter này
        this.panelBuoc4 = view.getPanelBuoc4(); 
        this.panelBuoc5 = view.getPanelBuoc5();

        // Khởi tạo logic liên kết
        initMediatorLogic();
    }

    /**
     * (Hàm bạn yêu cầu)
     * Đăng ký một hành động (Runnable) sẽ được gọi khi thanh toán thành công.
     */
    public void addPaymentSuccessListener(Runnable listener) {
        this.onPaymentSuccessListener = listener;
    }

    /**
     * (Hàm bạn yêu cầu)
     * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị.
     * Nhiệm vụ: Lấy dữ liệu từ session và đổ vào PanelBuoc4.
     */
    public void loadDataForConfirmation() {
        // 1. Đặt lại trạng thái
        // Buoc4 phải được kích hoạt, Buoc5 phải bị vô hiệu hóa
        panelBuoc4.setComponentsEnabled(true);
        panelBuoc5.setComponentsEnabled(false);

        // 2. Tải dữ liệu
        // Giả sử PanelBuoc4 có một hàm để nhận BookingSession
        // và tự hiển thị thông tin xác nhận.
//        panelBuoc4.hienThiThongTinVe(bookingSession);
    }

    /**
     * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
     */
    private void initMediatorLogic() {
    	// a. Vô hiệu hóa PanelBuoc4 (không cho sửa nữa)
        panelBuoc4.setComponentsEnabled(false);
        
        // b. Kích hoạt PanelBuoc5 (cho phép thanh toán)
        panelBuoc5.setComponentsEnabled(true);

        // c. (Tùy chọn) Tính tổng tiền từ session và đẩy vào Buoc5
        // double total = bookingSession.tinhTongTien(); 
        // panelBuoc5.setTotalAmount(total);

        // 2. Lắng nghe nút "Thanh toán" từ PanelBuoc5
        // (Giả sử PanelBuoc5 có hàm getPayButton())
        JButton payButton = panelBuoc5.getBtnThanhToan();
        if (payButton != null) {
            payButton.addActionListener(e -> {
                // ... (Thực hiện logic thanh toán ở đây) ...
                
                // Giả sử thanh toán thành công
                boolean paymentSuccess = true; 
                
                if (paymentSuccess) {
                    // a. Vô hiệu hóa PanelBuoc5
                    panelBuoc5.setComponentsEnabled(false);

                    // b. Báo cho wizard chính (PanelBanVe) biết
                    if (onPaymentSuccessListener != null) {
                        onPaymentSuccessListener.run();
                    }
                } else {
                    // (Hiển thị thông báo lỗi thanh toán...)
                }
            });
        }
    }
}