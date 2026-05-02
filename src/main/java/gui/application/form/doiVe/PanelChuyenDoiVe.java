package gui.application.form.doiVe;
/*
 * @(#) PanelChuyenDoiVe.java  1.0  [3:18:24 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import gui.application.form.banVe.PanelChieuLabel;

import javax.swing.*;
import java.awt.*;

public class PanelChuyenDoiVe extends JPanel {
    private PanelChieuLabel panelChieuLabel;
    private PanelChuyenTauDoiVe panelChuyenTau;
    private PanelDoanTauDoiVe panelDoanTau;
    private PanelSoDoChoDoiVe panelSoDoCho;

    public PanelChuyenDoiVe() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        panelChieuLabel = new PanelChieuLabel();
        panelChuyenTau = new PanelChuyenTauDoiVe();
        panelDoanTau = new PanelDoanTauDoiVe();
        panelSoDoCho = new PanelSoDoChoDoiVe();

        panelChuyenTau.setPreferredSize(new Dimension(0, 142));
        panelDoanTau.setPreferredSize(new Dimension(0, 80));

        add(panelChieuLabel);
        add(panelChuyenTau);
        add(panelDoanTau);
        add(panelSoDoCho);
    }

    // Getters để BanVe1Controller có thể lấy và gán cho Controller
    public PanelChieuLabel getPanelChieuLabel() {
        return panelChieuLabel;
    }

    public PanelChuyenTauDoiVe getPanelChuyenTau() {
        return panelChuyenTau;
    }

    public PanelDoanTauDoiVe getPanelDoanTau() {
        return panelDoanTau;
    }

    public PanelSoDoChoDoiVe getPanelSoDoCho() {
        return panelSoDoCho;
    }
}