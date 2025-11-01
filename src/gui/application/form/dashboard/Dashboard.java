package gui.application.form.dashboard;

import com.formdev.flatlaf.FlatDarkLaf; // Cần thư viện FlatLaf

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Giao diện Dashboard quản lý bán vé tàu hỏa.
 * Yêu cầu thư viện FlatLaf (ví dụ: flatlaf-3.4.jar) để chạy đúng giao diện.
 */
public class Dashboard extends JPanel {

    // Màu sắc chủ đạo (dark mode)
    private static final Color BG_COLOR = new Color(24, 26, 31); // Nền chính
    private static final Color PANEL_COLOR = new Color(34, 38, 46); // Nền của các ô panel
    private static final Color TEXT_COLOR = new Color(230, 230, 230); // Chữ chính
    private static final Color TEXT_MUTED = new Color(148, 163, 184); // Chữ phụ (mờ)
    private static final Color BORDER_COLOR = new Color(55, 63, 78); // Viền

    // Bảng màu cho biểu đồ (tương thích dark mode)
    private static final Color[] CHART_COLORS = {
            new Color(59, 130, 246), // Blue
            new Color(16, 185, 129), // Green
            new Color(249, 115, 22), // Orange
            new Color(239, 68, 68),  // Red
            new Color(168, 85, 247), // Purple
            new Color(217, 70, 239)  // Pink
    };

    public Dashboard() {
        // Thiết lập giao diện cơ bản
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- A. Header Bar (Thanh tiêu đề) ---
        add(createHeaderBar(), BorderLayout.NORTH);

        // --- B. Main Grid (Lưới nội dung chính) ---
        add(createMainGrid(), BorderLayout.CENTER);
    }

    /**
     * A. Tạo Header Bar (Tiêu đề và bộ lọc)
     */
    private JPanel createHeaderBar() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Trong suốt để lấy nền BG_COLOR
        headerPanel.setBorder(new EmptyBorder(0, 0, 12, 0)); // Khoảng cách với lưới

        // Tiêu đề
        JLabel title = new JLabel("HỆ THỐNG QUẢN LÝ BÁN VÉ TÀU HỎA");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        // Thêm icon (nếu có)
        // title.setIcon(new ImageIcon(getClass().getResource("/icons/train_icon.png")));
        headerPanel.add(title, BorderLayout.WEST);

        // Bộ lọc thời gian
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setOpaque(false);

        JLabel filterLabel = new JLabel("Bộ lọc:");
        filterLabel.setForeground(TEXT_MUTED);
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnToday = new JButton("Hôm nay");
        JButton btnWeek = new JButton("Tuần này");
        JButton btnMonth = new JButton("Tháng này");
        // Style cho nút (FlatLaf sẽ tự động style, nhưng có thể tùy chỉnh thêm)
        // btnToday.putClientProperty("JButton.buttonType", "roundRect");

        filterPanel.add(filterLabel);
        filterPanel.add(btnToday);
        filterPanel.add(btnWeek);
        filterPanel.add(btnMonth);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    /**
     * B. Tạo Lưới Nội dung Chính (KPIs và Biểu đồ)
     */
    private JPanel createMainGrid() {
        JPanel mainGrid = new JPanel(new GridBagLayout());
        mainGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.BOTH;

        // --- HÀNG 1: 4 Thẻ KPI ---
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2; // 20% chiều cao

        // (KPI 1) TỔNG DOANH THU
        gbc.gridx = 0;
        mainGrid.add(new KpiCard("TỔNG DOANH THU", "1,250,000,000", "+15% vs. tháng trước", CHART_COLORS[1]), gbc);

        // (KPI 2) SỐ VÉ ĐÃ BÁN
        gbc.gridx = 1;
        mainGrid.add(new KpiCard("SỐ VÉ ĐÃ BÁN", "9,870", "+19% vs. tháng trước", CHART_COLORS[0]), gbc);

        // (KPI 3) TỶ LỆ LẤP ĐẦY TB
        gbc.gridx = 2;
        mainGrid.add(new KpiCard("TỶ LỆ LẤP ĐẦY TB", "82%", "Mục tiêu: 80%", CHART_COLORS[2]), gbc);

        // (KPI 4) TUYẾN DOANH THU CAO NHẤT
        gbc.gridx = 3;
        mainGrid.add(new KpiCard("TUYẾN DOANH THU CAO NHẤT", "SG - HN", "Chiếm 35% tổng DT", CHART_COLORS[4]), gbc);

        // --- HÀNG 2: Biểu đồ (Line Chart, Top 5, Phân loại KH) ---
        gbc.gridy = 1;
        gbc.weighty = 0.4; // 40% chiều cao

        // (B.1) DOANH THU THEO THỜI GIAN (2 ô)
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Chiếm 2 cột
        mainGrid.add(new RevenueOverTimeChartPanel(), gbc);

        // (B.3) TOP 5 CHUYẾN DOANH THU CAO NHẤT (1 ô)
        gbc.gridx = 2;
        gbc.gridwidth = 1; // Chiếm 1 cột
        mainGrid.add(new Top5RevenueChartPanel(), gbc);

        // (B.4) PHÂN LOẠI KHÁCH HÀNG (1 ô) - THAY THẾ MỚI
        gbc.gridx = 3;
        gbc.gridwidth = 1; // Chiếm 1 cột
        mainGrid.add(new CustomerTypeChartPanel(), gbc);

        // --- HÀNG 3: Biểu đồ (Ngả vé, Giữ chỗ, Khuyến mãi) ---
        gbc.gridy = 2;
        gbc.weighty = 0.4; // 40% chiều cao

        // (B.5) NGẢ VÉ THEO LOẠI GHẾ (1 ô)
        gbc.gridx = 0;
        gbc.gridwidth = 1; // Chiếm 1 cột
        mainGrid.add(new StackedBarChartPanel(), gbc);

        // (B.6) PHÂN TÍCH GIỮ CHỖ (1 ô) - THAY THẾ MỚI
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        mainGrid.add(new ReservationStatusChartPanel(), gbc);

        // (B.7) TOP 5 KHUYẾN MÃI HIỆU QUẢ (2 ô) - THAY THẾ MỚI
        gbc.gridx = 2;
        gbc.gridwidth = 2; // Chiếm 2 cột
        mainGrid.add(new TopPromotionChartPanel(), gbc);

        return mainGrid;
    }

    // =========================================================================
    // LỚP NỘI BỘ: BasePanel (Panel cơ sở cho các ô)
    // =========================================================================
    static class BasePanel extends JPanel {
        protected final DecimalFormat formatter = new DecimalFormat("#,##0");

        public BasePanel() {
            setBackground(PANEL_COLOR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1), // Viền ngoài
                    new EmptyBorder(15, 20, 15, 20) // Padding bên trong
            ));
            setLayout(new BorderLayout());
        }

        // Hàm tiện ích vẽ Tiêu đề cho các ô biểu đồ
        protected void createChartTitle(Graphics2D g, String title) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.setColor(TEXT_COLOR);
            g.drawString(title, 20, 30); // Vị trí padding top + 15
        }

        // Hàm tiện ích vẽ Chú thích (Legend)
        protected void drawLegend(Graphics2D g, int x, int y, Color color, String text) {
            g.setColor(color);
            g.fillRect(x, y - 10, 12, 12);
            g.setColor(TEXT_MUTED);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString(text, x + 20, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Tạo hiệu ứng bo góc (hơi khó trong Swing)
            // Tạm thời dùng nền panel_color
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // HÀNG 1: Thẻ KPI
    // =========================================================================
    static class KpiCard extends BasePanel {
        private String title, value, subtext;
        private Color accentColor;

        public KpiCard(String title, String value, String subtext, Color accentColor) {
            this.title = title;
            this.value = value;
            this.subtext = subtext;
            this.accentColor = accentColor;
            // Đặt chiều cao tối thiểu (GridBagLayout sẽ co giãn)
            setPreferredSize(new Dimension(200, 120));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Vẽ một đường viền màu bên trái
            g2d.setColor(accentColor);
            g2d.fillRect(0, 0, 5, getHeight());

            // Title (ví dụ: TỔNG DOANH THU)
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g2d.setColor(TEXT_MUTED);
            g2d.drawString(title, 25, 35);

            // Value (ví dụ: 1,250,000,000)
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 28));
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(value, 25, 70);

            // Subtext (ví dụ: +15% vs. tháng trước)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.setColor(accentColor); // Dùng màu nhấn cho subtext
            g2d.drawString(subtext, 25, 95);

            // Icon (Placeholder)
            g2d.setColor(accentColor);
            g2d.fillOval(getWidth() - 60, 30, 30, 30);
            g2d.setColor(PANEL_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2d.drawString("i", getWidth() - 50, 52); // Icon chữ "i"
        }
    }

    // =========================================================================
    // HÀNG 2: (B.1) DOANH THU THEO THỜI GIAN (Line Chart)
    // =========================================================================
    static class RevenueOverTimeChartPanel extends BasePanel {
        // Dữ liệu mẫu (12 tháng)
        private final double[] data = {
                150.5, 180.2, 220.0, 200.8, 250.3, 270.1,
                300.7, 280.5, 310.9, 340.0, 370.4, 410.6
        };
        private final String[] labels = {
                "T1", "T2", "T3", "T4", "T5", "T6",
                "T7", "T8", "T9", "T10", "T11", "T12"
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            createChartTitle(g2d, "DOANH THU THEO THỜI GIAN (Triệu VND)");

            int padding = 20;
            int labelPadding = 25;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 30; // 30 cho title
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 30; // 30 cho title

            double maxVal = Arrays.stream(data).max().orElse(1) * 1.1; // 110% max

            // Vẽ các đường lưới Y và nhãn Y
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2d.setColor(TEXT_MUTED);
            int numYGrid = 5;
            for (int i = 0; i <= numYGrid; i++) {
                int y = y0 + h - (i * h / numYGrid);
                g2d.setColor(BORDER_COLOR);
                g2d.drawLine(x0, y, x0 + w, y); // Đường lưới
                g2d.setColor(TEXT_MUTED);
                String yLabel = formatter.format(maxVal * i / numYGrid);
                g2d.drawString(yLabel, insets.left + padding - 5, y + 5);
            }

            // Vẽ đường dữ liệu
            g2d.setColor(CHART_COLORS[0]);
            g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Polygon p = new Polygon();
            for (int i = 0; i < data.length; i++) {
                int x = x0 + (i * w / (data.length - 1));
                int y = y0 + h - (int) (data[i] / maxVal * h);
                p.addPoint(x, y);
            }
            g2d.drawPolyline(p.xpoints, p.ypoints, p.npoints);

            // Vẽ các điểm chấm
            for (int i = 0; i < p.npoints; i++) {
                g2d.setColor(CHART_COLORS[0]);
                g2d.fillOval(p.xpoints[i] - 4, p.ypoints[i] - 4, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.xpoints[i] - 2, p.ypoints[i] - 2, 4, 4);
            }

            // Vẽ nhãn X
            g2d.setColor(TEXT_MUTED);
            for (int i = 0; i < labels.length; i++) {
                int x = x0 + (i * w / (labels.length - 1));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(labels[i], x - fm.stringWidth(labels[i]) / 2, y0 + h + labelPadding - 5);
            }
        }
    }

    // =========================================================================
    // HÀNG 2: (B.3) TOP 5 CHUYẾN DOANH THU CAO NHẤT (Horizontal Bar)
    // =========================================================================
    static class Top5RevenueChartPanel extends BasePanel {
        // Dữ liệu mẫu
        private final Map<String, Double> revenueData = new HashMap<>();
        private final Map<String, Integer> occupancyData = new HashMap<>();

        public Top5RevenueChartPanel() {
            revenueData.put("SG-HN (SE1)", 120.5);
            revenueData.put("SG-DN (SE3)", 95.2);
            revenueData.put("HN-LP (LP5)", 88.0);
            revenueData.put("SG-NT (SNT2)", 75.3);
            revenueData.put("HN-LC (SP3)", 60.1);

            occupancyData.put("SG-HN (SE1)", 95);
            occupancyData.put("SG-DN (SE3)", 88);
            occupancyData.put("HN-LP (LP5)", 92);
            occupancyData.put("SG-NT (SNT2)", 78);
            occupancyData.put("HN-LC (SP3)", 85);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "TOP 5 CHUYẾN DOANH THU CAO NHẤT");

            int topMargin = 70;
            int barHeight = 30;
            int barGap = 20;
            int availableWidth = getWidth() - getInsets().left - getInsets().right - 40; // 40 padding
            double maxRevenue = revenueData.values().stream().max(Double::compare).orElse(1.0);

            int i = 0;
            for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
                String name = entry.getKey();
                double revenue = entry.getValue();
                int occupancy = occupancyData.get(name);

                int y = topMargin + i * (barHeight + barGap);
                int barWidth = (int) (revenue / maxRevenue * (availableWidth * 0.6)); // Thanh chỉ chiếm 60%

                // Vẽ tên chuyến + Tỷ lệ lấp đầy
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.drawString(name, getInsets().left + 20, y + 12);
                g2d.setColor(TEXT_MUTED);
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                g2d.drawString("Lấp đầy: " + occupancy + "%", getInsets().left + 20, y + 26);

                // Vẽ thanh (nền)
                g2d.setColor(BORDER_COLOR);
                g2d.fillRoundRect(getInsets().left + 20, y + 35, availableWidth, 8, 8, 8);

                // Vẽ thanh (giá trị)
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(getInsets().left + 20, y + 35, barWidth, 8, 8, 8);

                // Vẽ giá trị doanh thu
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2d.drawString(formatter.format(revenue) + " Tr", getInsets().left + 30 + (int)(availableWidth * 0.6), y + 18);


                i++;
            }
        }
    }

// HÀNG 2: (B.4) CƠ CẤU KHÁCH HÀNG (Biểu đồ cột hiển thị số lượng và tỉ lệ)
// =========================================================================
    static class CustomerTypeChartPanel extends BasePanel {
        // Dữ liệu mẫu (số lượng khách hàng)
        private final Map<String, Integer> data = new LinkedHashMap<>();

        public CustomerTypeChartPanel() {
            data.put("Khách hàng mới", 320);
            data.put("Khách hàng cũ", 680);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "CƠ CẤU KHÁCH HÀNG");

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            Insets insets = getInsets();
            int padding = 40;
            int labelPadding = 40;
            int w = getWidth() - insets.left - insets.right - padding * 2;
            int h = getHeight() - insets.top - insets.bottom - padding * 2 - 40; // 40 cho tiêu đề

            int x0 = insets.left + padding + 40;
            int y0 = insets.top + padding + 40;

            int numBars = data.size();
            int barWidth = w / (numBars * 2);
            int gap = barWidth; // khoảng cách giữa các cột
            int maxVal = data.values().stream().max(Integer::compareTo).orElse(1);

            // Vẽ trục Y
            g2d.setColor(BORDER_COLOR);
            g2d.drawLine(x0, y0, x0, y0 + h);
            g2d.drawLine(x0, y0 + h, x0 + w, y0 + h);

            // Vẽ cột
            int i = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String name = entry.getKey();
                int value = entry.getValue();
                double percent = value * 100.0 / total;

                int barHeight = (int) (value / (double) maxVal * (h * 0.9));
                int x = x0 + gap / 2 + i * (barWidth + gap);
                int y = y0 + h - barHeight;

                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(x, y, barWidth, barHeight, 10, 10);

                // Vẽ nhãn dưới cột
                g2d.setColor(TEXT_MUTED);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(name, x + (barWidth - fm.stringWidth(name)) / 2, y0 + h + fm.getAscent() + 5);

                // Hiển thị giá trị & phần trăm trên cột
                g2d.setColor(TEXT_COLOR);
                String label = formatter.format(value) + " (" + String.format("%.1f%%", percent) + ")";
                g2d.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, y - 8);

                i++;
            }

            // Hiển thị tổng
            g2d.setColor(TEXT_MUTED);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.drawString("Tổng: " + formatter.format(total) + " KH", x0, y0 + h + labelPadding);
        }
    }



    // =========================================================================
    // HÀNG 3: (B.5) NGẢ VÉ THEO LOẠI GHẾ (Stacked Bar Chart)
    // =========================================================================
    static class StackedBarChartPanel extends BasePanel {
        // Dữ liệu mẫu (từ hình ảnh)
        private final String[] labels = {"Current", "25-10", "26-10", "27-10", "28-10"};
        private final String[] seriesNames = {"Ghế mềm", "Giường nằm T4", "Giường nằm 6/4"};
        private final int[][] data = {
                // Tương ứng với labels
                {55, 38, 45, 30, 15}, // Ghế mềm (Blue)
                {40, 25, 20, 18, 10}, // Giường nằm T4 (Orange)
                {25, 15, 10, 8, 5}    // Giường nằm 6/4 (Green)
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "NGẢ VÉ THEO LOẠI GHẾ");

            int padding = 20;
            int labelPadding = 25;
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 60; // 60 cho title + legend
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 60; // 60 cho title + legend

            // Tính max Y
            int maxVal = 0;
            for (int j = 0; j < labels.length; j++) {
                int sum = 0;
                for (int i = 0; i < seriesNames.length; i++) {
                    sum += data[i][j];
                }
                maxVal = Math.max(maxVal, sum);
            }
            maxVal = (int) (maxVal * 1.1); // 110%

            // Vẽ lưới Y
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            int numYGrid = 5;
            for (int i = 0; i <= numYGrid; i++) {
                int y = y0 + h - (i * h / numYGrid);
                g2d.setColor(BORDER_COLOR);
                g2d.drawLine(x0, y, x0 + w, y);
                g2d.setColor(TEXT_MUTED);
                String yLabel = formatter.format(maxVal * i / numYGrid);
                g2d.drawString(yLabel, insets.left + padding - 5, y + 5);
            }

            // Vẽ các cột chồng
            int barWidth = (w / labels.length) - (labels.length * 10);
            int barGap = (w - (barWidth * labels.length)) / (labels.length + 1);
            for (int j = 0; j < labels.length; j++) {
                int x = x0 + barGap + j * (barWidth + barGap);
                int yBottom = y0 + h;
                for (int i = 0; i < seriesNames.length; i++) {
                    int barHeight = (int) (data[i][j] / (double) maxVal * h);
                    g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                    g2d.fillRect(x, yBottom - barHeight, barWidth, barHeight);
                    yBottom -= barHeight;
                }
            }

            // Vẽ nhãn X
            g2d.setColor(TEXT_MUTED);
            for (int i = 0; i < labels.length; i++) {
                int x = x0 + barGap + i * (barWidth + barGap) + barWidth / 2;
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(labels[i], x - fm.stringWidth(labels[i]) / 2, y0 + h + labelPadding - 5);
            }

            // Vẽ Chú thích (Legend)
            int legendX = x0;
            int legendY = insets.top + 50; // Dưới title
            for (int i = 0; i < seriesNames.length; i++) {
                drawLegend(g2d, legendX, legendY, CHART_COLORS[i], seriesNames[i]);
                legendX += g2d.getFontMetrics().stringWidth(seriesNames[i]) + 40;
            }
        }
    }

    // =========================================================================
    // HÀNG 3: (B.6) PHÂN TÍCH GIỮ CHỖ (Donut Chart) - THAY THẾ MỚI
    // =========================================================================
    static class ReservationStatusChartPanel extends BasePanel {
        // Dữ liệu mẫu (từ PhieuGiuCho.trangThai)
        private final Map<String, Integer> data = new HashMap<>();

        public ReservationStatusChartPanel() {
            data.put("Xác nhận", 650);
            data.put("Hết hạn", 210);
            data.put("Đang giữ", 140);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "PHÂN TÍCH GIỮ CHỖ");

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            int diameter = Math.min(getWidth(), getHeight()) / 2;
            int x = getWidth() / 2 - diameter / 2;
            int y = getHeight() / 2 - diameter / 2 + 10;
            int holeSize = (int) (diameter * 0.6);
            int innerX = x + (diameter - holeSize) / 2;
            int innerY = y + (diameter - holeSize) / 2;

            double startAngle = 90;
            int i = 0;
            int legendY = y + diameter + 20;

            // Sắp xếp dữ liệu để vẽ
            java.util.List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(data.entrySet());
            entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            for (Map.Entry<String, Integer> entry : entries) {
                double extent = (entry.getValue() / (double) total) * 360;
                g2d.setColor(CHART_COLORS[i]);
                g2d.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, -extent, Arc2D.PIE));
                startAngle -= extent;

                // Vẽ chú thích
                drawLegend(g2d, getInsets().left + 20, legendY + (i * 20), CHART_COLORS[i],
                        entry.getKey() + " (" + (int)(extent/3.6) + "%)");
                i++;
            }

            // Vẽ lỗ donut
            g2d.setColor(PANEL_COLOR);
            g2d.fillOval(innerX, innerY, holeSize, holeSize);

            // Hiển thị tổng
            g2d.setColor(TEXT_COLOR);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
            String totalStr = formatter.format(total);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(totalStr, x + (diameter - fm.stringWidth(totalStr)) / 2, y + (diameter / 2) + fm.getAscent() / 2 - 5);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2d.setColor(TEXT_MUTED);
            String phieuStr = "Phiếu";
            fm = g2d.getFontMetrics();
            g2d.drawString(phieuStr, x + (diameter - fm.stringWidth(phieuStr)) / 2, y + (diameter / 2) + fm.getAscent() + 10);
        }
    }


    // =========================================================================
    // HÀNG 3: (B.7) TOP 5 KHUYẾN MÃI HIỆU QUẢ (Bar Chart) - THAY THẾ MỚI
    // =========================================================================
    static class TopPromotionChartPanel extends BasePanel {
        // Dữ liệu mẫu (từ SuDungKhuyenMai)
        private final Map<String, Integer> data = new HashMap<>();

        public TopPromotionChartPanel() {
            data.put("HE2025", 1250);
            data.put("CHAOMUNG", 980);
            data.put("VIP10", 720);
            data.put("TET2025", 450);
            data.put("SINHVIEN", 310);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            createChartTitle(g2d, "TOP 5 KHUYẾN MÃI HIỆU QUẢ (Số lần sử dụng)");

            int padding = 20;
            int labelPadding = 60; // Tăng padding cho tên KM
            Insets insets = getInsets();
            int w = getWidth() - insets.left - insets.right - 2 * padding - labelPadding;
            int h = getHeight() - insets.top - insets.bottom - 2 * padding - labelPadding - 30; // 30 cho title
            int x0 = insets.left + padding + labelPadding;
            int y0 = insets.top + padding + 30;

            int numBars = data.size();
            double maxVal = data.values().stream().mapToInt(Integer::intValue).max().orElse(1) * 1.1;

            int barHeight = h / (numBars * 2 - 1); // Chiều cao thanh
            int barGap = barHeight; // Khoảng cách

            int i = 0;
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int y = y0 + i * (barHeight + barGap);
                int barWidth = (int) (entry.getValue() / maxVal * w);

                // Vẽ tên (Nhãn Y)
                g2d.setColor(TEXT_MUTED);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(entry.getKey(), x0 - fm.stringWidth(entry.getKey()) - 10, y + barHeight / 2 + fm.getAscent() / 2);

                // Vẽ thanh
                g2d.setColor(CHART_COLORS[i % CHART_COLORS.length]);
                g2d.fillRoundRect(x0, y, barWidth, barHeight, 8, 8);

                // Vẽ giá trị
                g2d.setColor(TEXT_COLOR);
                String valueStr = formatter.format(entry.getValue());
                g2d.drawString(valueStr, x0 + barWidth + 10, y + barHeight / 2 + fm.getAscent() / 2);

                i++;
            }

            // Vẽ trục X (đường 0)
            g2d.setColor(BORDER_COLOR);
            g2d.drawLine(x0, y0, x0, y0 + h);
        }
    }


    // =========================================================================
    // MAIN METHOD (Để chạy thử)
    // =========================================================================
    public static void main(String[] args) {
        // Cài đặt Look and Feel FlatLaf Dark
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Không thể cài đặt FlatLaf Look and Feel.");
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard Quản lý Bán vé Tàu hỏa (Swing)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Bọc Dashboard trong JScrollPane để đảm bảo cuộn nếu cửa sổ quá nhỏ
            JScrollPane scrollPane = new JScrollPane(new Dashboard());
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

            frame.setContentPane(scrollPane);
            frame.setSize(1400, 900); // Kích thước lớn để hiển thị đẹp
            frame.setLocationRelativeTo(null); // Giữa màn hình
            frame.setVisible(true);
        });
    }
}

