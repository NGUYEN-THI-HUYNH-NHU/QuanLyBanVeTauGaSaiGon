package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2ChuyenTau.java  1.0  [12:50:26 PM] Sep 29, 2025
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import entity.Chuyen;

public class PanelChuyenTau extends JPanel {
	private JPanel flowPanel;
	private JScrollPane scroll;
	private JPanel selectedCard = null;
	private PanelBuoc2Controller controller;

	public PanelChuyenTau() {
		setBorder(new TitledBorder("Chuyến tàu phù hợp"));
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(10, 100));
		
		flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		scroll = new JScrollPane(flowPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		
		add(scroll, BorderLayout.CENTER);
	}

	public void setController(PanelBuoc2Controller controller) {
		this.controller = controller;
	}

	public void showChuyenList(List<Chuyen> list) {
		flowPanel.removeAll();
		selectedCard = null;
		if (list == null || list.isEmpty()) {
			flowPanel.add(new JLabel("Không có chuyến"));
		} else {
			for (Chuyen c : list) {
				JPanel card = createChuyenCard(c, sel -> {
					if (controller != null)
						controller.onChuyenSelected(c);
				});
				card.putClientProperty("chuyenID", c.getChuyenID());
				flowPanel.add(card);
			}
		}
		flowPanel.revalidate();
		flowPanel.repaint();
	}

	private JPanel createChuyenCard(Chuyen c, Consumer<Chuyen> onSelect) {
		int cardW = 90;
		int cardH = 80;
		Font fontLbl = new Font("", Font.PLAIN, 10);

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setPreferredSize(new Dimension(cardW, cardH));
		p.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		p.setOpaque(true);

		// overlay panel (transparent) để chứa labels
		JPanel overlay = new JPanel(new GridBagLayout());
		overlay.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(2, 2, 0, 2);

		JLabel lblTau = new JLabel(c.getTau() == null ? "Tàu" : c.getTau().getTauID(), SwingConstants.CENTER);
		lblTau.setFont(fontLbl.deriveFont(Font.BOLD, 10f));
		lblTau.setOpaque(false);

		gbc.gridy = 0;
		overlay.add(lblTau, gbc);

		JLabel lblNgayGioDi = new JLabel(String.format("Đi  %s %s", c.getGioDi() == null ? "" : c.getGioDi().toString(),
				c.getNgayDi() == null ? "" : c.getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM"))),
				SwingConstants.CENTER);
		lblNgayGioDi.setFont(fontLbl);
		gbc.gridy = 1;
		overlay.add(lblNgayGioDi, gbc);

		JLabel lblNgayGioDen = new JLabel(
				String.format("Đến %s %s", c.getGioDen() == null ? "" : c.getGioDen().toString(),
						c.getNgayDen() == null ? "" : c.getNgayDen().format(DateTimeFormatter.ofPattern("dd/MM"))),
						SwingConstants.CENTER);
		lblNgayGioDen.setFont(fontLbl);
		gbc.gridy = 2;
		overlay.add(lblNgayGioDen, gbc);

		// seat info ở bottom (dạng badge)
		JLabel lblCho = new JLabel(
				String.format("Đặt: %d  Trống: %d", /* c.getBookedSeatsCount() */ 0, /* c.getAvailableSeatsCount() */0),
				SwingConstants.CENTER);
		lblCho.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
		lblCho.setOpaque(true);
		lblCho.setBackground(new Color(255, 255, 255, 200)); // hơi mờ để đọc được trên ảnh
		lblCho.setFont(fontLbl);
		gbc.gridy = 3;
		gbc.insets = new Insets(2, 6, 0, 6);
		overlay.add(lblCho, gbc);

		// thêm overlay và bottom label
		p.add(overlay, BorderLayout.NORTH);

		// cursor + hover + select
		p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		Color baseBg = p.getBackground();
		Color hoverBg = new Color(230, 240, 255, 120); // nhẹ, sẽ overlay trên ảnh (nếu muốn)
		Border normalBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
		Border selectedBorder = BorderFactory.createLineBorder(new Color(30, 120, 220), 2);

		p.setBorder(normalBorder);

		p.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if (p != selectedCard) {
					// đổi overlay hiệu ứng: có thể làm sáng nền bằng cách vẽ panel đặt opaque hoặc
					// thay border
					p.setBackground(hoverBg);
					p.setOpaque(true);
				}
			}

			public void mouseExited(MouseEvent e) {
				if (p != selectedCard) {
					p.setBackground(baseBg);
					p.setOpaque(true);
				}
			}

			public void mouseClicked(MouseEvent e) {
				// set selected card (cập nhật border & bg)
				if (selectedCard != null) {
					selectedCard.setBorder(normalBorder);
					selectedCard.setBackground(baseBg);
				}
				selectedCard = p;
				p.setBorder(selectedBorder);
				p.setBackground(new Color(210, 230, 255));
				onSelect.accept(c);
			}
		});

		return p;
	}

	public void setSelectedCard(JPanel card) {
		if (selectedCard != null) {
			selectedCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
			selectedCard.setBackground(UIManager.getColor("Panel.background"));
		}
		selectedCard = card;
		if (selectedCard != null) {
			selectedCard.setBorder(BorderFactory.createLineBorder(new Color(30, 120, 210), 3));
			selectedCard.setBackground(new Color(200, 220, 255));
		}
	}

	// controller gọi để chọn card mặc định theo id
	public void selectChuyenById(String chuyenID) {
		if (chuyenID == null)
			return;
		for (Component comp : flowPanel.getComponents()) {
			if (comp instanceof JPanel) {
				Object id = ((JPanel) comp).getClientProperty("chuyenID");
				if (chuyenID.equals(id)) {
					setSelectedCard((JPanel) comp);
					break;
				}
			}
		}
	}
}