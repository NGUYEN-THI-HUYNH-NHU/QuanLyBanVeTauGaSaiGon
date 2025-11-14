package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1.java  1.0  [10:38:53 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.itextpdf.text.Font;
import com.toedter.calendar.JDateChooser;

public class PanelBuoc1 extends JPanel {
	private JTextField txtGaDi;
	private JTextField txtGaDen;
	private JDateChooser dateChooserNgayDi;
	private JDateChooser dateChooserNgayVe;
	private JRadioButton radMotChieu;
	private JRadioButton radKhuHoi;
	private JButton btnTimKiem;
	private JPanel pnlTimKiem;

	private PanelBuoc1Controller controller;

	public PanelBuoc1() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(""));

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
		txtGaDi = new JTextField();
		txtGaDi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ga đi");
		pnlTimKiem.add(txtGaDi, gbc);

		// Ga đến
		gbc.gridx = 0;
		gbc.gridy = 1;
		pnlTimKiem.add(new JLabel("Ga đến:"), gbc);
		gbc.gridx = 1;
		txtGaDen = new JTextField();
		txtGaDen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ga đến");
		pnlTimKiem.add(txtGaDen, gbc);

		// Loại hành trình
		gbc.gridx = 0;
		gbc.gridy = 2;
		pnlTimKiem.add(new JLabel("Loại vé:"), gbc);
		gbc.gridx = 1;
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radMotChieu = new JRadioButton("Một chiều", true);
		radKhuHoi = new JRadioButton("Khứ hồi");
		ButtonGroup group = new ButtonGroup();
		group.add(radMotChieu);
		group.add(radKhuHoi);
		radioPanel.add(radMotChieu);
		radioPanel.add(radKhuHoi);
		pnlTimKiem.add(radioPanel, gbc);

		// Ngày đi
		gbc.gridx = 0;
		gbc.gridy = 3;
		pnlTimKiem.add(new JLabel("Ngày đi:"), gbc);
		gbc.gridx = 1;
		dateChooserNgayDi = new JDateChooser();
		dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
		dateChooserNgayDi.setDate(new java.util.Date());
		pnlTimKiem.add(dateChooserNgayDi, gbc);

		// Ngày về
		gbc.gridx = 0;
		gbc.gridy = 4;
		pnlTimKiem.add(new JLabel("Ngày về:"), gbc);
		gbc.gridx = 1;
		dateChooserNgayVe = new JDateChooser();
		dateChooserNgayVe.setDateFormatString("dd/MM/yyyy");
		dateChooserNgayVe.setEnabled(false);
		pnlTimKiem.add(dateChooserNgayVe, gbc);

		radMotChieu.addActionListener(e -> dateChooserNgayVe.setEnabled(false));
		radKhuHoi.addActionListener(e -> dateChooserNgayVe.setEnabled(true));

		add(lblTieuDe, BorderLayout.NORTH);
		add(pnlTimKiem, BorderLayout.CENTER);
		add(btnTimKiem = new JButton("Tìm chuyến tàu"), BorderLayout.SOUTH);

		// ----- Gắn sự kiện (Event Handling) -----
		radMotChieu.addActionListener(e -> dateChooserNgayVe.setEnabled(false));
		radKhuHoi.addActionListener(e -> dateChooserNgayVe.setEnabled(true));
	}

	public void setController(PanelBuoc1Controller controller) {
		this.controller = controller;
	}

	public PanelBuoc1Controller getController() {
		return controller;
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

	public JDateChooser getDateNgayVe() {
		return dateChooserNgayVe;
	}

	public boolean isKhuHoi() {
		return radKhuHoi.isSelected();
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
		Instant instant = d.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public LocalDate getNgayVe() {
		java.util.Date d = dateChooserNgayVe.getDate();
		if (d == null) {
			return null;
		}
		Instant instant = d.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setComponentsEnabled(boolean enabled) {
		txtGaDi.setEnabled(enabled);
		txtGaDen.setEnabled(enabled);
		dateChooserNgayDi.setEnabled(enabled);
		dateChooserNgayVe.setEnabled(enabled);
		radMotChieu.setEnabled(enabled);
		radKhuHoi.setEnabled(enabled);
		btnTimKiem.setEnabled(enabled);
	}
}