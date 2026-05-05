package gui.application.form.quanLyChuyen;
/*
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
import com.raven.datechooser.SelectedAction;
import controller.QuanLyChuyen_CTRL;
import dto.NhanVienDTO;
import entity.NhanVien;
import mapper.NhanVienMapper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyEvent;

public class PanelQuanLyChuyen extends JPanel {
    private final NhanVienDTO nhanVienThucHien;
    private final Font BASE_FONT = new Font(getFont().getFontName(), Font.PLAIN, 14);
    private final Color COLOR_ACCENT = new Color(36, 104, 155);
    private final Color COLOR_HEADER = new Color(36, 104, 155);
    private final Color COLOR_BG = new Color(245, 250, 255);
    private QuanLyChuyen_CTRL quanLyChuyenCtrl;
    private JTextField txtChiTietMaChuyen;
    private JTextField txtChiTietTenChuyen;
    private JTextField txtChiTietMaTuyen;
    private JTextField txtChiTietGaDi;
    private JTextField txtChiTietGaDen;
    private JTextField txtChiTietTau;
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

    public PanelQuanLyChuyen(NhanVienDTO nhanVien) {
        this.nhanVienThucHien = nhanVien;
        setLayout(new BorderLayout());
        setBackground(COLOR_BG);
        initComponents();

        new QuanLyChuyen_CTRL(this);
    }

    private void initComponents() {
        JPanel panelNorth = createNorthPanel();
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

    private JPanel createNorthPanel() {
        JPanel panelNorth = new JPanel(new MigLayout("wrap 1, fillx, insets 10 10 0 10", "[fill, grow]", "[]10[]"));
        panelNorth.setBackground(COLOR_BG);

        JPanel headerPanel = new JPanel(new MigLayout("insets 0, fill", "[grow]", "[50!]"));
        headerPanel.setBackground(COLOR_BG);
        txtNgayDi = new JTextField(10);
        txtNgayDi.setFont(BASE_FONT);
        dateChooser = new DateChooser();
        dateChooser.setTextRefernce(txtNgayDi);
        dateChooser.setDateFormat("dd/MM/yyyy");
        dateChooser.addEventDateChooser((action, date) -> {
            if (action.getAction() == SelectedAction.DAY_SELECTED) {
                dateChooser.hidePopup();
            }
        });

        JLabel lblGoiY = new JLabel("Chọn ngày để tìm kiếm chuyến!");
        lblGoiY.setFont(BASE_FONT.deriveFont(Font.ITALIC, 14));
        lblGoiY.setForeground(Color.GRAY);

        JPanel panelDate = new JPanel(new MigLayout("insets 0"));
        panelDate.setBackground(COLOR_BG);
        panelDate.add(new JLabel("Ngày:")).setFont(BASE_FONT);
        panelDate.add(txtNgayDi, "w 110!");
        panelDate.add(lblGoiY);

        headerPanel.add(panelDate, "pos 0 0.5al n n");

        JLabel title = new JLabel("QUẢN LÝ CHUYẾN TÀU", SwingConstants.CENTER);
        title.setFont(BASE_FONT.deriveFont(Font.BOLD, 24));
        title.setForeground(COLOR_HEADER);
        headerPanel.add(title, "cell 0 0, grow, center");

        panelNorth.add(headerPanel, "growx, h 50!");

        //Tìm kiếm chi tiết
        JPanel panelSearch = new JPanel(new MigLayout("fillx, insets 5 0 5 0",
                "[pref!]5[grow]10[pref!]5[grow]10[pref!]5[grow]10[pref!]5[grow]",
                "[]"));
        panelSearch.setBackground(COLOR_BG);

        txtMaChuyen = new JTextField();
        txtMaChuyen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập Mã Chuyến để tìm kiếm! (vd: SE1_20251001)");
        txtGaXuatPhat = new JTextField();
        txtGaXuatPhat.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Xuất Phát để tìm kiếm!");
        txtGaDich = new JTextField();
        txtGaDich.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Ga Đích để tìm kiếm!");
        txtTau = new JTextField();
        txtTau.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên Tàu để tìm kiếm!");

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

    private JSplitPane createCenterPanel() {
        JScrollPane tableScrollPane = createTablePanel();

        JPanel deatailPanel = createDetailPanel();
        tableScrollPane.setPreferredSize(new Dimension(600, getHeight()));
        deatailPanel.setPreferredSize(new Dimension(400, getHeight()));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, deatailPanel);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        splitPane.setContinuousLayout(true);
        return splitPane;
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"Mã Chuyến", "Tên Chuyến", "Tàu", "Loại Tàu",
                "Ngày Đi", "Giờ Đi", "Ngày Đến", "Giờ Đến"};
        tableModel = new DefaultTableModel(columnNames, 0) {
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
        tableChuyen.setGridColor(new Color(210, 210, 210));

        JTableHeader hd = tableChuyen.getTableHeader();
        hd.setFont(BASE_FONT.deriveFont(Font.BOLD, 14));
        hd.setBackground(COLOR_HEADER);
        hd.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableChuyen.getColumnModel().getColumnCount(); i++) {
            tableChuyen.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        tableChuyen.getColumnModel().getColumn(0).setPreferredWidth(130);

        tableChuyen.getColumnModel().getColumn(1).setPreferredWidth(170);

        tableChuyen.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableChuyen.getColumnModel().getColumn(3).setPreferredWidth(120);

        tableChuyen.getColumnModel().getColumn(4).setPreferredWidth(100);

        tableChuyen.getColumnModel().getColumn(5).setPreferredWidth(60);
        tableChuyen.getColumnModel().getColumn(6).setPreferredWidth(100);

        return new JScrollPane(tableChuyen);
    }

    private JPanel createDetailPanel() {
        JPanel detailPanel = new JPanel(new BorderLayout(0, 10));
        detailPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_HEADER),
                "THÔNG TIN CHI TIẾT CHUYẾN",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                BASE_FONT.deriveFont(Font.BOLD),
                COLOR_HEADER
        ));


        JPanel pnlThongTin = new JPanel(new MigLayout("wrap 2, fillx, insets 10", "[110!, shrink 0][]", "5[]5"));

        txtChiTietMaChuyen = createReadOnlyTextField();
        txtChiTietTenChuyen = createReadOnlyTextField();
        txtChiTietMaTuyen = createReadOnlyTextField();
        txtChiTietGaDi = createReadOnlyTextField();
        txtChiTietGaDen = createReadOnlyTextField();
        txtChiTietTau = createReadOnlyTextField();

        Font labelFont = BASE_FONT.deriveFont(Font.BOLD);

        pnlThongTin.add(new JLabel("Mã Chuyến:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietMaChuyen, "growx");

        pnlThongTin.add(new JLabel("Mã Tuyến:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietMaTuyen, "growx");

        pnlThongTin.add(new JLabel("Tên Chuyến:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietTenChuyen, "growx");

        pnlThongTin.add(new JLabel("Ga Xuất Phát:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietGaDi, "growx");

        pnlThongTin.add(new JLabel("Ga Đích:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietGaDen, "growx");

        pnlThongTin.add(new JLabel("Tàu:") {
            {
                setFont(BASE_FONT);
            }
        });
        pnlThongTin.add(txtChiTietTau, "growx");

        JPanel pnlLichTrinh = new JPanel(new BorderLayout());
        pnlLichTrinh.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_HEADER), "LỊCH TRÌNH", 0, 0, labelFont,
                COLOR_HEADER));

        String[] columns = {"STT", "Ga Đi", "Ngày Đi", "Giờ Đi", "Ga Đến", "Ngày Đến", "Giờ Đến"};
        modelLichTrinh = new DefaultTableModel(columns, 0) {
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
        tableLichTrinh.setGridColor(new Color(210, 210, 210));

        JTableHeader headerLT = tableLichTrinh.getTableHeader();
        headerLT.setFont(BASE_FONT.deriveFont(Font.BOLD, 14));
        headerLT.setBackground(COLOR_HEADER);
        headerLT.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tableLichTrinh.getColumnModel().getColumnCount(); i++) {
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

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
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
        for (JButton btn : buttons) {
            btn.setFont(labelFont);
            btn.setBackground(COLOR_ACCENT);
            btn.setForeground(Color.WHITE);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(5, 10, 5, 10));
        }
        btnLamMoi.setIcon(new FlatSVGIcon("icon/svg/refresh.svg", 0.35f));
        btnThemChuyen.setIcon(new FlatSVGIcon("icon/svg/add.svg", 0.35f));
        btnCapNhatChuen.setIcon(new FlatSVGIcon("icon/svg/edit.svg", 0.35f));

        panelActions.add(btnLamMoi);
        panelActions.add(btnThemChuyen);
        panelActions.add(btnCapNhatChuen);

        detailPanel.add(panelActions, BorderLayout.SOUTH);
        return detailPanel;
    }

    private JTextField createReadOnlyTextField() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        txt.setBackground(new Color(245, 245, 245)); // Light gray bg
        txt.setFont(BASE_FONT);
        return txt;
    }

    private void setupKeyBindings() {
        InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refreshAction");
        this.getActionMap().put("refreshAction", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (btnLamMoi != null && btnLamMoi.isEnabled()) {
                    btnLamMoi.doClick();
                }
            }
        });
    }

    public NhanVienDTO getNhanVienThucHien() {
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

    public JTextField getTxtChiTietMaChuyen() {
        return txtChiTietMaChuyen;
    }

    public void setTxtChiTietMaChuyen(JTextField txtChiTietMaChuyen) {
        this.txtChiTietMaChuyen = txtChiTietMaChuyen;
    }

    public JTextField getTxtChiTietTenChuyen() {
        return txtChiTietTenChuyen;
    }

    public void setTxtChiTietTenChuyen(JTextField txtChiTietTenChuyen) {
        this.txtChiTietTenChuyen = txtChiTietTenChuyen;
    }

    public JTextField getTxtChiTietMaTuyen() {
        return txtChiTietMaTuyen;
    }

    public void setTxtChiTietMaTuyen(JTextField txtChiTietMaTuyen) {
        this.txtChiTietMaTuyen = txtChiTietMaTuyen;
    }

    public JTextField getTxtChiTietGaDi() {
        return txtChiTietGaDi;
    }

    public void setTxtChiTietGaDi(JTextField txtChiTietGaDi) {
        this.txtChiTietGaDi = txtChiTietGaDi;
    }

    public JTextField getTxtChiTietGaDen() {
        return txtChiTietGaDen;
    }

    public void setTxtChiTietGaDen(JTextField txtChiTietGaDen) {
        this.txtChiTietGaDen = txtChiTietGaDen;
    }

    public JTextField getTxtChiTietTau() {
        return txtChiTietTau;
    }

    public void setTxtChiTietTau(JTextField txtChiTietTau) {
        this.txtChiTietTau = txtChiTietTau;
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

    public class Validator {

        public static boolean isValidMaTau(String str) {
            return str != null && str.matches("^[A-Z]{2,4}[0-9]{1,3}$");
        }

        public static boolean isValidMaChuyen(String str) {
            // Phần đầu là mã tàu, dấu gạch dưới, sau đó là 8 chữ số ngày tháng
            return str != null && str.matches("^[A-Z0-9]+_\\d{8}$");
        }

        public static boolean isValidGio(String str) {
            // 00-19 hoặc 20-23 : 00-59
            return str != null && str.matches("^([01]\\d|2[0-3]):[0-5]\\d$");
        }

        public static boolean isValidNgay(String str) {
            return str != null && str.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$");
        }
    }


}
