package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1Controller.java  1.0  [10:42:13 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;

import bus.Chuyen_BUS;
import entity.Chuyen;
import entity.Ga;

public class PanelBuoc1Controller {

	private final PanelBuoc1 panel;
	private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
	private SearchListener searchListener;
	// trạng thái được giữ ở controller (id đã chọn)
	private String selectedGaDi = null;
	private String selectedGaDen = null;
	// Autocomplete instances
	private AutoCompleteField acGaDi;
	private AutoCompleteField acGaDen;
	// debounce millis
	private static final int DEBOUNCE_MS = 300;

	// Interface để BanVe1Controller (Mediator) lắng nghe
	public interface SearchListener {
		void onSearchSuccess(List<Chuyen> outboundResults, List<Chuyen> returnResults, SearchCriteria criteria);

		void onSearchFailure();
	}

	public void addSearchListener(SearchListener listener) {
		this.searchListener = listener;
	}

	public PanelBuoc1Controller(PanelBuoc1 panel) {
		this.panel = panel;
		init();
	}

	private void init() {
		SwingUtilities.invokeLater(() -> {
			panel.getTxtGaDi().requestFocusInWindow();
		});

		// AutoComplete cho Ga đi: fetcher dùng chuyenBUS.goiYGaDi(prefix, limit)
		acGaDi = new AutoCompleteField(panel.getTxtGaDi(), (prefix, limit) -> {
			try {
				return chuyenBUS.goiYGaDi(prefix, limit);
			} catch (Exception ex) {
				ex.printStackTrace();
				return Collections.emptyList();
			}
		}, 8, ga -> {
			// onSelect
			selectedGaDi = ga.getGaID();
			// set text programmatically (AutoCompleteField sẽ suppress change)
			panel.getTxtGaDi().setText(ga.getTenGa());
			// clear gaDen khi gaDi nhthay đổi
			selectedGaDen = null;
			panel.getTxtGaDen().setText("");
		});

		// AutoComplete cho Ga den: fetcher phụ thuộc vào selectedGaDi
		acGaDen = new AutoCompleteField(panel.getTxtGaDen(), (prefix, limit) -> {
			try {
				String gaDiId = selectedGaDi;
				if (gaDiId != null) {
					return chuyenBUS.goiYGaDenTheoGaDi(gaDiId, prefix, limit);
				} else {
					return chuyenBUS.goiYGaDi(prefix, limit); // fallback chung
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				return Collections.emptyList();
			}
		}, 8, ga -> {
			selectedGaDen = ga.getGaID();
			panel.getTxtGaDen().setText(ga.getTenGa());
		});

		// Wiring navigation UX:
		acGaDi.setNextComponent(panel.getTxtGaDen());
		acGaDen.setPrevComponent(panel.getTxtGaDi());
		// When Enter on final field (GaDen) without selecting popup -> trigger search
		// button
		acGaDen.setOnFinalEnter(() -> SwingUtilities.invokeLater(() -> panel.getBtnTimKiem().doClick()));

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

	private static class SearchResultBundle {
		List<Chuyen> outboundTrips;
		List<Chuyen> returnTrips;
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

		new SwingWorker<SearchResultBundle, Void>() {
			@Override
			protected SearchResultBundle doInBackground() {
				SearchResultBundle bundle = new SearchResultBundle();
				try {
					String gaDiId = criteria.getGaDiId();
					String gaDenId = criteria.getGaDenId();

					/* Chieu di */
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
						bundle.outboundTrips = Collections.emptyList();
						return bundle;
					}

					LocalDate ngayDi = criteria.getNgayDi();
					if (ngayDi == null) {
						ngayDi = LocalDate.now();
					}

					bundle.outboundTrips = chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDiId, gaDenId, ngayDi);

					/* Chieu ve neu chon khu hoi */
					if (criteria.isKhuHoi() && criteria.getNgayVe() != null) {
						// Đảo ngược ga đi và ga đến
						bundle.returnTrips = chuyenBUS.timChuyenTheoGaDiGaDenNgayDi(gaDenId, gaDiId,
								criteria.getNgayVe());
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					bundle.outboundTrips = Collections.emptyList();
					bundle.returnTrips = Collections.emptyList();
				}
				return bundle;
			}

			@Override
			protected void done() {
				try {
					SearchResultBundle results = get();
					panel.getBtnTimKiem().setEnabled(true);

					// Kiểm tra kết quả chiều đi
					if (results.outboundTrips == null || results.outboundTrips.isEmpty()) {
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
								"Không tìm thấy chuyến đi phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE));
						if (searchListener != null) {
							searchListener.onSearchFailure();
						}
						return;
					}

					// (Thông báo nếu tìm được chiều đi nhưng không tìm được chiều về)
					if (criteria.isKhuHoi() && (results.returnTrips == null || results.returnTrips.isEmpty())) {
						SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(panel,
								"Đã tìm thấy chuyến đi, nhưng không tìm thấy chuyến về phù hợp.", "Lưu ý",
								JOptionPane.INFORMATION_MESSAGE));
					}

					if (searchListener == null) {
						System.err.println("PanelBuoc1Controller: searchListener chưa được set!");
						return;
					}

					SearchCriteria resolvedCriteria = new SearchCriteria.Builder().gaDiId(selectedGaDi)
							.tenGaDi(panel.getGaDi()).gaDenId(selectedGaDen).tenGaDen(panel.getGaDen())
							.ngayDi(panel.getNgayDi()).ngayVe(panel.getNgayVe()).khuHoi(panel.isKhuHoi()).build();

					searchListener.onSearchSuccess(results.outboundTrips, results.returnTrips, resolvedCriteria);
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

	// ---------- Inner class chung cho autocomplete ----------
	private class AutoCompleteField {
		private final JTextField field;
		private final BiFunction<String, Integer, List<Ga>> fetcher;
		private final int maxResults;
		private final Consumer<Ga> onSelect;

		private final Timer debounce;
		private JPopupMenu popup;
		private JList<Ga> list;
		private DefaultListModel<Ga> listModel;

		// flags
		private volatile boolean suppressChange = false;
		private boolean confirmed = false;
		private String lastConfirmedText = null;

		// navigation support
		private JComponent nextComponent = null;
		private JComponent prevComponent = null;
		private Runnable onFinalEnter = null; // called when Enter on final field

		AutoCompleteField(JTextField field, BiFunction<String, Integer, List<Ga>> fetcher, int maxResults,
				Consumer<Ga> onSelect) {
			this.field = field;
			this.fetcher = fetcher;
			this.maxResults = maxResults;
			this.onSelect = onSelect;

			this.debounce = new Timer(DEBOUNCE_MS, e -> fetchSuggestions());
			this.debounce.setRepeats(false);

			initListeners();
		}

		// setters for navigation
		public void setNextComponent(JComponent next) {
			this.nextComponent = next;
		}

		public void setPrevComponent(JComponent prev) {
			this.prevComponent = prev;
		}

		public void setOnFinalEnter(Runnable r) {
			this.onFinalEnter = r;
		}

		private void initListeners() {
			// Document change -> handle with debounce
			field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
				@Override
				public void insertUpdate(javax.swing.event.DocumentEvent e) {
					handleChange();
				}

				@Override
				public void removeUpdate(javax.swing.event.DocumentEvent e) {
					handleChange();
				}

				@Override
				public void changedUpdate(javax.swing.event.DocumentEvent e) {
					handleChange();
				}
			});

			// focus lost -> mark confirmed if non-empty, hide popup
			field.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					String txt = field.getText().trim();
					if (!txt.isEmpty()) {
						confirmed = true;
						lastConfirmedText = txt;
					} else {
						confirmed = false;
						lastConfirmedText = null;
					}
					hidePopup();
				}
			});

			// key bindings: ESC, UP, DOWN, ENTER
			InputMap im = field.getInputMap(JComponent.WHEN_FOCUSED);
			ActionMap am = field.getActionMap();

			im.put(KeyStroke.getKeyStroke("ESCAPE"), "hidePopup");
			am.put("hidePopup", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					hidePopup();
				}
			});

			im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
			am.put("moveUp", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// If popup visible -> moveUp in list, else move focus to prev component
					if (popup != null && popup.isVisible() && list != null) {
						int sel = list.getSelectedIndex();
						int size = listModel.getSize();
						if (size == 0) {
							return;
						}
						if (sel <= 0) {
							list.setSelectedIndex(size - 1);
							list.ensureIndexIsVisible(size - 1);
						} else {
							moveSelection(-1);
						}
					}
				}
			});

			im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
			am.put("moveDown", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Nếu popup visible -> moveDown trong list (và wrap về đầu nếu đang ở cuối)
					if (popup != null && popup.isVisible() && list != null) {
						int sel = list.getSelectedIndex();
						int size = listModel.getSize();
						if (size == 0) {
							return;
						}
						if (sel >= size - 1) {
							// wrap về đầu thay vì focus next component
							list.setSelectedIndex(0);
							list.ensureIndexIsVisible(0);
						} else {
							moveSelection(1);
						}
					}
				}
			});

			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "chooseOrConfirm");
			am.put("chooseOrConfirm", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (popup != null && popup.isVisible() && list != null && list.getSelectedIndex() >= 0) {
						selectIndex(list.getSelectedIndex());
					} else {
						// if there's a next component -> go to it, else final enter action
						String txt = field.getText().trim();
						if (!txt.isEmpty()) {
							confirmed = true;
							lastConfirmedText = txt;
						}
						if (nextComponent != null) {
							focusComponent(nextComponent);
						} else if (onFinalEnter != null) {
							onFinalEnter.run();
						}
						hidePopup();
					}
				}
			});
		}

		private void focusComponent(JComponent comp) {
			SwingUtilities.invokeLater(() -> {
				comp.requestFocusInWindow();
				if (comp instanceof JTextField) {
					((JTextField) comp).selectAll();
				}
			});
		}

		private void handleChange() {
			if (suppressChange) {
				suppressChange = false;
				return;
			}
			String txt = field.getText().trim();
			if (txt.length() < 1) {
				hidePopup();
				confirmed = false;
				lastConfirmedText = null;
				return;
			}
			if (confirmed && lastConfirmedText != null && lastConfirmedText.equals(txt)) {
				hidePopup();
				return;
			}
			confirmed = false;
			lastConfirmedText = null;
			debounce.restart();
		}

		private void fetchSuggestions() {
			final String prefix = field.getText().trim();
			if (prefix.length() < 1) {
				return;
			}
			final String cur = prefix;

			new SwingWorker<List<Ga>, Void>() {
				@Override
				protected List<Ga> doInBackground() {
					try {
						return fetcher.apply(cur, maxResults);
					} catch (Exception ex) {
						ex.printStackTrace();
						return Collections.emptyList();
					}
				}

				@Override
				protected void done() {
					try {
						if (!field.getText().trim().equals(cur)) {
							return; // user changed meanwhile
						}
						List<Ga> res = get();
						// If this is gaDen, and selectedGaDi exists, remove it (controller ensures
						// selectedGaDi updated externally)
						if (field == panel.getTxtGaDen() && selectedGaDi != null && res != null) {
							res.removeIf(g -> selectedGaDi.equals(g.getGaID()));
						}
						showPopup(res);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}.execute();
		}

		private void showPopup(List<Ga> items) {
			hidePopup();
			if (items == null || items.isEmpty()) {
				return;
			}

			// Don't show if confirmed and text unchanged
			String curText = field.getText().trim();
			if (confirmed && lastConfirmedText != null && lastConfirmedText.equals(curText)) {
				return;
			}

			listModel = new DefaultListModel<>();
			for (Ga g : items) {
				listModel.addElement(g);
			}

			list = new JList<>(listModel);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setVisibleRowCount(Math.min(8, listModel.getSize()));
			int vPadding = 8;
			int hPadding = 6;
			list.setCellRenderer(new DefaultListCellRenderer() {
				@Override
				public Component getListCellRendererComponent(JList<?> list, Object value, int index,
						boolean isSelected, boolean cellHasFocus) {
					JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
							cellHasFocus);
					if (value instanceof Ga) {
						lbl.setText(((Ga) value).getTenGa());
					}
					lbl.setBorder(BorderFactory.createEmptyBorder(vPadding, hPadding, vPadding, hPadding));
					if (isSelected) {
						lbl.setBackground(new Color(30, 144, 255)); // selected bg
						lbl.setForeground(Color.WHITE);
					} else {
						lbl.setBackground(Color.WHITE);
						lbl.setForeground(Color.BLACK);
					}
					lbl.setOpaque(true);
					return lbl;
				}
			});

			// double click or keyboard selection
			list.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int idx = list.locationToIndex(e.getPoint());
						if (idx >= 0) {
							selectIndex(idx);
						}
					}
				}
			});
			list.addListSelectionListener((ListSelectionEvent e) -> {
			});

			JScrollPane sc = new JScrollPane(list);
			sc.setBorder(BorderFactory.createEmptyBorder());
			sc.setPreferredSize(null);
			sc.getViewport().setOpaque(false);

			popup = new JPopupMenu();
			popup.setBackground(Color.WHITE);
			int desiredWidth = Math.max(field.getWidth(), 120);
			int estRowHeight = 20 + vPadding * 2;
			int desiredHeight = Math.min(8, listModel.getSize()) * estRowHeight;
			popup.setPopupSize(new Dimension(desiredWidth, Math.min(desiredHeight, 300)));
			popup.setFocusable(false);
			popup.add(sc);

			// show below field
			popup.show(field, 0, field.getHeight());
			field.requestFocusInWindow();

			// select first
			if (!listModel.isEmpty()) {
				list.setSelectedIndex(0);
			}
		}

		private void moveSelection(int delta) {
			if (popup == null || !popup.isVisible() || list == null) {
				return;
			}
			int idx = list.getSelectedIndex();
			int size = listModel.getSize();
			if (size == 0) {
				return;
			}
			int next = idx + delta;
			if (next < 0) {
				next = 0;
			}
			if (next >= size) {
				next = size - 1;
			}
			list.setSelectedIndex(next);
			list.ensureIndexIsVisible(next);
		}

		private void selectIndex(int idx) {
			if (listModel == null || idx < 0 || idx >= listModel.getSize()) {
				return;
			}
			Ga g = listModel.getElementAt(idx);
			if (g == null) {
				return;
			}

			// mark suppressChange so document listener won't trigger fetch again
			suppressChange = true;
			confirmed = true;
			lastConfirmedText = g.getTenGa();

			// onSelect may change external selectedGaDi/Den and modify other fields
			onSelect.accept(g);

			hidePopup();
		}

		private void hidePopup() {
			if (popup != null && popup.isVisible()) {
				popup.setVisible(false);
			}
			popup = null;
			list = null;
			listModel = null;
		}
	}
}