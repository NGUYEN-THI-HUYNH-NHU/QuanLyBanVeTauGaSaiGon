package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2.java  1.0  [10:39:25 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import javax.swing.*;

import controller.PanelBuoc2Controller;

import java.awt.*;
import java.util.List;
import entity.Chuyen;

/**
 * PanelBuoc2: container chính cho bước chọn chỗ.
 * Sử dụng controller để điều phối.
 */
public class PanelBuoc2 extends JPanel {
    private PanelChieuLabel panelChieuLabel;
    private PanelChuyenTau panelChuyenTau;
    private PanelDoanTau panelDoanTau;
    private PanelSoDoCho panelSoDoCho;
    private PanelGioVe panelGioVe;

    private JSplitPane splitMain;
    private JPanel centerPanel;

    private PanelBuoc2Controller controller;

    public PanelBuoc2() {
        setLayout(new BorderLayout());

        panelChieuLabel = new PanelChieuLabel();
        panelChuyenTau = new PanelChuyenTau();
        panelDoanTau = new PanelDoanTau();
        panelSoDoCho = new PanelSoDoCho();
        panelGioVe = new PanelGioVe();

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(panelChieuLabel);
        centerPanel.add(panelChuyenTau);
        centerPanel.add(panelDoanTau);
        centerPanel.add(panelSoDoCho);

        JScrollPane centerScroll = new JScrollPane(centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        splitMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerScroll, panelGioVe);
        splitMain.setResizeWeight(0.75);
        add(splitMain, BorderLayout.CENTER);

        controller = new PanelBuoc2Controller(panelChieuLabel, panelChuyenTau, panelDoanTau, panelSoDoCho, panelGioVe);
        panelChuyenTau.setController(controller);
        panelDoanTau.setController(controller);
        panelSoDoCho.setController(controller);
        panelGioVe.setController(controller);
    }

    /**
     * Called by outer flow (PanelBuoc1) to provide list of chuyens to show.
     */
    public void setChuyenList(List<Chuyen> chuyenList, String gaDiName, String gaDenName) {
        controller.setChuyenList(chuyenList, gaDiName, gaDenName);
    }
}