package gui.application.form.thongKe;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;
import java.util.Date;

public class PanelThongKeVe extends JPanel {
    private final JDateChooser tuNgay = new JDateChooser();
    private final JDateChooser denNgay = new JDateChooser();
    private final JComboBox<String> cbTuyen = new JComboBox<>(new String[]{"Tất cả", "Bắc - Nam", "Nam - Bắc"});
    private final JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã dùng", "Hoàn", "Đổi", "Còn hiệu lực"});

    public PanelThongKeVe() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== Bộ lọc =====
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

        filterPanel.add(new JLabel("Tuyến:"));
        filterPanel.add(cbTuyen);

        filterPanel.add(new JLabel("Trạng thái vé:"));
        filterPanel.add(cbTrangThai);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setBackground(new Color(231, 76, 60));
        btnLoc.setForeground(Color.WHITE);
        filterPanel.add(btnLoc);

        add(filterPanel, BorderLayout.NORTH);

        // ===== Khu tổng quan vé =====
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.add(createCard("Tổng số vé bán", "12.350", new Color(52, 152, 219)));
        summaryPanel.add(createCard("Vé hoàn", "300", new Color(231, 76, 60)));
        summaryPanel.add(createCard("Vé đổi", "120", new Color(241, 196, 15)));
        summaryPanel.add(createCard("Vé còn hiệu lực", "11.930", new Color(46, 204, 113)));

        add(summaryPanel, BorderLayout.CENTER);

        // ===== Biểu đồ + Chi tiết =====
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Biểu đồ thống kê vé", taoPanelBieuDo());
        tab.addTab("Chi tiết vé", taoPanelChiTiet());
        add(tab, BorderLayout.SOUTH);
    }

    private JPanel taoPanelBieuDo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("📊 Khu vực biểu đồ vé (sẽ hiển thị biểu đồ sau)", SwingConstants.CENTER);
        lbl.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    private JPanel taoPanelChiTiet() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        String[] columns = {"Mã vé", "Tuyến", "Ngày khởi hành", "Giá vé", "Trạng thái"};
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
        return card;
    }
}