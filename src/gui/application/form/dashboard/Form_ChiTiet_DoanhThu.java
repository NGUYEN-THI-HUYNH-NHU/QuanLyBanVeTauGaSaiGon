package gui.application.form.dashboard;

import dao.Dashboard_DAO; // Import DAO
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Form (JPanel) chi tiết doanh thu, được mở trong JDialog.
 * [CẬP NHẬT] - Hiển thị 4 biểu đồ theo lưới 2x2.
 */
public class Form_ChiTiet_DoanhThu extends JPanel {

    // --- (Copy màu sắc và BasePanel từ Dashboard.java) ---
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

    // Lớp BasePanel (Copy từ Dashboard)
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
    }
    // --- (Kết thúc copy BasePanel) ---

    private LocalDate startDate;
    private LocalDate endDate;
    private Dashboard_DAO dao;

    // [THAY ĐỔI] Khai báo 4 panel biểu đồ
    private RevenueByRouteChartPanel chartRevenueByRoute;
    private RevenueByMonthChartPanel chartRevenueByMonth;
    private RevenueByEmployeeChartPanel chartRevenueByEmployee; // Mới
    private RevenueBySeatTypeChartPanel chartRevenueBySeatType; // Mới

    public Form_ChiTiet_DoanhThu(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dao = new Dashboard_DAO(); // Khởi tạo DAO

        initComponents();
        loadDetailData();
    }

    private void initComponents() {
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setLayout(new BorderLayout(10, 10)); // Layout chính

        // Tiêu đề
        String dateFilterText;
        if (startDate == null && endDate == null) {
            dateFilterText = " (Tất cả)";
        } else if (startDate != null && endDate != null && startDate.equals(endDate)) {
            dateFilterText = " (Ngày: " + startDate.toString() + ")";
        } else {
            String start = (startDate != null) ? startDate.toString() : "Bắt đầu";
            String end = (endDate != null) ? endDate.toString() : "Hiện tại";
            dateFilterText = String.format(" (Từ %s đến %s)", start, end);
        }

        JLabel lblTitle = new JLabel("BÁO CÁO CHI TIẾT DOANH THU" + dateFilterText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // [THAY ĐỔI] Panel trung tâm 2x2
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10)); // 2 hàng, 2 cột
        centerPanel.setOpaque(false);

        // Khởi tạo 4 biểu đồ với dữ liệu rỗng
        chartRevenueByRoute = new RevenueByRouteChartPanel(new LinkedHashMap<>());
        chartRevenueByMonth = new RevenueByMonthChartPanel(new LinkedHashMap<>());
        chartRevenueByEmployee = new RevenueByEmployeeChartPanel(new LinkedHashMap<>()); // Mới
        chartRevenueBySeatType = new RevenueBySeatTypeChartPanel(new LinkedHashMap<>()); // Mới

        centerPanel.add(chartRevenueByRoute);
        centerPanel.add(chartRevenueByMonth);
        centerPanel.add(chartRevenueByEmployee); // Thêm
        centerPanel.add(chartRevenueBySeatType); // Thêm

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Tải dữ liệu từ DAO và cập nhật 4 biểu đồ
     */
    private void loadDetailData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            // [THAY ĐỔI] Thêm 2 biến
            Map<String, Double> routeData;
            Map<String, Double> monthData;
            Map<String, Double> employeeData;
            Map<String, Double> seatTypeData;

            @Override
            protected Void doInBackground() throws Exception {
                routeData = dao.getRevenueByRoute(startDate, endDate);
                monthData = dao.getRevenueByMonth(startDate, endDate);
                employeeData = dao.getRevenueByEmployee(startDate, endDate); // Mới
                seatTypeData = dao.getRevenueBySeatType(startDate, endDate); // Mới
                return null;
            }

            @Override
            protected void done() {
                // Cập nhật 4 biểu đồ
                chartRevenueByRoute.setData(routeData);
                chartRevenueByMonth.setData(monthData);
                chartRevenueByEmployee.setData(employeeData); // Mới
                chartRevenueBySeatType.setData(seatTypeData); // Mới
            }
        };
        worker.execute();
    }

    // =========================================================================
    // LỚP NỘI BỘ 1: BIỂU ĐỒ DOANH THU THEO TUYẾN (CỘT ĐỨNG)
    // =========================================================================
    static class RevenueByRouteChartPanel extends BasePanel {
        private Map<String, Double> dataMap;
        public RevenueByRouteChartPanel(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "DOANH THU THEO TUYẾN (Top 10)");

            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            String[] labels = dataMap.keySet().toArray(new String[0]);
            double[] data = dataMap.values().stream().mapToDouble(Double::doubleValue).toArray();
            int padding = 20; int labelPadding = 30;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 40;
            int x0 = insets.left + padding + labelPadding + 20;
            int y0 = insets.top + padding + 40;
            double maxVal = Arrays.stream(data).max().orElse(1) * 1.1;

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
            int barWidth = (int) (w / (numBars * 1.5));
            int barGap = (w - (barWidth * numBars)) / (numBars + 1);
            for (int i = 0; i < numBars; i++) {
                int barHeight = (int) (data[i] / maxVal * h);
                int x = x0 + barGap + i * (barWidth + barGap);
                int y = y0 + h - barHeight;
                g2d.setColor(CHART_COLORS[0]); // Màu Blue
                g2d.fillRoundRect(x, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_MUTED);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(labels[i], x + (barWidth - fm.stringWidth(labels[i])) / 2, y0 + h + fm.getAscent() + 5);
            }
        }
    }

    // =========================================================================
    // LỚP NỘI BỘ 2: BIỂU ĐỒ DOANH THU THEO THÁNG (ĐƯỜNG)
    // =========================================================================
    static class RevenueByMonthChartPanel extends BasePanel {
        private Map<String, Double> dataMap;
        public RevenueByMonthChartPanel(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            createChartTitle(g2d, "DOANH THU THEO THÁNG (VND)");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            String[] labels = dataMap.keySet().toArray(new String[0]);
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
            g2d.setColor(CHART_COLORS[2]); // Màu Cam
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Polygon p = new Polygon();
            for (int i = 0; i < data.length; i++) {
                int x = x0 + (i * w / Math.max(data.length - 1, 1));
                int y = y0 + h - (int) (data[i] / maxVal * h);
                p.addPoint(x, y);
            }
            g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);
            for (int i = 0; i < p.npoints; i++) {
                g2d.setColor(CHART_COLORS[2]); g2d.fillOval(p.xpoints[i] - 4, p.ypoints[i] - 4, 8, 8);
                g2d.setColor(Color.WHITE); g2d.fillOval(p.xpoints[i] - 2, p.ypoints[i] - 2, 4, 4);
            }
            g2d.setColor(TEXT_MUTED);
            int numLabels = labels.length; int maxLabelsToDraw = 12;
            int step = 1;
            if (numLabels > maxLabelsToDraw) {
                step = (int)Math.ceil((double)numLabels / maxLabelsToDraw);
            }
            for (int i = 0; i < numLabels; i++) {
                if (i % step == 0 || i == numLabels - 1) {
                    int x = x0 + (i * w / Math.max(numLabels - 1, 1));
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(labels[i], x - fm.stringWidth(labels[i]) / 2, y0 + h + labelPadding - 5);
                }
            }
        }
    }

    // =========================================================================
    // LỚP NỘI BỘ 3: BIỂU ĐỒ DOANH THU THEO NHÂN VIÊN (THANH NGANG)
    // =========================================================================
    static class RevenueByEmployeeChartPanel extends BasePanel {
        private Map<String, Double> dataMap;
        public RevenueByEmployeeChartPanel(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "DOANH THU THEO NHÂN VIÊN (Top 10)");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            int padding = 20; int labelPadding = 120; // Đệm cho tên
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - 40;
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 50;
            int numBars = dataMap.size();
            double maxVal = dataMap.values().stream().mapToDouble(Double::doubleValue).max().orElse(1) * 1.1;
            int barHeight = Math.max(10, h / (numBars * 2 - 1));
            int barGap = barHeight;
            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            java.util.List<Map.Entry<String, Double>> entries = new java.util.ArrayList<>(dataMap.entrySet());
            for (i = 0; i < entries.size(); i++) {
                Map.Entry<String, Double> entry = entries.get(i);
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);
                g2d.setColor(TEXT_MUTED); FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);
                g2d.setColor(CHART_COLORS[1]); // Màu Green
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_COLOR); String valueStr = formatter.format(entry.getValue());
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);
            }
        }
    }

    // =========================================================================
    // LỚP NỘI BỘ 4: BIỂU ĐỒ DOANH THU THEO LOẠI GHẾ (THANH NGANG)
    // =========================================================================
    static class RevenueBySeatTypeChartPanel extends BasePanel {
        private Map<String, Double> dataMap;
        public RevenueBySeatTypeChartPanel(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
        }
        public void setData(Map<String, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            this.repaint();
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "DOANH THU THEO LOẠI GHẾ");
            if (dataMap == null || dataMap.isEmpty()) {
                g2d.setColor(TEXT_MUTED); g2d.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2);
                return;
            }
            int padding = 20; int labelPadding = 150; // Đệm cho tên dài
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - 40;
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 50;
            int numBars = dataMap.size();
            double maxVal = dataMap.values().stream().mapToDouble(Double::doubleValue).max().orElse(1) * 1.1;
            int barHeight = Math.max(10, h / (numBars * 2 - 1));
            int barGap = barHeight;
            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            java.util.List<Map.Entry<String, Double>> entries = new java.util.ArrayList<>(dataMap.entrySet());
            for (i = 0; i < entries.size(); i++) {
                Map.Entry<String, Double> entry = entries.get(i);
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);
                g2d.setColor(TEXT_MUTED); FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);
                g2d.setColor(CHART_COLORS[4]); // Màu Purple
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);
                g2d.setColor(TEXT_COLOR); String valueStr = formatter.format(entry.getValue());
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);
            }
        }
    }
}