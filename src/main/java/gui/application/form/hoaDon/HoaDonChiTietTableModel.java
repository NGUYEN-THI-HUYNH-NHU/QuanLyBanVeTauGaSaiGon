package gui.application.form.hoaDon;
/*
 * @(#) HoaDonChiTietTableModel.java  1.0  [9:06:53 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */

import dto.HoaDonChiTietDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class HoaDonChiTietTableModel extends AbstractTableModel {
    public static final int COL_STT = 0;
    public static final int COL_VE_PHIEU_ID = 1;
    public static final int COL_TEN_DV = 2;
    public static final int COL_DVT = 3;
    public static final int COL_SO_LUONG = 4;
    public static final int COL_DON_GIA = 5;
    public static final int COL_THANH_TIEN = 6;

    private final String[] columnNames = {"STT", "Mã vé/phiếu", "Tên dịch vụ", "ĐVT", "SL", "Đơn giá", "Thành tiền"};

    private List<HoaDonChiTietDTO> rows;

    public HoaDonChiTietTableModel() {
        this.rows = new ArrayList<>();
    }

    // Hàm cập nhật dữ liệu cho bảng
    public void setRows(List<HoaDonChiTietDTO> newRows) {
        this.rows = newRows;
        fireTableDataChanged();
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
        if (columnIndex == COL_SO_LUONG) {
            return Integer.class;
        }
        if (columnIndex == COL_DON_GIA || columnIndex == COL_THANH_TIEN) {
            return Double.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        HoaDonChiTietDTO row = rows.get(rowIndex);
        switch (columnIndex) {
            case COL_STT:
                return rowIndex + 1;
            case COL_VE_PHIEU_ID:
                if (row.getVeID() != null) return row.getVeID();

                if (row.getPhieuDungPhongVIPID() != null) return row.getPhieuDungPhongVIPID();

                return "";
            case COL_TEN_DV:
                return row.getTenDichVu();
            case COL_DVT:
                return row.getDonViTinh();
            case COL_SO_LUONG:
                return row.getSoLuong();
            case COL_DON_GIA:
                return row.getDonGia();
            case COL_THANH_TIEN:
                return row.getThanhTien();
            default:
                return null;
        }
    }
}
