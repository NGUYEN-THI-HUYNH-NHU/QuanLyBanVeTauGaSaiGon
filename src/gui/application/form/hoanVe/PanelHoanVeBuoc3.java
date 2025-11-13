package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc3.java  1.0  [2:58:00 PM] Nov 9, 2025
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
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class PanelHoanVeBuoc3 extends JPanel {
	private HoanVeTableModel model;
	private JTable table;
	private JButton btnXacNhan;
	// Renderer
	private static final DecimalFormat df = new DecimalFormat("#,##0đ");

	public PanelHoanVeBuoc3() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new HoanVeTableModel();
		table = new JTable(model);

		setupTable();

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(0, 300));
		add(sp, BorderLayout.CENTER);

		JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnXacNhan = new JButton("Xác nhận");
		south.add(btnXacNhan);

		add(south, BorderLayout.SOUTH);
	}

	private void setupTable() {
		table.setRowHeight(70);

		table.removeColumn(table.getColumnModel().getColumn(HoanVeTableModel.COL_CHON));
		table.removeColumn(table.getColumnModel().getColumn(HoanVeTableModel.COL_THONG_TIN_PHI));
		table.removeColumn(table.getColumnModel().getColumn(HoanVeTableModel.COL_LOAI_HOAN));

		// Cấu hình độ rộng cột (dùng chỉ số mới)
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(1).setMinWidth(150);

		// === Áp dụng Renderer ===
		// 1. Renderer cho tiền (căn phải, định dạng)
		DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer();
		currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
		currencyRenderer.setVerticalAlignment(SwingConstants.TOP);
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

		table.getColumnModel().getColumn(2).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(currencyFormatRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(currencyFormatRenderer);

		// 2. Renderer cho các cột text (căn trên)
		DefaultTableCellRenderer topAlignRenderer = new DefaultTableCellRenderer();
		topAlignRenderer.setVerticalAlignment(SwingConstants.TOP);

		table.getColumnModel().getColumn(0).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(topAlignRenderer);
	}

	public void displayConfirmation(List<VeHoanRow> selectedRows) {
		model.setRows(selectedRows);
	}

	public JButton getBtnXacNhan() {
		return btnXacNhan;
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}
}
