package gui.application.form.bieuGia;

/*
 * @(#) FormThemSuaBieuGia.java  1.0  [8:34:29 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;

import entity.BieuGiaVe;
import entity.Tuyen;
import entity.type.HangToa;
import entity.type.LoaiTau;

public class FormThemSuaBieuGia extends JDialog {
	private JTextField txtTuyenSuggest;
	private JComboBox<String> cboLoaiTau;
	private JComboBox<String> cboHangToa;
	private JTextField txtMinKm;
	private JTextField txtMaxKm;
	private JDateChooser dateBatDau;
	private JDateChooser dateKetThuc;

	private JRadioButton radTheoKm;
	private JRadioButton radCoDinh;
	private JTextField txtDonGiaKm;
	private JTextField txtGiaCoDinh;
	private JTextField txtPhuPhi;
	private JSpinner spinUuTien;
	private JButton btnLuu;
	private JButton btnHuy;

	private String currentID = null;

	public FormThemSuaBieuGia(Frame parent) {
		super(parent, "Thiết lập biểu giá vé", true);
		setSize(700, 540);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());

		// --- CONTENT ---
		JPanel pnlContent = new JPanel(new GridBagLayout());
		pnlContent.setBackground(Color.WHITE);
		pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 10, 5, 10);

		// --- SECTION A: ĐIỀU KIỆN ---
		addSectionTitle(pnlContent, "A. Điều kiện áp dụng", 0, gbc);

		gbc.gridy = 1;
		addLabelAndComp(pnlContent, "Tuyến áp dụng:", txtTuyenSuggest = createSearchField(), 0, 1, gbc);
		addLabelAndComp(pnlContent, "Loại tàu:", cboLoaiTau = new JComboBox<>(new String[] { "Tất cả", "SE1", "TN1" }),
				2, 1, gbc);

		gbc.gridy = 2;
		addLabelAndComp(pnlContent, "Hạng toa:",
				cboHangToa = new JComboBox<>(new String[] { "Tất cả", "Ngồi mềm", "Giường nằm" }), 0, 2, gbc);
		addLabelAndComp(pnlContent, "Khoảng cách (Km):", createKmPanel(), 2, 2, gbc);

		gbc.gridy = 3;
		dateBatDau = new JDateChooser(new Date());
		dateBatDau.setDateFormatString("dd/MM/yyyy");
		dateKetThuc = new JDateChooser();
		dateKetThuc.setDateFormatString("dd/MM/yyyy");
		addLabelAndComp(pnlContent, "Hiệu lực từ:", dateBatDau, 0, 3, gbc);
		addLabelAndComp(pnlContent, "Đến ngày:", dateKetThuc, 2, 3, gbc);

		// --- SECTION B: CÔNG THỨC GIÁ ---
		addSectionTitle(pnlContent, "B. Công thức tính giá", 4, gbc);

		txtDonGiaKm = new JTextField();
		txtGiaCoDinh = new JTextField();
		txtGiaCoDinh.setEnabled(false);
		txtPhuPhi = new JTextField("0");

		radTheoKm = new JRadioButton("Giá theo Km (VNĐ/Km)");
		radTheoKm.setSelected(true);
		radCoDinh = new JRadioButton("Giá trọn gói (VNĐ)");
		ButtonGroup bg = new ButtonGroup();
		bg.add(radTheoKm);
		bg.add(radCoDinh);

		// Logic Radio
		radTheoKm.addActionListener(e -> {
			txtDonGiaKm.setEnabled(true);
			txtGiaCoDinh.setEnabled(false);
		});
		radCoDinh.addActionListener(e -> {
			txtDonGiaKm.setEnabled(false);
			txtGiaCoDinh.setEnabled(true);
		});

		gbc.gridy = 5;
		pnlContent.add(radTheoKm, gbcPos(0, 5, gbc));
		pnlContent.add(txtDonGiaKm, gbcPos(1, 5, gbc));
		pnlContent.add(radCoDinh, gbcPos(2, 5, gbc));
		pnlContent.add(txtGiaCoDinh, gbcPos(3, 5, gbc));

		gbc.gridy = 6;
		addLabelAndComp(pnlContent, "Phụ phí cao điểm:", txtPhuPhi, 0, 6, gbc);

		// --- SECTION C: KHÁC ---
		addSectionTitle(pnlContent, "C. Cấu hình khác", 7, gbc);

		gbc.gridy = 8;
		spinUuTien = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
		addLabelAndComp(pnlContent, "Độ ưu tiên (Cao > Thấp):", spinUuTien, 0, 8, gbc);

		add(new JScrollPane(pnlContent), BorderLayout.CENTER);

		// --- FOOTER BUTTONS ---
		JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
		pnlBtn.setBackground(new Color(245, 245, 245));
		pnlBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

		btnHuy = new JButton("Hủy bỏ");
		btnLuu = new JButton("Lưu thay đổi");
		btnLuu.setBackground(new Color(38, 117, 191));
		btnLuu.setForeground(Color.WHITE);
		btnLuu.setIcon(new FlatSVGIcon("gui/icon/svg/save.svg", 0.8f));

		pnlBtn.add(btnHuy);
		pnlBtn.add(btnLuu);
		add(pnlBtn, BorderLayout.SOUTH);

		btnHuy.addActionListener(e -> dispose());
	}

	private GridBagConstraints gbcPos(int x, int y, GridBagConstraints gbc) {
		gbc.gridx = x;
		gbc.gridy = y;
		return gbc;
	}

	private void addSectionTitle(JPanel p, String title, int y, GridBagConstraints gbc) {
		JLabel lbl = new JLabel(title);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
		lbl.setForeground(new Color(0, 102, 204));
		lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(20, 10, 10, 10);
		p.add(lbl, gbc);

		gbc.gridwidth = 1;
		gbc.insets = new Insets(5, 10, 5, 10);
	}

	private void addLabelAndComp(JPanel p, String text, Component comp, int x, int y, GridBagConstraints gbc) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weightx = 0;
		p.add(new JLabel(text), gbc);

		gbc.gridx = x + 1;
		gbc.weightx = 0.5;
		p.add(comp, gbc);
	}

	private JTextField createSearchField() {
		JTextField txt = new JTextField();
		txt.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên hoặc mã tuyến...");
		txt.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
				new FlatSVGIcon("gui/icon/svg/search.svg", 0.6f));
		return txt;
	}

	private JPanel createKmPanel() {
		JPanel p = new JPanel(new GridLayout(1, 2, 5, 0));
		p.setOpaque(false);
		txtMinKm = new JTextField("0");
		txtMinKm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Min");
		txtMaxKm = new JTextField("9999");
		txtMaxKm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Max");
		p.add(txtMinKm);
		p.add(txtMaxKm);
		return p;
	}

	public JTextField getTxtTuyenSuggest() {
		return txtTuyenSuggest;
	}

	public void addBtnLuuListener(java.awt.event.ActionListener l) {
		btnLuu.addActionListener(l);
	}

	public BieuGiaVe getModelFromForm() {
		BieuGiaVe bg = new BieuGiaVe();
		bg.setBieuGiaVeID(this.currentID);

		// 1. Lấy dữ liệu từ combo ("Tất cả" -> null)
		if (!txtTuyenSuggest.getText().trim().equals("")) {
			bg.setTuyenApDung(new Tuyen(txtTuyenSuggest.getText().trim()));
		}

		if (cboLoaiTau.getSelectedIndex() > 0) {
			bg.setLoaiTauApDung(LoaiTau.valueOf(cboLoaiTau.getSelectedItem().toString()));
		}

		if (cboHangToa.getSelectedIndex() > 0) {
			bg.setHangToaApDung(HangToa.valueOf(cboHangToa.getSelectedItem().toString()));
		}

		// 2. Parse số liệu
		try {
			bg.setMinKm(Integer.parseInt(txtMinKm.getText().trim()));
			bg.setMaxKm(Integer.parseInt(txtMaxKm.getText().trim()));
			bg.setPhuPhiCaoDiem(Double.parseDouble(txtPhuPhi.getText().trim()));

			if (radTheoKm.isSelected()) {
				bg.setDonGiaTrenKm(Double.parseDouble(txtDonGiaKm.getText().trim()));
				bg.setGiaCoBan(0);
			} else {
				bg.setGiaCoBan(Double.parseDouble(txtGiaCoDinh.getText().trim()));
				bg.setDonGiaTrenKm(0);
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Dữ liệu số (Km/Giá) không hợp lệ!");
		}

		bg.setDoUuTien((int) spinUuTien.getValue());

		// 3. Parse ngày
		try {
			bg.setNgayBatDau(toLocalDate(dateBatDau.getDate()));
			bg.setNgayKetThuc(toLocalDate(dateKetThuc.getDate()));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage());
		}

		return bg;
	}

	public void setModelToForm(BieuGiaVe bg) {
		this.currentID = bg.getBieuGiaVeID();

		// 1. Set ComboBox
		txtTuyenSuggest.setText(bg.getTuyenApDung() == null ? "Tất cả" : bg.getTuyenApDung().getTuyenID());
		cboLoaiTau.setSelectedItem(bg.getLoaiTauApDung() == null ? "Tất cả" : bg.getLoaiTauApDung().toString());
		cboHangToa.setSelectedItem(bg.getHangToaApDung() == null ? "Tất cả" : bg.getHangToaApDung().toString());

		// 2. Set Textfield
		txtMinKm.setText(String.valueOf(bg.getMinKm()));
		txtMaxKm.setText(String.valueOf(bg.getMaxKm()));
		txtPhuPhi.setText(String.valueOf(bg.getPhuPhiCaoDiem()));

		// 3. Set Radio & Giá
		if (bg.getGiaCoBan() > 0) {
			radCoDinh.setSelected(true);
			txtGiaCoDinh.setEnabled(true);
			txtDonGiaKm.setEnabled(false);
			txtGiaCoDinh.setText(String.valueOf(bg.getGiaCoBan()));
			txtDonGiaKm.setText("");
		} else {
			radTheoKm.setSelected(true);
			txtDonGiaKm.setEnabled(true);
			txtGiaCoDinh.setEnabled(false);
			txtDonGiaKm.setText(String.valueOf(bg.getDonGiaTrenKm()));
			txtGiaCoDinh.setText("");
		}

		// 4. Set Ưu tiên & Ngày
		spinUuTien.setValue(bg.getDoUuTien());
		dateBatDau.setDate(toDate(bg.getNgayBatDau()));
		if (bg.getNgayKetThuc() != null) {
			dateKetThuc.setDate(toDate(bg.getNgayKetThuc()));
		} else {
			dateKetThuc.setEnabled(false);
		}

	}

	private LocalDate toLocalDate(Date date) {
		if (date == null) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private Date toDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public void enableViewMode() {
		this.setTitle("Chi tiết biểu giá vé");

		btnLuu.setVisible(false);
		btnHuy.setText("Đóng");
		btnHuy.setFocusable(true);

		txtTuyenSuggest.setEditable(false);
		cboLoaiTau.setEnabled(false);
		cboHangToa.setEnabled(false);
		txtMinKm.setEditable(false);
		txtMaxKm.setEditable(false);
		dateBatDau.setEnabled(false);
		dateKetThuc.setEnabled(false);

		radTheoKm.setEnabled(false);
		radCoDinh.setEnabled(false);
		txtDonGiaKm.setEditable(false);
		txtGiaCoDinh.setEditable(false);
		txtPhuPhi.setEditable(false);

		spinUuTien.setEnabled(false);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				btnHuy.requestFocusInWindow();
			}
		});
	}
}