package gui.application.form.banVe;

/*
 * @(#) PanelBuoc2ChieuLabel.java  1.0  [12:49:52 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.*;

public class PanelChieuLabel extends JPanel {
    private JLabel lblInfo;

    public PanelChieuLabel() {
        setLayout(new BorderLayout());

        lblInfo = new JLabel("Chiều: —", SwingConstants.CENTER);
        lblInfo.setPreferredSize(new Dimension(10, 20));

        lblInfo.setBackground(new Color(32, 83, 145));
        lblInfo.putClientProperty(FlatClientProperties.STYLE, "foreground:$Menu.foreground");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(2, 1, 2, 1));
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.BOLD, 16f));
        lblInfo.setOpaque(true);

        add(lblInfo, BorderLayout.CENTER);
    }

    public void setText(String text) {
        lblInfo.setText("<html><b>" + text + "</b></html>");
    }
}
