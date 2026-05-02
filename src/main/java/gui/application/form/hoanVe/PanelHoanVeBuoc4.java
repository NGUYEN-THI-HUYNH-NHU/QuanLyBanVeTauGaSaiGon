package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc4.java  1.0  [2:15:18 PM] Nov 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 14, 2025
 * @version: 1.0
 */

import gui.tuyChinh.CurrencyTopRenderer;
import gui.tuyChinh.LeftTopRenderer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class PanelHoanVeBuoc4 extends JPanel {
    private VeHoanTableModel model;
    private JTable table;

    public PanelHoanVeBuoc4() {
        setLayout(new BorderLayout());
        setBorder(new LineBorder(new Color(220, 220, 220)));

        model = new VeHoanTableModel();
        table = new JTable(model);

        setupTable();

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 350));
        add(sp, BorderLayout.CENTER);
    }

    private void setupTable() {
        table.setRowHeight(80);

        table.getColumnModel().getColumn(VeHoanTableModel.COL_STT).setMaxWidth(30);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setMinWidth(140);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setMinWidth(180);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO).setMinWidth(120);

        // Áp dụng lớp Renderer nội tuyến để định dạng
        CurrencyTopRenderer currencyRenderer = new CurrencyTopRenderer();
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THANH_TIEN).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LE_PHI).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TIEN_HOAN).setCellRenderer(currencyRenderer);

        LeftTopRenderer leftTopRenderer = new LeftTopRenderer();
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TG_CON_LAI).setCellRenderer(leftTopRenderer);

        table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_PHI));
        table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_TG_CON_LAI - 1));
        table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_CHON - 2));
    }

    /**
     * @param row
     */
    public void removeRow(VeHoanRow row) {
        int rowIndex = model.getRowIndex(row);
        if (rowIndex != -1) {
            // Chỉ cập nhật dòng thay đổi, hiệu quả hơn fireTableDataChanged()
            row.setSelected(false);
            model.removeRow(rowIndex);
        }
    }

    /**
     * @param enabled
     */
    public void setComponentsEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }

    public void addRowSelectionListener(Consumer<VeHoanRow> listener) {
        if (model != null) {
            model.setRowSelectionListener(listener);
        }
    }

    /**
     * @param listVeHoanRow
     */
    public void hienThiThongTin(List<VeHoanRow> listVeHoanRow) {
        model.setRows(listVeHoanRow);
    }
}
