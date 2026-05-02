package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc3.java  1.0  [2:58:00 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import gui.tuyChinh.CurrencyTopRenderer;
import gui.tuyChinh.LeftTopRenderer;
import gui.tuyChinh.SimpleComboBoxRenderer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class PanelHoanVeBuoc3 extends JPanel {
    private VeHoanTableModel model;
    private JTable table;
    private JButton btnRefresh;
    private JButton btnXacNhan;

    public PanelHoanVeBuoc3() {
        setLayout(new BorderLayout());
        setBorder(new LineBorder(new Color(220, 220, 220)));

        model = new VeHoanTableModel();
        table = new JTable(model);

        setupTable();

        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(0, 300));
        add(sp, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.6f));
        btnXacNhan = new JButton("Xác nhận");
        south.add(btnRefresh, BorderLayout.WEST);
        south.add(btnXacNhan, BorderLayout.EAST);

        add(south, BorderLayout.SOUTH);
    }

    private void setupTable() {
        String[] lyDoHoan = {"Không còn nhu cầu", "Thay đổi kế hoạch", "Lý do cá nhân", "Trùng vé", "Khác"};
        JComboBox<String> cbLyDoHoan = new JComboBox<>(lyDoHoan);
        DefaultCellEditor cellEditor = new DefaultCellEditor(cbLyDoHoan);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO).setCellEditor(cellEditor);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO).setCellRenderer(new SimpleComboBoxRenderer());

        table.setRowHeight(70);

        // Cấu hình độ rộng cột (dùng chỉ số mới)
        table.getColumnModel().getColumn(VeHoanTableModel.COL_STT).setMaxWidth(30);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setMinWidth(130);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setMinWidth(200);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LY_DO).setMinWidth(120);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_CHON).setMaxWidth(50);

        // RENDERER CHO CỘT THỜI GIAN
        DefaultTableCellRenderer timeRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setVerticalAlignment(SwingConstants.TOP);
                c.setForeground(Color.GREEN);
                setFont(getFont().deriveFont(Font.BOLD));
                return c;
            }
        };

        table.getColumnModel().getColumn(VeHoanTableModel.COL_TG_CON_LAI).setCellRenderer(timeRenderer);

        CurrencyTopRenderer currencyRenderer = new CurrencyTopRenderer();
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THANH_TIEN).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_LE_PHI).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TIEN_HOAN).setCellRenderer(currencyRenderer);

        LeftTopRenderer leftTopRenderer = new LeftTopRenderer();
        table.getColumnModel().getColumn(VeHoanTableModel.COL_TEN).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_VE).setCellRenderer(leftTopRenderer);

        table.removeColumn(table.getColumnModel().getColumn(VeHoanTableModel.COL_THONG_TIN_PHI));
    }

    public void displayConfirmation(List<VeHoanRow> selectedRows) {
        model.setRows(selectedRows);
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JButton getBtnXacNhan() {
        return btnXacNhan;
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
}
