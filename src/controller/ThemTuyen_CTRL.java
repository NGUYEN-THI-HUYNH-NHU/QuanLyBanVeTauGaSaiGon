package controller;/*
 * @ (#) ThemTuyen_CTRL.java   1.0     28/10/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
 */

import bus.Ga_BUS;
import bus.Tuyen_BUS;
import dao.KhoangCachChuan_DAO;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ThemTuyen_CTRL {
    private final PanelThemTuyen panelThemTuyen;
    private final Tuyen_BUS tuyenBus;
    private final JDialog dialog;
    private final Ga_BUS gaBus;
    private final KhoangCachChuan_DAO khoangCachChuanDao;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;
    private List<String> listTenGaGoc;

    private JPopupMenu ppGaXuatPhat = new JPopupMenu();
    private JList<String> lstGaXuatPhat = new JList<>();

    private JPopupMenu ppGaDich = new JPopupMenu();
    private JList<String> lstGaDich = new JList<>();

    private JPopupMenu ppGaTrungGian = new JPopupMenu();
    private JList<String> lstGaTrungGian = new JList<>();

    public ThemTuyen_CTRL(PanelThemTuyen panelThemTuyen, JDialog dialog){
        this.panelThemTuyen = panelThemTuyen;
        this.dialog = dialog;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();
        khoangCachChuanDao = new KhoangCachChuan_DAO();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();
        listTenGaGoc = new ArrayList<>();

        khoiTaoDuLieuBanDau();
        thietLapListener();
    }

    private void khoiTaoDuLieuBanDau(){
        listTenGaGoc = gaBus.getDanhSachTenGa();

        for(String tenGa : listTenGaGoc){
            Ga ga = gaBus.getGaByTenGa(tenGa);
            if(ga != null){
                dsGaCoSan.put(tenGa, ga);
            }
        }

        setModelToComboBox(panelThemTuyen.getTxtGaXuatPhat());
        setModelToComboBox(panelThemTuyen.getTxtGaDich());
        setModelToComboBox(panelThemTuyen.getTxtGaTrungGian());
    }

    private void setModelToComboBox(JComboBox<String> cbo){
        cbo.setEditable(true);
        cbo.setSelectedIndex(-1);
    }

    private void thietLapListener(){
        JTextField txtXP = (JTextField) panelThemTuyen.getTxtGaXuatPhat().getEditor().getEditorComponent();
        JTextField txtDich = (JTextField) panelThemTuyen.getTxtGaDich().getEditor().getEditorComponent();
        JTextField txtTG = (JTextField) panelThemTuyen.getTxtGaTrungGian().getEditor().getEditorComponent();

        taoPopupGoiY(txtXP, ppGaXuatPhat, lstGaXuatPhat,
                input -> locDuLieu(listTenGaGoc, input),
                txtDich);

        taoPopupGoiY(txtDich, ppGaDich, lstGaDich,
                input -> locDuLieu(listTenGaGoc, input),
                txtTG);

        taoPopupGoiY(txtTG, ppGaTrungGian, lstGaTrungGian,
                input -> locDuLieu(listTenGaGoc, input),
                null);

        setupUpdateMatuyenEvent(panelThemTuyen.getTxtGaXuatPhat());
        setupUpdateMatuyenEvent(panelThemTuyen.getTxtGaDich());

        txtTG.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!ppGaTrungGian.isVisible()){
                        xuLyChonGaTrungGian();
                    }
                }
            }
        });

        panelThemTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
        panelThemTuyen.getBtnLuu().addActionListener(e -> xuLyLuuTuyen());
        panelThemTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
    }

    private String unAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s.toLowerCase(), java.text.Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private List<String> locDuLieu(List<String> src, String input){
        if(src == null || input.isEmpty()) return new ArrayList<>();
        String inputNorm = unAccent(input);
        return src.stream()
                .filter(s -> unAccent(s).contains(inputNorm))
                .limit(10)
                .collect(Collectors.toList());
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem){
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

        if(txt.isShowing()){
            pp.show(txt, 0, txt.getHeight());
            txt.requestFocus();
        }
    }

    private void taoPopupGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem, JComponent nextFocus){
        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            private void update() {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(300, e -> SwingUtilities.invokeLater(() -> {
                    if (txt.isFocusOwner()) {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }

            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
        lst.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    txt.setText(lst.getSelectedValue());
                    pp.setVisible(false);
                    capNhatMaTuyen();
                    if(nextFocus != null) {
                        nextFocus.requestFocusInWindow();
                    }
                }
            }
        });
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pp.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        int index = lst.getSelectedIndex();
                        if (index < lst.getModel().getSize() - 1) {
                            lst.setSelectedIndex(index + 1);
                            lst.ensureIndexIsVisible(index + 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int index = lst.getSelectedIndex();
                        if (index > 0) {
                            lst.setSelectedIndex(index - 1);
                            lst.ensureIndexIsVisible(index - 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (lst.getSelectedValue() != null) {
                            txt.setText(lst.getSelectedValue());
                            pp.setVisible(false);
                            capNhatMaTuyen();
                            if(nextFocus != null){
                                nextFocus.requestFocusInWindow();
                            }
                        }
                        e.consume();
                    }
                }
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!pp.isFocusOwner() && !lst.isFocusOwner()) {
                        pp.setVisible(false);
                    }
                });
            }
        });
    }

    private void setupUpdateMatuyenEvent(JComboBox<String> cbo){
        JTextField txt = (JTextField) cbo.getEditor().getEditorComponent();
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatMaTuyen();
            }
        });
    }

    private void capNhatMaTuyen() {
        Object selectedXP = panelThemTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object selectedDich = panelThemTuyen.getTxtGaDich().getEditor().getItem();

        String tenGaDi = selectedXP != null ? selectedXP.toString().trim() : "";
        String tenGaDen = selectedDich != null ? selectedDich.toString().trim() : "";

        if(!tenGaDi.isEmpty() && !tenGaDen.isEmpty()){
            String baseMa = tuyenBus.taoMaTuyenCoSo(tenGaDi,tenGaDen);
            if(baseMa.isEmpty()){
                panelThemTuyen.getTxtMaTuyen().setText("");
                return;
            }
            if(tuyenBus.kiemTraMaTuyuenDaTonTai(baseMa)) {
                panelThemTuyen.getTxtMaTuyen().setText("LỖI: Tuyến " + baseMa + " đã tồn tại!");
                panelThemTuyen.getTxtMaTuyen().setForeground(Color.RED);
            }else{
                panelThemTuyen.getTxtMaTuyen().setText(baseMa);
                panelThemTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
            }
        }else{
            panelThemTuyen.getTxtMaTuyen().setText("");
            panelThemTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
        }
    }

    private void xuLyChonGaTrungGian(){
        Object item = panelThemTuyen.getTxtGaTrungGian().getEditor().getItem();
        String tenGaMoi = item != null ? item.toString().trim() : "";
        if(tenGaMoi.isEmpty()){
            return;
        }

        Object itemXP = panelThemTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object itemDich = panelThemTuyen.getTxtGaDich().getEditor().getItem();
        String gaDi = itemXP != null ? itemXP.toString().trim() : "";
        String gaDen = itemDich != null ? itemDich.toString().trim() : "";

        if(!dsGaCoSan.containsKey(tenGaMoi)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian không tồn tại trong hệ thống!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Ga gaMoi = dsGaCoSan.get(tenGaMoi);
        if(tenGaMoi.equals(gaDi) || tenGaMoi.equals(gaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian không được trùng với ga xuất phát hoặc ga đích!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian đã được chọn!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dsGaDaChon.add(gaMoi);
        taovaThemTagGa(gaMoi);

        panelThemTuyen.getTxtGaTrungGian().getEditor().setItem("");
        ppGaTrungGian.setVisible(false);
    }

    private void taovaThemTagGa(Ga ga){
        JButton btnGaTag = new JButton(ga.getTenGa() + " \u2715");
        btnGaTag.setMargin(new Insets(3,5,3,5));
        btnGaTag.addActionListener(e -> {
            panelThemTuyen.getPnlGaTrungGianDaChon().remove(btnGaTag);
            dsGaDaChon.remove(ga);
            panelThemTuyen.getPnlGaTrungGianDaChon().revalidate();
            panelThemTuyen.getPnlGaTrungGianDaChon().repaint();
        });

        panelThemTuyen.getPnlGaTrungGianDaChon().add(btnGaTag);
        panelThemTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelThemTuyen.getPnlGaTrungGianDaChon().repaint();
    }

    private void capNhatDanhSachVaTinhKC() {
        DefaultTableModel model = panelThemTuyen.getModelGaChiTiet();
        model.setRowCount(0);

        Object itemXP = panelThemTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object itemDich = panelThemTuyen.getTxtGaDich().getEditor().getItem();
        String tenGaDi = itemXP != null ? itemXP.toString().trim() : "";
        String tenGaDen = itemDich != null ? itemDich.toString().trim() : "";

        if ( tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Vui lòng chọn Ga Xuất Phát và Ga Đích trước khi tính khoảng cách.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }
        if(!dsGaCoSan.containsKey(tenGaDi) || !dsGaCoSan.containsKey(tenGaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga Xuất Phát hoặc Ga Đích không tồn tại trong hệ thống.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("Lỗi dữ liệu");
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        List<Ga> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);

        int accumulatedDistance = 0;
        Ga gaTruoc = null;
        Ga gaHienTai = null;
        boolean isLoiKhoangCacg = false;

        for (int i = 0; i < toanBoTuyen.size(); i++) {
            gaHienTai = toanBoTuyen.get(i);
            String loaiGa;
            int kcTuGaXP = 0;
            int kcDoan = 0;

            if (gaTruoc != null) {
                kcDoan = tuyenBus.tinhKhoangCachTongDijsktra(gaTruoc.getGaID(), gaHienTai.getGaID());
                if(kcDoan == -1){
                    isLoiKhoangCacg = true;
                    break;
                }
                accumulatedDistance += kcDoan;
            }

            kcTuGaXP = accumulatedDistance;

            if (i == 0) loaiGa = "Xuất Phát";
            else if (i == toanBoTuyen.size() - 1) loaiGa = "Đích";
            else loaiGa = "Trung Gian";

            model.addRow(new Object[]{
                    gaHienTai.getTenGa(),
                    loaiGa,
                    kcTuGaXP
            });

            gaTruoc = gaHienTai;
        }
        if (isLoiKhoangCacg) {
            JOptionPane.showMessageDialog(panelThemTuyen,
                    "Lỗi: Không tìm thấy đường đi hoặc khoảng cách chuẩn giữa " +
                            (gaTruoc != null ? gaTruoc.getTenGa() : "N/A") + " và " +
                            (gaHienTai != null ? gaHienTai.getTenGa() : "N/A") + ".\nVui lòng kiểm tra lại dữ liệu.",
                    "Lỗi Dữ Liệu Tuyến", JOptionPane.ERROR_MESSAGE);
            model.setRowCount(0);

            panelThemTuyen.getTxtDoDaiQuangDuong().setText("Lỗi dữ liệu");

    }
        else {
            panelThemTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
        }
    }

    private void xuLyLuuTuyen() {
        String maTuyen = panelThemTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelThemTuyen.getTxtMoTa().getText().trim();
        String doDaiKCStr = panelThemTuyen.getTxtDoDaiQuangDuong().getText().trim();

        if (maTuyen.startsWith("LỖI") || maTuyen.isEmpty() || tuyenBus.kiemTraMaTuyuenDaTonTai(maTuyen)) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Mã tuyến không hợp lệ hoặc đã tồn tại.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (doDaiKCStr.equals("Lỗi dữ liệu") || doDaiKCStr.equals("0") || moTa.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Dữ liệu không hợp lệ (Khoảng cách hoặc Mô tả).", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (panelThemTuyen.getModelGaChiTiet().getRowCount() < 2) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Tuyến phải có ít nhất ga xuất phát và ga đích.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer.parseInt(doDaiKCStr);
            Tuyen tuyenMoi = new Tuyen(maTuyen, moTa);

            List<TuyenChiTiet> dsTuyenChiTiet = new ArrayList<>();
            DefaultTableModel modelChiTiet = panelThemTuyen.getModelGaChiTiet();

            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                String tenGa = (String) modelChiTiet.getValueAt(i, 0);
                Object kcXPValue = modelChiTiet.getValueAt(i, 2);
                int kcXP;
                if (kcXPValue instanceof Integer) {
                    kcXP = (Integer) kcXPValue;
                } else if (kcXPValue instanceof String) {
                    kcXP = Integer.parseInt((String) kcXPValue);
                } else {
                    throw new IllegalArgumentException("Khoảng cách Ga (" + tenGa + ") không hợp lệ.");
                }
                Ga ga = dsGaCoSan.get(tenGa);
                TuyenChiTiet tct = new TuyenChiTiet(tuyenMoi, ga, i + 1, kcXP);
                dsTuyenChiTiet.add(tct);
            }
            boolean luuTuyenThanhCong = tuyenBus.themTuyen(tuyenMoi, dsTuyenChiTiet);
            if (luuTuyenThanhCong) {
                JOptionPane.showMessageDialog(panelThemTuyen, "Đã lưu tuyến mới " + maTuyen + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(panelThemTuyen, "Lưu tuyến thất bại! Vui lòng kiểm tra lại dữ liệu.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(panelThemTuyen, e.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panelThemTuyen, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyBo() {
        int choice = JOptionPane.showConfirmDialog(
                panelThemTuyen,
                "Bạn có chắc chắn muốn hủy bỏ thao tác thêm tuyến?\nMọi thay đổi chưa lưu sẽ bị mất.",
                "Xác nhận Hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dialog.dispose();
        }
    }

//    private void chuyenFocusSauKhiChon(JTextField sourceField){
//        if(sourceField == panelThemTuyen.getTxtGaXuatPhat()) {
//            panelThemTuyen.getTxtGaDich().requestFocusInWindow();
//        } else if (sourceField == panelThemTuyen.getTxtGaDich()) {
//            panelThemTuyen.getTxtGaTrungGian().requestFocusInWindow();
//
//        }
//    }
}
