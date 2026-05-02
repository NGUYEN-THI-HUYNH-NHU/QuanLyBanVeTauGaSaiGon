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

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VeHoanTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_TEN = 1;
    public static final int COL_THONG_TIN_VE = 2;
    public static final int COL_THANH_TIEN = 3;
    public static final int COL_LE_PHI = 4;
    public static final int COL_TIEN_HOAN = 5;
    public static final int COL_THONG_TIN_PHI = 6;
    public static final int COL_LY_DO = 7;
    public static final int COL_TG_CON_LAI = 8;
    public static final int COL_CHON = 9;
    private final String[] columnNames = {"STT", "Hành khách", "Thông tin vé", "Thành tiền", "Lệ phí", "Tiền hoàn",
            "Thông tin phí", "Lý do hoàn", "TG còn lại", "Chọn"};
    private List<VeHoanRow> rows;

    private Consumer<VeHoanRow> rowSelectionListener;

    public VeHoanTableModel() {
        this.rows = new ArrayList<>();
    }

    public List<VeHoanRow> getRows() {
        return rows;
    }

    public void setRows(List<VeHoanRow> rows) {
        this.rows = new ArrayList<>(rows);
        fireTableDataChanged(); // Thông báo cho JTable cập nhật
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
        if (columnIndex == COL_STT) {
            return Integer.class;
        }
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
        if (columnIndex == COL_CHON || columnIndex == COL_LY_DO) {
            return rows.get(rowIndex).isDuDieuKien();
        }

        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VeHoanRow row = rows.get(rowIndex);
        switch (columnIndex) {
            case COL_STT:
                return rowIndex + 1;
            case COL_TEN:
                return row.getHanhKhach();
            case COL_THONG_TIN_VE:
                return row.getThongTinVe();
            case COL_THANH_TIEN:
                return row.getThanhTien();
            case COL_LE_PHI:
                return row.getLePhiHoanVe();
            case COL_TIEN_HOAN:
                return row.getTienHoan();
            case COL_THONG_TIN_PHI:
                return row.getThongTinPhiHoan();
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
            VeHoanRow row = rows.get(rowIndex);
            row.setSelected((Boolean) aValue);
            fireTableCellUpdated(rowIndex, columnIndex); // Thông báo cell này thay đổi

            // Phát sự kiện để HoanVeBuoc3Controller bắt
            if (rowSelectionListener != null) {
                rowSelectionListener.accept(row);
            }
        } else if (columnIndex == COL_LY_DO) {
            VeHoanRow row = rows.get(rowIndex);
            row.setLyDo((String) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
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

    public void setRowSelectionListener(Consumer<VeHoanRow> listener) {
        this.rowSelectionListener = listener;
    }

    public int getRowIndex(VeHoanRow rowToFind) {
        return rows.indexOf(rowToFind);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            rows.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }
}