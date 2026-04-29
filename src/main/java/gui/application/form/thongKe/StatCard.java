package gui.application.form.thongKe;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Component Card hiển thị 1 chỉ số thống kê.
 */
public class StatCard extends JPanel {

    private JLabel lblTitle;
    private JLabel lblValue;

    public StatCard(String title, String initialValue, Color valueColor) {
        setLayout(new BorderLayout(5, 5));

        Border padding = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        Border border = BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                padding
        );
        setBorder(border);
        setBackground(new Color(245, 245, 245));

        lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.DARK_GRAY);

        lblValue = new JLabel(initialValue);
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(valueColor);

        add(lblTitle, BorderLayout.NORTH);
        add(lblValue, BorderLayout.CENTER);
    }

    /** Set text value */
    public void setValue(String text) {
        lblValue.setText(text);
    }

    /** Lấy giá trị dạng số (xóa ký tự không phải số) */
    public double getNumericValue() {
        // Xóa hết ký tự không phải số
        String raw = lblValue.getText().replaceAll("[^\\d]", "");
        if (raw.isEmpty()) return 0;
        return Double.parseDouble(raw);
    }
}