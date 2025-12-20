package controller;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.*;
import java.util.*;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


import bus.BieuGiaVe_BUS;
import bus.Tuyen_BUS;
import entity.BieuGiaVe;
import entity.NhanVien;
import entity.Tuyen;
import gui.application.form.bieuGia.BieuGiaVeTableButtonRenderer;
import gui.application.form.bieuGia.BieuGiaVeTableModel;
import gui.application.form.bieuGia.FormThemSuaBieuGia;
import gui.application.form.bieuGia.PanelQuanLyBieuGia;

public class BieuGia_CTRL {
	private final PanelQuanLyBieuGia view;

	private final BieuGiaVe_BUS bieuGiaVeBUS;
	private final Tuyen_BUS tuyenBus;
	private List<BieuGiaVe> listCache;
	private List<String> lastSuggestionData = new ArrayList<>();

	private JPopupMenu tuyenSuggestionPopup;

	private List<String> listMaBieuGia = new ArrayList<>();
	private List<String> listMaTuyen = new ArrayList<>();
	private List<String> listMaTau = new ArrayList<>();

	private boolean isAdjusting = false;

	public BieuGia_CTRL(PanelQuanLyBieuGia view) {
		this.view = view;
		this.bieuGiaVeBUS = new BieuGiaVe_BUS();
		this.tuyenBus = new Tuyen_BUS();

		this.tuyenSuggestionPopup = new JPopupMenu();
		this.tuyenSuggestionPopup.setFocusable(false);

		loadData();
		initController();

		thietLapPhimTat();
	}

	private void thietLapPhimTat() {
		InputMap inputMap = view.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = view.getActionMap();

		KeyStroke keyF5 = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);

		inputMap.put(keyF5, "refresh");

		actionMap.put("refresh", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadData();
			}
		});
	}

	private void initController() {
		view.getBtnThemMoi().addActionListener(e -> handleThem());
		view.getBtnLamMoi().addActionListener(e -> loadData());

		view.getTxtTimKiem().addActionListener(e -> handleTimKiem());
		view.getCboLocTuyen().addActionListener(e -> handleTimKiem());
		view.getCboLocTau().addActionListener(e -> handleTimKiem());

		BieuGiaVeTableButtonRenderer btnRenderer = new BieuGiaVeTableButtonRenderer();
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_XEM).setCellRenderer(btnRenderer);
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_SUA).setCellRenderer(btnRenderer);

		int btnWidth = 40;
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_XEM).setMaxWidth(btnWidth);
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_SUA).setMaxWidth(btnWidth);

		view.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = view.getTable().rowAtPoint(e.getPoint());
				int col = view.getTable().columnAtPoint(e.getPoint());
				if (row < 0) return;

				BieuGiaVe bg = view.getTableModel().getRow(row);

				if (col == BieuGiaVeTableModel.COL_XEM) {
					handleXemChiTiet(bg);
				} else if (col == BieuGiaVeTableModel.COL_SUA) {
					handleSua(bg);
				}
			}
		});

		view.getTable().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int col = view.getTable().columnAtPoint(e.getPoint());
				if (col == BieuGiaVeTableModel.COL_XEM || col == BieuGiaVeTableModel.COL_SUA) {
					view.getTable().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				} else {
					view.getTable().setCursor(Cursor.getDefaultCursor());
				}
			}
		});
	}

	private void extractDataForComboBoxes() {
		listMaBieuGia.clear();
		listMaTuyen.clear();
		listMaTau.clear();

		Set<String> setMa = new HashSet<>();
		Set<String> setTuyen = new HashSet<>();
		Set<String> setTau = new HashSet<>();

		for (BieuGiaVe bg : listCache) {
			if (bg.getBieuGiaVeID() != null) setMa.add(bg.getBieuGiaVeID());
			if (bg.getTuyenApDung() != null) setTuyen.add(bg.getTuyenApDung().getTuyenID());
			if (bg.getLoaiTauApDung() != null) setTau.add(bg.getLoaiTauApDung().toString());
		}

		listMaBieuGia.addAll(setMa);
		listMaTuyen.addAll(setTuyen);
		listMaTau.addAll(setTau);

		Collections.sort(listMaBieuGia);
		Collections.sort(listMaTuyen);
		Collections.sort(listMaTau);

		listMaTuyen.add(0, "Tất cả");
		listMaTau.add(0, "Tất cả");
	}

	private void handleTimKiem() {
		String tuKhoa = ((JTextField)view.getTxtTimKiem().getEditor().getEditorComponent()).getText().trim();
		String textTuyen = ((JTextField)view.getCboLocTuyen().getEditor().getEditorComponent()).getText().trim();
		String maTuyen = textTuyen.isEmpty() ? "Tất cả" : textTuyen;
		String textTau = ((JTextField)view.getCboLocTau().getEditor().getEditorComponent()).getText().trim();
		String loaiTau = textTau.isEmpty() ? "Tất cả" : textTau;

		List<BieuGiaVe> listSearch = bieuGiaVeBUS.timKiem(tuKhoa, maTuyen, loaiTau);
		view.getTableModel().setRows(listSearch);
	}

	private void handleThem() {
		openFormThem();
	}

	private void openFormThem() {
		FormThemSuaBieuGia form = new FormThemSuaBieuGia((Frame) SwingUtilities.getWindowAncestor(view));

		// Nạp dữ liệu Tuyến vào ComboBox
		List<String> dsTuyenHienThi = tuyenBus.getAllMaVaTenTuyen();
		form.loadTuyenData(dsTuyenHienThi);

		form.addBtnLuuListener(e -> {
			try {
				BieuGiaVe bg = form.getModelFromForm();

				// Lấy nhân viên từ View (đã đăng nhập)
				NhanVien nv = view.getNhanVienThucHien();

				String res = bieuGiaVeBUS.themBieuGia(bg, nv);

				if (res.contains("thành công")) {
					JOptionPane.showMessageDialog(form, res);
					form.dispose();
					loadData();
				} else {
					JOptionPane.showMessageDialog(form, res, "Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(form, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Exception", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		});
		form.setVisible(true);
	}

	private void handleXemChiTiet(BieuGiaVe bg) {
		openFormChiTiet();
	}

	private void openFormChiTiet() {
		String selectedID = view.getSelectedID();
		if (selectedID == null) {
			JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng để xem chi tiết!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		BieuGiaVe bgCu = timBieuGiaTheoID(selectedID);

		if (bgCu == null) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu gốc (Có thể đã bị xóa)!");
			loadData();
			return;
		}

		FormThemSuaBieuGia form = new FormThemSuaBieuGia(getParentFrame());

		// Nạp dữ liệu Tuyến
		List<String> dsTuyenHienThi = tuyenBus.getAllMaVaTenTuyen();
		form.loadTuyenData(dsTuyenHienThi);

		form.setModelToForm(bgCu);
		form.enableViewMode();
		form.setVisible(true);
	}

	private void handleSua(BieuGiaVe bg) {
		openFormSua();
	}

	private void openFormSua() {
		String selectedID = view.getSelectedID();
		if (selectedID == null) {
			JOptionPane.showMessageDialog(view, "Vui lòng chọn một dòng để sửa!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		BieuGiaVe bgCu = timBieuGiaTheoID(selectedID);

		if (bgCu == null) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu gốc (Có thể đã bị xóa)!");
			loadData();
			return;
		}

		FormThemSuaBieuGia form = new FormThemSuaBieuGia(getParentFrame());

		// Nạp dữ liệu Tuyến
		List<String> dsTuyenHienThi = tuyenBus.getAllMaVaTenTuyen();
		form.loadTuyenData(dsTuyenHienThi);

		form.setModelToForm(bgCu);

		form.addBtnLuuListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BieuGiaVe bgMoi = form.getModelFromForm();

					// Lấy nhân viên từ View
					NhanVien nv = view.getNhanVienThucHien();

					String result = bieuGiaVeBUS.capNhatBieuGia(bgMoi, nv);

					if (result.contains("thành công")) {
						JOptionPane.showMessageDialog(form, result);
						form.dispose();
						loadData();
					} else {
						JOptionPane.showMessageDialog(form, result, "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(form, "Lỗi nhập liệu: " + ex.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		form.setVisible(true);
	}

	private Frame getParentFrame() {
		return (Frame) SwingUtilities.getWindowAncestor(view);
	}

	private void loadData() {
		listCache = bieuGiaVeBUS.layDanhSachBieuGia();
		view.getTableModel().setRows(listCache);

		extractDataForComboBoxes();

		thietLapAutoComplete();
	}

	private BieuGiaVe timBieuGiaTheoID(String id) {
		if (listCache == null) {
			return null;
		}
		for (BieuGiaVe bg : listCache) {
			if (bg.getBieuGiaVeID().equals(id)) {
				return bg;
			}
		}
		return null;
	}

	// --- Suggestion Logic for MAIN SEARCH PANEL ONLY ---
	private void taoPopupGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem) {
		pp.setFocusable(false);
		lst.setFocusable(false);
		lst.setRequestFocusEnabled(false);
		pp.removeAll();
		pp.add(new JScrollPane(lst));

		txt.getDocument().addDocumentListener(new DocumentListener() {
			private Timer timer;
			@Override public void insertUpdate(DocumentEvent e) { update(); }
			@Override public void removeUpdate(DocumentEvent e) { update(); }
			@Override public void changedUpdate(DocumentEvent e) {}
			private void update() {
				if (isAdjusting) return;
				if (timer != null && timer.isRunning()) timer.stop();
				timer = new Timer(200, ev -> SwingUtilities.invokeLater(() -> {
					if (txt.isFocusOwner()) hienThiGoiY(txt, lst, pp, timKiem);
				}));
				timer.setRepeats(false);
				timer.start();
			}
		});

		lst.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
					txt.setText(lst.getSelectedValue());
					pp.setVisible(false);
					handleTimKiem();
					txt.transferFocus();
				}
			}
		});

		txt.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (!pp.isVisible()) return;
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					isAdjusting = true;
					int index = lst.getSelectedIndex();
					if (index < lst.getModel().getSize() - 1) lst.setSelectedIndex(index + 1);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					isAdjusting = true;
					int index = lst.getSelectedIndex();
					if (index > 0) lst.setSelectedIndex(index - 1);
					e.consume();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					isAdjusting = true;
					if (lst.getSelectedValue() != null) {
						txt.setText(lst.getSelectedValue());
						pp.setVisible(false);
					}
					handleTimKiem();
					txt.transferFocus();
					e.consume();
				}
			}
			@Override public void keyReleased(KeyEvent e) { isAdjusting = false; }
		});

		txt.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				SwingUtilities.invokeLater(() -> {
					pp.setVisible(false);
					lastSuggestionData.clear();
				});
			}
		});
	}

	private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem) {
		String input = txt.getText().trim();
		List<String> ds = timKiem.apply(input);
		if (ds == null || ds.isEmpty()) {
			pp.setVisible(false);
			lastSuggestionData.clear();
			return;
		}
		if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
			pp.setVisible(false);
			lastSuggestionData.clear();
			return;
		}
		if (ds.equals(lastSuggestionData) && pp.isVisible()) return;

		lastSuggestionData = new ArrayList<>(ds);
		lst.setListData(ds.toArray(new String[0]));
		lst.setVisibleRowCount(Math.min(ds.size(), 10));
		if (lst.getSelectedIndex() == -1) lst.setSelectedIndex(0);

		if (txt.isFocusOwner()) {
			int popupWidth = Math.max(txt.getWidth(), 100);
			pp.setPopupSize(popupWidth, pp.getPreferredSize().height);
			pp.show(txt, 0, txt.getHeight());
		}
	}

	private List<String> locDuLieu(List<String> source, String input) {
		if (source == null || source.isEmpty()) return new ArrayList<>();
		String inputLower = input.toLowerCase();
		return source.stream().filter(s -> s.toLowerCase().contains(inputLower)).limit(10).collect(java.util.stream.Collectors.toList());
	}

	private void thietLapAutoComplete() {
		setupComboBoxSuggestion(view.getTxtTimKiem(), listMaBieuGia);
		setupComboBoxSuggestion(view.getCboLocTuyen(), listMaTuyen);
		setupComboBoxSuggestion(view.getCboLocTau(), listMaTau);
	}

	private void setupComboBoxSuggestion(JComboBox<String> cbo, List<String> data) {
		cbo.setEditable(true);
		cbo.setModel(new DefaultComboBoxModel<>(data.toArray(new String[0])));
		cbo.setSelectedIndex(-1);
		JTextField txtEditor = (JTextField) cbo.getEditor().getEditorComponent();
		JPopupMenu pp = new JPopupMenu();
		JList<String> lst = new JList<>();
		taoPopupGoiY(txtEditor, pp, lst, input -> locDuLieu(data, input));
	}

}