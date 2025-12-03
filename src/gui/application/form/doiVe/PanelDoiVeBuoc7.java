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
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import entity.KhuyenMai;
import gui.application.form.banVe.KhuyenMaiRenderer;
import gui.application.form.banVe.VeSession;
import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;

public class PanelDoiVeBuoc7 extends JPanel {
	private final MappingVeTableModel model;
	private final JTable table;

	protected interface KhuyenMaiProvider {
		List<KhuyenMai> getKhuyenMaiFor(VeSession veSession);
	}

	private KhuyenMaiProvider khuyenMaiProvider;
	private JComboBox cbKhuyenMai;

	private TableModelListener tableUpdateListener;

	public PanelDoiVeBuoc7() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Xác nhận thông tin vé"));
		setPreferredSize(new Dimension(getWidth(), 350));

		// 1. Khởi tạo model và table
		model = new MappingVeTableModel() {
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnIndex == COL_KHUYEN_MAI;
			}
		};
		table = new JTable(model);

		setUpTable();

		JScrollPane sp = new JScrollPane(table);
		add(sp, BorderLayout.CENTER);
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

		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setMaxWidth(30);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setMinWidth(150);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setMinWidth(150);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setMinWidth(130);

		// Cấu hình Cột Khuyến Mãi
		// 2. Cấu hình Cột Khuyến Mãi (Tách editor ra)
		TableColumn khuyenMaiCol = table.getColumnModel().getColumn(MappingVeTableModel.COL_KHUYEN_MAI);
		khuyenMaiCol.setMinWidth(140);

		cbKhuyenMai = new JComboBox<>();
		KhuyenMaiRenderer renderer = new KhuyenMaiRenderer();
		khuyenMaiCol.setCellRenderer(renderer);
		cbKhuyenMai.setRenderer(renderer);

		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		LeftCenterAlignRenderer leftCenterRenderer = new LeftCenterAlignRenderer();

		// Cột Tiền
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_PHIEU_VIP_GIA).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_LE_PHI).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_CHENH_LECH).setCellRenderer(currencyRenderer);

		// Cột Text thường (Tên, Thông tin vé)
		table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setCellRenderer(leftCenterRenderer);
	}

	public void setKhuyenMaiProvider(KhuyenMaiProvider provider) {
		this.khuyenMaiProvider = provider;

		// Khởi tạo Editor SAU KHI đã có provider (và model)
		TableColumn khuyenMaiCol = table.getColumnModel().getColumn(MappingVeTableModel.COL_KHUYEN_MAI);
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

	public MappingVeTableModel getModel() {
		return model;
	}

	public JTable getTable() {
		return table;
	}

}