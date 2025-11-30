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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.formdev.flatlaf.FlatClientProperties;

import gui.tuyChinh.RoundedBorder;

public class PanelBuoc3 extends JPanel {
	private final HanhKhachTableModel model;
	private final JTable table;
	private final JButton btnConfirm;
	private final JButton btnCancel;
	private final JLabel lblInfo;
	private JPanel formKhachHang;
	private JTextField txtTen;
	private JTextField txtCccd;
	private JTextField txtPhone;
	private JLabel lblError;

	private Consumer<PassengerRow> deleteListener;

	private PanelBuoc3Controller controller;

	public PanelBuoc3() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Nhập thông tin hành khách"));

		model = new HanhKhachTableModel();
//		{ "Hành khách", "Vé", "Giá", "Phòng chờ", "Giá dịch vụ", "Giảm đối tượng", "Khuyến mãi", "Thành tiền", "" }
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMinWidth(200);
		table.getColumnModel().getColumn(1).setMinWidth(110);
		table.getColumnModel().getColumn(0).setCellRenderer(new PassengerCellRenderer());
		table.getColumnModel().getColumn(0).setCellEditor(new PassengerCellEditor(this));
		table.removeColumn(table.getColumnModel().getColumn(5));
		table.removeColumn(table.getColumnModel().getColumn(5));
		table.removeColumn(table.getColumnModel().getColumn(5));

		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(2).setCellRenderer(right);
		table.getColumnModel().getColumn(4).setCellRenderer(right);
		table.getColumnModel().getColumn(5).setCellRenderer(right);

		int deleteColumnIndex = 5;
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
		lblInfo = new JLabel("Nhập thông tin hành khách cho các vé đã chọn", SwingConstants.LEFT);
		south.add(lblInfo, BorderLayout.WEST);

		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnConfirm = new JButton("Xác nhận");
		btnCancel = new JButton("Hủy");
		btns.add(btnConfirm);
		btns.add(btnCancel);
		south.add(btns, BorderLayout.EAST);

		add(south, BorderLayout.SOUTH);

		// FORM KHÁCH HÀNG (bên phải)
		formKhachHang = new JPanel(new GridBagLayout());
		formKhachHang.setBorder(new RoundedBorder(0, new Color(230, 230, 230), 1));

		txtTen = new JTextField(18);
		txtCccd = new JTextField(18);
		txtPhone = new JTextField(18);
		txtTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Họ và Tên");
		txtCccd.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số CCCD/Hộ chiếu");
		txtPhone.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số điện thoại");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		gbc.gridy = 0;
		gbc.insets = new Insets(2, 2, 6, 2);
		JLabel lblNguoiMuaVe = new JLabel("Người mua vé", SwingConstants.CENTER);
		lblNguoiMuaVe.setFont(lblNguoiMuaVe.getFont().deriveFont(Font.BOLD, 14f));
		lblNguoiMuaVe.setForeground(new Color(0, 145, 212));
		formKhachHang.add(lblNguoiMuaVe, gbc);

		gbc.insets = new Insets(2, 2, 2, 2);

		// ====== row 0: CCCD/Hộ chiếu * ======
		gbc.gridy = 1;
		formKhachHang.add(new JLabel("<html>Số CCCD/Hộ chiếu <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 2;
		formKhachHang.add(txtCccd, gbc);

		// ====== row 1: Họ và tên * ======
		gbc.gridy = 3;
		formKhachHang.add(new JLabel("<html>Họ và Tên <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 4;
		formKhachHang.add(txtTen, gbc);

		// ====== row 2: Số điện thoại * ======
		gbc.gridy = 5;
		formKhachHang.add(new JLabel("<html>Số điện thoại <font color='red'>*</font></html>"), gbc);
		gbc.gridy = 6;
		formKhachHang.add(txtPhone, gbc);

		// ====== row 3: lblError * ======
		gbc.gridy = 7;
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

	public HanhKhachTableModel getModel() {
		return model;
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
			lblInfo.setText("Không có vé nào để nhập hành khách.");
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
}