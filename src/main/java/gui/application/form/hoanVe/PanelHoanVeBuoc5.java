package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeBuoc5.java  1.0  [2:15:29 PM] Nov 14, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 14, 2025
 * @version: 1.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class PanelHoanVeBuoc5 extends JPanel {
    private static final String TIEN_MAT_CARD = "TienMat";
    private JRadioButton radTienMat;
    private JLabel lblTongTienVe;
    private JLabel lblTongPhiHoan;
    private JLabel lblTongTienHoan;
    private JButton btnXacNhanHoanVe;
    private JPanel pnlTienHoan;
    private JPanel pnlPaymentMethodContainer;
    private CardLayout paymentCardLayout;
    private DecimalFormat currencyFormat;
    private int tongTienHoan = 0;
    private JPanel pnlChiTiet;
    private JLabel lblTienHoanAmount;

    public PanelHoanVeBuoc5() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Thanh Toán"));

        currencyFormat = new DecimalFormat("#,##0đ");

        // Panel Phương thức thanh toán
        JPanel pnlPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        radTienMat = new JRadioButton("Tiền mặt", true);
        ButtonGroup bgPayment = new ButtonGroup();
        bgPayment.add(radTienMat);
        pnlPhuongThuc.add(radTienMat);
        add(pnlPhuongThuc, BorderLayout.NORTH);

        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlChiTiet = createChiTietPanel();

        paymentCardLayout = new CardLayout();
        pnlPaymentMethodContainer = new JPanel(paymentCardLayout);

        pnlTienHoan = createTienHoanPanel();

        pnlPaymentMethodContainer.add(pnlTienHoan, TIEN_MAT_CARD);

        pnlMain.add(pnlChiTiet);
        pnlMain.add(pnlPaymentMethodContainer);

        add(pnlMain, BorderLayout.CENTER);

        // Logic nội bộ
        addInternalLogic();

        // Show cash panel initially
        paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);
    }

    private JPanel createChiTietPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnl.add(new JLabel("Tổng tiền vé:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        lblTongTienVe = new JLabel("0 VND");
        pnl.add(lblTongTienVe, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnl.add(new JLabel("Tổng phí hoàn:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        lblTongPhiHoan = new JLabel("0 VND", JLabel.RIGHT);
        lblTongPhiHoan.setForeground(Color.RED);
        pnl.add(lblTongPhiHoan, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        pnl.add(Box.createVerticalGlue(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblTongTienHoanChu;
        pnl.add(lblTongTienHoanChu = new JLabel("Tổng tiền hoàn:"), gbc);
        lblTongTienHoanChu.setFont(getFont().deriveFont(Font.BOLD, 14f));

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        lblTongTienHoan = new JLabel("0 VND");
        lblTongTienHoan.setForeground(Color.BLUE);
        lblTongTienHoan.setFont(lblTongTienHoan.getFont().deriveFont(Font.BOLD, 14f));
        pnl.add(lblTongTienHoan, gbc);

        return pnl;
    }

    private JPanel createTienHoanPanel() {
        pnlTienHoan = new JPanel(new GridBagLayout());
        pnlTienHoan.setBorder(BorderFactory.createTitledBorder("Tiền mặt"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel lblTitle = new JLabel("Số tiền cần hoàn lại cho khách:");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        pnlTienHoan.add(lblTitle, gbc);

        gbc.gridy = 1;
        lblTienHoanAmount = new JLabel(tongTienHoan + "");
        lblTienHoanAmount.setFont(lblTienHoanAmount.getFont().deriveFont(Font.BOLD, 24f));
        lblTienHoanAmount.setForeground(Color.BLUE);
        lblTienHoanAmount.setHorizontalAlignment(SwingConstants.CENTER);
        pnlTienHoan.add(lblTienHoanAmount, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        pnlTienHoan.add(new JLabel(""), gbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        btnXacNhanHoanVe = new JButton("Xác nhận hoàn tiền");
        btnXacNhanHoanVe.setFont(btnXacNhanHoanVe.getFont().deriveFont(Font.BOLD, 14f));
        btnXacNhanHoanVe.setBackground(new Color(0, 153, 51));
        btnXacNhanHoanVe.setForeground(Color.WHITE);
        pnlTienHoan.add(btnXacNhanHoanVe, gbc);

        return pnlTienHoan;
    }

    private void addInternalLogic() {
        ActionListener paymentMethodListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTienHoanAmount();
                paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);

            }
        };
        radTienMat.addActionListener(paymentMethodListener);
    }

    public int getTongTienHoan() {
        return tongTienHoan;
    }

    private void setTienMatEnabled(boolean enabled) {
        for (Component c : pnlTienHoan.getComponents()) {
            if (c instanceof JTextField || c instanceof JPanel || c instanceof JButton) {
                c.setEnabled(enabled);
            }
        }
    }

    private void updateTienHoanAmount() {
        // Find the JLabel responsible for displaying the amount within pnlQRCode
        // This relies on the structure created in createQRCodePanel()
        for (Component comp : pnlTienHoan.getComponents()) {
            // A bit fragile, better to store a direct reference if possible
            if (comp instanceof JLabel && comp.getForeground() == Color.GREEN) {
                ((JLabel) comp).setText(currencyFormat.format(tongTienHoan));
                break;
            }
        }
    }

    public void setChiTietThanhToan(int tongVe, int tongPhiHoan) {
        this.tongTienHoan = tongVe - tongPhiHoan;

        if (this.tongTienHoan < 0) {
            this.tongTienHoan = 0;
        }

        lblTongTienVe.setText(currencyFormat.format(tongVe));
        lblTongPhiHoan.setText(currencyFormat.format(tongPhiHoan));
        lblTongTienHoan.setText(currencyFormat.format(this.tongTienHoan));

        lblTienHoanAmount.setText(currencyFormat.format(tongTienHoan));
    }

    public void setComponentsEnabled(boolean enabled) {
        super.setEnabled(enabled);
        radTienMat.setEnabled(enabled);

        // Disable detail panel components
        for (Component c : pnlChiTiet.getComponents()) {
            c.setEnabled(enabled);
        }

        // Disable components within the currently visible payment card
        setTienMatPanelEnabled(enabled);

        // Disable confirm buttons specifically if panel is disabled
        btnXacNhanHoanVe.setEnabled(enabled && radTienMat.isSelected());
    }

    private void setTienMatPanelEnabled(boolean enabled) {
        for (Component c : pnlTienHoan.getComponents()) {
            if (!(c instanceof JLabel)) {
                c.setEnabled(enabled);
            }
        }

        btnXacNhanHoanVe.setEnabled(enabled);
    }

    public JButton getBtnXacNhanHoanVe() {
        return btnXacNhanHoanVe;
    }

    public JRadioButton getRadTienMat() {
        return radTienMat;
    }
}