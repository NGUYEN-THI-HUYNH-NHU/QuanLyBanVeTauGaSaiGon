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
import gui.application.UngDung;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutput;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThemTuyen_CTRL {
    private final PanelThemTuyen panelThemTuyen;
    private final Tuyen_BUS tuyenBus;
    private final Ga_BUS gaBus;
    private final KhoangCachChuan_DAO khoangCachChuanDao;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;

    public ThemTuyen_CTRL(PanelThemTuyen panelThemTuyen){
        this.panelThemTuyen = panelThemTuyen;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();
        khoangCachChuanDao = new KhoangCachChuan_DAO();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();

        khoiTaoDuLieuBanDau();
        thietLapListener();
    }

    private void khoiTaoDuLieuBanDau(){
        List<String> dsGa = gaBus.getDanhSachTenGa();
        DefaultComboBoxModel<String> modelCmbGaXuatPhat = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modelCmbGaDich = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modelCmbGaTrungGian = new DefaultComboBoxModel<>();

        modelCmbGaXuatPhat.addElement("");
        modelCmbGaDich.addElement("");
        modelCmbGaTrungGian.addElement("");

        for(String tenGa : dsGa){
            modelCmbGaXuatPhat.addElement(tenGa);
            modelCmbGaDich.addElement(tenGa);
            modelCmbGaTrungGian.addElement(tenGa);

            Ga ga = gaBus.getGaByTenGa(tenGa);
            dsGaCoSan.put(tenGa, ga);
        }
        panelThemTuyen.getCmbGaXuatPhat().setModel(modelCmbGaXuatPhat);
        panelThemTuyen.getCmbGaDich().setModel(modelCmbGaDich);
        panelThemTuyen.getCmbGaTrungGian().setModel(modelCmbGaTrungGian);
    }

    private void thietLapListener(){
        ActionListener maTuyenListener = e -> capNhatMaTuyen();
        panelThemTuyen.getCmbGaXuatPhat().addActionListener(maTuyenListener);
        panelThemTuyen.getCmbGaDich().addActionListener(maTuyenListener);

        taoPopGoiYChoComboBox(panelThemTuyen.getCmbGaXuatPhat(), panelThemTuyen.getPpGaXuatPhat(), panelThemTuyen.getListGaXuatPhat(), gaBus::timTenGaChoGoiY);
        taoPopGoiYChoComboBox(panelThemTuyen.getCmbGaDich(), panelThemTuyen.getPpGaDich(), panelThemTuyen.getListGaDich(), gaBus::timTenGaChoGoiY);
        taoPopGoiYChoComboBox(panelThemTuyen.getCmbGaTrungGian(), panelThemTuyen.getPpGaTrungGian(), panelThemTuyen.getListGaTrungGian(), gaBus::timTenGaChoGoiY);

        panelThemTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());

        panelThemTuyen.getBtnLuu().addActionListener(e -> xuLyLuuTuyen());
        panelThemTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());

    }

    private void capNhatMaTuyen() {
        String tenGaDi = (String) panelThemTuyen.getCmbGaXuatPhat().getSelectedItem();
        String tenGaDen = (String) panelThemTuyen.getCmbGaDich().getSelectedItem();

        if (tenGaDi != null && tenGaDen != null && !tenGaDi.isEmpty() && !tenGaDen.isEmpty()) {
            String baseMa = tuyenBus.taoMaTuyen(tenGaDi, tenGaDen);
            if (!baseMa.isEmpty()) {
                panelThemTuyen.getTxtMaTuyen().setText(baseMa);
            } else {
                panelThemTuyen.getTxtMaTuyen().setText("");
            }
        } else {
            panelThemTuyen.getTxtMaTuyen().setText("");
        }
    }

    private void hienThiGoiYChoComboBox(JTextComponent editor, JComboBox<String> comboBox, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem){
        String input = editor.getText().trim();
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

        pp.show(comboBox, 0, comboBox.getHeight());
        editor.requestFocusInWindow();
    }

    private void taoPopGoiYChoComboBox(JComboBox<String> comboBox, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem) {
        // Lấy ô nhập liệu (editor) của JComboBox
        JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();

        pp.setFocusable(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        // Hiển thị gợi ý khi input thay đổi
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hienThiGoiYChoComboBox(editor, comboBox, lst, pp, timKiem);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hienThiGoiYChoComboBox(editor, comboBox, lst, pp, timKiem);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // Mouse click chọn item -> CHỈ CẬP NHẬT COMBOBOX VÀ GỌI HÀM XỬ LÝ LIÊN QUAN
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = lst.locationToIndex(e.getPoint());
                if (index >= 0) {
                    String selectedValue = lst.getModel().getElementAt(index);

                    // TẠM TẮT listener để tránh gọi lại khi set item
                    ActionListener[] listeners = comboBox.getActionListeners();
                    for (ActionListener l : listeners) comboBox.removeActionListener(l);

                    comboBox.setSelectedItem(selectedValue); // Cập nhật ComboBox

                    // GẮN LẠI listener
                    for (ActionListener l : listeners) comboBox.addActionListener(l);

                    pp.setVisible(false);


                    if (comboBox == panelThemTuyen.getCmbGaTrungGian()) {
                        xuLyChonGaTrungGian(null);
                    }
                    else {
                        capNhatMaTuyen();
                    }
                }
            }
        });

        // Key listener xử lý ↑ ↓, Enter (Chỉ cập nhật ComboBox)
        editor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int selectedIndex = lst.getSelectedIndex();

                if (pp.isVisible()) {
                    switch (key) {
                        case KeyEvent.VK_DOWN:
                            if (selectedIndex < lst.getModel().getSize() - 1) {
                                lst.setSelectedIndex(selectedIndex + 1);
                                lst.ensureIndexIsVisible(selectedIndex + 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_UP:
                            if (selectedIndex > 0) {
                                lst.setSelectedIndex(selectedIndex - 1);
                                lst.ensureIndexIsVisible(selectedIndex - 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_ENTER:
                            String selectedValue = lst.getSelectedValue();
                            if (selectedValue != null) {
                                // TẠM TẮT listener
                                ActionListener[] listeners = comboBox.getActionListeners();
                                for (ActionListener l : listeners) comboBox.removeActionListener(l);

                                comboBox.setSelectedItem(selectedValue);

                                // GẮN LẠI listener
                                for (ActionListener l : listeners) comboBox.addActionListener(l);
                            }
                            pp.setVisible(false);

                            // KÍCH HOẠT HÀM XỬ LÝ TƯƠNG ỨNG
                            if (comboBox == panelThemTuyen.getCmbGaTrungGian()) {
                                xuLyChonGaTrungGian(null);
                            } else {
                                capNhatMaTuyen();
                            }
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
    }

    private void xuLyChonGaTrungGian(ActionEvent e){
        JComboBox<String> cmb = panelThemTuyen.getCmbGaTrungGian();
        String tenGaMoi = (String) cmb.getSelectedItem();
        if(tenGaMoi == null || tenGaMoi.isEmpty()){
            return;
        }

        String gaDi = (String) panelThemTuyen.getCmbGaXuatPhat().getSelectedItem();
        String gaDen = (String) panelThemTuyen.getCmbGaDich().getSelectedItem();
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
        cmb.setSelectedIndex(0);
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

        String tenGaDi = (String) panelThemTuyen.getCmbGaXuatPhat().getSelectedItem();
        String tenGaDen = (String) panelThemTuyen.getCmbGaDich().getSelectedItem();

        if (tenGaDi == null || tenGaDen == null || tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        if (gaXP == null || gaDich == null) return;

        List<Ga> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);

        int accumulatedDistance = 0;
        Ga gaTruoc = null;
        Ga gaHienTai = null;
        boolean isLoiKhoangCach = false;

        for (int i = 0; i < toanBoTuyen.size(); i++) {
            gaHienTai = toanBoTuyen.get(i);
            String loaiGa;
            int kcTuGaXP = 0;
            int kcDoan = 0;

            if (gaTruoc != null) {
                kcDoan = khoangCachChuanDao.getKhoangCachDoan(gaTruoc.getGaID(), gaHienTai.getGaID());

                if (kcDoan == -1) {
                    isLoiKhoangCach = true;
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

        if (isLoiKhoangCach) {
            JOptionPane.showMessageDialog(panelThemTuyen,
                    "Lỗi: Không tìm thấy khoảng cách chuẩn giữa " + gaTruoc.getTenGa() + " và " + gaHienTai.getTenGa() + ". Vui lòng kiểm tra lại dữ liệu.",
                    "Lỗi Dữ Liệu Tuyến", JOptionPane.ERROR_MESSAGE);
            model.setRowCount(0);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("Lỗi Dữ Liệu");
        } else {
            panelThemTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
        }
    }

    private void xuLyLuuTuyen() {
        String maTuyen = panelThemTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelThemTuyen.getTxtMoTa().getText().trim();
        String doDaiKCStr = panelThemTuyen.getTxtDoDaiQuangDuong().getText().trim();
        if (doDaiKCStr.equals("Lỗi Dữ Liệu") || doDaiKCStr.equals("0") || maTuyen.isEmpty() || moTa.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Không thể lưu tuyến do có lỗi về khoảng cách giữa các ga.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (panelThemTuyen.getModelGaChiTiet().getRowCount() < 2) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Tuyến phải có ít nhất ga xuất phát và ga đích.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
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
                PanelQuanLyTuyen panelQuanLy = new PanelQuanLyTuyen(panelThemTuyen.getNhanVienThucHien());
                UngDung.showGiaoDienChinh(panelQuanLy);
            } else {
                JOptionPane.showMessageDialog(panelThemTuyen, "Lưu tuyến thất bại! Vui lòng kiểm tra lại dữ liệu.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Lỗi định dạng số: Khoảng cách hoặc GaID không phải là số nguyên hợp lệ.", "Lỗi Định Dạng", JOptionPane.ERROR_MESSAGE);
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
                "Bạn có chắc chắn muốn hủy bỏ thao tác thêm tuyến?\nMọi thay đổi chưa lưu sẽ bị mất.", // Nội dung câu hỏi
                "Xác nhận Hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            PanelQuanLyTuyen panelQuanLy = new PanelQuanLyTuyen(panelThemTuyen.getNhanVienThucHien());
            UngDung.showGiaoDienChinh(panelQuanLy);
        }
    }
}
