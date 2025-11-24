package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVE.java  1.0  [3:18:54 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import entity.DonDatCho;
import entity.KhachHang;

public class PanelHoanVe extends JPanel {
	private CardLayout cardLayout;
	private JPanel stepPanel;

	// Các panel giai đoạn
	private PanelHoanVe1 panelHoanVe1;
	private PanelHoanVe2 panelHoanVe2;

	// Các controller Mediator cho từng bước
	private HoanVe1Controller hoanVe1Controller;
	private HoanVe2Controller hoanVe2Controller;

	public PanelHoanVe() {
		setLayout(new BorderLayout());
		setBackground(new Color(230, 230, 230));

		// 1. Khởi tạo CardLayout và Panel chứa các bước
		cardLayout = new CardLayout();
		stepPanel = new JPanel(cardLayout);

		// 2. Khởi tạo các bước
		panelHoanVe1 = new PanelHoanVe1();
		panelHoanVe2 = new PanelHoanVe2();

		// 3. Thêm các bước vào CardLayout
		stepPanel.add(panelHoanVe1, "step1");
		stepPanel.add(panelHoanVe2, "step2");

		add(stepPanel, BorderLayout.CENTER);

		// 4. Khởi tạo các Controller (Mediator)
		hoanVe1Controller = new HoanVe1Controller(panelHoanVe1);
		hoanVe2Controller = new HoanVe2Controller(panelHoanVe2);

		// 5. Liên kết các Controller (Logic chính)
		// Lắng nghe sự kiện "Hoàn tất bước 1" (Bấm Xác nhận ở Buoc3)
		hoanVe1Controller.addPanel1CompleteListener(() -> {
			// 1. Chuẩn bị dữ liệu cho PanelHoanVe2
			DonDatCho donDatCho = hoanVe1Controller.getDonDatCho();
			KhachHang khachHang = hoanVe1Controller.getNguoiMua();
			List<VeHoanRow> listVeHoanRow = hoanVe1Controller.getListRowHoan();
			hoanVe2Controller.loadDataForConfirmation(donDatCho, khachHang, listVeHoanRow);
			// 2. Yêu cầu PanelHoanVe chuyển card
			showPanel("step2");
		});

		// Lắng nghe sự kiện "Quay lại" từ PanelHoanVe2
		panelHoanVe2.getBtnPrev().addActionListener(e -> {
			// Yêu cầu PanelHoanVe chuyển card về bước 1
			showPanel("step1");
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

	public PanelHoanVe1 getPanelHoanVe1() {
		return panelHoanVe1;
	}

	public PanelHoanVe2 getPanelHoanVe2() {
		return panelHoanVe2;
	}
}