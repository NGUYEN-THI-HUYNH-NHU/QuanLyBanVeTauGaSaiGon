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
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.PanelBuoc6;

public class PanelDoiVe extends JPanel {
	private CardLayout cardLayout;
	private JPanel stepPanel;
	private BookingSession bookingSession;

	// Các panel "bước" chính
	private PanelDoiVe1 panelDoiVe1;
	private PanelDoiVe2 panelDoiVe2;
	private PanelDoiVe3 panelDoiVe3;
	private PanelBuoc6 panelBuoc6;

	// Các controller "Mediator" cho từng bước
	private DoiVe1Controller doiVe1Controller;
	private DoiVe2Controller doiVe2Controller;
	private DoiVe3Controller doiVe3Controller;

	public PanelDoiVe() {
		setLayout(new BorderLayout());
		setBackground(new Color(230, 230, 230));
		UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));

		// 1. Khởi tạo CardLayout và Panel chứa các bước
		cardLayout = new CardLayout();
		stepPanel = new JPanel(cardLayout);

		// 2. Khởi tạo BookingSession
		bookingSession = new BookingSession();

		// 3. Khởi tạo các bước
		panelDoiVe1 = new PanelDoiVe1();
		panelDoiVe2 = new PanelDoiVe2();
		panelBuoc6 = new PanelBuoc6();

		// 4. Thêm các bước gộp vào CardLayout
		stepPanel.add(panelDoiVe1, "step1");
//		stepPanel.add(panelDoiVe2, "step2");
//		stepPanel.add(panelDoiVe3, "step3");
		stepPanel.add(panelBuoc6, "complete");

		add(stepPanel, BorderLayout.CENTER);

		// 5. Khởi tạo các Controller (Mediator)
		doiVe1Controller = new DoiVe1Controller(panelDoiVe1);
		doiVe2Controller = new DoiVe2Controller(panelDoiVe2, bookingSession);
		doiVe3Controller = new DoiVe3Controller(panelDoiVe3, bookingSession);

//		// 6. Liên kết các Controller (Logic chính)
//		// Lắng nghe sự kiện "Hoàn tất bước 1" (Bấm Xác nhận ở Buoc3)
//		doiVe1Controller.addPanel1CompleteListener(() -> {
//			// 1. Chuẩn bị dữ liệu cho PanelDoiVe2
//			doiVe2Controller.loadDataForConfirmation();
//			// 2. Yêu cầu PanelDoiVe chuyển card
//			showPanel("step2");
//		});
//
//		// Lắng nghe sự kiện "Quay lại" từ PanelDoiVe2
//		panelDoiVe2.getBtnPrev().addActionListener(e -> {
//			// Yêu cầu PanelDoiVe chuyển card về bước 1
//			showPanel("step1");
//		});
//
//		// TODO: Có thể thêm listener cho doiVe2Controller.addPaymentSuccessListener để
//		// gọi showPanel("complete") khi thanh toán xong)
//		doiVe2Controller.addPanel2PaymentSuccessListener(() -> {
//			panelBuoc6.loadCompletionData(bookingSession);
//			showPanel("complete");
//		});
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

	public PanelBuoc6 getPanelBuoc6() {
		return panelBuoc6;
	}
}