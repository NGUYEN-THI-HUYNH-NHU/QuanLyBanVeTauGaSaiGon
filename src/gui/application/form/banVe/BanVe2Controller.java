package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2Controller.java  1.0  [12:05:37 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import bus.BanVe_BUS;
import bus.KhuyenMai_BUS;
import entity.GiaoDichThanhToan;
import entity.type.LoaiDoiTuong;
import gui.application.CassoWebhookServer;
import gui.application.VietQRService;

/**
 * Controller (Mediator) cho PanelBanVe2. Nhiệm vụ: 1. Lấy dữ liệu từ
 * BookingSession. 2. Đổ dữ liệu vào PanelBuoc4 (Xác nhận) và PanelBuoc5 (Chi
 * tiết giá). 3. Lắng nghe sự kiện "Xác nhận Thanh toán" từ PanelBuoc5. 4. Báo
 * cho Wizard (PanelBanVe) khi thanh toán hoàn tất.
 */
public class BanVe2Controller {
	private final PanelBanVe2 view;
	private final PanelBuoc4 p4;
	private final PanelBuoc5 p5;

	private final BanVe_BUS banVeBUS = new BanVe_BUS();
	private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();

	private final BookingSession bookingSession;

	// Listener để báo cho wizard chính (PanelBanVe) biết
	private Runnable onPanel2ReturnListener;
	private Runnable onPaymentSuccessListener;

	private CassoWebhookServer cassoServer;

	public void addPanel2ReturnListener(Runnable listener) {
		this.onPanel2ReturnListener = listener;
	}

	public void addPanel2PaymentSuccessListener(Runnable listener) {
		this.onPaymentSuccessListener = listener;
	}

	public BanVe2Controller(PanelBanVe2 view, BookingSession session) {
		this.view = view;
		this.bookingSession = session;

		this.p4 = view.getPanelBuoc4();
		this.p5 = view.getPanelBuoc5();

		this.view.getBtnPrev().addActionListener(e -> {
			if (onPanel2ReturnListener != null) {
				onPanel2ReturnListener.run();
			}
		});

		this.p4.setKhuyenMaiProvider((veSession) -> {
			return khuyenMaiBUS.getDanhSachKhuyenMaiPhuHop(veSession);
		});

		this.p4.addTableUpdateListener((e) -> {
			updatePaymentInfo();
		});

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	private void updatePaymentInfo() {
		int tongTienVe = 0;
		double giamGiaDT = 0;
		int khuyenMai = 0;
		int dichVu = 0;

		List<VeSession> allTickets = bookingSession.getAllSelectedTickets();

		for (VeSession ve : allTickets) {
			tongTienVe += ve.getVe().getGia();
			dichVu += ve.getPhiPhieuDungPhongChoVIP();
			khuyenMai += ve.getGiamKM();

			// (Logic giảm đối tượng giữ nguyên)
			if (ve.getVe().getKhachHang().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				ve.setGiamDoiTuong((int) (Math.round((ve.getVe().getGia() * 0.25) / 1000) * 1000));
				giamGiaDT += ve.getGiamDoiTuong();
			}
		}

		// Cập nhật lại UI PanelBuoc5
		p5.setChiTietThanhToan(tongTienVe, (int) giamGiaDT, khuyenMai, dichVu);
	}

	/**
	 * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
	 */
	public void loadDataForConfirmation() {
		p4.setComponentsEnabled(true);
		p5.setComponentsEnabled(true);

		p4.hienThiThongTin(bookingSession);

		updatePaymentInfo();
	}

	/**
	 * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
	 */
	private void initMediatorLogic() {
		// Lắng nghe nút thanh toán từ PanelBuoc5
		JButton payButtonCash = p5.getBtnXacNhanVaInCash();
		JButton payButtonQR = p5.getBtnXacNhanVaInQR();

		ActionListener paymentListener = e -> {
			boolean isThanhToanTienMat = p5.isThanhToanTienMat();
			double tongTien = p5.getTongThanhToan();

			GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
			giaoDich.setTongTien(tongTien);
			giaoDich.setThanhToanTienMat(isThanhToanTienMat);

			if (!isThanhToanTienMat) {
				handleVietQRPayment(tongTien);
			} else {
				double tienNhan = p5.getTienKhachDua();
				double tienHoan = tienNhan - tongTien;

				giaoDich.setTongTien(tongTien);
				giaoDich.setTienNhan(tienNhan);
				giaoDich.setTienHoan(tienHoan);

				processPaymentAndSave(giaoDich);
			}
		};

		if (payButtonCash != null) {
			payButtonCash.addActionListener(paymentListener);
		}
		if (payButtonQR != null) {
			payButtonQR.addActionListener(paymentListener);
		}
	}

	private void handleVietQRPayment(double tongTien) {
		// 1. Tạo mã giao dịch (Chỉ chữ và số để tránh lỗi)
		String maGiaoDich = "VETAU" + System.currentTimeMillis();
		String noiDungCK = "TT " + maGiaoDich;

		System.out.println("--- BẮT ĐẦU THANH TOÁN QR ---");
		System.out.println("Mã mong đợi: " + maGiaoDich);

		// Cờ để tránh xử lý 2 lần
		final boolean[] isProcessed = { false };
		final GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
		giaoDich.setTongTien(tongTien);
		giaoDich.setThanhToanTienMat(false);

		// Khởi tạo Server lắng nghe
		cassoServer = new CassoWebhookServer();

		// 2. KHỞI CHẠY SERVER LẮNG NGHE WEBHOOK TỪ CASSO
		boolean isServerStarted = cassoServer.startServer(new CassoWebhookServer.OnTransactionListener() {
			@Override
			public void onTransactionSuccess(String jsonLog, float amount) {
				System.out.println(">> SERVER ĐÃ NHẬN TIN TỪ CASSO!");
				System.out.println("LOG BANK: " + jsonLog);

				// Chuẩn hóa chuỗi (Biến tất cả thành chữ hoa, chỉ giữ lại chữ và số)
				String cleanLog = jsonLog.toUpperCase().replaceAll("[^A-Z0-9]", "");
				String cleanMa = maGiaoDich.toUpperCase().replaceAll("[^A-Z0-9]", "");

				// Kiểm tra
				if (!isProcessed[0] && cleanLog.contains(cleanMa)) {
					System.out.println(">> KHỚP MÃ! TIỀN VỀ!");
					isProcessed[0] = true;
					cassoServer.stopServer(); // Tắt server ngay

					SwingUtilities.invokeLater(() -> {
						closePaymentDialog();

						JOptionPane.showMessageDialog(view,
								"ĐÃ NHẬN ĐƯỢC TIỀN! (" + String.format("%,.0f", tongTien)
										+ " VNĐ)\nHệ thống đang xuất vé...",
								"Thanh toán thành công", JOptionPane.INFORMATION_MESSAGE);

						giaoDich.setTienNhan(tongTien);
						processPaymentAndSave(giaoDich);
					});
				} else {
					System.out.println(">> Có tin nhắn nhưng không khớp mã hoặc đã xử lý rồi.");
				}
			}
		});

		// NẾU KHÔNG BẬT ĐƯỢC SERVER -> DỪNG NGAY
		if (!isServerStarted) {
			JOptionPane.showMessageDialog(view,
					"Lỗi hệ thống: Cổng kết nối 8080 đang bận.\nVui lòng tắt các chương trình Java khác và thử lại!",
					"Lỗi khởi tạo", JOptionPane.ERROR_MESSAGE);
			p5.setComponentsEnabled(true);
			return;
		}

		// 3. Tạo và hiển thị QR Code
		VietQRService qrService = new VietQRService();
		String qrUrl = qrService.generateQRUrl(tongTien, noiDungCK);

		new SwingWorker<ImageIcon, Void>() {
			@Override
			protected ImageIcon doInBackground() throws Exception {
				return qrService.getQRCodeImage(qrUrl);
			}

			@Override
			protected void done() {
				try {
					ImageIcon icon = get();
					if (icon != null) {
						// Tạo giao diện Dialog chờ
						JLabel lblImage = new JLabel(icon);
						JLabel lblNote = new JLabel("<html><div style='text-align:center; width: 350px;'>"
								+ "<b style='font-size:16px; color:#0056b3'>QUÉT MÃ ĐỂ THANH TOÁN</b><br/><br/>"
								+ "Số tiền: <b style='color:red; font-size:14px'>" + String.format("%,.0f", tongTien)
								+ " VNĐ</b><br/>" + "Nội dung: <b style='color:green'>" + noiDungCK + "</b><br/><br/>"
								+ "<i>(Vui lòng không tắt bảng này, hệ thống sẽ tự động xác nhận...)</i>"
								+ "</div></html>");
						lblNote.setHorizontalAlignment(JLabel.CENTER);

						JPanel panel = new JPanel(new BorderLayout());
						panel.add(lblNote, BorderLayout.NORTH);
						panel.add(lblImage, BorderLayout.CENTER);

						// CHỈ HIỆN NÚT HỦY (Vì đang chờ tự động)
						Object[] options = { "Hủy bỏ thanh toán" };
						int result = JOptionPane.showOptionDialog(view, panel, "Đang chờ thanh toán...",
								JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

						// Nếu người dùng bấm Hủy
						if (result == 0 || result == JOptionPane.CLOSED_OPTION) {
							if (!isProcessed[0]) {
								System.out.println(">> Khách đã bấm Hủy.");
								cassoServer.stopServer(); // Tắt server giải phóng cổng
								p5.setComponentsEnabled(true);
							}
						}
					} else {
						JOptionPane.showMessageDialog(view, "Không tải được mã QR. Kiểm tra mạng.");
						cassoServer.stopServer();
						p5.setComponentsEnabled(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (cassoServer != null) {
						cassoServer.stopServer();
					}
					p5.setComponentsEnabled(true);
				}
			}
		}.execute();
	}

	private void closePaymentDialog() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof JDialog && window.isVisible()) {
				window.dispose();
			}
		}
	}

	/**
	 * Hàm chung để thực hiện lưu giao dịch và in vé Được gọi khi: 1. Thanh toán
	 * tiền mặt xong. 2. Web Server nhận được tín hiệu VNPAY thành công. 3. Khách
	 * bấm xác nhận thủ công.
	 */
	private void processPaymentAndSave(GiaoDichThanhToan giaoDich) {
		bookingSession.setGiaoDichThanhToan(giaoDich);

		// Vô hiệu hóa nút để tránh bấm nhiều lần
		p5.setComponentsEnabled(false);

		// Thực thi giao dịch trong SwingWorker
		new SwingWorker<Boolean, Void>() {
			private String errorMessage = "Lỗi không xác định";

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					return banVeBUS.thucHienBanVe(bookingSession);
				} catch (Exception ex) {
					errorMessage = ex.getMessage();
					ex.printStackTrace();
					return false;
				}
			}

			@Override
			protected void done() {
				try {
					boolean saveSuccess = get();
					if (saveSuccess) {
						// Thông báo thành công
						JOptionPane.showMessageDialog(view, "Bán vé thành công!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
						p4.setComponentsEnabled(false);
						p5.setComponentsEnabled(false);

						// Báo cho wizard chính (PanelBanVe) biết để reset hoặc chuyển trang
						if (onPaymentSuccessListener != null) {
							onPaymentSuccessListener.run();
						}
					} else {
						JOptionPane.showMessageDialog(view, "Lỗi khi lưu dữ liệu!\n" + errorMessage, "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						p5.setComponentsEnabled(true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
					p5.setComponentsEnabled(true);
				}
			}
		}.execute();
	}
}