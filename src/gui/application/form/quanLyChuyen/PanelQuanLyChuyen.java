package gui.application.form.quanLyChuyen;/*
 * @ (#) PanelQuanLyChuyen.java   1.0     26/11/2025
package gui.application.form.quanLyChuyen;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 26/11/2025
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.raven.datechooser.DateChooser;
import controller.QuanLyChuyen_CTRL;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PanelQuanLyChuyen extends JPanel {
    private final NhanVien nhanVienThucHien;
    private QuanLyChuyen_CTRL quanLyChuyenCtrl;

    private JLabel lblMaChuyenValue;
    private JLabel lblTenChuyenValue;
    private JLabel lblTenTuyenValue;
    private JLabel lblGaDiValue;
    private JLabel lblGaDenValue;
    private JLabel lblTauValue;

    private JTable tableLichTrinh;
    private DefaultTableModel modelLichTrinh;
    private JScrollPane scrollPaneLichTrinh;

    private JPopupMenu ppMaChuyen, ppGaDi, ppGaDen, ppTau;
    private JList<String> listMaChuyen, listGaDi, listGaDen, listTau;

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

        new QuanLyChuyen_CTRL(this);
    }

    private void initComponents(){
        JPanel panelNorth  = createNorthPanel();
        add(panelNorth, BorderLayout.NORTH);

        JSplitPane splitPane = createCenterPanel();
        add(splitPane, BorderLayout.CENTER);

        setupKeyBindings();

        ppMaChuyen = new JPopupMenu();
        listMaChuyen = new JList<>();

        ppGaDi = new JPopupMenu();
        listGaDi = new JList<>();

        ppGaDen = new JPopupMenu();
        listGaDen = new JList<>();

        ppTau = new JPopupMenu();
        listTau = new JList<>();
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

        JLabel lblGoiY = new JLabel("Chọn ngày để tìm kiếm chuyến!");
        lblGoiY.setFont(BASE_FONT.deriveFont(Font.ITALIC, 12f));
        lblGoiY.setForeground(Color.GRAY);



        JPanel panelDate = new JPanel(new MigLayout("insets 0"));
        panelDate.setBackground(COLOR_BG);
        panelDate.add(new JLabel("Ngày:")).setFont(BASE_FONT);
        panelDate.add(txtNgayDi, "w 110!");
        panelDate.add(lblGoiY, "wrap");

        panelTitleBar.add(panelDate, "align left");

        JLabel title = new JLabel("QUẢN LÝ CHUYẾN TÀU", SwingConstants.CENTER);
        title.setFont(BASE_FONT.deriveFont(Font.BOLD, 28f));
        title.setForeground(COLOR_HEADER);
        panelTitleBar.add(title, "growx, center");

        panelNorth.add(panelTitleBar, "growx");

        //Tìm kiếm chi tiết
        JPanel panelSearch = new JPanel(new MigLayout("fillx, insets 5 0 5 0",
                "[pref!]5[grow]10[pref!]5[grow]10[pref!]5[grow]10[pref!]5[grow]",
                "[]"));
        panelSearch.setBackground(COLOR_BG);

        txtMaChuyen = new JTextField();
        txtMaChuyen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Nhập Mã Chuyến để tìm kiếm! (vd: SE1_20251001)");
        txtGaXuatPhat = new JTextField();
        txtGaXuatPhat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Nhập tên Ga Xuất Phát để tìm kiếm!");
        txtGaDich = new JTextField();
        txtGaDich.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Nhập tên Ga Đích để tìm kiếm!");
        txtTau = new JTextField();
        txtTau.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,"Nhập tên Tàu để tìm kiếm!");

        JLabel lblGaXuatPhat = new JLabel("Ga Xuất Phát:");
        lblGaXuatPhat.setFont(BASE_FONT);
        panelSearch.add(lblGaXuatPhat);
        panelSearch.add(txtGaXuatPhat, "growx");

        JLabel lblGaDich = new JLabel("Ga Đích:");
        lblGaDich.setFont(BASE_FONT);
        panelSearch.add(lblGaDich);
        panelSearch.add(txtGaDich, "growx");

        JLabel lblMaChuyen = new JLabel("Mã Chuyến:");
        lblMaChuyen.setFont(BASE_FONT);
        panelSearch.add(lblMaChuyen);
        panelSearch.add(txtMaChuyen, "growx");
        JLabel lblTau = new JLabel("Tàu:");
        lblTau.setFont(BASE_FONT);
        panelSearch.add(lblTau);
        panelSearch.add(txtTau, "growx");

        panelNorth.add(panelSearch, "growx, wrap 10");

        return panelNorth;
    }

    private JSplitPane createCenterPanel(){
        JScrollPane tableScrollPane = createTablePanel();

        JPanel deatailPanel = createDetailPanel();
        tableScrollPane.setPreferredSize(new Dimension(600, getHeight()));
        deatailPanel.setPreferredSize(new Dimension(400,getHeight()));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, deatailPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10,10));
        splitPane.setContinuousLayout(true);
        return splitPane;
    }

    private JScrollPane createTablePanel(){
        String[] columnNames = {"Mã Chuyến","Tên Chuyến", "Ga Xuất Phát", "Ga Đích", "Tàu", "Ngày Đi", "Giờ Đi", "Ngày Đến", "Giờ Đến"};
        tableModel = new DefaultTableModel(columnNames, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableChuyen = new JTable(tableModel);
        tableChuyen.setFont(BASE_FONT);
        tableChuyen.setRowHeight(30);

        tableChuyen.setShowGrid(true);
        tableChuyen.setShowVerticalLines(true);
        tableChuyen.setShowHorizontalLines(true);
        tableChuyen.setGridColor(new Color(210,210,210));

        JTableHeader hd = tableChuyen.getTableHeader();
        hd.setFont(BASE_FONT.deriveFont(Font.BOLD, 16f));
        hd.setBackground(COLOR_HEADER);
        hd.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableChuyen.getColumnModel().getColumnCount(); i++){
            tableChuyen.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        tableChuyen.getColumnModel().getColumn(0).setPreferredWidth(130);

        tableChuyen.getColumnModel().getColumn(1).setPreferredWidth(170);

        tableChuyen.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableChuyen.getColumnModel().getColumn(3).setPreferredWidth(100);

        tableChuyen.getColumnModel().getColumn(4).setPreferredWidth(80);

        tableChuyen.getColumnModel().getColumn(5).setPreferredWidth(100);
        tableChuyen.getColumnModel().getColumn(6).setPreferredWidth(60);

        tableChuyen.getColumnModel().getColumn(7).setPreferredWidth(100);
        tableChuyen.getColumnModel().getColumn(8).setPreferredWidth(60);
        return new JScrollPane(tableChuyen);
    }

    private JPanel createDetailPanel(){
        JPanel detailPanel = new JPanel(new BorderLayout(0,10));
        detailPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                "THÔNG TIN CHI TIẾT CHUYẾN",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                BASE_FONT.deriveFont(Font.BOLD)
        ));
        detailPanel.setBackground(COLOR_BG);

        JPanel pnlThongTin = new JPanel(new MigLayout("wrap 2, fillx, insets 10", "[110!, shrink 0][]", "5[]5"));
        pnlThongTin.setBackground(COLOR_BG);

        lblMaChuyenValue = new JLabel("...");
        lblTenChuyenValue = new JLabel("...");
        lblTenTuyenValue = new JLabel("...");
        lblGaDiValue = new JLabel("...");
        lblGaDenValue = new JLabel("...");
        lblTauValue = new JLabel("...");

        Font labelFont = BASE_FONT.deriveFont(Font.BOLD);

        pnlThongTin.add(new JLabel("Mã Chuyến:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblMaChuyenValue, "growx");

        pnlThongTin.add(new JLabel("Tên Chuyến:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblTenChuyenValue, "growx");

        pnlThongTin.add(new JLabel("Tên Tuyến:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblTenTuyenValue, "growx");

        pnlThongTin.add(new JLabel("Ga Xuất Phát:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblGaDiValue, "growx");

        pnlThongTin.add(new JLabel("Ga Đích:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblGaDenValue, "growx");

        pnlThongTin.add(new JLabel("Tàu:"){ { setFont(BASE_FONT); } });
        pnlThongTin.add(lblTauValue, "growx");

        JPanel pnlLichTrinh = new JPanel(new BorderLayout());
        pnlLichTrinh.setBackground(COLOR_BG);
        pnlLichTrinh.setBorder(BorderFactory.createTitledBorder(null, "LỊCH TRÌNH", 0, 0, labelFont));

        String[] columns = {"STT", "Ga Đi", "Ngày Đi", "Giờ Đi", "Ga Đến","Ngày Đến", "Giờ Đến"};
        modelLichTrinh = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableLichTrinh = new JTable(modelLichTrinh);
        tableLichTrinh.setFont(BASE_FONT);
        tableLichTrinh.setRowHeight(28);

        tableLichTrinh.setShowGrid(true);
        tableLichTrinh.setShowVerticalLines(true);
        tableLichTrinh.setShowHorizontalLines(true);
        tableLichTrinh.setGridColor(new Color(210,210,210));

        JTableHeader headerLT = tableLichTrinh.getTableHeader();
        headerLT.setFont(BASE_FONT.deriveFont(Font.BOLD, 13f));
        headerLT.setBackground(new Color(230, 230, 230));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 0; i < tableLichTrinh.getColumnModel().getColumnCount(); i++){
            tableLichTrinh.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        tableLichTrinh.getColumnModel().getColumn(0).setPreferredWidth(35);
        tableLichTrinh.getColumnModel().getColumn(0).setMaxWidth(40);

        tableLichTrinh.getColumnModel().getColumn(1).setPreferredWidth(100);

        tableLichTrinh.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableLichTrinh.getColumnModel().getColumn(3).setPreferredWidth(50);

        tableLichTrinh.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableLichTrinh.getColumnModel().getColumn(5).setPreferredWidth(100);

        tableLichTrinh.getColumnModel().getColumn(6).setPreferredWidth(50); // STT

        pnlLichTrinh.add(new JScrollPane(tableLichTrinh), BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new BorderLayout(0,10));
        centerContainer.setBackground(COLOR_BG);
        centerContainer.add(pnlThongTin, BorderLayout.NORTH);
        centerContainer.add(pnlLichTrinh, BorderLayout.CENTER);

        detailPanel.add(centerContainer, BorderLayout.CENTER);

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelActions.setBackground(COLOR_BG);

        btnLamMoi = new JButton("(F5) Làm Mới");
        btnThemChuyen = new JButton("Thêm Chuyến");
        btnCapNhatChuen = new JButton("Cập Nhật");

        JButton[] buttons = {btnLamMoi, btnThemChuyen, btnCapNhatChuen};
        for(JButton btn : buttons){
            btn.setFont(labelFont);
            btn.setBackground(COLOR_ACCENT);
            btn.setForeground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(5, 10, 5, 10));
        }
        btnLamMoi.setIcon(new FlatSVGIcon("gui/icon/svg/refresh.svg", 0.35f));
        btnThemChuyen.setIcon(new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f));
        btnCapNhatChuen.setIcon(new FlatSVGIcon("gui/icon/svg/edit.svg", 0.35f));

        panelActions.add(btnLamMoi);
        panelActions.add(btnThemChuyen);
        panelActions.add(btnCapNhatChuen);

        detailPanel.add(panelActions, BorderLayout.SOUTH);
        return detailPanel;
    }

    private void setupKeyBindings(){
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0), "refreshAction");
        this.getActionMap().put("refreshAction", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(btnLamMoi != null && btnLamMoi.isEnabled()){
                    btnLamMoi.doClick();
                }
            }
        });
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

    public QuanLyChuyen_CTRL getQuanLyChuyenCtrl() {
        return quanLyChuyenCtrl;
    }

    public void setQuanLyChuyenCtrl(QuanLyChuyen_CTRL quanLyChuyenCtrl) {
        this.quanLyChuyenCtrl = quanLyChuyenCtrl;
    }

    public JLabel getLblMaChuyenValue() {
        return lblMaChuyenValue;
    }

    public void setLblMaChuyenValue(JLabel lblMaChuyenValue) {
        this.lblMaChuyenValue = lblMaChuyenValue;
    }

    public JLabel getLblTenChuyenValue() {
        return lblTenChuyenValue;
    }

    public void setLblTenChuyenValue(JLabel lblTenChuyenValue) {
        this.lblTenChuyenValue = lblTenChuyenValue;
    }

    public JLabel getLblTenTuyenValue() {
        return lblTenTuyenValue;
    }

    public void setLblTenTuyenValue(JLabel lblTenTuyenValue) {
        this.lblTenTuyenValue = lblTenTuyenValue;
    }

    public JLabel getLblGaDiValue() {
        return lblGaDiValue;
    }

    public void setLblGaDiValue(JLabel lblGaDiValue) {
        this.lblGaDiValue = lblGaDiValue;
    }

    public JLabel getLblGaDenValue() {
        return lblGaDenValue;
    }

    public void setLblGaDenValue(JLabel lblGaDenValue) {
        this.lblGaDenValue = lblGaDenValue;
    }

    public JLabel getLblTauValue() {
        return lblTauValue;
    }

    public void setLblTauValue(JLabel lblTauValue) {
        this.lblTauValue = lblTauValue;
    }

    public JTable getTableLichTrinh() {
        return tableLichTrinh;
    }

    public void setTableLichTrinh(JTable tableLichTrinh) {
        this.tableLichTrinh = tableLichTrinh;
    }

    public DefaultTableModel getModelLichTrinh() {
        return modelLichTrinh;
    }

    public void setModelLichTrinh(DefaultTableModel modelLichTrinh) {
        this.modelLichTrinh = modelLichTrinh;
    }

    public JScrollPane getScrollPaneLichTrinh() {
        return scrollPaneLichTrinh;
    }

    public void setScrollPaneLichTrinh(JScrollPane scrollPaneLichTrinh) {
        this.scrollPaneLichTrinh = scrollPaneLichTrinh;
    }

    public DateChooser getDateChooser() {
        return dateChooser;
    }

    public void setDateChooser(DateChooser dateChooser) {
        this.dateChooser = dateChooser;
    }

    public JPopupMenu getPpMaChuyen() {
        return ppMaChuyen;
    }

    public void setPpMaChuyen(JPopupMenu ppMaChuyen) {
        this.ppMaChuyen = ppMaChuyen;
    }

    public JPopupMenu getPpGaDi() {
        return ppGaDi;
    }

    public void setPpGaDi(JPopupMenu ppGaDi) {
        this.ppGaDi = ppGaDi;
    }

    public JPopupMenu getPpGaDen() {
        return ppGaDen;
    }

    public void setPpGaDen(JPopupMenu ppGaDen) {
        this.ppGaDen = ppGaDen;
    }

    public JPopupMenu getPpTau() {
        return ppTau;
    }

    public void setPpTau(JPopupMenu ppTau) {
        this.ppTau = ppTau;
    }

    public JList<String> getListMaChuyen() {
        return listMaChuyen;
    }

    public void setListMaChuyen(JList<String> listMaChuyen) {
        this.listMaChuyen = listMaChuyen;
    }

    public JList<String> getListGaDi() {
        return listGaDi;
    }

    public void setListGaDi(JList<String> listGaDi) {
        this.listGaDi = listGaDi;
    }

    public JList<String> getListGaDen() {
        return listGaDen;
    }

    public void setListGaDen(JList<String> listGaDen) {
        this.listGaDen = listGaDen;
    }

    public JList<String> getListTau() {
        return listTau;
    }

    public void setListTau(JList<String> listTau) {
        this.listTau = listTau;
    }
}
