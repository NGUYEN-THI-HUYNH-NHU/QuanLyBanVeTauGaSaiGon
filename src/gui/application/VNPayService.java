package gui.application;
/*
 * @(#) VNPayService.java  1.0  [4:20:10 PM] Dec 5, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 5, 2025
 * @version: 1.0
 */

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayService {

	private final String VNP_PAY_URL;
	private final String VNP_TMN_CODE;
	private final String VNP_HASH_SECRET;
	private final String VNP_RETURN_URL;

	public VNPayService() {
		ConfigManager config = ConfigManager.getInstance();
		this.VNP_PAY_URL = config.getProperty("vnpay.url");
		this.VNP_TMN_CODE = config.getProperty("vnpay.tmn_code");
		this.VNP_HASH_SECRET = config.getProperty("vnpay.hash_secret");
		this.VNP_RETURN_URL = config.getProperty("vnpay.return_url", "https://google.com");
	}

	/**
	 * Tạo URL thanh toán VNPAY
	 * 
	 * @param orderInfo Nội dung thanh toán (Tiếng Việt không dấu)
	 * @param orderCode Mã đơn hàng
	 * @param amount    Số tiền (VNĐ)
	 * @return URL thanh toán
	 */
	public String createPaymentUrl(String orderInfo, String orderCode, double amount) {
		try {
			String vnp_Version = "2.1.0";
			String vnp_Command = "pay";
			String vnp_TxnRef = orderCode;
			String vnp_IpAddr = "127.0.0.1"; // IP khách hàng (với App Desktop thì để localhost)
			String vnp_TmnCode = VNP_TMN_CODE;

			// LƯU Ý QUAN TRỌNG: Số tiền bên VNPAY phải nhân 100
			// Ví dụ: 10.000 VNĐ -> Gửi lên là 1000000
			long amountVal = (long) (amount * 100);

			Map<String, String> vnp_Params = new HashMap<>();
			vnp_Params.put("vnp_Version", vnp_Version);
			vnp_Params.put("vnp_Command", vnp_Command);
			vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
			vnp_Params.put("vnp_Amount", String.valueOf(amountVal));
			vnp_Params.put("vnp_CurrCode", "VND");

			// Nếu bạn muốn tích hợp Bank cụ thể (ví dụ NCB), bỏ comment dòng dưới
			// vnp_Params.put("vnp_BankCode", "NCB");

			vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
			vnp_Params.put("vnp_OrderInfo", orderInfo);
			vnp_Params.put("vnp_OrderType", "other");
			vnp_Params.put("vnp_Locale", "vn");
			vnp_Params.put("vnp_ReturnUrl", VNP_RETURN_URL);
			vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

			// Tạo ngày giờ hiện tại
			Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String vnp_CreateDate = formatter.format(cld.getTime());
			vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

			// Hết hạn sau 15 phút
			cld.add(Calendar.MINUTE, 15);
			String vnp_ExpireDate = formatter.format(cld.getTime());
			vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

			// --- BƯỚC TẠO CHECKSUM (QUAN TRỌNG NHẤT) ---
			// 1. Sắp xếp tham số theo a-z
			List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
			Collections.sort(fieldNames);

			StringBuilder hashData = new StringBuilder();
			StringBuilder query = new StringBuilder();

			Iterator<String> itr = fieldNames.iterator();
			while (itr.hasNext()) {
				String fieldName = itr.next();
				String fieldValue = vnp_Params.get(fieldName);
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					// Build hash data
					hashData.append(fieldName);
					hashData.append('=');
					hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

					// Build query url
					query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
					query.append('=');
					query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

					if (itr.hasNext()) {
						query.append('&');
						hashData.append('&');
					}
				}
			}

			String queryUrl = query.toString();
			// Tạo Secure Hash bằng thuật toán HMAC-SHA512
			String vnp_SecureHash = hmacSHA512(VNP_HASH_SECRET, hashData.toString());
			queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

			return VNP_PAY_URL + "?" + queryUrl;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Hàm mã hóa HMAC-SHA512
	private String hmacSHA512(String key, String data) {
		try {
			if (key == null || data == null) {
				throw new NullPointerException();
			}
			final Mac hmac512 = Mac.getInstance("HmacSHA512");
			byte[] hmacKeyBytes = key.getBytes();
			final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
			hmac512.init(secretKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] result = hmac512.doFinal(dataBytes);

			StringBuilder sb = new StringBuilder(2 * result.length);
			for (byte b : result) {
				sb.append(String.format("%02x", b & 0xff));
			}
			return sb.toString();
		} catch (Exception ex) {
			return "";
		}
	}

	// Hàm mở trình duyệt
	public void openWebpage(String urlString) {
		try {
			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				Desktop.getDesktop().browse(new URI(urlString));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}