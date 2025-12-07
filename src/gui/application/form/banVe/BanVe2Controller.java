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
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import bus.BanVe_BUS;
import bus.KhuyenMai_BUS;
import entity.GiaoDichThanhToan;
import entity.type.LoaiDoiTuong;
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

//	private final VNPayService vnpayService = new VNPayService(); // Khởi tạo

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
			// 1. Lấy thông tin cơ bản
			boolean isThanhToanTienMat = p5.isThanhToanTienMat();
			double tongTien = p5.getTongThanhToan();

			GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
			giaoDich.setTongTien(tongTien);
			giaoDich.setThanhToanTienMat(isThanhToanTienMat);

			if (!isThanhToanTienMat) {
				// Biến cờ để kiểm soát việc lưu (tránh lưu 2 lần do cả Server và User cùng bấm)
				final boolean[] isProcessed = { false };

				try {
					handleVietQRPayment();
				} catch (Exception ex) {
					// Xử lý lỗi (Mất mạng, config sai...)
					ex.printStackTrace();

					int confirmOffline = JOptionPane.showConfirmDialog(view,
							"Không thể kết nối cổng thanh toán (Lỗi mạng/Server).\n"
									+ "Bạn có muốn XÁC NHẬN THỦ CÔNG là khách đã chuyển khoản thành công không?",
							"Lỗi kết nối Online", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

					if (confirmOffline == JOptionPane.YES_OPTION) {
						if (!isProcessed[0]) {
							isProcessed[0] = true;
							giaoDich.setTienNhan(tongTien);
							processPaymentAndSave(giaoDich);
						}
					} else {
						p5.setComponentsEnabled(true);
					}
				}

			} else {
				double tienNhan = p5.getTienKhachDua();
				double tienHoan = tienNhan - tongTien;

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

	private void handleVietQRPayment() {
		// 1. Lấy thông tin thanh toán
		double tongTien = p5.getTongThanhToan();
		String maGiaoDich = "VETAU_" + System.currentTimeMillis();
		String noiDungCK = "TT " + maGiaoDich; // Nội dung ngắn gọn, không dấu

		// 2. Gọi Service để lấy ảnh QR
		VietQRService qrService = new VietQRService();
		String qrUrl = qrService.generateQRUrl(tongTien, noiDungCK);

		System.out.println("Link QR: " + qrUrl); // In ra để test nếu ảnh không hiện

		// 3. Tải ảnh và hiển thị Dialog
		// Chạy trong SwingWorker để tránh đơ giao diện khi đang tải ảnh từ mạng
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
						// Tạo giao diện Dialog đẹp
						JLabel lblImage = new JLabel(icon);
						JLabel lblNote = new JLabel("<html><div style='text-align:center; width: 300px;'>"
								+ "<b style='font-size:14px; color:blue'>QUÉT MÃ ĐỂ THANH TOÁN NGAY</b><br/>"
								+ "Số tiền: <b style='color:red'>" + String.format("%,.0f", tongTien) + " VNĐ</b><br/>"
								+ "<i>(Tiền sẽ về tài khoản của bạn ngay lập tức)</i>" + "</div></html>");
						lblNote.setHorizontalAlignment(JLabel.CENTER);

						JPanel panel = new JPanel(new BorderLayout());
						panel.add(lblNote, BorderLayout.NORTH);
						panel.add(lblImage, BorderLayout.CENTER);

						// 4. Hiển thị Dialog và CHỜ XÁC NHẬN TỪ NGƯỜI DÙNG
						Object[] options = { "Tôi đã chuyển khoản xong", "Hủy bỏ" };
						int result = JOptionPane.showOptionDialog(view, panel, "Thanh toán VietQR - Tiền thật",
								JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

						// 5. Xử lý kết quả
						if (result == JOptionPane.YES_OPTION) {
							// A. Người dùng xác nhận đã trả tiền
							// Lưu giao dịch vào DB
							GiaoDichThanhToan giaoDich = new GiaoDichThanhToan();
							giaoDich.setTongTien(tongTien);
							giaoDich.setTienNhan(tongTien);
							giaoDich.setThanhToanTienMat(false);

							// Gọi hàm lưu
							processPaymentAndSave(giaoDich);

						} else {
							// B. Người dùng hủy
							p5.setComponentsEnabled(true);
						}
					} else {
						JOptionPane.showMessageDialog(view, "Không tải được mã QR. Vui lòng kiểm tra kết nối mạng.");
						p5.setComponentsEnabled(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
					p5.setComponentsEnabled(true);
				}
			}
		}.execute();
	}
}