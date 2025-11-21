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
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import gui.application.form.banVe.VeSession;

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

		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getColumnModel().getColumn(1).setMinWidth(150);
		table.getColumnModel().getColumn(2).setMinWidth(150);
		table.getColumnModel().getColumn(5).setMinWidth(150);
		table.getColumnModel().getColumn(6).setMinWidth(150);

		/// Áp dụng Renderer cho cột để hiển thị đẹp ngay cả khi không click vào
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI)
				.setCellRenderer(VeMoiRenderer.getTableCellRenderer());

		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		pnlSouth.add(btnCancel = new JButton("Quay lại"));
		pnlSouth.add(btnConfirm = new JButton("Xác nhận"));
		add(pnlSouth, BorderLayout.SOUTH);
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