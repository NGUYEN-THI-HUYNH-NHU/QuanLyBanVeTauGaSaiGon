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
import javax.swing.SwingUtilities;

import gui.application.UngDung;

public class PanelBanVe extends JPanel {
	private CardLayout cardLayout;
	private JPanel stepPanel;
	private BookingSession bookingSession;

	// Các panel "bước" chính
	private PanelBanVe1 panelBanVe1;
	private PanelBanVe2 panelBanVe2;

	// Các controller "Mediator" cho từng bước
	private BanVe1Controller banVe1Controller;
	private BanVe2Controller banVe2Controller;

	public PanelBanVe() {
		setLayout(new BorderLayout());

		// 1. Khởi tạo CardLayout và Panel chứa các bước
		cardLayout = new CardLayout();
		stepPanel = new JPanel(cardLayout);

		// 2. Khởi tạo BookingSession
		bookingSession = new BookingSession();

		// 3. Khởi tạo các bước
		panelBanVe1 = new PanelBanVe1();
		panelBanVe2 = new PanelBanVe2();

		// 4. Thêm các bước gộp vào CardLayout
		stepPanel.add(panelBanVe1, "step1");
		stepPanel.add(panelBanVe2, "step2");

		add(stepPanel, BorderLayout.CENTER);

		// 5. Khởi tạo các Controller (Mediator)
		banVe1Controller = new BanVe1Controller(panelBanVe1, bookingSession);
		banVe2Controller = new BanVe2Controller(panelBanVe2, bookingSession);

		// 6. Liên kết các Controller (Logic chính)
		// Lắng nghe sự kiện "Hoàn tất bước 1" (Bấm Xác nhận ở Buoc3)
		banVe1Controller.addPanel1CompleteListener(() -> {
			// 1. Chuẩn bị dữ liệu cho PanelBanVe2
			banVe2Controller.loadDataForConfirmation();
			// 2. Yêu cầu PanelBanVe chuyển card
			showPanel("step2");
		});

		// Lắng nghe sự kiện "Quay lại" từ PanelBanVe2
		banVe2Controller.addPanel2ReturnListener(() -> {
			showPanel("step1");
		});

		banVe2Controller.addPanel2PaymentSuccessListener(() -> {
			// 1. Dừng timer cũ
			banVe1Controller.stopAllTimers();
			// 2. Tạo lại giao diện mới hoàn toàn
			UngDung.reloadPanelBanVe();
		});
	}

	/**
	 * Hàm công khai để các controller gọi và chuyển Card
	 * 
	 * @param panelName Tên của card (ví dụ: "step1", "step2")
	 */
	public void showPanel(String panelName) {
		SwingUtilities.invokeLater(() -> {
			cardLayout.show(stepPanel, panelName);
		});
	}

	public PanelBanVe1 getPanelBanVe1() {
		return panelBanVe1;
	}

	public PanelBanVe2 getPanelBanVe2() {
		return panelBanVe2;
	}
}