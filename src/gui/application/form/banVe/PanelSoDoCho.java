package gui.application.form.banVe;
/*
 * @(#) PanelSoDoCho.java  1.0  [12:51:43 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;

public class PanelSoDoCho extends JPanel {
	private final JPanel pnlCho;
	private final JScrollPane scrollCho;
	private JPanel pnlNav;
	private JButton btnPrev, btnNext;
	private PanelBuoc2Controller panelBuoc2Controller;
	private JLabel lblToaInfo;
	private JButton btnSelectedCho = null;
	private int doanTauLen;

	// current toa context
	private Toa currentToa;
	private List<Toa> toaList;
	private int currentIndex = 0;
	private JPanel pnlNorth;

	// cache rendered panels per toa (keyed by soToa)
	private final Map<String, JPanel> panelCache = new ConcurrentHashMap<>();
	private volatile boolean loading = false;

	// sizing constants
	private static final int DEFAULT_COLUMNS = 6;
	private static final int CELL_W = 48;
	private static final int CELL_H = 34;
	private static final int H_GAP = 8;
	private static final int V_GAP = 8;

	// preferred viewport size to reduce jumps
	private Dimension stableViewportPreferredSize = new Dimension(420, 200);

	public PanelSoDoCho() {
		setBorder(new TitledBorder("Sơ đồ chỗ"));
		setLayout(new BorderLayout());

		lblToaInfo = new JLabel("Chưa chọn toa", SwingConstants.CENTER);
		lblToaInfo.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
		pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER));

		btnPrev = new JButton("<");
		btnNext = new JButton(">");
		pnlNav.add(btnPrev);
		pnlNav.add(btnNext);

		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(lblToaInfo, BorderLayout.NORTH);
		pnlNorth.add(pnlNav, BorderLayout.CENTER);

		// viewport container (we will swap views into the scroll pane viewport)
		pnlCho = new JPanel(new BorderLayout());
		scrollCho = new JScrollPane(pnlCho);
		scrollCho.setBorder(BorderFactory.createEmptyBorder());
		scrollCho.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollCho.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollCho.setPreferredSize(stableViewportPreferredSize);

		add(pnlNorth, BorderLayout.NORTH);
		add(scrollCho, BorderLayout.CENTER);

		setPreferredSize(new Dimension(10, 200));

		btnPrev.addActionListener(e -> showPrevToa());
		btnNext.addActionListener(e -> showNextToa());
	}

	public void setController(PanelBuoc2Controller c) {
		this.panelBuoc2Controller = c;
	}

	public void setToaList(List<Toa> list) {
		this.toaList = list == null ? Collections.emptyList() : new ArrayList<>(list);
		this.currentIndex = 0;
		this.doanTauLen = this.toaList.size();
		panelCache.clear();
		if (this.toaList.isEmpty()) {
			setCurrentToa(null);
		} else {
			setCurrentToa(this.toaList.get(0));
		}
	}

	public void setCurrentToa(Toa t) {
		this.currentToa = t;
		if (t == null) {
			lblToaInfo.setText("Chưa chọn toa");
			showEmptyView("Không có toa");
			return;
		} else {
			String soToa = String.valueOf(t.getSoToa());
			String moTaHang = null;
			try {
				moTaHang = (t.getHangToa().getDescription() != null) ? t.getHangToa().getDescription()
						: t.getHangToa().toString();
			} catch (Throwable ex) {
				try {
					moTaHang = t.getHangToa().getDescription();
				} catch (Throwable ignored) {
					moTaHang = "";
				}
			}
			lblToaInfo.setText("Toa số " + soToa + ": " + (moTaHang == null ? "" : moTaHang));
		}

		// If cached -> swap immediately
		try {
			String key = currentToa.getSoToa();
			if (key != null && panelCache.containsKey(key)) {
				swapViewportView(panelCache.get(key));
				return;
			}
		} catch (Throwable ignored) {
		}

		// show loading placeholder and disable nav
		setNavEnabled(false);
		showLoadingView();

		if (panelBuoc2Controller != null && t != null) {
			loading = true;
			panelBuoc2Controller.loadSeatsForToa(t, gheList -> {
				SwingUtilities.invokeLater(() -> {
					JPanel created = buildSeatPanel(t, gheList == null ? Collections.emptyList() : gheList);
					// cache it
					try {
						panelCache.put(t.getSoToa(), created);
					} catch (Throwable ignored) {
					}
					swapViewportView(created);
					loading = false;
					setNavEnabled(true);
				});
			});
		} else {
			// no controller: empty grid
			SwingUtilities.invokeLater(() -> {
				JPanel created = buildSeatPanel(t, Collections.emptyList());
				panelCache.put(t.getSoToa(), created);
				swapViewportView(created);
				setNavEnabled(true);
			});
		}
	}

	private void setNavEnabled(boolean enabled) {
		btnPrev.setEnabled(enabled);
		btnNext.setEnabled(enabled);
	}

	private void showLoadingView() {
		JLabel loading = new JLabel("Đang tải...", SwingConstants.CENTER);
		loading.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		swapViewportView(loading);
	}

	private void showEmptyView(String text) {
		JLabel empty = new JLabel(text, SwingConstants.CENTER);
		empty.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		swapViewportView(empty);
	}

	private void swapViewportView(Component comp) {
		JViewport vp = scrollCho.getViewport();
		vp.setView(comp);
		vp.revalidate();
		vp.repaint();
	}

	private JPanel buildSeatPanel(Toa toa, List<Ghe> gheList) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		// sort seats by numeric seat number
		List<Ghe> sorted = new ArrayList<>(gheList);
		sorted.sort(Comparator.comparingInt(Ghe::getSoGhe).thenComparing(g -> String.valueOf(g.getSoGhe())));

		int cols = DEFAULT_COLUMNS;
		if (sorted.size() <= cols) {
			cols = Math.max(1, sorted.size());
		}
		int rows = (int) Math.ceil(sorted.size() / (double) cols);
		rows = Math.max(1, rows);

		panel.setLayout(new GridLayout(rows, cols, H_GAP, V_GAP));

		for (Ghe g : sorted) {
			JButton b = new JButton(String.valueOf(g.getSoGhe()));
			b.setMargin(new Insets(2, 2, 2, 2));
			b.setPreferredSize(new Dimension(CELL_W, CELL_H));
			b.setFocusPainted(false);

			if (g.getTrangThai() == TrangThaiGhe.DA_BAN) {
				b.setBackground(new Color(220, 53, 53));
				b.setEnabled(false);
				b.setForeground(Color.WHITE);
			} else {
				b.setBackground(Color.WHITE);
				b.setEnabled(true);
				b.setForeground(Color.BLACK);
			}

			b.addActionListener(e -> {
				if (btnSelectedCho != null && btnSelectedCho != b) {
					boolean prevSold = !btnSelectedCho.isEnabled();
					btnSelectedCho.setBackground(prevSold ? new Color(220, 53, 53) : Color.WHITE);
					btnSelectedCho.setForeground(prevSold ? Color.WHITE : Color.BLACK);
				}
				btnSelectedCho = b;
				b.setBackground(new Color(40, 167, 69));
				b.setForeground(Color.WHITE);
				if (panelBuoc2Controller != null) {
					panelBuoc2Controller.onSeatClicked(toa, g);
				}
			});

			panel.add(b);
		}

		// fillers so grid remains stable even when seats < rows*cols
		int total = rows * cols;
		for (int i = sorted.size(); i < total; i++) {
			panel.add(Box.createRigidArea(new Dimension(CELL_W, CELL_H)));
		}

		return panel;
	}

	private void showPrevToa() {
		if (toaList == null || toaList.isEmpty()) {
			return;
		}
		if (loading) {
			return;
		}
		currentIndex = (currentIndex - 1 + toaList.size()) % toaList.size();
		Toa toa = toaList.get(currentIndex);
		setCurrentToa(toa);
		if (panelBuoc2Controller != null) {
			panelBuoc2Controller.highlightToa(toa);
		}
	}

	private void showNextToa() {
		if (toaList == null || toaList.isEmpty()) {
			return;
		}
		if (loading) {
			return;
		}
		currentIndex = (currentIndex + 1) % toaList.size();
		Toa toa = toaList.get(currentIndex);
		setCurrentToa(toa);
		if (panelBuoc2Controller != null) {
			panelBuoc2Controller.highlightToa(toa);
		}
	}

	// used by controller when user selects a chuyen -> set toa list
	public void setToaListAndSelect(java.util.List<Toa> list, int selectIndex) {
		setToaList(list);
		if (list != null && !list.isEmpty()) {
			currentIndex = Math.min(Math.max(0, selectIndex), list.size() - 1);
			setCurrentToa(list.get(currentIndex));
		}
	}
}