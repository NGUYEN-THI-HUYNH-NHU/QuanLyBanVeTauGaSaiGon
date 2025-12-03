package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVeBuoc2.java  1.0  [5:27:30 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
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
import entity.PhieuDungPhongVIP;
import entity.Ve;
import gui.tuyChinh.TextAreaRenderer;

public class PanelDoiVeBuoc2 extends JPanel {
	private DoiVeBuoc2Controller controller;

	private VeDoiTableModel model;
	private JTable table;
	private JButton btnTiepTuc;

	private JTextField txtTen;
	private JTextField txtCccd;
	private JTextField txtPhone;

	// Renderer
	private static final DecimalFormat df = new DecimalFormat("#,##0đ");

	public PanelDoiVeBuoc2() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new VeDoiTableModel();
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
	}

	private void setupTable() {
		table.setRowHeight(90);

		table.removeColumn(table.getColumnModel().getColumn(VeDoiTableModel.COL_LY_DO));

		// Cấu hình độ rộng cột
		table.getColumnModel().getColumn(VeDoiTableModel.COL_TEN).setMinWidth(150);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_THONG_TIN_VE_DOI).setMinWidth(150);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_THONG_TIN_PHI).setMinWidth(100);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_CHON - 1).setMaxWidth(50);

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

				// Lấy row model để check logic riêng của cột này
				int modelRow = table.convertRowIndexToModel(row);
				VeDoiRow dataRow = model.getRows().get(modelRow);

				// Logic riêng: Tô màu đỏ chữ cảnh báo
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
		timeRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		// --- 3. RENDERER CHO CÁC CỘT TEXT (HỌ TÊN, THÔNG TIN VÉ) ---
		// Phải chuyển sang Anonymous Class để nhúng logic tô màu nền
		DefaultTableCellRenderer topAlignRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// Căn lề trên
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
				VeDoiRow dataRow = model.getRows().get(modelRow);

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
		table.getColumnModel().getColumn(VeDoiTableModel.COL_CHON - 1).setCellRenderer(booleanRenderer);

		// Cột Thời gian
		table.getColumnModel().getColumn(VeDoiTableModel.COL_TG_CON_LAI - 1).setCellRenderer(timeRenderer);
		// Cột Tiền
		table.getColumnModel().getColumn(VeDoiTableModel.COL_THANH_TIEN).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_LE_PHI).setCellRenderer(currencyFormatRenderer);
		// Cột Text Area (Thông tin phí)
		table.getColumnModel().getColumn(VeDoiTableModel.COL_THONG_TIN_PHI).setCellRenderer(wrappedTextAreaRenderer);
		// Cột Text thường (Tên, Thông tin vé)
		table.getColumnModel().getColumn(VeDoiTableModel.COL_TEN).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_THONG_TIN_VE_DOI).setCellRenderer(topAlignRenderer);
	}

	/**
	 * Phương thức chung để tô màu nền cho dòng dựa trên điều kiện đổi vé.
	 */
	private void applyRowStyle(Component c, JTable table, int row) {
		int modelRow = table.convertRowIndexToModel(row);
		VeDoiRow dataRow = model.getRows().get(modelRow);

		if (!dataRow.isDuDieuKien()) {
			c.setBackground(new Color(240, 240, 240));
			if (c.getForeground() != Color.RED) {
				c.setForeground(Color.GRAY);
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
	 * Phương thức này được gọi bởi DoiVeBuoc2Controller để đổ dữ liệu vào view.
	 */
	public void showDonDatCho(List<Ve> listVe, List<PhieuDungPhongVIP> listPhieu, KhachHang khachHang) {
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
		if (listVe != null && !listVe.isEmpty() && listPhieu != null && !listPhieu.isEmpty()) {
			List<VeDoiRow> rows = new ArrayList<>();
			int soLuongVe = listVe.size();
			for (int i = 0; i < soLuongVe; i++) {
				// Logic tính toán phí nằm trong constructor của VeDoiRow
				rows.add(new VeDoiRow(listVe.get(i), listPhieu.get(i)));
			}
			model.setRows(rows);
		} else {
			model.setRows(new ArrayList<>());
		}
	}

	public JButton getBtnTiepTuc() {
		return btnTiepTuc;
	}

	/**
	 * Trả về danh sách các Vé gốc (entity) đã được chọn để hoàn.
	 */
	public List<Ve> getSelectedVe() {
		List<VeDoiRow> selectedRows = model.getSelectedRows();
		List<Ve> selectedVe = new ArrayList<>();
		for (VeDoiRow row : selectedRows) {
			selectedVe.add(row.getVe());
		}
		return selectedVe;
	}

	/**
	 * Yêu cầu model thông báo cho JTable rằng một dòng đã thay đổi. JTable sẽ đọc
	 * lại dữ liệu từ model cho dòng đó và vẽ lại.
	 */
	public void refreshRow(VeDoiRow row) {
		int rowIndex = model.getRowIndex(row);
		if (rowIndex != -1) {
			// Chỉ cập nhật dòng thay đổi, hiệu quả hơn fireTableDataChanged()
			row.setSelected(false);
			model.fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public List<VeDoiRow> getSelectedVeDoiRows() {
		return model.getSelectedRows();
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}
}
