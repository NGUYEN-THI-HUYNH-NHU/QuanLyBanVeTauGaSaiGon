package controller;/*
 * @ (#) CapNhatTuyen_CTRL.java   1.0     16/11/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 16/11/2025
 */

import bus.Ga_BUS;
import bus.Tuyen_BUS;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;
import gui.application.UngDung;
import gui.application.form.quanLyTuyen.PanelCapNhatTuyen;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CapNhatTuyen_CTRL {
    private boolean dangChonTuPopup = false;
    private final PanelCapNhatTuyen panelCapNhatTuyen;
    private final Tuyen_BUS tuyenBus;
    private final Ga_BUS gaBus;
    private final JDialog dialog;
    private final String tuyenID;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;

    private Tuyen tuyenHienTai;

    public CapNhatTuyen_CTRL(PanelCapNhatTuyen panelCapNhatTuyen, JDialog dialog, String tuyenID) {
        this.panelCapNhatTuyen = panelCapNhatTuyen;
        this.dialog = dialog;
        this.tuyenID = tuyenID;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();
        tuyenHienTai = null;

        khoiTaoDuLieuBanDau();
        thietLapListener();
        taiDuLieuTuyen(tuyenID);
    }

    private void khoiTaoDuLieuBanDau(){
        List<String> dsGa = gaBus.getDanhSachTenGa();

        for(String tenGa : dsGa){
            Ga ga = gaBus.getGaByTenGa(tenGa);
            if(ga != null){
                dsGaCoSan.put(tenGa, ga);
            }
        }
    }

    private void thietLapListener(){
        JTextField txtMaTuyen = panelCapNhatTuyen.getTxtMaTuyen();
        JTextField txtGaXuatPhat = panelCapNhatTuyen.getTxtGaXuatPhat();
        JTextField txtGaDich = panelCapNhatTuyen.getTxtGaDich();

        Runnable actionMaTuyen = () -> {
            taiDuLieuTuyen();
            chuyenFocusSauKhiChon(txtMaTuyen);
        };

        Runnable actionGaChinhXP = () -> {
            timVaTaiTuyenTheoGa();
            chuyenFocusSauKhiChon(txtGaXuatPhat);
        };

        Runnable actionGaChinhDich = () -> {
            timVaTaiTuyenTheoGa();
            chuyenFocusSauKhiChon(txtGaDich);
        };
        taoPopGoiY(
                panelCapNhatTuyen.getTxtGaXuatPhat(),
                panelCapNhatTuyen.getPpGaXuatPhat(),
                panelCapNhatTuyen.getListGaXuatPhat(),
                gaBus::timTenGaChoGoiY,
                actionGaChinhXP
        );

        taoPopGoiY(
                panelCapNhatTuyen.getTxtGaDich(),
                panelCapNhatTuyen.getPpGaDich(),
                panelCapNhatTuyen.getListGaDich(),
                gaBus::timTenGaChoGoiY,
                actionGaChinhDich
        );

        taoPopGoiY(
                txtMaTuyen,
                panelCapNhatTuyen.getPpGaTrungGian(),
                panelCapNhatTuyen.getListGaTrungGian(),
                tuyenBus::timIDTuyenChoGoiY,
                actionMaTuyen
        );

        txtMaTuyen.addActionListener(e -> taiDuLieuTuyen());
//        txtMaTuyen.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                if(!txtMaTuyen.getText().trim().isEmpty()){
//                    taiDuLieuTuyen();
//                }
//            }
//        });
//        txtGaXuatPhat.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                actionGaChinhXP.run();
//            }
//        });
//        txtGaDich.addFocusListener(new FocusAdapter() {
//            @Override
//            public void focusLost(FocusEvent e) {
//                actionGaChinhDich.run();
//            }
//        });

        taoPopGoiY(
                panelCapNhatTuyen.getTxtGaTrungGian(),
                panelCapNhatTuyen.getPpGaTrungGian(),
                panelCapNhatTuyen.getListGaTrungGian(),
                gaBus::timTenGaChoGoiY,
                this::xuLyChonGaTrungGian
        );

        panelCapNhatTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
        panelCapNhatTuyen.getBtnLuu().addActionListener(e -> xuLyCapNhatTuyen());
        panelCapNhatTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
    }

    private void timVaTaiTuyenTheoGa(){
        String tenGaDi = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();

        if(tenGaDi.isEmpty() || tenGaDen.isEmpty()){
            return;
        }

        if(!dsGaCoSan.containsKey(tenGaDi) || !dsGaCoSan.containsKey(tenGaDen)){
            return;
        }

        List<Tuyen> ketQua = tuyenBus.timTuyenTheoGa(tenGaDi, tenGaDen);

        if(ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không tìm thấy tuyến nào giữa ga " + tenGaDi + " và ga " + tenGaDen, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            panelCapNhatTuyen.getTxtMaTuyen().setText("");
            panelCapNhatTuyen.getTxtMoTa().setText("");
            capNhatDanhSachVaTinhKC();
        }else{
            taiDuLieuTuyen(ketQua.get(0).getTuyenID());
        }
    }

    private void taiDuLieuTuyen(){
        taiDuLieuTuyen(panelCapNhatTuyen.getTxtMaTuyen().getText().trim());
    }

    private void taiDuLieuTuyen(String maTuyen){
        if(maTuyen.isEmpty() || maTuyen.startsWith("LỖI:")) return;
        List<TuyenChiTiet> dsChiTiet = tuyenBus.getDanhSachTuyenChiTiet(maTuyen);
        if(dsChiTiet == null || dsChiTiet.size() <2){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không tìm thấy tuyến với mã: " + maTuyen, "Lỗi", JOptionPane.ERROR_MESSAGE);
            panelCapNhatTuyen.getTxtMaTuyen().setText("");
            panelCapNhatTuyen.getTxtGaXuatPhat().setText("");
            panelCapNhatTuyen.getTxtGaDich().setText("");
            return;
        }

        dsGaDaChon.clear();;
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().removeAll();
        panelCapNhatTuyen.getModelGaChiTiet().setRowCount(0);

        tuyenHienTai = dsChiTiet.get(0).getTuyen();
        Ga gaXP = dsChiTiet.get(0).getGa();
        Ga gaDich = dsChiTiet.get(dsChiTiet.size() -1).getGa();

        panelCapNhatTuyen.getTxtMaTuyen().setText(tuyenHienTai.getTuyenID());
        panelCapNhatTuyen.getTxtMoTa().setText(tuyenHienTai.getMoTa());

        panelCapNhatTuyen.getTxtGaXuatPhat().setText(gaXP.getTenGa());
        panelCapNhatTuyen.getTxtGaDich().setText(gaDich.getTenGa());

        if(dsChiTiet.size() > 2){
            List<TuyenChiTiet> dsGaTrungGian = dsChiTiet.subList(1, dsChiTiet.size() -1);
            for(TuyenChiTiet tct : dsGaTrungGian){
                Ga gaTG = tct.getGa();
                dsGaDaChon.add(gaTG);
                taovaThemTagGa(gaTG);
            }
        }
        capNhatDanhSachVaTinhKC();
    }

    private void xuLyCapNhatTuyen(){
        String maTuyen = panelCapNhatTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelCapNhatTuyen.getTxtMoTa().getText().trim();
        String doDaiKCStr = panelCapNhatTuyen.getTxtDoDaiQuangDuong().getText().trim();

        if(tuyenHienTai == null || !tuyenHienTai.getTuyenID().equals(maTuyen)){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Vui lòng tải tuyến cần cập nhật trước khi lưu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(doDaiKCStr.equals("Lỗi dữ liệu") || doDaiKCStr.equals("0") || moTa.isEmpty()){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Vui lòng đảm bảo dữ liệu tuyến hợp lệ trước khi lưu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(panelCapNhatTuyen.getModelGaChiTiet().getRowCount() < 2){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Tuyến phải có ít nhất ga xuất phát và ga đích.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            Tuyen tuyenCapNhat = new Tuyen(maTuyen, moTa);
            List<TuyenChiTiet> dsTuyenChiTiet = new ArrayList<>();
            DefaultTableModel modelChiTiet = panelCapNhatTuyen.getModelGaChiTiet();
            for(int i = 0; i< modelChiTiet.getRowCount(); i++){
                String tenGa = (String) modelChiTiet.getValueAt(i, 0);
                int kcXP = (Integer) modelChiTiet.getValueAt(i, 2);
                Ga ga = dsGaCoSan.get(tenGa);
                TuyenChiTiet tct = new TuyenChiTiet(tuyenCapNhat, ga, i+1, kcXP);
                dsTuyenChiTiet.add(tct);
            }
            boolean luuTuyenThanhCong = tuyenBus.capNhatTuyen(tuyenCapNhat, dsTuyenChiTiet);
            if(luuTuyenThanhCong){
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật tuyến thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }else{
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật tuyến thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Đã xảy ra lỗi trong quá trình cập nhật tuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem){
        if(!txt.isShowing()) return;
        String input = txt.getText().trim();
        if(input.isEmpty()){
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
        if(txt.isFocusOwner()){
            pp.show(txt,0,txt.getHeight());
            txt.requestFocusInWindow();
        }
    }

    private void taoPopGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem, Runnable actionOnSelect){
        pp.setFocusable(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));
        txt.getDocument().addDocumentListener(new DocumentListener() {
            private void handleDocumentChange(){
                String input = txt.getText().trim();
                if(input.isEmpty()){
                    if(txt == panelCapNhatTuyen.getTxtMaTuyen() ||
                    txt == panelCapNhatTuyen.getTxtGaDich() ||
                    txt == panelCapNhatTuyen.getTxtGaXuatPhat()){
                        resetForm();
                    }
                    pp.setVisible(false);
                    return;
                }
                if(!dangChonTuPopup){
                    hienThiGoiY(txt, lst, pp, timKiem);
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!dangChonTuPopup){
                    hienThiGoiY(txt, lst, pp, timKiem);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!dangChonTuPopup){
                    hienThiGoiY(txt, lst, pp, timKiem);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                int index = lst.locationToIndex(e.getPoint());
                if(index >= 0){
                    dangChonTuPopup = true;
                    txt.setText(lst.getModel().getElementAt(index));
                    pp.setVisible(false);
                    actionOnSelect.run();

                    dangChonTuPopup = false;
                }
            }
        });

        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                int key = e.getKeyCode();
                int selectedIndex = lst.getSelectedIndex();
                if(pp.isVisible()){
                    switch (e.getKeyCode()){
                        case KeyEvent.VK_DOWN:
                            if(selectedIndex < lst.getModel().getSize() - 1){
                                lst.setSelectedIndex(selectedIndex + 1);
                                lst.ensureIndexIsVisible(selectedIndex + 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_UP:
                            if(selectedIndex > 0){
                                lst.setSelectedIndex(selectedIndex - 1);
                                lst.ensureIndexIsVisible(selectedIndex - 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_ENTER:
                            dangChonTuPopup = true;
                            String selected = lst.getSelectedValue();
                            if(selected != null){
                                txt.setText(selected);
                            }
                            pp.setVisible(false);
                            actionOnSelect.run();
                            dangChonTuPopup = false;
                            e.consume();
                            break;
                        case KeyEvent.VK_ESCAPE:
                            pp.setVisible(false);
                            e.consume();
                            break;
                    }
                }
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e){
                    if(!pp.isFocusOwner()){
                        pp.setVisible(false);
                    }
            }
        });
    }

    private void xuLyChonGaTrungGian(){
        JTextField txtGaMoi = panelCapNhatTuyen.getTxtGaTrungGian();
        String tenGaMoi = txtGaMoi.getText().trim();
        if(tenGaMoi.isEmpty()){
            return;
        }
        String gaDi = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String gaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();

        if(!dsGaCoSan.containsKey(tenGaMoi)){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga " + tenGaMoi + " không tồn tại trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ga gaMoi = dsGaCoSan.get(tenGaMoi);
        if(tenGaMoi.equals(gaDi) || tenGaMoi.equals(gaDen)){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga trung gian không được trùng với ga xuất phát hoặc ga đích.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga " + tenGaMoi + " đã được thêm vào danh sách ga trung gian.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        dsGaDaChon.add(gaMoi);
        taovaThemTagGa(gaMoi);
        txtGaMoi.setText("");
    }

    private void taovaThemTagGa(Ga ga){
        JButton btnGaTag = new JButton(ga.getTenGa() + " \u2715");
        btnGaTag.setMargin(new Insets(3,5,3,5));
        btnGaTag.addActionListener(e -> {
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().remove(btnGaTag);
            dsGaDaChon.remove(ga);
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().revalidate();
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().repaint();
        });

        panelCapNhatTuyen.getPnlGaTrungGianDaChon().add(btnGaTag);
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().repaint();

    }

    private void capNhatDanhSachVaTinhKC(){
        DefaultTableModel model = panelCapNhatTuyen.getModelGaChiTiet();
        model.setRowCount(0);
        String tenGaDi = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();
        if(tenGaDi.isEmpty() || tenGaDen.isEmpty()){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Vui lòng nhập đầy đủ ga xuất phát và ga đích.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        if(gaXP == null || gaDich == null){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga xuất phát hoặc ga đích không tồn tại trong hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Ga> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);
        int accumulatedDistance = 0;
        Ga gaTruoc = null;
        Ga gaHienTai = null;
        boolean isLoiKhoangCach = false;
        for(int i = 0 ; i<toanBoTuyen.size(); i++){
            gaHienTai = toanBoTuyen.get(i);
            String loaiGa;
            int kcTuGaXP = 0;
            int kcDoan =0;

            if(gaTruoc != null){
                kcDoan = tuyenBus.tinhKhoangCachTongDijsktra(gaTruoc.getGaID(), gaHienTai.getGaID());
                if(kcDoan == -1){
                    isLoiKhoangCach = true;
                    break;
                }
                accumulatedDistance += kcDoan;
            }
            kcTuGaXP = accumulatedDistance;

            if(i ==0) loaiGa = "Xuất Phát";
            else if(i == toanBoTuyen.size() -1) loaiGa = "Đích";
            else loaiGa = "Trung Gian";

            model.addRow(new Object[]{
                    gaHienTai.getTenGa(),
                    loaiGa,
                    kcTuGaXP
            });
            gaTruoc = gaHienTai;
        }
//        if(isLoiKhoangCach){
//            JOptionPane.showMessageDialog(panelCapNhatTuyen, "LỖI: Không tìm thấy đường đi hoặc khoảng cách chuẩn giữa " +
//                    (gaTruoc != null ? gaTruoc.getTenGa() : "N/A" + " và " + (gaHienTai != null ? gaHienTai.getTenGa() : "N/A")), "Lỗi", JOptionPane.ERROR_MESSAGE);
//            model.setRowCount(0);
//            panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText("0");
//        else{
            panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
//        }
    }

    private void xuLyHuyBo(){
        int choice = JOptionPane.showConfirmDialog(
                panelCapNhatTuyen,
                "Bạn có chắc muốn hủy cập nhật tuyến không?",
                "Xác nhận hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION){
            dialog.dispose();
        }
    }

    private void chuyenFocusSauKhiChon(JTextField sourceField){
        if(sourceField == panelCapNhatTuyen.getTxtGaXuatPhat()) {
            panelCapNhatTuyen.getTxtGaDich().requestFocusInWindow();
        } else if (sourceField == panelCapNhatTuyen.getTxtGaDich()) {
            panelCapNhatTuyen.getTxtGaTrungGian().requestFocusInWindow();
        }else if (sourceField == panelCapNhatTuyen.getTxtMaTuyen()){
            panelCapNhatTuyen.getTxtGaTrungGian().requestFocusInWindow();
        }
    }

    private void resetForm(){
        panelCapNhatTuyen.getTxtMaTuyen().setText("");
        panelCapNhatTuyen.getTxtGaXuatPhat().setText("");
        panelCapNhatTuyen.getTxtGaDich().setText("");
        panelCapNhatTuyen.getTxtGaTrungGian().setText("");
        panelCapNhatTuyen.getTxtMoTa().setText("");
        panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText("0");
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().removeAll();
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().repaint();
        panelCapNhatTuyen.getModelGaChiTiet().setRowCount(0);
        dsGaDaChon.clear();
        tuyenHienTai = null;
    }

}
