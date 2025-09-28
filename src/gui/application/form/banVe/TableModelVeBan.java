package gui.application.form.banVe;
/*
 * @(#) TableModelVeBan.java  1.0  [10:12:02 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import entity.Ve;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class TableModelVeBan extends AbstractTableModel {
    private final String[] cols = new String[]{"Mã vé", "Khách hàng", "Trạng thái", "Ngày mua", "Giá"};
    private List<Ve> data = new ArrayList<>();

    public TableModelVeBan(List<Ve> data) {
        this.data = data;
    }

    public void setData(List<Ve> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Ve getTicketAt(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
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
        Ve t = data.get(rowIndex);
        return switch (columnIndex) {
//            case 0 -> t.getVeID();
//            case 1 -> t.getHanhKhach().getHoTen();
//            case 2 -> t.getTrangThai();
//            case 3 -> t.getNgayBan();
//            case 4 -> t.getGia();
            case 0 -> "aaa";
            case 1 -> "bbb";
            case 2 -> "ccc";
            case 3 -> "ddd";
            case 4 -> "eee";
            default -> "fff";
        };
    }
}