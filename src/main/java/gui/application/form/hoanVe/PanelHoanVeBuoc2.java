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
import javax.swing.JCheckBox;
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

	private VeHoanTableModel model;
	private JTable table;
	private JButton btnTiepTuc;

	private JTextField txtTen;
	private JTextField txtCccd;
	private JTextField txtPhone;

	private static final DecimalFormat df = new DecimalFormat("#,##0đ");
	private static final Color disableBgColor = new Color(255, 120, 120, 60);

	public PanelHoanVeBuoc2() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new VeHoanTableModel();
		table = new JTable(model);

		setupTable();

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(0, 280));
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
		table.setRowHeight(90);

		// Cấu hình độ rộng cột
		table.getColumnModel().getColumn(VeHoanTableModel.COL_STT).setMaxWidth(30);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setMinWidth(150);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setMinWidth(170);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_PHI).setMinWidth(120);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_CHON).setMaxWidth(50);

		// 1. Renderer cho tiền (căn phải, định dạng)
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

				applyRowStyle(label, table, row);
				return label;
			}
		};

		// 2. RENDERER CHO CỘT THỜI GIAN
		DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(SwingConstants.CENTER);
				setVerticalAlignment(SwingConstants.TOP);

				// Lấy row model để check logic riêng của cột này
				int modelRow = table.convertRowIndexToModel(row);
				VeHoanRow dataRow = model.getRows().get(modelRow);

				// Tô màu đỏ chữ cảnh báo
				if (!dataRow.isDuDieuKien()) {
					c.setForeground(Color.RED);
					setFont(getFont().deriveFont(Font.BOLD));
				} else {
					c.setForeground(Color.GREEN);
					setFont(getFont().deriveFont(Font.BOLD));
				}

				applyRowStyle(c, table, row);
				return c;
			}
		};

		// --- 3. RENDERER CHO CÁC CỘT TEXT (HỌ TÊN, THÔNG TIN VÉ) ---
		// Phải chuyển sang Anonymous Class để nhúng logic tô màu nền
		DefaultTableCellRenderer topAlignRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				setHorizontalAlignment(SwingConstants.LEFT);
				setVerticalAlignment(SwingConstants.TOP);

				// ÁP DỤNG STYLE XÁM
				applyRowStyle(c, table, row);

				return c;
			}
		};

		// --- 4. RENDERER CHO CỘT TEXT AREA (THÔNG TIN PHÍ) ---
		// Chúng ta cần bọc nó lại để áp dụng màu nền
		TableCellRenderer originalTextAreaRenderer = new TextAreaRenderer();

		TableCellRenderer wrappedTextAreaRenderer = new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// Lấy component gốc từ TextAreaRenderer của bạn
				Component c = originalTextAreaRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);

				// ÁP DỤNG STYLE XÁM
				applyRowStyle(c, table, row);

				return c;
			}
		};

		// --- 5. RENDERER CHO CỘT CHECKBOX (CHỌN VÉ ĐỔI) ---
		TableCellRenderer booleanRenderer = new TableCellRenderer() {
			private final JCheckBox checkBox = new JCheckBox();
			{
				checkBox.setHorizontalAlignment(SwingConstants.CENTER);
				checkBox.setOpaque(true);
			}

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// 1. Set giá trị (Checked/Unchecked)
				if (value instanceof Boolean) {
					checkBox.setSelected((Boolean) value);
				}

				// 2. Lấy dữ liệu dòng để kiểm tra điều kiện disable
				int modelRow = table.convertRowIndexToModel(row);
				VeHoanRow dataRow = model.getRows().get(modelRow);

				// 3. UX: Disable visual của checkbox nếu không đủ điều kiện
				// (Làm mờ ô vuông checkbox)
				checkBox.setEnabled(dataRow.isDuDieuKien());

				if (isSelected) {
					checkBox.setBackground(table.getSelectionBackground());
					checkBox.setForeground(table.getSelectionForeground());
				} else {
					checkBox.setBackground(table.getBackground());
					checkBox.setForeground(table.getForeground());
				}

				// 4. ÁP DỤNG MÀU NỀN (Xử lý vấn đề màu xanh khi click)
				applyRowStyle(checkBox, table, row);

				return checkBox;
			}
		};

		// Cột checkbox
		table.getColumnModel().getColumn(VeHoanTableModel.COL_CHON).setCellRenderer(booleanRenderer);

		// Cột Thời gian
		table.getColumnModel().getColumn(VeHoanTableModel.COL_TG_CON_LAI).setCellRenderer(timeRenderer);
		// Cột Tiền
		table.getColumnModel().getColumn(VeHoanTableModel.COL_THANH_TIEN).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_LE_PHI).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_TIEN_HOAN).setCellRenderer(currencyFormatRenderer);
		// Cột Text Area (Thông tin phí)
		table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_PHI).setCellRenderer(wrappedTextAreaRenderer);
		// Cột Text thường (Tên, Thông tin vé)
		table.getColumnModel().getColumn(VeHoanTableModel.COL_STT).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setCellRenderer(topAlignRenderer);

		table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO));
	}

	/**
	 * Phương thức chung để tô màu nền cho dòng dựa trên điều kiện hoàn vé.
	 */
	private void applyRowStyle(Component c, JTable table, int row) {
		int modelRow = table.convertRowIndexToModel(row);
		VeHoanRow dataRow = model.getRows().get(modelRow);

		if (!dataRow.isDuDieuKien()) {
			if (c instanceof JCheckBox) {
				c.setBackground(new Color(251, 219, 219));
			} else {
				c.setBackground(disableBgColor);
				if (c.getForeground() != Color.RED) {
					c.setForeground(Color.GRAY);
				}
			}
		} else {
			if (table.isRowSelected(row)) {
				c.setBackground(table.getSelectionBackground());
				if (c.getForeground() != Color.GREEN) {
					c.setForeground(table.getSelectionForeground());
				}
			} else {
				c.setBackground(getBackground());
				if (c.getForeground() != Color.GREEN) {
					c.setForeground(getForeground());
				}
			}
		}
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
		lblNguoiMuaVe.setForeground(new Color(0, 145, 212));
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