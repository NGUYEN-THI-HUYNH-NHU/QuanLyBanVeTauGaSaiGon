package gui.application.form.banVe;
/*
 * @(#) PanelChuyen.java  1.0  [4:14:27 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */

import javax.swing.*;
import java.awt.*;

public class PanelChuyen extends JPanel {
    private PanelChieuLabel panelChieuLabel;
    private PanelChuyenTau panelChuyenTau;
    private PanelDoanTau panelDoanTau;
    private PanelSoDoCho panelSoDoCho;

    public PanelChuyen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        panelChieuLabel = new PanelChieuLabel();
        panelChuyenTau = new PanelChuyenTau();
        panelDoanTau = new PanelDoanTau();
        panelSoDoCho = new PanelSoDoCho();

        panelChuyenTau.setPreferredSize(new Dimension(0, 126));
        panelDoanTau.setPreferredSize(new Dimension(0, 76));

        add(panelChieuLabel);
        add(panelChuyenTau);
        add(panelDoanTau);
        add(panelSoDoCho);
    }

    // Getters để BanVe1Controller có thể lấy và gán cho Controller
    public PanelChieuLabel getPanelChieuLabel() {
        return panelChieuLabel;
    }

    public PanelChuyenTau getPanelChuyenTau() {
        return panelChuyenTau;
    }

    public PanelDoanTau getPanelDoanTau() {
        return panelDoanTau;
    }

    public PanelSoDoCho getPanelSoDoCho() {
        return panelSoDoCho;
    }

    @Override
    public void setEnabled(boolean enabled) {
        panelChieuLabel.setEnabled(enabled);
        panelChuyenTau.setEnabled(enabled);
        panelDoanTau.setEnabled(enabled);
        panelSoDoCho.setEnabled(enabled);
    }
}