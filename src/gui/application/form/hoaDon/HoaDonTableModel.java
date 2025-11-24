package gui.application.form.hoaDon;
/*
 * @(#) HoaDonTableModel.java  1.0  [2:30:56 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.table.AbstractTableModel;

import entity.HoaDon;

public class HoaDonTableModel extends AbstractTableModel {
	public static final int COL_STT = 0;
	public static final int COL_HOA_DON_ID = 1;
	public static final int COL_KHACH_HANG_ID = 2;
	public static final int COL_TEN_KHACH_HANG = 3;
	public static final int COL_CCCD_KHACH_HANG = 4;
	public static final int COL_THOI_DIEM_TAO = 5;
	public static final int COL_TONG_TIEN = 6;
	public static final int COL_TIEN_NHAN = 7;
	public static final int COL_TIEN_HOAN = 8;
	public static final int COL_IS_TIEN_MAT = 9;
	public static final int COL_MA_GD = 10;
	public static final int COL_XEM = 11;
	public static final int COL_IN = 12;

	private final String[] columnNames = { "STT", "Hóa đơn ID", "KH ID", "Tên khách hàng", "CCCD KH", "Thời điểm tạo",
			"Tổng tiền", "Tiền nhận", "Tiền hoàn", "Tiền mặt", "Mã GDTT", "Xem", "In" };

	private List<HoaDon> rows;

	private Consumer<HoaDon> rowSelectionListener;

	public HoaDonTableModel() {
		this.rows = new ArrayList<>();
	}

	public void setRows(List<HoaDon> rows) {
		this.rows = new ArrayList<>(rows);
		fireTableDataChanged(); // Thông báo cho JTable cập nhật
	}

	public List<HoaDon> getRows() {
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

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_IS_TIEN_MAT) {
			return Boolean.class;
		}
		if (columnIndex == COL_TONG_TIEN || columnIndex == COL_TIEN_NHAN || columnIndex == COL_TIEN_HOAN) {
			return Double.class;
		}
		if (columnIndex == COL_THOI_DIEM_TAO) {
			return LocalDateTime.class;
		}
		return Object.class; // Để render button hoặc string
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		HoaDon row = rows.get(rowIndex);
		switch (columnIndex) {

		case COL_STT:
			return rowIndex + 1;
		case COL_HOA_DON_ID:
			return row.getHoaDonID();
		case COL_KHACH_HANG_ID:
			return row.getKhachHang().getKhachHangID();
		case COL_TEN_KHACH_HANG:
			return row.getKhachHang().getHoTen();
		case COL_CCCD_KHACH_HANG:
			return row.getKhachHang().getSoGiayTo();
		case COL_THOI_DIEM_TAO:
			return row.getThoiDiemTao();
		case COL_TONG_TIEN:
			return row.getTongTien();
		case COL_TIEN_NHAN:
			return row.getTienNhan();
		case COL_TIEN_HOAN:
			return row.getTienHoan();
		case COL_IS_TIEN_MAT:
			return row.isThanhToanTienMat();
		case COL_MA_GD:
			return row.getMaGD();
		case COL_XEM:
			return ""; // Trả về text để renderer vẽ thành nút
		case COL_IN:
			return "";
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		HoaDon row = rows.get(rowIndex);

		// Phát sự kiện để HoaDonController bắt
		if (rowSelectionListener != null) {
			rowSelectionListener.accept(row);
		}
	}

	public void setRowSelectionListener(Consumer<HoaDon> listener) {
		this.rowSelectionListener = listener;
	}

	public int getRowIndex(HoaDon rowToFind) {
		return rows.indexOf(rowToFind);
	}

	public HoaDon getRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			return rows.get(rowIndex);
		}
		return null;
	}

	public void removeRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			rows.remove(rowIndex);
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}
}
