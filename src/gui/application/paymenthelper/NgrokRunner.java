package gui.application.paymenthelper;
/*
 * @(#) NgrokRunner.java  1.0  [3:25:31 PM] Dec 8, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 8, 2025
 * @version: 1.0
 */

import java.io.IOException;

public class NgrokRunner {

	private static Process ngrokProcess;

	// Thay thế bằng domain tĩnh của bạn
	private static final String NGROK_DOMAIN = "pricily-postdevelopmental-noah.ngrok-free.dev";
	private static final String PORT = "8080";

	/**
	 * Hàm khởi động Ngrok chạy ngầm
	 */
	public static void startNgrok() {
		try {
			// Kiểm tra xem đã chạy chưa để tránh chạy trùng
			if (ngrokProcess != null && ngrokProcess.isAlive()) {
				System.out.println(">> Ngrok đã đang chạy rồi.");
				return;
			}

			System.out.println(">> Đang khởi động Ngrok Tunnel...");

			// Lệnh chạy: ngrok http --domain=xxx 8080
			// Lưu ý: "ngrok.exe" giả định file này nằm ở thư mục gốc dự án
			ProcessBuilder builder = new ProcessBuilder("ngrok.exe", "http", "--domain=" + NGROK_DOMAIN, PORT);

			// Tự động tắt Ngrok khi tắt App Java (Quan trọng)
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				stopNgrok();
			}));

			// Bắt đầu chạy
			ngrokProcess = builder.start();
			System.out.println(">> Ngrok đã khởi động thành công (Chạy ngầm).");

		} catch (IOException e) {
			System.err.println("LỖI: Không tìm thấy file ngrok.exe hoặc lỗi khởi chạy!");
			System.err.println("Vui lòng copy file ngrok.exe vào thư mục gốc của dự án.");
			e.printStackTrace();
		}
	}

	/**
	 * Hàm tắt Ngrok thủ công (nếu cần)
	 */
	public static void stopNgrok() {
		if (ngrokProcess != null) {
			ngrokProcess.destroy(); // Giết tiến trình
			System.out.println(">> Đã tắt Ngrok.");
		}
	}
}