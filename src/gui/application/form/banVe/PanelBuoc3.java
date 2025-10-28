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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import entity.KhachHang;
import gui.tuyChinh.RoundedBorder;

public class PanelBuoc3 extends JPanel {
	private final HanhKhachTableModel model;
	private final JTable table;
	private final JButton btnConfirm;
	private final JButton btnCancel;
	private final JLabel lblInfo;
	private JPanel formKhachHang;
	private JTextField txtTen;
	private JTextField txtCmnd;
	private JTextField txtPhone;

	public PanelBuoc3() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Nhập thông tin hành khách"));

		model = new HanhKhachTableModel();
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMinWidth(250);
		table.getColumnModel().getColumn(0).setCellRenderer(new PassengerCellRenderer());
		table.getColumnModel().getColumn(0).setCellEditor(new PassengerCellEditor());
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(2).setCellRenderer(center);
		table.getColumnModel().getColumn(3).setCellRenderer(center);
		table.getColumnModel().getColumn(4).setCellRenderer(center);

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);

		JPanel south = new JPanel(new BorderLayout());
		lblInfo = new JLabel("Nhập thông tin hành khách cho các vé đã chọn", SwingConstants.LEFT);
		south.add(lblInfo, BorderLayout.WEST);

		JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnConfirm = new JButton("Xác nhận");
		btnCancel = new JButton("Hủy");
		btns.add(btnCancel);
		btns.add(btnConfirm);
		south.add(btns, BorderLayout.EAST);

		add(south, BorderLayout.SOUTH);

		formKhachHang = new JPanel(new GridBagLayout());
		formKhachHang.setBorder(new RoundedBorder(0, new Color(230, 230, 230), 1));

		txtTen = new JTextField(18);
		txtCmnd = new JTextField(18);
		txtPhone = new JTextField(18);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		// row 0: Họ và tên *
		gbc.gridy = 0;
		formKhachHang.add(new JLabel("Họ và tên *"), gbc);
		gbc.gridy = 1;
		formKhachHang.add(txtTen, gbc);

		// row 1: CMND/Hộ chiếu *
		gbc.gridy = 2;
		formKhachHang.add(new JLabel("Số CMND/Hộ chiếu *"), gbc);
		gbc.gridy = 3;
		formKhachHang.add(txtCmnd, gbc);

		// row 2: Số di động *
		gbc.gridy = 4;
		formKhachHang.add(new JLabel("Số di động *"), gbc);
		gbc.gridy = 5;
		formKhachHang.add(txtPhone, gbc);

		// spacer and note
		gbc.gridy = 6;
		gbc.weighty = 1.0;
		formKhachHang.add(Box.createVerticalGlue(), gbc);

		add(formKhachHang, BorderLayout.EAST);
	}

	/**
	 * Khởi tạo bảng từ BookingSession (lấy VeSession cho tripIndex)
	 */
	public void initFromBookingSession(BookingSession session, int tripIndex) {
		model.clear();
		if (session == null) {
			return;
		}
		List<VeSession> vs = session.getSelectedTicketsForTrip(tripIndex);
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

		// --- FIX: force UI delegate refresh on EDT so FlatLaf re-applies rounded
		// corners ---
		SwingUtilities.invokeLater(() -> {
			// update UI for this panel only (cheaper than update entire app)
			SwingUtilities.updateComponentTreeUI(this);
			// optional: revalidate/repaint to ensure layout and painting refreshed
			this.revalidate();
			this.repaint();
		});
	}

	public List<PassengerRow> getPassengerRows() {
		// stop editor if any
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		return model.getRowsCopy();
	}

	/*
	 * Lấy thông tin người mua từ form và đóng gói vào entity KhachHang.
	 */
	public KhachHang getNguoiMua() {
		KhachHang nguoiMua = new KhachHang();
		nguoiMua.setHoTen(txtTen.getText().trim());
		nguoiMua.setSoGiayTo(txtCmnd.getText().trim());
		nguoiMua.setSoDienThoai(txtPhone.getText().trim());
		return nguoiMua;
	}

	public JButton getConfirmButton() {
		return btnConfirm;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public boolean validateRows() {
		for (PassengerRow r : model.getRowsCopy()) {
			if (r.getFullName() == null || r.getFullName().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đầy đủ cho tất cả hành khách trong bảng.", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			// Thêm validate cho idNumber nếu cần
			if (r.getIdNumber() == null || r.getIdNumber().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập Số giấy tờ cho hành khách: " + r.getFullName(),
						"Lỗi", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		return true;
	}

	public void setComponentsEnabled(boolean enabled) {
		// Vô hiệu hóa chính PanelBuoc3
		super.setEnabled(enabled);
		for (Component comp : this.getComponents()) {
			comp.setEnabled(enabled);
		}
	}
}