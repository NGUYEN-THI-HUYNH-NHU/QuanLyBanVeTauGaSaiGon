package gui.application;
/*
 * @(#) AboutUsHelper.java  1.0  [7:17:32 PM] Nov 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 25, 2025
 * @version: 1.0
 */

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AboutUsHelper {

	public static void openAboutUs() {
		try {
			// 1. Lấy file từ trong resources của file JAR
			// Giả sử file about-us.html nằm ngay trong thư mục gốc của resources
			InputStream inputStream = AboutUsHelper.class.getClassLoader().getResourceAsStream("about-us.html");

			if (inputStream == null) {
				System.out.println("Không tìm thấy file about.html!");
				return;
			}

			// 2. Tạo một file tạm thời (temp file) trên máy tính người dùng
			File tempFile = File.createTempFile("about_us", ".html");
			tempFile.deleteOnExit(); // Tự động xóa file khi tắt chương trình Java (tùy chọn)

			// 3. Copy nội dung từ file trong JAR ra file tạm
			try (OutputStream outputStream = new FileOutputStream(tempFile)) {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}

			// 4. Dùng Desktop API để mở file bằng trình duyệt mặc định
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(tempFile.toURI());
			} else {
				System.out.println("Hệ thống không hỗ trợ mở trình duyệt.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}