package gui.application.form.thongKe;

import com.toedter.calendar.JDateChooser;
import dao.impl.ThongKeKhachHangDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Panel Thống kê Khách hàng & Phân tích RFM (Recency - Frequency - Monetary).
 * Phiên bản: Đã xóa cột Khu Vực.
 */
public class PanelThongKeKhachHang extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PanelThongKeKhachHang.class.getName());
    private static final String CARD_TATCA = "CARD_TATCA";
    private static final String CARD_NGAY = "CARD_NGAY";
    private static final String CARD_THANG = "CARD_THANG";
    private static final String CARD_NAM = "CARD_NAM";
    private final ThongKeKhachHangDAO thongKeKHDao;
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
    // ===== Components Lọc Nghiệp Vụ =====
    private final JComboBox<String> cbLoaiDoiTuong;
    private final JComboBox<String> cbPhanLoai;

    // ===== Nút bấm =====
    private final JButton btnTimKiem;
    private final JButton btnXoaBoLoc;

    // ===== Labels Card =====
    private final JLabel lblTongKhachHangValue;
    private final JLabel lblKhachHangMoiValue;
    private final JLabel lblKhachHangQuayLaiValue;
    private final JLabel lblTongDoanhThuValue;

    // ===== Bảng & Biểu đồ =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;

    // ===== Thành phần Header Chi tiết =====
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;

    // 3 Label hiển thị thông tin lọc
    private JLabel lblInfoThoiGian;
    private JLabel lblInfoDoiTuong;
    private JLabel lblInfoPhanLoai;

    public PanelThongKeKhachHang() {
        this.thongKeKHDao = new ThongKeKhachHangDAO();

        // 1. Khởi tạo Components
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

        cbLoaiDoiTuong = new JComboBox<>(new String[]{"Tất cả"});
        cbPhanLoai = new JComboBox<>(new String[]{"Tất cả", "VIP", "Thân thiết", "Khách quay lại", "Khách mới", "Ngủ đông"});

        btnTimKiem = new JButton("Tìm kiếm");
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        setupButtons();

        lblTongKhachHangValue = createValueLabel("...");
        lblKhachHangMoiValue = createValueLabel("...");
        lblKhachHangQuayLaiValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");

        // 2. Layout Chính
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        "THỐNG KÊ KHÁCH HÀNG", TitledBorder.LEFT, TitledBorder.TOP,
                        new Font(getFont().getFontName(), Font.BOLD, 16), new Color(0, 110, 185)),
                new EmptyBorder(5, 5, 5, 5)));

        // 3. Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(buildFilterBar());

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(180, 180, 180));
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(sep);
        topPanel.add(Box.createVerticalStrut(15));

        // Cards Tổng quan
        JPanel infoWrapper = new JPanel(new GridLayout(1, 4, 20, 20));
        infoWrapper.setOpaque(false);
        infoWrapper.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 5));
        infoWrapper.add(createCard("Tổng Khách Hàng", lblTongKhachHangValue, new Color(52, 73, 94)));
        infoWrapper.add(createCard("Khách Hàng Mới", lblKhachHangMoiValue, new Color(46, 204, 113)));
        infoWrapper.add(createCard("Khách Quay Lại", lblKhachHangQuayLaiValue, new Color(41, 128, 185)));
        infoWrapper.add(createCard("Tổng Doanh Thu", lblTongDoanhThuValue, new Color(243, 156, 18)));
        topPanel.add(infoWrapper);
        add(topPanel, BorderLayout.NORTH);

        // 4. Center Panel
        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        // --- CẬP NHẬT CẤU TRÚC BẢNG (ĐÃ XÓA CỘT KHU VỰC) ---
        // Index cũ: 0-Mã, 1-Tên, 2-KhuVuc, 3-LoaiDT, 4-SoLan, 5-Tien, 6-Ngay, 7-PhanLoai
        // Index mới: 0-Mã, 1-Tên,          2-LoaiDT, 3-SoLan, 4-Tien, 5-Ngay, 6-PhanLoai
        String[] columnNames = {"Mã KH", "Tên Khách Hàng", "Loại Đối Tượng", "Số Lần Mua", "Tổng Chi Tiêu (VNĐ)", "Lần Mua Cuối", "Phân Loại"};

        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Integer.class; // Số lần mua
                if (columnIndex == 4) return Double.class;  // Tiền
                return String.class;
            }
        };
        tableChiTiet = new JTable(chiTietTableModel);
        setupTableDetails();

        // Header Chi tiết
        lblChiTietTitle = new JLabel("Chi tiết danh sách khách hàng", JLabel.CENTER);
        lblChiTietTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 18));
        lblChiTietTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

        // Panel Filter Info
        JPanel pnlFilterInfo = new JPanel(new GridLayout(2, 2, 20, 5));
        pnlFilterInfo.setOpaque(false);
        pnlFilterInfo.setBorder(new EmptyBorder(0, 50, 10, 50));
        Font fontInfo = new Font(getFont().getFontName(), Font.PLAIN, 14);
        lblInfoThoiGian = new JLabel("Thời gian: Tất cả");
        lblInfoThoiGian.setFont(fontInfo);
        lblInfoDoiTuong = new JLabel("Đối tượng: Tất cả");
        lblInfoDoiTuong.setFont(fontInfo);
        lblInfoPhanLoai = new JLabel("Phân loại: Tất cả");
        lblInfoPhanLoai.setFont(fontInfo);
        pnlFilterInfo.add(lblInfoThoiGian);
        pnlFilterInfo.add(lblInfoDoiTuong);
        pnlFilterInfo.add(lblInfoPhanLoai);
        pnlFilterInfo.add(new JLabel(""));

        JPanel pnlTopContainer = new JPanel(new BorderLayout());
        pnlTopContainer.setOpaque(false);
        pnlTopContainer.add(lblChiTietTitle, BorderLayout.NORTH);
        pnlTopContainer.add(pnlFilterInfo, BorderLayout.CENTER);

        // Export Button
        btnExportExcel = new JButton("Xuất Excel");
        btnExportExcel.setBackground(new Color(33, 115, 70));
        btnExportExcel.setForeground(Color.WHITE);
        btnExportExcel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportExcel.setEnabled(false);
        btnExportExcel.addActionListener(this::exportTableToExcel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnExportExcel);

        JPanel panelChiTietContainer = taoPanelChiTiet(pnlTopContainer, tableChiTiet, bottomPanel);

        tab.addTab("Cơ cấu khách hàng", chartPanelContainer);
        tab.addTab("Chi tiết khách hàng", panelChiTietContainer);
        add(tab, BorderLayout.CENTER);

        loadComboBoxesData();
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    // ================== LOGIC XỬ LÝ ==================
    private void xuLyThongKe() {
        // Logic lọc thời gian giữ nguyên
        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        LocalDate fromDate, toDate;
        String titleLoai, infoThoiGian;

        try {
            if ("Theo ngày".equals(loaiThoiGian)) {
                if (tuNgay.getDate() == null || denNgay.getDate() == null) return;
                fromDate = tuNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                toDate = denNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String strDate = String.format("%s - %s", fmtD(fromDate), fmtD(toDate));
                titleLoai = "ngày (" + strDate + ")";
                infoThoiGian = strDate;
            } else if ("Theo tháng".equals(loaiThoiGian)) {
                int m1 = cbTuThang.getSelectedIndex() + 1, y1 = (Integer) cbTuNamThang.getSelectedItem();
                int m2 = cbDenThang.getSelectedIndex() + 1, y2 = (Integer) cbDenNamThang.getSelectedItem();
                fromDate = LocalDate.of(y1, m1, 1);
                toDate = LocalDate.of(y2, m2, 1).plusMonths(1).minusDays(1);
                String strMonth = String.format("%d/%d - %d/%d", m1, y1, m2, y2);
                titleLoai = "tháng (" + strMonth + ")";
                infoThoiGian = strMonth;
            } else if ("Theo năm".equals(loaiThoiGian)) {
                int y1 = (Integer) cbTuNam.getSelectedItem(), y2 = (Integer) cbDenNam.getSelectedItem();
                fromDate = LocalDate.of(y1, 1, 1);
                toDate = LocalDate.of(y2, 12, 31);
                String strYear = String.format("%d - %d", y1, y2);
                titleLoai = "năm (" + strYear + ")";
                infoThoiGian = strYear;
            } else {
                fromDate = LocalDate.of(2000, 1, 1);
                toDate = LocalDate.now();
                titleLoai = "tất cả";
                infoThoiGian = "Tất cả";
            }
            if (toDate.isBefore(fromDate)) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!");
                return;
            }
        } catch (Exception e) {
            return;
        }

        String loaiDoiTuong = (String) cbLoaiDoiTuong.getSelectedItem();
        String phanLoai = (String) cbPhanLoai.getSelectedItem();

        // Update Labels
        lblInfoThoiGian.setText("Thời gian: " + infoThoiGian);
        lblInfoDoiTuong.setText("Đối tượng: " + (loaiDoiTuong == null ? "Tất cả" : loaiDoiTuong));
        lblInfoPhanLoai.setText("Phân loại: " + (phanLoai == null ? "Tất cả" : phanLoai));
        lblChiTietTitle.setText("Chi tiết danh sách khách hàng theo " + titleLoai);

        // Worker
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongKhachHangValue.setText("...");
        lblKhachHangMoiValue.setText("...");
        lblKhachHangQuayLaiValue.setText("...");
        lblTongDoanhThuValue.setText("...");
        chiTietTableModel.setRowCount(0);

        final LocalDate finalFrom = fromDate, finalTo = toDate;
        SwingWorker<ThongKeKHResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeKHResult doInBackground() throws Exception {
                ThongKeKHResult res = new ThongKeKHResult();
                res.chiTietKhachHang = thongKeKHDao.getThongKeKhachHang(finalFrom, finalTo, loaiDoiTuong, phanLoai);
                res.tongSoKhachHang = res.chiTietKhachHang.size();
                res.tongDoanhThu = res.chiTietKhachHang.values().stream().mapToDouble(k -> k.tongChiTieu).sum();

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
                    lblTongKhachHangValue.setText(integerFormatter.format(result.tongSoKhachHang));
                    lblKhachHangMoiValue.setText(integerFormatter.format(result.countMoi));
                    lblKhachHangQuayLaiValue.setText(integerFormatter.format(result.countQuayLai));
                    lblTongDoanhThuValue.setText(currencyFormatter.format(result.tongDoanhThu));
                    capNhatChartVaTable(result);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PanelThongKeKhachHang.this, "Lỗi khi tải dữ liệu: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void capNhatChartVaTable(ThongKeKHResult res) {
        // 1. Chart
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
        plot.setSectionPaint("Mới (1 lần)", new Color(46, 204, 113));
        plot.setSectionPaint("Quay lại (2-4 lần)", new Color(52, 152, 219));
        plot.setSectionPaint("Thân thiết (>5 lần)", new Color(155, 89, 182));
        plot.setSectionPaint("VIP", new Color(241, 196, 15));
        plot.setSectionPaint("Ngủ đông", new Color(149, 165, 166));
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        pieChart.getTitle().setFont(new Font(getFont().getFontName(), Font.BOLD, 16));

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanelContainer.removeAll();
        chartPanelContainer.add(chartPanel, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();

        // 2. Table Update - ĐÃ LOẠI BỎ KHU VỰC
        chiTietTableModel.setRowCount(0);
        if (res.chiTietKhachHang != null) {
            for (ThongKeKhachHangDAO.KhachHangRFM kh : res.chiTietKhachHang.values()) {
                chiTietTableModel.addRow(new Object[]{
                        kh.khachHangID,
                        kh.hoTen,
                        // kh.khuVuc, -> ĐÃ XÓA
                        kh.loaiDoiTuong,
                        kh.soLanMua,
                        kh.tongChiTieu,
                        kh.lanMuaCuoi != null ? kh.lanMuaCuoi.format(dateFormatter) : "",
                        kh.phanLoai
                });
            }
        }
        btnExportExcel.setEnabled(chiTietTableModel.getRowCount() > 0);
    }

    // ================== EXCEL EXPORT (UPDATED) ==================
    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCaoKhachHang_" + LocalDate.now() + ".xlsx"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");

            try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream os = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("KhachHang");

                // Styles
                CellStyle titleStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle infoStyle = wb.createCellStyle();
                infoStyle.setAlignment(HorizontalAlignment.LEFT);
                org.apache.poi.ss.usermodel.Font infoFont = wb.createFont();
                infoFont.setItalic(true);
                infoStyle.setFont(infoFont);

                CellStyle headerStyle = wb.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = wb.createFont();
                font.setBold(true);
                headerStyle.setFont(font);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                setBorder(headerStyle);

                CellStyle dataStyle = wb.createCellStyle();
                setBorder(dataStyle);

                // 1. Tiêu đề
                Row row0 = sheet.createRow(0);
                row0.setHeightInPoints(30);
                Cell cellTitle = row0.createCell(0);
                cellTitle.setCellValue(lblChiTietTitle.getText().toUpperCase());
                cellTitle.setCellStyle(titleStyle);
                int lastCol = Math.max(0, chiTietTableModel.getColumnCount() - 1);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));

                // 2. Info Headers
                Row row1 = sheet.createRow(1);
                Cell cellTime = row1.createCell(0);
                cellTime.setCellValue(lblInfoThoiGian.getText());
                cellTime.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));

                Row row2 = sheet.createRow(2);
                Cell cellObj = row2.createCell(0);
                cellObj.setCellValue(lblInfoDoiTuong.getText());
                cellObj.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCol));

                Row row3 = sheet.createRow(3);
                Cell cellClass = row3.createCell(0);
                cellClass.setCellValue(lblInfoPhanLoai.getText());
                cellClass.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, lastCol));

                // 3. Data Table
                int startRow = 5;
                Row header = sheet.createRow(startRow);
                for (int i = 0; i < chiTietTableModel.getColumnCount(); i++) {
                    Cell c = header.createCell(i);
                    c.setCellValue(chiTietTableModel.getColumnName(i));
                    c.setCellStyle(headerStyle);
                }

                for (int i = 0; i < chiTietTableModel.getRowCount(); i++) {
                    Row r = sheet.createRow(startRow + 1 + i);
                    for (int j = 0; j < chiTietTableModel.getColumnCount(); j++) {
                        Cell c = r.createCell(j);
                        Object val = chiTietTableModel.getValueAt(i, j);
                        if (val != null) {
                            if (val instanceof Number) c.setCellValue(((Number) val).doubleValue());
                            else c.setCellValue(val.toString());
                        } else {
                            c.setCellValue("");
                        }
                        c.setCellStyle(dataStyle);
                    }
                }
                for (int i = 0; i < chiTietTableModel.getColumnCount(); i++) sheet.autoSizeColumn(i);

                wb.write(os);
                Desktop.getDesktop().open(f);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + ex.getMessage());
            }
        }
    }

    // ================== UTILS ==================
    private void setupButtons() {
        Dimension btnSize = new Dimension(120, 32);
        btnTimKiem.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));
        btnTimKiem.setBackground(new Color(33, 150, 83));
        btnTimKiem.setForeground(Color.WHITE);
        btnTimKiem.setPreferredSize(btnSize);
        btnTimKiem.addActionListener(e -> xuLyThongKe());

        btnXoaBoLoc.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));
        btnXoaBoLoc.setBackground(new Color(108, 117, 125));
        btnXoaBoLoc.setForeground(Color.WHITE);
        btnXoaBoLoc.setPreferredSize(btnSize);
        btnXoaBoLoc.addActionListener(e -> xoaBoLoc());
    }

    private void setupTableDetails() {
        tableChiTiet.setRowHeight(28);
        tableChiTiet.getTableHeader().setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        tableChiTiet.getColumnModel().getColumn(0).setMaxWidth(80); // Mã KH

        // Căn giữa: Mã(0), Số lượng(3), Ngày(5), Phân loại(6)
        tableChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Tiền (4): Căn phải
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                if (v instanceof Number) v = currencyFormatter.format(v);
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(t, v, s, f, r, c);
            }
        });

        // Tô màu Phân loại (6)
        tableChiTiet.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = (String) v;
                comp.setFont(comp.getFont().deriveFont(Font.BOLD));
                if ("VIP".equals(val)) comp.setForeground(new Color(243, 156, 18));
                else if ("Khách quay lại".equals(val)) comp.setForeground(new Color(41, 128, 185));
                else if ("Ngủ đông".equals(val)) comp.setForeground(Color.GRAY);
                else comp.setForeground(Color.BLACK);
                setHorizontalAlignment(JLabel.CENTER);
                return comp;
            }
        });
    }

    private void setBorder(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private String fmtD(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void xoaBoLoc() {
        cbLoaiThoiGian.setSelectedIndex(0);
        tuNgay.setDate(new Date());
        denNgay.setDate(new Date());
        cbLoaiDoiTuong.setSelectedIndex(0);
        cbPhanLoai.setSelectedIndex(0);
        xuLyThongKe();
    }

    // UI Helpers (Filter bar, Cards, etc.)
    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        bar.add(new JLabel("Loại thời gian:"), gbc);
        gbc.gridx = 1;
        cbLoaiThoiGian.setPreferredSize(new Dimension(180, 32));
        bar.add(cbLoaiThoiGian, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        filterSwitcher.setOpaque(false);
        filterSwitcher.add(buildTatCaFilter(), CARD_TATCA);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);
        bar.add(filterSwitcher, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        bar.add(btnXoaBoLoc, gbc);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        row2.setOpaque(false);
        row2.add(new JLabel("Loại đối tượng:"));
        cbLoaiDoiTuong.setPreferredSize(new Dimension(150, 32));
        row2.add(cbLoaiDoiTuong);
        row2.add(new JLabel("Phân loại:"));
        cbPhanLoai.setPreferredSize(new Dimension(150, 32));
        row2.add(cbPhanLoai);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        bar.add(row2, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        bar.add(btnTimKiem, gbc);

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

    private JPanel buildTatCaFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.add(new JLabel("Toàn bộ thời gian"));
        return p;
    }

    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        tuNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setDate(new Date());
        denNgay.setDate(new Date());
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
            int y = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 2020; i <= y + 2; i++) {
                cbTuNamThang.addItem(i);
                cbDenNamThang.addItem(i);
            }
            cbTuThang.setSelectedIndex(0);
            cbDenThang.setSelectedIndex(11);
            cbTuNamThang.setSelectedItem(y);
            cbDenNamThang.setSelectedItem(y);
        }
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
            int y = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 2020; i <= y + 5; i++) {
                cbTuNam.addItem(i);
                cbDenNam.addItem(i);
            }
            cbTuNam.setSelectedItem(y);
            cbDenNam.setSelectedItem(y);
        }
        p.add(new JLabel("Từ năm:"));
        p.add(cbTuNam);
        p.add(new JLabel("Đến năm:"));
        p.add(cbDenNam);
        return p;
    }

    private JPanel taoPanelTongQuan() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.add(new JLabel("📊 Nhấn Tìm kiếm để xem biểu đồ", SwingConstants.CENTER), BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelChiTiet(JComponent title, JTable tbl, JPanel btm) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.add(title, BorderLayout.NORTH);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        p.add(btm, BorderLayout.SOUTH);
        return p;
    }

    private JLabel createValueLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font(getFont().getFontName(), Font.BOLD, 26));
        return l;
    }

    private JPanel createCard(String t, JLabel v, Color c) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        JLabel tl = new JLabel(t, SwingConstants.CENTER);
        tl.setForeground(Color.WHITE);
        tl.setFont(new Font(getFont().getFontName(), Font.BOLD, 16));
        p.setBackground(c);
        p.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        p.add(tl, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private void loadComboBoxesData() {
        try {
            List<String> loaiDTList = thongKeKHDao.getDanhSachLoaiDoiTuong();
            cbLoaiDoiTuong.removeAllItems();
            cbLoaiDoiTuong.addItem("Tất cả");
            for (String ldt : loaiDTList) if (ldt != null) cbLoaiDoiTuong.addItem(ldt);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Lỗi tải loại đối tượng", e);
        }
    }

    // ===== Struct kết quả =====
    private static class ThongKeKHResult {
        Map<String, ThongKeKhachHangDAO.KhachHangRFM> chiTietKhachHang;
        int tongSoKhachHang;
        double tongDoanhThu;
        long countVIP;
        long countThanThiet;
        long countQuayLai;
        long countMoi;
        long countNguDong;
    }
}