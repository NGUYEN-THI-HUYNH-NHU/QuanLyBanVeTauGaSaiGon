package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc1.java  1.0  [1:07:21 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.itextpdf.text.Font;

public class PanelHoanVeBuoc1 extends JPanel {
	private JTextField txtMaDDC;
	private JTextField txtCCCD;
	private JButton btnTraCuu;
	private JPanel pnlTraCuu;

	private HoanVeBuoc1Controller controller;

	public PanelHoanVeBuoc1() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(""));
		setPreferredSize(new Dimension(200, 250));

		// lblTieuDe
		JLabel lblTieuDe = new JLabel("Tra cứu đơn đặt chỗ", SwingConstants.CENTER);
		lblTieuDe.setIcon(new FlatSVGIcon(getClass().getResource("/gui/icon/svg/search-1.svg")));
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 16f));
		lblTieuDe.setOpaque(true);
		lblTieuDe.setForeground(new Color(0, 145, 212));
		lblTieuDe.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 130, 196)));

		// Form
		pnlTraCuu = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		// Mã đơn đặt chỗ
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 5, 5, 5);
		pnlTraCuu.add(new JLabel("Mã đơn đặt chỗ:"), gbc);
		gbc.gridy = 1;
		gbc.insets = new Insets(5, 5, 10, 5);
		txtMaDDC = new JTextField(15);
		txtMaDDC.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã đơn đặt chỗ");
		pnlTraCuu.add(txtMaDDC, gbc);

		// Số điện thoại người đặt chỗ
		gbc.gridy = 2;
		gbc.insets = new Insets(10, 5, 5, 5);
		pnlTraCuu.add(new JLabel("Số CCCD/Hộ chiếu:"), gbc);
		gbc.gridy = 3;
		gbc.insets = new Insets(5, 5, 10, 5);
		txtCCCD = new JTextField(15);
		txtCCCD.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Số CCCD/Hộ chiếu");
		pnlTraCuu.add(txtCCCD, gbc);

		add(lblTieuDe, BorderLayout.NORTH);
		add(pnlTraCuu, BorderLayout.CENTER);
		add(btnTraCuu = new JButton("Tra cứu"), BorderLayout.SOUTH);

		btnTraCuu.addActionListener(e -> {
			if (controller != null) {
				controller.performSearch();
			} else {
				JOptionPane.showMessageDialog(this, "PanelHoanVeBuoc1: HoanVe1Controller null");
			}
		});
	}

	public JTextField getTxtMaDDC() {
		return txtMaDDC;
	}

	public JTextField getTxtCCCD() {
		return txtCCCD;
	}

	public JButton getBtnTraCuu() {
		return btnTraCuu;
	}

	public JPanel getPnlTraCuu() {
		return pnlTraCuu;
	}

	public HoanVeBuoc1Controller getController() {
		return controller;
	}

	public void setController(HoanVeBuoc1Controller controller) {
		this.controller = controller;
	}

	/**
	 * @param enabled
	 */
	public void setComponentsEnabled(boolean enabled) {
		// TODO Auto-generated method stub
	}
}
