package gui.application.form.KhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import controller.KhuyenMai_CTRL;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.NhanVien;
import entity.Tuyen;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;



public class QuanLyKhuyenMai extends JPanel implements ActionListener, MouseListener, KeyListener {
    private final NhanVien nhanVien;
    private final KhuyenMai_CTRL khuyenMai_ctrl;
    private final Timer autoUpdateTimer;
    private JTextField txtMaKM, txtCodeKH, txtMoTa, txtTyLeGiamGia, txtTienGiamGia, txtSoLuong, txtGioiHan, txtNgayTrongTuan, txtMinGiaTriHoaDon;
    private JCheckBox txtTrangThai, txtNgayLe;
    private JDateChooser txtNgayBD, txtNgayKT;
    private JButton btnAdd, btnEdit, btnFind, btnClean;
    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<HangToa> txtHangToa;
    private JComboBox<LoaiTau> txtLoaiTau;
    private JComboBox<LoaiDoiTuong> txtLoaiDoiTuong;
    private JComboBox<Tuyen> txtTuyen;
    private List<JComponent> allFields;


    //màu cố định
    private final Color COLOR_PRIMARY = new Color(30, 100, 150);
    private final Color COLOR_ACCENT = new Color(74, 163, 208);
    private final Color COLOR_BG_MAIN = new Color(248, 250, 251);
    private final Color COLOR_BG_PANEL = new Color(226, 232, 240);
    private final Color COLOR_TEXT_TITLE = new Color(30, 41, 59);
    private final Color COLOR_TEXT_LABEL = new Color(51, 65, 85);


    //font cố định
    private final Font titleFont = new Font("Roboto", Font.BOLD, 20);
    private final Font labelFont = new Font("Roboto", Font.PLAIN, 14);
    private final Font inputFont = new Font("Roboto", Font.PLAIN, 14);
    private JTabbedPane tabPane;


    public QuanLyKhuyenMai(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        khuyenMai_ctrl = new KhuyenMai_CTRL();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        add(createTopSplitPanel(), BorderLayout.NORTH);
        add(createTabAndTablePanel(), BorderLayout.CENTER);

        //add sự kiện cho các nút
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);
        btnClean.addActionListener(this);
        table.addMouseListener(this);
        txtNgayBD.addMouseListener(this);
        txtNgayKT.addMouseListener(this);
        goiYMaKhuyenMai();


        autoUpdateTimer = new Timer(86400000 , e -> khuyenMai_ctrl.tuDongCapNhatTrangThai());
        autoUpdateTimer.start();
        autoUpdateTimer.setRepeats(true);

        allFields = new ArrayList<>();
        addAllFieldKey();
        initPlaceholders();
        capNhatTrangThaiTuDong();

    }

    // Đặt placeholder cho 1 JTextField
    private void applyPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }

    // Chỉ lấy giá trị thực tế từ JTextField, bỏ qua placeholder
    private String getRealText(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (text.equals(placeholder) && field.getForeground().equals(Color.GRAY)) {
            return "";
        }
        return text;
    }

    // Gắn placeholder cho các field
    private void initPlaceholders() {
        applyPlaceholder(txtCodeKH, "VD: LE_30_04, MUA_HE_2025, TUYEN_SG_HN");
        applyPlaceholder(txtMoTa, "VD: Giảm 10% giá vé dịp 30/4");
        applyPlaceholder(txtTyLeGiamGia, "VD: 0.1 (ở dạng số thập phân, 10% = 0.1)");
        applyPlaceholder(txtTienGiamGia, "VD: 20000");
        applyPlaceholder(txtSoLuong, "VD: 100 (số lượng mã)");
        applyPlaceholder(txtGioiHan, "VD: 1 (mỗi khách chỉ dùng 1 lần)");
        applyPlaceholder(txtNgayTrongTuan, "1=Thứ 2...7=Chủ nhật, 0=không áp dụng");
        applyPlaceholder(txtMinGiaTriHoaDon, "VD: 200000");
    }

    //panel input
    private JPanel createTopSplitPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ KHUYẾN MÃI");
        lblTitle.setFont(new Font("Roboto", Font.BOLD | Font.ITALIC, 26));
        lblTitle.setForeground(COLOR_TEXT_TITLE);
        lblTitle.setHorizontalAlignment(JLabel.CENTER);


        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(panelInput(), BorderLayout.CENTER);

        return panel;
    }


    //panel thông tin
    private JPanel createPanelThongTin() {
        JPanel panelThongTin = new JPanel(new GridBagLayout());
        panelThongTin.setBackground(Color.WHITE);
        panelThongTin.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY),
                "Thông tin khuyến mãi", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15),
                        COLOR_PRIMARY));
        panelThongTin.setBackground(COLOR_BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã khuyến mãi
        txtMaKM = new JTextField(20);
        txtMaKM.setEditable(false);
        formNhapThongTin(panelThongTin, gbc, 0, "Mã khuyến mãi:", txtMaKM, inputFont);

        // Code khuyến mãi
        txtCodeKH = new JTextField(20);
        formNhapThongTin(panelThongTin, gbc, 1, "Code khuyến mãi:", txtCodeKH, inputFont);

        // Mô tả
        txtMoTa = new JTextField(20);
        formNhapThongTin(panelThongTin, gbc, 2, "Mô tả:", txtMoTa, inputFont);

        // Tỷ lệ giảm giá
        txtTyLeGiamGia = new JTextField(20);
        formNhapThongTin(panelThongTin, gbc, 3, "Tỷ lệ giảm giá (%):", txtTyLeGiamGia, inputFont);

        // Tiền giảm giá
        txtTienGiamGia = new JTextField(20);
        formNhapThongTin(panelThongTin, gbc, 4, "Tiền giảm giá:", txtTienGiamGia,inputFont);

        // Ngày bắt đầu
        txtNgayBD = new JDateChooser();
        txtNgayBD.setDateFormatString("yyyy-MM-dd");
        formNhapThongTin(panelThongTin, gbc, 5, "Ngày bắt đầu:", txtNgayBD, inputFont);

        // Ngày kết thúc
        txtNgayKT = new JDateChooser();
        txtNgayKT.setDateFormatString("yyyy-MM-dd");
        formNhapThongTin(panelThongTin, gbc, 6, "Ngày kết thúc:", txtNgayKT, inputFont);

        return panelThongTin;

    }
    // panel điều kiện
    private JPanel createPanelDieuKien() {
        JPanel panelDieuKien = new JPanel(new GridBagLayout());
        panelDieuKien.setBackground(Color.WHITE);
        panelDieuKien.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY),
                "Điều kiện khuyến mãi", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15),
                COLOR_PRIMARY));
        panelDieuKien.setBackground(COLOR_BG_PANEL);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Số lượng
        txtSoLuong = new JTextField(20);
        formNhapThongTin(panelDieuKien, gbc, 0, "Số lượng:", txtSoLuong, inputFont);

        // Giới hạn mỗi khách hàng
        txtGioiHan = new JTextField(20);
        formNhapThongTin(panelDieuKien, gbc, 1, "Giới hạn mỗi khách hàng:", txtGioiHan, inputFont);

        // Trạng thái
        txtTrangThai = new JCheckBox("Đang hoạt động");
        formNhapThongTin(panelDieuKien, gbc, 2, "Trạng thái:", txtTrangThai, inputFont);

        // Tuyến ID
        txtTuyen = taoComboBoxTuyen();
        formNhapThongTin(panelDieuKien, gbc, 3, "Tuyến:", txtTuyen, inputFont);

        // Hạng toa
        txtHangToa = taoComboBoxCoTatCa(HangToa.class);
        formNhapThongTin(panelDieuKien, gbc, 4, "Hạng toa:", txtHangToa, inputFont);

        // Loại tàu
        txtLoaiTau = taoComboBoxCoTatCa(LoaiTau.class);
        formNhapThongTin(panelDieuKien, gbc, 5, "Loại tàu:", txtLoaiTau, inputFont);

        // Loại đối tượng
        txtLoaiDoiTuong = taoComboBoxCoTatCa(LoaiDoiTuong.class);
        formNhapThongTin(panelDieuKien, gbc, 6, "Loại đối tượng:", txtLoaiDoiTuong, inputFont);

        // Ngày trong tuần
        txtNgayTrongTuan = new JTextField(20);
        formNhapThongTin(panelDieuKien, gbc, 7, "Ngày trong tuần:", txtNgayTrongTuan, inputFont);

        // Ngày lễ
        txtNgayLe = new JCheckBox("Áp dụng ngày lễ");
        formNhapThongTin(panelDieuKien, gbc, 8, "Ngày lễ:", txtNgayLe, inputFont);

        // Min giá trị đơn hàng
        txtMinGiaTriHoaDon = new JTextField(20);
        formNhapThongTin(panelDieuKien, gbc, 9, "Min giá trị đơn hàng:", txtMinGiaTriHoaDon, inputFont);


        txtHangToa.setEditable(false);
        txtLoaiTau.setEditable(false);
        txtLoaiDoiTuong.setEditable(false);
        txtTuyen.setEditable(false);



        return panelDieuKien;
    }


    private JPanel panelInput() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);

        JPanel panelTop = new JPanel(new GridLayout(1, 2, 10, 10));
        panelTop.setBackground(Color.WHITE);

        JPanel panelThongTin = createPanelThongTin();
        JPanel panelDieuKien = createPanelDieuKien();

        panelTop.add(panelThongTin);
        panelTop.add(panelDieuKien);
        mainPanel.add(panelTop, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelButtons.setBackground(Color.WHITE);

        btnAdd = createButton("Thêm", "/gui/icon/png/save.png");
        btnEdit = createButton("Sửa", "/gui/icon/png/update.png");
        btnFind = createButton("Tìm kiếm", "/gui/icon/png/find.png");
        btnClean = createButton("Làm mới", "/gui/icon/png/clean.png");

        //tạo tooltip cho nút tìm kiếm
        btnFind.setToolTipText("Tìm kiếm: Code khuyến mãi, Mô tả, trong khoảng thời gian, trạng thái, mã tuyến");

        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnFind);
        panelButtons.add(btnClean);

        mainPanel.add(panelButtons, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void addAllFieldKey() {
        allFields.clear();


        allFields.add(txtCodeKH);
        allFields.add(txtMoTa);
        allFields.add(txtTyLeGiamGia);
        allFields.add(txtTienGiamGia);
        allFields.add((JComponent) txtNgayBD.getDateEditor().getUiComponent());
        allFields.add((JComponent) txtNgayKT.getDateEditor().getUiComponent());

        // Điều kiện KM
        allFields.add(txtSoLuong);
        allFields.add(txtGioiHan);
        allFields.add(txtTrangThai);
        allFields.add(txtTuyen);
        allFields.add(txtHangToa);
        allFields.add(txtLoaiTau);
        allFields.add(txtLoaiDoiTuong);


        allFields.add(txtNgayTrongTuan);
        allFields.add(txtNgayLe);
        allFields.add(txtMinGiaTriHoaDon);

        for (JComponent comp : allFields) {
            if (comp != null)
                comp.addKeyListener(this);
        }
    }



    // khuôn của form
    private void formNhapThongTin(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent field, Font font) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setFont(font);
        panel.add(field, gbc);
    }

    //tạo button với ícon tương ứng
    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 13));
        button.setBackground(new Color(173, 216, 230));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        try {
            button.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(iconPath))
                    .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }
        return button;
    }

    // tạo panel tab và bảng
    private JPanel createTabAndTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);

        // Tạo Tab Pane
        tabPane = new JTabbedPane();
        tabPane.setFont(new Font("Roboto", Font.BOLD, 14));

        String[] loaiKM = { "Tất cả","Mùa", "Lễ hội", "Đối tượng", "Tuyến", "Hạng vé", "Loại tàu", "Hạng toa", "Ngày trong tuần", "Min giá hóa đơn" };
        for (String loai : loaiKM) {
            tabPane.addTab(loai, new JLabel(""));
        }

        // Tạo bảng danh sách
        String[] columnNames = { "Mã khuyến mãi", "Code KH", "Mô tả", "Tỷ lệ giảm giá", "Tiền giảm giá",
                "Ngày bắt đầu", "Ngày kết thúc", "Số lượng", "Giới hạn/khách", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Roboto", Font.PLAIN, 13));
        table.setRowHeight(25);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 100, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Roboto", Font.BOLD, 14));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(173, 216, 230));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(30, 100, 150), 1),
                "Danh sách khuyến mãi", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15)));

        // Sự kiện đổi tab → load dữ liệu tương ứng
        tabPane.addChangeListener(e -> {
            int index = tabPane.getSelectedIndex();
            String loaiChon = tabPane.getTitleAt(index);
            loadKhuyenMaiTheoLoai(loaiChon);
        });

        tabPane.setSelectedIndex(0);
        loadKhuyenMaiTheoLoai("Tất cả");


        panel.add(tabPane, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    //cap nhap trang thai
    private void capNhatTrangThaiTuDong(){
        if(khuyenMai_ctrl.tuDongCapNhatTrangThai()){

        }else{
            System.out.print("Cập nhật trạng thái khuyến mãi thất bại!");
        }
    }

    //sự kiện
    //load danh sách khuyến mãi theo loại
    public void loadKhuyenMaiTheoLoai(String loai) {
        tableModel.setRowCount(0);

        List<KhuyenMai> dsKM = khuyenMai_ctrl.layKhuyenMaiTheoLoai(loai);
        for (KhuyenMai km : dsKM) {
            String prefix = km.getMaKhuyenMai().toUpperCase();

            boolean match = switch (loai) {
                case "Mùa" -> prefix.startsWith("MUA");
                case "Lễ hội" -> prefix.startsWith("LE");
                case "Đối tượng" -> prefix.startsWith("DOITUONG");
                case "Tuyến" -> prefix.startsWith("TUYEN");
                case "Hạng vé" -> prefix.startsWith("HANGVE");
                case "Loại tàu" -> prefix.startsWith("LOAITAU");
                case "Hạng toa" -> prefix.startsWith("HANGTOA");
                case "Ngày trong tuần" -> prefix.startsWith("NGAYTRONGTUAN");
                case "Min giá hóa đơn" -> prefix.startsWith("MINGIA");
                default -> true;
            };

            if (match) {
                Object[] rowData = {
                        km.getKhuyenMaiID(),
                        km.getMaKhuyenMai(),
                        km.getMoTa(),
                        km.getTyLeGiamGia(),
                        dinhDangTien(km.getTienGiamGia()),
                        km.getNgayBatDau(),
                        km.getNgayKetThuc(),
                        km.getSoLuong(),
                        km.getGioiHanMoiKhachHang(),
                        km.isTrangThai() ? "Đang hoạt động" : "Ngừng hoạt động"
                };
                tableModel.addRow(rowData);
            }
        }
    }

    //reset form
    public void resetForm(){
        txtMaKM.setText("");
        txtCodeKH.setText("");
        txtMoTa.setText("");
        txtTyLeGiamGia.setText("");
        txtTienGiamGia.setText("");
        txtNgayBD.setDate(null);
        txtNgayKT.setDate(null);
        txtSoLuong.setText("");
        txtGioiHan.setText("");
        txtTrangThai.setSelected(false);
        txtTuyen.setSelectedItem(null);
        txtLoaiTau.setSelectedItem(null);
        txtHangToa.setSelectedItem(null);
        txtLoaiDoiTuong.setSelectedItem(null);
        txtNgayTrongTuan.setText("");
        txtNgayLe.setSelected(false);
        txtMinGiaTriHoaDon.setText("");
        loadKhuyenMaiTheoLoai("Tất cả");

    }
    //Valid form
    public boolean validForm(boolean isEdit) {
        // 1. Lấy dữ liệu từ form
        String codeKh = getRealText(txtCodeKH, "VD: LE_30_04, MUA_HE_2025, TUYEN_SG_HN");
        String moTa = getRealText(txtMoTa, "VD: Giảm 10% giá vé dịp 30/4");
        String tyLeStr = getRealText(txtTyLeGiamGia, "VD: 0.1 (ở dạng số thập phân, 10% = 0.1)");
        String tienGiamGiaStr = getRealText(txtTienGiamGia, "VD: 20000");
        String soLuongStr = getRealText(txtSoLuong, "VD: 100 (số lượng mã)");
        String gioiHanStr = getRealText(txtGioiHan, "VD: 1 (mỗi khách chỉ dùng 1 lần)");
        String ngayTrongTuanStr = getRealText(txtNgayTrongTuan, "1=Thứ 2...7=Chủ nhật, 0=không áp dụng");
        String minGiaTriStr = getRealText(txtMinGiaTriHoaDon, "VD: 200000");

        Tuyen tuyen = (Tuyen) txtTuyen.getSelectedItem();
        HangToa hangToa = (HangToa) txtHangToa.getSelectedItem();
        LoaiTau loaiTau = (LoaiTau) txtLoaiTau.getSelectedItem();
        LoaiDoiTuong loaiDoiTuong = (LoaiDoiTuong) txtLoaiDoiTuong.getSelectedItem();

        double tyLeGiamGia = 0;
        double tienGiamGia = 0;
        double soLuong = 0;
        int gioiHan = 0;
        int ngayTrongTuan = 0;
        double minGiaTriDonHang = 0;


        //Validate code khuyến mãi
        if (codeKh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Code khuyến mãi không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtCodeKH.requestFocus();
            return false;
        }
        if (!khuyenMai_ctrl.kiemTraCodeKhuyenMai(codeKh)) {
            JOptionPane.showMessageDialog(this, "Code khuyến mãi không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtCodeKH.requestFocus();
            return false;
        }

        //Validate mô tả
        if (!khuyenMai_ctrl.kiemMoTa(moTa)) {
            JOptionPane.showMessageDialog(this, "Mô tả không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMoTa.requestFocus();
            return false;
        }


        if (!tyLeStr.isEmpty()) {
            try {
                tyLeGiamGia = Double.parseDouble(tyLeStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tỷ lệ giảm giá phải là số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtTyLeGiamGia.requestFocus();
                return false;
            }
        }
        if (!khuyenMai_ctrl.kiemTraTyLeGiamGia(tyLeGiamGia)) {
            JOptionPane.showMessageDialog(this, "Tỷ lệ giảm giá không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTyLeGiamGia.requestFocus();
            return false;
        }

        if (!tienGiamGiaStr.isEmpty()) {
            tienGiamGia = parseTien(tienGiamGiaStr);
        }
        if (!khuyenMai_ctrl.kiemTraTienGiamGia(tienGiamGia)) {
            JOptionPane.showMessageDialog(this, "Tiền giảm giá không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTienGiamGia.requestFocus();
            return false;
        }

        //Ít nhất có một giá trị tỷ lệ hoặc tiền giảm giá
        boolean coTyLe = tyLeGiamGia > 0;
        boolean coTien = tienGiamGia > 0;

        if (!coTyLe && !coTien) {
            JOptionPane.showMessageDialog(this,
                    "Khuyến mãi phải có TỶ LỆ GIẢM GIÁ hoặc TIỀN GIẢM GIÁ!\nBạn không thể để cả hai giá trị đều trống hoặc bằng 0.",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);

            txtTyLeGiamGia.requestFocus();
            txtTyLeGiamGia.selectAll();
            return false;
        }
        if (coTyLe && coTien) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ được chọn một hình thức giảm giá:\n- Tỷ lệ (%) HOẶC\n- Số tiền giảm giá (VNĐ).\nKhông được nhập cả hai!",
                    "Lỗi nhập liệu",
                    JOptionPane.ERROR_MESSAGE);

            txtTyLeGiamGia.requestFocus();
            txtTyLeGiamGia.selectAll();
            return false;
        }


        // Số lượng
        if (!soLuongStr.isEmpty()) {
            try {
                soLuong = Double.parseDouble(soLuongStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng phải là số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtSoLuong.requestFocus();
                return false;
            }
        }
        if (!khuyenMai_ctrl.kiemTraSoLuong(soLuong)) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoLuong.requestFocus();
            return false;
        }

        // Giới hạn / khách
        if (!gioiHanStr.isEmpty()) {
            try {
                gioiHan = Integer.parseInt(gioiHanStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Giới hạn mỗi khách hàng phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtGioiHan.requestFocus();
                return false;
            }
        }
        if (!khuyenMai_ctrl.kiemTraGioiHanMoiKhachHang(gioiHan)) {
            JOptionPane.showMessageDialog(this, "Giới hạn mỗi khách hàng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtGioiHan.requestFocus();
            return false;
        }

        // Ngày trong tuần
        if (!ngayTrongTuanStr.isEmpty()) {
            try {
                ngayTrongTuan = Integer.parseInt(ngayTrongTuanStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ngày trong tuần phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtNgayTrongTuan.requestFocus();
                return false;
            }
        }
        if (!khuyenMai_ctrl.kiemTraNgayTrongTuan(ngayTrongTuan)) {
            JOptionPane.showMessageDialog(this, "Ngày trong tuần không hợp lệ! Nhập 0 khi không áp dụng.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtNgayTrongTuan.requestFocus();
            return false;
        }

        // Min giá trị đơn hàng
        if (!minGiaTriStr.isEmpty()) {
            minGiaTriDonHang = parseTien(minGiaTriStr);
        }
        if (!khuyenMai_ctrl.kiemTraMinGiaTriDonHang(minGiaTriDonHang)) {
            JOptionPane.showMessageDialog(this, "Min giá trị đơn hàng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //Kiểm tra ngày bắt đầu / kết thúc
        if (txtNgayBD.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtNgayKT.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDate ngayBatDau = new java.sql.Date(txtNgayBD.getDate().getTime()).toLocalDate();
        LocalDate ngayKetThuc = new java.sql.Date(txtNgayKT.getDate().getTime()).toLocalDate();

        if (!isEdit) {
            if (!khuyenMai_ctrl.kiemTraNgayBatDau(ngayBatDau)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (!khuyenMai_ctrl.kiemTraNgayKetThuc(ngayBatDau, ngayKetThuc)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //Ràng buộc theo loại code khuyến mãi (prefix)
        String prefix = codeKh.toUpperCase();

        if (prefix.startsWith("TUYEN_") && tuyen == null) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo tuyến bắt buộc phải chọn tuyến!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (prefix.startsWith("LOAITAU_") && loaiTau == null) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo loại tàu bắt buộc phải chọn loại tàu!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (prefix.startsWith("HANGTOA_") && hangToa == null) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo hạng toa bắt buộc phải chọn hạng toa!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (prefix.startsWith("DOITUONG_") && loaiDoiTuong == null) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo đối tượng bắt buộc phải chọn loại đối tượng!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (prefix.startsWith("NGAYTRONGTUAN_") && ngayTrongTuan <= 0) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo ngày trong tuần bắt buộc phải nhập ngày trong tuần > 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (prefix.startsWith("MINGIA_") && minGiaTriDonHang <= 0) {
            JOptionPane.showMessageDialog(this, "Khuyến mãi theo min giá trị đơn hàng bắt buộc phải nhập min giá trị > 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    //gợi ý khi nhập mã khuyến mãi
    public void goiYMaKhuyenMai() {
        // Danh sách gợi ý
        String[] dsGoiY = {
                "LE_", "MUA_", "DOITUONG_", "TUYEN_", "HANGVE_",
                "LOAITAU_", "HANGTOA_", "NGAYTRONGTUAN_", "MINGIA_"
        };

        // Popup chứa menu gợi ý
        JPopupMenu popupKhuyenMai = new JPopupMenu();
        popupKhuyenMai.setFocusable(false);

        // Khi người dùng gõ
        txtCodeKH.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filterGoiY(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filterGoiY(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filterGoiY(); }

            private void filterGoiY() {
                String text = txtCodeKH.getText().trim().toUpperCase();
                popupKhuyenMai.removeAll();

                if (text.isEmpty()) {
                    popupKhuyenMai.setVisible(false);
                    return;
                }

                boolean coGoiY = false;
                for (String goiY : dsGoiY) {
                    if (goiY.startsWith(text)) {
                        JMenuItem item = new JMenuItem(goiY);
                        item.setFont(new Font("Roboto", Font.PLAIN, 14));

                        item.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mousePressed(java.awt.event.MouseEvent e) {
                                SwingUtilities.invokeLater(() -> {
                                    txtCodeKH.setText(goiY);
                                    popupKhuyenMai.setVisible(false);
                                });
                            }
                        });

                        popupKhuyenMai.add(item);
                        coGoiY = true;
                    }
                }

                if (coGoiY) {
                    popupKhuyenMai.show(txtCodeKH, 0, txtCodeKH.getHeight());
                } else {
                    popupKhuyenMai.setVisible(false);
                }

                popupKhuyenMai.revalidate();
                popupKhuyenMai.repaint();
            }
        });

        // Ẩn popup khi mất focus
        txtCodeKH.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                popupKhuyenMai.setVisible(false);
            }
        });

        txtCodeKH.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && popupKhuyenMai.isVisible()) {
                    if (popupKhuyenMai.getComponentCount() > 0) {
                        JMenuItem item = (JMenuItem) popupKhuyenMai.getComponent(0);
                        txtCodeKH.setText(item.getText());
                        popupKhuyenMai.setVisible(false);
                    }
                }
            }
        });
    }


    //xác định loại khuyến mãi
    public String xacDinhLoaiKhuyenMai(String maKhuyenMai) {
        if (maKhuyenMai.startsWith("MUA")) {
            return "Mùa";
        } else if (maKhuyenMai.startsWith("LE")) {
            return "Lễ hội";
        } else if (maKhuyenMai.startsWith("DOITUONG")) {
            return "Đối tượng";
        } else if (maKhuyenMai.startsWith("TUYEN")) {
            return "Tuyến";
        } else if (maKhuyenMai.startsWith("HANGVE")) {
            return "Hạng vé";
        } else if (maKhuyenMai.startsWith("LOAITAU")) {
            return "Loại tàu";
        } else if (maKhuyenMai.startsWith("HANGTOA")) {
            return "Hạng toa";
        } else if (maKhuyenMai.startsWith("NGAYTRONGTUAN")) {
            return "Ngày trong tuần";
        } else if (maKhuyenMai.startsWith("MINGIA")) {
            return "Min giá hóa đơn";
        } else {
            return "Không xác định";
        }
    }
    //thêm khuyến mãi
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        boolean flag = khuyenMai_ctrl.themKhuyenMai(km, dkkm);
        if (flag) {
            String loai = xacDinhLoaiKhuyenMai(km.getMaKhuyenMai());
            loadKhuyenMaiTheoLoai(loai);
            return true;
        } else {
            return false;
        }
    }

    //sửa khuyến mãi
    private boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm){
        boolean flag = khuyenMai_ctrl.suaKhuyenMai(km, dkkm);
        if (flag) {
            String loai = xacDinhLoaiKhuyenMai(km.getMaKhuyenMai());
            loadKhuyenMaiTheoLoai(loai);
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnAdd) || o.equals(btnEdit)) {
            boolean isEdit = o.equals(btnEdit);
            if (!validForm(isEdit)) {
                return;
            }
        }

        if(o.equals(btnClean)){
            resetForm();
            return;
        }
        if (o.equals(btnFind)) {
            String tuKhoa = !txtMaKM.getText().trim().isEmpty() ? txtMaKM.getText().trim() : txtMoTa.getText().trim();
            Tuyen tuyen = (Tuyen) txtTuyen.getSelectedItem();
            String maTuyen = tuyen != null && !"Tất cả".equals(tuyen.toString()) ? tuyen.getTuyenID() : null;
            Boolean tthai = txtTrangThai.isSelected();

            LocalDate nbd = null;
            LocalDate nkt = null;
            if (txtNgayBD.getDate() != null)
                nbd = new java.sql.Date(txtNgayBD.getDate().getTime()).toLocalDate();
            if (txtNgayKT.getDate() != null)
                nkt = new java.sql.Date(txtNgayKT.getDate().getTime()).toLocalDate();

            List<KhuyenMai> dsTimDuoc = khuyenMai_ctrl.timKhuyenMai(tuKhoa, maTuyen, tthai, nbd, nkt);
            tableModel.setRowCount(0);
            if (dsTimDuoc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi nào!", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (KhuyenMai khuyenMai : dsTimDuoc) {
                    Object[] rowData = {
                            khuyenMai.getKhuyenMaiID(),
                            khuyenMai.getMaKhuyenMai(),
                            khuyenMai.getMoTa(),
                            khuyenMai.getTyLeGiamGia(),
                            dinhDangTien(khuyenMai.getTienGiamGia()),
                            khuyenMai.getNgayBatDau(),
                            khuyenMai.getNgayKetThuc(),
                            khuyenMai.getSoLuong(),
                            khuyenMai.getGioiHanMoiKhachHang(),
                            khuyenMai.isTrangThai() ? "Đang hoạt động" : "Ngừng hoạt động"
                    };
                    tableModel.addRow(rowData);
                }
            }
            return;
        }

        //==============Lấy thông tin chung từ form===================

        // Thông tin khuyến mãi
        String codeKH = getRealText(txtCodeKH, "VD: LE_30_04, MUA_HE_2025, TUYEN_SG_HN");
        String moTa = getRealText(txtMoTa, "VD: Giảm 10% giá vé dịp 30/4");
        double tyLeGiamGia = txtTyLeGiamGia.getText().isEmpty() ? 0 : Double.parseDouble(txtTyLeGiamGia.getText().trim());
        double tienGiamGia = txtTienGiamGia.getText().isEmpty() ? 0 : parseTien(txtTienGiamGia.getText());
        double soLuong = txtSoLuong.getText().isEmpty() ? 0 : Double.parseDouble(txtSoLuong.getText().trim());
        int gioiHan = txtGioiHan.getText().isEmpty() ? 0 : Integer.parseInt(txtGioiHan.getText().trim());
        boolean trangThai = txtTrangThai.isSelected();

        LocalDate ngayBatDau = new Date(txtNgayBD.getDate().getTime()).toLocalDate();
        LocalDate ngayKetThuc = new Date(txtNgayKT.getDate().getTime()).toLocalDate();


        // Điều kiện
        Tuyen tuyen = (Tuyen) txtTuyen.getSelectedItem();
        HangToa hangToa = (HangToa) txtHangToa.getSelectedItem();
        LoaiTau loaiTau = (LoaiTau) txtLoaiTau.getSelectedItem();
        LoaiDoiTuong loaiDoiTuong = (LoaiDoiTuong) txtLoaiDoiTuong.getSelectedItem();
        boolean ngayLe = txtNgayLe.isSelected();
        double minGiaTriDonHang = txtMinGiaTriHoaDon.getText().isEmpty() ? 0 : parseTien(txtMinGiaTriHoaDon.getText());
        Integer ngayTrongTuan = txtNgayTrongTuan.getText().trim().isEmpty() ? null : Integer.parseInt(txtNgayTrongTuan.getText().trim());


        if (o.equals(btnAdd)) {
            String maKM = khuyenMai_ctrl.taoMaKhuyenMaiTuDong();

            KhuyenMai km = new KhuyenMai(maKM, codeKH, moTa, tyLeGiamGia, tienGiamGia,
                    ngayBatDau, ngayKetThuc, soLuong, gioiHan, trangThai);

            String maDK = khuyenMai_ctrl.taoMaDieuKienKhuyenMaiTuDong();
            DieuKienKhuyenMai dkkm = new DieuKienKhuyenMai(maDK, km, tuyen, loaiTau, hangToa, loaiDoiTuong,
                    ngayTrongTuan != null ? ngayTrongTuan : 0, ngayLe, minGiaTriDonHang);
            dkkm.setKhuyenMai(km);

            if (themKhuyenMai(km, dkkm)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
                return;
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (o.equals(btnEdit)) {
            String maKhuyenMaiHienTai = txtMaKM.getText().trim();

            KhuyenMai km = new KhuyenMai(maKhuyenMaiHienTai, codeKH, moTa, tyLeGiamGia, tienGiamGia,
                    ngayBatDau, ngayKetThuc, soLuong, gioiHan, trangThai);

            String maDKHienTai = khuyenMai_ctrl.layDieuKienKhuyenMaiTheoMaKhuyenMai(maKhuyenMaiHienTai);
            DieuKienKhuyenMai dkkm = new DieuKienKhuyenMai(maDKHienTai, km, tuyen, loaiTau, hangToa, loaiDoiTuong,
                    ngayTrongTuan != null ? ngayTrongTuan : 0, ngayLe, minGiaTriDonHang);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn cập nhật khuyến mãi này?",
                    "Xác nhận cập nhật",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            if (suaKhuyenMai(km, dkkm)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                String loai = xacDinhLoaiKhuyenMai(km.getMaKhuyenMai());
                loadKhuyenMaiTheoLoai(loai);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            System.out.print(minGiaTriDonHang);
            return;
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String maKhuyenMai = table.getValueAt(selectedRow, 0).toString();
            KhuyenMai km = null;

            JTabbedPane tabPane = (JTabbedPane) ((JPanel) table.getParent().getParent().getParent()).getComponent(0);
            String loaiChon = tabPane.getTitleAt(tabPane.getSelectedIndex());
            List<KhuyenMai> dskm = khuyenMai_ctrl.layKhuyenMaiTheoLoai(loaiChon);
            for (KhuyenMai k : dskm) {
                if (k.getKhuyenMaiID().equals(maKhuyenMai)) {
                    km = k;
                    break;
                }
            }

            if (km != null) {
                txtMaKM.setText(km.getKhuyenMaiID());
                txtCodeKH.setText(km.getMaKhuyenMai());
                txtMoTa.setText(km.getMoTa());
                txtTyLeGiamGia.setText(String.valueOf(km.getTyLeGiamGia()));
                txtTienGiamGia.setText(dinhDangTien(km.getTienGiamGia()));
                txtNgayBD.setDate(Date.valueOf(km.getNgayBatDau()));
                txtNgayKT.setDate(Date.valueOf(km.getNgayKetThuc()));
                txtSoLuong.setText(String.valueOf((int)km.getSoLuong()));
                txtGioiHan.setText(String.valueOf(km.getGioiHanMoiKhachHang()));
                txtTrangThai.setSelected(km.isTrangThai());

                DieuKienKhuyenMai dkkm = khuyenMai_ctrl.layDieuKienKhuyenMaiTheoMaKhuyenMaiObj(km.getKhuyenMaiID());
                if (dkkm != null) {
                    txtTuyen.setSelectedItem(dkkm.getTuyen());
                    txtLoaiTau.setSelectedItem(dkkm.getLoaiTau());
                    txtHangToa.setSelectedItem(dkkm.getHangToa());
                    txtLoaiDoiTuong.setSelectedItem(dkkm.getLoaiDoiTuong());
                    txtNgayTrongTuan.setText(dkkm.getNgayTrongTuan() > 0 ? String.valueOf(dkkm.getNgayTrongTuan()) : "");
                    txtNgayLe.setSelected(dkkm.isNgayLe());
                    txtMinGiaTriHoaDon.setText(dinhDangTien(dkkm.getMinGiaTriDonHang()));
                } else {
                    txtTuyen.setSelectedItem(null);
                    txtLoaiTau.setSelectedItem(null);
                    txtHangToa.setSelectedItem(null);
                    txtLoaiDoiTuong.setSelectedItem(null);
                    txtNgayTrongTuan.setText("");
                    txtNgayLe.setSelected(false);
                    txtMinGiaTriHoaDon.setText("");
                }
            }
        }
    }
            //tạo jcombox có tất cả
// Hàm tạo JComboBox có mục "Tất cả" — tự động fallback về "Tất cả" khi null
    private <T extends Enum<T>> JComboBox<T> taoComboBoxCoTatCa(Class<T> enumClass) {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.addItem(null); // null = “Tất cả”

        for (T value : enumClass.getEnumConstants()) {
            comboBox.addItem(value);
        }

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Tất cả");
                }
                return this;
            }
        });

        //Nếu người dùng chọn một giá trị không hợp lệ, tự động quay lại “Tất cả”
        comboBox.addActionListener(e -> {
            T selected = (T) comboBox.getSelectedItem();
            if (selected != null && !java.util.Arrays.asList(enumClass.getEnumConstants()).contains(selected)) {
                comboBox.setSelectedItem(null);
            }
        });

        return comboBox;
    }

    //định dạng tiền
    private String dinhDangTien(double soTien) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(soTien) + " VNĐ";
    }


    //parse tiền
    private double parseTien(String text) {
        if (text == null || text.trim().isEmpty()) return 0;

        try {
            String cleaned = text.replaceAll("[^0-9]", "");

            if (cleaned.isEmpty()) return 0;

            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá trị tiền không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }




    //tạo jcombox tuyen
    private JComboBox<Tuyen> taoComboBoxTuyen() {
        JComboBox<Tuyen> comboBox = new JComboBox<>();
        comboBox.addItem(null);

        // Lấy danh sách tuyến từ controller
        List<Tuyen> dsTuyen = khuyenMai_ctrl.layDanhSachTuyen();
        for (Tuyen t : dsTuyen) {
            comboBox.addItem(t);
        }

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Tất cả");
                } else {
                    Tuyen t = (Tuyen) value;
                    setText(t.getTuyenID() + " - " + (t.getMoTa() != null ? t.getMoTa() : ""));
                }
                return this;
            }
        });
        return comboBox;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {

            Component current = (Component) e.getSource();
            if (current instanceof JCheckBox checkbox) {
                checkbox.doClick();
            }

            int index = allFields.indexOf(current);
            if (index != -1) {
                if (index < allFields.size() - 1) {
                    allFields.get(index + 1).requestFocus();
                } else {
                    btnAdd.requestFocus();
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }
    @Override
    public void mouseReleased(MouseEvent e) {

    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
