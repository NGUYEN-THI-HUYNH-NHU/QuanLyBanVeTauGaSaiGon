package gui.application.form.donDatCho;
/*
 * @(#) ChiTietDonDatChoTableModel.java  1.0  [12:49:31 PM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dto.VeDTO;
import entity.type.TrangThaiVe;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDonDatChoTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_VE_ID = 1;
    public static final int COL_GA_DI = 2;
    public static final int COL_GA_DEN = 3;
    public static final int COL_GHE = 4;
    public static final int COL_NGAY_GIO_DI = 5;
    public static final int COL_GIA = 6;
    public static final int COL_TRANG_THAI = 7;

    private final String[] columnNames = {"STT", "Mã vé", "Ga đi", "Ga đến", "Thông tin ghế", "Ngày giờ đi", "Giá vé",
            "Trạng thái"};

    private List<VeDTO> rows;

    public ChiTietDonDatChoTableModel() {
        this.rows = new ArrayList<>();
    }

    public void setRows(List<VeDTO> newRows) {
        this.rows = newRows;
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COL_VE_ID;
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
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        VeDTO row = rows.get(rowIndex);
        switch (columnIndex) {
            case COL_STT:
                return rowIndex + 1;
            case COL_VE_ID:
                return row.getVeID();
            case COL_GA_DI:
                return row.getTenGaDi();
            case COL_GA_DEN:
                return row.getTenGaDen();
            case COL_GHE:
                StringBuilder sb = new StringBuilder();
                return sb.append("Tau: " + row.getTauID())
                        .append(" - Toa: " + row.getSoToa()).append(" - Ghế: " + row.getSoGhe());
            case COL_NGAY_GIO_DI:
                return row.getNgayGioDi().format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"));
            case COL_GIA:
                return row.getGia();
            case COL_TRANG_THAI:
                return TrangThaiVe.valueOf(row.getTrangThai()).getDescription();
            default:
                return null;
        }
    }
}