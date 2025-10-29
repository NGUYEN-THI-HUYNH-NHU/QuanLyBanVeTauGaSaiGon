package gui.application.form.dashboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Dashboard - Phiên bản được thiết kế lại theo hình ảnh mới.
 * - Cập nhật bố cục GridBagLayout để giống hình ảnh hơn.
 * - Điều chỉnh các KPI cards.
 * - Tinh chỉnh các biểu đồ để trông hiện đại và khớp với dữ liệu/nhãn trong hình.
 */
public class Dashboard extends JPanel {

    public Dashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Nền màu xám nhạt cho toàn bộ dashboard

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 247, 250)); // Nền cho khu vực nội dung
        contentPanel.add(buildTopBar(), BorderLayout.NORTH);
        contentPanel.add(buildContent(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ========== Top blue bar (Đã cập nhật tiêu đề, màu sắc) ==========
    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(28, 66, 108)); // Giữ màu xanh đậm
        bar.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("   QUẢN LÝ BÁN VÉ TÀU HỎA");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        bar.add(title, BorderLayout.WEST);

        // Thêm các nút điều khiển giả lập ở góc phải trên
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setOpaque(false); // Trong suốt để thấy màu nền bar

        // Các biểu tượng hoặc nút
        JLabel icon1 = new JLabel("🔍"); // Search
        JLabel icon2 = new JLabel("🔔"); // Notification
        JLabel icon3 = new JLabel("⚙️"); // Settings
        JLabel icon4 = new JLabel("👤"); // User

        icon1.setForeground(Color.WHITE);
        icon2.setForeground(Color.WHITE);
        icon3.setForeground(Color.WHITE);
        icon4.setForeground(Color.WHITE);

        controlPanel.add(icon1);
        controlPanel.add(icon2);
        controlPanel.add(icon3);
        controlPanel.add(icon4);

        bar.add(controlPanel, BorderLayout.EAST);

        return bar;
    }

    // ========== Central grid (Đã cập nhật bố cục và dữ liệu) ==========
    private JComponent buildContent() {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(new Color(245, 247, 250)); // Nền màu xám nhạt
        grid.setBorder(new EmptyBorder(12, 12, 12, 12)); // Khoảng cách xung quanh grid

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8); // Giảm insets để các card gần nhau hơn
        c.fill = GridBagConstraints.BOTH;

        // --- Row 1: KPI cards (4 cards) ---
        // Mỗi card có trọng số ngang bằng nhau, chiếm 1/4 tổng chiều rộng
        c.gridy = 0; c.weighty = 0.08;

        c.gridx = 0; c.weightx = 0.25; c.gridwidth = 1; // 25% chiều rộng
        grid.add(new KpiCard("TỔNG DOANH THU HÔM NAY", "245.500.000 VND", KpiCard.Style.BLUE, "+5.2% ↑"), c);

        c.gridx = 1; c.weightx = 0.25;
        grid.add(new KpiCard("SỐ VÉ ĐÃ BÁN", "1.250", KpiCard.Style.GREEN, "+2.1% ↑"), c);

        c.gridx = 2; c.weightx = 0.25;
        // Đổi tên "Tỷ lệ đầy TB" thành "SỐ VÉ ĐÃ BÁN" thứ 2 để khớp ảnh
        grid.add(new KpiCard("Tỷ lệ lắp đầy TB", "28%", KpiCard.Style.ORANGE, "-1.5% ↓"), c);

        c.gridx = 3; c.weightx = 0.25;
        // Đổi tên "Tỷ lệ hủy vé" thành "TỶ LỆ HỦY VÉ"
        grid.add(new KpiCard("TỶ LỆ HỦY VÉ", "3.5%", KpiCard.Style.RED, "+0.3% ↑"), c);

        // --- Row 2: Biểu đồ (BarChart, Gauge, StackedBar) ---
        c.gridy = 1; c.weighty = 0.42; // Cung cấp nhiều không gian theo chiều dọc

        // DOANH THU THEO TUYẾN (Bar Chart) - chiếm 2/4 chiều rộng
        String[] routes = {"SG - HN", "SG - Đà Nẵng", "HN - HP", "HN - Hạ Long", "TPHCM - NT", "TPHCM - ĐL"};
        // Giá trị mô phỏng trong ảnh: SG-HN ~12B, SG-ĐN ~23B, HN-HP ~6B, HN-HL ~5B, TPHCM-NT ~3B, TPHCM-ĐL ~2B
        double[] revenue = {12.3, 23.1, 5.8, 4.9, 2.7, 1.8}; // Đơn vị tỷ VNĐ
        ChartBar barsRoutes = new ChartBar("DOANH THU THEO TUYẾN", "Tuyến", "Tỷ VNĐ", routes, revenue, ChartBar.BarLabelPosition.ABOVE);
        c.gridx = 0; c.gridwidth = 2; c.weightx = 0.5; // Chiếm 50% chiều rộng
        grid.add(cardWrap(barsRoutes), c);

        // DSI | Ngày đổi vé trước TB (Gauge) - chiếm 1/4 chiều rộng
        Gauge7Day gauge = new Gauge7Day("DSI | Ngày đổi vé trước TB", 5.2); // 0..7
        c.gridx = 2; c.gridwidth = 1; c.weightx = 0.25; // Chiếm 25% chiều rộng
        grid.add(cardWrap(gauge), c);

        // NGẢ VÉ THEO LOẠI GHẾ (Stacked Bar Chart) - chiếm 1/4 chiều rộng
        String[] stackedPeriods = {"Current", "25-10", "26-10", "27-10", "28-10"};
        String[] stackedSeriesNames = {"Ghế mềm", "Giường nằm T4", "Giường nằm 6/4"};
        int[][] stackedValues = {
                {55, 38, 45, 30, 15}, // Ghế mềm
                {40, 25, 20, 18, 10}, // Giường nằm T4
                {25, 15, 10, 8, 5}   // Giường nằm 6/4
        };
        ChartStackedBar stacked = new ChartStackedBar("NGẢ VÉ THEO LOẠI GHẾ", stackedPeriods, stackedSeriesNames, stackedValues);
        c.gridx = 3; c.gridwidth = 1; c.weightx = 0.25; // Chiếm 25% chiều rộng
        grid.add(cardWrap(stacked), c);

        // --- Row 3: Biểu đồ (LineChart, BarChart, ComboChart) ---
        c.gridy = 2; c.weighty = 0.42;

        // DOANH THU THEO THÁNG (Line Chart) - chiếm 2/4 chiều rộng
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        // Dữ liệu mô phỏng giống hình ảnh
        double[] revMonth = {210,230,240,230,245,260,280,500,320,360,410,450};   // Doanh thu (triệu)
        double[] qtyMonth = {2.1,2.2,2.4,2.3,2.5,2.8,3.0,3.6,3.2,3.5,3.9,4.5}; // Số lượng vé (nghìn)
        ChartLine line = new ChartLine("DOANH THU THEO THÁNG", "Tháng", "Doanh thu (VNĐ) | Số lượng vé", months,
                new ChartLine.Serie("Doanh thu (VND)", revMonth, new Color(255, 170, 51)), // Màu cam
                new ChartLine.Serie("Số lượng vé", qtyMonth, new Color(58, 122, 202))   // Màu xanh
        );
        c.gridx = 0; c.gridwidth = 2; c.weightx = 0.5; // Chiếm 50% chiều rộng
        grid.add(cardWrap(line), c);

        // SỐ VÉ THEO LOẠI GHẾ (Bar Chart) - chiếm 1/4 chiều rộng
        String[] seatTypes = {"Ghế mềm", "Giường T4", "Giường T6"};
        // Dữ liệu mô phỏng giống hình ảnh
        double[] counts = {5200, 3100, 1800};
        ChartBar barSeats = new ChartBar("SỐ VÉ THEO LOẠI GHẾ", "Loại ghế", "Số vé", seatTypes, counts, ChartBar.BarLabelPosition.NONE);
        c.gridx = 2; c.gridwidth = 1; c.weightx = 0.25; // Chiếm 25% chiều rộng
        grid.add(cardWrap(barSeats), c);

        // SO SÁNH CÁC LOẠI VÉ (VÍ DỤ) (Combo Chart - Line + Stacked Bar) - chiếm 1/4 chiều rộng
        // Đây là một biểu đồ kết hợp, tôi sẽ cố gắng mô phỏng nó bằng cách tùy biến ChartLine
        // Hoặc một Bar Chart với nhiều Series để có được màu sắc đó
        String[] months3 = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        // Dữ liệu mô phỏng cho biểu đồ Line/Bar kết hợp ở cuối
        double[] compA = {100,200,150,250,300,400,350,300,250,300,350,500}; // Số lượng vé khuyến mãi (Line)
        int[][] compB = { // Dữ liệu Bar (chia thành 2 series, ví dụ vé thường và vé giảm giá)
                {150,180,160,200,220,280,250,230,200,220,250,350}, // Vé thường
                {50,70,40,60,80,120,100,70,50,80,100,150}        // Vé giảm giá
        };

        // Tạo một biểu đồ kết hợp đơn giản hoặc sử dụng một ChartLine với nhiều Series
        // Để đạt được hiệu ứng Bar + Line trong hình, chúng ta cần một component phức tạp hơn.
        // Tạm thời, tôi sẽ dùng ChartLine với nhiều Series để mô phỏng "số lượng khuyến mãi" và "số lượng vé thường".
        // Để vẽ Bar và Line cùng lúc, cần một class Chart riêng.
        ChartCombined comboChart = new ChartCombined("SO SÁNH CÁC LOẠI VÉ (ví dụ)", months3, compA, compB);
        c.gridx = 3; c.gridwidth = 1; c.weightx = 0.25; // Chiếm 25% chiều rộng
        grid.add(cardWrap(comboChart), c);

        return grid;
    }

    // ==== Card wrapper (Không đổi) ====
    private static JComponent cardWrap(JComponent inner) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE); // Màu nền trắng cho card
        p.setBorder(new CompoundBorder(
                new EmptyBorder(0,0,0,0), // Viền rỗng bên ngoài để tạo khoảng cách giữa các card
                new CompoundBorder(
                        BorderFactory.createLineBorder(new Color(235, 238, 245), 1), // Viền mỏng xám
                        new EmptyBorder(12, 12, 12, 12)))); // Padding bên trong card
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    // ===== KPI CARD (Đã cập nhật để khớp hình ảnh mới) =====
    static class KpiCard extends JPanel {
        enum Style { BLUE, GREEN, ORANGE, RED, TEAL } // Thêm ORANGE
        private final String title, value, change; // Thêm trường change
        private final Style style;

        public KpiCard(String title, String value, Style style, String change) {
            this.title = title; this.value = value; this.style = style; this.change = change;
            // setPreferredSize(new Dimension(260, 100)); // Xóa PreferredSize để GridBagLayout quản lý
            setBackground(Color.WHITE);
            setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(new Color(235,238,245)),
                    new EmptyBorder(12, 12, 12, 12)));
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color accent;
            Color textColor;
            switch (style) {
                case BLUE:  accent = new Color(32, 99, 155); textColor = new Color(58, 122, 202); break;
                case GREEN: accent = new Color(33, 150, 83);  textColor = new Color(33, 150, 83); break;
                case ORANGE: accent = new Color(255, 170, 51); textColor = new Color(255, 170, 51); break; // Màu cam
                case TEAL:  accent = new Color(0, 150, 136);  textColor = new Color(0, 150, 136); break;
                default:    accent = new Color(219, 68, 55); textColor = new Color(219, 68, 55); // RED
            }

            // Title
            g.setColor(new Color(101, 110, 121));
            g.setFont(getFont().deriveFont(Font.PLAIN, 12f));
            g.drawString(title, 6, 18);

            // Big value
            g.setColor(textColor); // Dùng textColor cho giá trị lớn
            g.setFont(getFont().deriveFont(Font.BOLD, 22f));
            g.drawString(value, 6, 48);

            // Change (Thêm vào dưới giá trị)
            g.setFont(getFont().deriveFont(Font.PLAIN, 12f));
            g.setColor(new Color(101, 110, 121)); // Màu xám cho phần trăm thay đổi
            g.drawString(change, 6, 68); // Vị trí dưới giá trị chính

            // tiny spark line (fake)
            int y = getHeight() - 18;
            int x0 = 6; int w = getWidth()-20;
            g.setColor(new Color(240, 247, 255));
            g.fillRoundRect(x0, y-10, w, 12, 12, 12);
            g.setColor(accent); // Dùng màu accent cho đường spark line
            for (int i = 0; i < 10; i++) {
                int x1 = x0 + (i*w)/10;
                int x2 = x0 + ((i+1)*w)/10;
                int y1 = y - (int)(Math.sin(i*0.6)*6 + 6);
                int y2 = y - (int)(Math.sin((i+1)*0.6)*6 + 6);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    // ===== Simple Bar Chart (Đã cập nhật để khớp hình ảnh mới) =====
    static class ChartBar extends JPanel {
        enum BarLabelPosition { ABOVE, NONE } // Để hiển thị nhãn trên cột hoặc không
        private final String title, xLabel, yLabel;
        private final String[] labels;
        private final double[] values;
        private final BarLabelPosition labelPosition;
        private final DecimalFormat valueFormat = new DecimalFormat("#,##0.#"); // Định dạng giá trị trên cột

        public ChartBar(String title, String xLabel, String yLabel, String[] labels, double[] values, BarLabelPosition labelPosition) {
            this.title = title; this.xLabel = xLabel; this.yLabel = yLabel; this.labels = labels; this.values = values;
            this.labelPosition = labelPosition;
            setBackground(Color.WHITE);
            // setPreferredSize(new Dimension(400, 260)); // Đã xóa
        }
        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Insets m = new Insets(38, 54, 46, 18);
            int W = getWidth(), H = getHeight();

            // Title
            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(40, 48, 59));
            g.drawString(title, m.left, 24);

            // Axes
            int x0 = m.left, y0 = H - m.bottom;
            int x1 = W - m.right, y1 = m.top;

            g.setColor(new Color(230, 235, 242));
            g.drawLine(x0, y0, x1, y0);
            g.drawLine(x0, y0, x0, y1);

            double max = Arrays.stream(values).max().orElse(1d) * 1.15; // Tăng max để có khoảng trống trên cùng
            int n = values.length;
            int barGap = 8; // Khoảng cách giữa các cột
            int bw = Math.max(18, (x1 - x0 - (n+1)*barGap) / n); // Chiều rộng cột động

            // grid + y labels
            g.setFont(getFont().deriveFont(11f));
            DecimalFormat dfY = new DecimalFormat("#,##0"); // Định dạng cho trục Y

            for (int i = 0; i <= 5; i++) {
                int y = y0 - i * (y0 - y1) / 5;
                g.setColor(new Color(241, 244, 248));
                g.drawLine(x0, y, x1, y);
                g.setColor(new Color(134, 142, 154));
                String t = dfY.format(max * i / 5);
                g.drawString(t, 8, y + 4);
            }

            // bars
            Color barColor = new Color(58, 122, 202); // Màu xanh cho cột
            for (int i = 0; i < n; i++) {
                int x = x0 + barGap + i * (bw + barGap); // Vị trí x của cột
                int h = (int) ((values[i] / max) * (y0 - y1));
                g.setColor(barColor);
                g.fillRoundRect(x, y0 - h, bw, h, 8, 8); // Cột bo góc

                // Nhãn giá trị trên cột
                if (labelPosition == BarLabelPosition.ABOVE) {
                    g.setColor(new Color(70, 78, 90)); // Màu chữ cho giá trị trên cột
                    g.setFont(getFont().deriveFont(9f));
                    String valText = valueFormat.format(values[i]) + "M"; // Thêm "M" để giống hình ảnh
                    int tw = g.getFontMetrics().stringWidth(valText);
                    g.drawString(valText, x + bw / 2 - tw / 2, y0 - h - 5); // Vị trí trên cột
                }

                // Nhãn dưới trục X
                g.setColor(new Color(90, 98, 110));
                String lab = labels[i];
                int tw = g.getFontMetrics().stringWidth(lab);
                g.setFont(getFont().deriveFont(10f));
                g.drawString(lab, x + bw / 2 - tw / 2, y0 + 16);
            }

            // axis labels (optional minimal)
            g.setFont(getFont().deriveFont(11f));
            g.setColor(new Color(120, 128, 139));
            g.drawString(xLabel, (x0 + x1) / 2 - 20, H - 8);
        }
    }

    // ===== Simple Line Chart (Đã cập nhật để khớp hình ảnh mới) =====
    static class ChartLine extends JPanel {
        static class Serie {
            final String name;
            final double[] vals;
            final Color color; // Thêm màu sắc cho từng series
            Serie(String name, double[] vals, Color color) {
                this.name = name; this.vals = vals; this.color = color;
            }
        }
        private final String title, xLabel, yLabel;
        private final String[] labels;
        private final Serie[] series;

        public ChartLine(String title, String x, String y, String[] labels, Serie... series) {
            this.title = title; this.xLabel = x; this.yLabel = y; this.labels = labels; this.series = series;
            setBackground(Color.WHITE);
            // setPreferredSize(new Dimension(500, 260)); // Đã xóa
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Insets m = new Insets(38, 54, 46, 18);
            int W = getWidth(), H = getHeight();

            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(40, 48, 59));
            g.drawString(title, m.left, 24);

            int x0 = m.left, y0 = H - m.bottom;
            int x1 = W - m.right, y1 = m.top;

            // grid
            g.setColor(new Color(230, 235, 242));
            g.drawLine(x0, y0, x1, y0);
            g.drawLine(x0, y0, x0, y1);

            double max = 0;
            for (Serie s : series) for (double v : s.vals) max = Math.max(max, v);
            max *= 1.15; // Tăng max để có khoảng trống trên cùng

            // y grid + labels
            g.setFont(getFont().deriveFont(11f));
            DecimalFormat dfY = new DecimalFormat("#,##0"); // Định dạng cho trục Y
            for (int i = 0; i <= 5; i++) {
                int y = y0 - i * (y0 - y1) / 5;
                g.setColor(new Color(241, 244, 248));
                g.drawLine(x0, y, x1, y);
                g.setColor(new Color(134, 142, 154));
                g.drawString(dfY.format(max * i / 5), 8, y + 4);
            }

            // x labels
            int n = labels.length;
            for (int i = 0; i < n; i++) {
                int x = x0 + i * (x1 - x0) / Math.max(1, n - 1);
                g.setColor(new Color(150, 158, 170));
                g.setFont(getFont().deriveFont(10f));
                int tw = g.getFontMetrics().stringWidth(labels[i]);
                g.drawString(labels[i], x - tw / 2, y0 + 16);
            }

            // series lines
            Stroke old = g.getStroke();
            for (int k = 0; k < series.length; k++) {
                Serie s = series[k];
                g.setStroke(new BasicStroke(2f));
                g.setColor(s.color); // Sử dụng màu của series

                int prevX = -1, prevY = -1;
                for (int i = 0; i < s.vals.length; i++) {
                    int x = x0 + i * (x1 - x0) / Math.max(1, n - 1);
                    int y = y0 - (int) ((s.vals[i] / max) * (y0 - y1));
                    if (prevX >= 0) g.drawLine(prevX, prevY, x, y);
                    g.fillOval(x - 3, y - 3, 6, 6); // Điểm tròn
                    prevX = x; prevY = y;
                }
            }
            g.setStroke(old);

            // axis labels (Y-axis title)
            g.setFont(getFont().deriveFont(11f));
            g.setColor(new Color(120, 128, 139));
            g.drawString(xLabel, (x0 + x1) / 2 - 20, H - 8);

            // Legend ở dưới cùng bên trái
            int legendX = x0;
            int legendY = H - 8; // Vị trí dưới cùng

            for (int i = 0; i < series.length; i++) {
                Serie s = series[i];
                g.setColor(s.color);
                g.fillRect(legendX, legendY, 10, 4); // Hình chữ nhật nhỏ tượng trưng
                g.setFont(getFont().deriveFont(9f));
                g.setColor(new Color(70, 78, 90));
                g.drawString(s.name, legendX + 14, legendY + 4);

                legendX += g.getFontMetrics().stringWidth(s.name) + 25; // Di chuyển sang phải cho legend tiếp theo
            }
        }
    }

    // ===== Gauge 0..7 days (Đã cập nhật để khớp hình ảnh mới) =====
    static class Gauge7Day extends JPanel {
        private final String title;
        private final double value; // 0..7

        public Gauge7Day(String title, double value) {
            this.title = title; this.value = Math.max(0, Math.min(7, value));
            setBackground(Color.WHITE);
            // setPreferredSize(new Dimension(260, 260)); // Đã xóa
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(40,48,59));
            g.drawString(title, 12, 24);

            int cx = getWidth()/2, cy = getHeight()/2 + 12;
            int r = Math.min(getWidth(), getHeight())/2 - 24; // Bán kính

            // Điều chỉnh vị trí của vòng cung để bắt đầu từ khoảng 200 độ và kết thúc ở 340 độ
            // Tổng 140 độ
            double startAngle = 200;
            double arcExtent = 140;

            // arc background
            g.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(236, 240, 245));
            g.draw(new Arc2D.Double(cx - r, cy - r, 2*r, 2*r, startAngle, arcExtent, Arc2D.OPEN));

            // value arc
            double pct = value/7.0; // Giá trị từ 0 đến 1
            g.setColor(new Color(58, 122, 202)); // Màu xanh cho giá trị
            g.draw(new Arc2D.Double(cx - r, cy - r, 2*r, 2*r, startAngle, (int)(arcExtent*pct), Arc2D.OPEN));

            // needle
            double ang = Math.toRadians(startAngle + arcExtent*pct); // Góc của kim
            int needleLength = r - 20; // Chiều dài kim
            int nx = (int)(cx + needleLength*Math.cos(ang));
            int ny = (int)(cy + needleLength*Math.sin(ang));

            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(33,150,243)); // Màu kim
            g.drawLine(cx, cy, nx, ny);
            g.fillOval(cx-5, cy-5, 10, 10); // Tâm kim

            // label (giá trị)
            g.setFont(getFont().deriveFont(Font.BOLD, 20f));
            g.setColor(new Color(58, 63, 72));
            String s = new DecimalFormat("#0.0").format(value) + " NGÀY";
            int tw = g.getFontMetrics().stringWidth(s);
            g.drawString(s, cx - tw/2, cy + r - 10);

            // Label "7 NGÀY" ở cạnh vòng tròn
            g.setFont(getFont().deriveFont(Font.PLAIN, 10f));
            g.setColor(new Color(101, 110, 121));
            String limitLabel = "7 NGÀY";
            int limitLabelTw = g.getFontMetrics().stringWidth(limitLabel);
            g.drawString(limitLabel, cx + r - limitLabelTw/2 - 5, cy + 10); // Ước lượng vị trí gần cuối cung
        }
    }

    // ===== Stacked Bar (Đã cập nhật để khớp hình ảnh mới) =====
    static class ChartStackedBar extends JPanel {
        private final String title;
        private final String[] xLabels;
        private final String[] seriesNames;
        private final int[][] values; // [serie][x]

        public ChartStackedBar(String title, String[] xLabels, String[] seriesNames, int[][] values) {
            this.title = title; this.xLabels = xLabels; this.seriesNames = seriesNames; this.values = values;
            setBackground(Color.WHITE);
            // setPreferredSize(new Dimension(360, 260)); // Đã xóa
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Insets m = new Insets(38, 54, 46, 18);
            int W = getWidth(), H = getHeight();

            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(40, 48, 59));
            g.drawString(title, m.left, 24);

            int x0 = m.left, y0 = H - m.bottom;
            int x1 = W - m.right, y1 = m.top;

            // axis
            g.setColor(new Color(230, 235, 242));
            g.drawLine(x0, y0, x1, y0);
            g.drawLine(x0, y0, x0, y1);

            // max height for Y-axis
            int n = xLabels.length; // Số lượng cột
            int k = seriesNames.length; // Số lượng series trong mỗi cột
            int maxTotalValue = 1;
            for (int i = 0; i < n; i++) {
                int s = 0;
                for (int j = 0; j < k; j++) s += values[j][i];
                maxTotalValue = Math.max(maxTotalValue, s);
            }
            maxTotalValue = (int) (maxTotalValue * 1.2); // Thêm khoảng trống 20% trên cùng

            // Y-axis labels and grid lines
            g.setFont(getFont().deriveFont(11f));
            DecimalFormat dfY = new DecimalFormat("#,##0");
            for (int i = 0; i <= 5; i++) {
                int y = y0 - i * (y0 - y1) / 5;
                g.setColor(new Color(241, 244, 248)); // Màu nhạt cho đường lưới
                g.drawLine(x0, y, x1, y);
                g.setColor(new Color(134, 142, 154)); // Màu xám cho chữ
                g.drawString(dfY.format(maxTotalValue * i / 5), 8, y + 4);
            }


            // bars
            Color[] cols = {new Color(58,122,202), new Color(255,152,0), new Color(76,175,80)}; // Blue, Orange, Green
            int barPadding = 10; // Khoảng cách giữa các nhóm cột
            int bw = Math.max(14, (x1 - x0 - (n + 1) * barPadding) / n); // Chiều rộng mỗi cột trong nhóm

            for (int i = 0; i < n; i++) { // Duyệt qua từng nhóm cột (tức là từng nhãn X)
                int baseY = y0;
                int x = x0 + barPadding + i * (bw + barPadding); // Vị trí x của nhóm cột

                for (int j = 0; j < k; j++) { // Duyệt qua từng series trong nhóm cột
                    int h = (int)((values[j][i]/(double)maxTotalValue)*(y0-y1));
                    g.setColor(cols[j % cols.length]); // Chọn màu cho series
                    g.fillRect(x, baseY - h, bw, h); // Vẽ thanh
                    baseY -= h; // Cập nhật vị trí đáy cho thanh tiếp theo
                }
                g.setColor(new Color(120,128,139));
                g.setFont(getFont().deriveFont(10f));
                int tw = g.getFontMetrics().stringWidth(xLabels[i]);
                g.drawString(xLabels[i], x + bw/2 - tw/2, y0 + 16); // Nhãn dưới trục X
            }

            // legend (Ở bên phải trên cùng, khớp với hình ảnh)
            int legendX = x1 - 100; // Căn lề phải
            int legendY = m.top + 5;
            g.setFont(getFont().deriveFont(10f)); // Font nhỏ hơn cho legend
            for (int j = 0; j < k; j++) {
                g.setColor(cols[j % cols.length]);
                g.fillRect(legendX, legendY + j*18, 10, 10); // Ô vuông màu
                g.setColor(new Color(70,78,90));
                g.drawString(seriesNames[j], legendX + 14, legendY + 9 + j*18);
            }
        }
    }

    // ===== Chart Combined (Mới: để mô phỏng biểu đồ Bar + Line) =====
    static class ChartCombined extends JPanel {
        private final String title;
        private final String[] xLabels;
        private final double[] lineValues; // Dữ liệu cho đường Line
        private final int[][] barValues;   // Dữ liệu cho Bar (có thể 2 series)

        public ChartCombined(String title, String[] xLabels, double[] lineValues, int[][] barValues) {
            this.title = title; this.xLabels = xLabels; this.lineValues = lineValues; this.barValues = barValues;
            setBackground(Color.WHITE);
            // setPreferredSize(new Dimension(400, 260)); // Đã xóa
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Insets m = new Insets(38, 54, 46, 18);
            int W = getWidth(), H = getHeight();

            g.setFont(getFont().deriveFont(Font.BOLD, 14f));
            g.setColor(new Color(40, 48, 59));
            g.drawString(title, m.left, 24);

            int x0 = m.left, y0 = H - m.bottom;
            int x1 = W - m.right, y1 = m.top;

            // axis
            g.setColor(new Color(230, 235, 242));
            g.drawLine(x0, y0, x1, y0);
            g.drawLine(x0, y0, x0, y1);

            // Calculate max for scaling
            double maxLine = Arrays.stream(lineValues).max().orElse(1d);
            int maxBar = 1;
            for (int i = 0; i < xLabels.length; i++) {
                int s = 0;
                for (int j = 0; j < barValues.length; j++) s += barValues[j][i];
                maxBar = Math.max(maxBar, s);
            }
            double overallMax = Math.max(maxLine, maxBar) * 1.15;

            // Y-axis labels and grid lines
            g.setFont(getFont().deriveFont(11f));
            DecimalFormat dfY = new DecimalFormat("#,##0");
            for (int i = 0; i <= 5; i++) {
                int y = y0 - i * (y0 - y1) / 5;
                g.setColor(new Color(241, 244, 248));
                g.drawLine(x0, y, x1, y);
                g.setColor(new Color(134, 142, 154));
                g.drawString(dfY.format(overallMax * i / 5), 8, y + 4);
            }

            // x labels
            int n = xLabels.length;
            int barGap = 10;
            int bw = Math.max(10, (x1 - x0 - (n + 1) * barGap) / n); // Chiều rộng mỗi nhóm bar

            for (int i = 0; i < n; i++) {
                g.setColor(new Color(150, 158, 170));
                g.setFont(getFont().deriveFont(10f));
                int x = x0 + barGap + i * (bw + barGap);
                int tw = g.getFontMetrics().stringWidth(xLabels[i]);
                g.drawString(xLabels[i], x + bw / 2 - tw / 2, y0 + 16);
            }

            // Draw Bars (2 series)
            Color[] barCols = {new Color(255, 170, 51, 150), new Color(255, 170, 51, 80)}; // Cam đậm và nhạt
            for (int i = 0; i < n; i++) {
                int baseY = y0;
                int x = x0 + barGap + i * (bw + barGap);
                for (int j = 0; j < barValues.length; j++) {
                    int h = (int) ((barValues[j][i] / overallMax) * (y0 - y1));
                    g.setColor(barCols[j]); // Sử dụng màu khác nhau cho mỗi series bar
                    g.fillRect(x, baseY - h, bw, h);
                    baseY -= h;
                }
            }

            // Draw Line
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(2f));
            g.setColor(new Color(76, 175, 80)); // Màu xanh lá cho đường line
            int prevX = -1, prevY = -1;
            for (int i = 0; i < lineValues.length; i++) {
                int x = x0 + barGap + i * (bw + barGap) + bw / 2; // Vị trí giữa cột bar
                int y = y0 - (int) ((lineValues[i] / overallMax) * (y0 - y1));
                if (prevX >= 0) g.drawLine(prevX, prevY, x, y);
                g.fillOval(x - 3, y - 3, 6, 6);
                prevX = x; prevY = y;
            }
            g.setStroke(old);

            // Legend
            g.setFont(getFont().deriveFont(10f));
            g.setColor(new Color(120, 128, 139));
            String legendText = "Số lượng vé khuyến mãi"; // Lấy từ hình ảnh
            int tw = g.getFontMetrics().stringWidth(legendText);
            g.drawString(legendText, (x0 + x1) / 2 - tw/2, H - 8); // Đặt ở dưới cùng
        }
    }


    // ====== MAIN (test độc lập) ======
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.themes.FlatMacLightLaf");
        } catch (Exception ignore) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Dashboard - Recreated");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Bọc Dashboard trong JScrollPane để đảm bảo cuộn nếu cần
            JScrollPane scroller = new JScrollPane(new Dashboard());
            scroller.setBorder(BorderFactory.createEmptyBorder());
            scroller.getVerticalScrollBar().setUnitIncrement(16);

            f.setContentPane(scroller);
            f.setSize(1200, 720);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}