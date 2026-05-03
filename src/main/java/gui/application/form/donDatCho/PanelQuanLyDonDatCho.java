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
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

@Getter
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
    private JButton btnPrevPage;
    private JButton btnNextPage;
    private JPanel pnlPageNumbers;
    private JComboBox<Integer> cboRowsPerPage;

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
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_DDC_ID).setPreferredWidth(100);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_TEN_KH).setPreferredWidth(140);
        table.getColumnModel().getColumn(DonDatChoTableModel.COL_TEN_KH).setMinWidth(140);
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

        // Tạo Panel Phân Trang
        JPanel pnlPagination = new JPanel(new BorderLayout());
        pnlPagination.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Khu vực canh giữa: Chứa các nút tiến/lùi và số trang
        JPanel pnlPageControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        pnlPageNumbers = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));

        pnlPageControls.add(btnPrevPage);
        pnlPageControls.add(pnlPageNumbers);
        pnlPageControls.add(btnNextPage);

        // Khu vực góc Phải: dropdown tùy chỉnh số dòng
        JPanel pnlRowsCount = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlRowsCount.add(new JLabel("Số dòng/trang:"));
        cboRowsPerPage = new JComboBox<>(new Integer[]{10, 15, 20, 25, 50});
        cboRowsPerPage.setSelectedItem(20);
        cboRowsPerPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pnlRowsCount.add(cboRowsPerPage);

        // Ghép vào thanh phân trang
        pnlPagination.add(pnlPageControls, BorderLayout.CENTER);
        pnlPagination.add(pnlRowsCount, BorderLayout.EAST);

        // Bọc Table và Phân trang lại
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        pnlCenter.add(pnlPagination, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
    }
}