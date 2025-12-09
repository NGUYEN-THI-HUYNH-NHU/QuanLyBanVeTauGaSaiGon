package gui.application.form.bieuGia;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import bus.BieuGiaVe_BUS;
import bus.Tuyen_BUS;
import entity.BieuGiaVe;
import entity.Tuyen;

public class BieuGiaController {
	private final PanelQuanLyBieuGia view;

	private final BieuGiaVe_BUS bieuGiaVeBUS;
	private final Tuyen_BUS tuyenBus;
	private List<BieuGiaVe> listCache;

	// Suggestion Components
	private JPopupMenu tuyenSuggestionPopup;
	private Tuyen selectedTuyenSuggest = null;

	public BieuGiaController(PanelQuanLyBieuGia view) {
		this.view = view;
		this.bieuGiaVeBUS = new BieuGiaVe_BUS();
		this.tuyenBus = new Tuyen_BUS();

		this.tuyenSuggestionPopup = new JPopupMenu();
		this.tuyenSuggestionPopup.setFocusable(false);

		loadData();
		initController();
	}

	private void initController() {
		// 1. Sự kiện nút Thêm (ở Panel Lọc)
		view.getBtnThemMoi().addActionListener(e -> handleThem());

		// 2. Sự kiện Tìm kiếm & Làm mới
		view.getBtnTimKiem().addActionListener(e -> handleTimKiem());
		view.getBtnLamMoi().addActionListener(e -> loadData());

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

	private void handleTimKiem() {
		// TODO Auto-generated method stub
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
}