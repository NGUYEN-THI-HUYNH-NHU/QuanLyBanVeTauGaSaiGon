package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVe2.java  1.0  [3:21:43 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */

import javax.swing.*;
import java.awt.*;

public class PanelHoanVe2 extends JPanel {
    private final PanelHoanVeBuoc4 panelHoanVeBuoc4;
    private final PanelHoanVeBuoc5 panelHoanVeBuoc5;
    private JPanel pnlNav;
    private JButton btnPrev;

    public PanelHoanVe2() {
        setLayout(new BorderLayout(8, 8));

        JPanel centerPanel = new JPanel(new BorderLayout());

        panelHoanVeBuoc4 = new PanelHoanVeBuoc4();
        panelHoanVeBuoc5 = new PanelHoanVeBuoc5();

        centerPanel.add(panelHoanVeBuoc4, BorderLayout.NORTH);
        centerPanel.add(panelHoanVeBuoc5, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        pnlNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPrev = new JButton("Quay lại");
        pnlNav.add(btnPrev);

        add(pnlNav, BorderLayout.SOUTH);
    }

    public PanelHoanVeBuoc4 getPanelHoanVeBuoc4() {
        return panelHoanVeBuoc4;
    }

    public PanelHoanVeBuoc5 getPanelHoanVeBuoc5() {
        return panelHoanVeBuoc5;
    }

    public JButton getBtnPrev() {
        return btnPrev;
    }
}