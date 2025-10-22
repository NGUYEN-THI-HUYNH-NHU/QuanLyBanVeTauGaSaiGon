package gui.application.form.thongKe;

import javax.swing.*;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

public class PanelThongKeDoanhThuTheoTuyen extends JPanel {

    public PanelThongKeDoanhThuTheoTuyen() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== BỘ LỌC =====
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Tuyến:"));
        JComboBox<String> cbTuyen = new JComboBox<>(new String[]{
                "Tất cả", "Sài Gòn - Hà Nội", "Sài Gòn - Đà Nẵng", "Sài Gòn - Nha Trang"
        });
        filterPanel.add(cbTuyen);

        filterPanel.add(new JLabel("Ga đi:"));
        JComboBox<String> cbGaDi = new JComboBox<>(new String[]{"Tất cả", "Sài Gòn", "Biên Hòa", "Nha Trang"});
        filterPanel.add(cbGaDi);

        filterPanel.add(new JLabel("Ga đến:"));
        JComboBox<String> cbGaDen = new JComboBox<>(new String[]{"Tất cả", "Đà Nẵng", "Huế", "Hà Nội"});
        filterPanel.add(cbGaDen);

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
        JLabel chartPlaceholder = new JLabel("🚆 Biểu đồ doanh thu theo tuyến (chưa có dữ liệu)", SwingConstants.CENTER);
        chartPlaceholder.setFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.add(chartPlaceholder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoPanelChiTiet() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Tuyến", "Ga đi", "Ga đến", "Ngày", "Số vé", "Doanh thu (VNĐ)"};
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
