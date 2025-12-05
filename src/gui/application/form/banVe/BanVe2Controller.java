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
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import bus.BanVe_BUS;
import bus.KhuyenMai_BUS;
import entity.GiaoDichThanhToan;
import entity.type.LoaiDoiTuong;
import gui.application.VNPayService;

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

	private final VNPayService vnpayService = new VNPayService(); // Khởi tạo

	private String currentTokenNL = null;

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
			// 1. Lấy thông tin thanh toán từ View
			boolean isThanhToanTienMat = p5.isThanhToanTienMat();
			double tongTien = p5.getTongThanhToan();

			GiaoDichThanhToan giaoDichThanhToan = new GiaoDichThanhToan();
			giaoDichThanhToan.setTongTien(tongTien);
			giaoDichThanhToan.setThanhToanTienMat(isThanhToanTienMat);

			if (!isThanhToanTienMat) {
				String maGiaoDich = "VETAUF4" + System.currentTimeMillis();

				try {
					// A. GỌI API VNPay
					// Nội dung thanh toán (Không dấu để tránh lỗi font)
					String noiDung = "Thanh toan ve tau " + maGiaoDich;
					// 1. Lấy URL
					String checkoutUrl = vnpayService.createPaymentUrl(noiDung, maGiaoDich, tongTien);

					if (checkoutUrl != null) {
						// Mở web thanh toán
						vnpayService.openWebpage(checkoutUrl);

//						showQRCodePayment(checkoutUrl);

						// Chờ khách xác nhận trên Dialog
						int option = JOptionPane.showOptionDialog(view,
								"Trình duyệt thanh toán đã được mở.\nVui lòng hoàn tất thanh toán trên trình duyệt sau đó bấm 'Đã thanh toán'.",
								"Đang thanh toán qua VNPay", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
								null, new Object[] { "Đã thanh toán xong", "Hủy bỏ" }, "Đã thanh toán xong");

						if (option != JOptionPane.YES_OPTION) {
							p5.setComponentsEnabled(true);
							return;
						}
						giaoDichThanhToan.setTienNhan(tongTien);
					} else {
						throw new Exception("URL thanh toán trả về null.");
					}

				} catch (Exception ex) {
					// B. XỬ LÝ KHI MẤT MẠNG HOẶC LỖI API (BACKUP PLAN)
					ex.printStackTrace();

					int confirmOffline = JOptionPane.showConfirmDialog(view,
							"Không thể kết nối cổng thanh toán (Lỗi mạng/Server).\n"
									+ "Bạn có muốn XÁC NHẬN THỦ CÔNG là khách đã chuyển khoản thành công không?",
							"Lỗi kết nối Online", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (confirmOffline == JOptionPane.YES_OPTION) {
						// Coi như đã thanh toán thành công
						// Code sẽ tiếp tục chạy xuống dưới để in vé
					} else {
						p5.setComponentsEnabled(true);
						return;
					}
				}
			} else {
				double tienNhan = p5.getTienKhachDua();
				double tienHoan = tienNhan - tongTien;
				giaoDichThanhToan.setTienNhan(tienNhan);
				giaoDichThanhToan.setTienHoan(tienHoan);
			}

			// 2. Cập nhật bookingSession
			bookingSession.setGiaoDichThanhToan(giaoDichThanhToan);

			// Vô hiệu hóa nút để tránh bấm 2 lần
			p5.setComponentsEnabled(false);

			// 3. Thực thi giao dịch trong SwingWorker
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
//							// a. Xuất file pdf
//							PdfTicketExporter exporter = new PdfTicketExporter();
//							exporter.exportTicketsToPdf(bookingSession);
							JOptionPane.showMessageDialog(view, "Bán vé thành công!", "Thông báo",
									JOptionPane.INFORMATION_MESSAGE);
							p4.setComponentsEnabled(false);
							p5.setComponentsEnabled(false);

							// b. Báo cho wizard chính (PanelBanVe) biết
							if (onPaymentSuccessListener != null) {
								onPaymentSuccessListener.run();
							}
						} else {
							// Nếu thất bại, báo lỗi và bật lại UI
							JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!\n" + errorMessage,
									"Lỗi", JOptionPane.ERROR_MESSAGE);
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
		};

		if (payButtonCash != null) {
			payButtonCash.addActionListener(paymentListener);
		}
		if (payButtonQR != null) {
			payButtonQR.addActionListener(paymentListener);
		}
	}

	// Hàm hiển thị QR
	public void showQRCodePayment(String paymentUrl) {
		try {
			// 1. Tạo QR Code từ URL
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(paymentUrl, BarcodeFormat.QR_CODE, 300, 300);

			// 2. Chuyển thành ảnh
			BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIcon icon = new ImageIcon(bufferedImage);

			// 3. Hiển thị lên Dialog
			JLabel label = new JLabel(icon);
			String message = "Vui lòng quét mã QR bên dưới bằng điện thoại để thanh toán:";

			// Hiển thị Dialog chứa ảnh QR
			int option = JOptionPane.showOptionDialog(null, label, "Thanh toán qua VNPAY", JOptionPane.YES_NO_OPTION,
					JOptionPane.PLAIN_MESSAGE, null, new Object[] { "Tôi đã thanh toán xong", "Hủy" },
					"Tôi đã thanh toán xong");

			if (option == JOptionPane.YES_OPTION) {
				// Gọi hàm kiểm tra kết quả giao dịch ở đây
				// checkPaymentStatus();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}