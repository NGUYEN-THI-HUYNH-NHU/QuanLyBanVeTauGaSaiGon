package controller.xacThuc;
/*
 * @(#) ThongTinNhanVienController.java  1.0  [5:58:30 PM] Nov 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.NhanVien_BUS;
import bus.NhatKyAudit_BUS;
import dto.NhanVienDTO;
import gui.application.form.thongTin.FormThongTinCaNhan;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ThongTinCaNhanController {

    private final NhanVien_BUS nhanVienBUS;
    private final FormThongTinCaNhan view;
    private final NhanVienDTO nhanVien;

    public ThongTinCaNhanController(FormThongTinCaNhan view) {
        this.view = view;
        this.nhanVien = view.getNhanVien();

        NhatKyAudit_BUS auditBus = new NhatKyAudit_BUS();
        this.nhanVienBUS = new NhanVien_BUS(auditBus);

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
                boolean success = nhanVienBUS.capNhatAvatar(nhanVien.getId(), imgBytes);

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