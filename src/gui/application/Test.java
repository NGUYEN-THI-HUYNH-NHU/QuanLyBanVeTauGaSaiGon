package gui.application;
/*
 * @(#) Test.java  1.0  [4:41:18 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

//Dua cac panel vao day test cho tien
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Test {

	public static void main(String[] args) {
		// Tạo cửa sổ JFrame
		JFrame frame = new JFrame("Open Link Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 200);

		// Tạo JLabel với liên kết
		JLabel label = new JLabel("<html><a href=''>Click here to visit OpenAI</a></html>");

		// Thiết lập màu chữ và con trỏ khi hover
		label.setForeground(Color.BLUE);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		// Thêm ActionListener để mở website khi click vào liên kết
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					// Mở trình duyệt và điều hướng đến URL
					URI uri = new URI("https://www.openai.com");
					Desktop.getDesktop().browse(uri); // Mở URL trong trình duyệt mặc định
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		// Thêm JLabel vào JFrame
		frame.add(label, BorderLayout.CENTER);

		// Hiển thị cửa sổ
		frame.setVisible(true);
	}
}
