package gui.application.form.thongTin;
/*
 * @(#) DoiMatKhauControllre.java  1.0  [4:54:35 PM] Nov 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 30, 2025
 * @version: 1.0
 */
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import bus.TaiKhoan_BUS;
import gui.application.EmailService;
import gui.application.UngDung;

public class DoiMatKhauController {
	private final TaiKhoan_BUS taiKhoanBUS = new TaiKhoan_BUS();
	private FormDoiMatKhau formDoiMatKhau;
	private ModalQuenMatKhau modalQuenMatKhau;

	private String generatedCode = "";
	private String verifiedMaNV = "";

	private Runnable onResetPasswordSuccess;

	public void addResetPasswordListener(Runnable listener) {
		this.onResetPasswordSuccess = listener;
	}

	public DoiMatKhauController(FormDoiMatKhau formDoiMatKhau) {
		this.formDoiMatKhau = formDoiMatKhau;
		initChangeController();
	}

	public DoiMatKhauController(ModalQuenMatKhau modalQuenMatKhau) {
		this.modalQuenMatKhau = modalQuenMatKhau;
		initResetController();
	}

	private void initChangeController() {
		formDoiMatKhau.getTxtMatKhauHienTai().addActionListener(e -> {
			formDoiMatKhau.getTxtMatKhauMoi().requestFocusInWindow();
		});

		formDoiMatKhau.getTxtMatKhauMoi().addActionListener(e -> {
			formDoiMatKhau.getTxtXacNhanMatKhauMoi().requestFocusInWindow();
		});

		formDoiMatKhau.getTxtXacNhanMatKhauMoi().addActionListener(e -> {
			formDoiMatKhau.getBtnDoiMatKhau().doClick();
		});

		formDoiMatKhau.getBtnDoiMatKhau().addActionListener(e -> handleChangeMatKhau());
	}

	private void handleChangeMatKhau() {
		String mk = formDoiMatKhau.getTxtMatKhauHienTai().getText();
		String mkMoi = formDoiMatKhau.getTxtMatKhauMoi().getText();
		String mkMoiXacNhan = formDoiMatKhau.getTxtXacNhanMatKhauMoi().getText();

		if (mk.isEmpty()) {
			JOptionPane.showMessageDialog(formDoiMatKhau, "Vui lòng nhập mật khẩu hiện tại!");
			clearTextFields();
			return;
		}

		if (!taiKhoanBUS.isKhopMatKhau(formDoiMatKhau.getNhanVien().getNhanVienID(), mk)) {
			JOptionPane.showMessageDialog(formDoiMatKhau, "Mật khẩu hiện tại không khớp. Vui lòng nhập lại!");
			clearTextFields();
			return;
		}

		if (mkMoi.equals(mk)) {
			JOptionPane.showMessageDialog(formDoiMatKhau,
					"Mật khẩu mới không được trùng với mật khẩu hiện tại. Vui lòng nhập lại!");
			formDoiMatKhau.getTxtXacNhanMatKhauMoi().setText("");
			formDoiMatKhau.getTxtMatKhauMoi().requestFocusInWindow();
			return;
		}

		if (!mkMoi.equals(mkMoiXacNhan)) {
			JOptionPane.showMessageDialog(formDoiMatKhau,
					"Mật khẩu xác nhận không khớp với mật khẩu mới. Vui lòng nhập lại!");
			formDoiMatKhau.getTxtXacNhanMatKhauMoi().setText("");
			formDoiMatKhau.getTxtMatKhauMoi().requestFocusInWindow();
			return;
		}

		if (taiKhoanBUS.doiMatKhau(formDoiMatKhau.getNhanVien().getNhanVienID(), mkMoi)) {
			JOptionPane.showMessageDialog(formDoiMatKhau,
					String.format("Đổi mật khẩu mới cho tài khoản nhân viên %s - %s\nVui lòng đăng nhập lại",
							"Thành công", formDoiMatKhau.getNhanVien().getNhanVienID(),
							formDoiMatKhau.getNhanVien().getHoTen()));
			UngDung.dangXuat();
		} else {
			JOptionPane.showMessageDialog(formDoiMatKhau, "Lỗi khi đổi mật khẩu. Vui lòng thử lại!");
		}
		clearTextFields();
	}

	private void clearTextFields() {
		formDoiMatKhau.getTxtMatKhauHienTai().setText("");
		formDoiMatKhau.getTxtMatKhauMoi().setText("");
		formDoiMatKhau.getTxtXacNhanMatKhauMoi().setText("");
		formDoiMatKhau.getTxtMatKhauHienTai().requestFocusInWindow();
	}

	/**
	 * @param text
	 * @param text2
	 * @param text3
	 * @return
	 */
	private boolean validate(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi) {
		// TODO Auto-generated method stub
		return true;
	}

	private void initResetController() {
		modalQuenMatKhau.getTxtMaNV().addActionListener(e -> {
			modalQuenMatKhau.getTxtCCCD().requestFocusInWindow();
		});

		modalQuenMatKhau.getTxtCCCD().addActionListener(e -> {
			modalQuenMatKhau.getTxtEmail().requestFocusInWindow();
		});

		modalQuenMatKhau.getTxtEmail().addActionListener(e -> {
			modalQuenMatKhau.getBtnGuiYeuCau().doClick();
		});

		modalQuenMatKhau.getTxtCodeInput().addActionListener(e -> {
			modalQuenMatKhau.getTxtMatKhauMoi().requestFocusInWindow();
		});

		modalQuenMatKhau.getTxtMatKhauMoi().addActionListener(e -> {
			modalQuenMatKhau.getTxtXacNhanMK().requestFocusInWindow();
		});

		modalQuenMatKhau.getTxtXacNhanMK().addActionListener(e -> {
			modalQuenMatKhau.getBtnDoiMatKhau().doClick();
		});

		modalQuenMatKhau.getBtnQuayLai().addActionListener(e -> {
			if (modalQuenMatKhau.getParentDialog() != null) {
				modalQuenMatKhau.getParentDialog().dispose();
			}
		});

		modalQuenMatKhau.getBtnGuiYeuCau().addActionListener(e -> handleGuiYeuCau());

		modalQuenMatKhau.getBtnDoiMatKhau().addActionListener(e -> handleResetMatKhau());
	}

	private void handleGuiYeuCau() {
		String maNV = modalQuenMatKhau.getTxtMaNV().getText().trim();
		String cccd = modalQuenMatKhau.getTxtCCCD().getText().trim();
		String email = modalQuenMatKhau.getTxtEmail().getText().trim();

		if (maNV.isEmpty() || cccd.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}

		// Kiểm tra dữ liệu
		boolean isValidUser = taiKhoanBUS.kiemTraThongTinQuenMatKhau(maNV, cccd, email);

		if (isValidUser) {
			verifiedMaNV = maNV;
			generatedCode = generateRandomCode(6);

			// Gửi email (Chạy luồng riêng để không đơ UI)
			modalQuenMatKhau.getBtnGuiYeuCau().setText("Đang gửi...");
			modalQuenMatKhau.getBtnGuiYeuCau().setEnabled(false);

			new Thread(() -> {
				boolean sent = EmailService.sendForgotPasswordEmail(email, generatedCode);
				SwingUtilities.invokeLater(() -> {
					if (sent) {
						JOptionPane.showMessageDialog(modalQuenMatKhau, "Mã xác nhận đã được gửi đến email của bạn.");
						modalQuenMatKhau.getBtnGuiYeuCau().setText("Đã gửi mã xác nhận");
						modalQuenMatKhau.togglePhase2(true);
					} else {
						JOptionPane.showMessageDialog(modalQuenMatKhau, "Lỗi gửi email. Vui lòng kiểm tra lại kết nối.",
								"Lỗi", JOptionPane.ERROR_MESSAGE);
						modalQuenMatKhau.getBtnGuiYeuCau().setText("Gửi mã xác nhận");
						modalQuenMatKhau.getBtnGuiYeuCau().setEnabled(true);
					}
				});
			}).start();

		} else {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Thông tin không khớp với dữ liệu hệ thống!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleResetMatKhau() {
		String inputCode = new String(modalQuenMatKhau.getTxtCodeInput().getPassword());
		String newPass = new String(modalQuenMatKhau.getTxtMatKhauMoi().getPassword());
		String confirmPass = new String(modalQuenMatKhau.getTxtXacNhanMK().getPassword());

		// 1. Kiểm tra mã xác nhận
		if (!inputCode.equals(generatedCode)) {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Mã xác nhận không đúng!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 2. Kiểm tra mật khẩu mới
//		if (newPass.isEmpty() || newPass.length() < 6) {
//			JOptionPane.showMessageDialog(this, "Mật khẩu mới phải từ 6 ký tự trở lên.");
//			return;
//		}
		if (newPass.isEmpty()) {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Mật khẩu mới không được rỗng");
			return;
		}

		if (taiKhoanBUS.kiemTraDatLaiMatKhauTrung(modalQuenMatKhau.getTxtMaNV().getText(), newPass)) {
			JOptionPane.showMessageDialog(modalQuenMatKhau,
					"Vui lòng đặt mật khẩu mới không trùng với mật khẩu gần nhất!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			modalQuenMatKhau.getTxtMatKhauMoi().requestFocus();
			modalQuenMatKhau.getTxtXacNhanMK().setText("");
			return;
		}

		if (!newPass.equals(confirmPass)) {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Mật khẩu xác nhận không khớp!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 3. Cập nhật vào DB
		boolean success = taiKhoanBUS.capNhatMatKhau(verifiedMaNV, newPass);
		if (success) {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
			if (modalQuenMatKhau.getParentDialog() != null) {
				modalQuenMatKhau.getParentDialog().dispose();
				if (onResetPasswordSuccess != null) {
					onResetPasswordSuccess.run();
				}
			}
		} else {
			JOptionPane.showMessageDialog(modalQuenMatKhau, "Lỗi cập nhật CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String generateRandomCode(int length) {
		String chars = "0123456789";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return sb.toString();
	}
}