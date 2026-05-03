package gui.application.form.xemInVe;
/*
 * @(#) PanelXemInVe.java  1.0  [7:09:32 PM] Dec 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import controller.xemInVe.XemInVeController;
import entity.type.TrangThaiVe;
import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 17, 2025
 * @version: 1.0
 */

@Getter
public class PanelXemInVe extends JPanel {
    private final XemInVeController controller;
    private JTextField txtTuKhoa;
    private JButton btnTraCuu;
    private JButton btnRefresh;
    private JComboBox<String> cboLoaiTimKiem;
    private JComboBox<String> cboLoaiVe;
    private JTextField txtKhachHangSuggest;
    private JDateChooser dateChooserTuNgay;
    private JDateChooser dateChooserDenNgay;
    private JButton btnLoc;
    private JButton btnReset;
    private JTable table;
    private VeTableModel tableModel;
    private JCheckBox checkBoxTatCaNgay;
    private JButton btnPrevPage;
    private JButton btnNextPage;
    private JPanel pnlPageNumbers;

    public PanelXemInVe() {
        setLayout(new BorderLayout());
        initUI();
        controller = new XemInVeController(this);
    }

    private void initUI() {
        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));

        // 1. PANEL TRA CỨU
        JPanel pnlTraCuu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTraCuu.setBorder(new TitledBorder("Tra cứu nhanh"));

        cboLoaiTimKiem = new JComboBox<>(new String[]{"Mã vé", "Mã đặt chỗ", "Số giấy tờ khách hàng"});
        txtTuKhoa = new JTextField(18);
        btnTraCuu = new JButton("Tra cứu");
        btnTraCuu.setBackground(new Color(36, 104, 155));
        btnTraCuu.setForeground(Color.WHITE);
        btnTraCuu.setIcon(new FlatSVGIcon("icon/svg/search.svg", 0.8f));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.8f));

        pnlTraCuu.add(new JLabel("Tìm theo: "));
        pnlTraCuu.add(cboLoaiTimKiem);
        pnlTraCuu.add(txtTuKhoa);
        pnlTraCuu.add(btnTraCuu);
        pnlTraCuu.add(btnRefresh);

        // 2. PANEL LỌC
        JPanel pnlLoc = new JPanel();
        pnlLoc.setLayout(new BorderLayout());
        pnlLoc.setBorder(new TitledBorder("Bộ lọc chi tiết"));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // 1. Loại HĐ
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại vé:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        cboLoaiVe = new JComboBox<>(new String[]{"Tất cả", "Vé đã bán", "Vé đã dùng", "Vé đã hoàn", "Vé đã đổi"});
        pnlInput.add(cboLoaiVe, gbc);

        // 2. Khách hàng
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        pnlInput.add(new JLabel("Khách hàng:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        txtKhachHangSuggest = new JTextField(14);
        txtKhachHangSuggest.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Họ tên/SĐT/CCCD/ID");
        txtKhachHangSuggest.setToolTipText("Nhập tên, SĐT, CCCD hoặc ID");
        pnlInput.add(txtKhachHangSuggest, gbc);

        // 4. Checkbox Tất cả ngày
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        checkBoxTatCaNgay = new JCheckBox("Tất cả ngày");
        checkBoxTatCaNgay.setSelected(true);
        checkBoxTatCaNgay.setBackground(Color.WHITE);
        pnlInput.add(checkBoxTatCaNgay, gbc);
        gbc.gridwidth = 1;

        // 5. Từ ngày
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pnlInput.add(new JLabel("Từ ngày:"), gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        dateChooserTuNgay = new JDateChooser();
        dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserTuNgay.setDate(new Date());
        dateChooserTuNgay.setEnabled(false);
        pnlInput.add(dateChooserTuNgay, gbc);

        // 6. Đến ngày
        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pnlInput.add(new JLabel("Đến ngày:"), gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        dateChooserDenNgay = new JDateChooser();
        dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
        dateChooserDenNgay.setDate(new Date());
        dateChooserDenNgay.setEnabled(false);
        pnlInput.add(dateChooserDenNgay, gbc);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlButtons.setBackground(Color.WHITE);

        btnReset = new JButton("Xóa bộ lọc");
        btnReset.setIcon(new FlatSVGIcon("icon/svg/reset.svg", 0.8f));

        btnLoc = new JButton("Lọc");
        btnLoc.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 0.8f));
        btnLoc.setBackground(new Color(38, 117, 191));
        btnLoc.setForeground(Color.WHITE);

        pnlButtons.add(btnLoc);
        pnlButtons.add(btnReset);

        pnlLoc.add(pnlInput, BorderLayout.CENTER);
        pnlLoc.add(pnlButtons, BorderLayout.SOUTH);

        pnlTop.add(pnlTraCuu);
        pnlTop.add(pnlLoc);

        // Bảng
        tableModel = new VeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize()));
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(1).setMinWidth(180);
        table.getColumnModel().getColumn(2).setMinWidth(130);
        table.getColumnModel().getColumn(3).setMinWidth(90);
        table.getColumnModel().getColumn(4).setMinWidth(110);
        table.getColumnModel().getColumn(5).setMinWidth(110);
        table.getColumnModel().getColumn(6).setMinWidth(70);
        table.getColumnModel().getColumn(7).setMaxWidth(70);
        table.getColumnModel().getColumn(8).setMaxWidth(70);
        table.getColumnModel().getColumn(9).setMaxWidth(40);

        LeftCenterAlignRenderer leftCenterRenderer = new LeftCenterAlignRenderer();
        CurrencyRenderer currencyRenderer = new CurrencyRenderer();

        table.getColumnModel().getColumn(VeTableModel.COL_VE_ID).setCellRenderer(leftCenterRenderer);
        table.getColumnModel().getColumn(VeTableModel.COL_TEN_KHACH_HANG).setCellRenderer(leftCenterRenderer);
        table.getColumnModel().getColumn(VeTableModel.COL_CCCD_KHACH_HANG).setCellRenderer(leftCenterRenderer);
        table.getColumnModel().getColumn(VeTableModel.COL_CHUYEN).setCellRenderer(leftCenterRenderer);
        table.getColumnModel().getColumn(VeTableModel.COL_GHE).setCellRenderer(leftCenterRenderer);

        table.getColumnModel().getColumn(VeTableModel.COL_GIA).setCellRenderer(currencyRenderer);

        table.getColumnModel().getColumn(VeTableModel.COL_TRANG_THAI).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if (status.equals(TrangThaiVe.DA_BAN.getDescription())) {
                    c.setForeground(Color.GREEN);
                    setFont(getFont().deriveFont(Font.ITALIC));
                } else if (status.equals(TrangThaiVe.DA_DUNG.getDescription())) {
                    c.setForeground(Color.BLACK);
                } else if (status.equals(TrangThaiVe.DA_HOAN.getDescription())) {
                    c.setForeground(Color.RED);
                } else if (status.equals(TrangThaiVe.DA_DOI.getDescription())) {
                    c.setForeground(Color.ORANGE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

        // THÊM: Tạo Panel Phân Trang
        JPanel pnlPagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPrevPage = new JButton("<");
        btnNextPage = new JButton(">");
        pnlPageNumbers = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));

        pnlPagination.add(btnPrevPage);
        pnlPagination.add(pnlPageNumbers);
        pnlPagination.add(btnNextPage);

        // Bọc Table và Phân trang lại
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        pnlCenter.add(pnlPagination, BorderLayout.SOUTH);

        add(pnlTop, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
    }
}
