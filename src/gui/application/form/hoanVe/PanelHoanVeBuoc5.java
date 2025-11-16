package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc5.java  1.0  [2:15:29 PM] Nov 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 14, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class PanelHoanVeBuoc5 extends JPanel {
	private JRadioButton radTienMat;
	private JLabel lblTongTienVe;
	private JLabel lblTongPhiHoan;
	private JLabel lblTongTienHoan;
	private JButton btnXacNhanVaInCash;

	private JPanel pnlTienHoan;
	private JPanel pnlPaymentMethodContainer;
	private CardLayout paymentCardLayout;

	private DecimalFormat currencyFormat;

	private int tongTienHoan = 0;
	private JPanel pnlChiTiet;

	private static final String TIEN_MAT_CARD = "TienMat";

	public PanelHoanVeBuoc5() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Thanh Toán"));

		currencyFormat = new DecimalFormat("#,##0đ");

		// Panel Phương thức thanh toán
		JPanel pnlPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		radTienMat = new JRadioButton("Tiền mặt", true);
		ButtonGroup bgPayment = new ButtonGroup();
		bgPayment.add(radTienMat);
		pnlPhuongThuc.add(radTienMat);
		add(pnlPhuongThuc, BorderLayout.NORTH);

		JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 0));
		pnlChiTiet = createChiTietPanel();

		paymentCardLayout = new CardLayout();
		pnlPaymentMethodContainer = new JPanel(paymentCardLayout);

		pnlTienHoan = createTienHoanPanel();

		pnlPaymentMethodContainer.add(pnlTienHoan, TIEN_MAT_CARD);

		pnlMain.add(pnlChiTiet);
		pnlMain.add(pnlPaymentMethodContainer);

		add(pnlMain, BorderLayout.CENTER);

		// Logic nội bộ
		addInternalLogic();

		// Show cash panel initially
		paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);
	}

	private JPanel createChiTietPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.CENTER;

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel("Tổng tiền vé:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongTienVe = new JLabel("0 VND");
		pnl.add(lblTongTienVe, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Tổng phí hoàn:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongPhiHoan = new JLabel("0 VND", JLabel.RIGHT);
		lblTongPhiHoan.setForeground(Color.RED);
		pnl.add(lblTongPhiHoan, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Tổng tiền hoàn:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongTienHoan = new JLabel("0 VND");
		lblTongTienHoan.setForeground(Color.GREEN);
		pnl.add(lblTongTienHoan, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		JLabel totalLabel = new JLabel("Tổng tiền hoàn:");
		totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
		pnl.add(totalLabel, gbc);

		gbc.gridy = 4;
		gbc.weighty = 1.0;
		pnl.add(new JLabel(), gbc);

		return pnl;
	}

	private JPanel createTienHoanPanel() {
		pnlTienHoan = new JPanel(new GridBagLayout());
		pnlTienHoan.setBorder(BorderFactory.createTitledBorder("Tiền mặt"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlTienHoan.add(new JLabel("Tiền hoàn:"), gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		JLabel lblTienHoanAmount = new JLabel(tongTienHoan + "");
		lblTienHoanAmount.setFont(lblTienHoanAmount.getFont().deriveFont(Font.BOLD, 18f));
		lblTienHoanAmount.setForeground(Color.GREEN);
		pnlTienHoan.add(lblTienHoanAmount, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(15, 5, 5, 5);
		btnXacNhanVaInCash = new JButton("Xác nhận hoàn vé");
		btnXacNhanVaInCash.setFont(btnXacNhanVaInCash.getFont().deriveFont(Font.BOLD, 14f));
		btnXacNhanVaInCash.setBackground(new Color(0, 153, 51));
		btnXacNhanVaInCash.setForeground(Color.WHITE);
		pnlTienHoan.add(btnXacNhanVaInCash, gbc);

		gbc.gridy = 3;
		gbc.weighty = 1.0;
		pnlTienHoan.add(new JLabel(), gbc);

		return pnlTienHoan;
	}

	private void addInternalLogic() {
		ActionListener paymentMethodListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTienHoanAmount();
				paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);

			}
		};
		radTienMat.addActionListener(paymentMethodListener);
	}

	public int getTongTienHoan() {
		return tongTienHoan;
	}

	private void setTienMatEnabled(boolean enabled) {
		for (Component c : pnlTienHoan.getComponents()) {
			if (c instanceof JTextField || c instanceof JPanel || c instanceof JButton) {
				c.setEnabled(enabled);
			}
		}
	}

	private void updateTienHoanAmount() {
		// Find the JLabel responsible for displaying the amount within pnlQRCode
		// This relies on the structure created in createQRCodePanel()
		for (Component comp : pnlTienHoan.getComponents()) {
			// A bit fragile, better to store a direct reference if possible
			if (comp instanceof JLabel && comp.getForeground() == Color.GREEN) {
				((JLabel) comp).setText(currencyFormat.format(tongTienHoan));
				break;
			}
		}
	}

	public void setChiTietThanhToan(int tongVe, int tongPhiHoan) {
		this.tongTienHoan = tongVe - tongPhiHoan;

		if (this.tongTienHoan < 0) {
			this.tongTienHoan = 0;
		}

		lblTongTienVe.setText(currencyFormat.format(tongVe));
		lblTongPhiHoan.setText(currencyFormat.format(tongPhiHoan));
		lblTongTienHoan.setText(currencyFormat.format(this.tongTienHoan));
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);
		radTienMat.setEnabled(enabled);

		// Disable detail panel components
		for (Component c : pnlChiTiet.getComponents()) {
			c.setEnabled(enabled);
		}

		// Disable components within the currently visible payment card
		setTienMatPanelEnabled(enabled);

		// Disable confirm buttons specifically if panel is disabled
		btnXacNhanVaInCash.setEnabled(enabled && radTienMat.isSelected());
	}

	private void setTienMatPanelEnabled(boolean enabled) {
		for (Component c : pnlTienHoan.getComponents()) {
			if (!(c instanceof JLabel)) {
				c.setEnabled(enabled);
			}
		}

		btnXacNhanVaInCash.setEnabled(enabled);
	}

	public JButton getBtnXacNhanVaInCash() {
		return btnXacNhanVaInCash;
	}
}