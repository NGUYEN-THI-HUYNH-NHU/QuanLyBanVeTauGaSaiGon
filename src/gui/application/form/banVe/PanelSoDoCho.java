package gui.application.form.banVe;
/*
 * @(#) PanelSoDoCho.java  1.0  [12:51:43 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class PanelSoDoCho extends JPanel {
    private JPanel seatGridPanel;
    private JPanel navPanel;
    private JButton btnPrev, btnNext;
    private PanelBuoc2Controller panelBuoc2Controller;
    private JLabel lblToaInfo;
    private JButton selectedSeatButton = null;
    private int doanTauLength;

    // current toa context
    private Toa currentToa;
    private List<Toa> toaList;
    private int currentIndex = 0;
	private JPanel pnlNorth;

    public PanelSoDoCho() {
        setBorder(new TitledBorder("Sơ đồ chỗ"));
        setLayout(new BorderLayout());

        lblToaInfo = new JLabel("Chưa chọn toa", SwingConstants.CENTER);
        lblToaInfo.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnPrev = new JButton("<");
        btnNext = new JButton(">");
        navPanel.add(btnPrev);
        navPanel.add(btnNext);

        pnlNorth = new JPanel();
        pnlNorth.setLayout(new BorderLayout());
        pnlNorth.add(lblToaInfo, BorderLayout.NORTH);
        pnlNorth.add(navPanel, BorderLayout.CENTER);

        seatGridPanel = new JPanel();

        add(pnlNorth, BorderLayout.NORTH);
        add(seatGridPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(10, 200));

        btnPrev.addActionListener(e -> showPrevToa());
        btnNext.addActionListener(e -> showNextToa());
    }

    public void setController(PanelBuoc2Controller c) {
    	this.panelBuoc2Controller = c;
    }

    public void setToaList(List<Toa> list) {
        this.toaList = list;
        this.currentIndex = 0;
        this.doanTauLength = list.size();
        if (list != null && !list.isEmpty()) {
            setCurrentToa(list.get(0));
        } else {
            setCurrentToa(null);
        }
    }

    public void setCurrentToa(Toa t) {
        this.currentToa = t;
        if (t == null) {
            lblToaInfo.setText("Chưa chọn toa");
        } else {
            String soToa = String.valueOf(t.getSoToa());
            String moTaHang = null;
            try {
              moTaHang = (t.getHangToa().getDescription() != null) ? t.getHangToa().getDescription() : t.getHangToa().toString();
            } catch (Throwable ex) {
                try {
                	moTaHang = t.getHangToa().getDescription();
                } catch (Throwable ignored) {
                	moTaHang = "";
                }
            }
            lblToaInfo.setText("Toa số " + soToa + ": " + (moTaHang == null ? "" : moTaHang));
        }

        showLoadingState();
        if (panelBuoc2Controller != null && t != null) {
            panelBuoc2Controller.loadSeatsForToa(t, gheList -> {
                SwingUtilities.invokeLater(() -> renderSeats(gheList));
            });
        } else {
            renderSeats(null);
        }
    }

    private void showLoadingState() {
        seatGridPanel.removeAll();
        seatGridPanel.setLayout(new BorderLayout());
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private int parseLeadingInt(String s) {
        if (s == null)
        	return Integer.MAX_VALUE;
        String num = "";
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (Character.isDigit(ch))
            	num += ch;
            else break;
        }
        try {
        	return num.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(num);
    	} catch (NumberFormatException ex) {
    		return Integer.MAX_VALUE;
    	}
    }

    private void renderSeats(List<Ghe> gheList) {
        seatGridPanel.removeAll();
        selectedSeatButton = null;
        if (gheList == null || gheList.isEmpty()) {
            seatGridPanel.add(new JLabel("Không có ghế", SwingConstants.CENTER));
            seatGridPanel.revalidate();
            seatGridPanel.repaint();
            return;
        }

        // clone + sort by numeric seat id (leading digits) then by full label
        List<Ghe> sorted = new ArrayList<>(gheList);
        sorted.sort(Comparator
                .comparingInt((Ghe g) -> g.getSoGhe())
                .thenComparing(g -> g.getSoGhe()));

        // choose columns: try detect layout (if gheList small -> single row)
        int cols = 6; // default nicer width
        if (sorted.size() <= 6)
        	cols = sorted.size();
        int rows = (int) Math.ceil(sorted.size() / (double) cols);
        seatGridPanel.setLayout(new GridLayout(rows, cols, 8, 8));

        for (Ghe g : sorted) {
            JButton b = new JButton(String.valueOf(g.getSoGhe()));
            b.setMargin(new Insets(2,2,2,2));
//            b.setOpaque(true);
            if (g.getTrangThai() == TrangThaiGhe.DA_BAN) {
                b.setBackground(new Color(220, 53, 53));
                b.setEnabled(false);
            } else {
                b.setBackground(Color.WHITE);
                b.setEnabled(true);
            }
            b.addActionListener(e -> {
                // visual select for seat
                if (selectedSeatButton != null && selectedSeatButton != b) {
                    selectedSeatButton.setBackground(Color.WHITE);
                }
                selectedSeatButton = b;
                b.setBackground(new Color(40, 167, 69));
                b.setForeground(Color.WHITE);
                if (panelBuoc2Controller != null)
                	panelBuoc2Controller.onSeatClicked(currentToa, g);
            });
            seatGridPanel.add(b);
        }

        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

	private void showPrevToa() {
        if (toaList == null || toaList.isEmpty())
        	return;
        if (currentIndex == 0) {
        	currentIndex = doanTauLength-1;
        	Toa toa = toaList.get(currentIndex);
        	setCurrentToa(toa);
        	panelBuoc2Controller.highlightToa(toa);
        	return;
        }
        currentIndex = Math.max(0, currentIndex - 1);
        Toa toa = toaList.get(currentIndex);
        setCurrentToa(toa);
    	panelBuoc2Controller.highlightToa(toa);
    }

    private void showNextToa() {
        if (toaList == null || toaList.isEmpty())
        	return;
        if (currentIndex == doanTauLength-1) {
        	currentIndex = 0;
        	Toa toa = toaList.get(currentIndex);
        	setCurrentToa(toa);
        	panelBuoc2Controller.highlightToa(toa);
        	return;
        }
        currentIndex = Math.min(toaList.size() - 1, currentIndex + 1);
        Toa toa = toaList.get(currentIndex);
        setCurrentToa(toa);
    	panelBuoc2Controller.highlightToa(toa);
    }

    // used by controller when user selects a chuyen -> set toa list
    public void setToaListAndSelect(java.util.List<Toa> list, int selectIndex) {
        setToaList(list);
        if (list != null && !list.isEmpty()) {
            currentIndex = Math.min(Math.max(0, selectIndex), list.size() - 1);
            setCurrentToa(list.get(currentIndex));
        }
    }
}