package gui.application.form.banVe;
/*
 * @(#) PanelBanVe1Controller.java  1.0  [10:42:48 AM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import bus.DatCho_BUS;
import entity.Chuyen;
import gui.application.form.banVe.PanelBuoc1Controller.SearchListener;
import gui.application.form.banVe.PanelBuoc2Controller.SeatSelectedListener;

public class BanVe1Controller {

	private final PanelBanVe1 p1;
	private final PanelBuoc2 p2;
	private final PanelBuoc3 p3;

	private final BookingSession bookingSession;

	// Các sub-controller
	private final PanelBuoc1Controller buoc1Controller;
	private final PanelBuoc2Controller buoc2Controller;
	private final PanelBuoc3Controller buoc3Controller;

	private final DatCho_BUS datChoBUS;

	private Runnable onPanel1CompleteListener;

	public void addPanel1CompleteListener(Runnable listener) {
		this.onPanel1CompleteListener = listener;
	}

	public BanVe1Controller(PanelBanVe1 p1, BookingSession session) {
		this.p1 = p1;
		this.bookingSession = session;

		// === 2. Khởi tạo BUS ===
		// (Hoặc DatCho_BUS.getInstance() nếu là Singleton)
		this.datChoBUS = new DatCho_BUS();

		// Khởi tạo các panel con
		this.buoc1Controller = new PanelBuoc1Controller(p1.getPanelBuoc1());
		this.p2 = p1.getPanelBuoc2();
		this.p3 = p1.getPanelBuoc3();

		this.buoc2Controller = new PanelBuoc2Controller(p2.getPanelChieuLabel(), p2.getPanelChuyenTau(),
				p2.getPanelDoanTau(), p2.getPanelSoDoCho(), p2.getPanelGioVe());

		this.buoc2Controller.setBookingSession(this.bookingSession);

		this.buoc3Controller = new PanelBuoc3Controller(p1.getPanelBuoc3(), this.bookingSession);

		initMediatorLogic();
	}

	private void initMediatorLogic() {

		// Lắng nghe sự kiện từ Buoc1 (Tìm chuyến)
		this.buoc1Controller.addSearchListener(new SearchListener() {
			@Override
			public void onSearchSuccess(List<Chuyen> results, SearchCriteria criteria) {
				bookingSession.setOutboundCriteria(criteria);
				bookingSession.setOutboundResults(results);
				p1.setBuoc2Enabled(true);
				p1.setBuoc3Enabled(false);
				buoc2Controller.displayChuyenList(criteria, results, 0);
			}

			@Override
			public void onSearchFailure() {
				bookingSession.setOutboundResults(null);
				p1.setBuoc2Enabled(false);
				p1.setBuoc3Enabled(false);
			}
		});

		// Lắng nghe sự kiện từ Buoc2 (Chọn ghế VÀ Bấm Mua vé)
		this.buoc2Controller.addSeatSelectedListener(new SeatSelectedListener() {

			@Override
			public void onSeatSelected(VeSession ticket) {
				// (Chưa cần làm gì khi chỉ chọn 1 ghế,
				// vì logic mới là chờ bấm "Mua vé")
			}

			@Override
			public void onMuaVeClicked() {
				// === 3. ĐÂY LÀ LOGIC CỦA BẠN ===
				// Bắt đầu quá trình giữ chỗ khi bấm "Mua vé"

				// 3a. Lấy danh sách vé từ giỏ hàng (session)
				final List<VeSession> veTrongGio = bookingSession
						.getSelectedTicketsForTrip(buoc2Controller.getCurrentTripIndex());

				if (veTrongGio == null || veTrongGio.isEmpty()) {
					JOptionPane.showMessageDialog(p1, "Giỏ vé trống. Vui lòng chọn ít nhất 1 vé.", "Lỗi",
							JOptionPane.WARNING_MESSAGE);
					return;
				}

				// 3b. Gọi BUS trong luồng nền (SwingWorker) để tránh đơ UI
				goiBusGiuCho(veTrongGio);
			}
		});

		this.buoc3Controller.setOnConfirmListener(() -> {
			// Tất cả đã xong (Buoc1+2+3) -> Báo cho PanelBanVe
			if (onPanel1CompleteListener != null) {
				onPanel1CompleteListener.run();
			}
		});

		// Lắng nghe sự kiện "Hủy" từ Buoc 3
		this.buoc3Controller.setOnCancelListener(() -> {
			p1.setBuoc3Enabled(false);
			// (Logic hủy phiếu giữ chỗ đã được chuyển vào handleCancel của buoc3Controller)
		});
	}

	/**
	 * Hàm này thực hiện gọi BUS trong luồng nền
	 */
	private void goiBusGiuCho(List<VeSession> veTrongGio) {

		new SwingWorker<Boolean, Void>() {
			// Kiểu Boolean: true = thành công, false = thất bại
			private String errorMessage = "Lỗi không xác định";

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					// === 4. GỌI BUS TẠI ĐÂY ===
					// Giả sử hàm này sẽ ném ra Exception nếu có lỗi (ghế bị trùng, v.v.)
					// (Bạn cần có hàm này trong DatCho_BUS)

					return datChoBUS.taoPhieuGiuChoVaChiTiet(veTrongGio);
				} catch (Exception e) {
					errorMessage = e.getMessage(); // Lấy thông báo lỗi nghiệp vụ
					return false;
				}
			}

			@Override
			protected void done() {
				try {
					Boolean success = get();
					if (success) {
						// 5. THÀNH CÔNG: Hiển thị PanelBuoc3
						p1.setBuoc3Enabled(true);
						p3.initFromBookingSession(bookingSession, buoc2Controller.getCurrentTripIndex());

						// (Bạn có thể thêm logic cuộn màn hình xuống p3 nếu cần)

					} else {
						// 6. THẤT BẠI: Hiển thị lỗi
						JOptionPane.showMessageDialog(p1, "Không thể giữ chỗ: \n" + errorMessage, "Lỗi giữ chỗ",
								JOptionPane.ERROR_MESSAGE);

						// (Tùy chọn: refresh lại sơ đồ ghế để thấy ghế bị trùng)
						// buoc2Controller.refreshCurrentSeats();
					}
				} catch (Exception e) {
					// Lỗi của chính SwingWorker
					JOptionPane.showMessageDialog(p1, "Lỗi hệ thống: " + e.getMessage(), "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}

//	/**
//	 * Tách logic gắn listener cho Buoc3 ra hàm riêng
//	 */
//	private void attachStep3Listeners() {
//		// Gắn listener cho nút Confirm (Xác nhận thông tin hành khách)
//		p3.getConfirmButton().addActionListener(ev -> {
//			if (!p3.validateRows()) {
//				JOptionPane.showMessageDialog(null, "Vui lòng nhập tên đầy đủ cho từng hành khách.");
//				return;
//			}
//			// Lấy thông tin người mua vé
//			KhachHang nguoiMua = p3.getNguoiMua();
//			bookingSession.setNguoiMua(nguoiMua);
//
//			// Cập nhật thông tin hành khách vào các VeSession trong bookingSession
//			List<PassengerRow> rows = p3.getPassengerRows();
//			for (PassengerRow r : rows) {
//				VeSession v = r.getVeSession();
//				KhachHang hanhKhach = new KhachHang();
//				hanhKhach.setHoTen(r.getFullName());
//				hanhKhach.setSoGiayTo(r.getIdNumber());
//				hanhKhach.setLoaiDoiTuong(r.getType());
//				v.setHanhKhach(hanhKhach);
//			}
//
//			System.out.println("BookingSession đã được cập nhật với thông tin hành khách và người mua.");
//
//			// Tất cả đã xong (Buoc1+2+3) -> Báo cho PanelBanVe
//			if (onPanel1CompleteListener != null) {
//				onPanel1CompleteListener.run();
//			}
//		});
//
//		// Gắn listener cho nút Hủy (của Buoc 3)
//		p3.getCancelButton().addActionListener(ev -> {
//			p1.setBuoc3Enabled(false);
//
//			// (Lưu ý: Bạn nên gọi BUS để HỦY phiếu giữ chỗ đã tạo ở đây
//			// nếu không các ghế sẽ bị khóa 10 phút)
//			// datChoBUS.huyPhieuGiuCho(bookingSession.getPhieuGiuChoID());
//		});
//	}
}