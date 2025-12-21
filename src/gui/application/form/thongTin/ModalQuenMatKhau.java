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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

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
	private JLabel lblCode, lblPassNew, lblPassConfirm, lblError;

	private JDialog parentDialog;

	private DoiMatKhauController controller;

	public ModalQuenMatKhau(JDialog parentDialog) {
		this.parentDialog = parentDialog;
		initComponents();
		setSize(500, 700);
		parentDialog.setResizable(false);
		this.controller = new DoiMatKhauController(this);
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

		lblError = new JLabel("Xác nhận mật khẩu:");
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font(lblError.getFont().getName(), Font.ITALIC, 11));
		lblError.setVisible(false);
		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		add(lblError, gbc);

		// Nút Đổi mật khẩu
		btnDoiMatKhau = new JButton("Đặt lại mật khẩu");
		btnDoiMatKhau.setBackground(new Color(36, 104, 155));
		btnDoiMatKhau.setForeground(Color.WHITE);
		gbc.gridy = 9;
		add(btnDoiMatKhau, gbc);

		// Nút Quay lại
		btnQuayLai = new JButton("Quay lại đăng nhập");
		gbc.gridy = 10;
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

	public void togglePhase2(boolean show) {
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

	public JTextField getTxtMaNV() {
		return txtMaNV;
	}

	public JTextField getTxtCCCD() {
		return txtCCCD;
	}

	public JTextField getTxtEmail() {
		return txtEmail;
	}

	public JButton getBtnGuiYeuCau() {
		return btnGuiYeuCau;
	}

	public JButton getBtnQuayLai() {
		return btnQuayLai;
	}

	public JPasswordField getTxtCodeInput() {
		return txtCodeInput;
	}

	public JPasswordField getTxtMatKhauMoi() {
		return txtMatKhauMoi;
	}

	public JPasswordField getTxtXacNhanMK() {
		return txtXacNhanMK;
	}

	public JButton getBtnDoiMatKhau() {
		return btnDoiMatKhau;
	}

	public JDialog getParentDialog() {
		return parentDialog;
	}

	public DoiMatKhauController getController() {
		return controller;
	}

	public JLabel getLblError() {
		return lblError;
	}

	public void setLblError(JLabel lblError) {
		this.lblError = lblError;
	}
}