package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2.java  1.0  [10:39:25 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import entity.Chuyen;

public class PanelBuoc2 extends JPanel {
    private JPanel pnlThongTinChuyen, pnlSoDoGhe, pnlDieuHuong;
    private JTextField txtTenChuyen, txtToa, txtNgay, txtGio;
    private JButton btnQuayLai, btnTiepTuc;
    private List<JToggleButton> danhSachGhe;

    public PanelBuoc2() {
        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Chọn ghế"));

        // Panel thông tin chuyến
        pnlThongTinChuyen = new TrainListPanel();
        
        add(pnlThongTinChuyen, BorderLayout.NORTH);

        // Panel sơ đồ ghế
        pnlSoDoGhe = new JPanel(new GridLayout(5, 4, 10, 10));
        danhSachGhe = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            JToggleButton ghe = new JToggleButton("G" + String.format("%02d", i));
            ghe.setBackground(Color.LIGHT_GRAY);
            ghe.setFont(new Font("Arial", Font.PLAIN, 14));
            danhSachGhe.add(ghe);
            pnlSoDoGhe.add(ghe);
        }

        add(pnlSoDoGhe, BorderLayout.CENTER);

        // Panel điều hướng
        pnlDieuHuong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnQuayLai = new JButton("Quay lại");
        btnTiepTuc = new JButton("Tiếp tục");
        pnlDieuHuong.add(btnQuayLai);
        pnlDieuHuong.add(btnTiepTuc);

        add(pnlDieuHuong, BorderLayout.SOUTH);
    }

    // Getter cho controller
    public JButton getBtnQuayLai() { return btnQuayLai; }
    public JButton getBtnTiepTuc() { return btnTiepTuc; }
    public List<JToggleButton> getDanhSachGhe() { return danhSachGhe; }

    public void setThongTinChuyen(String tenChuyen, String toa, String ngay, String gio) {
        txtTenChuyen.setText(tenChuyen);
        txtToa.setText(toa);
        txtNgay.setText(ngay);
        txtGio.setText(gio);
    }

    public List<String> getGheDaChon() {
        List<String> gheChon = new ArrayList<>();
        for (JToggleButton ghe : danhSachGhe) {
            if (ghe.isSelected()) {
                gheChon.add(ghe.getText());
            }
        }
        return gheChon;
    }

    public void setGheDaDat(List<String> gheDaDat) {
        for (JToggleButton ghe : danhSachGhe) {
            if (gheDaDat.contains(ghe.getText())) {
                ghe.setEnabled(false);
                ghe.setBackground(Color.RED);
            }
        }
    }

	/**
	 * @param results
	 */
	public void loadSearchResults(List<Chuyen> results) {
		// TODO Auto-generated method stub
		
	}
}