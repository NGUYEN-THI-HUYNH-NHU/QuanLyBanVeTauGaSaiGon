package gui.application.form.thongKe;

import dao.ThongKeVe_DAO;
import dao.ThongKeVe_DAO.ThongKeVeChiTietItem;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel hiển thị thống kê vé theo thời gian.
 * Phiên bản đầy đủ đã xóa Trạng Thái Vé và thu nhỏ nút Tìm kiếm.
 */
public class PanelThongKeVe extends JPanel {

    private final ThongKeVe_DAO thongKeVeDAO;
    private static final Logger LOGGER = Logger.getLogger(PanelThongKeVe.class.getName());

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");

    // ====== Lọc thời gian ======
    private final JComboBox<String> cbLoaiThoiGian;
    private final JDateChooser tuNgay;
    private final JDateChooser denNgay;
    private final JComboBox<String> cbTuThang;
    private final JComboBox<String> cbDenThang;
    private final JComboBox<Integer> cbTuNamThang;
    private final JComboBox<Integer> cbDenNamThang;
    private final JComboBox<Integer> cbTuNam;
    private final JComboBox<Integer> cbDenNam;
    private final JPanel filterSwitcher;
    private static final String CARD_TATCA = "CARD_TATCA";
    private static final String CARD_NGAY = "CARD_NGAY";
    private static final String CARD_THANG = "CARD_THANG";
    private static final String CARD_NAM = "CARD_NAM";

    // ====== Lọc tuyến, nhân viên, loại vé ======
    private final JComboBox<String> cbLoaiTuyen;
    private final JComboBox<String> cbGaDi;
    private final JComboBox<String> cbGaDen;
    private final JComboBox<String> cbNhanVien;
    private final JComboBox<String> cbLoaiVe;
    private final JPanel panelGaDiDen;
    private final JButton btnTimKiem;

    private final Map<String, String> nhanVienMap = new HashMap<>();
    private final Map<String, String> hangToaMap = new HashMap<>();

    // ====== Card thống kê ======
    private final JLabel lblTongSoVeBanValue;
    private final JLabel lblTongVeConHieuLucValue;
    private final JLabel lblTongVeDaDungValue;
    private final JLabel lblTongVeDaDoiValue;
    private final JLabel lblTongVeHoanValue;
    private final JLabel lblTongTienVeValue;

    // ====== Biểu đồ & Chi tiết ======
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    private static class ThongKeVeResult {
        int tongSoVeBan, tongVeConHieuLuc, tongVeDaDung, tongVeDaDoi, tongVeHoan;
        double tongTienVe;
        Map<String, ThongKeVeChiTietItem> thongKeVeChiTietTheoThoiGian;
    }

    public PanelThongKeVe() {
        this.thongKeVeDAO = new ThongKeVe_DAO();

        cbLoaiThoiGian = new JComboBox<>(new String[]{"Tất cả", "Theo ngày", "Theo tháng", "Theo năm"});
        tuNgay = new JDateChooser();
        denNgay = new JDateChooser();
        cbTuThang = new JComboBox<>();
        cbDenThang = new JComboBox<>();
        cbTuNamThang = new JComboBox<>();
        cbDenNamThang = new JComboBox<>();
        cbTuNam = new JComboBox<>();
        cbDenNam = new JComboBox<>();
        filterSwitcher = new JPanel(new CardLayout());

        cbLoaiTuyen = new JComboBox<>(new String[]{"Tất cả", "Theo Ga đi/đến"});
        cbGaDi = new JComboBox<>();
        cbGaDen = new JComboBox<>();
        cbNhanVien = new JComboBox<>();
        cbLoaiVe = new JComboBox<>();
        panelGaDiDen = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        btnTimKiem = new JButton("Tìm kiếm");

        lblTongSoVeBanValue = createValueLabel("...");
        lblTongVeConHieuLucValue = createValueLabel("...");
        lblTongVeDaDungValue = createValueLabel("...");
        lblTongVeDaDoiValue = createValueLabel("...");
        lblTongVeHoanValue = createValueLabel("...");
        lblTongTienVeValue = createValueLabel("...");

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "THỐNG KÊ VÉ",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(0, 110, 185)
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(buildFilterBar(), BorderLayout.NORTH);

        JPanel infoWrapper = new JPanel(new GridLayout(2, 3, 15, 15));
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        infoWrapper.add(createCard("Tổng số vé bán", lblTongSoVeBanValue, new Color(52, 152, 219)));
        infoWrapper.add(createCard("Vé còn hiệu lực", lblTongVeConHieuLucValue, new Color(46, 204, 113)));
        infoWrapper.add(createCard("Vé đã dùng", lblTongVeDaDungValue, new Color(155, 89, 182)));
        infoWrapper.add(createCard("Vé hoàn", lblTongVeHoanValue, new Color(231, 76, 60)));
        infoWrapper.add(createCard("Vé đổi", lblTongVeDaDoiValue, new Color(243, 156, 18)));
        infoWrapper.add(createCard("Tổng tiền vé", lblTongTienVeValue, new Color(39, 174, 96)));

        topPanel.add(infoWrapper, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        String[] columnNames = {"STT", "Thời Gian", "Vé bán", "Vé còn hiệu lực", "Vé đã dùng",
                "Vé đổi", "Vé hoàn", "Tuyến đường", "Tổng tiền vé (VNĐ)"};
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 3, 4, 5, 6 -> Integer.class;
                    case 8 -> Double.class;
                    default -> String.class;
                };
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails();

        lblChiTietTitle = new JLabel("Báo cáo thống kê chi tiết", JLabel.CENTER);
        lblChiTietTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblChiTietTitle.setBorder(new EmptyBorder(5, 0, 10, 0));

        btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setBackground(new Color(33, 115, 70));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportExcel.setEnabled(false);
        btnExportExcel.addActionListener(this::exportTableToExcel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnExportExcel);

        JPanel panelChiTietContainer = taoPanelChiTiet(lblChiTietTitle, tableChiTiet, bottomPanel);

        tab.addTab("Tổng quan", chartPanelContainer);
        tab.addTab("Chi tiết", panelChiTietContainer);
        add(tab, BorderLayout.CENTER);

        loadComboBoxesData();
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    // ===== buildFilterBar() =====
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ===== Hàng 0: Loại thời gian =====
        gbc.gridx = 0; gbc.gridy = 0;
        bar.add(new JLabel("Loại thời gian:"), gbc);
        gbc.gridx = 1;
        cbLoaiThoiGian.setPreferredSize(new Dimension(120, 32));
        bar.add(cbLoaiThoiGian, gbc);
        gbc.gridx = 2;
        filterSwitcher.setOpaque(false);
        filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);
        bar.add(filterSwitcher, gbc);

        // ===== Hàng 1: Lọc tuyến =====
        gbc.gridx = 0; gbc.gridy = 1;
        bar.add(new JLabel("Lọc tuyến:"), gbc);
        gbc.gridx = 1;
        cbLoaiTuyen.setPreferredSize(new Dimension(120, 32));
        bar.add(cbLoaiTuyen, gbc);
        gbc.gridx = 2;
        panelGaDiDen.setLayout(new GridLayout(1, 4, 6, 0));
        panelGaDiDen.setOpaque(false);
        panelGaDiDen.add(new JLabel("Ga đi:"));
        cbGaDi.setPreferredSize(new Dimension(140, 32));
        panelGaDiDen.add(cbGaDi);
        panelGaDiDen.add(new JLabel("Ga đến:"));
        cbGaDen.setPreferredSize(new Dimension(140, 32));
        panelGaDiDen.add(cbGaDen);
        bar.add(panelGaDiDen, gbc);

        // ===== Hàng 2: Nhân viên & Loại vé =====
        gbc.gridx = 0; gbc.gridy = 2;
        bar.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        cbNhanVien.setPreferredSize(new Dimension(220, 32));
        bar.add(cbNhanVien, gbc);
        gbc.gridx = 2;
        JPanel panelLoaiVe = new JPanel(new GridLayout(1, 2, 6, 0));
        panelLoaiVe.setOpaque(false);
        panelLoaiVe.add(new JLabel("Loại vé (Hạng toa):"));
        cbLoaiVe.setPreferredSize(new Dimension(220, 32));
        panelLoaiVe.add(cbLoaiVe);
        bar.add(panelLoaiVe, gbc);

        // ===== Hàng 3: Nút Tìm kiếm (ở góc dưới bên trái) =====
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 5, 5, 5);

        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.setFont(new Font("Arial", Font.PLAIN, 13));
        btnTimKiem.setBackground(new Color(33, 150, 83));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTimKiem.putClientProperty("JButton.buttonType", "tool");
        btnTimKiem.putClientProperty("JComponent.minimumHeight", 30);
        btnTimKiem.setPreferredSize(new Dimension(120, 32));
        bar.add(btnTimKiem, gbc);

        // ===== Sự kiện =====
        cbLoaiThoiGian.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CardLayout cl = (CardLayout) filterSwitcher.getLayout();
                switch ((String) e.getItem()) {
                    case "Theo ngày" -> cl.show(filterSwitcher, CARD_NGAY);
                    case "Theo tháng" -> cl.show(filterSwitcher, CARD_THANG);
                    case "Theo năm" -> cl.show(filterSwitcher, CARD_NAM);
                    default -> cl.show(filterSwitcher, CARD_TATCA);
                }
            }
        });

        cbLoaiTuyen.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean enable = e.getItem().equals("Theo Ga đi/đến");
                cbGaDi.setEnabled(enable);
                cbGaDen.setEnabled(enable);
            }
        });

        btnTimKiem.addActionListener(e -> xuLyThongKe());

        ((CardLayout) filterSwitcher.getLayout()).show(filterSwitcher, CARD_TATCA);
        return bar;
    }


    // ===== loadComboBoxesData() =====
    private void loadComboBoxesData() {
        // Tải Ga
        try {
            List<String> tenGaList = thongKeVeDAO.getDanhSachTenGa();
            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();

            if (tenGaList == null || tenGaList.isEmpty()) {
                cbGaDi.addItem("Lỗi"); cbGaDen.addItem("Lỗi");
            } else {
                for (String tenGa : tenGaList) {
                    cbGaDi.addItem(tenGa);
                    cbGaDen.addItem(tenGa);
                }
            }
            // Giá trị mặc định quen dùng
            cbGaDi.setSelectedItem("Sài Gòn");
            cbGaDen.setSelectedItem("Hà Nội");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi khi tải danh sách ga", e);
            cbGaDi.addItem("Lỗi CSDL"); cbGaDen.addItem("Lỗi CSDL");
        }

        // Tải Nhân Viên
        try {
            Map<String, String> dsNhanVien = thongKeVeDAO.getDanhSachNhanVien();
            nhanVienMap.clear();
            cbNhanVien.removeAllItems();
            cbNhanVien.addItem("Tất cả");

            if (dsNhanVien != null) {
                for (Map.Entry<String, String> entry : dsNhanVien.entrySet()) {
                    String id = entry.getKey();
                    String ten = entry.getValue();
                    String display = ten + " (" + id + ")";
                    nhanVienMap.put(display, id);
                    cbNhanVien.addItem(display);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi khi tải danh sách nhân viên", e);
            cbNhanVien.addItem("Lỗi CSDL");
        }

        // Tải Loại Vé (Hạng toa)
        try {
            Map<String, String> dsHangToa = thongKeVeDAO.getDanhSachLoaiVe();
            hangToaMap.clear();
            cbLoaiVe.removeAllItems();
            cbLoaiVe.addItem("Tất cả");

            if (dsHangToa != null) {
                for (Map.Entry<String, String> entry : dsHangToa.entrySet()) {
                    String id = entry.getKey();
                    String moTa = entry.getValue();
                    hangToaMap.put(moTa, id);
                    cbLoaiVe.addItem(moTa);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Lỗi khi tải danh sách loại vé", e);
            cbLoaiVe.addItem("Lỗi CSDL");
        }
    }

    // ===== create helpers =====
    private JLabel createValueLabel(String initialText) {
        JLabel label = new JLabel(initialText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Times New Roman", Font.BOLD, 16));
        return label;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel taoPanelTongQuan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        JLabel placeholder = new JLabel("📊 Chọn bộ lọc và nhấn Tìm kiếm", SwingConstants.CENTER);
        placeholder.setFont(new Font("Times New Roman", Font.ITALIC, 16));
        placeholder.setForeground(Color.GRAY);
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelChiTiet(JLabel titleLabel, JTable table, JPanel bottomPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        panel.add(titleLabel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ===== setupTableDetails() =====
    private void setupTableDetails() {
        tableChiTiet.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(28);
        JTableHeader header = tableChiTiet.getTableHeader();
        header.setFont(new Font("Times New Roman", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        tableChiTiet.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormatter.format(value);
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        DefaultTableCellRenderer integerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                if (value instanceof Number) {
                    value = integerFormatter.format(value);
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        TableColumnModel columnModel = tableChiTiet.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);

        columnModel.getColumn(1).setPreferredWidth(110);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);

        for (int i = 2; i <= 6; i++) { // Vé bán, HL, Dùng, Đổi, Hoàn
            columnModel.getColumn(i).setPreferredWidth(90);
            columnModel.getColumn(i).setCellRenderer(integerRenderer);
        }

        columnModel.getColumn(7).setPreferredWidth(180); // Tuyến đường
        columnModel.getColumn(7).setCellRenderer(leftRenderer);

        columnModel.getColumn(8).setPreferredWidth(140); // Tổng tiền vé
        columnModel.getColumn(8).setCellRenderer(currencyRenderer);
    }

    // ===== xuLyThongKe() =====
    private void xuLyThongKe() {
        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        if (loaiThoiGian == null) loaiThoiGian = "Tất cả";

        String loaiTuyen = (String) cbLoaiTuyen.getSelectedItem();
        String tenGaDi = (String) cbGaDi.getSelectedItem();
        String tenGaDen = (String) cbGaDen.getSelectedItem();

        // Bật/tắt lọc tuyến
        if ("Tất cả".equals(loaiTuyen)) {
            tenGaDi = null;
            tenGaDen = null;
        } else {
            if (tenGaDi == null || tenGaDen == null || tenGaDi.contains("Lỗi") || tenGaDen.contains("Lỗi")) {
                JOptionPane.showMessageDialog(this, "Không thể lọc vì danh sách ga bị lỗi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Lấy nhân viên & loại vé
        String selectedNhanVien = (String) cbNhanVien.getSelectedItem();
        String nhanVienID = (selectedNhanVien == null || "Tất cả".equals(selectedNhanVien))
                ? null : nhanVienMap.get(selectedNhanVien);

        String selectedLoaiVe = (String) cbLoaiVe.getSelectedItem();
        String hangToaID = (selectedLoaiVe == null || "Tất cả".equals(selectedLoaiVe))
                ? null : hangToaMap.get(selectedLoaiVe);

        LocalDate fromLocalDate, toLocalDate;
        String titleLoai, titleChart;

        try {
            switch (loaiThoiGian) {
                case "Theo ngày" -> {
                    Date utilFrom = tuNgay.getDate();
                    Date utilTo = denNgay.getDate();
                    if (!kiemTraKhoangNgayHopLe(utilFrom, utilTo)) return;
                    fromLocalDate = utilFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    toLocalDate = utilTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    titleLoai = "ngày (từ " + fmtD(fromLocalDate) + " đến " + fmtD(toLocalDate) + ")";
                    titleChart = "ngày";
                }
                case "Theo tháng" -> {
                    int fm = cbTuThang.getSelectedIndex() + 1; int fy = nvl((Integer) cbTuNamThang.getSelectedItem(), LocalDate.now().getYear());
                    int tm = cbDenThang.getSelectedIndex() + 1; int ty = nvl((Integer) cbDenNamThang.getSelectedItem(), LocalDate.now().getYear());
                    fromLocalDate = LocalDate.of(fy, fm, 1);
                    toLocalDate = LocalDate.of(ty, tm, LocalDate.of(ty, tm, 1).lengthOfMonth());
                    if (toLocalDate.isBefore(fromLocalDate)) {
                        JOptionPane.showMessageDialog(this, "⚠️ Thời điểm kết thúc phải ≥ thời điểm bắt đầu (tháng/năm).", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    titleLoai = "tháng (từ " + fm + "/" + fy + " đến " + tm + "/" + ty + ")";
                    titleChart = "tháng";
                }
                case "Theo năm" -> {
                    int sy = nvl((Integer) cbTuNam.getSelectedItem(), LocalDate.now().getYear());
                    int ey = nvl((Integer) cbDenNam.getSelectedItem(), LocalDate.now().getYear());
                    fromLocalDate = LocalDate.of(sy, 1, 1);
                    toLocalDate = LocalDate.of(ey, 12, 31);
                    if (toLocalDate.isBefore(fromLocalDate)) {
                        JOptionPane.showMessageDialog(this, "⚠️ Năm kết thúc phải ≥ năm bắt đầu.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    titleLoai = "năm (từ " + sy + " đến " + ey + ")";
                    titleChart = "năm";
                }
                default -> { // Tất cả
                    fromLocalDate = LocalDate.of(2000, 1, 1);
                    toLocalDate = LocalDate.now().plusDays(1);
                    titleLoai = "tất cả (thống kê theo năm)";
                    titleChart = "Tất cả (theo năm)";
                    loaiThoiGian = "Theo năm"; // ép kiểu hiển thị theo năm
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thông tin lọc thời gian: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin lọc thời gian", ex);
            return;
        }

        // Loading UI
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongSoVeBanValue.setText("...");
        lblTongVeConHieuLucValue.setText("...");
        lblTongVeDaDungValue.setText("...");
        lblTongVeDaDoiValue.setText("...");
        lblTongVeHoanValue.setText("...");
        lblTongTienVeValue.setText("...");
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        final LocalDate finalFrom = fromLocalDate;
        final LocalDate finalTo = toLocalDate;
        final String finalDaoLoai = loaiThoiGian;
        final String finalTitleLoai = titleLoai;
        final String finalChartTitle = titleChart;
        final String finalLoaiTuyen = loaiTuyen;
        final String finalTenGaDi = tenGaDi;
        final String finalTenGaDen = tenGaDen;
        final String finalNhanVienID = nhanVienID;
        final String finalHangToaID = hangToaID;

        SwingWorker<ThongKeVeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeVeResult doInBackground() throws Exception {
                ThongKeVeResult result = new ThongKeVeResult();

                // Lưu ý: vì đã bỏ "Trạng thái vé", truyền null cho tham số trạng thái (giữ tương thích DAO)
                result.thongKeVeChiTietTheoThoiGian = thongKeVeDAO.getThongKeVeChiTietTheoThoiGian(
                        finalDaoLoai, finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen,
                        finalNhanVienID, finalHangToaID, null);

                result.tongSoVeBan = thongKeVeDAO.getTongSoVeBanTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                result.tongVeConHieuLuc = thongKeVeDAO.getTongVeConHieuLucTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                result.tongVeDaDung = thongKeVeDAO.getTongVeDaDungTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                result.tongVeDaDoi = thongKeVeDAO.getTongVeDaDoiTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                result.tongVeHoan = thongKeVeDAO.getTongVeHoanTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                result.tongTienVe = thongKeVeDAO.getTongTienVeTrongKhoang(
                        finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen, finalNhanVienID, finalHangToaID, null);

                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeVeResult result = get();

                    lblTongSoVeBanValue.setText(integerFormatter.format(result.tongSoVeBan));
                    lblTongVeConHieuLucValue.setText(integerFormatter.format(result.tongVeConHieuLuc));
                    lblTongVeDaDungValue.setText(integerFormatter.format(result.tongVeDaDung));
                    lblTongVeDaDoiValue.setText(integerFormatter.format(result.tongVeDaDoi));
                    lblTongVeHoanValue.setText(integerFormatter.format(result.tongVeHoan));
                    lblTongTienVeValue.setText(currencyFormatter.format(result.tongTienVe));

                    capNhatChartVaTable(result.thongKeVeChiTietTheoThoiGian, finalChartTitle, finalTitleLoai);
                } catch (InterruptedException | ExecutionException ex) {
                    handleLoadingError(ex, "Lỗi khi thực hiện thống kê");
                } catch (Exception ex) {
                    handleLoadingError(ex, "Lỗi không xác định khi cập nhật UI");
                }
            }
        };
        worker.execute();
    }

    // ===== capNhatChartVaTable() =====
    private void capNhatChartVaTable(Map<String, ThongKeVeChiTietItem> data, String chartTitle, String reportTitle) {
        lblChiTietTitle.setText("Báo cáo thống kê vé chi tiết theo " + reportTitle.toLowerCase());

        if (data == null || data.isEmpty()) {
            capNhatChartRong("📉 Không có dữ liệu vé trong khoảng đã chọn");
            capNhatBangRong();
            btnExportExcel.setEnabled(false);
            return;
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final String seriesVeBan = "Vé bán";
        final String seriesVeHoanDoi = "Vé hoàn/đổi";

        data.forEach((thoiGian, item) -> {
            dataset.addValue(item.tongSoVeBan, seriesVeBan, thoiGian);
            dataset.addValue(item.tongVeHoan + item.tongVeDaDoi, seriesVeHoanDoi, thoiGian);
        });

        JFreeChart barChart = ChartFactory.createBarChart(
                "Số lượng vé theo " + chartTitle.toLowerCase(),
                "Thời gian", "Số lượng vé", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setOutlineVisible(false);
        plot.setInsets(new RectangleInsets(10, 5, 5, 10));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setUpperMargin(0.15);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.2);
        renderer.setShadowVisible(false);
        renderer.setMaximumBarWidth(0.08);

        // Màu mặc định (giữ nhẹ nhàng, có thể đổi theo theme)
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setSeriesPaint(1, new Color(231, 76, 60));

        CategoryAxis domainAxis = plot.getDomainAxis();
        if (!chartTitle.toLowerCase().contains("năm") && dataset.getColumnCount() > 8) {
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(10f));
        } else {
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
            domainAxis.setTickLabelFont(domainAxis.getTickLabelFont().deriveFont(11f));
        }
        domainAxis.setLowerMargin(0.02);
        domainAxis.setUpperMargin(0.02);

        if (barChart.getLegend() != null) {
            barChart.getLegend().setFrame(BlockBorder.NONE);
        }
        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setFont(new Font("Times New Roman", Font.BOLD, 16));

        ChartPanel chartDisplayPanel = new ChartPanel(barChart);
        chartDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chartDisplayPanel.setBackground(Color.WHITE);

        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartDisplayPanel, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();

        // Bảng chi tiết
        chiTietTableModel.setRowCount(0);
        int stt = 1;
        for (Map.Entry<String, ThongKeVeChiTietItem> entry : data.entrySet()) {
            String thoiGian = entry.getKey();
            ThongKeVeChiTietItem item = entry.getValue();
            chiTietTableModel.addRow(new Object[]{
                    stt++,
                    thoiGian,
                    item.tongSoVeBan,
                    item.tongVeConHieuLuc,
                    item.tongVeDaDung,
                    item.tongVeDaDoi,
                    item.tongVeHoan,
                    item.tuyenDuong,
                    item.tongTienVe
            });
        }
        btnExportExcel.setEnabled(true);
    }

    // ===== Export Excel =====
    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo Excel");
        String defaultFileName = "BaoCaoThongKeVe_" + LocalDate.now() + ".xlsx";
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        final File finalFileToSave = fileToSave;

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(finalFileToSave)) {

            Sheet sheet = workbook.createSheet("ChiTietThongKeVe");

            // Styles
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            CellStyle titleCellStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleCellStyle.setFont(titleFont);
            titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CreationHelper createHelper = workbook.getCreationHelper();
            CellStyle currencyCellStyle = workbook.createCellStyle();
            currencyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0 ₫"));
            currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle integerCellStyle = workbook.createCellStyle();
            integerCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
            integerCellStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle centerCellStyle = workbook.createCellStyle();
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle leftCellStyle = workbook.createCellStyle();
            leftCellStyle.setAlignment(HorizontalAlignment.LEFT);

            // Tiêu đề
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(lblChiTietTitle.getText());
            titleCell.setCellStyle(titleCellStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, chiTietTableModel.getColumnCount() - 1));

            // Header
            Row headerRow = sheet.createRow(2);
            for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                String name = chiTietTableModel.getColumnName(col);
                if (name != null) name = name.replace(" (VNĐ)", "");
                cell.setCellValue(name);
                cell.setCellStyle(headerCellStyle);
            }

            // Data
            for (int row = 0; row < chiTietTableModel.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 3);
                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object value = chiTietTableModel.getValueAt(row, col);

                    try {
                        if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                            cell.setCellStyle((col == 0) ? centerCellStyle : integerCellStyle);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                            cell.setCellStyle(currencyCellStyle);
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                            // cột 1 (Thời gian) căn giữa, tuyến đường căn trái
                            if (col == 1) cell.setCellStyle(centerCellStyle); else cell.setCellStyle(leftCellStyle);
                        } else if (value != null) {
                            cell.setCellValue(value.toString());
                        }
                    } catch (Exception ex) {
                        LOGGER.log(Level.WARNING, "Lỗi khi ghi dữ liệu Excel ô [" + row + "," + col + "]", ex);
                        cell.setCellValue("Lỗi dữ liệu");
                    }
                }
            }

            for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(outputStream);

            JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!\n" + finalFileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(finalFileToSave);
            }
        } catch (Exception ex) {
            handleLoadingError(ex, "Lỗi khi ghi file Excel");
        }
    }

    // ===== Helpers & Error handling =====
    private void handleLoadingError(Exception ex, String context) {
        setCursor(Cursor.getDefaultCursor());
        LOGGER.log(Level.SEVERE, context, ex);
        Throwable cause = (ex instanceof ExecutionException) ? ex.getCause() : ex;
        if (cause == null) cause = ex;

        capNhatChartRong("⚠️ Lỗi khi tải dữ liệu: " + cause.getMessage());
        capNhatBangRong();
        JOptionPane.showMessageDialog(this, context + ":\n" + cause.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);

        lblTongSoVeBanValue.setText("Lỗi");
        lblTongVeConHieuLucValue.setText("Lỗi");
        lblTongVeDaDungValue.setText("Lỗi");
        lblTongVeDaDoiValue.setText("Lỗi");
        lblTongVeHoanValue.setText("Lỗi");
        lblTongTienVeValue.setText("Lỗi");
    }

    private boolean kiemTraKhoangNgayHopLe(Date from, Date to) {
        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn đủ ngày bắt đầu và ngày kết thúc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!isSameDay(from, to) && to.before(from)) {
            JOptionPane.showMessageDialog(this, "⚠️ Ngày kết thúc phải ≥ ngày bắt đầu.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void capNhatChartRong(String msg) {
        SwingUtilities.invokeLater(() -> {
            chartPanelContainer.removeAll();
            JLabel l = new JLabel(msg, SwingConstants.CENTER);
            l.setFont(new Font("Times New Roman", Font.ITALIC, 16));
            l.setForeground(Color.GRAY);
            chartPanelContainer.add(l, BorderLayout.CENTER);
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();
        });
    }

    private void capNhatBangRong() {
        SwingUtilities.invokeLater(() -> chiTietTableModel.setRowCount(0));
    }

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        Calendar cal1 = Calendar.getInstance(); cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private String fmtD(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private int nvl(Integer v, int def) {
        return v == null ? def : v;
    }

    // Card content for filter switcher
    private JPanel buildTatCaFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel("Hiển thị dữ liệu theo năm");
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 14));
        lbl.setForeground(Color.GRAY);
        p.add(lbl);
        p.setPreferredSize(new Dimension(300, 30));
        return p;
    }

    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        tuNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setPreferredSize(new Dimension(130, 28));
        denNgay.setPreferredSize(new Dimension(130, 28));
        tuNgay.setDate(new Date());
        denNgay.setDate(new Date());
        addDateConstraint(tuNgay, denNgay);
        p.add(new JLabel("Từ:"));
        p.add(tuNgay);
        p.add(new JLabel("Đến:"));
        p.add(denNgay);
        return p;
    }

    private JPanel buildThangFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        if (cbTuThang.getItemCount() == 0) {
            for (int i = 1; i <= 12; i++) {
                cbTuThang.addItem("Tháng " + i);
                cbDenThang.addItem("Tháng " + i);
            }
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= currentYear + 5; y++) {
                cbTuNamThang.addItem(y);
                cbDenNamThang.addItem(y);
            }
            Calendar now = Calendar.getInstance();
            cbTuThang.setSelectedIndex(now.get(Calendar.MONTH));
            cbDenThang.setSelectedIndex(now.get(Calendar.MONTH));
            cbTuNamThang.setSelectedItem(now.get(Calendar.YEAR));
            cbDenNamThang.setSelectedItem(now.get(Calendar.YEAR));
        }
        Dimension monthDim = new Dimension(90, 28);
        Dimension yearDim = new Dimension(75, 28);
        cbTuThang.setPreferredSize(monthDim);
        cbDenThang.setPreferredSize(monthDim);
        cbTuNamThang.setPreferredSize(yearDim);
        cbDenNamThang.setPreferredSize(yearDim);

        p.add(new JLabel("Từ:"));
        p.add(cbTuThang);
        p.add(cbTuNamThang);
        p.add(new JLabel("Đến:"));
        p.add(cbDenThang);
        p.add(cbDenNamThang);
        return p;
    }

    private JPanel buildNamFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        if (cbTuNam.getItemCount() == 0) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= currentYear + 5; y++) {
                cbTuNam.addItem(y);
                cbDenNam.addItem(y);
            }
            cbTuNam.setSelectedItem(currentYear);
            cbDenNam.setSelectedItem(currentYear);
        }
        Dimension yearDim = new Dimension(90, 28);
        cbTuNam.setPreferredSize(yearDim);
        cbDenNam.setPreferredSize(yearDim);

        p.add(new JLabel("Từ năm:"));
        p.add(cbTuNam);
        p.add(new JLabel("Đến năm:"));
        p.add(cbDenNam);
        return p;
    }

    // Ràng buộc ngày
    private void addDateConstraint(JDateChooser from, JDateChooser to) {
        PropertyChangeListener sync = new PropertyChangeListener() {
            private boolean adjusting = false;
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (!"date".equals(evt.getPropertyName()) || adjusting) return;
                Date f = from.getDate();
                Date t = to.getDate();
                if (f == null || t == null) return;

                if (!isSameDay(f, t) && t.before(f)) {
                    adjusting = true;
                    to.setDate(f);
                    adjusting = false;
                }
            }
        };
        from.addPropertyChangeListener("date", sync);
        to.addPropertyChangeListener("date", sync);
    }
}
