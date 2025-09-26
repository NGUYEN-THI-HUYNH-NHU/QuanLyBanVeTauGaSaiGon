package gui.application.form.thongTin;
/*
 * @(#) FormDoiMatKhau.java  1.0  [1:08:07 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.DangNhap_Ctrl;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import gui.application.UngDung;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;
import raven.toast.Notifications;
import raven.toast.Notifications.Location;

public class FormDoiMatKhau extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel currentPasswordLabel;
	private JLabel newPasswordLabel;
	private JLabel confirmNewPasswordLabel;
	private JTextField currentPasswordTextField;
	private JTextField newPasswordTextField;
	private JTextField confirmNewPasswordTextField;
	private CrazyPanel container;
	private JLabel imageSourceDisplay;
	private JButton changePasswordButton;
	private JLabel imforamtionLabel;
	private JLabel errorLabel;
	private TaiKhoan_DAO taiKhoan_DAO;
	private DangNhap_Ctrl dangNhap_Ctrl;

	public FormDoiMatKhau(NhanVien nhanVien) {
		dangNhap_Ctrl = new DangNhap_Ctrl();
		setLayout(new MigLayout("fill"));
		initComponents(nhanVien);
		taiKhoan_DAO = new TaiKhoan_DAO();
	}

	private void initComponents(NhanVien nhanVien) {
		container = new CrazyPanel();
		currentPasswordLabel = new JLabel("Mật khẩu hiện tại:");
		newPasswordLabel = new JLabel("Mật khẩu mới:");
		confirmNewPasswordLabel = new JLabel("Xác nhận mật khẩu mới:");
		currentPasswordTextField = new JPasswordField();
		newPasswordTextField = new JPasswordField();
		confirmNewPasswordTextField = new JPasswordField(55);
		changePasswordButton = new JButton("Đổi mật khẩu");
		imforamtionLabel = new JLabel(nhanVien.getHoTen() + " - " + nhanVien.getVaiTroNhanVien().toString());
		errorLabel = new JLabel();

		// Title label
		JLabel titleLabel = new JLabel("Đổi mật khẩu");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 25));
		imforamtionLabel.setFont(new Font(imforamtionLabel.getFont().getName(), Font.ITALIC, 20));
		errorLabel.setBorder(BorderFactory.createEmptyBorder());
		errorLabel.setForeground(Color.RED);
		errorLabel.setFont(errorLabel.getFont().deriveFont(Font.ITALIC));

		container.setLayout(new MigLayout("wrap 2, fillx, insets 8 50 8 50, gap 20", "[grow 0,trail]15[fill]"));

		container.add(titleLabel, "wrap, span, al left, gapbottom 8");
		container.add(imforamtionLabel, "span 2, al left");
		container.add(imageSourceDisplay, "wrap, span, al center, gapbottom 8");
		container.add(currentPasswordLabel, "skip 6");
		container.add(currentPasswordTextField);
		container.add(newPasswordLabel);
		container.add(newPasswordTextField);
		container.add(confirmNewPasswordLabel);
		container.add(confirmNewPasswordTextField);
		container.add(new JLabel(""));
		container.add(errorLabel, "al left");
		container.add(changePasswordButton, "span 2, al right");

		add(container);
	}

}