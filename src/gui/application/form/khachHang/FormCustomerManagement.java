package gui.application.form.khachHang;

import com.beust.ah.A;
import dao.KhachHang_DAO;
import entity.KhachHang;
import entity.NhanVien;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class FormCustomerManagement extends JPanel implements ActionListener, MouseListener{
    private final KhachHang_DAO khachHang_dao;
    private final NhanVien nhanVienThucHien;


    //demo
    private JTextField txtMaKH;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JTextField txtEmail;
    private JButton btnAdd;
    private JButton btnEdit;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtDiaChi;
    private JButton btnFind;
    private JButton btnClean;
    private JLabel lblErrorMaKH;
    private JLabel lblErrorDiaChi;
    private JLabel lblErrorEmail;
    private JLabel lblErrorSDT;
    private JLabel lblErrorTenKH;
    private TitledBorder titleBorder;
    //demo

    public FormCustomerManagement(NhanVien nhanVienThucHien) {
        this.khachHang_dao = new KhachHang_DAO();
        this.nhanVienThucHien = nhanVienThucHien;

        setLayout(new BorderLayout());

        initComponents();
        loadDataToTable();

        //add action listener
        btnAdd.addActionListener(this);
        btnEdit.addActionListener(this);
        btnFind.addActionListener(this);
        btnClean.addActionListener(this);
        table.addMouseListener(this);




    }
    public JButton createButtonWithIcon(String text, String iconPath) {
        JButton button = new JButton(text);
        java.net.URL iconURL = getClass().getResource(iconPath);
        if(iconURL != null){
            ImageIcon icon = new ImageIcon(iconURL);
            Image img = icon.getImage();
            Image resizedImg = img.getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(resizedImg);
            button.setIcon(icon);
        } else {
            System.err.println("Icon not found: " + iconPath);
        }

        button.setFont(new Font("Roboto", Font.BOLD, 14));

        button.setBackground(new Color(173, 216, 230)); // Light blue background
        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);
        button.setBorderPainted(false);


        return button;
    }

    public void initComponents() {;

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //co mau
        Font font_text = new Font("Roboto", Font.PLAIN, 14);

        //panel input
        JPanel panelTop = new JPanel(new BorderLayout(5, 5));
        JPanel panelInput = new JPanel(new GridLayout(5, 2, 10, 10));
        JLabel lable = new JLabel("Thông tin khách hàng");
        lable.setFont(font_text);
        panelInput.setBorder(BorderFactory.createTitledBorder(lable.getText()));
        

        //cac input
        panelInput.add(new JLabel("Mã khách hàng:"));
        txtMaKH = new JTextField();
        txtMaKH.setEnabled(false);
        txtMaKH.setFont(font_text);
        lblErrorMaKH = new JLabel();
        lblErrorMaKH.setForeground(Color.RED);
        panelInput.add(txtMaKH);
        panelInput.add(lblErrorMaKH);

        panelInput.add(new JLabel("Tên khách hàng:"));
        txtTenKH = new JTextField();
        txtTenKH.setFont(font_text);
        lblErrorTenKH = new JLabel();
        lblErrorTenKH.setForeground(Color.RED);
        panelInput.add(txtTenKH);
        panelInput.add(lblErrorTenKH);

        panelInput.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        txtSDT.setFont(font_text);
        lblErrorSDT = new JLabel();
        lblErrorSDT.setForeground(Color.RED);
        panelInput.add(txtSDT);
        panelInput.add(lblErrorSDT);

        panelInput.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        txtEmail.setFont(font_text);
        lblErrorEmail = new JLabel();
        lblErrorEmail.setForeground(Color.RED);
        panelInput.add(txtEmail);
        panelInput.add(lblErrorEmail);

        panelInput.add(new JLabel("Địa chỉ:"));
        txtDiaChi = new JTextField();
        txtDiaChi.setFont(font_text);
        lblErrorDiaChi = new JLabel();
        lblErrorDiaChi.setForeground(Color.RED);
        panelInput.add(txtDiaChi);
        panelInput.add(lblErrorDiaChi);


        panelTop.add(panelInput, BorderLayout.CENTER);

        //cac button
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = createButtonWithIcon("Thêm", "/gui/icon/png/save.png");
        btnEdit = createButtonWithIcon("Sửa", "/gui/icon/png/update.png");
        btnFind = createButtonWithIcon("Tìm kiếm", "/gui/icon/png/find.png");
        btnClean = createButtonWithIcon("Xóa trắng", "/gui/icon/png/clean.png");

        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnFind);
        panelButtons.add(btnClean);
        panelTop.add(panelButtons, BorderLayout.SOUTH);
        panel.add(panelTop, BorderLayout.NORTH);

        //table
        String[] columnNames = {"Mã KH", "Tên KH", "SĐT", "Email", "Địa chỉ"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 100, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Roboto", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        JLabel title = new JLabel("Danh sách khách hàng");
        title.setFont(new Font("Roboto", Font.BOLD, 16));
        titleBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(30, 100, 150)), title.getText());
        titleBorder.setTitleFont(title.getFont());
        scrollPane.setBorder(titleBorder);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
    }

    // load data len bang
    public void loadDataToTable(){
        List<KhachHang> dsKH = khachHang_dao.getAllKhachHang();
        tableModel.setRowCount(0);
        for(KhachHang kh : dsKH){
            Object[] rowData = {
                kh.getKhachHangID(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getEmail(),
                kh.getDiaChi()
            };
            tableModel.addRow(rowData);
        }
    }

    //them khach hang
    public void themKhachHang(KhachHang kh){
        //tu dong sinh ma khach hang
        String maKH = sinhMaKhachHang();
        kh.setKhachHangID(maKH);

        if(khachHang_dao.themKhachHang(kh)){
            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!!!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //sua khach hang
    public void suaKhachHang(KhachHang kh){
        if(khachHang_dao.capNhatKhachHang(kh)){
            loadDataToTable();
            JOptionPane.showMessageDialog(this, "Sửa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Sửa khách hàng thất bại!!!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    //tim kiem khach hang
    public KhachHang timKiemKhachHang(String sdt){
        KhachHang kh = khachHang_dao.timKhachHangTheoSDT(sdt);
        tableModel.setRowCount(0);
        if(kh != null){
            Object[] rowData = {
                kh.getKhachHangID(),
                kh.getHoTen(),
                kh.getSoDienThoai(),
                kh.getEmail(),
                kh.getDiaChi()
            };
            tableModel.addRow(rowData);
        }else{
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        return kh;
    }

    //click table
    public void clickTable(int rowIndex){
        if(rowIndex >= 0 && rowIndex < tableModel.getRowCount()){
            txtMaKH.setText(tableModel.getValueAt(rowIndex, 0).toString());
            txtTenKH.setText(tableModel.getValueAt(rowIndex, 1).toString());
            txtSDT.setText(tableModel.getValueAt(rowIndex, 2).toString());
            txtEmail.setText(tableModel.getValueAt(rowIndex, 3).toString());
            txtDiaChi.setText(tableModel.getValueAt(rowIndex, 4).toString());
        }
    }
    //kiem tra sdt
    public boolean checkSDT(String num){
        num = txtSDT.getText().trim();
        for(KhachHang kh : khachHang_dao.getAllKhachHang()){
            if(kh.getSoDienThoai().equalsIgnoreCase(num)){
                return false;
            }
        }
        return true;
    }


    //xu ly regex
    public boolean isValidName(String name){
        //hỗ trợ tiếng việt
        String regex = "^([A-ZÀ-Ỹ][a-zà-ỹ]*)(\\s[A-ZÀ-Ỹ][a-zà-ỹ]*)*$";
        return name.matches(regex);
    }
    public boolean isValidPhoneNumber(String phoneNumber){
        String regex = "^(0[35789][0-9]{8})$";
        return phoneNumber.matches(regex);
    }
    public boolean isValidEmail(String email){
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }
    public boolean isValidAddress(String address){
        //hỗ trợ tiếng việt
        String regex = "^[a-zA-Z0-9À-ỹ\\s,.-]+$";
        return address.matches(regex);
    }
    public boolean isValidForm(){
//        resetErrorLabels();
        boolean isValid = true;

        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        if(tenKH.isEmpty() || !isValidName(tenKH)){
            lblErrorTenKH.setText("Tên khách hàng không hợp lệ!");
            isValid = false;
        } else {
            lblErrorTenKH.setText("");
        }
        if(sdt.isEmpty() || !isValidPhoneNumber(sdt)){
            lblErrorSDT.setText("Số điện thoại không hợp lệ! ");
            isValid = false;
        } else if(!checkSDT(sdt)){
            lblErrorSDT.setText("Số điện thoại đã tồn tại!");
            isValid = false;

        }
        else {
            lblErrorSDT.setText("");
        }
        if(!email.isEmpty() && !isValidEmail(email)){
            lblErrorEmail.setText("Email không hợp lệ!");
            isValid = false;
        } else {
            lblErrorEmail.setText("");
        }
        if(!diaChi.isEmpty() && !isValidAddress(diaChi)){
            lblErrorDiaChi.setText("Địa chỉ không hợp lệ!");
            isValid = false;
        } else {
            lblErrorDiaChi.setText("");
        }
        return isValid;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnAdd){
            resetErrorLabels();
            String tenKH = txtTenKH.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            String diaChi = txtDiaChi.getText().trim();

            if(tenKH.isEmpty() || sdt.isEmpty()){
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                clearInputFields();
                return;
            }
            if(!isValidForm()){
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại thông tin khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            KhachHang kh = new KhachHang("", tenKH, sdt, email, diaChi);
            themKhachHang(kh);

        } else if(e.getSource() == btnEdit){
            String maKH = txtMaKH.getText().trim();
            String tenKH = txtTenKH.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            String diaChi = txtDiaChi.getText().trim();


            //kiểm tra thông tin mới
            if(!isValidForm()){
                JOptionPane.showMessageDialog(this, "Vui lòng kiểm tra lại thông tin khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            KhachHang kh = new KhachHang(maKH, tenKH, sdt, email, diaChi);
            suaKhachHang(kh);
        } else if(e.getSource() == btnFind){
            String sdt = txtSDT.getText().trim();
            if(sdt.isEmpty()){
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại để tìm kiếm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                clearInputFields();
                return;
            }
            timKiemKhachHang(sdt);
        }else if(e.getSource() == btnClean){
            clearInputFields();
            loadDataToTable();
        }
    }

    //tu dong sinh ma khach hang
    public String sinhMaKhachHang(){
        List<KhachHang> dsKH = khachHang_dao.getAllKhachHang();
        int maxID = 0;
        for(KhachHang kh : dsKH){
            String idStr = kh.getKhachHangID().replace("KH", "");
            try{
                int id = Integer.parseInt(idStr);
                if(id > maxID){
                    maxID = id;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return String.format("KH%02d", maxID + 1);
    }

    //xoa trang cac textfield
    public void clearInputFields(){
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
    }

    //reset error labels
    public void resetErrorLabels(){
        lblErrorMaKH.setText("");
        lblErrorTenKH.setText("");
        lblErrorSDT.setText("");
        lblErrorEmail.setText("");
        lblErrorDiaChi.setText("");
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == table){
            int selectedRow = table.getSelectedRow();
            clickTable(selectedRow);
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
}
