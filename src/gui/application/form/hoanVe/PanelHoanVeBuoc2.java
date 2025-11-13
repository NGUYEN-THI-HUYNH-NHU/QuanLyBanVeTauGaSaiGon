package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc2.java  1.0  [2:47:14 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.FlatClientProperties;

import entity.KhachHang;
import entity.Ve;
import gui.tuyChinh.TextAreaRenderer;

public class PanelHoanVeBuoc2 extends JPanel {
	private HoanVeBuoc2Controller controller;

	private HoanVeTableModel model;
	private JTable table;
	private JButton btnTiepTuc;

	// Form thông tin người mua (bên phải)
	private JTextField txtTen;
	private JTextField txtCccd;
	private JTextField txtPhone;

	// Renderer
	private static final DecimalFormat df = new DecimalFormat("#,##0đ");

	public PanelHoanVeBuoc2() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new HoanVeTableModel();
		table = new JTable(model);

		setupTable();

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(0, 300));
		add(sp, BorderLayout.CENTER);

		add(createNguoiMuaVePanel(), BorderLayout.NORTH);

		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnTiepTuc = new JButton("Tiếp tục");
		south.add(btnTiepTuc);

		add(south, BorderLayout.SOUTH);

		btnTiepTuc.addActionListener(e -> {
			if (controller != null) {

			}
		});
	}

	private void setupTable() {
		table.setRowHeight(80);

		// Cấu hình độ rộng cột
		table.getColumnModel().getColumn(HoanVeTableModel.COL_TEN).setMinWidth(150);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_THONG_TIN_VE).setMinWidth(150);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_THONG_TIN_PHI).setMinWidth(150);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_CHON).setMaxWidth(50);

		// === Áp dụng Renderer ===

		// 1. Renderer cho tiền (căn phải, định dạng)
		DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
		currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		currencyRenderer.setVerticalAlignment(SwingConstants.TOP); // Căn lên trên
		currencyRenderer.setOpaque(true);

		// Áp dụng lớp Renderer nội tuyến để định dạng
		TableCellRenderer currencyFormatRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// Gọi super để lấy JLabel
				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				label.setHorizontalAlignment(SwingConstants.RIGHT);
				label.setVerticalAlignment(SwingConstants.TOP);
				if (value instanceof Double) {
					label.setText(df.format(value));
				}
				return label;
			}
		};

		table.getColumnModel().getColumn(HoanVeTableModel.COL_THANH_TIEN).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_LE_PHI).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_TIEN_HOAN).setCellRenderer(currencyFormatRenderer);

		TableCellRenderer textAreaRenderer = new TextAreaRenderer();
		table.getColumnModel().getColumn(HoanVeTableModel.COL_THONG_TIN_PHI).setCellRenderer(textAreaRenderer);
		// 2. Renderer cho các cột text (căn trên)
		DefaultTableCellRenderer topAlignRenderer = new DefaultTableCellRenderer();
		topAlignRenderer.setVerticalAlignment(SwingConstants.TOP);

		table.getColumnModel().getColumn(HoanVeTableModel.COL_TEN).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_THONG_TIN_VE).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(HoanVeTableModel.COL_LOAI_HOAN).setCellRenderer(topAlignRenderer);
	}

	/**
	 * Tạo panel thông tin người mua
	 */
	private JPanel createNguoiMuaVePanel() {
		JPanel formKhachHang = new JPanel(new GridBagLayout());
		formKhachHang.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		formKhachHang.setPreferredSize(new Dimension(0, 80));

		txtTen = new JTextField(18);
		txtCccd = new JTextField(18);
		txtPhone = new JTextField(18);

		// Đặt thuộc tính không cho sửa
		txtTen.setEditable(false);
		txtCccd.setEditable(false);
		txtPhone.setEditable(false);

		// Dùng PutClientProperty để hiển thị đẹp hơn khi không cho sửa
		txtTen.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0;");
		txtCccd.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0;");
		txtPhone.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0;");

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 0;

		gbc.gridx = 0;
		gbc.insets = new Insets(2, 2, 6, 2);
		JLabel lblNguoiMuaVe = new JLabel("Người mua vé");
		lblNguoiMuaVe.setFont(lblNguoiMuaVe.getFont().deriveFont(Font.BOLD, 14f));
		lblNguoiMuaVe.setForeground(new Color(232, 75, 2));
		formKhachHang.add(lblNguoiMuaVe, gbc);

		gbc.insets = new Insets(2, 30, 2, 30);

		// ====== Họ và tên ======
		gbc.gridx = 0;
		gbc.gridy = 1;
		formKhachHang.add(new JLabel("Họ và Tên:"), gbc);
		gbc.gridy = 2;
		formKhachHang.add(txtTen, gbc);

		// ====== CCCD ======
		gbc.gridx = 1;
		gbc.gridy = 1;
		formKhachHang.add(new JLabel("Số CCCD/Hộ chiếu:"), gbc);
		gbc.gridy = 2;
		formKhachHang.add(txtCccd, gbc);

		// ====== Số điện thoại ======
		gbc.gridx = 2;
		gbc.gridy = 1;
		formKhachHang.add(new JLabel("Số điện thoại:"), gbc);
		gbc.gridy = 2;
		formKhachHang.add(txtPhone, gbc);

		return formKhachHang;
	}

	/**
	 * Phương thức này được gọi bởi HoanVeBuoc2Controller để đổ dữ liệu vào view.
	 */
	public void showDonDatCho(List<Ve> listVe, KhachHang khachHang) {
		// 1. Cập nhật form thông tin người mua
		if (khachHang != null) {
			txtTen.setText(khachHang.getHoTen());
			txtCccd.setText(khachHang.getSoGiayTo());
			txtPhone.setText(khachHang.getSoDienThoai());
		} else {
			// Xóa thông tin cũ
			txtTen.setText("");
			txtCccd.setText("");
			txtPhone.setText("");
		}

		// 2. Cập nhật bảng
		if (listVe != null && !listVe.isEmpty()) {
			List<VeHoanRow> rows = new ArrayList<>();
			for (Ve ve : listVe) {
				// Logic tính toán phí nằm trong constructor của VeHoanRow
				rows.add(new VeHoanRow(ve));
			}
			model.setRows(rows);
		} else {
			model.setRows(new ArrayList<>());
		}
	}

	// === Getters cho Controller ===

	public JButton getBtnTiepTuc() {
		return btnTiepTuc;
	}

	/**
	 * Trả về danh sách các Vé gốc (entity) đã được chọn để hoàn.
	 */
	public List<Ve> getSelectedVe() {
		List<VeHoanRow> selectedRows = model.getSelectedRows();
		List<Ve> selectedVe = new ArrayList<>();
		for (VeHoanRow row : selectedRows) {
			selectedVe.add(row.getVe());
		}
		return selectedVe;
	}

	/**
	 * Yêu cầu model thông báo cho JTable rằng một dòng đã thay đổi. JTable sẽ đọc
	 * lại dữ liệu từ model cho dòng đó và vẽ lại.
	 */
	public void refreshRow(VeHoanRow row) {
		int rowIndex = model.getRowIndex(row);
		if (rowIndex != -1) {
			// Chỉ cập nhật dòng thay đổi, hiệu quả hơn fireTableDataChanged()
			row.setSelected(false);
			model.fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public List<VeHoanRow> getSelectedVeHoanRows() {
		return model.getSelectedRows();
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}
}
