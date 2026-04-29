package gui.application.form.quanLyGa;/*
 * @ (#) PanelQuanLyGa.java   1.0     20/11/2025
package gui.application.form.quanLyGa;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 20/11/2025
 */

import bus.Ga_BUS;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class PanelQuanLyGa extends JPanel {
    private final NhanVien nhanVienThucHien;
    private final Ga_BUS gaBus;

    private JTextField txtTimKiem;

    private JTable tableGa;
    private DefaultTableModel tableModelGa;

    public PanelQuanLyGa(NhanVien nhanVien){
        this.nhanVienThucHien = nhanVien;
        this.gaBus = new Ga_BUS();
        setLayout(new BorderLayout());
        initComponents();
    }

    public void initComponents() {
        JPanel panelNorth = new JPanel(new BorderLayout());
        JPanel panelHeader = new JPanel(new MigLayout("wrap 1, fillx, insets 10 10 5 10"));

        Font baseFont = new Font("Segoe UI", Font.PLAIN, 15);
        Font holdBaseFont = baseFont.deriveFont(Font.BOLD);

        JLabel title = new JLabel("QUẢN LÝ THÔNG TIN CÁC GA", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(new Color(30, 41, 58));
        panelHeader.add(title, "growx");

        JPanel panelSearch = new JPanel(new MigLayout("insets 5 10 10 10,fillx, wrap 3", "[grow, push][pref!][grow,push]","[]"));
        txtTimKiem = new JTextField(20);
        txtTimKiem.setFont(baseFont);
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Nhập tên ga hoặc mã ga để tìm kiếm:");

        panelSearch.add(new JLabel("Danh sách các ga theo chiều từ Nam ra Bắc:")).setFont(holdBaseFont);
        panelSearch.add(new JLabel("Tìm kiếm Ga:")).setFont(holdBaseFont);
        panelSearch.add(txtTimKiem, "growx,pushx,wrap 10");


        panelHeader.add(panelSearch, "growx");
        panelNorth.add(panelHeader, BorderLayout.NORTH);
        add(panelNorth, BorderLayout.NORTH);

        String[] columnNames = {"Tên Ga", "Mã Ga", "Tỉnh Thành"};
        tableModelGa = new DefaultTableModel(columnNames, 0){
        @Override
        public boolean isCellEditable ( int row, int column){
            return false;
            }
        };
        tableGa = new JTable(tableModelGa);
        tableGa.setRowHeight(45);
        tableGa.setFont(baseFont);

        JTableHeader tableHeader = tableGa.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableHeader.setBackground(new Color(30, 41, 58));
        tableHeader.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setFont(baseFont);

        tableGa.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableGa.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        tableGa.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);


        JScrollPane scrollPane = new JScrollPane(tableGa);
        add(scrollPane, BorderLayout.CENTER);

        loadDataToTable();
    }

    private void loadDataToTable(){
        List<Object[]> dsGaSorted = gaBus.getAllGaSortedByKhoangCachChuan();
        tableModelGa.setRowCount(0);
        for(Object[] row : dsGaSorted){
            tableModelGa.addRow(row);
        }
        tableGa.getColumnModel().getColumn(1).setPreferredWidth(150);
    }
}
