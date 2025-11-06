package gui.application.form.thongKe;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


    /**
     * Một component JPanel tùy chỉnh để hiển thị một chỉ số thống kê (Card).
     * Gồm 1 tiêu đề (ví dụ: "Tổng hóa đơn") và 1 giá trị (ví dụ: "1,200,000 VNĐ").
     */
    public class StatCard extends JPanel {

        private JLabel lblTitle;
        private JLabel lblValue;
        private Color valueColor;

        /**
         * Tạo một card thống kê mới.
         * @param title Tiêu đề của card (ví dụ: "Tổng hóa đơn bán được")
         * @param initialValue Giá trị ban đầu (ví dụ: "0")
         * @param valueColor Màu sắc cho giá trị
         */
        public StatCard(String title, String initialValue, Color valueColor) {
            this.valueColor = valueColor;

            // Thiết lập layout và viền
            setLayout(new BorderLayout(5, 5));
            // Viền 10px trống bên trong, và một đường viền xám nhạt bên ngoài
            Border emptyBorder = BorderFactory.createEmptyBorder(10, 15, 10, 15);
            Border lineBorder = BorderFactory.createEtchedBorder(); // Viền khắc
            setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));

            setBackground(new Color(245, 245, 245)); // Màu xám rất nhạt

            // Tiêu đề (chữ nhỏ, màu xám)
            lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
            lblTitle.setForeground(Color.DARK_GRAY);

            // Giá trị (chữ to, in đậm, màu sắc tùy chọn)
            lblValue = new JLabel(initialValue);
            lblValue.setFont(new Font("Arial", Font.BOLD, 24));
            lblValue.setForeground(valueColor);
            lblValue.setHorizontalAlignment(SwingConstants.LEFT);

            add(lblTitle, BorderLayout.NORTH);
            add(lblValue, BorderLayout.CENTER);
        }

        /**
         * Hàm công khai để cập nhật giá trị của card
         * @param newValue Giá trị mới
         */
        public void setValue(String newValue) {
            lblValue.setText(newValue);
        }

        /**
         * Hàm công khai để cập nhật giá trị (dạng số)
         * @param newValue Giá trị số mới
         * @param format Tiền tố/hậu tố (ví dụ: " VNĐ" hoặc " vé")
         */
        public void setValue(long newValue, String format) {
            lblValue.setText(String.format("%,d%s", newValue, format));
        }
    }
