package gui.application.form.doiVe;

/*
 * @(#) DoiVe3Controller.java  1.0  [5:47:12 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import bus.DoiVe_BUS;
import entity.GiaoDichThanhToan;
import gui.application.form.banVe.VeSession;

public class DoiVe3Controller {
	private final PanelDoiVe3 view;
	private final PanelDoiVeBuoc7 p7;
	private final PanelDoiVeBuoc8 p8;

	private final DoiVe_BUS doiVeBUS = new DoiVe_BUS();

	private final ExchangeSession exchangeSession;

	// Listener để báo cho wizard chính (PanelBanVe) biết khi thanh toán xong
	private Runnable onPaymentSuccessListener;

	public void addPanel2PaymentSuccessListener(Runnable listener) {
		this.onPaymentSuccessListener = listener;
	}

	public DoiVe3Controller(PanelDoiVe3 view, ExchangeSession session) {
		this.view = view;
		this.exchangeSession = session;

		this.p7 = view.getPanelDoiVeBuoc7();
		this.p8 = view.getPanelDoiVeBuoc8();

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	/**
	 * Được gọi bởi PanelDoiVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc7, Buoc8.
	 */
	public void loadDataForConfirmation() {
		// 1. Đặt lại trạng thái
		p7.setComponentsEnabled(true);
		p8.setComponentsEnabled(true);

		// 2. Tải dữ liệu vào bảng xác nhận (Buoc7)
		p7.hienThiThongTin(exchangeSession);

		// 3. Tính toán chi tiết thanh toán
		int tongTienVeCu = 0;
		int tongTienVeMoi = 0;
		int tongPhiDoiVe = 0;

		List<VeDoiRow> listVeDoi = exchangeSession.getListVeCuCanDoi();
		List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
		for (VeDoiRow veDoi : listVeDoi) {
			tongTienVeCu += veDoi.getVe().getGia();
			tongPhiDoiVe += veDoi.getLePhiDoiVe();
		}
		for (VeSession veMoi : listVeMoi) {
			tongTienVeMoi += veMoi.getVe().getGia();
		}

		// 4. Đẩy chi tiết thanh toán vào Buoc8
		p8.setChiTietThanhToan(tongTienVeCu, tongTienVeMoi, tongPhiDoiVe);
	}

	/**
	 * Hàm nội bộ để kết nối logic giữa Buoc7 và Buoc8
	 */
	private void initMediatorLogic() {
		// Lắng nghe nút thanh toán từ PanelDoiVeBuoc8
		JButton payButtonCash = p8.getBtnXacNhanVaInCash();
		JButton payButtonQR = p8.getBtnXacNhanVaInQR();

		ActionListener paymentListener = e -> {
			// 1. Lấy thông tin thanh toán từ View
			boolean isThanhToanTienMat = p8.isThanhToanTienMat();
			String maGiaoDich = null;
			double tongTien = p8.getTongThanhToan();
			double tienNhan = p8.getTienKhachDua();
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

			// 2. Cập nhật exchangeSession
			exchangeSession.setGiaoDichThanhToan(giaoDichThanhToan);

			// Vô hiệu hóa nút để tránh bấm 2 lần
			p8.setComponentsEnabled(false);

			// 3. Thực thi giao dịch trong SwingWorker
			new SwingWorker<Boolean, Void>() {
				private String errorMessage = "Lỗi không xác định";

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						return doiVeBUS.thucHienDoiVe(exchangeSession);
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
//							exporter.exportTicketsToPdf(exchangeSession);

							JOptionPane.showMessageDialog(view, "Đổi vé thành công!", "Thông báo",
									JOptionPane.INFORMATION_MESSAGE);
							p8.setComponentsEnabled(true);

							// b. Báo cho wizard chính (PanelBanVe) biết
							if (onPaymentSuccessListener != null) {
								onPaymentSuccessListener.run();
							}
						} else {
							// Nếu thất bại, báo lỗi và bật lại UI
							JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!\n" + errorMessage,
									"Lỗi", JOptionPane.ERROR_MESSAGE);
							p8.setComponentsEnabled(true);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						p8.setComponentsEnabled(true);
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