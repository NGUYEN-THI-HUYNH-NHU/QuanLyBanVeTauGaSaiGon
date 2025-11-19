package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc1Controller.java  1.0  [5:29:47 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import bus.DonDatCho_BUS;
import bus.KhachHang_BUS;
import bus.Ve_BUS;
import entity.DonDatCho;
import entity.KhachHang;
import entity.Ve;
import entity.type.TrangThaiVe;

public class DoiVeBuoc1Controller {
	private final PanelDoiVeBuoc1 panel;

	private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	// Interface để DoiVe1Controller (Mediator) lắng nghe
	protected interface SearchListener {
		void onSearchSuccess(DonDatCho donDatCho, List<Ve> danhSachVe, KhachHang khachHang);

		void onSearchFailure();
	}

	private SearchListener searchListener;

	public void addSearchListener(SearchListener listener) {
		this.searchListener = listener;
	}

	public DoiVeBuoc1Controller(PanelDoiVeBuoc1 panel) {
		this.panel = panel;
		init();
	}

	private void init() {
		// 1. Tự động focus vào ô Mã ĐĐC khi mở
		SwingUtilities.invokeLater(() -> {
			panel.getTxtMaDDC().requestFocusInWindow();
		});

		// 2. Gán sự kiện cho nút Tra cứu
		panel.getBtnTraCuu().addActionListener(e -> performSearch());

		// 3. Xử lý phím Enter
		KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		// --- Xử lý Enter trên txtMaDDC (chuyển focus xuống txtCCCD) ---
		InputMap imMaDDC = panel.getTxtMaDDC().getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap amMaDDC = panel.getTxtMaDDC().getActionMap();

		imMaDDC.put(enterKey, "focusNext");
		amMaDDC.put("focusNext", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.getTxtCCCD().requestFocusInWindow();
			}
		});

		// --- Xử lý Enter trên txtCCCD (tương tự click nút Tra cứu) ---
		InputMap imCCCD = panel.getTxtCCCD().getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap amCCCD = panel.getTxtCCCD().getActionMap();

		imCCCD.put(enterKey, "triggerSearch");
		amCCCD.put("triggerSearch", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kích hoạt sự kiện click của nút
				// Nút sẽ gọi performSearch() thông qua ActionListener đã gán ở trên
				panel.getBtnTraCuu().doClick();
			}
		});

	}

	public void performSearch() {
		String maDDC = panel.getTxtMaDDC().getText().trim();
		String cccd = panel.getTxtCCCD().getText().trim();

		panel.getBtnTraCuu().setEnabled(false);

		new SwingWorker<DonDatCho, Void>() {
			@Override
			protected DonDatCho doInBackground() throws Exception {
				DonDatCho donDatCho = null;
				donDatCho = donDatChoBUS.timDonDatChoTheoIDVaSoGiayTo(maDDC, cccd);
				return donDatCho;
			}

			@Override
			protected void done() {
				try {
					DonDatCho donDatCho = get();
					if (donDatCho != null) {
						// Nếu tìm thấy -> lấy vé và khách hàng
						List<Ve> danhSachVe = veBUS.timCacVeTheoDonDatChoID(maDDC, TrangThaiVe.DA_BAN);
						KhachHang khachHang = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccd);

						panel.getBtnTraCuu().setEnabled(true);

						// Báo cho Mediator (DoiVe1Controller)
						if (searchListener != null) {
							searchListener.onSearchSuccess(donDatCho, danhSachVe, khachHang);
						}
					} else {
						// Xử lý khi tra cứu thành công nhưng không tìm thấy kết quả
						panel.getBtnTraCuu().setEnabled(true);
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
								"Không tìm thấy đơn đặt chỗ với thông tin cung cấp.", "Không tìm thấy",
								JOptionPane.INFORMATION_MESSAGE));

						if (searchListener != null) {
							searchListener.onSearchFailure();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					panel.getBtnTraCuu().setEnabled(true);
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
							"Lỗi khi tìm đơn đặt chỗ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
					if (searchListener != null) {
						searchListener.onSearchFailure();
					}
				}
			}
		}.execute();
	}
}