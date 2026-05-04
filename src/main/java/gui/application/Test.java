package gui.application;
/*
 * @(#) Test.java  1.0  [4:41:18 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import com.formdev.flatlaf.FlatDarculaLaf;
import dto.NhanVienDTO;
import gui.application.form.banVe.PanelBanVe1;
import gui.application.form.khachHang.PanelQuanLyKhachHang;
import gui.application.form.khuyenMai.PanelQuanLyKhuyenMai;
import gui.application.form.nhanVien.PanelQuanLyNhanVien;
import gui.application.form.taiKhoan.PanelQuanLyTaiKhoan;
import tools.jackson.core.json.JsonFactory;

import javax.swing.*;
import java.awt.*;

public class Test {

    public static void main(String[] args) {
        // Tạo cửa sổ JFrame
//        JFrame frame = new JFrame("Test");
//        frame.add(new PanelBanVe1());
//        frame.setSize(1080, 700);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);

        NhanVienDTO nv = new NhanVienDTO();
        nv.setId("NV001");
        nv.setHoTen("Trần Văn Hoàng");
        nv.setVaiTroNhanVienID("QUAN-LY");

        JFrame frame = new JFrame("Kiểm thử");
//        frame.add(new PanelQuanLyKhachHang(nv), BorderLayout.CENTER);
        frame.add(new PanelQuanLyNhanVien(nv), BorderLayout.CENTER);
//        frame.add(new PanelQuanLyTaiKhoan(nv), BorderLayout.CENTER);
//        frame.add(new PanelQuanLyKhuyenMai(nv), BorderLayout.CENTER);

        // Cấu hình kích thước và vị trí hiển thị
        frame.setSize(1250, 850);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
