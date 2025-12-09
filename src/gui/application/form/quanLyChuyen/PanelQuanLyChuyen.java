package gui.application.form.quanLyChuyen;/*
 * @ (#) PanelQuanLyChuyen.java   1.0     26/11/2025
package gui.application.form.quanLyChuyen;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 26/11/2025
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.raven.datechooser.DateChooser;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class PanelQuanLyChuyen extends JPanel {
    private final NhanVien nhanVienThucHien;

    private JTextField txtMaChuyen, txtGaXuatPhat, txtGaDich, txtTau, txtNgayDi;
    private DateChooser dateChooser;
    private JButton btnLamMoi;

    private JTable tableChuyen;
    private DefaultTableModel tableModel;

    private JTextArea txtChiTietChuyen;
    private JButton btnThemChuyen, btnCapNhatChuen;

    private final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Color COLOR_ACCENT = new Color(36,104,155);
    private final Color COLOR_HEADER = new Color(30,41,58);
    private final Color COLOR_BG = new Color(245, 250, 255);

    public PanelQuanLyChuyen(NhanVien nhanVien){
        this.nhanVienThucHien = nhanVien;
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        initComponents();
    }

    private void initComponents(){
        //(HEADER VÀ TÌM KIẾM)
        JPanel panelNorth  = createNorthPanel();
        add(panelNorth, BorderLayout.NORTH);

        //(BẢNG VÀ CHI TIẾT) 10
        JSplitPane splitPane = createCenterPanel();
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createNorthPanel(){
        JPanel panelNorth = new JPanel(new MigLayout("wrap 1, fillx, insets 10 10 0 10", "[fill, grow]", "[]10[]"));
        panelNorth.setBackground(COLOR_BG);

        JPanel panelTitleBar = new JPanel(new MigLayout("fillx, insets 0", "[pref!][grow, center][pref!]", "[]"));
        panelTitleBar.setBackground(COLOR_BG);
        txtNgayDi = new JTextField(10);
        txtNgayDi.setFont(BASE_FONT);

        dateChooser = new DateChooser();
        dateChooser.setTextRefernce(txtNgayDi);
        dateChooser.setDateFormat("dd/MM/yyyy");

        JPanel panelDate = new JPanel(new MigLayout("insets 0"));
        panelDate.setBackground(COLOR_BG);
        panelDate.add(new JLabel("Ngày:")).setFont(BASE_FONT);
        panelDate.add(txtNgayDi, "w 110!");

        panelTitleBar.add(panelDate, "align left");

        JLabel title = new JLabel("QUẢN LÝ CHUYẾN TÀU", SwingConstants.CENTER);
        title.setFont(BASE_FONT.deriveFont(Font.BOLD, 28f));
        title.setForeground(COLOR_HEADER);
        panelTitleBar.add(title, "growx, center");

        btnLamMoi = new JButton("(F5) Làm Mới");
        btnLamMoi.setIcon(new FlatSVGIcon("gui/icon/svg/refresh.svg", 0.3f));
        btnLamMoi.setBackground(COLOR_ACCENT);
        btnLamMoi.setForeground(Color.WHITE);
        btnLamMoi.setFont(BASE_FONT);
        btnLamMoi.setMargin(new Insets(2,8,2,8));

        panelTitleBar.add(title, "growx, pushx, center");

        panelTitleBar.add(btnLamMoi, "align right");
        panelNorth.add(panelTitleBar, "growx");

        //Tìm kiếm chi tiết
        JPanel panelSearch = new JPanel(new MigLayout("fillx, insets 5 0 5 0" , "[label] 5 [grow] 20 [label] 5 [grow] 20 [label] 5 [grow]", "[]"));
        panelSearch.setBackground(COLOR_BG);

        txtMaChuyen = new JTextField();
        txtGaXuatPhat = new JTextField();
        txtGaDich = new JTextField();
        txtTau = new JTextField();

        JLabel lblGaXuatPhat = new JLabel("Ga Xuất Phát:");
        lblGaXuatPhat.setFont(BASE_FONT);
        panelSearch.add(lblGaXuatPhat, "w 80");
        panelSearch.add(txtGaXuatPhat, "w 200, gapright 15");

        JLabel lblGaDich = new JLabel("Ga Đích:");
        lblGaDich.setFont(BASE_FONT);
        panelSearch.add(lblGaDich, "w 80");
        panelSearch.add(txtGaDich, "w 200, gapright 15, wrap");

        JLabel lblMaChuyen = new JLabel("Mã Chuyến:");
        lblMaChuyen.setFont(BASE_FONT);
        panelSearch.add(lblMaChuyen,"w 80");
        panelSearch.add(txtMaChuyen, "w 200, gapright 15");
        JLabel lblTau = new JLabel("Tàu:");
        lblTau.setFont(BASE_FONT);
        panelSearch.add(lblTau, "w 80");
        panelSearch.add(txtTau, "w 200, gapright 15");

        panelNorth.add(panelSearch, "growx, wrap 10");

        return panelNorth;
    }

    private JSplitPane createCenterPanel(){
        JScrollPane tableScrollPane = createTablePanel();

        JPanel deatailPanel = createDetailPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, deatailPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10,10));
        return splitPane;
    }

    private JScrollPane createTablePanel(){
        String[] columnNames = {"Mã Chuyến","Tên Chuyến", "Ga Xuất Phát", "Ga Đích", "Tàu", "Ngày Đi", "Ngày Đến", "Giờ Đi", "Giờ Đến"};
        tableModel = new DefaultTableModel(columnNames, 0);
        tableChuyen = new JTable(tableModel);
        tableChuyen.setFont(BASE_FONT);
        tableChuyen.setRowHeight(30);

        JTableHeader hd = tableChuyen.getTableHeader();
        hd.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        hd.setBackground(COLOR_HEADER);
        hd.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableChuyen.getColumnModel().getColumnCount(); i++){
            tableChuyen.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        return new JScrollPane(tableChuyen);
    }

    private JPanel createDetailPanel(){
        JPanel detailPanel = new JPanel(new BorderLayout(10,10));
        detailPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Chi Tiết Chuyến"));

        txtChiTietChuyen = new JTextArea();
        txtChiTietChuyen.setFont(BASE_FONT);
        txtChiTietChuyen.setEditable(false);
        detailPanel.add(new JScrollPane(txtChiTietChuyen), BorderLayout.CENTER);

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnThemChuyen = new JButton("Thêm Chuyến");
        btnCapNhatChuen = new JButton("Cập Nhật Chuyến");

        btnThemChuyen.setFont(BASE_FONT);
        btnCapNhatChuen.setFont(BASE_FONT);
        btnThemChuyen.setBackground(COLOR_ACCENT);
        btnThemChuyen.setForeground(Color.WHITE);
        btnCapNhatChuen.setBackground(COLOR_ACCENT);
        btnCapNhatChuen.setForeground(Color.WHITE);

        panelActions.add(btnThemChuyen);
        panelActions.add(btnCapNhatChuen);

        detailPanel.add(panelActions, BorderLayout.SOUTH);
        return detailPanel;
    }

    public NhanVien getNhanVienThucHien() {
        return nhanVienThucHien;
    }

    public JTextField getTxtMaChuyen() {
        return txtMaChuyen;
    }

    public void setTxtMaChuyen(JTextField txtMaChuyen) {
        this.txtMaChuyen = txtMaChuyen;
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

    public JTextField getTxtTau() {
        return txtTau;
    }

    public void setTxtTau(JTextField txtTau) {
        this.txtTau = txtTau;
    }

    public JTextField getTxtNgayDi() {
        return txtNgayDi;
    }

    public void setTxtNgayDi(JTextField txtNgayDi) {
        this.txtNgayDi = txtNgayDi;
    }

    public JButton getBtnLamMoi() {
        return btnLamMoi;
    }

    public void setBtnLamMoi(JButton btnLamMoi) {
        this.btnLamMoi = btnLamMoi;
    }

    public JTable getTableChuyen() {
        return tableChuyen;
    }

    public void setTableChuyen(JTable tableChuyen) {
        this.tableChuyen = tableChuyen;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public JTextArea getTxtChiTietChuyen() {
        return txtChiTietChuyen;
    }

    public void setTxtChiTietChuyen(JTextArea txtChiTietChuyen) {
        this.txtChiTietChuyen = txtChiTietChuyen;
    }

    public JButton getBtnThemChuyen() {
        return btnThemChuyen;
    }

    public void setBtnThemChuyen(JButton btnThemChuyen) {
        this.btnThemChuyen = btnThemChuyen;
    }

    public JButton getBtnCapNhatChuen() {
        return btnCapNhatChuen;
    }

    public void setBtnCapNhatChuen(JButton btnCapNhatChuen) {
        this.btnCapNhatChuen = btnCapNhatChuen;
    }

    public Font getBASE_FONT() {
        return BASE_FONT;
    }

    public Color getCOLOR_ACCENT() {
        return COLOR_ACCENT;
    }

    public Color getCOLOR_HEADER() {
        return COLOR_HEADER;
    }

    public Color getCOLOR_BG() {
        return COLOR_BG;
    }
}
