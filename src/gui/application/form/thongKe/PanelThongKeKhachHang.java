package gui.application.form.thongKe;

import dao.ThongKeKhachHang_DAO; // <<< DAO MỚI
import dao.ThongKeKhachHang_DAO.KhachHangRFM; // <<< Lớp Item MỚI
// JFreeChart (Pie Chart)
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
// Apache POI
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// Swing, AWT, Calendar
import com.toedter.calendar.JDateChooser;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Panel hiển thị thống kê Khách hàng (RFM).
 */
public class PanelThongKeKhachHang extends JPanel {

    // ===== DAO =====
    private final ThongKeKhachHang_DAO thongKeKHDao;

    // Logger
    private static final Logger LOGGER = Logger.getLogger(PanelThongKeKhachHang.class.getName());

    // ===== Định dạng =====
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat integerFormatter = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===== Các thành phần lọc =====
    private final JDateChooser tuNgay;
    private final JDateChooser denNgay;
    private final JComboBox<String> cbKhuVuc;
    private final JComboBox<String> cbLoaiDoiTuong;
    private final JComboBox<String> cbPhanLoai; // (VIP, Mới,...)
    private final JButton btnTimKiem;

    // ===== Các JLabel chứa giá trị trên card =====
    private final JLabel lblTongKhachHangValue;
    private final JLabel lblKhachHangMoiValue;
    private final JLabel lblKhachHangQuayLaiValue;
    private final JLabel lblTongDoanhThuValue;

    // ===== Biểu đồ & Bảng =====
    private final JPanel chartPanelContainer; // Chứa biểu đồ tròn
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    // ===== Lớp chứa kết quả thống kê =====
    private static class ThongKeKHResult {
        Map<String, KhachHangRFM> chiTietKhachHang; // Dữ liệu chính
        // Dữ liệu tổng hợp (tính từ chiTietKhachHang)
        int tongSoKhachHang;
        int soKhachHangMoi;
        int soKhachHangQuayLai;
        double tongDoanhThu;
    }

    public PanelThongKeKhachHang() {
        this.thongKeKHDao = new ThongKeKhachHang_DAO();

        // Khởi tạo components
        tuNgay = new JDateChooser();
        denNgay = new JDateChooser();
        cbKhuVuc = new JComboBox<>(new String[]{"Tất cả"});
        cbLoaiDoiTuong = new JComboBox<>(new String[]{"Tất cả"});
        cbPhanLoai = new JComboBox<>(new String[]{"Tất cả", "VIP", "Thân thiết", "Khách mới", "Ngủ đông", "Khách quay lại"});
        btnTimKiem = new JButton("Tìm kiếm");

        // Khởi tạo labels
        lblTongKhachHangValue = createValueLabel("...");
        lblKhachHangMoiValue = createValueLabel("...");
        lblKhachHangQuayLaiValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");

        // --- Cấu hình Layout chính ---
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);

        // --- Tiêu đề (TitledBorder) ---
        javax.swing.border.Border lineBorder = BorderFactory.createLineBorder(new Color(200, 200, 200), 1);
        javax.swing.border.Border titledBorder = BorderFactory.createTitledBorder(
                lineBorder, "Thống kê khách hàng",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16), new Color(0, 110, 185)
        );
        setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(5, 5, 5, 5)));

        // ===== Khu vực NORTH: Thanh lọc và Cards =====
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);

        // --- Thanh lọc ---
        topPanel.add(buildFilterBar(), BorderLayout.NORTH);

        // --- Cards tổng quan (4 card) ---
        JPanel infoWrapper = new JPanel(new GridLayout(1, 4, 15, 15)); // 1 hàng, 4 cột
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        infoWrapper.add(createCard("Tổng số khách hàng (trong kỳ)", lblTongKhachHangValue, new Color(52, 152, 219)));
        infoWrapper.add(createCard("Khách hàng mới (trong kỳ)", lblKhachHangMoiValue, new Color(46, 204, 113)));
        infoWrapper.add(createCard("Khách hàng quay lại (trong kỳ)", lblKhachHangQuayLaiValue, new Color(155, 89, 182)));
        infoWrapper.add(createCard("Tổng doanh thu (từ nhóm này)", lblTongDoanhThuValue, new Color(243, 156, 18)));

        topPanel.add(infoWrapper, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // ===== Khu vực CENTER: Tabs biểu đồ & chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        // --- Tạo Table Model ---
        String[] columnNames = {
                "Mã KH", "Tên Khách Hàng", "Khu Vực", "Loại Đối Tượng",
                "Số Lần Mua", "Tổng Chi Tiêu (VNĐ)", "Lần Mua Cuối", "Phân Loại"
        };
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 4 -> Integer.class; // Số Lần Mua
                    case 5 -> Double.class; // Tổng Chi Tiêu
                    default -> String.class;
                };
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails();

        // --- Tạo Panel Chi Tiết ---
        lblChiTietTitle = new JLabel("Chi tiết khách hàng", JLabel.CENTER);
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

        // ===== Load dữ liệu ban đầu =====
        loadComboBoxesData();
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
        bar.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 1;
        tuNgay.setPreferredSize(new Dimension(120, 30));
        tuNgay.setDate(Date.from(LocalDate.now().minusMonths(6).atStartOfDay(ZoneId.systemDefault()).toInstant())); // Mặc định 6 tháng trước
        bar.add(tuNgay, gbc);

        gbc.gridx = 2;
        bar.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 3;
        denNgay.setPreferredSize(new Dimension(120, 30));
        denNgay.setDate(new Date()); // Mặc định hôm nay
        bar.add(denNgay, gbc);

        // === Hàng 1: Lọc nghiệp vụ ===
        gbc.gridx = 0; gbc.gridy = 1;
        bar.add(new JLabel("Khu vực:"), gbc);
        gbc.gridx = 1;
        cbKhuVuc.setPreferredSize(new Dimension(120, 30));
        bar.add(cbKhuVuc, gbc);

        gbc.gridx = 2;
        bar.add(new JLabel("Loại đối tượng:"), gbc);
        gbc.gridx = 3;
        cbLoaiDoiTuong.setPreferredSize(new Dimension(120, 30));
        bar.add(cbLoaiDoiTuong, gbc);

        gbc.gridx = 4;
        bar.add(new JLabel("Phân loại:"), gbc);
        gbc.gridx = 5;
        cbPhanLoai.setPreferredSize(new Dimension(120, 30));
        bar.add(cbPhanLoai, gbc);

        // === Nút Tìm kiếm ===
        btnTimKiem.setBackground(new Color(103, 194, 103));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setFont(new Font("Arial", Font.BOLD, 14));
        btnTimKiem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTimKiem.setPreferredSize(new Dimension(120, 40));

        gbc.gridx = 6; gbc.gridy = 0; // Cột cuối
        gbc.gridheight = 2; // Cao 2 hàng
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1.0; // Đẩy nút về bên phải
        gbc.insets = new Insets(5, 15, 5, 5);
        bar.add(btnTimKiem, gbc);

        // Gắn sự kiện
        btnTimKiem.addActionListener(e -> xuLyThongKe());

        return bar;
    }

    /**
     * Tải dữ liệu cho các JComboBox (Khu Vực, Loại Đối Tượng)
     */
    private void loadComboBoxesData() {
        // Tải Khu Vực
        try {
            List<String> khuVucList = thongKeKHDao.getDanhSachKhuVuc();
            cbKhuVuc.removeAllItems();
            cbKhuVuc.addItem("Tất cả");
            for (String kv : khuVucList) {
                if (kv != null && !kv.trim().isEmpty()) {
                    cbKhuVuc.addItem(kv);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi khi tải danh sách khu vực", e);
            cbKhuVuc.addItem("Lỗi CSDL");
        }

        // Tải Loại Đối Tượng
        try {
            List<String> loaiDTList = thongKeKHDao.getDanhSachLoaiDoiTuong();
            cbLoaiDoiTuong.removeAllItems();
            cbLoaiDoiTuong.addItem("Tất cả");
            for (String ldt : loaiDTList) {
                if (ldt != null && !ldt.trim().isEmpty()) {
                    cbLoaiDoiTuong.addItem(ldt);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi khi tải danh sách loại đối tượng", e);
            cbLoaiDoiTuong.addItem("Lỗi CSDL");
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

    // --- Hàm cấu hình JTable ---
    private void setupTableDetails() {
        tableChiTiet.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(28);
        JTableHeader header = tableChiTiet.getTableHeader();
        header.setFont(new Font("Times New Roman", Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));
        tableChiTiet.setAutoCreateRowSorter(true); // Cho phép sắp xếp

        // Căn lề
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Định dạng tiền
        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) {
                    value = currencyFormatter.format(value);
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        TableColumnModel columnModel = tableChiTiet.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80); // Mã KH
        columnModel.getColumn(1).setPreferredWidth(150); // Tên KH
        columnModel.getColumn(2).setPreferredWidth(100); // Khu Vực
        columnModel.getColumn(3).setPreferredWidth(100); // Loại ĐT
        columnModel.getColumn(4).setPreferredWidth(80); // Số Lần Mua
        columnModel.getColumn(4).setCellRenderer(rightRenderer);
        columnModel.getColumn(5).setPreferredWidth(120); // Tổng Chi Tiêu
        columnModel.getColumn(5).setCellRenderer(currencyRenderer);
        columnModel.getColumn(6).setPreferredWidth(100); // Lần Mua Cuối
        columnModel.getColumn(6).setCellRenderer(centerRenderer);
        columnModel.getColumn(7).setPreferredWidth(100); // Phân Loại
        columnModel.getColumn(7).setCellRenderer(centerRenderer);
    }


    // ================== HÀM XỬ LÝ CHÍNH ==================

    private void xuLyThongKe() {
        // --- 1. LẤY GIÁ TRỊ TỪ CÁC BỘ LỌC ---
        Date utilTuNgay = tuNgay.getDate();
        Date utilDenNgay = denNgay.getDate();
        if (!kiemTraKhoangNgayHopLe(utilTuNgay, utilDenNgay)) return;

        LocalDate fromLocalDate = utilTuNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = utilDenNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String khuVuc = (String) cbKhuVuc.getSelectedItem();
        String loaiDoiTuong = (String) cbLoaiDoiTuong.getSelectedItem();
        String phanLoai = (String) cbPhanLoai.getSelectedItem();

        // --- 3. Hiển thị loading ---
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongKhachHangValue.setText("...");
        lblKhachHangMoiValue.setText("...");
        lblKhachHangQuayLaiValue.setText("...");
        lblTongDoanhThuValue.setText("...");
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        // 4. Tạo và thực thi SwingWorker
        SwingWorker<ThongKeKHResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeKHResult doInBackground() throws Exception {
                ThongKeKHResult result = new ThongKeKHResult();

                // 1. Lấy dữ liệu chi tiết RFM
                result.chiTietKhachHang = thongKeKHDao.getThongKeKhachHang(
                        fromLocalDate, toLocalDate, khuVuc, loaiDoiTuong, phanLoai);

                // 2. Tính toán tổng quan từ dữ liệu chi tiết
                result.tongSoKhachHang = result.chiTietKhachHang.size();
                result.tongDoanhThu = result.chiTietKhachHang.values().stream()
                        .mapToDouble(kh -> kh.tongChiTieu).sum();

                // Đếm khách hàng mới/cũ DỰA TRÊN KHOẢNG THỜI GIAN LỌC
                // Khách mới: Người có ngày mua ĐẦU TIÊN nằm trong khoảng lọc
                result.soKhachHangMoi = (int) result.chiTietKhachHang.values().stream()
                        .filter(kh -> !kh.ngayMuaDauTien.isBefore(fromLocalDate))
                        .count();

                // Khách quay lại: Người có ngày mua ĐẦU TIÊN nằm TRƯỚC khoảng lọc
                result.soKhachHangQuayLai = (int) result.chiTietKhachHang.values().stream()
                        .filter(kh -> kh.ngayMuaDauTien.isBefore(fromLocalDate))
                        .count();

                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeKHResult result = get();

                    // CẬP NHẬT CARD
                    lblTongKhachHangValue.setText(integerFormatter.format(result.tongSoKhachHang));
                    lblKhachHangMoiValue.setText(integerFormatter.format(result.soKhachHangMoi));
                    lblKhachHangQuayLaiValue.setText(integerFormatter.format(result.soKhachHangQuayLai));
                    lblTongDoanhThuValue.setText(currencyFormatter.format(result.tongDoanhThu));

                    // CẬP NHẬT BIỂU ĐỒ VÀ BẢNG CHI TIẾT
                    capNhatChartVaTable(result.chiTietKhachHang, result.soKhachHangMoi, result.soKhachHangQuayLai);

                } catch (InterruptedException | ExecutionException ex) {
                    handleLoadingError(ex, "Lỗi khi thực hiện thống kê khách hàng");
                } catch (Exception ex) {
                    handleLoadingError(ex, "Lỗi không xác định khi cập nhật UI");
                }
            }
        };
        worker.execute();
    }

    /**
     * Cập nhật cả biểu đồ và bảng chi tiết.
     */
    private void capNhatChartVaTable(Map<String, KhachHangRFM> data, int soMoi, int soQuayLai) {
        if (data == null || data.isEmpty()) {
            capNhatChartRong("📉 Không có dữ liệu khách hàng trong khoảng đã chọn");
            capNhatBangRong();
            btnExportExcel.setEnabled(false);
        } else {
            // --- Cập nhật Biểu đồ (Pie Chart) ---
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Khách hàng mới", soMoi);
            dataset.setValue("Khách hàng quay lại", soQuayLai);

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Cơ cấu khách hàng Mới / Quay lại", dataset, true, true, false);

            PiePlot plot = (PiePlot) pieChart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
            plot.setLegendLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({1})"));
            plot.setSectionPaint("Khách hàng mới", new Color(46, 204, 113));
            plot.setSectionPaint("Khách hàng quay lại", new Color(52, 152, 219));

            if (pieChart.getLegend() != null) {
                pieChart.getLegend().setFrame(BlockBorder.NONE);
            }
            pieChart.setBackgroundPaint(Color.WHITE);
            pieChart.getTitle().setFont(new Font("Times New Roman", Font.BOLD, 16));

            ChartPanel chartDisplayPanel = new ChartPanel(pieChart);
            chartDisplayPanel.setBackground(Color.WHITE);

            chartPanelContainer.removeAll();
            chartPanelContainer.add(chartDisplayPanel, BorderLayout.CENTER);
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();

            // --- Cập nhật Bảng chi tiết ---
            chiTietTableModel.setRowCount(0);
            data.values().forEach(item -> {
                chiTietTableModel.addRow(new Object[]{
                        item.khachHangID,
                        item.hoTen,
                        item.khuVuc,
                        item.loaiDoiTuong,
                        item.soLanMua,
                        item.tongChiTieu,
                        item.lanMuaCuoi.format(dateFormatter),
                        item.phanLoai
                });
            });
            btnExportExcel.setEnabled(true);
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
        String defaultFileName = "BaoCaoKhachHang_" + LocalDate.now() + ".xlsx";
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

                Sheet sheet = workbook.createSheet("ChiTietKhachHang");

                // --- Tạo Style ---
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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

                // --- Ghi Header bảng ---
                Row headerRow = sheet.createRow(0);
                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    Cell cell = headerRow.createCell(col);
                    cell.setCellValue(chiTietTableModel.getColumnName(col).replace(" (VNĐ)", ""));
                    cell.setCellStyle(headerCellStyle);
                }

                // --- Ghi Dữ liệu bảng ---
                for (int row = 0; row < chiTietTableModel.getRowCount(); row++) {
                    Row dataRow = sheet.createRow(row + 1);
                    for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                        Cell cell = dataRow.createCell(col);
                        Object value = chiTietTableModel.getValueAt(row, col);

                        if (value instanceof Integer) {
                            cell.setCellValue((Integer) value);
                            cell.setCellStyle(integerCellStyle);
                        } else if (value instanceof Double) {
                            cell.setCellValue((Double) value);
                            cell.setCellStyle(currencyCellStyle);
                        } else if (value instanceof String) {
                            cell.setCellValue((String) value);
                            cell.setCellStyle((col == 0 || col == 6 || col == 7) ? centerCellStyle : leftCellStyle); // Căn giữa Mã, Ngày, Phân loại
                        }
                    }
                }

                for (int col = 0; col < chiTietTableModel.getColumnCount(); col++) {
                    sheet.autoSizeColumn(col);
                }

                workbook.write(outputStream);
                outputStream.close();

                JOptionPane.showMessageDialog(this, "Xuất báo cáo Excel thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(finalFileToSave);
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

        lblTongKhachHangValue.setText("Lỗi");
        lblKhachHangMoiValue.setText("Lỗi");
        lblKhachHangQuayLaiValue.setText("Lỗi");
        lblTongDoanhThuValue.setText("Lỗi");
    }

    // ================== CÁC HÀM TIỆN ÍCH KHÁC ==================

    private boolean kiemTraKhoangNgayHopLe(Date from, Date to) {
        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn đủ ngày bắt đầu và ngày kết thúc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (to.before(from)) {
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

    // --- Tạo Panel lọc con cho CardLayout (cho thời gian) ---
    // (Phiên bản này không dùng CardLayout thời gian, giữ lại để tham khảo nếu cần)
    private JPanel buildTatCaFilter() { return new JPanel(); }
    private JPanel buildNgayFilter() { return new JPanel(); }
    private JPanel buildThangFilter() { return new JPanel(); }
    private JPanel buildNamFilter() { return new JPanel(); }

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

    // Hàm tạo Panel Chi Tiết
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

}