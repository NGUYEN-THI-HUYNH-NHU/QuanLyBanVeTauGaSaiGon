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
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class HanhKhachTableModel extends AbstractTableModel {
	private final String[] cols = {"Hành khách", "Vé", "Giá", "Khuyến mãi", "Thành tiền"};
	private final List<PassengerRow> rows = new ArrayList<>();
	
	@Override
	public int getRowCount() { return rows.size(); }
	
	@Override
	public int getColumnCount() { return cols.length; }
	
	@Override
	public String getColumnName(int column) { return cols[column]; }
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
	    PassengerRow p = rows.get(rowIndex);
	    switch (columnIndex) {
	        case 0: return p; // renderer/editor will read PassengerRow
	        case 1: return p.getVeSession().prettyString();
	        case 2: return p.getPrice();
	        case 3: return p.getDiscount();
	        case 4: return p.getTotal();
	        default: return null;
	    }
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
	    return columnIndex == 0;
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
	    } else if (columnIndex == 2 && aValue instanceof Number) {
	        p.setPrice(((Number)aValue).doubleValue());
	        fireTableRowsUpdated(rowIndex, rowIndex);
	    } else if (columnIndex == 3 && aValue instanceof Number) {
	        p.setDiscount(((Number)aValue).doubleValue());
	        fireTableRowsUpdated(rowIndex, rowIndex);
	    }
	}
	
	public void setRows(List<PassengerRow> list) {
	    rows.clear();
	    if (list != null) rows.addAll(list);
	    fireTableDataChanged();
	}
	
	public void addRow(PassengerRow r) {
	    rows.add(r);
	    int idx = rows.size()-1;
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