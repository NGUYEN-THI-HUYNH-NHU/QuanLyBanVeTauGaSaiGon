package gui.application.form.thongKe;

import dao.ThongKeKhachHang_DAO;
import dao.ThongKeKhachHang_DAO.KhachHangRFM;

// --- JFreeChart (Biểu đồ) ---
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

// --- Apache POI (Xuất Excel) ---
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// --- Swing & AWT ---
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Panel Thống kê Khách hàng & Phân tích RFM (Recency - Frequency - Monetary).
 * Tính năng:
 * - Lọc theo thời gian, đối tượng.
 * - Phân loại 5 nhóm: VIP, Thân thiết, Quay lại (2-4 lần), Mới, Ngủ đông.
 * - Xuất Excel chuẩn định dạng .xlsx.
 */
public class PanelThongKeKhachHang extends JPanel {

    private final ThongKeKhachHang_DAO thongKeKHDao;
    private static final Logger LOGGER = Logger.getLogger(PanelThongKeKhachHang.class.getName());

    // Định dạng dữ liệu hiển thị
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===== Components Lọc Thời Gian =====
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

    // ===== Components Lọc Nghiệp Vụ =====
    private final JComboBox<String> cbLoaiDoiTuong;
    private final JComboBox<String> cbPhanLoai;

    // ===== Nút bấm =====
    private final JButton btnTimKiem;
    private final JButton btnXoaBoLoc;

    // ===== Labels Card (Thẻ hiển thị số liệu tổng) =====
    private final JLabel lblTongKhachHangValue;
    private final JLabel lblKhachHangMoiValue;
    private final JLabel lblKhachHangQuayLaiValue; // Target chính: 2-4 lần
    private final JLabel lblTongDoanhThuValue;

    // ===== Bảng & Biểu đồ =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    // ===== Struct chứa kết quả thống kê (Dùng cho SwingWorker) =====
    private static class ThongKeKHResult {
        Map<String, KhachHangRFM> chiTietKhachHang;
        int tongSoKhachHang;
        double tongDoanhThu;

        // Số lượng từng nhóm
        long countVIP;
        long countThanThiet;
        long countQuayLai; // Nhóm xương sống (2-4 lần)
        long countMoi;
        long countNguDong;
    }

    public PanelThongKeKhachHang() {
        this.thongKeKHDao = new ThongKeKhachHang_DAO();

        // 1. Khởi tạo Components
        cbLoaiThoiGian = new JComboBox<>(new String[]{"Tất cả", "Theo ngày", "Theo tháng", "Theo năm"});
        tuNgay = new JDateChooser();
        denNgay = new JDateChooser();
        cbTuThang = new JComboBox<>(); cbDenThang = new JComboBox<>();
        cbTuNamThang = new JComboBox<>(); cbDenNamThang = new JComboBox<>();
        cbTuNam = new JComboBox<>(); cbDenNam = new JComboBox<>();
        filterSwitcher = new JPanel(new CardLayout());

        cbLoaiDoiTuong = new JComboBox<>(new String[]{"Tất cả"});
        // Combo box lọc trạng thái khách hàng
        cbPhanLoai = new JComboBox<>(new String[]{"Tất cả", "VIP", "Thân thiết", "Khách quay lại", "Khách mới", "Ngủ đông"});

        btnTimKiem = new JButton("Tìm kiếm");
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        setupButtons();

        // Khởi tạo label giá trị
        lblTongKhachHangValue = createValueLabel("...");
        lblKhachHangMoiValue = createValueLabel("...");
        lblKhachHangQuayLaiValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");

        // 2. Layout Chính
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "THỐNG KÊ KHÁCH HÀNG",
                        javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16), new Color(0, 110, 185)
                ), new EmptyBorder(5, 5, 5, 5)));

        // 3. Top Panel (Bộ lọc + Cards Thông tin)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        topPanel.add(buildFilterBar()); // Thanh lọc

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(180, 180, 180));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(sep);
        topPanel.add(Box.createVerticalStrut(15));

        // --- CARDS TỔNG QUAN ---
        JPanel infoWrapper = new JPanel(new GridLayout(1, 4, 20, 20));
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));

        infoWrapper.add(createCard("Tổng Khách Hàng", lblTongKhachHangValue, new Color(52, 73, 94)));
        infoWrapper.add(createCard("Khách Hàng Mới", lblKhachHangMoiValue, new Color(46, 204, 113)));
        infoWrapper.add(createCard("Khách Quay Lại", lblKhachHangQuayLaiValue, new Color(41, 128, 185)));
        infoWrapper.add(createCard("Tổng Doanh Thu", lblTongDoanhThuValue, new Color(243, 156, 18)));

        topPanel.add(infoWrapper);
        add(topPanel, BorderLayout.NORTH);

        // 4. Center Panel (Biểu đồ + Bảng)
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        // Tab 1: Biểu đồ
        chartPanelContainer = taoPanelTongQuan();

        // Tab 2: Bảng chi tiết
        String[] columnNames = {
                "Mã KH", "Tên Khách Hàng", "Khu Vực", "Loại Đối Tượng",
                "Số Lần Mua", "Tổng Chi Tiêu (VNĐ)", "Lần Mua Cuối", "Phân Loại"
        };
        // TableModel lưu trữ đúng kiểu dữ liệu (Double, Integer) để sort và format
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4) return Integer.class; // Số lần mua
                if (columnIndex == 5) return Double.class;  // Tiền
                return String.class;
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails(); // Cài đặt render màu sắc, định dạng tiền

        lblChiTietTitle = new JLabel("Chi tiết danh sách khách hàng", JLabel.CENTER);
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

        tab.addTab("Cơ cấu khách hàng", chartPanelContainer);
        tab.addTab("Chi tiết khách hàng", panelChiTietContainer);

        add(tab, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadComboBoxesData();
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    private void setupButtons() {
        Dimension btnSize = new Dimension(120, 32);
        btnTimKiem.setFont(new Font("Arial", Font.BOLD, 13));
        btnTimKiem.setBackground(new Color(33, 150, 83));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setPreferredSize(btnSize);
        btnTimKiem.addActionListener(e -> xuLyThongKe());

        btnXoaBoLoc.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaBoLoc.setBackground(new Color(108, 117, 125));
        btnXoaBoLoc.setForeground(Color.WHITE);
        btnXoaBoLoc.setPreferredSize(btnSize);
        btnXoaBoLoc.addActionListener(e -> xoaBoLoc());
    }

    // --- Các hàm dựng giao diện bộ lọc (Giữ nguyên logic cũ nhưng làm gọn) ---
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 1: Thời gian
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        bar.add(new JLabel("Loại thời gian:"), gbc);
        gbc.gridx = 1;
        cbLoaiThoiGian.setPreferredSize(new Dimension(180, 32));
        bar.add(cbLoaiThoiGian, gbc);
        gbc.gridx = 2; gbc.weightx = 1.0;
        filterSwitcher.setOpaque(false);
        filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);
        bar.add(filterSwitcher, gbc);
        gbc.gridx = 3; gbc.weightx = 0.0;
        bar.add(btnXoaBoLoc, gbc);

        // Hàng 2: Nghiệp vụ
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setOpaque(false);
        row2.add(new JLabel("Loại đối tượng:"));
        cbLoaiDoiTuong.setPreferredSize(new Dimension(150, 32));
        row2.add(cbLoaiDoiTuong);
        row2.add(new JLabel("Phân loại:"));
        cbPhanLoai.setPreferredSize(new Dimension(150, 32));
        row2.add(cbPhanLoai);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        bar.add(row2, gbc);
        gbc.gridx = 3; gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        bar.add(btnTimKiem, gbc);

        // Sự kiện đổi loại thời gian
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
        ((CardLayout) filterSwitcher.getLayout()).show(filterSwitcher, CARD_TATCA);
        return bar;
    }

    // Các hàm phụ trợ dựng Panel thời gian (Giản lược để code ngắn gọn)
    private JPanel buildTatCaFilter() { JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); p.setOpaque(false); p.add(new JLabel("Toàn bộ thời gian")); return p; }
    private JPanel buildNgayFilter() { JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); p.setOpaque(false); tuNgay.setDateFormatString("dd/MM/yyyy"); denNgay.setDateFormatString("dd/MM/yyyy"); tuNgay.setDate(new Date()); denNgay.setDate(new Date()); p.add(new JLabel("Từ:")); p.add(tuNgay); p.add(new JLabel("Đến:")); p.add(denNgay); return p; }
    private JPanel buildThangFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); p.setOpaque(false);
        if(cbTuThang.getItemCount()==0) { for(int i=1;i<=12;i++) {cbTuThang.addItem("Tháng "+i); cbDenThang.addItem("Tháng "+i);} int y=Calendar.getInstance().get(Calendar.YEAR); for(int i=2020;i<=y+2;i++){cbTuNamThang.addItem(i); cbDenNamThang.addItem(i);} cbTuThang.setSelectedIndex(0); cbDenThang.setSelectedIndex(11); cbTuNamThang.setSelectedItem(y); cbDenNamThang.setSelectedItem(y); }
        p.add(new JLabel("Từ:")); p.add(cbTuThang); p.add(cbTuNamThang); p.add(new JLabel("Đến:")); p.add(cbDenThang); p.add(cbDenNamThang); return p;
    }
    private JPanel buildNamFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); p.setOpaque(false);
        if(cbTuNam.getItemCount()==0) { int y=Calendar.getInstance().get(Calendar.YEAR); for(int i=2020;i<=y+5;i++){cbTuNam.addItem(i); cbDenNam.addItem(i);} cbTuNam.setSelectedItem(y); cbDenNam.setSelectedItem(y); }
        p.add(new JLabel("Từ năm:")); p.add(cbTuNam); p.add(new JLabel("Đến năm:")); p.add(cbDenNam); return p;
    }

    private void loadComboBoxesData() {
        try {
            List<String> loaiDTList = thongKeKHDao.getDanhSachLoaiDoiTuong();
            cbLoaiDoiTuong.removeAllItems(); cbLoaiDoiTuong.addItem("Tất cả");
            for (String ldt : loaiDTList) if (ldt != null) cbLoaiDoiTuong.addItem(ldt);
        } catch (SQLException e) { LOGGER.log(Level.WARNING, "Lỗi tải loại đối tượng", e); }
    }

    // ================== HÀM XỬ LÝ LOGIC CHÍNH ==================
    private void xuLyThongKe() {
        // 1. Xác định khoảng thời gian
        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        LocalDate fromDate, toDate;
        try {
            if ("Theo ngày".equals(loaiThoiGian)) {
                if (tuNgay.getDate() == null || denNgay.getDate() == null) return;
                fromDate = tuNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                toDate = denNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if ("Theo tháng".equals(loaiThoiGian)) {
                int m1 = cbTuThang.getSelectedIndex() + 1; int y1 = (Integer) cbTuNamThang.getSelectedItem();
                int m2 = cbDenThang.getSelectedIndex() + 1; int y2 = (Integer) cbDenNamThang.getSelectedItem();
                fromDate = LocalDate.of(y1, m1, 1);
                toDate = LocalDate.of(y2, m2, 1).plusMonths(1).minusDays(1);
            } else if ("Theo năm".equals(loaiThoiGian)) {
                int y1 = (Integer) cbTuNam.getSelectedItem(); int y2 = (Integer) cbDenNam.getSelectedItem();
                fromDate = LocalDate.of(y1, 1, 1);
                toDate = LocalDate.of(y2, 12, 31);
            } else {
                fromDate = LocalDate.of(2000, 1, 1);
                toDate = LocalDate.now();
            }
            if (toDate.isBefore(fromDate)) { JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!"); return; }
        } catch (Exception e) { return; }

        String loaiDoiTuong = (String) cbLoaiDoiTuong.getSelectedItem();
        String phanLoai = (String) cbPhanLoai.getSelectedItem();

        // 2. Chạy Thread ngầm để lấy dữ liệu
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Reset label tạm thời
        lblTongKhachHangValue.setText("...");
        lblKhachHangMoiValue.setText("...");
        lblKhachHangQuayLaiValue.setText("...");
        lblTongDoanhThuValue.setText("...");

        final LocalDate finalFrom = fromDate;
        final LocalDate finalTo = toDate;

        SwingWorker<ThongKeKHResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeKHResult doInBackground() throws Exception {
                ThongKeKHResult res = new ThongKeKHResult();
                // Gọi DAO lấy danh sách chi tiết (Đã có logic phân loại trong SQL)
                res.chiTietKhachHang = thongKeKHDao.getThongKeKhachHang(finalFrom, finalTo, null, loaiDoiTuong, phanLoai);

                res.tongSoKhachHang = res.chiTietKhachHang.size();
                res.tongDoanhThu = res.chiTietKhachHang.values().stream().mapToDouble(k -> k.tongChiTieu).sum();

                // Đếm số lượng từng nhóm dựa trên trường 'phanLoai' trả về từ DAO
                Map<String, Long> counts = res.chiTietKhachHang.values().stream()
                        .collect(Collectors.groupingBy(k -> k.phanLoai, Collectors.counting()));

                res.countMoi = counts.getOrDefault("Khách mới", 0L);
                res.countQuayLai = counts.getOrDefault("Khách quay lại", 0L);
                res.countThanThiet = counts.getOrDefault("Thân thiết", 0L);
                res.countVIP = counts.getOrDefault("VIP", 0L);
                res.countNguDong = counts.getOrDefault("Ngủ đông", 0L);

                return res;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeKHResult result = get();
                    // Cập nhật Cards
                    lblTongKhachHangValue.setText(integerFormatter.format(result.tongSoKhachHang));
                    lblKhachHangMoiValue.setText(integerFormatter.format(result.countMoi));
                    lblKhachHangQuayLaiValue.setText(integerFormatter.format(result.countQuayLai));
                    lblTongDoanhThuValue.setText(currencyFormatter.format(result.tongDoanhThu));

                    // Cập nhật Chart & Table
                    capNhatChartVaTable(result);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PanelThongKeKhachHang.this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void xoaBoLoc() {
        cbLoaiThoiGian.setSelectedIndex(0);
        tuNgay.setDate(new Date()); denNgay.setDate(new Date());
        cbLoaiDoiTuong.setSelectedIndex(0);
        cbPhanLoai.setSelectedIndex(0);
        xuLyThongKe();
    }

    private void capNhatChartVaTable(ThongKeKHResult res) {
        // 1. Vẽ Biểu đồ tròn 5 phần
        DefaultPieDataset dataset = new DefaultPieDataset();
        if (res.countMoi > 0) dataset.setValue("Mới (1 lần)", res.countMoi);
        if (res.countQuayLai > 0) dataset.setValue("Quay lại (2-4 lần)", res.countQuayLai);
        if (res.countThanThiet > 0) dataset.setValue("Thân thiết (>5 lần)", res.countThanThiet);
        if (res.countVIP > 0) dataset.setValue("VIP", res.countVIP);
        if (res.countNguDong > 0) dataset.setValue("Ngủ đông", res.countNguDong);

        JFreeChart pieChart = ChartFactory.createPieChart("Cơ cấu khách hàng", dataset, true, true, false);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        // Set màu sắc nhận diện
        plot.setSectionPaint("Mới (1 lần)", new Color(46, 204, 113));       // Xanh lá
        plot.setSectionPaint("Quay lại (2-4 lần)", new Color(52, 152, 219)); // Xanh dương
        plot.setSectionPaint("Thân thiết (>5 lần)", new Color(155, 89, 182)); // Tím
        plot.setSectionPaint("VIP", new Color(241, 196, 15));              // Vàng
        plot.setSectionPaint("Ngủ đông", new Color(149, 165, 166));        // Xám

        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();

        // 2. Cập nhật Bảng
        chiTietTableModel.setRowCount(0);
        if (res.chiTietKhachHang != null) {
            for (KhachHangRFM kh : res.chiTietKhachHang.values()) {
                chiTietTableModel.addRow(new Object[]{
                        kh.khachHangID,
                        kh.hoTen,
                        kh.khuVuc,
                        kh.loaiDoiTuong,
                        kh.soLanMua,     // Integer
                        kh.tongChiTieu,  // Double
                        kh.lanMuaCuoi != null ? kh.lanMuaCuoi.format(dateFormatter) : "",
                        kh.phanLoai
                });
            }
        }
        btnExportExcel.setEnabled(chiTietTableModel.getRowCount() > 0);
    }

    // ================== HÀM XUẤT EXCEL (APACHE POI) ==================
    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất dữ liệu khách hàng ra Excel");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Workbook (*.xlsx)", "xlsx"));

        String timeStamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
        chooser.setSelectedFile(new File("ThongKeKhachHang_" + timeStamp + ".xlsx"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".xlsx")) {
                file = new File(file.getParentFile(), file.getName() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Chi Tiết Khách Hàng");

                // --- TẠO STYLES ---
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                // SỬA LỖI TẠI ĐÂY: Chỉ định rõ Font của POI
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                setBorder(headerStyle);

                CellStyle currencyStyle = workbook.createCellStyle();
                DataFormat format = workbook.createDataFormat();
                currencyStyle.setDataFormat(format.getFormat("#,##0 \"₫\""));
                currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
                setBorder(currencyStyle);

                CellStyle centerStyle = workbook.createCellStyle();
                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                setBorder(centerStyle);

                CellStyle normalStyle = workbook.createCellStyle();
                setBorder(normalStyle);

                // --- GHI HEADER ---
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < chiTietTableModel.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(chiTietTableModel.getColumnName(i));
                    cell.setCellStyle(headerStyle);
                }

                // --- GHI DATA ---
                for (int r = 0; r < chiTietTableModel.getRowCount(); r++) {
                    Row row = sheet.createRow(r + 1);
                    for (int c = 0; c < chiTietTableModel.getColumnCount(); c++) {
                        Cell cell = row.createCell(c);
                        Object val = chiTietTableModel.getValueAt(r, c);

                        if (val != null) {
                            if (c == 4 && val instanceof Number) {
                                cell.setCellValue(((Number) val).intValue());
                                cell.setCellStyle(centerStyle);
                            } else if (c == 5 && val instanceof Number) {
                                cell.setCellValue(((Number) val).doubleValue());
                                cell.setCellStyle(currencyStyle);
                            } else if (c == 0 || c == 6) {
                                cell.setCellValue(val.toString());
                                cell.setCellStyle(centerStyle);
                            } else {
                                cell.setCellValue(val.toString());
                                cell.setCellStyle(normalStyle);
                            }
                        } else {
                            cell.setCellStyle(normalStyle);
                        }
                    }
                }

                // Auto size cột
                for(int i=0; i<chiTietTableModel.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(this, "Xuất file thành công!");
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (Exception ex) {}

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    // --- Cấu hình bảng hiển thị (Renderer) ---
    private void setupTableDetails() {
        tableChiTiet.setRowHeight(28);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa Mã, Số lần, Ngày
        tableChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Format tiền tệ
        tableChiTiet.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                if(v instanceof Number) v = currencyFormatter.format(v);
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(t, v, s, f, r, c);
            }
        });

        // Tô màu cột Phân loại
        tableChiTiet.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = (String) v;
                Font font = comp.getFont();
                comp.setFont(font.deriveFont(Font.BOLD));

                if ("VIP".equals(val)) comp.setForeground(new Color(243, 156, 18)); // Vàng
                else if ("Khách quay lại".equals(val)) comp.setForeground(new Color(41, 128, 185)); // Xanh
                else if ("Ngủ đông".equals(val)) comp.setForeground(Color.GRAY);
                else comp.setForeground(Color.BLACK);

                setHorizontalAlignment(JLabel.CENTER);
                return comp;
            }
        });
    }

    // --- Helpers tạo giao diện ---
    private JPanel taoPanelTongQuan() { JPanel p = new JPanel(new BorderLayout()); p.setBackground(Color.WHITE); p.add(new JLabel("📊 Nhấn Tìm kiếm để xem biểu đồ", SwingConstants.CENTER), BorderLayout.CENTER); return p; }
    private JPanel taoPanelChiTiet(JLabel title, JTable tbl, JPanel btm) { JPanel p = new JPanel(new BorderLayout(0, 10)); p.setBackground(Color.WHITE); p.add(title, BorderLayout.NORTH); p.add(new JScrollPane(tbl), BorderLayout.CENTER); p.add(btm, BorderLayout.SOUTH); return p; }
    private JLabel createValueLabel(String t) { JLabel l = new JLabel(t, SwingConstants.CENTER); l.setForeground(Color.WHITE); l.setFont(new Font("Times New Roman", Font.BOLD, 26)); return l; }
    private JPanel createCard(String t, JLabel v, Color c) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        JLabel tl = new JLabel(t, SwingConstants.CENTER); tl.setForeground(Color.WHITE); tl.setFont(new Font("Times New Roman", Font.BOLD, 16));
        p.setBackground(c); p.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        p.add(tl, BorderLayout.NORTH); p.add(v, BorderLayout.CENTER);
        return p;
    }
}