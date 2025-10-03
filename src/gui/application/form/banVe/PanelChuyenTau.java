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
import javax.swing.*;
import javax.swing.border.TitledBorder;

import entity.Chuyen;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class PanelChuyenTau extends JPanel {
	private JPanel flowPanel;
	private JScrollPane scroll;
	private PanelBuoc2Controller controller;
	private JPanel selectedCard = null;

	public PanelChuyenTau() {
		setBorder(new TitledBorder("Chuyến tàu phù hợp"));
		setLayout(new BorderLayout());
		flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		scroll = new JScrollPane(flowPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		add(scroll, BorderLayout.CENTER);
		setPreferredSize(new Dimension(10, 90));
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
	                if (controller != null) controller.onChuyenSelected(c);
	            });
	            card.putClientProperty("chuyenID", c.getChuyenID());
	            flowPanel.add(card);
	        }
	    }
	    flowPanel.revalidate();
	    flowPanel.repaint();
	}

	private JPanel createChuyenCard(Chuyen c, Consumer<Chuyen> onSelect) {
	    JPanel p = new JPanel(new BorderLayout());
	    p.setPreferredSize(new Dimension(200, 110));
	    p.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
	    p.setBackground(UIManager.getColor("Panel.background"));
	    p.setOpaque(true);
		JLabel lblTau = new JLabel(c.getTau() == null ? "Tau" : c.getTau().getTauID(), SwingConstants.CENTER);
	    JLabel lblNgayGioDi = new JLabel(String.format("TG đi  %s", c.getNgayGioKhoiHanh()), SwingConstants.CENTER);
	    JLabel lblNgayGioDen = new JLabel(String.format("TG đến %s", c.getNgayGioDen()), SwingConstants.CENTER);
	    JLabel lblSeats = new JLabel(String.format("Đặt: %d  Trống: %d", 0, 0), SwingConstants.CENTER);

	    JPanel top = new JPanel(new GridLayout(3,1));
	    top.setOpaque(false);
	    top.add(lblTau);
	    top.add(lblNgayGioDi);
	    top.add(lblNgayGioDen);

	    p.add(top, BorderLayout.CENTER);
	    p.add(lblSeats, BorderLayout.SOUTH);

	    p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

	    Color baseBg = p.getBackground();
	    Color hoverBg = new Color(230, 240, 255);

	    p.addMouseListener(new MouseAdapter() {
	        public void mouseEntered(MouseEvent e) {
	            if (p != selectedCard)
	            	p.setBackground(hoverBg);
	        }
	        public void mouseExited(MouseEvent e) {
	            if (p != selectedCard)
	            	p.setBackground(baseBg);
	        }
	        public void mouseClicked(MouseEvent e) {
	            setSelectedCard(p);
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
	         selectedCard.setBorder(BorderFactory.createLineBorder(new Color(30, 120, 210), 2));
	         selectedCard.setBackground(new Color(200, 220, 255));
	     }
	 }

	 // controller gọi để chọn card mặc định theo id
	 public void selectChuyenById(String chuyenID) {
	     if (chuyenID == null) return;
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