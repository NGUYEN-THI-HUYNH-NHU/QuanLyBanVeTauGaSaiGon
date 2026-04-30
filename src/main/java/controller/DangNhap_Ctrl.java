package controller;
/*
 * @(#) DangNhap_Ctrl.java  1.0  [10:55:26 AM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.impl.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;
import gui.application.AuthService;
import gui.application.UngDung;
import gui.application.form.FormDangNhap;
import gui.application.form.thongTin.ModalQuenMatKhau;
import gui.application.paymentHelper.NgrokRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalTime;

public class DangNhap_Ctrl {
    private final FormDangNhap view;
    ;
    private TaiKhoan_DAO taiKhoan_DAO = new TaiKhoan_DAO();

    public DangNhap_Ctrl(FormDangNhap view) {
        this.view = view;
        initController();
    }

    private void initController() {
        view.getBtnQuenMK().addActionListener(e -> handleQuenMatKhau());
        view.getBtnLogin().addActionListener(e -> dangNhap());
        view.getTxtTenDangNhap().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    view.getTxtMatKhau().requestFocus();
                }
            }
        });

        view.getTxtMatKhau().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dangNhap();
                }
            }
        });
    }

    private void handleQuenMatKhau() {
        // 1. Cấu hình Overlay
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(null);

        UngDung.getInstance().setGlassPane(overlay);
        overlay.setVisible(true);

        // 2. Tạo JDialog (Modal)
        JDialog dialog = new JDialog(UngDung.getInstance(), "Lấy lại mật khẩu - Hệ thống quản lý bán vé tàu Ga Sài Gòn",
                true);

        dialog.setSize(500, 700);

        // 3. Khởi tạo Panel và đưa vào Dialog
        ModalQuenMatKhau modal = new ModalQuenMatKhau(dialog);
        modal.getController().addResetPasswordListener(() -> {
            view.getTxtTenDangNhap().requestFocusInWindow();
        });

        dialog.setContentPane(modal);

        // Căn giữa Dialog so với
        dialog.setLocationRelativeTo(view);

        dialog.setVisible(true);

        overlay.setVisible(false);
    }

    private void dangNhap() {
        String tenDangNhap = view.getTxtTenDangNhap().getText();
        String matKhau = view.getTxtMatKhau().getText();
        NhanVien nhanVien = getNhanVienVoiTaiKhoan(tenDangNhap, matKhau);
        UngDung ungDung = UngDung.getInstance();

        if (nhanVien == null) {
            JOptionPane.showMessageDialog(view, "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            view.resetDangNhap();
        } else if (!checkDungCaLam(nhanVien)) {
            JOptionPane.showMessageDialog(view,
                    "Không thể đăng nhập vào ứng dụng Quản lý Bán vé tàu Ga Sài Gòn vì đây không phải ca làm của bạn.\nVui lòng đăng nhập khi đến ca làm của bạn!");
            view.resetDangNhap();
        } else {
            AuthService.getInstance().setCurrentUser(nhanVien);
            ungDung.createGiaoDienChinh(nhanVien);
            ungDung.setContentPane(ungDung.getGiaoDienChinh());
            if (nhanVien.getVaiTroNhanVien().getVaiTroNhanVienID().equals("NHAN_VIEN")) {
                UngDung.setSelectedMenu(2, 0);
                NgrokRunner.startNgrok();
            } else {
                UngDung.setSelectedMenu(1, 0);
            }
            SwingUtilities.updateComponentTreeUI(ungDung.getGiaoDienChinh());
        }
    }

    private NhanVien getNhanVienVoiTaiKhoan(String tenDangNhap, String matKhau) {
        return taiKhoan_DAO.getNhanVienByTenDangNhap(tenDangNhap, checkCredentials(tenDangNhap, matKhau));
    }

    private boolean checkCredentials(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = taiKhoan_DAO.getTaiKhoanVoiTenDangNhap(tenDangNhap);
        if (taiKhoan == null || !taiKhoan.getMatKhauHash().equals(matKhau)) {
            return false;
        }
//		if (taiKhoan == null || !BCrypt.checkpw(matKhau, taiKhoan.getMatKhauHash())) {
//			return false;
//		}
        return true;
    }

    private boolean checkDungCaLam(NhanVien nhanVien) {
        LocalTime gioVao = nhanVien.getCaLam().getGioVaoCa();
        LocalTime gioKet = nhanVien.getCaLam().getGioKetCa();
        LocalTime hienTai = LocalTime.now();

        // Ca không qua đêm
        if (gioVao.isBefore(gioKet)) {
            return !hienTai.isBefore(gioVao) && hienTai.isBefore(gioKet);
        } else {
            // Ca qua đêm
            return !hienTai.isBefore(gioVao) || hienTai.isBefore(gioKet);
        }
    }
}