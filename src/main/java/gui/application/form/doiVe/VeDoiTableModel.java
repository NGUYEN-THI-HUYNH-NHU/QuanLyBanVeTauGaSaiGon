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

import dto.VeDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VeDoiTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_TEN = 1;
    public static final int COL_THONG_TIN_VE_DOI = 2;
    public static final int COL_THANH_TIEN = 3;
    public static final int COL_DU_DK = 4;
    public static final int COL_LE_PHI = 5;
    public static final int COL_THONG_TIN_PHI = 6;
    public static final int COL_LY_DO = 7;
    public static final int COL_TG_CON_LAI = 8;
    public static final int COL_CHON = 9;
    private final String[] columnNames = {"STT", "Hành khách", "Thông tin vé đổi", "Thành tiền", "Đủ điều kiện",
            "Lệ phí", "Thông tin phí", "Lý do đổi", "TG còn lại", "Chọn"};
    private List<VeDoiRow> rows;

    private Consumer<VeDoiRow> rowSelectionListener;

    public VeDoiTableModel() {
        this.rows = new ArrayList<>();
    }

    public List<VeDoiRow> getRows() {
        return rows;
    }

    public void setRows(List<VeDoiRow> rows) {
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

    // Báo cho JTable biết cột nào là Checkbox
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_CHON) {
            return Boolean.class;
        }
        if (columnIndex == COL_THANH_TIEN || columnIndex == COL_LE_PHI) {
            return Double.class;
        }
        return String.class;
    }

    // Chỉ cho phép sửa cột Checkbox
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == COL_CHON || columnIndex == COL_LY_DO) {
            return rows.get(rowIndex).isDuDieuKien();
        }

        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VeDoiRow row = rows.get(rowIndex);
        switch (columnIndex) {
            case COL_STT:
                return rowIndex + 1;
            case COL_TEN:
                return row.getHanhKhach();
            case COL_THONG_TIN_VE_DOI:
                VeDTO ve = row.getVe();
                if (row.getPhieuDungPhongVIP() == null) {
                    return String.format("<html>%s %s<br/>Toa: %s; Chỗ: %s<br/>Mã vé: %s</html>",
                            ve.getTauID(), ve.getNgayGioDi(), ve.getSoToa(), ve.getSoGhe(), ve.getVeID());
                }
                return String.format("<html>%s %s<br/>Toa: %s; Chỗ: %s<br/>Vé: %s<br/>Phiếu: %s</html>",
                        ve.getTauID(), ve.getNgayGioDi(), ve.getSoToa(), ve.getSoGhe(), ve.getVeID(),
                        row.getPhieuDungPhongVIP().getId());
            case COL_THANH_TIEN:
                return row.getVe().getGia();
            case COL_DU_DK:
                return row.getDieuKien();
            case COL_LE_PHI:
                return row.getLePhiDoiVe();
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

    // Cập nhật model khi người dùng tick vào checkbox
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