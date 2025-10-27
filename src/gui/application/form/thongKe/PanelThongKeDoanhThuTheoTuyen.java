package gui.application.form.thongKe;

import dao.ThongKe_DAO;
import dao.ThongKe_DAO.ThongKeTuyenItem;
import com.toedter.calendar.JDateChooser;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PanelThongKeDoanhThuTheoTuyen extends JPanel {

    private final ThongKe_DAO thongKeDAO = new ThongKe_DAO();
    private final Logger LOGGER = Logger.getLogger(PanelThongKeDoanhThuTheoTuyen.class.getName());

    // ======= Components =======
    private final JComboBox<String> cboTuyen = new JComboBox<>(new String[]{"Tất cả", "Bắc - Nam", "Sài Gòn - Đà Nẵng", "Sài Gòn - Nha Trang"});
    private final JComboBox<String> cboGaDi = new JComboBox<>(new String[]{"Tất cả", "Sài Gòn", "Biên Hòa", "Nha Trang"});
    private final JComboBox<String> cboGaDen = new JComboBox<>(new String[]{"Tất cả", "Đà Nẵng", "Huế", "Hà Nội"});

    private final JDateChooser tuNgay = new JDateChooser();
    private final JDateChooser denNgay = new JDateChooser();
    private final JButton btnThongKe = new JButton("Tìm kiếm");

    private final JLabel lblTongTuyen = new JLabel("0", JLabel.CENTER);
    private final JLabel lblTongChuyen = new JLabel("0", JLabel.CENTER);
    private final JLabel lblTongVe = new JLabel("0", JLabel.CENTER);
    private final JLabel lblTongDoanhThu = new JLabel("0 đ", JLabel.CENTER);
    private final JLabel lblTongLoiNhuan = new JLabel("0 đ", JLabel.CENTER);

    private final JPanel chartPanelContainer = new JPanel(new BorderLayout());
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"STT", "Tên tuyến", "Số chuyến", "Số vé", "Doanh thu (VNĐ)", "Chi phí (VNĐ)", "Lợi nhuận (VNĐ)"}, 0);

    private final JTable table = new JTable(tableModel);

    private static final Color COLOR_RED = new Color(238, 69, 58); // Chủ đạo
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public PanelThongKeDoanhThuTheoTuyen() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildContentPanel(), BorderLayout.CENTER);

        btnThongKe.addActionListener(this::handleThongKe);
    }

    private JPanel buildFilterPanel() {
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filter.setBackground(Color.WHITE);

        tuNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setDate(new Date());
        denNgay.setDate(new Date());

        styleButton(btnThongKe);

        filter.add(new JLabel("Tuyến:"));
        filter.add(cboTuyen);
        filter.add(new JLabel("Ga đi:"));
        filter.add(cboGaDi);
        filter.add(new JLabel("Ga đến:"));
        filter.add(cboGaDen);
        filter.add(new JLabel("Từ ngày:"));
        filter.add(tuNgay);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(denNgay);
        filter.add(btnThongKe);

        return filter;
    }

    private JPanel buildContentPanel() {
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(Color.WHITE);

        // Cards tổng quan
        JPanel cards = new JPanel(new GridLayout(1, 5, 10, 0));
        cards.setOpaque(false);
        cards.add(createCard("Tổng số Tuyến", lblTongTuyen, new Color(52, 152, 219)));
        cards.add(createCard("Tổng số Chuyến", lblTongChuyen, new Color(46, 204, 113)));
        cards.add(createCard("Tổng Vé bán", lblTongVe, new Color(41, 128, 185)));
        cards.add(createCard("Tổng Doanh thu", lblTongDoanhThu, new Color(39, 174, 96)));
        cards.add(createCard("Tổng Lợi nhuận", lblTongLoiNhuan, new Color(230, 126, 34)));
        content.add(cards, BorderLayout.NORTH);

        // Tabs: Biểu đồ / Chi tiết
        JTabbedPane tab = new JTabbedPane();
        chartPanelContainer.setBackground(Color.WHITE);
        chartPanelContainer.add(new JLabel("Chưa có dữ liệu", JLabel.CENTER), BorderLayout.CENTER);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);

        tab.addTab("Biểu đồ tổng quan", chartPanelContainer);
        tab.addTab("Bảng chi tiết", scroll);
        content.add(tab, BorderLayout.CENTER);

        return content;
    }

    private JPanel createCard(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel(title, JLabel.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 14));

        value.setForeground(Color.WHITE);
        value.setFont(new Font("Times New Roman", Font.BOLD, 18));

        p.add(lbl, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    private void styleButton(JButton b) {
        b.setBackground(COLOR_RED);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Times New Roman", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(255, 90, 78));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(COLOR_RED);
            }
        });
    }

    private void handleThongKe(ActionEvent e) {
        LocalDate from = tuNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate to = denNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        btnThongKe.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        chartPanelContainer.removeAll();
        chartPanelContainer.add(new JLabel("Đang tải dữ liệu...", JLabel.CENTER), BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();

        SwingWorker<Map<String, ThongKeTuyenItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, ThongKeTuyenItem> doInBackground() throws Exception {
                return thongKeDAO.getThongKeTheoTuyen(from, to);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnThongKe.setEnabled(true);
                try {
                    Map<String, ThongKeTuyenItem> data = get();
                    if (data == null || data.isEmpty()) {
                        showEmptyChart();
                        return;
                    }
                    updateData(data);
                } catch (InterruptedException | ExecutionException ex) {
                    LOGGER.log(Level.SEVERE, "Lỗi khi tải thống kê", ex);
                    showEmptyChart();
                }
            }
        };
        worker.execute();
    }

    private void updateData(Map<String, ThongKeTuyenItem> data) {
        tableModel.setRowCount(0);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int stt = 1;
        double tongDT = 0, tongLN = 0;
        for (Map.Entry<String, ThongKeTuyenItem> entry : data.entrySet()) {
            ThongKeTuyenItem item = entry.getValue();
            tableModel.addRow(new Object[]{
                    stt++, item.tenTuyen, item.soChuyen, item.soVeBan,
                    item.tongDoanhThu, item.tongChi, item.loiNhuan
            });
            dataset.addValue(item.tongDoanhThu, "Doanh thu", item.tenTuyen);
            dataset.addValue(item.loiNhuan, "Lợi nhuận", item.tenTuyen);
            tongDT += item.tongDoanhThu;
            tongLN += item.loiNhuan;
        }

        lblTongTuyen.setText(String.valueOf(data.size()));
        lblTongChuyen.setText(String.valueOf(stt - 1));
        lblTongVe.setText(String.valueOf(data.values().stream().mapToInt(i -> i.soVeBan).sum()));
        lblTongDoanhThu.setText(currencyFormat.format(tongDT));
        lblTongLoiNhuan.setText(currencyFormat.format(tongLN));

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu và Lợi nhuận theo Tuyến",
                "Tuyến", "VNĐ", dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setSeriesPaint(1, new Color(39, 174, 96));

        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setBackgroundPaint(Color.WHITE);

        chartPanelContainer.removeAll();
        chartPanelContainer.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }

    private void showEmptyChart() {
        chartPanelContainer.removeAll();
        JLabel msg = new JLabel("Không có dữ liệu trong khoảng đã chọn", JLabel.CENTER);
        msg.setFont(new Font("Times New Roman", Font.ITALIC, 14));
        msg.setForeground(Color.GRAY);
        chartPanelContainer.add(msg, BorderLayout.CENTER);
        chartPanelContainer.revalidate();
        chartPanelContainer.repaint();
    }
}
