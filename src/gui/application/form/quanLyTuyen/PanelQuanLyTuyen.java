package gui.application.form.quanLyTuyen;/*
 * @ (#) PanelQuanLyTuyen.java   1.0     29/09/2025


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 29/09/2025
 */
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

public class   PanelQuanLyTuyen extends JPanel {
    private final Tuyen_BUS tuyen_bus;

    private final NhanVien nhanVienThucHien;

    private JTextField txtGaDi;
    private JTextField txtGaDen;
    private JTextField txtTimKiem;

//    private JButton btnTimKiem;
    private JButton btnThemTuyen;
    private JButton btnCapNhatTuyen;
    private JButton btnLamMoiTuyen;

    private JTable tableTuyen;
    private DefaultTableModel tableModelTuyen;
    private JScrollPane scrollPane;

    private JPopupMenu ppGaDi;
    private JPopupMenu ppGaDen;
    private JPopupMenu ppTuyenID;
    private JList<String> listTuyenID;
    private JList<String> listGaDi;
    private JList<String> listGaDen;

    public PanelQuanLyTuyen(NhanVien nhanVien){
        setLayout(new BorderLayout());

        this.tuyen_bus = new Tuyen_BUS();
        this.nhanVienThucHien = nhanVien;

        initComponents();
        new QuanLyTuyen_CTRL(this, tuyen_bus);

    }

    public void initComponents(){
        JPanel panelNorth = new JPanel(new BorderLayout());

        // --- 1. HEADER PANEL ---
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new MigLayout("wrap 1, fillx, insets 10 10 5 10"));

        JLabel title = new JLabel("QUẢN LÝ VÀ TRA CỨU TUYẾN ĐƯỜNG SẮT", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        title.setForeground(new Color(30,41,58));
        panelHeader.add(title, "growx");

        //Tìm kiếm
        JPanel panelSearch = new JPanel(new MigLayout("insets 5 10 10 10, gap 10"));
        txtGaDen = new JTextField(15);
        txtGaDen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên ga để tìm kiếm tuyến");
        txtGaDi = new JTextField(15);
        txtGaDi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên ga để tìm kiếm tuyến");
        btnLamMoiTuyen = new JButton("(F5) Làm mới tuyến");
        txtTimKiem = new JTextField(10);
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã tuyến để tìm kiếm tuyến");
//        btnTimKiem = new JButton("Tìm kiếm");
        btnThemTuyen = new JButton("Thêm tuyến");
        btnCapNhatTuyen = new JButton("Cập nhật tuyến");
        setMauBTN();

//        btnTimKiem.setIcon(new FlatSVGIcon("gui/icon/svg/search.svg", 0.35f));
        btnLamMoiTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/refresh.svg", 0.35f));
        btnLamMoiTuyen.setBackground(new Color(36, 104, 155));
        btnLamMoiTuyen.setForeground(Color.white);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .put(KeyStroke.getKeyStroke("F5"), "lamMoiTuyenAction");

        this.getActionMap().put("lamMoiTuyenAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLamMoiTuyen.doClick();
            }
        });
        btnThemTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f));
        btnThemTuyen.setBackground(new Color(36, 104, 155));
        btnThemTuyen.setForeground(Color.white);
        btnCapNhatTuyen.setIcon(new FlatSVGIcon("gui/icon/svg/edit.svg", 0.35f));
        btnCapNhatTuyen.setBackground(new Color(36, 104, 155));
        btnCapNhatTuyen.setForeground(Color.white);


       panelSearch = new JPanel(new MigLayout(
                "insets 5 10 10 10, gap 10",
                "[grow, push][grow, push][][][]",
                "[]"
        ));

        JPanel col1Panel = new JPanel(new MigLayout(
                "insets 0, wrap 2, fillx",
                "[][grow, push]",
                "[][]"
        ));
        col1Panel.add(new JLabel("Ga Xuất Phát:"));
        col1Panel.add(txtGaDi, "growx");
        col1Panel.add(new JLabel("Ga Đích:"));
        col1Panel.add(txtGaDen, "growx");

        panelSearch.add(col1Panel, "grow, pushy");

        JPanel col2Panel = new JPanel(new MigLayout(
                "insets 0, wrap 2, fillx",
                "[][grow, push]",
                "[]"
        ));
        col2Panel.add(new JLabel("Mã Tuyến:"));
        col2Panel.add(txtTimKiem, "growx");

        panelSearch.add(col2Panel, "growx, pushy, top");

        panelSearch.add(btnThemTuyen, "top");
        panelSearch.add(btnCapNhatTuyen, "top");
        panelSearch.add(btnLamMoiTuyen, "top");

        panelHeader.add(panelSearch, "growx");
        panelNorth.add(panelHeader, BorderLayout.NORTH);

        add(panelNorth, BorderLayout.NORTH);

        // --- 2. TABLE DỮ LIỆU ---
        String[] columnNames = {"Mã Tuyến", "Ga Xuất Phát", "Ga Đích","Danh Sách Ga Trung Gian", "Khoảng Cách (km)"};
        tableModelTuyen = new DefaultTableModel(columnNames,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Vô hiệu hóa chỉnh sửa trực tiếp trong bảng
            }
        };
        tableTuyen = new JTable(tableModelTuyen);
        tableTuyen.setRowHeight(28);

        JTableHeader hd = tableTuyen.getTableHeader();
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) hd.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        hd.setFont(new Font("Times New Roman", Font.BOLD, 16));
        hd.setBackground(new Color(30,41,58));
        hd.setForeground(Color.white);

        tableTuyen.setShowGrid(true);
        tableTuyen.setShowHorizontalLines(true);
        tableTuyen.setShowVerticalLines(true);

        TableColumnModel columnModel = tableTuyen.getColumnModel();
        StripedRowRenderer stripedRenderer = new StripedRowRenderer();
        for(int i = 0; i < columnModel.getColumnCount(); i++){
                columnModel.getColumn(i).setCellRenderer(stripedRenderer);
        }

        scrollPane = new JScrollPane(tableTuyen);
        add(scrollPane, BorderLayout.CENTER);

        // POPUP GỢI Ý //
        ppGaDi = new JPopupMenu();
        listGaDi = new JList<>();
        ppGaDi.add(new JScrollPane(listGaDi), BorderLayout.CENTER);

        ppGaDen = new JPopupMenu();
        listGaDen = new JList<>();
        ppGaDen.add(new JScrollPane(listGaDen), BorderLayout.CENTER);

        ppTuyenID = new JPopupMenu();
        listTuyenID = new JList<>();
        ppTuyenID.setLayout(new BorderLayout());
        ppTuyenID.add(new JScrollPane(listTuyenID), BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        capNhatBang(tuyen_bus.getDuLieuBang());

    }

    private class StripedRowRenderer extends DefaultTableCellRenderer {
        private final Color evenColor = new Color(240, 248, 255);
        private final Color oddColor = Color.WHITE;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(evenColor);
                } else {
                    c.setBackground(oddColor);
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }

    public void capNhatBang(List<Object[]> dsTuyen){
         tableModelTuyen.setRowCount(0);

         for(Object[] row : dsTuyen){
             tableModelTuyen.addRow(row);
         }
    }

    public void addListeners( ActionListener timKiemListener,ActionListener lamMoiListener, ActionListener themTuyenListener){
        btnLamMoiTuyen.addActionListener(lamMoiListener);
        btnThemTuyen.addActionListener(themTuyenListener);
    }

    public void setMauBTN() {
        Color mauNutChu = new Color(30,41,58);

        JButton[] buttons = { btnThemTuyen, btnCapNhatTuyen, btnLamMoiTuyen};

        for (JButton btn : buttons) {
            btn.setForeground(mauNutChu);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        }
    }

    public JTable getTableTuyen(){
        return tableTuyen;
    }

    public JTextField getTxtGaDi() {
        return txtGaDi;
    }
    public JTextField getTxtGaDen() {
        return txtGaDen;
    }
    public JTextField getTxtTimKiem() {
        return txtTimKiem;
    }
    public JList<String> getListGaDi() {
        return listGaDi;
    }
    public JPopupMenu getPpGaDi() {
        return ppGaDi;
    }
    public JList<String> getListGaDen() {
        return listGaDen;
    }
    public JPopupMenu getPpGaDen() {
        return ppGaDen;
    }
    public JList<String> getListTuyenID() {
        return listTuyenID;
    }
    public JPopupMenu getPpTuyenID() {
        return ppTuyenID;
    }

    public JButton getBtnCapNhatTuyen() {
        return btnCapNhatTuyen;
    }

    public JButton getBtnThemTuyen() {
        return btnThemTuyen;
    }

    public NhanVien getNhanVienThucHien() {
        return nhanVienThucHien;
    }

    public JButton getBtnLamMoiTuyen() {
        return btnLamMoiTuyen;
    }

}
