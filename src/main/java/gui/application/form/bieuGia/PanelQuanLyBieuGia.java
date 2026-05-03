package gui.application.form.bieuGia;
/*
 * @(#) PanelQuanLyBieuGia.java  1.0  [8:31:14 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import controller.bieuGiaVe.BieuGia_CTRL;
import dto.NhanVienDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PanelQuanLyBieuGia extends JPanel {
    private final BieuGia_CTRL controller;
    private final NhanVienDTO nhanVienThucHien;
    private JTable table;
    private BieuGiaVeTableModel tableModel;
    private JComboBox<String> txtTimKiem;
    private JComboBox<String> cboLocTuyen;
    private JComboBox<String> cboLocTau;
    private JButton btnLamMoi;
    private JButton btnThemMoi;

    public PanelQuanLyBieuGia(NhanVienDTO nhanVienThucHien) {
        setLayout(new BorderLayout(10, 10));
        initUI();
        this.controller = new BieuGia_CTRL(this);
        this.nhanVienThucHien = nhanVienThucHien;
    }

    private void initUI() {
        Color base_color = new Color(36, 104, 155);
        JLabel lblTitle = new JLabel("QUẢN LÝ BIỂU GIÁ VÉ");
        lblTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 24));
        lblTitle.setForeground(base_color);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // --- 1. PANEL LỌC ---
        JPanel pnlLoc = new JPanel(new BorderLayout());
        pnlLoc.setBorder(new TitledBorder("Bộ lọc tìm kiếm"));

        JPanel pnlLocLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtTimKiem = new JComboBox<>();
        cboLocTuyen = new JComboBox<>();
        cboLocTau = new JComboBox<>();

        btnLamMoi = new JButton("(F5) Làm mới");
        btnLamMoi.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.8f));
        btnLamMoi.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        btnLamMoi.setBackground(new Color(36, 104, 155));
        btnLamMoi.setForeground(Color.WHITE);

        btnThemMoi = new JButton("Thêm biểu giá");
        btnThemMoi.setIcon(new FlatSVGIcon("icon/svg/add-1.svg", 0.8f));
        btnThemMoi.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        btnThemMoi.setBackground(new Color(36, 104, 155));
        btnThemMoi.setForeground(Color.WHITE);

        pnlLocLeft.add(new JLabel("Mã Biểu Giá:"));
        pnlLocLeft.add(txtTimKiem);
        pnlLocLeft.add(new JLabel("Tuyến:"));
        pnlLocLeft.add(cboLocTuyen);
        pnlLocLeft.add(new JLabel("Tàu:"));
        pnlLocLeft.add(cboLocTau);

        pnlLocLeft.add(btnLamMoi);
        pnlLocLeft.add(btnThemMoi);

        pnlLoc.add(pnlLocLeft, BorderLayout.CENTER);

        JPanel pnlNorthContainer = new JPanel();
        pnlNorthContainer.setLayout(new BoxLayout(pnlNorthContainer, BoxLayout.Y_AXIS));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlLoc.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlLoc.getPreferredSize().height));

        pnlNorthContainer.add(lblTitle);
        pnlNorthContainer.add(pnlLoc);
        add(pnlNorthContainer, BorderLayout.NORTH);

        // --- 2. TABLE ---
        tableModel = new BieuGiaVeTableModel();
        table = new JTable(tableModel);
        table.setRowHeight(40);

        table.getTableHeader().setEnabled(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã biểu giá
        table.getColumnModel().getColumn(1).setPreferredWidth(50); // Ưu tiên
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Tuyến
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Tàu
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Toa
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Khoảng cách
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Hiệu lực
        table.getColumnModel().getColumn(7).setPreferredWidth(150); // Giá
        table.getColumnModel().getColumn(8).setPreferredWidth(80); // Xem
        table.getColumnModel().getColumn(9).setPreferredWidth(120); // Cập nhật

        table.setShowGrid(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 220, 220));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == BieuGiaVeTableModel.COL_XEM || i == BieuGiaVeTableModel.COL_SUA) {
                table.getColumnModel().getColumn(i).setCellRenderer(new BieuGiaVeTableButtonRenderer());
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);

                lbl.setBackground(base_color);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE)); // Viền trắng nhẹ giữa các cột
                // header
                return lbl;
            }
        });
        header.setFocusable(false);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public String getSelectedID() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return tableModel.getValueAt(row, 0).toString();
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public BieuGiaVeTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public void setBtnLamMoi(JButton btnLamMoi) {
        this.btnLamMoi = btnLamMoi;
    }

    public JButton getBtnThemMoi() {
        return btnThemMoi;
    }

    public void setBtnThemMoi(JButton btnThemMoi) {
        this.btnThemMoi = btnThemMoi;
    }

    public JComboBox<String> getTxtTimKiem() {
        return txtTimKiem;
    }

    public JComboBox<String> getCboLocTuyen() {
        return cboLocTuyen;
    }

    public JComboBox<String> getCboLocTau() {
        return cboLocTau;
    }

    public BieuGia_CTRL getController() {
        return controller;
    }

    public NhanVienDTO getNhanVienThucHien() {
        return nhanVienThucHien;
    }
}