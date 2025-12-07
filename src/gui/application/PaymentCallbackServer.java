package gui.application;
/*
 * @(#) PaymentCallbackServer.java  1.0  [5:12:27 PM] Dec 5, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 5, 2025
 * @version: 1.0
 */
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class PaymentCallbackServer {

	private HttpServer server;
	private static final int PORT = 9876; // Chọn cổng ít trùng (8080 hay bị trùng)
	private PaymentListener listener;

	// Interface để báo ngược lại cho Controller
	public interface PaymentListener {
		void onPaymentSuccess(String transactionId);

		void onPaymentFail();
	}

	public void startServer(PaymentListener listener) {
		this.listener = listener;
		try {
			// Khởi tạo Server tại http://localhost:9876/
			server = HttpServer.create(new InetSocketAddress(PORT), 0);

			// Tạo đường dẫn nhận kết quả
			server.createContext("/vnpay-result", new ResultHandler());

			server.setExecutor(null);
			server.start();
			System.out.println("Payment Server đang lắng nghe tại port " + PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		if (server != null) {
			server.stop(0);
			System.out.println("Payment Server đã tắt.");
		}
	}

	// Class xử lý yêu cầu HTTP
	class ResultHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			// 1. Lấy Query String (dữ liệu VNPAY trả về)
			String query = t.getRequestURI().getQuery();
			System.out.println("Callback Query: " + query);

			// 2. Phân tích kết quả (vnp_ResponseCode = 00 là thành công)
			boolean isSuccess = query != null && query.contains("vnp_ResponseCode=00");

			// 3. Phản hồi giao diện Web cho người dùng thấy
			String responseHTML = "<html><head><meta charset='UTF-8'><title>Kết quả thanh toán</title></head>"
					+ "<body style='text-align:center; font-family: Arial; margin-top: 50px;'>"
					+ (isSuccess
							? "<h1 style='color:green'>Thanh toán THÀNH CÔNG!</h1><p>Bạn có thể đóng tab này và quay lại ứng dụng.</p>"
							: "<h1 style='color:red'>Thanh toán THẤT BẠI.</h1><p>Vui lòng thử lại.</p>")
					+ "<script>setTimeout(function(){window.close()}, 3000);</script>" // Tự động đóng tab sau 3s (nếu
																						// trình duyệt cho phép)
					+ "</body></html>";

			t.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
			t.sendResponseHeaders(200, responseHTML.getBytes(StandardCharsets.UTF_8).length);
			try (OutputStream os = t.getResponseBody()) {
				os.write(responseHTML.getBytes(StandardCharsets.UTF_8));
			}

			// 4. Báo về Java Swing (Logic xử lý chính)
			if (isSuccess) {
				// Lấy mã giao dịch nếu cần (parse từ query)
				listener.onPaymentSuccess("MãGD_Demo");
			} else {
				listener.onPaymentFail();
			}

			// Tắt server sau khi nhận được tin (chạy trên luồng khác để kịp trả response về
			// web đã)
			new Thread(() -> {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				stopServer();
			}).start();
		}
	}

	// Hàm tiện ích để lấy URL Callback chuẩn
	public static String getCallbackUrl() {
		return "http://localhost:" + PORT + "/vnpay-result";
	}
}