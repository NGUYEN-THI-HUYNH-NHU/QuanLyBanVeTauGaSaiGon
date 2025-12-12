package gui.application.form.dashboard;

import com.formdev.flatlaf.FlatLightLaf;
import connectDB.ConnectDB;
import dao.Dashboard_DAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private static final Color COLOR_REVENUE = new Color(16, 185, 129);

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
    private Dashboard_DAO dashboardDAO;
    private KpiCard kpiRevenue, kpiTicketsSold, kpiOccupancy, kpiRefundRate;

    private RevenueBarChartPanel revenueBarChart;
    private InvoiceAnalysisChartPanel invoiceAnalysisChart;
    private StackedBarChartPanel stackedBarChart;
    // Đã xóa PromotionRateChartPanel
    private CustomerSplitChartPanel customerSplitChart;

    private JButton btnToday, btnWeek, btnMonth, btnYear, btnAll;

    // ========================================================================

    public Dashboard() {
        try {
            ConnectDB.getInstance();
            this.dashboardDAO = new Dashboard_DAO();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        add(createHeaderBar(), BorderLayout.NORTH);
        add(createMainGrid(), BorderLayout.CENTER);

        if (this.dashboardDAO != null) {
            LocalDate today = LocalDate.now();
            updateFilterButtonStyle(btnToday);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
            revenueBarChart.setDateFormat(fmt);
            stackedBarChart.setDateFormat(fmt);

            loadDashboardData(today, today, 0, fmt);
        }
    }

    // ========================================================================
    // LOAD DATA
    // ========================================================================

    private void loadDashboardData(LocalDate startDate, LocalDate endDate, int viewType, DateTimeFormatter currentFmt) {

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {

                updateKpiCards(startDate, endDate);

                // --- 1. DOANH THU ---
                Map<LocalDate, Double> finalData = new LinkedHashMap<>();
                if (viewType == 1) { // Năm
                    Map<LocalDate, Double> rawData = dashboardDAO.getRevenueOverTimeByMonth(startDate, endDate);
                    LocalDate step = startDate.withDayOfYear(1);
                    LocalDate limit = startDate.with(TemporalAdjusters.lastDayOfYear());
                    while (!step.isAfter(limit)) {
                        finalData.put(step, rawData.getOrDefault(step, 0.0));
                        step = step.plusMonths(1);
                    }
                } else if (viewType == 2) { // Tất cả
                    finalData = dashboardDAO.getRevenueOverTimeByYear(startDate, endDate);
                } else { // Ngày/Tuần/Tháng
                    finalData = dashboardDAO.getRevenueOverTime(startDate, endDate);
                }
                revenueBarChart.setYearlyMode(viewType == 1);
                revenueBarChart.setData(finalData);
                revenueBarChart.setDateFormat(currentFmt);

                // --- 2. NGẢ VÉ (Stacked Bar) ---
                Map<LocalDate, Map<String, Integer>> ticketData = dashboardDAO.getTicketsBySeatTypeOverTime(startDate, endDate);
                stackedBarChart.setViewType(viewType);
                stackedBarChart.setData(ticketData);
                stackedBarChart.setDateFormat(currentFmt);

                // --- 3. BIỂU ĐỒ ĐƯỜNG (HÓA ĐƠN) ---
                Map<LocalDate, Integer> sold, refund;
                if (viewType == 1) {
                    Map<LocalDate, Integer> rawSold = dashboardDAO.getInvoicesPaidByMonth(startDate, endDate);
                    Map<LocalDate, Integer> rawRefund = dashboardDAO.getInvoicesRefundedByMonth(startDate, endDate);

                    sold = new LinkedHashMap<>();
                    refund = new LinkedHashMap<>();
                    LocalDate step = startDate.withDayOfYear(1);
                    for(int i=0; i<12; i++) {
                        sold.put(step, rawSold.getOrDefault(step, 0));
                        refund.put(step, rawRefund.getOrDefault(step, 0));
                        step = step.plusMonths(1);
                    }
                } else if (viewType == 2) {
                    sold = dashboardDAO.getInvoicesPaidByYear(startDate, endDate);
                    refund = dashboardDAO.getInvoicesRefundedByYear(startDate, endDate);
                } else {
                    sold = dashboardDAO.getInvoicesPaidOverTime(startDate, endDate);
                    refund = dashboardDAO.getInvoicesRefundedOverTime(startDate, endDate);
                }
                invoiceAnalysisChart.setData(sold, refund, currentFmt);

                // --- 4. CƠ CẤU KHÁCH (Đã xóa Promotion Chart) ---
                customerSplitChart.setData(dashboardDAO.getCustomerSplitData(startDate, endDate));

                return null;
            }
        };
        worker.execute();
    }

    // =========================================================================
    // BASE PANEL
    // =========================================================================
    static class BasePanel extends JPanel {
        protected final DecimalFormat formatter = new DecimalFormat("#,##0");

        public BasePanel() {
            setBackground(PANEL_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 2),
                    new EmptyBorder(15, 20, 15, 20)
            ));
            setLayout(new BorderLayout());
        }

        protected void createChartTitle(Graphics2D g, String t) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(TEXT_COLOR);
            g.drawString(t, 20, 30);
        }

        protected void drawLegend(Graphics2D g, int x, int y, Color c, String t) {
            g.setColor(c);
            g.fillRect(x, y - 10, 12, 12);
            g.setColor(TEXT_MUTED);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(t, x + 20, y);
        }

        // HÀM VẼ PIE CHART CHUNG
        protected void drawPieChartWithCallouts(Graphics g, String title, Map<String, Integer> data, Map<String, Color> colorMap) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            createChartTitle(g2, title);

            if (data == null || data.isEmpty()) {
                g2.setColor(TEXT_MUTED);
                g2.drawString("Không có dữ liệu.", getWidth() / 2 - 40, getHeight() / 2);
                return;
            }

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            // Tính toán kích thước (60% vùng khả dụng)
            int availableHeight = getHeight() - 80;
            int availableWidth = getWidth() - 200;
            int chartSize = (int) (Math.min(availableWidth, availableHeight) * 0.60);

            if (chartSize > 280) chartSize = 280;
            if (chartSize < 100) chartSize = 100;

            int radius = chartSize / 2;
            int centerX = getWidth() / 2;
            int centerY = (int) (getHeight() * 0.45);

            double currentAngle = 90;
            List<LabelData> labels = new ArrayList<>();

            // 1. Vẽ Pie
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String labelName = entry.getKey();
                int value = entry.getValue();
                if (value == 0) continue;

                double angleExtent = (value / (double) total) * 360;
                Color sliceColor = colorMap.getOrDefault(labelName, Color.LIGHT_GRAY);

                g2.setColor(sliceColor);
                g2.fill(new Arc2D.Double(centerX - radius, centerY - radius, chartSize, chartSize, currentAngle, -angleExtent, Arc2D.PIE));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new Arc2D.Double(centerX - radius, centerY - radius, chartSize, chartSize, currentAngle, -angleExtent, Arc2D.PIE));

                double midAngle = currentAngle - angleExtent / 2;
                int percent = (int) Math.round((value / (double) total) * 100);
                String labelText = String.format("%s: %d (%d%%)", labelName, value, percent);

                labels.add(new LabelData(midAngle, labelText, sliceColor));
                currentAngle -= angleExtent;
            }

            // 2. Vẽ Callout & Text
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            int kneeRadius = radius + 15;
            int tailLength = 15;

            for (LabelData lbl : labels) {
                double rad = Math.toRadians(lbl.angle);
                int x1 = (int) (centerX + Math.cos(rad) * radius);
                int y1 = (int) (centerY - Math.sin(rad) * radius);
                int x2 = (int) (centerX + Math.cos(rad) * kneeRadius);
                int y2 = (int) (centerY - Math.sin(rad) * kneeRadius);

                boolean isRight = (Math.cos(rad) >= 0);
                int x3 = isRight ? x2 + tailLength : x2 - tailLength;
                int y3 = y2;

                g2.setColor(Color.DARK_GRAY);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(x1, y1, x2, y2);
                g2.drawLine(x2, y2, x3, y3);

                FontMetrics fm = g2.getFontMetrics();
                int textW = fm.stringWidth(lbl.text);
                int textH = fm.getHeight();
                int pad = 4;

                int boxX = isRight ? x3 + 2 : x3 - textW - pad * 2 - 2;
                int boxY = y3 - textH / 2 - pad;

                g2.setColor(new Color(0, 0, 0, 30));
                g2.fillRect(boxX + 2, boxY + 2, textW + pad * 2, textH + pad * 2);
                g2.setColor(new Color(255, 255, 225));
                g2.fillRect(boxX, boxY, textW + pad * 2, textH + pad * 2);
                g2.setColor(Color.GRAY);
                g2.drawRect(boxX, boxY, textW + pad * 2, textH + pad * 2);
                g2.setColor(Color.BLACK);
                g2.drawString(lbl.text, boxX + pad, boxY + textH + pad - 4);
            }

            // 3. Legend
            int legendY = getHeight() - 25;
            int totalLegendW = 0;
            for (String key : colorMap.keySet()) {
                if (data.containsKey(key) && data.get(key) > 0) {
                    totalLegendW += g2.getFontMetrics().stringWidth(key) + 30;
                }
            }
            int currentX = (getWidth() - totalLegendW) / 2;
            if (currentX < 10) currentX = 10;

            for (Map.Entry<String, Color> entry : colorMap.entrySet()) {
                String key = entry.getKey();
                if (data.containsKey(key) && data.get(key) > 0) {
                    drawLegend(g2, currentX, legendY, entry.getValue(), key);
                    currentX += g2.getFontMetrics().stringWidth(key) + 30;
                }
            }
        }

        protected static class LabelData {
            double angle; String text; Color color;
            public LabelData(double a, String t, Color c) { this.angle = a; this.text = t; this.color = c; }
        }
    }

    // =========================================================================
    // 1. DOANH THU (BAR CHART)
    // =========================================================================
    static class RevenueBarChartPanel extends BasePanel {
        private Map<LocalDate, Double> dataMap;
        private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM");
        private Point mousePoint = null;
        private boolean isYearlyMode = false;

        public RevenueBarChartPanel(Map<LocalDate, Double> dataMap) {
            this.dataMap = (dataMap != null) ? dataMap : new LinkedHashMap<>();
            addMouseMotionListener(new MouseMotionAdapter() { @Override public void mouseMoved(MouseEvent e) { mousePoint = e.getPoint(); repaint(); } });
            addMouseListener(new MouseAdapter() { @Override public void mouseExited(MouseEvent e) { mousePoint = null; repaint(); } });
        }
        public void setYearlyMode(boolean isYearly) { this.isYearlyMode = isYearly; repaint(); }
        public void setDateFormat(DateTimeFormatter f) { this.dateFormatter = f; repaint(); }
        public void setData(Map<LocalDate, Double> map) { this.dataMap = (map != null) ? map : new LinkedHashMap<>(); repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            createChartTitle(g2, "Doanh thu theo thời gian (VND)");
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            if (dataMap == null || dataMap.isEmpty()) {
                g2.setColor(TEXT_MUTED); g2.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2); return;
            }

            double maxVal = 0;
            ArrayList<LocalDate> dates = null;
            double[] monthly = new double[12];
            int n = 0;

            if (isYearlyMode) {
                n = 12;
                for (Map.Entry<LocalDate, Double> e : dataMap.entrySet()) {
                    int m = e.getKey().getMonthValue() - 1;
                    if (m >= 0 && m < 12) monthly[m] += e.getValue();
                }
                for (double v : monthly) maxVal = Math.max(maxVal, v);
            } else {
                dates = new ArrayList<>(dataMap.keySet());
                Collections.sort(dates);
                n = dates.size();
                maxVal = dataMap.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
            }

            if (maxVal == 0) maxVal = 1; maxVal *= 1.1;
            Insets ins = getInsets();
            int padding = 20, bottom = 40;
            FontMetrics fm = g2.getFontMetrics();
            String maxStr = formatter.format(maxVal);
            int left = fm.stringWidth(maxStr) + 15;
            int x0 = ins.left + padding + left;
            int w = getWidth() - x0 - padding - 20;
            int h = getHeight() - ins.top - ins.bottom - 2*padding - bottom;
            int y0 = ins.top + padding + 30;

            for (int i = 0; i <= 4; i++) {
                int y = y0 + h - (i * h / 4);
                g2.setColor(BORDER_COLOR); g2.drawLine(x0, y, x0 + w, y);
                g2.setColor(TEXT_MUTED);
                g2.drawString(formatter.format(maxVal * i / 4), x0 - fm.stringWidth(maxStr) - 10, y + 4);
            }

            double slotW = (double) w / n;
            int barW = (int) (slotW * 0.6);
            String hover = null; int hx = 0, hy = 0;

            for (int i = 0; i < n; i++) {
                double val; String label;
                if (isYearlyMode) { val = monthly[i]; label = "T" + (i+1); }
                else { LocalDate d = dates.get(i); val = dataMap.get(d); label = d.format(dateFormatter); }

                int x = x0 + (int)(i * slotW) + (int)(slotW * 0.2);
                int bh = (int)(val / maxVal * h);
                int yBar = y0 + h - bh;
                Rectangle bar = new Rectangle(x, yBar, barW, bh);
                Rectangle hit = isYearlyMode ? new Rectangle(x, y0, barW, h) : bar;

                if (mousePoint != null && hit.contains(mousePoint)) {
                    g2.setColor(COLOR_REVENUE.darker()); hover = formatter.format(val); hx = mousePoint.x; hy = mousePoint.y;
                } else g2.setColor(COLOR_REVENUE);
                g2.fillRect(x, yBar, barW, bh);
                g2.setColor(TEXT_MUTED);
                g2.drawString(label, x + barW/2 - fm.stringWidth(label)/2, y0 + h + 20);
            }
            if (hover != null) drawTooltip(g2, hover, hx, hy);
        }

        private void drawTooltip(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 12, h = fm.getHeight() + 6;
            int bx = x + 10, by = y - 25;
            if (bx + w > getWidth()) bx = x - w - 5; if (by < 0) by = y + 15;
            g2.setColor(new Color(255,255,225)); g2.fillRoundRect(bx,by,w,h,5,5);
            g2.setColor(Color.BLACK); g2.drawRoundRect(bx,by,w,h,5,5);
            g2.drawString(text, bx + 6, by + h - 6);
        }
    }

    // =========================================================================
    // 2. BIỂU ĐỒ ĐƯỜNG: HÓA ĐƠN
    // =========================================================================
    static class InvoiceAnalysisChartPanel extends BasePanel {
        private Map<LocalDate, Integer> soldData;
        private Map<LocalDate, Integer> refundData;
        private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM");
        private Point mousePoint = null;

        public InvoiceAnalysisChartPanel() {
            this.soldData = new LinkedHashMap<>();
            this.refundData = new LinkedHashMap<>();
            addMouseMotionListener(new MouseMotionAdapter() { @Override public void mouseMoved(MouseEvent e) { mousePoint = e.getPoint(); repaint(); } });
            addMouseListener(new MouseAdapter() { @Override public void mouseExited(MouseEvent e) { mousePoint = null; repaint(); } });
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            createChartTitle(g2, "TÌNH HÌNH HÓA ĐƠN");
            drawLegend(g2, getWidth() - 250, 35, Color.BLUE, "Hóa đơn bán");
            drawLegend(g2, getWidth() - 130, 35, Color.RED, "Hóa đơn hoàn/đổi");

            if (soldData.isEmpty() && refundData.isEmpty()) {
                g2.setColor(TEXT_MUTED); g2.drawString("Không có dữ liệu.", getWidth()/2 - 40, getHeight()/2); return;
            }

            Set<LocalDate> allDates = new TreeSet<>(soldData.keySet()); allDates.addAll(refundData.keySet());
            List<LocalDate> dateList = new ArrayList<>(allDates);
            if (dateList.isEmpty()) return;

            int maxSold = soldData.values().stream().max(Integer::compare).orElse(0);
            int maxRefund = refundData.values().stream().max(Integer::compare).orElse(0);
            int maxVal = Math.max(maxSold, maxRefund);
            if (maxVal == 0) maxVal = 1; maxVal = (int)(maxVal * 1.2);

            Insets ins = getInsets();
            int padding = 20, leftM = 40, bottomM = 40;
            int w = getWidth() - ins.left - ins.right - padding - leftM;
            int h = getHeight() - ins.top - ins.bottom - 60 - bottomM;
            int x0 = ins.left + leftM, y0 = ins.top + 60, yBase = y0 + h;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int y = yBase - (i * h / 5);
                g2.setColor(BORDER_COLOR); g2.drawLine(x0, y, x0 + w, y);
                g2.setColor(TEXT_MUTED); g2.drawString(String.valueOf(maxVal * i / 5), x0 - 25, y + 4);
            }

            int n = dateList.size();
            double stepX = (n > 1) ? (double) w / (n - 1) : 0;
            int[][] pointsSold = new int[n][2];
            int[][] pointsRefund = new int[n][2];

            for (int i = 0; i < n; i++) {
                LocalDate d = dateList.get(i);
                int vSold = soldData.getOrDefault(d, 0);
                int vRefund = refundData.getOrDefault(d, 0);
                int x = (n == 1) ? (x0 + w/2) : (x0 + (int)(i * stepX));
                int ySold = yBase - (int)((double)vSold / maxVal * h);
                int yRefund = yBase - (int)((double)vRefund / maxVal * h);

                pointsSold[i][0] = x; pointsSold[i][1] = ySold;
                pointsRefund[i][0] = x; pointsRefund[i][1] = yRefund;

                if (n <= 15 || i % (n/10) == 0 || i == n-1) {
                    g2.setColor(TEXT_MUTED);
                    String label = d.format(dateFormat);
                    int lblW = g2.getFontMetrics().stringWidth(label);
                    g2.drawString(label, x - lblW/2, yBase + 20);
                }
            }
            if (n > 1) {
                drawPath(g2, pointsSold, Color.BLUE, dateList, soldData, "Bán");
                drawPath(g2, pointsRefund, Color.RED, dateList, refundData, "Hoàn/đổi");
            } else {
                drawSinglePoint(g2, pointsSold[0], Color.BLUE, dateList.get(0), soldData, "Bán");
                drawSinglePoint(g2, pointsRefund[0], Color.RED, dateList.get(0), refundData, "Hoàn/đổi");
            }
        }
        private void drawPath(Graphics2D g2, int[][] points, Color c, List<LocalDate> dates, Map<LocalDate, Integer> data, String name) {
            g2.setColor(c); g2.setStroke(new BasicStroke(2f));
            for (int i = 0; i < points.length - 1; i++) g2.drawLine(points[i][0], points[i][1], points[i+1][0], points[i+1][1]);
            for (int i = 0; i < points.length; i++) drawSinglePoint(g2, points[i], c, dates.get(i), data, name);
        }
        private void drawSinglePoint(Graphics2D g2, int[] point, Color c, LocalDate date, Map<LocalDate, Integer> data, String name) {
            int x = point[0], y = point[1], r = 6;
            g2.setColor(Color.WHITE); g2.fillOval(x - r/2, y - r/2, r, r);
            g2.setColor(c); g2.drawOval(x - r/2, y - r/2, r, r);
            if (mousePoint != null && mousePoint.distance(x, y) < 15) {
                drawTooltip(g2, name + ": " + data.getOrDefault(date, 0) + " (" + date.format(dateFormat) + ")", x, y);
            }
        }
        private void drawTooltip(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 10, h = fm.getHeight() + 4;
            int bx = x + 10; if (bx + w > getWidth()) bx = x - w - 10;
            int by = y - 25; if (by < 0) by = y + 25;
            g2.setColor(new Color(255, 255, 225)); g2.fillRoundRect(bx, by, w, h, 5, 5);
            g2.setColor(Color.BLACK); g2.drawRoundRect(bx, by, w, h, 5, 5);
            g2.drawString(text, bx + 5, by + h - 5);
        }
    }

    // =========================================================================
    // 3. BIỂU ĐỒ CƠ CẤU KHÁCH (PIE CHART)
    // =========================================================================
    static class CustomerSplitChartPanel extends BasePanel {
        private Map<String, Integer> data;
        private final Map<String, Color> colorMap = new HashMap<>();

        public CustomerSplitChartPanel(Map<String, Integer> d) {
            this.data = d;
            colorMap.put("Khách mới", new Color(46, 204, 113));       // Xanh lá
            colorMap.put("Khách quay lại", new Color(52, 152, 219));  // Xanh dương
            colorMap.put("Thân thiết", new Color(155, 89, 182));      // Tím
            colorMap.put("VIP", new Color(241, 196, 15));             // Vàng
            colorMap.put("Ngủ đông", new Color(149, 165, 166));       // Xám
        }

        public void setData(Map<String, Integer> d) {
            this.data = d;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawPieChartWithCallouts(g, "CƠ CẤU KHÁCH", data, colorMap);
        }
    }

    // =========================================================================
    // 4. BIỂU ĐỒ CỘT CHỒNG
    // =========================================================================
    static class StackedBarChartPanel extends BasePanel {
        private Map<LocalDate, Map<String, Integer>> data = new LinkedHashMap<>();
        private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        private int viewType = 0;
        private Point mousePoint = null;

        public StackedBarChartPanel(Map<LocalDate, Map<String, Integer>> data) {
            this.data = (data != null) ? data : new LinkedHashMap<>();
            addMouseMotionListener(new MouseMotionAdapter() { @Override public void mouseMoved(MouseEvent e) { mousePoint = e.getPoint(); repaint(); } });
            addMouseListener(new MouseAdapter() { @Override public void mouseExited(MouseEvent e) { mousePoint = null; repaint(); } });
        }
        public void setViewType(int v) { this.viewType = v; repaint(); }
        public void setData(Map<LocalDate, Map<String, Integer>> d) { this.data = (d != null) ? d : new LinkedHashMap<>(); repaint(); }
        public void setDateFormat(DateTimeFormatter p) { this.fmt = p; repaint(); }
        private String normalizeSeat(String s) {
            s = s.toLowerCase();
            if (s.contains("khoang 4")) return "Giường nằm 4";
            if (s.contains("khoang 6")) return "Giường nằm 6";
            if (s.contains("ngồi"))     return "Ghế ngồi";
            return s;
        }
        private String[] getSeatTypes() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            for (Map<String, Integer> sm : data.values()) for (String k : sm.keySet()) set.add(normalizeSeat(k));
            if (set.isEmpty()) { set.add("Giường nằm 4"); set.add("Giường nằm 6"); set.add("Ghế ngồi"); }
            return set.toArray(new String[0]);
        }
        private Map<String, Integer> emptySeatMap(String[] types) {
            Map<String, Integer> m = new LinkedHashMap<>();
            for (String s : types) m.put(s, 0); return m;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            createChartTitle(g2, "NGẢ VÉ THEO LOẠI GHẾ");
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            if (data == null || data.isEmpty()) { g2.setColor(TEXT_MUTED); g2.drawString("Không có dữ liệu.", getWidth()/2 - 50, getHeight()/2); return; }
            String[] seatTypes = getSeatTypes();
            List<String> xLabels = new ArrayList<>();
            List<Map<String, Integer>> stacked = new ArrayList<>();
            List<LocalDate> dates = new ArrayList<>(data.keySet()); Collections.sort(dates);

            if (viewType == 1) { // 12 Tháng
                int year = dates.isEmpty() ? LocalDate.now().getYear() : dates.get(0).getYear();
                for (int m = 1; m <= 12; m++) {
                    xLabels.add("T" + m); Map<String, Integer> total = emptySeatMap(seatTypes);
                    for (LocalDate d : dates) if (d.getYear() == year && d.getMonthValue() == m) {
                        Map<String, Integer> sm = data.get(d); for (String s : sm.keySet()) total.merge(normalizeSeat(s), sm.get(s), Integer::sum);
                    }
                    stacked.add(total);
                }
            } else if (viewType == 2) { // Năm
                Map<Integer, Map<String, Integer>> yearMap = new TreeMap<>();
                for (LocalDate d : dates) {
                    int y = d.getYear(); yearMap.putIfAbsent(y, emptySeatMap(seatTypes));
                    Map<String, Integer> sm = data.get(d); for (String s : sm.keySet()) yearMap.get(y).merge(normalizeSeat(s), sm.get(s), Integer::sum);
                }
                for (Integer y : yearMap.keySet()) { xLabels.add(String.valueOf(y)); stacked.add(yearMap.get(y)); }
            } else { // Ngày/Tuần
                for (LocalDate d : dates) {
                    xLabels.add(d.format(fmt)); Map<String, Integer> daySum = emptySeatMap(seatTypes);
                    Map<String, Integer> sm = data.get(d); for (String s : sm.keySet()) daySum.merge(normalizeSeat(s), sm.get(s), Integer::sum);
                    stacked.add(daySum);
                }
            }

            int n = xLabels.size(); if (n == 0) return;
            double max = 0; for (Map<String, Integer> sm : stacked) max = Math.max(max, sm.values().stream().mapToInt(Integer::intValue).sum());
            if (max == 0) max = 1; max *= 1.1;

            Insets ins = getInsets();
            int padding = 20, topSpace = 80, bottomSpace = 40;
            String maxStr = formatter.format(max);
            int yLabelW = g2.getFontMetrics().stringWidth(maxStr) + 15;
            int x0 = ins.left + padding + yLabelW, h = getHeight() - ins.top - ins.bottom - topSpace - bottomSpace;
            int w = getWidth() - x0 - padding, y0 = ins.top + topSpace, yBase = y0 + h;

            for (int i = 0; i <= 5; i++) {
                int y = yBase - (i * h / 5);
                g2.setColor(BORDER_COLOR); g2.drawLine(x0, y, x0 + w, y);
                g2.setColor(TEXT_MUTED); String label = formatter.format(max * i / 5);
                g2.drawString(label, x0 - g2.getFontMetrics().stringWidth(label) - 5, y + 4);
            }

            double slotW = (double) w / n; int barW = (int)(slotW * 0.6);
            String hoverText = null; int hoverX = 0, hoverY = 0;

            for (int i = 0; i < n; i++) {
                double center = x0 + (i * slotW) + slotW/2;
                int x = (int)(center - barW/2);
                int yStack = yBase;
                Map<String, Integer> sm = stacked.get(i);
                int colorIndex = 0;

                for (String seat : seatTypes) {
                    int v = sm.get(seat);
                    if (v > 0) { // CHỈ VẼ KHI CÓ DỮ LIỆU
                        int bh = (int)((double)v / max * h);
                        if (bh < 1) bh = 1;
                        int y = yStack - bh;
                        g2.setColor(CHART_COLORS[colorIndex % CHART_COLORS.length]);
                        g2.fillRect(x, y, barW, bh);
                        Rectangle rect = new Rectangle(x, y, barW, bh);
                        if (mousePoint != null && rect.contains(mousePoint)) {
                            hoverText = seat + ": " + formatter.format(v) + " — " + xLabels.get(i);
                            hoverX = mousePoint.x; hoverY = mousePoint.y;
                        }
                        yStack -= bh;
                    }
                    colorIndex++;
                }
                g2.setColor(TEXT_MUTED); String lbl = xLabels.get(i);
                g2.drawString(lbl, (int)(center - g2.getFontMetrics().stringWidth(lbl)/2), yBase + 20);
            }

            int lx = x0, ly = ins.top + 50;
            for (int i = 0; i < seatTypes.length; i++) {
                drawLegend(g2, lx, ly, CHART_COLORS[i % CHART_COLORS.length], seatTypes[i]); lx += 140;
            }
            if (hoverText != null) drawTooltip(g2, hoverText, hoverX, hoverY);
        }
        private void drawTooltip(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text) + 12, h = fm.getHeight() + 6;
            int bx = x + 10, by = y - 25;
            if (bx + w > getWidth()) bx = x - w - 5; if (by < 0) by = y + 15;
            g2.setColor(new Color(255,255,225)); g2.fillRoundRect(bx, by, w, h, 5, 5);
            g2.setColor(Color.BLACK); g2.drawRoundRect(bx, by, w, h, 5, 5);
            g2.drawString(text, bx + 6, by + h - 6);
        }
    }

    // =========================================================================
    // 6. FILTER & KPI & GRID
    // =========================================================================
    private void updateKpiCards(LocalDate s, LocalDate e) {
        double r = dashboardDAO.getKpiTotalRevenue(s, e);
        kpiRevenue.setData(formatter.format(r) + " VND", "+0%");
        int t = dashboardDAO.getKpiTicketsSold(s, e);
        kpiTicketsSold.setData(formatter.format(t), "+0%");
        int seat = dashboardDAO.getTotalAvailableSeats(s, e);
        double rate = (seat > 0) ? ((double) t / seat * 100) : 0;
        kpiOccupancy.setData(percentFormatter.format(rate), formatter.format(t) + "/" + formatter.format(seat));
        int ref = dashboardDAO.getTotalRefundsAndExchanges(s, e);
        double refRate = (t > 0) ? ((double) ref / t * 100) : 0;
        kpiRefundRate.setData(percentFormatter.format(refRate), formatter.format(ref) + "/" + formatter.format(t));
    }

    private class FilterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton source = (JButton) e.getSource();
            updateFilterButtonStyle(source);

            LocalDate now = LocalDate.now();
            LocalDate startDate = null, endDate = null;
            int viewType = 0; // 0: Ngày, 1: Năm (T1-T12), 2: Tất cả (Năm)

            DateTimeFormatter chartFormatter = DateTimeFormatter.ofPattern("dd/MM");

            if (source == btnToday) {
                startDate = now;
                endDate = now;
                chartFormatter = DateTimeFormatter.ofPattern("dd/MM");
            }
            else if (source == btnWeek) {
                startDate = now.with(DayOfWeek.MONDAY);
                endDate = now.with(DayOfWeek.SUNDAY);
                chartFormatter = DateTimeFormatter.ofPattern("EEE (dd/MM)", new Locale("vi", "VN"));
            }
            else if (source == btnMonth) {
                startDate = now.withDayOfMonth(1);
                endDate = now.with(TemporalAdjusters.lastDayOfMonth());
                chartFormatter = DateTimeFormatter.ofPattern("dd/MM");
            }
            else if (source == btnYear) {
                startDate = now.withDayOfYear(1);
                endDate = now.with(TemporalAdjusters.lastDayOfYear());
                chartFormatter = DateTimeFormatter.ofPattern("'T'M");
                viewType = 1;
            }
            else if (source == btnAll) {
                startDate = null;
                endDate = null;
                chartFormatter = DateTimeFormatter.ofPattern("yyyy");
                viewType = 2;
            }

            loadDashboardData(startDate, endDate, viewType, chartFormatter);
        }
    }

    private JPanel createMainGrid() {
        JPanel g = new JPanel(new GridBagLayout()); g.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6); c.fill = GridBagConstraints.BOTH;
        c.gridy = 0; c.gridx = 0; c.weightx = 1; c.weighty = 0;

        kpiRevenue = new KpiCard("TỔNG DOANH THU", "...", "+0%", CHART_COLORS[1]); g.add(kpiRevenue, c);
        c.gridx = 1; kpiTicketsSold = new KpiCard("SỐ VÉ ĐÃ BÁN", "...", "+0%", CHART_COLORS[0]); g.add(kpiTicketsSold, c);
        c.gridx = 2; kpiOccupancy = new KpiCard("TỶ LỆ LẤP ĐẦY", "...", "0/0", CHART_COLORS[2]); g.add(kpiOccupancy, c);
        c.gridx = 3; kpiRefundRate = new KpiCard("TỶ LỆ HOÀN ĐỔI", "...", "0/0", CHART_COLORS[4]); g.add(kpiRefundRate, c);

        c.gridy = 1; c.weighty = 0.5; c.gridx = 0; c.gridwidth = 2;
        revenueBarChart = new RevenueBarChartPanel(new LinkedHashMap<>()); g.add(revenueBarChart, c);

        c.gridx = 2; c.gridwidth = 2;
        invoiceAnalysisChart = new InvoiceAnalysisChartPanel();
        g.add(invoiceAnalysisChart, c);

        c.gridy = 2; c.gridx = 0; c.gridwidth = 2;
        stackedBarChart = new StackedBarChartPanel(new LinkedHashMap<>()); g.add(stackedBarChart, c);

        // Đã xóa PromotionRateChartPanel, Customer Chart chiếm toàn bộ bên phải
        c.gridx = 2; c.gridwidth = 2;
        customerSplitChart = new CustomerSplitChartPanel(new LinkedHashMap<>());
        g.add(customerSplitChart, c);

        return g;
    }

    private JPanel createHeaderBar() {
        JPanel h = new JPanel(new BorderLayout()); h.setOpaque(false); h.setBorder(new EmptyBorder(0, 0, 12, 0));
        JLabel t = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ TÀU HỎA"); t.setFont(new Font("Segoe UI", Font.BOLD, 24)); t.setForeground(TEXT_COLOR); h.add(t, BorderLayout.WEST);
        JPanel f = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); f.setOpaque(false);
        btnToday = createFilterButton("Hôm nay"); btnWeek = createFilterButton("Tuần này"); btnMonth = createFilterButton("Tháng này"); btnYear = createFilterButton("Năm này"); btnAll = createFilterButton("Tất cả");
        FilterActionListener l = new FilterActionListener();
        btnToday.addActionListener(l); btnWeek.addActionListener(l); btnMonth.addActionListener(l); btnYear.addActionListener(l); btnAll.addActionListener(l);
        JLabel lblFilter = new JLabel("Bộ lọc: "); lblFilter.setForeground(TEXT_MUTED); lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 14));
        f.add(lblFilter); f.add(btnToday); f.add(btnWeek); f.add(btnMonth); f.add(btnYear); f.add(btnAll);
        h.add(f, BorderLayout.EAST);
        return h;
    }

    private JButton createFilterButton(String text) {
        JButton b = new JButton(text); b.setBackground(PANEL_COLOR); b.setForeground(TEXT_COLOR); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1), new EmptyBorder(5, 15, 5, 15)));
        return b;
    }

    private void updateFilterButtonStyle(JButton active) {
        JButton[] arr = {btnToday, btnWeek, btnMonth, btnYear, btnAll};
        for (JButton b : arr) {
            if (b == null) continue;
            if (b == active) { b.setBackground(ACTIVE_BTN_COLOR); b.setForeground(Color.WHITE); }
            else { b.setBackground(PANEL_COLOR); b.setForeground(TEXT_COLOR); }
        }
    }

    static class KpiCard extends BasePanel {
        String title, value, sub; Color barColor;
        public KpiCard(String t, String v, String s, Color c) { this.title = t; this.value = v; this.sub = s; this.barColor = c; setPreferredSize(new Dimension(200, 110)); }
        public void setData(String v, String s) { this.value = v; this.sub = s; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setColor(barColor); g2.fillRect(0, 0, 5, getHeight());
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14)); g2.setColor(TEXT_MUTED); g2.drawString(title, 25, 35);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 28)); g2.setColor(TEXT_COLOR); g2.drawString(value, 25, 70);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12)); g2.setColor(barColor); g2.drawString(sub, 25, 95);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Dashboard Light"); f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setContentPane(new Dashboard()); f.setExtendedState(JFrame.MAXIMIZED_BOTH); f.setVisible(true);
        });
    }
}