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
import gui.application.UngDung;
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
        pnlTuyen.addListeners(new TimKiemListener(),new LamMoiListener(), new ThemTuyenListener(), new CapNhatTuyenListener());
        pnlTuyen.getTableTuyen().addMouseListener(new TuyenTableListener());

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

    private void hienThiChiTietTuyenDaChon(){
        JTable table = pnlTuyen.getTableTuyen();
        int row = table.getSelectedRow();

        if(row == -1) return;

        int modelRow = table.convertRowIndexToModel(row);
        String tuyenID = table.getValueAt(modelRow, 0).toString();
        String thongTinChung = tuyen_bus.getChiTietTuyen(tuyenID);
        List<Object[]> dsGaTrungGian = tuyen_bus.getDuLieuGaTrungGianChiTiet(tuyenID);

        Window owner = SwingUtilities.getWindowAncestor(pnlTuyen);
        JDialog dialog = new JDialog((Frame) owner, "Thông Tin Chi Tiết Tuyến " + tuyenID, Dialog.ModalityType.APPLICATION_MODAL); //chặn tương tác với cửa sổ chính
        dialog.setResizable(false);
        dialog.setLayout(new BorderLayout());

        JTextArea txtThongTinCHung = new JTextArea(thongTinChung);
        txtThongTinCHung.setEditable(false);
        txtThongTinCHung.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        String[] columnNames = { "Tên Ga", "Loại Ga", "Khoảng cách từ ga xuất phát (km)"};
        DefaultTableModel detailModel = new DefaultTableModel(columnNames,0);
        for(Object[] rowData : dsGaTrungGian){
            detailModel.addRow(rowData);
        }

        JTable detailTable = new JTable(detailModel);
        detailTable.setFillsViewportHeight(true);
        detailTable.setRowHeight(25);
        detailTable.setShowGrid(true);
        detailTable.setShowHorizontalLines(true);
        detailTable.setShowVerticalLines(true);
        JTableHeader hd = detailTable.getTableHeader();
//        hd.setOpaque(false);
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) hd.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        hd.setFont(new Font("Times New Roman", Font.BOLD, 14));
        hd.setBackground(new Color(36, 104, 155));
        hd.setForeground(Color.white);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();// căn phait cho cột khoảng cách
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        detailTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        JScrollPane tableScrollPane = new JScrollPane(detailTable);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(txtThongTinCHung), BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        dialog.add(mainPanel, BorderLayout.CENTER);
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,5));
        southPanel.add(btnClose);

        dialog.add(southPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(pnlTuyen);
        dialog.setVisible(true);

    }

    private void hienThiManHinhThemTuyen(){
        PanelThemTuyen panelThemTuyen = new PanelThemTuyen(pnlTuyen.getNhanVienThucHien());
        UngDung.showGiaoDienChinh(panelThemTuyen);
    }

    private void hienThiManHinhCapNhatTuyen(){
        PanelCapNhatTuyen panelCapNhatTuyen = new PanelCapNhatTuyen(pnlTuyen.getNhanVienThucHien());
        UngDung.showGiaoDienChinh(panelCapNhatTuyen);
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
                        timKiemTuyen();
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

        // Ẩn popup khi mất focus
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                pp.setVisible(false);
            }
        });
    }
}
