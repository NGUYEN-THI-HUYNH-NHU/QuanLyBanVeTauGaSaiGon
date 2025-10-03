package controller;
/*
 * @(#) DangNhap_Ctrl.java  1.0  [10:55:26 AM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import org.mindrot.jbcrypt.BCrypt;

import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

public class DangNhap_Ctrl {
	private TaiKhoan_DAO taiKhoan_DAO;

	public DangNhap_Ctrl() {
		taiKhoan_DAO = new TaiKhoan_DAO();
	}

	public boolean checkCredentials(String tenDangNhap, String matKhau) {
//		TaiKhoan taiKhoan = taiKhoan_DAO.getTaiKhoanByTenDangNhap(tenDangNhap);
//		if (taiKhoan == null || !BCrypt.checkpw(matKhau, taiKhoan.getMatKhauHash())) {
//			return false;
//		}
        TaiKhoan taiKhoan = taiKhoan_DAO.getTaiKhoanVoiTenDangNhap(tenDangNhap);
        if (taiKhoan == null || !taiKhoan.getMatKhauHash().equals(matKhau)) {
            return false;
        }
		return true;
	}

	public NhanVien getNhanVienVoiTaiKhoan(String tenDangNhap, String matKhau) {
		return taiKhoan_DAO.getNhanVienByTenDangNhap(tenDangNhap, checkCredentials(tenDangNhap, matKhau));
	}
}
