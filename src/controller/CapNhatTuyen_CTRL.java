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
import gui.application.form.quanLyTuyen.PanelCapNhatTuyen;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CapNhatTuyen_CTRL {
    private boolean dangChonTuPopup = false;
    private final PanelCapNhatTuyen panelCapNhatTuyen;
    private final Tuyen_BUS tuyenBus;
    private final Ga_BUS gaBus;
    private final JDialog dialog;
    private final String tuyenIDCanCapNhat;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;
    private List<String> listTenGaGoc;

    private boolean isDataLoading  = false;

    public CapNhatTuyen_CTRL(PanelCapNhatTuyen panelCapNhatTuyen, JDialog dialog, String tuyenID) {
        this.panelCapNhatTuyen = panelCapNhatTuyen;
        this.dialog = dialog;
        this.tuyenIDCanCapNhat = tuyenID;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();
        listTenGaGoc = new ArrayList<>();

        khoiTaoDuLieuBanDau();
        thietLapListener();
        taiDuLieuTuyen(tuyenIDCanCapNhat);
    }

    private void khoiTaoDuLieuBanDau(){
        listTenGaGoc = gaBus.getDanhSachTenGa();

        for(String tenGa : listTenGaGoc){
            Ga ga = gaBus.getGaByTenGa(tenGa);
            if(ga != null){
                dsGaCoSan.put(tenGa, ga);
            }
        }
        setModelToComboBox(panelCapNhatTuyen.getTxtGaXuatPhat(), listTenGaGoc);
        setModelToComboBox(panelCapNhatTuyen.getTxtGaDich(), listTenGaGoc);
        setModelToComboBox(panelCapNhatTuyen.getTxtGaTrungGian(), listTenGaGoc);
    }

    private void setModelToComboBox(JComboBox<String> cbo, List<String> data){
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(data.toArray(new String[0]));
        cbo.setModel(model);
        cbo.setSelectedIndex(-1);
    }

    private void thietLapListener(){
        setModelToComboBox(panelCapNhatTuyen.getTxtGaXuatPhat(), listTenGaGoc);
        setModelToComboBox(panelCapNhatTuyen.getTxtGaDich(), listTenGaGoc);
        setModelToComboBox(panelCapNhatTuyen.getTxtGaTrungGian(), listTenGaGoc);

        setupUpdateMaTuyenEvent(panelCapNhatTuyen.getTxtGaXuatPhat());
        setupUpdateMaTuyenEvent(panelCapNhatTuyen.getTxtGaDich());

        panelCapNhatTuyen.getTxtGaTrungGian().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    xuLyChonGaTrungGian();
                }
            }
        });

        panelCapNhatTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
        panelCapNhatTuyen.getBtnLuu().addActionListener(e -> xuLyCapNhatTuyen());
        panelCapNhatTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
    }

    private void taiDuLieuTuyen(String maTuyen){
        if(maTuyen == null || maTuyen.isEmpty()) return;
        isDataLoading = true;
        try{
            List<TuyenChiTiet> dsChiTiet = tuyenBus.getDanhSachTuyenChiTiet(maTuyen);
            if (dsChiTiet == null || dsChiTiet.size() < 2) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không tải được dữ liệu tuyến: " + maTuyen, "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tuyen tuyen = dsChiTiet.get(0).getTuyen();
            Ga gaXP = dsChiTiet.get(0).getGa();
            Ga gaDich = dsChiTiet.get(dsChiTiet.size() - 1).getGa();

            panelCapNhatTuyen.getTxtMaTuyen().setText(tuyen.getTuyenID());
            panelCapNhatTuyen.getTxtMoTa().setText(tuyen.getMoTa());

            if (tuyen.isTrangThai()) {
                panelCapNhatTuyen.getCboTrangThai().setSelectedIndex(0); // Hoạt động
            } else {
                panelCapNhatTuyen.getCboTrangThai().setSelectedIndex(1); // Tạm ngưng/Không hoạt động
            }

            panelCapNhatTuyen.getTxtGaXuatPhat().setSelectedItem(gaXP.getTenGa());
            panelCapNhatTuyen.getTxtGaDich().setSelectedItem(gaDich.getTenGa());

            dsGaDaChon.clear();
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().removeAll();
            if (dsChiTiet.size() > 2) {
                for (int i = 1; i < dsChiTiet.size() - 1; i++) {
                    Ga gaTG = dsChiTiet.get(i).getGa();
                    dsGaDaChon.add(gaTG);
                    taovaThemTagGa(gaTG);
                }
            }

            capNhatDanhSachVaTinhKC();
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            isDataLoading = false;
        }
    }

    private String unAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private void setupComboBoxAutocomplete(JComboBox<String> comboBox, List<String> sourceData) {
        comboBox.setEditable(true);
        final JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();

        Timer debounceTimer = new Timer(300, e -> {
            if (!textfield.isShowing()) return;
            if(isDataLoading) return;
            String text = textfield.getText();

            String textNormalized = unAccent(text);
            List<String> filteredList = sourceData.stream()
                    .filter(item -> unAccent(item).contains(textNormalized))
                    .collect(Collectors.toList());

            int caretPosition = textfield.getCaretPosition();
            DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(filteredList.toArray(new String[0]));

            if (newModel.getSize() > 0) newModel.setSelectedItem(null);

            comboBox.setModel(newModel);
            textfield.setText(text);
            try {
                if (caretPosition <= text.length()) textfield.setCaretPosition(caretPosition);
                else textfield.setCaretPosition(text.length());
            } catch (Exception ex) {}

            if (!filteredList.isEmpty()) comboBox.showPopup();
            else comboBox.hidePopup();
        });
        debounceTimer.setRepeats(false);

        textfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                        e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT ||
                        e.getKeyCode() == KeyEvent.VK_ENTER) return;
                debounceTimer.restart();
            }
        });
    }

    private void setupUpdateMaTuyenEvent(JComboBox<String> cbo){
        JTextField txt = (JTextField) cbo.getEditor().getEditorComponent();

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatMaTuyenTuDong();
            }
        });

        cbo.addActionListener(e -> {
            if(cbo.isPopupVisible() && cbo.getSelectedIndex() != -1) {
                capNhatMaTuyenTuDong();
            }
        });
    }

    private void capNhatMaTuyenTuDong() {
        if (isDataLoading) return;

        Object itemXP = panelCapNhatTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object itemDich = panelCapNhatTuyen.getTxtGaDich().getEditor().getItem();

        String tenGaDi = itemXP != null ? itemXP.toString().trim() : "";
        String tenGaDen = itemDich != null ? itemDich.toString().trim() : "";
        if(!tenGaDi.isEmpty() && !tenGaDen.isEmpty()){
            String maMoi = tuyenBus.taoMaTuyenCoSo(tenGaDi, tenGaDen);

            if (!maMoi.equals(panelCapNhatTuyen.getTxtMaTuyen().getText())) {
                if(tuyenBus.kiemTraMaTuyuenDaTonTai(maMoi) && !maMoi.equals(tuyenIDCanCapNhat)) {
                    panelCapNhatTuyen.getTxtMaTuyen().setText(maMoi);
                    panelCapNhatTuyen.getTxtMaTuyen().setForeground(Color.RED);
                } else {
                    panelCapNhatTuyen.getTxtMaTuyen().setText(maMoi);
                    panelCapNhatTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
                }
            }
        }
    }


    private void xuLyChonGaTrungGian(){
        // 1. Lấy thông tin từ giao diện
        Object item = panelCapNhatTuyen.getTxtGaTrungGian().getEditor().getItem();
        String tenGaMoi = item != null ? item.toString().trim() : "";
        if(tenGaMoi.isEmpty()) return;

        Object itemXP = panelCapNhatTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object itemDich = panelCapNhatTuyen.getTxtGaDich().getEditor().getItem();
        String tenGaXP = itemXP != null ? itemXP.toString().trim() : "";
        String tenGaDen = itemDich != null ? itemDich.toString().trim() : "";

        // 2. Kiểm tra tính hợp lệ cơ bản
        if(!dsGaCoSan.containsKey(tenGaMoi)){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Tên ga không tồn tại trong hệ thống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(tenGaMoi.equals(tenGaXP) || tenGaMoi.equals(tenGaDen)){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga trung gian không được trùng với Ga XP hoặc Ga Đích!", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))){
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga này đã có trong danh sách!", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ga gaMoi = dsGaCoSan.get(tenGaMoi);
        Ga gaXP = dsGaCoSan.get(tenGaXP);
        Ga gaDen = dsGaCoSan.get(tenGaDen);

        // --- 3. KIỂM TRA LOGIC KHOẢNG CÁCH & THỨ TỰ (BỔ SUNG) ---
        try {
            // A. Tính tổng quãng đường (XP -> Đích)
            int kcTong = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaDen.getGaID());
            // B. Tính quãng đường từ XP -> Ga Mới
            int kcTuDauDenMoi = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaMoi.getGaID());
            // C. Tính quãng đường từ Ga Mới -> Đích
            int kcTuMoiDenDich = tuyenBus.tinhKhoangCachTongDijsktra(gaMoi.getGaID(), gaDen.getGaID());

            // Check lỗi DB/Không tìm thấy đường
            if (kcTong == -1 || kcTuDauDenMoi == -1 || kcTuMoiDenDich == -1) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen,
                        "Không thể tính toán khoảng cách. Vui lòng kiểm tra lại kết nối giữa các ga.",
                        "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check 3.1: Ga Mới có nằm TRÊN tuyến đường không?
            // (XP->Mới) + (Mới->Đích) ≈ (XP->Đích) (cho phép sai số nhỏ)
            if (Math.abs((kcTuDauDenMoi + kcTuMoiDenDich) - kcTong) > 20) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen,
                        "Ga " + tenGaMoi + " không nằm trên lộ trình từ " + tenGaXP + " đến " + tenGaDen + ".\n" +
                                "Hoặc ga này nằm ngược chiều di chuyển.",
                        "Sai lộ trình", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check 3.2: Kiểm tra THỨ TỰ so với ga vừa nhập gần nhất
            // (Chỉ kiểm tra nếu danh sách ga trung gian không rỗng)
            if (!dsGaDaChon.isEmpty()) {
                Ga gaCuoiCung = dsGaDaChon.get(dsGaDaChon.size() - 1);
                int kcTuDauDenCuoiList = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaCuoiCung.getGaID());

                // Ga mới nhập vào phải xa Ga XP hơn so với ga đã nhập trước đó
                if (kcTuDauDenMoi <= kcTuDauDenCuoiList) {
                    JOptionPane.showMessageDialog(panelCapNhatTuyen,
                            "Sai thứ tự hành trình!\n" +
                                    "- Ga " + gaCuoiCung.getTenGa() + " cách " + tenGaXP + ": " + kcTuDauDenCuoiList + " km\n" +
                                    "- Ga " + tenGaMoi + " cách " + tenGaXP + ": " + kcTuDauDenMoi + " km\n\n" +
                                    "-> Ga " + tenGaMoi + " phải nằm TRƯỚC ga " + gaCuoiCung.getTenGa() + ".\n" +
                                    "Vui lòng xóa ga " + gaCuoiCung.getTenGa() + " trước nếu muốn chèn ga này.",
                            "Sai thứ tự nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            dsGaDaChon.add(gaMoi);
            taovaThemTagGa(gaMoi);

            panelCapNhatTuyen.getTxtGaTrungGian().setSelectedItem(null);
            panelCapNhatTuyen.getTxtGaTrungGian().getEditor().setItem("");

            capNhatDanhSachVaTinhKC();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Lỗi tính toán: " + ex.getMessage());
        }
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

    private void capNhatDanhSachVaTinhKC() {
        DefaultTableModel model = panelCapNhatTuyen.getModelGaChiTiet();
        model.setRowCount(0);

        Object itemXP = panelCapNhatTuyen.getTxtGaXuatPhat().getEditor().getItem();
        Object itemDich = panelCapNhatTuyen.getTxtGaDich().getEditor().getItem();
        String tenGaDi = itemXP != null ? itemXP.toString().trim() : "";
        String tenGaDen = itemDich != null ? itemDich.toString().trim() : "";

        if (tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }
        if(!dsGaCoSan.containsKey(tenGaDi) || !dsGaCoSan.containsKey(tenGaDen)){
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        List<Ga> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);

        int accumulatedDistance = 0;

        for (int i = 0; i < toanBoTuyen.size(); i++) {
            Ga gaHienTai = toanBoTuyen.get(i);
            String loaiGa = (i == 0) ? "Xuất Phát" : (i == toanBoTuyen.size() - 1 ? "Đích" : "Trung Gian");
            int kcDoan = 0;

            if (i > 0) {
                Ga gaTruoc = toanBoTuyen.get(i-1);
                kcDoan = tuyenBus.tinhKhoangCachTongDijsktra(gaTruoc.getGaID(), gaHienTai.getGaID());
                if(kcDoan == -1) {
                    JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không tìm thấy đường đi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                accumulatedDistance += kcDoan;
            }
            model.addRow(new Object[]{gaHienTai.getTenGa(), loaiGa, accumulatedDistance});
        }
        panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
    }

    private void xuLyCapNhatTuyen() {
        String maTuyenMoi = panelCapNhatTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelCapNhatTuyen.getTxtMoTa().getText().trim();

        if (maTuyenMoi.isEmpty() || panelCapNhatTuyen.getTxtDoDaiQuangDuong().getText().equals("0")) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Dữ liệu không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!maTuyenMoi.equals(tuyenIDCanCapNhat) && tuyenBus.kiemTraMaTuyuenDaTonTai(maTuyenMoi)) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Mã tuyến mới đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean trangThai = panelCapNhatTuyen.getCboTrangThai().getSelectedIndex() == 0;
            Tuyen tuyenCapNhat = new Tuyen(maTuyenMoi, moTa, trangThai);
            List<TuyenChiTiet> dsChiTiet = new ArrayList<>();
            DefaultTableModel model = panelCapNhatTuyen.getModelGaChiTiet();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGa = model.getValueAt(i, 0).toString();
                int kc = Integer.parseInt(model.getValueAt(i, 2).toString());
                dsChiTiet.add(new TuyenChiTiet(tuyenCapNhat, dsGaCoSan.get(tenGa), i + 1, kc));
            }

            if (tuyenBus.capNhatTuyen(tuyenCapNhat, dsChiTiet)) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật thành công!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xuLyHuyBo() {
        if (JOptionPane.showConfirmDialog(panelCapNhatTuyen, "Hủy cập nhật?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dialog.dispose();
        }
    }

}
