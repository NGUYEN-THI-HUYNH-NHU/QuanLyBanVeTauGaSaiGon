package gui.application.form.thongKe;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class PanelThongKeDoanhThu extends JPanel {

    public PanelThongKeDoanhThu() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== TẠO TAB CHÍNH =====
        JTabbedPane tab = new JTabbedPane();
        tab.addTab("Thời gian", new PanelThongKeDoanhThuTheoThoiGian());
        tab.addTab("Tuyến", new PanelThongKeDoanhThuTheoTuyen());
        tab.addTab("Nhân viên", new PanelThongKeDoanhThuTheoNhanVien());
        tab.addTab("Thanh toán", new PanelThongKeDoanhThuTheoThanhToan());

        // ===== TẠO BORDER CHO TOÀN KHUNG =====
        Border outer = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border inner = BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true); // viền bo tròn
        Border titled = BorderFactory.createTitledBorder(inner, "Thống kê doanh thu",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("", Font.BOLD, 20), new Color(30, 41, 41));
        setBorder(BorderFactory.createCompoundBorder(outer, titled));

        add(tab, BorderLayout.CENTER);

        // ===== ĐỔI MÀU TAB KHI ĐƯỢC CHỌN =====
        tab.addChangeListener(e -> {
            int index = tab.getSelectedIndex();
            for (int i = 0; i < tab.getTabCount(); i++) {
                if (i == index) {
                    tab.setBackgroundAt(i, new Color(58, 190, 229));
                    tab.setForegroundAt(i, Color.WHITE);
                } else {
                    tab.setBackgroundAt(i, new Color(245, 245, 245));
                    tab.setForegroundAt(i, Color.BLACK);
                }
            }
        });

        // Set màu cho tab đầu tiên
        tab.setBackgroundAt(0, new Color(58, 190, 229));
        tab.setForegroundAt(0, Color.WHITE);
    }
}
