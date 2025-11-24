package gui.application.form.TaiKhoan;

import controller.NhanVien_CTRL;
import controller.TaiKhoan_CTRL;
import entity.NhanVien;
import entity.TaiKhoan;
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

public class QuanLyTaiKhoan extends JPanel implements ActionListener, MouseListener, KeyListener{

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
    private JTextField txtTenDangNhap, txtThoiDiemTao, txtMatKhau, txtXacNhanMatKhau;
    private JCheckBox cbHoatDong;
    private JButton btnAdd, btnEdit, btnFind, btnClean;
    private DefaultTableModel model;
    private JTable table;
    private List<JComponent> allFields;

    public QuanLyTaiKhoan(NhanVien nhanVienHienTai){
        this.nhanVienHienTai = nhanVienHienTai;
        taiKhoan_ctrl = new TaiKhoan_CTRL();
        nhanVien_ctrl = new NhanVien_CTRL();


        setLayout(new BorderLayout(10,  10));
        setBackground(COLOR_BG_MAIN);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Roboto", Font.BOLD | Font.ITALIC, 26));
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

        table.addMouseListener(this);
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);
        btnClean.addActionListener(this);

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
        Font font = new Font("Roboto", Font.PLAIN, 14);

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
                    label.setText(((VaiTroTaiKhoan) value).name());  // Hiển thị description của enum
                }
                return label;
            }
        });
        cbNhanVien = new JComboBox<>();
        cbNhanVien.addItem("Chọn mã nhân viên");
        nhanVien_ctrl.layDanhSachNhanVien().forEach(nv -> cbNhanVien.addItem(nv.getNhanVienID()));
        cbNhanVien.setSelectedIndex(0);
        txtTenDangNhap = new JTextField();
        txtMatKhau = new JPasswordField();
        txtXacNhanMatKhau = new JPasswordField();
        txtThoiDiemTao = new JTextField();
        txtThoiDiemTao.setEnabled(false);
        cbHoatDong = new JCheckBox("Đang hoạt động");

        allFields = List.of(txtTaiKhoanID, cbVaiTro, cbNhanVien, txtTenDangNhap,
                txtMatKhau, txtXacNhanMatKhau, txtThoiDiemTao, cbHoatDong);

        for(JComponent field : allFields){
            field.addKeyListener(this);
        }

        addField(form, gbc, "ID tài khoản: ", txtTaiKhoanID, font);
        addField(form, gbc, "Vai trò: ", cbVaiTro, font);
        addField(form, gbc, "Nhân viên: ", cbNhanVien, font);
        addField(form, gbc, "Tên đăng nhập: ", txtTenDangNhap, font);
        addField(form, gbc, "Mật khẩu: ", txtMatKhau, font);
        addField(form, gbc, "Xác nhận mật khẩu: ", txtXacNhanMatKhau, font);
        addField(form, gbc, "Thời điểm tạo: ", txtThoiDiemTao, font);
        addField(form, gbc, "Hoạt động: ", cbHoatDong, font);

        //các nút chức năng
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(COLOR_BG_PANEL);

        btnAdd = createButton("Thêm", "/gui/icon/png/save.png");
        btnEdit = createButton("Sửa", "/gui/icon/png/update.png");
        btnFind = createButton("Tìm kiếm", "/gui/icon/png/find.png");
        btnClean = createButton("Xóa trắng", "/gui/icon/png/clean.png");

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnFind);
        btnPanel.add(btnClean);

        p.add(form, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);

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

    private JButton createButton(String text, String iconPath){
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setBackground(COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));


        try {
            btn.setIcon(new ImageIcon(new ImageIcon(getClass().getResource(iconPath))
                    .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }
        return btn;
    }

    //table
    private JScrollPane panelTable(){
        String[] cols = {"ID tài khoản", "Vai trò", "Mã nhân viên", "Tên đang nhập", "Thời điểm tạo", "Hoạt động"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Roboto", Font.BOLD, 14));
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
            clearForm();
        }else if(source == btnEdit){
            TaiKhoan tk = new TaiKhoan();
            capNhatTaiKhoan(tk);
            clearForm();
        }else if(source == btnFind){
            String maNV = cbNhanVien.getSelectedItem() != "Chọn mã nhân viên" ? cbNhanVien.getSelectedItem().toString() : null;
            String vaiTro = cbVaiTro.getSelectedItem() != null ? cbVaiTro.getSelectedItem().toString() : null;
            String tenDangNhap = txtTenDangNhap.getText().trim() != null ? txtTenDangNhap.getText().trim() : null;
            boolean hoatDong = cbHoatDong.isSelected();
            timKiemTaiKhoan(maNV, tenDangNhap, vaiTro, hoatDong);
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
            cbNhanVien.setSelectedItem(model.getValueAt(row, 2).toString());
            txtTenDangNhap.setText(model.getValueAt(row, 3).toString());
            txtThoiDiemTao.setText(model.getValueAt(row, 4).toString());
            cbHoatDong.setSelected(model.getValueAt(row, 5).toString().equals("Hoạt động"));
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
        cbVaiTro.setSelectedIndex(0);
        if (cbNhanVien.getItemCount() > 0) {
            cbNhanVien.setSelectedIndex(0);
        }
        txtTenDangNhap.setText("");
        txtMatKhau.setText("");
        txtXacNhanMatKhau.setText("");
        txtThoiDiemTao.setText("");
        cbHoatDong.setSelected(false);
    }

    //them tai khoan
    private boolean themTaiKhoan(TaiKhoan tk){
        if(!validateForm()){
            return false;
        }
        try{
            String newID = taiKhoan_ctrl.taoMaTaiKhoan();
            if(newID == null || newID.isEmpty()){
                JOptionPane.showMessageDialog(this, "Tạo mã tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String maNV = (String) cbNhanVien.getSelectedItem();
            NhanVien nv = nhanVien_ctrl.layNhanVienBangMaNV(maNV);
            String matKhauPlain = new String(((JPasswordField) txtMatKhau).getPassword()).trim();
            LocalDateTime thoiDiemTao = LocalDateTime.now();

            tk.setTaiKhoanID(newID);
            tk.setVaiTroTaiKhoan((VaiTroTaiKhoan) cbVaiTro.getSelectedItem());
            tk.setNhanVien(nv);
            tk.setTenDangNhap(txtTenDangNhap.getText().trim());
            tk.setMatKhauHash(BCrypt.hashpw(matKhauPlain, BCrypt.gensalt(12)));
            tk.setHoatDong(cbHoatDong.isSelected());
            tk.setThoiDiemTao(thoiDiemTao);

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
        try {
            String taiKhoanID = txtTaiKhoanID.getText().trim();
            String maNV = (String) cbNhanVien.getSelectedItem();
            NhanVien nv = nhanVien_ctrl.layNhanVienBangMaNV(maNV);
            String matKhauPlain = new String(((JPasswordField) txtMatKhau).getPassword()).trim();
            LocalDateTime thoiDiemTao = LocalDateTime.now();

            tk.setTaiKhoanID(taiKhoanID);
            tk.setVaiTroTaiKhoan((VaiTroTaiKhoan) cbVaiTro.getSelectedItem());
            tk.setNhanVien(nv);
            tk.setTenDangNhap(txtTenDangNhap.getText().trim());
            tk.setMatKhauHash(BCrypt.hashpw(matKhauPlain, BCrypt.gensalt(12)));
            tk.setHoatDong(cbHoatDong.isSelected());
            tk.setThoiDiemTao(thoiDiemTao);

            if(taiKhoan_ctrl.capNhatTaiKhoan(tk)){
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                clearForm();
                return true;
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }

    //tim kiem tai khoan
    private void timKiemTaiKhoan(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        model.setRowCount(0);

        try{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            List<TaiKhoan> ketQua = taiKhoan_ctrl.timKiemTongHop(maNV,tenDN, vaiTro, trangThai);
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
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Tìm kiếm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //vailid form
    private boolean validateForm(){
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        String xacNhanMatKhau = txtXacNhanMatKhau.getText().trim();

        if(cbNhanVien.getSelectedIndex() == 0){
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (tenDangNhap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }else if(taiKhoan_ctrl.kiemTraTenDangNhapTonTai(tenDangNhap)){
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }else if(!taiKhoan_ctrl.kiemTraTenDangNhap(tenDangNhap)){
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không hợp lệ. Ví dụ: ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!taiKhoan_ctrl.kiemTraMatKhau(matKhau, xacNhanMatKhau)) {
            JOptionPane.showMessageDialog(this, "Xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
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
