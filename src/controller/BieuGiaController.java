package controller;
/*
 * @(#) BieuGiaController.java  1.0  [8:36:14 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import bus.BieuGiaVe_BUS;
import bus.Tuyen_BUS;
import entity.BieuGiaVe;
import entity.Tuyen;
import gui.application.form.bieuGia.BieuGiaVeTableButtonRenderer;
import gui.application.form.bieuGia.BieuGiaVeTableModel;
import gui.application.form.bieuGia.FormThemSuaBieuGia;
import gui.application.form.bieuGia.PanelQuanLyBieuGia;

public class BieuGiaController {
	private final PanelQuanLyBieuGia view;

	private final BieuGiaVe_BUS bieuGiaVeBUS;
	private final Tuyen_BUS tuyenBus;
	private List<BieuGiaVe> listCache;
	private List<String> lastSuggestionData = new ArrayList<>();


	// Suggestion Components
	private JPopupMenu tuyenSuggestionPopup;
	private Tuyen selectedTuyenSuggest = null;

	private List<String> listMaBieuGia = new ArrayList<>();
	private List<String> listMaTuyen = new ArrayList<>();
	private List<String> listMaTau = new ArrayList<>();

	private boolean isAdjusting = false;

	public BieuGiaController(PanelQuanLyBieuGia view) {
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
		// 1. Sự kiện nút Thêm (ở Panel Lọc)
		view.getBtnThemMoi().addActionListener(e -> handleThem());

		// 2. Sự kiện Tìm kiếm & Làm mới
//		view.getBtnTimKiem().addActionListener(e -> handleTimKiem());
		view.getBtnLamMoi().addActionListener(e -> loadData());

		view.getTxtTimKiem().addActionListener(e -> handleTimKiem());
		view.getCboLocTuyen().addActionListener(e -> handleTimKiem());
		view.getCboLocTau().addActionListener(e -> handleTimKiem());

		// 1. Setup Renderer cho 3 cột mới
		BieuGiaVeTableButtonRenderer btnRenderer = new BieuGiaVeTableButtonRenderer();
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_XEM).setCellRenderer(btnRenderer);
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_SUA).setCellRenderer(btnRenderer);

		// Set width nhỏ cho cột nút
		int btnWidth = 40;
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_XEM).setMaxWidth(btnWidth);
		view.getTable().getColumnModel().getColumn(BieuGiaVeTableModel.COL_SUA).setMaxWidth(btnWidth);

		// 2. Mouse Listener (Xử lý click 3 cột)
		view.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = view.getTable().rowAtPoint(e.getPoint());
				int col = view.getTable().columnAtPoint(e.getPoint());
				if (row < 0) {
					return;
				}

				BieuGiaVe bg = view.getTableModel().getRow(row);

				if (col == BieuGiaVeTableModel.COL_XEM) {
					handleXemChiTiet(bg);
				} else if (col == BieuGiaVeTableModel.COL_SUA) {
					handleSua(bg);
				}
			}
		});

		// 3. Mouse Motion
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

	private void fillComboBoxData(JComboBox<String> cbo, List<String> data) {
		cbo.setModel(new DefaultComboBoxModel<>(data.toArray(new String[0])));
		cbo.setSelectedIndex(-1);
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
		setupTuyenSuggestion(form); // Gắn logic suggestion vào Form

		form.addBtnLuuListener(e -> {
			try {
				BieuGiaVe bg = form.getModelFromForm();
				// Logic thông minh: Nếu text khớp với đối tượng đã chọn -> dùng đối tượng đó
				String txtInput = form.getTxtTuyenSuggest().getText().trim();
				if (selectedTuyenSuggest != null && (txtInput.equals(selectedTuyenSuggest.getMoTa())
						|| txtInput.equals(selectedTuyenSuggest.getTuyenID()))) {
					bg.setTuyenApDung(selectedTuyenSuggest);
				} else if (!txtInput.isEmpty()) {
					// Trường hợp user gõ mà không chọn suggestion -> Có thể tìm lại hoặc báo lỗi
					// Ở đây giả định user phải chọn đúng
				}

				String res = bieuGiaVeBUS.themBieuGia(bg);
				JOptionPane.showMessageDialog(form, res);
				if (res.contains("thành công")) {
					form.dispose();
					loadData();
				}
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(form, ex.getMessage());
			}
		});
		form.setVisible(true);
	}

	private void setupTuyenSuggestion(FormThemSuaBieuGia form) {
		JTextField txtTuyen = form.getTxtTuyenSuggest();

		txtTuyen.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				showSuggestions(txtTuyen);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				showSuggestions(txtTuyen);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				showSuggestions(txtTuyen);
			}
		});

		txtTuyen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (txtTuyen.getText().trim().isEmpty()) {
					tuyenSuggestionPopup.setVisible(false);
				}
			}
		});

		// Gắn phím điều hướng (Up/Down/Enter)
		addSuggestionKeyListeners(txtTuyen, tuyenSuggestionPopup, null);
	}

	private void showSuggestions(JTextField txtTuyen) {
		String keyword = txtTuyen.getText().trim();
		tuyenSuggestionPopup.setVisible(false);
		tuyenSuggestionPopup.removeAll();

		if (keyword.isEmpty()) {
			return;
		}

		List<Tuyen> list = tuyenBus.layKiemTop10Tuyen(keyword);

		if (!list.isEmpty()) {
			for (Tuyen t : list) {
				String html = String.format("<html><b>%s</b> <i style='color:gray'>(%s)</i></html>", t.getTuyenID(),
						t.getMoTa());
				JMenuItem item = new JMenuItem(html);
				item.setIcon(new FlatSVGIcon("gui/icon/svg/route.svg", 0.6f));

				item.addActionListener(e -> {
					txtTuyen.setText(t.getTuyenID());
					selectedTuyenSuggest = t;
					tuyenSuggestionPopup.setVisible(false);
				});
				tuyenSuggestionPopup.add(item);
			}
			tuyenSuggestionPopup.show(txtTuyen, 0, txtTuyen.getHeight());
			txtTuyen.requestFocus();
		}
	}

	private void addSuggestionKeyListeners(JTextField textField, JPopupMenu popup, Runnable defaultEnterAction) {
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (popup.isVisible()) {
						navigatePopup(popup, 1);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (popup.isVisible()) {
						navigatePopup(popup, -1);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
					if (popup.isVisible() && path != null && path.length > 0 && path[0].getComponent() == popup) {
						Component selectedComp = path[path.length - 1].getComponent();
						if (selectedComp instanceof JMenuItem) {
							((JMenuItem) selectedComp).doClick();
						}
					} else if (defaultEnterAction != null) {
						defaultEnterAction.run();
						popup.setVisible(false);
					}
				}
			}
		});
	}

	private void navigatePopup(JPopupMenu popup, int direction) {
		MenuSelectionManager menuManager = MenuSelectionManager.defaultManager();
		MenuElement[] selection = menuManager.getSelectedPath();
		MenuElement[] items = popup.getSubElements();
		if (items.length == 0) {
			return;
		}

		int selectedIndex = -1;
		if (selection != null && selection.length > 0) {
			Component current = selection[selection.length - 1].getComponent();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getComponent() == current) {
					selectedIndex = i;
					break;
				}
			}
		}
		int nextIndex;
		if (selectedIndex == -1) {
			nextIndex = (direction > 0) ? 0 : items.length - 1;
		} else {
			nextIndex = (selectedIndex + direction + items.length) % items.length;
		}
		MenuElement[] newSelection = new MenuElement[] { popup, items[nextIndex] };
		menuManager.setSelectedPath(newSelection);
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

		// 1. Tìm đối tượng cũ trong list cache (hoặc gọi BUS nếu muốn load mới nhất)
		BieuGiaVe bgCu = timBieuGiaTheoID(selectedID);

		if (bgCu == null) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu gốc (Có thể đã bị xóa)!");
			loadData();
			return;
		}

		// 2. Mở form và đổ dữ liệu cũ vào
		FormThemSuaBieuGia form = new FormThemSuaBieuGia(getParentFrame());
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

		// 1. Tìm đối tượng cũ trong list cache (hoặc gọi BUS nếu muốn load mới nhất)
		BieuGiaVe bgCu = timBieuGiaTheoID(selectedID);

		if (bgCu == null) {
			JOptionPane.showMessageDialog(view, "Không tìm thấy dữ liệu gốc (Có thể đã bị xóa)!");
			loadData();
			return;
		}

		// 2. Mở form và đổ dữ liệu cũ vào
		FormThemSuaBieuGia form = new FormThemSuaBieuGia(getParentFrame());
		form.setModelToForm(bgCu);

		// 3. Xử lý sự kiện Lưu (Cập nhật)
		form.addBtnLuuListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Lấy dữ liệu mới từ form
					BieuGiaVe bgMoi = form.getModelFromForm();

					// Cập nhật
					String result = bieuGiaVeBUS.capNhatBieuGia(bgMoi);

					JOptionPane.showMessageDialog(form, result);
					if (result.contains("thành công")) {
						form.dispose();
						loadData();
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

	// Tìm trong list cache (Thay vì phải query DB 1 lần nữa)
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
					if (txt.isFocusOwner()) {
						hienThiGoiY(txt, lst, pp, timKiem);
					}
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
			@Override
			public void keyPressed(KeyEvent e) {
				if (!pp.isVisible()) return;

				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					isAdjusting = true;
					int index = lst.getSelectedIndex();
					if (index < lst.getModel().getSize() - 1) {
						lst.setSelectedIndex(index + 1);
						lst.ensureIndexIsVisible(index + 1);
					}
					e.consume();
				}
				else if (e.getKeyCode() == KeyEvent.VK_UP) {
					isAdjusting = true;
					int index = lst.getSelectedIndex();
					if (index > 0) {
						lst.setSelectedIndex(index - 1);
						lst.ensureIndexIsVisible(index - 1);
					}
					e.consume();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

			@Override
			public void keyReleased(KeyEvent e) {
				isAdjusting = false;
			}
		});


		txt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				SwingUtilities.invokeLater(() -> {
					pp.setVisible(false);
					lastSuggestionData.clear();
				});
			}

		});
	}

	private void hienThiGoiY(JTextField txt, JList<String> lst,
							 JPopupMenu pp,
							 Function<String, List<String>> timKiem) {

		String input = txt.getText().trim();
		List<String> ds = timKiem.apply(input);

		if (ds == null || ds.isEmpty()) {
			pp.setVisible(false);
			lastSuggestionData.clear();
			return;
		}

		if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
			pp.setVisible(false);
			lastSuggestionData.clear(); // Xóa cache để nếu người dùng xóa bớt ký tự thì popup hiện lại ngay
			return;
		}

		if (ds.equals(lastSuggestionData) && pp.isVisible()) {
			return;
		}

		lastSuggestionData = new ArrayList<>(ds);

		lst.setListData(ds.toArray(new String[0]));
		lst.setVisibleRowCount(Math.min(ds.size(), 10));

		if (lst.getSelectedIndex() == -1) {
			lst.setSelectedIndex(0);
		}

		if (txt.isFocusOwner()) {
			int popupWidth = Math.max(txt.getWidth(), 100);
			pp.setPopupSize(popupWidth, pp.getPreferredSize().height);
			pp.show(txt, 0, txt.getHeight());
		}
	}


	private List<String> locDuLieu(List<String> source, String input) {
		if (source == null || source.isEmpty()) return new java.util.ArrayList<>();
		String inputLower = input.toLowerCase();
		return source.stream()
				.filter(s -> s.toLowerCase().contains(inputLower))
				.limit(10)
				.collect(java.util.stream.Collectors.toList());
	}

	private boolean isMainSearchField(JTextField txt) {
		return txt == view.getTxtTimKiem().getEditor().getEditorComponent() ||
				txt == view.getCboLocTuyen().getEditor().getEditorComponent() ||
				txt == view.getCboLocTau().getEditor().getEditorComponent();
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

