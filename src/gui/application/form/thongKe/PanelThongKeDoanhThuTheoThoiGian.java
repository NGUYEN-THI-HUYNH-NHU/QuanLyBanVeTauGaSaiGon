package gui.application.form.thongKe; // Đảm bảo đúng package

import dao.ThongKe_DAO;
// Đổi import sang lớp mới
import dao.ThongKe_DAO.ThongKeChiTietItem; // <<< THAY ĐỔI IMPORT
// Import Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet; // <<< SỬ DỤNG SHEET (Interface)
import org.apache.poi.ss.usermodel.BorderStyle;
// KHÔNG import org.apache.poi.ss.usermodel.Color
// KHÔNG import org.apache.poi.ss.usermodel.Font
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
// Import Swing và AWT
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout; // <<< Import rõ ràng
import java.awt.CardLayout; // <<< Import rõ ràng
import java.awt.Color; // <<< Import rõ ràng
import java.awt.Component; // <<< Import rõ ràng
import java.awt.Cursor; // <<< Import rõ ràng
import java.awt.Desktop; // <<< Import Desktop để mở file
import java.awt.Dimension; // <<< Import rõ ràng
import java.awt.FlowLayout; // <<< Import rõ ràng
import java.awt.Font; // <<< Import rõ ràng
import java.awt.GridLayout; // <<< Import rõ ràng
import java.awt.event.ActionEvent; // Import ActionEvent
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
// Import IO và Util
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter (cho tiêu đề)
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level; // Import logging
import java.util.logging.Logger; // Import logging


/**
 * Panel hiển thị thống kê doanh thu theo thời gian, bao gồm cards tổng quan,
 * biểu đồ cột thu/chi và bảng chi tiết (có xuất Excel).
 * Các card cũng được cập nhật theo bộ lọc thời gian.
 */
public class PanelThongKeDoanhThuTheoThoiGian extends JPanel {

    // ===== DAO =====
    private final ThongKe_DAO thongKeDAO;

    // Logger để ghi lỗi
    private static final Logger LOGGER = Logger.getLogger(PanelThongKeDoanhThuTheoThoiGian.class.getName());


    // ===== Định dạng =====
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");

    // ===== Các thành phần lọc =====
    private final JComboBox<String> cbLoai;
    private final com.toedter.calendar.JDateChooser tuNgay;
    private final com.toedter.calendar.JDateChooser denNgay;
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

    // ===== Các JLabel chứa giá trị trên card =====
    private final JLabel lblTongHDDaBanValue;
    private final JLabel lblTongHDHoanDoiValue;
    private final JLabel lblTongThuDichVuValue;
    private final JLabel lblTongDoanhThuValue;
    private final JLabel lblTongChiValue;
    private final JLabel lblTongLoiNhuanValue;

    // ===== Biểu đồ & Bảng =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;

    // ===== Thành phần Panel Chi Tiết =====
    private final JLabel lblChiTietTitle; // <<< THÊM: Label tiêu đề
    private final JButton btnExportExcel; // <<< THÊM: Nút xuất Excel

    // ===== Lớp chứa kết quả thống kê (Sửa để dùng Map mới) =====
    private static class ThongKeResult {
        // Dữ liệu tổng quan theo khoảng thời gian (cho card)
        int tongHDTrongKhoang;
        int tongHDHoanDoiTrongKhoang;
        double tongThuDVTrongKhoang;
        double tongThuTrongKhoang;
        double tongChiHoanDoiTrongKhoang;
        // Dữ liệu chi tiết (thay đổi kiểu Map)
        Map<String, ThongKeChiTietItem> thongKeChiTietTheoThoiGian; // <<< THAY ĐỔI KIỂU
    }

    public PanelThongKeDoanhThuTheoThoiGian() {
        // Khởi tạo DAO *trước* khi dùng
        this.thongKeDAO = new ThongKe_DAO();

        // Khởi tạo các components trước khi sử dụng
        cbLoai = new JComboBox<>(new String[]{"Tất cả", "Theo ngày", "Theo tháng", "Theo năm"});
        tuNgay = new com.toedter.calendar.JDateChooser();
        denNgay = new com.toedter.calendar.JDateChooser();
        cbTuThang = new JComboBox<>();
        cbDenThang = new JComboBox<>();
        cbTuNamThang = new JComboBox<>();
        cbDenNamThang = new JComboBox<>();
        cbTuNam = new JComboBox<>();
        cbDenNam = new JComboBox<>();
        filterSwitcher = new JPanel(new CardLayout());

        lblTongHDDaBanValue = createValueLabel("...");
        lblTongHDHoanDoiValue = createValueLabel("...");
        lblTongThuDichVuValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");
        lblTongChiValue = createValueLabel("...");
        lblTongLoiNhuanValue = createValueLabel("...");

        // --- Cấu hình Layout chính ---
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE); // Dùng java.awt.Color
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ===== Khu vực NORTH: Thanh lọc và Cards =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // --- Thanh lọc ---
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        bar.setOpaque(false);
        bar.add(new JLabel("Loại thời gian:"));
        bar.add(cbLoai);
        filterSwitcher.setOpaque(false);
        JPanel panelTatCa = buildTatCaFilter();
        JPanel panelNgay = buildNgayFilter();
        JPanel panelThang = buildThangFilter();
        JPanel panelNam = buildNamFilter();
        filterSwitcher.add(panelTatCa, CARD_TATCA);
        filterSwitcher.add(panelNgay, CARD_NGAY);
        filterSwitcher.add(panelThang, CARD_THANG);
        filterSwitcher.add(panelNam, CARD_NAM);
        bar.add(filterSwitcher);
        JButton btnTim = new JButton("Tìm kiếm");
        btnTim.setBackground(new Color(46, 204, 113));
        btnTim.setForeground(Color.WHITE);
        btnTim.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTim.setPreferredSize(new Dimension(100, 30));
        bar.add(btnTim);
        topPanel.add(bar, BorderLayout.NORTH);

        // --- Cards tổng quan ---
        JPanel infoWrapper = new JPanel(new GridLayout(2, 1, 0, 15));
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        JPanel topRow = new JPanel(new GridLayout(1, 3, 15, 0)); topRow.setOpaque(false);
        topRow.add(createCard("Tổng hóa đơn đã bán", lblTongHDDaBanValue, new Color(52, 152, 219)));
        topRow.add(createCard("Tổng hóa đơn hoàn/đổi", lblTongHDHoanDoiValue, new Color(243, 156, 18)));
        topRow.add(createCard("Tổng thu dịch vụ", lblTongThuDichVuValue, new Color(41, 128, 185)));
        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 15, 0)); bottomRow.setOpaque(false);
        bottomRow.add(createCard("Tổng doanh thu", lblTongDoanhThuValue, new Color(46, 204, 113)));
        bottomRow.add(createCard("Tổng chi", lblTongChiValue, new Color(231, 76, 60)));
        bottomRow.add(createCard("Tổng lợi nhuận", lblTongLoiNhuanValue, new Color(39, 174, 96)));
        infoWrapper.add(topRow);
        infoWrapper.add(bottomRow);
        topPanel.add(infoWrapper, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);


        // ===== Khu vực CENTER: Tabs biểu đồ & chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan(); // Panel chứa biểu đồ

        // --- Tạo Table Model VỚI CÁC CỘT MỚI ---
        String[] columnNames = {
                "STT", "Thời Gian", "Tổng HĐ Bán", "Tổng HĐ Trả",
                "Tổng Thu DV (VNĐ)", "Tổng Doanh Thu (VNĐ)", "Tổng Chi (VNĐ)", "Lợi Nhuận (VNĐ)"
        }; // <<< CỘT MỚI
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 3 -> Integer.class; // STT, SL HĐ Bán, SL HĐ Trả
                    case 4, 5, 6, 7 -> Double.class; // Các cột tiền tệ (lưu trữ là double)
                    default -> String.class; // Thời gian
                };
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails(); // Gọi hàm tùy chỉnh bảng

        // --- Tạo Panel Chi Tiết với Tiêu đề và Nút Export ---
        lblChiTietTitle = new JLabel("Báo cáo thống kê chi tiết", JLabel.CENTER); // <<< KHỞI TẠO TITLE
        lblChiTietTitle.setFont(new Font("Times New Roman", Font.BOLD, 16));
        lblChiTietTitle.setBorder(new EmptyBorder(5, 0, 10, 0));

        btnExportExcel = new JButton("Xuất Excel"); // <<< KHỞI TẠO NÚT EXPORT
        btnExportExcel.setBackground(new Color(33, 115, 70));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportExcel.setEnabled(false); // Ban đầu vô hiệu hóa
        btnExportExcel.addActionListener(this::exportTableToExcel); // <<< THÊM ACTION LISTENER

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Panel chứa nút export
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnExportExcel);

        // Panel chứa tiêu đề, bảng và nút export
        JPanel panelChiTietContainer = taoPanelChiTiet(lblChiTietTitle, tableChiTiet, bottomPanel); // <<< GỌI HÀM MỚI

        // Thêm tab
        tab.addTab("Tổng quan", chartPanelContainer);
        tab.addTab("Chi tiết", panelChiTietContainer); // <<< THÊM PANEL CONTAINER MỚI

        add(tab, BorderLayout.CENTER);


        // ===== Sự kiện =====
        cbLoai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CardLayout cl = (CardLayout) filterSwitcher.getLayout();
                switch ((String) e.getItem()) {
                    case "Tất cả"   -> cl.show(filterSwitcher, CARD_TATCA);
                    case "Theo ngày"-> cl.show(filterSwitcher, CARD_NGAY);
                    case "Theo tháng"-> cl.show(filterSwitcher, CARD_THANG);
                    case "Theo năm" -> cl.show(filterSwitcher, CARD_NAM);
                }
            }
        });
        btnTim.addActionListener(e -> xuLyThongKe());

        // ===== Load dữ liệu ban đầu =====
        SwingUtilities.invokeLater(this::xuLyThongKe); // Load dữ liệu ban đầu khi UI sẵn sàng
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

    // --- Hàm mới để cấu hình JTable ---
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
        tableChiTiet.setAutoCreateRowSorter(true); // Cho phép sắp xếp cột

        // --- Căn chỉnh và Định dạng cột ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Renderer đặc biệt cho tiền tệ
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormatter.format(value); // Định dạng tiền tệ
                }
                setHorizontalAlignment(JLabel.RIGHT); // Căn phải
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        // Renderer đặc biệt cho số nguyên (không có VNĐ)
        DefaultTableCellRenderer integerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = integerFormatter.format(value); // Định dạng số nguyên
                }
                setHorizontalAlignment(JLabel.RIGHT); // Căn phải
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };


        TableColumnModel columnModel = tableChiTiet.getColumnModel();
        // STT (index 0)
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(0).setCellRenderer(centerRenderer);
        // Thời gian (index 1)
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(1).setCellRenderer(centerRenderer);
        // SL HĐ Bán (index 2)
        columnModel.getColumn(2).setPreferredWidth(90);
        columnModel.getColumn(2).setCellRenderer(integerRenderer); // <<< SỬ DỤNG INTEGER RENDERER
        // SL HĐ Trả (index 3)
        columnModel.getColumn(3).setPreferredWidth(90);
        columnModel.getColumn(3).setCellRenderer(integerRenderer); // <<< SỬ DỤNG INTEGER RENDERER
        // Các cột tiền tệ (index 4 đến 7)
        for (int i = 4; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setPreferredWidth(140);
            columnModel.getColumn(i).setCellRenderer(currencyRenderer); // <<< SỬ DỤNG CURRENCY RENDERER
        }
    }


    // ================== HÀM XỬ LÝ CHÍNH ==================

    // --- Xử lý khi nhấn nút "Tìm kiếm" HOẶC load ban đầu ---
    private void xuLyThongKe() {
        String loai = (String) cbLoai.getSelectedItem();
        if (loai == null) {
            loai = "Tất cả";
            cbLoai.setSelectedItem(loai);
        }

        LocalDate fromLocalDate = null, toLocalDate = null;
        String titleLoai = loai; // Tên hiển thị báo cáo
        String titleChart = loai; // Tên hiển thị chart

        // 1. Xác định khoảng thời gian LocalDate từ bộ lọc
        try {
            switch (loai) {
                case "Theo ngày":
                    Date utilFromDateNgay = tuNgay.getDate();
                    Date utilToDateNgay = denNgay.getDate();
                    if (!kiemTraKhoangNgayHopLe(utilFromDateNgay, utilToDateNgay)) return;
                    fromLocalDate = utilFromDateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    toLocalDate = utilToDateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    // Tạo titleLoai chi tiết hơn
                    titleLoai = String.format("ngày (từ %s đến %s)",
                            fromLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            toLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    titleChart = "ngày"; // Giữ title chart ngắn gọn
                    break;
                case "Theo tháng":
                    if (cbTuNamThang.getSelectedItem() == null || cbDenNamThang.getSelectedItem() == null) {
                        LOGGER.warning("ComboBox tháng/năm chưa sẵn sàng.");
                        Calendar now = Calendar.getInstance();
                        fromLocalDate = LocalDate.now().withDayOfMonth(1);
                        toLocalDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                        cbTuThang.setSelectedIndex(now.get(Calendar.MONTH)); cbDenThang.setSelectedIndex(now.get(Calendar.MONTH));
                        cbTuNamThang.setSelectedItem(now.get(Calendar.YEAR)); cbDenNamThang.setSelectedItem(now.get(Calendar.YEAR));
                    } else {
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
                    }
                    break;
                case "Theo năm":
                    if (cbTuNam.getSelectedItem() == null || cbDenNam.getSelectedItem() == null) {
                        LOGGER.warning("ComboBox năm chưa sẵn sàng.");
                        int currentYear = LocalDate.now().getYear();
                        fromLocalDate = LocalDate.of(currentYear, 1, 1); toLocalDate = LocalDate.of(currentYear, 12, 31);
                        cbTuNam.setSelectedItem(currentYear); cbDenNam.setSelectedItem(currentYear);
                    } else {
                        int startYear = (Integer) cbTuNam.getSelectedItem(); int endYear = (Integer) cbDenNam.getSelectedItem();
                        fromLocalDate = LocalDate.of(startYear, 1, 1);
                        toLocalDate = LocalDate.of(endYear, 12, 31);
                        if (toLocalDate.isBefore(fromLocalDate)) {
                            JOptionPane.showMessageDialog(this, "⚠️ Năm kết thúc phải ≥ năm bắt đầu.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        titleLoai = String.format("năm (từ %d đến %d)", startYear, endYear);
                        titleChart = "năm";
                    }
                    break;
                default: // "Tất cả"
                    fromLocalDate = LocalDate.of(2000, 1, 1);
                    toLocalDate = LocalDate.now();
                    titleLoai = "tất cả (thống kê theo năm)";
                    titleChart = "Tất cả (theo năm)";
                    loai = "Tất cả"; // 'loai' cho DAO
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thông tin lọc thời gian: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy thông tin lọc thời gian", ex);
            return;
        }

        // --- Hiển thị loading ---
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongHDDaBanValue.setText("..."); lblTongHDHoanDoiValue.setText("...");
        lblTongThuDichVuValue.setText("..."); lblTongDoanhThuValue.setText("...");
        lblTongChiValue.setText("..."); lblTongLoiNhuanValue.setText("...");
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        // 2. Tạo và thực thi SwingWorker
        final LocalDate finalFrom = fromLocalDate;
        final LocalDate finalTo = toLocalDate;
        final String finalDaoLoai = loai; // (Tất cả, Theo ngày, Theo tháng, Theo năm)
        final String finalTitleLoai = titleLoai; // Title dài cho báo cáo
        final String finalChartTitle = titleChart; // Title ngắn cho chart

        SwingWorker<ThongKeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeResult doInBackground() throws Exception {
                ThongKeResult result = new ThongKeResult();
                // Lấy dữ liệu chi tiết
                result.thongKeChiTietTheoThoiGian = thongKeDAO.getThongKeChiTietTheoThoiGian(finalDaoLoai, finalFrom, finalTo);
                // Lấy dữ liệu tổng quan THEO KHOẢNG THỜI GIAN
                result.tongHDTrongKhoang = thongKeDAO.getTongHoaDonBanTrongKhoang(finalFrom, finalTo);
                result.tongHDHoanDoiTrongKhoang = thongKeDAO.getTongHoaDonHoanDoiTrongKhoang(finalFrom, finalTo);
                result.tongThuDVTrongKhoang = thongKeDAO.getTongThuDichVuTrongKhoang(finalFrom, finalTo);
                result.tongThuTrongKhoang = thongKeDAO.getTongThuTrongKhoang(finalFrom, finalTo);
                result.tongChiHoanDoiTrongKhoang = thongKeDAO.getTongChiHoanDoiTrongKhoang(finalFrom, finalTo);
                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeResult result = get();

                    // CẬP NHẬT CARD
                    double loiNhuanTrongKhoang = result.tongThuTrongKhoang - result.tongChiHoanDoiTrongKhoang;
                    lblTongHDDaBanValue.setText(integerFormatter.format(result.tongHDTrongKhoang));
                    lblTongHDHoanDoiValue.setText(integerFormatter.format(result.tongHDHoanDoiTrongKhoang));
                    lblTongThuDichVuValue.setText(currencyFormatter.format(result.tongThuDVTrongKhoang));
                    lblTongDoanhThuValue.setText(currencyFormatter.format(result.tongThuTrongKhoang));
                    lblTongChiValue.setText(currencyFormatter.format(result.tongChiHoanDoiTrongKhoang));
                    lblTongLoiNhuanValue.setText(currencyFormatter.format(loiNhuanTrongKhoang));

                    // CẬP NHẬT BIỂU ĐỒ VÀ BẢNG CHI TIẾT
                    capNhatChartVaTable(result.thongKeChiTietTheoThoiGian, finalChartTitle, finalTitleLoai);

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
     * @param data Map dữ liệu từ DAO (Key: String thời gian, Value: ThongKeChiTietItem).
     * @param chartTitle Title cho biểu đồ (ngắn gọn: "ngày", "tháng", ...).
     * @param reportTitle Title cho báo cáo/bảng (chi tiết: "ngày (từ...)", ...).
     */
    private void capNhatChartVaTable(Map<String, ThongKeChiTietItem> data, String chartTitle, String reportTitle) {
        // Cập nhật tiêu đề bảng chi tiết
        lblChiTietTitle.setText("Báo cáo thống kê chi tiết theo " + reportTitle.toLowerCase());

        if (data == null || data.isEmpty()) {
            capNhatChartRong("📉 Không có dữ liệu thu/chi trong khoảng đã chọn");
            capNhatBangRong();
            btnExportExcel.setEnabled(false); // Vô hiệu hóa nút export
        } else {
            // --- Cập nhật Biểu đồ (vẫn dùng Doanh Thu và Chi Phí) ---
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            final String seriesThu = "Tổng thu";
            final String seriesChi = "Tổng chi";
            data.forEach((thoiGian, item) -> {
                dataset.addValue(item.tongDoanhThu, seriesThu, thoiGian); // Dùng tongDoanhThu
                dataset.addValue(item.tongChi, seriesChi, thoiGian);      // Dùng tongChi
            });

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Doanh thu và Chi phí theo " + chartTitle.toLowerCase(), // Dùng title ngắn gọn
                    "Thời gian", "Số tiền (VNĐ)", dataset,
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
            rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance(new Locale("vi","VN")));
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            // Tùy chỉnh Renderer (cột)
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
            domainAxis.setLowerMargin(0.02); domainAxis.setUpperMargin(0.02);

            // Tùy chỉnh Legend
            if (barChart.getLegend() != null) {
                barChart.getLegend().setFrame(BlockBorder.NONE);
                barChart.getLegend().setItemFont(new Font("Times New Roman", Font.PLAIN, 12));
                barChart.getLegend().setBorder(5, 5, 5, 5);
            }

            // Tùy chỉnh Chart tổng thể
            barChart.setBorderVisible(false);
            barChart.setBackgroundPaint(Color.WHITE);
            barChart.getTitle().setFont(new Font("Times New Roman", Font.BOLD, 16));


            ChartPanel chartDisplayPanel = new ChartPanel(barChart);
            chartDisplayPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            chartDisplayPanel.setBackground(Color.WHITE);

            // Hiển thị chart lên UI
            chartPanelContainer.removeAll();
            chartPanelContainer.add(chartDisplayPanel, BorderLayout.CENTER);
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();

            // --- Cập nhật Bảng chi tiết VỚI CÁC CỘT MỚI ---
            chiTietTableModel.setRowCount(0); // Xóa dữ liệu cũ
            int stt = 1;
            for (Map.Entry<String, ThongKeChiTietItem> entry : data.entrySet()) {
                String thoiGian = entry.getKey();
                ThongKeChiTietItem item = entry.getValue();
                chiTietTableModel.addRow(new Object[]{
                        stt++,
                        thoiGian,
                        item.soLuongHoaDonBan,
                        item.soLuongHoaDonHoanDoi,
                        item.tongThuDichVu,  // Lưu Double
                        item.tongDoanhThu,   // Lưu Double
                        item.tongChi,        // Lưu Double
                        item.loiNhuan       // Lưu Double
                });
            }
            btnExportExcel.setEnabled(true); // Kích hoạt nút export
        }
    }

    // --- Hàm xử lý xuất Excel ---
    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo Excel");
        String defaultFileName = "BaoCaoThongKe_" + LocalDate.now() + ".xlsx";
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                fileToSave = new File(filePath + ".xlsx");
            }

            final File finalFileToSave = fileToSave; // Dùng cho Desktop.open

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 FileOutputStream outputStream = new FileOutputStream(finalFileToSave)) {

                Sheet sheet = workbook.createSheet("ChiTietDoanhThu"); // <<< DÙNG Sheet (interface)

                // --- Tạo Style ---
                // Dùng tên đầy đủ của POI Font để tránh nhầm lẫn
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12);
                CellStyle headerCellStyle = workbook.createCellStyle(); headerCellStyle.setFont(headerFont);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER); headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); headerCellStyle.setBorderBottom(BorderStyle.THIN);
                headerCellStyle.setBorderTop(BorderStyle.THIN); headerCellStyle.setBorderLeft(BorderStyle.THIN); headerCellStyle.setBorderRight(BorderStyle.THIN);

                CellStyle titleCellStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont(); // <<< DÙNG POI FONT
                titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 14); titleCellStyle.setFont(titleFont);
                titleCellStyle.setAlignment(HorizontalAlignment.CENTER);

                CreationHelper createHelper = workbook.getCreationHelper();
                // Style cho tiền tệ
                CellStyle currencyCellStyle = workbook.createCellStyle();
                currencyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0")); // Định dạng số nguyên
                currencyCellStyle.setAlignment(HorizontalAlignment.RIGHT);
                // Style cho số nguyên
                CellStyle integerCellStyle = workbook.createCellStyle();
                integerCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0"));
                integerCellStyle.setAlignment(HorizontalAlignment.RIGHT);
                // Style cho căn giữa
                CellStyle centerCellStyle = workbook.createCellStyle();
                centerCellStyle.setAlignment(HorizontalAlignment.CENTER);


                // --- Ghi Tiêu đề báo cáo ---
                Row titleRow = sheet.createRow(0);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue(lblChiTietTitle.getText()); // Lấy tiêu đề từ JLabel
                titleCell.setCellStyle(titleCellStyle);
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, chiTietTableModel.getColumnCount() - 1));

                // --- Ghi Header bảng ---
                Row headerRow = sheet.createRow(2);
                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(chiTietTableModel.getColumnName(col).replace(" (VNĐ)", "")); // Bỏ (VNĐ)
                    cell.setCellStyle(headerCellStyle);
                }

                // --- Ghi Dữ liệu bảng ---
                for (int row = 0; row < chiTietTableModel.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 3);
                    for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object value = chiTietTableModel.getValueAt(row, col);

                        try {
                            if (value instanceof Integer) { // STT, SL HĐ Bán, SL HĐ Trả
                                cell.setCellValue((Integer) value);
                                cell.setCellStyle( (col == 0) ? centerCellStyle : integerCellStyle); // Căn giữa STT, căn phải SL
                            } else if (value instanceof Double) { // Các cột tiền
                                cell.setCellValue((Double) value);
                                cell.setCellStyle(currencyCellStyle); // Dùng style tiền tệ (số)
                            } else if (value instanceof String) { // Thời gian
                                cell.setCellValue((String) value);
                                cell.setCellStyle(centerCellStyle); // Căn giữa cột Thời gian
                            } else if (value != null) {
                                cell.setCellValue(value.toString());
                            }
                        } catch (Exception ex) {
                            LOGGER.log(Level.WARNING, "Lỗi khi ghi dữ liệu Excel ô ["+row+","+col+"]", ex);
                            cell.setCellValue("Lỗi dữ liệu");
                        }
                    }
                }

                // --- Tự động điều chỉnh độ rộng cột ---
                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    sheet.autoSizeColumn(col);
                }

                // Ghi workbook ra file
                workbook.write(outputStream);
                outputStream.close(); // << Đóng stream trước khi mở file

                // Hiển thị thông báo thành công
                JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!\n" + finalFileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

                // Mở file sau khi lưu
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(finalFileToSave);
                    } catch (IOException ioex) {
                        LOGGER.log(Level.WARNING, "Không thể tự động mở file Excel.", ioex);
                        JOptionPane.showMessageDialog(this, "Không thể tự động mở file:\n" + ioex.getMessage(), "Lỗi mở file", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    LOGGER.log(Level.WARNING, "java.awt.Desktop không được hỗ trợ.");
                }


            } catch (IOException ioException) {
                LOGGER.log(Level.SEVERE, "Lỗi khi ghi file Excel", ioException);
                JOptionPane.showMessageDialog(this, "Lỗi khi ghi file Excel:\n" + ioException.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception generalException) {
                LOGGER.log(Level.SEVERE, "Lỗi không xác định khi xuất Excel", generalException);
                JOptionPane.showMessageDialog(this, "Lỗi không xác định khi xuất Excel:\n" + generalException.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /** Xử lý lỗi khi tải dữ liệu trong SwingWorker.done() */
    private void handleLoadingError(Exception ex, String context) {
        setCursor(Cursor.getDefaultCursor());
        LOGGER.log(Level.SEVERE, context, ex);
        capNhatChartRong("⚠️ Lỗi khi tải dữ liệu: " + ex.getMessage());
        capNhatBangRong();
        JOptionPane.showMessageDialog(this, context + ":\n" + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        lblTongHDDaBanValue.setText("Lỗi"); lblTongHDHoanDoiValue.setText("Lỗi");
        lblTongThuDichVuValue.setText("Lỗi"); lblTongDoanhThuValue.setText("Lỗi");
        lblTongChiValue.setText("Lỗi"); lblTongLoiNhuanValue.setText("Lỗi");
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

    // --- Tạo Panel lọc ---
    private JPanel buildTatCaFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel("Hiển thị dữ liệu theo năm");
        lbl.setFont(new Font("Times New Roman", Font.ITALIC, 14));
        lbl.setForeground(Color.GRAY);
        p.add(lbl);
        p.setPreferredSize(new Dimension(450, 30));
        return p;
    }
    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        tuNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setPreferredSize(new Dimension(130, 28));
        denNgay.setPreferredSize(new Dimension(130, 28));
        tuNgay.setDate(new Date()); // Mặc định ngày hiện tại
        denNgay.setDate(new Date());
        addDateConstraint(tuNgay, denNgay); // Thêm ràng buộc
        p.add(new JLabel("Từ:")); p.add(tuNgay);
        p.add(new JLabel("Đến:")); p.add(denNgay);
        return p;
    }
    private JPanel buildThangFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        if (cbTuThang.getItemCount() == 0) { // Chỉ khởi tạo 1 lần
            for (int i = 1; i <= 12; i++) { cbTuThang.addItem("Tháng " + i); cbDenThang.addItem("Tháng " + i); }
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= currentYear + 5; y++) { cbTuNamThang.addItem(y); cbDenNamThang.addItem(y); }
            Calendar now = Calendar.getInstance();
            cbTuThang.setSelectedIndex(now.get(Calendar.MONTH));
            cbDenThang.setSelectedIndex(now.get(Calendar.MONTH));
            cbTuNamThang.setSelectedItem(now.get(Calendar.YEAR));
            cbDenNamThang.setSelectedItem(now.get(Calendar.YEAR));
        }
        Dimension monthDim = new Dimension(90, 28);
        Dimension yearDim = new Dimension(75, 28);
        cbTuThang.setPreferredSize(monthDim); cbDenThang.setPreferredSize(monthDim);
        cbTuNamThang.setPreferredSize(yearDim); cbDenNamThang.setPreferredSize(yearDim);

        p.add(new JLabel("Từ:")); p.add(cbTuThang); p.add(cbTuNamThang);
        p.add(new JLabel("Đến:")); p.add(cbDenThang); p.add(cbDenNamThang);
        return p;
    }
    private JPanel buildNamFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        if (cbTuNam.getItemCount() == 0) { // Chỉ khởi tạo 1 lần
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= currentYear + 5; y++) { cbTuNam.addItem(y); cbDenNam.addItem(y); }
            cbTuNam.setSelectedItem(currentYear);
            cbDenNam.setSelectedItem(currentYear);
        }
        Dimension yearDim = new Dimension(90, 28);
        cbTuNam.setPreferredSize(yearDim); cbDenNam.setPreferredSize(yearDim);

        p.add(new JLabel("Từ năm:")); p.add(cbTuNam);
        p.add(new JLabel("Đến năm:")); p.add(cbDenNam);
        return p;
    }
    // Ràng buộc JDateChooser
    private void addDateConstraint(com.toedter.calendar.JDateChooser from, com.toedter.calendar.JDateChooser to) {
        PropertyChangeListener sync = new PropertyChangeListener() {
            private boolean adjusting = false;
            @Override public void propertyChange(PropertyChangeEvent evt) {
                if (!"date".equals(evt.getPropertyName()) || adjusting) return;
                Date f = from.getDate(); Date t = to.getDate();
                if (f == null || t == null) return;

                if (!isSameDay(f, t) && t.before(f)) {
                    adjusting = true;
                    to.setDate(f); // Tự động sửa, không popup
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
    // Sửa lại hàm taoPanelChiTiet để thêm Tiêu đề và Nút Export
    private JPanel taoPanelChiTiet(JLabel titleLabel, JTable table, JPanel bottomPanel) {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // Khoảng cách dọc
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5)); // Padding

        panel.add(titleLabel, BorderLayout.NORTH); // Tiêu đề

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER); // Bảng

        panel.add(bottomPanel, BorderLayout.SOUTH); // Nút Export

        return panel;
    }

} // Kết thúc lớp PanelThongKeDoanhThuTheoThoiGian