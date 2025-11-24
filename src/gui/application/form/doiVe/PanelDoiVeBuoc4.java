package gui.application.form.doiVe;

/*
 * @(#) PanelDoiVeBuoc5.java  1.0  [12:22:28 PM] Nov 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 18, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;

public class PanelDoiVeBuoc4 extends JPanel {
	private JTextField txtGaDi;
	private JTextField txtGaDen;
	private JDateChooser dateChooserNgayDi;
	private JButton btnTimKiem;
	private JPanel pnlTimKiem;

	private DoiVeBuoc4Controller controller;

	public PanelDoiVeBuoc4() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(""));
		setPreferredSize(new Dimension(200, 0));

		// lblTieuDe
		JLabel lblTieuDe = new JLabel("Thông tin hành trình", SwingConstants.CENTER);
		lblTieuDe.setIcon(new FlatSVGIcon(getClass().getResource("/gui/icon/svg/map-search.svg")));
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 16f));
		lblTieuDe.setOpaque(true);
		lblTieuDe.setForeground(new Color(0, 145, 212));
		lblTieuDe.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 130, 196)));

		// Form
		pnlTimKiem = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 5, 10, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Ga đi
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlTimKiem.add(new JLabel("Ga đi:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1;
		txtGaDi = new JTextField();
		txtGaDi.setEditable(false);
		txtGaDi.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0;");
		pnlTimKiem.add(txtGaDi, gbc);

		// Ga đến
		gbc.gridx = 0;
		gbc.gridy = 1;
		pnlTimKiem.add(new JLabel("Ga đến:"), gbc);
		gbc.gridx = 1;
		txtGaDen = new JTextField();
		txtGaDen.setEditable(false);
		txtGaDen.putClientProperty(FlatClientProperties.STYLE, "background: #F0F0F0;");
		pnlTimKiem.add(txtGaDen, gbc);

		// Ngày đi
		gbc.gridx = 0;
		gbc.gridy = 2;
		pnlTimKiem.add(new JLabel("Ngày đi:"), gbc);
		gbc.gridx = 1;
		dateChooserNgayDi = new JDateChooser();
		dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
		dateChooserNgayDi.setDate(new java.util.Date());
		// Giới hạn không cho chọn ngày quá khứ
		dateChooserNgayDi.setMinSelectableDate(new java.util.Date());
		pnlTimKiem.add(dateChooserNgayDi, gbc);

		add(lblTieuDe, BorderLayout.NORTH);
		add(pnlTimKiem, BorderLayout.CENTER);
		add(btnTimKiem = new JButton("Tìm chuyến tàu mới"), BorderLayout.SOUTH);
	}

	public DoiVeBuoc4Controller getController() {
		return this.controller;
	}

	public void setController(DoiVeBuoc4Controller controller) {
		this.controller = controller;
	}

	public JTextField getTxtGaDi() {
		return txtGaDi;
	}

	public JTextField getTxtGaDen() {
		return txtGaDen;
	}

	public JDateChooser getDateNgayDi() {
		return dateChooserNgayDi;
	}

	public JButton getBtnTimKiem() {
		return btnTimKiem;
	}

	public String getGaDi() {
		return txtGaDi.getText();
	}

	public String getGaDen() {
		return txtGaDen.getText();
	}

	public LocalDate getNgayDi() {
		java.util.Date d = dateChooserNgayDi.getDate();
		if (d == null) {
			return null;
		}
		return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setComponentsEnabled(boolean enabled) {
		dateChooserNgayDi.setEnabled(enabled);
		btnTimKiem.setEnabled(enabled);
	}
}