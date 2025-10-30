package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2Controller.java  1.0  [12:05:37 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.event.ActionListener;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import gui.application.PdfTicketExporter;

/**
 * Controller (Mediator) cho PanelBanVe2. Nhiệm vụ: 1. Lấy dữ liệu từ
 * BookingSession. 2. Đổ dữ liệu vào PanelBuoc4 (Xác nhận) và PanelBuoc5 (Chi
 * tiết giá). 3. Lắng nghe sự kiện "Xác nhận Thanh toán" từ PanelBuoc5. 4. Báo
 * cho Wizard (PanelBanVe) khi thanh toán hoàn tất.
 */
public class BanVe2Controller {

	private final PanelBanVe2 view;
	private final BookingSession bookingSession;

	// Các panel con
	private final PanelBuoc4 panelBuoc4;
	private final PanelBuoc5 panelBuoc5;

	// Listener để báo cho wizard chính (PanelBanVe) biết khi thanh toán xong
	private Runnable onPaymentSuccessListener;

	public BanVe2Controller(PanelBanVe2 view, BookingSession session) {
		this.view = view;
		this.bookingSession = session;

		// Lấy các panel con từ view gộp
		this.panelBuoc4 = view.getPanelBuoc4();
		this.panelBuoc5 = view.getPanelBuoc5();

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	public void addPaymentSuccessListener(Runnable listener) {
		this.onPaymentSuccessListener = listener;
	}

	/**
	 * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
	 */
	public void loadDataForConfirmation() {
		// 1. Đặt lại trạng thái
		panelBuoc4.setComponentsEnabled(true);
		panelBuoc5.setComponentsEnabled(true); // Bật cả 2 panel

		// 2. Tải dữ liệu vào bảng xác nhận (Buoc4)
		panelBuoc4.hienThiThongTin(bookingSession);

		// 3. Tính toán chi tiết thanh toán
		int tongTienVe = 0;
		int giamGiaDT = 0;
		int khuyenMai = 0;
		int dichVu = 0;

		List<VeSession> allTickets = new ArrayList<>(bookingSession.getOutboundSelectedTickets());
		if (bookingSession.isRoundTrip()) {
			allTickets.addAll(bookingSession.getReturnSelectedTickets());
		}

		for (VeSession ve : allTickets) {
			tongTienVe += ve.getGia();
			khuyenMai += ve.getGiam();

			// TODO: Bạn cần thêm logic tính giảm giá đối tượng ở đây
			// ví dụ: if (ve.getHanhKhach().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
			// giamGiaDT = giamGiaDT.add(ve.getGia().multiply(new int("0.25"))); //
			// Giảm 25%
			// }
		}

		// Cập nhật hardcode từ prototype (bạn sẽ thay bằng logic thật)
		giamGiaDT = 0;

		// 4. Đẩy chi tiết thanh toán vào Buoc5
		panelBuoc5.setChiTietThanhToan(tongTienVe, giamGiaDT, khuyenMai, dichVu);
	}

	/**
	 * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
	 */
	private void initMediatorLogic() {

		// Lắng nghe nút "Thanh toán" từ PanelBuoc5
		// CẦN LẤY CẢ HAI NÚT
		JButton payButtonCash = panelBuoc5.getBtnXacNhanVaInCash();
		JButton payButtonQR = panelBuoc5.getBtnXacNhanVaInQR();

		ActionListener paymentListener = e -> {
			// TODO: Gọi BUS để thực hiện lưu CSDL (lưu Vé, Hóa đơn, Giao dịch...)
			// boolean saveSuccess = banVeBUS.luuThongTinThanhToan(bookingSession,
			// isTienMat);
			boolean saveSuccess = true; // Giả sử lưu thành công

			if (saveSuccess) {
				// a. Vô hiệu hóa PanelBuoc5
				panelBuoc5.setComponentsEnabled(false);

				// --- GỌI HÀM XUẤT PDF ---
				PdfTicketExporter exporter = new PdfTicketExporter();
				exporter.exportTicketsToPdf(bookingSession);
				// --- KẾT THÚC GỌI PDF ---

				// b. Báo cho wizard chính (PanelBanVe) biết để chuyển sang bước Hoàn tất
				if (onPaymentSuccessListener != null) {
					onPaymentSuccessListener.run();
				}
			} else {
				JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		};

		if (payButtonCash != null) {
			payButtonCash.addActionListener(paymentListener);
		}
		if (payButtonQR != null) {
			payButtonQR.addActionListener(paymentListener);
		}
	}
}