package gui.application.form.thongTin;
/*
 * @(#) DoiMatKhauControllre.java  1.0  [4:54:35 PM] Nov 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 30, 2025
 * @version: 1.0
 */
import javax.swing.JOptionPane;

import bus.TaiKhoan_BUS;

public class DoiMatKhauController {
	private final TaiKhoan_BUS taiKhoanBUS = new TaiKhoan_BUS();
	private FormDoiMatKhau view;

	public DoiMatKhauController(FormDoiMatKhau view) {
		this.view = view;
		initController();
	}

	private void initController() {
		view.getBtnDoiMatKhau().addActionListener(e -> handleDoiMatKhau());
	}

	private void handleDoiMatKhau() {
		String mk = view.getTxtMatKhauHienTai().getText();
		String mkMoi = view.getTxtMatKhauMoi().getText();
		String mkMoiXacNhan = view.getTxtXacNhanMatKhauMoi().getText();

		if (!validate(mk, mkMoi, mkMoiXacNhan)) {
			return;
		}
		if (!taiKhoanBUS.isKhopMatKhau(view.getNhanVien().getNhanVienID(), mk)) {
			JOptionPane.showMessageDialog(view, "Mật khẩu hiện tại không khớp. Vui lòng nhập lại!");
			return;
		}

		if (!mkMoi.equals(mkMoiXacNhan)) {
			JOptionPane.showMessageDialog(view, "Mật khẩu xác nhận không khớp với mật khẩu mới. Vui lòng nhập lại!");
			return;
		}

		if (taiKhoanBUS.doiMatKhau(view.getNhanVien().getNhanVienID(), mkMoi)) {
			JOptionPane.showMessageDialog(view,
					String.format("Đổi mật khẩu mới cho tài khoản nhân viên %s - %s thành công.",
							view.getNhanVien().getHoTen(), view.getNhanVien().getVaiTroNhanVien().getDescription()));
		} else {
			JOptionPane.showMessageDialog(view, "Lỗi khi đổi mật khẩu. Vui lòng thử lại!");
		}
	}

	/**
	 * @param text
	 * @param text2
	 * @param text3
	 * @return
	 */
	private boolean validate(String matKhauHienTai, String matKhauMoi, String xacNhanMatKhauMoi) {
		// TODO Auto-generated method stub
		return true;
	}

}
