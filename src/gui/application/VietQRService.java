package gui.application;
/*
 * @(#) VietQRService.java  1.0  [5:40:47 PM] Dec 5, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 5, 2025
 * @version: 1.0
 */
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class VietQRService {
	// Mã ngân hàng (Tra cứu tại: https://api.vietqr.io/v2/banks)
	// Ví dụ: MB, VCB (Vietcombank), TCB (Techcombank), ACB, BIDV, v.v.
	private static final String MY_BANK_CODE = "ICB";
	// Số tài khoản
	private static final String MY_ACCOUNT_NUMBER = "107879609064";
	// Giao diện QR: "compact" (gọn), "print" (đầy đủ), "qr_only" (chỉ mã)
	private static String TEMPLATE;

	/**
	 * Hàm tạo URL ảnh QR Code theo chuẩn VietQR
	 * 
	 * @param amount  Số tiền cần thanh toán
	 * @param content Nội dung chuyển khoản (Nên viết không dấu)
	 * @return Đường dẫn ảnh QR
	 */
	public String generateQRUrl(double amount, String content, String template) {
		try {
			String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
			long amountInt = (long) amount;

			// Template được truyền vào thay vì cố định
			TEMPLATE = template;
			return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s", MY_BANK_CODE,
					MY_ACCOUNT_NUMBER, TEMPLATE, amountInt, encodedContent);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Giữ lại hàm cũ để tương thích (mặc định là compact)
	public String generateQRUrl(double amount, String content) {
		return generateQRUrl(amount, content, "compact");
	}

	/**
	 * Hàm tải ảnh từ URL về để hiển thị lên Swing
	 * 
	 * @param qrUrl Đường dẫn ảnh lấy từ hàm generateQRUrl
	 * @return ImageIcon để gán vào JLabel
	 */
	public ImageIcon getQRCodeImage(String qrUrl) {
		try {
			URL url = new URL(qrUrl);
			BufferedImage image = ImageIO.read(url);

			if (image != null) {
				// Resize ảnh cho vừa mắt (nếu cần), ví dụ 400x400
				Image scaledImage = image.getScaledInstance(400, 400, Image.SCALE_SMOOTH);
				return new ImageIcon(scaledImage);
			}
		} catch (Exception e) {
			System.err.println("Không thể tải ảnh QR: " + e.getMessage());
		}
		return null;
	}
}