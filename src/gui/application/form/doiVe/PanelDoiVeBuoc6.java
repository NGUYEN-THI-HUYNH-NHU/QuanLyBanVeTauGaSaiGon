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
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import gui.application.form.banVe.VeSession;

public class PanelDoiVeBuoc6 extends JPanel {
	private JTable table;
	private MappingVeTableModel model;
	private VeMoiCellEditor veMoiEditor;
	private JButton btnConfirm;

	private Consumer<MappingRow> deleteListener;

	public PanelDoiVeBuoc6() {
		setLayout(new BorderLayout());
		model = new MappingVeTableModel();
		table = new JTable(model);

		table.setRowHeight(110);

		/// Áp dụng Renderer cho cột để hiển thị đẹp ngay cả khi không click vào
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHON_VE_MOI)
				.setCellRenderer(VeMoiRenderer.getTableCellRenderer());

		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		btnConfirm = new JButton("Xác nhận");
		pnlSouth.add(btnConfirm);
		add(pnlSouth, BorderLayout.SOUTH);
	}

	/**
	 * Hàm này được gọi từ Controller khi có danh sách vé mới
	 */
	public void updateNewTicketOptions(List<VeSession> veMoiList) {
		// Khởi tạo Editor với danh sách vé mới và tham chiếu tới Model bảng
		veMoiEditor = new VeMoiCellEditor(veMoiList, model);

		// Gán Editor cho cột
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

	public void setPassengerDeleteListener(Consumer<MappingRow> listener) {
		this.deleteListener = listener;
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	public void setController(DoiVeBuoc6Controller doiVeBuoc6Controller) {
		// TODO Auto-generated method stub

	}

	public JButton getConfirmButton() {
		return btnConfirm;
	}
}