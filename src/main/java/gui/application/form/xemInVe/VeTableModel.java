package gui.application.form.xemInVe;
/*
 * @(#) VeTableModel.java  1.0  [7:12:00 PM] Dec 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dto.VeDTO;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VeTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_VE_ID = 1;
    public static final int COL_TEN_KHACH_HANG = 2;
    public static final int COL_CCCD_KHACH_HANG = 3;
    public static final int COL_DDC_ID = 4;
    public static final int COL_CHUYEN = 5;
    public static final int COL_GHE = 6;
    public static final int COL_GIA = 7;
    public static final int COL_TRANG_THAI = 8;
    public static final int COL_IN = 9;

    private final String[] columnNames = {"STT", "Vé ID", "Tên khách hàng", "CCCD KH", "Đơn đặt chỗ ID", "TT chuyến",
            "TT ghế", "Giá", "Trạng thái", "In"};

    private List<VeDTO> rows;

    private Consumer<VeDTO> rowSelectionListener;

    public VeTableModel() {
        this.rows = new ArrayList<>();
    }

    public List<VeDTO> getRows() {
        return rows;
    }

    public void setRows(List<VeDTO> rows) {
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COL_GIA) {
            return Double.class;
        }

        return Object.class; // Để render button hoặc string
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;

    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VeDTO row = rows.get(rowIndex);
        switch (columnIndex) {

            case COL_STT:
                return rowIndex + 1;
            case COL_VE_ID:
                return row.getVeID();
            case COL_TEN_KHACH_HANG:
                return row.getKhachHangDTO().getHoTen();
            case COL_CCCD_KHACH_HANG:
                return row.getKhachHangDTO().getSoGiayTo();
            case COL_DDC_ID:
                return row.getDonDatChoID();
            case COL_CHUYEN:
                return String.format("<html>%s [%s - %s]<br/>%s</html>", row.getTauID(), row.getGaDiID(),
                        row.getGaDenID(), row.getNgayGioDi().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));
            case COL_GHE:
                return String.format("<html>Toa: %s - Chỗ: %s</html>", row.getSoToa(), row.getSoGhe());
            case COL_GIA:
                return row.getGia();
            case COL_TRANG_THAI:
                return row.getTrangThai();
            case COL_IN:
                return ""; // Trả về text để renderer vẽ thành nút
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        VeDTO row = rows.get(rowIndex);

        // Phát sự kiện để VeController bắt
        if (rowSelectionListener != null) {
            rowSelectionListener.accept(row);
        }
    }

    public VeDTO getRow(int rowIndex) {
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
