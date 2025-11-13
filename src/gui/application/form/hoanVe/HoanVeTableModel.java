package gui.application.form.hoanVe;
/*
 * @(#) HoanVeTableModel.java  1.0  [11:26:23 AM] Nov 13, 2025
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

import javax.swing.table.AbstractTableModel;

public class HoanVeTableModel extends AbstractTableModel {
	private final String[] columnNames = { "Hành khách", "Thông tin vé", "Thành tiền", "Loại hoàn vé", "Lệ phí",
			"Tiền hoàn", "Thông tin phí", "Chọn" };

	// (Column indices)
	public static final int COL_TEN = 0;
	public static final int COL_THONG_TIN_VE = 1;
	public static final int COL_THANH_TIEN = 2;
	public static final int COL_LOAI_HOAN = 3;
	public static final int COL_LE_PHI = 4;
	public static final int COL_TIEN_HOAN = 5;
	public static final int COL_THONG_TIN_PHI = 6;
	public static final int COL_CHON = 7;

	private List<VeHoanRow> rows;

	public HoanVeTableModel() {
		this.rows = new ArrayList<>();
	}

	public void setRows(List<VeHoanRow> rows) {
		this.rows = new ArrayList<>(rows);
		fireTableDataChanged(); // Thông báo cho JTable cập nhật
	}

	public List<VeHoanRow> getRows() {
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
		if (columnIndex == COL_THANH_TIEN || columnIndex == COL_LE_PHI || columnIndex == COL_TIEN_HOAN) {
			return Double.class;
		}
		return String.class;
	}

	// Quan trọng: Chỉ cho phép sửa cột Checkbox
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == COL_CHON;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		VeHoanRow row = rows.get(rowIndex);
		switch (columnIndex) {
		case COL_TEN:
			return row.getHanhKhach();
		case COL_THONG_TIN_VE:
			return row.getThongTinVe();
		case COL_THANH_TIEN:
			return row.getThanhTien();
		case COL_LOAI_HOAN:
			return row.getLoaiHoanVe();
		case COL_LE_PHI:
			return row.getLePhiHoanVe();
		case COL_TIEN_HOAN:
			return row.getTienHoanLai();
		case COL_THONG_TIN_PHI:
			return row.getThongTinPhiHoan();
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
			VeHoanRow row = rows.get(rowIndex);
			row.setSelected((Boolean) aValue);
			fireTableCellUpdated(rowIndex, columnIndex); // Thông báo cell này thay đổi

			// Có thể phát một sự kiện ở đây để Controller lắng nghe
			// (ví dụ: tính lại tổng tiền hoàn)
		}
	}

	/**
	 * Helper để lấy các dòng đã được chọn
	 */
	public List<VeHoanRow> getSelectedRows() {
		List<VeHoanRow> selected = new ArrayList<>();
		for (VeHoanRow row : rows) {
			if (row.isSelected()) {
				selected.add(row);
			}
		}
		return selected;
	}
}