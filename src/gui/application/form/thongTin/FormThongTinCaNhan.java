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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import entity.NhanVien;

public class FormThongTinCaNhan extends JPanel {
	private static final long serialVersionUID = 1L;

	// Components
	private JLabel lblAvatar;
	private JButton btnDoiHinh;
	private JTextField txtNhanVienID, txtVaiTro, txtHoTen, txtGioiTinh, txtNgaySinh, txtSDT, txtEmail, txtDiaChi,
			txtNgayThamGia, txtTrangThai;

	private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private NhanVien nhanVien;
	private ThongTinCaNhanController controller;

	public FormThongTinCaNhan(NhanVien nhanVien) {
		this.nhanVien = nhanVien;

		setLayout(new BorderLayout());
		initComponents();
		fillData();

		this.controller = new ThongTinCaNhanController(this);
	}

	private void initComponents() {
		// Main Container dùng GridBagLayout
		JPanel pnlMain = new JPanel(new GridBagLayout());
		pnlMain.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();

		// --- TITLE (Row 0) ---
		JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN");
		lblTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 24));
		lblTitle.setForeground(new Color(60, 60, 60));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 20, 50, 20);
		gbc.anchor = GridBagConstraints.CENTER;
		pnlMain.add(lblTitle, gbc);

		JPanel pnlAvatar = createAvatarPanel();

		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 20, 0, 20);
		pnlMain.add(pnlAvatar, gbc);

		JPanel pnlInfo = createInfoPanel();

		gbc.gridy = 2;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 50, 20, 50);
		pnlMain.add(pnlInfo, gbc);

		// Wrap vào ScrollPane
		JScrollPane scroll = new JScrollPane(pnlMain);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll, BorderLayout.CENTER);
	}

	// Tách Panel Avatar ra method riêng cho gọn
	private JPanel createAvatarPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);
		GridBagConstraints g = new GridBagConstraints();

		lblAvatar = new JLabel();
		lblAvatar.setPreferredSize(new Dimension(200, 200));
		lblAvatar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);

		btnDoiHinh = new JButton("Đổi ảnh đại diện");
		btnDoiHinh.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnDoiHinh.setFocusPainted(false);

		// Add Avatar Image
		g.gridx = 0;
		g.gridy = 0;
		p.add(lblAvatar, g);

		// Add Button
		g.gridy = 1;
		g.insets = new Insets(10, 0, 0, 0);
		p.add(btnDoiHinh, g);

		return p;
	}

	// Tách Panel Thông tin ra method riêng
	private JPanel createInfoPanel() {
		JPanel p = new JPanel(new GridBagLayout());
		p.setOpaque(false);

		// Init fields
		txtNhanVienID = createReadOnlyField();
		txtVaiTro = createReadOnlyField();
		txtHoTen = createReadOnlyField();
		txtGioiTinh = createReadOnlyField();
		txtNgaySinh = createReadOnlyField();
		txtSDT = createReadOnlyField();
		txtEmail = createReadOnlyField();
		txtDiaChi = createReadOnlyField();
		txtNgayThamGia = createReadOnlyField();
		txtTrangThai = createReadOnlyField();

		// Hàng 1
		addItem(p, "Mã nhân viên:", 0, 0);
		addItem(p, txtNhanVienID, 1, 0);
		addItem(p, "Vai trò:", 2, 0);
		addItem(p, txtVaiTro, 3, 0);

		// Hàng 2
		addItem(p, "Họ và tên:", 0, 1);
		addItem(p, txtHoTen, 1, 1);
		addItem(p, "Giới tính:", 2, 1);
		addItem(p, txtGioiTinh, 3, 1);

		// Hàng 3
		addItem(p, "Ngày sinh:", 0, 2);
		addItem(p, txtNgaySinh, 1, 2);
		addItem(p, "Số điện thoại:", 2, 2);
		addItem(p, txtSDT, 3, 2);

		// Hàng 4 (Email - Span 3 cột)
		addItem(p, "Email:", 0, 3);
		addFullWidthItem(p, txtEmail, 1, 3);

		// Hàng 5 (Địa chỉ - Span 3 cột)
		addItem(p, "Địa chỉ:", 0, 4);
		addFullWidthItem(p, txtDiaChi, 1, 4);

		// Hàng 6
		addItem(p, "Ngày tham gia:", 0, 5);
		addItem(p, txtNgayThamGia, 1, 5);
		addItem(p, "Trạng thái:", 2, 5);
		addItem(p, txtTrangThai, 3, 5);

		// Spacer để đẩy nội dung lên trên (nếu panel form cao hơn nội dung)
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.weighty = 1.0;
		p.add(new JLabel(), gbc);

		return p;
	}

	private void fillData() {
		if (nhanVien == null) {
			return;
		}

		txtNhanVienID.setText(nhanVien.getNhanVienID());
		txtVaiTro.setText(nhanVien.getVaiTroNhanVien().toString());
		txtHoTen.setText(nhanVien.getHoTen());
		txtGioiTinh.setText(nhanVien.isNu() ? "Nữ" : "Nam");
		txtNgaySinh.setText(formatDate(nhanVien.getNgaySinh()));
		txtSDT.setText(nhanVien.getSoDienThoai());
		txtEmail.setText(nhanVien.getEmail());
		txtDiaChi.setText(nhanVien.getDiaChi());
		txtNgayThamGia.setText(formatDate(nhanVien.getNgayThamGia()));
		txtTrangThai.setText(nhanVien.isHoatDong() ? "Đang hoạt động" : "Ngưng");

		hienThiAnh(nhanVien.getAvatar());
	}

	void hienThiAnh(byte[] imgData) {
		if (imgData != null && imgData.length > 0) {
			ImageIcon icon = new ImageIcon(imgData);
			Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
			lblAvatar.setIcon(new ImageIcon(img));
			lblAvatar.setText("");
		} else {
			lblAvatar.setIcon(null);
			lblAvatar.setText("Chưa có ảnh");
		}
	}

	// Thêm Label hoặc Field bình thường
	private void addItem(JPanel p, Object component, int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		if (component instanceof String) {
			JLabel lbl = new JLabel((String) component);
			lbl.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));
			p.add(lbl, gbc);
		} else if (component instanceof Component) {
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 0.5;
			p.add((Component) component, gbc);
		}
	}

	// Thêm Field chiếm hết chiều ngang còn lại (cho Email, Địa chỉ)
	private void addFullWidthItem(JPanel p, Component component, int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5, 5, 5, 5);
		p.add(component, gbc);
	}

	private String formatDate(LocalDate d) {
		return d != null ? d.format(DATE_FORMAT) : "";
	}

	private JTextField createReadOnlyField() {
		JTextField t = new JTextField(15);
		t.setEditable(false);
		t.setBackground(new Color(250, 250, 250));

		return t;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public JLabel getLblAvatar() {
		return lblAvatar;
	}

	public JButton getBtnDoiHinh() {
		return btnDoiHinh;
	}

	public JTextField getTxtNhanVienID() {
		return txtNhanVienID;
	}

	public JTextField getTxtVaiTro() {
		return txtVaiTro;
	}

	public JTextField getTxtHoTen() {
		return txtHoTen;
	}

	public JTextField getTxtGioiTinh() {
		return txtGioiTinh;
	}

	public JTextField getTxtNgaySinh() {
		return txtNgaySinh;
	}

	public JTextField getTxtSDT() {
		return txtSDT;
	}

	public JTextField getTxtEmail() {
		return txtEmail;
	}

	public JTextField getTxtDiaChi() {
		return txtDiaChi;
	}

	public JTextField getTxtNgayThamGia() {
		return txtNgayThamGia;
	}

	public JTextField getTxtTrangThai() {
		return txtTrangThai;
	}

	public DateTimeFormatter getDATE_FORMAT() {
		return DATE_FORMAT;
	}

	public ThongTinCaNhanController getController() {
		return controller;
	}
}