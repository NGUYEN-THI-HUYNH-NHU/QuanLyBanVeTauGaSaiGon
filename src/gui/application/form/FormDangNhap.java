
package gui.application.form;/*
								* @ (#) DangNhap.java   1.0     25/09/2025
								package gui.ungDunglication.form;
								
								
								/**
								* @description :
								* @author : Vy, Pham Kha Vy
								* @version 1.0
								* @created : 25/09/2025
								*/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.formdev.flatlaf.FlatClientProperties;

import controller.DangNhap_Ctrl;
import gui.tuyChinh.RoundedBorder;
import net.miginfocom.swing.MigLayout;

public class FormDangNhap extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField txtTenDangNhap;
	private JTextField txtMatKhau;
	private JButton btnLogin;
	private JPanel pnlLogin;
	private JLabel lblTitle;
	private Image backgroundImage;
	private JButton btnQuenMK;

	private final DangNhap_Ctrl dangNhap_Ctrl;

	public FormDangNhap() {
		backgroundImage = new ImageIcon(getClass().getResource("/gui/icon/png/dang-nhap.png")).getImage();

		setLayout(new GridBagLayout());
		setOpaque(false);

		pnlLogin = new JPanel(new MigLayout("wrap 1, fillx", "[grow,fill]", "[]40[]10[]10[]10[]40[]20[]"));
		pnlLogin.setBorder(new RoundedBorder(20, new Color(220, 220, 220), 1, true, new Color(230, 230, 230)));
		pnlLogin.setOpaque(false);
		pnlLogin.setPreferredSize(new Dimension(360, 400));

		pnlLogin.add(lblTitle = new JLabel("Đăng nhập", SwingConstants.CENTER));
		lblTitle.setFont(new Font("", Font.BOLD, 24));
		pnlLogin.add(new JLabel("Tên đăng nhập"));
		pnlLogin.add(txtTenDangNhap = new JTextField());
		txtTenDangNhap.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên đăng nhập");
		txtTenDangNhap.requestFocusInWindow();
		pnlLogin.add(new JLabel("Mật khẩu"));
		pnlLogin.add(txtMatKhau = new JPasswordField());
		txtMatKhau.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
		pnlLogin.add(btnQuenMK = new JButton("Quên mật khẩu?"));
		btnQuenMK.setForeground(new Color(7, 43, 143));
		pnlLogin.add(btnLogin = new JButton("Đăng nhập"));
		btnLogin.setBackground(new Color(36, 104, 155));
		btnLogin.setForeground(Color.WHITE);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 720, 0, 0);
		add(pnlLogin, gbc);

		dangNhap_Ctrl = new DangNhap_Ctrl(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();

		// vẽ ảnh nền
		if (backgroundImage != null) {
			g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}

		Color overlay = new Color(0, 94, 158, 30);
		g2.setColor(overlay);
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.dispose();
	}

	public void resetDangNhap() {
		txtTenDangNhap.setText("");
		txtMatKhau.setText("");
		txtTenDangNhap.requestFocus();
	}

	public JTextField getTxtTenDangNhap() {
		return txtTenDangNhap;
	}

	public JTextField getTxtMatKhau() {
		return txtMatKhau;
	}

	public JButton getBtnLogin() {
		return btnLogin;
	}

	public JButton getBtnQuenMK() {
		return btnQuenMK;
	}
}