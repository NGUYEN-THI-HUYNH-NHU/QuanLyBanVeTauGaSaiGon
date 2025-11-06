package gui.application.form.banVe;
/*
 * @(#) HanhKhachTableModel.java  1.0  [7:25:41 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class HanhKhachTableModel extends AbstractTableModel {
	private final String[] cols = { "Hành khách", "Vé", "Giá", "Giảm đối tượng", "Khuyến mãi", "Thành tiền", "" };
	private final List<PassengerRow> rows = new ArrayList<>();

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public String getColumnName(int column) {
		return cols[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PassengerRow p = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return p;
		case 1:
			return p.getVeSession().prettyString();
		case 2:
			return p.getVeSession().getGia();
		case 3:
			return p.getVeSession().getGiamDoiTuong();
		case 4:
			return p.getVeSession().getGiam();
		case 5:
			return p.getVeSession().getGia() - p.getVeSession().getGiam() - p.getVeSession().getGiamDoiTuong();
		case 6:
			return "Xóa";
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 6;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PassengerRow p = rows.get(rowIndex);
		if (columnIndex == 0 && aValue instanceof PassengerRow) {
			PassengerRow src = (PassengerRow) aValue;
			p.setFullName(src.getFullName());
			p.setType(src.getType());
			p.setIdNumber(src.getIdNumber());
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public void setRows(List<PassengerRow> list) {
		rows.clear();
		if (list != null) {
			rows.addAll(list);
		}
		fireTableDataChanged();
	}

	public void addRow(PassengerRow r) {
		rows.add(r);
		int idx = rows.size() - 1;
		fireTableRowsInserted(idx, idx);
	}

	public PassengerRow getRowAt(int idx) {
		return rows.get(idx);
	}

	public List<PassengerRow> getRowsCopy() {
		return new ArrayList<>(rows);
	}

	public void clear() {
		rows.clear();
		fireTableDataChanged();
	}
}