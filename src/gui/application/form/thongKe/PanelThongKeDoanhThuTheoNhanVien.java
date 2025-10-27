package gui.application.form.thongKe;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class PanelThongKeDoanhThuTheoNhanVien extends JPanel {

    public PanelThongKeDoanhThuTheoNhanVien() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== BỘ LỌC =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Nhân viên:"));
        JComboBox<String> cbNhanVien = new JComboBox<>(new String[]{
                "Tất cả", "Nguyễn Văn A", "Trần Thị B", "Phạm Văn C", "Lê Thị D"
        });
        filterPanel.add(cbNhanVien);

        filterPanel.add(new JLabel("Từ ngày:"));
        JDateChooser tuNgay = new JDateChooser();
        tuNgay.setDateFormatString("dd/MM/yyyy");
        filterPanel.add(tuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        JDateChooser denNgay = new JDateChooser();
        denNgay.setDateFormatString("dd/MM/yyyy");
        filterPanel.add(denNgay);

        JButton btnThongKe = new JButton("Thống kê");
        btnThongKe.setBackground(new Color(46, 204, 113));
        btnThongKe.setForeground(Color.WHITE);
        filterPanel.add(btnThongKe);

        add(filterPanel, BorderLayout.NORTH);

        // ===== THẺ TAB =====
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Tổng quan", taoPanelTongQuan());
        tab.addTab("Chi tiết", taoPanelChiTiet());
        add(tab, BorderLayout.CENTER);
    }

    private JPanel taoPanelTongQuan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Placeholder biểu đồ tổng quan
        JLabel chartPlaceholder = new JLabel("👨‍💼 Biểu đồ doanh thu theo nhân viên (chưa có dữ liệu)", SwingConstants.CENTER);
        chartPlaceholder.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(chartPlaceholder, BorderLayout.CENTER);

        // Có thể thêm thẻ thống kê nhỏ ở trên biểu đồ
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(createCard("Tổng doanh thu", "45.000.000 VNĐ", new Color(52, 152, 219)));
        infoPanel.add(createCard("Tổng vé bán", "120 vé", new Color(41, 128, 185)));
        infoPanel.add(createCard("Trung bình/nhân viên", "15.000.000 VNĐ", new Color(46, 204, 113)));
        panel.add(infoPanel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel taoPanelChiTiet() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Bảng dữ liệu chi tiết
        String[] columns = { "Mã nhân viên", "Tên nhân viên", "Số vé bán", "Tổng doanh thu (VNĐ)" };
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("<html><center>" + title + "<br><b>" + value + "</b></center></html>", SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        card.setBackground(color);
        card.add(lbl, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(150, 80));
        return card;
    }
}