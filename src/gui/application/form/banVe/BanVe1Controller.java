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
import entity.PhieuGiuCho;
import entity.PhieuGiuChoChiTiet;
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
			// 1. Cập nhật chữ ký (signature) của hàm để nhận 2 danh sách
			public void onSearchSuccess(List<Chuyen> outboundResults, List<Chuyen> returnResults,
					SearchCriteria criteria) {

				// 2. Lưu cả criteria và CẢ HAI danh sách kết quả vào session
				bookingSession.setOutboundCriteria(criteria);
				bookingSession.setOutboundResults(outboundResults);
				bookingSession.setReturnResults(returnResults); // <-- Thêm dòng này

				// 3. Kích hoạt Bước 2
				p1.setBuoc2Enabled(true);
				p1.setBuoc3Enabled(false);

				// 4. CHỈ hiển thị danh sách CHIỀU ĐI (outboundResults) lên PanelBuoc2
				// Chúng ta truyền tripIndex = 0 để Buoc2Controller biết đây là chiều đi.
				buoc2Controller.displayChuyenList(criteria, outboundResults, 0);
			}

			@Override
			public void onSearchFailure() {
				// Xóa cả hai danh sách kết quả khi tìm thất bại
				bookingSession.setOutboundResults(null);
				bookingSession.setReturnResults(null); // <-- Thêm dòng này

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
				p2.setEnabled(false);
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

		// Lắng nghe sự kiện từ Buoc3 (Nhập thông tin hành khách/khách hàng)
		this.buoc3Controller.setOnDeleteListener(veSession -> {
			// 1. Gọi BUS để xóa phiếu giữ chỗ chi tiết TRONG BACKGROUND
			new SwingWorker<Boolean, Void>() {
				private String errorMessage = "Lỗi không xác định khi xóa phiếu.";

				@Override
				protected Boolean doInBackground() throws Exception {
					try {

						// Gọi BUS để xóa Phiếu giữ chỗ chi tiết trong CSDL
						return datChoBUS.xoaPhieuGiuChoChiTiet(veSession.getPgcct().getPhieuGiuChoChiTietID());

					} catch (Exception e) {
						errorMessage = e.getMessage();
						e.printStackTrace();
						return false;
					}
				}

				@Override
				protected void done() {
					try {
						Boolean deleteSuccess = get();
						if (deleteSuccess) {
							// 2. (SAU KHI DB ĐÃ XÓA THÀNH CÔNG)
							// Gọi Buoc2Controller để xóa vé khỏi session (client-side)
							if (buoc2Controller != null) {
								// onRemoveVe sẽ tự động refresh PanelGioVe VÀ PanelSoDoCho
								buoc2Controller.onRemoveVe(veSession);
							}

							// Nếu không còn vé nào trong giỏ thì xóa Phiếu giữ chỗ
							if (bookingSession.getOutboundSelectedTickets().size() == 0
									&& bookingSession.getReturnSelectedTickets().size() == 0) {
								if (bookingSession.getPgc() != null) {
									datChoBUS.xoaPhieuGiuCho(bookingSession.getPgc().getPhieuGiuChoID());
								}

							}

							// 3. Tải lại dữ liệu cho bảng của Buoc3
							if (p3 != null && bookingSession != null && buoc2Controller != null) {
								p3.initFromBookingSession(bookingSession, buoc2Controller.getCurrentTripIndex());
							}

						} else {
							// Báo lỗi nếu xóa DB thất bại
							JOptionPane.showMessageDialog(p1, "Lỗi: " + errorMessage, "Lỗi xóa vé",
									JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(p1, "Lỗi hệ thống khi xóa phiếu giữ chỗ.", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}.execute();
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
			private String errorMessage = "Lỗi không xác định";

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					// === 4. GỌI BUS TẠI ĐÂY ===
					PhieuGiuCho pgc = datChoBUS.themPhieuGiuCho();

					bookingSession.setPgc(pgc);

					for (VeSession v : veTrongGio) {
						PhieuGiuChoChiTiet pgcct = datChoBUS.themPhieuGiuChoChiTiet(pgc, v);
						if (pgcct == null) {
							return false;
						}
						v.setPgcct(pgcct);
					}

					return true;
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
}