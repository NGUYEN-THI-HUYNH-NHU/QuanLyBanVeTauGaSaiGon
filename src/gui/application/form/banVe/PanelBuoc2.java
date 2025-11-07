package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2.java  1.0  [10:39:25 AM] Sep 28, 2025
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
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

public class PanelBuoc2 extends JPanel {
	private PanelGioVe panelGioVe;

	private JTabbedPane tabbedPane;
	private PanelChuyen panelChieuDi;
	private PanelChuyen panelChieuVe;

	public PanelBuoc2() {
		setLayout(new BorderLayout(2, 0));
		setBorder(new TitledBorder(""));
		setPreferredSize(new Dimension(0, 400));

		panelGioVe = new PanelGioVe();
		add(panelGioVe, BorderLayout.EAST);

		panelChieuDi = new PanelChuyen();
		panelChieuVe = new PanelChuyen();

		// 3. Khởi tạo JTabbedPane
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(" Chiều đi ", panelChieuDi);
		tabbedPane.addTab(" Chiều về ", panelChieuVe);

		// 4. Thêm JTabbedPane và PanelGioVe vào layout
		add(tabbedPane, BorderLayout.CENTER);
		add(panelGioVe, BorderLayout.EAST);
	}

	public PanelGioVe getPanelGioVe() {
		return panelGioVe;
	}

	/**
	 * Phương thức này được BanVe1Controller gọi để ẩn/hiện tab "Chiều về"
	 */
	public void showReturnTab(boolean show) {
		// Tạm thời tắt tab "Chiều về"
		// (Chúng ta dùng setEnabledAt để giữ vị trí, hoặc removeTab/addTab)
		if (tabbedPane.getTabCount() > 1) {
			tabbedPane.setEnabledAt(1, show);
			if (!show) {
				tabbedPane.setSelectedIndex(0); // Quay về tab 1 nếu đang ẩn tab 2
			}
		}
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public PanelChuyen getPanelChieuDi() {
		return panelChieuDi;
	}

	public PanelChuyen getPanelChieuVe() {
		return panelChieuVe;
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (tabbedPane != null) {
			tabbedPane.setEnabled(enabled);
		}
		if (panelGioVe != null) {
			panelGioVe.setEnabled(enabled);
		}
	}
}