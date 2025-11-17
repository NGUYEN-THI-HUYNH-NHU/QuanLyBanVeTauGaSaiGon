package gui.application.form.dashboard;

import com.formdev.flatlaf.FlatDarkLaf;
import connectDB.ConnectDB;
import dao.Dashboard_DAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter; // Thêm import
import java.awt.event.MouseEvent; // Thêm import
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Set;

/**
 * Giao diện Dashboard quản lý bán vé tàu hỏa.
 * [CẬP NHẬT] - Thêm MouseListener cho KpiCard Doanh Thu.
 * - Thêm biến lưu bộ lọc ngày.
 */
public class Dashboard extends JPanel {

    // --- (Các hằng số màu sắc và định dạng giữ nguyên) ---
    private static final Color BG_COLOR = new Color(24, 26, 31);
    private static final Color PANEL_COLOR = new Color(34, 38, 46);
    private static final Color TEXT_COLOR = new Color(230, 230, 230);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(55, 63, 78);
    private static final Color[] CHART_COLORS = {
            new Color(59, 130, 246), new Color(16, 185, 129),
            new Color(249, 115, 22), new Color(239, 68, 68),
            new Color(168, 85, 247), new Color(217, 70, 239)
    };
    private final DecimalFormat formatter = new DecimalFormat("#,##0");
    private final DecimalFormat percentFormatter = new DecimalFormat("#,##0.0'%'");

    // --- (Khai báo DAO và Components) ---
    private Dashboard_DAO dashboardDAO;
    private KpiCard kpiRevenue, kpiTicketsSold, kpiOccupancy, kpiRefundRate;
    private RevenueOverTimeChartPanel revenueChart;
    private Top5RevenueChartPanel top5RevenueChart;
    private CustomerTypeChartPanel customerTypeChart;
    private StackedBarChartPanel stackedBarChart;
    private TopPromotionChartPanel topPromotionChart;
    private JButton btnToday, btnWeek, btnMonth, btnYear, btnAll;

    // [THÊM MỚI] Biến lưu trữ bộ lọc ngày hiện tại
    private LocalDate currentStartDate = null;
    private LocalDate currentEndDate = null;


    public Dashboard() {
        // Khởi tạo DAO
        try {
            ConnectDB.getInstance();
            this.dashboardDAO = new Dashboard_DAO();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Không thể khởi tạo kết nối CSDL.\n" + e.getMessage(),
                    "Lỗi Kết Nối",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Thiết lập giao diện cơ bản
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- A. Header Bar (Thanh tiêu đề) ---
        add(createHeaderBar(), BorderLayout.NORTH);

        // --- B. Main Grid (Lưới nội dung chính) ---
        add(createMainGrid(), BorderLayout.CENTER);

        // --- C. Tải dữ liệu từ DAO ---
        if (this.dashboardDAO != null) {
            loadDashboardData(null, null); // Mặc định tải "Tất cả"
        }
    }

    /**
     * A. Tạo Header Bar (Tiêu đề và bộ lọc)
     * (Không đổi)
     */
    private JPanel createHeaderBar() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ TÀU HỎA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        headerPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);
        JLabel filterLabel = new JLabel("Bộ lọc:");
        filterLabel.setForeground(TEXT_MUTED);
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnToday = new JButton("Hôm nay");
        btnWeek = new JButton("Tuần này");
        btnMonth = new JButton("Tháng này");
        btnYear = new JButton("Năm này");
        btnAll = new JButton("Tất cả");

        FilterActionListener listener = new FilterActionListener();
        btnToday.addActionListener(listener);
        btnWeek.addActionListener(listener);
        btnMonth.addActionListener(listener);
        btnYear.addActionListener(listener);
        btnAll.addActionListener(listener);

        filterPanel.add(filterLabel);
        filterPanel.add(btnToday);
        filterPanel.add(btnWeek);
        filterPanel.add(btnMonth);
        filterPanel.add(btnYear);
        filterPanel.add(btnAll);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * B. Tạo Lưới Nội dung Chính (KPIs và Biểu đồ)
     * [THAY ĐỔI] Thêm MouseListener cho kpiRevenue
     */
    private JPanel createMainGrid() {
        JPanel mainGrid = new JPanel(new GridBagLayout());
        mainGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        // --- HÀNG 1: 4 Thẻ KPI ---
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        kpiRevenue = new KpiCard("TỔNG DOANH THU", "...", "+0%", CHART_COLORS[1]);

        // [THÊM MỚI] Thêm sự kiện click
        kpiRevenue.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ khi di chuột
        kpiRevenue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openDoanhThuChiTietWindow(); // Gọi hàm mở cửa sổ chi tiết
            }
        });

        gbc.gridx = 0; mainGrid.add(kpiRevenue, gbc);

        kpiTicketsSold = new KpiCard("SỐ VÉ ĐÃ BÁN", "...", "+0%", CHART_COLORS[0]);
        gbc.gridx = 1; mainGrid.add(kpiTicketsSold, gbc);
        kpiOccupancy = new KpiCard("TỶ LỆ LẤP ĐẦY", "...", "0/0", CHART_COLORS[2]);
        gbc.gridx = 2; mainGrid.add(kpiOccupancy, gbc);
        kpiRefundRate = new KpiCard("TỶ LỆ ĐỔI TRẢ", "...", "0/0", CHART_COLORS[4]);
        gbc.gridx = 3; mainGrid.add(kpiRefundRate, gbc);

        // --- HÀNG 2: Layout theo yêu cầu ---
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;

        revenueChart = new RevenueOverTimeChartPanel(new LinkedHashMap<>());
        gbc.gridx = 0; gbc.gridwidth = 2; mainGrid.add(revenueChart, gbc);
        top5RevenueChart = new Top5RevenueChartPanel(new LinkedHashMap<>());
        gbc.gridx = 2; gbc.gridwidth = 1; mainGrid.add(top5RevenueChart, gbc);
        customerTypeChart = new CustomerTypeChartPanel(new LinkedHashMap<>());
        gbc.gridx = 3; gbc.gridwidth = 1; mainGrid.add(customerTypeChart, gbc);

        // --- HÀNG 3: Layout theo yêu cầu ---
        gbc.gridy = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;

        stackedBarChart = new StackedBarChartPanel(new LinkedHashMap<>());
        gbc.gridx = 0; gbc.gridwidth = 2; mainGrid.add(stackedBarChart, gbc);
        topPromotionChart = new TopPromotionChartPanel(new LinkedHashMap<>());
        gbc.gridx = 2; gbc.gridwidth = 2; mainGrid.add(topPromotionChart, gbc);

        return mainGrid;
    }

    /**
     * C. Tải tất cả dữ liệu từ DAO và cập nhật UI.
     * [THAY ĐỔI] Cập nhật biến currentStartDate/endDate
     */
    private void loadDashboardData(LocalDate startDate, LocalDate endDate) {

        // [THÊM MỚI] Lưu lại bộ lọc hiện tại
        this.currentStartDate = startDate;
        this.currentEndDate = endDate;

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                updateKpiCards(startDate, endDate);
                revenueChart.setData(dashboardDAO.getRevenueOverTime(startDate, endDate));
                top5RevenueChart.setData(dashboardDAO.getTop5RevenueTrips(startDate, endDate));
                customerTypeChart.setData(dashboardDAO.getCustomerTypeDistribution(startDate, endDate));
                stackedBarChart.setData(dashboardDAO.getTicketsBySeatTypeOverTime(startDate, endDate));
                topPromotionChart.setData(dashboardDAO.getTop5Promotions(startDate, endDate));
                return null;
            }
        };

        worker.execute();
    }

    /**
     * Helper: Lấy dữ liệu cho 4 thẻ KPI và cập nhật UI
     * (Không đổi)
     */
    private void updateKpiCards(LocalDate startDate, LocalDate endDate) {
        double revenue = dashboardDAO.getKpiTotalRevenue(startDate, endDate);
        kpiRevenue.setData(formatter.format(revenue) + " VND", "+0%");
        int ticketsSold = dashboardDAO.getKpiTicketsSold(startDate, endDate);
        kpiTicketsSold.setData(formatter.format(ticketsSold), "+0%");
        int totalSeats = dashboardDAO.getTotalAvailableSeats(startDate, endDate);
        double occupancyRate = (totalSeats > 0) ? ((double)ticketsSold / totalSeats) * 100.0 : 0.0;
        kpiOccupancy.setData(
                percentFormatter.format(occupancyRate),
                String.format("%s/%s vé", formatter.format(ticketsSold), formatter.format(totalSeats))
        );
        int totalRefunds = dashboardDAO.getTotalRefundsAndExchanges(startDate, endDate);
        double refundRate = (ticketsSold > 0) ? ((double)totalRefunds / ticketsSold) * 100.0 : 0.0;
        kpiRefundRate.setData(
                percentFormatter.format(refundRate),
                String.format("%s/%s vé", formatter.format(totalRefunds), formatter.format(ticketsSold))
        );
    }

    /**
     * Lớp nội bộ để xử lý sự kiện nhấn nút lọc
     * [THAY ĐỔI] Cập nhật biến currentStartDate/endDate
     */
    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            LocalDate now = LocalDate.now();

            LocalDate startDate = null;
            LocalDate endDate = null;

            if (source == btnToday) {
                startDate = now; endDate = now;
            } else if (source == btnWeek) {
                startDate = now.with(DayOfWeek.MONDAY); endDate = now.with(DayOfWeek.SUNDAY);
            } else if (source == btnMonth) {
                startDate = now.withDayOfMonth(1); endDate = now.with(TemporalAdjusters.lastDayOfMonth());
            } else if (source == btnYear) {
                startDate = now.withDayOfYear(1); endDate = now.with(TemporalAdjusters.lastDayOfYear());
            } else if (source == btnAll) {
                // Để startDate và endDate là null
            }

            // Tải dữ liệu (hàm này cũng sẽ tự động lưu bộ lọc)
            loadDashboardData(startDate, endDate);
        }
    }

    /**
     * [HÀM MỚI] Mở cửa sổ chi tiết Doanh Thu
     */
    private void openDoanhThuChiTietWindow() {
        System.out.println("Mở chi tiết doanh thu với bộ lọc: " + currentStartDate + " đến " + currentEndDate);

        // 1. Tạo Form chi tiết và truyền bộ lọc ngày
        Form_ChiTiet_DoanhThu detailForm = new Form_ChiTiet_DoanhThu(currentStartDate, currentEndDate);

        // 2. Tạo một JDialog (cửa sổ popup)
        // (Lấy cửa sổ cha (JFrame) từ panel này)
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow, "Báo Cáo Chi Tiết Doanh Thu", Dialog.ModalityType.MODELESS); // Modeless để không khóa dashboard

        // 3. Đặt nội dung cho JDialog
        dialog.setContentPane(detailForm);
        dialog.setSize(900, 700); // Kích thước cửa sổ chi tiết
        dialog.setLocationRelativeTo(parentWindow); // Hiển thị ở giữa màn hình cha
        dialog.setVisible(true);
    }


    // =========================================================================
    // CÁC LỚP NỘI BỘ (PANEL VÀ CHART)
    // (Toàn bộ code từ KpiCard đến StackedBarChartPanel giữ nguyên)
    // =========================================================================

    // Lớp BasePanel
    static class BasePanel extends JPanel {
        protected final DecimalFormat formatter = new DecimalFormat("#,##0");
        public BasePanel() {
            setBackground(PANEL_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            setLayout(new BorderLayout());
        }
        protected void createChartTitle(Graphics2D g, String title) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(TEXT_COLOR);
            g.drawString(title, 20, 30);
        }
        protected void drawLegend(Graphics2D g, int x, int y, Color color, String text) {
            g.setColor(color);
            g.fillRect(x, y - 10, 12, 12);
            g.setColor(TEXT_MUTED);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(text, x + 20, y);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }
    }

    // Lớp KpiCard (Đã xóa Icon, đã thu nhỏ)
    static class KpiCard extends BasePanel {
        private String title, value, subtext;
        private Color accentColor;
        public KpiCard(String title, String value, String subtext, Color accentColor) {
            this.title = title; this.value = value; this.subtext = subtext; this.accentColor = accentColor;
            setPreferredSize(new Dimension(200, 110));
        }
        public void setData(String value, String subtext) {
            this.value = value; this.subtext = subtext;
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setColor(accentColor); g2d.fillRect(0, 0, 5, getHeight());
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14)); g2d.setColor(TEXT_MUTED); g2d.drawString(title, 25, 35);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 28)); g2d.setColor(TEXT_COLOR); g2d.drawString(value, 25, 70);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12)); g2d.setColor(accentColor); g2d.drawString(subtext, 25, 95);
        }
    }

    // Lớp RevenueOverTimeChartPanel
    static class RevenueOverTimeChartPanel extends BasePanel {
        private Map<LocalDate, Double> dataMap;
        private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("d/M");
        public RevenueOverTimeChartPanel(Map<LocalDate, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<LocalDate, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            createChartTitle(g2d, "DOANH THU THEO THỜI GIAN (VND)");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            ArrayList<LocalDate> dates = new ArrayList<>(dataMap.keySet());
            double[] data = dataMap.values().stream().mapToDouble(Double::doubleValue).toArray();
            int padding = 20; int labelPadding = 25;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 30;
            int x0 = insets.left + padding + labelPadding + 20;
            int y0 = insets.top + padding + 30;
            double maxVal = Arrays.stream(data).max().orElse(1) * 1.1;
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(TEXT_MUTED);
            int numYGrid = 5;
            for (int i = 0; i <= numYGrid; i++) {
                int y = y0 + h - (i * h / numYGrid);
                g2d.setColor(BORDER_COLOR); g2d.drawLine(x0, y, x0 + w, y);
                g2d.setColor(TEXT_MUTED);
                String yLabel = formatter.format(maxVal * i / numYGrid);
                g2d.drawString(yLabel, insets.left + padding - 5, y + 5);
            }
            g2d.setColor(CHART_COLORS[0]);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Polygon p = new Polygon();
            for (int i = 0; i < data.length; i++) {
                int x = x0 + (i * w / Math.max(data.length - 1, 1));
                int y = y0 + h - (int) (data[i] / maxVal * h);
                p.addPoint(x, y);
            }
            g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);
            for (int i = 0; i < p.npoints; i++) {
                g2d.setColor(CHART_COLORS[0]); g2d.fillOval(p.xpoints[i] - 4, p.ypoints[i] - 4, 8, 8);
                g2d.setColor(Color.WHITE); g2d.fillOval(p.xpoints[i] - 2, p.ypoints[i] - 2, 4, 4);
            }
            g2d.setColor(TEXT_MUTED);
            int numLabels = dates.size(); int maxLabelsToDraw = 12;
            int step = 1;
            if (numLabels > maxLabelsToDraw) {
                step = (int)Math.ceil((double)numLabels / maxLabelsToDraw);
            }
            for (int i = 0; i < numLabels; i++) {
                if (i % step == 0 || i == numLabels - 1) {
                    int x = x0 + (i * w / Math.max(numLabels - 1, 1));
                    FontMetrics fm = g2d.getFontMetrics();
                    String label = dates.get(i).format(shortDateFormatter);
                    g2d.drawString(label, x - fm.stringWidth(label) / 2, y0 + h + labelPadding - 5);
                }
            }
        }
    }

    // Lớp Top5RevenueChartPanel
    static class Top5RevenueChartPanel extends BasePanel {
        private Map<String, Double> revenueData;
        public Top5RevenueChartPanel(Map<String, Double> revenueData) {
            this.revenueData = (revenueData != null) ? revenueData : new LinkedHashMap<>();
        }
        public void setData(Map<String, Double> revenueData) {
            this.revenueData = (revenueData != null) ? revenueData : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "TOP 5 CHUYẾN DOANH THU CAO NHẤT");
            if (revenueData == null || revenueData.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            int padding = 20; int labelPadding = 120;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - 40;
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 50;
            int numBars = revenueData.size();
            double maxVal = revenueData.values().stream().mapToDouble(Double::doubleValue).max().orElse(1) * 1.1;
            int barHeight = Math.max(10, h / (numBars * 2 - 1));
            int barGap = barHeight;
            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            java.util.List<Map.Entry<String, Double>> entries = new java.util.ArrayList<>(revenueData.entrySet());
            for (i = 0; i < entries.size(); i++) {
                Map.Entry<String, Double> entry = entries.get(i);
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);
                g2d.setColor(TEXT_MUTED); FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_COLOR); String valueStr = formatter.format(entry.getValue());
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);
            }
        }
    }

    // Lớp CustomerTypeChartPanel
    static class CustomerTypeChartPanel extends BasePanel {
        private Map<String, Integer> dataMap;
        public CustomerTypeChartPanel(Map<String, Integer> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Integer> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        public Map<String, Integer> getDataMap() { return this.dataMap; }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "CƠ CẤU KHÁCH HÀNG");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            int total = dataMap.values().stream().mapToInt(Integer::intValue).sum();
            int padding = 20; int labelPadding = 100;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - 40;
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 50;
            int numBars = dataMap.size();
            double maxVal = dataMap.values().stream().mapToInt(Integer::intValue).max().orElse(1) * 1.1;
            int barHeight = Math.max(10, h / (numBars * 2 - 1));
            int barGap = barHeight;
            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            java.util.List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(dataMap.entrySet());
            for (i = 0; i < entries.size(); i++) {
                Map.Entry<String, Integer> entry = entries.get(i);
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);
                double percent = (total > 0) ? (entry.getValue() * 100.0 / total) : 0;
                g2d.setColor(TEXT_MUTED); FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_COLOR);
                String valueStr = formatter.format(entry.getValue()) + String.format(" (%.1f%%)", percent);
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);
            }
        }
    }

    // Lớp StackedBarChartPanel (Biểu đồ cột chồng)
    static class StackedBarChartPanel extends BasePanel {
        private Map<LocalDate, Map<String, Integer>> dataMap;
        private final DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("d/M");

        public StackedBarChartPanel(Map<LocalDate, Map<String, Integer>> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<LocalDate, Map<String, Integer>> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            createChartTitle(g2d, "NGẢ VÉ THEO LOẠI GHẾ");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            Set<String> seriesSet = new HashSet<>();
            dataMap.values().forEach(dailyMap -> seriesSet.addAll(dailyMap.keySet()));
            String[] seriesNames = seriesSet.toArray(new String[0]);
            Arrays.sort(seriesNames);
            LocalDate[] labels = dataMap.keySet().toArray(new LocalDate[0]);
            int padding = 20; int labelPadding = 25;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 60;
            int x0 = insets.left + padding + labelPadding + 20;
            int y0 = insets.top + padding + 60;
            double maxVal = 0;
            for (Map<String, Integer> dailyData : dataMap.values()) {
                maxVal = Math.max(maxVal, dailyData.values().stream().mapToDouble(Integer::doubleValue).sum());
            }
            maxVal *= 1.1;
            if (maxVal == 0) maxVal = 1;
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            int numYGrid = 5;
            for (int i = 0; i <= numYGrid; i++) {
                int y = y0 + h - (i * h / numYGrid);
                g2d.setColor(BORDER_COLOR); g2d.drawLine(x0, y, x0 + w, y);
                g2d.setColor(TEXT_MUTED);
                String yLabel = formatter.format(maxVal * i / numYGrid);
                g2d.drawString(yLabel, insets.left + padding - 5, y + 5);
            }
            int numBars = labels.length;
            if (numBars == 0) return;
            int barWidth = (int) (w / (numBars * 1.5));
            int barGap = (w - (barWidth * numBars)) / (numBars + 1);
            if (barGap < 5) {
                barGap = 5;
                barWidth = (w - (barGap * (numBars + 1))) / numBars;
            }
            for (int i = 0; i < numBars; i++) {
                int x = x0 + barGap + i * (barWidth + barGap);
                int yBottom = y0 + h;
                Map<String, Integer> dailyData = dataMap.get(labels[i]);
                for (int j = 0; j < seriesNames.length; j++) {
                    int value = dailyData.getOrDefault(seriesNames[j], 0);
                    int barHeight = (int) (value / maxVal * h);
                    g2d.setColor(CHART_COLORS[j % CHART_COLORS.length]);
                    g2d.fillRect(x, yBottom - barHeight, barWidth, barHeight);
                    yBottom -= barHeight;
                }
            }
            g2d.setColor(TEXT_MUTED);
            int maxLabelsToDraw = 12;
            int step = 1;
            if (numBars > maxLabelsToDraw) {
                step = (int)Math.ceil((double)numBars / maxLabelsToDraw);
            }
            for (int i = 0; i < numBars; i++) {
                if (i % step == 0 || i == numBars - 1) {
                    int x = x0 + barGap + i * (barWidth + barGap) + barWidth / 2;
                    FontMetrics fm = g2d.getFontMetrics();
                    String label = labels[i].format(shortDateFormatter);
                    g2d.drawString(label, x - fm.stringWidth(label) / 2, y0 + h + labelPadding - 5);
                }
            }
            int legendX = x0;
            int legendY = insets.top + 50;
            for (int i = 0; i < seriesNames.length; i++) {
                drawLegend(g2d, legendX, legendY, CHART_COLORS[i % CHART_COLORS.length], seriesNames[i]);
                legendX += g2d.getFontMetrics().stringWidth(seriesNames[i]) + 40;
            }
        }
    }

    // Lớp TopPromotionChartPanel
    static class TopPromotionChartPanel extends BasePanel {
        private Map<String, Integer> dataMap;
        public TopPromotionChartPanel(Map<String, Integer> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Integer> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "TOP 5 KHUYẾN MÃI HIỆU QUẢ (Số lần sử dụng)");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            int padding = 20; int labelPadding = 100;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - 40;
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 50;
            int numBars = dataMap.size();
            double maxVal = dataMap.values().stream().mapToInt(Integer::intValue).max().orElse(1) * 1.1;
            int barHeight = Math.max(10, h / (numBars * 2 - 1));
            int barGap = barHeight;
            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            java.util.List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(dataMap.entrySet());
            for (i = 0; i < entries.size(); i++) {
                Map.Entry<String, Integer> entry = entries.get(i);
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);
                g2d.setColor(TEXT_MUTED); FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_COLOR); String valueStr = formatter.format(entry.getValue());
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);
            }
        }
    }

    // =========================================================================
    // MAIN METHOD (Để chạy thử)
    // (Không đổi - Mở toàn màn hình)
    // =========================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Không thể cài đặt FlatLaf Look and Feel.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Quản lý Bán vé Tàu hỏa (Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setContentPane(new Dashboard());
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}