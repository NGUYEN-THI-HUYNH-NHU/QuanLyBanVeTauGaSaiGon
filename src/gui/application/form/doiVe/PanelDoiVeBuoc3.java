package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVeBuoc3.java  1.0  [5:27:38 PM] Nov 17, 2025
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
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;

public class PanelDoiVeBuoc3 extends JPanel {
	private VeDoiTableModel model;
	private JTable table;
	private JButton btnXacNhan;

	public PanelDoiVeBuoc3() {
		setLayout(new BorderLayout());
		setBorder(new LineBorder(new Color(220, 220, 220)));

		model = new VeDoiTableModel();
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
		String[] lyDoHoan = { "Không còn nhu cầu", "Thay đổi kế hoạch", "Lý do cá nhân", "Trùng vé", "Khác" };
		JComboBox<String> cbLyDoHoan = new JComboBox<>(lyDoHoan);
		DefaultCellEditor cellEditor = new DefaultCellEditor(cbLyDoHoan);
		table.getColumnModel().getColumn(VeDoiTableModel.COL_LY_DO).setCellEditor(cellEditor);

		table.setRowHeight(80);

		table.removeColumn(table.getColumnModel().getColumn(VeDoiTableModel.COL_THONG_TIN_PHI));

		// Cấu hình độ rộng cột (dùng chỉ số mới)
		table.getColumnModel().getColumn(0).setMinWidth(150);
		table.getColumnModel().getColumn(1).setMinWidth(180);
		table.getColumnModel().getColumn(6).setMaxWidth(50);

		// 2. RENDERER CHO CỘT THỜI GIAN
		DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(SwingConstants.CENTER);
				setVerticalAlignment(SwingConstants.CENTER);
				c.setForeground(Color.GREEN);
				setFont(getFont().deriveFont(Font.BOLD));
				return c;
			}
		};
		table.getColumnModel().getColumn(5).setCellRenderer(timeRenderer);

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		table.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);

		LeftCenterAlignRenderer leftCenterRenderer = new LeftCenterAlignRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(leftCenterRenderer);
	}

	public void displayConfirmation(List<VeDoiRow> selectedRows) {
		model.setRows(selectedRows);
	}

	public JButton getBtnXacNhan() {
		return btnXacNhan;
	}

	/**
	 * @param row
	 */
	public void removeRow(VeDoiRow row) {
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

	public void addRowSelectionListener(Consumer<VeDoiRow> listener) {
		if (model != null) {
			model.setRowSelectionListener(listener);
		}
	}

	public List<VeDoiRow> getVeDoiRows() {
		return model.getRows();
	}
}
