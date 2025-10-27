package gui.application.form.thongKe;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class PanelThongKeKhachHang extends JPanel {
    private final JDateChooser tuNgay = new JDateChooser();
    private final JDateChooser denNgay = new JDateChooser();
    private final JComboBox<String> cbLoaiKH = new JComboBox<>(new String[]{
            "Tất cả", "Khách hàng mới", "Khách hàng quay lại", "Khách hàng VIP"
    });

    public PanelThongKeKhachHang() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== Bộ lọc khách hàng =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Từ ngày:"));
        tuNgay.setDateFormatString("dd/MM/yyyy");
        tuNgay.setDate(new Date());
        filterPanel.add(tuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        denNgay.setDateFormatString("dd/MM/yyyy");
        denNgay.setDate(new Date());
        filterPanel.add(denNgay);

        filterPanel.add(new JLabel("Loại khách hàng:"));
        filterPanel.add(cbLoaiKH);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setBackground(new Color(231, 76, 60));
        btnLoc.setForeground(Color.WHITE);
        filterPanel.add(btnLoc);

        add(filterPanel, BorderLayout.NORTH);

        // ===== Tổng quan khách hàng =====
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.add(createCard("Tổng khách hàng", "8.520", new Color(52, 152, 219)));
        summaryPanel.add(createCard("Khách hàng mới", "1.250", new Color(46, 204, 113)));
        summaryPanel.add(createCard("Khách hàng quay lại", "6.800", new Color(41, 128, 185)));
        summaryPanel.add(createCard("Khách hàng VIP", "470", new Color(241, 196, 15)));

        add(summaryPanel, BorderLayout.CENTER);

        // ===== Tabs biểu đồ & chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Phân tích hành vi", taoPanelPhanTich());
        tab.addTab("Danh sách chi tiết", taoPanelChiTiet());
        add(tab, BorderLayout.SOUTH);
    }

    private JPanel taoPanelPhanTich() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("📊 Khu vực biểu đồ phân tích khách hàng (sẽ thêm biểu đồ sau)", SwingConstants.CENTER);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelChiTiet() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        String[] columns = {"Mã KH", "Tên khách hàng", "Số điện thoại", "Số lượt mua", "Tổng chi tiêu (VNĐ)", "Hạng"};
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
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
}
