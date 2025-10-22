package gui.application.form.thongKe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class PanelThongKeDoanhThuTheoThoiGian extends JPanel {

    private final JDateChooser tuNgay = createDateChooser("dd/MM/yyyy");
    private final JDateChooser denNgay = createDateChooser("dd/MM/yyyy");

    private final JDateChooser tuThang = createDateChooser("MM/yyyy");
    private final JDateChooser denThang = createDateChooser("MM/yyyy");

    private final JDateChooser tuNam = createDateChooser("yyyy");
    private final JDateChooser denNam = createDateChooser("yyyy");

    private final JPanel filterSwitcher = new JPanel(new CardLayout());
    private static final String CARD_NGAY = "CARD_NGAY";
    private static final String CARD_THANG = "CARD_THANG";
    private static final String CARD_NAM = "CARD_NAM";

    public PanelThongKeDoanhThuTheoThoiGian() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Thanh bộ lọc =====
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(Color.WHITE);

        JLabel lblLoai = new JLabel("Loại thời gian:");
        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm"});

        filterSwitcher.setBackground(Color.WHITE);
        filterSwitcher.add(buildNgayFilter(), CARD_NGAY);
        filterSwitcher.add(buildThangFilter(), CARD_THANG);
        filterSwitcher.add(buildNamFilter(), CARD_NAM);

        JButton btnTim = new JButton("Tìm kiếm");
        btnTim.setBackground(new Color(46, 204, 113));
        btnTim.setForeground(Color.WHITE);

        bar.add(lblLoai);
        bar.add(cbLoai);
        bar.add(filterSwitcher);
        bar.add(btnTim);

        add(bar, BorderLayout.NORTH);

        // ===== Đường phân cách =====
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(180, 180, 180));
        add(sep1, BorderLayout.AFTER_LAST_LINE);

        // ===== Nội dung chính =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // --- Tổng quan doanh thu ---
        JPanel infoPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.add(createCard("Tổng thu", "15.000.000 VNĐ", new Color(52, 152, 219)));
        infoPanel.add(createCard("Dịch vụ", "2.000.000 VNĐ", new Color(41, 128, 185)));
        infoPanel.add(createCard("Tổng chi", "1.500.000 VNĐ", new Color(231, 76, 60)));
        infoPanel.add(createCard("Lợi nhuận", "15.500.000 VNĐ", new Color(46, 204, 113)));
        contentPanel.add(infoPanel, BorderLayout.NORTH);

        // --- Đường phân chia giữa tổng quan và biểu đồ ---
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(200, 200, 200));
        contentPanel.add(sep2, BorderLayout.CENTER);

        // --- Tab biểu đồ và chi tiết ---
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Tổng quan", taoPanelTongQuan());
        tab.addTab("Chi tiết", taoPanelChiTiet());
        contentPanel.add(tab, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // ===== Ràng buộc =====
        addConstraint(tuNgay, denNgay, "Ngày");
        addConstraint(tuThang, denThang, "Tháng");
        addConstraint(tuNam, denNam, "Năm");

        // ===== Sự kiện chọn loại =====
        cbLoai.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CardLayout cl = (CardLayout) filterSwitcher.getLayout();
                String loai = (String) cbLoai.getSelectedItem();
                if ("Theo ngày".equals(loai)) {
                    cl.show(filterSwitcher, CARD_NGAY);
                } else if ("Theo tháng".equals(loai)) {
                    cl.show(filterSwitcher, CARD_THANG);
                } else {
                    cl.show(filterSwitcher, CARD_NAM);
                }
            }
        });
    }

    private JPanel buildNgayFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Từ ngày:"));
        p.add(tuNgay);
        p.add(new JLabel("Đến ngày:"));
        p.add(denNgay);
        return p;
    }

    private JPanel buildThangFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Từ tháng:"));
        p.add(tuThang);
        p.add(new JLabel("Đến tháng:"));
        p.add(denThang);
        return p;
    }

    private JPanel buildNamFilter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        p.setBackground(Color.WHITE);
        p.add(new JLabel("Từ năm:"));
        p.add(tuNam);
        p.add(new JLabel("Đến năm:"));
        p.add(denNam);
        return p;
    }

    private void addConstraint(JDateChooser from, JDateChooser to, String type) {
        PropertyChangeListener l = (PropertyChangeEvent evt) -> {
            Date f = from.getDate();
            Date t = to.getDate();
            if (f != null && t != null && t.before(f)) {
                JOptionPane.showMessageDialog(this,
                        "⚠️ " + type + " kết thúc phải ≥ " + type + " bắt đầu",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                to.setDate(f);
            }
        };
        from.addPropertyChangeListener("date", l);
        to.addPropertyChangeListener("date", l);
    }

    private JPanel taoPanelTongQuan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel chartPlaceholder = new JLabel("📊 Biểu đồ tổng quan doanh thu (chưa có dữ liệu)", SwingConstants.CENTER);
        chartPlaceholder.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        panel.add(chartPlaceholder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelChiTiet() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        String[] columns = {"Ngày/Tháng/Năm", "Tuyến", "Doanh thu (VNĐ)"};
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("<html><center>" + title + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(150, 80));
        card.add(lbl, BorderLayout.CENTER);
        return card;
    }

    private static JDateChooser createDateChooser(String pattern) {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString(pattern);
        dc.setPreferredSize(new Dimension(120, 25));
        dc.setDate(new Date());
        return dc;
    }
}
