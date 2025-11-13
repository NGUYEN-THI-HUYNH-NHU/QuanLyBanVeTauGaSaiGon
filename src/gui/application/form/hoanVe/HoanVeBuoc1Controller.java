package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc1Controller.java  1.0  [1:09:40 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import bus.DonDatCho_BUS;
import bus.KhachHang_BUS;
import bus.Ve_BUS;
import entity.DonDatCho;
import entity.KhachHang;
import entity.Ve;

public class HoanVeBuoc1Controller {
	private final PanelHoanVeBuoc1 panel;

	private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	private SearchListener searchListener;

	// Interface để HoanVeController (Mediator) lắng nghe
	public interface SearchListener {
		void onSearchSuccess(DonDatCho donDatCho, List<Ve> danhSachVe, KhachHang khachHang);

		void onSearchFailure();
	}

	public void addSearchListener(SearchListener listener) {
		this.searchListener = listener;
	}

	public HoanVeBuoc1Controller(PanelHoanVeBuoc1 panel) {
		this.panel = panel;
		SwingUtilities.invokeLater(() -> {
			panel.getTxtMaDDC().requestFocusInWindow();
		});
	}

	public void performSearch() {
		String maDDC = panel.getTxtMaDDC().getText().trim();
		String cccd = panel.getTxtCCCD().getText().trim();

		panel.getBtnTraCuu().setEnabled(false);

		new SwingWorker<DonDatCho, Void>() {
			@Override
			protected DonDatCho doInBackground() {
				DonDatCho donDatCho = null;
				try {
					donDatCho = donDatChoBUS.timDonDatChoTheoIDVaSoGiayTo(maDDC, cccd);
				} catch (Exception ex) {
					System.out.println("PanelHoanVe1Controller: Loi");
					ex.printStackTrace();
				}
				return donDatCho;
			}

			@Override
			protected void done() {
				try {
					DonDatCho donDatCho = get();
					if (donDatCho != null) {
						List<Ve> danhSachVe = veBUS.timCacVeTheoDonDatChoID(maDDC);
						KhachHang khachHang = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccd);
						searchListener.onSearchSuccess(donDatCho, danhSachVe, khachHang);
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
							"Lỗi khi tìm chuyến: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
					if (searchListener != null) {
						searchListener.onSearchFailure();
					}
				}
			}
		}.execute();
	}
}