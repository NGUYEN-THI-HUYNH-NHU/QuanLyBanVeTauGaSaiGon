package gui.application.form.donDatCho;
/*
 * @(#) PanelQuanLyDonDatCho.java  1.0  [11:44:06 AM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import controller.donDatCho.DonDatChoController;
import gui.tuyChinh.DateTimeRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PanelQuanLyDonDatCho extends JPanel {
    private final Font fontBold = new Font(getFont().getFontName(), Font.BOLD, 12);
    private final DonDatChoController controller;
    private JTextField txtTuKhoa;
    private JButton btnTraCuu;
    private JButton btnRefresh;
    private JComboBox<String> cboLoaiTimKiem;
    private JDateChooser dateChooserTuNgay;
    private JDateChooser dateChooserDenNgay;
    private JCheckBox checkBoxTatCaNgay;
    private JButton btnLoc;
    private JButton btnReset;
    private JTable table;
    private DonDatChoTableModel tableModel;

    public PanelQuanLyDonDatCho() {
        setLayout(new BorderLayout());
        initUI();
        controller = new DonDatChoController(this);
    }

    private void initUI() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));

        // --- 1. PANEL TRA CỨU ---
        JPanel pnlTraCuu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTraCuu.setBorder(new TitledBorder("Tra cứu đơn đặt chỗ"));

        cboLoaiTimKiem = new JComboBox<>(
                new String[]{"Mã đặt chỗ", "Số giấy tờ", "Số điện thoại", "Tên khách hàng"});
        txtTuKhoa = new JTextField(20);
        btnTraCuu = new JButton("Tìm kiếm");
        btnTraCuu.setBackground(new Color(36, 104, 155));
        btnTraCuu.setForeground(Color.WHITE);
        btnTraCuu.setIcon(new FlatSVGIcon("icon/svg/search.svg", 0.8f));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.8f));

        pnlTraCuu.add(new JLabel("Tiêu chí: "));
        pnlTraCuu.add(cboLoaiTimKiem);
        pnlTraCuu.add(txtTuKhoa);
        pnlTraCuu.add(btnTraCuu);
        pnlTraCuu.add(btnRefresh);

        // --- 2. PANEL LỌC (Thống kê, tìm kiếm theo ngày) ---
        JPanel pnlLoc = new JPanel(new BorderLayout());
        pnlLoc.setBorder(new TitledBorder("Bộ lọc nâng cao"));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Ngày tháng
        gbc.gridx = 0;
        gbc.gridy = 0;
        checkBoxTatCaNgay = new JCheckBox("Tất cả ngày");
        checkBoxTatCaNgay.setSelected(true);
        pnlInput.add(checkBoxTatCaNgay, gbc);

        gbc.gridx = 1;
        pnlInput.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 2;
        dateChooserTuNgay = new JDateChooser();
        dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserTuNgay.setEnabled(false);
        pnlInput.add(dateChooserTuNgay, gbc);

        gbc.gridx = 3;
        pnlInput.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 4;
        dateChooserDenNgay = new JDateChooser();
        dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserDenNgay.setEnabled(false);
        pnlInput.add(dateChooserDenNgay, gbc);

        // Nút lọc nằm bên phải
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnReset = new JButton("Xóa bộ lọc");
        btnReset.setIcon(new FlatSVGIcon("icon/svg/reset.svg", 0.8f));
        btnLoc = new JButton("Lọc");
        btnLoc.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 0.8f));
        btnLoc.setBackground(new Color(36, 104, 155));
        btnLoc.setForeground(Color.WHITE);
        pnlButtons.add(btnLoc);
        pnlButtons.add(btnReset);

        pnlLoc.add(pnlInput, BorderLayout.CENTER);
        pnlLoc.add(pnlButtons, BorderLayout.SOUTH);

        pnlTop.add(pnlTraCuu);
        pnlTop.add(pnlLoc);

        // --- 3. BẢNG DỮ LIỆU ---
        tableModel = new DonDatChoTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setFont(fontBold);
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        // Cấu hình độ rộng cột
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_STT).setMaxWidth(36);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_DDC_ID).setPreferredWidth(100);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_TEN_KH).setPreferredWidth(130);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_TEN_KH).setMinWidth(130);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_TONG_VE).setMaxWidth(50);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_SO_HOAN).setMaxWidth(50);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_SO_DOI).setMaxWidth(50);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_THOI_DIEM_DAT).setPreferredWidth(100);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_XEM).setMaxWidth(40);

        table.getColumnModel().getColumn(DonDatChoTableModel.COL_THOI_DIEM_DAT).setCellRenderer(new DateTimeRenderer());

        // Renderer cho nút Xem
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_XEM)
                .setCellRenderer(new DonDatChoViewButtonRenderer());

        JScrollPane scrollPane = new JScrollPane(table);

        add(pnlTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Getters để Controller sử dụng
    public JTextField getTxtTuKhoa() {
        return txtTuKhoa;
    }

    public JButton getBtnTraCuu() {
        return btnTraCuu;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JComboBox<String> getCboLoaiTimKiem() {
        return cboLoaiTimKiem;
    }

    public JDateChooser getDateChooserTuNgay() {
        return dateChooserTuNgay;
    }

    public JDateChooser getDateChooserDenNgay() {
        return dateChooserDenNgay;
    }

    public JCheckBox getCheckBoxTatCaNgay() {
        return checkBoxTatCaNgay;
    }

    public JButton getBtnLoc() {
        return btnLoc;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public JTable getTable() {
        return table;
    }

    public DonDatChoTableModel getTableModel() {
        return tableModel;
    }
}