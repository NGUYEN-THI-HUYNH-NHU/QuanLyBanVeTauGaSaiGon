package gui.application.form.quanLyTuyen;/*
 * @ (#) PanelThemTuyen.java   1.0     23/10/2025
package gui.application.form.quanLyTuyen;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 23/10/2025
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.jhlabs.image.GaussianFilter;
import controller.ThemTuyen_CTRL;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PanelThemTuyen extends JPanel {
    private final NhanVien nhanVienThucHien;
    private BufferedImage backgroundImage;

    private JPanel pnlGaTrungGianDaChon;

    private JTextField txtGaXuatPhat;
    private JTextField txtGaDich;
    private JTextField txtGaTrungGian;

    private JPopupMenu ppGaXuatPhat;
    private JPopupMenu ppGaDich;
    private JPopupMenu ppGaTrungGian;
    private JList<String> listGaXuatPhat;
    private JList<String> listGaDich;
    private JList<String> listGaTrungGian;

    private JButton btnLuu;
    private JButton btnHuy;
    private JButton btnXacNhanTinhKC;

    private JTextField txtMaTuyen;
    private JTextField txtDoDaiQuangDuong;

    private JTextArea txtMoTa;

    private DefaultTableModel modelGaChiTiet;
    private JTable tblGaChiTiet;

    private final Color COLOR_PRIMARY_BG = new Color(245, 250, 255);
    private final Color COLOR_BORDER = new Color(180, 180, 200);
    private final Color COLOR_ACCENT = new Color(36,104,155);
    private final Color COLOR_DANGER = new Color(220, 80, 80);
    private final Color COLOR_TABLE_BG = new Color(255,255,255,180);
    private final Color COLOR_TABLE_STRIPE = new Color(240,245,250,180);
    private final Color COLOR_TEXT_BG =new Color(255,255,255,180);

    public PanelThemTuyen(NhanVien nhanVien){
        this.nhanVienThucHien = nhanVien;
        setLayout(new BorderLayout());
        initComponents();
    }

    public void initComponents(){
        JPanel pnlContent = new JPanel(new MigLayout("wrap 4, fillx, insets 20 20 5 20","[120, left][250, grow][120, right][250, grow]","[]")); //layout lưới 4 cột, fill ngang

        pnlContent.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        // --- 1. Tiêu đề --- //
        JLabel lblTieuDe = new JLabel("THÊM TUYẾN MỚI");
        lblTieuDe.setFont(new Font("Times New Roman", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(36,104,155));
        pnlContent.add(lblTieuDe, "span 4, left, wrap 25 ");

        txtGaXuatPhat = new JTextField();
        txtGaXuatPhat.setBackground(COLOR_TEXT_BG);
        txtGaDich = new JTextField();
        txtGaDich.setBackground(COLOR_TEXT_BG);
        txtGaXuatPhat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Xuất Phát muốn thêm");
        txtGaDich.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Đích muốn thêm");
        pnlContent.add(new JLabel("Ga Xuất Phát:"), "w 100%");
        pnlContent.add(txtGaXuatPhat,"growx");
        pnlContent.add(new JLabel("Ga Đích:"));
        pnlContent.add(txtGaDich,"growx, wrap 20");

        txtMaTuyen = new JTextField();
        txtMaTuyen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã Tuyến được tạo tự động!");
        txtMaTuyen.setEditable(false);
        txtMaTuyen.setFocusable(false);
        txtMaTuyen.setBackground(COLOR_TEXT_BG);
        txtDoDaiQuangDuong = new JTextField();
        txtDoDaiQuangDuong.setEditable(false);
        txtDoDaiQuangDuong.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Khoảng cách được tính tự động!");
        txtDoDaiQuangDuong.setFocusable(false);
        txtDoDaiQuangDuong.setBackground(COLOR_TEXT_BG);
        pnlContent.add(new JLabel("Mã Tuyến:"));
        pnlContent.add(txtMaTuyen, "growx");
        pnlContent.add(new JLabel("Khoảng cách từ ga xuất phát đến ga đích (km):"));
        pnlContent.add(txtDoDaiQuangDuong, "growx, wrap 20");

        txtGaTrungGian = new JTextField();
        txtGaTrungGian.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên các Ga Trung Gian muốn thêm");
        txtGaTrungGian.setBackground(COLOR_TEXT_BG);
        pnlContent.add(new JLabel("Ga Trung Gian:"), "w 150");
        pnlContent.add(txtGaTrungGian, "growx");

        ppGaXuatPhat = new JPopupMenu();
        listGaXuatPhat = new JList<>();
        ppGaXuatPhat.add(new JScrollPane(listGaXuatPhat));

        ppGaDich = new JPopupMenu();
        listGaDich = new JList<>();
        ppGaDich.add(new JScrollPane(listGaDich));

        ppGaTrungGian = new JPopupMenu();
        listGaTrungGian = new JList<>();
        ppGaTrungGian.add(new JScrollPane(listGaTrungGian));

        pnlGaTrungGianDaChon = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        pnlGaTrungGianDaChon.setBorder(BorderFactory.createTitledBorder("Danh Sách Ga Trung Gian Đã Chọn:"));
        JScrollPane scrollPane = new JScrollPane(pnlGaTrungGianDaChon);
        scrollPane.setPreferredSize(new Dimension(200,70));
        pnlContent.add(scrollPane, "span 2, growx, pushx, height 70, wrap 20");

        btnXacNhanTinhKC = new JButton("Xác Nhận Danh Sách Các Ga");
        btnXacNhanTinhKC.setBackground(COLOR_ACCENT);
        btnXacNhanTinhKC.setForeground(Color.WHITE);
        pnlContent.add(btnXacNhanTinhKC, "wrap 20");

        JLabel lblHuongDanMoTa = new JLabel("Hãy nhập mô tả về tuyến (Ví Dụ: Tuyến Sài Gòn - Hà Nội) (*)Bắt buộc");
        lblHuongDanMoTa.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHuongDanMoTa.setForeground(Color.RED);
        txtMoTa = new JTextArea(3,30);
        txtMoTa.setBackground(COLOR_TEXT_BG);
        txtMoTa.setOpaque(false);
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.getViewport().setOpaque(false);
        scrollMoTa.setOpaque(false);
        scrollMoTa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnlContent.add(new JLabel("Mô Tả Tuyến:"));
        pnlContent.add(lblHuongDanMoTa, "span 3, wrap 5");
        pnlContent.add(scrollMoTa, "span 4, growx, pushx, wrap 20");

        String[] columnNames = {"Tên Ga", "Loại Ga", "Khoảng Cách Từ Ga Xuất Phát (km)"};
        modelGaChiTiet = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tất cả các ô đều không thể chỉnh sửa
            }
        };
        tblGaChiTiet = new JTable(modelGaChiTiet);
        tblGaChiTiet.setRowHeight(25);
        tblGaChiTiet.setShowGrid(true);
        tblGaChiTiet.setShowHorizontalLines(true);
        tblGaChiTiet.setGridColor(COLOR_BORDER);
        tblGaChiTiet.setEnabled(false);
        tblGaChiTiet.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(COLOR_TABLE_STRIPE);
                    } else {
                        c.setBackground(COLOR_TABLE_BG);
                    }
                }
                return c;
            }
        });
        tblGaChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTableHeader header = tblGaChiTiet.getTableHeader();
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD,12f));
        header.setBackground(new Color(36,104,155));

        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Thứ Tự và Khoảng Cách Các Ga Từ Ga Xuất Phát của các Ga trên Tuyến"));

        JScrollPane tableScrollPane = new JScrollPane(tblGaChiTiet);
        tableScrollPane.getViewport().setOpaque(false);
        pnlTable.add(tableScrollPane, BorderLayout.CENTER);
        pnlContent.add(pnlTable, "span 4, grow, push,height 200, wrap 0");


        this.add(pnlContent, BorderLayout.CENTER);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAction.setOpaque(false);
        btnLuu = new JButton("Lưu Tuyến");
        btnHuy = new JButton("Hủy Bỏ");
        btnLuu.setBackground(COLOR_ACCENT);
        btnLuu.setForeground(Color.WHITE);
        btnHuy.setBackground(COLOR_DANGER);
        btnHuy.setForeground(Color.WHITE);
        pnlAction.add(btnLuu);
        pnlAction.add(btnHuy);

        this.add(pnlAction, BorderLayout.SOUTH);

        ppGaXuatPhat = new JPopupMenu();
        listGaXuatPhat = new JList<>();
        ppGaXuatPhat.add(new JScrollPane(listGaXuatPhat));
        ppGaDich = new JPopupMenu();
        listGaDich = new JList<>();
        ppGaDich.add(new JScrollPane(listGaDich));
        ppGaTrungGian = new JPopupMenu();
        listGaTrungGian = new JList<>();
        ppGaTrungGian.add(new JScrollPane(listGaTrungGian));
    }


    @Override
    protected  void paintComponent(Graphics g){
        super.paintComponent(g);
        if(backgroundImage != null){
            g.drawImage(backgroundImage, 0,0,getWidth(), getHeight(), this);
        }
    }

    public NhanVien getNhanVienThucHien() {
        return nhanVienThucHien;
    }

    public JPanel getPnlGaTrungGianDaChon() {
        return pnlGaTrungGianDaChon;
    }

    public JPopupMenu getPpGaXuatPhat() {
        return ppGaXuatPhat;
    }

    public JPopupMenu getPpGaDich() {
        return ppGaDich;
    }

    public JPopupMenu getPpGaTrungGian() {
        return ppGaTrungGian;
    }

    public JList<String> getListGaXuatPhat() {
        return listGaXuatPhat;
    }

    public JList<String> getListGaDich() {
        return listGaDich;
    }

    public JList<String> getListGaTrungGian() {
        return listGaTrungGian;
    }

    public JButton getBtnLuu() {
        return btnLuu;
    }

    public JButton getBtnHuy() {
        return btnHuy;
    }

    public JButton getBtnXacNhanTinhKC() {
        return btnXacNhanTinhKC;
    }

    public JTextField getTxtMaTuyen() {
        return txtMaTuyen;
    }

    public JTextField getTxtDoDaiQuangDuong() {
        return txtDoDaiQuangDuong;
    }

    public JTextArea getTxtMoTa() {
        return txtMoTa;
    }

    public DefaultTableModel getModelGaChiTiet() {
        return modelGaChiTiet;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setPnlGaTrungGianDaChon(JPanel pnlGaTrungGianDaChon) {
        this.pnlGaTrungGianDaChon = pnlGaTrungGianDaChon;
    }

    public JTextField getTxtGaXuatPhat() {
        return txtGaXuatPhat;
    }

    public void setTxtGaXuatPhat(JTextField txtGaXuatPhat) {
        this.txtGaXuatPhat = txtGaXuatPhat;
    }

    public JTextField getTxtGaDich() {
        return txtGaDich;
    }

    public void setTxtGaDich(JTextField txtGaDich) {
        this.txtGaDich = txtGaDich;
    }

    public JTextField getTxtGaTrungGian() {
        return txtGaTrungGian;
    }

    public void setTxtGaTrungGian(JTextField txtGaTrungGian) {
        this.txtGaTrungGian = txtGaTrungGian;
    }

    public void setPpGaXuatPhat(JPopupMenu ppGaXuatPhat) {
        this.ppGaXuatPhat = ppGaXuatPhat;
    }

    public void setPpGaDich(JPopupMenu ppGaDich) {
        this.ppGaDich = ppGaDich;
    }

    public void setPpGaTrungGian(JPopupMenu ppGaTrungGian) {
        this.ppGaTrungGian = ppGaTrungGian;
    }

    public void setListGaXuatPhat(JList<String> listGaXuatPhat) {
        this.listGaXuatPhat = listGaXuatPhat;
    }

    public void setListGaDich(JList<String> listGaDich) {
        this.listGaDich = listGaDich;
    }

    public void setListGaTrungGian(JList<String> listGaTrungGian) {
        this.listGaTrungGian = listGaTrungGian;
    }

    public void setBtnLuu(JButton btnLuu) {
        this.btnLuu = btnLuu;
    }

    public void setBtnHuy(JButton btnHuy) {
        this.btnHuy = btnHuy;
    }

    public void setBtnXacNhanTinhKC(JButton btnXacNhanTinhKC) {
        this.btnXacNhanTinhKC = btnXacNhanTinhKC;
    }

    public void setTxtMaTuyen(JTextField txtMaTuyen) {
        this.txtMaTuyen = txtMaTuyen;
    }

    public void setTxtDoDaiQuangDuong(JTextField txtDoDaiQuangDuong) {
        this.txtDoDaiQuangDuong = txtDoDaiQuangDuong;
    }

    public void setTxtMoTa(JTextArea txtMoTa) {
        this.txtMoTa = txtMoTa;
    }

    public void setModelGaChiTiet(DefaultTableModel modelGaChiTiet) {
        this.modelGaChiTiet = modelGaChiTiet;
    }

    public JTable getTblGaChiTiet() {
        return tblGaChiTiet;
    }

    public void setTblGaChiTiet(JTable tblGaChiTiet) {
        this.tblGaChiTiet = tblGaChiTiet;
    }

    public Color getCOLOR_PRIMARY_BG() {
        return COLOR_PRIMARY_BG;
    }

    public Color getCOLOR_BORDER() {
        return COLOR_BORDER;
    }

    public Color getCOLOR_ACCENT() {
        return COLOR_ACCENT;
    }

    public Color getCOLOR_DANGER() {
        return COLOR_DANGER;
    }

    public Color getCOLOR_TABLE_BG() {
        return COLOR_TABLE_BG;
    }

    public Color getCOLOR_TABLE_STRIPE() {
        return COLOR_TABLE_STRIPE;
    }

    public Color getCOLOR_TEXT_BG() {
        return COLOR_TEXT_BG;
    }
}
