package gui.application.form.thongTin;
/*
 * @(#) ThongTinNhanVienControlller.java  1.0  [5:58:30 PM] Nov 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import bus.NhanVien_BUS;
import entity.NhanVien;

public class ThongTinCaNhanController {

	private final NhanVien_BUS nhanVienBUS = new NhanVien_BUS();
	private final FormThongTinCaNhan view;
	private final NhanVien nhanVien;

	public ThongTinCaNhanController(FormThongTinCaNhan view) {
		this.view = view;
		this.nhanVien = view.getNhanVien();
		initController();
	}

	/**
	 * 
	 */
	private void initController() {
		// Sự kiện gọi Controller
		view.getBtnDoiHinh().addActionListener(e -> handleDoiHinh());
	}

	private void handleDoiHinh() {

		byte[] newImg = xuLyThayDoiAnhDaiDien();
		if (newImg != null) {
			view.hienThiAnh(newImg);
		}
	}

	/**
	 * 
	 * @param view     Component cha để hiển thị Dialog
	 * @param nhanVien Đối tượng nhân viên cần update
	 * @return byte[] hình ảnh mới nếu thành công, null nếu thất bại hoặc hủy
	 */
	public byte[] xuLyThayDoiAnhDaiDien() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn ảnh hồ sơ");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png", "jpeg"));

		int userSelection = fileChooser.showOpenDialog(view);

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToUpload = fileChooser.getSelectedFile();
			try {
				// 1. Đọc file
				byte[] imgBytes = Files.readAllBytes(fileToUpload.toPath());

				// 2. Validate ảnh (ví dụ kích thước - tuỳ chọn)
				if (imgBytes.length > 5 * 1024 * 1024) { // 5MB
					JOptionPane.showMessageDialog(view, "File ảnh quá lớn (>5MB)!", "Cảnh báo",
							JOptionPane.WARNING_MESSAGE);
					return null;
				}

				// 3. Gọi update
				boolean success = nhanVienBUS.capNhatAvatar(nhanVien.getNhanVienID(), imgBytes);

				if (success) {
					JOptionPane.showMessageDialog(view, "Cập nhật ảnh đại diện thành công!");
					// Cập nhật vào model trong bộ nhớ để đồng bộ
					nhanVien.setAvatar(imgBytes);
					return imgBytes;
				} else {
					JOptionPane.showMessageDialog(view, "Lỗi khi lưu vào cơ sở dữ liệu!", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(view, "Không thể đọc file ảnh: " + ex.getMessage(), "Lỗi IO",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}
}