package gui.application.form.banVe;
/*
 * @(#) TicketSalePanel.java  1.0  [10:36:50 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import entity.NhanVien;

public class PanelBanVe extends JPanel {
    
    private CardLayout cardLayout;
    private JPanel stepPanel; // Panel chứa các card
    private WizardController wizardController; // Controller wizard CẤP CAO
    private BookingSession bookingSession; // Session dữ liệu chung

    // Các panel "bước" chính
    private PanelBanVe1 panelBanVe1;
    private PanelBanVe2 panelBanVe2;
    private PanelBuoc6 panelBuoc6; // Giữ lại panel "Hoàn tất"

    // Các controller "Mediator" cho từng bước
    private BanVe1Controller panelBanVe1Controller;
    private PanelBanVe2Controller panelBanVe2Controller;

    public PanelBanVe(NhanVien nhanVien) {
        setLayout(new BorderLayout());

        // 1. Khởi tạo CardLayout và Panel chứa các bước
        cardLayout = new CardLayout();
        stepPanel = new JPanel(cardLayout);

        // 2. Khởi tạo BookingSession (NGUỒN DỮ LIỆU THẬT)
        // Session này sẽ được chia sẻ cho tất cả controller
        bookingSession = new BookingSession();

        // 3. Khởi tạo các "bước" gộp
        // (PanelBuoc1, 2, 3... được tạo BÊN TRONG các panel gộp này)
        panelBanVe1 = new PanelBanVe1(); // Gồm Buoc1, Buoc2, Buoc3
        panelBanVe2 = new PanelBanVe2(); // Gồm Buoc4, Buoc5
        panelBuoc6 = new PanelBuoc6(); // Panel hoàn tất (từ code cũ)

        // 4. Thêm các "bước" gộp vào CardLayout
        stepPanel.add(panelBanVe1, "step1"); // Màn hình đầu tiên
        stepPanel.add(panelBanVe2, "step2"); // Màn hình thứ hai
        stepPanel.add(panelBuoc6, "complete"); // Màn hình cuối

        add(stepPanel, BorderLayout.CENTER);

        // 5. Khởi tạo các Controller (Mediator) cho từng bước
        // Tiêm (inject) View và Session vào
        panelBanVe1Controller = new BanVe1Controller(panelBanVe1, bookingSession);
//        panelBanVe2Controller = new PanelBanVe2Controller(panelBanVe2, bookingSession);

        // 6. Khởi tạo Navigation (Next/Back)
        // Chúng ta tận dụng lại WizardNavigationPanel và WizardController cũ
        // để xử lý việc chuyển CardLayout (chuyển các bước chính)
        WizardNavigationPanel navPanel = new WizardNavigationPanel();
        add(navPanel, BorderLayout.SOUTH);

        wizardController = new WizardController(cardLayout, stepPanel);
        navPanel.setController(wizardController);

        // 7. Đăng ký các "bước" gộp với WizardController
        // (Giờ chỉ còn 3 bước thay vì 6)
        wizardController.registerPanel(1, "step1", panelBanVe1);
        wizardController.registerPanel(2, "step2", panelBanVe2);
        wizardController.registerPanel(3, "complete", panelBuoc6);

        // 8. Liên kết các Controller (Logic chính)
        // Lắng nghe sự kiện "Hoàn tất bước 1" từ PanelBanVe1Controller
        
        /* * LƯU Ý: Bạn cần thêm 2 hàm "add...Listener" này vào 
         * PanelBanVe1Controller và PanelBanVe2Controller.
         * * Ví dụ trong PanelBanVe1Controller:
         * private Runnable onPanel1CompleteListener;
         * public void addPanel1CompleteListener(Runnable r) { this.onPanel1CompleteListener = r; }
         * * Và gọi nó khi Buoc3 hoàn tất:
         * if (onPanel1CompleteListener != null) { onPanel1CompleteListener.run(); }
         */
        
        panelBanVe1Controller.addPanel1CompleteListener(() -> {
            // Khi bước 1 (gộp) xong:
            
            // 1. Yêu cầu PanelBanVe2Controller tải dữ liệu từ session
//            panelBanVe2Controller.loadDataForConfirmation(); // (Cần viết hàm này trong PanelBanVe2Controller)
            
            // 2. Chuyển sang thẻ "step2"
            wizardController.goToStep(2);
        });

//        // Lắng nghe sự kiện "Hoàn tất thanh toán" từ PanelBanVe2Controller
//        panelBanVe2Controller.addPaymentSuccessListener(() -> {
//            // Khi bước 2 (gộp) xong:
//            
//            // 1. Yêu cầu PanelBuoc6 tải dữ liệu
////            panelBuoc6.loadCompletionData(bookingSession); // (Cần viết hàm này trong PanelBuoc6)
//            
//            // 2. Chuyển sang thẻ "complete"
////            wizardController.goToStep(3);
//        	System.out.println("OK");
//        });
    }
}