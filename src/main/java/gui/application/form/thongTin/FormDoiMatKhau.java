package gui.application.form.thongTin;
/*
 * @(#) FormDoiMatKhau.java  1.0  [1:08:07 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import dto.NhanVienDTO;
import entity.type.VaiTroNhanVienEnums;

import javax.swing.*;
import java.awt.*;

public class FormDoiMatKhau extends JPanel {
    private final NhanVienDTO nhanVien;
    private final DoiMatKhauController controller;
    private JLabel lblMatKhauHienTai;
    private JLabel lblMatKhauMoi;
    private JLabel lblXacNhanMatKhauMoi;
    private JTextField txtMatKhauHienTai;
    private JTextField txtMatKhauMoi;
    private JTextField txtXacNhanMatKhauMoi;
    private JButton btnDoiMatKhau;
    private JLabel lblThongTin;
    private JLabel lblError;

    public FormDoiMatKhau(NhanVienDTO nhanVien) {
        initComponents(nhanVien);
        this.nhanVien = nhanVien;
        this.controller = new DoiMatKhauController(this);
    }

    private void initComponents(NhanVienDTO nhanVien) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        lblMatKhauHienTai = new JLabel("Mật khẩu hiện tại:");
        lblMatKhauMoi = new JLabel("Mật khẩu mới:");
        lblXacNhanMatKhauMoi = new JLabel("Xác nhận mật khẩu mới:");
        txtMatKhauHienTai = new JPasswordField();
        txtMatKhauMoi = new JPasswordField();
        txtXacNhanMatKhauMoi = new JPasswordField(55);
        btnDoiMatKhau = new JButton("Đổi mật khẩu");
        lblThongTin = new JLabel(nhanVien.getHoTen() + " - " + VaiTroNhanVienEnums.valueOf(nhanVien.getVaiTroNhanVienID()));
        lblError = new JLabel();

        JLabel titleLabel = new JLabel("Đổi mật khẩu");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 25));
        lblThongTin.setFont(new Font(lblThongTin.getFont().getName(), Font.ITALIC, 20));
        lblError.setBorder(BorderFactory.createEmptyBorder());
        lblError.setForeground(Color.RED);
        lblError.setFont(lblError.getFont().deriveFont(Font.ITALIC));

        gbc.insets = new Insets(8, 50, 8, 50);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridy++;
        add(lblThongTin, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        add(lblMatKhauHienTai, gbc);

        gbc.gridx = 1;
        add(txtMatKhauHienTai, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(lblMatKhauMoi, gbc);

        gbc.gridx = 1;
        add(txtMatKhauMoi, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(lblXacNhanMatKhauMoi, gbc);

        gbc.gridx = 1;
        add(txtXacNhanMatKhauMoi, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel(""), gbc);

        gbc.gridx = 1;
        add(lblError, gbc);

        gbc.gridy++;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(btnDoiMatKhau, gbc);
    }

    public JTextField getTxtMatKhauHienTai() {
        return txtMatKhauHienTai;
    }

    public JTextField getTxtMatKhauMoi() {
        return txtMatKhauMoi;
    }

    public JTextField getTxtXacNhanMatKhauMoi() {
        return txtXacNhanMatKhauMoi;
    }

    public JButton getBtnDoiMatKhau() {
        return btnDoiMatKhau;
    }

    public NhanVienDTO getNhanVien() {
        return nhanVien;
    }
}