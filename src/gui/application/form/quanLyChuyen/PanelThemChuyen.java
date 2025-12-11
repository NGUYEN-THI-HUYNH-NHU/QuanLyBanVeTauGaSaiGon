package gui.application.form.quanLyChuyen;/*
 * @ (#) PanelThemChuyen.java   1.0     10/12/2025
package gui.application.form.quanLyChuyen;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 10/12/2025
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.raven.datechooser.DateChooser;
import com.raven.datechooser.SelectedAction;
import com.raven.swing.TimePicker;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelThemChuyen extends JPanel {
    private final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Color COLOR_BG = new Color(245, 250, 255);

    private JTextField txtMaChuyen;
    private JComboBox<String> comboTuyen;
    private JComboBox<String> comboGaXuatPhat;
    private JComboBox<String> comboGaDich;
    private JComboBox<String> comboTau;
    private JTextField txtNgayDi;
    private JTextField txtGioDi;
    private JButton btnGioDi;
    private JComboBox<String> comboGaDiMoi;
    private JComboBox<String> comboGaDenMoi;
    private JTextField txtGioDenMoi;
    private JTextField txtNgayDenMoi;
    private JTextField txtGioDiMoi;
    private JButton btnGioDiMoi;
    private JButton btnGioDenMoi;
    private JTextField txtNgayDiMoi;
    private DefaultTableModel modelLichTrinh;
    private JTable tableLichTrinh;
    private JButton btnThemGa;
    private JButton btnXoaGa;
    private JButton btnThemChuyen;

    private TimePicker timePicker;

    public PanelThemChuyen() {
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents(){
        timePicker = new TimePicker();
        timePicker.set24hourMode(true);

        JPanel pnlThongTin = new JPanel(new MigLayout("fillx, insets 20", "[pref!]10[grow]20[pref!]10[grow]", "[]15[]15[]15[]"));
        pnlThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến"));
        pnlThongTin.setBackground(Color.WHITE);
        pnlThongTin.setBackground(COLOR_BG);

        pnlThongTin.add(new JLabel("Mã chuyến:"));
        txtMaChuyen = new JTextField();
        txtMaChuyen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã chuyến được tạo tự động!");
        txtMaChuyen.setEditable(false);
        txtMaChuyen.setFocusable(false);
        txtMaChuyen.setBackground(new Color(240, 240, 240));
        pnlThongTin.add(txtMaChuyen, "growx");

        pnlThongTin.add(new JLabel("Tuyến:"));
        comboTuyen = new JComboBox<>();
        pnlThongTin.add(comboTuyen, "growx, wrap");

        pnlThongTin.add(new JLabel("Mã Tàu:"));
        comboTau = new JComboBox<>();
        pnlThongTin.add(comboTau, "growx");

        pnlThongTin.add(new JLabel("Ngày Đi:"));
        txtNgayDi = new JTextField();
        DateChooser dateChooser = new DateChooser();
        dateChooser.setTextRefernce(txtNgayDi);
        dateChooser.setDateFormat("dd/MM/yyyy");
        dateChooser.addEventDateChooser((action, date)->{
            if(action.getAction() == SelectedAction.DAY_SELECTED){
                dateChooser.hidePopup();
            }
        });
        pnlThongTin.add(txtNgayDi, "growx, wrap");

        pnlThongTin.add(new JLabel("Ga Xuất Phát:"));
        comboGaXuatPhat = new JComboBox<>();
        pnlThongTin.add(comboGaXuatPhat, "growx");

        pnlThongTin.add(new JLabel("Ga Đích:"));
        comboGaDich = new JComboBox<>();
        pnlThongTin.add(comboGaDich, "growx, wrap");

        pnlThongTin.add(new JLabel("Giờ Đi:"));
        JPanel pnlGioDi = new JPanel(new BorderLayout());
        pnlGioDi.setBackground(Color.WHITE);


        txtGioDi = new JTextField();
        txtGioDi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập giờ đi (HH:mm)");
        btnGioDi = new JButton("Chọn Giờ");
        btnGioDi.setBackground(new Color(36,104,155));
        btnGioDi.setForeground(Color.WHITE);
        btnGioDi.setMargin(new Insets(2,5,2,5));
        pnlGioDi.add(txtGioDi, BorderLayout.CENTER);
        pnlGioDi.add(btnGioDi, BorderLayout.EAST);
        pnlThongTin.add(pnlGioDi, "growx, wrap");

        btnGioDi.addActionListener(e -> {
            timePicker.setDisplayText(txtGioDi);
            timePicker.showPopup(pnlGioDi,0,pnlGioDi.getHeight());
        });

        add(pnlThongTin, BorderLayout.NORTH);

        JPanel pnlLichTrinh = new JPanel(new BorderLayout(0,10));
        pnlLichTrinh.setBorder(BorderFactory.createTitledBorder("Lịch trình chi tiết:"));
        pnlLichTrinh.setBackground(Color.WHITE);

        JPanel pnlNhapGa = new JPanel(new MigLayout("fillx", "[pref!]5[grow]10[pref!]5[grow]10[pref!]5[grow]", "[]"));
        pnlNhapGa.setBackground(Color.WHITE);

        comboGaDiMoi = new JComboBox<>();
        comboGaDenMoi = new JComboBox<>();
        pnlNhapGa.add(new JLabel("Ga Đi:"));
        pnlNhapGa.add(comboGaDiMoi, "growx");
        pnlNhapGa.add(new JLabel("Ga Đến:"));
        pnlNhapGa.add(comboGaDenMoi, "growx, wrap");

        txtNgayDiMoi = new JTextField();
        DateChooser dc1 = new DateChooser();
        dc1.setTextRefernce(txtNgayDiMoi);
        dc1.setDateFormat("dd/MM/yyyy");
        dc1.addEventDateChooser((action, date)->{
            if(action.getAction() == SelectedAction.DAY_SELECTED){
                dc1.hidePopup();
            }
        });

        JPanel pnlGioDiMoi = new JPanel(new BorderLayout());
        txtGioDiMoi = new JTextField();
        txtGioDiMoi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập giờ đi (HH:mm)");
        btnGioDiMoi = new JButton("Chọn Giờ");
        btnGioDiMoi.setMargin(new Insets(2,2,2,2));
        btnGioDiMoi.setBackground(new Color(36,104,155));
        btnGioDiMoi.setForeground(Color.WHITE);
        pnlGioDiMoi.add(txtGioDiMoi, BorderLayout.CENTER);
        pnlGioDiMoi.add(btnGioDiMoi, BorderLayout.EAST);

        btnGioDiMoi.addActionListener(e -> {
            timePicker.setDisplayText(txtGioDiMoi);
            timePicker.showPopup(pnlGioDiMoi,0,pnlGioDiMoi.getHeight());
        });

        pnlNhapGa.add(new JLabel("Ngày Đi:"));
        pnlNhapGa.add(txtNgayDiMoi, "growx");
        pnlNhapGa.add(new JLabel("Giờ Đi:"));
        pnlNhapGa.add(pnlGioDiMoi, "growx, wrap");

        txtNgayDenMoi = new JTextField();
        DateChooser dc2 = new DateChooser();
        dc2.setTextRefernce(txtNgayDenMoi);
        dc2.setDateFormat("dd/MM/yyyy");
        dc2.addEventDateChooser((action, date)->{
            if(action.getAction() == SelectedAction.DAY_SELECTED){
                dc2.hidePopup();
            }
        });
        JPanel pnlGioDenMoi = new JPanel(new BorderLayout());
        txtGioDenMoi = new JTextField();
        txtGioDenMoi.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập giờ đến (HH:mm)");
        btnGioDenMoi = new JButton("Chọn Giờ");
        btnGioDenMoi.setMargin(new Insets(2,2,2,2));
        btnGioDenMoi.setBackground(new Color(36,104,155));
        btnGioDenMoi.setForeground(Color.WHITE);
        pnlGioDenMoi.add(txtGioDenMoi, BorderLayout.CENTER);
        pnlGioDenMoi.add(btnGioDenMoi, BorderLayout.EAST);

        btnGioDenMoi.addActionListener(e -> {
            timePicker.setDisplayText(txtGioDenMoi);
            timePicker.showPopup(pnlGioDenMoi,0,pnlGioDenMoi.getHeight());
        });
        pnlNhapGa.add(new JLabel("Ngày Đến:"));
        pnlNhapGa.add(txtNgayDenMoi, "growx");
        pnlNhapGa.add(new JLabel("Giờ Đến:"));
        pnlNhapGa.add(pnlGioDenMoi, "growx");

        btnThemGa = new JButton("Thêm Chặng");
        btnThemGa.setBackground(new Color(36,104,155));
        btnThemGa.setForeground(Color.WHITE);
        btnThemGa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXoaGa = new JButton("Xóa Chặng");
        btnXoaGa.setBackground(new Color(36,104,155));
        btnXoaGa.setForeground(Color.WHITE);
        btnXoaGa.setFont(new Font("Segoe UI", Font.BOLD, 14));

        pnlNhapGa.add(btnThemGa, "w 100!, h 30!");
        pnlNhapGa.add(btnXoaGa, "w 100!, h 30!");

        pnlLichTrinh.add(pnlNhapGa, BorderLayout.NORTH);

        String[] cols = {"STT", "Ga Đi","Ngày Đi","Giờ Đi","Ga Đến", "Ngày Đến", "Giờ Đến"};
        modelLichTrinh = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableLichTrinh = new JTable(modelLichTrinh);
        tableLichTrinh.setShowGrid(true);
        tableLichTrinh.setShowHorizontalLines(true);
        tableLichTrinh.setShowVerticalLines(true);
        tableLichTrinh.setGridColor(Color.LIGHT_GRAY);
        tableLichTrinh.setRowHeight(25);
        tableLichTrinh.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableLichTrinh.getTableHeader().setBackground(new Color(36,104,155));
        tableLichTrinh.getTableHeader().setForeground(Color.WHITE);
        tableLichTrinh.getColumnModel().getColumn(0).setMaxWidth(40);
        pnlLichTrinh.add(new JScrollPane(tableLichTrinh), BorderLayout.CENTER);

        add(pnlLichTrinh, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.setBackground(Color.WHITE);
        btnThemChuyen = new JButton("Lưu Chuyến Tàu");
        btnThemChuyen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThemChuyen.setBackground(new Color(36,104,155));
        btnThemChuyen.setForeground(Color.WHITE);
        btnThemChuyen.setPreferredSize(new Dimension(180,40));

        pnlSouth.add(btnThemChuyen);
        add(pnlSouth, BorderLayout.SOUTH);
    }

    public Font getBASE_FONT() {
        return BASE_FONT;
    }

    public Color getCOLOR_BG() {
        return COLOR_BG;
    }

    public JTextField getTxtMaChuyen() {
        return txtMaChuyen;
    }

    public void setTxtMaChuyen(JTextField txtMaChuyen) {
        this.txtMaChuyen = txtMaChuyen;
    }

    public JComboBox<String> getComboTuyen() {
        return comboTuyen;
    }

    public void setComboTuyen(JComboBox<String> comboTuyen) {
        this.comboTuyen = comboTuyen;
    }

    public JComboBox<String> getComboGaXuatPhat() {
        return comboGaXuatPhat;
    }

    public void setComboGaXuatPhat(JComboBox<String> comboGaXuatPhat) {
        this.comboGaXuatPhat = comboGaXuatPhat;
    }

    public JComboBox<String> getComboGaDich() {
        return comboGaDich;
    }

    public void setComboGaDich(JComboBox<String> comboGaDich) {
        this.comboGaDich = comboGaDich;
    }

    public JComboBox<String> getComboTau() {
        return comboTau;
    }

    public void setComboTau(JComboBox<String> comboTau) {
        this.comboTau = comboTau;
    }

    public JTextField getTxtNgayDi() {
        return txtNgayDi;
    }

    public void setTxtNgayDi(JTextField txtNgayDi) {
        this.txtNgayDi = txtNgayDi;
    }

    public JTextField getTxtGioDi() {
        return txtGioDi;
    }

    public void setTxtGioDi(JTextField txtGioDi) {
        this.txtGioDi = txtGioDi;
    }

    public JComboBox<String> getComboGaDiMoi() {
        return comboGaDiMoi;
    }

    public void setComboGaDiMoi(JComboBox<String> comboGaDiMoi) {
        this.comboGaDiMoi = comboGaDiMoi;
    }

    public JComboBox<String> getComboGaDenMoi() {
        return comboGaDenMoi;
    }

    public void setComboGaDenMoi(JComboBox<String> comboGaDenMoi) {
        this.comboGaDenMoi = comboGaDenMoi;
    }

    public JTextField getTxtGioDenMoi() {
        return txtGioDenMoi;
    }

    public void setTxtGioDenMoi(JTextField txtGioDenMoi) {
        this.txtGioDenMoi = txtGioDenMoi;
    }

    public JTextField getTxtNgayDenMoi() {
        return txtNgayDenMoi;
    }

    public void setTxtNgayDenMoi(JTextField txtNgayDenMoi) {
        this.txtNgayDenMoi = txtNgayDenMoi;
    }

    public JTextField getTxtGioDiMoi() {
        return txtGioDiMoi;
    }

    public void setTxtGioDiMoi(JTextField txtGioDiMoi) {
        this.txtGioDiMoi = txtGioDiMoi;
    }

    public JTextField getTxtNgayDiMoi() {
        return txtNgayDiMoi;
    }

    public void setTxtNgayDiMoi(JTextField txtNgayDiMoi) {
        this.txtNgayDiMoi = txtNgayDiMoi;
    }

    public DefaultTableModel getModelLichTrinh() {
        return modelLichTrinh;
    }

    public void setModelLichTrinh(DefaultTableModel modelLichTrinh) {
        this.modelLichTrinh = modelLichTrinh;
    }

    public JTable getTableLichTrinh() {
        return tableLichTrinh;
    }

    public void setTableLichTrinh(JTable tableLichTrinh) {
        this.tableLichTrinh = tableLichTrinh;
    }

    public JButton getBtnThemGa() {
        return btnThemGa;
    }

    public void setBtnThemGa(JButton btnThemGa) {
        this.btnThemGa = btnThemGa;
    }

    public JButton getBtnXoaGa() {
        return btnXoaGa;
    }

    public void setBtnXoaGa(JButton btnXoaGa) {
        this.btnXoaGa = btnXoaGa;
    }

    public JButton getBtnThemChuyen() {
        return btnThemChuyen;
    }

    public void setBtnThemChuyen(JButton btnThemChuyen) {
        this.btnThemChuyen = btnThemChuyen;
    }
}