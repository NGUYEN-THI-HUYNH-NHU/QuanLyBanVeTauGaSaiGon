package gui.application.form.nhatKyAudit;

import entity.NhatKyAudit;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

public class DialogChiTietAudit extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Theme nhẹ, hợp FlatLaf
    private final Color COLOR_TITLE = new Color(36, 104, 155);
    private final Color COLOR_LABEL = new Color(110, 110, 110);
    private final Color COLOR_VALUE = new Color(30, 30, 30);
    private final Color COLOR_BORDER = new Color(220, 220, 220);
    private final Color COLOR_AREA_BG = new Color(245, 247, 250);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_AREA = new Font("Segoe UI", Font.PLAIN, 13);

    public DialogChiTietAudit(Window owner, NhatKyAudit audit, String tenNhanVien) {
        super(owner, "Chi tiết nhật ký audit", ModalityType.APPLICATION_MODAL);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 16, 14, 16));
        root.setBackground(Color.WHITE);

        // ===== Header =====
        root.add(createHeader(), BorderLayout.NORTH);

        // ===== Center =====
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);

        JPanel infoCard = createInfoCard(audit, tenNhanVien);
        JScrollPane detailCard = createDetailCard(audit);

        center.add(infoCard, BorderLayout.NORTH);
        center.add(detailCard, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        // ===== Footer buttons =====
        JPanel footer = createFooter(detailCard);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
        setMinimumSize(new Dimension(500, 520));
        pack();
        setLocationRelativeTo(owner);

        // ESC để đóng
        bindEscToClose();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel lblTitle = new JLabel("CHI TIẾT NHẬT KÝ AUDIT");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_TITLE);

        JLabel lblSub = new JLabel("Xem thông tin thao tác và nội dung chi tiết");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(COLOR_LABEL);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setOpaque(false);
        titles.add(lblTitle);
        titles.add(Box.createVerticalStrut(2));
        titles.add(lblSub);

        header.add(titles, BorderLayout.WEST);
        header.add(new JSeparator(), BorderLayout.SOUTH);
        return header;
    }

    private JPanel createInfoCard(NhatKyAudit audit, String tenNhanVien) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(true);
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        addRow(card, g, r++, "Mã nhật ký", getFieldAuditId(audit));
        addRow(card, g, r++, "Nhân viên", audit.getNhanVienID() + "  —  " + tenNhanVien);
        addRow(card, g, r++, "Thời điểm", formatTime(audit));
        addRow(card, g, r++, "Đối tượng thao tác", audit.getDoiTuongThaoTac());
        addRow(card, g, r++, "Đối tượng ID", audit.getDoiTuongID());

        return card;
    }

    private JScrollPane createDetailCard(NhatKyAudit audit) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(FONT_AREA);
        area.setBackground(COLOR_AREA_BG);
        area.setBorder(new EmptyBorder(10, 10, 10, 10));
        area.setText(audit.getChiTiet() == null ? "" : audit.getChiTiet());
        area.setCaretPosition(0);

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new CompoundBorder(
                new TitledBorder(
                        new LineBorder(COLOR_BORDER, 1, true),
                        "Chi tiết thao tác",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 12),
                        COLOR_LABEL
                ),
                new EmptyBorder(6, 6, 6, 6)
        ));
        sp.getViewport().setBackground(COLOR_AREA_BG);
        return sp;
    }

    private JPanel createFooter(JScrollPane detailCard) {
        // lấy JTextArea từ scrollpane
        JTextArea area = (JTextArea) detailCard.getViewport().getView();

        JButton btnCopy = new JButton("Copy");
        btnCopy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCopy.setFocusPainted(false);
        btnCopy.addActionListener(e -> copyToClipboard(area.getText()));

        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDong.setFocusPainted(false);
        btnDong.addActionListener(e -> dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 0, 0, 0));
        footer.add(btnCopy);
        footer.add(btnDong);

        return footer;
    }


    // Thêm một hàng thông tin vào card
    private void addRow(JPanel p, GridBagConstraints g, int row, String label, String value) {
        g.gridy = row;

        g.gridx = 0;
        g.weightx = 0;
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(COLOR_LABEL);
        lbl.setPreferredSize(new Dimension(140, lbl.getPreferredSize().height));
        p.add(lbl, g);

        g.gridx = 1;
        g.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(FONT_VALUE);
        val.setForeground(COLOR_VALUE);
        p.add(val, g);
    }


    // Định dạng thời gian hiển thị
    private String formatTime(NhatKyAudit audit) {
        if (audit.getThoiDiemThaoTac() == null) return "-";
        return audit.getThoiDiemThaoTac().format(FMT);
    }

    // Nếu entity của bạn dùng getNhatKyID() thay vì getNhatKyAuditID(), đổi ở đây cho chắc
    private String getFieldAuditId(NhatKyAudit audit) {
        // Bạn sửa đúng theo entity của bạn:
        // return audit.getNhatKyID();
        try {
            // cố gọi getNhatKyID() nếu có
            return (String) audit.getClass().getMethod("getNhatKyID").invoke(audit);
        } catch (Exception ignored) {
            try {
                return (String) audit.getClass().getMethod("getNhatKyAuditID").invoke(audit);
            } catch (Exception ignored2) {
                return null;
            }
        }
    }

    // Sao chép văn bản vào clipboard
    private void copyToClipboard(String text) {
        if (text == null) text = "";
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
    }


    // Đóng dialog khi nhấn ESC
    private void bindEscToClose() {
        String key = "ESC_CLOSE";
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), key);
        getRootPane().getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
