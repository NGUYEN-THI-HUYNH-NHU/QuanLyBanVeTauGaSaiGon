
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import controller.DangNhap_Ctrl;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;
import gui.application.AuthService;
import gui.application.UngDung;
import gui.application.form.banVe.PanelBanVe;
import gui.application.form.dashboard.Dashboard;
import gui.tuyChinh.RoundedBorder;
import net.miginfocom.swing.MigLayout;

public class FormDangNhap extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField txtTenDangNhap;
	private JTextField txtMatKhau;
	private JButton btnLogin;
	private DangNhap_Ctrl dangNhap_Ctrl;
	private JPanel pnlLogin;
	private JLabel lblTitle;
	private JLabel lblTenDangNhap;
	private JLabel lblMatKhau;
	private Image backgroundImage;
	private JLabel lblQuenMK;

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
		pnlLogin.add(lblTenDangNhap = new JLabel("Tên đăng nhập"));
		pnlLogin.add(txtTenDangNhap = new JTextField());
		txtTenDangNhap.requestFocusInWindow();
		pnlLogin.add(lblMatKhau = new JLabel("Mật khẩu"));
		pnlLogin.add(txtMatKhau = new JPasswordField());
		pnlLogin.add(lblQuenMK = new JLabel("Quên mật khẩu?", JLabel.RIGHT));
		lblQuenMK.setForeground(new Color(7, 43, 143));
		pnlLogin.add(btnLogin = new JButton("Đăng nhập"));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 720, 0, 0);
		add(pnlLogin, gbc);

		addEvents();

		dangNhap_Ctrl = new DangNhap_Ctrl();
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

	private void addEvents() {
		btnLogin.addActionListener(e -> dangNhap());
		txtTenDangNhap.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					txtMatKhau.requestFocus();
				}
			}
		});

		txtMatKhau.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					dangNhap();
				}
			}
		});
	}

	private void dangNhap() {
		String tenDangNhap = txtTenDangNhap.getText().trim();
		String matKhau = new String(txtMatKhau.getText().trim());
		NhanVien nhanVien = dangNhap_Ctrl.getNhanVienVoiTaiKhoan(tenDangNhap, matKhau);
		UngDung ungDung = UngDung.getInstance();

		if (nhanVien == null) {
			JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.",
					"Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
			resetDangNhap();
		} else {
			AuthService.getInstance().setCurrentUser(nhanVien);
			ungDung.createGiaoDienChinh(nhanVien);
			ungDung.setContentPane(ungDung.getGiaoDienChinh());
			if (nhanVien.getVaiTroNhanVien() == VaiTroNhanVien.NHAN_VIEN) {
				ungDung.showGiaoDienChinh(new PanelBanVe(nhanVien));
			} else {
				ungDung.showGiaoDienChinh(new Dashboard());
			}
			SwingUtilities.updateComponentTreeUI(ungDung.getGiaoDienChinh());
		}
	}

	public void resetDangNhap() {
		txtTenDangNhap.setText("");
		txtMatKhau.setText("");
		txtTenDangNhap.requestFocus();
	}
}