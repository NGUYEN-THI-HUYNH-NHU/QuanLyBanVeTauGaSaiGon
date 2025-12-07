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

	// --- CẤU HÌNH TÀI KHOẢN NHẬN TIỀN CỦA BẠN (TIỀN THẬT) ---
	// Mã ngân hàng (Tra cứu tại: https://api.vietqr.io/v2/banks)
	// Ví dụ: MB, VCB (Vietcombank), TCB (Techcombank), ACB, BIDV, v.v.
	private static final String MY_BANK_CODE = "ICB";

	// Số tài khoản thật của bạn
	private static final String MY_ACCOUNT_NUMBER = "107879609064";

	// Giao diện QR: "compact" (gọn), "print" (đầy đủ), "qr_only" (chỉ mã)
	private static final String TEMPLATE = "compact";

	/**
	 * Hàm tạo URL ảnh QR Code theo chuẩn VietQR
	 * 
	 * @param amount  Số tiền cần thanh toán
	 * @param content Nội dung chuyển khoản (Nên viết không dấu)
	 * @return Đường dẫn ảnh QR
	 */
	public String generateQRUrl(double amount, String content) {
		try {
			// 1. Xử lý nội dung: Encode URL để tránh lỗi ký tự đặc biệt/dấu cách
			// Ví dụ: "THANH TOAN VE" -> "THANH%20TOAN%20VE"
			String encodedContent = URLEncoder.encode(content, StandardCharsets.UTF_8.toString());

			// 2. Định dạng số tiền về số nguyên (VietQR không nhận số lẻ)
			long amountInt = (long) amount;

			// 3. Ghép chuỗi URL theo cấu trúc VietQR QuickLink
			// Format:
			// https://img.vietqr.io/image/<BANK>-<ACC>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<CONTENT>
			String url = String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s", MY_BANK_CODE,
					MY_ACCOUNT_NUMBER, TEMPLATE, amountInt, encodedContent);

			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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