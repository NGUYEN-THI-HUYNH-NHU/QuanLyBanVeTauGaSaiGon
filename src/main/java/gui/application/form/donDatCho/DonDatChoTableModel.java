package gui.application.form.donDatCho;
/*
 * @(#) DonDatChoTableModel.java  1.0  [11:45:30 AM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */

import dto.DonDatChoDTO;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DonDatChoTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_DDC_ID = 1;
    public static final int COL_TEN_KH = 2;
    public static final int COL_CCCD_KH = 3;
    public static final int COL_SDT_KH = 4;
    public static final int COL_TONG_VE = 5;
    public static final int COL_SO_HOAN = 6;
    public static final int COL_SO_DOI = 7;
    public static final int COL_THOI_DIEM_DAT = 8;
    public static final int COL_NGUOI_LAP = 9;
    public static final int COL_XEM = 10;

    private final String[] columnNames = {"STT", "Mã đặt chỗ", "Tên khách hàng", "CCCD KH", "SĐT KH", "SL vé",
            "Đã hoàn", "Đã đổi", "Ngày đặt chỗ", "Người lập", "Xem"};

    private List<DonDatChoDTO> rows;

    public DonDatChoTableModel() {
        this.rows = new ArrayList<>();
    }

    public void setRows(List<DonDatChoDTO> rows) {
        this.rows = new ArrayList<>(rows);
        fireTableDataChanged();
    }

    public DonDatChoDTO getRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            return rows.get(rowIndex);
        }
        return null;
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
        if (columnIndex == COL_THOI_DIEM_DAT) {
            return LocalDateTime.class;
        }
        if (columnIndex == COL_TONG_VE || columnIndex == COL_SO_HOAN || columnIndex == COL_SO_DOI) {
            return Integer.class;
        }
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex > COL_STT && columnIndex < COL_TONG_VE;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        DonDatChoDTO row = rows.get(rowIndex);
        switch (columnIndex) {
            case COL_STT:
                return rowIndex + 1;
            case COL_DDC_ID:
                return row.getId();
            case COL_TEN_KH:
                return row.getKhachHangDTO().getHoTen();
            case COL_CCCD_KH:
                return row.getKhachHangDTO().getSoGiayTo();
            case COL_SDT_KH:
                return row.getKhachHangDTO().getSoDienThoai();
            case COL_TONG_VE:
                return row.getTongSoVe();
            case COL_SO_HOAN:
                return row.getSoVeHoan();
            case COL_SO_DOI:
                return row.getSoVeDoi();
            case COL_THOI_DIEM_DAT:
                return row.getThoiDiemDatCho();
            case COL_NGUOI_LAP:
                return row.getNhanVienID();
            case COL_XEM:
                return ""; // Render button
            default:
                return null;
        }
    }
}