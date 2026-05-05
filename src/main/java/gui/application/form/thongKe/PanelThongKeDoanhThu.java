package gui.application.form.thongKe;

import bus.ThongKeDoanhThu_BUS;
import com.toedter.calendar.JDateChooser;
import dao.impl.ThongKeDoanhThuDAO;
import dao.impl.ThongKeVeDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Panel hiển thị thống kê DOANH THU.
 */
public class PanelThongKeDoanhThu extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(PanelThongKeDoanhThu.class.getName());
    private static final String CARD_TATCA = "CARD_TATCA";
    private static final String CARD_NGAY = "CARD_NGAY";
    private static final String CARD_THANG = "CARD_THANG";
    private static final String CARD_NAM = "CARD_NAM";

    // ===== BUS =====
    private final ThongKeDoanhThu_BUS thongKeBUS;
    private final ThongKeVeDAO thongKeVeDAO;

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
    private final JComboBox<String> cbLoaiTuyen;
    private final JComboBox<String> cbGaDi;
    private final JComboBox<String> cbGaDen;
    private final JComboBox<String> cbNhanVien;
    private final JComboBox<String> cbThanhToan;
    private final Map<String, String> nhanVienMap = new HashMap<>();
    private final JButton btnTimKiem;
    private final JButton btnXoaBoLoc;

    // ===== Các JLabel chứa giá trị trên card =====
    private final JLabel lblTongHDDaBanValue;
    private final JLabel lblTongHDHoanDoiValue;
    private final JLabel lblTongChiValue;
    private final JLabel lblTongTienMatValue;
    private final JLabel lblTongChuyenKhoanValue;
    private final JLabel lblTongDoanhThuValue;
    private final JLabel lblTongLoiNhuanValue;

    // ===== Biểu đồ & Bảng =====
    private final JPanel chartPanelContainer;
    private final JTable tableChiTiet;
    private final DefaultTableModel chiTietTableModel;

    // ===== Thành phần Panel Chi Tiết & Header Lọc =====
    private final JLabel lblChiTietTitle;
    private final JButton btnExportExcel;
    private JLabel lblInfoThoiGian;
    private JLabel lblInfoTuyen;
    private JLabel lblInfoNhanVien;
    private JLabel lblInfoThanhToan;

    // Danh sách gốc để phục vụ tìm kiếm
    private List<String> danhSachGaGoc;

    public PanelThongKeDoanhThu() {
        this.thongKeBUS = new ThongKeDoanhThu_BUS();
        this.thongKeVeDAO = new ThongKeVeDAO();

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
        cbThanhToan = new JComboBox<>(new String[]{"Tất cả", "Tiền mặt", "Chuyển khoản"});

        btnTimKiem = new JButton("Tìm kiếm");
        btnXoaBoLoc = new JButton("Xóa bộ lọc");
        btnXoaBoLoc.setFont(new Font("Arial", Font.BOLD, 13));
        btnXoaBoLoc.setBackground(new Color(108, 117, 125));
        btnXoaBoLoc.setForeground(Color.WHITE);
        btnXoaBoLoc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnXoaBoLoc.addActionListener(e -> xoaBoLoc());

        lblTongHDDaBanValue = createValueLabel("...");
        lblTongHDHoanDoiValue = createValueLabel("...");
        lblTongChiValue = createValueLabel("...");
        lblTongTienMatValue = createValueLabel("...");
        lblTongChuyenKhoanValue = createValueLabel("...");
        lblTongDoanhThuValue = createValueLabel("...");
        lblTongLoiNhuanValue = createValueLabel("...");

        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);

        javax.swing.border.Border titledLineBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1), "THỐNG KÊ DOANH THU",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16), new Color(0, 110, 185));
        setBorder(BorderFactory.createCompoundBorder(titledLineBorder, BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(buildFilterBar());

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(180, 180, 180));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        topPanel.add(sep);

        JPanel cardsContainer = new JPanel(new BorderLayout(15, 0));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JPanel leftGridPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        leftGridPanel.setOpaque(false);
        leftGridPanel.add(createCard("Tổng hóa đơn đã bán", lblTongHDDaBanValue, new Color(52, 152, 219)));
        leftGridPanel.add(createCard("Tổng hóa đơn hoàn trả", lblTongHDHoanDoiValue, new Color(243, 156, 18)));
        leftGridPanel.add(createCard("Tổng Chi", lblTongChiValue, new Color(231, 76, 60)));
        leftGridPanel.add(createCard("Tổng tiền mặt", lblTongTienMatValue, new Color(155, 89, 182)));
        leftGridPanel.add(createCard("Tổng chuyển khoản", lblTongChuyenKhoanValue, new Color(52, 73, 94)));
        leftGridPanel.add(createCard("Tổng doanh thu", lblTongDoanhThuValue, new Color(46, 204, 113)));

        JPanel rightCirclePanel = new JPanel(new GridBagLayout());
        rightCirclePanel.setOpaque(false);
        CirclePanel circleCard = new CirclePanel("Tổng lợi nhuận", lblTongLoiNhuanValue, new Color(39, 174, 96));
        circleCard.setPreferredSize(new Dimension(180, 180));
        rightCirclePanel.add(circleCard);

        cardsContainer.add(leftGridPanel, BorderLayout.CENTER);
        cardsContainer.add(rightCirclePanel, BorderLayout.EAST);
        topPanel.add(cardsContainer);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tab = new JTabbedPane();
        tab.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
        chartPanelContainer = taoPanelTongQuan();

        String[] columnNames = {"STT", "Thời Gian", "HĐ Bán", "HĐ Hoàn/Đổi", "Doanh Thu (VNĐ)", "Tổng Chi (VNĐ)", "Lợi Nhuận (VNĐ)"};
        chiTietTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

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

        lblChiTietTitle = new JLabel("Báo cáo thống kê chi tiết", JLabel.CENTER);
        lblChiTietTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 18));
        lblChiTietTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

        JPanel pnlFilterInfo = new JPanel(new GridLayout(2, 2, 20, 5));
        pnlFilterInfo.setOpaque(false);
        pnlFilterInfo.setBorder(new EmptyBorder(0, 50, 10, 50));

        Font fontInfo = new Font(getFont().getFontName(), Font.PLAIN, 14);
        lblInfoThoiGian = new JLabel("Thời gian: Tất cả");
        lblInfoThoiGian.setFont(fontInfo);
        lblInfoTuyen = new JLabel("Tuyến: Tất cả");
        lblInfoTuyen.setFont(fontInfo);
        lblInfoNhanVien = new JLabel("Nhân viên: Tất cả");
        lblInfoNhanVien.setFont(fontInfo);
        lblInfoThanhToan = new JLabel("Thanh toán: Tất cả");
        lblInfoThanhToan.setFont(fontInfo);

        pnlFilterInfo.add(lblInfoThoiGian);
        pnlFilterInfo.add(lblInfoTuyen);
        pnlFilterInfo.add(lblInfoNhanVien);
        pnlFilterInfo.add(lblInfoThanhToan);

        JPanel pnlTopContainer = new JPanel(new BorderLayout());
        pnlTopContainer.setOpaque(false);
        pnlTopContainer.add(lblChiTietTitle, BorderLayout.NORTH);
        pnlTopContainer.add(pnlFilterInfo, BorderLayout.CENTER);

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

        tab.addTab("Tổng quan", chartPanelContainer);
        tab.addTab("Chi tiết", panelChiTietContainer);
        add(tab, BorderLayout.CENTER);

        loadComboBoxesData();
        SwingUtilities.invokeLater(this::xuLyThongKe);
    }

    private void setupAutoComplete(final JComboBox<String> comboBox, final List<String> items) {
        final JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    String text = textfield.getText();
                    if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40) return;
                    filterInfo(comboBox, text, items);
                });
            }
        });
        textfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (comboBox.isEnabled()) comboBox.setPopupVisible(true);
            }
        });
        comboBox.setEditable(true);
    }

    private void filterInfo(JComboBox<String> comboBox, String enteredText, List<String> items) {
        if (!comboBox.isPopupVisible()) comboBox.showPopup();
        List<String> filterArray = items.stream()
                .filter(p -> p.toLowerCase().contains(enteredText.toLowerCase()))
                .collect(Collectors.toList());
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
        model.removeAllElements();
        for (String s : filterArray) model.addElement(s);
        JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();
        textfield.setText(enteredText);
    }

    private void xuLyThongKe() {
        String loaiThoiGian = (String) cbLoaiThoiGian.getSelectedItem();
        if (loaiThoiGian == null) loaiThoiGian = "Tất cả";

        String loaiTuyen = (String) cbLoaiTuyen.getSelectedItem();
        String tenGaDi = (String) cbGaDi.getEditor().getItem();
        String tenGaDen = (String) cbGaDen.getEditor().getItem();

        if (!loaiTuyen.equals("Tất cả")) {
            if (tenGaDi == null || tenGaDi.trim().isEmpty() || !danhSachGaGoc.contains(tenGaDi)) {
                JOptionPane.showMessageDialog(this, "Ga đi không hợp lệ hoặc không có trong danh sách!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (tenGaDen == null || tenGaDen.trim().isEmpty() || !danhSachGaGoc.contains(tenGaDen)) {
                JOptionPane.showMessageDialog(this, "Ga đến không hợp lệ hoặc không có trong danh sách!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String selectedNhanVien = (String) cbNhanVien.getSelectedItem();
        String nhanVienID = (selectedNhanVien == null || selectedNhanVien.equals("Tất cả")) ? null : nhanVienMap.get(selectedNhanVien);

        String thanhToan = (String) cbThanhToan.getSelectedItem();
        Integer isTienMat = null;
        if ("Tiền mặt".equals(thanhToan)) isTienMat = 1;
        else if ("Chuyển khoản".equals(thanhToan)) isTienMat = 0;

        LocalDate from, to;
        String titleChart = loaiThoiGian;
        String titleLoai = loaiThoiGian;
        String infoThoiGian = "Tất cả";

        try {
            switch (loaiThoiGian) {
                case "Theo ngày" -> {
                    Date d1 = tuNgay.getDate(), d2 = denNgay.getDate();
                    if (!kiemTraKhoangNgayHopLe(d1, d2)) return;
                    from = d1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    to = d2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    String strDate = String.format("%s - %s",
                            from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    titleLoai = "ngày (" + strDate + ")";
                    titleChart = "ngày";
                    infoThoiGian = strDate;
                }
                case "Theo tháng" -> {
                    int fm = cbTuThang.getSelectedIndex() + 1, fy = (int) cbTuNamThang.getSelectedItem();
                    int tm = cbDenThang.getSelectedIndex() + 1, ty = (int) cbDenNamThang.getSelectedItem();
                    from = LocalDate.of(fy, fm, 1);
                    to = LocalDate.of(ty, tm, LocalDate.of(ty, tm, 1).lengthOfMonth());
                    if (to.isBefore(from)) {
                        JOptionPane.showMessageDialog(this, "Tháng kết thúc phải >= tháng bắt đầu.");
                        return;
                    }
                    String strMonth = String.format("%d/%d - %d/%d", fm, fy, tm, ty);
                    titleLoai = "tháng (" + strMonth + ")";
                    titleChart = "tháng";
                    infoThoiGian = strMonth;
                }
                case "Theo năm" -> {
                    int sy = (int) cbTuNam.getSelectedItem(), ey = (int) cbDenNam.getSelectedItem();
                    from = LocalDate.of(sy, 1, 1);
                    to = LocalDate.of(ey, 12, 31);
                    if (ey < sy) {
                        JOptionPane.showMessageDialog(this, "Năm kết thúc phải >= năm bắt đầu.");
                        return;
                    }
                    String strYear = String.format("%d - %d", sy, ey);
                    titleLoai = "năm (" + strYear + ")";
                    titleChart = "năm";
                    infoThoiGian = strYear;
                }
                default -> {
                    from = LocalDate.of(2000, 1, 1);
                    to = LocalDate.of(2030, 12, 31);
                    titleLoai = "tất cả";
                    titleChart = "Tất cả (năm)";
                    loaiThoiGian = "Theo năm";
                    infoThoiGian = "Tất cả";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        lblInfoThoiGian.setText("Thời gian: " + infoThoiGian);
        lblInfoTuyen.setText("Tuyến: " + (loaiTuyen.equals("Tất cả") ? "Tất cả" : tenGaDi + " -> " + tenGaDen));
        lblInfoNhanVien.setText("Nhân viên: " + (selectedNhanVien == null ? "Tất cả" : selectedNhanVien));
        lblInfoThanhToan.setText("Thanh toán: " + (thanhToan == null ? "Tất cả" : thanhToan));
        lblChiTietTitle.setText("Báo cáo thống kê chi tiết theo " + titleLoai.toLowerCase());

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        lblTongHDDaBanValue.setText("...");
        lblTongHDHoanDoiValue.setText("...");
        lblTongChiValue.setText("...");
        lblTongDoanhThuValue.setText("...");
        lblTongTienMatValue.setText("...");
        lblTongChuyenKhoanValue.setText("...");
        lblTongLoiNhuanValue.setText("...");
        capNhatChartRong("🔄 Đang tải dữ liệu...");
        chiTietTableModel.setRowCount(0);

        LocalDate fFrom = from;
        LocalDate fTo = to;
        String fDaoLoai = loaiThoiGian;
        String fChartTitle = titleChart;
        String fLoaiTuyen = loaiTuyen;
        String fTenGaDi = tenGaDi;
        String fTenGaDen = tenGaDen;
        String fNhanVienID = nhanVienID;
        Integer fIsTienMat = isTienMat;

        SwingWorker<ThongKeDoanhThuResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeDoanhThuResult doInBackground() {
                ThongKeDoanhThuResult r = new ThongKeDoanhThuResult();

                r.thongKeChiTietTheoThoiGian = thongKeBUS.getThongKeChiTiet(
                        fDaoLoai, fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);

                r.tongHDTrongKhoang = thongKeBUS.getTongHoaDonBan(
                        fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);

                r.tongHDHoanDoiTrongKhoang = thongKeBUS.getTongHoaDonHoanDoi(
                        fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);

                Map<String, Double> mapTong = thongKeBUS.getTongDoanhThuChiLoiNhuan(
                        fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat);
                r.tongDoanhThu = mapTong.get("doanhThu");
                r.tongChi = mapTong.get("chi");
                r.tongLoiNhuan = mapTong.get("loiNhuan");

                Map<String, Double> mapTienMat = thongKeBUS.getTongTienMatVaChuyenKhoan(
                        fFrom, fTo, fLoaiTuyen, fTenGaDi, fTenGaDen, fNhanVienID, fIsTienMat, r.tongDoanhThu);
                r.tongTienMat = mapTienMat.get("tongTienMat");
                r.tongChuyenKhoan = mapTienMat.get("tongChuyenKhoan");

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
                    capNhatChartVaTable(r.thongKeChiTietTheoThoiGian, fChartTitle);
                } catch (Exception ex) {
                    handleLoadingError(ex, "Lỗi khi tải thống kê");
                }
            }
        };
        worker.execute();
    }

    private void capNhatChartVaTable(Map<String, ThongKeDoanhThuDAO.ThongKeChiTietItem> data, String chartTitle) {
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
                    "Thời gian", "Số tiền (VNĐ)", dataset, PlotOrientation.VERTICAL, true, true, false);

            CategoryPlot plot = barChart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setRangeGridlinePaint(Color.GRAY);
            plot.setOutlineVisible(false);
            plot.setInsets(new RectangleInsets(10, 5, 5, 10));

            NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setUpperMargin(0.15);
            rangeAxis.setNumberFormatOverride(NumberFormat.getNumberInstance(new Locale("vi", "VN")));

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(46, 204, 113));
            renderer.setSeriesPaint(1, new Color(231, 76, 60));
            renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
            renderer.setDrawBarOutline(false);
            renderer.setItemMargin(0.2);
            renderer.setShadowVisible(false);
            renderer.setMaximumBarWidth(0.08);

            CategoryAxis domainAxis = plot.getDomainAxis();
            if (!chartTitle.contains("năm") && dataset.getColumnCount() > 8) {
                domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
            }
            domainAxis.setLowerMargin(0.02);
            domainAxis.setUpperMargin(0.02);

            if (barChart.getLegend() != null) barChart.getLegend().setFrame(BlockBorder.NONE);
            barChart.setBackgroundPaint(Color.WHITE);
            barChart.getTitle().setFont(new Font(getFont().getFontName(), Font.BOLD, 16));

            ChartPanel cp = new ChartPanel(barChart);
            cp.setBackground(Color.WHITE);
            cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            chartPanelContainer.removeAll();
            chartPanelContainer.add(cp, BorderLayout.CENTER);
            chartPanelContainer.revalidate();
            chartPanelContainer.repaint();

            chiTietTableModel.setRowCount(0);
            int stt = 1;
            for (Map.Entry<String, ThongKeDoanhThuDAO.ThongKeChiTietItem> e : data.entrySet()) {
                ThongKeDoanhThuDAO.ThongKeChiTietItem item = e.getValue();
                chiTietTableModel.addRow(new Object[]{
                        stt++, e.getKey(), item.soLuongHoaDonBan,
                        item.soLuongHoaDonHoanDoi, item.tongDoanhThu, item.tongChi, item.loiNhuan
                });
            }
            btnExportExcel.setEnabled(true);
        }
    }

    private void exportTableToExcel(ActionEvent e) {
        if (chiTietTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("BaoCaoChiTiet_" + LocalDate.now() + ".xlsx"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");

            try (XSSFWorkbook wb = new XSSFWorkbook(); FileOutputStream os = new FileOutputStream(f)) {
                Sheet sheet = wb.createSheet("ChiTiet");

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
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                CellStyle dataStyle = wb.createCellStyle();
                dataStyle.setBorderBottom(BorderStyle.THIN);
                dataStyle.setBorderTop(BorderStyle.THIN);
                dataStyle.setBorderLeft(BorderStyle.THIN);
                dataStyle.setBorderRight(BorderStyle.THIN);

                int lastCol = Math.max(0, chiTietTableModel.getColumnCount() - 1);

                Row row0 = sheet.createRow(0);
                row0.setHeightInPoints(30);
                Cell cellTitle = row0.createCell(0);
                cellTitle.setCellValue(lblChiTietTitle.getText().toUpperCase());
                cellTitle.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, lastCol));

                Row row1 = sheet.createRow(1);
                Cell c1 = row1.createCell(0);
                c1.setCellValue(lblInfoThoiGian.getText());
                c1.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, lastCol));

                Row row2 = sheet.createRow(2);
                Cell c2 = row2.createCell(0);
                c2.setCellValue(lblInfoTuyen.getText());
                c2.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, lastCol));

                Row row3 = sheet.createRow(3);
                Cell c3 = row3.createCell(0);
                c3.setCellValue(lblInfoNhanVien.getText());
                c3.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, lastCol));

                Row row4 = sheet.createRow(4);
                Cell c4 = row4.createCell(0);
                c4.setCellValue(lblInfoThanhToan.getText());
                c4.setCellStyle(infoStyle);
                sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, lastCol));

                int startRow = 6;
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
                        if (val instanceof Number) c.setCellValue(((Number) val).doubleValue());
                        else if (val != null) c.setCellValue(val.toString());
                        else c.setCellValue("");
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

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new GridBagLayout());
        bar.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Dimension comboSize = new Dimension(250, 32);

        gbc.gridx = 0;
        gbc.gridy = 0;
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

        gbc.gridx = 0;
        gbc.gridy = 1;
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
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        bar.add(panelCot2, gbc);
        gbc.gridheight = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        bar.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        cbNhanVien.setPreferredSize(comboSize);
        bar.add(cbNhanVien, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelButtons.setOpaque(false);
        btnTimKiem.setPreferredSize(new Dimension(120, 35));
        btnTimKiem.setBackground(new Color(33, 150, 83));
        btnTimKiem.setForeground(Color.WHITE);
        btnXoaBoLoc.setPreferredSize(new Dimension(120, 35));
        panelButtons.add(btnTimKiem);
        panelButtons.add(btnXoaBoLoc);
        bar.add(panelButtons, gbc);

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
                boolean enable = "Theo Ga đi/đến".equals(e.getItem());
                cbGaDi.setEnabled(enable);
                cbGaDen.setEnabled(enable);
                if (!enable) {
                    ((JTextField) cbGaDi.getEditor().getEditorComponent()).setText("");
                    ((JTextField) cbGaDen.getEditor().getEditorComponent()).setText("");
                }
            }
        });
        cbGaDi.setEnabled(false);
        cbGaDen.setEnabled(false);

        btnTimKiem.addActionListener(e -> xuLyThongKe());
        ((CardLayout) filterSwitcher.getLayout()).show(filterSwitcher, CARD_TATCA);
        return bar;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JLabel createValueLabel(String initialText) {
        JLabel label = new JLabel(initialText, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font(getFont().getFontName(), Font.BOLD, 18));
        return label;
    }

    private void loadComboBoxesData() {
        try {
            danhSachGaGoc = thongKeVeDAO.getDanhSachTenGa();
            cbGaDi.removeAllItems();
            cbGaDen.removeAllItems();
            if (danhSachGaGoc == null || danhSachGaGoc.isEmpty()) {
                cbGaDi.addItem("Lỗi");
                cbGaDen.addItem("Lỗi");
            } else {
                for (String tenGa : danhSachGaGoc) {
                    cbGaDi.addItem(tenGa);
                    cbGaDen.addItem(tenGa);
                }
                setupAutoComplete(cbGaDi, danhSachGaGoc);
                setupAutoComplete(cbGaDen, danhSachGaGoc);
            }
            if (danhSachGaGoc.contains("Sài Gòn")) cbGaDi.setSelectedItem("Sài Gòn");
            if (danhSachGaGoc.contains("Hà Nội")) cbGaDen.setSelectedItem("Hà Nội");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Map<String, String> dsNhanVien = thongKeVeDAO.getDanhSachNhanVien();
            nhanVienMap.clear();
            cbNhanVien.removeAllItems();
            cbNhanVien.addItem("Tất cả");
            for (Map.Entry<String, String> entry : dsNhanVien.entrySet()) {
                String displayText = String.format("%s (%s)", entry.getValue(), entry.getKey());
                nhanVienMap.put(displayText, entry.getKey());
                cbNhanVien.addItem(displayText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableDetails() {
        tableChiTiet.setRowHeight(28);
        tableChiTiet.getColumnModel().getColumn(0).setMaxWidth(34);

        JTableHeader header = tableChiTiet.getTableHeader();
        header.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        header.setBackground(new Color(230, 230, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Number) value = currencyFormatter.format(value);
                setHorizontalAlignment(JLabel.RIGHT);
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };

        TableColumnModel cm = tableChiTiet.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(0).setCellRenderer(centerRenderer);
        cm.getColumn(1).setPreferredWidth(100);
        cm.getColumn(1).setCellRenderer(centerRenderer);
        cm.getColumn(2).setPreferredWidth(80);
        cm.getColumn(2).setCellRenderer(centerRenderer);
        cm.getColumn(3).setPreferredWidth(80);
        cm.getColumn(3).setCellRenderer(centerRenderer);
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

    private boolean kiemTraKhoangNgayHopLe(Date d1, Date d2) {
        return d1 != null && d2 != null && (d2.after(d1) || isSameDay(d1, d2));
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
                && c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    private void capNhatChartRong(String m) {
        chartPanelContainer.removeAll();
        JLabel l = new JLabel(m, SwingConstants.CENTER);
        l.setFont(new Font("SansSerif", Font.ITALIC, 16));
        chartPanelContainer.add(l, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private void capNhatBangRong() {
        chiTietTableModel.setRowCount(0);
    }

    private JPanel taoPanelTongQuan() {
        return new JPanel(new BorderLayout());
    }

    private JPanel taoPanelChiTiet(JComponent t, JTable tbl, JPanel bot) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(5, 5, 5, 5));
        p.add(t, BorderLayout.NORTH);
        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        p.add(bot, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildTatCaFilter() {
        return new JPanel();
    }

    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setOpaque(false);
        tuNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setPreferredSize(new Dimension(140, 28));
        denNgay.setPreferredSize(new Dimension(140, 28));
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
            int now = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= now + 5; y++) {
                cbTuNamThang.addItem(y);
                cbDenNamThang.addItem(y);
            }
            cbTuThang.setSelectedIndex(0);
            cbDenThang.setSelectedIndex(11);
            cbTuNamThang.setSelectedItem(now);
            cbDenNamThang.setSelectedItem(now);
        }
        Dimension d = new Dimension(100, 28);
        cbTuThang.setPreferredSize(d);
        cbDenThang.setPreferredSize(d);
        cbTuNamThang.setPreferredSize(d);
        cbDenNamThang.setPreferredSize(d);
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
            int now = Calendar.getInstance().get(Calendar.YEAR);
            for (int y = 2020; y <= now + 5; y++) {
                cbTuNam.addItem(y);
                cbDenNam.addItem(y);
            }
            cbTuNam.setSelectedItem(now);
            cbDenNam.setSelectedItem(now);
        }
        cbTuNam.setPreferredSize(new Dimension(100, 28));
        cbDenNam.setPreferredSize(new Dimension(100, 28));
        p.add(new JLabel("Từ năm:"));
        p.add(cbTuNam);
        p.add(new JLabel("Đến năm:"));
        p.add(cbDenNam);
        return p;
    }

    private void xoaBoLoc() {
        cbLoaiThoiGian.setSelectedIndex(0);
        tuNgay.setDate(new Date());
        denNgay.setDate(new Date());
        cbLoaiTuyen.setSelectedIndex(0);
        cbGaDi.setEnabled(false);
        cbGaDen.setEnabled(false);
        ((JTextField) cbGaDi.getEditor().getEditorComponent()).setText("");
        ((JTextField) cbGaDen.getEditor().getEditorComponent()).setText("");
        if (cbNhanVien.getItemCount() > 0) cbNhanVien.setSelectedIndex(0);
        if (cbThanhToan.getItemCount() > 0) cbThanhToan.setSelectedIndex(0);
        xuLyThongKe();
    }

    // ===== Lớp chứa kết quả thống kê =====
    private static class ThongKeDoanhThuResult {
        int tongHDTrongKhoang;
        int tongHDHoanDoiTrongKhoang;
        double tongChi;
        double tongTienMat;
        double tongChuyenKhoan;
        double tongDoanhThu;
        double tongLoiNhuan;
        Map<String, ThongKeDoanhThuDAO.ThongKeChiTietItem> thongKeChiTietTheoThoiGian;
    }

    private static class CirclePanel extends JPanel {
        private final JLabel valueLabel;
        private final Color bgColor;

        public CirclePanel(String title, JLabel valueLabel, Color bgColor) {
            this.valueLabel = valueLabel;
            this.bgColor = bgColor;
            setOpaque(false);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(5, 0, 5, 0);
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font(getFont().getFontName(), Font.PLAIN, 15));
            lblTitle.setForeground(Color.WHITE);
            add(lblTitle, gbc);
            gbc.gridy = 1;
            valueLabel.setFont(new Font(getFont().getFontName(), Font.BOLD, 22));
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
            g2.setColor(new Color(255, 255, 255, 60));
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(x + 3, y + 3, size - 6, size - 6);
            g2.dispose();
        }
    }
}