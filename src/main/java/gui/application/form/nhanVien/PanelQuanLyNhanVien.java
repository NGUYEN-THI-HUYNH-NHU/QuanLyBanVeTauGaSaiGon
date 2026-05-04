package gui.application.form.nhanVien;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import controller.NhanVien_CTRL;
import dto.NhanVienDTO;
import entity.CaLam;
import entity.NhanVien;
import entity.VaiTroNhanVien;
import entity.type.VaiTroNhanVienEnums;
import mapper.NhanVienMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelQuanLyNhanVien extends JPanel implements ActionListener, MouseListener, KeyListener {
    private final NhanVienDTO nhanVienHienTai;
    private final NhanVien_CTRL nhanVien_ctrl;

    // Màu sắc giao diện
    private final Color COLOR_PRIMARY = new Color(30, 100, 150);
    private final Color COLOR_BG_MAIN = new Color(248, 250, 251);
    private final Color COLOR_BG_PANEL = new Color(226, 232, 240);
    private final Color COLOR_TEXT_TITLE = new Color(30, 41, 59);
    private final Color COLOR_TEXT_LABEL = new Color(51, 65, 85);

    private JTextField txtMaNV, txtTenNV, txtEmail, txtSDT, txtDiaChi;
    private JComboBox<VaiTroNhanVienEnums> cbVaiTro;
    private JRadioButton rbtnNam, rbtnNu;
    private JCheckBox chkDangHoatDong;
    private List<JComponent> allField;
    private JDateChooser txtNgaySinh, txtNgayThamGia;
    private JLabel lblMaNVDetail, lblVaiTroDetail, lblTenNVDetail, lblGioiTinhDetail, lblNgaySinhDetail, lblSDTDetail,
            lblEmailDetail, lblDiaChiDetail, lblNgayThamGiaDetail, lblTrangThaiDetail, lblCaLamDetail, lblAvatar;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnAdd, btnEdit, btnFind, btnClean;
    private boolean isEditing = false;
    private JComboBox<CaLam> cbCaLam;
    private Font font = new Font("Roboto", Font.PLAIN, 12);

    public PanelQuanLyNhanVien(NhanVienDTO nhanVienHienTai) {
        this.nhanVienHienTai = nhanVienHienTai;
        this.nhanVien_ctrl = new NhanVien_CTRL(NhanVienMapper.INSTANCE.toEntity(nhanVienHienTai));

        btnAdd = createButton("Thêm", "icon/svg/add-kh.svg");
        btnEdit = createButton("Sửa", "icon/svg/edit-kh.svg");
        btnFind = createButton("Tìm kiếm", "icon/svg/search-kh.svg");
        btnClean = createButton("Xóa trắng", "icon/svg/refresh-kh.svg");

        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_TEXT_TITLE);
        add(lblTitle, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelInput(), panelChiTiet());
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        add(splitPane, BorderLayout.CENTER);

        add(panelTable(), BorderLayout.SOUTH);

        table.addMouseListener(this);
        btnAdd.addActionListener(this);
        btnClean.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);

        loadDataToTable();
        initPlaceholders();
        loadDataToCaLamCombo();
    }

    private JPanel panelInput() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin nhân viên",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 12), COLOR_PRIMARY));
        p.setBackground(COLOR_BG_PANEL);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        txtMaNV = new JTextField(20);
        txtMaNV.setEnabled(false);
        txtTenNV = new JTextField(20);
        cbVaiTro = new JComboBox<>(VaiTroNhanVienEnums.values());
        txtNgaySinh = new JDateChooser();
        txtNgaySinh.setDateFormatString("dd/MM/yyyy");
        txtNgaySinh.setCalendar(java.util.Calendar.getInstance());
        txtSDT = new JTextField();
        txtEmail = new JTextField();
        txtDiaChi = new JTextField();
        txtNgayThamGia = new JDateChooser();
        txtNgayThamGia.setDateFormatString("dd/MM/yyyy");
        txtNgayThamGia.setCalendar(java.util.Calendar.getInstance());

        cbCaLam = new JComboBox<>();
        rbtnNu = new JRadioButton("Nữ");
        rbtnNam = new JRadioButton("Nam");
        ButtonGroup group = new ButtonGroup();
        group.add(rbtnNam);
        group.add(rbtnNu);
        chkDangHoatDong = new JCheckBox("Đang hoạt động", true);

        JComponent ngaySinhEditor = txtNgaySinh.getDateEditor().getUiComponent();
        JComponent ngayTGEditor = txtNgayThamGia.getDateEditor().getUiComponent();

        cbCaLam.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof CaLam ca) {
                    setText(ca.getCaLamID() + " (" + ca.getGioVaoCa() + " - " + ca.getGioKetCa() + ")");
                }
                return this;
            }
        });

        allField = List.of(txtTenNV, cbVaiTro, ngaySinhEditor, txtSDT, txtEmail, txtDiaChi, ngayTGEditor, cbCaLam,
                rbtnNam, rbtnNu, chkDangHoatDong);

        for (JComponent comp : allField) {
            comp.addKeyListener(this);
        }

        setupComboKeyboard(cbVaiTro);
        setupComboKeyboard(cbCaLam);

        addField(form, gbc, "Mã nhân viên:", txtMaNV, font);
        addField(form, gbc, "Tên nhân viên:", txtTenNV, font);
        addField(form, gbc, "Vai trò:", cbVaiTro, font);
        addField(form, gbc, "Giới tính:", genderPanel(), font);
        addField(form, gbc, "Ngày sinh:", txtNgaySinh, font);
        addField(form, gbc, "Số điện thoại:", txtSDT, font);
        addField(form, gbc, "Email:", txtEmail, font);
        addField(form, gbc, "Địa chỉ:", txtDiaChi, font);
        addField(form, gbc, "Ngày tham gia:", txtNgayThamGia, font);
        addField(form, gbc, "Trạng thái:", chkDangHoatDong, font);
        addField(form, gbc, "Ca làm:", cbCaLam, font);

        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(new Color(245, 245, 245));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        btnPanel.setBackground(new Color(245, 245, 245));

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnFind);
        btnPanel.add(btnClean);

        g.gridy = 0;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(0, 10, 0, 10);
        footer.add(btnPanel, g);

        JLabel lblTieuChi = new JLabel("<html><b><i>Tìm kiếm theo:</i></b> Tên, SĐT, Vai trò, Trạng thái</html>");
        lblTieuChi.setFont(new Font("Roboto", Font.ITALIC, 12));
        lblTieuChi.setForeground(COLOR_TEXT_LABEL);

        g.gridy = 1;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(0, 12, 6, 10);
        footer.add(lblTieuChi, g);

        p.add(form, BorderLayout.CENTER);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    private void loadDataToCaLamCombo() {
        cbCaLam.removeAllItems();
        List<CaLam> dsCaLam = nhanVien_ctrl.layDanhSachCaLam();
        for (CaLam ca : dsCaLam) {
            cbCaLam.addItem(ca);
        }
    }

    private void addField(JPanel p, GridBagConstraints gbc, String label, JComponent comp, Font font) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        comp.setFont(font);
        p.add(comp, gbc);
        gbc.gridy++;
    }

    private JPanel genderPanel() {
        JPanel g = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        g.setBackground(new Color(245, 245, 245));
        g.add(rbtnNam);
        g.add(rbtnNu);
        return g;
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 12));
        button.setBackground(new Color(173, 216, 230));
        button.setIcon(new FlatSVGIcon(iconPath, 16, 16));
        button.setPreferredSize(new Dimension(105, 30));
        return button;
    }

    private JPanel panelChiTiet() {
        JPanel info = new JPanel(new BorderLayout());
        info.setBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY), "Thông tin chi tiết",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("Roboto", Font.BOLD, 12), COLOR_PRIMARY));
        info.setBackground(COLOR_BG_PANEL);

        lblAvatar = new JLabel(new ImageIcon(new ImageIcon(getClass().getResource("/icon/png/avatar.png"))
                .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        info.add(lblAvatar, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridLayout(11, 2, 10, 5));
        details.setBackground(new Color(245, 245, 245));

        Color color = COLOR_TEXT_LABEL;

        lblMaNVDetail = createValueLabel();
        lblVaiTroDetail = createValueLabel();
        lblTenNVDetail = createValueLabel();
        lblGioiTinhDetail = createValueLabel();
        lblNgaySinhDetail = createValueLabel();
        lblSDTDetail = createValueLabel();
        lblEmailDetail = createValueLabel();
        lblDiaChiDetail = createValueLabel();
        lblNgayThamGiaDetail = createValueLabel();
        lblTrangThaiDetail = createValueLabel();
        lblCaLamDetail = createValueLabel();

        addDetailRow(details, "Mã nhân viên:", lblMaNVDetail, font, color);
        addDetailRow(details, "Vai trò:", lblVaiTroDetail, font, color);
        addDetailRow(details, "Tên nhân viên:", lblTenNVDetail, font, color);
        addDetailRow(details, "Giới tính:", lblGioiTinhDetail, font, color);
        addDetailRow(details, "Ngày sinh:", lblNgaySinhDetail, font, color);
        addDetailRow(details, "Số điện thoại:", lblSDTDetail, font, color);
        addDetailRow(details, "Email:", lblEmailDetail, font, color);
        addDetailRow(details, "Địa chỉ:", lblDiaChiDetail, font, color);
        addDetailRow(details, "Ngày tham gia:", lblNgayThamGiaDetail, font, color);
        addDetailRow(details, "Trạng thái:", lblTrangThaiDetail, font, color);
        addDetailRow(details, "Ca làm:", lblCaLamDetail, font, color);

        info.add(details, BorderLayout.CENTER);
        return info;
    }

    private void applyPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setFont(font);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    field.setFont(font);
                }
            }
        });
    }

    private String getRealText(JTextField field, String placeholder) {
        String text = field.getText().trim();
        if (text.equals(placeholder) && field.getForeground().equals(Color.GRAY)) {
            return "";
        }
        return text;
    }

    private void initPlaceholders() {
        applyPlaceholder(txtTenNV, "VD: Nguyễn Văn A");
        applyPlaceholder(txtSDT, "VD: 0912345678");
        applyPlaceholder(txtEmail, "VD: email123@gmail.com");
        applyPlaceholder(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");
    }

    private JLabel createValueLabel() {
        JLabel lbl = new JLabel("");
        lbl.setFont(new Font("Roboto", Font.PLAIN, 12));
        lbl.setForeground(COLOR_TEXT_LABEL);
        return lbl;
    }

    private void addDetailRow(JPanel panel, String title, JLabel value, Font font, Color color) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(font);
        lblTitle.setForeground(color);
        panel.add(lblTitle);
        panel.add(value);
    }

    private JScrollPane panelTable() {
        String[] cols = {"Mã NV", "Vai trò", "Tên nhân viên", "Giới tính", "Ngày sinh", "Số điện thoại", "Email",
                "Địa chỉ", "Ngày tham gia", "Trạng thái", "Ca làm"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Roboto", Font.PLAIN, 12));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 14));
        header.setBackground(new Color(30, 41, 58));
        header.setForeground(Color.WHITE);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(240, 248, 255));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } else {
                    c.setBackground(new Color(184, 207, 229));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(1000, 200));
        return scroll;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAdd) {
            themNhanVien();
            return;
        }

        if (src == btnClean) {
            cleanInputFields();
            btnEdit.setText("Sửa");
            isEditing = false;
            loadDataToTable();
            return;
        }

        if (src == btnFind) {
            timKiemNhanVien();
        }

        if (src == btnEdit) {
            if (!isEditing) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa.", "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                txtMaNV.setText(lblMaNVDetail.getText());
                String selectedRole = lblVaiTroDetail.getText();
                VaiTroNhanVienEnums vaiTro = VaiTroNhanVienEnums.fromDescription(selectedRole);
                cbVaiTro.setSelectedItem(vaiTro);

                txtTenNV.setText(lblTenNVDetail.getText());
                txtTenNV.setForeground(Color.BLACK);
                txtSDT.setText(lblSDTDetail.getText());
                txtSDT.setForeground(Color.BLACK);
                txtEmail.setText(lblEmailDetail.getText());
                txtEmail.setForeground(Color.BLACK);
                txtDiaChi.setText(lblDiaChiDetail.getText());
                txtDiaChi.setForeground(Color.BLACK);

                selectCaLamById(lblCaLamDetail.getText());
                chkDangHoatDong.setSelected(lblTrangThaiDetail.getText().equals("Đang hoạt động"));

                if (lblGioiTinhDetail.getText().equalsIgnoreCase("Nữ")) {
                    rbtnNu.setSelected(true);
                } else {
                    rbtnNam.setSelected(true);
                }

                try {
                    String nsStr = lblNgaySinhDetail.getText();
                    if (!nsStr.isEmpty()) {
                        LocalDate ns = LocalDate.parse(nsStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        txtNgaySinh.setDate(java.sql.Date.valueOf(ns));
                    } else {
                        txtNgaySinh.setDate(null);
                    }
                } catch (Exception ex) {
                    txtNgaySinh.setDate(null);
                }

                try {
                    String ntgStr = lblNgayThamGiaDetail.getText();
                    if (!ntgStr.isEmpty()) {
                        LocalDate ntg = LocalDate.parse(ntgStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        txtNgayThamGia.setDate(java.sql.Date.valueOf(ntg));
                    } else {
                        txtNgayThamGia.setDate(null);
                    }
                } catch (Exception ex) {
                    txtNgayThamGia.setDate(null);
                }

                btnEdit.setText("Lưu");
                isEditing = true;
            } else {
                if (!validForm()) {
                    return;
                }

                suaNhanVien();
                txtNgaySinh.setEnabled(true);
                rbtnNam.setEnabled(true);
                rbtnNu.setEnabled(true);

                btnEdit.setText("Sửa");
                isEditing = false;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == table) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                lblMaNVDetail.setText("" + model.getValueAt(row, 0));
                lblVaiTroDetail.setText("" + model.getValueAt(row, 1));
                lblTenNVDetail.setText("" + model.getValueAt(row, 2));
                lblGioiTinhDetail.setText("" + model.getValueAt(row, 3));
                lblNgaySinhDetail.setText("" + model.getValueAt(row, 4));
                lblSDTDetail.setText("" + model.getValueAt(row, 5));
                lblEmailDetail.setText("" + model.getValueAt(row, 6));
                lblDiaChiDetail.setText("" + model.getValueAt(row, 7));
                lblNgayThamGiaDetail.setText("" + model.getValueAt(row, 8));
                lblTrangThaiDetail.setText("" + model.getValueAt(row, 9));
                lblCaLamDetail.setText("" + model.getValueAt(row, 10));
            }
        }
    }

    public void loadDataToTable() {
        model.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (NhanVien nv : nhanVien_ctrl.layDanhSachNhanVien()) {
            model.addRow(new Object[]{
                    nv.getNhanVienID(),
                    nv.getVaiTroNhanVien() != null ? nv.getVaiTroNhanVien().getDescription() : "",
                    nv.getHoTen(),
                    nv.isNu() ? "Nữ" : "Nam",
                    nv.getNgaySinh() != null ? nv.getNgaySinh().format(dtf) : "",
                    nv.getSoDienThoai(),
                    nv.getEmail(),
                    nv.getDiaChi(),
                    nv.getNgayThamGia() != null ? nv.getNgayThamGia().format(dtf) : "",
                    nv.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động",
                    nv.getCaLam() != null ? nv.getCaLam().getCaLamID() : ""
            });
        }
    }

    public void cleanInputFields() {
        txtMaNV.setText("");
        txtTenNV.setText("");
        cbVaiTro.setSelectedIndex(0);
        txtNgaySinh.setCalendar(java.util.Calendar.getInstance());
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        txtNgayThamGia.setCalendar(java.util.Calendar.getInstance());
        cbCaLam.setSelectedIndex(0);
        rbtnNam.setSelected(true);
        chkDangHoatDong.setSelected(true);

        lblMaNVDetail.setText("");
        lblVaiTroDetail.setText("");
        lblTenNVDetail.setText("");
        lblGioiTinhDetail.setText("");
        lblNgaySinhDetail.setText("");
        lblSDTDetail.setText("");
        lblEmailDetail.setText("");
        lblDiaChiDetail.setText("");
        lblNgayThamGiaDetail.setText("");
        lblTrangThaiDetail.setText("");
        lblCaLamDetail.setText("");

        table.clearSelection();
        initPlaceholders();
    }

    private boolean validForm() {
        String ten = getRealText(txtTenNV, "VD: Nguyễn Văn A");
        String sdt = getRealText(txtSDT, "VD: 0912345678");
        String email = getRealText(txtEmail, "VD: email123@gmail.com");
        String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");
        CaLam ca = (CaLam) cbCaLam.getSelectedItem();

        if (ca == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            cbCaLam.requestFocus();
            return false;
        }

        if (!nhanVien_ctrl.validHoTen(ten)) {
            JOptionPane.showMessageDialog(this, "Tên không hợp lệ VD: Nguyễn Văn A", "Lỗi dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            txtTenNV.requestFocus();
            return false;
        }

        if (!nhanVien_ctrl.validSDT(sdt)) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ (đầu 0 theo dải nhà mạng VN.VD: 0912345678).", "Lỗi dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }

        if (!email.isEmpty() && !nhanVien_ctrl.validEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ! VD: haNguyen123@gmail.com", "Lỗi dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        if (!nhanVien_ctrl.validDiaChi(diaChi)) {
            JOptionPane.showMessageDialog(this,
                    "Địa chỉ không hợp lệ (tối thiểu 5 ký tự, cho phép chữ/số/khoảng trắng , . -). VD: 45/2 Nguyễn Huệ, Quận 1",
                    "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            txtDiaChi.requestFocus();
            return false;
        }

        if (txtNgaySinh.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            txtNgaySinh.requestFocus();
            return false;
        } else {
            LocalDate ns = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (!nhanVien_ctrl.ngaySinh(ns)) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (phải trước ngày hiện tại).", "Lỗi dữ liệu",
                        JOptionPane.WARNING_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
        }

        if (txtNgayThamGia.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tham gia.", "Lỗi dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            txtNgayThamGia.requestFocus();
            return false;
        } else {
            LocalDate ntg = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            if (!nhanVien_ctrl.ngayThamGia(ntg)) {
                JOptionPane.showMessageDialog(this, "Ngày tham gia không hợp lệ (không được sau hôm nay).",
                        "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
                txtNgayThamGia.requestFocus();
                return false;
            }
        }

        return true;
    }

    public void themNhanVien() {
        if (!validForm()) {
            return;
        }

        String maNV = nhanVien_ctrl.taoMaNhanVien();
        VaiTroNhanVienEnums vaiTroEnum = (VaiTroNhanVienEnums) cbVaiTro.getSelectedItem();
        VaiTroNhanVien vaiTro = new VaiTroNhanVien(vaiTroEnum.name());
        vaiTro.setMoTa(vaiTroEnum.getDescription());

        String hoTen = getRealText(txtTenNV, "VD: Nguyễn Văn A");
        boolean isNu = rbtnNu.isSelected();
        LocalDate ngaySinh = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        String soDienThoai = getRealText(txtSDT, "VD: 0912345678");
        String email = getRealText(txtEmail, "VD: email123@gmail.com");
        String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");
        LocalDate ngayThamGia = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        boolean isHoatDong = chkDangHoatDong.isSelected();
        CaLam caLam = (CaLam) cbCaLam.getSelectedItem();

        NhanVien nv = new NhanVien(maNV, vaiTro, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia,
                isHoatDong, caLam);

        boolean success = nhanVien_ctrl.themNhanVien(nv);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            loadDataToTable();
            cleanInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại! Vui lòng kiểm tra lại.", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectCaLamById(String caLamId) {
        for (int i = 0; i < cbCaLam.getItemCount(); i++) {
            CaLam ca = cbCaLam.getItemAt(i);
            if (ca != null && caLamId.equalsIgnoreCase(ca.getCaLamID())) {
                cbCaLam.setSelectedIndex(i);
                return;
            }
        }
    }

    public void suaNhanVien() {
        try {
            String maNV = txtMaNV.getText().trim();
            VaiTroNhanVienEnums vaiTroEnum = (VaiTroNhanVienEnums) cbVaiTro.getSelectedItem();
            VaiTroNhanVien vaiTro = new VaiTroNhanVien(vaiTroEnum.name());
            vaiTro.setMoTa(vaiTroEnum.getDescription());

            String hoTen = getRealText(txtTenNV, "VD: Nguyễn Văn A");
            boolean isNu = rbtnNu.isSelected();
            LocalDate ngaySinh = txtNgaySinh.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String soDienThoai = getRealText(txtSDT, "VD: 0912345678");
            String email = getRealText(txtEmail, "VD: email123@gmail.com");
            String diaChi = getRealText(txtDiaChi, "VD: 45/2 Nguyễn Huệ, Quận 1");

            LocalDate ngayThamGia = null;
            if (txtNgayThamGia.getDate() != null) {
                ngayThamGia = txtNgayThamGia.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            }

            boolean isHoatDong = chkDangHoatDong.isSelected();
            CaLam caLam = (CaLam) cbCaLam.getSelectedItem();

            NhanVien nv = new NhanVien(maNV, vaiTro, hoTen, isNu, ngaySinh, soDienThoai, email, diaChi, ngayThamGia,
                    isHoatDong, caLam);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn cập nhật thông tin nhân viên này không?", "Xác nhận cập nhật",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean success = nhanVien_ctrl.suaNhanVien(nv);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                cleanInputFields();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void timKiemNhanVien() {
        String ten = getRealText(txtTenNV, "VD: Nguyễn Văn A");
        String sdt = getRealText(txtSDT, "VD: 0912345678");
        VaiTroNhanVienEnums vaiTro = (VaiTroNhanVienEnums) cbVaiTro.getSelectedItem();
        Boolean isHoatDong = chkDangHoatDong.isSelected();

        model.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (NhanVien nv : nhanVien_ctrl.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong)) {
            model.addRow(new Object[]{
                    nv.getNhanVienID(),
                    nv.getVaiTroNhanVien() != null ? nv.getVaiTroNhanVien().getDescription() : "",
                    nv.getHoTen(),
                    nv.isNu() ? "Nữ" : "Nam",
                    nv.getNgaySinh() != null ? nv.getNgaySinh().format(dtf) : "",
                    nv.getSoDienThoai(),
                    nv.getEmail(),
                    nv.getDiaChi(),
                    nv.getNgayThamGia() != null ? nv.getNgayThamGia().format(dtf) : "",
                    nv.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động",
                    nv.getCaLam() != null ? nv.getCaLam().getCaLamID() : ""
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên phù hợp với tiêu chí đã chọn.", "Kết quả",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Component current = (Component) e.getSource();
            if (current instanceof JCheckBox checkbox) {
                checkbox.doClick();
            }

            int index = allField.indexOf(current);
            if (index != -1) {
                if (index < allField.size() - 1) {
                    allField.get(index + 1).requestFocus();
                } else {
                    btnAdd.requestFocus();
                }
            }
        }
    }

    private void setupComboKeyboard(JComboBox<?> combo) {
        JComponent target = combo;
        if (combo.isEditable() && combo.getEditor().getEditorComponent() instanceof JComponent editor) {
            target = editor;
        }

        InputMap im = target.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = target.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "openOrMoveDown");
        am.put("openOrMoveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!combo.isPopupVisible()) {
                    combo.showPopup();
                } else {
                    Action def = combo.getActionMap().get("selectNext");
                    if (def != null) {
                        def.actionPerformed(e);
                    }
                }
            }
        });

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterSelectOrNext");
        am.put("enterSelectOrNext", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (combo.isPopupVisible()) {
                    combo.hidePopup();
                } else {
                    combo.transferFocus();
                }
            }
        });
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}