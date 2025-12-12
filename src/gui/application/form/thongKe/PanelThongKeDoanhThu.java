package gui.application.form.thongKe;

import dao.ThongKeDoanhThu_DAO;
import dao.ThongKeDoanhThu_DAO.ThongKeChiTietItem;
import dao.ThongKeVe_DAO;
// JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.DefaultCategoryDataset;
// Apache POI
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Swing, AWT, Calendar
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
// IO và Util
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Panel hiển thị thống kê DOANH THU
 * Giao diện: 7 Cards (6 Rect left, 1 Circle right)
 */
public class PanelThongKeDoanhThu extends JPanel {

    // ===== DAO =====
    private final ThongKeDoanhThu_DAO thongKeDoanhThuDAO;
    private final ThongKeVe_DAO thongKeVeDAO;

    private static final Logger LOGGER = Logger.getLogger(PanelThongKeDoanhThu.class.getName());

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

    private final JComboBox<String> cbLoaiTuyen;
    private final JComboBox<String> cbGaDi;
    private final JComboBox<String> cbGaDen;

    private final JComboBox<String> cbNhanVien;
    private final JComboBox<String> cbThanhToan;
    private final Map<String, String> nhanVienMap = new HashMap<>();

    private final JButton btnTimKiem;
    private final JButton btnXoaBoLoc;

    // ===== Các JLabel chứa giá trị trên card =====
    // Hàng 1
    private final JLabel lblTongHDDaBanValue;
    private final JLabel lblTongHDHoanDoiValue;
    private final JLabel lblTongChiValue;
    // Hàng 2
    private final JLabel lblTongTienMatValue;
    private final JLabel lblTongChuyenKhoanValue;
    private final JLabel lblTongDoanhThuValue;
    // Card Tròn bên phải
    private final JLabel lblTongLoiNhuanValue;

    // ===== Biểu đồ & Bảng =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;

    // ===== Thành phần Panel Chi Tiết =====
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    // ===== Lớp chứa kết quả thống kê =====
    private static class ThongKeDoanhThuResult {
        int tongHDTrongKhoang;
        int tongHDHoanDoiTrongKhoang;

        double tongChi;          // Tổng tiền hoàn trả (âm)
        double tongTienMat;      // Doanh thu tiền mặt
        double tongChuyenKhoan;  // Doanh thu chuyển khoản
        double tongDoanhThu;     // Tổng doanh thu (dương)
        double tongLoiNhuan;     // Doanh thu - Chi phí

        Map<String, ThongKeChiTietItem> thongKeChiTietTheoThoiGian;
    }

    public PanelThongKeDoanhThu() {
        this.thongKeDoanhThuDAO = new ThongKeDoanhThu_DAO();
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

        // Khởi tạo components tuyến
        cbLoaiTuyen = new JComboBox<>(new String[]{"Tất cả", "Theo Ga đi/đến"});
        cbGaDi = new JComboBox<>();
        cbGaDen = new JComboBox<>();

        // Khởi tạo components nghiệp vụ
        cbNhanVien = new JComboBox<>();
        cbThanhToan = new JComboBox<>(new String[]{"Tất cả", "Tiền mặt", "Chuyển khoản"});

        btnTimKiem = new JButton("Tìm kiếm");
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaBoLoc.setBackground(new Color(108, 117, 125));
        btnXoaBoLoc.setForeground(Color.WHITE);
        btnXoaBoLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXoaBoLoc.addActionListener(e -> xoaBoLoc());

        // Khởi tạo labels (7 Cards)
        lblTongHDDaBanValue = createValueLabel("...");
        lblTongHDHoanDoiValue = createValueLabel("...");
        lblTongChiValue = createValueLabel("...");

        lblTongTienMatValue = createValueLabel("...");
        lblTongChuyenKhoanValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");

        lblTongLoiNhuanValue = createValueLabel("...");

        // --- Cấu hình Layout chính ---
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);

        javax.swing.border.Border titledLineBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                "THỐNG KÊ DOANH THU",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                new Color(0, 110, 185)
        );
        setBorder(BorderFactory.createCompoundBorder(titledLineBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // ===== Khu vực NORTH: Thanh lọc và Cards =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // --- Thanh lọc ---
        topPanel.add(buildFilterBar());

        // --- Separator ---
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(180, 180, 180));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        topPanel.add(sep);

        // ============================================================
        // --- LOGIC LAYOUT 7 CARDS ---
        // ============================================================

        // Container chính cho khu vực Cards
        JPanel cardsContainer = new JPanel(new BorderLayout(15, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // 1. Panel Bên Trái (Grid 2x3 cho 6 Cards hình chữ nhật)
        JPanel leftGridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        leftGridPanel.setOpaque(false);

        // Hàng 1: HD Bán | HD Hoàn | Tổng Chi
        leftGridPanel.add(createCard("Tổng hóa đơn đã bán", lblTongHDDaBanValue, new Color(52, 152, 219)));
        leftGridPanel.add(createCard("Tổng hóa đơn hoàn trả", lblTongHDHoanDoiValue, new Color(243, 156, 18)));
        leftGridPanel.add(createCard("Tổng Chi", lblTongChiValue, new Color(231, 76, 60))); // Đỏ

        // Hàng 2: Tiền mặt | Chuyển khoản | Doanh thu
        leftGridPanel.add(createCard("Tổng tiền mặt", lblTongTienMatValue, new Color(155, 89, 182))); // Tím
        leftGridPanel.add(createCard("Tổng chuyển khoản", lblTongChuyenKhoanValue, new Color(52, 73, 94))); // Xám xanh
        leftGridPanel.add(createCard("Tổng doanh thu", lblTongDoanhThuValue, new Color(46, 204, 113))); // Xanh lá

        // 2. Panel Bên Phải (Chứa 1 Card Tròn Lợi Nhuận)
        JPanel rightCirclePanel = new JPanel(new GridBagLayout()); // Dùng GridBag để căn giữa
        rightCirclePanel.setOpaque(false);

        // Tạo Card hình tròn
        CirclePanel circleCard = new CirclePanel("Tổng lợi nhuận", lblTongLoiNhuanValue, new Color(39, 174, 96));
        circleCard.setPreferredSize(new Dimension(180, 180)); // Kích thước hình tròn

        rightCirclePanel.add(circleCard);

        // Gắn vào container
        cardsContainer.add(leftGridPanel, BorderLayout.CENTER);
        cardsContainer.add(rightCirclePanel, BorderLayout.EAST);

        topPanel.add(cardsContainer);
        add(topPanel, BorderLayout.NORTH);

        // ===== Khu vực CENTER: Tabs biểu đồ & chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        // --- Tạo Table Model ---
        String[] columnNames = {
                "STT", "Thời Gian", "HĐ Bán", "HĐ Hoàn/Đổi", "Doanh Thu (VNĐ)", "Tổng Chi (VNĐ)", "Lợi Nhuận (VNĐ)"
        };
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2, 3 -> Integer.class;
                    case 4, 5, 6 -> Double.class;
                    default -> String.class;
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
        loadComboBoxesData();
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    /**
     * CLASS NỘI BỘ: Tạo Panel hình tròn cho thẻ Lợi Nhuận
     */
    private static class CirclePanel extends JPanel {
        private final JLabel valueLabel;
        private final Color bgColor;

        public CirclePanel(String title, JLabel valueLabel, Color bgColor) {
            this.valueLabel = valueLabel;
            this.bgColor = bgColor;
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.insets = new Insets(5, 0, 5, 0);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Times New Roman", Font.PLAIN, 15));
            lblTitle.setForeground(Color.WHITE);
            add(lblTitle, gbc);

            gbc.gridy = 1;
            valueLabel.setFont(new Font("Times New Roman", Font.BOLD, 22)); // Font to hơn
            add(valueLabel, gbc);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(bgColor);
            g2.fillOval(x, y, size, size);

            // Vẽ viền nhẹ
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(x+3, y+3, size-6, size-6);

            g2.dispose();
        }
    }

    // ================== HÀM XỬ LÝ CHÍNH ==================
    private void xuLyThongKe() {
        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        if (loaiThoiGian == null) loaiThoiGian = "Tất cả";

        String loaiTuyen = (String) cbLoaiTuyen.getSelectedItem();
        String tenGaDi = (String) cbGaDi.getSelectedItem();
        String tenGaDen = (String) cbGaDen.getSelectedItem();
        String selectedNhanVien = (String) cbNhanVien.getSelectedItem();
        String nhanVienID = (selectedNhanVien == null || selectedNhanVien.equals("Tất cả")) ? null : nhanVienMap.get(selectedNhanVien);

        // Xử lý bộ lọc thanh toán chung
        String thanhToan = (String) cbThanhToan.getSelectedItem();
        Integer isTienMat = null; // null = Tất cả
        if ("Tiền mặt".equals(thanhToan)) isTienMat = 1;
        else if ("Chuyển khoản".equals(thanhToan)) isTienMat = 0;

        if (!loaiTuyen.equals("Tất cả") && (tenGaDi == null || tenGaDen == null)) return;

        LocalDate from, to;
        String titleChart = loaiThoiGian;
        String titleLoai = loaiThoiGian;

        try {
            switch (loaiThoiGian) {
                case "Theo ngày" -> {
                    Date d1 = tuNgay.getDate(), d2 = denNgay.getDate();
                    if (!kiemTraKhoangNgayHopLe(d1, d2)) return;
                    from = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    to = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    titleLoai = String.format("ngày (%s - %s)", from.format(DateTimeFormatter.ofPattern("dd/MM")), to.format(DateTimeFormatter.ofPattern("dd/MM")));
                    titleChart = "ngày";
                }
                case "Theo tháng" -> {
                    int fm = cbTuThang.getSelectedIndex()+1, fy = (int) cbTuNamThang.getSelectedItem();
                    int tm = cbDenThang.getSelectedIndex()+1, ty = (int) cbDenNamThang.getSelectedItem();
                    from = LocalDate.of(fy, fm, 1);
                    to = LocalDate.of(ty, tm, LocalDate.of(ty, tm, 1).lengthOfMonth());
                    if (to.isBefore(from)) { JOptionPane.showMessageDialog(this, "Tháng kết thúc phải >= tháng bắt đầu."); return; }
                    titleLoai = String.format("tháng (%d/%d - %d/%d)", fm, fy, tm, ty);
                    titleChart = "tháng";
                }
                case "Theo năm" -> {
                    int sy = (int) cbTuNam.getSelectedItem(), ey = (int) cbDenNam.getSelectedItem();
                    from = LocalDate.of(sy, 1, 1);
                    to = LocalDate.of(ey, 12, 31);
                    if (ey < sy) { JOptionPane.showMessageDialog(this, "Năm kết thúc phải >= năm bắt đầu."); return; }
                    titleLoai = String.format("năm (%d - %d)", sy, ey);
                    titleChart = "năm";
                }
                default -> {
                    from = LocalDate.of(2000, 1, 1);
                    to = LocalDate.now().plusDays(1);
                    titleLoai = "tất cả";
                    titleChart = "Tất cả (năm)";
                    loaiThoiGian = "Theo năm";
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); return; }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String loading = "...";
        lblTongHDDaBanValue.setText(loading); lblTongHDHoanDoiValue.setText(loading);
        lblTongChiValue.setText(loading); lblTongDoanhThuValue.setText(loading);
        lblTongTienMatValue.setText(loading); lblTongChuyenKhoanValue.setText(loading);
        lblTongLoiNhuanValue.setText(loading);
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        // Chuẩn bị biến final cho Worker
        LocalDate fFrom = from; LocalDate fTo = to;
        String fDaoLoai = loaiThoiGian; String fTitleLoai = titleLoai; String fChartTitle = titleChart;
        String fLoaiTuyen = loaiTuyen; String fTenGaDi = tenGaDi; String fTenGaDen = tenGaDen;
        String fNhanVienID = nhanVienID; Integer fIsTienMat = isTienMat;

        SwingWorker<ThongKeDoanhThuResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeDoanhThuResult doInBackground() throws Exception {
                ThongKeDoanhThuResult r = new ThongKeDoanhThuResult();

                // 1. Lấy chi tiết chung (theo bộ lọc hiện tại)
                r.thongKeChiTietTheoThoiGian = thongKeDoanhThuDAO.getThongKeDoanhThuChiTiet(
                        fDaoLoai, fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);

                // 2. Số lượng hóa đơn (theo bộ lọc)
                r.tongHDTrongKhoang = thongKeDoanhThuDAO.getTongHoaDonBan(fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);
                r.tongHDHoanDoiTrongKhoang = thongKeDoanhThuDAO.getTongHoaDonHoanDoi(fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);

                // 3. Tài chính tổng quan (theo bộ lọc)
                Map<String, Double> mapTong = thongKeDoanhThuDAO.getTongDoanhThuChiLoiNhuan(fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);
                r.tongDoanhThu = mapTong.get("doanhThu"); // Tổng tiền dương
                r.tongChi = mapTong.get("chi");           // Tổng tiền âm (ABS)
                r.tongLoiNhuan = mapTong.get("loiNhuan");

                // 4. Tính riêng Tiền Mặt / Chuyển Khoản cho các Card
                // Nếu User chọn "Tiền mặt", Card CK = 0.
                if (fIsTienMat != null) {
                    if (fIsTienMat == 1) { // Đang lọc tiền mặt
                        r.tongTienMat = r.tongDoanhThu;
                        r.tongChuyenKhoan = 0;
                    } else { // Đang lọc CK
                        r.tongTienMat = 0;
                        r.tongChuyenKhoan = r.tongDoanhThu;
                    }
                } else {
                    // Nếu chọn "Tất cả", gọi thêm 2 query để tách doanh thu từng loại
                    Map<String, Double> mTM = thongKeDoanhThuDAO.getTongDoanhThuChiLoiNhuan(fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, 1);
                    Map<String, Double> mCK = thongKeDoanhThuDAO.getTongDoanhThuChiLoiNhuan(fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, 0);
                    r.tongTienMat = mTM.get("doanhThu");
                    r.tongChuyenKhoan = mCK.get("doanhThu");
                }
                return r;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeDoanhThuResult r = get();
                    lblTongHDDaBanValue.setText(integerFormatter.format(r.tongHDTrongKhoang));
                    lblTongHDHoanDoiValue.setText(integerFormatter.format(r.tongHDHoanDoiTrongKhoang));
                    lblTongChiValue.setText(currencyFormatter.format(r.tongChi));

                    lblTongTienMatValue.setText(currencyFormatter.format(r.tongTienMat));
                    lblTongChuyenKhoanValue.setText(currencyFormatter.format(r.tongChuyenKhoan));
                    lblTongDoanhThuValue.setText(currencyFormatter.format(r.tongDoanhThu));

                    lblTongLoiNhuanValue.setText(currencyFormatter.format(r.tongLoiNhuan));

                    capNhatChartVaTable(r.thongKeChiTietTheoThoiGian, fChartTitle, fTitleLoai);
                } catch (Exception ex) {
                    handleLoadingError(ex, "Lỗi khi tải thống kê");
                }
            }
        };
        worker.execute();
    }

    private void capNhatChartVaTable(Map<String, ThongKeChiTietItem> data, String chartTitle, String reportTitle) {
        lblChiTietTitle.setText("Báo cáo thống kê chi tiết theo " + reportTitle.toLowerCase());

        if (data == null || data.isEmpty()) {
            capNhatChartRong("📉 Không có dữ liệu doanh thu");
            capNhatBangRong();
            btnExportExcel.setEnabled(false);
        } else {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            data.forEach((thoiGian, item) -> {
                dataset.addValue(item.tongDoanhThu, "Doanh thu", thoiGian);
                dataset.addValue(item.tongChi, "Chi phí", thoiGian);
            });

            JFreeChart barChart = ChartFactory.createBarChart(
                    "Doanh thu và Chi phí theo " + chartTitle.toLowerCase(),
                    "Thời gian", "Số tiền (VNĐ)", dataset,
                    PlotOrientation.VERTICAL, true, true, false);

            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(46, 204, 113)); // Doanh thu (Xanh)
            renderer.setSeriesPaint(1, new Color(231, 76, 60));  // Chi (Đỏ)
            renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance(new Locale("vi","VN")));

            CategoryAxis domainAxis = plot.getDomainAxis();
            if (dataset.getColumnCount() > 10) {
                domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            }

            ChartPanel cp = new ChartPanel(barChart);
            cp.setBackground(Color.WHITE);
            chartPanelContainer.removeAll();
            chartPanelContainer.add(cp, BorderLayout.CENTER);
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();

            // Fill Table
            chiTietTableModel.setRowCount(0);
            int stt = 1;
            for (Map.Entry<String, ThongKeChiTietItem> e : data.entrySet()) {
                ThongKeChiTietItem item = e.getValue();
                chiTietTableModel.addRow(new Object[]{
                        stt++, e.getKey(), item.soLuongHoaDonBan, item.soLuongHoaDonHoanDoi,
                        item.tongDoanhThu, item.tongChi, item.loiNhuan
                });
            }
            btnExportExcel.setEnabled(true);
        }
    }

    // ===== UI LAYOUT HELPERS =====

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension comboSize = new Dimension(250, 32);

        // HÀNG 0
        gbc.gridx = 0; gbc.gridy = 0;
        bar.add(new JLabel("Loại thời gian:"), gbc);
        gbc.gridx = 1;
        cbLoaiThoiGian.setPreferredSize(comboSize);
        bar.add(cbLoaiThoiGian, gbc);
        gbc.gridx = 2;
        filterSwitcher.setOpaque(false);
        filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);
        filterSwitcher.setPreferredSize(new Dimension(350, 32));
        bar.add(filterSwitcher, gbc);

        // HÀNG 1
        gbc.gridx = 0; gbc.gridy = 1;
        bar.add(new JLabel("Lọc tuyến:"), gbc);
        gbc.gridx = 1;
        cbLoaiTuyen.setPreferredSize(comboSize);
        bar.add(cbLoaiTuyen, gbc);

        JPanel panelGa = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelGa.setOpaque(false);
        panelGa.add(new JLabel("Ga đi:"));
        cbGaDi.setPreferredSize(comboSize);
        panelGa.add(cbGaDi);
        panelGa.add(new JLabel("Ga đến:"));
        cbGaDen.setPreferredSize(comboSize);
        panelGa.add(cbGaDen);

        JPanel panelThanhToan = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelThanhToan.setOpaque(false);
        panelThanhToan.add(new JLabel("Thanh toán:"));
        cbThanhToan.setPreferredSize(comboSize);
        panelThanhToan.add(cbThanhToan);

        JPanel panelCot2 = new JPanel(new GridLayout(2, 1, 0, 4));
        panelCot2.setOpaque(false);
        panelCot2.add(panelGa);
        panelCot2.add(panelThanhToan);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridheight = 2;
        bar.add(panelCot2, gbc);
        gbc.gridheight = 1;

        // HÀNG 2
        gbc.gridx = 0; gbc.gridy = 2;
        bar.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        cbNhanVien.setPreferredSize(comboSize);
        bar.add(cbNhanVien, gbc);

        // HÀNG 3: NÚT
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelButtons.setOpaque(false);
        btnTimKiem.setPreferredSize(new Dimension(120, 35));
        btnTimKiem.setBackground(new Color(33, 150, 83));
        btnTimKiem.setForeground(Color.WHITE);
        btnXoaBoLoc.setPreferredSize(new Dimension(120, 35));
        panelButtons.add(btnTimKiem);
        panelButtons.add(btnXoaBoLoc);
        bar.add(panelButtons, gbc);

        // Events
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

    private JLabel createValueLabel(String initialText) {
        JLabel label = new JLabel(initialText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Times New Roman", Font.BOLD, 18));
        return label;
    }

    // ===== DATA LOADING & EXPORT =====

    private void loadComboBoxesData() {
        try {
            List<String> tenGaList = thongKeVeDAO.getDanhSachTenGa();
            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();
            if (tenGaList.isEmpty()) {
                cbGaDi.addItem("Lỗi"); cbGaDen.addItem("Lỗi");
            } else {
                for (String tenGa : tenGaList) {
                    cbGaDi.addItem(tenGa);
                    cbGaDen.addItem(tenGa);
                }
            }
            cbGaDi.setSelectedItem("Sài Gòn");
            cbGaDen.setSelectedItem("Hà Nội");
        } catch (Exception e) { e.printStackTrace(); }

        try {
            Map<String, String> dsNhanVien = thongKeVeDAO.getDanhSachNhanVien();
            nhanVienMap.clear();
            cbNhanVien.removeAllItems();
            cbNhanVien.addItem("Tất cả");
            for (Map.Entry<String, String> entry : dsNhanVien.entrySet()) {
                String id = entry.getKey();
                String ten = entry.getValue();
                String displayText = String.format("%s (%s)", ten, id);
                nhanVienMap.put(displayText, id);
                cbNhanVien.addItem(displayText);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCaoDoanhThu_" + LocalDate.now() + ".xlsx"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");

            try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream os = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("ChiTiet");
                // Style cơ bản
                CellStyle headerStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = wb.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                // Header
                Row header = sheet.createRow(0);
                for (int i=0; i<chiTietTableModel.getColumnCount(); i++) {
                    Cell c = header.createCell(i);
                    c.setCellValue(chiTietTableModel.getColumnName(i));
                    c.setCellStyle(headerStyle);
                }

                // Data
                for (int i=0; i<chiTietTableModel.getRowCount(); i++) {
                    Row r = sheet.createRow(i+1);
                    for (int j=0; j<chiTietTableModel.getColumnCount(); j++) {
                        Object val = chiTietTableModel.getValueAt(i,j);
                        if(val != null) {
                            if (val instanceof Number) r.createCell(j).setCellValue(((Number)val).doubleValue());
                            else r.createCell(j).setCellValue(val.toString());
                        }
                    }
                }
                for(int i=0; i<chiTietTableModel.getColumnCount(); i++) sheet.autoSizeColumn(i);

                wb.write(os);
                JOptionPane.showMessageDialog(this, "Xuất thành công!");
                Desktop.getDesktop().open(f);
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage()); }
        }
    }

    // ===== UTILS NHỎ =====
    private void setupTableDetails() {
        tableChiTiet.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(28);
        JTableHeader header = tableChiTiet.getTableHeader();
        header.setFont(new Font("Times New Roman", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) value = currencyFormatter.format(value);
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        TableColumnModel cm = tableChiTiet.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40); cm.getColumn(0).setCellRenderer(centerRenderer);
        cm.getColumn(1).setPreferredWidth(100); cm.getColumn(1).setCellRenderer(centerRenderer);
        cm.getColumn(2).setPreferredWidth(80); cm.getColumn(2).setCellRenderer(centerRenderer);
        cm.getColumn(3).setPreferredWidth(80); cm.getColumn(3).setCellRenderer(centerRenderer);
        for (int i = 4; i <= 6; i++) {
            cm.getColumn(i).setPreferredWidth(140);
            cm.getColumn(i).setCellRenderer(currencyRenderer);
        }
    }

    private void handleLoadingError(Exception ex, String msg) {
        setCursor(Cursor.getDefaultCursor());
        ex.printStackTrace();
        capNhatChartRong("Lỗi: " + ex.getMessage());
        JOptionPane.showMessageDialog(this, msg + "\n" + ex.getMessage());
    }

    private boolean kiemTraKhoangNgayHopLe(Date d1, Date d2) { return d1 != null && d2 != null && (d2.after(d1) || isSameDay(d1, d2)); }
    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(); c1.setTime(d1);
        Calendar c2 = Calendar.getInstance(); c2.setTime(d2);
        return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }
    private void capNhatChartRong(String m) {
        chartPanelContainer.removeAll();
        JLabel l = new JLabel(m, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.ITALIC, 16));
        chartPanelContainer.add(l, BorderLayout.CENTER);
        chartPanelContainer.revalidate(); chartPanelContainer.repaint();
    }
    private void capNhatBangRong() { chiTietTableModel.setRowCount(0); }
    private JPanel taoPanelTongQuan() { return new JPanel(new BorderLayout()); }
    private JPanel taoPanelChiTiet(JLabel t, JTable tbl, JPanel bot) {
        JPanel p = new JPanel(new BorderLayout(0,10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(5,5,5,5));
        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    // Components lọc thời gian
    private JPanel buildTatCaFilter() { return new JPanel(); }
    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        tuNgay.setDateFormatString("dd/MM/yyyy"); denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setPreferredSize(new Dimension(140, 28)); denNgay.setPreferredSize(new Dimension(140, 28));
        tuNgay.setDate(new Date()); denNgay.setDate(new Date());
        p.add(new JLabel("Từ:")); p.add(tuNgay); p.add(new JLabel("Đến:")); p.add(denNgay);
        return p;
    }
    private JPanel buildThangFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        if (cbTuThang.getItemCount() == 0) {
            for (int i = 1; i <= 12; i++) { cbTuThang.addItem("Tháng " + i); cbDenThang.addItem("Tháng " + i); }
            int now = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= now + 5; y++) { cbTuNamThang.addItem(y); cbDenNamThang.addItem(y); }
            cbTuThang.setSelectedIndex(0); cbDenThang.setSelectedIndex(11);
            cbTuNamThang.setSelectedItem(now); cbDenNamThang.setSelectedItem(now);
        }
        Dimension d = new Dimension(100, 28);
        cbTuThang.setPreferredSize(d); cbDenThang.setPreferredSize(d);
        cbTuNamThang.setPreferredSize(d); cbDenNamThang.setPreferredSize(d);
        p.add(new JLabel("Từ:")); p.add(cbTuThang); p.add(cbTuNamThang);
        p.add(new JLabel("Đến:")); p.add(cbDenThang); p.add(cbDenNamThang);
        return p;
    }
    private JPanel buildNamFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        if (cbTuNam.getItemCount() == 0) {
            int now = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= now + 5; y++) { cbTuNam.addItem(y); cbDenNam.addItem(y); }
            cbTuNam.setSelectedItem(now); cbDenNam.setSelectedItem(now);
        }
        cbTuNam.setPreferredSize(new Dimension(100, 28)); cbDenNam.setPreferredSize(new Dimension(100, 28));
        p.add(new JLabel("Từ năm:")); p.add(cbTuNam); p.add(new JLabel("Đến năm:")); p.add(cbDenNam);
        return p;
    }

    private void xoaBoLoc() {
        cbLoaiThoiGian.setSelectedIndex(0);
        tuNgay.setDate(new Date()); denNgay.setDate(new Date());
        cbLoaiTuyen.setSelectedIndex(0);
        if (cbNhanVien.getItemCount() > 0) cbNhanVien.setSelectedIndex(0);
        if (cbThanhToan.getItemCount() > 0) cbThanhToan.setSelectedIndex(0);
        xuLyThongKe();
    }
}