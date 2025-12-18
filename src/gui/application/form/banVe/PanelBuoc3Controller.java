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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

		// 3. Enter trên SĐT -> focus Email
		view.getTxtPhoneNguoiMua().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getTxtEmailNguoiMua().requestFocusInWindow();
			}
		});

		// 4. Enter trên SĐT -> focus Nút Xác nhận
		view.getTxtEmailNguoiMua().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getConfirmButton().requestFocusInWindow();
			}
		});

		addValidateListener(view.getTxtCccdNguoiMua());
		addValidateListener(view.getTxtTenNguoiMua());
		addValidateListener(view.getTxtPhoneNguoiMua());
		addValidateListener(view.getTxtEmailNguoiMua());
	}

	// Kiểm tra lỗi ngay khi gõ
	private void addValidateListener(JTextField textField) {
		textField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				validate(textField);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				validate(textField);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				validate(textField);
			}
		});
	}

	private boolean validate(JTextField textField) {
		// 1. Check ID (CCCD/Hộ chiếu)
		if (view.getTxtCccdNguoiMua().isFocusOwner()) {
			String cccdNguoiMua = view.getTxtCccdNguoiMua().getText();
			if (cccdNguoiMua.isEmpty()) {
				showError("Vui lòng nhập CCCD/Hộ chiếu", view.getTxtCccdNguoiMua());
				return false;
			}
			// Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
			if (!cccdNguoiMua.matches("^[0-9]{12}$")) {
				showError("CCCD không đúng định dạng (12 ký số)", view.getTxtCccdNguoiMua());
				return false;
			}
		}
		// 2. Check Tên
		else if (view.getTxtTenNguoiMua().isFocusOwner()) {
			String tenNguoiMua = view.getTxtTenNguoiMua().getText();
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
		}
		// 3. Check số điện thoại
		else if (view.getTxtPhoneNguoiMua().isFocusOwner()) {
			String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText();
			if (phoneNguoiMua.isEmpty()) {
				showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
				return false;
			}
			// Regex: 10 số bắt đầu bằng 0
			if (!phoneNguoiMua.matches("^0[0-9]{9}$")) {
				showError("Số điện thoại gồm 10 số, bắt đầu bằng 0", view.getTxtPhoneNguoiMua());
				return false;
			}
		}
		// 4. Check email
		else if (view.getTxtEmailNguoiMua().isFocusOwner()) {
			String emailNguoiMua = view.getTxtEmailNguoiMua().getText();
			if (emailNguoiMua.isEmpty()) {
				showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
				return false;
			}
			if (!emailNguoiMua.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
				showError("Email không hợp lệ", view.getTxtEmailNguoiMua());
				return false;
			}
		}

		hideError();
		return true;
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
			view.getTxtEmailNguoiMua().setText(kh.getEmail());
			// Lưu khách hàng tìm thấy vào session
			bookingSession.setKhachHang(kh);
		} else {
			// Không tìm thấy -> Xóa dữ liệu cũ (nếu có)
			view.getTxtTenNguoiMua().setText("");
			view.getTxtPhoneNguoiMua().setText("");
			view.getTxtEmailNguoiMua().setText("");
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
		String emailNguoiMua = view.getTxtEmailNguoiMua().getText().trim();

		// 2. Validate
		if (!validate(cccdNguoiMua, tenNguoiMua, phoneNguoiMua, emailNguoiMua)) {
			return;
		}

		// --- DÙNG MAP ĐỂ TRÁNH TRÙNG LẶP TRONG PHIÊN XỬ LÝ ---
		// Key: Số giấy tờ (CCCD), Value: Đối tượng KhachHang
		Map<String, KhachHang> processedCustomers = new HashMap<>();

		// 3. Cập nhật Model (BookingSession)
		// 3a. Cập nhật thông tin Hành Khách vào từng VeSession
		for (PassengerRow row : rows) {
			VeSession ve = row.getVeSession();
			String cccdHanhKhach = row.getSoGiayTo();

			// Bước 1: Kiểm tra trong Map cục bộ (đã xử lý ở vòng lặp trước chưa?)
			KhachHang hanhKhach = processedCustomers.get(cccdHanhKhach);

			// Bước 2: Nếu chưa có trong Map, kiểm tra trong CSDL
			if (hanhKhach == null) {
				hanhKhach = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccdHanhKhach);
			}

			// Bước 3: Nếu chưa có ở đâu cả -> Tạo mới
			if (hanhKhach == null) {
				hanhKhach = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), row.getHoTen(), null, null,
						cccdHanhKhach, null, row.getLoaiDoiTuong(), LoaiKhachHang.HANH_KHACH);
				khachHangBUS.themKhachHang(hanhKhach);
				System.out.println("Tạo hành khách mới: " + hanhKhach.getHoTen());
			} else {
				// Nếu đã có, cập nhật thông tin mới nhất từ UI (ví dụ tên có thể sửa)
				hanhKhach.setHoTen(row.getHoTen());
				hanhKhach.setLoaiDoiTuong(row.getLoaiDoiTuong());
				khachHangBUS.capNhatKhachHang(hanhKhach);
			}

			// Bước 4: Lưu vào Map để dùng lại (cho vé khứ hồi hoặc cho người mua)
			processedCustomers.put(cccdHanhKhach, hanhKhach);

			// Gán vào vé
			ve.getVe().setKhachHang(hanhKhach);
		}

		// 3b. Cập nhật Khách hàng (Người Mua)
		// Ưu tiên 1: Lấy từ Map (nếu người mua chính là một trong các hành khách vừa
		// nhập)
		KhachHang nguoiMua = processedCustomers.get(cccdNguoiMua);

		// Ưu tiên 2: Nếu không phải hành khách, tìm trong CSDL (khách cũ)
		if (nguoiMua == null) {
			nguoiMua = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccdNguoiMua);
		}

		if (nguoiMua != null) {
			// === TRƯỜNG HỢP: NGƯỜI MUA ĐÃ TỒN TẠI (hoặc trùng với hành khách) ===
			// Cập nhật thông tin người mua
			nguoiMua.setHoTen(tenNguoiMua);
			nguoiMua.setSoDienThoai(phoneNguoiMua);
			nguoiMua.setEmail(emailNguoiMua);

			// Logic cập nhật loại khách hàng
			if (nguoiMua.getLoaiKhachHang() == LoaiKhachHang.HANH_KHACH) {
				// Nếu trước đây chỉ là hành khách, giờ thành Hành khách + Người mua
				nguoiMua.setLoaiKhachHang(LoaiKhachHang.HANH_KHACH_KHACH_HANG);
			}

			khachHangBUS.capNhatKhachHang(nguoiMua);
			System.out.println("Cập nhật thông tin người mua: " + nguoiMua.getHoTen());

		} else {
			// === TRƯỜNG HỢP: NGƯỜI MUA MỚI TINH (Và không đi tàu) ===
			nguoiMua = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), tenNguoiMua, phoneNguoiMua, emailNguoiMua,
					cccdNguoiMua, null, null, LoaiKhachHang.KHACH_HANG);
			khachHangBUS.themKhachHang(nguoiMua);
			System.out.println("Tạo người mua mới: " + nguoiMua.getHoTen());
		}

		// Lưu vào session
		bookingSession.setKhachHang(nguoiMua);

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
	private boolean validate(String cccdNguoiMua, String tenNguoiMua, String phoneNguoiMua, String emailNguoiMua) {
		// 1. Check ID (CCCD/Hộ chiếu)
		if (cccdNguoiMua.isEmpty()) {
			showError("Vui lòng nhập CCCD/Hộ chiếu", view.getTxtCccdNguoiMua());
			return false;
		}
		// Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
		if (!cccdNguoiMua.matches("^[0-9]{12}$")) {
			showError("CCCD không đúng định dạng (12 ký số)", view.getTxtCccdNguoiMua());
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

		if (emailNguoiMua.isEmpty()) {
			showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
			return false;
		}
		if (!emailNguoiMua.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
			showError("Email không hợp lệ", view.getTxtEmailNguoiMua());
			return false;
		}

		hideError();
		return true;
	}

	private void showError(String msg, JTextField textField) {
		view.getLblError().setText(msg);
		view.getLblError().setVisible(true);
		textField.requestFocusInWindow();
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
		if (bookingSession.getPhieuGiuCho() == null) {
			return;
		}
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