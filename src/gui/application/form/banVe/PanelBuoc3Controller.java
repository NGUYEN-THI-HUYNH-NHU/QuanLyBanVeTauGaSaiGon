package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3Controller.java  1.0  [8:06:26 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import bus.DatCho_BUS;
import entity.KhachHang;

public class PanelBuoc3Controller {

	private final PanelBuoc3 view;

	private final BookingSession session;
	private final DatCho_BUS datChoBUS = new DatCho_BUS();

	// Listeners để báo cho Controller Mediator (BanVe1Controller)
	private Runnable onConfirmListener;
	private Runnable onCancelListener;

	private Consumer<VeSession> onDeleteListener;

	public PanelBuoc3Controller(PanelBuoc3 view, BookingSession session) {
		this.view = view;
		this.session = session;
		attachListeners();
	}

	// Gắn listener vào các nút của View
	private void attachListeners() {
		view.getConfirmButton().addActionListener(e -> handleConfirm());
		view.getCancelButton().addActionListener(e -> handleCancel());
		view.setPassengerDeleteListener(row -> {
			handleDelete(row);
		});
	}

	private void handleDelete(PassengerRow rowToDelete) {
		if (rowToDelete == null) {
			return;
		}

		VeSession veSession = rowToDelete.getVeSession();

		// Hiển thị xác nhận
		int choice = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa vé:\n" + veSession.prettyString(),
				"Xác nhận xóa vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			// Nếu người dùng đồng ý, báo cho BanVe1Controller
			if (onDeleteListener != null) {
				onDeleteListener.accept(veSession);
			}
		}
	}

	/**
	 * Xử lý logic khi bấm "Xác nhận"
	 */
	private void handleConfirm() {
		// 1. Lấy dữ liệu thô từ View
		List<PassengerRow> rows = view.getPassengerRows();
		String tenNguoiMua = view.getTenNguoiMua();
		String cmndNguoiMua = view.getCccdNguoiMua();
		String phoneNguoiMua = view.getPhoneNguoiMua();

		// 2. Thực hiện Validation (Logic đã chuyển về đây)
		if (!validateInput(rows, tenNguoiMua, cmndNguoiMua, phoneNguoiMua)) {
			return;
		}

		// 3. Cập nhật Model (BookingSession)

		// 3a. Tạo và set Người Mua
		KhachHang nguoiMua = new KhachHang();
		nguoiMua.setHoTen(tenNguoiMua);
		nguoiMua.setSoGiayTo(cmndNguoiMua);
		nguoiMua.setSoDienThoai(phoneNguoiMua);
		session.setNguoiMua(nguoiMua);

		// 3b. Cập nhật thông tin Hành Khách vào từng VeSession
		for (PassengerRow row : rows) {
			VeSession ve = row.getVeSession();

			// Tạo entity HanhKhach
			KhachHang hanhKhach = new KhachHang();
			hanhKhach.setHoTen(row.getFullName());
			hanhKhach.setSoGiayTo(row.getIdNumber());
			hanhKhach.setLoaiDoiTuong(row.getType());

			// Gán vào VeSession
			ve.setHanhKhach(hanhKhach);
		}

		System.out.println("BookingSession đã được cập nhật với thông tin hành khách và người mua.");

		// 4. Báo cho Controller cha (BanVe1Controller) biết là đã xong
		if (onConfirmListener != null) {
			onConfirmListener.run();
		}
	}

	/**
	 * Xử lý logic khi bấm "Hủy"
	 */
	private void handleCancel() {
		// 1. Gọi BUS để hủy phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuChoChiTietByPgcID(session.getPgc().getPhieuGiuChoID());

		// 2. Nếu sau khi xóa mà không còn vé nào thì xóa luôn Phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuCho(session.getPgc().getPhieuGiuChoID());

		// 3. Báo cho Controller cha biết
		if (onCancelListener != null) {
			onCancelListener.run();
		}
	}

	/**
	 * Logic validation, giờ nằm trong Controller
	 */
	private boolean validateInput(List<PassengerRow> rows, String ten, String cmnd, String phone) {
		// Validate Bảng hành khách
		for (PassengerRow r : rows) {
			if (r.getFullName() == null || r.getFullName().trim().isEmpty()) {
				JOptionPane.showMessageDialog(view, "Vui lòng nhập tên đầy đủ cho tất cả hành khách.", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (r.getIdNumber() == null || r.getIdNumber().trim().isEmpty()) {
				JOptionPane.showMessageDialog(view, "Vui lòng nhập Số giấy tờ cho hành khách: " + r.getFullName(),
						"Lỗi", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		// Validate Form người mua
		if (ten.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Vui lòng nhập họ tên người mua vé.", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (cmnd.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Vui lòng nhập CMND/Hộ chiếu người mua vé.", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		if (phone.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Vui lòng nhập số điện thoại người mua vé.", "Lỗi",
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	// Setter cho các listener
	public void setOnConfirmListener(Runnable listener) {
		this.onConfirmListener = listener;
	}

	public void setOnCancelListener(Runnable listener) {
		this.onCancelListener = listener;
	}

	public void setOnDeleteListener(Consumer<VeSession> listener) {
		this.onDeleteListener = listener;
	}
}