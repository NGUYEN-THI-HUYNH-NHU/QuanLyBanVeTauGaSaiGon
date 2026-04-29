package gui.application.form.taiKhoan;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import controller.NhanVien_CTRL;
import controller.TaiKhoan_CTRL;
import entity.NhanVien;
import entity.TaiKhoan;
import entity.type.VaiTroNhanVien;
import entity.type.VaiTroTaiKhoan;
import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelQuanLyTaiKhoan extends JPanel implements ActionListener, MouseListener, KeyListener{

    private final NhanVien nhanVienHienTai;
    private final TaiKhoan_CTRL taiKhoan_ctrl;
    private final NhanVien_CTRL nhanVien_ctrl;


    private final Color COLOR_PRIMARY = new Color(30, 100, 150);
    private final Color COLOR_ACCENT = new Color(74, 163, 208);
    private final Color COLOR_BG_MAIN = new Color(248, 250, 251);
    private final Color COLOR_BG_PANEL = new Color(226, 232, 240);
    private final Color COLOR_TEXT_TITLE = new Color(30, 41, 59);
    private final Color COLOR_TEXT_LABEL = new Color(51, 65, 85);

    private JTextField txtTaiKhoanID;
    private JComboBox<VaiTroTaiKhoan> cbVaiTro;
    private JComboBox<Object> cbNhanVien;
    private JTextField txtTenDangNhap, txtThoiDiemTao;
    private JPasswordField txtMatKhau, txtXacNhanMatKhau;
    private JCheckBox cbHoatDong;
    private JButton btnAdd, btnEdit, btnFind, btnClean;
    private DefaultTableModel model;
    private JTable table;
    private List<JComponent> allFields;
    private String tenDangNhapCu = "";

    public PanelQuanLyTaiKhoan(NhanVien nhanVienHienTai) {
        this.nhanVienHienTai = nhanVienHienTai;
        taiKhoan_ctrl = new TaiKhoan_CTRL();
        this.nhanVien_ctrl = new NhanVien_CTRL(nhanVienHienTai);


        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 26));
        lblTitle.setForeground(COLOR_TEXT_TITLE);
        add(lblTitle, BorderLayout.NORTH);


        //panel body
        JPanel panelBody = new JPanel();
        panelBody.setLayout(new BorderLayout());
        panelBody.setBackground(COLOR_BG_MAIN);
        add(panelBody, BorderLayout.CENTER);
        panelBody.add(panelInput(), BorderLayout.NORTH);
        panelBody.add(panelTable(), BorderLayout.CENTER);
        loadDataToTable();
        initPlaceholders();
        autoFillVaiTro();

        table.addMouseListener(this);
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);
        btnClean.addActionListener(this);
    }
    //chọn maNV tự cập nhật vai trò
    private void updateVaiTroTheoMaNV(){
        Object selectedMaNV = cbNhanVien.getSelectedItem();
        if(selectedMaNV != null && !selectedMaNV.equals("Chọn mã nhân viên")){
            String maNV = selectedMaNV.toString();
            VaiTroNhanVien vaiTroNhanVien = nhanVien_ctrl.layVaiTroNhanVienTheoMaNV(maNV);
            if(vaiTroNhanVien != null){
                switch (vaiTroNhanVien){
                    case QUAN_LY -> cbVaiTro.setSelectedItem(VaiTroTaiKhoan.QUAN_LY);
                    case NHAN_VIEN-> cbVaiTro.setSelectedItem(VaiTroTaiKhoan.NHAN_VIEN);
                    default -> cbVaiTro.setSelectedIndex(0);
                }
            }else{
                cbVaiTro.setSelectedIndex(0);
            }
        }else{
            cbVaiTro.setSelectedIndex(0);
        }
    }

    private void autoFillVaiTro(){
        cbNhanVien.addItemListener(e -> {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    updateVaiTroTheoMaNV();
                }
        });
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
                    field.setFont(new Font("Roboto", Font.PLAIN, 13));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    field.setFont(new Font("Roboto", Font.PLAIN, 13));
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
        applyPlaceholder(txtTenDangNhap, "VD: user123");
        applyPlaceholder(txtMatKhau, "VD: P@ssw0rd");
        applyPlaceholder(txtXacNhanMatKhau, "VD: P@ssw0rd");
    }

    private JPanel panelInput(){
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRIMARY),
                "Thông Tin Tài Khoản",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 16),
                COLOR_PRIMARY));
        p.setBackground(COLOR_BG_PANEL);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_BG_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        Font font = new Font("Roboto", Font.PLAIN, 13);

        //Text
        txtTaiKhoanID = new JTextField();
        txtTaiKhoanID.setEnabled(false);
        cbVaiTro = new JComboBox<>();
        cbVaiTro.addItem(null);
        for (VaiTroTaiKhoan vaiTro : VaiTroTaiKhoan.values()) {
            cbVaiTro.addItem(vaiTro);
        }
        cbVaiTro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    label.setText("Chọn vai trò");
                } else if (value instanceof VaiTroTaiKhoan) {
                    label.setText(((VaiTroTaiKhoan) value).name());
                }
                return label;
            }
        });


        cbNhanVien = new JComboBox<>();
        cbNhanVien.removeAllItems();
        cbNhanVien.addItem("Chọn mã nhân viên");
        nhanVien_ctrl.layDanhSachNhanVien()
                .forEach(nv -> cbNhanVien.addItem(nv.getNhanVienID()));
        cbNhanVien.setSelectedIndex(0);

        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();
        txtXacNhanMatKhau = new JPasswordField();
        txtThoiDiemTao = new JTextField();
        txtThoiDiemTao.setEnabled(false);
        cbHoatDong = new JCheckBox("Đang hoạt động");
        cbHoatDong.setSelected(true);

        //gán sự kiện key listener cho tất cả các field
        allFields = List.of(txtTaiKhoanID, cbVaiTro, cbNhanVien, txtTenDangNhap,
                txtMatKhau, txtXacNhanMatKhau, txtThoiDiemTao, cbHoatDong);

        for(JComponent field : allFields){
            field.addKeyListener(this);
        }

        addField(form, gbc, "ID tài khoản: ", txtTaiKhoanID, font);
        addField(form, gbc, "Nhân viên: ", cbNhanVien, font);
        addField(form, gbc, "Vai trò: ", cbVaiTro, font);
        addField(form, gbc, "Tên đăng nhập: ", txtTenDangNhap, font);
        addField(form, gbc, "Mật khẩu: ", txtMatKhau, font);
        addField(form, gbc, "Xác nhận mật khẩu: ", txtXacNhanMatKhau, font);
        addField(form, gbc, "Thời điểm tạo: ", txtThoiDiemTao, font);
        addField(form, gbc, "Hoạt động: ", cbHoatDong, font);

        //các nút chức năng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(COLOR_BG_PANEL);

        btnAdd = createButton("Thêm", "icon/svg/add-kh.svg");
        btnEdit = createButton("Sửa", "icon/svg/edit-kh.svg");
        btnFind = createButton("Tìm kiếm", "icon/svg/search-kh.svg");
        btnClean = createButton("Xóa trắng", "icon/svg/refresh-kh.svg");

        // tooltips
        btnFind.setToolTipText("Tìm kiếm: Mã nhân viên, Tên đăng nhập, Vai trò và Trạng thái hoạt động.");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnFind);
        btnPanel.add(btnClean);

        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(COLOR_BG_PANEL);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridy = 0;
        g.anchor = GridBagConstraints.EAST;
        g.insets = new Insets(0, 10, 0, 10);
        footer.add(btnPanel, g);

        JLabel lblTieuChi = new JLabel(
                "<html><b><i>Tìm kiếm theo:</i></b> Mã nhân viên, Tên đăng nhập, Vai trò, Trạng thái</html>"
        );
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

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, Font labelFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(COLOR_TEXT_LABEL);
        gbc.gridx = 0;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);

        gbc.gridy++;
    }

    private JButton createButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 13));
        button.setBackground(new Color(173, 216, 230));
        button.setIcon(new FlatSVGIcon(iconPath, 16, 16));
        button.setPreferredSize(new Dimension(120, 30));

        return button;
    }

    //table
    private JScrollPane panelTable(){
        String[] cols = {"ID tài khoản", "Vai trò", "Mã nhân viên", "Tên đang nhập", "Thời điểm tạo", "Hoạt động"};

        model = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Roboto", Font.PLAIN, 13));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setBackground(COLOR_PRIMARY);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(COLOR_ACCENT);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_BG_PANEL);
                    c.setForeground(Color.BLACK);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(1000, 200));
        return scrollPane;

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source == btnAdd){
            TaiKhoan tk = new TaiKhoan();
            themTaiKhoan(tk);
        }else if(source == btnFind){
            String maNV = null;
            Object nvSelected = cbNhanVien.getSelectedItem();
            if(nvSelected != null && !nvSelected.equals("Chọn mã nhân viên")){
                maNV = nvSelected.toString();
            }
            String vaiTro = null;
            Object vaiTroSelected = cbVaiTro.getSelectedItem();
            if(vaiTroSelected != null){
                vaiTro = vaiTroSelected.toString();
            }
            String tenDN = getRealText(txtTenDangNhap, "VD: user123").trim();
            Boolean hoatDong = null;
            if(cbHoatDong.isSelected()){
                hoatDong = true;
            }else{
                hoatDong = false;
            }
            timKiemTaiKhoan(maNV, tenDN, vaiTro, hoatDong);

        }else if(source == btnEdit){
        if (txtTaiKhoanID.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn tài khoản cần sửa từ bảng.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan tk = new TaiKhoan();
        capNhatTaiKhoan(tk);
        clearForm();
    }else if(source == btnClean){
            clearForm();
            loadDataToTable();
        }

}

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtTaiKhoanID.setText(model.getValueAt(row, 0).toString());

            cbVaiTro.setSelectedItem(VaiTroTaiKhoan.valueOf(model.getValueAt(row, 1).toString()));
            cbVaiTro.setEnabled(false);

            cbNhanVien.setSelectedItem(model.getValueAt(row, 2).toString());
            cbNhanVien.setEnabled(false);

            tenDangNhapCu = model.getValueAt(row, 3).toString().trim();
            txtTenDangNhap.setForeground(Color.BLACK);
            txtTenDangNhap.setText(tenDangNhapCu);

            txtThoiDiemTao.setText(model.getValueAt(row, 4).toString());
            cbHoatDong.setSelected(model.getValueAt(row, 5).toString().equals("Hoạt động"));

            txtMatKhau.setText("");
            txtXacNhanMatKhau.setText("");
        }
    }


    //load dữ liệu lên table
    private void loadDataToTable(){
        model.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for(TaiKhoan tk : taiKhoan_ctrl.layDanhSachTaiKhoan()){
            model.addRow(new Object[]{
                    tk.getTaiKhoanID(),
                    tk.getVaiTroTaiKhoan(),
                    tk.getNhanVien().getNhanVienID(),
                    tk.getTenDangNhap(),
                    tk.getThoiDiemTao().format(dtf),
                    tk.isHoatDong() ? "Hoạt động" : "Không hoạt động"
            });
        }
    }

    //xóa trắng form
    private void clearForm(){
        txtTaiKhoanID.setText("");
        txtTaiKhoanID.setEnabled(false);

        cbVaiTro.setSelectedIndex(0);
        cbVaiTro.setEnabled(true);

        if (cbNhanVien.getItemCount() > 0) {
            cbNhanVien.setSelectedIndex(0);
        }
        cbNhanVien.setEnabled(true);

        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtXacNhanMatKhau.setText("");
        txtThoiDiemTao.setText("");
        cbHoatDong.setSelected(false);
        initPlaceholders();
    }


    //them tai khoan
    private boolean themTaiKhoan(TaiKhoan tk){
        if(!validateformThem()){
            return false;
        }
        try{
            String newID = taiKhoan_ctrl.taoMaTaiKhoan();
            if(newID == null || newID.isEmpty()){
                JOptionPane.showMessageDialog(this, "Tạo mã tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String maNV = cbNhanVien.getSelectedItem().toString();
            NhanVien nv = nhanVien_ctrl.layNhanVienBangMaNV(maNV);
            VaiTroTaiKhoan vaiTro = (VaiTroTaiKhoan) cbVaiTro.getSelectedItem();
            String tenDangNhap = getRealText(txtTenDangNhap, "VD: user123").trim();
            String matKhauPlain = getRealText((JPasswordField) txtMatKhau, "VD: P@ssw0rd").trim();
            LocalDateTime thoiDiemTao = LocalDateTime.now();

            tk.setTaiKhoanID(newID);
            tk.setVaiTroTaiKhoan(vaiTro);
            tk.setNhanVien(nv);
            tk.setTenDangNhap(tenDangNhap);
            tk.setMatKhauHash(BCrypt.hashpw(matKhauPlain, BCrypt.gensalt(12)));
            tk.setHoatDong(cbHoatDong.isSelected());
            tk.setThoiDiemTao(thoiDiemTao);

            if (cbNhanVien.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                cbNhanVien.requestFocus();
                return false;
            }

            if (taiKhoan_ctrl.layTKTheoMaNV(maNV)!= null) {
                JOptionPane.showMessageDialog(this,
                        "Nhân viên này đã có tài khoản rồi. Vui lòng chọn nhân viên khác!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if(taiKhoan_ctrl.themTaiKhoan(tk)){
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                clearForm();
                return true;
            }
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;

        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }


    //cap nhat tai khoan
    private boolean capNhatTaiKhoan(TaiKhoan tk){
        if(!validateFormSua()){
            return false;
        }

        try {
            String taiKhoanID = txtTaiKhoanID.getText().trim();
            String maNV = cbNhanVien.getSelectedItem().toString();
            NhanVien nv = nhanVien_ctrl.layNhanVienBangMaNV(maNV);

            String tenDangNhap = getRealText(txtTenDangNhap, "VD: user123").trim();

            String matKhauPlain = getRealText((JPasswordField) txtMatKhau, "VD: P@ssw0rd").trim();
            boolean coDoiMatKhau = !matKhauPlain.isBlank();

            tk.setTaiKhoanID(taiKhoanID);
            tk.setVaiTroTaiKhoan((VaiTroTaiKhoan) cbVaiTro.getSelectedItem());
            tk.setNhanVien(nv);
            tk.setTenDangNhap(tenDangNhap);
            tk.setHoatDong(cbHoatDong.isSelected());


            // Chỉ set matKhauHash nếu có đổi mật khẩu
            if (coDoiMatKhau) {
                tk.setMatKhauHash(BCrypt.hashpw(matKhauPlain, BCrypt.gensalt(12)));
            } else {
                tk.setMatKhauHash(null);
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn cập nhật tài khoản này?",
                    "Xác nhận cập nhật",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return false;

            if(taiKhoan_ctrl.capNhatTaiKhoan(tk)){
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                clearForm();
                return true;
            }

            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật tài khoản thất bại!\nChi tiết: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
            return false;
        }

    }


    //tim kiem tai khoan
    private void timKiemTaiKhoan(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        model.setRowCount(0);

        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            List<TaiKhoan> ketQua = taiKhoan_ctrl.timKiemTongHop(maNV, tenDN, vaiTro, trangThai);
            for (TaiKhoan tk : ketQua) {
                if(tk == null) continue;
                model.addRow(new Object[]{
                        tk.getTaiKhoanID(),
                        tk.getVaiTroTaiKhoan(),
                        tk.getNhanVien().getNhanVienID(),
                        tk.getTenDangNhap(),
                        tk.getThoiDiemTao().format(dtf),
                        tk.isHoatDong() ? "Hoạt động" : "Không hoạt động"
                });
            }

            if(ketQua.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản phù hợp!", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Tìm kiếm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //vailid form
    private boolean validateformThem(){
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = getRealText(txtMatKhau, "VD: P@ssw0rd").trim();
        String xacNhanMatKhau = getRealText(txtXacNhanMatKhau, "VD: P@ssw0rd").trim();

        if(cbNhanVien.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            cbNhanVien.requestFocus();
            return false;
        }
        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }
        if(taiKhoan_ctrl.kiemTraTenDangNhapTonTai(tenDangNhap)){
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }
        if(!taiKhoan_ctrl.kiemTraTenDangNhap(tenDangNhap)){
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không hợp lệ. VD: user123", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtMatKhau.requestFocus();
            return false;
        }
        if (!taiKhoan_ctrl.kiemTraMatKhau(matKhau, xacNhanMatKhau)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtXacNhanMatKhau.requestFocus();
            return false;
        }
        return true;
    }

    //valid form sua
    private boolean validateFormSua() {
        String taiKhoanID = txtTaiKhoanID.getText().trim();
        if (taiKhoanID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần sửa từ bảng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (cbNhanVien.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            cbNhanVien.requestFocus();
            return false;
        }

        String tenDangNhap = getRealText(txtTenDangNhap, "VD: user123").trim();
        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }

        if (!taiKhoan_ctrl.kiemTraTenDangNhap(tenDangNhap)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không hợp lệ. VD: user123", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }

        if (!tenDangNhap.equalsIgnoreCase(tenDangNhapCu)
                && taiKhoan_ctrl.kiemTraTenDangNhapTonTai(tenDangNhap)) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtTenDangNhap.requestFocus();
            return false;
        }

        // Nếu đổi mật khẩu thì mới check confirm
        String mk = getRealText((JPasswordField) txtMatKhau, "VD: P@ssw0rd").trim();
        String mk2 = getRealText((JPasswordField) txtXacNhanMatKhau, "VD: P@ssw0rd").trim();
        if (!mk.isBlank() || !mk2.isBlank()) {
            if (mk.isBlank() || mk2.isBlank()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ mật khẩu và xác nhận mật khẩu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtMatKhau.requestFocus();
                return false;
            }
            if (!taiKhoan_ctrl.kiemTraMatKhau(mk, mk2)) {
                JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                txtXacNhanMatKhau.requestFocus();
                return false;
            }
        }
        VaiTroTaiKhoan vaiTro = (VaiTroTaiKhoan) cbVaiTro.getSelectedItem();
        if (vaiTro == null) {
            updateVaiTroTheoMaNV();
            vaiTro = (VaiTroTaiKhoan) cbVaiTro.getSelectedItem();
            if (vaiTro == null) {
                JOptionPane.showMessageDialog(this, "Không xác định được vai trò tài khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }



        return true;
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


    @Override public void mousePressed(MouseEvent e) {

    }
    @Override public void mouseReleased(MouseEvent e) {

    }
    @Override public void mouseEntered(MouseEvent e) {

    }
    @Override public void mouseExited(MouseEvent e) {

    }
    @Override public void keyTyped(KeyEvent e) {

    }
    @Override public void keyReleased(KeyEvent e) {

    }
}
