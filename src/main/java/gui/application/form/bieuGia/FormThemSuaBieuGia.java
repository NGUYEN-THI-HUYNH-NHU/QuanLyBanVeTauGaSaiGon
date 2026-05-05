package gui.application.form.bieuGia;

/*
 * @(#) FormThemSuaBieuGia.java  1.0  [8:34:29 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import dto.BieuGiaVeDTO;
import dto.HangToaDTO;
import dto.LoaiTauDTO;
import entity.type.HangToaEnums;
import entity.type.LoaiTauEnums;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class FormThemSuaBieuGia extends JDialog {
    private JComboBox<String> cboTuyenSuggest;
    private JComboBox<String> cboLoaiTau;
    private JComboBox<String> cboHangToa;
    private JTextField txtMinKm;
    private JTextField txtMaxKm;
    private JDateChooser dateBatDau;
    private JDateChooser dateKetThuc;

    private JRadioButton radTheoKm;
    private JRadioButton radCoDinh;
    private JTextField txtDonGiaKm;
    private JTextField txtGiaCoDinh;
    private JTextField txtPhuPhi;
    private JSpinner spinUuTien;
    private JButton btnLuu;
    private JButton btnHuy;

    private String currentID = null;

    public FormThemSuaBieuGia(Frame parent) {
        super(parent, "Thiết lập biểu giá vé", true);
        setSize(900, 540);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());


        // --- CONTENT ---
        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        // --- SECTION A: ĐIỀU KIỆN ---
        addSectionTitle(pnlContent, "A. Điều kiện áp dụng", 0, gbc);

        gbc.gridy = 1;
        cboTuyenSuggest = new JComboBox<>();
        cboTuyenSuggest.setEditable(true);
        addLabelAndComp(pnlContent, "Tuyến áp dụng:", cboTuyenSuggest, 0, 1, gbc);
        addLabelAndComp(pnlContent, "Loại tàu:", cboLoaiTau = new JComboBox<>(),
                2, 1, gbc);

        gbc.gridy = 2;
        addLabelAndComp(pnlContent, "Hạng toa:",
                cboHangToa = new JComboBox<>(), 0, 2, gbc);
        addLabelAndComp(pnlContent, "Khoảng cách (Km):", createKmPanel(), 2, 2, gbc);

        gbc.gridy = 3;
        dateBatDau = new JDateChooser(new Date());
        dateBatDau.setDateFormatString("dd/MM/yyyy");
        dateKetThuc = new JDateChooser();
        dateKetThuc.setDateFormatString("dd/MM/yyyy");
        addLabelAndComp(pnlContent, "Hiệu lực từ:", dateBatDau, 0, 3, gbc);
        addLabelAndComp(pnlContent, "Đến ngày:", dateKetThuc, 2, 3, gbc);

        // --- SECTION B: CÔNG THỨC GIÁ ---
        addSectionTitle(pnlContent, "B. Công thức tính giá", 4, gbc);

        txtDonGiaKm = new JTextField();
        txtGiaCoDinh = new JTextField();
        txtGiaCoDinh.setEnabled(false);
        txtPhuPhi = new JTextField("0");

        radTheoKm = new JRadioButton("Giá theo Km (VNĐ/Km)");
        radTheoKm.setSelected(true);
        radCoDinh = new JRadioButton("Giá trọn gói (VNĐ)");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radTheoKm);
        bg.add(radCoDinh);

        // Logic Radio
        radTheoKm.addActionListener(e -> {
            txtDonGiaKm.setEnabled(true);
            txtGiaCoDinh.setEnabled(false);
        });
        radCoDinh.addActionListener(e -> {
            txtDonGiaKm.setEnabled(false);
            txtGiaCoDinh.setEnabled(true);
        });

        gbc.gridy = 5;
        pnlContent.add(radTheoKm, gbcPos(0, 5, gbc));
        pnlContent.add(txtDonGiaKm, gbcPos(1, 5, gbc));
        pnlContent.add(radCoDinh, gbcPos(2, 5, gbc));
        pnlContent.add(txtGiaCoDinh, gbcPos(3, 5, gbc));

        gbc.gridy = 6;
        addLabelAndComp(pnlContent, "Phụ phí cao điểm:", txtPhuPhi, 0, 6, gbc);

        // --- SECTION C: KHÁC ---
        addSectionTitle(pnlContent, "C. Cấu hình khác", 7, gbc);

        gbc.gridy = 8;
        spinUuTien = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        addLabelAndComp(pnlContent, "Độ ưu tiên (Cao > Thấp):", spinUuTien, 0, 8, gbc);

        add(new JScrollPane(pnlContent), BorderLayout.CENTER);

        // --- FOOTER BUTTONS ---
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlBtn.setBackground(new Color(245, 245, 245));
        pnlBtn.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        btnHuy = new JButton("Hủy bỏ");
        btnLuu = new JButton("Lưu");
        btnLuu.setBackground(new Color(38, 117, 191));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setIcon(new FlatSVGIcon("icon/svg/save.svg", 0.8f));

        pnlBtn.add(btnHuy);
        pnlBtn.add(btnLuu);
        add(pnlBtn, BorderLayout.SOUTH);

        btnHuy.addActionListener(e -> dispose());

        loadEnumData();
    }

    private void loadEnumData() {
        cboLoaiTau.removeAllItems();
        cboLoaiTau.addItem("Tất cả");
        for (LoaiTauEnums lt : LoaiTauEnums.values()) {
            cboLoaiTau.addItem(lt.getDescription());
        }

        cboHangToa.removeAllItems();
        cboHangToa.addItem("Tất cả");
        for (HangToaEnums ht : HangToaEnums.values()) {
            cboHangToa.addItem(ht.getDescription());
        }
    }

    public void loadTuyenData(List<String> listTuyen) {
        cboTuyenSuggest.removeAllItems();
        cboTuyenSuggest.addItem("Tất cả");
        if (listTuyen != null) {
            for (String t : listTuyen) {
                cboTuyenSuggest.addItem(t);
            }
        }
    }

    private LoaiTauDTO getLoaiTauByDescription(String desc) {
        for (LoaiTauEnums lt : LoaiTauEnums.values()) {
            if (lt.getDescription().equals(desc)) {
                return new LoaiTauDTO(lt.name());
            }
        }
        return null;
    }

    private HangToaDTO getHangToaByDescription(String desc) {
        for (HangToaEnums ht : HangToaEnums.values()) {
            if (ht.getDescription().equals(desc)) {
                return new HangToaDTO(ht.name());
            }
        }
        return null;
    }

    private GridBagConstraints gbcPos(int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    private void addSectionTitle(JPanel p, String title, int y, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        lbl.setForeground(new Color(0, 102, 204));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        p.add(lbl, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
    }

    private void addLabelAndComp(JPanel p, String text, Component comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0;
        p.add(new JLabel(text), gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 0.5;
        p.add(comp, gbc);
    }


    private JPanel createKmPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 5, 0));
        p.setOpaque(false);
        txtMinKm = new JTextField("0");
        txtMinKm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Min");
        txtMaxKm = new JTextField("9999");
        txtMaxKm.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Max");
        p.add(txtMinKm);
        p.add(txtMaxKm);
        return p;
    }


    public void addBtnLuuListener(java.awt.event.ActionListener l) {
        btnLuu.addActionListener(l);
    }

    public BieuGiaVeDTO getModelFromForm() {
        BieuGiaVeDTO bg = new BieuGiaVeDTO();
        bg.setId(this.currentID);

        if (cboLoaiTau.getSelectedIndex() > 0) {
            String selectedDesc = cboLoaiTau.getSelectedItem().toString();
            bg.setLoaiTauApDungID(getLoaiTauByDescription(selectedDesc).getId());
        } else {
            bg.setLoaiTauApDungID(null);
        }

        if (cboHangToa.getSelectedIndex() > 0) {
            String selectedDesc = cboHangToa.getSelectedItem().toString();
            bg.setHangToaApDungID(getHangToaByDescription(selectedDesc).getId());
        } else {
            bg.setHangToaApDungID(null);
        }

        Object selectedObj = cboTuyenSuggest.getSelectedItem();
        String rawTuyen = (selectedObj != null) ? selectedObj.toString().trim() : "";

        if (rawTuyen.isEmpty() || rawTuyen.equalsIgnoreCase("Tất cả")) {
            bg.setTuyenApDungID(null);
        } else {
            String tuyenID = rawTuyen;
            if (rawTuyen.contains("(") && rawTuyen.contains(")")) {
                try {
                    tuyenID = rawTuyen.substring(0, rawTuyen.indexOf("(")).trim();
                } catch (Exception e) {
                    tuyenID = rawTuyen;
                }
            }
            bg.setTuyenApDungID(tuyenID);
        }

        try {
            String sMin = txtMinKm.getText().trim();
            String sMax = txtMaxKm.getText().trim();
            bg.setMinKm(sMin.isEmpty() ? 0 : Integer.parseInt(sMin));
            bg.setMaxKm(sMax.isEmpty() ? 0 : Integer.parseInt(sMax));
            bg.setPhuPhiCaoDiem(Double.parseDouble(txtPhuPhi.getText().trim()));

            if (radTheoKm.isSelected()) {
                bg.setDonGiaTrenKm(Double.parseDouble(txtDonGiaKm.getText().trim()));
                bg.setGiaCoBan(0.0);
            } else {
                bg.setGiaCoBan(Double.parseDouble(txtGiaCoDinh.getText().trim()));
                bg.setDonGiaTrenKm(0.0);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Dữ liệu số (Km/Giá) không hợp lệ!");
        }
        bg.setDoUuTien((int) spinUuTien.getValue());

        try {
            bg.setNgayBatDau(toLocalDate(dateBatDau.getDate()));
            bg.setNgayKetThuc(toLocalDate(dateKetThuc.getDate()));
        } catch (Exception e) {
        }

        return bg;
    }

    public void setModelToForm(BieuGiaVeDTO bg) {
        this.currentID = bg.getId();

        if (bg.getLoaiTauApDungID() != null) {
            cboLoaiTau.setSelectedItem(LoaiTauEnums.valueOf(bg.getLoaiTauApDungID()).getDescription());
        } else {
            cboLoaiTau.setSelectedIndex(0);
        }

        if (bg.getHangToaApDungID() != null) {
            cboHangToa.setSelectedItem(HangToaEnums.valueOf(bg.getHangToaApDungID()).getDescription());
        } else {
            cboHangToa.setSelectedIndex(0);
        }

        if (bg.getTuyenApDungID() == null) {
            cboTuyenSuggest.setSelectedItem("Tất cả");
        } else {
            String idCanTim = bg.getTuyenApDungID();
            boolean found = false;
            for (int i = 0; i < cboTuyenSuggest.getItemCount(); i++) {
                String item = cboTuyenSuggest.getItemAt(i);
                if (item.startsWith(idCanTim)) {
                    cboTuyenSuggest.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cboTuyenSuggest.setSelectedItem(idCanTim);
            }
        }

        txtMinKm.setText(String.valueOf(bg.getMinKm()));
        txtMaxKm.setText(String.valueOf(bg.getMaxKm()));
        txtPhuPhi.setText(String.valueOf(bg.getPhuPhiCaoDiem()));

        // 3. Set Radio & Giá
        if (bg.getGiaCoBan() > 0) {
            radCoDinh.setSelected(true);
            txtGiaCoDinh.setEnabled(true);
            txtDonGiaKm.setEnabled(false);
            txtGiaCoDinh.setText(String.valueOf(bg.getGiaCoBan()));
            txtDonGiaKm.setText("");
        } else {
            radTheoKm.setSelected(true);
            txtDonGiaKm.setEnabled(true);
            txtGiaCoDinh.setEnabled(false);
            txtDonGiaKm.setText(String.valueOf(bg.getDonGiaTrenKm()));
            txtGiaCoDinh.setText("");
        }

        // 4. Set Ưu tiên & Ngày
        spinUuTien.setValue(bg.getDoUuTien());
        dateBatDau.setDate(toDate(bg.getNgayBatDau()));
        if (bg.getNgayKetThuc() != null) {
            dateKetThuc.setDate(toDate(bg.getNgayKetThuc()));
        } else {
            dateKetThuc.setEnabled(false);
        }

    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public void enableViewMode() {
        this.setTitle("Chi tiết biểu giá vé");

        btnLuu.setVisible(false);
        btnHuy.setText("Đóng");
        btnHuy.setFocusable(true);

        cboTuyenSuggest.setEditable(false);
        cboTuyenSuggest.setEnabled(false);
        cboTuyenSuggest.setFocusable(false);
        cboLoaiTau.setEnabled(false);
        cboHangToa.setEnabled(false);
        txtMinKm.setEditable(false);
        txtMinKm.setFocusable(false);
        txtMaxKm.setEditable(false);
        txtMaxKm.setFocusable(false);
        dateBatDau.setEnabled(false);
        dateKetThuc.setEnabled(false);

        radTheoKm.setEnabled(false);
        radTheoKm.setFocusable(false);
        radCoDinh.setEnabled(false);
        radCoDinh.setFocusable(false);
        txtDonGiaKm.setEditable(false);
        txtDonGiaKm.setFocusable(false);
        txtGiaCoDinh.setEditable(false);
        txtGiaCoDinh.setFocusable(false);
        txtPhuPhi.setEditable(false);
        txtPhuPhi.setFocusable(false);

        spinUuTien.setEnabled(false);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                btnHuy.requestFocusInWindow();
            }
        });
    }

}