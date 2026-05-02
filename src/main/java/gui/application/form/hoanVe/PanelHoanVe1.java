package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVe.java  1.0  [1:04:59 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

import javax.swing.*;
import java.awt.*;

public class PanelHoanVe1 extends JPanel {
    private PanelHoanVeBuoc1 panelHoanVeBuoc1;
    private PanelHoanVeBuoc2 panelHoanVeBuoc2;
    private PanelHoanVeBuoc3 panelHoanVeBuoc3;

    public PanelHoanVe1() {
        setLayout(new BorderLayout());

        JPanel pnlNorth = new JPanel(new BorderLayout(1, 0));

        panelHoanVeBuoc1 = new PanelHoanVeBuoc1();
        panelHoanVeBuoc2 = new PanelHoanVeBuoc2();
        panelHoanVeBuoc3 = new PanelHoanVeBuoc3();

        pnlNorth.add(panelHoanVeBuoc1, BorderLayout.WEST);
        pnlNorth.add(panelHoanVeBuoc2, BorderLayout.CENTER);

        add(pnlNorth, BorderLayout.NORTH);
        add(panelHoanVeBuoc3, BorderLayout.CENTER);

        panelHoanVeBuoc2.setEnabled(false);
        panelHoanVeBuoc2.setEnabled(false);

    }

    public PanelHoanVeBuoc1 getPanelHoanVeBuoc1() {
        return panelHoanVeBuoc1;
    }

    public PanelHoanVeBuoc2 getPanelHoanVeBuoc2() {
        return panelHoanVeBuoc2;
    }

    public PanelHoanVeBuoc3 getPanelHoanVeBuoc3() {
        return panelHoanVeBuoc3;
    }

    public void setBuoc1Enabled(boolean enabled) {
        panelHoanVeBuoc1.setComponentsEnabled(enabled);
    }

    public void setBuoc2Enabled(boolean enabled) {
        panelHoanVeBuoc2.setComponentsEnabled(enabled);
    }

    public void setBuoc3Enabled(boolean enabled) {
        panelHoanVeBuoc3.setComponentsEnabled(enabled);
    }

}
