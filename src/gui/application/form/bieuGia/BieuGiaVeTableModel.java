package gui.application.form.bieuGia;
/*
 * @(#) BieuGiaVeTableModel.java  1.0  [9:27:58 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */
import javax.swing.table.AbstractTableModel;

import entity.BieuGiaVe;

public class BieuGiaVeTableModel extends AbstractTableModel {
	public static final int COL_ID = 0;
	public static final int COL_UU_TIEN = 1;
	public static final int COL_TUYEN = 2;
	public static final int COL_TAU = 3;
	public static final int COL_TOA = 4;
	public static final int COL_KHOANG_CACH = 5;
	public static final int COL_HIEU_LUC = 6;
	public static final int COL_GIA = 7;
	public static final int COL_XEM = 8;
	public static final int COL_SUA = 9;

	private final String[] columnNames = { "Mã Biểu Giá", "Ưu tiên", "Tuyến", "Tàu", "Toa", "Khoảng cách", "Hiệu lực", "Giá",
			"Xem", "Sửa" };

	private List<BieuGiaVe> rows;

	public BieuGiaVeTableModel() {
		this.rows = new ArrayList<>();
	}

	public void setRows(List<BieuGiaVe> rows) {
		this.rows = rows;
		fireTableDataChanged();
	}

	public BieuGiaVe getRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			return rows.get(rowIndex);
		}
		return null;
	}

	public void setRowCount(int rowCount) {
		if (rowCount == 0) {
			rows.clear();
			fireTableDataChanged();
		}
	}

	public void addRow(BieuGiaVe bg) {
		rows.add(bg);
		fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
	}

	public void removeRow(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			rows.remove(rowIndex);
			fireTableRowsDeleted(rowIndex, rowIndex);
		}
	}

	private String formatVND(double value){
		NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
		return nf.format(value) + " VND";
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
	public Object getValueAt(int rowIndex, int columnIndex) {
		BieuGiaVe bg = rows.get(rowIndex);
		switch (columnIndex) {
		case COL_ID:
			return bg.getBieuGiaVeID();
		case COL_UU_TIEN:
			return bg.getDoUuTien();
		case COL_TUYEN:
			return (bg.getTuyenApDung() == null ? "Tất cả" : bg.getTuyenApDung().getTuyenID());
		case COL_TAU:
			return (bg.getLoaiTauApDung().getDescription() == null ? "Tất cả" : bg.getLoaiTauApDung().getDescription());
		case COL_TOA:
			return (bg.getHangToaApDung().getDescription() == null ? "Tất cả" : bg.getHangToaApDung().getDescription());
		case COL_KHOANG_CACH:
			return bg.getMinKm() + " - " + bg.getMaxKm() + " km";
		case COL_HIEU_LUC:
			String start = (bg.getNgayBatDau() != null) ? bg.getNgayBatDau().toString() : "?";
			String end = (bg.getNgayKetThuc() != null) ? bg.getNgayKetThuc().toString() : "∞";
			return start + " -> " + end;
		case COL_GIA:
			if(bg.getGiaCoBan() > 0){
				return "Cố định: " + formatVND(bg.getGiaCoBan());
			}else{
				return formatVND(bg.getDonGiaTrenKm()) + " / Km";
			}
		case COL_XEM:
			return "Xem";
		case COL_SUA:
			return "Sửa";
		default:
			return null;
		}
	}
}