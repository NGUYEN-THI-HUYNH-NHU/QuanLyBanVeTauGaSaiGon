package controller;/*
 * @ (#) QuanLyTuyen_CTRL.java   1.0     30/09/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import bus.Ga_BUS;
import bus.PhanQuyen_BUS;
import bus.Tuyen_BUS;
import entity.type.VaiTroNhanVien;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;

public class QuanLyTuyen_CTRL {
    private final PanelQuanLyTuyen pnlTuyen;
    private final Tuyen_BUS tuyen_bus;
    private Ga_BUS ga_bus;
    private VaiTroNhanVien vaiTroHienTai;

    public QuanLyTuyen_CTRL(PanelQuanLyTuyen pnlTuyen, Tuyen_BUS tuyen_bus){
        this.pnlTuyen = pnlTuyen;
        this.tuyen_bus = tuyen_bus;
        this.ga_bus = new Ga_BUS();
        this.vaiTroHienTai = pnlTuyen.getNhanVienThucHien().getVaiTroNhanVien();
        pnlTuyen.addListeners(new TimKiemListener(),new LamMoiListener());

        PhanQuyen_BUS.phanQuyenQuanLyTuyen(pnlTuyen,vaiTroHienTai);
        thietLapAutoCompleteListener();
    }

    private class TimKiemListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            timKiemTuyen();
        }
    }

    private class LamMoiListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            lamMoiTuyen();
        }
    }

    private void thietLapAutoCompleteListener(){
        taoPopGoiY(pnlTuyen.getTxtGaDi(), pnlTuyen.getPpGaDi(),
                pnlTuyen.getListGaDi(), input -> ga_bus.timTenGaChoGoiY(input));
        taoPopGoiY(pnlTuyen.getTxtGaDen(), pnlTuyen.getPpGaDen(),
                pnlTuyen.getListGaDen(), input -> ga_bus.timTenGaChoGoiY(input));
        taoPopGoiY(pnlTuyen.getTxtTimKiem(), pnlTuyen.getPpTuyenID(),
                pnlTuyen.getListTuyenID(),input -> tuyen_bus.timIDTuyenChoGoiY(input));
    }

    private void timKiemTuyen(){
        String gaDi = pnlTuyen.getTxtGaDi().getText();
        String gaDen = pnlTuyen.getTxtGaDen().getText();
        String maTuyen = pnlTuyen.getTxtTimKiem().getText();

        List<Object[]> ketQuaDuLieuBang;

        if(!maTuyen.trim().isEmpty()){

            ketQuaDuLieuBang = tuyen_bus.getDuLieuBangTheoTuyenID(maTuyen.trim());
        } else {

            ketQuaDuLieuBang = tuyen_bus.getDuLieuBangTheoGa(gaDi, gaDen);
        }

        // Cập nhật bảng với List<Object[]>
        pnlTuyen.capNhatBang(ketQuaDuLieuBang);

        if(ketQuaDuLieuBang.isEmpty()){
            JOptionPane.showMessageDialog(pnlTuyen, "Không tìm thấy tuyến nào!", "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void lamMoiTuyen(){
        pnlTuyen.getTxtGaDi().setText("");
        pnlTuyen.getTxtGaDen().setText("");
        pnlTuyen.getTxtTimKiem().setText("");

        pnlTuyen.capNhatBang(tuyen_bus.getDuLieuBang());
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp,
                             Function<String, List<String>> timKiem){
        String input = txt.getText().trim();
        if(input.length() < 1){
            pp.setVisible(false);
            return;
        }

        List<String> ds = timKiem.apply(input);
        if(ds == null || ds.isEmpty()){
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 8));

        pp.show(txt, 0, txt.getHeight());
    }

    private void taoPopGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem) {
        pp.setFocusable(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        // Hiển thị gợi ý khi input thay đổi
        txt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { hienThiGoiY(txt, lst, pp, timKiem); }
            @Override
            public void removeUpdate(DocumentEvent e) { hienThiGoiY(txt, lst, pp, timKiem); }
            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        // Mouse click chọn item
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = lst.locationToIndex(e.getPoint());
                if (index >= 0) {
                    txt.setText(lst.getModel().getElementAt(index));
                    pp.setVisible(false);
                    timKiemTuyen();
                }
            }
        });

        // Key listener xử lý ↑ ↓, Enter, → ←
        txt.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();
                int selectedIndex = lst.getSelectedIndex();

                switch (key) {
                    case java.awt.event.KeyEvent.VK_DOWN:
                        if (selectedIndex < lst.getModel().getSize() - 1) {
                            lst.setSelectedIndex(selectedIndex + 1);
                            lst.ensureIndexIsVisible(selectedIndex + 1);
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        if (selectedIndex > 0) {
                            lst.setSelectedIndex(selectedIndex - 1);
                            lst.ensureIndexIsVisible(selectedIndex - 1);
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_ENTER:
                        String selected = lst.getSelectedValue();
                        if (selected != null) {
                            txt.setText(selected);
                        }
                        pp.setVisible(false);
                        timKiemTuyen();
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        if (txt == pnlTuyen.getTxtGaDi()) {
                            pnlTuyen.getTxtGaDen().requestFocus();
                        } else if (txt == pnlTuyen.getTxtGaDen()) {
                            pnlTuyen.getTxtTimKiem().requestFocus();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        if (txt == pnlTuyen.getTxtTimKiem()) {
                            pnlTuyen.getTxtGaDen().requestFocus();
                        } else if (txt == pnlTuyen.getTxtGaDen()) {
                            pnlTuyen.getTxtGaDi().requestFocus();
                        }
                        break;
                }
            }
        });

        // Ẩn popup khi mất focus
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                pp.setVisible(false);
            }
        });
    }




}
