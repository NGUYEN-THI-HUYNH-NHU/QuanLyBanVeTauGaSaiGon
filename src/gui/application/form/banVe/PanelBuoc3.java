package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3.java  1.0  [10:39:57 AM] Sep 28, 2025
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
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;

import java.awt.*;


public class PanelBuoc3 extends JPanel {
    public PanelBuoc3() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("(3) Nhập thông tin hành khách"));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        header.setBackground(new Color(7, 122, 182));
        JLabel lblTitle = new JLabel("THÔNG TIN GIỎ VÉ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        header.add(lblTitle);
        header.add(Box.createHorizontalStrut(8));
        JLabel info = new JLabel("i");
        info.setOpaque(true);
        info.setBackground(new Color(82, 168, 226));
        info.setForeground(Color.WHITE);
        info.setBorder(new EmptyBorder(4,8,4,8));
        header.add(info);

        add(header, BorderLayout.NORTH);

        // Center: list of cards in a vertical box
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(12,0,0,0));

        // two sample cards
        center.add(createPassengerCard("SE8", "Toa 1 Chỗ 20 Ngồi mềm", "Hà Nội Đi Sài Gòn 01/10/2025 06:00", 1027000));
        center.add(Box.createVerticalStrut(12));
        center.add(createPassengerCard("SE8", "Toa 1 Chỗ 21 Ngồi mềm", "Hà Nội Đi Sài Gòn 01/10/2025 06:00", 1027000));
        center.add(Box.createVerticalStrut(18));
        center.add(createCustomerCard());
   
        JScrollPane sc = new JScrollPane(center);
        sc.setBorder(null);
        add(sc, BorderLayout.CENTER);
    }

    private JPanel createPassengerCard(String trainCode, String seatText, String routeAndTime, long totalVND) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(new Color(200,200,200), 1, true));
        card.setBackground(new Color(245,245,245));

        // Top area with text on left and trash icon on right
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(240,240,240));
        top.setBorder(new EmptyBorder(12,12,12,12));

        // left lines
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        JLabel l1 = new JLabel("- Tàu " + trainCode + " " + seatText);
        l1.setFont(l1.getFont().deriveFont(12f));
        l1.setForeground(new Color(80, 80, 80));
        left.add(l1);

        JLabel l2 = new JLabel("- " + routeAndTime);
        l2.setFont(l2.getFont().deriveFont(Font.BOLD, 13f));
        l2.setForeground(new Color(10, 88, 160));
        left.add(Box.createVerticalStrut(6));
        left.add(l2);

        JLabel l3 = new JLabel("- Thành tiền (VNĐ) : " + formatCurrency(totalVND) + " VNĐ");
        l3.setFont(l3.getFont().deriveFont(12f));
        l3.setForeground(new Color(80,80,80));
        left.add(Box.createVerticalStrut(8));
        left.add(l3);

        top.add(left, BorderLayout.WEST);

        // right area: small number and trash icon
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel small = new JLabel("556");
        small.setForeground(new Color(180, 20, 20));
        small.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(small);
        right.add(Box.createVerticalStrut(8));
        JButton trash = new JButton();
        trash.setIcon(new ImageIcon("img/delete.png"));
        trash.setFont(trash.getFont().deriveFont(22f));
        trash.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(trash);
        top.add(right, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);

        // Form area with labels and controls
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(10,12,12,12));
        form.setBackground(new Color(250,250,250));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0;

        // Row: Họ tên
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Họ tên"), gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        JTextField tfName = new JTextField();
		tfName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,  "Họ tên");
        form.add(tfName, gc);

        // Row: Đối tượng
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Đối tượng"), gc);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        JComboBox<String> cb = new JComboBox<>(new String[]{"Người lớn","Trẻ em","Người cao tuổi"});
        cb.setSelectedIndex(0);
        form.add(cb, gc);
        
        // Row: Số giấy tờ
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        form.add(new JLabel("Số giấy tờ"), gc);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        JTextField tfId = new JTextField();
		tfId.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,  "Số CCCD/ Hộ chiếu/ Ngày tháng năm sinh trẻ em");
        form.add(tfId, gc);

        card.add(form, BorderLayout.CENTER);

        return card;
    }
    
    private JPanel createCustomerCard() {
    	JPanel customerCard = new JPanel();
    	customerCard.setLayout(new BorderLayout());
        customerCard.setBorder(new LineBorder(new Color(200,200,200), 1, true));


        // Header (title + description)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(234, 109, 23));
        header.setOpaque(false);

        JLabel title = new JLabel("Thông tin người đặt vé");
        title.setForeground(new Color(234, 109, 23));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        header.add(title, BorderLayout.NORTH);

        JTextArea desc = new JTextArea(
            "Vui lòng điền đầy đủ và chính xác các thông tin về người mua vé dưới đây. " +
            "Các thông tin này sẽ được sử dụng để xác minh người mua vé và lấy vé tại ga trước khi lên tàu " +
            "theo đúng các quy định của Tổng công ty Đường sắt Việt Nam.");
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setOpaque(false);
        desc.setFont(desc.getFont().deriveFont(13f));
        desc.setForeground(new Color(40, 100, 120));
        desc.setBorder(new EmptyBorder(10, 0, 0, 0));
        header.add(desc, BorderLayout.CENTER);

        customerCard.add(header, BorderLayout.NORTH);

        // Form area
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(18, 0, 0, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 8, 10, 8);
        gc.anchor = GridBagConstraints.WEST;

        // Left column labels - align right
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        form.add(requiredLabel("Họ và tên", true), gc);

        gc.gridy++;
        form.add(requiredLabel("Số CMND/Hộ chiếu", true), gc);

        gc.gridy++;
        form.add(requiredLabel("Email để nhận vé điện tử", false), gc);

        gc.gridy++;
        form.add(requiredLabel("Xác nhận email", false), gc);

        gc.gridy++;
        form.add(requiredLabel("Số di động", true), gc);

        // Right column inputs - align left, fill horizontal
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(placeholderField("Họ và tên"), gc);

        gc.gridy++;
        form.add(placeholderField("Số CMND/Hộ chiếu"), gc);

        gc.gridy++;
        form.add(placeholderField("Email để nhận vé điện tử"), gc);

        gc.gridy++;
        form.add(placeholderField("Xác nhận email"), gc);

        gc.gridy++;
        form.add(placeholderField("Số di động"), gc);

        customerCard.add(form, BorderLayout.CENTER);
        
        return customerCard;
    }

 // helper: label with optional red asterisk
    private static Component requiredLabel(String text, boolean required) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        JLabel lbl = new JLabel(text + (required ? " " : ""));
        lbl.setFont(lbl.getFont().deriveFont(14f));
        p.add(lbl);
        if (required) {
            JLabel star = new JLabel("*");
            star.setForeground(new Color(200, 30, 30));
            star.setFont(star.getFont().deriveFont(Font.BOLD, 14f));
            p.add(star);
        }
        p.setPreferredSize(new Dimension(220, 24)); // giữ cột label ổn định
        return p;
    }

    // helper: text field with placeholder text (visual only)
    private static JComponent placeholderField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setText(placeholder);
        tf.setForeground(new Color(120, 120, 120));
        tf.setPreferredSize(new Dimension(360, 30));
        return tf;
    }

    private static String formatCurrency(long v) {
        return String.format("%,d", v).replace(',', '.') ;
    }
}