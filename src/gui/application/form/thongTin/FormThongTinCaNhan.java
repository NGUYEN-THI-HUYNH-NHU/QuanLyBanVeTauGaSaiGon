package gui.application.form.thongTin;
/*
 * @(#) FormThongTinCaNhan.java  1.0  [1:01:02 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.swing.*;

import entity.NhanVien;
import net.miginfocom.swing.MigLayout;
import raven.crazypanel.CrazyPanel;

public class FormThongTinCaNhan extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private CrazyPanel pnlContainer;
    private JLabel lblNhanVienID, lblVaiTroNhanVien, lblHoTen, lblGioiTinh, lblNgaySinh,
                   lblSoDienThoai, lblEmail, lblDiaChi, lblNgayThamGia, lblTrangThaiHoatDong,
                   lblTitle;
    private JTextField txtNhanVienID, txtVaiTroNhanVien, txtHoTen, txtGioiTinh, txtNgaySinh,
                       txtSoDienThoai, txtEmail, txtDiaChi, txtNgayThamGia, txtTrangThaiHoatDong;

    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public FormThongTinCaNhan(NhanVien nhanVien) {
        setLayout(new BorderLayout());
        initComponents(nhanVien);
    }

    private void initComponents(NhanVien nhanVien) {
        pnlContainer = new CrazyPanel();
        lblTitle = new JLabel("Thông tin cá nhân");

        txtNhanVienID = new JTextField(20);
        txtVaiTroNhanVien = new JTextField(20);
        txtHoTen = new JTextField(20);
        txtGioiTinh = new JTextField(8);
        txtNgaySinh = new JTextField(12);
        txtSoDienThoai = new JTextField(15);
        txtEmail = new JTextField(20);
        txtDiaChi = new JTextField(25);
        txtNgayThamGia = new JTextField(12);
        txtTrangThaiHoatDong = new JTextField(12);

        txtNhanVienID.setText(nhanVien.getNhanVienID());
        txtVaiTroNhanVien.setText(nhanVien.getVaiTroNhanVien() != null ? nhanVien.getVaiTroNhanVien().getDescription() : "");
        txtHoTen.setText(nhanVien.getHoTen());
        txtGioiTinh.setText(nhanVien.isNu() ? "Nữ" : "Nam");
        LocalDate ngaySinh = nhanVien.getNgaySinh();
        txtNgaySinh.setText(ngaySinh != null ? ngaySinh.format(DATE_FORMAT) : "");
        txtSoDienThoai.setText(nhanVien.getSoDienThoai());
        txtEmail.setText(nhanVien.getEmail());
        txtDiaChi.setText(nhanVien.getDiaChi());
        LocalDate ngayThamGia = nhanVien.getNgayThamGia();
        txtNgayThamGia.setText(ngayThamGia != null ? ngayThamGia.format(DATE_FORMAT) : "");
        txtTrangThaiHoatDong.setText(nhanVien.isHoatDong() ? "Hoạt động" : "Ngưng hoạt động");

        lblNhanVienID = new JLabel("Mã nhân viên:");
        lblVaiTroNhanVien = new JLabel("Vai trò:");
        lblHoTen = new JLabel("Họ và tên:");
        lblGioiTinh = new JLabel("Giới tính:");
        lblNgaySinh = new JLabel("Ngày sinh:");
        lblSoDienThoai = new JLabel("Số điện thoại:");
        lblEmail = new JLabel("Email:");
        lblDiaChi = new JLabel("Địa chỉ:");
        lblNgayThamGia = new JLabel("Ngày tham gia:");
        lblTrangThaiHoatDong = new JLabel("Trạng thái:");

        lblTitle.setFont(new Font(lblTitle.getFont().getFontName(), Font.BOLD, 22));

        pnlContainer.setLayout(new MigLayout(
                "wrap 2, fillx, insets 10 20 10 20, gap 10",
                "[right]10[fill, grow]"
        ));

        pnlContainer.add(lblTitle, "wrap, span, align left, gapbottom 10");

        // Thêm cặp label + field theo đúng thứ tự
        pnlContainer.add(lblNhanVienID); pnlContainer.add(txtNhanVienID);
        pnlContainer.add(lblHoTen); pnlContainer.add(txtHoTen);
        pnlContainer.add(lblGioiTinh); pnlContainer.add(txtGioiTinh);
        pnlContainer.add(lblNgaySinh); pnlContainer.add(txtNgaySinh);
        pnlContainer.add(lblSoDienThoai); pnlContainer.add(txtSoDienThoai);
        pnlContainer.add(lblEmail); pnlContainer.add(txtEmail);
        pnlContainer.add(lblDiaChi); pnlContainer.add(txtDiaChi);
        pnlContainer.add(lblVaiTroNhanVien); pnlContainer.add(txtVaiTroNhanVien);
        pnlContainer.add(lblNgayThamGia); pnlContainer.add(txtNgayThamGia);
        pnlContainer.add(lblTrangThaiHoatDong); pnlContainer.add(txtTrangThaiHoatDong);

        // Wrap vào JScrollPane và thêm vào panel chính
        JScrollPane scrollPane = new JScrollPane(pnlContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Đặt tất cả TextField ở chế độ chỉ đọc
        for (JTextField tf : Arrays.asList(
                txtNhanVienID, txtVaiTroNhanVien, txtHoTen, txtGioiTinh, txtNgaySinh,
                txtSoDienThoai, txtEmail, txtDiaChi, txtNgayThamGia, txtTrangThaiHoatDong)) {
            tf.setEditable(false);
        }
    }
}
