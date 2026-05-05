package gui.application.form.dashboard;

import com.formdev.flatlaf.FlatLightLaf;
import connectDB.ConnectDB;
import dao.impl.DashboardDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class Dashboard extends JPanel {

    // --- CẤU HÌNH MÀU SẮC ---
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color PANEL_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color ACTIVE_BTN_COLOR = new Color(59, 130, 246);

    // Màu cho Pie Chart (Doanh thu)
    private static final Color COLOR_CASH = new Color(46, 204, 113);      // Xanh lá
    private static final Color COLOR_TRANSFER = new Color(52, 152, 219);  // Xanh dương
    private static final Color COLOR_OTHER = new Color(149, 165, 166);    // Xám

    // Màu Cảnh báo
    private static final Color ALERT_GREEN = new Color(103, 178, 68);
    private static final Color ALERT_RED = new Color(212, 59, 41);

    // Màu cho biểu đồ cột chồng
    private static final Color[] CHART_COLORS = {
            new Color(59, 130, 246), // xanh dương
            new Color(16, 185, 129), // xanh lá
            new Color(249, 115, 22), // cam
            new Color(239, 68, 68),  // đỏ
            new Color(168, 85, 247), // tím
            new Color(217, 70, 239)  // hồng
    };

    private final DecimalFormat formatter = new DecimalFormat("#,##0");
    private final DecimalFormat percentFormatter = new DecimalFormat("#,##0.0'%'");

    // COMPONENTS
    private DashboardDAO dashboardBUS;
    private KpiCard kpiRevenue, kpiTicketsSold, kpiOccupancy, kpiRefundRate;

    // Thay thế BarChart cũ bằng PieChartPanel mới
    private RevenuePieChartPanel revenuePieChart;

    private InvoiceAnalysisChartPanel invoiceAnalysisChart;
    private StackedBarChartPanel stackedBarChart;
    private AlertsPanel alertsPanel;

    private JButton btnToday, btnWeek, btnMonth, btnYear, btnAll;

    // Lưu trạng thái lọc để dùng cho Dialog chi tiết
    private LocalDate currentStart;
    private LocalDate currentEnd;

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================
    public Dashboard() {
        try {
            ConnectDB.getInstance();
            this.dashboardBUS = new DashboardDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(createHeaderBar(), BorderLayout.NORTH);
        add(createMainGrid(), BorderLayout.CENTER);

        if (this.dashboardBUS != null) {
            LocalDate now = LocalDate.now();

            // 1. Highlight nút "Tháng này" thay vì "Hôm nay"
            updateFilterButtonStyle(btnMonth);

            // 2. Tính ngày bắt đầu (mùng 1) và ngày kết thúc tháng
            LocalDate startMonth = now.withDayOfMonth(1);
            LocalDate endMonth = now.with(TemporalAdjusters.lastDayOfMonth());

            // 3. Định dạng ngày hiển thị (dd/MM)
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
            stackedBarChart.setDateFormat(fmt);

            // 4. Gọi hàm load dữ liệu với khoảng thời gian của tháng
            // viewType = 0 để biểu đồ đường/cột hiển thị chi tiết theo từng ngày trong tháng
            loadDashboardData(startMonth, endMonth, 0, fmt);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Dashboard Light");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new Dashboard());
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.setVisible(true);
        });
    }

    // ========================================================================
    // LOAD DATA
    // ========================================================================
    private void loadDashboardData(LocalDate startDate, LocalDate endDate, int viewType, DateTimeFormatter currentFmt) {
        this.currentStart = startDate;
        this.currentEnd = endDate;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // 1. KPI
                updateKpiCards(startDate, endDate);

                // 2. DOANH THU (PIE CHART)
                Map<String, Double> revenueByMethod = dashboardBUS.getRevenueByPaymentMethod(startDate, endDate);
                revenuePieChart.setData(revenueByMethod);

                // 3. NGẢ VÉ (STACKED BAR)
                Map<LocalDate, Map<String, Integer>> ticketData = dashboardBUS.getTicketsBySeatTypeOverTime(startDate, endDate);
                stackedBarChart.setViewType(viewType);
                stackedBarChart.setData(ticketData);
                stackedBarChart.setDateFormat(currentFmt);

                // 4. HÓA ĐƠN (LINE CHART)
                Map<LocalDate, Integer> sold, refund;
                if (viewType == 1) { // Năm
                    sold = dashboardBUS.getInvoicesPaidByMonth(startDate, endDate);
                    refund = dashboardBUS.getInvoicesRefundedByMonth(startDate, endDate);
                } else if (viewType == 2) { // Tất cả
                    sold = dashboardBUS.getInvoicesPaidByYear(startDate, endDate);
                    refund = dashboardBUS.getInvoicesRefundedByYear(startDate, endDate);
                } else { // Ngày/Tuần/Tháng
                    sold = dashboardBUS.getInvoicesPaidOverTime(startDate, endDate);
                    refund = dashboardBUS.getInvoicesRefundedOverTime(startDate, endDate);
                }
                invoiceAnalysisChart.setData(sold, refund, currentFmt);

                // 5. CẢNH BÁO (ALERTS)
                int[] alertData = dashboardBUS.getTripOccupancyAlerts(startDate, endDate);
                alertsPanel.setAlertData(alertData[0], alertData[1]);

                return null;
            }
        };
        worker.execute();
    }

    private void updateKpiCards(LocalDate s, LocalDate e) {
        double r = dashboardBUS.getKpiTotalRevenue(s, e);
        kpiRevenue.setData(formatter.format(r) + " VND", "+0%");

        int t = dashboardBUS.getKpiTicketsSold(s, e);
        kpiTicketsSold.setData(formatter.format(t), "+0%");

        int seat = dashboardBUS.getTotalAvailableSeats(s, e);
        double rate = (seat > 0) ? ((double) t / seat * 100) : 0;
        kpiOccupancy.setData(percentFormatter.format(rate), formatter.format(t) + "/" + formatter.format(seat));

        int ref = dashboardBUS.getTotalRefundsAndExchanges(s, e);
        double refRate = (t > 0) ? ((double) ref / t * 100) : 0;
        kpiRefundRate.setData(percentFormatter.format(refRate), formatter.format(ref) + "/" + formatter.format(t));
    }

    // =========================================================================
    // DIALOG CHI TIẾT
    // =========================================================================
    // =========================================================================
    // HÀM HIỂN THỊ DIALOG CHI TIẾT (ĐÃ LÀM ĐẸP UI - BƯỚC 4)
    // =========================================================================
    private void showDetailsDialog(String title, boolean isLowOccupancy) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Đặt màu nền cho Dialog
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialog.setContentPane(contentPanel);

        // 1. LẤY DỮ LIỆU TỪ DAO
        List<Object[]> dataList;
        if (isLowOccupancy) {
            dataList = dashboardBUS.getLowOccupancyList(currentStart, currentEnd);
        } else {
            dataList = dashboardBUS.getHighOccupancyList(currentStart, currentEnd);
        }

        // 2. CẤU HÌNH CỘT
        String[] columnNames;
        if (isLowOccupancy) {
            columnNames = new String[]{"STT", "Chuyến ID", "Tuyến ID", "Ga đi", "Ga đến", "Ngày đi", "Giờ đi", "Số vé bán", "Tỉ lệ (%)"};
        } else {
            columnNames = new String[]{"STT", "Chuyến ID", "Tuyến ID", "Ga đi", "Ga đến", "Ngày đi", "Giờ đi", "Số vé bán"};
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        int stt = 1;
        for (Object[] row : dataList) {
            Object[] tRow;
            if (isLowOccupancy) {
                tRow = new Object[]{stt++, row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7] + "%"};
            } else {
                tRow = new Object[]{stt++, row[0], row[1], row[2], row[3], row[4], row[5], row[6]};
            }
            model.addRow(tRow);
        }

        // 3. TẠO TABLE VÀ STYLE LUNG LINH (PHẦN QUAN TRỌNG NHẤT)
        JTable table = new JTable(model);

        // --- Style cho nội dung bảng ---
        table.setRowHeight(40); // Tăng chiều cao hàng cho thoáng
        table.setShowVerticalLines(false); // Bỏ đường kẻ dọc
        table.setGridColor(new Color(230, 230, 230)); // Màu đường kẻ ngang nhạt
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254)); // Màu nền khi chọn dòng (Xanh nhạt hiện đại)
        table.setSelectionForeground(Color.BLACK); // Màu chữ khi chọn

        // --- Style cho Header (Tiêu đề cột) ---
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.WHITE); // Nền trắng
        table.getTableHeader().setForeground(new Color(100, 100, 100)); // Chữ xám
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230))); // Viền dưới header
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40)); // Chiều cao header

        // Căn giữa nội dung các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // 4. SCROLL PANE
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230))); // Viền ngoài mỏng
        sp.getViewport().setBackground(Color.WHITE); // Nền trắng cho vùng trống

        // 5. TIÊU ĐỀ DIALOG
        JLabel lblTitle = new JLabel(title.toUpperCase(), JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        lblTitle.setForeground(isLowOccupancy ? ALERT_RED : ALERT_GREEN);

        // Thêm vào panel
        contentPanel.add(lblTitle, BorderLayout.NORTH);
        contentPanel.add(sp, BorderLayout.CENTER);

        dialog.setVisible(true);
    }

    // =========================================================================
    // LAYOUT SETUP
    // =========================================================================
    private JPanel createHeaderBar() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel t = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ TÀU HỎA");
        t.setFont(new Font("Segoe UI", Font.BOLD, 24));
        t.setForeground(TEXT_COLOR);
        h.add(t, BorderLayout.WEST);

        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        f.setOpaque(false);

        btnToday = createFilterButton("Hôm nay");
        btnWeek = createFilterButton("Tuần này");
        btnMonth = createFilterButton("Tháng này");
        btnYear = createFilterButton("Năm này");
        btnAll = createFilterButton("Tất cả");

        FilterActionListener l = new FilterActionListener();
        btnToday.addActionListener(l);
        btnWeek.addActionListener(l);
        btnMonth.addActionListener(l);
        btnYear.addActionListener(l);
        btnAll.addActionListener(l);

        JLabel lblFilter = new JLabel("Bộ lọc: ");
        lblFilter.setForeground(TEXT_MUTED);
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));

        f.add(lblFilter);
        f.add(btnToday);
        f.add(btnWeek);
        f.add(btnMonth);
        f.add(btnYear);
        f.add(btnAll);
        h.add(f, BorderLayout.EAST);
        return h;
    }

    private JButton createFilterButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PANEL_COLOR);
        b.setForeground(TEXT_COLOR);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(5, 15, 5, 15)
        ));
        return b;
    }

    private void updateFilterButtonStyle(JButton active) {
        JButton[] arr = {btnToday, btnWeek, btnMonth, btnYear, btnAll};
        for (JButton b : arr) {
            if (b == active) {
                b.setBackground(ACTIVE_BTN_COLOR);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(PANEL_COLOR);
                b.setForeground(TEXT_COLOR);
            }
        }
    }

    private JPanel createMainGrid() {
        JPanel g = new JPanel(new GridBagLayout());
        g.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 0;

        // KPI Row
        kpiRevenue = new KpiCard("TỔNG DOANH THU", "...", "+0%", CHART_COLORS[1]);
        g.add(kpiRevenue, c);
        c.gridx = 1;
        kpiTicketsSold = new KpiCard("SỐ VÉ ĐÃ BÁN", "...", "+0%", CHART_COLORS[0]);
        g.add(kpiTicketsSold, c);
        c.gridx = 2;
        kpiOccupancy = new KpiCard("TỶ LỆ LẤP ĐẦY", "...", "0/0", CHART_COLORS[2]);
        g.add(kpiOccupancy, c);
        c.gridx = 3;
        kpiRefundRate = new KpiCard("TỶ LỆ HOÀN ĐỔI", "...", "0/0", CHART_COLORS[4]);
        g.add(kpiRefundRate, c);

        // Chart Row 1
        c.gridy = 1;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridwidth = 2;
        revenuePieChart = new RevenuePieChartPanel(new HashMap<>());
        g.add(revenuePieChart, c);

        c.gridx = 2;
        c.gridwidth = 2;
        invoiceAnalysisChart = new InvoiceAnalysisChartPanel();
        g.add(invoiceAnalysisChart, c);

        // Chart Row 2
        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 2;
        stackedBarChart = new StackedBarChartPanel(new LinkedHashMap<>());
        g.add(stackedBarChart, c);

        c.gridx = 2;
        c.gridwidth = 2;
        alertsPanel = new AlertsPanel();
        g.add(alertsPanel, c);

        return g;
    }

    // =========================================================================
    // INNER CLASSES (PANELS)
    // =========================================================================

    // 1. BASE PANEL
    // =========================================================================
    // 1. BASE PANEL (NÂNG CẤP: BO TRÒN + BÓNG ĐỔ NHẸ)
    // =========================================================================
    static class BasePanel extends JPanel {
        protected final DecimalFormat formatter = new DecimalFormat("#,##0");

        public BasePanel() {
            setOpaque(false); // Để vẽ background thủ công
            setBorder(new EmptyBorder(10, 10, 10, 10)); // Khoảng cách bóng đổ
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 5;
            int y = 5;
            int w = getWidth() - 10;
            int h = getHeight() - 10;
            int arc = 20; // Độ bo tròn

            // 1. Vẽ bóng đổ (Shadow)
            g2.setColor(new Color(220, 220, 220));
            g2.fillRoundRect(x + 3, y + 3, w, h, arc, arc);

            // 2. Vẽ nền trắng (Card)
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            // 3. Vẽ viền mỏng
            g2.setColor(new Color(240, 240, 240));
            g2.drawRoundRect(x, y, w, h, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        protected void createChartTitle(Graphics2D g, String t) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(new Color(80, 80, 80)); // Màu chữ xám đậm sang trọng hơn đen
            g.drawString(t, 25, 35);
        }

        protected void drawLegend(Graphics2D g, int x, int y, Color c, String t) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(c);
            g.fillOval(x, y - 10, 10, 10); // Dùng hình tròn thay vì hình vuông cho legend
            g.setColor(new Color(100, 100, 100));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(t, x + 15, y);
        }
    }

    // 2. REVENUE PIE CHART
    // =========================================================================
    // CLASS MỚI: REVENUE PIE CHART (PHIÊN BẢN BIG & VIVID)
    // =========================================================================
    static class RevenuePieChartPanel extends BasePanel {
        private Map<String, Double> dataMap;

        public RevenuePieChartPanel(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new HashMap<>();
        }

        public void setData(Map<String, Double> map) {
            this.dataMap = (map != null) ? map : new HashMap<>();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Vẽ nền card
            Graphics2D g2 = (Graphics2D) g.create();

            // 1. Cấu hình Render chất lượng cao nhất
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            createChartTitle(g2, "CƠ CẤU DOANH THU");

            if (dataMap == null || dataMap.isEmpty()) {
                g2.setColor(TEXT_MUTED);
                g2.drawString("Không có dữ liệu.", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            double total = dataMap.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total == 0) {
                g2.setColor(TEXT_MUTED);
                g2.drawString("Doanh thu bằng 0.", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            // --- 2. TÍNH TOÁN KÍCH THƯỚC ĐỘNG (TO HƠN) ---
            int w = getWidth();
            int h = getHeight();
            // Cho phép biểu đồ chiếm 75% chiều cao panel (Thay vì fix cứng 220px như cũ)
            int size = (int) (Math.min(w, h) * 0.75);

            // Giới hạn max size để không quá khổng lồ trên màn hình 4k, nhưng vẫn to hơn cũ nhiều
            if (size > 400) size = 400;
            if (size < 200) size = 200; // Đảm bảo không quá bé

            // Căn giữa theo chiều dọc, lệch trái một chút để chừa chỗ cho Legend
            int chartX = (w / 2) - (size / 2) - 60;
            int chartY = (h - size) / 2 + 10;

            // --- 3. VẼ BÓNG ĐỔ (SHADOW) ---
            g2.setColor(new Color(220, 220, 220, 100));
            g2.fillOval(chartX + 4, chartY + 4, size, size);

            // --- 4. VẼ CÁC MIẾNG PIE ---
            double currentAngle = 90;
            DecimalFormat df = new DecimalFormat("#,##0");

            // Tọa độ Legend nằm bên phải biểu đồ
            int legendX = chartX + size + 40;
            int legendY = chartY + (size / 2) - (dataMap.size() * 25); // Căn giữa Legend theo chiều dọc

            for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
                double val = entry.getValue();
                if (val <= 0) continue;

                double angle = (val / total) * 360;

                // Chọn màu cơ bản
                Color baseColor;
                if (entry.getKey().contains("Tiền mặt")) baseColor = COLOR_CASH;
                else if (entry.getKey().contains("Chuyển khoản")) baseColor = COLOR_TRANSFER;
                else baseColor = COLOR_OTHER;

                // --- KỸ THUẬT GRADIENT (TẠO KHỐI 3D) ---
                // Tạo dải màu từ sáng (góc trên trái) sang tối (góc dưới phải) của chính màu đó
                GradientPaint gp = new GradientPaint(
                        chartX, chartY, baseColor.brighter(),
                        chartX + size, chartY + size, baseColor.darker()
                );

                g2.setPaint(gp);
                g2.fill(new Arc2D.Double(chartX, chartY, size, size, currentAngle, -angle, Arc2D.PIE));

                // Vẽ viền trắng mỏng để tách các miếng
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Arc2D.Double(chartX, chartY, size, size, currentAngle, -angle, Arc2D.PIE));

                // --- VẼ LEGEND (CHÚ THÍCH) ---
                // Bullet point tròn
                g2.setColor(baseColor);
                g2.fillOval(legendX, legendY, 12, 12);

                // Tên + Phần trăm
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                double percent = (val / total) * 100;
                String labelTitle = String.format("%s (%.1f%%)", entry.getKey(), percent);
                g2.drawString(labelTitle, legendX + 20, legendY + 11);

                // Số tiền (Dòng dưới)
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString(df.format(val) + " VND", legendX + 20, legendY + 28);

                legendY += 50; // Khoảng cách giữa các mục legend
                currentAngle -= angle;
            }

            // --- 5. VẼ LỖ TRÒN (HIỆU ỨNG DONUT) ---
            int innerSize = (int) (size * 0.50); // Lỗ tròn bằng 50% kích thước
            int innerX = chartX + (size - innerSize) / 2;
            int innerY = chartY + (size - innerSize) / 2;

            // Bóng đổ trong cho lỗ tròn (tạo chiều sâu)
            g2.setColor(new Color(200, 200, 200, 50));
            g2.fillOval(innerX - 2, innerY - 2, innerSize + 4, innerSize + 4);

            g2.setColor(Color.WHITE);
            g2.fillOval(innerX, innerY, innerSize, innerSize);

            // --- 6. VẼ TEXT TỔNG Ở GIỮA ---
            g2.setColor(TEXT_MUTED);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            String totalLabel = "Tổng thu";
            int labelW = g2.getFontMetrics().stringWidth(totalLabel);
            g2.drawString(totalLabel, innerX + (innerSize - labelW) / 2, innerY + innerSize / 2 - 5);

            g2.setColor(TEXT_COLOR);
            // Font to hơn, đậm hơn cho số tiền
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

            // Rút gọn số tiền nếu quá lớn (ví dụ: 1.5 Tỷ) để vừa lỗ tròn
            String moneyStr;
            if (total >= 1_000_000_000) {
                moneyStr = String.format("%.1f Tỷ", total / 1_000_000_000);
            } else if (total >= 1_000_000) {
                moneyStr = String.format("%.1f Tr", total / 1_000_000);
            } else {
                moneyStr = df.format(total);
            }

            int moneyW = g2.getFontMetrics().stringWidth(moneyStr);
            g2.drawString(moneyStr, innerX + (innerSize - moneyW) / 2, innerY + innerSize / 2 + 20);

            g2.dispose();
        }
    }

    // 3. INVOICE LINE CHART
    // =========================================================================
    // 2. BIỂU ĐỒ ĐƯỜNG: HÓA ĐƠN (PHIÊN BẢN NÂNG CẤP: SMOOTH + AREA FILL)
    // =========================================================================
    static class InvoiceAnalysisChartPanel extends BasePanel {
        private Map<LocalDate, Integer> soldData;
        private Map<LocalDate, Integer> refundData;
        private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM");
        private Point mousePoint = null;

        public InvoiceAnalysisChartPanel() {
            this.soldData = new LinkedHashMap<>();
            this.refundData = new LinkedHashMap<>();

            // Sự kiện chuột để hover hiệu ứng
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePoint = e.getPoint();
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    mousePoint = null;
                    repaint();
                }
            });
        }

        public void setData(Map<LocalDate, Integer> sold, Map<LocalDate, Integer> refund, DateTimeFormatter fmt) {
            this.soldData = (sold != null) ? sold : new LinkedHashMap<>();
            this.refundData = (refund != null) ? refund : new LinkedHashMap<>();
            if (fmt != null) this.dateFormat = fmt;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Khử răng cưa chất lượng cao
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            createChartTitle(g2, "TÌNH HÌNH HÓA ĐƠN");

            // Vẽ Legend đẹp hơn (Hình tròn thay vì vuông)
            drawLegend(g2, getWidth() - 250, 35, new Color(59, 130, 246), "Hóa đơn bán");
            drawLegend(g2, getWidth() - 130, 35, new Color(239, 68, 68), "Hóa đơn hoàn/đổi");

            if (soldData.isEmpty() && refundData.isEmpty()) {
                g2.setColor(TEXT_MUTED);
                g2.drawString("Không có dữ liệu.", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }

            Set<LocalDate> allDates = new TreeSet<>(soldData.keySet());
            allDates.addAll(refundData.keySet());
            List<LocalDate> dateList = new ArrayList<>(allDates);
            if (dateList.isEmpty()) return;

            int maxSold = soldData.values().stream().max(Integer::compare).orElse(0);
            int maxRefund = refundData.values().stream().max(Integer::compare).orElse(0);
            int maxVal = Math.max(maxSold, maxRefund);
            if (maxVal == 0) maxVal = 1;
            maxVal = (int) (maxVal * 1.2); // Tăng trần thêm 20% cho thoáng

            Insets ins = getInsets();
            int padding = 20, leftM = 40, bottomM = 40;
            int w = getWidth() - ins.left - ins.right - padding - leftM;
            int h = getHeight() - ins.top - ins.bottom - 60 - bottomM;
            int x0 = ins.left + leftM, y0 = ins.top + 60, yBase = y0 + h;

            // --- 1. VẼ LƯỚI NỀN (Dashed Grid) ---
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            Stroke defaultStroke = g2.getStroke();
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0);

            for (int i = 0; i <= 5; i++) {
                int y = yBase - (i * h / 5);
                g2.setColor(new Color(230, 230, 230)); // Màu lưới nhạt
                g2.setStroke(dashed);
                g2.drawLine(x0, y, x0 + w, y);

                g2.setStroke(defaultStroke);
                g2.setColor(TEXT_MUTED);
                g2.drawString(String.valueOf(maxVal * i / 5), x0 - 25, y + 4);
            }

            int n = dateList.size();
            double stepX = (n > 1) ? (double) w / (n - 1) : 0;

            // Tính toán tọa độ các điểm
            Point[] pSold = new Point[n];
            Point[] pRefund = new Point[n];

            for (int i = 0; i < n; i++) {
                LocalDate d = dateList.get(i);
                int vSold = soldData.getOrDefault(d, 0);
                int vRefund = refundData.getOrDefault(d, 0);

                int x = (n == 1) ? (x0 + w / 2) : (x0 + (int) (i * stepX));
                int ySold = yBase - (int) ((double) vSold / maxVal * h);
                int yRefund = yBase - (int) ((double) vRefund / maxVal * h);

                pSold[i] = new Point(x, ySold);
                pRefund[i] = new Point(x, yRefund);

                // Vẽ nhãn trục X (giãn cách nếu quá nhiều điểm)
                if (n <= 15 || i % (n / 10) == 0 || i == n - 1) {
                    g2.setColor(TEXT_MUTED);
                    String label = d.format(dateFormat);
                    int lblW = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, x - lblW / 2, yBase + 20);
                }
            }

            // --- 2. VẼ ĐƯỜNG BIỂU ĐỒ (Smooth Curves + Area Fill) ---
            if (n > 1) {
                // Vẽ đường Bán (Xanh)
                drawSmoothLine(g2, pSold, new Color(59, 130, 246), yBase);
                // Vẽ đường Hoàn (Đỏ)
                drawSmoothLine(g2, pRefund, new Color(239, 68, 68), yBase);
            }

            // --- 3. VẼ ĐIỂM (Points) & TOOLTIP ---
            // Vẽ các điểm tròn nhỏ tại mỗi mốc dữ liệu
            for (int i = 0; i < n; i++) {
                drawDataPoint(g2, pSold[i], new Color(59, 130, 246), dateList.get(i), soldData.getOrDefault(dateList.get(i), 0), "Bán");
                drawDataPoint(g2, pRefund[i], new Color(239, 68, 68), dateList.get(i), refundData.getOrDefault(dateList.get(i), 0), "Hoàn/Đổi");
            }
        }

        // Hàm vẽ đường cong mượt mà + tô màu vùng dưới
        private void drawSmoothLine(Graphics2D g2, Point[] points, Color color, int yBase) {
            if (points.length < 2) return;

            // Tạo đường cong (Path)
            java.awt.geom.GeneralPath path = new java.awt.geom.GeneralPath();
            path.moveTo(points[0].x, points[0].y);

            for (int i = 0; i < points.length - 1; i++) {
                double x1 = points[i].x;
                double y1 = points[i].y;
                double x2 = points[i + 1].x;
                double y2 = points[i + 1].y;

                // Logic Control Points để tạo độ cong (Curvature)
                double ctrlX1 = x1 + (x2 - x1) / 3;
                double ctrlY1 = y1;
                double ctrlX2 = x2 - (x2 - x1) / 3;
                double ctrlY2 = y2;

                path.curveTo(ctrlX1, ctrlY1, ctrlX2, ctrlY2, x2, y2);
            }

            // --- Bước 1: Tô màu vùng dưới (Area Fill) ---
            // Tạo bản sao của path để khép kín vùng tô màu
            java.awt.geom.GeneralPath fillPath = (java.awt.geom.GeneralPath) path.clone();
            fillPath.lineTo(points[points.length - 1].x, yBase); // Kéo xuống đáy
            fillPath.lineTo(points[0].x, yBase);               // Kéo về đầu
            fillPath.closePath();

            // Gradient tô màu (Mờ dần từ trên xuống dưới)
            GradientPaint fillPaint = new GradientPaint(0, 0, new Color(color.getRed(), color.getGreen(), color.getBlue(), 80),
                    0, yBase, new Color(color.getRed(), color.getGreen(), color.getBlue(), 5));
            g2.setPaint(fillPaint);
            g2.fill(fillPath);

            // --- Bước 2: Vẽ đường viền (Line Stroke) ---
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(path);
        }

        // Hàm vẽ điểm dữ liệu (Có hiệu ứng Hover)
        private void drawDataPoint(Graphics2D g2, Point p, Color color, LocalDate date, int value, String name) {
            int r = 6; // Bán kính thường
            boolean isHovered = false;

            // Kiểm tra xem chuột có đang hover gần điểm này không
            if (mousePoint != null && mousePoint.distance(p) < 15) {
                isHovered = true;
                r = 9; // Phóng to khi hover
            }

            // Vẽ điểm tròn trắng viền màu
            g2.setColor(Color.WHITE);
            g2.fillOval(p.x - r / 2, p.y - r / 2, r, r);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(p.x - r / 2, p.y - r / 2, r, r);

            // Nếu hover thì hiện Tooltip
            if (isHovered) {
                drawTooltip(g2, name + ": " + value + " (" + date.format(dateFormat) + ")", p.x, p.y);
            }
        }

        private void drawTooltip(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 12, h = fm.getHeight() + 6;

            int bx = x + 10;
            int by = y - 25;
            // Xử lý tràn màn hình
            if (bx + w > getWidth()) bx = x - w - 10;
            if (by < 0) by = y + 15;

            // Vẽ bóng đổ nhẹ
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(bx + 2, by + 2, w, h, 5, 5);

            // Vẽ nền tooltip
            g2.setColor(new Color(255, 255, 240));
            g2.fillRoundRect(bx, by, w, h, 5, 5);

            // Vẽ viền
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(bx, by, w, h, 5, 5);

            // Vẽ chữ
            g2.setColor(Color.BLACK);
            g2.drawString(text, bx + 6, by + h - 6);
        }
    }

    // 4. STACKED BAR CHART
// =========================================================================
    // 4. STACKED BAR CHART (FULL CODE: DỮ LIỆU + GIAO DIỆN ĐẸP)
    // =========================================================================
// =========================================================================
    // 4. STACKED BAR CHART (FULL CODE: ĐẸP + THÔNG MINH + KHÔNG LỖI)
    // =========================================================================
// =========================================================================
    // 4. STACKED BAR CHART (HIỂN THỊ TẤT CẢ NGÀY - KHÔNG BỎ BƯỚC)
    // =========================================================================
// =========================================================================
    // 4. STACKED BAR CHART (CHỈ HIỆN NGÀY CÓ DỮ LIỆU - KHÔNG LẤP ĐẦY)
    // =========================================================================
    static class StackedBarChartPanel extends BasePanel {
        private Map<LocalDate, Map<String, Integer>> data = new LinkedHashMap<>();
        private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        private int viewType = 0;
        private Point mousePoint = null;
        private java.awt.geom.Rectangle2D hoveredRect = null;

        public StackedBarChartPanel(Map<LocalDate, Map<String, Integer>> data) {
            this.data = (data != null) ? data : new LinkedHashMap<>();
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mousePoint = e.getPoint();
                    hoveredRect = null;
                    repaint();
                }
            });
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    mousePoint = null;
                    hoveredRect = null;
                    repaint();
                }
            });
        }

        public void setViewType(int v) {
            this.viewType = v;
            repaint();
        }

        public void setData(Map<LocalDate, Map<String, Integer>> d) {
            this.data = (d != null) ? d : new LinkedHashMap<>();
            repaint();
        }

        public void setDateFormat(DateTimeFormatter p) {
            this.fmt = p;
            repaint();
        }

        // --- HELPER METHODS ---
        private String normalizeSeat(String s) {
            s = s.toLowerCase();
            if (s.contains("khoang 4")) return "Giường nằm 4";
            if (s.contains("khoang 6")) return "Giường nằm 6";
            if (s.contains("ngồi")) return "Ghế ngồi";
            return s;
        }

        private String[] getSeatTypes() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            for (Map<String, Integer> sm : data.values()) for (String k : sm.keySet()) set.add(normalizeSeat(k));
            if (set.isEmpty()) {
                set.add("Giường nằm 4");
                set.add("Giường nằm 6");
                set.add("Ghế ngồi");
            }
            return set.toArray(new String[0]);
        }

        private Map<String, Integer> emptySeatMap(String[] types) {
            Map<String, Integer> m = new LinkedHashMap<>();
            for (String s : types) m.put(s, 0);
            return m;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            createChartTitle(g2, "CƠ CẤU VÉ THEO LOẠI GHẾ");

            if (data == null || data.isEmpty()) {
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString("Không có dữ liệu.", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            // --- 1. XỬ LÝ DỮ LIỆU ---
            String[] seatTypes = getSeatTypes();
            List<String> xLabels = new ArrayList<>();
            List<Map<String, Integer>> stacked = new ArrayList<>();
            List<LocalDate> dates = new ArrayList<>(data.keySet());
            Collections.sort(dates);

            if (viewType == 1) { // 12 Tháng
                int year = dates.isEmpty() ? LocalDate.now().getYear() : dates.get(0).getYear();
                for (int m = 1; m <= 12; m++) {
                    xLabels.add("T" + m);
                    Map<String, Integer> total = emptySeatMap(seatTypes);
                    for (LocalDate d : dates) {
                        if (d.getYear() == year && d.getMonthValue() == m) {
                            Map<String, Integer> sm = data.get(d);
                            for (String s : sm.keySet()) total.merge(normalizeSeat(s), sm.get(s), Integer::sum);
                        }
                    }
                    stacked.add(total);
                }
            } else if (viewType == 2) { // Năm
                Map<Integer, Map<String, Integer>> yearMap = new TreeMap<>();
                for (LocalDate d : dates) {
                    int y = d.getYear();
                    yearMap.putIfAbsent(y, emptySeatMap(seatTypes));
                    Map<String, Integer> sm = data.get(d);
                    for (String s : sm.keySet()) yearMap.get(y).merge(normalizeSeat(s), sm.get(s), Integer::sum);
                }
                for (Integer y : yearMap.keySet()) {
                    xLabels.add(String.valueOf(y));
                    stacked.add(yearMap.get(y));
                }
            } else {
                // --- LOGIC MỚI: CHỈ DUYỆT CÁC NGÀY CÓ TRONG DỮ LIỆU ---
                for (LocalDate d : dates) {
                    xLabels.add(d.format(fmt));

                    Map<String, Integer> daySum = emptySeatMap(seatTypes);
                    Map<String, Integer> sm = data.get(d);

                    if (sm != null) {
                        for (String s : sm.keySet()) {
                            daySum.merge(normalizeSeat(s), sm.get(s), Integer::sum);
                        }
                    }
                    stacked.add(daySum);
                }
            }

            int n = xLabels.size();
            if (n == 0) return;

            double max = 0;
            for (Map<String, Integer> sm : stacked) {
                max = Math.max(max, sm.values().stream().mapToInt(Integer::intValue).sum());
            }
            if (max == 0) max = 1;
            max *= 1.1;

            // --- 2. VẼ KHUNG & LƯỚI ---
            Insets ins = getInsets();
            int padding = 20, topSpace = 80, bottomSpace = 40;
            String maxStr = formatter.format(max);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int yLabelW = g2.getFontMetrics().stringWidth(maxStr) + 15;

            int x0 = ins.left + padding + yLabelW;
            int w = getWidth() - x0 - padding;
            int h = getHeight() - ins.top - ins.bottom - topSpace - bottomSpace;
            int y0 = ins.top + topSpace;
            int yBase = y0 + h;

            Stroke defaultStroke = g2.getStroke();
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 5}, 0);

            for (int i = 0; i <= 5; i++) {
                int y = yBase - (i * h / 5);
                g2.setColor(new Color(230, 230, 230));
                g2.setStroke(dashed);
                g2.drawLine(x0, y, x0 + w, y);

                g2.setStroke(defaultStroke);
                g2.setColor(TEXT_MUTED);
                String label = formatter.format(max * i / 5);
                g2.drawString(label, x0 - g2.getFontMetrics().stringWidth(label) - 5, y + 4);
            }

            // --- 3. VẼ CỘT ---
            double slotW = (double) w / n;
            int barW = (int) (slotW * 0.6);
            if (barW > 60) barW = 60;

            String hoverText = null;
            int hx = 0, hy = 0;

            for (int i = 0; i < n; i++) {
                double center = x0 + (i * slotW) + slotW / 2;
                int x = (int) (center - barW / 2);
                int yStack = yBase;
                Map<String, Integer> sm = stacked.get(i);
                int colorIndex = 0;

                for (String seat : seatTypes) {
                    int v = sm.get(seat);
                    if (v > 0) {
                        int bh = (int) ((double) v / max * h);
                        if (bh < 1) bh = 1;
                        int y = yStack - bh;

                        Color baseColor = CHART_COLORS[colorIndex % CHART_COLORS.length];
                        java.awt.geom.Rectangle2D rect = new java.awt.geom.Rectangle2D.Double(x, y, barW, bh);

                        if (mousePoint != null && rect.contains(mousePoint)) {
                            hoverText = seat + ": " + formatter.format(v) + " — " + xLabels.get(i);
                            hx = mousePoint.x;
                            hy = mousePoint.y;
                            hoveredRect = rect;
                            baseColor = baseColor.brighter();
                        }

                        GradientPaint gp = new GradientPaint(x, y, baseColor, x, y + bh, baseColor.darker());
                        g2.setPaint(gp);
                        g2.fill(rect);
                        g2.setColor(baseColor.darker());
                        g2.draw(rect);

                        yStack -= bh;
                    }
                    colorIndex++;
                }

                // Vẽ nhãn trục X (Vẽ tất cả, không bỏ qua)
                g2.setColor(TEXT_MUTED);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

                String lbl = xLabels.get(i);
                int lblW = g2.getFontMetrics().stringWidth(lbl);

                g2.drawString(lbl, (int) (center - lblW / 2), yBase + 20);

                g2.setColor(new Color(200, 200, 200));
                g2.drawLine((int) center, yBase, (int) center, yBase + 4);
            }

            if (hoveredRect != null) {
                g2.setColor(new Color(255, 255, 255, 150));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(hoveredRect);
                g2.setStroke(defaultStroke);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int lx = x0, ly = ins.top + 50;
            for (int i = 0; i < seatTypes.length; i++) {
                drawLegend(g2, lx, ly, CHART_COLORS[i % CHART_COLORS.length], seatTypes[i]);
                lx += 140;
            }

            if (hoverText != null) drawTooltip(g2, hoverText, hx, hy);
        }

        private void drawTooltip(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 12, h = fm.getHeight() + 6;
            int bx = x + 10, by = y - 25;
            if (bx + w > getWidth()) bx = x - w - 5;
            if (by < 0) by = y + 15;

            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(bx + 2, by + 2, w, h, 5, 5);

            g2.setColor(new Color(255, 255, 240));
            g2.fillRoundRect(bx, by, w, h, 5, 5);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(bx, by, w, h, 5, 5);
            g2.setColor(Color.BLACK);
            g2.drawString(text, bx + 6, by + h - 6);
        }
    }

    // 6. KPI CARD
    // =========================================================================
    // 2. KPI CARD (NÂNG CẤP: GRADIENT + ICON GIẢ LẬP)
    // =========================================================================
    static class KpiCard extends JPanel { // Kế thừa JPanel thường để tự vẽ full
        private String title, value, sub;
        private Color color1, color2;

        public KpiCard(String title, String value, String sub, Color baseColor) {
            this.title = title;
            this.value = value;
            this.sub = sub;
            // Tạo hiệu ứng Gradient từ màu gốc
            this.color1 = baseColor;
            this.color2 = baseColor.darker();
            setOpaque(false);
            setPreferredSize(new Dimension(220, 120));
        }

        public void setData(String v, String s) {
            this.value = v;
            this.sub = s;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();

            // Vẽ Gradient Background
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 25, 25); // Bo tròn mạnh hơn

            // Vẽ họa tiết trang trí mờ (Circle transparent)
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillOval(w - 60, -20, 100, 100);
            g2.fillOval(w - 90, h - 50, 70, 70);

            // Vẽ Nội dung
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(title.toUpperCase(), 20, 30); // Tiêu đề

            g2.setFont(new Font("Segoe UI", Font.BOLD, 26));
            g2.drawString(value, 20, 70); // Số liệu chính

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString(sub, 20, 95); // Phụ đề

            g2.dispose();
        }
    }

    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton s = (JButton) e.getSource();
            updateFilterButtonStyle(s);

            LocalDate n = LocalDate.now();
            LocalDate st = null, en = null;
            int v = 0;
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM");

            if (s == btnToday) {
                st = n;
                en = n;
            } else if (s == btnWeek) {
                st = n.with(DayOfWeek.MONDAY);
                en = n.with(DayOfWeek.SUNDAY);
                f = DateTimeFormatter.ofPattern("EEE (dd/MM)", new Locale("vi", "VN"));
            } else if (s == btnMonth) {
                st = n.withDayOfMonth(1);
                en = n.with(TemporalAdjusters.lastDayOfMonth());
            } else if (s == btnYear) {
                st = n.withDayOfYear(1);
                en = n.with(TemporalAdjusters.lastDayOfYear());
                v = 1;
                f = DateTimeFormatter.ofPattern("'T'M");
            } else if (s == btnAll) {
                st = null;
                en = null;
                f = DateTimeFormatter.ofPattern("yyyy");
                v = 2;
            }

            loadDashboardData(st, en, v, f);
        }
    }

    // 5. ALERTS PANEL
    // =========================================================================
    // 5. ALERTS PANEL (ĐÃ FIX LỖI TRÀN CHỮ: CĂN GIỮA NỘI DUNG)
    // =========================================================================
    class AlertsPanel extends BasePanel {
        private int high = 0;
        private int low = 0;

        private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
        private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

        // Định nghĩa Font sẵn để dùng chung
        private Font fontNumber = new Font("Segoe UI", Font.BOLD, 36);
        private Font fontTitle = new Font("Segoe UI", Font.BOLD, 15);
        private Font fontIcon = new Font("Segoe UI", Font.BOLD, 20);

        public AlertsPanel() {
            super();
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (getGreenBounds().contains(e.getPoint()) || getRedBounds().contains(e.getPoint())) {
                        setCursor(handCursor);
                    } else {
                        setCursor(defaultCursor);
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (getGreenBounds().contains(e.getPoint())) {
                        showDetailsDialog("CHI TIẾT CHUYẾN SẮP HẾT VÉ", false);
                    } else if (getRedBounds().contains(e.getPoint())) {
                        showDetailsDialog("CHI TIẾT CHUYẾN CÓ TỈ LỆ BÁN THẤP", true);
                    }
                }
            });
        }

        public void setAlertData(int h, int l) {
            this.high = h;
            this.low = l;
            repaint();
        }

        private Rectangle getGreenBounds() {
            int padding = 20; // Giảm padding chút cho rộng
            int titleH = 40;  // Giảm chiều cao title chút
            int w = getWidth() - padding * 2;

            // Tính toán chiều cao box động
            int availableH = getHeight() - titleH - (padding * 2);
            int boxH = (availableH - 20) / 2; // 20 là khoảng cách giữa 2 box

            // Giới hạn chiều cao tối đa và tối thiểu để không bị xấu
            if (boxH > 100) boxH = 100;
            if (boxH < 60) boxH = 60; // Đảm bảo tối thiểu đủ chứa chữ

            return new Rectangle(padding, titleH + 10, w, boxH);
        }

        private Rectangle getRedBounds() {
            Rectangle g = getGreenBounds();
            return new Rectangle(g.x, g.y + g.height + 20, g.width, g.height);
        }

        // Hàm hỗ trợ vẽ nội dung căn giữa vào trong Box
        private void drawBoxContent(Graphics2D g2, Rectangle box, String numberStr, String titleStr, String iconStr) {
            // 1. Tính toán kích thước Font
            g2.setFont(fontNumber);
            FontMetrics fmNum = g2.getFontMetrics();
            int numHeight = fmNum.getAscent();

            g2.setFont(fontTitle);
            FontMetrics fmTitle = g2.getFontMetrics();
            int titleHeight = fmTitle.getAscent();

            int gap = 5; // Khoảng cách giữa số và chữ
            int totalContentHeight = numHeight + gap + titleHeight;

            // 2. Tính tọa độ Y bắt đầu để khối văn bản nằm CHÍNH GIỮA box theo chiều dọc
            int startY = box.y + (box.height - totalContentHeight) / 2;

            // 3. Vẽ Số (Number)
            g2.setColor(Color.WHITE);
            g2.setFont(fontNumber);
            // Vẽ số tại tọa độ Baseline
            int numY = startY + numHeight;
            g2.drawString(numberStr, box.x + 25, numY);

            // 4. Vẽ Tiêu đề (Title)
            g2.setFont(fontTitle);
            int titleY = numY + gap + titleHeight;
            g2.drawString(titleStr, box.x + 25, titleY);

            // 5. Vẽ Icon góc phải (Căn giữa theo chiều dọc của box)
            g2.setFont(fontIcon);
            FontMetrics fmIcon = g2.getFontMetrics();
            int iconW = fmIcon.stringWidth(iconStr);
            int iconH = fmIcon.getAscent();
            int iconY = box.y + (box.height + iconH) / 2 - 5; // Căn giữa tương đối
            g2.drawString(iconStr, box.x + box.width - iconW - 20, iconY);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            createChartTitle(g2, "CẢNH BÁO VẬN HÀNH");

            Rectangle gr = getGreenBounds();
            Rectangle re = getRedBounds();

            // --- VẼ BOX XANH ---
            GradientPaint greenGrad = new GradientPaint(gr.x, gr.y, new Color(46, 204, 113),
                    gr.x + gr.width, gr.y + gr.height, new Color(129, 236, 156));
            g2.setPaint(greenGrad);
            g2.fillRoundRect(gr.x, gr.y, gr.width, gr.height, 20, 20);

            // Decoration
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillOval(gr.x + gr.width - 50, gr.y - 10, 80, 80);

            // Vẽ nội dung XANH (Gọi hàm helper)
            drawBoxContent(g2, gr, String.valueOf(high), "Chuyến sắp hết vé", "↗");


            // --- VẼ BOX ĐỎ ---
            GradientPaint redGrad = new GradientPaint(re.x, re.y, new Color(231, 76, 60),
                    re.x + re.width, re.y + re.height, new Color(255, 107, 107));
            g2.setPaint(redGrad);
            g2.fillRoundRect(re.x, re.y, re.width, re.height, 20, 20);

            // Decoration
            g2.setColor(new Color(255, 255, 255, 50));
            g2.fillOval(re.x + re.width - 50, re.y - 10, 80, 80);

            // Vẽ nội dung ĐỎ (Gọi hàm helper)
            drawBoxContent(g2, re, String.valueOf(low), "Chuyến có tỉ lệ bán thấp", "!");

            g2.dispose();
        }
    }
}