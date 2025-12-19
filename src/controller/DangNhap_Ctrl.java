package controller;
/*
 * @(#) DangNhap_Ctrl.java  1.0  [10:55:26 AM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.type.VaiTroNhanVien;
import gui.application.AuthService;
import gui.application.UngDung;
import gui.application.form.FormDangNhap;
import gui.application.paymentHelper.NgrokRunner;

public class DangNhap_Ctrl {
	private TaiKhoan_DAO taiKhoan_DAO = new TaiKhoan_DAO();;
	private final FormDangNhap view;

	public DangNhap_Ctrl(FormDangNhap view) {
		this.view = view;
		initController();
	}

	private void initController() {

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

	private void dangNhap() {
		String tenDangNhap = view.getTxtTenDangNhap().getText();
		String matKhau = view.getTxtMatKhau().getText();
		NhanVien nhanVien = getNhanVienVoiTaiKhoan(tenDangNhap, matKhau);
		UngDung ungDung = UngDung.getInstance();

		if (nhanVien == null) {
			JOptionPane.showMessageDialog(view, "Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại.",
					"Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
			view.resetDangNhap();
		} else {
			AuthService.getInstance().setCurrentUser(nhanVien);
			ungDung.createGiaoDienChinh(nhanVien);
			ungDung.setContentPane(ungDung.getGiaoDienChinh());
			if (nhanVien.getVaiTroNhanVien() == VaiTroNhanVien.NHAN_VIEN) {
				UngDung.setSelectedMenu(2, 0);
			} else {
				UngDung.setSelectedMenu(1, 0);
			}
			NgrokRunner.startNgrok();
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
}