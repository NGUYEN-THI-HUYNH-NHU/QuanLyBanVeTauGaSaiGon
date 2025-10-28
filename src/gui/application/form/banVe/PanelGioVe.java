package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2GioVe.java  1.0  [12:52:28 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.itextpdf.text.Font;

public class PanelGioVe extends JPanel {
	private JPanel container;
	private PanelBuoc2Controller controller;
	private JLabel lblGioVe;
	private JScrollPane scr;
	private JButton btnMuaVe;

	public PanelGioVe() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(260, 400));

		container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.setBackground(Color.WHITE);

		scr = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scr.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

		lblGioVe = new JLabel("Giỏ vé");
		lblGioVe.setIcon(new FlatSVGIcon(getClass().getResource("/gui/icon/svg/shopping-cart.svg")));
		lblGioVe.setFont(lblGioVe.getFont().deriveFont(Font.BOLD, 16f));
		lblGioVe.setOpaque(true);
		lblGioVe.setBackground(new Color(230, 230, 230));
		lblGioVe.setForeground(new Color(0, 145, 212));
		lblGioVe.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 130, 196)));

		add(lblGioVe, BorderLayout.NORTH);
		add(scr, BorderLayout.CENTER);
		add(btnMuaVe = new JButton("Mua vé"), BorderLayout.SOUTH);

	}

	public void setController(PanelBuoc2Controller c) {
		this.controller = c;
	}

	public void addBuyButtonListener(ActionListener l) {
		if (btnMuaVe != null && l != null) {
			btnMuaVe.addActionListener(l);
		}
	}

	public void refresh(List<VeSession> dsVeSession) {
		container.removeAll();

		if (dsVeSession == null || dsVeSession.isEmpty()) {
			JLabel emptyLabel = new JLabel("Giỏ vé trống");
			emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			container.add(emptyLabel);
		} else {
			for (VeSession v : dsVeSession) {
				JPanel row = createVeRow(v);
				container.add(row);
			}
		}

		container.revalidate();
		container.repaint();
	}

	private JPanel createVeRow(VeSession v) {
		JPanel row = new JPanel(new BorderLayout());
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Chiều cao cố định
		row.setPreferredSize(new Dimension(240, 80));
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
		row.setBackground(Color.WHITE);

		JLabel info = new JLabel(v.prettyString());
		info.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

		JLabel lblTimer = new JLabel(formatRemaining(100));
		lblTimer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		lblTimer.setForeground(new Color(205, 0, 0));

		JButton btnTrash = new JButton("");
		btnTrash.setIcon(new FlatSVGIcon("gui/icon/svg/delete.svg", 0.35f));
		btnTrash.setToolTipText("Xóa vé");
		btnTrash.setFocusable(false);
		btnTrash.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		btnTrash.setBackground(Color.WHITE);
		btnTrash.addActionListener(e -> {
			if (controller != null) {
				controller.onRemoveVe(v);
			}
		});

		JPanel east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		east.setBackground(Color.WHITE);
		east.add(lblTimer);
		east.add(btnTrash);

		row.add(info, BorderLayout.CENTER);
		row.add(east, BorderLayout.EAST);

		if (controller != null) {
			controller.registerCountdownLabelForVe(v, lblTimer);
		}

		return row;
	}

	private String formatRemaining(long seconds) {
		if (seconds <= 0) {
			return "00:00";
		}
		long m = seconds / 60;
		long s = seconds % 60;
		return String.format("%02d:%02d", m, s);
	}
}