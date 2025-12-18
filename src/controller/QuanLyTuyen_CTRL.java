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
import entity.Tuyen;
import entity.type.VaiTroNhanVien;
import gui.application.UngDung;
import gui.application.form.quanLyChuyen.PanelCapNhatChuyen;
import gui.application.form.quanLyChuyen.PanelThemChuyen;
import gui.application.form.quanLyTuyen.PanelCapNhatTuyen;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Function;

public class QuanLyTuyen_CTRL {
    private final PanelQuanLyTuyen pnlTuyen;
    private final Tuyen_BUS tuyen_bus;
    private Ga_BUS ga_bus;
    private VaiTroNhanVien vaiTroHienTai;

    private PanelThemTuyen panelThemTuyen;
    private PanelCapNhatTuyen panelCapNhatTuyen;
    private JDialog dialogThemTuyen;
    private JDialog dialogCapNhatTuyen;

    public QuanLyTuyen_CTRL(PanelQuanLyTuyen pnlTuyen, Tuyen_BUS tuyen_bus){
        this.pnlTuyen = pnlTuyen;
        this.tuyen_bus = tuyen_bus;
        this.ga_bus = new Ga_BUS();
        this.vaiTroHienTai = pnlTuyen.getNhanVienThucHien().getVaiTroNhanVien();
        pnlTuyen.addListeners(new TimKiemListener(),new LamMoiListener(), new ThemTuyenListener(), new CapNhatTuyenListener());
        pnlTuyen.getTableTuyen().addMouseListener(new TuyenTableListener());

        PhanQuyen_BUS.phanQuyenQuanLyTuyen(pnlTuyen,vaiTroHienTai);
        thietLapAutoCompleteListener();
        thietLapPhimTatF5();
    }

    private boolean thucHienTimKiem() {
        String gaDi = pnlTuyen.getTxtGaDi().getText();
        String gaDen = pnlTuyen.getTxtGaDen().getText();
        String maTuyen = pnlTuyen.getTxtTimKiem().getText();

        List<Object[]> ketQua;

        if (!maTuyen.trim().isEmpty()) {
            ketQua = tuyen_bus.getDuLieuBangTheoTuyenID(maTuyen.trim());
        } else {
            ketQua = tuyen_bus.getDuLieuBangTheoGa(gaDi, gaDen);
        }

        pnlTuyen.capNhatBang(ketQua);
        return ketQua != null && !ketQua.isEmpty();
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

    private class TuyenTableListener extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e){
            if(e.getClickCount() == 1){
                hienThiChiTietTuyenDaChon();
            }
        }
    }

    private class ThemTuyenListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            hienThiManHinhThemTuyen();
        }
    }

    private class CapNhatTuyenListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            hienThiManHinhCapNhatTuyen();
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

    private void hienThiChiTietTuyenDaChon(){
        JTable table = pnlTuyen.getTableTuyen();
        int row = table.getSelectedRow();

        if(row == -1) return;
        int modelRow = table.convertRowIndexToModel(row);

        Object objID = table.getValueAt(modelRow, 0);
        String tuyenID = (objID != null) ? objID.toString() : "";

        Tuyen tuyen = tuyen_bus.getTuyenTheoMa(tuyenID);

        List<Object[]> dsGaChiTiet = tuyen_bus.getDuLieuGaTrungGianChiTiet(tuyenID);

        if (tuyen != null) {
            Object objKC = table.getValueAt(modelRow, 3);
            String khoangCach = (objKC != null) ? objKC.toString() + " km" : "";

            Object objTrangThai = table.getValueAt(modelRow, 4);
            String trangThai = (objTrangThai != null) ? objTrangThai.toString() : "Không xác định";

            Object objTenGaXP = table.getValueAt(modelRow, 1);
            Object objTenGaDen = table.getValueAt(modelRow, 2);
            String tenTuyen = "";
            if (objTenGaXP != null && objTenGaDen != null) {
                tenTuyen = objTenGaXP.toString() + " - " + objTenGaDen.toString();
            }

            pnlTuyen.getTxtChiTietMaTuyen().setText(tuyen.getTuyenID());
            pnlTuyen.getTxtChiTietTenTuyen().setText(tenTuyen);
            pnlTuyen.getTxtChiTietKhoangCach().setText(khoangCach);
            pnlTuyen.getTxtChiTietMoTa().setText(tuyen.getMoTa());

            if (pnlTuyen.getTxtChiTietTrangThai() != null) {
                pnlTuyen.getTxtChiTietTrangThai().setText(trangThai);
            }
        }

        DefaultTableModel modelChiTiet = pnlTuyen.getModelChiTietGa();
        modelChiTiet.setRowCount(0);

        int stt = 1;
        for(Object[] rowData : dsGaChiTiet){
            modelChiTiet.addRow(new Object[]{ stt++, rowData[0], rowData[1], rowData[2] });
        }
    }

    private void hienThiManHinhThemTuyen(){
        if(dialogThemTuyen == null) {
            panelThemTuyen = new PanelThemTuyen(pnlTuyen.getNhanVienThucHien());
            dialogThemTuyen = new JDialog(SwingUtilities.getWindowAncestor(pnlTuyen), "Thêm Tuyến Đường Sắt Mới", Dialog.ModalityType.APPLICATION_MODAL);

            dialogThemTuyen.setContentPane(panelThemTuyen);
            dialogThemTuyen.setSize(1000, 700);
            dialogThemTuyen.setLocationRelativeTo(pnlTuyen);
            dialogThemTuyen.setResizable(false);

            new ThemTuyen_CTRL(panelThemTuyen, dialogThemTuyen);

            dialogThemTuyen.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    lamMoiTuyen();
                }
            });
        }
        dialogThemTuyen.setVisible(true);
    }

    private void hienThiManHinhCapNhatTuyen(){
        int row = pnlTuyen.getTableTuyen().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(pnlTuyen, "Vui lòng chọn tuyến cần cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maTuyen = pnlTuyen.getTableTuyen().getValueAt(row, 0).toString();

        panelCapNhatTuyen = new PanelCapNhatTuyen(pnlTuyen.getNhanVienThucHien());

        dialogCapNhatTuyen = new JDialog(SwingUtilities.getWindowAncestor(pnlTuyen), "Cập Nhật Tuyến Đường Sắt", Dialog.ModalityType.APPLICATION_MODAL);
        dialogCapNhatTuyen.setContentPane(panelCapNhatTuyen);
        dialogCapNhatTuyen.setSize(1100, 750);
        dialogCapNhatTuyen.setLocationRelativeTo(pnlTuyen);
        dialogCapNhatTuyen.setResizable(false);

        new CapNhatTuyen_CTRL(panelCapNhatTuyen, dialogCapNhatTuyen, maTuyen);

        dialogCapNhatTuyen.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                lamMoiTuyen();
            }
        });

        dialogCapNhatTuyen.setVisible(true);
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp,
                             Function<String, List<String>> timKiem){
        String input = txt.getText().trim();

        List<String> ds = timKiem.apply(input);

        if(ds == null || ds.isEmpty()){
            pp.setVisible(false);
            return;
        }

        if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 8));

        pp.setPopupSize(txt.getWidth(), pp.getPreferredSize().height);

        if(txt.isShowing()){
            pp.show(txt, 0, txt.getHeight());
            txt.requestFocus();
        }
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
                    thucHienTimKiem();
                    chuyenFocusSauChon(txt);
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
                        else if(txt == pnlTuyen.getTxtGaDi()){
                            pnlTuyen.getTxtGaDen().requestFocus();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        if (selectedIndex > 0) {
                            lst.setSelectedIndex(selectedIndex - 1);
                            lst.ensureIndexIsVisible(selectedIndex - 1);
                        }
                        else if(txt == pnlTuyen.getTxtGaDen()){
                            pnlTuyen.getTxtGaDi().requestFocus();
                        }
                        break;
                    case java.awt.event.KeyEvent.VK_ENTER:
                        String selected = lst.getSelectedValue();
                        if (selected != null) {
                            txt.setText(selected);
                        }
                        pp.setVisible(false);
                        thucHienTimKiem();
                        chuyenFocusSauChon(txt);
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                         if (txt == pnlTuyen.getTxtGaDen()) {
                            pnlTuyen.getTxtTimKiem().requestFocus();
                        }
                         else if(txt == pnlTuyen.getTxtGaDi()){
                             pnlTuyen.getTxtTimKiem().requestFocus();
                         }
                        break;
                    case java.awt.event.KeyEvent.VK_LEFT:
                        if (txt == pnlTuyen.getTxtTimKiem()) {
                            pnlTuyen.getTxtGaDi().requestFocus();
                        }
                        break;
                }
            }
        });

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                pp.setVisible(false);
            }
        });
    }
    private void thietLapPhimTatF5() {
        JComponent root = pnlTuyen;

        InputMap im = root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "LAM_MOI_TUYEN");

        am.put("LAM_MOI_TUYEN", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiTuyen();
            }
        });
    }


    private void chuyenFocusSauChon(JTextField txt) {
        if (txt == pnlTuyen.getTxtGaDi()) {
            pnlTuyen.getTxtGaDen().requestFocus();
        } else if (txt == pnlTuyen.getTxtGaDen()) {
            pnlTuyen.getTxtTimKiem().requestFocus();
        } else if (txt == pnlTuyen.getTxtTimKiem()) {
            txt.selectAll(); // hoặc không làm gì
        }
    }

}
