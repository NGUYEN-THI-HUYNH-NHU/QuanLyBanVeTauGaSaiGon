package gui.application;

/*
 * @(#) MobileScannerJava.java  1.0  [8:48:01 PM] Dec 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 9, 2025
 * @version: 1.0
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dao.Ve_DAO;
import entity.Ve;
import entity.type.TrangThaiVe;

public class MobileScannerServer {
	private HttpServer server;
	private Ve_DAO veDAO;

	public MobileScannerServer() {
		veDAO = new Ve_DAO();
	}

	public void startServer() {
		try {
			// Cổng 8888 (Khớp với Ngrok)
			server = HttpServer.create(new InetSocketAddress(8080), 0);

			// Trang chủ: Chỉ hiện Form Upload đơn giản
			server.createContext("/", new IndexHandler());

			// Xử lý Upload: Nhận Form Data
			server.createContext("/upload", new UploadHandler());

			server.setExecutor(null);
			server.start();
			System.out.println(">>> Server Mobile (HTML Form Mode) đang chạy port 8888...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	// --- GIAO DIỆN: HTML FORM THUẦN TÚY (KHÔNG JS PHỨC TẠP) ---
	static class IndexHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			// Lấy thông báo lỗi/thành công từ URL (nếu có)
			String query = t.getRequestURI().getQuery();
			String msg = "";
			String msgClass = "";

			if (query != null && query.contains("msg=")) {
				String rawMsg = query.split("msg=")[1];
				String decodedMsg = java.net.URLDecoder.decode(rawMsg, StandardCharsets.UTF_8);

				if (decodedMsg.startsWith("OK")) {
					msg = "✅ " + decodedMsg.substring(3); // Bỏ chữ OK_
					msgClass = "valid";
				} else {
					msg = "❌ " + decodedMsg;
					msgClass = "invalid";
				}
			}

			String response = "<!DOCTYPE html>" + "<html><head>" + "<meta charset='UTF-8'>"
					+ "<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'>"
					+ "<title>FORM SCANNER</title>" + "<style>"
					+ "body { font-family: sans-serif; padding: 20px; text-align: center; background: #eee; }"
					+ ".box { background: white; padding: 20px; border-radius: 10px; border: 1px solid #ccc; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }"
					+ "h2 { margin-top: 0; }" +

					// Input File To Rõ
					"input[type=file] { border: 2px dashed #007bff; padding: 20px; width: 90%; margin-bottom: 20px; background: #f8f9fa; border-radius: 8px; }"
					+

					// Nút Submit
					"input[type=submit] { background: #28a745; color: white; padding: 20px; width: 100%; font-size: 20px; font-weight: bold; border: none; border-radius: 8px; cursor: pointer; }"
					+

					".msg { padding: 15px; border-radius: 8px; font-weight: bold; margin-bottom: 20px; color: white; }"
					+ ".valid { background: #28a745; } .invalid { background: #dc3545; }" + "</style></head>" + "<body>"
					+

					"<h2>MÁY SOÁT VÉ</h2>" +

					// HIỆN THÔNG BÁO KẾT QUẢ (NẾU CÓ)
					(msg.isEmpty() ? "" : "<div class='msg " + msgClass + "'>" + msg + "</div>") +

					"<div class='box'>" +
					// FORM CHUẨN HTML - BROWSER TỰ LO VIỆC GỬI
					"<form method='POST' action='/upload' enctype='multipart/form-data'>"
					+ "<p><b>BƯỚC 1:</b> Chọn hoặc Chụp ảnh</p>"
					+ "<input type='file' name='file' accept='image/*' capture='environment' required>" +

					"<p><b>BƯỚC 2:</b> Bấm Gửi</p>" + "<input type='submit' value='🚀 GỬI ẢNH ĐI'>" + "</form>"
					+ "</div>" +

					"<p style='color:#666; font-size:12px'>Sử dụng công nghệ HTML Form Native.<br>Tương thích mọi thiết bị.</p>"
					+ "</body></html>";

			byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
			t.sendResponseHeaders(200, bytes.length);
			try (OutputStream os = t.getResponseBody()) {
				os.write(bytes);
			}
		}
	}

	// --- BACKEND: XỬ LÝ MULTIPART (CẮT DỮ LIỆU THỦ CÔNG) ---
	class UploadHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println(">> Đang nhận ảnh qua Form...");

			try {
				// 1. Đọc toàn bộ dữ liệu body
				InputStream is = t.getRequestBody();
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				int nRead;
				byte[] data = new byte[8192];
				while ((nRead = is.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}
				buffer.flush();
				byte[] bodyBytes = buffer.toByteArray();

				if (bodyBytes.length == 0) {
					redirect(t, "LOI: Khong nhan duoc du lieu");
					return;
				}

				System.out.println(">> Kích thước gói tin: " + bodyBytes.length + " bytes");

				// 2. Tìm vị trí bắt đầu của ảnh (Sau dòng trống \r\n\r\n đầu tiên)
				int imageStartIndex = -1;
				for (int i = 0; i < bodyBytes.length - 4; i++) {
					// Tìm sequence: 13, 10, 13, 10 (\r\n\r\n)
					if (bodyBytes[i] == 13 && bodyBytes[i + 1] == 10 && bodyBytes[i + 2] == 13
							&& bodyBytes[i + 3] == 10) {
						imageStartIndex = i + 4;
						break;
					}
				}

				if (imageStartIndex == -1) {
					redirect(t, "LOI: Dinh dang Multipart sai");
					return;
				}

				// 3. Cắt lấy phần dữ liệu ảnh (Từ sau header đến hết)
				// Lưu ý: ImageIO đủ thông minh để bỏ qua cái boundary ở cuối
				int imageLength = bodyBytes.length - imageStartIndex;
				ByteArrayInputStream imageStream = new ByteArrayInputStream(bodyBytes, imageStartIndex, imageLength);

				BufferedImage image = ImageIO.read(imageStream);

				if (image == null) {
					redirect(t, "LOI: File khong phai anh hop le");
					return;
				}

				// 4. Soi QR
				System.out.println(">> Đang soi ảnh...");
				String decodedText = null;
				try {
					BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
					BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
					Result result = new MultiFormatReader().decode(bitmap);
					decodedText = result.getText();
					System.out.println(">>> TÌM THẤY QR: " + decodedText);
				} catch (Exception e) {
					System.out.println(">>> Không tìm thấy QR.");
				}

				// 5. Xử lý kết quả
				String msg = "";
				if (decodedText != null) {
					String veID = extractIdFlexible(decodedText);
					if (veID != null && !veID.isEmpty()) {
						Ve ve = veDAO.getVeByVeID(veID);
						if (ve == null) {
							msg = "Ve khong ton tai: " + veID;
						} else {
							TrangThaiVe status = ve.getTrangThai();
							String tenKhach = ve.getKhachHang().getHoTen();
							if (status == TrangThaiVe.DA_DUNG) {
								msg = "VE DA DUNG (" + tenKhach + ")";
							} else if (status == TrangThaiVe.DA_HOAN || status == TrangThaiVe.DA_DOI) {
								msg = "VE DA HUY (" + tenKhach + ")";
							} else {
								veDAO.updateTrangThaiVe(veID, TrangThaiVe.DA_DUNG);
								msg = "OK_HOP LE: " + tenKhach; // Bắt đầu bằng OK_ để báo thành công
							}
						}
					} else {
						msg = "QR khong dung dinh dang";
					}
				} else {
					msg = "Anh mo hoac khong co QR";
				}

				// Chuyển hướng lại trang chủ kèm thông báo
				redirect(t, msg);

			} catch (Exception e) {
				e.printStackTrace();
				redirect(t, "LOI Server: " + e.getMessage());
			}
		}

		private void redirect(HttpExchange t, String msg) throws IOException {
			// Encode tin nhắn để an toàn trên URL
			String encodedMsg = java.net.URLEncoder.encode(msg, StandardCharsets.UTF_8);

			// Redirect code 303 (See Other) về trang chủ
			t.getResponseHeaders().set("Location", "/?msg=" + encodedMsg);
			t.sendResponseHeaders(303, -1);
		}
	}

	private String extractIdFlexible(String input) {
		if (input == null) {
			return null;
		}
		String data = input.trim();
		if (data.contains("\"id\"")) {
			try {
				int start = data.indexOf("\"id\"") + 4;
				while (start < data.length()
						&& (data.charAt(start) == ':' || data.charAt(start) == '"' || data.charAt(start) == ' ')) {
					start++;
				}
				int end = data.indexOf("\"", start);
				if (end > start) {
					return data.substring(start, end);
				}
			} catch (Exception e) {
			}
		}
		if (!data.contains("{")) {
			return data;
		}
		return null;
	}
}