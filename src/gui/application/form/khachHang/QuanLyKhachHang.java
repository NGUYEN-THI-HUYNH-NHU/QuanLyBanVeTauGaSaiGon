package gui.application.form.khachHang;

import controller.KhachHang_CTRL;
import entity.KhachHang;
import entity.NhanVien;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiKhachHang;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class QuanLyKhachHang extends JPanel implements ActionListener, MouseListener {

    private final KhachHang_CTRL khachHang_ctrl;
    private final NhanVien nhanVienThucHien;

    private JTextField txtMaKH, txtTenKH, txtSDT, txtEmail, txtSoGiayTo, txtDiaChi;
    private JComboBox<LoaiDoiTuong> cbLDT;
    private JComboBox<LoaiKhachHang> cbLKH;
    private JLabel lblErrorTenKH, lblErrorSDT, lblErrorEmail, lblErrorDiaChi, lblErrorSGT;

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnFind, btnClean;

    // panel hiển thị thông tin khi click
    private JLabel lblChiTietTen, lblChiTietSDT, lblChiTietEmail, lblChiTietDiaChi, lblChiTietLoaiDoiTuong, lblChiTietLoaiKhachHang, lblChiTietGiayTo;
    private boolean Editing;
    private Font titleFont;

    public QuanLyKhachHang(NhanVien nhanVienThucHien) {
        this.khachHang_ctrl = new KhachHang_CTRL();
        this.nhanVienThucHien = nhanVienThucHien;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        add(createTopSplitPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        loadDataToTable();
    }

    // tạo panel chia đôi ở phía trên
    private JSplitPane createTopSplitPanel() {
        JPanel leftForm = panelInput();
        JPanel rightInfo = createPanleInfor();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftForm, rightInfo);
        splitPane.setDividerLocation(550);
        splitPane.setResizeWeight(0.5);
        splitPane.setContinuousLayout(true);
        return splitPane;
    }
    // panel nhập thông tin khách hàng
    private JPanel panelInput() {
        JPanel panelTop = new JPanel(new BorderLayout(10, 10));
        panelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(30, 100, 150), 1), "Thông tin khách hàng", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 8, 3, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        Font font = new Font("Roboto", Font.PLAIN, 14);

        int y = 0;
        formAddField(form, gbc, y++, "Mã khách hàng:", txtMaKH = new JTextField(), null, font);
        txtMaKH.setEnabled(false);
        formAddField(form, gbc, y++, "Tên khách hàng:", txtTenKH = new JTextField(), lblErrorTenKH = errorLabel(), font);
        formAddField(form, gbc, y++, "Số điện thoại:", txtSDT = new JTextField(), lblErrorSDT = errorLabel(), font);
        formAddField(form, gbc, y++, "Email:", txtEmail = new JTextField(), lblErrorEmail = errorLabel(), font);
        formAddField(form, gbc, y++, "Số giấy tờ:", txtSoGiayTo = new JTextField(), lblErrorSGT = errorLabel(), font);
        formAddField(form, gbc, y++, "Loại đối tượng:", cbLDT = new JComboBox<>(LoaiDoiTuong.values()), null, font);
        formAddField(form, gbc, y++, "Loại khách hàng:", cbLKH = new JComboBox<>(LoaiKhachHang.values()), null, font);
        formAddField(form, gbc, y++, "Địa chỉ:", txtDiaChi = new JTextField(), lblErrorDiaChi = errorLabel(), font);

        // Các nút thao tác
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnPanel.setBackground(Color.WHITE);
        btnAdd = createButton("Thêm", "/gui/icon/png/save.png");
        btnEdit = createButton("Sửa", "/gui/icon/png/update.png");
        btnFind = createButton("Tìm kiếm", "/gui/icon/png/find.png");
        btnClean = createButton("Xóa trắng", "/gui/icon/png/clean.png");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnFind);
        btnPanel.add(btnClean);

        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);
        btnClean.addActionListener(this);

        panelTop.add(form, BorderLayout.CENTER);
        panelTop.add(btnPanel, BorderLayout.SOUTH);
        return panelTop;
    }

    // Chọn 1 dòng trong table
    private JPanel createPanleInfor() {
        JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(30, 100, 150), 1), "Thông tin chi tiết", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15)));
        infoPanel.setBackground(Color.WHITE);

        titleFont = new Font("Roboto", Font.PLAIN, 14);

        lblChiTietTen = lableInfor("Tên khách hàng:", titleFont);
        lblChiTietSDT = lableInfor("Số điện thoại:", titleFont);
        lblChiTietEmail = lableInfor("Email:", titleFont);
        lblChiTietDiaChi = lableInfor("Địa chỉ:", titleFont);
        lblChiTietLoaiDoiTuong = lableInfor("Loại đối tượng:", titleFont);
        lblChiTietLoaiKhachHang = lableInfor("Loại khách hàng:", titleFont);
        lblChiTietGiayTo = lableInfor("Giấy tờ:", titleFont);

        infoPanel.add(lblChiTietTen);
        infoPanel.add(lblChiTietSDT);
        infoPanel.add(lblChiTietEmail);
        infoPanel.add(lblChiTietDiaChi);
        infoPanel.add(lblChiTietLoaiDoiTuong);
        infoPanel.add(lblChiTietLoaiKhachHang);
        infoPanel.add(lblChiTietGiayTo);
        return infoPanel;
    }

    // gắn giá trị ban đầu cho cái panel này nè
    private JLabel lableInfor(String title, Font titleFont) {
        JLabel lbl = new JLabel(title + " — ");
        lbl.setFont(titleFont);
        return lbl;
    }

    //reset thong tin panel
    private void lableInfor() {
        lblChiTietTen.setText("Tên khách hàng: — ");
        lblChiTietSDT.setText("Số điện thoại: — ");
        lblChiTietEmail.setText("Email: — ");
        lblChiTietDiaChi.setText("Địa chỉ: — ");
        lblChiTietLoaiDoiTuong.setText("Loại đối tượng: — ");
        lblChiTietLoaiKhachHang.setText("Loại khách hàng: — ");
        lblChiTietGiayTo.setText("Giấy tờ: — ");
    }


    // khuôn của form
    private void formAddField(JPanel panel, GridBagConstraints gbc, int y, String labelText, JComponent field, JLabel errorLabel, Font font) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(font);
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        field.setFont(font);
        panel.add(field, gbc);

        if (errorLabel != null) {
            gbc.gridx = 2; gbc.weightx = 0.5;
            panel.add(errorLabel, gbc);
        }
    }

    //mẫu label lỗi
    private JLabel errorLabel() {
        JLabel lbl = new JLabel("");
        lbl.setForeground(Color.RED);
        lbl.setFont(new Font("Roboto", Font.ITALIC, 12));
        return lbl;
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

    // Panle danh sách khách hàng
    private JScrollPane createTablePanel() {
        String[] columnNames = { "STT","Mã KH", "Tên KH", "SĐT", "Email", "Giấy tờ", "Địa chỉ", "Loại đối tượng", "Loại KH"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Roboto", Font.PLAIN, 13));
        table.setRowHeight(25);
        table.addMouseListener(this);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 100, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Roboto", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(30, 100, 150), 1), "Danh sách khách hàng", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 15)));

        return scroll;
    }

    //Load dữ liệu lên bảng
    public void loadDataToTable() {
        List<KhachHang> dsKH = khachHang_ctrl.getAllKhachHang();
        int stt = 1;
        tableModel.setRowCount(0);
        for (KhachHang kh : dsKH) {
            tableModel.addRow(new Object[]{
                    stt++,
                    kh.getKhachHangID(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getEmail(),
                    kh.getSoGiayTo(),
                    kh.getDiaChi(),
                    kh.getLoaiDoiTuong(),
                    kh.getLoaiKhachHang()
            });
        }
    }

    // click 1 dòng trên table
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == table) {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String ten = tableModel.getValueAt(row, 2).toString();
                String sdt = tableModel.getValueAt(row, 3).toString();
                String email = tableModel.getValueAt(row, 4).toString();
                String giayTo = tableModel.getValueAt(row, 5).toString();
                String diaChi = tableModel.getValueAt(row, 6).toString();
                String loaiDoiTuong = tableModel.getValueAt(row, 7).toString();
                String loaiKhachHang = tableModel.getValueAt(row, 8).toString();

                lblChiTietTen.setText("Tên khách hàng: " + ten);
                lblChiTietSDT.setText("Số điện thoại: " + sdt);
                lblChiTietEmail.setText("Email: " + email);
                lblChiTietDiaChi.setText("Địa chỉ: " + diaChi);
                lblChiTietGiayTo.setText("Giấy tờ: " + giayTo);
                lblChiTietLoaiDoiTuong.setText("Loại đối tượng: " + loaiDoiTuong);
                lblChiTietLoaiKhachHang.setText("Loại khách hàng: " + loaiKhachHang);
            }
        }
    }

    //reset lableError
    public void resetErrorLabels(){
        lblErrorTenKH.setText("");
        lblErrorSDT.setText("");
        lblErrorEmail.setText("");
        lblErrorDiaChi.setText("");
        lblErrorSGT.setText("");
    }

    //Valid form
    public boolean isValidForm(){
        resetErrorLabels();
        boolean isValid = true;

        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String soGiayTo = txtSoGiayTo.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if(tenKH.isEmpty() || !khachHang_ctrl.isValidTen(tenKH)){
            lblErrorTenKH.setText("Tên khách hàng không hợp lệ!");
            isValid = false;
        } else {
            lblErrorTenKH.setText("");
        }
        if(sdt.isEmpty() || !khachHang_ctrl.isValidPhoneNumber(sdt)){
            lblErrorSDT.setText("Số điện thoại không hợp lệ! ");
            isValid = false;
        } else if(khachHang_ctrl.kiemTraTrungSDT(sdt)){
            lblErrorSDT.setText("Số điện thoại đã tồn tại!");
            isValid = false;

        }else {
            lblErrorSDT.setText("");
        }
        if(soGiayTo.isEmpty()){
            lblErrorSGT.setText("Số giấy tờ đã tồn tại!");
            isValid = false;
        }else if(khachHang_ctrl.kiemTraTrungSoGiayTo(soGiayTo)){
            lblErrorSGT.setText("Số giấy tờ đã tồn tại!");
            isValid = false;
        }
        else{
            lblErrorSGT.setText("");
        }
        if(!email.isEmpty() && !khachHang_ctrl.isValidEmail(email)){
            lblErrorEmail.setText("Email không hợp lệ!");
            isValid = false;
        } else {
            lblErrorEmail.setText("");
        }
        if(!diaChi.isEmpty() && !khachHang_ctrl.isValidDiaChi(diaChi)){
            lblErrorDiaChi.setText("Địa chỉ không hợp lệ!");
            isValid = false;
        } else {
            lblErrorDiaChi.setText("");
        }
        return isValid;
    }

    //thêm khách hàng
    public boolean themKhachHang(KhachHang kh){
        if(!isValidForm()) return false;
        if(khachHang_ctrl.themKhachHang(kh)){
            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!!!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    //tìm kiếm khách hàng bằng sdt
    public KhachHang timKiemKhachHangTheoSDT(String sdt){
        KhachHang kh = khachHang_ctrl.timKiemKhachHang(sdt);
        tableModel.setRowCount(0);
        if(kh != null){
            tableModel.addRow(new Object[]{
                    kh.getKhachHangID(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getEmail(),
                    kh.getSoGiayTo(),
                    kh.getDiaChi(),
                    kh.getLoaiDoiTuong(),
                    kh.getLoaiKhachHang()
            });
            return kh;
        }else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }
    //tìm kiếm khách hàng bằng số giấy tờ
    public KhachHang timKiemKhachHangTheoSGT(String sgt){
        KhachHang kh = khachHang_ctrl.timKiemKhachHangTheoSoGiayTo(sgt);
        if(kh != null){
            tableModel.addRow(new Object[]{
                    kh.getKhachHangID(),
                    kh.getHoTen(),
                    kh.getSoDienThoai(),
                    kh.getEmail(),
                    kh.getSoGiayTo(),
                    kh.getDiaChi(),
                    kh.getLoaiDoiTuong(),
                    kh.getLoaiKhachHang()
            });
            return kh;
        }else{
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số giấy tờ: " + sgt, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
    }

    //clean txtField
    public void clearInputFields() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtSoGiayTo.setText("");
        txtDiaChi.setText("");
        cbLDT.setSelectedIndex(0);
        cbLKH.setSelectedIndex(0);

    }
    public void actionPerformed(ActionEvent e) {
        String maKH = khachHang_ctrl.taoMaKhachHang();
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String soGiayTo = txtSoGiayTo.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String ldtStr = cbLDT.getSelectedItem().toString();
        String lkhStr = cbLKH.getSelectedItem().toString();
        KhachHang kh = new KhachHang(maKH, tenKH, sdt, email, soGiayTo, diaChi, LoaiDoiTuong.valueOf(ldtStr), LoaiKhachHang.valueOf(lkhStr));
        if (e.getSource() == btnAdd) {
            themKhachHang(kh);
        } else if (e.getSource() == btnFind) {
            if (!sdt.isEmpty()) {
                timKiemKhachHangTheoSDT(sdt);
            } else if (!soGiayTo.isEmpty()) {
                timKiemKhachHangTheoSGT(soGiayTo);
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại hoặc số giấy tờ để tìm kiếm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                clearInputFields();
            }
        } else if (e.getSource() == btnClean) {
            clearInputFields();
            loadDataToTable();
            resetErrorLabels();
            lableInfor();
            btnEdit.setText("Sửa");
            Editing = false;
        } else if (e.getSource() == btnEdit) {
            if (!Editing) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow < 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                txtMaKH.setText(table.getValueAt(selectedRow, 0).toString());
                txtTenKH.setText(table.getValueAt(selectedRow, 1).toString());
                txtSDT.setText(table.getValueAt(selectedRow, 2).toString());
                txtEmail.setText(table.getValueAt(selectedRow, 3).toString());
                txtSoGiayTo.setText(table.getValueAt(selectedRow, 4).toString());
                txtDiaChi.setText(table.getValueAt(selectedRow, 5).toString());
                cbLDT.setSelectedItem(LoaiDoiTuong.valueOf(table.getValueAt(selectedRow, 6).toString()));
                cbLKH.setSelectedItem(LoaiKhachHang.valueOf(table.getValueAt(selectedRow, 7).toString()));

                Editing = true;
                btnEdit.setText("Lưu");
            } else {
                if (!khachHang_ctrl.isValidDiaChi(txtDiaChi.getText().trim())) {
                    lblErrorDiaChi.setText("Địa chỉ không hợp lệ!");
                    return;
                }else if(!khachHang_ctrl.isValidEmail(txtEmail.getText().trim())){
                    lblErrorEmail.setText("Email không hợp lệ!");
                    return;
                }else if(!khachHang_ctrl.isValidTen(txtTenKH.getText().trim())){
                    lblErrorTenKH.setText("Tên khách hàng không hợp lệ!");
                    return;
                }else if(!khachHang_ctrl.isValidPhoneNumber(txtSDT.getText().trim())){
                    lblErrorSDT.setText("Số điện thoại không hợp lệ!");
                    return;
                }

                String maKH1 = txtMaKH.getText().trim();
                String tenKH1 = txtTenKH.getText().trim();
                String sdt1 = txtSDT.getText().trim();
                String email1 = txtEmail.getText().trim();
                String soGiayTo1 = txtSoGiayTo.getText().trim();
                String diaChi1 = txtDiaChi.getText().trim();

                LoaiDoiTuong loaiDT1 = (LoaiDoiTuong) cbLDT.getSelectedItem();
                LoaiKhachHang loaiKH1 = (LoaiKhachHang) cbLKH.getSelectedItem();

                KhachHang kh1 = new KhachHang(maKH1, tenKH1, sdt1, email1, soGiayTo1, diaChi1, loaiDT1, loaiKH1);

                if (khachHang_ctrl.capNhatKhachHang(kh1)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    Editing = false;
                    btnEdit.setText("Sửa");
                    clearInputFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
