package gui.application.form.banVe;
/*
 * @(#) PanelBuoc4.java  1.0  [1:38:19 PM] Sep 28, 2025
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;

public class PanelBuoc4 extends JPanel {

    private JLabel lblTitle;
    private JPanel pnlTop;
    private JPanel pnlThongTinNguoiMua;
    private JPanel pnlThongTinHoaDon;

    private JLabel lblSectionTitle;
    private JPanel pnlTabBar;
    private JPanel pnlTicketList;
    private JScrollPane scrTicketList;
    
    public PanelBuoc4() {
        initComponents();
        buildLayout();
    }

    private void initComponents() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(12, 12));
        setBorder(new TitledBorder("(4) Xác nhận"));

        // Title
        lblTitle = new JLabel("Xác nhận thông tin đặt mua vé tàu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(40, 40, 40));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Top info: left buyer, right invoice ---
        pnlTop = new JPanel(new GridLayout(1, 2, 24, 0));
        pnlTop.setOpaque(false);
        pnlTop.setPreferredSize(new Dimension(10, 140)); // cho pnlTop chiều cao tham khảo
        pnlTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Left: Thông tin người mua
        pnlThongTinNguoiMua = new JPanel();
        pnlThongTinNguoiMua.setLayout(new BoxLayout(pnlThongTinNguoiMua, BoxLayout.Y_AXIS));
        pnlThongTinNguoiMua.setOpaque(false);
        pnlThongTinNguoiMua.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel lblBuyerTitle = new JLabel("Thông tin người mua vé");
        lblBuyerTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBuyerTitle.setForeground(new Color(204, 85, 0));
        lblBuyerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtBuyer = new JTextArea();
        txtBuyer.setEditable(false);
        txtBuyer.setOpaque(false);
        txtBuyer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuyer.setText(
                "- Họ và tên: Nguyen A\n" +
                "- Số CMND/Hộ chiếu: 091919191919\n" +
                "- Số di động: 0389390300\n" +
                "- Email để nhận vé điện tử:\n\n" +
                "- Phương thức thanh toán: Thanh toán qua ví MoMo"
        );
        txtBuyer.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtBuyer.setBorder(null);

        pnlThongTinNguoiMua.add(lblBuyerTitle);
        pnlThongTinNguoiMua.add(Box.createVerticalStrut(8));
        pnlThongTinNguoiMua.add(txtBuyer);

        // Right: Thông tin hóa đơn
        pnlThongTinHoaDon = new JPanel();
        pnlThongTinHoaDon.setLayout(new BoxLayout(pnlThongTinHoaDon, BoxLayout.Y_AXIS));
        pnlThongTinHoaDon.setOpaque(false);
        pnlThongTinHoaDon.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel lblInvoiceTitle = new JLabel("Thông tin hóa đơn");
        lblInvoiceTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInvoiceTitle.setForeground(new Color(204, 85, 0));
        lblInvoiceTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtInvoice = new JTextArea();
        txtInvoice.setEditable(false);
        txtInvoice.setOpaque(false);
        txtInvoice.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtInvoice.setText(
                "- Người mua hàng: Nguyen A\n" +
                "- Tên Công ty/Đơn vị:\n" +
                "- Mã số thuế:\n" +
                "- Mã đơn vị quan hệ NS:\n" +
                "- Địa chỉ:"
        );
        txtInvoice.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtInvoice.setBorder(null);

        pnlThongTinHoaDon.add(lblInvoiceTitle);
        pnlThongTinHoaDon.add(Box.createVerticalStrut(8));
        pnlThongTinHoaDon.add(txtInvoice);

        pnlTop.add(pnlThongTinNguoiMua);
        pnlTop.add(pnlThongTinHoaDon);

        // Section title "Thông tin vé mua"
        lblSectionTitle = new JLabel("Thông tin vé mua");
        lblSectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSectionTitle.setForeground(new Color(204, 85, 0));

        // Tab bar with "Chiều đi" pill
        pnlTabBar = new JPanel(new BorderLayout());
        pnlTabBar.setOpaque(false);
        JPanel pnlTab = new JPanel();
        pnlTab.setBackground(new Color(226, 244, 252)); // light blue
        pnlTab.setBorder(new EmptyBorder(8, 12, 8, 12));
        JLabel lblChieuDi = new JLabel("Chiều đi");
        lblChieuDi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblChieuDi.setForeground(new Color(50, 90, 110));
        pnlTab.add(lblChieuDi);
        pnlTabBar.add(pnlTab, BorderLayout.WEST);

        // Ticket list (cards)
        pnlTicketList = new JPanel();
        pnlTicketList.setLayout(new BoxLayout(pnlTicketList, BoxLayout.Y_AXIS));
        pnlTicketList.setOpaque(false);

        // Add sample cards
        pnlTicketList.add(createTicketCard(1,
                "Tàu SE8 Toa 1 Chỗ 20 Ngồi mềm",
                "Sài Gòn - Biên Hòa ngày 01/10/2025 06:00",
                "Nguyen A",
                "091919191919",
                "Người lớn",
                "Phòng chờ VIP ga Sài Gòn",
                20000,
                0,
                31000,
                459));

        pnlTicketList.add(Box.createVerticalStrut(12));

        pnlTicketList.add(createTicketCard(2,
                "Tàu SE8 Toa 1 Chỗ 21 Ngồi mềm",
                "Sài Gòn - Biên Hòa ngày 01/10/2025 06:00",
                "Nguyen B",
                "091818181818",
                "Người lớn",
                "Phòng chờ VIP ga Sài Gòn",
                20000,
                0,
                31000,
                459));

        // Wrap ticket list in scroll pane
        scrTicketList = new JScrollPane(pnlTicketList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrTicketList.setBorder(new LineBorder(new Color(230,230,230)));
        scrTicketList.getVerticalScrollBar().setUnitIncrement(12);
    }

    private void buildLayout() {
        // Build header panel (title + top info + separator) and put it in NORTH
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Title area
        JPanel titleWrap = new JPanel(new BorderLayout());
        titleWrap.setOpaque(false);
        titleWrap.add(lblTitle, BorderLayout.WEST);
        titleWrap.setBorder(new EmptyBorder(0, 0, 8, 0));
        headerPanel.add(titleWrap);

        // Add pnlTop
        JPanel topWrap = new JPanel(new BorderLayout());
        topWrap.setOpaque(false);
        topWrap.add(pnlTop, BorderLayout.CENTER);
        topWrap.setBorder(new EmptyBorder(0, 0, 8, 0));
        headerPanel.add(topWrap);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setOpaque(true);
        sep.setForeground(new Color(220,220,220));
        headerPanel.add(sep);

        // Put headerPanel to NORTH so it never overlaps center content
        add(headerPanel, BorderLayout.NORTH);

        // Build center content: section title + tabbar on top, scroll list center
        JPanel center = new JPanel(new BorderLayout(8,8));
        center.setOpaque(false);

        JPanel topOfCenter = new JPanel(new BorderLayout());
        topOfCenter.setOpaque(false);
        topOfCenter.add(lblSectionTitle, BorderLayout.WEST);
        topOfCenter.add(pnlTabBar, BorderLayout.SOUTH);
        topOfCenter.setBorder(new EmptyBorder(8, 0, 8, 0));

        center.add(topOfCenter, BorderLayout.NORTH);
        center.add(scrTicketList, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    /**
     * Create a ticket card panel (static)
     */
    private JPanel createTicketCard(int number,
                                   String brief,
                                   String routeAndTime,
                                   String buyerName,
                                   String docNumber,
                                   String passengerType,
                                   String serviceName,
                                   int servicePrice,
                                   int serviceDiscount,
                                   int totalPrice,
                                   int badgeNumber) {

        JPanel pnlCardContainer = new JPanel(new BorderLayout(0, 0));
        pnlCardContainer.setOpaque(false);

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(59, 123, 192));
        pnlHeader.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblCardTitle = new JLabel("Vé số " + number);
        lblCardTitle.setForeground(Color.WHITE);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblBadge = new JLabel(String.valueOf(badgeNumber));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(220, 40, 40));
        lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadge.setPreferredSize(new Dimension(36, 20));
        lblBadge.setBorder(new EmptyBorder(2, 6, 2, 6));

        pnlHeader.add(lblCardTitle, BorderLayout.WEST);
        pnlHeader.add(lblBadge, BorderLayout.EAST);

        JPanel pnlBody = new JPanel(new BorderLayout());
        pnlBody.setBackground(new Color(245, 245, 245));
        pnlBody.setBorder(new CompoundBorder(new LineBorder(new Color(200,200,200)), new EmptyBorder(12,12,12,12)));

        StringBuilder sb = new StringBuilder("<html><div style='font-family:Segoe UI; font-size:12px;'>");
        sb.append("<b>- </b>").append("<b>").append(brief).append("</b><br/>");
        sb.append("<b>- </b>").append(routeAndTime).append("<br/>");
        sb.append("<b>- Họ tên:</b> ").append("<b>").append(buyerName).append("</b><br/>");
        sb.append("<b>- Số giấy tờ:</b> ").append(docNumber).append("<br/>");
        sb.append("<b>- Đối tượng:</b> ").append(passengerType).append("<br/>");
        sb.append("<b>- Mua dịch vụ :</b> ").append(serviceName).append("<br/>");
        sb.append("<b>- Giá (VND):</b>").append(String.format("%,d", servicePrice)).append(" VND<br/>");
        sb.append("<b>- Giảm giá DV :</b> ").append(String.format("%,d", serviceDiscount)).append(" VND<br/>");
        sb.append("<b>- Thành tiền (VND):</b> ").append(String.format("%,d", totalPrice)).append(" VND<br/>");
        sb.append("</div></html>");

        JLabel lblInfo = new JLabel(sb.toString());
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setVerticalAlignment(SwingConstants.TOP);

        pnlBody.add(lblInfo, BorderLayout.CENTER);

        pnlCardContainer.add(pnlHeader, BorderLayout.NORTH);
        pnlCardContainer.add(pnlBody, BorderLayout.CENTER);

        JPanel padding = new JPanel(new BorderLayout());
        padding.setOpaque(false);
        padding.setBorder(new EmptyBorder(4, 8, 4, 8));
        padding.add(pnlCardContainer, BorderLayout.CENTER);

        return padding;
    } 
    
    public void setComponentsEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	}
}