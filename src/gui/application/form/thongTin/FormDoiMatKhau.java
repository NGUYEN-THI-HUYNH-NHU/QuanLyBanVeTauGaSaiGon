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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import entity.NhanVien;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;

public class FormDoiMatKhau extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel lblMatKhauHienTai;
	private JLabel lblMatKhauMoi;
	private JLabel lblXacNhanMatKhauMoi;
	private JTextField txtMatKhauHienTai;
	private JTextField txtMatKhauMoi;
	private JTextField txtXacNhanMatKhauMoi;
	private CrazyPanel container;
	private JButton btnDoiMatKhau;
	private JLabel lblThongTin;
	private JLabel lblError;

	public FormDoiMatKhau(NhanVien nhanVien) {
		setLayout(new MigLayout("fill"));
		initComponents(nhanVien);
	}

	private void initComponents(NhanVien nhanVien) {
		container = new CrazyPanel();
		lblMatKhauHienTai= new JLabel("Mật khẩu hiện tại:");
		lblMatKhauMoi = new JLabel("Mật khẩu mới:");
		lblXacNhanMatKhauMoi = new JLabel("Xác nhận mật khẩu mới:");
		txtMatKhauHienTai = new JPasswordField();
		txtMatKhauMoi = new JPasswordField();
		txtXacNhanMatKhauMoi = new JPasswordField(55);
		btnDoiMatKhau = new JButton("Đổi mật khẩu");
		lblThongTin = new JLabel(nhanVien.getHoTen() + " - " + nhanVien.getVaiTroNhanVien().toString());
		lblError = new JLabel();

		// Title label
		JLabel titleLabel = new JLabel("Đổi mật khẩu");
		titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 25));
		lblThongTin.setFont(new Font(lblThongTin.getFont().getName(), Font.ITALIC, 20));
		lblError.setBorder(BorderFactory.createEmptyBorder());
		lblError.setForeground(Color.RED);
		lblError.setFont(lblError.getFont().deriveFont(Font.ITALIC));

		container.setLayout(new MigLayout("wrap 2, fillx, insets 8 50 8 50, gap 20", "[grow 0,trail]15[fill]"));

		container.add(titleLabel, "wrap, span, al left, gapbottom 8");
		container.add(lblThongTin, "span 2, al left");
		container.add(lblMatKhauHienTai, "skip 6");
		container.add(txtMatKhauHienTai);
		container.add(lblMatKhauMoi);
		container.add(txtMatKhauMoi);
		container.add(lblXacNhanMatKhauMoi);
		container.add(txtXacNhanMatKhauMoi);
		container.add(new JLabel(""));
		container.add(lblError, "al left");
		container.add(btnDoiMatKhau, "span 2, al right");

		add(container);
	}
}