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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;

import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;

public class PanelSoDoCho extends JPanel {
	private final JPanel pnlGridChoNgoi;
	private final JScrollPane scroll;
	private final JButton btnPrev, btnNext;
	private final JLabel lblToaInfo;
	private final JPanel pnlNorth;

	private PanelBuoc2Controller panelBuoc2Controller;
	private JButton btnChoSelected = null;
	private Toa currentToa;
	private List<Toa> toaList;
	private int currentIndex = 0;
	private int doanTauLength;

	private static final int CELL_WIDTH = 20;
	private static final int CELL_HEIGHT = 20;
	private static final int CELL_GAP = 8;
	private static final int VIEWPORT_HEIGHT = 160;
	private static final int VIEWPORT_WIDTH = 400;

	public PanelSoDoCho() {
		setBorder(new TitledBorder("Sơ đồ chỗ"));
		setLayout(new BorderLayout());

		lblToaInfo = new JLabel("Chưa chọn toa", SwingConstants.CENTER);
		lblToaInfo.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 8));

		btnPrev = new JButton("<");
		btnNext = new JButton(">");
		btnPrev.setPreferredSize(new Dimension(25, 10));
		btnNext.setPreferredSize(new Dimension(25, 10));

		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.add(lblToaInfo, BorderLayout.NORTH);

		pnlGridChoNgoi = new JPanel();
		pnlGridChoNgoi.setLayout(new GridLayout(1, 1)); // initial placeholder
		scroll = new JScrollPane(pnlGridChoNgoi);
		scroll.setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
		scroll.setBorder(BorderFactory.createEmptyBorder()); // cleaner look

		add(pnlNorth, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(btnPrev, BorderLayout.WEST);
		add(btnNext, BorderLayout.EAST);

		// Actions
		btnPrev.addActionListener(e -> showPrevToa());
		btnNext.addActionListener(e -> showNextToa());
	}

	// ==== Controller binding ====
	public void setController(PanelBuoc2Controller c) {
		this.panelBuoc2Controller = c;
	}

	public void setToaList(List<Toa> list) {
		this.toaList = list;
		this.doanTauLength = (list == null) ? 0 : list.size();
		this.currentIndex = 0;
		if (list != null && !list.isEmpty()) {
			setCurrentToa(list.get(0));
		} else {
			setCurrentToa(null);
		}
	}

	public void setCurrentToa(Toa t) {
		this.currentToa = t;
		if (t == null) {
			lblToaInfo.setText("Chưa chọn toa");
			showMessage("Không có toa được chọn");
			return;
		}

		lblToaInfo.setText(
				"Toa số " + t.getSoToa() + ": " + (t.getHangToa() != null ? t.getHangToa().getDescription() : ""));

//		showLoadingState();

		// Run seat loading on background thread
		if (panelBuoc2Controller != null) {
			new LoadSeatWorker(t).execute();
		}
	}

	public void showMessage(String text) {
		pnlGridChoNgoi.removeAll();
		pnlGridChoNgoi.setLayout(new BorderLayout());
		pnlGridChoNgoi.add(new JLabel(text, SwingConstants.CENTER), BorderLayout.CENTER);
		pnlGridChoNgoi.revalidate();
		pnlGridChoNgoi.repaint();
	}

//	public void showLoadingState() {
//		showMessage("Đang tải ...");
//	}

	// ==== Seat Rendering ====
	public void renderSeats(List<Ghe> gheList) {
		pnlGridChoNgoi.removeAll();

		if (gheList == null || gheList.isEmpty()) {
			showMessage("Không có ghế");
			return;
		}

		// Sort seats
		List<Ghe> sorted = new ArrayList<>(gheList);
		sorted.sort(Comparator.comparingInt(Ghe::getSoGhe));

		// Layout
		int cols = Math.min(sorted.size(), 6);
		int rows = (int) Math.ceil(sorted.size() / (double) cols);
		pnlGridChoNgoi.setLayout(new GridLayout(rows, cols, CELL_GAP, CELL_GAP));

		for (Ghe g : sorted) {
			JButton b = new JButton(String.valueOf(g.getSoGhe()));
			b.setPreferredSize(new Dimension(CELL_WIDTH, CELL_HEIGHT));
			b.setFocusPainted(false);

			if (g.getTrangThai() == TrangThaiGhe.DA_BAN) {
				b.setBackground(new Color(220, 53, 53));
				b.setEnabled(false);
			} else {
				b.setBackground(Color.WHITE);
				b.setEnabled(true);
			}

			b.addActionListener(e -> {
				if (btnChoSelected != null && btnChoSelected != b) {
					btnChoSelected.setBackground(Color.WHITE);
					btnChoSelected.setForeground(Color.BLACK);
				}
				btnChoSelected = b;
				b.setBackground(new Color(40, 167, 69));
				b.setForeground(Color.WHITE);

				if (panelBuoc2Controller != null) {
					panelBuoc2Controller.onSeatClicked(currentToa, g);
				}
			});

			pnlGridChoNgoi.add(b);
		}

		// Avoid multiple revalidate calls
		pnlGridChoNgoi.revalidate();
		pnlGridChoNgoi.repaint();
	}

	// ==== Navigation ====
	public void showPrevToa() {
		if (toaList == null || toaList.isEmpty()) {
			return;
		}
		currentIndex = (currentIndex == 0) ? doanTauLength - 1 : currentIndex - 1;
		Toa t = toaList.get(currentIndex);
		setCurrentToa(t);
		panelBuoc2Controller.highlightToa(t);
	}

	public void showNextToa() {
		if (toaList == null || toaList.isEmpty()) {
			return;
		}
		currentIndex = (currentIndex == doanTauLength - 1) ? 0 : currentIndex + 1;
		Toa t = toaList.get(currentIndex);
		setCurrentToa(t);
		panelBuoc2Controller.highlightToa(t);
	}

	// ==== Background Worker ====
	private class LoadSeatWorker extends SwingWorker<List<Ghe>, Void> {
		private final Toa toa;

		LoadSeatWorker(Toa t) {
			this.toa = t;
		}

		@Override
		protected List<Ghe> doInBackground() throws Exception {
			final List<Ghe>[] resultHolder = new List[1];
			final Object lock = new Object();

			panelBuoc2Controller.loadSeatsForToa(toa, gheList -> {
				synchronized (lock) {
					resultHolder[0] = gheList;
					lock.notifyAll();
				}
			});

			synchronized (lock) {
				while (resultHolder[0] == null) {
					lock.wait();
				}
			}
			return resultHolder[0];
		}

		@Override
		protected void done() {
			try {
				List<Ghe> seats = get();
				SwingUtilities.invokeLater(() -> renderSeats(seats));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				showMessage("Lỗi tải dữ liệu chỗ ngồi");
			}
		}
	}
}