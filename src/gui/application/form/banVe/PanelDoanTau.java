package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2DoanTau.java  1.0  [12:51:09 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Toa;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class PanelDoanTau extends JPanel {
    private JPanel flow;
    private PanelBuoc2Controller controller;
    private JButton selectedButton = null;

    public PanelDoanTau() {
        setBorder(new TitledBorder("Sơ đồ đoàn tàu"));
        setLayout(new BorderLayout());
        flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        JScrollPane scr = new JScrollPane(flow,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scr.setBorder(BorderFactory.createEmptyBorder());
        add(scr, BorderLayout.CENTER);
        setPreferredSize(new Dimension(10, 20));
    }

    public void setController(PanelBuoc2Controller controller) {
        this.controller = controller;
    }

    public void showToaList(List<Toa> list, Consumer<Toa> onSelect) {
        flow.removeAll();
        selectedButton = null;
        if (list == null || list.isEmpty()) {
            flow.add(new JLabel("Không có toa"));
        } else {
            for (Toa t : list) {
                JButton btn = new JButton(t.getSoToa());
                btn.setPreferredSize(new Dimension(60, 40));
                btn.setOpaque(true);
                btn.setBorderPainted(true);
                btn.setBackground(UIManager.getColor("Button.background"));
                btn.putClientProperty("toaID", t.getToaID());
                btn.addActionListener(e -> {
                    onSelect.accept(t);
                    highlightButton(btn);
                });
                flow.add(btn);
            }
        }
        flow.revalidate();
        flow.repaint();
    }

    private void highlightButton(JButton selected) {
        Color defaultBg = UIManager.getColor("Button.background");
        Component[] comps = flow.getComponents();
        for (Component c : comps) {
            if (c instanceof JButton) {
                c.setBackground(defaultBg);
            }
        }
        selectedButton = selected;
        if (selectedButton != null) selectedButton.setBackground(new Color(135, 206, 250));
    }

    // controller có thể gọi để chọn tự động
    public void selectToaById(String toaID) {
        if (toaID == null) return;
        for (Component c : flow.getComponents()) {
            if (c instanceof JButton) {
                Object id = ((JButton)c).getClientProperty("toaID");
                if (toaID.equals(id)) {
                    highlightButton((JButton)c);
                    break;
                }
            }
        }
    }
}