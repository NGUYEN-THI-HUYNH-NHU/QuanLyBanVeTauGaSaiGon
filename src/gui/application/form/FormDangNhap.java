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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.itextpdf.text.Font;

import controller.DangNhap_Ctrl;
import entity.NhanVien;
import gui.application.UngDung;
//import gui.application.form.banVe.PanelBanVe;
import gui.application.form.thongTin.FormThongTinCaNhan;
import net.miginfocom.swing.MigLayout;

public class FormDangNhap extends JPanel {
	private static final long serialVersionUID = 1L;

    private JTextField txtTenDangNhap;
    private JTextField txtMatKhau;
    private JButton btnLogin;
    private DangNhap_Ctrl dangNhap_Ctrl;
    private JPanel loginPanel;
    private JLabel lblTitle;
    private JLabel lblTenDangNhap;
    private JLabel lblMatKhau;

    /**
     * Constructor khởi tạo giao diện đăng nhập, thiết lập layout và gán sự kiện cho các thành phần.
     */
    public FormDangNhap() {
        dangNhap_Ctrl = new DangNhap_Ctrl();
        setLayout(new MigLayout("fill", "[grow]", "[grow]"));

        loginPanel = new JPanel(new MigLayout("wrap 1, fillx", "[grow,fill]", "[]30[]5[]10[]5[]30[]"));
        loginPanel.setBorder(new EmptyBorder(20, 20, 40, 20));
        loginPanel.setPreferredSize(new Dimension(400, 300));
        loginPanel.setBackground(new Color(220, 220, 220));

        loginPanel.add(lblTitle = new JLabel("Đăng nhập", SwingConstants.CENTER), "align center");
        lblTitle.setFont(new java.awt.Font("", Font.BOLD, 24));
        loginPanel.add(lblTenDangNhap = new JLabel("Tên đăng nhập"));
        loginPanel.add(txtTenDangNhap = new JTextField());
        loginPanel.add(lblMatKhau = new JLabel("Mật khẩu"));
        loginPanel.add(txtMatKhau = new JTextField());
        loginPanel.add(btnLogin = new JButton("Đăng nhập"), "align center");
        
        add(loginPanel, "align center");
        addEvents();
    }

    /**
     * Thiết lập các sự kiện cho nút đăng nhập và phím Enter trên các trường nhập liệu.
     */
    private void addEvents() {
        btnLogin.addActionListener(e -> dangNhap());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dangNhap();
                }
            }
        };
        txtTenDangNhap.addKeyListener(enterKey);
        txtMatKhau.addKeyListener(enterKey);
    }

    /**
     * Thực hiện kiểm tra thông tin đăng nhập.
     * Nếu đúng, mở giao diện chính và hiển thị thông tin người dùng.
     * Nếu sai, hiển thị thông báo lỗi và reset lại form đăng nhập.
     */
    private void dangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getText().trim());
        NhanVien nhanVien = dangNhap_Ctrl.getNhanVienVoiTaiKhoan(tenDangNhap, matKhau);
        UngDung ungDung = UngDung.getInstance();

        if (nhanVien == null) {
            resetDangNhap();
        } else {
            ungDung.createGiaoDienChinh(nhanVien);
            ungDung.setContentPane(ungDung.getGiaoDienChinh());
//            ungDung.showGiaoDienChinh(new PanelBanVe(nhanVien));
            SwingUtilities.updateComponentTreeUI(ungDung.getGiaoDienChinh());
        }
    }

    /**
     * Xóa nội dung trong ô nhập tên đăng nhập và mật khẩu, đồng thời focus lại vào ô nhập tên.
     */
    public void resetDangNhap() {
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtTenDangNhap.requestFocus();
    }
}