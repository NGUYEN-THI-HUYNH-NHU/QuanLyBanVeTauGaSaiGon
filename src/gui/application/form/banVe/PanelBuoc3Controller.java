package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3Controller.java  1.0  [8:06:26 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import bus.KhachHang_BUS;
import entity.KhachHang;
import entity.type.LoaiKhachHang;

public class PanelBuoc3Controller {

	private final PanelBuoc3 view;

	private final BookingSession bookingSession;
	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	// Listeners để báo cho Controller Mediator (BanVe1Controller)
	private Runnable onConfirmListener;
	private Runnable onCancelListener;

	private Consumer<VeSession> onDeleteListener;

	public PanelBuoc3Controller(PanelBuoc3 view, BookingSession bookingSession) {
		this.view = view;
		this.bookingSession = bookingSession;
		this.view.setController(this);
		attachListeners();
	}

	// Gắn listener vào các nút của View
	private void attachListeners() {
		view.getConfirmButton().addActionListener(e -> handleConfirm());
		view.getCancelButton().addActionListener(e -> handleCancel());
		view.setPassengerDeleteListener(row -> {
			handleDelete(row);
		});

		// 1. Enter trên CCCD -> Tìm kiếm và focus Tên
		view.getTxtCccdNguoiMua().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				findNguoiMua();
				view.getTxtTenNguoiMua().requestFocusInWindow();
			}
		});

		// 2. Enter trên Tên -> focus Số điện thoại
		view.getTxtTenNguoiMua().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getTxtPhoneNguoiMua().requestFocusInWindow();
			}
		});

		// 3. Enter trên SĐT -> focus Nút Xác nhận
		view.getTxtPhoneNguoiMua().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getConfirmButton().requestFocusInWindow();
			}
		});
	}

	/**
	 * Hàm tìm kiếm người mua (KhachHang) bằng CCCD và cập nhật View nếu tìm thấy.
	 */
	private void findNguoiMua() {
		String id = view.getTxtCccdNguoiMua().getText().trim();
		if (id.isEmpty()) {
			return;
		}

		// Gọi BUS để tìm
		KhachHang kh = findKhachHangByID(id);

		if (kh != null) {
			// Tìm thấy -> Cập nhật View
			view.getTxtTenNguoiMua().setText(kh.getHoTen());
			view.getTxtPhoneNguoiMua().setText(kh.getSoDienThoai());
			// Lưu khách hàng tìm thấy vào session
			bookingSession.setKhachHang(kh);
		} else {
			// Không tìm thấy -> Xóa dữ liệu cũ (nếu có)
			view.getTxtTenNguoiMua().setText("");
			view.getTxtPhoneNguoiMua().setText("");
			// Đặt session về null để handleConfirm biết là khách mới
			bookingSession.setKhachHang(null);
		}
	}

	public KhachHang findKhachHangByID(String id) {
		if (id == null || id.trim().isEmpty()) {
			return null;
		}
		try {
			return khachHangBUS.timKiemKhachHangTheoSoGiayTo(id.trim());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
//	private void handleConfirm() {
//		// 1. Lấy dữ liệu thô từ View
//		List<PassengerRow> rows = view.getPassengerRows();
//		String tenNguoiMua = view.getTxtTenNguoiMua().getText();
//		String cmndNguoiMua = view.getTxtCccdNguoiMua().getText();
//		String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText();
//
//		// 2. Thực hiện Validation (Logic đã chuyển về đây)
//		if (!validateInput(rows, tenNguoiMua, cmndNguoiMua, phoneNguoiMua)) {
//			return;
//		}
//
//		// 3. Cập nhật Model (BookingSession)
//
//		// 3a. Tạo và set Khách hàng (Người Mua)
//		// Nếu là khách hàng mới thì tạo và thêm vào CSDL
//		if (bookingSession.getKhachHang() != null) {
//			KhachHang khachHang = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), tenNguoiMua, phoneNguoiMua,
//					cmndNguoiMua, LoaiKhachHang.KHACH_HANG);
//			bookingSession.setKhachHang(khachHang);
//			System.out.println(khachHang);
//		}
//
//		// 3b. Cập nhật thông tin Hành Khách vào từng VeSession
//		for (PassengerRow row : rows) {
//			VeSession ve = row.getVeSession();
//			// Nếu là hành khách mới thì tạo và thêm vào CSDL
//			if (ve.getHanhKhach() != null) {
//				KhachHang hanhKhach = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), row.getFullName(),
//						row.getType(), row.getIdNumber());
//				// Gán vào VeSession
//				ve.setHanhKhach(hanhKhach);
//				System.out.println(hanhKhach);
//			}
//		}
//
//		System.out.println("BookingSession đã được cập nhật với thông tin hành khách và người mua.");
//
//		// 4. Báo cho Controller cha (BanVe1Controller) biết là đã xong
//		if (onConfirmListener != null) {
//			onConfirmListener.run();
//		}
//	}
	private void handleConfirm() {
		// 1. Lấy dữ liệu thô từ View
		List<PassengerRow> rows = view.getPassengerRows();
		String tenNguoiMua = view.getTxtTenNguoiMua().getText().trim();
		String cmndNguoiMua = view.getTxtCccdNguoiMua().getText().trim();
		String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText().trim();

		// 2. Validate (Giữ nguyên)
		if (!validateInput(rows, tenNguoiMua, cmndNguoiMua, phoneNguoiMua)) {
			return;
		}

		// 3. Cập nhật Model (BookingSession)

		// 3a. Cập nhật Khách hàng (Người Mua)
		KhachHang nguoiMua = bookingSession.getKhachHang();
		if (nguoiMua == null) {
			// Không tìm thấy (hoặc không nhập) -> Tạo khách hàng mới
			nguoiMua = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), tenNguoiMua, phoneNguoiMua, cmndNguoiMua,
					LoaiKhachHang.KHACH_HANG);
			// TODO: Gọi khachHangBUS.themKhachHang(nguoiMua)
			bookingSession.setKhachHang(nguoiMua);
			System.out.println("Tạo người mua mới: " + nguoiMua);
		} else {
			// Tìm thấy -> Cập nhật lại thông tin (nếu người dùng sửa)
			nguoiMua.setHoTen(tenNguoiMua);
			nguoiMua.setSoDienThoai(phoneNguoiMua);
			// TODO: Gọi khachHangBUS.capNhatKhachHang(nguoiMua)
			System.out.println("Cập nhật người mua: " + nguoiMua);
		}

		// 3b. Cập nhật thông tin Hành Khách vào từng VeSession
		for (PassengerRow row : rows) {
			VeSession ve = row.getVeSession();
			KhachHang hanhKhach = ve.getHanhKhach(); // Lấy từ VeSession (được set bởi CellPanel)

			if (hanhKhach == null) {
				// Không tìm thấy -> Tạo hành khách mới
				hanhKhach = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), row.getFullName(), row.getType(),
						row.getIdNumber());
				// TODO: Gọi khachHangBUS.themKhachHang(hanhKhach)
				ve.setHanhKhach(hanhKhach);
				System.out.println("Tạo hành khách mới: " + hanhKhach);
			}
			// (Nếu tìm thấy, nó đã được gán vào VeSession, không cần làm gì)
		}

		System.out.println("BookingSession đã được cập nhật.");

		// 4. Báo cho Controller cha
		if (onConfirmListener != null) {
			onConfirmListener.run();
		}
	}

	/**
	 * Xử lý logic khi bấm "Hủy"
	 */
	private void handleCancel() {
		// 1. Gọi BUS để hủy phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuChoChiTietByPgcID(bookingSession.getPhieuGiuCho().getPhieuGiuChoID());

		// 2. Nếu sau khi xóa mà không còn vé nào thì xóa luôn Phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuCho(bookingSession.getPhieuGiuCho().getPhieuGiuChoID());

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