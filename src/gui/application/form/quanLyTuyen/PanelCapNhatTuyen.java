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
import controller.CapNhatTuyen_CTRL;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PanelCapNhatTuyen extends JPanel {
    private final NhanVien nhanVienThucHien;

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
    private final Color COLOR_ACCENT = new Color(30,41,58);
    private final Color COLOR_DANGER = new Color(220, 80, 80);
    private final Color COLOR_TABLE_BG = new Color(240, 245, 250);

    public PanelCapNhatTuyen(NhanVien nhanVien){
        this.nhanVienThucHien = nhanVien;
//        this.setBackground(COLOR_PRIMARY_BG);
        setLayout(new BorderLayout());
        initComponents();
        new CapNhatTuyen_CTRL(this);
    }

    public void initComponents(){
        JPanel pnlContent = new JPanel(new MigLayout("wrap 4, fillx, insets 20 20 5 20","[120, left][250, grow][120, right][250, grow]","[]")); //layout lưới 4 cột, fill ngang
        pnlContent.setOpaque(false);

        pnlContent.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));

        // --- 1. Tiêu đề --- //
        JLabel lblTieuDe = new JLabel("CẬP NHẬT THÔNG TIN TUYẾN");
        lblTieuDe.setFont(new Font("Times New Roman", Font.BOLD, 24));
        lblTieuDe.setForeground(new Color(30,41,58));
        pnlContent.add(lblTieuDe, "span 4, left, wrap 25 ");

        txtGaXuatPhat = new JTextField();
        txtGaDich = new JTextField();
        txtGaXuatPhat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Xuất Phát muốn cập nhật");
        txtGaDich.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Đích muốn cập nhật");
        pnlContent.add(new JLabel("Ga Xuất Phát:"), "w 100%");
        pnlContent.add(txtGaXuatPhat,"growx");
        pnlContent.add(new JLabel("Ga Đích:"));
        pnlContent.add(txtGaDich,"growx, wrap 20");

        txtMaTuyen = new JTextField();
        txtMaTuyen.setEditable(true);
        txtMaTuyen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập Mã Tuyến để cập nhật");
        txtDoDaiQuangDuong = new JTextField();
        txtDoDaiQuangDuong.setEditable(false);
        txtDoDaiQuangDuong.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Khoảng cách được tính tự động!");
        txtDoDaiQuangDuong.setEditable(false);
        pnlContent.add(new JLabel("Mã Tuyến:"));
        pnlContent.add(txtMaTuyen, "growx");
        pnlContent.add(new JLabel("Khoảng cách từ ga xuất phát đến ga đích (km):"));
        pnlContent.add(txtDoDaiQuangDuong, "growx, wrap 20");

        txtGaTrungGian = new JTextField();
        txtGaTrungGian.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên các Ga Trung Gian muốn cập nhật");
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

        txtMoTa = new JTextArea(3,20);
        txtMoTa.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        pnlContent.add(new JLabel("Mô Tả Tuyến:"));
        pnlContent.add(scrollMoTa, "span 3, growx, pushx, wrap 20");

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
        tblGaChiTiet.setShowVerticalLines(true);
        tblGaChiTiet.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//        tblGaChiTiet.setBackground(COLOR_TABLE_BG);
        tblGaChiTiet.setGridColor(COLOR_BORDER);
        JTableHeader header = tblGaChiTiet.getTableHeader();
        header.setBackground(COLOR_ACCENT);
        header.setForeground(Color.WHITE);

        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Thứ Tự và Khoảng Cách Các Ga Từ Ga Xuất Phát của các Ga trên Tuyến"));
        pnlTable.add(new JScrollPane(tblGaChiTiet), BorderLayout.CENTER);
        pnlContent.add(pnlTable, "span 4, grow, push,height 200, wrap 0");


        this.add(pnlContent, BorderLayout.CENTER);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAction.setOpaque(false);
        btnLuu = new JButton("Cập Nhật Tuyến");
        btnHuy = new JButton("Hủy Bỏ");
        btnLuu.setBackground(COLOR_ACCENT);
        btnLuu.setForeground(Color.WHITE);
        btnHuy.setBackground(COLOR_DANGER);
        btnHuy.setForeground(Color.WHITE);
        pnlAction.add(btnLuu);
        pnlAction.add(btnHuy);

        this.add(pnlAction, BorderLayout.SOUTH);
    }



    public NhanVien getNhanVienThucHien() {
        return nhanVienThucHien;
    }

    public JPanel getPnlGaTrungGianDaChon() {
        return pnlGaTrungGianDaChon;
    }

    public JTextField getTxtGaXuatPhat() {
        return txtGaXuatPhat;
    }

    public JTextField getTxtGaDich() {
        return txtGaDich;
    }

    public JTextField getTxtGaTrungGian() {
        return txtGaTrungGian;
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

    public JTable getTblGaChiTiet() {
        return tblGaChiTiet;
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
}
