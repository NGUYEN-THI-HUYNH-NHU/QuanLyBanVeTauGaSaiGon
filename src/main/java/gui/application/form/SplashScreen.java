/**
 * File: SplashScreen.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/5/2026
 */

package gui.application.form;

import gui.application.UngDung;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Getter
@Setter
public class SplashScreen extends JWindow {
    private final int TARGET_WIDTH = 560;
    private final int TARGET_HEIGHT = 385;
    private JProgressBar progressBar;
    private JLabel lblStatus;

    public SplashScreen() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // 1. Cấu hình Label trạng thái
        lblStatus = new JLabel(" Đang khởi động hệ thống...");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 10));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setOpaque(false);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        // 2. Thêm ảnh Splash Screen và lồng lblStatus vào trong ảnh
        URL imgURL = UngDung.class.getResource("/icon/png/splash-screen.png");
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(TARGET_WIDTH, TARGET_HEIGHT, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(lblStatus, BorderLayout.SOUTH);

        contentPane.add(imageLabel, BorderLayout.CENTER);

        // 3. Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 95, 159));
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(TARGET_WIDTH, 12));

        contentPane.add(progressBar, BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
