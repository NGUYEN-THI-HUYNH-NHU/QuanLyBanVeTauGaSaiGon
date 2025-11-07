package gui.application.form.thongKe;

import dao.ThongKeVe_DAO;
import dao.ThongKeVe_DAO.ThongKeVeChiTietItem;
// Import JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
// Import Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
// org.apache.poi.ss.usermodel.Font đã được bạn xử lý ở bước trước
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Import Swing, AWT, Calendar
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
// Import IO và Util
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Panel hiển thị thống kê vé theo thời gian.
 * ĐÃ NÂNG CẤP: Thêm bộ lọc Tuyến (Tất cả / Theo Ga đi - Ga đến).
 */
public class PanelThongKeVe extends JPanel {

    // ===== DAO =====
    private final ThongKeVe_DAO thongKeVeDAO;

    // Logger
    private static final Logger LOGGER = Logger.getLogger(PanelThongKeVe.class.getName());

    // ===== Định dạng =====
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");

    // ===== Các thành phần lọc =====
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

    // <<< THÀNH PHẦN LỌC TUYẾN MỚI >>>
    private final JComboBox<String> cbLoaiTuyen;
    private final JComboBox<String> cbGaDi;
    private final JComboBox<String> cbGaDen;
    private final JPanel panelGaDiDen; // Panel chứa 2 cb ga

    private final JButton btnTimKiem;

    // ===== Các JLabel chứa giá trị trên card =====
    private final JLabel lblTongSoVeBanValue;
    private final JLabel lblTongVeConHieuLucValue;
    private final JLabel lblTongVeDaDungValue;
    private final JLabel lblTongVeDaDoiValue;
    private final JLabel lblTongVeHoanValue;
    private final JLabel lblTongTienVeValue;

    // ===== Biểu đồ & Bảng =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;

    // ===== Thành phần Panel Chi Tiết =====
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    // ===== Lớp chứa kết quả thống kê =====
    private static class ThongKeVeResult {
        int tongSoVeBan, tongVeConHieuLuc, tongVeDaDung, tongVeDaDoi, tongVeHoan;
        double tongTienVe;
        Map<String, ThongKeVeChiTietItem> thongKeVeChiTietTheoThoiGian;
    }

    public PanelThongKeVe() {
        this.thongKeVeDAO = new ThongKeVe_DAO();

        // Khởi tạo components thời gian
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

        // <<< KHỞI TẠO COMPONENTS TUYẾN MỚI >>>
        cbLoaiTuyen = new JComboBox<>(new String[]{"Tất cả", "Theo Ga đi/đến"});
        cbGaDi = new JComboBox<>();
        cbGaDen = new JComboBox<>();
        panelGaDiDen = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Panel chứa 2 cb ga

        btnTimKiem = new JButton("Tìm kiếm");

        // Khởi tạo labels
        lblTongSoVeBanValue = createValueLabel("...");
        lblTongVeConHieuLucValue = createValueLabel("...");
        lblTongVeDaDungValue = createValueLabel("...");
        lblTongVeDaDoiValue = createValueLabel("...");
        lblTongVeHoanValue = createValueLabel("...");
        lblTongTienVeValue = createValueLabel("...");

        // --- Cấu hình Layout chính ---
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        // Bước 1: Tạo đường viền bên ngoài (LineBorder) CÓ TIÊU ĐỀ

        javax.swing.border.Border titledLineBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true), // Border BẠN MUỐN DÙNG
                "THỐNG KÊ VÉ", // Tiêu đề
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(0, 110, 185)
        );

        // Bước 2: Tạo padding (khoảng đệm) bên trong
        javax.swing.border.Border padding = BorderFactory.createEmptyBorder(5, 5, 5, 5);

        // Bước 3: Gộp 2 border lại. (TitledBorder bên ngoài, padding bên trong)
        setBorder(BorderFactory.createCompoundBorder(
                titledLineBorder,
                padding
        ));

        // ===== Khu vực NORTH: Thanh lọc và Cards =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // --- Thanh lọc ---
        topPanel.add(buildFilterBar(), BorderLayout.NORTH);

        // --- Cards tổng quan ---
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


        // ===== Khu vực CENTER: Tabs biểu đồ & chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        // --- Tạo Table Model (THÊM CỘT GA ĐI, GA ĐẾN) ---
        String[] columnNames = {
                "STT", "Thời Gian", "Vé bán", "Vé còn hiệu lực", "Vé đã dùng",
                "Vé đổi", "Vé hoàn", "Tuyến đường", "Tổng tiền vé (VNĐ)"
        };
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 3, 4, 5, 6 -> Integer.class;
                    case 8 -> Double.class; // Cột tiền là 8
                    default -> String.class; // Thời gian, Tuyến
                };
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails();

        // --- Tạo Panel Chi Tiết ---
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

        // ===== Load dữ liệu ban đầu =====
        loadComboBoxesData(); // TẢI DỮ LIỆU CHO COMBOBOX GA
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    /**
     * Xây dựng panel lọc, sử dụng GridBagLayout.
     */
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel();
        bar.setOpaque(false);
        bar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // === Hàng 0: Lọc thời gian ===
        gbc.gridx = 0; gbc.gridy = 0;
        bar.add(new JLabel("Loại thời gian:"), gbc);

        gbc.gridx = 1;
        cbLoaiThoiGian.setPreferredSize(new Dimension(100, 30));
        bar.add(cbLoaiThoiGian, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        filterSwitcher.setOpaque(false);
        filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);
        filterSwitcher.setPreferredSize(new Dimension(450, 30));
        bar.add(filterSwitcher, gbc);

        // === Nút Tìm kiếm (Nằm bên phải, cao 2 hàng) ===
        btnTimKiem.setBackground(new Color(103, 194, 103));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Arial", Font.BOLD, 14));
        btnTimKiem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTimKiem.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 3; gbc.gridy = 0; // Cột 3, Hàng 0
        gbc.gridheight = 2; // NÚT CAO 2 HÀNG
        gbc.fill = GridBagConstraints.VERTICAL; // Lấp đầy theo chiều dọc
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 15, 5, 5);
        bar.add(btnTimKiem, gbc);

        // Reset
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        // === Hàng 1: Lọc Tuyến (MỚI) ===
        gbc.gridx = 0; gbc.gridy = 1;
        bar.add(new JLabel("Lọc tuyến:"), gbc);

        gbc.gridx = 1;
        cbLoaiTuyen.setPreferredSize(new Dimension(100, 30));
        bar.add(cbLoaiTuyen, gbc);

        // Panel con cho Ga đi / Ga đến
        panelGaDiDen.setOpaque(false);
        panelGaDiDen.add(new JLabel("Ga đi:"));
        cbGaDi.setPreferredSize(new Dimension(150, 30));
        panelGaDiDen.add(cbGaDi);
        panelGaDiDen.add(new JLabel("Ga đến:"));
        cbGaDen.setPreferredSize(new Dimension(150, 30));
        panelGaDiDen.add(cbGaDen);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bar.add(panelGaDiDen, gbc);

        // === Gắn sự kiện ===
        cbLoaiThoiGian.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String item = (String) e.getItem();
                CardLayout cl = (CardLayout) filterSwitcher.getLayout();

                switch (item) {
                    case "Theo ngày":
                        cl.show(filterSwitcher, CARD_NGAY);
                        break;
                    case "Theo tháng":
                        cl.show(filterSwitcher, CARD_THANG);
                        break;
                    case "Theo năm":
                        cl.show(filterSwitcher, CARD_NAM);
                        break;
                    case "Tất cả":
                    default:
                        cl.show(filterSwitcher, CARD_TATCA);
                        break;
                }
            }
        });

        cbLoaiTuyen.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean enableGaFilter = e.getItem().equals("Theo Ga đi/đến");
                cbGaDi.setEnabled(enableGaFilter);
                cbGaDen.setEnabled(enableGaFilter);
            }
        });

        btnTimKiem.addActionListener(e -> xuLyThongKe());

        // Cài đặt trạng thái ban đầu
        cbGaDi.setEnabled(false);
        cbGaDen.setEnabled(false);
        ((CardLayout) filterSwitcher.getLayout()).show(filterSwitcher, CARD_TATCA);

        return bar;
    }

    /**
     * Tải dữ liệu cho các JComboBox (ví dụ: danh sách ga) từ DAO.
     */
    private void loadComboBoxesData() {
        try {
            List<String> tenGaList = thongKeVeDAO.getDanhSachTenGa();

            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();

            //cbGaDi.addItem("Tất cả"); // Giao diện sẽ tự thêm "Tất cả"
            //cbGaDen.addItem("Tất cả");

            if (tenGaList.isEmpty()) {
                LOGGER.warning("Không tìm thấy ga nào trong CSDL.");
            } else {
                for (String tenGa : tenGaList) {
                    cbGaDi.addItem(tenGa);
                    cbGaDen.addItem(tenGa);
                }
            }
            // Đặt ga đi và ga đến mặc định
            cbGaDi.setSelectedItem("Sài Gòn");
            cbGaDen.setSelectedItem("Hà Nội");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải danh sách ga", e);
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách ga: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            cbGaDi.addItem("Lỗi CSDL");
            cbGaDen.addItem("Lỗi CSDL");
        }
    }


    // ===== Hàm tạo JLabel cho giá trị trên card =====
    private JLabel createValueLabel(String initialText) {
        JLabel label = new JLabel(initialText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Times New Roman", Font.BOLD, 16));
        return label;
    }

    // ===== Hàm tạo Card =====
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

    // --- Hàm cấu hình JTable (Đã cập nhật) ---
    private void setupTableDetails() {
        tableChiTiet.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(28);
        JTableHeader header = tableChiTiet.getTableHeader();
        header.setFont(new Font("Times New Roman", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        tableChiTiet.setGridColor(new Color(220, 220, 220));
        tableChiTiet.setSelectionBackground(new Color(184, 207, 229));
        tableChiTiet.setSelectionForeground(Color.BLACK);
        tableChiTiet.setShowGrid(true);
        tableChiTiet.setShowVerticalLines(true);
        tableChiTiet.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer(); // <<< THÊM MỚI
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    DecimalFormat df = new DecimalFormat("#,##0 ₫");
                    value = df.format(value);
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        DefaultTableCellRenderer integerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = integerFormatter.format(value);
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };


        TableColumnModel columnModel = tableChiTiet.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40); // STT
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        columnModel.getColumn(1).setPreferredWidth(100); // Thời gian
        columnModel.getColumn(1).setCellRenderer(centerRenderer);

        for (int i = 2; i <= 6; i++) { // Vé bán, HL, Dùng, Đổi, Hoàn
            columnModel.getColumn(i).setPreferredWidth(90);
            columnModel.getColumn(i).setCellRenderer(integerRenderer);
        }

        columnModel.getColumn(7).setPreferredWidth(150); // <<< CỘT TUYẾN ĐƯỜNG MỚI
        columnModel.getColumn(7).setCellRenderer(leftRenderer); // Căn trái cho dễ đọc

        columnModel.getColumn(8).setPreferredWidth(140); // Tổng tiền vé (index 8)
        columnModel.getColumn(8).setCellRenderer(currencyRenderer);
    }


    // ================== HÀM XỬ LÝ CHÍNH ==================

    private void xuLyThongKe() {
        // --- 1. LẤY GIÁ TRỊ TỪ CÁC BỘ LỌC ---

        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        if (loaiThoiGian == null) loaiThoiGian = "Tất cả";

        String loaiTuyen = (String) cbLoaiTuyen.getSelectedItem();
        String tenGaDi = (String) cbGaDi.getSelectedItem();
        String tenGaDen = (String) cbGaDen.getSelectedItem();

        // <<< SỬA LOGIC LẤY GA >>>
        if (loaiTuyen.equals("Tất cả")) {
            tenGaDi = null; // Gửi null nếu chọn "Tất cả"
            tenGaDen = null;
        } else { // "Theo Ga đi/đến"
            if (tenGaDi == null || tenGaDen == null || tenGaDi.contains("Lỗi") || tenGaDen.contains("Không")) {
                JOptionPane.showMessageDialog(this, "Không thể lọc vì danh sách ga bị lỗi.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        LocalDate fromLocalDate, toLocalDate;
        String titleLoai = loaiThoiGian;
        String titleChart = loaiThoiGian;

        // 2. Xử lý khoảng thời gian
        try {
            switch (loaiThoiGian) {
                case "Theo ngày":
                    Date utilFromDateNgay = tuNgay.getDate();
                    Date utilToDateNgay = denNgay.getDate();
                    if (!kiemTraKhoangNgayHopLe(utilFromDateNgay, utilToDateNgay)) return;
                    fromLocalDate = utilFromDateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    toLocalDate = utilToDateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    titleLoai = String.format("ngày (từ %s đến %s)",
                            fromLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            toLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    titleChart = "ngày";
                    break;
                case "Theo tháng":
                    int fm = cbTuThang.getSelectedIndex() + 1; int fy = (Integer) cbTuNamThang.getSelectedItem();
                    int tm = cbDenThang.getSelectedIndex() + 1; int ty = (Integer) cbDenNamThang.getSelectedItem();
                    fromLocalDate = LocalDate.of(fy, fm, 1);
                    toLocalDate = LocalDate.of(ty, tm, LocalDate.of(ty, tm, 1).lengthOfMonth());
                    if (toLocalDate.isBefore(fromLocalDate)) {
                        JOptionPane.showMessageDialog(this, "⚠️ Thời điểm kết thúc phải ≥ thời điểm bắt đầu (tháng/năm).", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    titleLoai = String.format("tháng (từ %d/%d đến %d/%d)", fm, fy, tm, ty);
                    titleChart = "tháng";
                    break;
                case "Theo năm":
                    int startYear = (Integer) cbTuNam.getSelectedItem(); int endYear = (Integer) cbDenNam.getSelectedItem();
                    fromLocalDate = LocalDate.of(startYear, 1, 1);
                    toLocalDate = LocalDate.of(endYear, 12, 31);
                    if (toLocalDate.isBefore(fromLocalDate)) {
                        JOptionPane.showMessageDialog(this, "⚠️ Năm kết thúc phải ≥ năm bắt đầu.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    titleLoai = String.format("năm (từ %d đến %d)", startYear, endYear);
                    titleChart = "năm";
                    break;
                default: // "Tất cả"
                    fromLocalDate = LocalDate.of(2000, 1, 1);
                    toLocalDate = LocalDate.now().plusDays(1); // Để lấy cả ngày hôm nay
                    titleLoai = "tất cả (thống kê theo năm)";
                    titleChart = "Tất cả (theo năm)";
                    loaiThoiGian = "Theo năm"; // Dùng "Theo năm" cho DAO khi chọn "Tất cả"
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thông tin lọc thời gian: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin lọc thời gian", ex);
            return;
        }

        // --- 3. Hiển thị loading ---
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongSoVeBanValue.setText("...");
        lblTongVeConHieuLucValue.setText("...");
        lblTongVeDaDungValue.setText("...");
        lblTongVeDaDoiValue.setText("...");
        lblTongVeHoanValue.setText("...");
        lblTongTienVeValue.setText("...");
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        // 4. Tạo và thực thi SwingWorker
        final LocalDate finalFrom = fromLocalDate;
        final LocalDate finalTo = toLocalDate;
        final String finalDaoLoai = loaiThoiGian;
        final String finalTitleLoai = titleLoai;
        final String finalChartTitle = titleChart;
        final String finalLoaiTuyen = loaiTuyen;
        final String finalTenGaDi = tenGaDi;
        final String finalTenGaDen = tenGaDen;

        SwingWorker<ThongKeVeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeVeResult doInBackground() throws Exception {
                ThongKeVeResult result = new ThongKeVeResult();

                result.thongKeVeChiTietTheoThoiGian = thongKeVeDAO.getThongKeVeChiTietTheoThoiGian(
                        finalDaoLoai, finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);

                result.tongSoVeBan = thongKeVeDAO.getTongSoVeBanTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);
                result.tongVeConHieuLuc = thongKeVeDAO.getTongVeConHieuLucTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);
                result.tongVeDaDung = thongKeVeDAO.getTongVeDaDungTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);
                result.tongVeDaDoi = thongKeVeDAO.getTongVeDaDoiTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);
                result.tongVeHoan = thongKeVeDAO.getTongVeHoanTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);
                result.tongTienVe = thongKeVeDAO.getTongTienVeTrongKhoang(finalFrom, finalTo, finalLoaiTuyen, finalTenGaDi, finalTenGaDen);

                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeVeResult result = get();
                    // CẬP NHẬT CARD
                    lblTongSoVeBanValue.setText(integerFormatter.format(result.tongSoVeBan));
                    lblTongVeConHieuLucValue.setText(integerFormatter.format(result.tongVeConHieuLuc));
                    lblTongVeDaDungValue.setText(integerFormatter.format(result.tongVeDaDung));
                    lblTongVeDaDoiValue.setText(integerFormatter.format(result.tongVeDaDoi));
                    lblTongVeHoanValue.setText(integerFormatter.format(result.tongVeHoan));
                    lblTongTienVeValue.setText(currencyFormatter.format(result.tongTienVe));

                    // CẬP NHẬT BIỂU ĐỒ VÀ BẢNG CHI TIẾT
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

    /**
     * Cập nhật cả biểu đồ và bảng chi tiết dựa trên dữ liệu Map chi tiết.
     */
    private void capNhatChartVaTable(Map<String, ThongKeVeChiTietItem> data, String chartTitle, String reportTitle) {
        lblChiTietTitle.setText("Báo cáo thống kê vé chi tiết theo " + reportTitle.toLowerCase());

        if (data == null || data.isEmpty()) {
            capNhatChartRong("📉 Không có dữ liệu vé trong khoảng đã chọn");
            capNhatBangRong();
            btnExportExcel.setEnabled(false);
        } else {
            // --- Cập nhật Biểu đồ ---
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

            // Tùy chỉnh Plot
            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            plot.setOutlineVisible(false);
            plot.setInsets(new RectangleInsets(10, 5, 5, 10));

            // Tùy chỉnh trục Y
            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setUpperMargin(0.15);
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // Tùy chỉnh Renderer
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(52, 152, 219));
            renderer.setSeriesPaint(1, new Color(231, 76, 60));
            renderer.setDrawBarOutline(false);
            renderer.setItemMargin(0.2);
            renderer.setShadowVisible(false);
            renderer.setMaximumBarWidth(0.08);

            // Tùy chỉnh trục X
            CategoryAxis domainAxis = plot.getDomainAxis();
            if (!chartTitle.contains("năm") && dataset.getColumnCount() > 8) {
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

            // --- Cập nhật Bảng chi tiết (THÊM CỘT MỚI) ---
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
                        item.tuyenDuong, // <<< CỘT MỚI
                        item.tongTienVe
                });
            }
            btnExportExcel.setEnabled(true);
        }
    }

    // --- Hàm xử lý xuất Excel (Đã cập nhật) ---
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

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
            }

            final File finalFileToSave = fileToSave;

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream outputStream = new FileOutputStream(finalFileToSave)) {

                Sheet sheet = workbook.createSheet("ChiTietThongKeVe");

                // --- Tạo Style ---
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
                CellStyle leftCellStyle = workbook.createCellStyle(); // <<< Style cho cột Tuyến đường
                leftCellStyle.setAlignment(HorizontalAlignment.LEFT);

                // --- Ghi Tiêu đề báo cáo ---
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(lblChiTietTitle.getText());
                titleCell.setCellStyle(titleCellStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, chiTietTableModel.getColumnCount() - 1));

                // --- Ghi Header bảng ---
                Row headerRow = sheet.createRow(2);
                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(chiTietTableModel.getColumnName(col).replace(" (VNĐ)", ""));
                    cell.setCellStyle(headerCellStyle);
                }

                // --- Ghi Dữ liệu bảng ---
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
                                // Cột 1 (Thời gian) căn giữa, Cột 7 (Tuyến) căn trái
                                cell.setCellStyle((col == 1) ? centerCellStyle : leftCellStyle);
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
                outputStream.close();

                JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!\n" + finalFileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(finalFileToSave);
                }
            } catch (Exception ex) {
                handleLoadingError(ex, "Lỗi khi ghi file Excel");
            }
        }
    }


    /** Xử lý lỗi khi tải dữ liệu trong SwingWorker.done() */
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

    // ================== CÁC HÀM TIỆN ÍCH KHÁC ==================

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

    private boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null) return false;
        Calendar cal1 = Calendar.getInstance(); cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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

    // --- Tạo Panel lọc con cho CardLayout ---
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

    // Ràng buộc JDateChooser
    private void addDateConstraint(com.toedter.calendar.JDateChooser from, com.toedter.calendar.JDateChooser to) {
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

    // --- Tạo Panel Tab ---
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

    // Hàm tạo Panel Chi Tiết với Tiêu đề và Nút Export
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

} // Kết thúc lớp PanelThongKeVe