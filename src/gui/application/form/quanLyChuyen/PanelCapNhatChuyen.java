package gui.application.form.quanLyChuyen;/*
 * @ (#) PanelCapNhatChuyen.java   1.0     10/12/2025
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

public class PanelCapNhatChuyen extends JPanel {
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

    private JComboBox<String> comboChuKy;
    private JCheckBox chkKetThuc;
    private JTextField txtNgayKetThuc;
    private DateChooser dateKetThuc;
    private JPanel pnlChuKy;

    private DefaultTableModel modelLichTrinh;
    private JTable tableLichTrinh;
    private JButton btnCapNhatGa;
    private JButton btnXoaGa;
    private JButton btnCapNhatChuyen;
    private JButton btnCapNhatChang;

    private TimePicker timePicker;


    public PanelCapNhatChuyen(){
        setLayout(new BorderLayout(10,10));
        initComponents();
        setFocusable(false);
        requestFocusInWindow();
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
        comboTuyen.setEditable(false);
        comboTuyen.setFocusable(false);
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
        pnlThongTin.add(pnlGioDi, "growx");

        btnGioDi.addActionListener(e -> {
            timePicker.setDisplayText(txtGioDi);
            timePicker.showPopup(pnlGioDi,0,pnlGioDi.getHeight());
        });

        add(pnlThongTin, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setBackground(Color.WHITE);

        JPanel pnlLichTrinh = new JPanel(new BorderLayout(0,10));
        pnlLichTrinh.setBorder(BorderFactory.createTitledBorder("Lịch trình chi tiết:"));
        pnlLichTrinh.setBackground(Color.WHITE);

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

        pnlCenter.add(pnlLichTrinh, BorderLayout.CENTER);

        pnlChuKy = new JPanel(new MigLayout("fillx, insets 10", "[pref!]10[grow]20[pref!]10[grow]", "[]"));
        pnlChuKy.setBackground(Color.WHITE);
        pnlChuKy.setBorder(BorderFactory.createTitledBorder("Thiết lập chu kỳ chạy tàu"));

        pnlChuKy.add(new JLabel("Chu kỳ lặp:"));
        String[] chuKyOptions = {"Chuyến phát sinh", "Hàng ngày", "Hàng tuần", "Hàng tháng", "Hàng năm"};
        comboChuKy = new JComboBox<>(chuKyOptions);
        pnlChuKy.add(comboChuKy, "w 200!");

        chkKetThuc = new JCheckBox("Ngày kết thúc chu kỳ:");
        chkKetThuc.setBackground(Color.WHITE);
        pnlChuKy.add(chkKetThuc);

        txtNgayKetThuc = new JTextField();
        txtNgayKetThuc.setEnabled(false);
        dateKetThuc = new DateChooser();
        dateKetThuc.setTextRefernce(txtNgayKetThuc);
        dateKetThuc.setDateFormat("dd/MM/yyyy");
        dateKetThuc.addEventDateChooser((action, date)->{
            if(action.getAction() == SelectedAction.DAY_SELECTED){
                dateKetThuc.hidePopup();
            }
        });
        pnlChuKy.add(txtNgayKetThuc, "growx");
        chkKetThuc.addActionListener(e -> {
            txtNgayKetThuc.setEnabled(chkKetThuc.isSelected());
            if (!chkKetThuc.isSelected()) txtNgayKetThuc.setText("");
        });

        pnlCenter.add(pnlChuKy, BorderLayout.SOUTH);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSouth.setBackground(Color.WHITE);
        btnCapNhatChuyen = new JButton("Lưu Chuyến Tàu");
        btnCapNhatChuyen.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCapNhatChuyen.setBackground(new Color(36,104,155));
        btnCapNhatChuyen.setForeground(Color.WHITE);
        btnCapNhatChuyen.setPreferredSize(new Dimension(180,40));

        pnlSouth.add( btnCapNhatChuyen);
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

    public JButton getBtnGioDi() {
        return btnGioDi;
    }

    public void setBtnGioDi(JButton btnGioDi) {
        this.btnGioDi = btnGioDi;
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

    public JButton getBtnCapNhatGa() {
        return btnCapNhatGa;
    }

    public void setBtnCapNhatGa(JButton btnCapNhatGa) {
        this.btnCapNhatGa = btnCapNhatGa;
    }

    public JButton getBtnXoaGa() {
        return btnXoaGa;
    }

    public void setBtnXoaGa(JButton btnXoaGa) {
        this.btnXoaGa = btnXoaGa;
    }

    public JButton getBtnCapNhatChuyen() {
        return btnCapNhatChuyen;
    }

    public void setBtnCapNhatChuyen(JButton btnCapNhatChuyen) {
        this.btnCapNhatChuyen = btnCapNhatChuyen;
    }

    public JButton getBtnCapNhatChang() {
        return btnCapNhatChang;
    }

    public void setBtnCapNhatChang(JButton btnCapNhatChang) {
        this.btnCapNhatChang = btnCapNhatChang;
    }

    public TimePicker getTimePicker() {
        return timePicker;
    }

    public void setTimePicker(TimePicker timePicker) {
        this.timePicker = timePicker;
    }

    public JComboBox<String> getComboChuKy() {
        return comboChuKy;
    }

    public void setComboChuKy(JComboBox<String> comboChuKy) {
        this.comboChuKy = comboChuKy;
    }

    public JCheckBox getChkKetThuc() {
        return chkKetThuc;
    }

    public void setChkKetThuc(JCheckBox chkKetThuc) {
        this.chkKetThuc = chkKetThuc;
    }

    public JTextField getTxtNgayKetThuc() {
        return txtNgayKetThuc;
    }

    public void setTxtNgayKetThuc(JTextField txtNgayKetThuc) {
        this.txtNgayKetThuc = txtNgayKetThuc;
    }

    public DateChooser getDateKetThuc() {
        return dateKetThuc;
    }

    public void setDateKetThuc(DateChooser dateKetThuc) {
        this.dateKetThuc = dateKetThuc;
    }

    public JPanel getPnlChuKy() {
        return pnlChuKy;
    }

    public void setPnlChuKy(JPanel pnlChuKy) {
        this.pnlChuKy = pnlChuKy;
    }
}
