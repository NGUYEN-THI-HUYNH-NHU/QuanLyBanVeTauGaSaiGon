package gui.application.form.doiVe;
/*
 * @(#) HoanVeBuoc4Controller.java  1.0  [12:24:37 PM] Nov 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 18, 2025
 * @version: 1.0
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import bus.Chuyen_BUS;
import entity.Chuyen;
import entity.Ga;
import gui.application.form.banVe.SearchCriteria;

public class DoiVeBuoc4Controller {

	private final PanelDoiVeBuoc4 panel;
	private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
	private SearchListener searchListener;
	// trạng thái được giữ ở controller (id đã chọn)
	private String selectedGaDi = null;
	private String selectedGaDen = null;

	// Interface để DoiVe1Controller (Mediator) lắng nghe
	protected interface SearchListener {
		void onSearchSuccess(List<Chuyen> results, SearchCriteria criteria);

		void onSearchFailure();
	}

	public void addSearchListener(SearchListener listener) {
		this.searchListener = listener;
	}

	public DoiVeBuoc4Controller(PanelDoiVeBuoc4 panel) {
		this.panel = panel;
		init();
	}

	private void init() {
		SwingUtilities.invokeLater(() -> {
			panel.getTxtGaDi().requestFocusInWindow();
		});

		selectedGaDi = panel.getTxtGaDi().getText();
		selectedGaDen = panel.getTxtGaDen().getText();
		
		// Button tìm kiếm
		panel.getBtnTimKiem().addActionListener(e -> performSearch());

		InputMap btnIm = panel.getBtnTimKiem().getInputMap(JComponent.WHEN_FOCUSED);
		// Lấy ActionMap của nút
		ActionMap btnAm = panel.getBtnTimKiem().getActionMap();

		// Map phím ENTER với một "key" (chuỗi tùy ý)
		btnIm.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "pressButton");

		// Map "key" đó với một hành động
		btnAm.put("pressButton", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Kích hoạt sự kiện click của nút (sẽ gọi performSearch() qua ActionListener)
				panel.getBtnTimKiem().doClick();
			}
		});
	}

	// ----- Tìm chuyến -----
	public void performSearch() {
		final SearchCriteria criteria = buildSearchCriteriaFromPanel();

		if (criteria == null || !criteria.isValidForSearch()) {
			SwingUtilities.invokeLater(
					() -> JOptionPane.showMessageDialog(panel, "Vui lòng chọn hoặc nhập đúng Ga đi, Ga đến và Ngày đi.",
							"Thiếu thông tin", JOptionPane.WARNING_MESSAGE));
			return;
		}

		if (criteria.isKhuHoi() && criteria.getNgayVe() == null) {
			SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
					"Vui lòng chọn Ngày về cho vé khứ hồi.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE));
			return;
		}

		panel.getBtnTimKiem().setEnabled(false);

		new SwingWorker<List<Chuyen>, Void>() {
			@Override
			protected List<Chuyen> doInBackground() {
				List<Chuyen> listChuyen = new ArrayList<Chuyen>();
				try {
					String gaDiId = criteria.getGaDiId();
					String gaDenId = criteria.getGaDenId();

					// Resolve bằng tên nếu id chưa có
					if (gaDiId == null || gaDiId.trim().isEmpty()) {
						String name = criteria.getGaDiName();
						if (name != null && !name.trim().isEmpty()) {
							Ga g = chuyenBUS.timGaTheoTenGa(name);
							if (g != null) {
								gaDiId = g.getGaID();
							}
						}
					}
					if (gaDenId == null || gaDenId.trim().isEmpty()) {
						String name = criteria.getGaDenName();
						if (name != null && !name.trim().isEmpty()) {
							Ga g = chuyenBUS.timGaTheoTenGa(name);
							if (g != null) {
								gaDenId = g.getGaID();
							}
						}
					}

					if (gaDiId == null || gaDenId == null) {
						listChuyen = Collections.emptyList();
						return listChuyen;
					}

					LocalDate ngayDi = criteria.getNgayDi();
					if (ngayDi == null) {
						ngayDi = LocalDate.now();
					}

					listChuyen = chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);
				} catch (Exception ex) {
					ex.printStackTrace();
					listChuyen = Collections.emptyList();
					listChuyen = Collections.emptyList();
				}
				return listChuyen;
			}

			@Override
			protected void done() {
				try {
					List<Chuyen> results = get();
					panel.getBtnTimKiem().setEnabled(true);

					// Kiểm tra kết quả chiều đi
					if (results == null || results.isEmpty()) {
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
								"Không tìm thấy chuyến đi phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE));
						if (searchListener != null) {
							searchListener.onSearchFailure();
						}
						return;
					}

					// (Thông báo nếu tìm được chiều đi nhưng không tìm được chiều về)
					if (criteria.isKhuHoi() && (results == null || results.isEmpty())) {
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
								"Đã tìm thấy chuyến đi, nhưng không tìm thấy chuyến về phù hợp.", "Lưu ý",
								JOptionPane.INFORMATION_MESSAGE));
					}

					if (searchListener == null) {
						System.err.println("DoiVeBuoc4Controller: searchListener chưa được set!");
						return;
					}

					SearchCriteria resolvedCriteria = new SearchCriteria.Builder().gaDiId(selectedGaDi)
							.tenGaDi(panel.getGaDi()).gaDenId(selectedGaDen).tenGaDen(panel.getGaDen())
							.ngayDi(panel.getNgayDi()).ngayVe(panel.getNgayVe()).khuHoi(panel.isKhuHoi()).build();

					searchListener.onSearchSuccess(results, resolvedCriteria);
				} catch (Exception ex) {
					ex.printStackTrace();
					panel.getBtnTimKiem().setEnabled(true);
					SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
							"Lỗi khi tìm chuyến: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
					if (searchListener != null) {
						searchListener.onSearchFailure();
					}
				}
			}
		}.execute();
	}

	private SearchCriteria buildSearchCriteriaFromPanel() {
		return new SearchCriteria.Builder().gaDiId(selectedGaDi).tenGaDi(panel.getGaDi()).gaDenId(selectedGaDen)
				.tenGaDen(panel.getGaDen()).ngayDi(panel.getNgayDi()).ngayVe(panel.getNgayVe()).khuHoi(panel.isKhuHoi())
				.build();
	}
}