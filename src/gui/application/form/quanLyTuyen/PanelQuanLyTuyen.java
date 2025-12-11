package gui.application.form.quanLyTuyen;

import bus.Tuyen_BUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import controller.QuanLyTuyen_CTRL;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PanelQuanLyTuyen extends JPanel {
    private final Tuyen_BUS tuyen_bus;
    private final NhanVien nhanVienThucHien;

    private JTextField txtGaDi;
    private JTextField txtGaDen;
    private JTextField txtTimKiem;
    private JButton btnThemTuyen;
    private JButton btnCapNhatTuyen;
    private JButton btnLamMoiTuyen;

    private JTable tableTuyen;
    private DefaultTableModel tableModelTuyen;
    private JScrollPane scrollPaneTable;

    private JPanel pnlChiTiet;
    private JTextArea txtThongTinChung;
    private JTable tableChiTietGa;
    private DefaultTableModel modelChiTietGa;

    private JPopupMenu ppGaDi;
    private JPopupMenu ppGaDen;
    private JPopupMenu ppTuyenID;
    private JList<String> listTuyenID;
    private JList<String> listGaDi;
    private JList<String> listGaDen;
    private JTextField txtChiTietMaTuyen;
    private JTextField txtChiTietTenTuyen;
    private JTextField txtChiTietKhoangCach;
    private JTextArea txtChiTietMoTa;


    public PanelQuanLyTuyen(NhanVien nhanVien){
        setLayout(new BorderLayout());
        this.tuyen_bus = new Tuyen_BUS();
        this.nhanVienThucHien = nhanVien;

        initComponents();
        new QuanLyTuyen_CTRL(this, tuyen_bus);
    }

    public void initComponents(){
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 15);

        // --- 1. HEADER PANEL (NORTH) ---
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.setOpaque(false);

        // Tiêu đề
        JPanel panelHeader = new JPanel(new MigLayout("wrap 1, fillx, insets 10 10 5 10"));
        panelHeader.setOpaque(false);
        JLabel title = new JLabel("QUẢN LÝ VÀ TRA CỨU TUYẾN ĐƯỜNG SẮT", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(30,41,58));
        panelHeader.add(title, "growx");

        // Input fields
        Color translucentWhite = new Color(255,255,255,180);
        txtGaDi = new JTextField(15);
        txtGaDi.setFont(baseFont);
        txtGaDi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên ga đi");
        txtGaDi.setBackground(translucentWhite);

        txtGaDen = new JTextField(15);
        txtGaDen.setFont(baseFont);
        txtGaDen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên ga đến");
        txtGaDen.setBackground(translucentWhite);

        txtTimKiem = new JTextField(10);
        txtTimKiem.setFont(baseFont);
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã tuyến");
        txtTimKiem.setBackground(translucentWhite);

        // Buttons
        btnThemTuyen = new JButton("Thêm tuyến");
        btnCapNhatTuyen = new JButton("Cập nhật tuyến");
        btnLamMoiTuyen = new JButton("(F5) Làm mới");
        setMauBTN();

        btnThemTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f));
        btnThemTuyen.setBackground(new Color(36, 104, 155));
        btnThemTuyen.setForeground(Color.white);

        btnCapNhatTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/edit.svg", 0.35f));
        btnCapNhatTuyen.setBackground(new Color(36, 104, 155));
        btnCapNhatTuyen.setForeground(Color.white);

        btnLamMoiTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/refresh.svg", 0.35f));
        btnLamMoiTuyen.setBackground(new Color(36, 104, 155));
        btnLamMoiTuyen.setForeground(Color.white);

        // Panel Search Layout
        JPanel panelSearch = new JPanel(new MigLayout("insets 5 10 10 10, gap 10", "[grow][grow][][][]", "[]"));
        panelSearch.setOpaque(false);

        JPanel col1 = new JPanel(new MigLayout("insets 0, wrap 2, fillx", "[][grow]", "[][]"));
        col1.setOpaque(false);
        col1.add(new JLabel("Ga Xuất Phát:")); col1.add(txtGaDi, "growx");
        col1.add(new JLabel("Ga Đích:")); col1.add(txtGaDen, "growx");

        JPanel col2 = new JPanel(new MigLayout("insets 0, wrap 2, fillx", "[][grow]", "[]"));
        col2.setOpaque(false);
        col2.add(new JLabel("Mã Tuyến:")); col2.add(txtTimKiem, "growx");

        panelSearch.add(col1, "grow, pushy");
        panelSearch.add(col2, "grow, pushy, top");
        panelSearch.add(btnThemTuyen, "top");
        panelSearch.add(btnCapNhatTuyen, "top");
        panelSearch.add(btnLamMoiTuyen, "top");

        panelHeader.add(panelSearch, "growx");
        panelNorth.add(panelHeader, BorderLayout.NORTH);
        add(panelNorth, BorderLayout.NORTH);

        // --- 2. CENTER PANEL (SPLIT PANE) ---

        // A. Bảng Danh Sách Tuyến (LEFT)
        String[] columnNames = {"Mã Tuyến", "Ga XP", "Ga Đích", "Quãng Đường (km)"};
        tableModelTuyen = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTuyen = new JTable(tableModelTuyen);
        tableTuyen.setRowHeight(28);
        tableTuyen.setFont(baseFont);

        // Table Style
        JTableHeader hd = tableTuyen.getTableHeader();
        hd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hd.setBackground(new Color(30,41,58));
        hd.setForeground(Color.white);
        ((DefaultTableCellRenderer)hd.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        tableTuyen.setShowGrid(true);
        tableTuyen.setShowHorizontalLines(true);
        tableTuyen.setShowVerticalLines(true);

        // Striped Rows
        tableTuyen.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 248, 255) : Color.WHITE);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        scrollPaneTable = new JScrollPane(tableTuyen);
        scrollPaneTable.setMinimumSize(new Dimension(600, 0)); // Chiều rộng tối thiểu cho bảng

        // B. Panel Chi Tiết (RIGHT)
        pnlChiTiet = new JPanel(new BorderLayout(0, 10));
        pnlChiTiet.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(36, 104, 155)),
                "THÔNG TIN CHI TIẾT",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(36, 104, 155))
        );
        pnlChiTiet.setBackground(Color.WHITE);

        // B1. Panel Thông Tin Chung (Form phía trên)
        JPanel pnlThongTinCuThe = new JPanel(new MigLayout("fillx, insets 5", "[pref!][grow]", "[]10[]10[]10[]"));
        pnlThongTinCuThe.setBackground(Color.WHITE);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color readOnlyColor = new Color(245, 245, 245);

        // Mã Tuyến
        pnlThongTinCuThe.add(new JLabel("Mã Tuyến:"));
        txtChiTietMaTuyen = new JTextField();
        txtChiTietMaTuyen.setEditable(false);
        txtChiTietMaTuyen.setBackground(readOnlyColor);
        txtChiTietMaTuyen.setFont(textFont);
        pnlThongTinCuThe.add(txtChiTietMaTuyen, "growx, wrap");

        // Tên Tuyến (Ga XP - Ga Đích)
        pnlThongTinCuThe.add(new JLabel("Tên Tuyến:"));
        txtChiTietTenTuyen = new JTextField();
        txtChiTietTenTuyen.setEditable(false);
        txtChiTietTenTuyen.setBackground(readOnlyColor);
        txtChiTietTenTuyen.setFont(textFont);
        pnlThongTinCuThe.add(txtChiTietTenTuyen, "growx, wrap");

        // Khoảng cách
        pnlThongTinCuThe.add(new JLabel("Quãng Đường:"));
        txtChiTietKhoangCach = new JTextField();
        txtChiTietKhoangCach.setEditable(false);
        txtChiTietKhoangCach.setBackground(readOnlyColor);
        txtChiTietKhoangCach.setFont(textFont);
        pnlThongTinCuThe.add(txtChiTietKhoangCach, "growx, wrap");

        // Mô tả
        pnlThongTinCuThe.add(new JLabel("Mô Tả:"), "top");
        txtChiTietMoTa = new JTextArea(3, 20);
        txtChiTietMoTa.setEditable(false);
        txtChiTietMoTa.setBackground(readOnlyColor);
        txtChiTietMoTa.setFont(textFont);
        txtChiTietMoTa.setLineWrap(true);
        txtChiTietMoTa.setWrapStyleWord(true);
        txtChiTietMoTa.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlThongTinCuThe.add(new JScrollPane(txtChiTietMoTa), "growx, wrap");

        // B2. Panel Danh Sách Ga (Phía dưới)
        JPanel pnlDanhSachGa = new JPanel(new BorderLayout());
        pnlDanhSachGa.setBackground(Color.WHITE);

        // Label tiêu đề cho bảng
        JLabel lblTableTitle = new JLabel("Danh sách các ga trung gian trên tuyến:");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
        lblTableTitle.setForeground(new Color(36, 104, 155));
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        // Bảng
        String[] colChiTiet = {"STT", "Tên Ga", "Loại Ga", "Khoảng cách giữa 2 ga"};
        modelChiTietGa = new DefaultTableModel(colChiTiet, 0);
        tableChiTietGa = new JTable(modelChiTietGa);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableChiTietGa.setDefaultRenderer(Object.class, centerRenderer);
        tableChiTietGa.setRowHeight(25);
        tableChiTietGa.setShowGrid(true);
        tableChiTietGa.getTableHeader().setBackground(new Color(36, 104, 155));
        tableChiTietGa.getTableHeader().setForeground(Color.WHITE);

        tableChiTietGa.getColumnModel().getColumn(0).setWidth(20);
        tableChiTietGa.getColumnModel().getColumn(1).setWidth(100);
        tableChiTietGa.getColumnModel().getColumn(2).setWidth(150);
        tableChiTietGa.getColumnModel().getColumn(3).setWidth(180);

        pnlDanhSachGa.add(lblTableTitle, BorderLayout.NORTH);
        pnlDanhSachGa.add(new JScrollPane(tableChiTietGa), BorderLayout.CENTER);

        // Add 2 phần vào Panel Chi Tiết chính
        pnlChiTiet.add(pnlThongTinCuThe, BorderLayout.NORTH);
        pnlChiTiet.add(pnlDanhSachGa, BorderLayout.CENTER);

        // C. SPLIT PANE (Chia đôi)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneTable, pnlChiTiet);
        splitPane.setResizeWeight(0.65);
        splitPane.setDividerSize(8);
        splitPane.setOneTouchExpandable(true);

        add(splitPane, BorderLayout.CENTER);

        // --- 3. POPUP GỢI Ý ---
        ppGaDi = new JPopupMenu(); listGaDi = new JList<>(); ppGaDi.add(new JScrollPane(listGaDi));
        ppGaDen = new JPopupMenu(); listGaDen = new JList<>(); ppGaDen.add(new JScrollPane(listGaDen));
        ppTuyenID = new JPopupMenu(); listTuyenID = new JList<>(); ppTuyenID.add(new JScrollPane(listTuyenID));

        // Load data ban đầu
        capNhatBang(tuyen_bus.getDuLieuBang());
    }

    public void setMauBTN() {
        Color mauNutChu = new Color(30,41,58);
        JButton[] buttons = { btnThemTuyen, btnCapNhatTuyen, btnLamMoiTuyen};
        for (JButton btn : buttons) {
            btn.setForeground(mauNutChu);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        }
    }

    public void capNhatBang(List<Object[]> dsTuyen){
        tableModelTuyen.setRowCount(0);
        for(Object[] row : dsTuyen){

            Object[] rowData = { row[0], row[1], row[2], row[4] };
            tableModelTuyen.addRow(rowData);
        }
    }

    public void addListeners(ActionListener timKiemListener, ActionListener lamMoiListener, ActionListener themTuyenListener, ActionListener capNhatTuyenListener){
        btnLamMoiTuyen.addActionListener(lamMoiListener);
        btnThemTuyen.addActionListener(themTuyenListener);
        btnCapNhatTuyen.addActionListener(capNhatTuyenListener);
        txtGaDi.addActionListener(timKiemListener);
        txtGaDen.addActionListener(timKiemListener);
        txtTimKiem.addActionListener(timKiemListener);
    }

    public class Validator {

        public static boolean isValidMaTuyen(String str) {
            return str != null && str.matches("^[A-Z]{3,5}-[A-Z]{3,5}$");
        }

        public static boolean isValidTenGa(String str) {
            return str != null && str.matches("^[\\p{L}\\s]+$");
        }

        public static boolean isValidKhoangCach(String str) {
            return str != null && str.matches("^[0-9]+(\\.[0-9]{1,2})?$");
        }
    }

    public Tuyen_BUS getTuyen_bus() {
        return tuyen_bus;
    }

    public void setTxtGaDi(JTextField txtGaDi) {
        this.txtGaDi = txtGaDi;
    }

    public void setTxtGaDen(JTextField txtGaDen) {
        this.txtGaDen = txtGaDen;
    }

    public void setTxtTimKiem(JTextField txtTimKiem) {
        this.txtTimKiem = txtTimKiem;
    }

    public void setBtnThemTuyen(JButton btnThemTuyen) {
        this.btnThemTuyen = btnThemTuyen;
    }

    public void setBtnCapNhatTuyen(JButton btnCapNhatTuyen) {
        this.btnCapNhatTuyen = btnCapNhatTuyen;
    }

    public void setBtnLamMoiTuyen(JButton btnLamMoiTuyen) {
        this.btnLamMoiTuyen = btnLamMoiTuyen;
    }

    public void setTableTuyen(JTable tableTuyen) {
        this.tableTuyen = tableTuyen;
    }

    public DefaultTableModel getTableModelTuyen() {
        return tableModelTuyen;
    }

    public void setTableModelTuyen(DefaultTableModel tableModelTuyen) {
        this.tableModelTuyen = tableModelTuyen;
    }

    public JScrollPane getScrollPaneTable() {
        return scrollPaneTable;
    }

    public void setScrollPaneTable(JScrollPane scrollPaneTable) {
        this.scrollPaneTable = scrollPaneTable;
    }

    public JPanel getPnlChiTiet() {
        return pnlChiTiet;
    }

    public void setPnlChiTiet(JPanel pnlChiTiet) {
        this.pnlChiTiet = pnlChiTiet;
    }

    public void setTxtThongTinChung(JTextArea txtThongTinChung) {
        this.txtThongTinChung = txtThongTinChung;
    }

    public JTable getTableChiTietGa() {
        return tableChiTietGa;
    }

    public void setTableChiTietGa(JTable tableChiTietGa) {
        this.tableChiTietGa = tableChiTietGa;
    }

    public void setModelChiTietGa(DefaultTableModel modelChiTietGa) {
        this.modelChiTietGa = modelChiTietGa;
    }

    public void setPpGaDi(JPopupMenu ppGaDi) {
        this.ppGaDi = ppGaDi;
    }

    public void setPpGaDen(JPopupMenu ppGaDen) {
        this.ppGaDen = ppGaDen;
    }

    public void setPpTuyenID(JPopupMenu ppTuyenID) {
        this.ppTuyenID = ppTuyenID;
    }

    public void setListTuyenID(JList<String> listTuyenID) {
        this.listTuyenID = listTuyenID;
    }

    public void setListGaDi(JList<String> listGaDi) {
        this.listGaDi = listGaDi;
    }

    public void setListGaDen(JList<String> listGaDen) {
        this.listGaDen = listGaDen;
    }

    public JTextField getTxtChiTietMaTuyen() {
        return txtChiTietMaTuyen;
    }

    public void setTxtChiTietMaTuyen(JTextField txtChiTietMaTuyen) {
        this.txtChiTietMaTuyen = txtChiTietMaTuyen;
    }

    public JTextField getTxtChiTietTenTuyen() {
        return txtChiTietTenTuyen;
    }

    public void setTxtChiTietTenTuyen(JTextField txtChiTietTenTuyen) {
        this.txtChiTietTenTuyen = txtChiTietTenTuyen;
    }

    public JTextField getTxtChiTietKhoangCach() {
        return txtChiTietKhoangCach;
    }

    public void setTxtChiTietKhoangCach(JTextField txtChiTietKhoangCach) {
        this.txtChiTietKhoangCach = txtChiTietKhoangCach;
    }

    public JTextArea getTxtChiTietMoTa() {
        return txtChiTietMoTa;
    }

    public void setTxtChiTietMoTa(JTextArea txtChiTietMoTa) {
        this.txtChiTietMoTa = txtChiTietMoTa;
    }

    public JTable getTableTuyen() { return tableTuyen; }
    public JTextField getTxtGaDi() { return txtGaDi; }
    public JTextField getTxtGaDen() { return txtGaDen; }
    public JTextField getTxtTimKiem() { return txtTimKiem; }
    public JList<String> getListGaDi() { return listGaDi; }
    public JPopupMenu getPpGaDi() { return ppGaDi; }
    public JList<String> getListGaDen() { return listGaDen; }
    public JPopupMenu getPpGaDen() { return ppGaDen; }
    public JList<String> getListTuyenID() { return listTuyenID; }
    public JPopupMenu getPpTuyenID() { return ppTuyenID; }
    public JButton getBtnCapNhatTuyen() { return btnCapNhatTuyen; }
    public JButton getBtnThemTuyen() { return btnThemTuyen; }
    public NhanVien getNhanVienThucHien() { return nhanVienThucHien; }
    public JButton getBtnLamMoiTuyen() { return btnLamMoiTuyen; }

    public JTextArea getTxtThongTinChung() { return txtThongTinChung; }
    public DefaultTableModel getModelChiTietGa() { return modelChiTietGa; }
}