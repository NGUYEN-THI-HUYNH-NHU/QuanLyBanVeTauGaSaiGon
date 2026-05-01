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

import dto.ChuyenDTO;
import gui.tuyChinh.RoundedBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PanelChuyenTau extends JPanel {
    private JPanel flowPanel;
    private JScrollPane scroll;
    private JPanel selectedCard = null;

    // Map để lưu trữ JLabel thống kê theo ChuyenID
    private Map<String, JLabel> mapSeatLabels = new HashMap<>();

    private Border normalBorder = new RoundedBorder(20, new Color(200, 200, 200), 2, true, new Color(230, 230, 230));
    private Border selectedBorder = new RoundedBorder(20, new Color(30, 120, 220), 2, true, new Color(30, 150, 220));

    private PanelBuoc2Controller controller;

    public PanelChuyenTau() {
        setBorder(new TitledBorder("Chuyến tàu có sẵn"));
        setLayout(new BorderLayout(8, 0));
        setPreferredSize(new Dimension(10, 140));

        flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        scroll = new JScrollPane(flowPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        // Tăng tốc độ cuộn
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    public void setController(PanelBuoc2Controller controller) {
        this.controller = controller;
    }

    public void showChuyenList(List<ChuyenDTO> list) {
        flowPanel.removeAll();
        selectedCard = null;
        mapSeatLabels.clear(); // Clear map cũ

        if (list == null || list.isEmpty()) {
            flowPanel.add(new JLabel("Không có chuyến"));
        } else {
            for (ChuyenDTO c : list) {
                JPanel card = createChuyenCard(c, sel -> {
                    if (controller != null) {
                        controller.onChuyenSelected(c);
                    }
                });
                card.putClientProperty("chuyenID", c.getId());
                flowPanel.add(card);
            }
        }
        flowPanel.revalidate();
        flowPanel.repaint();
    }

    private JPanel createChuyenCard(ChuyenDTO c, Consumer<ChuyenDTO> onSelect) {
        int cardW = 108;
        int cardH = 75;
        Font fontLbl = new Font("", Font.PLAIN, 10);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(cardW, cardH));
        p.setOpaque(false);

        // overlay panel (transparent) để chứa labels
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 2, 0, 2);

        JLabel lblTau = new JLabel(c.getTauID() == null ? "Tàu" : c.getTauID(), SwingConstants.CENTER);
        lblTau.setFont(fontLbl.deriveFont(Font.BOLD, 10f));
        gbc.gridy = 0;
        overlay.add(lblTau, gbc);

        JLabel lblNgayGioDi = new JLabel(
                String.format("Đi  %s %s", c.getGioDi() == null ? "" : c.getGioDi().toString(),
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
        JLabel lblCho = new JLabel(String.format("Đặt: %d  Trống: %d", 0, 0), SwingConstants.CENTER);
        lblCho.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));
        lblCho.setBackground(new Color(255, 255, 255, 200));
        lblCho.setFont(fontLbl);
        gbc.gridy = 3;
        gbc.insets = new Insets(1, 6, 0, 6);
        overlay.add(lblCho, gbc);

        // Lưu label vào Map để update sau
        mapSeatLabels.put(c.getId(), lblCho);

        // thêm overlay và bottom label
        p.add(overlay, BorderLayout.NORTH);
        // cursor + hover + select
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.setBorder(normalBorder);

        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (p != selectedCard) {
                    p.setBorder(normalBorder);
                    p.setOpaque(false);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (p != selectedCard) {
                    p.setBorder(normalBorder);
                    p.setOpaque(false);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // set selected card (cập nhật border & bg)
                if (selectedCard != null) {
                    selectedCard.setBorder(normalBorder);
                }
                selectedCard = p;
                p.setBorder(selectedBorder);
                onSelect.accept(c);
            }
        });

        return p;
    }

    /**
     * Cập nhật số chỗ trên giao diện cho một chuyến cụ thể
     */
    public void updateSeatCount(String chuyenID, int soChoDat, int soChoTrong) {
        JLabel lbl = mapSeatLabels.get(chuyenID);
        if (lbl != null) {
            lbl.setText(String.format("Đặt: %d  Trống: %d", soChoDat, soChoTrong));
            // Có thể đổi màu chữ nếu hết chỗ
            if (soChoTrong == 0) {
                lbl.setForeground(Color.RED);
            }
        }
    }

    // Lấy số chỗ hiện tại (để cộng trừ phía Client)
    public int[] getCurrentSeatCount(String chuyenID) {
        JLabel lbl = mapSeatLabels.get(chuyenID);
        if (lbl != null) {
            String text = lbl.getText();
            try {
                String[] parts = text.split("\\s+"); // Split by whitespace
                if (parts.length >= 4) {
                    int dat = Integer.parseInt(parts[1]);
                    int trong = Integer.parseInt(parts[3]);
                    return new int[]{dat, trong};
                }
            } catch (Exception e) {
            }
        }
        return new int[]{0, 0};
    }

    public void setSelectedCard(JPanel card) {
        if (selectedCard != null) {
            selectedCard.setBorder(normalBorder);
        }
        selectedCard = card;
        if (selectedCard != null) {
            selectedCard.setBorder(selectedBorder);
        }
    }

    // controller gọi để chọn card mặc định theo id
    public void selectChuyenById(String chuyenID) {
        if (chuyenID == null) {
            return;
        }
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

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        flowPanel.setEnabled(enabled);
        scroll.setEnabled(enabled);
    }
}