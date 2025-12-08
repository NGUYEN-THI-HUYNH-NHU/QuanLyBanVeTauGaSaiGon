package gui.application.paymenthelper;
/*
 * @(#) CassoWebhookServer.java  1.0  [4:27:14 PM] Dec 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 7, 2025
 * @version: 1.0
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CassoWebhookServer {

	private HttpServer server;
	private static final int PORT = 8080; // Trùng với cổng chạy Ngrok
	private OnTransactionListener listener;

	public interface OnTransactionListener {
		void onTransactionSuccess(String orderCode, float amount);
	}

	public boolean startServer(OnTransactionListener listener) {
		// Tắt server cũ nếu có
		stopServer();

		this.listener = listener;
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 0);
			server.createContext("/casso-handler", new WebhookHandler());
			server.setExecutor(null);
			server.start();
			System.out.println(">> Casso Webhook Server đang chạy tại port " + PORT);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("LỖI: Không thể mở cổng " + PORT + ". Đang có ứng dụng khác chiếm dụng!");
			return false;
		}
	}

	public void stopServer() {
		if (server != null) {
			server.stop(0);
			System.out.println(">> Đã tắt Server.");
		}
	}

	class WebhookHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			// 1. Chỉ nhận phương thức POST
			if (!"POST".equalsIgnoreCase(t.getRequestMethod())) {
				t.sendResponseHeaders(405, -1);
				return;
			}

			// 2. Đọc dữ liệu JSON mà Casso gửi sang
			InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder requestBody = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				requestBody.append(line);
			}

			String jsonLog = requestBody.toString();
			System.out.println(">> Nhận dữ liệu từ Bank: " + jsonLog);

			// 3. Phản hồi cho Casso biết đã nhận (để nó không gửi lại)
			String response = "{\"error\":0, \"message\":\"Success\"}";
			t.getResponseHeaders().set("Content-Type", "application/json");
			t.sendResponseHeaders(200, response.length());
			try (OutputStream os = t.getResponseBody()) {
				os.write(response.getBytes());
			}

			if (listener != null) {
				listener.onTransactionSuccess(jsonLog, 0);
			}
		}
	}
}