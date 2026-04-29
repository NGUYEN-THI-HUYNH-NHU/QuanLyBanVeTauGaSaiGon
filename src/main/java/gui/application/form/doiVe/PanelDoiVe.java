package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVe.java  1.0  [5:23:08 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.application.UngDung;

public class PanelDoiVe extends JPanel {
	private CardLayout cardLayout;
	private JPanel stepPanel;
	private ExchangeSession exchangeSession;

	// Các panel "bước" chính
	private PanelDoiVe1 panelDoiVe1;
	private PanelDoiVe2 panelDoiVe2;
	private PanelDoiVe3 panelDoiVe3;

	// Các controller "Mediator" cho từng bước
	private DoiVe1Controller doiVe1Controller;
	private DoiVe2Controller doiVe2Controller;
	private DoiVe3Controller doiVe3Controller;

	public PanelDoiVe() {
		setLayout(new BorderLayout());
		setBackground(new Color(230, 230, 230));

		// 1. Khởi tạo CardLayout và Panel chứa các bước
		cardLayout = new CardLayout();
		stepPanel = new JPanel(cardLayout);

		// 2. Khởi tạo BookingSession
		exchangeSession = ExchangeSession.getInstance();

		// 3. Khởi tạo các bước
		panelDoiVe1 = new PanelDoiVe1();
		panelDoiVe2 = new PanelDoiVe2();
		panelDoiVe3 = new PanelDoiVe3();

		// 4. Thêm các bước gộp vào CardLayout
		stepPanel.add(panelDoiVe1, "step1");
		stepPanel.add(panelDoiVe2, "step2");
		stepPanel.add(panelDoiVe3, "step3");

		add(stepPanel, BorderLayout.CENTER);

		// 5. Khởi tạo các Controller (Mediator)
		doiVe1Controller = new DoiVe1Controller(panelDoiVe1, exchangeSession);
		doiVe2Controller = new DoiVe2Controller(panelDoiVe2, exchangeSession);
		doiVe3Controller = new DoiVe3Controller(panelDoiVe3, exchangeSession);

		// 6. Liên kết các Controller (Logic chính)
		doiVe1Controller.addPanel1RefreshListner(() -> {
			UngDung.reloadPanelDoiVe();
			showPanel("step1");
		});
		// Lắng nghe sự kiện "Hoàn tất bước 1" (Bấm Xác nhận ở Buoc3)
		doiVe1Controller.addPanel1CompleteListener(() -> {
			// 1. Chuẩn bị dữ liệu cho PanelDoiVe2
			doiVe2Controller.loadDataForChoosingNewTickets();
			// 2. Yêu cầu PanelDoiVe chuyển card
			showPanel("step2");
		});

		// Lắng nghe sự kiện "Quay lại" từ PanelDoiVe2
		doiVe2Controller.addPanel2ReturnListener(() -> {
			showPanel("step1");
		});

		// Lắng nghe sự kiện xác nhận từ PanelDoiVe2
		doiVe2Controller.addPanel2CompleteListener(() -> {
			doiVe3Controller.loadDataForConfirmation();
			showPanel("step3");
		});

		// Lắng nghe sự kiện "Quay lại" từ PanelDoiVe2
		doiVe3Controller.addPanel3ReturnListener(() -> {
			showPanel("step2");
		});

		// Lắng nghe sự kiện thanh toán thành công từ PanelDoiVe3
		doiVe3Controller.addPanel3PaymentSuccessListener(() -> {
			doiVe2Controller.stopAllTimers();
			UngDung.reloadPanelDoiVe();
		});
	}

	/**
	 * Hàm công khai để các controller gọi và chuyển Card
	 * 
	 * @param panelName Tên của card (ví dụ: "step1", "step2", "complete")
	 */
	public void showPanel(String panelName) {
		SwingUtilities.invokeLater(() -> {
			cardLayout.show(stepPanel, panelName);
		});
	}

	public PanelDoiVe1 getPanelDoiVe1() {
		return panelDoiVe1;
	}

	public PanelDoiVe2 getPanelDoiVe2() {
		return panelDoiVe2;
	}

	public PanelDoiVe3 getPanelDoiVe3() {
		return panelDoiVe3;
	}
}