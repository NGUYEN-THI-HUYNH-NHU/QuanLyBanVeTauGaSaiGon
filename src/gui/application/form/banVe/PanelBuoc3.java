package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3.java  1.0  [10:39:57 AM] Sep 28, 2025
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import gui.tuyChinh.CurrencyRenderer;

public class PanelBuoc3 extends JPanel {
	private final VeBanTableModel model;
	private final JTable table;
	private final JButton btnConfirm;
	private final JButton btnCancel;
	private JPanel formKhachHang;
	private JTextField txtTen;
	private JTextField txtCccd;
	private JTextField txtPhone;
	private JLabel lblError;

	private Consumer<PassengerRow> deleteListener;

	private PanelBuoc3Controller controller;
	private JTextField txtEmail;
	private JButton btnRefresh;

	public PanelBuoc3() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Nhập thông tin hành khách"));

		model = new VeBanTableModel();
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMaxWidth(36);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(2).setMinWidth(110);
		table.getColumnModel().getColumn(1).setCellRenderer(new PassengerCellRenderer());
		table.getColumnModel().getColumn(1).setCellEditor(new PassengerCellEditor(this));
		table.removeColumn(table.getColumnModel().getColumn(6));
		table.removeColumn(table.getColumnModel().getColumn(6));
		table.removeColumn(table.getColumnModel().getColumn(6));
		table.removeColumn(table.getColumnModel().getColumn(6));

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);

		int deleteColumnIndex = 6;
		TableColumn deleteColumn = table.getColumnModel().getColumn(deleteColumnIndex);
		deleteColumn.setCellRenderer(new DeleteButtonRenderer());
		deleteColumn.setCellEditor(new DeleteButtonEditor());
		deleteColumn.setPreferredWidth(40);
		deleteColumn.setMaxWidth(40);

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int column = table.getColumnModel().getColumnIndexAtX(e.getX());
				int row = e.getY() / table.getRowHeight();

				// Kiểm tra nếu click đúng vào cột 5 và trong phạm vi bảng
				if (row < table.getRowCount() && row >= 0 && column == deleteColumnIndex) {
					// Lấy PassengerRow tại dòng đó
					PassengerRow rowToDelete = model.getRowAt(row);
					if (deleteListener != null) {
						deleteListener.accept(rowToDelete);
					}
				}
			}
		});

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);

		JPanel south = new JPanel(new BorderLayout());

		btnCancel = new JButton("Hủy");
		btnConfirm = new JButton("Xác nhận");
		btnConfirm.setBackground(new Color(36, 104, 155));
		btnRefresh = new JButton("Làm mới");
		btnRefresh.setIcon(new FlatSVGIcon("gui/icon/svg/refresh-1.svg", 0.6f));

		JPanel leftPanel = new JPanel(new GridLayout(1, 1));
		leftPanel.add(btnRefresh);

		JPanel rightPanel = new JPanel(new GridLayout(1, 2, 5, 0));
		rightPanel.add(btnCancel);
		rightPanel.add(btnConfirm);

		south.add(leftPanel, BorderLayout.WEST);
		south.add(rightPanel, BorderLayout.EAST);

		add(south, BorderLayout.SOUTH);

		// FORM KHÁCH HÀNG (bên phải)
		formKhachHang = new JPanel(new GridBagLayout());
		formKhachHang.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
		formKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
		formKhachHang.setSize(new Dimension(300, 0));

		txtTen = new JTextField(18);
		txtCccd = new JTextField(18);
		txtPhone = new JTextField(18);
		txtEmail = new JTextField(18);
		txtTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Họ và Tên");
		txtCccd.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số CCCD/Hộ chiếu");
		txtPhone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số điện thoại");
		txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Email");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(2, 2, 6, 2);
		JLabel lblNguoiMuaVe = new JLabel("Người mua vé", SwingConstants.CENTER);
		lblNguoiMuaVe.setFont(lblNguoiMuaVe.getFont().deriveFont(Font.BOLD, 14f));
		lblNguoiMuaVe.setForeground(new Color(0, 145, 212));
//		formKhachHang.add(lblNguoiMuaVe, gbc);

		gbc.insets = new Insets(1, 2, 1, 2);
		gbc.gridwidth = 1;

		// ====== row 0: CCCD/Hộ chiếu * ======
		gbc.gridy = 0;
		formKhachHang.add(new JLabel("<html>Số CCCD/Hộ chiếu <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 1;
		formKhachHang.add(txtCccd, gbc);

		// ====== row 1: Họ và tên * ======
		gbc.gridy = 2;
		formKhachHang.add(new JLabel("<html>Họ và Tên <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 3;
		formKhachHang.add(txtTen, gbc);

		// ====== row 2: Số điện thoại * ======
		gbc.gridy = 4;
		formKhachHang.add(new JLabel("<html>Số điện thoại <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 5;
		formKhachHang.add(txtPhone, gbc);

		// ====== row 3: Email * ======
		gbc.gridy = 6;
		formKhachHang.add(new JLabel("<html>Email <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 7;
		formKhachHang.add(txtEmail, gbc);

		gbc.gridy = 8;
		formKhachHang.add(Box.createVerticalStrut(20));

		// ====== row 4: lblError * ======
		gbc.gridy = 9;
		lblError = new JLabel("");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font(lblError.getFont().getName(), Font.ITALIC, 11));
		lblError.setVisible(false);
		formKhachHang.add(lblError, gbc);

		add(formKhachHang, BorderLayout.EAST);
	}

	public JLabel getLblError() {
		return lblError;
	}

	public void setController(PanelBuoc3Controller controller) {
		this.controller = controller;
	}

	public PanelBuoc3Controller getController() {
		return this.controller;
	}

	public JTable getTable() {
		return table;
	}

	public JTextField getTxtTenNguoiMua() {
		return this.txtTen;
	}

	public JTextField getTxtCccdNguoiMua() {
		return this.txtCccd;
	}

	public JTextField getTxtPhoneNguoiMua() {
		return this.txtPhone;
	}

	public JTextField getTxtEmailNguoiMua() {
		return this.txtEmail;
	}

	public VeBanTableModel getModel() {
		return model;
	}

	public JButton getRefreshButton() {
		return btnRefresh;
	}

	public JButton getConfirmButton() {
		return btnConfirm;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public void setPassengerDeleteListener(Consumer<PassengerRow> listener) {
		this.deleteListener = listener;
	}

	/**
	 * Khởi tạo bảng từ BookingSession (lấy VeSession cho tripIndex)
	 */
	public void initFromBookingSession(BookingSession session, int tripIndex) {
		model.clear();
		if (session == null) {
			return;
		}
		List<VeSession> vs = session.getAllSelectedTickets();
		if (vs == null || vs.isEmpty()) {
			return;
		}

		List<PassengerRow> rows = new ArrayList<>();
		for (VeSession v : vs) {
			PassengerRow r = new PassengerRow(v);
			rows.add(r);
		}
		model.setRows(rows);
	}

	public List<PassengerRow> getPassengerRows() {
		// stop editor if any
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		return model.getRowsCopy();
	}

	public void setComponentsEnabled(boolean enabled) {
		// Vô hiệu hóa chính PanelBuoc3
		super.setEnabled(enabled);
		for (Component comp : this.getComponents()) {
			comp.setEnabled(enabled);
		}
	}

	public void focusErrorRow(int rowIndex) {
		int vColIndex = VeBanTableModel.COL_HANH_KHACH;

		// 1. Cuộn màn hình đến dòng lỗi (nếu danh sách dài)
		table.scrollRectToVisible(table.getCellRect(rowIndex, vColIndex, true));

		// 2. Chọn dòng đó (về mặt giao diện)
		table.setRowSelectionInterval(rowIndex, rowIndex);

		// 3. Kích hoạt chế độ chỉnh sửa (Edit Mode)
		// Nếu không edit, JTable chỉ vẽ hình ảnh (Renderer) chứ không phải Component
		// thật
		if (table.editCellAt(rowIndex, vColIndex)) {

			// 4. Lấy component đang edit (Chính là PassengerCellPanel thực sự đang sống)
			Component editorComp = table.getEditorComponent();

			if (editorComp instanceof PassengerCellPanel) {
				SwingUtilities.invokeLater(() -> {
					((PassengerCellPanel) editorComp).focusTxtCCCD();
				});
			}
		}
	}
}