package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc4.java  1.0  [2:15:18 PM] Nov 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 14, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class PanelHoanVeBuoc4 extends JPanel {
	private VeHoanTableModel model;
	private JTable table;
	// Renderer
	private static final DecimalFormat df = new DecimalFormat("#,##0đ");

	public PanelHoanVeBuoc4() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new VeHoanTableModel();
		table = new JTable(model);

		setupTable();

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(0, 350));
		add(sp, BorderLayout.CENTER);
	}

	private void setupTable() {
		table.setRowHeight(80);

		table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_PHI));

		// Cấu hình độ rộng cột
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(1).setMinWidth(150);
		table.getColumnModel().getColumn(7).setMaxWidth(50);

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
		table.getColumnModel().getColumn(5).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(topAlignRenderer);
	}

	/**
	 * @param row
	 */
	public void removeRow(VeHoanRow row) {
		int rowIndex = model.getRowIndex(row);
		if (rowIndex != -1) {
			// Chỉ cập nhật dòng thay đổi, hiệu quả hơn fireTableDataChanged()
			row.setSelected(false);
			model.removeRow(rowIndex);
		}
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	public void addRowSelectionListener(Consumer<VeHoanRow> listener) {
		if (model != null) {
			model.setRowSelectionListener(listener);
		}
	}

	/**
	 * @param listVeHoanRow
	 */
	public void hienThiThongTin(List<VeHoanRow> listVeHoanRow) {
		model.setRows(listVeHoanRow);
	}
}
