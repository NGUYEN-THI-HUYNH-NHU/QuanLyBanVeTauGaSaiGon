package gui.application.form.doiVe;
/*
 * @(#) VeDoiTableModel.java  1.0  [11:26:23 AM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.table.AbstractTableModel;

public class VeDoiTableModel extends AbstractTableModel {
	private final String[] columnNames = { "Hành khách", "Thông tin vé đổi", "Thành tiền", "Lệ phí", "Thông tin vé mới",
			"Tiền vé mới", "Chênh lệch", "Thông tin phí", "Lý do đổi", "TG còn lại", "Chọn" };

	public static final int COL_TEN = 0;
	public static final int COL_THONG_TIN_VE_DOI = 1;
	public static final int COL_THANH_TIEN = 2;
	public static final int COL_LE_PHI = 3;
	public static final int COL_THONG_TIN_VE_MOI = 4;
	public static final int COL_TIEN_VE_MOI = 5;
	public static final int COL_CHENH_LECH = 6;
	public static final int COL_THONG_TIN_PHI = 7;
	public static final int COL_LY_DO = 8;
	public static final int COL_TG_CON_LAI = 9;
	public static final int COL_CHON = 10;

	private List<VeDoiRow> rows;

	private Consumer<VeDoiRow> rowSelectionListener;

	public VeDoiTableModel() {
		this.rows = new ArrayList<>();
	}

	public void setRows(List<VeDoiRow> rows) {
		this.rows = new ArrayList<>(rows);
		fireTableDataChanged(); // Thông báo cho JTable cập nhật
	}

	public List<VeDoiRow> getRows() {
		return rows;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	// Quan trọng: Báo cho JTable biết cột nào là Checkbox
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_CHON) {
			return Boolean.class;
		}
		if (columnIndex == COL_THANH_TIEN || columnIndex == COL_LE_PHI || columnIndex == COL_TIEN_VE_MOI
				|| columnIndex == COL_CHENH_LECH) {
			return Double.class;
		}
		return String.class;
	}

	// Quan trọng: Chỉ cho phép sửa cột Checkbox
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == COL_CHON || columnIndex == COL_LY_DO;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		VeDoiRow row = rows.get(rowIndex);
		switch (columnIndex) {
		case COL_TEN:
			return row.getHanhKhach();
		case COL_THONG_TIN_VE_DOI:
			return row.getVeDoi().thongTinVeDoi();
		case COL_THANH_TIEN:
			return row.getVeDoi().getGia();
		case COL_LE_PHI:
			return row.getLePhiDoiVe();
		case COL_THONG_TIN_VE_MOI:
			return row.getVeMoi().thongTinVeDoi();
		case COL_TIEN_VE_MOI:
			return row.getVeMoi().getGia();
		case COL_CHENH_LECH:
			return row.getVeMoi().getGia() + row.getLePhiDoiVe() - row.getVeDoi().getGia();
		case COL_THONG_TIN_PHI:
			return row.getThongTinPhiDoi();
		case COL_LY_DO:
			return row.getLyDo();
		case COL_TG_CON_LAI:
			return row.getThoiGianConLai();
		case COL_CHON:
			return row.isSelected();
		default:
			return null;
		}
	}

	// Quan trọng: Cập nhật model khi người dùng tick vào checkbox
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == COL_CHON) {
			VeDoiRow row = rows.get(rowIndex);
			row.setSelected((Boolean) aValue);
			fireTableCellUpdated(rowIndex, columnIndex); // Thông báo cell này thay đổi

			// Phát sự kiện để DoiVeBuoc3Controller bắt
			if (rowSelectionListener != null) {
				rowSelectionListener.accept(row);
			}
		} else if (columnIndex == COL_LY_DO) {
			VeDoiRow row = rows.get(rowIndex);
			row.setLyDo((String) aValue);
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * Helper để lấy các dòng đã được chọn
	 */
	public List<VeDoiRow> getSelectedRows() {
		List<VeDoiRow> selected = new ArrayList<>();
		for (VeDoiRow row : rows) {
			if (row.isSelected()) {
				selected.add(row);
			}
		}
		return selected;
	}

	public void setRowSelectionListener(Consumer<VeDoiRow> listener) {
		this.rowSelectionListener = listener;
	}

	public int getRowIndex(VeDoiRow rowToFind) {
		return rows.indexOf(rowToFind);
	}

	public void removeRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			rows.remove(rowIndex);
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}
}