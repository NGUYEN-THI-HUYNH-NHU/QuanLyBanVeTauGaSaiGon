package gui.application.form;/*
								* @ (#) DangNhap.java   1.0     25/09/2025
								package gui.ungDunglication.form;
								
								
								/**
								* @description :
								* @author : Vy, Pham Kha Vy
								* @version 1.0
								* @created : 25/09/2025
								*/

import com.formdev.flatlaf.FlatClientProperties;
import controller.xacThuc.DangNhap_Ctrl;
import gui.tuyChinh.RoundedBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class FormDangNhap extends JPanel {
    private static final long serialVersionUID = 1L;
    private final DangNhap_Ctrl dangNhap_Ctrl;
    private JTextField txtTenDangNhap;
    private JTextField txtMatKhau;
    private JButton btnLogin;
    private JPanel pnlLogin;
    private JLabel lblTitle;
    private Image backgroundImage;
    private JButton btnQuenMK;

    public FormDangNhap() {
        backgroundImage = new ImageIcon(getClass().getResource("/icon/png/dang-nhap.png")).getImage();

        setLayout(new GridBagLayout());
        setOpaque(false);

        pnlLogin = new JPanel(new MigLayout("wrap 1, fillx", "[grow,fill]", "[]40[]10[]10[]10[]40[]60[]"));
        pnlLogin.setBorder(new RoundedBorder(20, new Color(220, 220, 220), 1, true, new Color(250, 250, 250)));
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
        pnlLogin.add(btnLogin = new JButton("Đăng nhập"));
        btnLogin.setBackground(new Color(0, 95, 159));
        pnlLogin.add(btnQuenMK = new JButton("Quên mật khẩu?"));
        btnQuenMK.setForeground(new Color(7, 43, 143));
        btnQuenMK.setBorderPainted(false);
        btnQuenMK.setContentAreaFilled(false);
        btnQuenMK.setFocusPainted(false);
        btnQuenMK.setMargin(new Insets(50, 0, 0, 0));
        btnQuenMK.setText("<html><u>Quên mật khẩu?</u></html>");
        btnQuenMK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnQuenMK.setForeground(new Color(0, 81, 204));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnQuenMK.setForeground(new Color(7, 43, 143));
            }
        });
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