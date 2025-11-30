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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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

		addClearErrorListener(view.getTxtCccdNguoiMua());
		addClearErrorListener(view.getTxtTenNguoiMua());
		addClearErrorListener(view.getTxtPhoneNguoiMua());
	}

	// Helper: Tự động ẩn lỗi khi user gõ
	private void addClearErrorListener(JTextField textField) {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				hideError();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				hideError();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				hideError();
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

	public boolean addHanhKhach(KhachHang hanhKhach) {
		if (hanhKhach != null) {
			String hanhKhachID = khachHangBUS.taoMaKhachHangTuDong();
			hanhKhach.setKhachHangID(hanhKhachID);
			return khachHangBUS.themKhachHang(hanhKhach);
		}
		return false;
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
		String tenNguoiMua = view.getTxtTenNguoiMua().getText().trim();
		String cccdNguoiMua = view.getTxtCccdNguoiMua().getText().trim();
		String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText().trim();

		// 2. Validate (Giữ nguyên)
		if (!validate(cccdNguoiMua, tenNguoiMua, phoneNguoiMua)) {
			return;
		}

		// 3. Cập nhật Model (BookingSession)
		// 3a. Cập nhật thông tin Hành Khách vào từng VeSession
		for (PassengerRow row : rows) {
			VeSession ve = row.getVeSession();
			KhachHang hanhKhach = ve.getVe().getKhachHang();
			if (hanhKhach == null) {
				// Không tìm thấy -> Tạo hành khách mới
				hanhKhach = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), row.getFullName(), null, null,
						row.getIdNumber(), null, row.getType(), LoaiKhachHang.HANH_KHACH);
				ve.getVe().setKhachHang(hanhKhach);
				khachHangBUS.themKhachHang(hanhKhach);
				System.out.println("Tạo hành khách mới: " + hanhKhach);
			}
			// (Nếu tìm thấy, nó đã được gán vào VeSession, không cần làm gì)
		}
		// 3b. Cập nhật Khách hàng (Người Mua)
		KhachHang nguoiMua = bookingSession.getKhachHang();
		if (nguoiMua == null) {
			// Không tìm thấy (hoặc không nhập) -> Tạo khách hàng mới
			boolean isHanhKhach = false;
			for (PassengerRow row : rows) {
				// Nếu khách hàng cũng là hành khách thì cập nhật loại khách hàng
				if (cccdNguoiMua.equalsIgnoreCase(row.getVeSession().getVe().getKhachHang().getKhachHangID())) {
					row.getVeSession().getVe().getKhachHang().setLoaiKhachHang(LoaiKhachHang.HANH_KHACH_KHACH_HANG);
					row.getVeSession().getVe().getKhachHang().setSoDienThoai(phoneNguoiMua);
					nguoiMua = row.getVeSession().getVe().getKhachHang();
					bookingSession.setKhachHang(nguoiMua);
					khachHangBUS.capNhatKhachHang(nguoiMua);
					isHanhKhach = true;
					System.out.println("Cập nhật hành khách: " + nguoiMua);
					break;
				}
			}
			// Nếu khách hàng khác hành khách
			if (!isHanhKhach) {
				nguoiMua = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), tenNguoiMua, phoneNguoiMua, null,
						cccdNguoiMua, null, null, LoaiKhachHang.KHACH_HANG);
				bookingSession.setKhachHang(nguoiMua);
				khachHangBUS.themKhachHang(nguoiMua);
				System.out.println("Tạo người mua mới: " + nguoiMua);
			}
		} else {
			// Tìm thấy -> Cập nhật lại thông tin (nếu người dùng sửa)
			nguoiMua.setHoTen(tenNguoiMua);
			nguoiMua.setSoDienThoai(phoneNguoiMua);
			khachHangBUS.capNhatKhachHang(nguoiMua);
			System.out.println("Cập nhật người mua: " + nguoiMua);
		}

		System.out.println("BookingSession đã được cập nhật.");

		// 4. Báo cho Controller cha
		if (onConfirmListener != null) {
			onConfirmListener.run();
		}
	}

	/**
	 * @param cccdNguoiMua
	 * @param tenNguoiMua
	 * @param phoneNguoiMua
	 * @return
	 */
	private boolean validate(String cccdNguoiMua, String tenNguoiMua, String phoneNguoiMua) {
		// 1. Check ID (CCCD/Hộ chiếu)
		if (cccdNguoiMua.isEmpty()) {
			showError("Vui lòng nhập CCCD/Hộ chiếu", view.getTxtCccdNguoiMua());
			return false;
		}
		// Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
		if (!cccdNguoiMua.matches("^[0-9]{12}$")) {
			showError("CCCD/Hộ chiếu không đúng định dạng (12 ký số)", view.getTxtCccdNguoiMua());
			return false;
		}

		// 2. Check Tên
		if (tenNguoiMua.isEmpty()) {
			showError("Vui lòng nhập họ tên", view.getTxtTenNguoiMua());
			return false;
		}
		// Regex: Chấp nhận chữ cái unicode (tiếng Việt), khoảng trắng, dấu chấm (nếu
		// cần)
		// [^0-9!@#...] -> Đơn giản là không chứa số và ký tự đặc biệt cơ bản
		if (tenNguoiMua.matches(".*\\d.*") || tenNguoiMua.matches(".*[!@#$%^&*()_+=<>?].*")) {
			showError("Tên không được chứa số hoặc ký tự đặc biệt", view.getTxtTenNguoiMua());
			return false;
		}

		// 2. Check số điện thoại
		if (phoneNguoiMua.isEmpty()) {
			showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
			return false;
		}
		// Regex: 10 số bắt đầu bằng 0
		if (!phoneNguoiMua.matches("^0[0-9]{9}$")) {
			showError("Số điện thoại gồm 10 số, bắt đầu bằng 0", view.getTxtPhoneNguoiMua());
			return false;
		}

		hideError();
		return true;
	}

	private void showError(String msg, JTextField textField) {
		view.getLblError().setText(msg);
		view.getLblError().setVisible(true);
		textField.requestFocusInWindow();
		textField.putClientProperty("JComponent.outline", "error");
	}

	private void hideError() {
		view.getLblError().setVisible(false);
		view.getLblError().setText("");
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