package gui.application.form.doiVe;
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import gui.application.form.banVe.VeSession;
import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;

public class PanelDoiVeBuoc6 extends JPanel {
	private JTable table;
	private MappingVeTableModel model;
	private VeMoiCellEditor veMoiEditor;
	private final JButton btnCancel;
	private JButton btnConfirm;

	private DoiVeBuoc6Controller controller;

	public PanelDoiVeBuoc6() {
		setLayout(new BorderLayout());
		model = new MappingVeTableModel();
		table = new JTable(model);

		setUpTable();

		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		pnlSouth.add(btnCancel = new JButton("Quay lại"));
		pnlSouth.add(btnConfirm = new JButton("Xác nhận"));
		add(pnlSouth, BorderLayout.SOUTH);
	}

	private void setUpTable() {
		table.setRowHeight(110);

//		public static final int COL_STT = 0;
//		public static final int COL_HANH_KHACH = 1;
//		public static final int COL_VE_CU_INFO = 2;
//		public static final int COL_VE_CU_GIA = 3;
//		public static final int COL_CHON_VE_MOI = 4;
//		public static final int COL_VE_MOI_INFO = 5;
//		public static final int COL_VE_MOI_GIA = 6;
//		public static final int COL_KHUYEN_MAI = 7;
//		public static final int COL_GIAM_KM = 8;
//		public static final int COL_CHON_PHIEU_VIP = 9;
//		public static final int COL_PHIEU_VIP_GIA = 10;
//		public static final int COL_LE_PHI = 11;
//		public static final int COL_CHENH_LECH = 12;

		table.removeColumn(table.getColumnModel().getColumn(MappingVeTableModel.COL_KHUYEN_MAI));
		table.removeColumn(table.getColumnModel().getColumn(MappingVeTableModel.COL_GIAM_KM - 1));

		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setMaxWidth(30);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setMinWidth(100);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setMinWidth(150);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI).setMinWidth(100);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setMinWidth(130);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_PHIEU_VIP - 2).setMaxWidth(50);

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		LeftCenterAlignRenderer topAlignRenderer = new LeftCenterAlignRenderer();

		// Cột Tiền
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_PHIEU_VIP_GIA - 2).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHENH_LECH - 2).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_LE_PHI - 2).setCellRenderer(currencyRenderer);

		// Cột Text thường (Tên, Thông tin vé)
		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setCellRenderer(topAlignRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setCellRenderer(topAlignRenderer);

		/// Áp dụng Renderer cho cột để hiển thị đẹp ngay cả khi không click vào
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI)
				.setCellRenderer(VeMoiRenderer.getTableCellRenderer());
	}

	/**
	 * Hàm này được gọi từ Controller khi có danh sách vé mới
	 */
	public void updateNewTicketOptions(List<VeSession> veMoiList) {
		veMoiEditor = new VeMoiCellEditor(veMoiList);
		// Gán Editor
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI).setCellEditor(veMoiEditor);
	}

	public void initFromBookingSession(ExchangeSession session) {
		model.clear();
		if (session == null) {
			return;
		}

		List<VeDoiRow> listVeDoi = session.getListVeCuCanDoi();
		List<VeSession> listVeMoi = session.getListVeMoiDangChon();

		// Cập nhật lại Editor với danh sách mới nhất từ Session (quan trọng)
		if (veMoiEditor != null) {
			veMoiEditor.setSourceList(listVeMoi);
		} else {
			// Fallback nếu updateNewTicketOptions chưa được gọi trước đó
			updateNewTicketOptions(listVeMoi);
		}

		List<MappingRow> rows = new ArrayList<>();
		for (int i = 0; i < listVeDoi.size(); i++) {
			VeSession vm = (i < listVeMoi.size()) ? listVeMoi.get(i) : null;
			MappingRow r = new MappingRow(listVeDoi.get(i), vm);
			rows.add(r);
		}
		model.setRows(rows);
	}

	/**
	 * Chọn dòng bị lỗi, cuộn tới đó và kích hoạt ComboBox
	 */
	public void highlightAndFocusError(int rowIndex) {
		// 1. Chọn dòng
		table.setRowSelectionInterval(rowIndex, rowIndex);

		// 2. Cuộn bảng tới vị trí dòng đó (nếu đang bị khuất)
		table.scrollRectToVisible(table.getCellRect(rowIndex, 0, true));

		// 3. Focus và kích hoạt Editor (ComboBox) tại cột "Chọn vé mới"
		int colIndex = MappingVeTableModel.COL_CHON_VE_MOI;
		table.editCellAt(rowIndex, colIndex);

		Component editor = table.getEditorComponent();
		if (editor != null) {
			editor.requestFocusInWindow();
		}
	}

	public List<MappingRow> getMappingRows() {
		if (table.isEditing()) {
			table.getCellEditor().stopCellEditing();
		}
		return model.getRowsCopy();
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	public JButton getConfirmButton() {
		return btnConfirm;
	}

	public JButton getCancelButton() {
		return btnCancel;
	}

	public DoiVeBuoc6Controller getController() {
		return controller;
	}

	public void setController(DoiVeBuoc6Controller controller) {
		this.controller = controller;
	}
}