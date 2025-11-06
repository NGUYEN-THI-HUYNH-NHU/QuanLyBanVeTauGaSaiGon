package gui.application.form.banVe;
/*
 * @(#) PanelBuoc4.java  1.0  [1:38:19 PM] Sep 28, 2025
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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class PanelBuoc4 extends JPanel {
	private final HanhKhachTableModel model;
	private final JTable table;

	public PanelBuoc4() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Xác nhận thông tin vé"));
		setPreferredSize(new Dimension(getWidth(), 350));

		// 1. Khởi tạo model và table
		model = new HanhKhachTableModel();
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMinWidth(250);
		table.removeColumn(table.getColumnModel().getColumn(6));

		// 2. CHỈ DÙNG Renderer (để hiển thị), KHÔNG DÙNG Editor (để không thể sửa)
		table.getColumnModel().getColumn(0).setCellRenderer(new PassengerCellRenderer());

		// Căn giữa các cột số
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);
	}

	/**
	 * Được gọi bởi BanVe2Controller để đổ dữ liệu từ session vào bảng.
	 */
	public void hienThiThongTin(BookingSession session) {
		model.clear();
		if (session == null) {
			return;
		}

		// Lấy TẤT CẢ vé (chiều đi + chiều về nếu có)
		List<VeSession> allTickets = new ArrayList<>(session.getOutboundSelectedTickets());
		if (session.isRoundTrip()) {
			allTickets.addAll(session.getReturnSelectedTickets());
		}

		if (allTickets.isEmpty()) {
			return;
		}

		// Chuyển VeSession (đã có HanhKhach) thành PassengerRow
		List<PassengerRow> rows = new ArrayList<>();
		for (VeSession v : allTickets) {
			// Giả sử HanhKhach đã được gán vào VeSession ở Buoc3
			PassengerRow r = new PassengerRow(v);
			if (v.getHanhKhach() != null) {
				r.setFullName(v.getHanhKhach().getHoTen());
				r.setIdNumber(v.getHanhKhach().getSoGiayTo());
				r.setType(v.getHanhKhach().getLoaiDoiTuong());
			}
			rows.add(r);
		}
		model.setRows(rows);
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