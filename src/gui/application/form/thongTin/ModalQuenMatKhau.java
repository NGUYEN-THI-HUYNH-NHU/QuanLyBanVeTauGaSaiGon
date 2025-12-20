package gui.application.form.thongTin;

/*
 * @(#) PanelQuenMatKhau.java  1.0  [5:11:02 PM] Dec 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 19, 2025
 * @version: 1.0
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dao.TaiKhoan_DAO;
import gui.application.EmailService;

public class ModalQuenMatKhau extends JPanel {
	private JTextField txtMaNV;
	private JTextField txtCCCD;
	private JTextField txtEmail;
	private JButton btnGuiYeuCau;
	private JButton btnQuayLai;
	private JPasswordField txtCodeInput;
	private JPasswordField txtMatKhauMoi;
	private JPasswordField txtXacNhanMK;
	private JButton btnDoiMatKhau;
	private JLabel lblCode, lblPassNew, lblPassConfirm;

	private String generatedCode = "";
	private TaiKhoan_DAO taiKhoan_DAO = new TaiKhoan_DAO();
	private String verifiedMaNV = "";
	private JDialog parentDialog;

	private Runnable onResetPasswordSuccess;

	public void addResetPasswordListener(Runnable listener) {
		this.onResetPasswordSuccess = listener;
	}

	public ModalQuenMatKhau(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		initComponents();
		setSize(500, 700);
		controller();
	}

	private void initComponents() {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Tiêu đề
		JLabel lblTitle = new JLabel("QUÊN MẬT KHẨU", SwingUtilities.CENTER);
		lblTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 24));
		lblTitle.setForeground(new Color(36, 104, 155));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		add(lblTitle, gbc);

		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.WEST;

		// Mã Nhân Viên
		add(new JLabel("Mã Nhân Viên:"), setGBC(gbc, 0, 1));
		txtMaNV = new JTextField(20);
		add(txtMaNV, setGBC(gbc, 1, 1));

		// CCCD
		add(new JLabel("Số điện thoại:"), setGBC(gbc, 0, 2));
		txtCCCD = new JTextField(20);
		add(txtCCCD, setGBC(gbc, 1, 2));

		// Email
		add(new JLabel("Email đăng ký:"), setGBC(gbc, 0, 3));
		txtEmail = new JTextField(20);
		add(txtEmail, setGBC(gbc, 1, 3));

		// Nút Gửi yêu cầu
		btnGuiYeuCau = new JButton("Gửi mã xác nhận");
		btnGuiYeuCau.setBackground(new Color(36, 104, 155));
		btnGuiYeuCau.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnGuiYeuCau, gbc);

		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		// Mã xác nhận (Mật khẩu được cấp)
		lblCode = new JLabel("Mã xác nhận (qua Email):");
		add(lblCode, setGBC(gbc, 0, 5));
		txtCodeInput = new JPasswordField(20);
		add(txtCodeInput, setGBC(gbc, 1, 5));

		// Mật khẩu mới
		lblPassNew = new JLabel("Mật khẩu mới:");
		add(lblPassNew, setGBC(gbc, 0, 6));
		txtMatKhauMoi = new JPasswordField(20);
		add(txtMatKhauMoi, setGBC(gbc, 1, 6));

		// Xác nhận mật khẩu mới
		lblPassConfirm = new JLabel("Xác nhận mật khẩu:");
		add(lblPassConfirm, setGBC(gbc, 0, 7));
		txtXacNhanMK = new JPasswordField(20);
		add(txtXacNhanMK, setGBC(gbc, 1, 7));

		// Nút Đổi mật khẩu
		btnDoiMatKhau = new JButton("Đặt lại mật khẩu");
		btnDoiMatKhau.setBackground(new Color(36, 104, 155));
		btnDoiMatKhau.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		add(btnDoiMatKhau, gbc);

		// Nút Quay lại
		btnQuayLai = new JButton("Quay lại đăng nhập");
		gbc.gridy = 9;
		add(btnQuayLai, gbc);

		// Ẩn các trường giai đoạn 2
		togglePhase2(false);
	}

	// Helper để set vị trí nhanh
	private GridBagConstraints setGBC(GridBagConstraints gbc, int x, int y) {
		gbc.gridx = x;
		gbc.gridy = y;
		return gbc;
	}

	private void togglePhase2(boolean show) {
		lblCode.setVisible(show);
		txtCodeInput.setVisible(show);
		txtCodeInput.requestFocusInWindow();
		lblPassNew.setVisible(show);
		txtMatKhauMoi.setVisible(show);
		lblPassConfirm.setVisible(show);
		txtXacNhanMK.setVisible(show);
		btnDoiMatKhau.setVisible(show);

		// Khi hiện phase 2 thì ẩn nút gửi yêu cầu và disable các trường nhập cũ
		btnGuiYeuCau.setEnabled(!show);
		txtMaNV.setEditable(!show);
		txtCCCD.setEditable(!show);
		txtEmail.setEditable(!show);
	}

	private void controller() {
		txtMaNV.addActionListener(e -> {
			txtCCCD.requestFocusInWindow();
		});

		txtCCCD.addActionListener(e -> {
			txtEmail.requestFocusInWindow();
		});

		txtEmail.addActionListener(e -> {
			btnGuiYeuCau.doClick();
		});

		txtCodeInput.addActionListener(e -> {
			txtMatKhauMoi.requestFocusInWindow();
		});

		txtMatKhauMoi.addActionListener(e -> {
			txtXacNhanMK.requestFocusInWindow();
		});

		txtXacNhanMK.addActionListener(e -> {
			btnDoiMatKhau.doClick();
		});

		btnQuayLai.addActionListener(e -> {
			if (parentDialog != null) {
				parentDialog.dispose();
			}
		});

		btnGuiYeuCau.addActionListener(e -> handleGuiYeuCau());

		btnDoiMatKhau.addActionListener(e -> handleDoiMatKhau());
	}

	private void handleGuiYeuCau() {
		String maNV = txtMaNV.getText().trim();
		String cccd = txtCCCD.getText().trim();
		String email = txtEmail.getText().trim();

		if (maNV.isEmpty() || cccd.isEmpty() || email.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
			return;
		}

		// Kiểm tra dữ liệu
		boolean isValidUser = taiKhoan_DAO.checkForgotPasswordInfo(maNV, cccd, email);

		if (isValidUser) {
			verifiedMaNV = maNV;
			generatedCode = generateRandomCode(6);

			// Gửi email (Chạy luồng riêng để không đơ UI)
			btnGuiYeuCau.setText("Đang gửi...");
			btnGuiYeuCau.setEnabled(false);

			new Thread(() -> {
				boolean sent = EmailService.sendForgotPasswordEmail(email, generatedCode);
				SwingUtilities.invokeLater(() -> {
					if (sent) {
						JOptionPane.showMessageDialog(this, "Mã xác nhận đã được gửi đến email của bạn.");
						btnGuiYeuCau.setText("Đã gửi mã xác nhận");
						togglePhase2(true);
					} else {
						JOptionPane.showMessageDialog(this, "Lỗi gửi email. Vui lòng kiểm tra lại kết nối.", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						btnGuiYeuCau.setText("Gửi mã xác nhận");
						btnGuiYeuCau.setEnabled(true);
					}
				});
			}).start();

		} else {
			JOptionPane.showMessageDialog(this, "Thông tin không khớp với dữ liệu hệ thống!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleDoiMatKhau() {
		String inputCode = new String(txtCodeInput.getPassword());
		String newPass = new String(txtMatKhauMoi.getPassword());
		String confirmPass = new String(txtXacNhanMK.getPassword());

		// 1. Kiểm tra mã xác nhận
		if (!inputCode.equals(generatedCode)) {
			JOptionPane.showMessageDialog(this, "Mã xác nhận không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 2. Kiểm tra mật khẩu mới
//		if (newPass.isEmpty() || newPass.length() < 6) {
//			JOptionPane.showMessageDialog(this, "Mật khẩu mới phải từ 6 ký tự trở lên.");
//			return;
//		}
		if (newPass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Mật khẩu mới không được rỗng");
			return;
		}

		if (taiKhoan_DAO.checkDuplicatingPasswords(txtMaNV.getText(), newPass)) {
			JOptionPane.showMessageDialog(this, "Vui lòng đặt mật khẩu mới không trùng với mật khẩu gần nhất!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			txtMatKhauMoi.requestFocus();
			txtXacNhanMK.setText("");
			return;
		}

		if (!newPass.equals(confirmPass)) {
			JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 3. Cập nhật vào DB
		boolean success = taiKhoan_DAO.capNhatMatKhau(verifiedMaNV, newPass);
		if (success) {
			JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
			if (parentDialog != null) {
				parentDialog.dispose();
				if (onResetPasswordSuccess != null) {
					onResetPasswordSuccess.run();
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
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