package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVeBuoc7.java  1.0  [11:18:55 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import gui.application.form.banVe.PassengerCellRenderer;

public class PanelDoiVeBuoc7 extends JPanel {
	private final MappingVeTableModel model;
	private final JTable table;

	public PanelDoiVeBuoc7() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Xác nhận thông tin vé"));
		setPreferredSize(new Dimension(getWidth(), 350));

		// 1. Khởi tạo model và table
		model = new MappingVeTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		};
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMinWidth(180);
		table.getColumnModel().getColumn(1).setMinWidth(180);
		table.removeColumn(table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI));

		// 2. CHỈ DÙNG Renderer (để hiển thị), KHÔNG DÙNG Editor (để không thể sửa)
		table.getColumnModel().getColumn(0).setCellRenderer(new PassengerCellRenderer());

		// Căn giữa các cột số
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);
	}

	/**
	 * Được gọi bởi DoiVe3Controller để đổ dữ liệu từ session vào bảng.
	 */
	public void hienThiThongTin(ExchangeSession session) {

	}

	/**
	 * Được gọi bởi controller để bật/tắt toàn bộ panel.
	 */
	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);
		table.setEnabled(enabled);
		for (Component comp : getComponents()) {
			comp.setEnabled(enabled);
		}
	}
}