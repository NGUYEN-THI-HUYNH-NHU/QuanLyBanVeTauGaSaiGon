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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import bus.BanVe_BUS;
import entity.GiaoDichThanhToan;
import entity.type.LoaiDoiTuong;

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

	private final BookingSession bookingSession;

	// Listener để báo cho wizard chính (PanelBanVe) biết khi thanh toán xong
	private Runnable onPaymentSuccessListener;

	public void addPanel2PaymentSuccessListener(Runnable listener) {
		this.onPaymentSuccessListener = listener;
	}

	public BanVe2Controller(PanelBanVe2 view, BookingSession session) {
		this.view = view;
		this.bookingSession = session;

		this.p4 = view.getPanelBuoc4();
		this.p5 = view.getPanelBuoc5();

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	/**
	 * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
	 */
	public void loadDataForConfirmation() {
		// 1. Đặt lại trạng thái
		p4.setComponentsEnabled(true);
		p5.setComponentsEnabled(true);

		// 2. Tải dữ liệu vào bảng xác nhận (Buoc4)
		p4.hienThiThongTin(bookingSession);

		// 3. Tính toán chi tiết thanh toán
		int tongTienVe = 0;
		double giamGiaDT = 0;
		int khuyenMai = 0;
		int dichVu = 0;

		List<VeSession> allTickets = new ArrayList<>(bookingSession.getOutboundSelectedTickets());
		if (bookingSession.isRoundTrip()) {
			allTickets.addAll(bookingSession.getReturnSelectedTickets());
		}

		for (VeSession ve : allTickets) {
			tongTienVe += ve.getVe().getGia();
			dichVu += ve.getPhiPhieuDungPhongChoVIP();
			khuyenMai += ve.getGiamKM();

			// Giảm giá đối tượng ở đây
			if (ve.getVe().getKhachHang().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				ve.setGiamDoiTuong((int) (Math.round((ve.getVe().getGia() * 0.25) / 1000) * 1000));
				giamGiaDT += ve.getGiamDoiTuong();
			}
		}

		// 4. Đẩy chi tiết thanh toán vào Buoc5
		p5.setChiTietThanhToan(tongTienVe, (int) giamGiaDT, khuyenMai, dichVu);
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
			String maGiaoDich = null;
			double tongTien = p5.getTongThanhToan();
			double tienNhan = p5.getTienKhachDua();
			double tienHoan = tienNhan - tongTien;
			boolean trangThai = true;
			GiaoDichThanhToan giaoDichThanhToan = null;

			if (!isThanhToanTienMat) {
				// TODO: Lấy mã giao dịch thật
				maGiaoDich = "GDTEST";
				giaoDichThanhToan = new GiaoDichThanhToan(tienNhan, maGiaoDich, tongTien, isThanhToanTienMat,
						trangThai);
			} else {
				giaoDichThanhToan = new GiaoDichThanhToan(tienNhan, tienHoan, tongTien, isThanhToanTienMat, trangThai);
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
}