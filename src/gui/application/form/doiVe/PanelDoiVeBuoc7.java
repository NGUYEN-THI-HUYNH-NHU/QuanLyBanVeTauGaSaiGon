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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import gui.application.form.banVe.VeSession;
import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.TopAlignRenderer;

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

		setUpTable();

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);
	}

	private void setUpTable() {
		table.setRowHeight(90);

		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setMaxWidth(30);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setMinWidth(150);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setMinWidth(150);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setMinWidth(150);
		table.removeColumn(table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI));

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		TopAlignRenderer topAlignRenderer = new TopAlignRenderer();

		// Cột Tiền
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_GIA - 1).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_LE_PHI - 1).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHENH_LECH - 1).setCellRenderer(currencyRenderer);

		// Cột Text thường (Tên, Thông tin vé)
		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO - 1).setCellRenderer(topAlignRenderer);
	}

	/**
	 * Được gọi bởi DoiVe3Controller để đổ dữ liệu từ session vào bảng.
	 */
	public void hienThiThongTin(ExchangeSession session) {
		if (session == null) {
			model.setData(null, null);
			return;
		}

		List<VeDoiRow> listVeDoi = session.getListVeCuCanDoi();
		List<VeSession> listVeMoi = session.getListVeMoiDangChon();

		model.setData(listVeDoi, listVeMoi);
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