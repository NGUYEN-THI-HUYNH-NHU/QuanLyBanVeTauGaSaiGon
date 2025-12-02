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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import entity.KhuyenMai;
import gui.tuyChinh.CurrencyRenderer;

public class PanelBuoc4 extends JPanel {
	private final HanhKhachTableModel model;
	private final JTable table;

	public interface KhuyenMaiProvider {
		List<KhuyenMai> getKhuyenMaiFor(VeSession veSession);
	}

	private KhuyenMaiProvider khuyenMaiProvider;
	private JComboBox cbKhuyenMai;

	private TableModelListener tableUpdateListener;

	public PanelBuoc4() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Xác nhận thông tin vé"));
		setPreferredSize(new Dimension(getWidth(), 350));

		// 1. Khởi tạo model và table
		model = new HanhKhachTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == COL_KHUYEN_MAI;
			}
		};
		table = new JTable(model);
		table.setRowHeight(110);
		table.getColumnModel().getColumn(0).setMaxWidth(36);
		table.getColumnModel().getColumn(1).setPreferredWidth(180);
		table.getColumnModel().getColumn(2).setPreferredWidth(120);
		table.removeColumn(table.getColumnModel().getColumn(10));

		// Cấu hình Cột Khuyến Mãi
		// 2. Cấu hình Cột Khuyến Mãi (Tách editor ra)
		TableColumn khuyenMaiCol = table.getColumnModel().getColumn(7);
		khuyenMaiCol.setMinWidth(150);

		cbKhuyenMai = new JComboBox<>();
		KhuyenMaiRenderer renderer = new KhuyenMaiRenderer();
		khuyenMaiCol.setCellRenderer(renderer);
		cbKhuyenMai.setRenderer(renderer);

		table.getColumnModel().getColumn(1).setCellRenderer(new PassengerCellRenderer());

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		table.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(9).setCellRenderer(currencyRenderer);

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);
	}

	public void setKhuyenMaiProvider(KhuyenMaiProvider provider) {
		this.khuyenMaiProvider = provider;

		// Khởi tạo Editor SAU KHI đã có provider (và model)
		TableColumn khuyenMaiCol = table.getColumnModel().getColumn(7);
		// Dùng class Editor mới tách
		KhuyenMaiCellEditor editor = new KhuyenMaiCellEditor(cbKhuyenMai, provider, model);
		khuyenMaiCol.setCellEditor(editor);
	}

	// Thêm hàm để Controller đăng ký lắng nghe thay đổi
	public void addTableUpdateListener(TableModelListener l) {
		this.tableUpdateListener = l;
		model.addTableModelListener(l);
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
			if (v.getVe().getKhachHang() != null) {
				r.setHoTen(v.getVe().getKhachHang().getHoTen());
				r.setSoGiayTo(v.getVe().getKhachHang().getSoGiayTo());
				r.setLoaiDoiTuong(v.getVe().getKhachHang().getLoaiDoiTuong());
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